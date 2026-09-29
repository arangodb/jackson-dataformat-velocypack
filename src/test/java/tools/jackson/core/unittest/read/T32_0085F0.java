package tools.jackson.core.unittest.read;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.ObjectWriteContext;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0085F0 {
private static final byte[] BIG_DECIMAL_1E_PLUS_999 = {
            (byte) 0xC8, 0x01, (byte) 0xE7, 0x03, 0x00, 0x00, 0x01
    };

    void copyCurrentEventExactRetainsLiteralBigDecimalPrecisionAndScale() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonParser parser = new VPackFactory().createParser(
                ObjectReadContext.empty(), BIG_DECIMAL_1E_PLUS_999);
                JsonGenerator generator = new VPackFactory().createGenerator(
                        ObjectWriteContext.empty(), output)) {
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            assertEquals(new BigDecimal("1E+999"), parser.getDecimalValue());
            assertEquals(-999, parser.getDecimalValue().scale());
            generator.copyCurrentEventExact(parser);
        }
        assertArrayEquals(BIG_DECIMAL_1E_PLUS_999, output.toByteArray());
    }

    void __invoke_copyCurrentEventExactRetainsLiteralBigDecimalPrecisionAndScale() throws Exception {
        try {
            copyCurrentEventExactRetainsLiteralBigDecimalPrecisionAndScale();
        } finally {
        }
    }

}
