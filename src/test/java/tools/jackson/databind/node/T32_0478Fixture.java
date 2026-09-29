package tools.jackson.databind.node;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.core.Base64Variant;
import tools.jackson.core.Base64Variants;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.type.WritableTypeId;
import tools.jackson.databind.JacksonSerializable;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.jsontype.TypeSerializer;
import tools.jackson.databind.node.BinaryNode;
import tools.jackson.databind.node.ObjectNode;
import tools.jackson.databind.util.TokenBuffer;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0478Fixture {
private static final byte[] FALSE = VPackWireFixtureTest.hex("19");
private static final byte[] TRUE = VPackWireFixtureTest.hex("1a");
private static final byte[] DOUBLE_EXP = VPackWireFixtureTest.hex(
            "1b 00 80 08 6a 03 1c 76 42");
private final VPackMapper MAPPER = VPackMapper.builder().build();

    // Provenance: JsonNodeConversionsTest#nestedByteArrayToTree6059().
    void nestedByteArrayToTree6059Vpack() {
        final byte[] bytes = { 4, 5, 6 };

        JsonNode object = MAPPER.valueToTree(Collections.singletonMap("b", bytes));
        JsonNode objectBinary = object.get("b");
        assertInstanceOf(BinaryNode.class, objectBinary);
        assertArrayEquals(bytes, objectBinary.binaryValue());

        JsonNode array = MAPPER.valueToTree(Collections.singletonList(bytes));
        JsonNode arrayBinary = array.get(0);
        assertInstanceOf(BinaryNode.class, arrayBinary);
        assertArrayEquals(bytes, arrayBinary.binaryValue());
    }

    // Provenance: JsonNodeConversionsTest#testAsBoolean().
    void testAsBooleanVpack() throws Exception {
        assertEquals(false, MAPPER.getNodeFactory().booleanNode(false).asBoolean());
        assertEquals(true, MAPPER.getNodeFactory().booleanNode(true).asBoolean());
        assertEquals(false, MAPPER.getNodeFactory().numberNode(0).asBoolean());
        assertEquals(true, MAPPER.getNodeFactory().numberNode(1).asBoolean());
        assertEquals(false, MAPPER.getNodeFactory().numberNode(0L).asBoolean());
        assertEquals(true, MAPPER.getNodeFactory().numberNode(-34L).asBoolean());
        assertEquals(true, MAPPER.getNodeFactory().stringNode("true").asBoolean());
        assertEquals(false, MAPPER.getNodeFactory().stringNode("false").asBoolean());
        assertEquals(true, MAPPER.getNodeFactory().pojoNode(Boolean.TRUE).asBoolean());
        assertEquals(false, MAPPER.readTree(FALSE).asBoolean());
        assertEquals(true, MAPPER.readTree(TRUE).asBoolean());
    }

    // Provenance: JsonNodeConversionsTest#testBase64Text().
    void testBase64TextVpack() throws Exception {
        final int[] lengths = { 1, 2, 3, 4, 7, 9, 32, 33, 34, 35 };
        final Base64Variant[] variants = {
                Base64Variants.MIME,
                Base64Variants.MIME_NO_LINEFEEDS,
                Base64Variants.MODIFIED_FOR_URL,
                Base64Variants.PEM
        };

        for (int length : lengths) {
            byte[] input = new byte[length];
            for (int i = 0; i < input.length; ++i) input[i] = (byte) i;
            for (Base64Variant variant : variants) {
                try (VPackParser parser = (VPackParser) MAPPER.tokenStreamFactory()
                        .createParser(vpackString(variant.encode(input)))) {
                    assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
                    assertArrayEquals(input, parser.getBinaryValue(variant));
                }
            }
        }
    }

    // Provenance: JsonNodeConversionsTest#testBeanToTree().
    void testBeanToTreeVpack() throws Exception {
        CustomSerializedPojo pojo = new CustomSerializedPojo();
        pojo.setFoo("bar");
        JsonNode expected = MAPPER.readTree(VPackWireFixtureTest.hex(
                "14 0b 43 66 6f 6f 43 62 61 72 01"));

        JsonNode node = MAPPER.valueToTree(pojo);
        assertEquals(expected, node);
        assertEquals(expected, MAPPER.writer().valueToTree(pojo));
    }

    // Provenance: JsonNodeConversionsTest#testBigDecimalAsPlainStringTreeConversion().
    void testBigDecimalAsPlainStringTreeConversionVpack() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("pi", new BigDecimal("3.00000000"));

        JsonNode tree = MAPPER.valueToTree(map);
        assertNotNull(tree);
        assertEquals(1, tree.size());
        assertTrue(tree.has("pi"));
        assertEquals(new BigDecimal("3.00000000"), tree.get("pi").decimalValue());
        assertEquals(8, tree.get("pi").decimalValue().scale());
        assertEquals(tree, MAPPER.writer().valueToTree(map));
    }

    // Provenance: JsonNodeConversionsTest#testBufferedLongViaCoercion().
    void testBufferedLongViaCoercionVpack() throws Exception {
        ObjectNode tree = MAPPER.createObjectNode();
        tree.set("longObj", MAPPER.readTree(DOUBLE_EXP));
        tree.put("_class", LongContainer1940.class.getName());

        LongContainer1940 result = MAPPER.treeToValue(tree, LongContainer1940.class);
        assertEquals(1519348261000L, result.longObj.longValue());
    }

    // Provenance: JsonNodeConversionsTest#testConversionOfPojos().
    void testConversionOfPojosVpack() throws Exception {
        Issue467Bean input = new Issue467Bean(13);
        JsonNode expected = MAPPER.readTree(VPackWireFixtureTest.hex(
                "14 07 41 78 28 0d 01"));

        assertEquals(expected, MAPPER.readTree(MAPPER.writeValueAsBytes(input)));
        assertEquals(expected, MAPPER.valueToTree(input));
        assertEquals(expected, MAPPER.writer().valueToTree(input));
    }

    // Provenance: JsonNodeConversionsTest#testConversionOfTrees().
    void testConversionOfTreesVpack() throws Exception {
        Issue467Tree input = new Issue467Tree();
        JsonNode expected = MAPPER.readTree(TRUE);

        assertEquals(expected, MAPPER.readTree(MAPPER.writeValueAsBytes(input)));
        JsonNode tree = MAPPER.valueToTree(input);
        assertEquals(expected, tree);
        assertEquals(expected, MAPPER.writer().valueToTree(tree));
    }

    // Provenance: JsonNodeConversionsTest#testConversionsOfNull().
    void testConversionsOfNullVpack() throws Exception {
        JsonNode node = MAPPER.valueToTree(null);
        assertNotNull(node);
        assertTrue(node.isNull());
        node = MAPPER.writer().valueToTree(null);
        assertNotNull(node);
        assertTrue(node.isNull());

        assertNull(MAPPER.treeToValue(node, Root.class));
        assertNull(MAPPER.treeToValue(node, MAPPER.constructType(Root.class)));

        AtomicReference<?> result = MAPPER.treeToValue(node, AtomicReference.class);
        assertNotNull(result);
        assertNull(result.get());
        result = MAPPER.treeToValue(node, MAPPER.constructType(AtomicReference.class));
        assertNotNull(result);
        assertNull(result.get());
    }

    // Provenance: JsonNodeConversionsTest#testEmbeddedByteArray().
    void testEmbeddedByteArrayVpack() throws Exception {
        TokenBuffer buffer = TokenBuffer.forGeneration();
        buffer.writePOJO(new byte[3]);
        JsonNode node = MAPPER.readTree(buffer.asParser(ObjectReadContext.empty()));
        buffer.close();

        assertTrue(node.isBinary());
        assertArrayEquals(new byte[3], node.binaryValue());
    }

    // Provenance: JsonNodeConversionsTest#testIssue709().
    void testIssue709Vpack() throws Exception {
        byte[] inputData = { 1, 2, 3 };
        ObjectNode node = MAPPER.createObjectNode();
        node.put("data", inputData);

        Issue709Bean result = MAPPER.treeToValue(node, Issue709Bean.class);
        Issue709Bean resultFromBytes = MAPPER.readValue(
                MAPPER.writeValueAsBytes(node), Issue709Bean.class);
        Issue709Bean resultFromConvert = MAPPER.convertValue(node, Issue709Bean.class);

        assertArrayEquals(inputData, result.data);
        assertArrayEquals(inputData, resultFromBytes.data);
        assertArrayEquals(inputData, resultFromConvert.data);
    }

    // Provenance: JsonNodeConversionsTest#testNodeConvert().
    void testNodeConvertVpack() throws Exception {
        ObjectNode source = (ObjectNode) MAPPER.readTree(
                VPackWireFixtureTest.hex("0a"));
        JsonNode node = source;
        ObjectNode result = MAPPER.treeToValue(node, ObjectNode.class);
        assertSame(source, result);
        result = MAPPER.treeToValue(node, MAPPER.constructType(ObjectNode.class));
        assertSame(source, result);
    }
