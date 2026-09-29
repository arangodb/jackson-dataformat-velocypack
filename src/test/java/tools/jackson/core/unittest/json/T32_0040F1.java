package tools.jackson.core.unittest.json;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.ObjectWriteContext;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0040F1 {
private static final byte[] NESTED_OBJECT = {
            0x14, 0x1B,
            0x45, 'f', 'i', 'r', 's', 't',
            0x14, 0x12,
            0x46, 's', 'e', 'c', 'o', 'n', 'd', 0x33,
            0x45, 't', 'h', 'i', 'r', 'd', 0x19, 0x02,
            0x01
    };

    void parserTokenAccessFollowsCoreContractForLiteralVpackArray() throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(
                ObjectReadContext.empty(), new byte[] { 0x01 })) {
            assertNull(parser.currentToken());
            parser.clearCurrentToken();
            assertNull(parser.currentToken());
            assertNull(parser.getEmbeddedObject());
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(JsonToken.START_ARRAY, parser.currentToken());
            parser.clearCurrentToken();
            assertNull(parser.currentToken());
            assertThrows(UnsupportedOperationException.class, parser::readValueAsTree);
        }
    }

    void parserCurrentNameTracksNestedLiteralVpackObject() throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(NESTED_OBJECT)) {
            assertNull(parser.currentToken());
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("first", parser.currentName());
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals("first", parser.currentName());

            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("second", parser.currentName());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals("second", parser.currentName());

            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("third", parser.currentName());
            assertEquals(JsonToken.VALUE_FALSE, parser.nextToken());
            assertEquals("third", parser.currentName());

            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            parser.clearCurrentToken();
            assertNull(parser.currentToken());
        }
    }
private static void assertBigDecimalBytes(VPackFactory factory, BigDecimal value,
            byte[] expected) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = factory.createGenerator(
                ObjectWriteContext.empty(), output)) {
            generator.writeNumber(value);
        }
        assertArrayEquals(expected, output.toByteArray());
    }

    void __invoke_parserTokenAccessFollowsCoreContractForLiteralVpackArray() throws Exception {
        try {
            parserTokenAccessFollowsCoreContractForLiteralVpackArray();
        } finally {
        }
    }


    void __invoke_parserCurrentNameTracksNestedLiteralVpackObject() throws Exception {
        try {
            parserCurrentNameTracksNestedLiteralVpackObject();
        } finally {
        }
    }

}
