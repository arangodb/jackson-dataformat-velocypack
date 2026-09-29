package tools.jackson.databind.deser;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.Nulls;
import tools.jackson.core.Version;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.exc.InvalidNullException;
import tools.jackson.databind.module.SimpleModule;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0182F0 {
private static final byte[] NULL = VPackWireFixtureTest.hex("18");
private static final byte[] NULL_FIELD = VPackWireFixtureTest.hex(
            "14 08 43 73 74 72 18 01");
private static final byte[] LIST_NULL = VPackWireFixtureTest.hex(
            "13 04 18 01");
private static final byte[] MAP_NULL = VPackWireFixtureTest.hex(
            "14 08 43 6b 65 79 18 01");
private static final byte[] POLYMORPHIC_TYPE_A = VPackWireFixtureTest.hex(
            "14 2b 45 70 72 6f 78 79 "
          + "14 22 45 40 74 79 70 65 45 54 79 70 65 41 46 61 56 61 6c 75 65 4b 54 68 69 73 20 77 6f 72 6b 73 21 02 "
          + "01");
private static final byte[] POLYMORPHIC_TYPE_B = VPackWireFixtureTest.hex(
            "14 2f 45 70 72 6f 78 79 "
          + "14 26 45 40 74 79 70 65 45 54 79 70 65 42 46 62 56 61 6c 75 65 4f 54 68 69 73 20 77 6f 72 6b 73 20 74 6f 6f 21 02 "
          + "01");
private static final byte[] POLYMORPHIC_TYPE_B_NULL = VPackWireFixtureTest.hex(
            "14 0a 45 70 72 6f 78 79 18 01");
private static final byte[] MAP_CONTENT_NULL = VPackWireFixtureTest.hex(
            "14 14 45 66 69 65 6c 64 18 48 70 72 6f 70 65 72 74 79 31 02");
private static final byte[] ENUM_MAP_CONTENT_NULL = VPackWireFixtureTest.hex(
            "14 09 41 41 31 41 42 18 02");
private static final byte[] ARRAY_CONTENT_NULL_FIRST = VPackWireFixtureTest.hex(
            "13 0e 18 14 0a 45 66 69 65 6c 64 31 01 02");
private static final byte[] ARRAY_CONTENT_NULL_LAST = VPackWireFixtureTest.hex(
            "13 0e 14 0a 45 66 69 65 6c 64 31 01 18 02");
private static final byte[] STRING_CONTENT_NULL = VPackWireFixtureTest.hex(
            "13 08 43 66 6f 6f 18 02");
private static final byte[] ASCENDING_KEYS = VPackWireFixtureTest.hex(
            "14 21 "
          + "41 61 47 61 2d 76 61 6c 75 65 "
          + "41 62 47 62 2d 76 61 6c 75 65 "
          + "41 63 47 63 2d 76 61 6c 75 65 "
          + "03");
private static final byte[] DESCENDING_KEYS = VPackWireFixtureTest.hex(
            "14 21 "
          + "41 63 47 63 2d 76 61 6c 75 65 "
          + "41 62 47 62 2d 76 61 6c 75 65 "
          + "41 61 47 61 2d 76 61 6c 75 65 "
          + "03");
private static final ObjectMapper MAPPER = new VPackMapper();
private static final ObjectMapper CONTENT_NULL_FAIL_MAPPER = VPackMapper.builder()
            .changeDefaultNullHandling(n -> n.withContentNulls(Nulls.FAIL))
            .build();

    void testNull() {
        assertNull(MAPPER.readValue(NULL, Object.class));
        assertNull(MAPPER.readValue(NULL, String.class));

        StringWrapper result = MAPPER.readValue(NULL_FIELD, StringWrapper.class);
        assertNotNull(result);
        assertNull(result.str);
    }

    void testCustomRootNulls() {
        ObjectMapper mapper = mapperWithFunnyNulls();

        String direct = mapper.readValue(NULL, String.class);
        assertEquals("funny", direct);

        String throughReader = mapper.readerFor(String.class).readValue(NULL);
        assertEquals("funny", throughReader);
    }

    void testListOfNulls() {
        ObjectMapper mapper = mapperWithFunnyNulls();
        TypeReference<List<String>> type = new TypeReference<>() { };

        List<String> direct = mapper.readValue(LIST_NULL, type);
        assertEquals(List.of("funny"), direct);

        List<String> throughReader = mapper.readerFor(type).readValue(LIST_NULL);
        assertEquals(List.of("funny"), throughReader);
    }

    void testMapOfNulls() {
        ObjectMapper mapper = mapperWithFunnyNulls();
        TypeReference<Map<String, String>> type = new TypeReference<>() { };

        Map<String, String> direct = mapper.readValue(MAP_NULL, type);
        assertEquals(Map.of("key", "funny"), direct);

        Map<String, String> throughReader = mapper.readerFor(type).readValue(MAP_NULL);
        assertEquals(Map.of("key", "funny"), throughReader);
    }

    void testPolymorphicDataNull() {
        RootData typeA = MAPPER.readValue(POLYMORPHIC_TYPE_A, RootData.class);
        assertEquals("This works!", ((TypeA) typeA.proxy).aValue);

        RootData typeB = MAPPER.readValue(POLYMORPHIC_TYPE_B, RootData.class);
        assertEquals("This works too!", ((TypeB) typeB.proxy).bValue);

        RootData typeBNull = MAPPER.readValue(POLYMORPHIC_TYPE_B_NULL, RootData.class);
        assertNull(typeBNull.proxy);
    }

    void testContentsNullFailForMaps() {
        assertInvalidNull(MAP_CONTENT_NULL, Map.class);
        assertInvalidNull(ENUM_MAP_CONTENT_NULL,
                new TypeReference<EnumMap<EnumMapTestEnum, Integer>>() { });
    }

    void testContentsNullFailForCollections() {
        assertInvalidNull(ARRAY_CONTENT_NULL_FIRST,
                new TypeReference<List<Object>>() { });
        assertInvalidNull(ARRAY_CONTENT_NULL_LAST,
                new TypeReference<Set<Object>>() { });
        assertInvalidNull(STRING_CONTENT_NULL,
                new TypeReference<List<String>>() { });
        assertInvalidNull(STRING_CONTENT_NULL,
                new TypeReference<Set<String>>() { });
    }

    void testContentsNullFailForArrays() {
        assertInvalidNull(ARRAY_CONTENT_NULL_FIRST, Object[].class);
        assertInvalidNull(STRING_CONTENT_NULL, String[].class);
    }
private static ObjectMapper mapperWithFunnyNulls() {
        SimpleModule module = new SimpleModule("test", Version.unknownVersion());
        module.addDeserializer(String.class, new FunnyNullDeserializer());
        return VPackMapper.builder().addModule(module).build();
    }
private static void assertInvalidNull(byte[] input, Class<?> type) {
        InvalidNullException exception = assertThrows(InvalidNullException.class,
                () -> CONTENT_NULL_FAIL_MAPPER.readValue(input, type));
        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().contains("Invalid `null` value encountered"));
    }
