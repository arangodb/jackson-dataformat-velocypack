package tools.jackson.databind;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import tools.jackson.core.FormatSchema;
import tools.jackson.core.SerializableString;
import tools.jackson.core.StreamWriteFeature;
import tools.jackson.core.Base64Variants;
import tools.jackson.core.io.SerializedString;
import tools.jackson.core.json.JsonWriteFeature;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.PropertyName;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.node.ObjectNode;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0135Fixture {
private static final byte[] OBJECT_A_ONE = VPackWireFixtureTest.hex(
            "0b 07 01 41 61 31 03");
private static final byte[] OBJECT_A_FIVE = VPackWireFixtureTest.hex(
            "0b 07 01 41 61 35 03");
private static final byte[] POLY_A = VPackWireFixtureTest.hex(
            "0b 13 02 44 74 79 70 65 41 41 45 76 61 6c 75 65 33 03 0a");
private static final byte[] POLY_B = VPackWireFixtureTest.hex(
            "0b 0f 02 44 74 79 70 65 41 42 41 62 3b 0a 03");

    void prettyPrinterHasNoTextualEffectOnLiteralVpack() throws Exception {
        ObjectWriter writer = new VPackMapper().writer();
        LinkedHashMap<String, Integer> data = new LinkedHashMap<>();
        data.put("a", 1);

        assertArrayEquals(OBJECT_A_ONE, writer.writeValueAsBytes(data));
        assertArrayEquals(OBJECT_A_ONE,
                writer.withDefaultPrettyPrinter().writeValueAsBytes(data));
        assertArrayEquals(OBJECT_A_ONE,
                writer.with((tools.jackson.core.PrettyPrinter) null)
                        .writeValueAsBytes(data));
    }

    void prefetchStateTracksWriterForType() {
        ObjectWriter writer = new VPackMapper().writer();
        assertFalse(writer.hasPrefetchedSerializer());
        assertTrue(writer.forType(String.class).hasPrefetchedSerializer());
    }

    void jsonPropertyQuotingFeatureDoesNotChangeLiteralVpack() throws Exception {
        ObjectWriter writer = new VPackMapper().writer();
        Map<String, Integer> data = Collections.singletonMap("a", 1);

        assertArrayEquals(OBJECT_A_ONE,
                writer.without(JsonWriteFeature.QUOTE_PROPERTY_NAMES)
                        .writeValueAsBytes(data));
        assertArrayEquals(OBJECT_A_ONE,
                writer.with(JsonWriteFeature.QUOTE_PROPERTY_NAMES)
                        .writeValueAsBytes(data));
    }

    void objectWriterWithNodeWritesIndependentLiteralObject() throws Exception {
        ObjectWriter writer = new VPackMapper().writer();
        assertNotNull(writer.jsonNodeFactory());
        ObjectNode object = writer.createObjectNode();
        object.put("a", 5);
        assertArrayEquals(OBJECT_A_FIVE,
                writer.forType(JsonNode.class).writeValueAsBytes(object));
        assertTrue(writer.createArrayNode().isArray());
    }

    void polymorphicTypingWritesOrdinaryVpackObjectProperties() throws Exception {
        ObjectWriter writer = new VPackMapper().writerFor(PolyBase.class);
        assertArrayEquals(POLY_A, writer.writeValueAsBytes(new ImplA(3)));
        assertArrayEquals(POLY_B, writer.writeValueAsBytes(new ImplB(-5)));
    }

    void writerForNullTypesStillReturnsWriters() {
        ObjectMapper mapper = new VPackMapper();
        assertNotNull(mapper.writerFor((Class<?>) null));
        assertNotNull(mapper.writerFor((JavaType) null));
        assertNotNull(mapper.writerFor((TypeReference<?>) null));
    }

    void miscellaneousWriterSettingsRemainImmutableAndConfigurable() {
        ObjectMapper mapper = new VPackMapper();
        ObjectWriter writer = mapper.writer();
        assertSame(mapper.tokenStreamFactory(), writer.generatorFactory());
        assertFalse(writer.hasPrefetchedSerializer());
        assertNotNull(writer.typeFactory());

        ObjectWriter changed = writer.with(Base64Variants.MODIFIED_FOR_URL);
        assertNotSame(writer, changed);
        assertSame(changed, changed.with(Base64Variants.MODIFIED_FOR_URL));

        changed = changed.withAttributes(Collections.emptyMap())
                .withAttribute("a", "b");
        assertTrue("b".equals(changed.getAttributes().getAttribute("a")));
        changed = changed.withoutAttribute("a");
        assertNull(changed.getAttributes().getAttribute("a"));
    }

    void noPrefetchWriterWritesIndependentLiteralInteger() throws Exception {
        ObjectWriter writer = new VPackMapper().writer()
                .without(SerializationFeature.EAGER_SERIALIZER_FETCH);
        assertArrayEquals(VPackWireFixtureTest.hex("33"),
                writer.writeValueAsBytes(Integer.valueOf(3)));
    }

    void rootSettingsPreserveWriterCopySemantics() {
        ObjectWriter writer = new VPackMapper().writer();
        ObjectWriter changed = writer.withRootName("foo");
        assertNotSame(writer, changed);
        assertSame(changed, changed.withRootName(PropertyName.construct("foo")));
        writer = changed;
        changed = writer.withRootName((String) null);
        assertNotSame(writer, changed);
        assertSame(changed, changed.withRootName((PropertyName) null));

        writer = writer.withRootValueSeparator(new SerializedString(","));
        assertSame(writer, writer.withRootValueSeparator(new SerializedString(",")));
        assertSame(writer, writer.withRootValueSeparator(","));

        changed = writer.withRootValueSeparator("/");
        assertNotSame(writer, changed);
        assertSame(changed, changed.withRootValueSeparator("/"));
        assertNotSame(writer, writer.withRootValueSeparator((String) null));
        assertNotSame(writer, writer.withRootValueSeparator((SerializableString) null));
    }

    void featureSettingsRemainImmutableAndToggleable() {
        ObjectWriter writer = new VPackMapper().writer();
        assertFalse(writer.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES));
        assertFalse(writer.isEnabled(StreamWriteFeature.STRICT_DUPLICATE_DETECTION));
        ObjectWriter changed = writer.with(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS,
                SerializationFeature.INDENT_OUTPUT);
        assertNotSame(writer, changed);
        assertTrue(changed.isEnabled(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS));
        assertTrue(changed.isEnabled(SerializationFeature.INDENT_OUTPUT));
        assertSame(changed, changed.with(SerializationFeature.INDENT_OUTPUT));
        assertSame(changed, changed.withFeatures(SerializationFeature.INDENT_OUTPUT));

        changed = writer.withFeatures(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS,
                SerializationFeature.INDENT_OUTPUT);
        assertNotSame(writer, changed);
        changed = writer.without(SerializationFeature.FAIL_ON_EMPTY_BEANS,
                SerializationFeature.EAGER_SERIALIZER_FETCH);
        assertNotSame(writer, changed);
        assertFalse(changed.isEnabled(SerializationFeature.FAIL_ON_EMPTY_BEANS));
        assertFalse(changed.isEnabled(SerializationFeature.EAGER_SERIALIZER_FETCH));
        assertSame(changed, changed.without(SerializationFeature.FAIL_ON_EMPTY_BEANS));
        assertSame(changed, changed.withoutFeatures(SerializationFeature.FAIL_ON_EMPTY_BEANS));
        assertNotSame(writer, writer.withoutFeatures(SerializationFeature.FAIL_ON_EMPTY_BEANS,
                SerializationFeature.EAGER_SERIALIZER_FETCH));
    }

    void schemaWriterRetainsVpackFormatBoundary() {
        ObjectMapper mapper = new VPackMapper();
        FormatSchema schema = new BogusSchema();
        assertThrows(IllegalArgumentException.class, () -> mapper.writer(schema));
        assertThrows(IllegalArgumentException.class,
                () -> mapper.writerFor(String.class).with(schema));
        assertNotNull(mapper.writer((FormatSchema) null));
    }

    void streamWriteFeaturesRemainConfigurableForVpackWriter() {
        ObjectWriter writer = new VPackMapper().writer();
        assertNotSame(writer, writer.with(JsonWriteFeature.ESCAPE_NON_ASCII));
        assertNotSame(writer, writer.withFeatures(JsonWriteFeature.ESCAPE_NON_ASCII));
        assertSame(writer, writer.without(JsonWriteFeature.ESCAPE_NON_ASCII));
        assertSame(writer, writer.withoutFeatures(JsonWriteFeature.ESCAPE_NON_ASCII));

        assertTrue(writer.isEnabled(StreamWriteFeature.AUTO_CLOSE_TARGET));
        assertNotSame(writer, writer.without(StreamWriteFeature.AUTO_CLOSE_TARGET));
        assertNotSame(writer, writer.withoutFeatures(StreamWriteFeature.AUTO_CLOSE_TARGET));
        assertSame(writer, writer.with(StreamWriteFeature.AUTO_CLOSE_TARGET));
        assertSame(writer, writer.withFeatures(StreamWriteFeature.AUTO_CLOSE_TARGET));
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY,
            property = "type")
    private static class PolyBase { }
