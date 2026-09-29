package tools.jackson.databind;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.core.FormatSchema;
import tools.jackson.core.JsonParser;
import tools.jackson.core.StreamReadFeature;
import tools.jackson.core.type.ResolvedType;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.DeserializationProblemHandler;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.util.TokenBuffer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0134F0 {
private static final byte[] ONE_ELEMENT_ARRAY = VPackWireFixtureTest.hex("02 03 31");
private static final byte[] EMPTY_ARRAY = VPackWireFixtureTest.hex("01");
private static final byte[] ENUM_ARRAY_AC = VPackWireFixtureTest.hex(
            "02 06 41 41 41 43");
private static final byte[] ENUM_ARRAY_BC = VPackWireFixtureTest.hex(
            "02 06 41 42 41 43");
private static final byte[] ENUM_MAP = VPackWireFixtureTest.hex(
            "0b 0a 01 43 6b 65 79 41 42 03");
private static final byte[] TREE_ARRAY = VPackWireFixtureTest.hex(
            "02 06 43 78 79 7a");
private static final byte[] UNKNOWN_FIELD_OBJECT = VPackWireFixtureTest.hex(
            "14 21 4c 75 6e 6b 6e 6f 77 6e 46 69 65 6c 64 31 "
            + "4a 6b 6e 6f 77 6e 46 69 65 6c 64 44 74 65 73 74 02");

    void simpleReadViaParserUsesLiteralVpack() throws Exception {
        ObjectMapper mapper = new VPackMapper();
        try (JsonParser parser = mapper.readerFor(Object.class)
                .createParser(ONE_ELEMENT_ARRAY)) {
            Object value = mapper.readerFor(Object.class).readValue(parser);
            assertInstanceOf(List.class, value);
            assertEquals(Collections.singletonList(1), value);
        }
    }

    void simpleAlternativeSourcesUseLiteralVpackAndRejectTextSources() throws Exception {
        ObjectReader reader = new VPackMapper().readerFor(Object.class);
        Object expected = Collections.singletonList(1);

        assertEquals(expected, reader.readValue(ONE_ELEMENT_ARRAY));
        assertEquals(expected, reader.readValue(ONE_ELEMENT_ARRAY, 0,
                ONE_ELEMENT_ARRAY.length));
        assertEquals(expected, reader.readValue(new ByteArrayInputStream(ONE_ELEMENT_ARRAY)));

        assertThrows(UnsupportedOperationException.class,
                () -> reader.readValue("[1]"));
        assertThrows(UnsupportedOperationException.class,
                () -> reader.readValue(new StringReader("[1]")));
        assertThrows(MismatchedInputException.class,
                () -> reader.readValue(new byte[0]));
    }

    void readerForArrayOfReadsLiteralEnumArray() throws Exception {
        Object value = new VPackMapper().readerForArrayOf(ABC.class)
                .readValue(ENUM_ARRAY_AC);
        assertEquals(ABC[].class, value.getClass());
        ABC[] values = (ABC[]) value;
        assertEquals(2, values.length);
        assertEquals(ABC.A, values[0]);
        assertEquals(ABC.C, values[1]);
    }

    void readerForListOfReadsLiteralEnumArray() throws Exception {
        Object value = new VPackMapper().readerForListOf(ABC.class)
                .readValue(ENUM_ARRAY_BC);
        assertEquals(ArrayList.class, value.getClass());
        assertEquals(Arrays.asList(ABC.B, ABC.C), value);
    }

    void readerForMapOfReadsLiteralEnumObject() throws Exception {
        Object value = new VPackMapper().readerForMapOf(ABC.class)
                .readValue(ENUM_MAP);
        assertEquals(LinkedHashMap.class, value.getClass());
        assertEquals(Collections.singletonMap("key", ABC.B), value);
    }

    void readValuesVariantsAcceptLiteralParserAndTokenBuffer() throws Exception {
        ObjectReader reader = new VPackMapper().reader();
        try (JsonParser parser = reader.createParser(EMPTY_ARRAY)) {
            assertNotNull(reader.readValues(parser, List.class));
        }
        try (JsonParser parser = reader.createParser(EMPTY_ARRAY)) {
            assertNotNull(reader.readValues(parser, reader.constructType(List.class)));
        }
        try (JsonParser parser = reader.createParser(EMPTY_ARRAY)) {
            ResolvedType type = reader.constructType(List.class);
            assertNotNull(reader.readValues(parser, type));
        }
        try (JsonParser parser = reader.createParser(EMPTY_ARRAY)) {
            assertNotNull(reader.readValues(parser,
                    new TypeReference<List<String>>() { }));
        }
        try (TokenBuffer buffer = TokenBuffer.forGeneration()) {
            buffer.writeStartArray();
            buffer.writeEndArray();
            assertNotNull(reader.forType(List.class).readValues(buffer));
        }
    }

    void streamReadFeaturesRemainConfigurableOnVpackReader() {
        ObjectReader reader = new VPackMapper().reader();
        assertFalse(reader.isEnabled(StreamReadFeature.IGNORE_UNDEFINED));
        ObjectReader enabled = reader.with(StreamReadFeature.IGNORE_UNDEFINED);
        assertTrue(enabled.isEnabled(StreamReadFeature.IGNORE_UNDEFINED));
        ObjectReader reset = enabled.without(StreamReadFeature.IGNORE_UNDEFINED);
        assertFalse(reset.isEnabled(StreamReadFeature.IGNORE_UNDEFINED));

        reader = reader.withFeatures(StreamReadFeature.AUTO_CLOSE_SOURCE,
                StreamReadFeature.CLEAR_CURRENT_TOKEN_ON_CLOSE);
        assertTrue(reader.isEnabled(StreamReadFeature.AUTO_CLOSE_SOURCE));
        assertTrue(reader.isEnabled(StreamReadFeature.CLEAR_CURRENT_TOKEN_ON_CLOSE));
        reader = reader.withoutFeatures(StreamReadFeature.AUTO_CLOSE_SOURCE,
                StreamReadFeature.CLEAR_CURRENT_TOKEN_ON_CLOSE);
        assertFalse(reader.isEnabled(StreamReadFeature.AUTO_CLOSE_SOURCE));
        assertFalse(reader.isEnabled(StreamReadFeature.CLEAR_CURRENT_TOKEN_ON_CLOSE));
    }

    void treeToValueConvertsLiteralVpackTree() throws Exception {
        ObjectMapper mapper = new VPackMapper();
        JsonNode node = mapper.readTree(TREE_ARRAY);
        ObjectReader reader = mapper.readerFor(String.class);
        List<?> list = reader.treeToValue(node, List.class);
        assertEquals(1, list.size());

        String[] values = reader.treeToValue(node, mapper.constructType(String[].class));
        assertEquals(1, values.length);
        assertEquals("xyz", values[0]);
    }

    void schemaSupportHasTheVpackFormatBoundary() {
        ObjectReader reader = new VPackMapper().readerFor(String.class);
        assertNotNull(reader.with((FormatSchema) null));
        assertThrows(IllegalArgumentException.class,
                () -> reader.with(new BogusSchema()));
        assertThrows(IllegalArgumentException.class,
                () -> new VPackMapper().reader(new BogusSchema()));
    }

    void unknownPropertyHandlerConsumesLiteralVpackTree() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addHandler(new DeserializationProblemHandler() {
                    @Override
                    public boolean handleUnknownProperty(DeserializationContext context,
                            JsonParser parser, ValueDeserializer<?> deserializer,
                            Object beanOrClass, String propertyName) {
                        context.readTree(parser);
                        return true;
                    }
                }).build();
        A2297 value = mapper.readValue(UNKNOWN_FIELD_OBJECT, A2297.class);
        assertEquals("test", value.knownField);
    }
