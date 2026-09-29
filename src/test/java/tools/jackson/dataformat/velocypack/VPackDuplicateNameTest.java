package tools.jackson.dataformat.velocypack;

import org.junit.jupiter.api.Test;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.StreamReadFeature;
import tools.jackson.core.exc.StreamReadException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VPackDuplicateNameTest {
    @Test
    void duplicateNamesFollowEffectiveStrictReadFlag() throws Exception {
        byte[] body = VPackObjectParserTest.body(
                VPackObjectParserTest.pair("a", new byte[] { 0x31 }),
                VPackObjectParserTest.pair("a", new byte[] { 0x32 }));
        byte[] input = VPackObjectParserTest.object(1, true, body,
                new long[] { 3, 6 });
        try (JsonParser parser = new VPackFactory().createParser(input)) {
            while (parser.nextToken() != null) { }
        }

        VPackFactory strict = new VPackFactory().rebuild()
                .enable(StreamReadFeature.STRICT_DUPLICATE_DETECTION).build();
        try (JsonParser parser = strict.createParser(input)) {
            assertThrows(StreamReadException.class, () -> {
                while (parser.nextToken() != null) { }
            });
        }
    }

    @Test
    void equalNameSortedIndexTiesAreStructurallyValid() throws Exception {
        byte[] body = VPackObjectParserTest.body(
                VPackObjectParserTest.pair("a", new byte[] { 0x31 }),
                VPackObjectParserTest.pair("a", new byte[] { 0x32 }));
        byte[] input = VPackObjectParserTest.object(1, true, body,
                new long[] { 6, 3 });
        try (JsonParser parser = new VPackFactory().createParser(input)) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("a", parser.currentName());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
        }
    }
}
