package tools.jackson.databind.ext.jdk8;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0369F0 {
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

    // Provenance: IntStreamSerializerTest#testEmptyStream().
    void testEmptyStreamVpack() throws Exception {
        assertArrayEquals(EMPTY_STREAM, MAPPER.writeValueAsBytes(IntStream.empty()));
        assertArrayEquals(new int[0], MAPPER.readValue(EMPTY_STREAM, int[].class));
    }

    // Provenance: IntStreamSerializerTest#testSingleElement().
    void testSingleElementVpack() throws Exception {
        assertArrayEquals(SINGLE_ELEMENT,
                MAPPER.writeValueAsBytes(IntStream.of(1)));
        assertArrayEquals(new int[] { 1 },
                MAPPER.readValue(SINGLE_ELEMENT, int[].class));
    }

    // Provenance: IntStreamSerializerTest#testMultiElements().
    void testMultiElementsVpack() throws Exception {
        int[] expected = { Integer.MIN_VALUE, Integer.MAX_VALUE, 1, 0, 6, -3 };
        assertArrayEquals(MULTI_ELEMENTS,
                MAPPER.writeValueAsBytes(IntStream.of(expected)));
        assertArrayEquals(expected, MAPPER.readValue(MULTI_ELEMENTS, int[].class));
    }

    // Provenance: IntStreamSerializerTest#testIntStreamCloses().
    void testIntStreamClosesVpack() throws Exception {
        AtomicBoolean closed = new AtomicBoolean(false);
        MAPPER.writeValueAsBytes(IntStream.of(1, 2, 3)
                .onClose(() -> closed.set(true)));
        assertTrue(closed.get());
    }

    // Provenance: IntStreamSerializerTest#testIntStreamInWrapper().
    void testIntStreamInWrapperVpack() throws Exception {
        assertArrayEquals(WRAPPED_STREAM,
                MAPPER.writeValueAsBytes(new IntStreamWrapper(
                        IntStream.of(10, 20, 30))));
    }

    // Provenance: IntStreamSerializerTest#testIntStreamSingleNegative().
    void testIntStreamSingleNegativeVpack() throws Exception {
        assertArrayEquals(SINGLE_NEGATIVE,
                MAPPER.writeValueAsBytes(IntStream.of(-1)));
    }

    // Provenance: IntStreamSerializerTest#testIntStreamBoundaryValues().
    void testIntStreamBoundaryValuesVpack() throws Exception {
        int[] expected = { Integer.MIN_VALUE, 0, Integer.MAX_VALUE };
        assertArrayEquals(BOUNDARY_VALUES,
                MAPPER.writeValueAsBytes(IntStream.of(expected)));
        assertArrayEquals(expected, MAPPER.readValue(BOUNDARY_VALUES, int[].class));
    }

    // Provenance: IntStreamSerializerTest#testIntStreamInWrapperCloses().
    void testIntStreamInWrapperClosesVpack() throws Exception {
        AtomicBoolean closed = new AtomicBoolean(false);
        IntStream stream = IntStream.of(1, 2, 3)
                .onClose(() -> closed.set(true));
        MAPPER.writeValueAsBytes(new IntStreamWrapper(stream));
        assertTrue(closed.get());
    }
static class IntStreamWrapper {
        public IntStream value;

        public IntStreamWrapper() { }

        IntStreamWrapper(IntStream value) {
            this.value = value;
        }
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


    void __invoke_testIntStreamClosesVpack() throws Exception {
        try {
            testIntStreamClosesVpack();
        } finally {
        }
    }


    void __invoke_testIntStreamInWrapperVpack() throws Exception {
        try {
            testIntStreamInWrapperVpack();
        } finally {
        }
    }


    void __invoke_testIntStreamSingleNegativeVpack() throws Exception {
        try {
            testIntStreamSingleNegativeVpack();
        } finally {
        }
    }


    void __invoke_testIntStreamBoundaryValuesVpack() throws Exception {
        try {
            testIntStreamBoundaryValuesVpack();
        } finally {
        }
    }


    void __invoke_testIntStreamInWrapperClosesVpack() throws Exception {
        try {
            testIntStreamInWrapperClosesVpack();
        } finally {
        }
    }

}
