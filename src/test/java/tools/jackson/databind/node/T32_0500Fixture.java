package tools.jackson.databind.node;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;
import java.util.OptionalDouble;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.JsonNodeException;
import tools.jackson.databind.node.ObjectNode;
import tools.jackson.databind.node.POJONode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0500Fixture {
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private final ObjectMapper mapper = new VPackMapper();
private POJONode pojo(Object value) throws Exception {
        ObjectNode root = (ObjectNode) mapper.readTree(EMPTY_OBJECT);
        root.putPOJO("value", value);
        return (POJONode) root.get("value");
    }

    // Provenance: POJONodeTest#testAsBigIntegerOpt().
    void testAsBigIntegerOptVpack() throws Exception {
        assertTrue(pojo(null).asBigIntegerOpt().isEmpty());
        assertEquals(Optional.of(BigInteger.valueOf(99)), pojo(99.99D).asBigIntegerOpt());
        assertEquals(Optional.of(BigInteger.valueOf(99)), pojo(99L).asBigIntegerOpt());
        assertEquals(Optional.of(BigInteger.valueOf(99)), pojo(99).asBigIntegerOpt());
        assertEquals(Optional.of(BigInteger.valueOf(99)), pojo((short) 99).asBigIntegerOpt());
        assertEquals(Optional.of(BigInteger.valueOf(99)), pojo((byte) 99).asBigIntegerOpt());
        assertEquals(Optional.of(BigInteger.valueOf(99)), pojo(BigInteger.valueOf(99)).asBigIntegerOpt());
        assertEquals(Optional.of(BigInteger.valueOf(99)), pojo(BigDecimal.valueOf(99.99)).asBigIntegerOpt());
        assertTrue(pojo(new Data()).asBigIntegerOpt().isEmpty());
    }

    // Provenance: POJONodeTest#testAsBoolean().
    void testAsBooleanVpack() throws Exception {
        assertFalse(pojo(null).asBoolean());
        assertTrue(pojo(Boolean.TRUE).asBoolean());
        assertFalse(pojo(Boolean.FALSE).asBoolean());
        assertThrows(JsonNodeException.class, () -> pojo(new Data()).asBoolean());
    }

    // Provenance: POJONodeTest#testAsBooleanDefaultValue().
    void testAsBooleanDefaultValueVpack() throws Exception {
        assertTrue(pojo(null).asBoolean(true));
        assertTrue(pojo(Boolean.TRUE).asBoolean(false));
        assertFalse(pojo(Boolean.FALSE).asBoolean(true));
        assertTrue(pojo(new Data()).asBoolean(true));
    }

    // Provenance: POJONodeTest#testAsBooleanOpt().
    void testAsBooleanOptVpack() throws Exception {
        assertTrue(pojo(null).asBooleanOpt().isEmpty());
        assertEquals(Optional.of(Boolean.TRUE), pojo(Boolean.TRUE).asBooleanOpt());
        assertEquals(Optional.of(Boolean.FALSE), pojo(Boolean.FALSE).asBooleanOpt());
        assertTrue(pojo(new Data()).asBooleanOpt().isEmpty());
    }

    // Provenance: POJONodeTest#testAsDecimal().
    void testAsDecimalVpack() throws Exception {
        assertEquals(BigDecimal.ZERO, pojo(null).asDecimal());
        assertEquals(BigDecimal.valueOf(99.99), pojo(99.99D).asDecimal());
        assertEquals(BigDecimal.valueOf(99), pojo(99L).asDecimal());
        assertEquals(BigDecimal.valueOf(99), pojo(99).asDecimal());
        assertEquals(BigDecimal.valueOf(99), pojo((short) 99).asDecimal());
        assertEquals(BigDecimal.valueOf(99), pojo((byte) 99).asDecimal());
        assertEquals(BigDecimal.valueOf(99), pojo(BigInteger.valueOf(99)).asDecimal());
        assertEquals(BigDecimal.valueOf(99.99), pojo(BigDecimal.valueOf(99.99)).asDecimal());
        assertThrows(JsonNodeException.class, () -> pojo(new Data()).asDecimal());
    }

    // Provenance: POJONodeTest#testAsDecimalDefaultValue().
    void testAsDecimalDefaultValueVpack() throws Exception {
        assertEquals(BigDecimal.TEN, pojo(null).asDecimal(BigDecimal.TEN));
        assertEquals(BigDecimal.valueOf(99.99), pojo(99.99D).asDecimal(BigDecimal.TEN));
        assertEquals(BigDecimal.valueOf(99), pojo(99L).asDecimal(BigDecimal.TEN));
        assertEquals(BigDecimal.valueOf(99), pojo(99).asDecimal(BigDecimal.TEN));
        assertEquals(BigDecimal.valueOf(99), pojo((short) 99).asDecimal(BigDecimal.TEN));
        assertEquals(BigDecimal.valueOf(99), pojo((byte) 99).asDecimal(BigDecimal.TEN));
        assertEquals(BigDecimal.valueOf(99), pojo(BigInteger.valueOf(99)).asDecimal(BigDecimal.TEN));
        assertEquals(BigDecimal.valueOf(99.99), pojo(BigDecimal.valueOf(99.99)).asDecimal(BigDecimal.TEN));
        assertEquals(BigDecimal.TEN, pojo(new Data()).asDecimal(BigDecimal.TEN));
    }

    // Provenance: POJONodeTest#testAsDecimalOpt().
    void testAsDecimalOptVpack() throws Exception {
        assertTrue(pojo(null).asDecimalOpt().isEmpty());
        assertEquals(Optional.of(BigDecimal.valueOf(99.99)), pojo(99.99D).asDecimalOpt());
        assertEquals(Optional.of(BigDecimal.valueOf(99)), pojo(99L).asDecimalOpt());
        assertEquals(Optional.of(BigDecimal.valueOf(99)), pojo(99).asDecimalOpt());
        assertEquals(Optional.of(BigDecimal.valueOf(99)), pojo((short) 99).asDecimalOpt());
        assertEquals(Optional.of(BigDecimal.valueOf(99)), pojo((byte) 99).asDecimalOpt());
        assertEquals(Optional.of(BigDecimal.valueOf(99)), pojo(BigInteger.valueOf(99)).asDecimalOpt());
        assertEquals(Optional.of(BigDecimal.valueOf(99.99)), pojo(BigDecimal.valueOf(99.99)).asDecimalOpt());
        assertTrue(pojo(new Data()).asDecimalOpt().isEmpty());
    }

    // Provenance: POJONodeTest#testAsDouble().
    void testAsDoubleVpack() throws Exception {
        assertEquals(0.0D, pojo(null).asDouble());
        assertEquals(99.99D, pojo(99.99D).asDouble());
        assertEquals(99D, pojo(99L).asDouble());
        assertEquals(99D, pojo(99).asDouble());
        assertEquals(99D, pojo((short) 99).asDouble());
        assertEquals(99D, pojo((byte) 99).asDouble());
        assertEquals(99D, pojo(BigInteger.valueOf(99)).asDouble());
        assertEquals(99.99D, pojo(BigDecimal.valueOf(99.99)).asDouble());
        assertThrows(JsonNodeException.class, () -> pojo(new Data()).asDouble());
    }

    // Provenance: POJONodeTest#testAsDoubleDefaultValue().
    void testAsDoubleDefaultValueVpack() throws Exception {
        assertEquals(10.42D, pojo(null).asDouble(10.42D));
        assertEquals(99.99D, pojo(99.99D).asDouble(10.42D));
        assertEquals(99D, pojo(99L).asDouble(10.42D));
        assertEquals(99D, pojo(99).asDouble(10.42D));
        assertEquals(99D, pojo((short) 99).asDouble(10.42D));
        assertEquals(99D, pojo((byte) 99).asDouble(10.42D));
        assertEquals(99D, pojo(BigInteger.valueOf(99)).asDouble(10.42D));
        assertEquals(99.99D, pojo(BigDecimal.valueOf(99.99)).asDouble(10.42D));
        assertEquals(10.42D, pojo(new Data()).asDouble(10.42D));
    }

    // Provenance: POJONodeTest#testAsDoubleOpt().
    void testAsDoubleOptVpack() throws Exception {
        assertTrue(pojo(null).asDoubleOpt().isEmpty());
        assertEquals(OptionalDouble.of(99.99D), pojo(99.99D).asDoubleOpt());
        assertEquals(OptionalDouble.of(99D), pojo(99L).asDoubleOpt());
        assertEquals(OptionalDouble.of(99D), pojo(99).asDoubleOpt());
        assertEquals(OptionalDouble.of(99D), pojo((short) 99).asDoubleOpt());
        assertEquals(OptionalDouble.of(99D), pojo((byte) 99).asDoubleOpt());
        assertEquals(OptionalDouble.of(99D), pojo(BigInteger.valueOf(99)).asDoubleOpt());
        assertEquals(OptionalDouble.of(99.99D), pojo(BigDecimal.valueOf(99.99)).asDoubleOpt());
        assertTrue(pojo(new Data()).asDoubleOpt().isEmpty());
    }

    // Provenance: POJONodeTest#testAsFloat().
    void testAsFloatVpack() throws Exception {
        assertEquals(0.0f, pojo(null).asFloat());
        assertEquals(99.99f, pojo(99.99D).asFloat());
        assertEquals(99f, pojo(99L).asFloat());
        assertEquals(99f, pojo(99).asFloat());
        assertEquals(99f, pojo((short) 99).asFloat());
        assertEquals(99f, pojo((byte) 99).asFloat());
        assertEquals(99f, pojo(BigInteger.valueOf(99)).asFloat());
        assertEquals(99.99f, pojo(BigDecimal.valueOf(99.99)).asFloat());
        assertThrows(JsonNodeException.class, () -> pojo(new Data()).asFloat());
    }

    // Provenance: POJONodeTest#testAsFloatDefaultValue().
    void testAsFloatDefaultValueVpack() throws Exception {
        assertEquals(10.0f, pojo(null).asFloat(10.0f));
        assertEquals(99.99f, pojo(99.99D).asFloat(10.0f));
        assertEquals(99f, pojo(99L).asFloat(10.0f));
        assertEquals(99f, pojo(99).asFloat(10.0f));
        assertEquals(99f, pojo((short) 99).asFloat(10.0f));
        assertEquals(99f, pojo((byte) 99).asFloat(10.0f));
        assertEquals(99f, pojo(BigInteger.valueOf(99)).asFloat(10.0f));
        assertEquals(99.99f, pojo(BigDecimal.valueOf(99.99)).asFloat(10.0f));
        assertEquals(10.0f, pojo(new Data()).asFloat(10.0f));
    }
private static final class Data {
    }

    void __invoke_testAsBigIntegerOptVpack() throws Exception {
        try {
            testAsBigIntegerOptVpack();
        } finally {
        }
    }


    void __invoke_testAsBooleanVpack() throws Exception {
        try {
            testAsBooleanVpack();
        } finally {
        }
    }


    void __invoke_testAsBooleanDefaultValueVpack() throws Exception {
        try {
            testAsBooleanDefaultValueVpack();
        } finally {
        }
    }


    void __invoke_testAsBooleanOptVpack() throws Exception {
        try {
            testAsBooleanOptVpack();
        } finally {
        }
    }


    void __invoke_testAsDecimalVpack() throws Exception {
        try {
            testAsDecimalVpack();
        } finally {
        }
    }


    void __invoke_testAsDecimalDefaultValueVpack() throws Exception {
        try {
            testAsDecimalDefaultValueVpack();
        } finally {
        }
    }


    void __invoke_testAsDecimalOptVpack() throws Exception {
        try {
            testAsDecimalOptVpack();
        } finally {
        }
    }


    void __invoke_testAsDoubleVpack() throws Exception {
        try {
            testAsDoubleVpack();
        } finally {
        }
    }


    void __invoke_testAsDoubleDefaultValueVpack() throws Exception {
        try {
            testAsDoubleDefaultValueVpack();
        } finally {
        }
    }


    void __invoke_testAsDoubleOptVpack() throws Exception {
        try {
            testAsDoubleOptVpack();
        } finally {
        }
    }


    void __invoke_testAsFloatVpack() throws Exception {
        try {
            testAsFloatVpack();
        } finally {
        }
    }


    void __invoke_testAsFloatDefaultValueVpack() throws Exception {
        try {
            testAsFloatDefaultValueVpack();
        } finally {
        }
    }

}
