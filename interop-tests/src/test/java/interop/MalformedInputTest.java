package interop;

import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.*;
import org.junit.jupiter.api.*;
import tools.jackson.core.exc.*;
import static interop.Harness.*;
import static org.junit.jupiter.api.Assertions.*;

class MalformedInputTest {
    @Test void nativeAndJavaDepthLimitsUseExplicitBoundaryPolicies() throws Throwable {
        Report.run("depth-boundaries",Report.Policy.REJECTION,null,"Java accepts <=8; native limit 8 accepts <8",()->{
            var f=com.arangodb.jackson.dataformat.velocypack.VPackFactory.builder().streamReadConstraints(tools.jackson.core.StreamReadConstraints.builder().maxNestingDepth(8).build()).build();
            for(int d:new int[]{7,8,9}){
                String json="[".repeat(d)+"1"+"]".repeat(d);byte[] b=REF.parse(json,3);
                if(d<=8)equal(json(json),javaRead(b,false,f),"$.javaDepth"+d,false);
                else assertThrows(StreamConstraintsException.class,()->javaRead(b,false,f));
                var nativeResult=REF.call("validate",b,2,8);
                if(d<8)nativeResult.require();else {assertEquals(1,nativeResult.status());assertEquals(24,nativeResult.code());}
            }
        });
    }
    @Test void readerRejectsInvalidIndexOffset() throws Throwable {
        byte[] b=Spec.indexedArray(List.of(Spec.head(0x31),Spec.string("x",false)),1,false);b[b.length-1]=0;
        Report.run("invalid-array-index",Report.Policy.REJECTION,null,"invalid index error",()->{
            nativeError("invalid-index-native","validate",b,2,50,"index");javaReject(b,DEFAULT,"index");
        });
    }
    record Bad(String id,byte[] bytes,int code,long flags,long depth) {}
    static List<Bad> cases() {
        var list=new ArrayList<Bad>();
        for(int head:Stream.concat(Stream.of(0x15,0x16),IntStream.rangeClosed(0xd8,0xed).boxed()).toList())list.add(new Bad("reserved-"+Integer.toHexString(head),Spec.head(head),51,2,64));
        list.add(new Bad("empty",new byte[0],50,2,64));
        for(byte[] valid:List.of(Spec.doubleValue(1.25),Spec.integer(java.math.BigInteger.valueOf(1234),8,false),Spec.string("string",true),Spec.binary(new byte[]{1,2,3},8),Spec.custom(0xfd,new byte[]{1,2,3}),Spec.tag(java.math.BigInteger.valueOf(42),true,Spec.head(0x18)))) {
            for(int len:new int[]{1,valid.length-1})list.add(new Bad("truncated-"+Integer.toHexString(valid[0]&255)+"-"+len,Arrays.copyOf(valid,len),50,2,64));
        }
        list.add(new Bad("trailing-data",new byte[]{0x18,0x18},50,2,64));
        list.add(new Bad("oversize-string",Spec.cat(Spec.head(0xbf),Spec.le(Long.MAX_VALUE,8)),50,2,64));
        list.add(new Bad("oversize-binary",Spec.cat(Spec.head(0xc7),Spec.le(Long.MAX_VALUE,8)),50,2,64));
        list.add(new Bad("oversize-custom",Spec.cat(Spec.head(0xff),Spec.le(Long.MAX_VALUE,8)),50,2,64));
        list.add(new Bad("invalid-key",Spec.compact(true,List.of(Spec.head(0x19),Spec.head(0x31)),1),50,2,64));
        list.add(new Bad("empty-custom",new byte[]{(byte)0xf4,0},50,2,64));
        byte[] index=Spec.indexedArray(List.of(Spec.head(0x31),Spec.string("x",false)),1,false);index[index.length-1]=0;
        list.add(new Bad("invalid-index",index,50,2,64));
        byte[] invalidLength=Spec.compact(false,List.of(Spec.head(0x31)),1);invalidLength[1]=127;list.add(new Bad("invalid-container-length",invalidLength,50,2,64));
        list.add(new Bad("invalid-utf8",new byte[]{0x42,(byte)0xc0,(byte)0xaf},15,2,64));
        byte[] nested=Spec.head(0x18);for(int d=0;d<12;d++)nested=Spec.compact(false,List.of(nested),1);list.add(new Bad("depth-limit",nested,24,2,8));
        list.add(new Bad("bcd-invalid-digit",new byte[]{(byte)0xc8,1,0,0,0,0,(byte)0xfa},2,2,64));
        return list;
    }
    @TestFactory Stream<DynamicTest> nativeMalformedInputsAreIsolated() {
        return cases().stream().map(c->DynamicTest.dynamicTest(c.id,()->Report.run("malformed-"+c.id,Report.Policy.REJECTION,null,"native error="+c.code,()->{
            Report.bytes("input",c.bytes);var result=isolate(c);
            for(String op:List.of("validate","dump","inspect")){var r=result.get(op);assertEquals(1,r.get("status").asInt(),op);assertEquals(c.code,r.get("code").asInt(),op+": "+r);assertFalse(r.get("message").asString().isBlank());}
            assertEquals(0,result.get("outstanding").asLong());Report.outcome(c.id,Report.Policy.REJECTION,c.code==2?"NATIVE_UNSUPPORTED":"REJECTED");
            if(c.id.startsWith("reserved"))Report.coverage("reserved",c.bytes[0]&255,"REJECTED");
        })));
    }
    static tools.jackson.databind.JsonNode isolate(Bad c) throws Exception {
        Path dir=Path.of("target/reports/isolation").toAbsolutePath();Files.createDirectories(dir);
        Path input=dir.resolve(c.id+".vpack"),log=dir.resolve(c.id+".log");Files.write(input,c.bytes);
        var command=List.of(Path.of(System.getProperty("java.home"),"bin/java").toString(),"--enable-native-access=ALL-UNNAMED","-Xmx128m","-XX:ErrorFile="+dir+"/"+c.id+"-hs_err_pid%p.log","-Dinterop.library="+Path.of("target/native-build/libvpack_reference.so").toAbsolutePath(),"-cp",System.getProperty("surefire.test.class.path",System.getProperty("java.class.path")),MalformedProbe.class.getName(),input.toString(),Long.toString(c.flags),Long.toString(c.depth));
        var p=new ProcessBuilder(command).directory(dir.toFile()).redirectErrorStream(true).redirectOutput(log.toFile()).start();
        boolean finished=p.waitFor(10,TimeUnit.SECONDS);if(!finished){p.destroyForcibly();p.waitFor(5,TimeUnit.SECONDS);fail("isolated native timeout: "+c.id+" log="+log);}
        String output=Files.readString(log);Report.detail("isolatedLog",log.toString());assertEquals(0,p.exitValue(),"native crash/child failure: "+c.id+" "+output);
        String json=output.lines().filter(s->s.startsWith("{" )).reduce((a,b)->b).orElseThrow(()->new AssertionError("no native result: "+output));
        return JSON.readTree(json);
    }
    @Test void javaStructuralRejectionsAndResourceLimits() throws Throwable {
        Report.run("java-malformed",Report.Policy.REJECTION,null,"specified read errors and constraints",()->{
            for(byte[] b:List.of(new byte[]{0x1b},new byte[]{0x27,1},new byte[]{0x42,1},new byte[]{(byte)0xee,1},new byte[]{(byte)0xef,1},new byte[]{(byte)0xf4,3,1})) {
                for(boolean stream:new boolean[]{false,true}){var e=assertThrows(StreamReadException.class,()->javaRead(b,stream));assertTrue(e.getMessage().matches("(?s).*(Truncated|truncated|EOF|end-of-input|Unexpected end).*"),e.getMessage());}
            }
            for(byte[] b:List.of(Spec.cat(Spec.head(0xbf),Spec.le(Long.MAX_VALUE,8)),Spec.cat(Spec.head(0xc7),Spec.le(Long.MAX_VALUE,8))))for(boolean stream:new boolean[]{false,true})assertThrows(StreamReadException.class,()->javaRead(b,stream));
            var f=com.arangodb.jackson.dataformat.velocypack.VPackFactory.builder().streamReadConstraints(tools.jackson.core.StreamReadConstraints.builder().maxStringLength(4).maxNameLength(4).maxNestingDepth(4).build()).build();
            assertThrows(StreamConstraintsException.class,()->javaRead(Spec.string("12345",false),false,f));
            byte[] object=Spec.compact(true,List.of(Spec.string("12345",false),Spec.head(0x18)),1);assertThrows(StreamConstraintsException.class,()->javaRead(object,false,f));
            byte[] depth=REF.parse("[[[[[1]]]]]",3);assertThrows(StreamConstraintsException.class,()->javaRead(depth,false,f));
        });
    }
}
