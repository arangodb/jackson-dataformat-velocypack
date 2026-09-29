package tools.jackson.databind.node;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.DatabindContext;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.exc.JsonNodeException;
import tools.jackson.databind.jsontype.TypeDeserializer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0506F0 {
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
                "30 24 46 6f 6f 43 62 61 72 43 62 61 7a 03 35");
private final VPackMapper mapper = new VPackMapper();

    // Provenance: TreeTraversingParserTest#testSkipChildrenWrt370().
    void testSkipChildrenWrt370Vpack() throws Exception {
        Jackson370Bean value = mapper.readValue(SKIP_CHILDREN_ROOT, Jackson370Bean.class);
        assertNotNull(value.inner);
        assertEquals("test", value.inner.value);
    }

    // Provenance: TreeTraversingParserTest#testSpecDoc().
    void testSpecDocVpack() throws Exception {
        try (JsonParser parser = mapper.readTree(SPEC_ROOT).traverse(ObjectReadContext.empty())) {
            assertToken(JsonToken.START_OBJECT, parser.nextToken());
            assertToken(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("Image", parser.getString());
            assertEquals("Image", parser.currentName());
            assertToken(JsonToken.START_OBJECT, parser.nextToken());
            assertToken(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("Width", parser.getString());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals("800", parser.getString());
            assertToken(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("Height", parser.getString());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals("600", parser.getString());
            assertToken(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("Title", parser.getString());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("View from 15th Floor", parser.getString());
            assertToken(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("Thumbnail", parser.getString());
            assertToken(JsonToken.START_OBJECT, parser.nextToken());
            assertToken(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("Url", parser.getString());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("http://www.example.com/image/481989943", parser.getString());
            assertToken(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("Height", parser.getString());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals("125", parser.getString());
            assertToken(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("Width", parser.getString());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("100", parser.getString());
            assertToken(JsonToken.END_OBJECT, parser.nextToken());
            assertToken(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(116, parser.nextIntValue(-1));
            assertEquals(943, parser.nextIntValue(-1));
            assertEquals(234, parser.nextIntValue(-1));
            assertEquals(38793, parser.nextIntValue(-1));
            assertToken(JsonToken.END_ARRAY, parser.nextToken());
            assertToken(JsonToken.END_OBJECT, parser.nextToken());
            assertToken(JsonToken.END_OBJECT, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }

    // Provenance: TreeTraversingParserTest#testTextAsBinary().
    void testTextAsBinaryVpack() throws Exception {
        try (JsonParser parser = mapper.readTree(TEXT_BINARY).traverse(ObjectReadContext.empty())) {
            assertNull(parser.currentToken());
            assertToken(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("APs=", parser.getString().trim());
            assertArrayEquals(new byte[] { 0, -5 }, parser.getBinaryValue());
            assertNull(parser.nextToken());
        }
        try (JsonParser parser = mapper.readTree(TEXT_BINARY_GARBAGE).traverse(ObjectReadContext.empty())) {
            assertToken(JsonToken.VALUE_STRING, parser.nextToken());
            JsonNodeException failure = assertThrows(JsonNodeException.class, parser::getBinaryValue);
            assertTrue(failure.getMessage().contains("binaryValue"));
            assertTrue(failure.getMessage().contains("Illegal character"));
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

    void __invoke_testSkipChildrenWrt370Vpack() throws Exception {
        try {
            testSkipChildrenWrt370Vpack();
        } finally {
        }
    }


    void __invoke_testSpecDocVpack() throws Exception {
        try {
            testSpecDocVpack();
        } finally {
        }
    }


    void __invoke_testTextAsBinaryVpack() throws Exception {
        try {
            testTextAsBinaryVpack();
        } finally {
        }
    }

}
