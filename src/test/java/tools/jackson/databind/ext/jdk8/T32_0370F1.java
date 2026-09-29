package tools.jackson.databind.ext.jdk8;

import java.util.Optional;
import java.util.stream.LongStream;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0370F1 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] EMPTY_STREAM = VPackWireFixtureTest.hex("01");
private static final byte[] SINGLE_ELEMENT = VPackWireFixtureTest.hex(
            "02 03 31");
private static final byte[] MULTI_ELEMENTS = VPackWireFixtureTest.hex(
            "06 1f 06 "
            + "27 00 00 00 00 00 00 00 80 "
            + "2f ff ff ff ff ff ff ff 7f "
            + "31 30 36 3d "
            + "03 0c 15 16 17 18");
private static final byte[] WRAPPED_STREAM = VPackWireFixtureTest.hex(
            "0b 17 01 45 76 61 6c 75 65 "
            + "06 0d 03 28 64 28 c8 29 2c 01 03 05 07 03");
private static final byte[] SINGLE_NEGATIVE = VPackWireFixtureTest.hex(
            "02 03 3f");
private static final byte[] BOUNDARY_VALUES = VPackWireFixtureTest.hex(
            "06 19 03 "
            + "27 00 00 00 00 00 00 00 80 30 "
            + "2f ff ff ff ff ff ff ff 7f "
            + "03 0c 0d");
private static final byte[] OPTIONAL_NULL = VPackWireFixtureTest.hex("18");
private static final byte[] OPTIONAL_STRING_PROPERTY = VPackWireFixtureTest.hex(
            "0b 1a 01 48 6d 79 53 74 72 69 6e 67 "
            + "4c 73 69 6d 70 6c 65 53 74 72 69 6e 67 03");
private static final byte[] OPTIONAL_GENERIC_PROPERTY = VPackWireFixtureTest.hex(
            "0b 18 01 46 6d 79 44 61 74 61 "
            + "4c 73 69 6d 70 6c 65 53 74 72 69 6e 67 03");

    // Provenance: OptionalBasicTest#testDeserAbsent().
    void testDeserAbsentVpack() throws Exception {
        Optional<?> value = MAPPER.readValue(OPTIONAL_NULL,
                new TypeReference<Optional<String>>() { });
        assertFalse(value.isPresent());
    }

    // Provenance: OptionalBasicTest#testDeserInsideObject().
    void testDeserInsideObjectVpack() throws Exception {
        OptionalData data = MAPPER.readValue(OPTIONAL_STRING_PROPERTY,
                OptionalData.class);
        assertTrue(data.myString.isPresent());
        assertEquals("simpleString", data.myString.get());
    }

    // Provenance: OptionalBasicTest#testDeserComplexObject().
    void testDeserComplexObjectVpack() throws Exception {
        TypeReference<Optional<OptionalData>> type =
                new TypeReference<Optional<OptionalData>>() { };
        Optional<OptionalData> data = MAPPER.readValue(OPTIONAL_STRING_PROPERTY, type);
        assertTrue(data.isPresent());
        assertTrue(data.get().myString.isPresent());
        assertEquals("simpleString", data.get().myString.get());
    }

    // Provenance: OptionalBasicTest#testDeserGeneric().
    void testDeserGenericVpack() throws Exception {
        TypeReference<Optional<OptionalGenericData<String>>> type =
                new TypeReference<Optional<OptionalGenericData<String>>>() { };
        Optional<OptionalGenericData<String>> data =
                MAPPER.readValue(OPTIONAL_GENERIC_PROPERTY, type);
        assertTrue(data.isPresent());
        assertTrue(data.get().myData.isPresent());
        assertEquals("simpleString", data.get().myData.get());
    }
static class LongStreamWrapper {
        public LongStream value;

        public LongStreamWrapper() { }

        LongStreamWrapper(LongStream value) {
            this.value = value;
        }
    }
static class OptionalData {
        public Optional<String> myString;
    }
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
    static class OptionalGenericData<T> {
        Optional<T> myData;
    }

    void __invoke_testDeserAbsentVpack() throws Exception {
        try {
            testDeserAbsentVpack();
        } finally {
        }
    }


    void __invoke_testDeserInsideObjectVpack() throws Exception {
        try {
            testDeserInsideObjectVpack();
        } finally {
        }
    }


    void __invoke_testDeserComplexObjectVpack() throws Exception {
        try {
            testDeserComplexObjectVpack();
        } finally {
        }
    }


    void __invoke_testDeserGenericVpack() throws Exception {
        try {
            testDeserGenericVpack();
        } finally {
        }
    }

}
