package tools.jackson.databind.deser.jdk;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0265Fixture {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] FLOAT_VALUES = VPackWireFixtureTest.hex(
            "13 27 "
          + "1b 00 00 00 a0 7f c8 b5 3a "
          + "1b 00 00 00 20 33 33 f3 3f "
          + "1b 00 00 00 e0 ff ff ef 47 "
          + "1b 00 00 00 00 00 00 a0 36 04");
private static final byte[] POSITIVE_INFINITY = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f0 7f");
private static final byte[] NEGATIVE_INFINITY = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f0 ff");
private static final byte[] ONE_DOUBLE = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f0 3f");
private static final byte[] EMPTY_STRING = VPackWireFixtureTest.hex("40");
private static final byte[] PLUS_INFINITY = VPackWireFixtureTest.hex(
            "49 2b 49 6e 66 69 6e 69 74 79");
private static final byte[] PLUS_INF = VPackWireFixtureTest.hex("44 2b 49 4e 46");
private static final byte[] NEGATIVE_DECIMAL_ARRAY = VPackWireFixtureTest.hex(
            "13 0b d0 02 fe ff ff ff 19 37 01");
private static final byte[] NEGATIVE_DECIMAL_OBJECT = VPackWireFixtureTest.hex(
            "14 0d 41 61 d0 02 fe ff ff ff 19 37 01");
