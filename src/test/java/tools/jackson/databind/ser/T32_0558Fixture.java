package tools.jackson.databind.ser;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.SerializationConfig;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.exc.InvalidDefinitionException;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import tools.jackson.dataformat.velocypack.*;

class T32_0558Fixture {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] NONZERO = VPackWireFixtureTest.hex(
            "0b 0c 01 45 76 61 6c 75 65 28 7b 03");
private static final byte[] ALL_GETTERS = VPackWireFixtureTest.hex(
            "0b 13 04 41 61 33 41 62 34 41 63 35 41 64 36 03 06 09 0c");
private static final byte[] MUTATOR_GETTERS = VPackWireFixtureTest.hex(
            "0b 0f 03 41 61 33 41 63 35 41 64 36 03 06 09");
private static final byte[] ANNOTATED_GETTER = VPackWireFixtureTest.hex(
            "0b 08 01 41 61 28 7b 03");
private static final byte[] INDENTABLE = VPackWireFixtureTest.hex(
            "0b 07 01 41 61 33 03");
private static final byte[] INDENTED_MAP = VPackWireFixtureTest.hex(
            "0b 07 01 41 61 32 03");

    // Provenance: SerializationFeaturesTest#testFlushingAutomatic().
    void testFlushingAutomaticVpack() throws Exception {
        assertTrue(MAPPER.serializationConfig()
                .isEnabled(SerializationFeature.FLUSH_AFTER_WRITE_VALUE));

        TrackingOutputStream output = new TrackingOutputStream();
        MAPPER.writeValue(output, Integer.valueOf(13));
        assertArrayEquals(VPackWireFixtureTest.hex("28 0d"), output.toByteArray());
        assertTrue(output.flushes > 0);

        output = new TrackingOutputStream();
        MAPPER.writer().writeValue(output, Integer.valueOf(99));
        assertArrayEquals(VPackWireFixtureTest.hex("28 63"), output.toByteArray());
        assertTrue(output.flushes > 0);
    }

    // Provenance: SerializationFeaturesTest#testFlushingNotAutomatic().
    void testFlushingNotAutomaticVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .configure(SerializationFeature.FLUSH_AFTER_WRITE_VALUE, false)
                .build();

        TrackingOutputStream output = new TrackingOutputStream();
        JsonGenerator generator = mapper.createGenerator(output);
        mapper.writeValue(generator, Integer.valueOf(13));
        assertArrayEquals(VPackWireFixtureTest.hex("28 0d"), output.toByteArray());
        assertEquals(0, output.flushes);
        generator.flush();
        assertArrayEquals(VPackWireFixtureTest.hex("28 0d"), output.toByteArray());
        assertTrue(output.flushes > 0);
        generator.close();

        output = new TrackingOutputStream();
        generator = mapper.createGenerator(output);
        mapper.writer().writeValue(generator, Integer.valueOf(99));
        assertArrayEquals(VPackWireFixtureTest.hex("28 63"), output.toByteArray());
        assertEquals(0, output.flushes);
        generator.flush();
        assertArrayEquals(VPackWireFixtureTest.hex("28 63"), output.toByteArray());
        assertTrue(output.flushes > 0);
        generator.close();
    }

    // Provenance: SerializationFeaturesTest#testEmptyWithAnnotations().
    void testEmptyWithAnnotationsVpack() throws Exception {
        try {
            MAPPER.writer().with(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                    .writeValueAsBytes(new Empty());
            fail("FAIL_ON_EMPTY_BEANS must reject an unannotated empty bean");
        } catch (InvalidDefinitionException e) {
            assertTrue(e.getMessage().contains("No serializer found for class"));
        }

        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                MAPPER.writeValueAsBytes(new EmptyWithAnno()));

        ObjectMapper mixinMapper = VPackMapper.builder()
                .addMixIn(Empty.class, EmptyWithAnno.class)
                .build();
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                mixinMapper.writeValueAsBytes(new Empty()));
    }

    // Provenance: SerializationFeaturesTest#testEmptyWithFeature().
    void testEmptyWithFeatureVpack() throws Exception {
        assertFalse(MAPPER.isEnabled(SerializationFeature.FAIL_ON_EMPTY_BEANS));
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                MAPPER.writer().writeValueAsBytes(new Empty()));
    }

    // Provenance: SerializationFeaturesTest#testCustomNoEmpty().
    void testCustomNoEmptyVpack() throws Exception {
        assertArrayEquals(NONZERO,
                MAPPER.writeValueAsBytes(new NonZeroWrapper(123)));
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                MAPPER.writeValueAsBytes(new NonZeroWrapper(0)));
    }

    // Provenance: SerializationFeaturesTest#testGettersWithoutSetters().
    void testGettersWithoutSettersVpack() throws Exception {
        assertFalse(MAPPER.isEnabled(MapperFeature.REQUIRE_SETTERS_FOR_GETTERS));
        GettersWithoutSetters bean = new GettersWithoutSetters(123);
        assertArrayEquals(ALL_GETTERS, MAPPER.writeValueAsBytes(bean));

        ObjectMapper mapper = VPackMapper.builder()
                .enable(MapperFeature.REQUIRE_SETTERS_FOR_GETTERS)
                .build();
        assertArrayEquals(MUTATOR_GETTERS, mapper.writeValueAsBytes(bean));
    }

    // Provenance: SerializationFeaturesTest#testGettersWithoutSettersOverride().
    void testGettersWithoutSettersOverrideVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(MapperFeature.REQUIRE_SETTERS_FOR_GETTERS)
                .build();
        assertArrayEquals(ANNOTATED_GETTER,
                mapper.writeValueAsBytes(new GettersWithoutSetters2()));
    }

    // Provenance: SerializationFeaturesTest#testEnumIndexes().
    void testEnumIndexesVpack() {
        int max = 0;
        for (SerializationFeature feature : SerializationFeature.values()) {
            max = Math.max(max, feature.ordinal());
        }
        assertTrue(max < 31, "SerializationFeature masks must fit in one int: " + max);
    }

    // Provenance: SerializationFeaturesTest#testDefaults().
    void testDefaultsVpack() {
        SerializationConfig config = MAPPER.serializationConfig();
        assertTrue(config.isEnabled(MapperFeature.USE_ANNOTATIONS));
        assertTrue(config.isEnabled(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS));
        assertFalse(config.isEnabled(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS));
        assertEquals(MapperFeature.DEFAULT_VIEW_INCLUSION.enabledByDefault(),
                config.isEnabled(MapperFeature.DEFAULT_VIEW_INCLUSION));
        assertFalse(config.isEnabled(MapperFeature.USE_STATIC_TYPING));
        assertEquals(SerializationFeature.FAIL_ON_EMPTY_BEANS.enabledByDefault(),
                config.isEnabled(SerializationFeature.FAIL_ON_EMPTY_BEANS));
        assertFalse(config.isEnabled(SerializationFeature.INDENT_OUTPUT));
    }

    // Provenance: SerializationFeaturesTest#testIndentation().
    void testIndentationVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .build();
        assertTrue(mapper.isEnabled(SerializationFeature.INDENT_OUTPUT));
        assertArrayEquals(INDENTED_MAP,
                mapper.writeValueAsBytes(java.util.Map.of("a", 2)));
        try (JsonGenerator generator = mapper.createGenerator(new ByteArrayOutputStream())) {
            assertNull(generator.getPrettyPrinter());
        }
    }

    // Provenance: SerializationFeaturesTest#testIndentWithPassedGenerator().
    void testIndentWithPassedGeneratorVpack() throws Exception {
        assertArrayEquals(INDENTABLE, MAPPER.writeValueAsBytes(new Indentable()));

        ObjectMapper mapper = VPackMapper.builder()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .build();
        assertArrayEquals(INDENTABLE, mapper.writeValueAsBytes(new Indentable()));

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = mapper.createGenerator(output)) {
            assertNull(generator.getPrettyPrinter());
            mapper.writeValue(generator, new Indentable());
        }
        assertArrayEquals(INDENTABLE, output.toByteArray());
    }

    // Provenance: SerializationFeaturesTest#testDateFormatConfig().
    void testDateFormatConfigVpack() {
        TimeZone tz1 = TimeZone.getTimeZone("America/Los_Angeles");
        TimeZone tz2 = TimeZone.getTimeZone("US/Central");
        assertEquals(tz1, tz1);
        assertEquals(tz2, tz2);
        assertFalse(tz1.equals(tz2));

        ObjectMapper mapper = VPackMapper.builder()
                .defaultTimeZone(tz1)
                .build();
        assertEquals(tz1, mapper.serializationConfig().getTimeZone());
        assertEquals(tz1, mapper.deserializationConfig().getTimeZone());
        assertEquals(tz1, mapper.writer().getConfig().getTimeZone());
        assertEquals(tz1, mapper.reader().getConfig().getTimeZone());

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(tz2);
        mapper = VPackMapper.builder()
                .defaultTimeZone(tz1)
                .defaultDateFormat(format)
                .build();
        assertEquals(tz1, mapper.serializationConfig().getTimeZone());
        assertEquals(tz1, mapper.deserializationConfig().getTimeZone());
        assertEquals(tz1, mapper.writer().getConfig().getTimeZone());
        assertEquals(tz1, mapper.reader().getConfig().getTimeZone());
    }
