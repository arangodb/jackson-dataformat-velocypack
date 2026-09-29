package tools.jackson.databind.convert;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.cfg.CoercionAction;
import tools.jackson.databind.cfg.CoercionInputShape;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.util.StdConverter;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0159F0 {
private static final byte[] OBJECT_VALUE_3_4 = VPackWireFixtureTest.hex(
            "0b 0e 01 45 76 61 6c 75 65 02 04 33 34 03");
private static final byte[] OBJECT_REF_3_4 = VPackWireFixtureTest.hex(
            "0b 0c 01 43 72 65 66 02 04 33 34 03");
private static final byte[] OBJECT_OPT_6_7 = VPackWireFixtureTest.hex(
            "0b 0c 01 43 6f 70 74 02 04 36 37 03");
private static final byte[] OBJECT_FIELD_ARRAY_INTEGER = VPackWireFixtureTest.hex(
            "0b 0d 01 45 66 69 65 6c 64 02 03 31 03");
private static final byte[] OBJECT_FIELD_ARRAY_ARRAY_INTEGER = VPackWireFixtureTest.hex(
            "0b 0f 01 45 66 69 65 6c 64 02 05 02 03 31 03");
private static final byte[] OBJECT_FIELD_ARRAY_OBJECT = VPackWireFixtureTest.hex(
            "0b 17 01 45 66 69 65 6c 64 02 0d 0b 0b 01 45 66 69 65 6c 64 31 03 03");
private static final byte[] BLANK_STRING = VPackWireFixtureTest.hex("41 20");
private static final ObjectMapper NORMAL_MAPPER = VPackMapper.builder()
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .build();
private static final ObjectMapper COERCION_MAPPER = VPackMapper.builder()
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .withCoercionConfigDefaults(cfg -> cfg.setAcceptBlankAsEmpty(true)
                    .setCoercion(CoercionInputShape.EmptyString, CoercionAction.AsEmpty))
            .build();

    void testPropertyAnnotationSimple() throws Exception {
        assertArrayEquals(OBJECT_VALUE_3_4,
                new VPackMapper().writeValueAsBytes(new PointWrapper(3, 4)));
    }

    void testPropertyAnnotationForReferences() throws Exception {
        assertArrayEquals(OBJECT_REF_3_4,
                new VPackMapper().writeValueAsBytes(new PointReferenceBean(3, 4)));
    }

    void testPropertyAnnotationForOptionals() throws Exception {
        assertArrayEquals(OBJECT_OPT_6_7,
                new VPackMapper().writeValueAsBytes(new PointOptionalBean(6, 7)));
    }
private static ObjectMapper strictMapper() {
        return VPackMapper.builder()
                .withCoercionConfigDefaults(config -> {
                    config.setCoercion(CoercionInputShape.Boolean, CoercionAction.Fail)
                            .setCoercion(CoercionInputShape.Integer, CoercionAction.Fail)
                            .setCoercion(CoercionInputShape.Float, CoercionAction.Fail)
                            .setCoercion(CoercionInputShape.String, CoercionAction.Fail)
                            .setCoercion(CoercionInputShape.Array, CoercionAction.Fail)
                            .setCoercion(CoercionInputShape.Object, CoercionAction.Fail);
                })
                .build();
    }
private static void verifyFailedCoercion(byte[] input, String expectedMessage,
            ObjectMapper mapper, JavaType inputType) throws Exception {
        MismatchedInputException failure = assertThrows(MismatchedInputException.class,
                () -> mapper.readValue(input, inputType));
        assertEquals(String.class, failure.getTargetType());
        assertTrue(failure.getMessage().contains(expectedMessage), failure.getMessage());
    }
static class PointWrapper {
        @JsonSerialize(converter = PointConverter.class)
        public Point value;
        PointWrapper(int x, int y) { value = new Point(x, y); }
    }
static class PointReferenceBean {
        @JsonSerialize(contentConverter = PointConverter.class)
        public AtomicReference<Point> ref;
        PointReferenceBean(int x, int y) { ref = new AtomicReference<>(new Point(x, y)); }
    }
static class PointOptionalBean {
        @JsonSerialize(contentConverter = PointConverter.class)
        public Optional<Point> opt;
        PointOptionalBean(int x, int y) { opt = Optional.of(new Point(x, y)); }
    }
static class Point {
        public int x, y;
        Point(int x, int y) { this.x = x; this.y = y; }
    }
static class PointConverter extends StdConverter<Point, int[]> {
        @Override public int[] convert(Point value) { return new int[] { value.x, value.y }; }
    }
static class Input3690 {
        public List<String> field;
    }
static class Input3924<T> {
        private T field;
        @JsonProperty("field")
        public T getField() { return field; }
        @JsonProperty("field")
        public void setField(T field) { this.field = field; }
    }
static final class StringWrapper {
        private final String s;

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        StringWrapper(String s) { this.s = s; }

        @Override public boolean equals(Object obj) {
            return obj instanceof StringWrapper sw && sw.s.equals(s);
        }

        @Override public int hashCode() { return s.hashCode(); }
    }

    void __invoke_testPropertyAnnotationSimple() throws Exception {
        try {
            testPropertyAnnotationSimple();
        } finally {
        }
    }


    void __invoke_testPropertyAnnotationForReferences() throws Exception {
        try {
            testPropertyAnnotationForReferences();
        } finally {
        }
    }


    void __invoke_testPropertyAnnotationForOptionals() throws Exception {
        try {
            testPropertyAnnotationForOptionals();
        } finally {
        }
    }

}