@JsonTypeName("A")
    private static class ImplA extends PolyBase {
        public int value;
        ImplA(int value) { this.value = value; }
    }
@JsonTypeName("B")
    private static class ImplB extends PolyBase {
        public int b;
        ImplB(int b) { this.b = b; }
    }
private static final class BogusSchema implements FormatSchema {
        @Override
        public String getSchemaType() { return "test"; }
    }

    void __invoke_prettyPrinterHasNoTextualEffectOnLiteralVpack() throws Exception {
        try {
            prettyPrinterHasNoTextualEffectOnLiteralVpack();
        } finally {
        }
    }


    void __invoke_prefetchStateTracksWriterForType() throws Exception {
        try {
            prefetchStateTracksWriterForType();
        } finally {
        }
    }


    void __invoke_jsonPropertyQuotingFeatureDoesNotChangeLiteralVpack() throws Exception {
        try {
            jsonPropertyQuotingFeatureDoesNotChangeLiteralVpack();
        } finally {
        }
    }


    void __invoke_objectWriterWithNodeWritesIndependentLiteralObject() throws Exception {
        try {
            objectWriterWithNodeWritesIndependentLiteralObject();
        } finally {
        }
    }


    void __invoke_polymorphicTypingWritesOrdinaryVpackObjectProperties() throws Exception {
        try {
            polymorphicTypingWritesOrdinaryVpackObjectProperties();
        } finally {
        }
    }


    void __invoke_writerForNullTypesStillReturnsWriters() throws Exception {
        try {
            writerForNullTypesStillReturnsWriters();
        } finally {
        }
    }


    void __invoke_miscellaneousWriterSettingsRemainImmutableAndConfigurable() throws Exception {
        try {
            miscellaneousWriterSettingsRemainImmutableAndConfigurable();
        } finally {
        }
    }


    void __invoke_noPrefetchWriterWritesIndependentLiteralInteger() throws Exception {
        try {
            noPrefetchWriterWritesIndependentLiteralInteger();
        } finally {
        }
    }


    void __invoke_rootSettingsPreserveWriterCopySemantics() throws Exception {
        try {
            rootSettingsPreserveWriterCopySemantics();
        } finally {
        }
    }


    void __invoke_featureSettingsRemainImmutableAndToggleable() throws Exception {
        try {
            featureSettingsRemainImmutableAndToggleable();
        } finally {
        }
    }


    void __invoke_schemaWriterRetainsVpackFormatBoundary() throws Exception {
        try {
            schemaWriterRetainsVpackFormatBoundary();
        } finally {
        }
    }


    void __invoke_streamWriteFeaturesRemainConfigurableForVpackWriter() throws Exception {
        try {
            streamWriteFeaturesRemainConfigurableForVpackWriter();
        } finally {
        }
    }

}
