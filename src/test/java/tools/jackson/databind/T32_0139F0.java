package tools.jackson.databind;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.fasterxml.jackson.annotation.JsonRootName;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.ObjectWriter;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.exc.MismatchedInputException;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0139F0 {
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

    void testRootViaMapper() throws Exception {
        ObjectMapper mapper = rootMapper();
        assertArrayEquals(WRAPPED_RUDY, mapper.writeValueAsBytes(new Bean()));
        assertNotNull(mapper.readValue(WRAPPED_RUDY, Bean.class));

        assertArrayEquals(WRAPPED_DEFAULT_ROOT,
                mapper.writeValueAsBytes(new RootBeanWithEmpty()));
        RootBeanWithEmpty result = mapper.readValue(WRAPPED_DEFAULT_ROOT,
                RootBeanWithEmpty.class);
        assertNotNull(result);
        assertEquals(2, result.a);
    }

    void testRootViaMapperFails() {
        ObjectMapper mapper = rootMapper();
        assertRootFailures(mapper.readerFor(Bean.class));
    }

    void testRootViaReaderFails() {
        assertRootFailures(rootMapper().readerFor(Bean.class));
    }

    void testRootViaWriterAndReader() throws Exception {
        ObjectMapper mapper = rootMapper();
        ObjectWriter writer = mapper.writer();
        assertArrayEquals(WRAPPED_RUDY, writer.writeValueAsBytes(new Bean()));
        assertNotNull(mapper.readerFor(Bean.class).readValue(WRAPPED_RUDY));
    }

    void testReconfiguringOfWrapping() throws Exception {
        ObjectMapper mapper = new VPackMapper();
        assertArrayEquals(BEAN, mapper.writeValueAsBytes(new Bean()));
        assertArrayEquals(WRAPPED_RUDY,
                mapper.writer(SerializationFeature.WRAP_ROOT_VALUE)
                        .writeValueAsBytes(new Bean()));

        assertNotNull(mapper.readValue(BEAN, Bean.class));
        assertThrows(MismatchedInputException.class,
                () -> mapper.readerFor(Bean.class)
                        .with(DeserializationFeature.UNWRAP_ROOT_VALUE)
                        .readValue(BEAN));
        assertNotNull(mapper.readerFor(Bean.class)
                .with(DeserializationFeature.UNWRAP_ROOT_VALUE)
                .readValue(WRAPPED_RUDY));
    }

    void testRootUsingExplicitConfig() throws Exception {
        ObjectMapper mapper = new VPackMapper();
        ObjectWriter writer = mapper.writer().withRootName("wrapper");
        assertArrayEquals(WRAPPED_WRAPPER, writer.writeValueAsBytes(new Bean()));
        assertNotNull(mapper.readerFor(Bean.class).withRootName("wrapper")
                .readValue(WRAPPED_WRAPPER));

        ObjectMapper wrapping = rootMapper();
        assertArrayEquals(WRAPPED_SOMETHING,
                wrapping.writer().withRootName("something")
                        .writeValueAsBytes(new Bean()));
        assertArrayEquals(BEAN,
                wrapping.writer().withRootName("")
                        .writeValueAsBytes(new Bean()));
        assertArrayEquals(BEAN,
                wrapping.writer().withoutRootName()
                        .writeValueAsBytes(new Bean()));

        Bean bean = wrapping.readerFor(Bean.class).withRootName("")
                .readValue(BEAN);
        assertEquals(3, bean.a);
        bean = wrapping.readerFor(Bean.class).withoutRootName().readValue(BEAN);
        assertEquals(3, bean.a);
        bean = wrapping.readerFor(Bean.class).readValue(WRAPPED_RUDY);
        assertEquals(3, bean.a);
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

    void __invoke_testRootViaMapper() throws Exception {
        try {
            testRootViaMapper();
        } finally {
        }
    }


    void __invoke_testRootViaMapperFails() throws Exception {
        try {
            testRootViaMapperFails();
        } finally {
        }
    }


    void __invoke_testRootViaReaderFails() throws Exception {
        try {
            testRootViaReaderFails();
        } finally {
        }
    }


    void __invoke_testRootViaWriterAndReader() throws Exception {
        try {
            testRootViaWriterAndReader();
        } finally {
        }
    }


    void __invoke_testReconfiguringOfWrapping() throws Exception {
        try {
            testReconfiguringOfWrapping();
        } finally {
        }
    }


    void __invoke_testRootUsingExplicitConfig() throws Exception {
        try {
            testRootUsingExplicitConfig();
        } finally {
        }
    }

}