private static byte[] vpackString(String value) {
        byte[] utf8 = value.getBytes(StandardCharsets.UTF_8);
        if (utf8.length > 126) throw new IllegalArgumentException("fixture too long");
        byte[] result = new byte[utf8.length + 1];
        result[0] = (byte) (0x40 + utf8.length);
        System.arraycopy(utf8, 0, result, 1, utf8.length);
        return result;
    }
private static final JsonNode MAPPER_NODE_TRUE = new VPackMapper().getNodeFactory()
            .booleanNode(true);
static class Root { }
static class Issue709Bean {
        public byte[] data;
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY,
            property = "_class")
    static class LongContainer1940 {
        public Long longObj;
    }
@JsonSerialize(using = Issue467Serializer.class)
    static class Issue467Bean {
        public int i;

        Issue467Bean() { }
        Issue467Bean(int value) { i = value; }
    }
static class Issue467Serializer extends ValueSerializer<Issue467Bean> {
        @Override
        public void serialize(Issue467Bean value, JsonGenerator generator,
                SerializationContext provider) {
            generator.writePOJO(new Issue467TmpBean(value.i));
        }
    }
static class Issue467TmpBean {
        public int x;

        Issue467TmpBean(int value) { x = value; }
    }
@JsonSerialize(using = Issue467TreeSerializer.class)
    static class Issue467Tree { }
