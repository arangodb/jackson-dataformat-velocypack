package tools.jackson.databind.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.type.CollectionType;
import tools.jackson.databind.type.MapType;
import tools.jackson.databind.type.SimpleType;
import tools.jackson.databind.type.TypeFactory;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0618Fixture {
private static final VPackMapper MAPPER = new VPackMapper();

    // Provenance: TypeFactoryTest#testMoreSpecificType().
    void moreSpecificTypeVpack() {
        TypeFactory types = MAPPER.getTypeFactory();
        JavaType collection = types.constructCollectionType(Collection.class, Object.class);
        JavaType list = types.constructCollectionType(List.class, Object.class);
        assertSame(list, types.moreSpecificType(collection, list));
        assertSame(list, types.moreSpecificType(list, collection));

        JavaType decimal = types.constructType(Double.class);
        JavaType number = types.constructType(Number.class);
        assertSame(decimal, types.moreSpecificType(decimal, number));
        assertSame(decimal, types.moreSpecificType(number, decimal));
        JavaType string = types.constructType(String.class);
        assertSame(decimal, types.moreSpecificType(decimal, string));
        assertSame(string, types.moreSpecificType(string, decimal));
    }

    // Provenance: TypeFactoryTest#testNestedTypes1604Simple().
    void nestedTypes1604SimpleVpack() throws Exception {
        List<Inner1604> values = List.of(new Inner1604(0), new Inner1604(1));
        BadOuter1604 input = new BadOuter1604(NestedData1604.of(values));
        byte[] wire = MAPPER.writeValueAsBytes(input);
        BadOuter1604 output = MAPPER.readValue(wire, BadOuter1604.class);
        assertIndexes(output.inner.getData(), 0, 1);
    }

    // Provenance: TypeFactoryTest#testNestedTypes1604Sneaky().
    void nestedTypes1604SneakyVpack() throws Exception {
        List<Inner1604> values = List.of(new Inner1604(2), new Inner1604(3));
        BadOuter1604 input = new BadOuter1604(NestedData1604.ofSneaky(values));
        byte[] wire = MAPPER.writeValueAsBytes(input);
        BadOuter1604 output = MAPPER.readValue(wire, BadOuter1604.class);
        assertIndexes(output.inner.getData(), 2, 3);
        JavaType subtype = MAPPER.getTypeFactory().constructSpecializedType(
                MAPPER.getTypeFactory().constructType(new TypeReference<NestedData1604<List<Inner1604>>>() { }),
                SneakyNestedDataList1604.class);
        assertSame(Object.class, subtype.containedType(0).getRawClass());
        assertSame(Inner1604.class, subtype.containedType(1).getRawClass());
    }

    // Provenance: TypeFactoryTest#testNestedTypes1604Subtype().
    void nestedTypes1604SubtypeVpack() throws Exception {
        List<Inner1604> values = List.of(new Inner1604(4), new Inner1604(5));
        BadOuter1604 input = new BadOuter1604(NestedData1604.ofRefined(values));
        byte[] wire = MAPPER.writeValueAsBytes(input);
        BadOuter1604 output = MAPPER.readValue(wire, BadOuter1604.class);
        assertIndexes(output.inner.getData(), 4, 5);
        JavaType subtype = MAPPER.getTypeFactory().constructSpecializedType(
                MAPPER.getTypeFactory().constructType(new TypeReference<NestedData1604<List<Inner1604>>>() { }),
                RefinedNestedDataList1604.class);
        assertSame(Inner1604.class, subtype.containedType(0).getRawClass());
    }

    // Provenance: TypeFactoryTest#testParameterizedClassType().
    void parameterizedClassTypeVpack() {
        JavaType type = MAPPER.getTypeFactory().constructType(
                new TypeReference<Class<? extends CharSequence>>() { });
        assertEquals(SimpleType.class, type.getClass());
        assertEquals(1, type.containedTypeCount());
        assertSame(CharSequence.class, type.containedType(0).getRawClass());
    }

    // Provenance: TypeFactoryTest#testParameterizedSimpleType().
    void parameterizedSimpleTypeVpack() {
        TypeFactory types = MAPPER.getTypeFactory();
        JavaType chars = types.constructType(new TypeReference<Class<? extends CharSequence>>() { });
        JavaType numbers = types.constructType(new TypeReference<Class<? extends Number>>() { });
        assertEquals(SimpleType.class, chars.getClass());
        assertEquals(SimpleType.class, numbers.getClass());
        assertNotEquals(chars, numbers);
        assertNotEquals(chars.hashCode(), numbers.hashCode());
    }

    // Provenance: TypeFactoryTest#testParametricTypes().
    void parametricTypesVpack() throws Exception {
        TypeFactory types = MAPPER.getTypeFactory();
        JavaType strings = types.constructParametricType(ArrayList.class, String.class);
        assertEquals(CollectionType.class, strings.getClass());
        assertEquals(1, strings.containedTypeCount());
        assertSame(String.class, strings.containedType(0).getRawClass());
        assertNull(strings.containedType(1));

        JavaType nestedMap = types.constructParametricType(Map.class,
                types.constructType(String.class), strings);
        assertEquals(MapType.class, nestedMap.getClass());
        assertSame(String.class, nestedMap.containedType(0).getRawClass());
        assertEquals(strings, nestedMap.containedType(1));
        assertNull(nestedMap.containedType(2));

        JavaType set = types.constructParametricType(HashSet.class, strings.getBindings());
        assertEquals(CollectionType.class, set.getClass());
        assertSame(String.class, set.containedType(0).getRawClass());

        JavaType custom = types.constructParametricType(SingleArgGeneric.class, String.class);
        assertEquals(SimpleType.class, custom.getClass());
        assertSame(String.class, custom.containedType(0).getRawClass());
        assertEquals(List.of("a", "b"), MAPPER.readValue(
                MAPPER.writeValueAsBytes(List.of("a", "b")), strings));
    }

    // Provenance: TypeFactoryTest#testProperties().
    void propertiesVpack() throws Exception {
        JavaType type = MAPPER.getTypeFactory().constructType(Properties.class);
        assertEquals(MapType.class, type.getClass());
        assertSame(Properties.class, type.getRawClass());
        MapType map = (MapType) type;
        assertSame(String.class, map.getKeyType().getRawClass());
        assertSame(String.class, map.getContentType().getRawClass());

        Properties input = new Properties();
        input.setProperty("language", "Java");
        Properties output = MAPPER.readValue(MAPPER.writeValueAsBytes(input), Properties.class);
        assertEquals("Java", output.getProperty("language"));
    }

    // Provenance: TypeFactoryTest#testRawCollections().
    void rawCollectionsVpack() {
        TypeFactory types = MAPPER.getTypeFactory();
        JavaType type = types.constructRawCollectionType(ArrayList.class);
        assertTrue(type.isContainerType());
        assertEquals(TypeFactory.unknownType(), type.getContentType());
        type = types.constructRawCollectionLikeType(CollectionLike.class);
        assertTrue(type.isCollectionLikeType());
        assertEquals(TypeFactory.unknownType(), type.getContentType());
        type = types.constructRawCollectionLikeType(String.class);
        assertTrue(type.isCollectionLikeType());
        assertEquals(TypeFactory.unknownType(), type.getContentType());
    }

    // Provenance: TypeFactoryTest#testRawMapType().
    void rawMapTypeVpack() throws Exception {
        TypeFactory types = MAPPER.getTypeFactory();
        JavaType type = types.constructParametricType(Wrapper1297.class, Map.class);
        assertNotNull(type);
        assertSame(Wrapper1297.class, type.getRawClass());
        Wrapper1297<Map<?, ?>> input = new Wrapper1297<>();
        input.content = Map.of("value", 7);
        Wrapper1297<Map<?, ?>> output = MAPPER.readValue(MAPPER.writeValueAsBytes(input),
                new TypeReference<Wrapper1297<Map<?, ?>>>() { });
        assertEquals(input.content, output.content);
    }

    // Provenance: TypeFactoryTest#testRawMaps().
    void rawMapsVpack() {
        TypeFactory types = MAPPER.getTypeFactory();
        JavaType type = types.constructRawMapType(java.util.HashMap.class);
        assertTrue(type.isContainerType());
        assertEquals(TypeFactory.unknownType(), type.getKeyType());
        assertEquals(TypeFactory.unknownType(), type.getContentType());

        type = types.constructRawMapLikeType(MapLike.class);
        assertTrue(type.isMapLikeType());
        assertEquals(TypeFactory.unknownType(), type.getKeyType());
        assertEquals(TypeFactory.unknownType(), type.getContentType());

        type = types.constructRawMapLikeType(String.class);
        assertTrue(type.isMapLikeType());
        assertEquals(TypeFactory.unknownType(), type.getKeyType());
        assertEquals(TypeFactory.unknownType(), type.getContentType());
    }

    // Provenance: TypeFactoryTest#testResolveGenericPartialSubtypes().
    void resolveGenericPartialSubtypesVpack() {
        TypeFactory types = MAPPER.getTypeFactory();
        JavaType base = types.constructType(new TypeReference<Either<Object, Object>>() { });
        JavaType left = types.constructSpecializedType(base, Left.class);
        assertSame(Left.class, left.getRawClass());
        JavaType[] params = types.findTypeParameters(left, Either.class);
        assertEquals(2, params.length);
        assertSame(Object.class, params[0].getRawClass());
        assertSame(Void.class, params[1].getRawClass());

        JavaType right = types.constructSpecializedType(base, Right.class);
        assertSame(Right.class, right.getRawClass());
        params = types.findTypeParameters(right, Either.class);
        assertEquals(2, params.length);
        assertSame(Void.class, params[0].getRawClass());
        assertSame(Object.class, params[1].getRawClass());
    }
private static void assertIndexes(List<Inner1604> values, int first, int second) {
        assertEquals(List.of(first, second), values.stream().map(Inner1604::getIndex).toList());
    }
static class SingleArgGeneric<T> { }
static class CollectionLike<T> { }
static class MapLike<K, V> { }
static class Wrapper1297<T> { public T content; }
static class Either<L, R> { }
static class Left<L> extends Either<L, Void> { }
static class Right<R> extends Either<Void, R> { }
public static class NestedData1604<T> {
        private final T data;
        @JsonCreator
        public NestedData1604(@JsonProperty("data") T value) { data = value; }
        public T getData() { return data; }
        public static <T> NestedData1604<List<T>> of(List<T> values) {
            return new NestedDataList1604<>(values);
        }
        public static <T> NestedData1604<List<T>> ofRefined(List<T> values) {
            return new RefinedNestedDataList1604<>(values);
        }
        public static <T> NestedData1604<List<T>> ofSneaky(List<T> values) {
            return new SneakyNestedDataList1604<String, T>(values);
        }
    }
public static class NestedDataList1604<T> extends NestedData1604<List<T>> {
        public NestedDataList1604(List<T> values) { super(values); }
    }
public static class RefinedNestedDataList1604<T> extends NestedDataList1604<T> {
        public RefinedNestedDataList1604(List<T> values) { super(values); }
    }
public static class SneakyNestedDataList1604<X, T> extends NestedData1604<List<T>> {
        public SneakyNestedDataList1604(List<T> values) { super(values); }
    }
public static class Inner1604 {
        private final int index;
        @JsonCreator
        public Inner1604(@JsonProperty("index") int value) { index = value; }
        public int getIndex() { return index; }
    }
public static class BadOuter1604 {
        private final NestedData1604<List<Inner1604>> inner;
        @JsonCreator
        public BadOuter1604(@JsonProperty("inner") NestedData1604<List<Inner1604>> value) { inner = value; }
        public NestedData1604<List<Inner1604>> getInner() { return inner; }
    }

    void __invoke_moreSpecificTypeVpack() throws Exception {
        try {
            moreSpecificTypeVpack();
        } finally {
        }
    }


    void __invoke_nestedTypes1604SimpleVpack() throws Exception {
        try {
            nestedTypes1604SimpleVpack();
        } finally {
        }
    }


    void __invoke_nestedTypes1604SneakyVpack() throws Exception {
        try {
            nestedTypes1604SneakyVpack();
        } finally {
        }
    }


    void __invoke_nestedTypes1604SubtypeVpack() throws Exception {
        try {
            nestedTypes1604SubtypeVpack();
        } finally {
        }
    }


    void __invoke_parameterizedClassTypeVpack() throws Exception {
        try {
            parameterizedClassTypeVpack();
        } finally {
        }
    }


    void __invoke_parameterizedSimpleTypeVpack() throws Exception {
        try {
            parameterizedSimpleTypeVpack();
        } finally {
        }
    }


    void __invoke_parametricTypesVpack() throws Exception {
        try {
            parametricTypesVpack();
        } finally {
        }
    }


    void __invoke_propertiesVpack() throws Exception {
        try {
            propertiesVpack();
        } finally {
        }
    }


    void __invoke_rawCollectionsVpack() throws Exception {
        try {
            rawCollectionsVpack();
        } finally {
        }
    }


    void __invoke_rawMapTypeVpack() throws Exception {
        try {
            rawMapTypeVpack();
        } finally {
        }
    }


    void __invoke_rawMapsVpack() throws Exception {
        try {
            rawMapsVpack();
        } finally {
        }
    }


    void __invoke_resolveGenericPartialSubtypesVpack() throws Exception {
        try {
            resolveGenericPartialSubtypesVpack();
        } finally {
        }
    }

}
