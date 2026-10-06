package interop;

import java.nio.file.*;
import java.util.*;

/** Launched in a fresh JVM; never inspects or dumps an unvalidated buffer. */
public final class MalformedProbe {
    public static void main(String[] args) throws Exception {
        byte[] bytes=Files.readAllBytes(Path.of(args[0]));long flags=Long.parseLong(args[1]),depth=Long.parseLong(args[2]);
        var ref=new NativeVPackReference();var out=new LinkedHashMap<String,Object>();out.put("revision",NativeVPackReference.revision());
        for(String op:List.of("validate","dump","inspect")){
            var r=ref.call(op,bytes,flags,depth);out.put(op,Map.of("status",r.status(),"code",r.code(),"message",r.message()));
        }
        out.put("outstanding",NativeVPackReference.outstanding());System.out.println(Harness.JSON.writeValueAsString(out));
    }
}
