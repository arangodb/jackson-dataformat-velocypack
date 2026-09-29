package tools.jackson.databind;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tools.jackson.core.Base64Variants;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonPointer;
import tools.jackson.core.JsonToken;
import tools.jackson.core.json.JsonReadFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.cfg.ContextAttributes;
import tools.jackson.databind.cfg.EnumFeature;
import tools.jackson.databind.deser.DeserializationProblemHandler;
import tools.jackson.databind.exc.InvalidDefinitionException;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.jsontype.TypeSerializer;
import tools.jackson.databind.node.BaseJsonNode;
import tools.jackson.databind.node.JsonNodeFactory;
import tools.jackson.databind.node.JsonNodeType;
import tools.jackson.databind.node.ObjectNode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0132Fixture {
private static final byte[] OBJECT_WITH_POINT = VPackWireFixtureTest.hex(
            "14 09 41 78 31 41 79 32 02");
private static final byte[] ONE = VPackWireFixtureTest.hex("31");

    void customObjectNodeReadsPointFromLiteralVpackTree() throws Exception {
        ObjectMapper mapper = new VPackMapper();
        ObjectNode defaultNode = (ObjectNode) mapper.readTree(OBJECT_WITH_POINT);
        CustomObjectNode customObjectNode = new CustomObjectNode(defaultNode);

        Point point = mapper.readerFor(Point.class).readValue(customObjectNode);

        assertEquals(1, point.x);
        assertEquals(2, point.y);
    }

    void datatypeFeaturesCanBeToggledOnObjectReader() {
        ObjectReader reader = new VPackMapper().reader();
        reader = reader.withFeatures(EnumFeature.READ_ENUM_KEYS_USING_INDEX,
                EnumFeature.FAIL_ON_NUMBERS_FOR_ENUMS);
        assertTrue(reader.isEnabled(EnumFeature.READ_ENUM_KEYS_USING_INDEX));
        assertTrue(reader.isEnabled(EnumFeature.FAIL_ON_NUMBERS_FOR_ENUMS));

        reader = reader.withoutFeatures(EnumFeature.READ_ENUM_KEYS_USING_INDEX,
                EnumFeature.FAIL_ON_NUMBERS_FOR_ENUMS);
        assertFalse(reader.isEnabled(EnumFeature.READ_ENUM_KEYS_USING_INDEX));
        assertFalse(reader.isEnabled(EnumFeature.FAIL_ON_NUMBERS_FOR_ENUMS));
    }

    void deserializationFeaturesCanBeToggledOnObjectReader() {
        ObjectReader reader = new VPackMapper().reader();
        assertFalse(reader.isEnabled(tools.jackson.databind.MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES));
        assertFalse(reader.isEnabled(tools.jackson.core.StreamReadFeature.IGNORE_UNDEFINED));

        reader = reader.withoutFeatures(
                tools.jackson.databind.DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES,
                tools.jackson.databind.DeserializationFeature.FAIL_ON_INVALID_SUBTYPE);
        assertFalse(reader.isEnabled(tools.jackson.databind.DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES));
        assertFalse(reader.isEnabled(tools.jackson.databind.DeserializationFeature.FAIL_ON_INVALID_SUBTYPE));

        reader = reader.withFeatures(
                tools.jackson.databind.DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES,
                tools.jackson.databind.DeserializationFeature.FAIL_ON_INVALID_SUBTYPE);
        assertTrue(reader.isEnabled(tools.jackson.databind.DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES));
        assertTrue(reader.isEnabled(tools.jackson.databind.DeserializationFeature.FAIL_ON_INVALID_SUBTYPE));
        assertSame(reader, reader.with(
                tools.jackson.databind.DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES,
                tools.jackson.databind.DeserializationFeature.FAIL_ON_INVALID_SUBTYPE));
        assertSame(reader, reader.with(reader.getConfig()));
    }

    void getValueTypeTracksUnboundAndTypedReaders() {
        ObjectMapper mapper = new VPackMapper();
        ObjectReader reader = mapper.reader();
        assertNull(reader.getValueType());

        reader = reader.forType(String.class);
        assertEquals(mapper.constructType(String.class), reader.getValueType());
    }

    void emptyFileReadReportsNoContent() throws Exception {
        Path file = Files.createTempFile("vpack-t32-0132-", ".vpack");
        try {
            assertThrows(MismatchedInputException.class,
                    () -> new VPackMapper().readerFor(FilePerson.class)
                            .readValue(file.toFile()));
        } finally {
            Files.deleteIfExists(file);
        }
    }

    void jsonNodeCreationUsesReaderNodeFactory() {
        ObjectReader reader = new VPackMapper().reader();
        assertTrue(reader.createArrayNode().isArray());
        assertTrue(reader.createObjectNode().isObject());
        assertTrue(reader.booleanNode(true).isBoolean());
        assertTrue(reader.nullNode().isNull());
        assertTrue(reader.missingNode().isMissingNode());
        assertTrue(reader.stringNode("abc").isString());
    }

    void jsonReadFeaturesCanBeEnabledAndDisabledOnReader() {
        ObjectReader reader = new VPackMapper().reader();
        reader = reader.withFeatures(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER,
                JsonReadFeature.ALLOW_JAVA_COMMENTS);

        ObjectReader reset = reader.withoutFeatures(
                JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER,
                JsonReadFeature.ALLOW_JAVA_COMMENTS);
        assertNotSame(reader, reset);
    }

    void miscReaderCreationRetainsNodeFactoryAndBase64Configuration() {
        JsonNodeFactory nodeFactory = new JsonNodeFactory();
        ObjectReader reader = new VPackMapper().reader(nodeFactory);
        assertSame(nodeFactory, reader.jsonNodeFactory());

        reader = new VPackMapper().reader(Base64Variants.MODIFIED_FOR_URL);
        assertEquals(Base64Variants.MODIFIED_FOR_URL,
                reader.getConfig().getBase64Variant());
    }

    void miscSettingsRemainAvailableOnVpackReader() {
        ObjectMapper mapper = new VPackMapper();
        ObjectReader reader = mapper.reader();
        assertSame(mapper.tokenStreamFactory(), reader.parserFactory());
        assertNotNull(reader.typeFactory());
        assertNull(reader.getInjectableValues());

        reader = reader.withAttributes(Collections.emptyMap());
        ContextAttributes attributes = reader.getAttributes();
        assertNotNull(attributes);
        assertNull(attributes.getAttribute("abc"));
        assertSame(reader, reader.withoutAttribute("foo"));

        ObjectReader typed = reader.forType(mapper.constructType(String.class));
        assertNotSame(reader, typed);
        assertSame(typed, typed.forType(String.class));

        DeserializationProblemHandler handler = new DeserializationProblemHandler() { };
        typed = reader.withHandler(handler);
        assertNotSame(reader, typed);
        assertSame(typed, typed.withHandler(handler));
    }

    void missingTypeFailsForLiteralVpackInput() {
        ObjectReader reader = new VPackMapper().reader();
        InvalidDefinitionException exception = assertThrows(InvalidDefinitionException.class,
                () -> reader.readValue(ONE));
        assertTrue(exception.getMessage().contains("No value type configured"));
    }
static class FilePerson {
        public String name;
    }
record Point(int x, int y) { }
static class CustomObjectNode extends BaseJsonNode {
        private static final long serialVersionUID = 1L;
        private final ObjectNode delegate;

        CustomObjectNode(ObjectNode delegate) {
            this.delegate = delegate;
        }

        @Override
        protected String _valueDesc() { return "<CUSTOM>"; }

        @Override
        public boolean isObject() { return true; }

        @Override
        public int size() { return delegate.size(); }

        @Override
        public Set<Map.Entry<String, JsonNode>> properties() {
            return delegate.properties();
        }

        @Override
        public Collection<JsonNode> values() { return Collections.emptyList(); }

        @Override
        public JsonToken asToken() { return JsonToken.START_OBJECT; }

        @Override
        public void serialize(JsonGenerator generator, SerializationContext ctxt) { }

        @Override
        public void serializeWithType(JsonGenerator generator, SerializationContext ctxt,
                TypeSerializer typeSer) { }

        @Override
        public CustomObjectNode deepCopy() { return new CustomObjectNode(delegate); }

        @Override
        public JsonNode get(int index) { return null; }

        @Override
        public JsonNode path(String fieldName) { return null; }

        @Override
        public JsonNode path(int index) { return null; }

        @Override
        protected JsonNode _at(JsonPointer pointer) { return null; }

        @Override
        public JsonNodeType getNodeType() { return JsonNodeType.OBJECT; }

        @Override
        public String asString() { return ""; }

        @Override
        public JsonNode findValue(String fieldName) { return null; }

        @Override
        public JsonNode findParent(String fieldName) { return null; }

        @Override
        public List<JsonNode> findValues(String fieldName, List<JsonNode> foundSoFar) {
            return Collections.emptyList();
        }

        @Override
        public List<String> findValuesAsString(String fieldName, List<String> foundSoFar) {
            return foundSoFar;
        }

        @Override
        public List<JsonNode> findParents(String fieldName, List<JsonNode> foundSoFar) {
            return foundSoFar;
        }

        @Override
        public boolean equals(Object other) {
            return other == this || (other instanceof CustomObjectNode node
                    && delegate.equals(node.delegate));
        }

        @Override
        public int hashCode() { return delegate.hashCode(); }
    }

    void __invoke_customObjectNodeReadsPointFromLiteralVpackTree() throws Exception {
        try {
            customObjectNodeReadsPointFromLiteralVpackTree();
        } finally {
        }
    }


    void __invoke_datatypeFeaturesCanBeToggledOnObjectReader() throws Exception {
        try {
            datatypeFeaturesCanBeToggledOnObjectReader();
        } finally {
        }
    }


    void __invoke_deserializationFeaturesCanBeToggledOnObjectReader() throws Exception {
        try {
            deserializationFeaturesCanBeToggledOnObjectReader();
        } finally {
        }
    }


    void __invoke_getValueTypeTracksUnboundAndTypedReaders() throws Exception {
        try {
            getValueTypeTracksUnboundAndTypedReaders();
        } finally {
        }
    }


    void __invoke_emptyFileReadReportsNoContent() throws Exception {
        try {
            emptyFileReadReportsNoContent();
        } finally {
        }
    }


    void __invoke_jsonNodeCreationUsesReaderNodeFactory() throws Exception {
        try {
            jsonNodeCreationUsesReaderNodeFactory();
        } finally {
        }
    }


    void __invoke_jsonReadFeaturesCanBeEnabledAndDisabledOnReader() throws Exception {
        try {
            jsonReadFeaturesCanBeEnabledAndDisabledOnReader();
        } finally {
        }
    }


    void __invoke_miscReaderCreationRetainsNodeFactoryAndBase64Configuration() throws Exception {
        try {
            miscReaderCreationRetainsNodeFactoryAndBase64Configuration();
        } finally {
        }
    }


    void __invoke_miscSettingsRemainAvailableOnVpackReader() throws Exception {
        try {
            miscSettingsRemainAvailableOnVpackReader();
        } finally {
        }
    }


    void __invoke_missingTypeFailsForLiteralVpackInput() throws Exception {
        try {
            missingTypeFailsForLiteralVpackInput();
        } finally {
        }
    }

}
