package tools.jackson.databind.type;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.type.IterationType;
import tools.jackson.databind.type.MapType;
import tools.jackson.databind.type.TypeFactory;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0617Fixture {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();

    // Provenance: TypeFactoryTest#testCustomTypesRefinedSimple().
    void customTypesRefinedSimpleVpack() {
        TypeFactory types = MAPPER.getTypeFactory();
        JavaType base = types.constructType(new TypeReference<Data1604<List<Long>>>() { });
        assertSame(Data1604.class, base.getRawClass());
        assertEquals(1, base.containedTypeCount());
        assertSame(List.class, base.containedType(0).getRawClass());

        JavaType subtype = types.constructSpecializedType(base, DataList1604.class);
        assertSame(DataList1604.class, subtype.getRawClass());
        assertEquals(1, subtype.containedTypeCount());
        assertSame(Long.class, subtype.containedType(0).getRawClass());
    }

    // Provenance: TypeFactoryTest#testCustomTypesRefinedSneaky().
    void customTypesRefinedSneakyVpack() {
        TypeFactory types = MAPPER.getTypeFactory();
        JavaType base = types.constructType(new TypeReference<Data1604<List<Long>>>() { });
        JavaType subtype = types.constructSpecializedType(base, SneakyDataList1604.class);
        assertSame(SneakyDataList1604.class, subtype.getRawClass());
        assertEquals(2, subtype.containedTypeCount());
        assertSame(Object.class, subtype.containedType(0).getRawClass());
        assertSame(Long.class, subtype.containedType(1).getRawClass());
        assertSame(Data1604.class, subtype.getSuperClass().getRawClass());
    }

    // Provenance: TypeFactoryTest#testErrorForMismatch1604().
    void errorForMismatch1604Vpack() {
        TypeFactory types = MAPPER.getTypeFactory();
        JavaType base = types.constructType(new TypeReference<Data1604<String>>() { });
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> types.constructSpecializedType(base, DataList1604.class));
        assertTrue(error.getMessage().contains("Failed to specialize"));
        assertTrue(error.getMessage().contains("Data1604"));
        assertTrue(error.getMessage().contains("DataList1604"));
    }

    // Provenance: TypeFactoryTest#testIterator().
    void iteratorVpack() {
        JavaType type = MAPPER.getTypeFactory().constructType(new TypeReference<Iterator<String>>() { });
        assertEquals(IterationType.class, type.getClass());
        assertTrue(type.isIterationType());
        assertSame(Iterator.class, type.getRawClass());
        assertEquals(1, type.containedTypeCount());
        assertEquals(MAPPER.getTypeFactory().constructType(String.class), type.containedType(0));
        assertNull(type.containedType(1));
    }

    // Provenance: TypeFactoryTest#testMalicousCanonical().
    void maliciousCanonicalVpack() {
        TypeFactory types = MAPPER.getTypeFactory();
        StringBuilder deep = new StringBuilder();
        for (int i = 0; i < 1100; ++i) {
            deep.append("java.util.List<");
        }
        deep.append("java.lang.String");
        deep.append(">".repeat(1100));
        IllegalArgumentException nesting = assertThrows(IllegalArgumentException.class,
                () -> types.constructFromCanonical(deep.toString()));
        assertTrue(nesting.getMessage().contains("too deeply nested"));

        StringBuilder longName = new StringBuilder("java.util.List<");
        while (longName.length() <= 64_000) {
            longName.append("java.lang.String,");
        }
        longName.append("java.lang.Integer>");
        IllegalArgumentException length = assertThrows(IllegalArgumentException.class,
                () -> types.constructFromCanonical(longName.toString()));
        assertTrue(length.getMessage().contains("too long"));
    }

    // Provenance: TypeFactoryTest#testMapEntryResolution().
    void mapEntryResolutionVpack() {
        JavaType type = MAPPER.getTypeFactory().constructType(StringIntMapEntry.class);
        JavaType entry = type.findSuperType(Map.Entry.class);
        assertNotNull(entry);
        assertTrue(entry.hasGenericTypes());
        assertEquals(2, entry.containedTypeCount());
        assertSame(String.class, entry.containedType(0).getRawClass());
        assertSame(Integer.class, entry.containedType(1).getRawClass());
    }

    // Provenance: TypeFactoryTest#testMapTypesAdvanced().
    void mapTypesAdvancedVpack() {
        TypeFactory types = MAPPER.getTypeFactory();
        JavaType type = types.constructType(MyMap.class);
        assertEquals(MapType.class, type.getClass());
        MapType map = (MapType) type;
        assertSame(String.class, map.getKeyType().getRawClass());
        assertSame(Long.class, map.getContentType().getRawClass());

        type = types.constructType(MapInterface.class);
        assertEquals(MapType.class, type.getClass());
        map = (MapType) type;
        assertSame(String.class, map.getKeyType().getRawClass());
        assertSame(Integer.class, map.getContentType().getRawClass());

        type = types.constructType(MyStringIntMap.class);
        assertEquals(MapType.class, type.getClass());
        map = (MapType) type;
        assertSame(String.class, map.getKeyType().getRawClass());
        assertSame(Integer.class, map.getContentType().getRawClass());
    }

    // Provenance: TypeFactoryTest#testMapTypesRaw().
    void mapTypesRawVpack() {
        TypeFactory types = MAPPER.getTypeFactory();
        JavaType type = types.constructType(HashMap.class);
        assertEquals(MapType.class, type.getClass());
        MapType map = (MapType) type;
        assertEquals(types.constructType(Object.class), map.getKeyType());
        assertEquals(types.constructType(Object.class), map.getContentType());
    }

    // Provenance: TypeFactoryTest#testMapTypesRefined().
    void mapTypesRefinedVpack() {
        TypeFactory types = MAPPER.getTypeFactory();
        JavaType type = types.constructType(new TypeReference<Map<String,List<Integer>>>() { });
        assertEquals(MapType.class, type.getClass());
        MapType map = (MapType) type;
        assertSame(Map.class, map.getRawClass());
        assertSame(String.class, map.getKeyType().getRawClass());
        assertSame(List.class, map.getContentType().getRawClass());
        assertSame(Integer.class, map.getContentType().getContentType().getRawClass());
        assertNull(type.getSuperClass());

        MapType subtype = (MapType) types.constructSpecializedType(type, LinkedHashMap.class);
        assertSame(LinkedHashMap.class, subtype.getRawClass());
        assertSame(String.class, subtype.getKeyType().getRawClass());
        assertSame(Integer.class, subtype.getContentType().getContentType().getRawClass());
        MapType parent = (MapType) subtype.getSuperClass();
        assertSame(HashMap.class, parent.getRawClass());
        assertSame(String.class, parent.getKeyType().getRawClass());
        assertSame(Integer.class, parent.getContentType().getContentType().getRawClass());
        assertTypeEqualsAndHash(type, types.constructType(new TypeReference<Map<String,List<Integer>>>() { }));
        assertTypeEqualsAndHash(subtype, types.constructSpecializedType(type, LinkedHashMap.class));
    }

    // Provenance: TypeFactoryTest#testMapTypesSneaky().
    void mapTypesSneakyVpack() {
        JavaType type = MAPPER.getTypeFactory().constructType(IntLongMap.class);
        assertEquals(MapType.class, type.getClass());
        MapType map = (MapType) type;
        assertSame(Integer.class, map.getKeyType().getRawClass());
        assertSame(Long.class, map.getContentType().getRawClass());
    }

    // Provenance: TypeFactoryTest#testMaps().
    void mapsVpack() {
        TypeFactory types = MAPPER.getTypeFactory();
        JavaType raw = types.constructType(HashMap.class);
        assertEquals(MapType.class, raw.getClass());
        assertSame(HashMap.class, raw.getRawClass());
        assertTypeEqualsAndHash(raw, types.constructType(HashMap.class));

        MapType explicit = types.constructMapType(java.util.TreeMap.class, String.class, Integer.class);
        assertEquals(MapType.class, explicit.getClass());
        assertSame(String.class, explicit.getKeyType().getRawClass());
        assertSame(Integer.class, explicit.getContentType().getRawClass());
        assertTypeEqualsAndHash(explicit, types.constructMapType(java.util.TreeMap.class,
                String.class, Integer.class));

        MapType typed = (MapType) types.constructType(new TypeReference<HashMap<String,Integer>>() { });
        assertEquals(MapType.class, typed.getClass());
        assertSame(HashMap.class, typed.getRawClass());
        assertEquals(types.constructType(String.class), typed.getKeyType());
        assertEquals(types.constructType(Integer.class), typed.getContentType());
        assertTypeEqualsAndHash(typed, types.constructType(new TypeReference<HashMap<String,Integer>>() { }));

        MapType custom = (MapType) types.constructType(new TypeReference<LongValuedMap<Boolean>>() { });
        assertEquals(MapType.class, custom.getClass());
        assertSame(LongValuedMap.class, custom.getRawClass());
        assertEquals(types.constructType(Boolean.class), custom.getKeyType());
        assertEquals(types.constructType(Long.class), custom.getContentType());
        assertTypeEqualsAndHash(custom, types.constructType(new TypeReference<LongValuedMap<Boolean>>() { }));

        MapType standard = (MapType) types.constructType(new TypeReference<Map<String,Boolean>>() { });
        assertEquals(MapType.class, standard.getClass());
        assertEquals(types.constructType(String.class), standard.getKeyType());
        assertEquals(types.constructType(Boolean.class), standard.getContentType());
        assertTypeEqualsAndHash(standard, types.constructType(new TypeReference<Map<String,Boolean>>() { }));
    }

    // Provenance: TypeFactoryTest#testMapsHashCode().
    void mapsHashCodeVpack() {
        TypeFactory types = MAPPER.getTypeFactory();
        JavaType stringInteger = types.constructType(new TypeReference<Map<String,Integer>>() { });
        JavaType integerString = types.constructType(new TypeReference<Map<Integer,String>>() { });
        assertNotEquals(stringInteger, integerString);
        assertNotEquals(stringInteger.hashCode(), integerString.hashCode());
        JavaType stringString = types.constructType(new TypeReference<Map<String,String>>() { });
        JavaType integerInteger = types.constructType(new TypeReference<Map<Integer,Integer>>() { });
        assertNotEquals(stringString, integerInteger);
        assertNotEquals(stringString.hashCode(), integerInteger.hashCode());
    }