private static void assertInvalidNull(byte[] input, TypeReference<?> type) {
        InvalidNullException exception = assertThrows(InvalidNullException.class,
                () -> CONTENT_NULL_FAIL_MAPPER.readValue(input, type));
        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().contains("Invalid `null` value encountered"));
    }
static class FunnyNullDeserializer extends ValueDeserializer<String> {
        @Override
        public String deserialize(tools.jackson.core.JsonParser parser,
                tools.jackson.databind.DeserializationContext ctxt) {
            return "text";
        }

        @Override
        public String getNullValue(tools.jackson.databind.DeserializationContext ctxt) {
            return "funny";
        }
    }
static class StringWrapper {
        public String str;
    }
enum EnumMapTestEnum {
        A, B, C
    }
static class RootData {
        public String name;
        public String type;

        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@type")
        @JsonSubTypes({
                @JsonSubTypes.Type(value = TypeA.class, name = "TypeA"),
                @JsonSubTypes.Type(value = TypeB.class, name = "TypeB")
        })
        public Proxy proxy;
    }
interface Proxy { }
static class TypeA implements Proxy {
        public String aValue;
    }
static class TypeB implements Proxy {
        public String bValue;
    }
static class FixedOrderAliasBean {
        @JsonAlias({"a", "b", "c"})
        public String value;
    }
static class AscendingOrderAliasBean {
        @JsonAlias({"a", "b", "c"})
        public String value;
    }
static class DescendingOrderAliasBean {
        @JsonAlias({"c", "b", "a"})
        public String value;
    }

    void __invoke_testNull() throws Exception {
        try {
            testNull();
        } finally {
        }
    }


    void __invoke_testCustomRootNulls() throws Exception {
        try {
            testCustomRootNulls();
        } finally {
        }
    }


    void __invoke_testListOfNulls() throws Exception {
        try {
            testListOfNulls();
        } finally {
        }
    }


    void __invoke_testMapOfNulls() throws Exception {
        try {
            testMapOfNulls();
        } finally {
        }
    }


    void __invoke_testPolymorphicDataNull() throws Exception {
        try {
            testPolymorphicDataNull();
        } finally {
        }
    }


    void __invoke_testContentsNullFailForMaps() throws Exception {
        try {
            testContentsNullFailForMaps();
        } finally {
        }
    }


    void __invoke_testContentsNullFailForCollections() throws Exception {
        try {
            testContentsNullFailForCollections();
        } finally {
        }
    }


    void __invoke_testContentsNullFailForArrays() throws Exception {
        try {
            testContentsNullFailForArrays();
        } finally {
        }
    }

}
