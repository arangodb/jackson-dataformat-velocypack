package tools.jackson.databind.ext.jdk8;

import java.util.Optional;
import java.util.stream.LongStream;
import java.util.concurrent.atomic.AtomicBoolean;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import tools.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0370F0 {
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

    // Provenance: LongStreamSerializerTest#testEmptyStream().
    void testEmptyStreamVpack() throws Exception {
        assertArrayEquals(EMPTY_STREAM,
                MAPPER.writeValueAsBytes(LongStream.empty()));
        assertArrayEquals(new long[0], MAPPER.readValue(EMPTY_STREAM, long[].class));
    }

    // Provenance: LongStreamSerializerTest#testSingleElement().
    void testSingleElementVpack() throws Exception {
        assertArrayEquals(SINGLE_ELEMENT,
                MAPPER.writeValueAsBytes(LongStream.of(1L)));
        assertArrayEquals(new long[] { 1L },
                MAPPER.readValue(SINGLE_ELEMENT, long[].class));
    }

    // Provenance: LongStreamSerializerTest#testMultiElements().
    void testMultiElementsVpack() throws Exception {
        long[] expected = { Long.MIN_VALUE, Long.MAX_VALUE, 1L, 0L, 6L, -3L };
        assertArrayEquals(MULTI_ELEMENTS,
                MAPPER.writeValueAsBytes(LongStream.of(expected)));
        assertArrayEquals(expected, MAPPER.readValue(MULTI_ELEMENTS, long[].class));
    }

    // Provenance: LongStreamSerializerTest#testLongStreamCloses().
    void testLongStreamClosesVpack() throws Exception {
        AtomicBoolean closed = new AtomicBoolean(false);
        MAPPER.writeValueAsBytes(LongStream.of(
                Long.MIN_VALUE, Long.MAX_VALUE, 1L, 0L, 6L, -3L)
                .onClose(() -> closed.set(true)));
        assertTrue(closed.get());
    }

    // Provenance: LongStreamSerializerTest#testLongStreamInWrapper().
    void testLongStreamInWrapperVpack() throws Exception {
        assertArrayEquals(WRAPPED_STREAM,
                MAPPER.writeValueAsBytes(new LongStreamWrapper(
                        LongStream.of(100L, 200L, 300L))));
    }

    // Provenance: LongStreamSerializerTest#testLongStreamSingleNegative().
    void testLongStreamSingleNegativeVpack() throws Exception {
        assertArrayEquals(SINGLE_NEGATIVE,
                MAPPER.writeValueAsBytes(LongStream.of(-1L)));
    }

    // Provenance: LongStreamSerializerTest#testLongStreamBoundaryValues().
    void testLongStreamBoundaryValuesVpack() throws Exception {
        long[] expected = { Long.MIN_VALUE, 0L, Long.MAX_VALUE };
        assertArrayEquals(BOUNDARY_VALUES,
                MAPPER.writeValueAsBytes(LongStream.of(expected)));
        assertArrayEquals(expected, MAPPER.readValue(BOUNDARY_VALUES, long[].class));
    }

    // Provenance: LongStreamSerializerTest#testLongStreamInWrapperCloses().
    void testLongStreamInWrapperClosesVpack() throws Exception {
        AtomicBoolean closed = new AtomicBoolean(false);
        LongStream stream = LongStream.of(1L, 2L)
                .onClose(() -> closed.set(true));
        MAPPER.writeValueAsBytes(new LongStreamWrapper(stream));
        assertTrue(closed.get());
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

    void __invoke_testEmptyStreamVpack() throws Exception {
        try {
            testEmptyStreamVpack();
        } finally {
        }
    }


    void __invoke_testSingleElementVpack() throws Exception {
        try {
            testSingleElementVpack();
        } finally {
        }
    }


    void __invoke_testMultiElementsVpack() throws Exception {
        try {
            testMultiElementsVpack();
        } finally {
        }
    }


    void __invoke_testLongStreamClosesVpack() throws Exception {
        try {
            testLongStreamClosesVpack();
        } finally {
        }
    }


    void __invoke_testLongStreamInWrapperVpack() throws Exception {
        try {
            testLongStreamInWrapperVpack();
        } finally {
        }
    }


    void __invoke_testLongStreamSingleNegativeVpack() throws Exception {
        try {
            testLongStreamSingleNegativeVpack();
        } finally {
        }
    }


    void __invoke_testLongStreamBoundaryValuesVpack() throws Exception {
        try {
            testLongStreamBoundaryValuesVpack();
        } finally {
        }
    }


    void __invoke_testLongStreamInWrapperClosesVpack() throws Exception {
        try {
            testLongStreamInWrapperClosesVpack();
        } finally {
        }
    }

}
