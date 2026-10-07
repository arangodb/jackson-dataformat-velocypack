package interop;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Per-call arenas own inputs; malloc-backed outputs are copied and freed in finally. */
final class NativeVPackReference {
    static final long COMPACT=1, STRICT_UTF=2, UNIQUE=4, BINARY_HEX=8, DATE_INTEGER=16,
            NONFINITE_STRING=32, TRANSLATED_KEYS=64, PADDING=128, NO_CUSTOM=256, NO_TAGS=512, NO_BCD=1024;
    static final MemoryLayout RESULT = MemoryLayout.structLayout(
            ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS,
            ValueLayout.JAVA_LONG, ValueLayout.ADDRESS, ValueLayout.JAVA_LONG);
    private static final Linker LINKER = Linker.nativeLinker();
    private static final SymbolLookup LIB;
    private static final Map<String, MethodHandle> CALLS;
    private static final MethodHandle FREE, OUTSTANDING;
    static {
        if (!System.getProperty("os.name").equals("Linux") || ValueLayout.ADDRESS.byteSize()!=8)
            throw new IllegalStateException("Native reference requires 64-bit Linux");
        Path library=Path.of(System.getProperty("interop.library", "target/native-build/libvpack_reference.so")).toAbsolutePath();
        if (!library.toFile().isFile()) throw new IllegalStateException("Build native reference with ./build-native.sh: "+library);
        LIB=SymbolLookup.libraryLookup(library,Arena.global());
        var desc=FunctionDescriptor.ofVoid(ValueLayout.ADDRESS,ValueLayout.JAVA_LONG,ValueLayout.JAVA_LONG,ValueLayout.JAVA_LONG,ValueLayout.ADDRESS);
        CALLS=Stream.of("parse","dump","validate","inspect","fixture","external").collect(Collectors.toMap(x->x,x->LINKER.downcallHandle(LIB.find("vp_"+x).orElseThrow(),desc)));
        FREE=LINKER.downcallHandle(LIB.find("vp_free").orElseThrow(),FunctionDescriptor.ofVoid(ValueLayout.ADDRESS));
        OUTSTANDING=LINKER.downcallHandle(LIB.find("vp_outstanding").orElseThrow(),FunctionDescriptor.of(ValueLayout.JAVA_LONG));
    }
    record Result(int status,int code,byte[] data,String message) {
        byte[] require() {
            if (status!=0) throw new NativeError(status,code,message);
            return data;
        }
        String text() { return new String(require(),StandardCharsets.UTF_8); }
    }
    static final class NativeError extends RuntimeException {
        final int status,code;
        NativeError(int status,int code,String message) { super("native status="+status+" code="+code+": "+message); this.status=status; this.code=code; }
    }
    Result call(String op,byte[] input,long flags,long depth) {
        if (input.length>1<<20) throw new IllegalArgumentException("case exceeds 1 MiB budget");
        try (Arena arena=Arena.ofConfined()) {
            var bytes=arena.allocate(Math.max(1,input.length)); bytes.asSlice(0,input.length).copyFrom(MemorySegment.ofArray(input));
            var result=arena.allocate(RESULT); result.fill((byte)0);
            try {
                CALLS.get(op).invokeExact(bytes,(long)input.length,flags,depth,result);
                int status=result.get(ValueLayout.JAVA_INT,0), code=result.get(ValueLayout.JAVA_INT,4);
                byte[] data=copy(result,8,16);
                String message=new String(copy(result,24,32),StandardCharsets.UTF_8);
                return new Result(status,code,data,message);
            } finally { FREE.invokeExact(result); }
        } catch (RuntimeException|Error e) { throw e; } catch (Throwable t) { throw new AssertionError("FFM call failed: "+op,t); }
    }
    private static byte[] copy(MemorySegment result,long pointer,long size) {
        long n=result.get(ValueLayout.JAVA_LONG,size);
        if (n<0 || n>16L<<20) throw new AssertionError("invalid native result size "+n);
        return n==0 ? new byte[0] : result.get(ValueLayout.ADDRESS,pointer).reinterpret(n).toArray(ValueLayout.JAVA_BYTE);
    }
    Result call(String op,byte[] input,long flags) { return call(op,input,flags,64); }
    Result call(String op,byte[] input) { return call(op,input,STRICT_UTF); }
    byte[] parse(String json,long flags) { return call("parse",json.getBytes(StandardCharsets.UTF_8),flags).require(); }
    byte[] fixture(String type,Object value) { return call("fixture",Harness.JSON.writeValueAsBytes(Map.of("type",type,"value",value))).require(); }
    static long outstanding() {
        try { return (long)OUTSTANDING.invokeExact(); } catch (Throwable t) { throw new AssertionError(t); }
    }
    static String revision() {
        try {
            var f=LINKER.downcallHandle(LIB.find("vp_revision").orElseThrow(),FunctionDescriptor.of(ValueLayout.ADDRESS));
            return ((MemorySegment)f.invokeExact()).reinterpret(41).getString(0);
        } catch (Throwable t) { throw new AssertionError(t); }
    }
}
