package tools.jackson.dataformat.velocypack;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.util.HexFormat;

import org.junit.jupiter.api.Test;

import tools.jackson.core.JsonToken;
import tools.jackson.core.Base64Variants;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.StreamReadConstraints;
import tools.jackson.core.exc.InputCoercionException;
import tools.jackson.core.exc.StreamConstraintsException;

import static org.junit.jupiter.api.Assertions.*;

/** Independent wire fixtures for the September 2026 adversarial review. */
class VPackReviewParserRegressionTest {
    @Test
    void literalNamesObeyNameLimitRatherThanStringValueLimit() {
        byte[][] objects = {
                hex("14 09 44 61 62 63 64 31 01"), // {"abcd":1}, short key
                hex("14 11 bf 04 00 00 00 00 00 00 00 61 62 63 64 31 01") // long key
        };
        VPackFactory reject = constrained(3, 100, 100);
        VPackFactory accept = constrained(4, 1, 100);
        for (byte[] input : objects) {
            for (int mode = 0; mode < 4; ++mode) {
                try (VPackParser parser = parser(reject, input, mode)) {
                    assertEquals(JsonToken.START_OBJECT, parser.nextToken());
                    assertThrows(StreamConstraintsException.class, parser::nextToken);
                }
                try (VPackParser parser = parser(accept, input, mode)) {
                    assertEquals(JsonToken.START_OBJECT, parser.nextToken());
                    assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
                    assertEquals("abcd", parser.currentName());
                    assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
                    assertEquals(JsonToken.END_OBJECT, parser.nextToken());
                }
            }
        }
        // Keeping names independent must not disable limits on string values.
        try (VPackParser parser = parser(accept, hex("44 61 62 63 64"), 0)) {
            assertThrows(StreamConstraintsException.class, parser::nextToken);
        }
    }

