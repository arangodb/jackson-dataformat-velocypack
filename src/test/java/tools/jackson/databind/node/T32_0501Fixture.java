package tools.jackson.databind.node;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.OptionalLong;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.JsonNodeException;
import tools.jackson.databind.node.ObjectNode;
import tools.jackson.databind.node.POJONode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0501Fixture {
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private final ObjectMapper mapper = new VPackMapper();
private POJONode pojo(Object value) throws Exception {
        ObjectNode root = (ObjectNode) mapper.readTree(EMPTY_OBJECT);
        root.putPOJO("value", value);
        return (POJONode) root.get("value");
    }

    // Provenance: POJONodeTest#testAsString().
    void testAsStringVpack() throws Exception {
        assertEquals("", pojo(null).asString());
        assertEquals("test", pojo("test").asString());
        assertThrows(JsonNodeException.class, () -> pojo(new Data()).asString());
    }

    // Provenance: POJONodeTest#testAsStringDefaultValue().
    void testAsStringDefaultValueVpack() throws Exception {
        assertEquals("fallback", pojo(null).asString("fallback"));
        assertEquals("test", pojo("test").asString("fallback"));
        assertEquals("fallback", pojo(new Data()).asString("fallback"));
    }

    // Provenance: POJONodeTest#testAsShort().
    void testAsShortVpack() throws Exception {
        assertEquals((short) 0, pojo(null).asShort());
        assertEquals((short) 99, pojo(99.99D).asShort());
        assertEquals((short) 99, pojo(99L).asShort());
        assertEquals((short) 99, pojo(99).asShort());
        assertEquals((short) 99, pojo((short) 99).asShort());
        assertEquals((short) 99, pojo((byte) 99).asShort());
        assertEquals((short) 99, pojo(BigInteger.valueOf(99)).asShort());
        assertEquals((short) 99, pojo(BigDecimal.valueOf(99.99)).asShort());
        assertThrows(JsonNodeException.class, () -> pojo(new Data()).asShort());
    }

    // Provenance: POJONodeTest#testAsShortDefaultValue().
    void testAsShortDefaultValueVpack() throws Exception {
        assertEquals((short) 10, pojo(null).asShort((short) 10));
        assertEquals((short) 99, pojo(99.99D).asShort((short) 10));
        assertEquals((short) 99, pojo(99L).asShort((short) 10));
        assertEquals((short) 99, pojo(99).asShort((short) 10));
        assertEquals((short) 99, pojo((short) 99).asShort((short) 10));
        assertEquals((short) 99, pojo((byte) 99).asShort((short) 10));
        assertEquals((short) 99, pojo(BigInteger.valueOf(99)).asShort((short) 10));
        assertEquals((short) 99, pojo(BigDecimal.valueOf(99.99)).asShort((short) 10));
        assertEquals((short) 10, pojo(new Data()).asShort((short) 10));
    }

    // Provenance: POJONodeTest#testAsShortOpt().
    void testAsShortOptVpack() throws Exception {
        assertTrue(pojo(null).asShortOpt().isEmpty());
        assertEquals(Optional.of((short) 99), pojo(99.99D).asShortOpt());
        assertEquals(Optional.of((short) 99), pojo(99L).asShortOpt());
        assertEquals(Optional.of((short) 99), pojo(99).asShortOpt());
        assertEquals(Optional.of((short) 99), pojo((short) 99).asShortOpt());
        assertEquals(Optional.of((short) 99), pojo((byte) 99).asShortOpt());
        assertEquals(Optional.of((short) 99), pojo(BigInteger.valueOf(99)).asShortOpt());
        assertEquals(Optional.of((short) 99), pojo(BigDecimal.valueOf(99.99)).asShortOpt());
        assertTrue(pojo(new Data()).asShortOpt().isEmpty());
    }

    // Provenance: POJONodeTest#testAsInt().
    void testAsIntVpack() throws Exception {
        assertEquals(0, pojo(null).asInt());
        assertEquals(99, pojo(99.99D).asInt());
        assertEquals(99, pojo(99L).asInt());
        assertEquals(99, pojo(99).asInt());
        assertEquals(99, pojo((short) 99).asInt());
        assertEquals(99, pojo((byte) 99).asInt());
        assertEquals(99, pojo(BigInteger.valueOf(99)).asInt());
        assertEquals(99, pojo(BigDecimal.valueOf(99.99)).asInt());
        assertThrows(JsonNodeException.class, () -> pojo(new Data()).asInt());
    }

    // Provenance: POJONodeTest#testAsIntDefaultValue().
    void testAsIntDefaultValueVpack() throws Exception {
        assertEquals(10, pojo(null).asInt(10));
        assertEquals(99, pojo(99.99D).asInt(10));
        assertEquals(99, pojo(99L).asInt(10));
        assertEquals(99, pojo(99).asInt(10));
        assertEquals(99, pojo((short) 99).asInt(10));
        assertEquals(99, pojo((byte) 99).asInt(10));
        assertEquals(99, pojo(BigInteger.valueOf(99)).asInt(10));
        assertEquals(99, pojo(BigDecimal.valueOf(99.99)).asInt(10));
        assertEquals(10, pojo(new Data()).asInt(10));
    }

    // Provenance: POJONodeTest#testAsIntOpt().
    void testAsIntOptVpack() throws Exception {
        assertTrue(pojo(null).asIntOpt().isEmpty());
        assertEquals(OptionalInt.of(99), pojo(99.99D).asIntOpt());
        assertEquals(OptionalInt.of(99), pojo(99L).asIntOpt());
        assertEquals(OptionalInt.of(99), pojo(99).asIntOpt());
        assertEquals(OptionalInt.of(99), pojo((short) 99).asIntOpt());
        assertEquals(OptionalInt.of(99), pojo((byte) 99).asIntOpt());
        assertEquals(OptionalInt.of(99), pojo(BigInteger.valueOf(99)).asIntOpt());
        assertEquals(OptionalInt.of(99), pojo(BigDecimal.valueOf(99.99)).asIntOpt());
        assertTrue(pojo(new Data()).asIntOpt().isEmpty());
    }

    // Provenance: POJONodeTest#testAsLong().
    void testAsLongVpack() throws Exception {
        assertEquals(0L, pojo(null).asLong());
        assertEquals(99L, pojo(99.99D).asLong());
        assertEquals(33L, pojo(33.3f).asLong());
        assertEquals(99L, pojo(99L).asLong());
        assertEquals(99L, pojo(99).asLong());
        assertEquals(99L, pojo((short) 99).asLong());
        assertEquals(99L, pojo((byte) 99).asLong());
        assertEquals(99L, pojo(BigInteger.valueOf(99)).asLong());
        assertEquals(99L, pojo(BigDecimal.valueOf(99.99)).asLong());
        assertThrows(JsonNodeException.class, () -> pojo(new Data()).asLong());

        assertThrows(JsonNodeException.class,
                () -> pojo((float) Long.MAX_VALUE * 2.0f).asLong());
        final double bigD = (double) Long.MAX_VALUE * 2.0;
        assertThrows(JsonNodeException.class, () -> pojo(bigD).asLong());
        assertThrows(JsonNodeException.class,
                () -> pojo(BigDecimal.valueOf(bigD)).asLong());
    }

    // Provenance: POJONodeTest#testAsLongDefaultValue().
    void testAsLongDefaultValueVpack() throws Exception {
        assertEquals(10L, pojo(null).asLong(10));
        assertEquals(99L, pojo(99.99D).asLong(10));
        assertEquals(99L, pojo(99L).asLong(10));
        assertEquals(99L, pojo(99).asLong(10));
        assertEquals(99L, pojo((short) 99).asLong(10));
        assertEquals(99L, pojo((byte) 99).asLong(10));
        assertEquals(99L, pojo(BigInteger.valueOf(99)).asLong(10));
        assertEquals(99L, pojo(BigDecimal.valueOf(99.99)).asLong(10));
        assertEquals(10L, pojo(new Data()).asLong(10));
    }

    // Provenance: POJONodeTest#testAsLongOpt().
    void testAsLongOptVpack() throws Exception {
        assertTrue(pojo(null).asLongOpt().isEmpty());
        assertEquals(OptionalLong.of(99L), pojo(99.99D).asLongOpt());
        assertEquals(OptionalLong.of(99L), pojo(99L).asLongOpt());
        assertEquals(OptionalLong.of(99L), pojo(99).asLongOpt());
        assertEquals(OptionalLong.of(99L), pojo((short) 99).asLongOpt());
        assertEquals(OptionalLong.of(99L), pojo((byte) 99).asLongOpt());
        assertEquals(OptionalLong.of(99L), pojo(BigInteger.valueOf(99)).asLongOpt());
        assertEquals(OptionalLong.of(99L), pojo(BigDecimal.valueOf(99.99)).asLongOpt());
        assertTrue(pojo(new Data()).asLongOpt().isEmpty());
    }

    // Provenance: POJONodeTest#testAsFloatOpt().
    void testAsFloatOptVpack() throws Exception {
        assertTrue(pojo(null).asFloatOpt().isEmpty());
        assertEquals(Optional.of(99.99f), pojo(99.99D).asFloatOpt());
        assertEquals(Optional.of(99f), pojo(99L).asFloatOpt());
        assertEquals(Optional.of(99f), pojo(99).asFloatOpt());
        assertEquals(Optional.of(99f), pojo((short) 99).asFloatOpt());
        assertEquals(Optional.of(99f), pojo((byte) 99).asFloatOpt());
        assertEquals(Optional.of(99f), pojo(BigInteger.valueOf(99)).asFloatOpt());
        assertEquals(Optional.of(99.99f), pojo(BigDecimal.valueOf(99.99)).asFloatOpt());
        assertTrue(pojo(new Data()).asFloatOpt().isEmpty());
    }
private static final class Data {
    }

    void __invoke_testAsStringVpack() throws Exception {
        try {
            testAsStringVpack();
        } finally {
        }
    }


    void __invoke_testAsStringDefaultValueVpack() throws Exception {
        try {
            testAsStringDefaultValueVpack();
        } finally {
        }
    }


    void __invoke_testAsShortVpack() throws Exception {
        try {
            testAsShortVpack();
        } finally {
        }
    }


    void __invoke_testAsShortDefaultValueVpack() throws Exception {
        try {
            testAsShortDefaultValueVpack();
        } finally {
        }
    }


    void __invoke_testAsShortOptVpack() throws Exception {
        try {
            testAsShortOptVpack();
        } finally {
        }
    }


    void __invoke_testAsIntVpack() throws Exception {
        try {
            testAsIntVpack();
        } finally {
        }
    }


    void __invoke_testAsIntDefaultValueVpack() throws Exception {
        try {
            testAsIntDefaultValueVpack();
        } finally {
        }
    }


    void __invoke_testAsIntOptVpack() throws Exception {
        try {
            testAsIntOptVpack();
        } finally {
        }
    }


    void __invoke_testAsLongVpack() throws Exception {
        try {
            testAsLongVpack();
        } finally {
        }
    }


    void __invoke_testAsLongDefaultValueVpack() throws Exception {
        try {
            testAsLongDefaultValueVpack();
        } finally {
        }
    }


    void __invoke_testAsLongOptVpack() throws Exception {
        try {
            testAsLongOptVpack();
        } finally {
        }
    }


    void __invoke_testAsFloatOptVpack() throws Exception {
        try {
            testAsFloatOptVpack();
        } finally {
        }
    }

}
