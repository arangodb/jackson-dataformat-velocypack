package interop;

import java.util.*;
import org.junit.jupiter.api.Test;
import static interop.Harness.*;
import static org.junit.jupiter.api.Assertions.*;

class NativeSmokeTest {
    @Test void nativeLoadingErrorsAndOwnership() throws Throwable {
        Report.run("native-smoke",Report.Policy.EXACT_VALUE,"{\"text\":\"a\\u0000b\"}","independent value and no live native allocations",()->{
            assertTrue(NativeVPackReference.revision().matches("[0-9a-f]{40}"));
            for(int i=0;i<1000;i++) {
                byte[] b=REF.parse("{\"text\":\"a\\u0000b\"}",3);
                equal(V.of(Map.of("text","a\0b")),nativeValue(inspection(b)),"$",false);
                nativeError("invalid-json","parse",new byte[]{'{'},2,11,"Expecting");
            }
            assertEquals(0,NativeVPackReference.outstanding());
            var fixtureError=REF.call("fixture",JSON.writeValueAsBytes(Map.of("type","unknown","value","")));
            assertEquals(2,fixtureError.status());assertEquals(999,fixtureError.code());assertTrue(fixtureError.message().contains("unsupported fixture kind"));
            assertEquals(0,NativeVPackReference.outstanding());
            byte[] owned=REF.parse("\"retained\"",2);REF.parse("123",2);
            equal(V.of("retained"),javaRead(owned,false),"$",false);
        });
    }
    @Test void nativeOwnedExternalResolution() throws Throwable {
        Report.run("external-owned",Report.Policy.JSON_PROJECTION,null,42,()->{
            var result=JSON.readTree(REF.call("external",new byte[0]).require());
            equal(V.of(42),nativeValue(result),"$",false);
            Report.coverage("external",0x1d,"NATIVE_OWNED_RESOLVED");
            javaReject(Spec.cat(Spec.head(0x1d),new byte[8]),DEFAULT,"external");
            nativeError("external-wire","validate",Spec.cat(Spec.head(0x1d),new byte[8]),2,37,"External");
        });
    }
}
