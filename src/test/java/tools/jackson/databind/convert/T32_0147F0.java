package tools.jackson.databind.convert;

import java.util.List;
import java.util.Map;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.cfg.CoercionAction;
import tools.jackson.databind.cfg.CoercionInputShape;
import tools.jackson.databind.exc.MismatchedInputException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0147F0 {
private static final byte[] EMPTY_STRING = VPackWireFixtureTest.hex("40");
private static final byte[] EMPTY_ARRAY = VPackWireFixtureTest.hex("01");
private static final byte[] NESTED_EMPTY_ARRAY = VPackWireFixtureTest.hex(
            "13 0a 45 68 65 6c 6c 6f 01 02");
private static final ObjectMapper VANILLA_MAPPER = new VPackMapper();
private static final ObjectReader DEFAULT_READER = VANILLA_MAPPER.reader();
private static final ObjectReader READER_WITH_ARRAYS = DEFAULT_READER
            .with(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);
private static final ObjectMapper EMPTY_STRING_TO_EMPTY = VPackMapper.builder()
            .withCoercionConfigDefaults(cfg ->
                    cfg.setCoercion(CoercionInputShape.EmptyString, CoercionAction.AsEmpty))
            .build();
private static final ObjectMapper EMPTY_ARRAY_TO_EMPTY = VPackMapper.builder()
            .withCoercionConfigDefaults(cfg ->
                    cfg.setCoercion(CoercionInputShape.EmptyArray, CoercionAction.AsEmpty))
            .build();
private static final ObjectMapper EMPTY_ARRAY_TO_NULL = VPackMapper.builder()
            .withCoercionConfigDefaults(cfg ->
                    cfg.setCoercion(CoercionInputShape.EmptyArray, CoercionAction.AsNull))
            .build();
private static final ObjectMapper EMPTY_ARRAY_TO_TRY_CONVERT = VPackMapper.builder()
            .withCoercionConfigDefaults(cfg ->
                    cfg.setCoercion(CoercionInputShape.EmptyArray, CoercionAction.TryConvert))
            .build();
private static final ObjectMapper EMPTY_ARRAY_TO_FAIL = VPackMapper.builder()
            .withCoercionConfigDefaults(cfg ->
                    cfg.setCoercion(CoercionInputShape.EmptyArray, CoercionAction.Fail))
            .build();

    void testScalarCollections() throws Exception {
        JavaType listType = VANILLA_MAPPER.getTypeFactory()
                .constructType(new TypeReference<List<Double>>() { });

        assertThrows(DatabindException.class,
                () -> VANILLA_MAPPER.readerFor(listType).readValue(EMPTY_STRING));

        List<Double> result = EMPTY_STRING_TO_EMPTY.readValue(EMPTY_STRING, listType);
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    void testStringCollections() throws Exception {
        JavaType listType = VANILLA_MAPPER.getTypeFactory()
                .constructType(new TypeReference<List<String>>() { });
        assertVanillaEmptyStringFails(listType);

        List<String> result = EMPTY_STRING_TO_EMPTY.readValue(EMPTY_STRING, listType);
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    void testScalarMap() throws Exception {
        JavaType mapType = VANILLA_MAPPER.getTypeFactory()
                .constructType(new TypeReference<Map<Long, Boolean>>() { });
        assertVanillaEmptyStringFails(mapType);

        Map<?, ?> result = EMPTY_STRING_TO_EMPTY.readValue(EMPTY_STRING, mapType);
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    void testStringArray() throws Exception {
        JavaType arrayType = VANILLA_MAPPER.getTypeFactory()
                .constructType(new TypeReference<String[]>() { });
        assertVanillaEmptyStringFails(arrayType);

        String[] result = EMPTY_STRING_TO_EMPTY.readValue(EMPTY_STRING, arrayType);
        assertNotNull(result);
        assertEquals(0, result.length);
    }

    void testPOJOArray() throws Exception {
        assertVanillaEmptyStringFails(VANILLA_MAPPER.constructType(StringWrapper[].class));

        StringWrapper[] result = EMPTY_STRING_TO_EMPTY.readValue(EMPTY_STRING,
                StringWrapper[].class);
        assertNotNull(result);
        assertEquals(0, result.length);
    }
private static void assertVanillaEmptyStringFails(JavaType targetType) {
        assertThrows(DatabindException.class,
                () -> VANILLA_MAPPER.readerFor(targetType).readValue(EMPTY_STRING));
    }
private static void assertEmptyArrayFails(ObjectMapper mapper, Class<?> targetType) {
        assertEmptyArrayFails(mapper.reader(), targetType);
    }
private static void assertEmptyArrayFails(ObjectReader reader, Class<?> targetType) {
        MismatchedInputException failure = assertThrows(MismatchedInputException.class,
                () -> reader.forType(targetType).readValue(EMPTY_ARRAY));
        assertTrue(failure.getMessage().contains("from Array value"), failure.getMessage());
    }
static class Bean {
        public String a = "foo";

        @Override
        public boolean equals(Object o) {
            return (o instanceof Bean b) && a.equals(b.a);
        }
    }
static class StringWrapper {
        public String str;
    }
enum ABC { A, B, C }

    void __invoke_testScalarCollections() throws Exception {
        try {
            testScalarCollections();
        } finally {
        }
    }


    void __invoke_testStringCollections() throws Exception {
        try {
            testStringCollections();
        } finally {
        }
    }


    void __invoke_testScalarMap() throws Exception {
        try {
            testScalarMap();
        } finally {
        }
    }


    void __invoke_testStringArray() throws Exception {
        try {
            testStringArray();
        } finally {
        }
    }


    void __invoke_testPOJOArray() throws Exception {
        try {
            testPOJOArray();
        } finally {
        }
    }

}
