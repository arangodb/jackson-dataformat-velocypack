package tools.jackson.databind;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.fasterxml.jackson.annotation.JsonRootName;
import tools.jackson.databind.DeserializationConfig;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.ObjectWriter;
import tools.jackson.databind.SerializationConfig;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.type.TypeFactory;
import tools.jackson.databind.exc.MismatchedInputException;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0139F1 {
private static final byte[] BEAN = VPackWireFixtureTest.hex(
            "0b 07 01 41 61 33 03");
private static final byte[] WRAPPED_RUDY = VPackWireFixtureTest.hex(
            "0b 10 01 44 72 75 64 79 0b 07 01 41 61 33 03 03");
private static final byte[] WRAPPED_DEFAULT_ROOT = VPackWireFixtureTest.hex(
            "0b 1d 01 51 52 6f 6f 74 42 65 61 6e 57 69 74 68 45 6d 70 74 79 "
            + "0b 07 01 41 61 32 03 03");
private static final byte[] WRAPPED_WRAPPER = VPackWireFixtureTest.hex(
            "0b 13 01 47 77 72 61 70 70 65 72 0b 07 01 41 61 33 03 03");
private static final byte[] WRAPPED_SOMETHING = VPackWireFixtureTest.hex(
            "0b 15 01 49 73 6f 6d 65 74 68 69 6e 67 0b 07 01 41 61 33 03 03");
private static final byte[] WRONG_ROOT = VPackWireFixtureTest.hex(
            "0b 12 01 47 6e 6f 74 52 75 64 79 0b 07 01 41 61 33 03 03");
private static final byte[] ARRAY_ROOT = VPackWireFixtureTest.hex(
            "02 13 00 0b 10 01 44 72 75 64 79 0b 07 01 41 61 33 03 03");
private static final byte[] EXTRA_ROOT = VPackWireFixtureTest.hex(
            "0b 18 02 44 72 75 64 79 0b 07 01 41 61 33 03 45 65 78 74 72 61 33 "
            + "0f 03");
private static final byte[] MY_POJO = VPackWireFixtureTest.hex(
            "0b 0b 02 41 78 32 41 79 33 03 06");
private static final byte[] MY_POJO_READ = VPackWireFixtureTest.hex(
            "0b 0b 02 41 78 31 41 79 32 03 06");
private static final byte[] ANY_BEAN = VPackWireFixtureTest.hex(
            "0b 08 01 41 61 41 62 03");
private static final byte[] ENUM_POJO = VPackWireFixtureTest.hex(
            "0b 12 02 43 61 62 63 41 42 45 73 74 75 66 66 0a 03 09");

    void testConfigs() throws Exception {
        VPackMapper mapper = new VPackMapper();
        DeserializationConfig originalDC = mapper.deserializationConfig();
        SerializationConfig originalSC = mapper.serializationConfig();
        DeserializationConfig dc = deserialize(serialize(originalDC));
        SerializationConfig sc = deserialize(serialize(originalSC));
        assertNotNull(dc);
        assertEquals(originalDC.getDeserializationFeatures(),
                dc.getDeserializationFeatures());
        assertNotNull(sc);
        assertEquals(originalSC.getSerializationFeatures(),
                sc.getSerializationFeatures());
    }

    void testEnumHandlers() throws Exception {
        VPackMapper mapper = new VPackMapper();
        byte[] encoded = mapper.writerFor(EnumPOJO.class)
                .writeValueAsBytes(new EnumPOJO());
        assertArrayEquals(ENUM_POJO, encoded);
        assertNotNull(mapper.readerFor(EnumPOJO.class).readValue(encoded));

        VPackMapper restored = deserialize(serialize(mapper));
        assertNotNull(restored);
        assertArrayEquals(ENUM_POJO,
                restored.writerFor(EnumPOJO.class)
                        .writeValueAsBytes(new EnumPOJO()));
        assertNotNull(restored.readValue(ENUM_POJO, EnumPOJO.class));
    }

    void testMapperWithModule() throws Exception {
        SimpleModule module = new SimpleModule("JDKSerTestModule");
        VPackMapper mapper = VPackMapper.builder()
                .enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
                .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                .addModule(module)
                .build();
        assertArrayEquals(MY_POJO, mapper.writeValueAsBytes(new MyPojo(2, 3)));

        VPackMapper restored = deserialize(serialize(mapper));
        assertTrue(mapper.isEnabled(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS));
        assertTrue(mapper.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY));
        assertArrayEquals(MY_POJO,
                restored.writeValueAsBytes(new MyPojo(2, 3)));
        MyPojo result = restored.readValue(MY_POJO_READ, MyPojo.class);
        assertEquals(1, result.x);
        assertEquals(2, result.y);

        VPackMapper rebuilt = restored.rebuild()
                .disable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
                .disable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                .build();
        assertFalse(rebuilt.isEnabled(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS));
        assertFalse(rebuilt.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY));
        VPackMapper restoredRebuilt = deserialize(serialize(rebuilt));
        assertFalse(restoredRebuilt.isEnabled(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS));
        assertFalse(restoredRebuilt.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY));
    }

    void testObjectReader() throws Exception {
        VPackMapper restored = deserialize(serialize(new VPackMapper()));
        MyPojo pojo = restored.readerFor(MyPojo.class).readValue(MY_POJO_READ);
        assertEquals(1, pojo.x);
        assertEquals(2, pojo.y);
        AnyBean any = restored.readerFor(AnyBean.class).readValue(MY_POJO_READ);
        assertEquals(Integer.valueOf(2), any.properties().get("y"));
    }

    void testObjectWriter() throws Exception {
        VPackMapper restored = deserialize(serialize(new VPackMapper()));
        ObjectWriter writer = restored.writer();
        assertArrayEquals(MY_POJO, writer.writeValueAsBytes(new MyPojo(2, 3)));
        assertArrayEquals(ANY_BEAN,
                writer.writeValueAsBytes(new AnyBean().addEntry("a", "b")));
    }

    void testTypeFactory() throws Exception {
        TypeFactory original = TypeFactory.createDefaultInstance();
        assertNotNull(original.constructType(JavaType.class));
        TypeFactory restored = deserialize(serialize(original));
        assertNotNull(restored);
        assertEquals(JavaType.class,
                restored.constructType(JavaType.class).getRawClass());
    }