private static final byte[] INTEGER_123 = VPackWireFixtureTest.hex("28 7b");
private static final byte[] INTEGER_42 = VPackWireFixtureTest.hex("28 2a");

    // Provenance: JDKNumberDeserTest#testDoubleAsNumber().
    void testDoubleAsNumber() throws Exception {
        Number result = MAPPER.readValue(ONE_DOUBLE, Number.class);
        assertEquals(Double.class, result.getClass());
        assertEquals(Double.valueOf(1.0), result);
    }

    // Provenance: JDKNumberDeserTest#testDoubleInf().
    void testDoubleInf() throws Exception {
        assertEquals(Double.valueOf(Double.POSITIVE_INFINITY),
                MAPPER.readValue(POSITIVE_INFINITY, Double.class));
        assertEquals(Double.valueOf(Double.NEGATIVE_INFINITY),
                MAPPER.readValue(NEGATIVE_INFINITY, Double.class));
    }

    // Provenance: JDKNumberDeserTest#testDoublePlusInf5898().
    void testDoublePlusInf5898() throws Exception {
        assertEquals(Double.POSITIVE_INFINITY,
                MAPPER.readValue(PLUS_INFINITY, Double.class));
        assertEquals(Double.POSITIVE_INFINITY,
                MAPPER.readValue(PLUS_INFINITY, Double.TYPE));
        assertEquals(Double.POSITIVE_INFINITY,
                MAPPER.readValue(PLUS_INF, Double.class));
        assertEquals(Double.POSITIVE_INFINITY,
                MAPPER.readValue(PLUS_INF, Double.TYPE));
    }

    // Provenance: JDKNumberDeserTest#testEmptyAsNumber().
    void testEmptyAsNumber() throws Exception {
        assertNull(MAPPER.readValue(EMPTY_STRING, Byte.class));
        assertNull(MAPPER.readValue(EMPTY_STRING, Short.class));
        assertNull(MAPPER.readValue(EMPTY_STRING, Character.class));
        assertNull(MAPPER.readValue(EMPTY_STRING, Integer.class));
        assertNull(MAPPER.readValue(EMPTY_STRING, Long.class));
        assertNull(MAPPER.readValue(EMPTY_STRING, Float.class));
        assertNull(MAPPER.readValue(EMPTY_STRING, Double.class));
        assertNull(MAPPER.readValue(EMPTY_STRING, java.math.BigInteger.class));
        assertNull(MAPPER.readValue(EMPTY_STRING, BigDecimal.class));
    }

    // Provenance: JDKNumberDeserTest#testFloatClass().
    void testFloatClass() throws Exception {
        Float[] values = MAPPER.readValue(FLOAT_VALUES, Float[].class);
        assertEquals(4, values.length);
        assertEquals(Float.valueOf(7.038531e-26f), values[0]);
        assertEquals(Float.valueOf(1.1999999f), values[1]);
        assertEquals(Float.valueOf(3.4028235e38f), values[2]);
        assertEquals(Float.valueOf("1.4E-45"), values[3]);
    }

    // Provenance: JDKNumberDeserTest#testFloatPrimitive().
    void testFloatPrimitive() throws Exception {
        assertEquals(7.038531e-26f, MAPPER.readValue(
                VPackWireFixtureTest.hex("1b 00 00 00 a0 7f c8 b5 3a"), float.class));
        assertEquals(1.1999999f, MAPPER.readValue(
                VPackWireFixtureTest.hex("1b 00 00 00 20 33 33 f3 3f"), float.class));
        assertEquals(3.4028235e38f, MAPPER.readValue(
                VPackWireFixtureTest.hex("1b 00 00 00 e0 ff ff ef 47"), float.class));
        assertEquals("1.4E-45", MAPPER.readValue(
                VPackWireFixtureTest.hex("1b 00 00 00 00 00 00 a0 36"), float.class).toString());
    }

    // Provenance: JDKNumberDeserTest#testFloatPlusInf5898().
    void testFloatPlusInf5898() throws Exception {
        assertEquals(Float.POSITIVE_INFINITY,
                MAPPER.readValue(PLUS_INFINITY, Float.class));
        assertEquals(Float.POSITIVE_INFINITY,
                MAPPER.readValue(PLUS_INFINITY, Float.TYPE));
        assertEquals(Float.POSITIVE_INFINITY,
                MAPPER.readValue(PLUS_INF, Float.class));
        assertEquals(Float.POSITIVE_INFINITY,
                MAPPER.readValue(PLUS_INF, Float.TYPE));
    }

    // Provenance: JDKNumberDeserTest#testIntAsNumber().
    void testIntAsNumber() throws Exception {
        Number result = MAPPER.readValue(INTEGER_123, Number.class);
        assertEquals(Integer.valueOf(123), result);
    }

    // Provenance: JDKNumberDeserTest#testIntTypeOverride().
    void testIntTypeOverride() throws Exception {
        ObjectReader reader = MAPPER.reader(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS);

        Number result = reader.forType(Number.class).readValue(INTEGER_123);
        assertEquals(java.math.BigInteger.class, result.getClass());
        assertEquals(java.math.BigInteger.valueOf(123L), result);

        Object value = reader.forType(Object.class).readValue(INTEGER_123);
        assertEquals(java.math.BigInteger.class, value.getClass());
        assertEquals(java.math.BigInteger.valueOf(123L), value);

        JsonNode node = reader.readTree(INTEGER_123);
        assertTrue(node.isBigInteger());
        assertEquals(123, node.asInt());
    }

    // Provenance: JDKNumberDeserTest#testFpTypeOverrideSimple().
    void testFpTypeOverrideSimple() throws Exception {
        ObjectReader reader = MAPPER.reader(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
        BigDecimal expected = new BigDecimal("0.1");
        byte[] decimal = VPackWireFixtureTest.hex("c8 01 ff ff ff ff 01");

        Number result = reader.forType(Number.class).readValue(decimal);
        assertEquals(BigDecimal.class, result.getClass());
        assertEquals(expected, result);

        Object value = reader.forType(Object.class).readValue(decimal);
        assertEquals(BigDecimal.class, value.getClass());
        assertEquals(expected, value);

        JsonNode node = reader.readTree(decimal);
        assertTrue(node.isBigDecimal());
        assertEquals(expected, node.decimalValue());
    }

    // Provenance: JDKNumberDeserTest#testFpTypeOverrideStructured().
    void testFpTypeOverrideStructured() throws Exception {
        ObjectReader reader = MAPPER.reader(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
        BigDecimal expected = new BigDecimal("-19.37");

        @SuppressWarnings("unchecked")
        List<Object> list = (List<Object>) reader.forType(List.class)
                .readValue(NEGATIVE_DECIMAL_ARRAY);
        assertEquals(1, list.size());
        Object listValue = list.get(0);
        assertEquals(BigDecimal.class, listValue.getClass());
        assertEquals(expected, listValue);

        Map<?, ?> map = reader.forType(Map.class).readValue(NEGATIVE_DECIMAL_OBJECT);
        Object mapValue = map.get("a");
        assertEquals(BigDecimal.class, mapValue.getClass());
        assertEquals(expected, mapValue);
    }

    // Provenance: JDKNumberDeserTest#testForceIntsToLongs().
    void testForceIntsToLongs() throws Exception {
        ObjectReader reader = MAPPER.reader(DeserializationFeature.USE_LONG_FOR_INTS);

        Object value = reader.forType(Object.class).readValue(INTEGER_42);
        assertEquals(Long.class, value.getClass());
        assertEquals(Long.valueOf(42L), value);

        Number number = reader.forType(Number.class).readValue(INTEGER_42);
        assertEquals(Long.class, number.getClass());
        assertEquals(Long.valueOf(42L), number);

        JsonNode node = reader.readTree(INTEGER_42);
        assertTrue(node.isLong());
        assertEquals(42, node.asInt());
    }

    void __invoke_testDoubleAsNumber() throws Exception {
        try {
            testDoubleAsNumber();
        } finally {
        }
    }


    void __invoke_testDoubleInf() throws Exception {
        try {
            testDoubleInf();
        } finally {
        }
    }


    void __invoke_testDoublePlusInf5898() throws Exception {
        try {
            testDoublePlusInf5898();
        } finally {
        }
    }


    void __invoke_testEmptyAsNumber() throws Exception {
        try {
            testEmptyAsNumber();
        } finally {
        }
    }


    void __invoke_testFloatClass() throws Exception {
        try {
            testFloatClass();
        } finally {
        }
    }


    void __invoke_testFloatPrimitive() throws Exception {
        try {
            testFloatPrimitive();
        } finally {
        }
    }


    void __invoke_testFloatPlusInf5898() throws Exception {
        try {
            testFloatPlusInf5898();
        } finally {
        }
    }


    void __invoke_testIntAsNumber() throws Exception {
        try {
            testIntAsNumber();
        } finally {
        }
    }


    void __invoke_testIntTypeOverride() throws Exception {
        try {
            testIntTypeOverride();
        } finally {
        }
    }


    void __invoke_testFpTypeOverrideSimple() throws Exception {
        try {
            testFpTypeOverrideSimple();
        } finally {
        }
    }


    void __invoke_testFpTypeOverrideStructured() throws Exception {
        try {
            testFpTypeOverrideStructured();
        } finally {
        }
    }


    void __invoke_testForceIntsToLongs() throws Exception {
        try {
            testForceIntsToLongs();
        } finally {
        }
    }

}
