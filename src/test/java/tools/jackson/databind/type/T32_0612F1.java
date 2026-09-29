package tools.jackson.databind.type;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.type.CollectionType;
import tools.jackson.databind.type.MapType;
import tools.jackson.databind.util.SimpleLookupCache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import tools.jackson.dataformat.velocypack.*;

class T32_0612F1 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] FILTERED_JSON_VALUE = VPackWireFixtureTest.hex(
            "0b 0e 01 47 70 72 65 73 65 6e 74 41 78 03");

    // Provenance: ContainerTypesTest#testExplicitCollectionType().
    void explicitCollectionTypeVpack() {
        JavaType type = MAPPER.getTypeFactory().constructCollectionType(LongList.class, Long.class);
        assertEquals(LongList.class, type.getRawClass());
        assertEquals(Long.class, type.getContentType().getRawClass());
    }

    // Provenance: ContainerTypesTest#testExplicitMapType().
    void explicitMapTypeVpack() {
        JavaType type = MAPPER.getTypeFactory().constructMapType(
                StringLongMap.class, String.class, Long.class);
        assertEquals(StringLongMap.class, type.getRawClass());
        assertEquals(String.class, type.getKeyType().getRawClass());
        assertEquals(Long.class, type.getContentType().getRawClass());
    }

    // Provenance: ContainerTypesTest#testImplicitCollectionType().
    void implicitCollectionTypeVpack() {
        JavaType type = MAPPER.getTypeFactory().constructParametricType(List.class, Long.class);
        assertTrue(type instanceof CollectionType);
        assertEquals(List.class, type.getRawClass());
        assertEquals(Long.class, type.getContentType().getRawClass());
    }

    // Provenance: ContainerTypesTest#testImplicitMapType().
    void implicitMapTypeVpack() {
        JavaType type = MAPPER.getTypeFactory().constructParametricType(
                Map.class, Long.class, Boolean.class);
        assertTrue(type instanceof MapType);
        assertEquals(Long.class, type.getKeyType().getRawClass());
        assertEquals(Boolean.class, type.getContentType().getRawClass());
    }

    // Provenance: ContainerTypesTest#testMissingCollectionType().
    void missingCollectionTypeVpack() {
        JavaType type = MAPPER.getTypeFactory().withCache(new SimpleLookupCache<Object, JavaType>(4, 8))
                .constructParametricType(List.class, HashMap.class);
        assertTrue(type instanceof CollectionType);
        assertEquals(List.class, type.getRawClass());
        assertEquals(HashMap.class, type.getContentType().getRawClass());
    }

    // Provenance: ContainerTypesTest#testMismatchedCollectionType().
    void mismatchedCollectionTypeVpack() {
        try {
            MAPPER.getTypeFactory().constructCollectionType(LongList.class, String.class);
            fail("Should not pass");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("element type"), e.getMessage());
        }
    }

    // Provenance: ContainerTypesTest#testMismatchedMapType().
    void mismatchedMapTypeVpack() {
        try {
            MAPPER.getTypeFactory().constructMapType(StringLongMap.class,
                    Boolean.class, Long.class);
            fail("Should not pass for mismatched key type");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("key type"), e.getMessage());
        }
        try {
            MAPPER.getTypeFactory().constructMapType(StringLongMap.class,
                    String.class, HashSet.class);
            fail("Should not pass for mismatched value type");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("value type"), e.getMessage());
        }
    }
static class JsonValueWithInclude {
        @JsonValue
        @JsonInclude(value = JsonInclude.Include.NON_NULL,
                content = JsonInclude.Include.NON_NULL)
        public final Map<String, Object> value;

        JsonValueWithInclude(Map<String, Object> value) { this.value = value; }
    }
@SuppressWarnings("unused")
    static class FieldBean {
        public static boolean DUMMY;
        private long bar;
        @JsonProperty private String props;
    }
static class Bean1005 {
        Bean1005(int ignored) { }
    }
static abstract class LongList implements List<Long> { }
static abstract class StringLongMap implements Map<String, Long> { }

    void __invoke_explicitCollectionTypeVpack() throws Exception {
        try {
            explicitCollectionTypeVpack();
        } finally {
        }
    }


    void __invoke_explicitMapTypeVpack() throws Exception {
        try {
            explicitMapTypeVpack();
        } finally {
        }
    }


    void __invoke_implicitCollectionTypeVpack() throws Exception {
        try {
            implicitCollectionTypeVpack();
        } finally {
        }
    }


    void __invoke_implicitMapTypeVpack() throws Exception {
        try {
            implicitMapTypeVpack();
        } finally {
        }
    }


    void __invoke_missingCollectionTypeVpack() throws Exception {
        try {
            missingCollectionTypeVpack();
        } finally {
        }
    }


    void __invoke_mismatchedCollectionTypeVpack() throws Exception {
        try {
            mismatchedCollectionTypeVpack();
        } finally {
        }
    }


    void __invoke_mismatchedMapTypeVpack() throws Exception {
        try {
            mismatchedMapTypeVpack();
        } finally {
        }
    }

}
