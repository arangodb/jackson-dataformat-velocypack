package tools.jackson.dataformat.velocypack;

import java.io.ByteArrayInputStream;
import org.junit.jupiter.api.Test;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.exc.StreamReadException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VPackNestedLayoutReuseTest {
    private final VPackFactory factory = new VPackFactory();

    @Test
    void nestedFixedAndCompactLayoutsTraverseFromBorrowedAndStreamRoots() throws Exception {
        // Indexed parent -> compact array [1]. Every field is a local-profile literal.
        byte[] compactArray = { 0x06, 0x08, 0x01, 0x13, 0x04, 0x31, 0x01, 0x03 };
        // Indexed parent -> equal-size fixed array [1].
        byte[] fixedArray = { 0x06, 0x07, 0x01, 0x02, 0x03, 0x31, 0x03 };
        // Sorted object {"a":{"a":1}} with a compact nested object.
        byte[] compactObject = { 0x0b, 0x0c, 0x01, 0x41, 0x61,
                0x14, 0x06, 0x41, 0x61, 0x31, 0x01, 0x03 };

        assertTokens(factory.createParser(compactArray),
                JsonToken.START_ARRAY, JsonToken.START_ARRAY, JsonToken.VALUE_NUMBER_INT,
                JsonToken.END_ARRAY, JsonToken.END_ARRAY);
        assertTokens(factory.createParser(new ByteArrayInputStream(fixedArray)),
                JsonToken.START_ARRAY, JsonToken.START_ARRAY, JsonToken.VALUE_NUMBER_INT,
                JsonToken.END_ARRAY, JsonToken.END_ARRAY);
        assertTokens(factory.createParser(compactObject),
                JsonToken.START_OBJECT, JsonToken.PROPERTY_NAME, JsonToken.START_OBJECT,
                JsonToken.PROPERTY_NAME, JsonToken.VALUE_NUMBER_INT,
                JsonToken.END_OBJECT, JsonToken.END_OBJECT);
    }

    @Test
    void nestedLayoutCannotExtendPastItsParentBody() throws Exception {
        // The nested equal array claims four bytes, but its indexed parent grants it three.
        byte[] parentOverflow = { 0x06, 0x07, 0x01, 0x02, 0x04, 0x31, 0x03 };
        try (JsonParser parser = factory.createParser(parentOverflow)) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertThrows(StreamReadException.class, parser::nextToken);
        }
    }

    @Test
    void enteringNestedContainerDoesNotConsumeTheFollowingSequenceRoot() throws Exception {
        byte[] nestedThenScalar = { 0x06, 0x07, 0x01, 0x02, 0x03, 0x31, 0x03, 0x32 };
        try (JsonParser parser = factory.createParser(new ByteArrayInputStream(nestedThenScalar))) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(1, parser.getIntValue());
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(2, parser.getIntValue());
            assertNull(parser.nextToken());
        }
    }

    private static void assertTokens(JsonParser parser, JsonToken... expected) throws Exception {
        try (parser) {
            for (JsonToken token : expected) assertEquals(token, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }
}
