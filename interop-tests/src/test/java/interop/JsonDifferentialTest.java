package interop;

import java.math.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.*;
import org.junit.jupiter.api.*;
import static interop.Harness.*;
import static org.junit.jupiter.api.Assertions.*;

class JsonDifferentialTest {
    record Case(String id,String json,V expected) {}
    static Case c(String id,String json,Object expected){return new Case(id,json,V.of(expected));}
    static List<Case> cases() {
        var cases=new ArrayList<Case>();
        cases.add(c("null","null",null));cases.add(c("true","true",true));cases.add(c("false","false",false));
        cases.add(c("empty-string","\"\"",""));cases.add(c("empty-array","[]",List.of()));cases.add(c("empty-object","{}",Map.of()));
        cases.add(c("nested","{\"z\":[1,true,null,{\"a\":\"é漢😀\"}],\"a\":[]}",Map.of("z",Arrays.asList(1,true,null,Map.of("a","é漢😀")),"a",List.of())));
        cases.add(c("escaped","\"\\\"\\\\\\/\\b\\f\\n\\r\\t\\u0000\"","\"\\/\b\f\n\r\t\0"));
        cases.add(c("supplementary","\"\\ud83d\\ude00\"","😀"));
        for(int i=-6;i<=9;i++)cases.add(c("small-"+i,Integer.toString(i),i));
        var integers=new TreeSet<BigInteger>();
        for(int bits:new int[]{7,8,15,16,23,24,31,32,39,40,47,48,55,56,62,63}) {
            var base=BigInteger.ONE.shiftLeft(bits);for(int d=-1;d<=1;d++){integers.add(base.add(BigInteger.valueOf(d)));integers.add(base.negate().add(BigInteger.valueOf(d)));}
        }
        // Shared JSON range: integer Java encoding stays in native Int/UInt/SmallInt families.
        integers.removeIf(n->n.compareTo(BigInteger.valueOf(Long.MIN_VALUE))<0||n.compareTo(BigInteger.valueOf(Long.MAX_VALUE))>0);
        integers.forEach(n->cases.add(c("integer-"+n,n.toString(),n)));
        for(double d:new double[]{0.0,-0.0,1.0,-1.0,Math.nextUp(1.0),Math.nextDown(1.0),Double.MIN_VALUE,-Double.MIN_VALUE,Double.MIN_NORMAL,Math.nextDown(Double.MIN_NORMAL),Double.MAX_VALUE,-Double.MAX_VALUE,Math.scalb(1.0,53),Math.nextUp(Math.scalb(1.0,53)),1e-300,1e300})cases.add(c("double-"+Long.toHexString(Double.doubleToRawLongBits(d)),Double.toString(d),d));
        StringBuilder controls=new StringBuilder();for(int i=0;i<32;i++)controls.append((char)i);
        for(String s:List.of(controls.toString(),"a\0b","\u007f\u0080\u07ff\u0800\uffff😀","\\\"/","é".repeat(63),"a".repeat(126),"a".repeat(127),"é".repeat(64)))cases.add(c("string-"+cases.size(),JSON.writeValueAsString(s),s));
        for(String s:List.of("",controls.toString(),"a\0b","é漢😀","a".repeat(126),"a".repeat(127)))cases.add(c("name-"+cases.size(),JSON.writeValueAsString(Map.of(s,"value")),Map.of(s,"value")));
        return cases;
    }
    @TestFactory Stream<DynamicTest> sharedJson() {
        return cases().stream().flatMap(c->IntStream.range(0,4).mapToObj(mode->DynamicTest.dynamicTest(c.id+"-mode"+mode,()->
                Report.run(c.id+"-mode"+mode,Report.Policy.NUMERIC_VALUE,c.json,c.expected,()->differential(c.id,c.json,c.expected,(mode&1)==0,(mode&2)==0)))));
    }
    @Test void duplicateKeysUseOrderedTokensAndExplicitUniqueness() throws Throwable {
        String json="{\"a\":1,\"a\":2,\"b\":3}";
        Report.run("duplicates",Report.Policy.EXACT_VALUE,json,List.of("a=1","a=2","b=3"),()->{
            for(boolean compact:new boolean[]{true,false}) {
                byte[] java=javaEncode(json,factory(compact,true,true)),cpp=REF.parse(json,2|(compact?1:0));
                for(byte[] bytes:List.of(java,cpp)) {
                    for(boolean stream:new boolean[]{true,false})assertEquals(List.of("a=1","a=2","b=3"),tokens(bytes,stream));
                    var n=inspection(bytes);var pairs=new ArrayList<String>();for(var pair:n.get("value"))pairs.add(pair.get("key").get("value").asString()+"="+pair.get("value").get("value").asString());
                    assertEquals(List.of("a=1","a=2","b=3"),pairs);
                    // Upstream Validator checks structure; uniqueness is a Builder/Parser option.
                    REF.call("validate",bytes,2|4).require();
                }
                nativeError("duplicate-json","parse",json.getBytes(StandardCharsets.UTF_8),2|4|(compact?1:0),18,"Duplicate");
            }
        });
    }
    static List<String> tokens(byte[] b,boolean stream) {
        var out=new ArrayList<String>();try(var p=stream?DEFAULT.createParser(new Chunked(b,1)):DEFAULT.createParser(b)){
            assertEquals(tools.jackson.core.JsonToken.START_OBJECT,p.nextToken());
            while(p.nextToken()!=tools.jackson.core.JsonToken.END_OBJECT){String name=p.currentName();p.nextToken();out.add(name+"="+p.getBigIntegerValue());}assertNull(p.nextToken());
        }return out;
    }
    @Test void legacyIndexedUnsortedObjectHasExplicitRejectionContract() throws Throwable {
        Report.run("legacy-unsorted",Report.Policy.REJECTION,"{\"z\":1,\"a\":2}","native ValidatorInvalidType; Java value",()->{
            byte[] b=javaEncode("{\"z\":1,\"a\":2}",factory(false,false,true));Report.bytes("java",b);
            for(boolean stream:new boolean[]{false,true})equal(V.of(Map.of("z",1,"a",2)),javaRead(b,stream),"$",false);
            nativeError("unsorted-object","validate",b,2,51,"Invalid type");Report.coverage("object",b[0]&255,"NATIVE_REJECTED_LEGACY_UNSORTED");
        });
    }
    @Test void uint64JsonAndBigIntegerDatabindingDeclareBcdPolicy() throws Throwable {
        Report.run("uint64-json",Report.Policy.UNSUPPORTED_OPERATION,"18446744073709551615",new BigInteger("18446744073709551615"),()->{
            var n=new BigInteger("18446744073709551615");
            byte[] cpp=REF.parse(n.toString(),2);equal(V.of(n),nativeValue(inspection(cpp)),"$.native",false);equal(V.of(n),javaRead(cpp,false),"$.java",false);
            byte[] java=javaEncode(n.toString(),DEFAULT);equal(V.of(n),javaRead(java,false),"$.javaBcd",false);
            nativeError("uint64-java-bcd","validate",java,2,2,"Not implemented");Report.coverage("bcd",java[0]&255,"NATIVE_UNSUPPORTED");
            byte[] databound=new com.arangodb.jackson.dataformat.velocypack.VPackMapper().writeValueAsBytes(n);
            equal(V.of(n),javaRead(databound,false),"$.databinding",false);nativeError("uint64-databinding","validate",databound,2,2,"Not implemented");
        });
    }
    @TestFactory Stream<DynamicTest> integersOutsideNativeRangesHaveExplicitProjectionOrRejection() {
        return Stream.of(BigInteger.valueOf(Long.MIN_VALUE).subtract(BigInteger.ONE),BigInteger.ONE.shiftLeft(64),BigInteger.TEN.pow(4095).subtract(BigInteger.ONE)).map(n->{
            String id="out-of-native-integer-range-"+n.bitLength()+"-sign"+n.signum();
            boolean finite=Double.isFinite(n.doubleValue());
            return DynamicTest.dynamicTest(id,()->Report.run(id,finite?Report.Policy.JSON_PROJECTION:Report.Policy.REJECTION,n.toString(),finite?"Java exact integer; native double projection":"Java exact integer; native NumberOutOfRange",()->{
                byte[] java=javaEncode(n.toString(),DEFAULT);Report.bytes("java",java);
                equal(V.of(n),javaRead(java,false),"$.javaInteger",false);nativeError(id,"validate",java,2,2,"Not implemented");
                if(!finite){nativeError(id,"parse",n.toString().getBytes(StandardCharsets.UTF_8),2,14,"range");return;}
                byte[] cpp=REF.parse(n.toString(),2);Report.bytes("cpp",cpp);
                var inspected=inspection(cpp);assertEquals("double",inspected.get("type").asString());
                equal(V.of(n.doubleValue()),nativeValue(inspected),"$.nativeDoubleProjection",false);
                equal(V.of(n.doubleValue()),javaRead(cpp,false),"$.javaNativeProjection",false);
            }));
        });
    }
    @Test void nativeExponentOverflowHasSpecificRangeError() throws Throwable {
        Report.run("native-double-overflow",Report.Policy.REJECTION,"1e309","NumberOutOfRange",()->{
            nativeError("exponent-overflow","parse","1e309".getBytes(StandardCharsets.UTF_8),2,14,"range");
        });
    }
}
