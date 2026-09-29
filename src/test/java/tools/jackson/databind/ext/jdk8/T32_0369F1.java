package tools.jackson.databind.ext.jdk8;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.stream.IntStream;

import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.type.ReferenceType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0369F1 {
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

    // Provenance: JDK8TypesTest#testOptionalsAreReferentialTypes().
    void testOptionalsAreReferentialTypesVpack() {
        JavaType type = MAPPER.constructType(Optional.class);
        assertTrue(type.isReferenceType());
        assertEquals(Object.class, ((ReferenceType) type).getContentType().getRawClass());

        type = MAPPER.constructType(OptionalInt.class);
        assertTrue(type.isReferenceType());
        assertEquals(Integer.TYPE, ((ReferenceType) type).getContentType().getRawClass());

        type = MAPPER.constructType(OptionalLong.class);
        assertTrue(type.isReferenceType());
        assertEquals(Long.TYPE, ((ReferenceType) type).getContentType().getRawClass());

        type = MAPPER.constructType(OptionalDouble.class);
        assertTrue(type.isReferenceType());
        assertEquals(Double.TYPE, ((ReferenceType) type).getContentType().getRawClass());
    }
static class IntStreamWrapper {
        public IntStream value;

        public IntStreamWrapper() { }

        IntStreamWrapper(IntStream value) {
            this.value = value;
        }
    }

    void __invoke_testOptionalsAreReferentialTypesVpack() throws Exception {
        try {
            testOptionalsAreReferentialTypesVpack();
        } finally {
        }
    }

}
