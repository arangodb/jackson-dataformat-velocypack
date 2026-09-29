package tools.jackson.databind.ext.jdk8;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.DoubleStream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0368F2 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] CONTEXTUAL_OPTIONALS = VPackWireFixtureTest.hex(
            "0b 38 03 44 64 61 74 65 4a 31 39 37 30 2f 30 31 2f 30 31 "
            + "45 64 61 74 65 31 4a 31 39 37 30 2b 30 31 2b 30 31 "
            + "45 64 61 74 65 32 4a 31 39 37 30 2a 30 31 2a 30 31 03 13 24");
private static final byte[] CREATOR_WITH_OPTIONAL = VPackWireFixtureTest.hex(
            "0b 0a 01 41 61 43 66 6f 6f 03");
private static final byte[] EMPTY_DOUBLE_STREAM = VPackWireFixtureTest.hex("01");
private static final byte[] SINGLE_DOUBLE_STREAM = VPackWireFixtureTest.hex(
            "02 0b 1b 00 00 00 00 00 00 f0 3f");
private static final byte[] MULTI_DOUBLE_STREAM = VPackWireFixtureTest.hex(
            "02 38 "
            + "1b 01 00 00 00 00 00 00 00 "
            + "1b ff ff ff ff ff ff ef 7f "
            + "1b 00 00 00 00 00 00 f0 3f "
            + "1b 00 00 00 00 00 00 00 00 "
            + "1b 00 00 00 00 00 00 18 40 "
            + "1b 00 00 00 00 00 00 08 c0");
private static final byte[] SINGLE_NEGATIVE_DOUBLE_STREAM = VPackWireFixtureTest.hex(
            "02 0b 1b 00 00 00 00 00 00 f8 bf");
private static final byte[] BOUNDARY_DOUBLE_STREAM = VPackWireFixtureTest.hex(
            "02 1d "
            + "1b 01 00 00 00 00 00 00 00 "
            + "1b 00 00 00 00 00 00 00 00 "
            + "1b ff ff ff ff ff ff ef 7f");
private static final byte[] WRAPPED_DOUBLE_STREAM = VPackWireFixtureTest.hex(
            "0b 27 01 45 76 61 6c 75 65 02 1d "
            + "1b 9a 99 99 99 99 99 f1 3f "
            + "1b 9a 99 99 99 99 99 01 40 "
            + "1b 66 66 66 66 66 66 0a 40 03");