private static void assertTypeEqualsAndHash(JavaType expected, JavaType actual) {
        assertEquals(expected, actual);
        assertEquals(expected.hashCode(), actual.hashCode());
    }
static class Data1604<T> { }
static class DataList1604<T> extends Data1604<List<T>> { }
static class SneakyDataList1604<BOGUS,T> extends Data1604<List<T>> { }
static abstract class StringIntMapEntry implements Map.Entry<String,Integer> { }
static abstract class IntermediateMap<K,V> implements Map<K,V> { }
static abstract class MyMap extends IntermediateMap<String,Long> { }
interface IntermediateInterfaceMap<K> extends Map<K,Integer> { }
interface MapInterface extends IntermediateInterfaceMap<String> { }
static class MyStringXMap<V> extends HashMap<String,V> { private static final long serialVersionUID = 1L; }
static class MyStringIntMap extends MyStringXMap<Integer> { private static final long serialVersionUID = 1L; }
static abstract class XXMap<K,V> implements Map<K,V> { }
static abstract class XLongMap<K> extends XXMap<K,Long> { }
static abstract class IntLongMap extends XLongMap<Integer> { }
static class LongValuedMap<K> extends HashMap<K,Long> { private static final long serialVersionUID = 1L; }

    void __invoke_customTypesRefinedSimpleVpack() throws Exception {
        try {
            customTypesRefinedSimpleVpack();
        } finally {
        }
    }


    void __invoke_customTypesRefinedSneakyVpack() throws Exception {
        try {
            customTypesRefinedSneakyVpack();
        } finally {
        }
    }


    void __invoke_errorForMismatch1604Vpack() throws Exception {
        try {
            errorForMismatch1604Vpack();
        } finally {
        }
    }


    void __invoke_iteratorVpack() throws Exception {
        try {
            iteratorVpack();
        } finally {
        }
    }


    void __invoke_maliciousCanonicalVpack() throws Exception {
        try {
            maliciousCanonicalVpack();
        } finally {
        }
    }


    void __invoke_mapEntryResolutionVpack() throws Exception {
        try {
            mapEntryResolutionVpack();
        } finally {
        }
    }


    void __invoke_mapTypesAdvancedVpack() throws Exception {
        try {
            mapTypesAdvancedVpack();
        } finally {
        }
    }


    void __invoke_mapTypesRawVpack() throws Exception {
        try {
            mapTypesRawVpack();
        } finally {
        }
    }


    void __invoke_mapTypesRefinedVpack() throws Exception {
        try {
            mapTypesRefinedVpack();
        } finally {
        }
    }


    void __invoke_mapTypesSneakyVpack() throws Exception {
        try {
            mapTypesSneakyVpack();
        } finally {
        }
    }


    void __invoke_mapsVpack() throws Exception {
        try {
            mapsVpack();
        } finally {
        }
    }


    void __invoke_mapsHashCodeVpack() throws Exception {
        try {
            mapsHashCodeVpack();
        } finally {
        }
    }

}
