package tools.jackson.databind;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0138F1 {
private static final byte[] VALUE_STRING = VPackWireFixtureTest.hex(
            "45 76 61 6c 75 65");
private static final byte[] POLY_OBJECT = VPackWireFixtureTest.hex(
            "0b 21 02 45 40 74 79 70 65 47 73 75 62 74 79 70 65 "
            + "47 63 6f 6e 74 65 6e 74 45 68 65 6c 6c 6f 03 11");
private static final byte[] POLY_ARRAY = VPackWireFixtureTest.hex(
            "02 23 0b 21 02 45 40 74 79 70 65 47 73 75 62 74 79 70 65 "
            + "47 63 6f 6e 74 65 6e 74 45 68 65 6c 6c 6f 03 11");

    void valueToTreeDirectPolymorphicTypeUsesLiteralVpackObject() throws Exception {
        ObjectMapper mapper = new VPackMapper();
        ObjectWriter writer = mapper.writerFor(new TypeReference<Supertype>() {});

        JsonNode tree = writer.valueToTree(new Subtype());

        assertEquals(mapper.readTree(POLY_OBJECT), tree);
        assertTrue(tree.has("@type"));
        assertEquals("subtype", tree.get("@type").asString());
        assertEquals("hello", tree.get("content").asString());
    }

    void valueToTreeListWrappedPolymorphicTypeUsesLiteralVpackArray()
            throws Exception {
        ObjectMapper mapper = new VPackMapper();
        ObjectWriter writer = mapper.writerFor(new TypeReference<List<Supertype>>() {});

        JsonNode tree = writer.valueToTree(List.of(new Subtype()));

        assertEquals(mapper.readTree(POLY_ARRAY), tree);
        assertEquals("subtype", tree.get(0).get("@type").asString());
        assertEquals("hello", tree.get(0).get("content").asString());
    }

    void valueToTreeOptionalWrappedPolymorphicTypeUsesLiteralVpackObject()
            throws Exception {
        ObjectMapper mapper = new VPackMapper();
        ObjectWriter writer = mapper.writerFor(new TypeReference<Optional<Supertype>>() {});

        JsonNode tree = writer.valueToTree(Optional.of(new Subtype()));

        assertEquals(mapper.readTree(POLY_OBJECT), tree);
        assertEquals("subtype", tree.get("@type").asString());
        assertEquals("hello", tree.get("content").asString());
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY,
            property = "@type")
    @JsonSubTypes(@JsonSubTypes.Type(name = "subtype", value = Subtype.class))
    private interface Supertype { }
@JsonTypeName("subtype")
    private static class Subtype implements Supertype {
        public String content = "hello";
    }

    void __invoke_valueToTreeDirectPolymorphicTypeUsesLiteralVpackObject() throws Exception {
        try {
            valueToTreeDirectPolymorphicTypeUsesLiteralVpackObject();
        } finally {
        }
    }


    void __invoke_valueToTreeListWrappedPolymorphicTypeUsesLiteralVpackArray() throws Exception {
        try {
            valueToTreeListWrappedPolymorphicTypeUsesLiteralVpackArray();
        } finally {
        }
    }


    void __invoke_valueToTreeOptionalWrappedPolymorphicTypeUsesLiteralVpackObject() throws Exception {
        try {
            valueToTreeOptionalWrappedPolymorphicTypeUsesLiteralVpackObject();
        } finally {
        }
    }

}
