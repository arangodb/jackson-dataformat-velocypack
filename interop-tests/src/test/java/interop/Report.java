package interop;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import org.junit.jupiter.api.function.Executable;

final class Report {
    enum Policy { EXACT_VALUE, NUMERIC_VALUE, LOGICAL_TYPE, JSON_PROJECTION, UNSUPPORTED_OPERATION, REJECTION }
    private static final Path DIR=Path.of("target/reports");
    private static final Map<String,Long> COUNTS=new TreeMap<>();
    private static final Map<String,Set<String>> TYPES=new TreeMap<>();
    private static final Set<Integer> HEADS=new TreeSet<>();
    private static final Properties META=new Properties();
    private static final ThreadLocal<Map<String,Object>> CONTEXT=new ThreadLocal<>();
    static {
        try {
            Files.createDirectories(DIR);
            try(var in=Files.newInputStream(Path.of("target/native.properties"))){META.load(in);}
            if (!META.getProperty("native.revision").equals(NativeVPackReference.revision())) throw new AssertionError("Library revision differs from native.properties");
            META.setProperty("java.version",System.getProperty("java.runtime.version"));
            META.setProperty("java.vendor",System.getProperty("java.vendor"));
            META.setProperty("jackson.version",System.getProperty("interop.jacksonVersion","3.2.0"));
            META.setProperty("seed",System.getProperty("interop.seed","1592614637"));
            META.setProperty("json.cases",System.getProperty("interop.jsonCases","10000"));
            META.setProperty("typed.cases",System.getProperty("interop.typedCases","5000"));
            META.setProperty("limits","depth=8;bytes=1048576;decimalDigits=4096;nativeDepth=64");
            META.setProperty("native.options","STRICT_UTF=2;UNIQUE=4;COMPACT=1;PADDING=128;externalsDisallowed=true;unsupported=FailOnUnsupportedType");
            try(var out=Files.newOutputStream(DIR.resolve("run.properties"))){META.store(out,"Native VelocyPack differential run");}
            Files.writeString(DIR.resolve("outcomes.tsv"),"case\tpolicy\toutcome\n");
            Files.writeString(DIR.resolve("failures.jsonl"),"");
            Runtime.getRuntime().addShutdownHook(new Thread(Report::finish));
        } catch(IOException e){throw new UncheckedIOException(e);}
    }
    static void run(String id,Policy policy,String json,Object expected,Executable body) throws Throwable {
        var c=new LinkedHashMap<String,Object>(); c.put("case",id); c.put("policy",policy.name());c.put("metadata",new TreeMap<>(META));
        c.put("json",json);c.put("expected",String.valueOf(expected));CONTEXT.set(c);
        try { body.execute(); outcome(id,policy,"CONTRACT_PASSED"); }
        catch(Throwable e) {
            c.put("actual",e.toString()); c.put("firstDifferingPath",e instanceof Harness.Difference d?d.path:"$ (operation failed)");
            c.put("cause",e.getCause()==null?"":e.getCause().toString());
            Files.writeString(DIR.resolve("failures.jsonl"),Harness.JSON.writeValueAsString(c)+"\n",StandardOpenOption.APPEND);
            outcome(id,policy,"FAILED");
            throw new AssertionError("case="+id+" policy="+policy+" seed="+META.getProperty("seed")+" revision="+META.getProperty("native.revision")+" path="+c.get("firstDifferingPath")+"; "+e+"; see target/reports/failures.jsonl",e);
        } finally { CONTEXT.remove(); }
    }
    static void detail(String name,Object value) { var c=CONTEXT.get();if(c!=null)c.put(name,value); }
    static synchronized void metadata(String name,String value) {
        META.setProperty(name,value);
        try(var out=Files.newOutputStream(DIR.resolve("run.properties"))){META.store(out,"Native VelocyPack differential run");}catch(IOException e){throw new UncheckedIOException(e);}
    }
    static void bytes(String name,byte[] bytes) {detail(name+"Hex",HexFormat.of().formatHex(bytes));}
    static synchronized void outcome(String id,Policy policy,String result) {
        COUNTS.merge(result,1L,Long::sum);
        try{Files.writeString(DIR.resolve("outcomes.tsv"),id+"\t"+policy+"\t"+result+"\n",StandardOpenOption.APPEND);}catch(IOException e){throw new UncheckedIOException(e);}
    }
    static synchronized void coverage(String type,int head,String outcome) { TYPES.computeIfAbsent(type,x->new TreeSet<>()).add(outcome);if(head>=0)HEADS.add(head); }
    private static synchronized void finish() {
        try {
            var s=new StringBuilder();COUNTS.forEach((k,v)->s.append(k).append('=').append(v).append('\n'));
            s.append("native.outstanding=").append(NativeVPackReference.outstanding()).append('\n');
            s.append("coverage.heads=").append(HEADS.size()).append('\n');
            var required=new TreeSet<>(List.of("none","illegal","null","bool","array","object","double","utc-date","external","min-key","max-key","int","uint","smallint","string","binary","bcd","custom","tagged"));
            required.removeAll(TYPES.keySet());s.append("coverage.missingTypes=").append(String.join(",",required)).append('\n');
            var missing=new ArrayList<String>();for(int h=0;h<256;h++)if(!HEADS.contains(h))missing.add(String.format("%02x",h));s.append("coverage.missingHeads=").append(String.join(",",missing)).append('\n');
            Files.writeString(DIR.resolve("summary.properties"),s);
            var t=new StringBuilder("ValueType\toutcomes\n");TYPES.forEach((k,v)->t.append(k).append('\t').append(String.join(",",v)).append('\n'));
            t.append("\nHeads exercised\n");HEADS.forEach(h->t.append(String.format("%02x ",h)));
            Files.writeString(DIR.resolve("type-coverage.tsv"),t+"\n");
        }catch(IOException e){e.printStackTrace();}
    }
}
