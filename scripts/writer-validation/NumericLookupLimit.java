package com.arangodb.jackson.dataformat.velocypack;
import java.nio.file.*;
import java.util.Arrays;
public class NumericLookupLimit {
    public static void main(String[] args) throws Exception {
        int features=VPackWriteFeature.collectDefaults()|VPackWriteFeature.WRITE_OBJECT_KEYS_SORTED.getMask();
        features&=~VPackWriteFeature.WRITE_COMPACT_OBJECTS.getMask();
        java.util.function.Consumer<WriterReplay> calls=w->{w.g.writeStartObject();for(int i=6;i>=1;i--){w.g.writePropertyId(i);w.g.writeNumber(i);}w.g.writeEndObject();};
        byte[] actual=WriterReplay.encode(false,features,calls),legacy=WriterReplay.encode(true,features,calls);
        if(!Arrays.equals(actual,legacy))throw new AssertionError("Numeric sorting changed");
        Files.write(Path.of(args[0]).resolve("numeric-sorted-semantic-limit.vpack"),actual);
        System.out.println("Numeric sorted semantic fixture matches original writer byte for byte: "+actual.length+" B");
    }
}
