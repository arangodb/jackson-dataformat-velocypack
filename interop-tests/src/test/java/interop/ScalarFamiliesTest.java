package interop;

import java.math.*;
import java.util.*;
import java.util.stream.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;
import com.arangodb.jackson.dataformat.velocypack.*;
import static interop.Harness.*;
import static org.junit.jupiter.api.Assertions.*;

class ScalarFamiliesTest {
    static DynamicTest test(String id,Report.Policy policy,byte[] bytes,Object expected,Executable body) {
        return DynamicTest.dynamicTest(id,()->Report.run(id,policy,null,expected,()->{Report.bytes("input",bytes);body.execute();}));
    }
    @TestFactory Stream<DynamicTest> allSignedAndUnsignedWidths() {
        var tests=new ArrayList<DynamicTest>();
        for(int w=1;w<=8;w++)for(boolean unsigned:new boolean[]{false,true}) {
            var max=BigInteger.ONE.shiftLeft(w*8-(unsigned?0:1)).subtract(BigInteger.ONE);
            var min=unsigned?BigInteger.ZERO:max.add(BigInteger.ONE).negate();
            var values=new TreeSet<BigInteger>(List.of(min,min.add(BigInteger.ONE),max.subtract(BigInteger.ONE),max,BigInteger.ZERO,BigInteger.ONE));
            if(!unsigned)values.add(BigInteger.ONE.negate());
            for(var n:values) {
                String id=(unsigned?"uint":"int")+w+"-"+n;byte[] wire=Spec.integer(n,w,unsigned);
                tests.add(test(id,Report.Policy.NUMERIC_VALUE,wire,n,()->wire(id,wire,V.of(n),unsigned?"uint":"int")));
            }
        }
        for(int i=-6;i<=9;i++){final int n=i;byte[] b=Spec.head(i>=0?0x30+i:0x40+i);tests.add(test("smallint-"+i,Report.Policy.NUMERIC_VALUE,b,i,()->wire("smallint-"+n,b,V.of(n),"smallint")));}
        return tests.stream();
    }
    @TestFactory Stream<DynamicTest> scalarHeadFamiliesAndNativeFixtureConstruction() {
        var tests=new ArrayList<DynamicTest>();
        for(var c:List.of(new Object[]{"Null",null,"null"},new Object[]{"Bool",true,"bool"},new Object[]{"Bool",false,"bool"},new Object[]{"Int","-129","int"},new Object[]{"UInt","18446744073709551615","uint"},new Object[]{"String","é\0😀","string"},new Object[]{"Binary","0001ff80","binary"},new Object[]{"UTCDate",Long.toString(Long.MIN_VALUE),"utc-date"})) {
            String type=(String)c[0],nativeType=(String)c[2];Object value=c[1];
            tests.add(test("native-fixture-"+type+"-"+value,Report.Policy.LOGICAL_TYPE,new byte[0],nativeType,()->{
                byte[] b=REF.fixture(type,value==null?"":value);Report.bytes("fixture",b);
                var n=inspection(b);assertEquals(nativeType,n.get("type").asString());
                V expected=switch(type){case "Int","UInt","UTCDate"->V.of(new BigInteger(value.toString()));case "Binary"->new V("binary",value);default->V.of(value);};
                equal(expected,nativeValue(n),"$.native",false);equal(expected,javaRead(b,false),"$.java",false);equal(expected,javaRead(b,true),"$.stream",false);
            }));
        }
        for(int head:new int[]{0,0x17,0x1e,0x1f}) {
            String type=switch(head){case 0->"none";case 0x17->"illegal";case 0x1e->"min-key";default->"max-key";};byte[] b=Spec.head(head);
            tests.add(test("special-"+type,Report.Policy.JSON_PROJECTION,b,type,()->{
                assertEquals(type,inspection(b).get("type").asString());
                nativeError(type,"dump",b,2,10,"JSON");
                if(head==0||head==0x17)javaReject(b,DEFAULT,head==0?"none":"illegal");
                else for(boolean stream:new boolean[]{false,true})equal(new V("sentinel",head==0x1e?"minKey":"maxKey"),javaRead(b,stream),"$",false);
            }));
        }
        return tests.stream();
    }
    @TestFactory Stream<DynamicTest> ieee754BoundariesAndNonfiniteClassifications() {
        var values=new LinkedHashSet<Double>(List.of(0d,-0d,Double.MIN_VALUE,-Double.MIN_VALUE,Double.MIN_NORMAL,Math.nextDown(Double.MIN_NORMAL),Double.MAX_VALUE,-Double.MAX_VALUE,Double.NaN,Double.POSITIVE_INFINITY,Double.NEGATIVE_INFINITY));
        for(int exp:new int[]{-1074,-1022,-1000,-53,-1,0,23,24,31,32,52,53,54,63,64,1000,1023}){double d=Math.scalb(1d,exp);values.add(d);values.add(Math.nextDown(d));values.add(Math.nextUp(d));values.add(-d);}
        return values.stream().map(d->{String id="ieee-"+Long.toHexString(Double.doubleToRawLongBits(d));byte[] b=Spec.doubleValue(d);
            return test(id,Double.isFinite(d)?Report.Policy.EXACT_VALUE:Report.Policy.LOGICAL_TYPE,b,d,()->{
                wire(id,b,V.of(d),"double");byte[] fixture=REF.fixture("Double",Long.toHexString(Double.doubleToRawLongBits(d)));wire(id+"-fixture",fixture,V.of(d),"double");
                byte[] java=write(g->g.writeNumber(d),DEFAULT);wire(id+"-java",java,V.of(d),"double");
                if(Double.isFinite(d))equal(V.of(d),json(REF.call("dump",b,2).text()),"$.json",true);
                else {nativeError(id,"dump",b,2,10,"JSON");assertEquals(Double.isNaN(d)?"NaN":d>0?"Infinity":"-Infinity",JSON.readTree(REF.call("dump",b,2|32).require()).asString());}
            });});
    }
    @TestFactory Stream<DynamicTest> everyShortStringHeadAndLongStringVariant() {
        return IntStream.rangeClosed(0,127).boxed().flatMap(n->Stream.of(false,true).map(longForm->{
            String s="x".repeat(n);byte[] b=Spec.string(s,longForm);String id="string-"+n+"-long="+longForm;
            return test(id,Report.Policy.EXACT_VALUE,b,s,()->wire(id,b,V.of(s),"string"));
        }));
    }
    @TestFactory Stream<DynamicTest> nanPayloadsHaveExplicitClassificationPolicy() {
        return Stream.of(0x7ff0000000000001L,0x7ff8000000000001L,0x7fffffffffffffffL,0xfff0000000000001L,0xfff8000000000001L).map(bits->{
            double value=Double.longBitsToDouble(bits);byte[] b=Spec.cat(Spec.head(0x1b),Spec.le(bits,8));String id="nan-class-"+Long.toHexString(bits);
            return test(id,Report.Policy.LOGICAL_TYPE,b,"NaN classification",()->{
                wire(id,b,V.of(Double.NaN),"double");assertTrue(Double.isNaN((double)nativeValue(inspection(b)).value()));
                byte[] java=write(g->g.writeNumber(value),DEFAULT);wire(id+"-java",java,V.of(Double.NaN),"double");
            });
        });
    }
    @TestFactory Stream<DynamicTest> binaryLengthWidthsAndPayloadPatterns() {
        var tests=new ArrayList<DynamicTest>();
        for(int w=1;w<=8;w++)for(int size:new int[]{0,1,2,127,255,256,65535,65536}) {
            if(w==1&&size>255||w==2&&size>65535)continue;
            byte[] payload=new byte[size];for(int i=0;i<size;i++)payload[i]=(byte)i;byte[] b=Spec.binary(payload,w);String id="binary-"+w+"-"+size;
            tests.add(test(id,Report.Policy.JSON_PROJECTION,b,"exact binary payload",()->{
                wire(id,b,new V("binary",HexFormat.of().formatHex(payload)),"binary");
                assertEquals(HexFormat.of().formatHex(payload),JSON.readTree(REF.call("dump",b,2|8).require()).asString());
                nativeError(id,"dump",b,2,10,"JSON");
                byte[] java=write(g->g.writeBinary(payload),DEFAULT);wire(id+"-java",java,new V("binary",HexFormat.of().formatHex(payload)),"binary");
            }));
        }return tests.stream();
    }
    @TestFactory Stream<DynamicTest> allSixteenCustomLayouts() {
        return IntStream.rangeClosed(0xf0,0xff).mapToObj(h->{int size=h<0xf4?1<<(h-0xf0):3;byte[] payload=new byte[size];Arrays.fill(payload,(byte)0xab);byte[] b=Spec.custom(h,payload);String id="custom-"+Integer.toHexString(h);
            return test(id,Report.Policy.LOGICAL_TYPE,b,h,()->{
                V e=new V("custom",h+":"+HexFormat.of().formatHex(payload));wire(id,b,e,"custom");
                byte[] fixture=REF.fixture("Custom",HexFormat.of().formatHex(b));wire(id+"-fixture",fixture,e,"custom");
                nativeError(id,"dump",b,2,19,"custom");nativeError(id,"validate",b,2|256,40,"Custom");
                javaReject(b,VPackFactory.builder().enable(VPackReadFeature.FAIL_ON_CUSTOM_TYPES).build(),"FAIL_ON_CUSTOM_TYPES");
            });});
    }
    @TestFactory Stream<DynamicTest> customLengthTransitions() {
        var tests=new ArrayList<DynamicTest>();
        for(int h=0xf4;h<=0xff;h++)for(int n:new int[]{1,255,256,65535,65536}) {
            int width=1<<((h-0xf4)/3);if(width==1&&n>255||width==2&&n>65535)continue;
            byte[] payload=new byte[n];for(int i=0;i<n;i++)payload[i]=(byte)(255-i);byte[] b=Spec.custom(h,payload);String id="custom-length-"+Integer.toHexString(h)+"-"+n;final int head=h;
            tests.add(test(id,Report.Policy.LOGICAL_TYPE,b,"custom payload",()->wire(id,b,new V("custom",head+":"+HexFormat.of().formatHex(payload)),"custom")));
        }return tests.stream();
    }
    @Test void nativeTaggedFixtureAndJavaPrimitiveFloats() throws Throwable {
        Report.run("native-tagged-and-floats",Report.Policy.LOGICAL_TYPE,null,"tag 256 wraps 42; floats widen to double",()->{
            byte[] tagged=REF.call("fixture",JSON.writeValueAsBytes(Map.of("type","Tagged","value","256","number",42))).require();
            wire("native-tag-fixture",tagged,V.of(42),"tagged");assertEquals("256",inspection(tagged).get("tags").get(0).asString());
            for(float f:new float[]{0f,-0f,Float.MIN_VALUE,Float.MIN_NORMAL,Math.nextDown(Float.MIN_NORMAL),Float.MAX_VALUE,Math.nextUp(1f),Float.NaN,Float.POSITIVE_INFINITY,Float.NEGATIVE_INFINITY}){
                byte[] b=write(g->g.writeNumber(f),DEFAULT);wire("float-widening",b,V.of((double)f),"double");
            }
            nativeError("builder-none","fixture",JSON.writeValueAsBytes(Map.of("type","None","value","")),2,34,"None");
            for(String type:List.of("Illegal","MinKey","MaxKey")){
                byte[] b=REF.fixture(type,"");Report.bytes("fixture"+type,b);String expected=switch(type){case "None"->"none";case "Illegal"->"illegal";case "MinKey"->"min-key";default->"max-key";};assertEquals(expected,inspection(b).get("type").asString());
            }
        });
    }
    @TestFactory Stream<DynamicTest> dateExtremaAndProjection() {
        return Stream.of(Long.MIN_VALUE,Long.MIN_VALUE+1,-1L,0L,1L,Long.MAX_VALUE-1,Long.MAX_VALUE).map(ms->{byte[] b=Spec.cat(Spec.head(0x1c),Spec.le(ms,8));String id="date-"+ms;
            return test(id,Report.Policy.JSON_PROJECTION,b,ms,()->{wire(id,b,V.of(ms),"utc-date");equal(V.of(ms),json(REF.call("dump",b,2|16).text()),"$.json",false);nativeError(id,"dump",b,2,10,"JSON");});});
    }
    @TestFactory Stream<DynamicTest> tagsWidthsBoundariesAndChains() {
        var values=List.of(BigInteger.ZERO,BigInteger.ONE,BigInteger.valueOf(255),BigInteger.valueOf(256),BigInteger.valueOf(Integer.MAX_VALUE),BigInteger.valueOf(Long.MAX_VALUE),BigInteger.ONE.shiftLeft(63),BigInteger.ONE.shiftLeft(64).subtract(BigInteger.ONE));
        return values.stream().flatMap(tag->Stream.of(false,true).filter(wide->wide||tag.bitLength()<=8).map(wide->{
            byte[] b=Spec.tag(tag,wide,Spec.tag(BigInteger.valueOf(7),false,Spec.head(0x35)));String id="tag-"+tag+"-wide="+wide;
            return test(id,Report.Policy.JSON_PROJECTION,b,"native complete chain; Java innermost tag",()->{
                wire(id,b,V.of(5),"tagged");var tags=inspection(b).get("tags");assertEquals(tag.toString(),tags.get(0).asString());assertEquals("7",tags.get(1).asString());
                for(boolean stream:new boolean[]{false,true})try(var p=(VPackParser)(stream?DEFAULT.createParser(new Chunked(b,1)):DEFAULT.createParser(b))){p.nextToken();assertEquals(7,p.getLastTagNumber());}
                byte[] single=Spec.tag(tag,wide,Spec.head(0x35));
                for(boolean stream:new boolean[]{false,true})try(var p=(VPackParser)(stream?DEFAULT.createParser(new Chunked(single,1)):DEFAULT.createParser(single))){p.nextToken();assertEquals(tag.longValue(),p.getLastTagNumber(),"Java exposes the unsigned tag's long bit pattern");}
                equal(V.of(5),json(REF.call("dump",b).text()),"$.json",false);
                nativeError(id,"validate",b,2|512,41,"Tagged");javaReject(b,VPackFactory.builder().enable(VPackReadFeature.FAIL_ON_TAGGED_VALUES).build(),"FAIL_ON_TAGGED_VALUES");
                byte[] java=write(g->{g.writeTaggedValuePrefix(tag.longValue());g.writeTaggedValuePrefix(7);g.writeNumber(5);},DEFAULT);wire(id+"-java",java,V.of(5),"tagged");assertEquals(tag.toString(),inspection(java).get("tags").get(0).asString());
            });}));
    }
}
