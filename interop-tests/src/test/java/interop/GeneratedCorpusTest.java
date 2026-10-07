package interop;

import java.math.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.*;
import java.util.stream.*;
import org.junit.jupiter.api.*;
import static interop.Harness.*;
import static org.junit.jupiter.api.Assertions.*;

class GeneratedCorpusTest {
    static final long SEED=Long.getLong("interop.seed",1592614637L);
    static final int JSON_COUNT=Integer.getInteger("interop.jsonCases",10000),TYPED_COUNT=Integer.getInteger("interop.typedCases",5000);
    static final MessageDigest JSON_HASH=digest(), TYPED_HASH=digest();
    static MessageDigest digest(){try{return MessageDigest.getInstance("SHA-256");}catch(Exception e){throw new AssertionError(e);}}
    @AfterAll static void recordCorpusFingerprints() {
        Report.metadata("corpus.json.sha256",HexFormat.of().formatHex(JSON_HASH.digest()));
        Report.metadata("corpus.typed.sha256",HexFormat.of().formatHex(TYPED_HASH.digest()));
    }
    static final String[] STRINGS={"","\0","\"\\/\b\f\n\r\t","é漢😀","\u007f\u0080\u07ff\u0800", "x".repeat(126),"é".repeat(64)};
    static Object generate(Random r,int depth) {
        int kind=r.nextInt(depth==8?7:10);
        return switch(kind) {
            case 0->null;case 1->r.nextBoolean();case 2->r.nextLong();case 3->r.nextInt();
            case 4->{double d;do{d=Double.longBitsToDouble(r.nextLong());}while(!Double.isFinite(d));yield d;}
            case 5->STRINGS[r.nextInt(STRINGS.length)];case 6->randomString(r);
            case 7,8->{var a=new ArrayList<>();for(int i=0,n=r.nextInt(5);i<n;i++)a.add(generate(r,depth+1));yield a;}
            default->{var o=new LinkedHashMap<String,Object>();for(int i=0,n=r.nextInt(5);i<n;i++)o.put(randomString(r)+i,generate(r,depth+1));yield o;}
        };
    }
    static String randomString(Random r){var b=new StringBuilder();for(int i=0,n=r.nextInt(30);i<n;i++)b.appendCodePoint(switch(r.nextInt(4)){case 0->r.nextInt(128);case 1->0x80+r.nextInt(0x700);case 2->0x800+r.nextInt(0xd000-0x800);default->0x10000+r.nextInt(0x100000);});return b.toString();}
    @TestFactory Stream<DynamicTest> fixedSeedJsonCorpus() {
        if(JSON_COUNT<0)throw new IllegalArgumentException("negative corpus size");
        return IntStream.range(0,JSON_COUNT).mapToObj(i->{
            long caseSeed=SEED+0x9e3779b97f4a7c15L*i;Object value=generate(new Random(caseSeed),0);V expected=V.of(value);String source=JSON.writeValueAsString(value);String id="generated-json-"+i;
            assertTrue(source.getBytes(StandardCharsets.UTF_8).length<=1<<20);
            JSON_HASH.update(source.getBytes(StandardCharsets.UTF_8));JSON_HASH.update((byte)'\n');
            return DynamicTest.dynamicTest(id,()->Report.run(id,Report.Policy.NUMERIC_VALUE,source,expected,()->{
                Report.detail("caseSeed",caseSeed);
                try {differential(id,source,expected,(i&1)==0,(i&2)==0);}
                catch(AssertionError|RuntimeException failure){shrinkJson(id,value,(i&1)==0,(i&2)==0);throw failure;}
            }));
        });
    }
    // Shrinking is deterministic and only retains candidates reproducing the same failure class/path family.
    static void shrinkJson(String id,Object original,boolean compact,boolean minWidth) throws Exception {
        Object current=original;String fingerprint=fingerprint(current,compact,minWidth);
        if(fingerprint==null)return;
        for(int round=0;round<32;round++) {
            boolean changed=false;
            for(Object candidate:candidates(current)) {
                if(Objects.equals(candidate,current))continue;
                if(fingerprint.equals(fingerprint(candidate,compact,minWidth))){current=candidate;changed=true;break;}
            }
            if(!changed)break;
        }
        String json=JSON.writeValueAsString(current);Report.detail("shrunkJson",json);
        Files.createDirectories(Path.of("target/reports/shrunk"));Files.writeString(Path.of("target/reports/shrunk",id+".json"),json);
    }
    static String fingerprint(Object value,boolean compact,boolean minWidth) {
        try {String source=JSON.writeValueAsString(value);V expected=V.of(value);
            byte[] java=javaEncode(source,factory(compact,true,minWidth)),cpp=REF.parse(source,2|4|(compact?1:0));
            for(byte[] b:List.of(java,cpp)){equal(expected,javaRead(b,false),"$.java",false);equal(expected,nativeValue(inspection(b)),"$.native",false);equal(expected,json(REF.call("dump",b).text()),"$.dump",true);}return null;
        }catch(Difference d){return "difference:"+d.path.replaceAll("\\[[^]]*]", "[]");}
        catch(NativeVPackReference.NativeError e){return "native:"+e.code;}
        catch(tools.jackson.core.JacksonException e){return e.getClass().getName();}
    }
    static List<Object> candidates(Object o) {
        var out=new ArrayList<Object>();
        if(o instanceof List<?> a){out.addAll(a);out.add(List.of());if(a.size()>1)out.add(a.subList(0,a.size()/2));for(int i=0;i<a.size();i++)for(var c:candidates(a.get(i))){var copy=new ArrayList<Object>(a);copy.set(i,c);out.add(copy);}}
        else if(o instanceof Map<?,?> m){out.addAll(m.values());out.add(Map.of());for(var key:m.keySet()){var copy=new LinkedHashMap<>(m);copy.remove(key);out.add(copy);}}
        else if(o instanceof String s){out.add("");if(!s.isEmpty())out.add(s.substring(0,s.offsetByCodePoints(0,s.codePointCount(0,s.length())/2)));}
        else if(o instanceof Double d){
            out.add(0d);out.add(-0d);out.add(Math.copySign(1d,d));
            if(Double.isFinite(d)){
                var decimal=BigDecimal.valueOf(d);
                for(int precision=1;precision<decimal.precision();precision++)
                    out.add(decimal.round(new MathContext(precision,RoundingMode.DOWN)).doubleValue());
            }
        }
        else if(o instanceof Number){out.add(0L);out.add(1L);out.add(-1L);}
        return out;
    }
    record Typed(byte[] wire,V expected,String type) {}
    static Typed typed(Random r,int i) {
        return switch(i%10) {
            case 0->{int w=1+r.nextInt(8);long value=r.nextLong();BigInteger n=BigInteger.valueOf(value);if(w<8)n=n.and(BigInteger.ONE.shiftLeft(w*8).subtract(BigInteger.ONE));if(w<8&&n.testBit(w*8-1))n=n.subtract(BigInteger.ONE.shiftLeft(w*8));yield new Typed(Spec.integer(n,w,false),V.of(n),"int");}
            case 1->{int w=1+r.nextInt(8);var n=new BigInteger(w*8,r);yield new Typed(Spec.integer(n,w,true),V.of(n),"uint");}
            case 2->{double d=Double.longBitsToDouble(r.nextLong());yield new Typed(Spec.doubleValue(d),V.of(d),"double");}
            case 3->{String s=randomString(r);yield new Typed(Spec.string(s,r.nextBoolean()),V.of(s),"string");}
            case 4->{byte[] b=new byte[r.nextInt(256)];r.nextBytes(b);yield new Typed(Spec.binary(b,1+r.nextInt(8)),new V("binary",HexFormat.of().formatHex(b)),"binary");}
            case 5->{int h=0xf0+r.nextInt(16);byte[] b=new byte[h<0xf4?1<<(h-0xf0):1+r.nextInt(255)];r.nextBytes(b);yield new Typed(Spec.custom(h,b),new V("custom",h+":"+HexFormat.of().formatHex(b)),"custom");}
            case 6->{var tag=new BigInteger(64,r);byte[] b=Spec.tag(tag,true,Spec.tag(BigInteger.valueOf(r.nextInt(256)),false,Spec.head(0x35)));yield new Typed(b,V.of(5),"tagged");}
            case 7->{long ms=r.nextLong();yield new Typed(Spec.cat(Spec.head(0x1c),Spec.le(ms,8)),V.of(ms),"utc-date");}
            case 8->{int head=r.nextBoolean()?0x1e:0x1f;yield new Typed(Spec.head(head),new V("sentinel",head==0x1e?"minKey":"maxKey"),head==0x1e?"min-key":"max-key");}
            default->{boolean b=r.nextBoolean();yield new Typed(Spec.head(b?0x1a:0x19),V.of(b),"bool");}
        };
    }
    @TestFactory Stream<DynamicTest> fixedSeedTypedCorpus() {
        if(TYPED_COUNT<0)throw new IllegalArgumentException("negative corpus size");
        return IntStream.range(0,TYPED_COUNT).mapToObj(i->{long caseSeed=SEED^0x632be59bd9b4e019L^i;Random r=new Random(caseSeed);Typed t=typed(r,i);String id="generated-typed-"+i;
            // Bounded mixed nesting up to depth 8, with both indexed and compact arrays.
            byte[] bytes=t.wire;V expected=t.expected;String type=t.type;int depth=r.nextInt(9);
            var shrinkCandidates=new ArrayList<Typed>();shrinkCandidates.add(t);
            for(int d=0;d<depth;d++) {
                if(r.nextBoolean()){bytes=Spec.compact(true,List.of(Spec.string("k",false),bytes),1);expected=new V("object",Map.of("k",expected));}
                else {bytes=r.nextBoolean()?Spec.compact(false,List.of(bytes,Spec.head(0x18)),2):Spec.indexedArray(List.of(bytes,Spec.head(0x18)),2,false);expected=new V("array",Arrays.asList(expected,V.of(null)));}
                type=(bytes[0]&255)==0x14?"object":"array";
                shrinkCandidates.add(new Typed(bytes,expected,type));
            }
            final byte[] wire=bytes;final V e=expected;final String nativeType=type;
            TYPED_HASH.update(Spec.le(wire.length,4));TYPED_HASH.update(wire);
            return DynamicTest.dynamicTest(id,()->Report.run(id,Report.Policy.LOGICAL_TYPE,null,e,()->{
                Report.detail("caseSeed",caseSeed);
                try { wire(id,wire,e,nativeType); }
                catch(AssertionError|RuntimeException failure){shrinkTyped(id,shrinkCandidates,failure);throw failure;}
            }));
        });
    }
    static String typedFingerprint(Throwable t){return t instanceof NativeVPackReference.NativeError n?"native:"+n.code:t.getClass().getName();}
    static void shrinkTyped(String id,List<Typed> candidates,Throwable original) throws Exception {
        for(Typed candidate:candidates) {
            try {
                equal(candidate.expected,javaRead(candidate.wire,false),"$.java",false);
                equal(candidate.expected,nativeValue(inspection(candidate.wire)),"$.native",false);
            }catch(AssertionError|RuntimeException failure){
                if(typedFingerprint(failure).equals(typedFingerprint(original))) {
                    Report.bytes("shrunkTyped",candidate.wire);Report.detail("shrunkExpected",candidate.expected.toString());
                    Files.createDirectories(Path.of("target/reports/shrunk"));Files.writeString(Path.of("target/reports/shrunk",id+".hex"),HexFormat.of().formatHex(candidate.wire));return;
                }
            }
        }
    }
}
