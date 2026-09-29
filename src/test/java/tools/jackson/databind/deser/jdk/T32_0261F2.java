package tools.jackson.databind.deser.jdk;

import java.nio.file.Path;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.deser.jdk.JDKFromStringDeserializer.NioPathDeserializer;
import tools.jackson.databind.module.SimpleModule;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0261F2 {
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

    // Provenance: JDKAtomicTypesDeserTest#testAbsentAtomicRefViaCreator.
    void testAbsentAtomicRefViaCreator() throws Exception {
        AtomicRefBean bean = MAPPER.readValue(ATOMIC_NULL, AtomicRefBean.class);
        assertNotNull(bean.atomic);
        assertNull(bean.atomic.get());

        bean = MAPPER.readValue(EMPTY_OBJECT, AtomicRefBean.class);
        assertNotNull(bean.atomic);
        assertNull(bean.atomic.get());

        bean = MAPPER.readerFor(AtomicRefBean.class)
                .with(DeserializationFeature.USE_NULL_FOR_MISSING_REFERENCE_VALUES)
                .readValue(EMPTY_OBJECT);
        assertNull(bean.atomic);

        AtomicRefBeanWithEmpty withEmpty = MAPPER.readValue(EMPTY_OBJECT,
                AtomicRefBeanWithEmpty.class);
        assertNotNull(withEmpty.atomic);
        assertNull(withEmpty.atomic.get());
    }

    // Provenance: JDKAtomicTypesDeserTest#testAbsentExclusion.
    void testAbsentExclusion() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .changeDefaultPropertyInclusion(
                        incl -> incl.withValueInclusion(JsonInclude.Include.NON_ABSENT))
                .build();
        assertArrayEquals(WRAPPER_TRUE,
                mapper.writeValueAsBytes(new SimpleWrapper(Boolean.TRUE)));
        assertArrayEquals(EMPTY_OBJECT,
                mapper.writeValueAsBytes(new SimpleWrapper(null)));
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

    void __invoke_testAbsentAtomicRefViaCreator() throws Exception {
        try {
            testAbsentAtomicRefViaCreator();
        } finally {
        }
    }


    void __invoke_testAbsentExclusion() throws Exception {
        try {
            testAbsentExclusion();
        } finally {
        }
    }

}
