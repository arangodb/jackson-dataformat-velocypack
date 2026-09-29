package tools.jackson.databind.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.StreamWriteFeature;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.ObjectWriter;
import static org.junit.jupiter.api.Assertions.*;

import tools.jackson.dataformat.velocypack.*;

class T32_0633F2 {
private final VPackFactory factory = new VPackFactory();
private final VPackMapper mapper = new VPackMapper();

    void tokenBufferBasicConfigMapsToVPackFactoryConfiguration() throws IOException {
        VPackFactory factory = new VPackFactory();
        assertEquals("5.0.0", factory.version().toString());
        assertTrue(factory.isEnabled(VPackWriteFeature.USE_EQUAL_LENGTH_ARRAYS));
        assertFalse(factory.isEnabled(VPackWriteFeature.WRITE_COMPACT_ARRAYS));
        assertFalse(factory.isEnabled(VPackWriteFeature.WRITE_COMPACT_OBJECTS));
        VPackFactory configured = VPackFactory.builder()
                .disable(VPackWriteFeature.USE_EQUAL_LENGTH_ARRAYS)
                .enable(VPackWriteFeature.WRITE_COMPACT_ARRAYS)
                .build();
        assertFalse(configured.isEnabled(VPackWriteFeature.USE_EQUAL_LENGTH_ARRAYS));
        assertTrue(configured.isEnabled(VPackWriteFeature.WRITE_COMPACT_ARRAYS));
        assertFalse(configured.isEnabled(VPackWriteFeature.WRITE_COMPACT_OBJECTS));

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        JsonGenerator generator = factory.createGenerator(output);
        assertNotNull(generator.streamWriteContext());
        assertFalse(generator.isClosed());
        assertFalse(generator.isEnabled(StreamWriteFeature.WRITE_BIGDECIMAL_AS_PLAIN));
        generator.configure(StreamWriteFeature.WRITE_BIGDECIMAL_AS_PLAIN, true);
        assertTrue(generator.isEnabled(StreamWriteFeature.WRITE_BIGDECIMAL_AS_PLAIN));
        generator.configure(StreamWriteFeature.WRITE_BIGDECIMAL_AS_PLAIN, false);
        assertFalse(generator.isEnabled(StreamWriteFeature.WRITE_BIGDECIMAL_AS_PLAIN));
        generator.close();
        assertTrue(generator.isClosed());
        assertEquals(0, output.size());
    }

    void tokenBufferBasicSerializationHasVPackEquivalentValues() throws Exception {
        ByteArrayOutputStream emptyRoot = new ByteArrayOutputStream();
        try (JsonGenerator generator = new VPackFactory().createGenerator(emptyRoot)) { }
        assertEquals(0, emptyRoot.size());
        assertArrayEquals(new byte[] { 0x18 }, mapper.writeValueAsBytes(null));

        List<Object> values = List.of(true, false, 1L + Integer.MAX_VALUE, 4, 0.5d);
        byte[] array = mapper.writeValueAsBytes(values);
        assertEquals(values, mapper.readValue(array, List.class));

        var value = new java.util.LinkedHashMap<String, Object>();
        value.put("foo", null);
        value.put("bar", BigInteger.valueOf(123));
        value.put("dec", BigDecimal.valueOf(5).movePointLeft(2));
        var result = mapper.readValue(mapper.writeValueAsBytes(value), java.util.Map.class);
        assertNull(result.get("foo"));
        assertEquals(123, ((Number) result.get("bar")).intValue());
        assertEquals(new BigDecimal("0.05"), result.get("dec"));
    }

    void tokenBufferAppendResultMapsToVPackObjectValues() throws Exception {
        var fields = new java.util.LinkedHashMap<String, Object>();
        fields.put("a", true);
        fields.put("b", 13);
        var result = mapper.readValue(mapper.writeValueAsBytes(fields), java.util.Map.class);
        assertEquals(Boolean.TRUE, result.get("a"));
        assertEquals(13, ((Number) result.get("b")).intValue());
    }

    void tokenBufferBigNumberStringsRetainExactValuesThroughVPack() throws Exception {
        BigInteger bigInteger = BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE);
        BigDecimal decimal = new BigDecimal("-10000000000.0000000001");
        byte[] integerBytes = writeNumberText(bigInteger.toString());
        try (JsonParser parser = factory.createParser(integerBytes)) {
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(JsonParser.NumberType.BIG_INTEGER, parser.getNumberType());
            assertEquals(bigInteger, parser.getBigIntegerValue());
        }
        byte[] decimalBytes = writeNumberText(decimal.toString());
        try (JsonParser parser = factory.createParser(decimalBytes)) {
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            assertEquals(JsonParser.NumberType.BIG_DECIMAL, parser.getNumberType());
            assertEquals(decimal, parser.getDecimalValue());
        }
    }
private byte[] writeNumberText(String text) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = factory.createGenerator(output)) {
            generator.writeNumber(text);
        }
        return output.toByteArray();
    }

    void vpackBytesAreReadableThroughMapperAndReaderOverloads() throws Exception {
        Point expected = new Point(123, -456);
        byte[] encoded = mapper.writeValueAsBytes(expected);
        assertEquals(expected, mapper.readValue(encoded, Point.class));
        assertEquals(expected, mapper.readValue(encoded, mapper.constructType(Point.class)));
        assertEquals(expected, mapper.readValue(encoded, new tools.jackson.core.type.TypeReference<Point>() { }));
        assertEquals(mapper.valueToTree(expected), mapper.readTree(encoded));

        Point readerExpected = new Point(234, 5678);
        byte[] readerBytes = mapper.writeValueAsBytes(readerExpected);
        ObjectReader reader = mapper.readerFor(Point.class);
        assertEquals(readerExpected, reader.readValue(readerBytes));
        assertEquals(mapper.valueToTree(readerExpected), reader.readTree(readerBytes));
        ObjectWriter writer = mapper.writerFor(Point.class);
        assertEquals(readerExpected, reader.readValue(writer.writeValueAsBytes(readerExpected)));
    }
static class Base { public String a; }
static class Impl extends Base {
        public String b;
        Impl() { }
        Impl(String a, String b) { this.a = a; this.b = b; }
    }
static class Point {
        public int x;
        public int y;
        Point() { }
        Point(int x, int y) { this.x = x; this.y = y; }
        @Override public boolean equals(Object other) {
            return other instanceof Point p && x == p.x && y == p.y;
        }
        @Override public int hashCode() { return 31 * x + y; }
    }

    void __invoke_tokenBufferBasicConfigMapsToVPackFactoryConfiguration() throws Exception {
        try {
            tokenBufferBasicConfigMapsToVPackFactoryConfiguration();
        } finally {
        }
    }


    void __invoke_tokenBufferBasicSerializationHasVPackEquivalentValues() throws Exception {
        try {
            tokenBufferBasicSerializationHasVPackEquivalentValues();
        } finally {
        }
    }


    void __invoke_tokenBufferAppendResultMapsToVPackObjectValues() throws Exception {
        try {
            tokenBufferAppendResultMapsToVPackObjectValues();
        } finally {
        }
    }


    void __invoke_tokenBufferBigNumberStringsRetainExactValuesThroughVPack() throws Exception {
        try {
            tokenBufferBigNumberStringsRetainExactValuesThroughVPack();
        } finally {
        }
    }


    void __invoke_vpackBytesAreReadableThroughMapperAndReaderOverloads() throws Exception {
        try {
            vpackBytesAreReadableThroughMapperAndReaderOverloads();
        } finally {
        }
    }

}
