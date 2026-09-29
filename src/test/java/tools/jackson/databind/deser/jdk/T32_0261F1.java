package tools.jackson.databind.deser.jdk;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.deser.jdk.JDKFromStringDeserializer.NioPathDeserializer;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.module.SimpleModule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0261F1 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] DATE_LENIENT = VPackWireFixtureTest.hex(
            "4a 32 30 31 35 2d 31 31 2d 33 32");
private static final byte[] SIGNED_TIMESTAMP = VPackWireFixtureTest.hex(
            "4e 2d 31 33 38 33 30 34 33 36 36 39 39 33 35");
private static final byte[] POSITIVE_SIGNED_TIMESTAMP = VPackWireFixtureTest.hex(
            "4e 2b 31 33 38 33 30 34 33 36 36 39 39 33 35");
private static final byte[] PST = VPackWireFixtureTest.hex("43 50 53 54");
private static final byte[] PATH = VPackWireFixtureTest.hex(
            "4c 2f 74 6d 70 2f 66 6f 6f 2e 74 78 74");
private static final byte[] FILE_URI = VPackWireFixtureTest.hex(
            "53 66 69 6c 65 3a 2f 2f 2f 74 6d 70 2f 66 6f 6f 2e 74 78 74");
private static final byte[] FILE_URI_UPPER = VPackWireFixtureTest.hex(
            "53 46 49 4c 45 3a 2f 2f 2f 74 6d 70 2f 66 6f 6f 2e 74 78 74");
private static final byte[] FILE_URI_MIXED = VPackWireFixtureTest.hex(
            "53 46 69 4c 65 3a 2f 2f 2f 74 6d 70 2f 66 6f 6f 2e 74 78 74");
private static final byte[] JAR_URI = VPackWireFixtureTest.hex(
            "64 6a 61 72 3a 68 74 74 70 3a 2f 2f 65 78 61 6d 70 6c 65 2e 63 6f 6d 2f 66 6f 6f 2e 6a 61 72 21 2f 70 61 74 68");
private static final byte[] HTTP_URI = VPackWireFixtureTest.hex(
            "57 68 74 74 70 3a 2f 2f 65 78 61 6d 70 6c 65 2e 63 6f 6d 2f 70 61 74 68");
private static final byte[] S3_URI = VPackWireFixtureTest.hex(
            "4f 73 33 3a 2f 2f 62 75 63 6b 65 74 2f 6b 65 79");
private static final byte[] CUSTOM_URI = VPackWireFixtureTest.hex(
            "52 63 75 73 74 6f 6d 3a 2f 2f 73 6f 6d 65 74 68 69 6e 67");
private static final byte[] JIMFS_URI = VPackWireFixtureTest.hex(
            "56 6a 69 6d 66 73 3a 2f 2f 62 75 63 6b 65 74 2f 66 6f 6f 2e 74 78 74");
