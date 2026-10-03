package com.arangodb.jackson.dataformat.velocypack;
import java.io.ByteArrayOutputStream;
import java.lang.management.ManagementFactory;
import tools.jackson.core.*;
/** Diagnostic micro-experiment; fresh generator, target storage and returned array each operation. */
public class DepthExperiment {
    static final byte[] scalar=new byte[262144];
    static final com.sun.management.ThreadMXBean mx=(com.sun.management.ThreadMXBean)ManagementFactory.getThreadMXBean();
    static volatile int sink;
    static InstrumentedVPackGenerator counted;
    static CountingOut countedOut;
    static class CountingOut extends ByteArrayOutputStream {
        long growthCopies,storageCopies,returnedCopies;
        @Override public synchronized void write(byte[] b,int off,int len) {
            int old=buf.length;super.write(b,off,len);if(buf.length!=old)growthCopies+=old;storageCopies+=len;
        }
        @Override public synchronized byte[] toByteArray(){returnedCopies+=count;return super.toByteArray();}
    }
    static byte[] encode(int depth,boolean objects,boolean count) {
        var out=new CountingOut();
        var g=new InstrumentedVPackGenerator(ObjectWriteContext.empty(),BaseTestForVPack.testIOContext(),StreamWriteFeature.collectDefaults(),VPackWriteFeature.collectDefaults(),out);
        try(g){for(int i=0;i<depth;i++){if(objects){g.writeStartObject();g.writeName("child");}else g.writeStartArray();}
            g.writeBinary(scalar);for(int i=0;i<depth;i++){if(objects)g.writeEndObject();else g.writeEndArray();}}
        if(count){counted=g;countedOut=out;}
        return out.toByteArray();
    }
    static void cursor(boolean sorted) {
        var mapper=new VPackMapper();var tree=mapper.readTree(mapper.writeValueAsBytes(ArangoDocuments.cursor(ArangoDocuments.customers(1000,ArangoDocuments.SEED))));
        var out=new CountingOut();var g=new InstrumentedVPackGenerator(ObjectWriteContext.empty(),BaseTestForVPack.testIOContext(),StreamWriteFeature.collectDefaults(),VPackWriteFeature.collectDefaults(),out);
        if(sorted)g.enable(VPackWriteFeature.WRITE_OBJECT_KEYS_SORTED);
        try(g){mapper.writeTree(g,tree);}
        System.out.printf("cursor,%s,%d,%d,%d,%d,%d,%d,%d%n",sorted,out.size(),g.growthCopies,g.headerCopies,g.sortingCopies,g.capacityBytes,out.growthCopies,out.storageCopies);
    }
    public static void main(String[] args) {
        mx.setThreadAllocatedMemoryEnabled(true);long tid=Thread.currentThread().threadId();
        System.out.println("kind,depth,round,operations,us_per_op,allocated_B_per_op,output_B,growth_copied_B,header_moved_B,sort_copied_B,capacity_B,output_growth_copied_B,output_storage_copied_B,returned_array_copied_B");
        for(boolean objects:new boolean[]{false,true})for(int depth:new int[]{0,1,2,4,8,16,32,64,128}){
            for(long end=System.nanoTime()+500_000_000L;System.nanoTime()<end;)sink=encode(depth,objects,false).length;
            for(int r=0;r<3;r++){
                long allocated=mx.getThreadAllocatedBytes(tid),start=System.nanoTime();int n=0;
                do {sink=encode(depth,objects,false).length;++n;}while(System.nanoTime()-start<500_000_000L);
                long elapsed=System.nanoTime()-start,bytes=mx.getThreadAllocatedBytes(tid)-allocated;
                byte[] result=encode(depth,objects,true);
                System.out.printf("%s,%d,%d,%d,%.3f,%.3f,%d,%d,%d,%d,%d,%d,%d,%d%n",objects?"object":"array",depth,r,n,elapsed/1000.0/n,(double)bytes/n,result.length,counted.growthCopies,counted.headerCopies,counted.sortingCopies,counted.capacityBytes,countedOut.growthCopies,countedOut.storageCopies,countedOut.returnedCopies);
            }
        }
        System.out.println("fixture,sorted,output_B,growth_copied_B,header_moved_B,sort_copied_B,capacity_B,output_growth_copied_B,output_storage_copied_B");cursor(false);cursor(true);
    }
}