private enum ABC { A, B, C }
private static final class BogusSchema implements FormatSchema {
        @Override
        public String getSchemaType() {
            return "test";
        }
    }
private static final class A2297 {
        final String knownField;

        @JsonCreator
        private A2297(@JsonProperty("knownField") String knownField) {
            this.knownField = knownField;
        }
    }

    void __invoke_simpleReadViaParserUsesLiteralVpack() throws Exception {
        try {
            simpleReadViaParserUsesLiteralVpack();
        } finally {
        }
    }


    void __invoke_simpleAlternativeSourcesUseLiteralVpackAndRejectTextSources() throws Exception {
        try {
            simpleAlternativeSourcesUseLiteralVpackAndRejectTextSources();
        } finally {
        }
    }


    void __invoke_readerForArrayOfReadsLiteralEnumArray() throws Exception {
        try {
            readerForArrayOfReadsLiteralEnumArray();
        } finally {
        }
    }


    void __invoke_readerForListOfReadsLiteralEnumArray() throws Exception {
        try {
            readerForListOfReadsLiteralEnumArray();
        } finally {
        }
    }


    void __invoke_readerForMapOfReadsLiteralEnumObject() throws Exception {
        try {
            readerForMapOfReadsLiteralEnumObject();
        } finally {
        }
    }


    void __invoke_readValuesVariantsAcceptLiteralParserAndTokenBuffer() throws Exception {
        try {
            readValuesVariantsAcceptLiteralParserAndTokenBuffer();
        } finally {
        }
    }


    void __invoke_streamReadFeaturesRemainConfigurableOnVpackReader() throws Exception {
        try {
            streamReadFeaturesRemainConfigurableOnVpackReader();
        } finally {
        }
    }


    void __invoke_treeToValueConvertsLiteralVpackTree() throws Exception {
        try {
            treeToValueConvertsLiteralVpackTree();
        } finally {
        }
    }


    void __invoke_schemaSupportHasTheVpackFormatBoundary() throws Exception {
        try {
            schemaSupportHasTheVpackFormatBoundary();
        } finally {
        }
    }


    void __invoke_unknownPropertyHandlerConsumesLiteralVpackTree() throws Exception {
        try {
            unknownPropertyHandlerConsumesLiteralVpackTree();
        } finally {
        }
    }

}
