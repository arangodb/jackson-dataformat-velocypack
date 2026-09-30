package tools.jackson.dataformat.velocypack;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.StringReader;
import java.io.StringWriter;

import org.junit.jupiter.api.Test;

import tools.jackson.core.JsonParser;
import tools.jackson.core.ObjectReadContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class VPackFactoryReadTest {
    @Test
    void builderPublishesImmutableFeatureAndConstraintSnapshots() {
        VPackReadConstraints read = VPackReadConstraints.builder()
                .maxRootValueBytes(17).maxRootEntries(3).maxRootNameBytes(9).build();
        VPackWriteConstraints write = VPackWriteConstraints.builder()
                .maxRootValueBytes(18).maxRootEntries(4).maxRootNameBytes(10)
                .maxNumberDigits(11).build();
        VPackFactory original = VPackFactory.builder()
                .vpackReadConstraints(read).vpackWriteConstraints(write).build();
        VPackFactory copy = original.rebuild().build();

        assertEquals("VPack", original.getFormatName());
        assertEquals(read, original.vpackReadConstraints());
        assertEquals(write, original.vpackWriteConstraints());
        assertEquals(original.vpackReadConstraints(), copy.vpackReadConstraints());
        assertNotSame(original, copy);
        assertEquals(VPackWriteFeature.USE_EQUAL_LENGTH_ARRAYS.getMask(),
                VPackWriteFeature.collectDefaults());

        try (VPackParser parser = (VPackParser) original.createParser(new byte[] { 0x18 })) {
            assertSame(read, parser.vpackReadConstraints());
            assertEquals(original.getFormatReadFeatures(), parser.formatReadFeatures());
        }
    }

    @Test
    void allRequiredBinarySourcesUseTheSameScalarParser() throws Exception {
        VPackFactory factory = new VPackFactory();
        try (JsonParser parser = factory.createParser(new byte[] { 0x18, 0x1A })) {
            assertEquals(tools.jackson.core.JsonToken.VALUE_NULL, parser.nextToken());
            assertEquals(tools.jackson.core.JsonToken.VALUE_TRUE, parser.nextToken());
        }
        try (JsonParser parser = factory.createParser(new byte[] { 0x7F, 0x18, 0x1A, 0x7F }, 1, 2)) {
            assertEquals(tools.jackson.core.JsonToken.VALUE_NULL, parser.nextToken());
            assertEquals(tools.jackson.core.JsonToken.VALUE_TRUE, parser.nextToken());
        }
        try (JsonParser parser = factory.createParser(
                new ByteArrayInputStream(new byte[] { 0x30, 0x31 }))) {
            assertEquals(tools.jackson.core.JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(tools.jackson.core.JsonToken.VALUE_NUMBER_INT, parser.nextToken());
        }
        DataInput input = new DataInputStream(new ByteArrayInputStream(new byte[] { 0x1A }));
        try (JsonParser parser = factory.createParser(ObjectReadContext.empty(), input)) {
            assertEquals(tools.jackson.core.JsonToken.VALUE_TRUE, parser.nextToken());
        }
    }

    @Test
    void textSourcesAndWriterTargetsRemainBinaryOnly() {
        VPackFactory factory = new VPackFactory();
        assertThrows(UnsupportedOperationException.class, () -> factory.createParser("18"));
        assertThrows(UnsupportedOperationException.class,
                () -> factory.createParser(new StringReader("18")));
        assertThrows(UnsupportedOperationException.class,
                () -> factory.createParser(new char[] { '1', '8' }));
        assertThrows(UnsupportedOperationException.class, () -> factory.createGenerator(
                new StringWriter()));
    }

}
