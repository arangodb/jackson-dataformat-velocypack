package tools.jackson.databind.node;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonPointer;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.DatabindContext;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.exc.JsonNodeException;
import tools.jackson.databind.jsontype.TypeDeserializer;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;
import tools.jackson.databind.JsonNode.OverwriteMode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0506F2 {
private static final byte[] SKIP_CHILDREN_ROOT = VPackWireFixtureTest.hex(
            "14 29 45 69 6e 6e 65 72 14 0e 45 76 61 6c 75 65 44 74 65 73 74 01 "
          + "47 75 6e 6b 6e 6f 77 6e 14 0a 45 69 6e 6e 65 72 18 01 02");
private static final byte[] SPEC_ROOT = VPackWireFixtureTest.hex(
            "14 98 01 45 49 6d 61 67 65 14 8e 01 "
          + "45 57 69 64 74 68 29 20 03 "
          + "46 48 65 69 67 68 74 29 58 02 "
          + "45 54 69 74 6c 65 54 56 69 65 77 20 66 72 6f 6d 20 31 35 74 68 20 46 6c 6f 6f 72 "
          + "49 54 68 75 6d 62 6e 61 69 6c 14 41 "
          + "43 55 72 6c 66 68 74 74 70 3a 2f 2f 77 77 77 2e 65 78 61 6d 70 6c 65 2e 63 6f 6d 2f 69 6d 61 67 65 2f 34 38 31 39 38 39 39 34 33 "
          + "46 48 65 69 67 68 74 28 7d 45 57 69 64 74 68 43 31 30 30 03 "
          + "43 49 44 73 13 0d 28 74 29 af 03 28 ea 29 89 97 04 05 01");
private static final byte[] TEXT_BINARY = VPackWireFixtureTest.hex(
            "48 20 20 20 41 50 73 3d 0a");
private static final byte[] TEXT_BINARY_GARBAGE = VPackWireFixtureTest.hex(
            "44 3f 21 3f 3f");
private static final byte[] TYPED_TREE = VPackWireFixtureTest.hex(
            "0b 3f 02 46 40 63 6c 61 73 73 6a 74 6f 6f 6c 73" +
                "2e 6a 61 63 6b 73 6f 6e 2e 64 61 74 61 62 69 6e" +
                "64 2e 6e 6f 64 65 2e 54 33 32 5f 30 35 30 36 46" +
                "32 24 46 6f 6f 43 62 61 72 43 62 61 7a 03 35");
private final VPackMapper mapper = new VPackMapper();

    // Provenance: WithPathTest#testInvalidWithArrayTrivial().
    void testInvalidWithArrayTrivialVpack() throws Exception {
        ArrayNode root = (ArrayNode) mapper.readTree(VPackWireFixtureTest.hex("01"));
        JsonNodeException failure = assertThrows(JsonNodeException.class,
                () -> root.withObject(JsonPointer.compile("/a")));
        assertTrue(failure.getMessage().contains("Cannot replace context node"));
        assertTrue(failure.getMessage().contains("ArrayNode"));
    }

    // Provenance: WithPathTest#testInvalidWithObjectTrivial().
    void testInvalidWithObjectTrivialVpack() throws Exception {
        ArrayNode root = (ArrayNode) mapper.readTree(VPackWireFixtureTest.hex("01"));
        JsonNodeException failure = assertThrows(JsonNodeException.class,
                () -> root.withObject(JsonPointer.compile("/a")));
        assertTrue(failure.getMessage().contains("Cannot replace context node"));
        assertTrue(failure.getMessage().contains("ArrayNode"));
    }

    // Provenance: WithPathTest#testObjectPathWithReplace().
    void testObjectPathWithReplaceVpack() throws Exception {
        for (boolean compiled : new boolean[] { true, false }) {
            final ObjectNode firstRoot = (ObjectNode) mapper.readTree(
                    VPackWireFixtureTest.hex("14 07 41 61 28 0d 01"));
            JsonPointer path = JsonPointer.compile("/a/b");
            JsonNodeException initial = assertThrows(JsonNodeException.class,
                    () -> firstRoot.withObject(path));
            assertTrue(initial.getMessage().contains("Cannot replace `JsonNode` of type"));
            assertTrue(initial.getMessage().contains("OverwriteMode.NULLS"));

            ObjectNode root = firstRoot;
            root.putNull("a");
            ObjectNode nested = compiled ? root.withObject(path) : root.withObject("/a/b");
            nested.put("value", 42);
            assertEquals(42, root.path("a").path("b").path("value").intValue());

            final ObjectNode replacementRoot = (ObjectNode) mapper.readTree(
                    VPackWireFixtureTest.hex("14 06 41 61 18 01"));
            JsonNodeException prevented = assertThrows(JsonNodeException.class,
                    () -> replacementRoot.withObject(path, OverwriteMode.NONE, true));
            assertTrue(prevented.getMessage().contains("Cannot replace `JsonNode` of type"));
            assertTrue(prevented.getMessage().contains("OverwriteMode.NONE"));
        }
    }
private ObjectMapper typedMapper() {
        return VPackMapper.builder()
                .activateDefaultTyping(NoCheckSubTypeValidator.instance,
                        DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY)
                .build();
    }
private static void assertToken(JsonToken expected, JsonToken actual) {
        assertEquals(expected, actual);
    }
@JsonIgnoreProperties(ignoreUnknown = true)
    public static class Jackson370Bean {
        public Inner inner;
    }
public static class Inner {
        public String value;
    }
public static class Foo {
        public String bar;
        public Foo() { }
        public Foo(String bar) { this.bar = bar; }
    }
public static class SavedCookie {
        public String name;
        public String value;
        public SavedCookie() { }
        public SavedCookie(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }
public static class SavedCookieDeserializer extends ValueDeserializer<SavedCookie> {
        @Override
        public SavedCookie deserialize(JsonParser parser, DeserializationContext ctxt) {
            JsonNode node = parser.objectReadContext().readTree(parser);
            return new SavedCookie(node.path("name").stringValue(),
                    node.path("value").stringValue());
        }

        @Override
        public SavedCookie deserializeWithType(JsonParser parser, DeserializationContext ctxt,
                TypeDeserializer typeDeserializer) {
            return (SavedCookie) typeDeserializer.deserializeTypedFromObject(parser, ctxt);
        }
    }
private static final class NoCheckSubTypeValidator
            extends tools.jackson.databind.jsontype.PolymorphicTypeValidator.Base {
        private static final long serialVersionUID = 1L;
        static final NoCheckSubTypeValidator instance = new NoCheckSubTypeValidator();

        @Override
        public Validity validateBaseType(DatabindContext ctxt, JavaType baseType) {
            return Validity.ALLOWED;
        }
    }

    void __invoke_testInvalidWithArrayTrivialVpack() throws Exception {
        try {
            testInvalidWithArrayTrivialVpack();
        } finally {
        }
    }


    void __invoke_testInvalidWithObjectTrivialVpack() throws Exception {
        try {
            testInvalidWithObjectTrivialVpack();
        } finally {
        }
    }


    void __invoke_testObjectPathWithReplaceVpack() throws Exception {
        try {
            testObjectPathWithReplaceVpack();
        } finally {
        }
    }

}
