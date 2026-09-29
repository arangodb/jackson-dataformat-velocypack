package tools.jackson.core.unittest.io;

import java.io.ByteArrayInputStream;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.exc.InputCoercionException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0027Fixture {

    void literalMinimumNormalDoubleDoesNotUnderflow() throws Exception {
        // 2.2250738585072012e-308 rounds to IEEE-754 binary64 minimum normal.
        byte[] fixture = {
                0x1B, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x10, 0x00
        };
        try (VPackParser parser = (VPackParser) new VPackFactory().createParser(fixture)) {
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            assertEquals(0x0010_0000_0000_0000L, VPackTestAccess.currentDoubleBits(parser));
            assertEquals(Double.MIN_NORMAL, parser.getDoubleValue());
            assertEquals(Double.MIN_NORMAL, parser.getNumberValue());
            assertEquals(null, parser.nextToken());
        }
    }

    void longRangeBoundariesRemainExactAcrossBinarySources() throws Exception {
        // All fixtures are independently written VPack scalars. The unsigned
        // 64-bit forms are intentionally not assembled by a production helper.
        byte[][] fixtures = {
                { 0x29, 0x39, 0x30 }, // 12345
                { 0x2F, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                        (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 0x7F },
                { 0x2F, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0x80 },
                { 0x27, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0x80 },
                { (byte) 0xD0, 0x0A, 0x00, 0x00, 0x00, 0x00,
                        0x09, 0x22, 0x33, 0x72, 0x03, 0x68, 0x54, 0x77, 0x58, 0x09 }
        };

        assertLongValue(fixtures[0], 12345L);
        assertLongValue(fixtures[1], Long.MAX_VALUE);
        assertLongValue(fixtures[3], Long.MIN_VALUE);
        assertLongOverflow(fixtures[2]);
        assertLongOverflow(fixtures[4]);

        // The same independent boundaries remain the same when input is
        // delivered through VPack's forward-only stream source.
        assertLongValue(new ByteArrayInputStream(fixtures[1]), Long.MAX_VALUE);
        assertLongValue(new ByteArrayInputStream(fixtures[3]), Long.MIN_VALUE);
        assertLongOverflow(new ByteArrayInputStream(fixtures[2]));
        assertLongOverflow(new ByteArrayInputStream(fixtures[4]));
    }
private static void assertLongValue(byte[] fixture, long expected) throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(fixture)) {
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(expected, parser.getLongValue());
            assertEquals(null, parser.nextToken());
        }
    }
private static void assertLongOverflow(byte[] fixture) throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(fixture)) {
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertThrows(InputCoercionException.class, parser::getLongValue);
        }
    }
private static void assertLongValue(ByteArrayInputStream input, long expected)
            throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(input)) {
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(expected, parser.getLongValue());
            assertEquals(null, parser.nextToken());
        }
    }
private static void assertLongOverflow(ByteArrayInputStream input) throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(input)) {
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertThrows(InputCoercionException.class, parser::getLongValue);
        }
    }

    void __invoke_literalMinimumNormalDoubleDoesNotUnderflow() throws Exception {
        try {
            literalMinimumNormalDoubleDoesNotUnderflow();
        } finally {
        }
    }


    void __invoke_longRangeBoundariesRemainExactAcrossBinarySources() throws Exception {
        try {
            longRangeBoundariesRemainExactAcrossBinarySources();
        } finally {
        }
    }

}
