package tools.jackson.core.unittest.json;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.core.StreamWriteCapability;
import tools.jackson.core.StreamWriteFeature;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0040F0 {
private static final byte[] NESTED_OBJECT = {
            0x14, 0x1B,
            0x45, 'f', 'i', 'r', 's', 't',
            0x14, 0x12,
            0x46, 's', 'e', 'c', 'o', 'n', 'd', 0x33,
            0x45, 't', 'h', 'i', 'r', 'd', 0x19, 0x02,
            0x01
    };

    void vpackGeneratorDefaultsExposeBinaryCapabilitiesAndCoreDefaults() throws Exception {
        VPackFactory factory = new VPackFactory();
        assertTrue(factory.isEnabled(VPackWriteFeature.USE_EQUAL_LENGTH_ARRAYS));
        assertFalse(factory.isEnabled(VPackWriteFeature.WRITE_COMPACT_ARRAYS));
        assertFalse(factory.isEnabled(VPackWriteFeature.WRITE_COMPACT_OBJECTS));

        try (JsonGenerator generator = factory.createGenerator(
                ObjectWriteContext.empty(), new ByteArrayOutputStream())) {
            assertFalse(generator.isEnabled(StreamWriteFeature.WRITE_BIGDECIMAL_AS_PLAIN));
            assertTrue(generator.canOmitProperties());
            assertFalse(generator.canWriteObjectId());
            assertFalse(generator.canWriteTypeId());
            assertTrue(generator.has(StreamWriteCapability.CAN_WRITE_BINARY_NATIVELY));
        }
    }

    void vpackBigDecimalPlainFeatureRetainsExactBcdValueAndScale() throws Exception {
        BigDecimal value = new BigDecimal("1E+2");
        byte[] expected = { (byte) 0xC8, 0x01, 0x02, 0x00, 0x00, 0x00, 0x01 };

        assertBigDecimalBytes(new VPackFactory(), value, expected);
        assertBigDecimalBytes(VPackFactory.builder()
                .enable(StreamWriteFeature.WRITE_BIGDECIMAL_AS_PLAIN).build(), value, expected);

        // Independent literal input checks the corresponding canonical value and scale.
        try (JsonParser parser = new VPackFactory().createParser(expected)) {
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            assertEquals(value, parser.getDecimalValue());
            assertEquals(value.scale(), parser.getDecimalValue().scale());
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

    void __invoke_vpackGeneratorDefaultsExposeBinaryCapabilitiesAndCoreDefaults() throws Exception {
        try {
            vpackGeneratorDefaultsExposeBinaryCapabilitiesAndCoreDefaults();
        } finally {
        }
    }


    void __invoke_vpackBigDecimalPlainFeatureRetainsExactBcdValueAndScale() throws Exception {
        try {
            vpackBigDecimalPlainFeatureRetainsExactBcdValueAndScale();
        } finally {
        }
    }

}
