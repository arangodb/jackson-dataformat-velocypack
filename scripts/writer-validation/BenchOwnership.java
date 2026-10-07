package com.arangodb.jackson.dataformat.velocypack;
import java.util.Arrays;
public class BenchOwnership {
    static void check(java.util.function.Supplier<byte[]> operation) {
        byte[] first=operation.get(),second=operation.get();
        if(first==second||!Arrays.equals(first,second))throw new AssertionError("Fresh equal output required");
        first[0]^=1;
        if(!Arrays.equals(second,operation.get()))throw new AssertionError("Returned array aliases future output");
    }
    public static void main(String[] args) throws Exception {
        for(var format:Bench.Format.values()) {
            var b=new Bench();b.format=format;b.batchSize=1000;b.setup();
            check(b::treeWriteCursor);check(b::treeWriteDocument);check(b::pojoWriteCursor);check(b::streamingWriteCursor);
            check(()->{try{return b.sequenceWrite();}catch(java.io.IOException e){throw new RuntimeException(e);}});
            System.out.println("PASS fresh owned arrays: "+format+" tree cursor/document, POJO cursor, streaming cursor, sequence");
        }
    }
}
