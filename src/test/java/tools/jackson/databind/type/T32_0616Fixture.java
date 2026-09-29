package tools.jackson.databind.type;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicReference;

import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.type.CollectionType;
import tools.jackson.databind.type.SimpleType;
import tools.jackson.databind.type.TypeFactory;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0616Fixture {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();

    // Provenance: TypeFactoryTest#testAtomicArrayRefParameters().
    void atomicArrayRefParametersVpack() {
        TypeFactory types = MAPPER.getTypeFactory();
        JavaType reference = types.constructType(new TypeReference<AtomicReference<long[]>>() { });
        JavaType[] parameters = types.findTypeParameters(reference, AtomicReference.class);
        assertNotNull(parameters);
        assertEquals(1, parameters.length);
        assertEquals(types.constructType(long[].class), parameters[0]);
    }

    // Provenance: TypeFactoryTest#testBasePropertiesIncludedWhenSerializingSubWhenSubTypeLoadedAfterBaseType().
    void basePropertiesIncludedWhenSubtypeLoadedAfterBaseVpack() throws Exception {
        TypeFactory types = MAPPER.getTypeFactory();
        types.constructType(BaseProperty.class);
        types.constructType(SubProperty.class);

        // Independently derived sorted-index object fixture. The body remains in
        // serializer call order: base, then sub.
        assertArrayEquals(new byte[] {
                0x0b, 0x10, 0x02,
                0x44, 'b', 'a', 's', 'e', 0x31,
                0x43, 's', 'u', 'b', 0x32,
                0x03, 0x09
        }, MAPPER.writeValueAsBytes(new SubProperty()));
    }

    // Provenance: TypeFactoryTest#testCacheClearing().
    void cacheClearingRetainsConstructionSemanticsVpack() {
        InspectableTypeFactory types = new InspectableTypeFactory();
        JavaType before = types.constructType(CacheSample.class);
        assertNotNull(before);
        assertTrue(types.cacheSize() > 0);
        types.clearCache();
        assertEquals(0, types.cacheSize());
        JavaType after = types.constructType(CacheSample.class);
        assertEquals(before, after);
        assertTrue(types.cacheSize() > 0);
        assertSame(CacheSample.class, after.getRawClass());
    }

    // Provenance: TypeFactoryTest#testCanonicalNames().
    void canonicalNamesVpack() {
        TypeFactory types = MAPPER.getTypeFactory();
        assertCanonicalRoundTrip(types, Calendar.class);
        assertEquals("java.util.Calendar", types.constructType(Calendar.class).toCanonical());
        assertEquals("java.util.ArrayList<java.lang.Object>",
                types.constructType(ArrayList.class).toCanonical());
        assertCanonicalRoundTrip(types, ArrayList.class);
        assertEquals("java.util.TreeMap<java.lang.Object,java.lang.Object>",
                types.constructType(TreeMap.class).toCanonical());
        assertCanonicalRoundTrip(types, TreeMap.class);

        JavaType enumMap = types.constructMapType(EnumMap.class, CanonicalEnum.class, String.class);
        assertEquals("java.util.EnumMap<tools.jackson.databind.type.T32_0616Fixture$CanonicalEnum,java.lang.String>",
                enumMap.toCanonical());
        assertEquals(CanonicalEnum.class, enumMap.getKeyType().getRawClass());
        assertEquals(String.class, enumMap.getContentType().getRawClass());
        assertEquals(enumMap, types.constructFromCanonical(enumMap.toCanonical()));

        JavaType atomic = types.constructType(new TypeReference<AtomicReference<Long>>() { });
        assertTrue(atomic.isReferenceType());
        assertEquals("java.util.concurrent.atomic.AtomicReference<java.lang.Long>", atomic.toCanonical());
        assertEquals(atomic, types.constructFromCanonical(atomic.toCanonical()));

        JavaType rawList = types.constructFromCanonical("java.util.List");
        assertSame(List.class, rawList.getRawClass());
        assertEquals(CollectionType.class, rawList.getClass());
        assertSame(Object.class, rawList.getContentType().getRawClass());
        assertEquals("java.util.List<java.lang.Object>", rawList.toCanonical());
        assertEquals(rawList, types.constructFromCanonical(rawList.toCanonical()));
    }

    // Provenance: TypeFactoryTest#testCanonicalWithCustomCollection().
    void canonicalWithCustomCollectionVpack() {
        TypeFactory types = MAPPER.getTypeFactory();
        JavaType parsed = types.constructFromCanonical(types.constructType(StringList.class).toCanonical());
        assertSame(StringList.class, parsed.getRawClass());
        assertTrue(parsed.isCollectionLikeType());
        assertEquals(String.class, parsed.getContentType().getRawClass());
    }

    // Provenance: TypeFactoryTest#testCanonicalWithCustomGenericType().
    void canonicalWithCustomGenericTypeVpack() {
        TypeFactory types = MAPPER.getTypeFactory();
        JavaType parsed = types.constructFromCanonical(types.constructType(ConcreteGeneric.class).toCanonical());
        assertSame(ConcreteGeneric.class, parsed.getRawClass());
        assertEquals(Integer.class, parsed.getSuperClass().containedType(0).getRawClass());
    }

    // Provenance: TypeFactoryTest#testCanonicalWithCustomMap().
    void canonicalWithCustomMapVpack() {
        TypeFactory types = MAPPER.getTypeFactory();
        JavaType parsed = types.constructFromCanonical(types.constructType(StringStringMap.class).toCanonical());
        assertSame(StringStringMap.class, parsed.getRawClass());
        assertTrue(parsed.isMapLikeType());
        assertEquals(String.class, parsed.getKeyType().getRawClass());
        assertEquals(String.class, parsed.getContentType().getRawClass());
    }

    // Provenance: TypeFactoryTest#testCanonicalWithSpaces().
    void canonicalWithSpacesVpack() {
        Object value = new TreeMap<Object, Object>() { };
        String reflected = value.getClass().getGenericSuperclass().toString();
        JavaType expected = MAPPER.getTypeFactory().constructType(value.getClass().getGenericSuperclass());
        JavaType parsed = MAPPER.getTypeFactory().constructFromCanonical(reflected);
        assertNotNull(parsed);
        assertEquals(expected, parsed);
    }

    // Provenance: TypeFactoryTest#testCollectionTypesRefined().
    void collectionTypesRefinedVpack() {
        TypeFactory types = MAPPER.getTypeFactory();
        JavaType base = types.constructType(new TypeReference<List<Long>>() { });
        assertSame(List.class, base.getRawClass());
        assertSame(Long.class, base.getContentType().getRawClass());
        assertNull(base.getSuperClass());
        assertTypeEqualsAndHash(base, types.constructType(new TypeReference<List<Long>>() { }));

        JavaType subtype = types.constructSpecializedType(base, ArrayList.class);
        assertSame(ArrayList.class, subtype.getRawClass());
        assertSame(Long.class, subtype.getContentType().getRawClass());
        assertTypeEqualsAndHash(subtype, types.constructSpecializedType(base, ArrayList.class));
        assertSame(java.util.AbstractList.class, subtype.getSuperClass().getRawClass());
    }

    // Provenance: TypeFactoryTest#testCollections().
    void collectionsVpack() {
        TypeFactory types = MAPPER.getTypeFactory();
        JavaType raw = types.constructType(ArrayList.class);
        assertEquals(CollectionType.class, raw.getClass());
        assertSame(ArrayList.class, raw.getRawClass());
        assertSame(Object.class, raw.getContentType().getRawClass());
        assertTypeEqualsAndHash(raw, types.constructType(ArrayList.class));

        JavaType typed = types.constructType(new TypeReference<ArrayList<String>>() { });
        assertEquals(CollectionType.class, typed.getClass());
        assertSame(ArrayList.class, typed.getRawClass());
        assertNotNull(typed.getContentType());
        assertEquals(SimpleType.class, typed.getContentType().getClass());
        assertSame(String.class, typed.getContentType().getRawClass());
        assertTypeEqualsAndHash(typed, types.constructType(new TypeReference<ArrayList<String>>() { }));

        JavaType explicit = types.constructCollectionType(ArrayList.class, String.class);
        assertEquals(CollectionType.class, explicit.getClass());
        assertSame(String.class, explicit.getContentType().getRawClass());
        assertTypeEqualsAndHash(explicit, types.constructCollectionType(ArrayList.class, String.class));
    }

    // Provenance: TypeFactoryTest#testCollectionsHashCode().
    void collectionsHashCodeVpack() {
        TypeFactory types = MAPPER.getTypeFactory();
        JavaType listOfCollection = types.constructType(new TypeReference<List<Collection>>() { });
        JavaType collectionOfList = types.constructType(new TypeReference<Collection<List>>() { });
        assertNotEquals(listOfCollection, collectionOfList);
        assertNotEquals(listOfCollection.hashCode(), collectionOfList.hashCode());
    }

    // Provenance: TypeFactoryTest#testCustomTypesRefinedNested().
    void customTypesRefinedNestedVpack() {
        TypeFactory types = MAPPER.getTypeFactory();
        JavaType base = types.constructType(new TypeReference<GenericData<List<Long>>>() { });
        assertSame(GenericData.class, base.getRawClass());

        JavaType subtype = types.constructSpecializedType(base, RefinedDataList.class);
        assertSame(RefinedDataList.class, subtype.getRawClass());
        assertSame(DataList.class, subtype.getSuperClass().getRawClass());
        assertEquals(1, subtype.containedTypeCount());
        assertSame(Long.class, subtype.containedType(0).getRawClass());
    }
private static void assertCanonicalRoundTrip(TypeFactory types, Class<?> rawClass) {
        JavaType type = types.constructType(rawClass);
        JavaType parsed = types.constructFromCanonical(type.toCanonical());
        assertEquals(type, parsed);
        assertSame(rawClass, parsed.getRawClass());
    }
private static void assertTypeEqualsAndHash(JavaType expected, JavaType actual) {
        assertEquals(expected, actual);
        assertEquals(expected.hashCode(), actual.hashCode());
    }
enum CanonicalEnum { ONE, TWO }
static class CacheSample { }
static class InspectableTypeFactory extends TypeFactory {
        int cacheSize() { return _typeCache.size(); }
    }
static class StringList extends ArrayList<String> { private static final long serialVersionUID = 1L; }
static class StringStringMap extends HashMap<String, String> { private static final long serialVersionUID = 1L; }
static class GenericBase<T> { }
static class ConcreteGeneric extends GenericBase<Integer> { }
static class GenericData<T> { }
static class DataList<T> extends GenericData<List<T>> { }
static class RefinedDataList<T> extends DataList<T> { }
static class BaseProperty {
        @JsonProperty int base = 1;
    }
static class SubProperty extends BaseProperty {
        @JsonProperty int sub = 2;
    }

    void __invoke_atomicArrayRefParametersVpack() throws Exception {
        try {
            atomicArrayRefParametersVpack();
        } finally {
        }
    }


    void __invoke_basePropertiesIncludedWhenSubtypeLoadedAfterBaseVpack() throws Exception {
        try {
            basePropertiesIncludedWhenSubtypeLoadedAfterBaseVpack();
        } finally {
        }
    }


    void __invoke_cacheClearingRetainsConstructionSemanticsVpack() throws Exception {
        try {
            cacheClearingRetainsConstructionSemanticsVpack();
        } finally {
        }
    }


    void __invoke_canonicalNamesVpack() throws Exception {
        try {
            canonicalNamesVpack();
        } finally {
        }
    }


    void __invoke_canonicalWithCustomCollectionVpack() throws Exception {
        try {
            canonicalWithCustomCollectionVpack();
        } finally {
        }
    }


    void __invoke_canonicalWithCustomGenericTypeVpack() throws Exception {
        try {
            canonicalWithCustomGenericTypeVpack();
        } finally {
        }
    }


    void __invoke_canonicalWithCustomMapVpack() throws Exception {
        try {
            canonicalWithCustomMapVpack();
        } finally {
        }
    }


    void __invoke_canonicalWithSpacesVpack() throws Exception {
        try {
            canonicalWithSpacesVpack();
        } finally {
        }
    }


    void __invoke_collectionTypesRefinedVpack() throws Exception {
        try {
            collectionTypesRefinedVpack();
        } finally {
        }
    }


    void __invoke_collectionsVpack() throws Exception {
        try {
            collectionsVpack();
        } finally {
        }
    }


    void __invoke_collectionsHashCodeVpack() throws Exception {
        try {
            collectionsHashCodeVpack();
        } finally {
        }
    }


    void __invoke_customTypesRefinedNestedVpack() throws Exception {
        try {
            customTypesRefinedNestedVpack();
        } finally {
        }
    }

}