private static final byte[] WRAPPED_EMPTY_DOUBLE_STREAM = VPackWireFixtureTest.hex(
            "0b 0b 01 45 76 61 6c 75 65 01 03");

    // Provenance: DoubleStreamSerializerTest#testEmptyStream().
    void testEmptyStreamVpack() throws Exception {
        assertArrayEquals(EMPTY_DOUBLE_STREAM,
                MAPPER.writeValueAsBytes(DoubleStream.empty()));
        assertArrayEquals(new double[0], MAPPER.readValue(EMPTY_DOUBLE_STREAM, double[].class), 0.0);
    }

    // Provenance: DoubleStreamSerializerTest#testSingleElement().
    void testSingleElementVpack() throws Exception {
        assertArrayEquals(SINGLE_DOUBLE_STREAM,
                MAPPER.writeValueAsBytes(DoubleStream.of(1.0)));
        assertArrayEquals(new double[] { 1.0 },
                MAPPER.readValue(SINGLE_DOUBLE_STREAM, double[].class), 0.0);
    }

    // Provenance: DoubleStreamSerializerTest#testMultiElements().
    void testMultiElementsVpack() throws Exception {
        double[] expected = { Double.MIN_VALUE, Double.MAX_VALUE, 1.0, 0.0, 6.0, -3.0 };
        assertArrayEquals(MULTI_DOUBLE_STREAM, MAPPER.writeValueAsBytes(DoubleStream.of(expected)));
        assertArrayEquals(expected, MAPPER.readValue(MULTI_DOUBLE_STREAM, double[].class), 0.0);
    }

    // Provenance: DoubleStreamSerializerTest#testDoubleStreamCloses().
    void testDoubleStreamClosesVpack() throws Exception {
        AtomicBoolean closed = new AtomicBoolean(false);
        MAPPER.writeValueAsBytes(DoubleStream.of(
                Double.MIN_VALUE, Double.MAX_VALUE, 1.0, 0.0, 6.0, -3.0)
                .onClose(() -> closed.set(true)));
        assertTrue(closed.get());
    }

    // Provenance: DoubleStreamSerializerTest#testDoubleStreamInWrapper().
    void testDoubleStreamInWrapperVpack() throws Exception {
        assertArrayEquals(WRAPPED_DOUBLE_STREAM,
                MAPPER.writeValueAsBytes(new DoubleStreamWrapper(DoubleStream.of(1.1, 2.2, 3.3))));
    }

    // Provenance: DoubleStreamSerializerTest#testDoubleStreamSingleNegative().
    void testDoubleStreamSingleNegativeVpack() throws Exception {
        assertArrayEquals(SINGLE_NEGATIVE_DOUBLE_STREAM,
                MAPPER.writeValueAsBytes(DoubleStream.of(-1.5)));
    }

    // Provenance: DoubleStreamSerializerTest#testDoubleStreamBoundaryValues().
    void testDoubleStreamBoundaryValuesVpack() throws Exception {
        double[] expected = { Double.MIN_VALUE, 0.0, Double.MAX_VALUE };
        assertArrayEquals(BOUNDARY_DOUBLE_STREAM,
                MAPPER.writeValueAsBytes(DoubleStream.of(expected)));
        assertArrayEquals(expected, MAPPER.readValue(BOUNDARY_DOUBLE_STREAM, double[].class), 0.0);
    }

    // Provenance: DoubleStreamSerializerTest#testDoubleStreamEmpty().
    void testDoubleStreamEmptyVpack() throws Exception {
        assertArrayEquals(WRAPPED_EMPTY_DOUBLE_STREAM,
                MAPPER.writeValueAsBytes(new DoubleStreamWrapper(DoubleStream.empty())));
    }

    // Provenance: DoubleStreamSerializerTest#testDoubleStreamInWrapperCloses().
    void testDoubleStreamInWrapperClosesVpack() throws Exception {
        AtomicBoolean closed = new AtomicBoolean(false);
        DoubleStream stream = DoubleStream.of(1.0, 2.0).onClose(() -> closed.set(true));
        MAPPER.writeValueAsBytes(new DoubleStreamWrapper(stream));
        assertTrue(closed.get());
    }
@JsonPropertyOrder({ "date", "date1", "date2" })
    static class ContextualOptionals {
        public Optional<Date> date;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy+MM+dd")
        public Optional<Date> date1;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy*MM*dd")
        public Optional<Date> date2;
    }
static class CreatorWithOptionalStrings {
        Optional<String> a;
        Optional<String> b;

        @JsonCreator
        public CreatorWithOptionalStrings(@JsonProperty("a") Optional<String> a,
                @JsonProperty("b") Optional<String> b) {
            this.a = a;
            this.b = b;
        }
    }
static class DoubleStreamWrapper {
        public DoubleStream value;

        public DoubleStreamWrapper() { }

        DoubleStreamWrapper(DoubleStream value) {
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


    void __invoke_testDoubleStreamClosesVpack() throws Exception {
        try {
            testDoubleStreamClosesVpack();
        } finally {
        }
    }


    void __invoke_testDoubleStreamInWrapperVpack() throws Exception {
        try {
            testDoubleStreamInWrapperVpack();
        } finally {
        }
    }


    void __invoke_testDoubleStreamSingleNegativeVpack() throws Exception {
        try {
            testDoubleStreamSingleNegativeVpack();
        } finally {
        }
    }


    void __invoke_testDoubleStreamBoundaryValuesVpack() throws Exception {
        try {
            testDoubleStreamBoundaryValuesVpack();
        } finally {
        }
    }


    void __invoke_testDoubleStreamEmptyVpack() throws Exception {
        try {
            testDoubleStreamEmptyVpack();
        } finally {
        }
    }


    void __invoke_testDoubleStreamInWrapperClosesVpack() throws Exception {
        try {
            testDoubleStreamInWrapperClosesVpack();
        } finally {
        }
    }

}
