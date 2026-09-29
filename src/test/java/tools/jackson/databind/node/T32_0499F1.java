package tools.jackson.databind.node;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.exc.JsonNodeException;
import tools.jackson.databind.node.ObjectNode;
import tools.jackson.databind.node.POJONode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0499F1 {
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] STRING_OBJECT_ABC = VPackWireFixtureTest.hex(
            "14 0f 41 61 41 61 41 62 41 62 41 63 41 63 03");
private static final byte[] NULLS_AND_NUMBER = VPackWireFixtureTest.hex(
            "14 0c 41 61 18 41 62 32 41 63 18 03");
private static final byte[] SIMPLE_OBJECT = VPackWireFixtureTest.hex(
            "14 0c 43 6b 65 79 31 41 62 41 78 02");
private static final byte[] SIMPLE_PATH = VPackWireFixtureTest.hex(
            "14 11 47 72 65 73 75 6c 74 73 14 06 41 61 33 01 01");
private static final byte[] ARRAY_123 = VPackWireFixtureTest.hex(
            "02 05 31 32 33");
private final VPackMapper mapper = new VPackMapper();

    // Provenance: POJONodeTest#testAddJava8DateAsPojo().
    void testAddJava8DateAsPojoVpack() throws Exception {
        LocalDateTime dateTime = LocalDateTime.parse("2025-03-31T12:00");
        JsonNode node = ((ObjectNode) mapper.readTree(EMPTY_OBJECT)).putPOJO("test", dateTime);
        byte[] encoded = mapper.writeValueAsBytes(node);
        assertNotNull(encoded);

        JsonNode result = mapper.readTree(encoded);
        assertEquals(dateTime, LocalDateTime.parse(result.path("test").asString()));
    }

    // Provenance: POJONodeTest#testAsBigInteger().
    void testAsBigIntegerVpack() {
        assertEquals(BigInteger.ZERO, new POJONode(null).asBigInteger());
        assertEquals(BigInteger.valueOf(99), new POJONode(99.99D).asBigInteger());
        assertEquals(BigInteger.valueOf(99), new POJONode(99L).asBigInteger());
        assertEquals(BigInteger.valueOf(99), new POJONode(99).asBigInteger());
        assertEquals(BigInteger.valueOf(99), new POJONode((short) 99).asBigInteger());
        assertEquals(BigInteger.valueOf(99), new POJONode((byte) 99).asBigInteger());
        assertEquals(BigInteger.valueOf(99), new POJONode(BigInteger.valueOf(99)).asBigInteger());
        assertEquals(BigInteger.valueOf(99), new POJONode(BigDecimal.valueOf(99.99)).asBigInteger());
        assertThrows(JsonNodeException.class,
                () -> new POJONode(new Object()).asBigInteger());
    }

    // Provenance: POJONodeTest#testAsBigIntegerDefaultValue().
    void testAsBigIntegerDefaultValueVpack() {
        assertEquals(BigInteger.TEN, new POJONode(null).asBigInteger(BigInteger.TEN));
        assertEquals(BigInteger.valueOf(99), new POJONode(99.99D).asBigInteger(BigInteger.TEN));
        assertEquals(BigInteger.valueOf(99), new POJONode(99L).asBigInteger(BigInteger.TEN));
        assertEquals(BigInteger.valueOf(99), new POJONode(99).asBigInteger(BigInteger.TEN));
        assertEquals(BigInteger.valueOf(99), new POJONode((short) 99).asBigInteger(BigInteger.TEN));
        assertEquals(BigInteger.valueOf(99), new POJONode((byte) 99).asBigInteger(BigInteger.TEN));
        assertEquals(BigInteger.valueOf(99),
                new POJONode(BigInteger.valueOf(99)).asBigInteger(BigInteger.TEN));
        assertEquals(BigInteger.valueOf(99),
                new POJONode(BigDecimal.valueOf(99.99)).asBigInteger(BigInteger.TEN));
        assertEquals(BigInteger.TEN, new POJONode(new Object()).asBigInteger(BigInteger.TEN));
    }

    void __invoke_testAddJava8DateAsPojoVpack() throws Exception {
        try {
            testAddJava8DateAsPojoVpack();
        } finally {
        }
    }


    void __invoke_testAsBigIntegerVpack() throws Exception {
        try {
            testAsBigIntegerVpack();
        } finally {
        }
    }


    void __invoke_testAsBigIntegerDefaultValueVpack() throws Exception {
        try {
            testAsBigIntegerDefaultValueVpack();
        } finally {
        }
    }

}