static class Empty { }
@JsonSerialize
    static class EmptyWithAnno { }
@JsonSerialize(using = NonZeroSerializer.class)
    static class NonZero {
        public int nr;
        NonZero(int value) { nr = value; }
    }
@JsonInclude(JsonInclude.Include.NON_EMPTY)
    static class NonZeroWrapper {
        public NonZero value;
        NonZeroWrapper(int value) { this.value = new NonZero(value); }
    }
static class NonZeroSerializer extends ValueSerializer<NonZero> {
        @Override
        public void serialize(NonZero value, JsonGenerator generator,
                SerializationContext context) {
            generator.writeNumber(value.nr);
        }

        @Override
        public boolean isEmpty(SerializationContext context, NonZero value) {
            return value == null || value.nr == 0;
        }
    }
@JsonPropertyOrder(alphabetic = true)
    static class GettersWithoutSetters {
        public int d = 0;
        GettersWithoutSetters(@JsonProperty("a") int ignored) { }
        public int getA() { return 3; }
        public int getB() { return 4; }
        public int getC() { return 5; }
        public void setC(int value) { }
        public int getD() { return 6; }
    }
static class GettersWithoutSetters2 {
        @JsonProperty
        public int getA() { return 123; }
    }
static class Indentable {
        public int a = 3;
    }
