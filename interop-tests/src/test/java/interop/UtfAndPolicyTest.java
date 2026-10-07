package interop;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.*;
import org.junit.jupiter.api.*;
import com.arangodb.jackson.dataformat.velocypack.*;
import tools.jackson.core.JsonToken;
import static interop.Harness.*;
import static org.junit.jupiter.api.Assertions.*;

class UtfAndPolicyTest {
    @TestFactory Stream<DynamicTest> lenientReadsUseExplicitReplacementVectors() {
        var vectors=List.of(new byte[]{(byte)0xc0,(byte)0xaf},new byte[]{(byte)0x80},new byte[]{(byte)0xed,(byte)0xa0,(byte)0x80},new byte[]{(byte)0xf4,(byte)0x90,(byte)0x80,(byte)0x80},new byte[]{(byte)0xe2,(byte)0x82});
        var replacements=List.of("\ufffd\ufffd","\ufffd","\ufffd","\ufffd\ufffd\ufffd\ufffd","\ufffd");
        return IntStream.range(0,vectors.size()).mapToObj(i->{byte[] payload=vectors.get(i),b=Spec.cat(Spec.head(0x40+payload.length),payload);String expected=replacements.get(i);
            return ScalarFamiliesTest.test("lenient-utf-"+i,Report.Policy.JSON_PROJECTION,b,expected,()->{
                nativeError("strict-utf-"+i,"validate",b,2,15,"UTF-8");REF.call("validate",b,0).require();
                var f=VPackFactory.builder().enable(VPackReadFeature.LENIENT_UTF_ENCODING).build();
                for(boolean stream:new boolean[]{false,true})equal(V.of(expected),javaRead(b,stream,f),"$.lenient",false);
                byte[] obj=Spec.compact(true,List.of(b,Spec.head(0x18)),1);
                for(boolean stream:new boolean[]{false,true})equal(V.of(Collections.singletonMap(expected,null)),javaRead(obj,stream,f),"$.lenientName",false);
            });});
    }
    @TestFactory Stream<DynamicTest> strictNativeSurrogateParsingAndValidPairs() {
        return Stream.of("\\ud800","\\udc00","\\ud800\\ud800").map(escape->DynamicTest.dynamicTest("native-surrogate-"+escape,()->Report.run("native-surrogate-"+escape,Report.Policy.REJECTION,"\""+escape+"\"","native code 15",()->nativeError("surrogate-json","parse",("\""+escape+"\"").getBytes(StandardCharsets.UTF_8),2,15,"surrogate"))));
    }
    @Test void zeroLengthCustomHasExplicitReferenceAndJavaPolicies() throws Throwable {
        byte[] b={(byte)0xf4,0};
        Report.run("zero-length-custom-policy",Report.Policy.REJECTION,null,"native rejects; Java exposes an empty opaque payload",()->{
            nativeError("empty-custom-native","validate",b,2,50,"Custom");
            // The specification describes an unsigned payload length without a
            // positive minimum. The pinned validator requires a nonempty payload.
            for(boolean stream:new boolean[]{false,true})equal(new V("custom",0xf4+":"),javaRead(b,stream),"$.javaOpaque",false);
        });
    }
    @Test void javaReservedBytesAndMultipleRootsHaveSeparateContracts() throws Throwable {
        Report.run("java-rejection-policies",Report.Policy.REJECTION,null,"reserved and absent types rejected; multiple roots exposed",()->{
            for(int h:Stream.concat(Stream.of(0x15,0x16),IntStream.rangeClosed(0xd8,0xed).boxed()).toList())javaReject(Spec.head(h),DEFAULT,"reserved");
            javaReject(Spec.head(0),DEFAULT,"none");javaReject(Spec.head(0x17),DEFAULT,"illegal");
            byte[] roots={0x18,0x1a};nativeError("native-single-root","validate",roots,2,50,"length");
            // Jackson streaming APIs intentionally expose multiple root values.
            try(var p=DEFAULT.createParser(roots)){assertEquals(JsonToken.VALUE_NULL,p.nextToken());assertEquals(JsonToken.VALUE_TRUE,p.nextToken());assertNull(p.nextToken());}
        });
    }
}
