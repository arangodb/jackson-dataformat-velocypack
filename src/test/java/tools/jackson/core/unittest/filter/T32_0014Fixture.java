package tools.jackson.core.unittest.filter;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.core.filter.FilteringGeneratorDelegate;
import tools.jackson.core.filter.TokenFilter;
import tools.jackson.core.filter.TokenFilter.Inclusion;

import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0014Fixture {
private static final TokenFilter INCLUDE_EMPTY_IF_NOT_FILTERED = new TokenFilter() {
        @Override
        public boolean includeEmptyArray(boolean contentsFiltered) {
            return !contentsFiltered;
        }

        @Override
        public boolean includeEmptyObject(boolean contentsFiltered) {
            return !contentsFiltered;
        }

        @Override
        protected boolean _includeScalar() {
            return false;
        }
    };
private static final TokenFilter INCLUDE_EMPTY = new TokenFilter() {
        @Override
        public boolean includeEmptyArray(boolean contentsFiltered) {
            return true;
        }

        @Override
        public boolean includeEmptyObject(boolean contentsFiltered) {
            return true;
        }

        @Override
        protected boolean _includeScalar() {
            return false;
        }
    };

    void includeEmptyArrayIfNotFiltered() throws Exception {
        assertEquals(Map.of("empty_array", List.of()), filtered(INCLUDE_EMPTY_IF_NOT_FILTERED,
                generator -> {
                    generator.writeStartObject();
                    generator.writeArrayPropertyStart("empty_array");
                    generator.writeEndArray();
                    generator.writeArrayPropertyStart("filtered_array");
                    generator.writeNumber(6);
                    generator.writeEndArray();
                    generator.writeEndObject();
                }));
    }

    void includeEmptyArray() throws Exception {
        assertEquals(Map.of("empty_array", List.of(), "filtered_array", List.of()),
                filtered(INCLUDE_EMPTY, generator -> {
                    generator.writeStartObject();
                    generator.writeArrayPropertyStart("empty_array");
                    generator.writeEndArray();
                    generator.writeArrayPropertyStart("filtered_array");
                    generator.writeNumber(6);
                    generator.writeEndArray();
                    generator.writeEndObject();
                }));
    }

    void includeEmptyArrayInObjectIfNotFiltered() throws Exception {
        assertEquals(Map.of("object_with_empty_array", Map.of("foo", List.of())),
                filtered(INCLUDE_EMPTY_IF_NOT_FILTERED, generator -> {
                    generator.writeStartObject();
                    generator.writeObjectPropertyStart("object_with_empty_array");
                    generator.writeArrayPropertyStart("foo");
                    generator.writeEndArray();
                    generator.writeEndObject();
                    generator.writeObjectPropertyStart("object_with_filtered_array");
                    generator.writeArrayPropertyStart("foo");
                    generator.writeNumber(5);
                    generator.writeEndArray();
                    generator.writeEndObject();
                    generator.writeEndObject();
                }));
    }

    void includeEmptyArrayInObject() throws Exception {
        assertEquals(Map.of(
                "object_with_empty_array", Map.of("foo", List.of()),
                "object_with_filtered_array", Map.of("foo", List.of())),
                filtered(INCLUDE_EMPTY, generator -> {
                    generator.writeStartObject();
                    generator.writeObjectPropertyStart("object_with_empty_array");
                    generator.writeArrayPropertyStart("foo");
                    generator.writeEndArray();
                    generator.writeEndObject();
                    generator.writeObjectPropertyStart("object_with_filtered_array");
                    generator.writeArrayPropertyStart("foo");
                    generator.writeNumber(5);
                    generator.writeEndArray();
                    generator.writeEndObject();
                    generator.writeEndObject();
                }));
    }

    void includeEmptyObjectIfNotFiltered() throws Exception {
        assertEquals(Map.of("empty_object", Map.of()),
                filtered(INCLUDE_EMPTY_IF_NOT_FILTERED, generator -> {
                    generator.writeStartObject();
                    generator.writeObjectPropertyStart("empty_object");
                    generator.writeEndObject();
                    generator.writeObjectPropertyStart("filtered_object");
                    generator.writeNumberProperty("foo", 6);
                    generator.writeEndObject();
                    generator.writeEndObject();
                }));
    }

    void includeEmptyObject() throws Exception {
        assertEquals(Map.of("empty_object", Map.of(), "filtered_object", Map.of()),
                filtered(INCLUDE_EMPTY, generator -> {
                    generator.writeStartObject();
                    generator.writeObjectPropertyStart("empty_object");
                    generator.writeEndObject();
                    generator.writeObjectPropertyStart("filtered_object");
                    generator.writeNumberProperty("foo", 6);
                    generator.writeEndObject();
                    generator.writeEndObject();
                }));
    }

    void includeEmptyObjectInArrayIfNotFiltered() throws Exception {
        assertEquals(Map.of("array_with_empty_object", List.of(Map.of())),
                filtered(INCLUDE_EMPTY_IF_NOT_FILTERED, generator -> {
                    generator.writeStartObject();
                    generator.writeArrayPropertyStart("array_with_empty_object");
                    generator.writeStartObject();
                    generator.writeEndObject();
                    generator.writeEndArray();
                    generator.writeArrayPropertyStart("array_with_filtered_object");
                    generator.writeStartObject();
                    generator.writeNumberProperty("foo", 5);
                    generator.writeEndObject();
                    generator.writeEndArray();
                    generator.writeEndObject();
                }));
    }

    void includeEmptyObjectInArray() throws Exception {
        assertEquals(Map.of(
                "array_with_empty_object", List.of(Map.of()),
                "array_with_filtered_object", List.of(Map.of())),
                filtered(INCLUDE_EMPTY, generator -> {
                    generator.writeStartObject();
                    generator.writeArrayPropertyStart("array_with_empty_object");
                    generator.writeStartObject();
                    generator.writeEndObject();
                    generator.writeEndArray();
                    generator.writeArrayPropertyStart("array_with_filtered_object");
                    generator.writeStartObject();
                    generator.writeNumberProperty("foo", 5);
                    generator.writeEndObject();
                    generator.writeEndArray();
                    generator.writeEndObject();
                }));
    }

    void includeEmptyTopLevelArray() throws Exception {
        assertEquals(List.of(), filtered(INCLUDE_EMPTY_IF_NOT_FILTERED, generator -> {
            generator.writeStartArray();
            generator.writeEndArray();
        }));
    }
private static Object filtered(TokenFilter filter, WriterCall call) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = new FilteringGeneratorDelegate(
                new VPackFactory().createGenerator(ObjectWriteContext.empty(), output),
                filter, Inclusion.INCLUDE_ALL_AND_PATH, true)) {
            call.write(generator);
        }
        return new VPackMapper().readValue(output.toByteArray(), Object.class);
    }