private static final byte[] ATOMIC_NULL = VPackWireFixtureTest.hex(
            "14 0b 46 61 74 6f 6d 69 63 18 01");
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] WRAPPER_TRUE = VPackWireFixtureTest.hex(
            "0b 0b 01 45 76 61 6c 75 65 1a 03");

    // Provenance: JDK7TypesTest#testPathRoundTrip.
    void testPathRoundTrip() throws Exception {
        Path input = Path.of("/tmp", "foo.txt");
        byte[] encoded = MAPPER.writeValueAsBytes(input);
        assertNotNull(encoded);

        Path roundTrip = MAPPER.readValue(encoded, Path.class);
        assertNotNull(roundTrip);
        assertEquals(input.toUri(), roundTrip.toUri());
        assertEquals(input.toAbsolutePath(), roundTrip.toAbsolutePath());

        // Independent literal decode exercises the VPack string path as well.
        Path literal = MAPPER.readValue(PATH, Path.class);
        assertEquals(input.toUri(), literal.toUri());
    }

    // Provenance: JDK7TypesTest#testRejectNonFileSchemes.
    void testRejectNonFileSchemes() throws Exception {
        assertNotNull(MAPPER.readValue(PATH, Path.class));
        verifyRejectScheme(JAR_URI);
        verifyRejectScheme(HTTP_URI);
        verifyRejectScheme(S3_URI);
        verifyRejectScheme(CUSTOM_URI);

        DatabindException failure = assertThrows(DatabindException.class,
                () -> MAPPER.readValue(S3_URI, Path.class));
        assertTrue(failure.getMessage().contains("scheme 's3' not allowed"),
                failure.getMessage());
        assertTrue(failure.getMessage().contains("allowed: [\"file\"]"),
                failure.getMessage());
    }

    // Provenance: JDK7TypesTest#testAllowedSchemeCaseInsensitive.
    void testAllowedSchemeCaseInsensitive() throws Exception {
        Path input = Path.of("/tmp", "foo.txt");
        for (byte[] fixture : new byte[][] { FILE_URI_UPPER, FILE_URI_MIXED }) {
            Path result = MAPPER.readValue(fixture, Path.class);
            assertNotNull(result);
            assertEquals(input.toAbsolutePath(), result.toAbsolutePath());
        }
    }

    // Provenance: JDK7TypesTest#testCustomAllowedSchemes.
    void testCustomAllowedSchemes() throws Exception {
        Path input = Path.of("/tmp", "foo.txt");
        ObjectMapper mapper = mapperWithSchemes(Arrays.asList("FILE"));
        Path result = mapper.readValue(FILE_URI, Path.class);
        assertNotNull(result);
        assertEquals(input.toAbsolutePath(), result.toAbsolutePath());

        mapper = mapperWithSchemes(Arrays.asList("jar", "jrt"));
        ObjectMapper restrictedMapper = mapper;
        DatabindException failure = assertThrows(DatabindException.class,
                () -> restrictedMapper.readValue(FILE_URI, Path.class));
        assertTrue(failure.getMessage().contains("scheme 'file' not allowed"),
                failure.getMessage());
        assertTrue(failure.getMessage().contains("allowed: [\"jar\", \"jrt\"]"),
                failure.getMessage());

        assertNotNull(mapper.readValue(PATH, Path.class));
        verifyRejectScheme(mapperWithSchemes(Collections.emptyList()), FILE_URI);
    }

    // Provenance: JDK7TypesTest#testAllowedSchemeWithNoProvider.
    void testAllowedSchemeWithNoProvider() throws Exception {
        ObjectMapper mapper = mapperWithSchemes(Arrays.asList("jimfs"));
        DatabindException failure = assertThrows(DatabindException.class,
                () -> mapper.readValue(JIMFS_URI, Path.class));
        assertTrue(failure.getMessage().contains("Provider \"jimfs\" not installed"),
                failure.getMessage());
    }

    // Provenance: JDK7TypesTest#testNullAllowedSchemes.
    void testNullAllowedSchemes() {
        IllegalArgumentException failure = assertThrows(IllegalArgumentException.class,
                () -> new NioPathDeserializer(null));
        assertTrue(failure.getMessage().contains("`allowedSchemes` must not be null"),
                failure.getMessage());
    }

    // Provenance: JDK7TypesTest#testPolymorphicPath.
    void testPolymorphicPath() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .activateDefaultTyping(BasicPolymorphicTypeValidator.builder()
                        .allowIfBaseType(Object.class).build(), DefaultTyping.NON_FINAL)
                .build();
        Path input = Path.of("/tmp", "foo.txt");

        byte[] encoded = mapper.writeValueAsBytes(new Object[] { input });
        Object[] values = mapper.readValue(encoded, Object[].class);
        assertEquals(1, values.length);
        Object value = values[0];
        assertNotNull(value);
        assertTrue(value instanceof Path,
                "Should deserialize as Path, got: " + value.getClass().getName());
        assertEquals(input.toAbsolutePath().toString(), value.toString());
    }
@SuppressWarnings("unchecked")
    private static ObjectMapper mapperWithSchemes(Collection<String> allowedSchemes) {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Path.class,
                (tools.jackson.databind.ValueDeserializer<Path>) (tools.jackson.databind.ValueDeserializer<?>)
                        new NioPathDeserializer(allowedSchemes));
        return VPackMapper.builder().addModule(module).build();
    }
private static void verifyRejectScheme(byte[] fixture) {
        verifyRejectScheme(MAPPER, fixture);
    }
private static void verifyRejectScheme(ObjectMapper mapper, byte[] fixture) {
        DatabindException failure = assertThrows(DatabindException.class,
                () -> mapper.readValue(fixture, Path.class));
        assertTrue(failure.getMessage().contains("not allowed for Path deserialization"),
                failure.getMessage());
    }
static class AtomicRefBean {
        protected AtomicReference<String> atomic;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public AtomicRefBean(@JsonProperty("atomic") AtomicReference<String> ref) {
            atomic = ref;
        }
    }
static class AtomicRefBeanWithEmpty {
        protected AtomicReference<String> atomic;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public AtomicRefBeanWithEmpty(@JsonProperty("atomic")
                @JsonSetter(nulls = Nulls.AS_EMPTY) AtomicReference<String> ref) {
            atomic = ref;
        }
    }
static class SimpleWrapper {
        public AtomicReference<Object> value;

        public SimpleWrapper(Object value) {
            this.value = new AtomicReference<>(value);
        }
    }

    void __invoke_testPathRoundTrip() throws Exception {
        try {
            testPathRoundTrip();
        } finally {
        }
    }


    void __invoke_testRejectNonFileSchemes() throws Exception {
        try {
            testRejectNonFileSchemes();
        } finally {
        }
    }


    void __invoke_testAllowedSchemeCaseInsensitive() throws Exception {
        try {
            testAllowedSchemeCaseInsensitive();
        } finally {
        }
    }


    void __invoke_testCustomAllowedSchemes() throws Exception {
        try {
            testCustomAllowedSchemes();
        } finally {
        }
    }


    void __invoke_testAllowedSchemeWithNoProvider() throws Exception {
        try {
            testAllowedSchemeWithNoProvider();
        } finally {
        }
    }


    void __invoke_testNullAllowedSchemes() throws Exception {
        try {
            testNullAllowedSchemes();
        } finally {
        }
    }


    void __invoke_testPolymorphicPath() throws Exception {
        try {
            testPolymorphicPath();
        } finally {
        }
    }

}
