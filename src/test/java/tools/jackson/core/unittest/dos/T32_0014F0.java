package tools.jackson.core.unittest.dos;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.core.filter.FilteringGeneratorDelegate;
import tools.jackson.core.filter.TokenFilter;
import tools.jackson.core.filter.TokenFilter.Inclusion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0014F0 {
private static final TokenFilter INCLUDE_EMPTY_IF_NOT_FILTERED = new TokenFilter() {
        @Override
        public boolean includeEmptyArray(boolean contentsFiltered) {
            return !contentsFiltered;
        }

        @Override
        public boolean includeEmptyObject(boolean contentsFiltered) {
            return !contentsFiltered;
        }

        @Override
        protected boolean _includeScalar() {
            return false;
        }
    };
private static final TokenFilter INCLUDE_EMPTY = new TokenFilter() {
        @Override
        public boolean includeEmptyArray(boolean contentsFiltered) {
            return true;
        }

        @Override
        public boolean includeEmptyObject(boolean contentsFiltered) {
            return true;
        }

        @Override
        protected boolean _includeScalar() {
            return false;
        }
    };

    
    void bigDecimalFromString() throws Exception {
        // c9 fa 00 80 96 98 00 followed by 250 bytes of packed decimal 11s:
        // 500 mantissa digits and exponent +10,000,000.
        byte[] fixture = largeExponentDecimalFixture();
        try (JsonParser parser = new VPackFactory().createParser(fixture)) {
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            BigDecimal value = parser.getDecimalValue();
            assertNotNull(value);
            assertEquals(-10_000_000, value.scale());
            assertEquals(new BigInteger("1".repeat(500)), value.unscaledValue());
        }
    }
private static Object filtered(TokenFilter filter, WriterCall call) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = new FilteringGeneratorDelegate(
                new VPackFactory().createGenerator(ObjectWriteContext.empty(), output),
                filter, Inclusion.INCLUDE_ALL_AND_PATH, true)) {
            call.write(generator);
        }
        return new VPackMapper().readValue(output.toByteArray(), Object.class);
    }
private static byte[] largeExponentDecimalFixture() {
        byte[] result = new byte[1 + 2 + 4 + 250];
        result[0] = (byte) 0xC9;
        result[1] = (byte) 0xFA;
        result[2] = 0;
        result[3] = (byte) 0x80;
        result[4] = (byte) 0x96;
        result[5] = (byte) 0x98;
        result[6] = 0;
        java.util.Arrays.fill(result, 7, result.length, (byte) 0x11);
        return result;
    }
private static byte[] bcdFixture(int exponent) {
        return new byte[] {
                (byte) 0xC8, 0x01,
                (byte) exponent, (byte) (exponent >>> 8),
                (byte) (exponent >>> 16), (byte) (exponent >>> 24),
                0x01
        };
    }
@FunctionalInterface
    private interface WriterCall {
        void write(JsonGenerator generator) throws Exception;
    }

    void __invoke_bigDecimalFromString() throws Exception {
        try {
            bigDecimalFromString();
        } finally {
        }
    }

}