private static ObjectMapper rootMapper() {
        return VPackMapper.builder()
                .enable(SerializationFeature.WRAP_ROOT_VALUE)
                .enable(DeserializationFeature.UNWRAP_ROOT_VALUE)
                .build();
    }
private static void assertRootFailures(ObjectReader reader) {
        assertThrows(MismatchedInputException.class,
                () -> reader.readValue(WRONG_ROOT));
        assertThrows(MismatchedInputException.class,
                () -> reader.readValue(ARRAY_ROOT));
        assertThrows(MismatchedInputException.class,
                () -> reader.readValue(new byte[] { 0x0a }));
        assertThrows(MismatchedInputException.class,
                () -> reader.readValue(EXTRA_ROOT));
    }
@SuppressWarnings("unchecked")
    private static <T> T deserialize(byte[] bytes)
            throws IOException, ClassNotFoundException {
        try (ObjectInputStream input = new ObjectInputStream(
                new ByteArrayInputStream(bytes))) {
            return (T) input.readObject();
        }
    }
private static byte[] serialize(Object value) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream output = new ObjectOutputStream(bytes)) {
            output.writeObject(value);
        }
        return bytes.toByteArray();
    }
@JsonRootName("rudy")
    static class Bean {
        public int a = 3;
    }
@JsonRootName("")
    static class RootBeanWithEmpty {
        public int a = 2;
    }
static class MyPojo {
        public int x;
        protected int y;

        public MyPojo() { }
        protected MyPojo(int x0, int y0) {
            x = x0;
            y = y0;
        }
        public int getY() { return y; }
        public void setY(int y) { this.y = y; }
    }
static class EnumPOJO {
        public ABC abc = ABC.B;
        public java.util.Map<String, ABC> stuff = new java.util.LinkedHashMap<>();
    }
enum ABC { A, B }
static class AnyBean {
        private final java.util.Map<String, Object> map = new java.util.LinkedHashMap<>();

        @com.fasterxml.jackson.annotation.JsonAnySetter
        AnyBean addEntry(String key, Object value) {
            map.put(key, value);
            return this;
        }

        @com.fasterxml.jackson.annotation.JsonAnyGetter
        public java.util.Map<String, Object> properties() {
            return map;
        }
    }

    void __invoke_testConfigs() throws Exception {
        try {
            testConfigs();
        } finally {
        }
    }


    void __invoke_testEnumHandlers() throws Exception {
        try {
            testEnumHandlers();
        } finally {
        }
    }


    void __invoke_testMapperWithModule() throws Exception {
        try {
            testMapperWithModule();
        } finally {
        }
    }


    void __invoke_testObjectReader() throws Exception {
        try {
            testObjectReader();
        } finally {
        }
    }


    void __invoke_testObjectWriter() throws Exception {
        try {
            testObjectWriter();
        } finally {
        }
    }


    void __invoke_testTypeFactory() throws Exception {
        try {
            testTypeFactory();
        } finally {
        }
    }

}