private static byte[] largeExponentDecimalFixture() {
        byte[] result = new byte[1 + 2 + 4 + 250];
        result[0] = (byte) 0xC9;
        result[1] = (byte) 0xFA;
        result[2] = 0;
        result[3] = (byte) 0x80;
        result[4] = (byte) 0x96;
        result[5] = (byte) 0x98;
        result[6] = 0;
        java.util.Arrays.fill(result, 7, result.length, (byte) 0x11);
        return result;
    }
private static byte[] bcdFixture(int exponent) {
        return new byte[] {
                (byte) 0xC8, 0x01,
                (byte) exponent, (byte) (exponent >>> 8),
                (byte) (exponent >>> 16), (byte) (exponent >>> 24),
                0x01
        };
    }
@FunctionalInterface
    private interface WriterCall {
        void write(JsonGenerator generator) throws Exception;
    }

    void __invoke_includeEmptyArrayIfNotFiltered() throws Exception {
        try {
            includeEmptyArrayIfNotFiltered();
        } finally {
        }
    }


    void __invoke_includeEmptyArray() throws Exception {
        try {
            includeEmptyArray();
        } finally {
        }
    }


    void __invoke_includeEmptyArrayInObjectIfNotFiltered() throws Exception {
        try {
            includeEmptyArrayInObjectIfNotFiltered();
        } finally {
        }
    }


    void __invoke_includeEmptyArrayInObject() throws Exception {
        try {
            includeEmptyArrayInObject();
        } finally {
        }
    }


    void __invoke_includeEmptyObjectIfNotFiltered() throws Exception {
        try {
            includeEmptyObjectIfNotFiltered();
        } finally {
        }
    }


    void __invoke_includeEmptyObject() throws Exception {
        try {
            includeEmptyObject();
        } finally {
        }
    }


    void __invoke_includeEmptyObjectInArrayIfNotFiltered() throws Exception {
        try {
            includeEmptyObjectInArrayIfNotFiltered();
        } finally {
        }
    }


    void __invoke_includeEmptyObjectInArray() throws Exception {
        try {
            includeEmptyObjectInArray();
        } finally {
        }
    }


    void __invoke_includeEmptyTopLevelArray() throws Exception {
        try {
            includeEmptyTopLevelArray();
        } finally {
        }
    }

}