static class Issue467TreeSerializer extends ValueSerializer<Issue467Tree> {
        @Override
        public void serialize(Issue467Tree value, JsonGenerator generator,
                SerializationContext provider) {
            generator.writeTree(MAPPER_NODE_TRUE);
        }
    }
static class CustomSerializedPojo implements JacksonSerializable {
        private final ObjectNode node = new VPackMapper().createObjectNode();

        void setFoo(String foo) { node.put("foo", foo); }

        @Override
        public void serialize(JsonGenerator generator, SerializationContext provider) {
            generator.writeTree(node);
        }

        @Override
        public void serializeWithType(JsonGenerator generator, SerializationContext ctxt,
                TypeSerializer typeSer) {
            WritableTypeId typeId = new WritableTypeId(this, JsonToken.START_OBJECT);
            typeSer.writeTypePrefix(generator, ctxt, typeId);
            serialize(generator, ctxt);
            typeSer.writeTypePrefix(generator, ctxt, typeId);
        }
    }

    void __invoke_nestedByteArrayToTree6059Vpack() throws Exception {
        try {
            nestedByteArrayToTree6059Vpack();
        } finally {
        }
    }


    void __invoke_testAsBooleanVpack() throws Exception {
        try {
            testAsBooleanVpack();
        } finally {
        }
    }


    void __invoke_testBase64TextVpack() throws Exception {
        try {
            testBase64TextVpack();
        } finally {
        }
    }


    void __invoke_testBeanToTreeVpack() throws Exception {
        try {
            testBeanToTreeVpack();
        } finally {
        }
    }


    void __invoke_testBigDecimalAsPlainStringTreeConversionVpack() throws Exception {
        try {
            testBigDecimalAsPlainStringTreeConversionVpack();
        } finally {
        }
    }


    void __invoke_testBufferedLongViaCoercionVpack() throws Exception {
        try {
            testBufferedLongViaCoercionVpack();
        } finally {
        }
    }


    void __invoke_testConversionOfPojosVpack() throws Exception {
        try {
            testConversionOfPojosVpack();
        } finally {
        }
    }


    void __invoke_testConversionOfTreesVpack() throws Exception {
        try {
            testConversionOfTreesVpack();
        } finally {
        }
    }


    void __invoke_testConversionsOfNullVpack() throws Exception {
        try {
            testConversionsOfNullVpack();
        } finally {
        }
    }


    void __invoke_testEmbeddedByteArrayVpack() throws Exception {
        try {
            testEmbeddedByteArrayVpack();
        } finally {
        }
    }


    void __invoke_testIssue709Vpack() throws Exception {
        try {
            testIssue709Vpack();
        } finally {
        }
    }


    void __invoke_testNodeConvertVpack() throws Exception {
        try {
            testNodeConvertVpack();
        } finally {
        }
    }

}
