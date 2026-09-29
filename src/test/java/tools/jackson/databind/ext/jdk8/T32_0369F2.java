package tools.jackson.databind.ext.jdk8;

import java.util.stream.IntStream;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JacksonStdImpl;
import tools.jackson.databind.ext.jdk8.DoubleStreamSerializer;
import tools.jackson.databind.ext.jdk8.IntStreamSerializer;
import tools.jackson.databind.ext.jdk8.Jdk8OptionalDeserializer;
import tools.jackson.databind.ext.jdk8.Jdk8OptionalSerializer;
import tools.jackson.databind.ext.jdk8.Jdk8StreamSerializer;
import tools.jackson.databind.ext.jdk8.LongStreamSerializer;
import tools.jackson.databind.ext.jdk8.OptionalDoubleDeserializer;
import tools.jackson.databind.ext.jdk8.OptionalDoubleSerializer;
import tools.jackson.databind.ext.jdk8.OptionalIntDeserializer;
import tools.jackson.databind.ext.jdk8.OptionalIntSerializer;
import tools.jackson.databind.ext.jdk8.OptionalLongDeserializer;
import tools.jackson.databind.ext.jdk8.OptionalLongSerializer;
import tools.jackson.databind.util.ClassUtil;

import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0369F2 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] EMPTY_STREAM = VPackWireFixtureTest.hex("01");
private static final byte[] SINGLE_ELEMENT = VPackWireFixtureTest.hex(
            "02 03 31");
private static final byte[] MULTI_ELEMENTS = VPackWireFixtureTest.hex(
            "06 17 06 "
            + "23 00 00 00 80 "
            + "2b ff ff ff 7f "
            + "31 30 36 3d "
            + "03 08 0d 0e 0f 10");
private static final byte[] WRAPPED_STREAM = VPackWireFixtureTest.hex(
            "0b 12 01 45 76 61 6c 75 65 "
            + "02 08 28 0a 28 14 28 1e 03");
private static final byte[] SINGLE_NEGATIVE = VPackWireFixtureTest.hex(
            "02 03 3f");
private static final byte[] BOUNDARY_VALUES = VPackWireFixtureTest.hex(
            "06 11 03 "
            + "23 00 00 00 80 30 2b ff ff ff 7f "
            + "03 08 09");

    // Provenance: Jdk8HandlersStdImplTest#jdk8HandlersMarkedAsStdImpl().
    void jdk8HandlersMarkedAsStdImplVpack() {
        Class<?>[] handlers = {
                Jdk8OptionalDeserializer.class,
                OptionalIntDeserializer.class,
                OptionalLongDeserializer.class,
                OptionalDoubleDeserializer.class,
                Jdk8OptionalSerializer.class,
                OptionalIntSerializer.class,
                OptionalLongSerializer.class,
                OptionalDoubleSerializer.class,
                Jdk8StreamSerializer.class,
                IntStreamSerializer.class,
                LongStreamSerializer.class,
                DoubleStreamSerializer.class
        };
        for (Class<?> handler : handlers) {
            assertTrue(ClassUtil.isJacksonStdImpl(handler),
                    "Expected @" + JacksonStdImpl.class.getSimpleName()
                            + " on " + handler.getName());
        }
    }
static class IntStreamWrapper {
        public IntStream value;

        public IntStreamWrapper() { }

        IntStreamWrapper(IntStream value) {
            this.value = value;
        }
    }

    void __invoke_jdk8HandlersMarkedAsStdImplVpack() throws Exception {
        try {
            jdk8HandlersMarkedAsStdImplVpack();
        } finally {
        }
    }

}