static final class TrackingOutputStream extends ByteArrayOutputStream {
        int flushes;

        @Override
        public void flush() {
            flushes++;
        }
    }

    void __invoke_testFlushingAutomaticVpack() throws Exception {
        try {
            testFlushingAutomaticVpack();
        } finally {
        }
    }


    void __invoke_testFlushingNotAutomaticVpack() throws Exception {
        try {
            testFlushingNotAutomaticVpack();
        } finally {
        }
    }


    void __invoke_testEmptyWithAnnotationsVpack() throws Exception {
        try {
            testEmptyWithAnnotationsVpack();
        } finally {
        }
    }


    void __invoke_testEmptyWithFeatureVpack() throws Exception {
        try {
            testEmptyWithFeatureVpack();
        } finally {
        }
    }


    void __invoke_testCustomNoEmptyVpack() throws Exception {
        try {
            testCustomNoEmptyVpack();
        } finally {
        }
    }


    void __invoke_testGettersWithoutSettersVpack() throws Exception {
        try {
            testGettersWithoutSettersVpack();
        } finally {
        }
    }


    void __invoke_testGettersWithoutSettersOverrideVpack() throws Exception {
        try {
            testGettersWithoutSettersOverrideVpack();
        } finally {
        }
    }


    void __invoke_testEnumIndexesVpack() throws Exception {
        try {
            testEnumIndexesVpack();
        } finally {
        }
    }


    void __invoke_testDefaultsVpack() throws Exception {
        try {
            testDefaultsVpack();
        } finally {
        }
    }


    void __invoke_testIndentationVpack() throws Exception {
        try {
            testIndentationVpack();
        } finally {
        }
    }


    void __invoke_testIndentWithPassedGeneratorVpack() throws Exception {
        try {
            testIndentWithPassedGeneratorVpack();
        } finally {
        }
    }


    void __invoke_testDateFormatConfigVpack() throws Exception {
        try {
            testDateFormatConfigVpack();
        } finally {
        }
    }

}
