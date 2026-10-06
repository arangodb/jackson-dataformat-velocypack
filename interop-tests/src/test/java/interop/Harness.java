package interop;

import com.arangodb.jackson.dataformat.velocypack.*;
import tools.jackson.core.*;
import tools.jackson.databind.*;
import tools.jackson.databind.json.JsonMapper;
import java.io.*;
import java.math.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

final class Harness {
    static final JsonMapper JSON=JsonMapper.builder(tools.jackson.core.json.JsonFactory.builder().streamReadConstraints(StreamReadConstraints.builder().maxNumberLength(8192).maxNestingDepth(128).build()).build()).build();
    static final NativeVPackReference REF=new NativeVPackReference();
    static VPackFactory factory(boolean compact,boolean sorted,boolean minWidth) {
        return VPackFactory.builder().streamReadConstraints(StreamReadConstraints.builder().maxNumberLength(4096).maxNestingDepth(64).build())
                .configure(VPackWriteFeature.WRITE_COMPACT_ARRAYS,compact).configure(VPackWriteFeature.WRITE_COMPACT_OBJECTS,compact)
                .configure(VPackWriteFeature.WRITE_OBJECT_KEYS_SORTED,sorted).configure(VPackWriteFeature.WRITE_MIN_INT_WIDTH,minWidth).build();
    }
    static final VPackFactory DEFAULT=factory(true,true,true);
    record V(String type,Object value) {
        static V of(Object o) {
            if(o==null)return new V("null",null);
            if(o instanceof Boolean)return new V("bool",o);
            if(o instanceof String)return new V("string",o);
            if(o instanceof BigDecimal)return new V("decimal",o);
            if(o instanceof Float||o instanceof Double)return new V("double",((Number)o).doubleValue());
            if(o instanceof Number)return new V("integer",new BigInteger(o.toString()));
            if(o instanceof List<?> l)return new V("array",l.stream().map(V::of).toList());
            if(o instanceof Map<?,?> m){var v=new LinkedHashMap<String,V>();m.forEach((k,x)->v.put(k.toString(),of(x)));return new V("object",v);}
            throw new IllegalArgumentException("unsupported expected value "+o);
        }
        @SuppressWarnings("unchecked") Object jsonValue() {
            if(type.equals("array"))return ((List<V>)value).stream().map(V::jsonValue).toList();
            if(type.equals("object")){var out=new LinkedHashMap<String,Object>();((Map<String,V>)value).forEach((k,v)->out.put(k,v.jsonValue()));return out;}
            return value;
        }
    }
    static final class Difference extends AssertionError {
        final String path;
        Difference(String path,Object expected,Object actual){super(path+" expected="+expected+" actual="+actual);this.path=path;}
    }
    @SuppressWarnings("unchecked") static void equal(V e,V a,String path,boolean jsonProjection) {
        if(jsonProjection && e.type.equals("double") && a.type.equals("integer")) {
            equal(e,new V("double",((BigInteger)a.value).doubleValue()),path,false);return;
        }
        if(!e.type.equals(a.type))throw new Difference(path,e,a);
        if(e.type.equals("array")) {
            var el=(List<V>)e.value;var al=(List<V>)a.value;if(el.size()!=al.size())throw new Difference(path+".length",el.size(),al.size());
            for(int i=0;i<el.size();i++)equal(el.get(i),al.get(i),path+"["+i+"]",jsonProjection);return;
        }
        if(e.type.equals("object")) {
            var em=(Map<String,V>)e.value;var am=(Map<String,V>)a.value;if(!em.keySet().equals(am.keySet()))throw new Difference(path+".keys",em.keySet(),am.keySet());
            for(var k:em.keySet())equal(em.get(k),am.get(k),path+"["+JSON.writeValueAsString(k)+"]",jsonProjection);return;
        }
        if(e.type.equals("double")) {
            double x=(double)e.value,y=(double)a.value;
            if(Double.doubleToLongBits(x)!=Double.doubleToLongBits(y))throw new Difference(path,e,a);
        } else if(e.type.equals("decimal")) {
            if(((BigDecimal)e.value).compareTo((BigDecimal)a.value)!=0)throw new Difference(path,e,a);
        } else if(!Objects.equals(e.value,a.value))throw new Difference(path,e,a);
    }
    static V json(String s) {
        try(var p=JSON.createParser(s)){p.nextToken();var v=read(p,true);assertNull(p.nextToken(),"trailing JSON");return v;}
    }
    static V javaRead(byte[] bytes,boolean stream,VPackFactory f) {
        try(var p=stream?f.createParser(new Chunked(bytes,3)):f.createParser(bytes)) {
            assertNotNull(p.nextToken(),"missing root");V v=read(p);assertNull(p.nextToken(),"trailing VPack value");return v;
        }
    }
    static V javaRead(byte[] bytes,boolean stream){return javaRead(bytes,stream,DEFAULT);}
    static V read(JsonParser p) { return read(p,false); }
    static V read(JsonParser p,boolean projection) {
        return switch(p.currentToken()) {
            case START_ARRAY -> {var v=new ArrayList<V>();while(p.nextToken()!=JsonToken.END_ARRAY){if(p.currentToken()==null)throw new AssertionError("unterminated array");v.add(read(p,projection));}yield new V("array",v);}
            case START_OBJECT -> {var v=new LinkedHashMap<String,V>();while(p.nextToken()!=JsonToken.END_OBJECT){String key=p.currentName();p.nextToken();if(v.put(key,read(p,projection))!=null)throw new AssertionError("duplicate key needs token-sequence policy");}yield new V("object",v);}
            case VALUE_NULL -> V.of(null);
            case VALUE_TRUE -> V.of(true);
            case VALUE_FALSE -> V.of(false);
            case VALUE_STRING -> V.of(p.getString());
            // The native dumper emits IEEE negative zero as the JSON lexeme -0.
            // Preserve that sign in its declared projection; input conversion still uses integers.
            case VALUE_NUMBER_INT -> projection && p.getString().equals("-0") ? V.of(-0.0) : V.of(p.getBigIntegerValue());
            case VALUE_NUMBER_FLOAT -> p.getNumberType()==JsonParser.NumberType.BIG_DECIMAL ? V.of(p.getDecimalValue()):V.of(p.getDoubleValue());
            case VALUE_EMBEDDED_OBJECT -> {
                Object v=p.getEmbeddedObject();
                if(v instanceof byte[] b)yield new V("binary",HexFormat.of().formatHex(b));
                if(v instanceof VPackCustomValue c)yield new V("custom",c.getTypeByte()+":"+HexFormat.of().formatHex(c.getPayload()));
                yield new V("sentinel",v);
            }
            default -> throw new AssertionError("unexpected token "+p.currentToken());
        };
    }
    static byte[] javaEncode(String source,VPackFactory f) {
        var out=new ByteArrayOutputStream();
        try(var p=JSON.createParser(source);var g=f.createGenerator(out)) {
            while(p.nextToken()!=null) switch(p.currentToken()) {
                case START_ARRAY -> g.writeStartArray(); case END_ARRAY -> g.writeEndArray();
                case START_OBJECT -> g.writeStartObject(); case END_OBJECT -> g.writeEndObject();case PROPERTY_NAME -> g.writeName(p.currentName());
                case VALUE_NULL -> g.writeNull();case VALUE_TRUE -> g.writeBoolean(true);case VALUE_FALSE -> g.writeBoolean(false);case VALUE_STRING -> g.writeString(p.getString());
                // Explicit streaming policy avoids databinding's BigDecimal path.
                case VALUE_NUMBER_INT -> {var n=p.getBigIntegerValue();if(n.bitLength()<64)g.writeNumber(n.longValueExact());else g.writeNumber(n);}
                case VALUE_NUMBER_FLOAT -> g.writeNumber(p.getDoubleValue());
                default -> throw new AssertionError(p.currentToken());
            }
        }return out.toByteArray();
    }
    static byte[] write(java.util.function.Consumer<VPackGenerator> body,VPackFactory f) {
        var out=new ByteArrayOutputStream();try(var g=(VPackGenerator)f.createGenerator(out)){body.accept(g);}return out.toByteArray();
    }
    static JsonNode inspection(byte[] bytes) {
        var node=JSON.readTree(REF.call("inspect",bytes).require());cover(node);return node;
    }
    static void cover(JsonNode n) {
        String type=n.get("type").asString();Report.coverage(type,n.get("head").asInt(),"INSPECTED");
        if(type.equals("tagged"))cover(n.get("value"));
        else if(type.equals("array"))for(var c:n.get("value"))cover(c);
        else if(type.equals("object"))for(var c:n.get("value")){cover(c.get("key"));cover(c.get("value"));}
    }
    static V nativeValue(JsonNode n) {
        String type=n.get("type").asString();
        return switch(type) {
            case "smallint","int","uint","utc-date" -> V.of(new BigInteger(n.get("value").asString()));
            case "double" -> V.of(Double.longBitsToDouble(Long.parseUnsignedLong(n.get("bits").asString())));
            case "string" -> V.of(n.get("value").asString());case "bool" -> V.of(n.get("value").asBoolean());case "null" -> V.of(null);
            case "array" -> {var a=new ArrayList<V>();for(var v:n.get("value"))a.add(nativeValue(v));yield new V("array",a);}
            case "object" -> {var m=new LinkedHashMap<String,V>();for(var pair:n.get("value")){V key=nativeValue(pair.get("key"));String k=key.value.toString();if(m.put(k,nativeValue(pair.get("value")))!=null)throw new AssertionError("duplicate keys need ordered comparison");}yield new V("object",m);}
            case "tagged" -> nativeValue(n.get("value"));
            case "binary" -> new V("binary",n.get("value").asString());
            case "min-key" -> new V("sentinel","minKey");case "max-key" -> new V("sentinel","maxKey");
            case "custom" -> {byte[] b=HexFormat.of().parseHex(n.get("wire").asString());int h=b[0]&255,offset=h<0xf4?1:1+(1<<((h-0xf4)/3));yield new V("custom",h+":"+HexFormat.of().formatHex(b,offset,b.length));}
            default -> throw new AssertionError("no semantic projection for "+type);
        };
    }
    static void differential(String id,String source,V expected,boolean compact,boolean minWidth) {
        long flags=NativeVPackReference.STRICT_UTF|(compact?NativeVPackReference.COMPACT:0)|NativeVPackReference.UNIQUE;
        Report.detail("nativeCallOptions",flags);Report.detail("javaOptions","compact="+compact+";sorted=true;minWidth="+minWidth);
        byte[] java=javaEncode(source,factory(compact,true,minWidth)), cpp=REF.parse(source,flags);
        Report.bytes("java",java);Report.bytes("cpp",cpp);
        for(var entry:Map.of("java",java,"cpp",cpp).entrySet()) {
            var bytes=entry.getValue();String name=entry.getKey();
            equal(expected,javaRead(bytes,false),"$."+name+".java.buffer",false);
            equal(expected,javaRead(bytes,true),"$."+name+".java.stream",false);
            var inspected=inspection(bytes);equal(expected,nativeValue(inspected),"$."+name+".native.typed",false);
            var dumped=REF.call("dump",bytes,flags).text();Report.detail(name+"NativeDump",dumped);
            equal(expected,json(dumped),"$."+name+".native.json",true);
            Report.coverage(inspected.get("type").asString(),bytes[0]&255,"JSON_EQUIVALENT");
        }
        Report.outcome(id,Report.Policy.NUMERIC_VALUE,"JSON_EQUIVALENT");
    }
    static void wire(String id,byte[] wire,V expected,String nativeType) {
        Report.bytes("input",wire);Report.coverage(nativeType,wire[0]&255,"ATTEMPTED");
        equal(expected,javaRead(wire,false),"$.java.buffer",false);equal(expected,javaRead(wire,true),"$.java.stream",false);
        var n=inspection(wire);assertEquals(nativeType,n.get("type").asString());equal(expected,nativeValue(n),"$.native",false);
        boolean projection=nativeType.equals("tagged")||nativeType.equals("utc-date");
        String outcome=projection?"PROJECTED":nativeType.endsWith("-key")?"TYPE_CHECKED":"TYPED_EQUIVALENT";
        Report.coverage(nativeType,wire[0]&255,outcome);
        Report.outcome(id,projection?Report.Policy.JSON_PROJECTION:Report.Policy.LOGICAL_TYPE,outcome);
    }
    static void nativeError(String id,String op,byte[] bytes,long flags,int code,String messagePart) {
        Report.detail("nativeOperation",op);Report.detail("nativeCallOptions",flags);Report.bytes("input",bytes);
        var r=(Set.of("validate","dump","inspect").contains(op)&&Set.of(50,51,15).contains(code))
                ? isolatedError(id,op,bytes,flags,code) : REF.call(op,bytes,flags);
        assertEquals(1,r.status(),r.toString());assertEquals(code,r.code(),r.message());assertTrue(r.message().contains(messagePart),r.message());
        Report.detail("nativeError",r.code()+":"+r.message());boolean unsupported=code==2||code==10||code==19||code==20||code==34;
        Report.outcome(id,unsupported?Report.Policy.UNSUPPORTED_OPERATION:Report.Policy.REJECTION,unsupported?"NATIVE_UNSUPPORTED":"REJECTED");
    }
    static NativeVPackReference.Result isolatedError(String id,String op,byte[] bytes,long flags,int code) {
        try {
            var result=MalformedInputTest.isolate(new MalformedInputTest.Bad(id,bytes,code,flags,64));
            for(String operation:List.of("validate","dump","inspect")){
                var r=result.get(operation);assertEquals(1,r.get("status").asInt(),operation);assertEquals(code,r.get("code").asInt(),operation);
            }
            var r=result.get(op);return new NativeVPackReference.Result(r.get("status").asInt(),r.get("code").asInt(),new byte[0],r.get("message").asString());
        }catch(RuntimeException|Error e){throw e;}catch(Exception e){throw new AssertionError("native isolation failed for "+id,e);}
    }
    static void javaReject(byte[] bytes,VPackFactory f,String message) {
        var e=assertThrows(tools.jackson.core.exc.StreamReadException.class,()->{
            var actual=javaRead(bytes,false,f);Report.detail("unexpectedBufferedValue",actual.toString());
        });assertTrue(e.getMessage().contains(message),e.getMessage());
        var s=assertThrows(tools.jackson.core.exc.StreamReadException.class,()->{
            var actual=javaRead(bytes,true,f);Report.detail("unexpectedStreamValue",actual.toString());
        });assertTrue(s.getMessage().contains(message),s.getMessage());
    }
    static class Chunked extends ByteArrayInputStream {
        final int chunk;Chunked(byte[] b,int chunk){super(b);this.chunk=chunk;}
        @Override public synchronized int read(byte[] b,int off,int len){return super.read(b,off,Math.min(chunk,len));}
    }
}