    @Test
    void namesUseUtf16LengthAndEffectiveReadContextConstraints() {
        byte[] input = hex("14 09 44 f0 9f 98 80 31 01"); // one supplementary code point
        try (VPackParser parser = parser(constrained(2, 1, 100), input, 0)) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals(2, parser.currentName().length());
        }
        ObjectReadContext context = new ObjectReadContext.Base() {
            @Override public StreamReadConstraints streamReadConstraints() {
                return StreamReadConstraints.builder().maxNameLength(1).build();
            }
        };
        try (VPackParser parser = (VPackParser) new VPackFactory().createParser(context, input)) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertThrows(StreamConstraintsException.class, parser::nextToken);
        }
    }

    @Test
    void fixedIntegersAndDatesHonorTheDecimalDigitBudget() {
        VPackFactory limited = constrained(100, 100, 2);
        for (String value : new String[] {
                "20 7b", "20 85", "28 7b", "1c 7b 00 00 00 00 00 00 00" }) {
            for (int mode = 0; mode < 4; ++mode) {
                try (VPackParser parser = parser(limited, hex(value), mode)) {
                    assertThrows(StreamConstraintsException.class, parser::nextToken);
                }
            }
        }
        try (VPackParser parser = parser(limited, hex("20 9d 28 63 3a 39"), 0)) {
            for (int expected : new int[] {-99, 99, -6, 9}) {
                assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
                assertEquals(expected, parser.getIntValue());
            }
        }
        byte[] unsignedMaximum = hex("2f ff ff ff ff ff ff ff ff");
        try (VPackParser parser = parser(constrained(100, 100, 19), unsignedMaximum, 0)) {
            assertThrows(StreamConstraintsException.class, parser::nextToken);
        }
        try (VPackParser parser = parser(constrained(100, 100, 20), unsignedMaximum, 0)) {
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals("18446744073709551615", parser.getBigIntegerValue().toString());
        }
    }

    @Test
    void physicalTypeIsAbsentOnEndTokensAndClearedTokens() {
        for (int mode = 0; mode < 4; ++mode) {
            try (VPackParser parser = parser(new VPackFactory(), hex("01 0a 02 04 31 32"), mode)) {
                assertNull(parser.currentVPackType());
                while (parser.nextToken() != null) {
                    if (parser.currentToken() == JsonToken.END_ARRAY
                            || parser.currentToken() == JsonToken.END_OBJECT) {
                        assertNull(parser.currentVPackType());
                    } else {
                        assertNotNull(parser.currentVPackType());
                    }
                }
                assertNull(parser.currentVPackType());
            }
        }
        try (VPackParser parser = parser(new VPackFactory(), hex("31"), 0)) {
            parser.nextToken();
            parser.clearCurrentToken();
            assertNull(parser.currentVPackType());
        }
    }

    @Test
    void nonFiniteFlagDoesNotLeakIntoFollowingBcdValue() {
        for (String nonFinite : new String[] {
                "1b 00 00 00 00 00 00 f8 7f", "1b 00 00 00 00 00 00 f0 7f",
                "1b 00 00 00 00 00 00 f0 ff" }) {
            for (int mode = 0; mode < 4; ++mode) {
                try (VPackParser parser = parser(new VPackFactory(),
                        hex(nonFinite + " c8 01 ff ff ff ff 12"), mode)) {
                    assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
                    assertTrue(parser.isNaN());
                    assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
                    assertFalse(parser.isNaN());
                    assertEquals("1.2", parser.getDecimalValue().toPlainString());
                }
            }
        }
    }

    @Test
    void doubleLongCoercionUsesExactBinaryBoundaryNotRoundedDecimalText() {
        try (VPackParser parser = parser(new VPackFactory(),
                hex("1b 00 00 00 00 00 00 e0 c3"), 0)) {
            parser.nextToken();
            assertEquals(Long.MIN_VALUE, parser.getLongValue());
        }
        for (String outside : new String[] {
                "1b 00 00 00 00 00 00 e0 43", // +2^63
                "1b 01 00 00 00 00 00 e0 c3" // next double below -2^63
        }) {
            try (VPackParser parser = parser(new VPackFactory(), hex(outside), 0)) {
                parser.nextToken();
                assertThrows(InputCoercionException.class, parser::getLongValue);
            }
        }
    }

    @Test
    void explicitBase64CoercionNeverChangesThePhysicalToken() {
        try (VPackParser parser = parser(new VPackFactory(), hex("44 41 51 49 44"), 0)) {
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertArrayEquals(new byte[] {1, 2, 3},
                    parser.getBinaryValue(Base64Variants.getDefaultVariant()));
            assertEquals(JsonToken.VALUE_STRING, parser.currentToken());
            assertEquals(VPackType.STRING, parser.currentVPackType());
            assertNull(parser.getEmbeddedObject());
            assertThrows(tools.jackson.core.exc.StreamReadException.class, () -> parser.readBinaryValue(
                    Base64Variants.getDefaultVariant(), new ByteArrayOutputStream()));
        }
    }

    private static VPackFactory constrained(int name, int string, int number) {
        return VPackFactory.builder().streamReadConstraints(StreamReadConstraints.builder()
                .maxNameLength(name).maxStringLength(string).maxNumberLength(number).build()).build();
    }

    private static VPackParser parser(VPackFactory factory, byte[] input, int mode) {
        return (VPackParser) switch (mode) {
        case 0 -> factory.createParser(input);
        case 1 -> {
            byte[] padded = new byte[input.length + 6];
            System.arraycopy(input, 0, padded, 3, input.length);
            yield factory.createParser(padded, 3, input.length);
        }
        case 2 -> factory.createParser(new ByteArrayInputStream(input));
        case 3 -> factory.createParser(ObjectReadContext.empty(),
                (DataInput) new DataInputStream(new ByteArrayInputStream(input)));
        default -> throw new IllegalArgumentException("Unknown transport");
        };
    }

    private static byte[] hex(String value) {
        return HexFormat.of().parseHex(value.replace(" ", ""));
    }
}
