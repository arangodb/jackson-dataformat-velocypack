package tools.jackson.databind.node;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.io.SerializedString;
import tools.jackson.core.exc.StreamWriteException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.TreeBuildingGenerator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0503F2 {
private static final byte[] ABC = VPackWireFixtureTest.hex("43 61 62 63");
private static final byte[] DEF = VPackWireFixtureTest.hex("43 64 65 66");
private static final byte[] OBJECT = VPackWireFixtureTest.hex(
            "14 17 "
            + "43 6b 65 79 31 "
            + "41 62 41 78 "
            + "45 61 72 72 61 79 13 05 31 19 02 "
            + "03");
private static final byte[] BINARY = VPackWireFixtureTest.hex(
            "c0 05 01 02 03 04 06");
private final VPackMapper mapper = new VPackMapper();
private final JsonMapper jsonMapper = JsonMapper.builder().build();

    // Provenance: TreeBuildingGeneratorTest#testBasicArrayBuilding().
    void testBasicArrayBuildingVpack() throws Exception {
        TreeBuildingGenerator generator = generator();
        assertFalse(generator.isClosed());
        List<String> values = Arrays.asList("foo", "bar");
        generator.writeStartArray(values, values.size());
        generator.writeString(values.get(0));
        generator.writeString(new SerializedString(values.get(1)));
        generator.writeNumber((short) 123);
        generator.writeNull();
        generator.writeNumber(BigInteger.valueOf(999));
        generator.writeBoolean(false);
        generator.writeArray(new long[0], 0, 0);
        generator.writeStartObject(Boolean.TRUE, 0);
        generator.writeEndObject();
        generator.writeEndArray();

        assertEquals("[\"foo\",\"bar\",123,null,999,false,[],{}]",
                generator.treeBuilt().toString());
    }

    // Provenance: TreeBuildingGeneratorTest#testBasicObjectBuilding().
    void testBasicObjectBuildingVpack() throws Exception {
        TreeBuildingGenerator generator = generator();
        generator.writeStartObject("foo");
        generator.writeName(new SerializedString("null"));
        generator.writeNull();
        generator.writeName("arr");
        generator.writeStartArray("foo", 0);
        generator.writeEndArray();
        generator.writeName("b");
        generator.writeBoolean(true);
        generator.writeName("ob");
        generator.writeStartObject(Boolean.TRUE, 0);
        generator.writeEndObject();
        generator.writeEndObject();

        assertEquals("{\"null\":null,\"arr\":[],\"b\":true,\"ob\":{}}",
                generator.treeBuilt().toString());
    }

    // Provenance: TreeBuildingGeneratorTest#testNumberAsString().
    void testNumberAsStringVpack() throws Exception {
        try (JsonGenerator generator = generator()) {
            generator.writeStartArray();
            StreamWriteException failure = assertThrows(StreamWriteException.class,
                    () -> generator.writeNumber("123"));
            assertTrue(failure.getMessage().contains(
                    "TreeBuildingGenerator` does not support `writeNumber(String)"));
        }
    }
private TreeBuildingGenerator generator() {
        return TreeBuildingGenerator.forSerialization(null, mapper.getNodeFactory());
    }
private void verifyToStrings(JsonNode node) throws Exception {
        assertEquals(jsonMapper.writeValueAsString(node), node.toString());
        assertEquals(jsonMapper.writer().withDefaultPrettyPrinter()
                .writeValueAsString(node), node.toPrettyString());
    }

    void __invoke_testBasicArrayBuildingVpack() throws Exception {
        try {
            testBasicArrayBuildingVpack();
        } finally {
        }
    }


    void __invoke_testBasicObjectBuildingVpack() throws Exception {
        try {
            testBasicObjectBuildingVpack();
        } finally {
        }
    }


    void __invoke_testNumberAsStringVpack() throws Exception {
        try {
            testNumberAsStringVpack();
        } finally {
        }
    }

}
