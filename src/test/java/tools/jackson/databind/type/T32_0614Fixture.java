package tools.jackson.databind.type;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.type.ClassKey;
import tools.jackson.databind.type.IterationType;
import tools.jackson.databind.type.TypeBindings;
import tools.jackson.databind.type.TypeFactory;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0614Fixture {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();

    // Provenance: JavaTypeTest#testLocalType728().
    void localType728Vpack() throws Exception {
        TypeFactory types = MAPPER.getTypeFactory();
        Method method = GenericCharSequence.class.getMethod("method", CharSequence.class);
        assertNotNull(method);
        assertEquals(CharSequence.class, types.constructType(method.getReturnType()).getRawClass());
        assertEquals(CharSequence.class, types.constructType(method.getGenericReturnType()).getRawClass());
        assertEquals(CharSequence.class, types.constructType(method.getParameterTypes()[0]).getRawClass());
        assertEquals(CharSequence.class, types.constructType(method.getGenericParameterTypes()[0]).getRawClass());
    }

    // Provenance: JavaTypeTest#testSimpleClass().
    void simpleClassVpack() {
        JavaType type = MAPPER.constructType(BaseType.class);
        assertSame(BaseType.class, type.getRawClass());
        assertTrue(type.hasRawClass(BaseType.class));
        assertFalse(type.isTypeOrSubTypeOf(SubType.class));
        assertFalse(type.isArrayType());
        assertFalse(type.isContainerType());
        assertFalse(type.isEnumType());
        assertFalse(type.isInterface());
        assertFalse(type.isIterationType());
        assertFalse(type.isPrimitive());
        assertFalse(type.isReferenceType());
        assertFalse(type.hasContentType());
        assertNull(type.getContentType());
        assertNull(type.getKeyType());
        assertNull(type.getValueHandler());
        assertEquals("Ltools/jackson/databind/type/T32_0614Fixture$BaseType;",
                type.getGenericSignature());
        assertEquals(type.getGenericSignature(), type.getErasedSignature());
    }

    // Provenance: JavaTypeTest#testMapType().
    void mapTypeVpack() {
        JavaType type = MAPPER.constructType(HashMap.class);
        assertTrue(type.isContainerType());
        assertFalse(type.isIterationType());
        assertFalse(type.isReferenceType());
        assertTrue(type.hasContentType());
        assertNotNull(type.toString());
        assertNotNull(type.getContentType());
        assertNotNull(type.getKeyType());
        assertEquals("Ljava/util/HashMap<Ljava/lang/Object;Ljava/lang/Object;>;", type.getGenericSignature());
        assertEquals("Ljava/util/HashMap;", type.getErasedSignature());
        assertTrue(type.equals(type));
        assertFalse(type.equals(null));
        assertFalse(type.equals("xyz"));
    }

    // Provenance: JavaTypeTest#testEnumType().
    void enumTypeVpack() {
        TypeFactory types = MAPPER.getTypeFactory();
        JavaType type = MAPPER.constructType(MyEnum.class);
        assertTrue(type.getRawClass().isEnum());
        assertTrue(type.isEnumType());
        assertTrue(type.isEnumImplType());
        assertFalse(type.isIterationType());
        assertFalse(type.hasHandlers());
        assertTrue(type.isTypeOrSubTypeOf(MyEnum.class));
        assertTrue(type.isTypeOrSubTypeOf(Object.class));
        assertNull(type.containedType(3));
        assertTrue(type.containedTypeOrUnknown(3).isJavaLangObject());
        assertEquals("Ltools/jackson/databind/type/T32_0614Fixture$MyEnum;",
                type.getGenericSignature());
        assertEquals(type.getGenericSignature(), type.getErasedSignature());
        assertTrue(types.constructType(MyEnum2.class).isEnumType());
        assertTrue(types.constructType(MyEnum.A.getClass()).isEnumType());
        assertTrue(types.constructType(MyEnum2.A.getClass()).isEnumType());
        assertFalse(types.constructType(Enum.class).isEnumImplType());
        JavaType enumSubType = types.constructType(MyEnumSub.B.getClass());
        assertTrue(enumSubType.isEnumType());
        assertTrue(enumSubType.isEnumImplType());
        assertFalse(enumSubType.getRawClass().isEnum());
    }

    // Provenance: JavaTypeTest#testClassKey().
    void classKeyVpack() {
        ClassKey key = new ClassKey(String.class);
        ClassKey sameKey = key;
        assertEquals(0, key.compareTo(sameKey));
        assertTrue(key.equals(key));
        assertFalse(key.equals(null));
        assertFalse(key.equals("foo"));
        assertFalse(key.equals(new ClassKey(Integer.class)));
        assertEquals(String.class.getName(), key.toString());
    }

    // Provenance: JavaTypeTest#testJavaTypeAsJLRType().
    void javaTypeAsJlrTypeVpack() {
        JavaType first = MAPPER.constructType(getClass());
        assertSame(first, MAPPER.constructType(first));
    }

    // Provenance: JavaTypeTest#testGenericSignature1194().
    void genericSignature1194Vpack() throws Exception {
        TypeFactory types = MAPPER.getTypeFactory();
        Method method = GenericTypes.class.getMethod("getList");
        JavaType type = types.constructType(method.getGenericReturnType());
        assertEquals("Ljava/util/List<Ljava/lang/String;>;", type.getGenericSignature());
        assertEquals("Ljava/util/List;", type.getErasedSignature());
        method = GenericTypes.class.getMethod("getMap");
        type = types.constructType(method.getGenericReturnType());
        assertEquals("Ljava/util/Map<Ljava/lang/String;Ljava/lang/String;>;", type.getGenericSignature());
        method = GenericTypes.class.getMethod("getGeneric");
        type = types.constructType(method.getGenericReturnType());
        assertEquals("Ljava/util/concurrent/atomic/AtomicReference<Ljava/lang/String;>;",
                type.getGenericSignature());
    }

    // Provenance: JavaTypeTest#testObjectToReferenceSpecialization().
    void objectToReferenceSpecializationVpack() {
        TypeFactory types = MAPPER.getTypeFactory();
        JavaType base = types.constructType(Object.class);
        assertTrue(base.isJavaLangObject());
        JavaType specialized = types.constructSpecializedType(base, AtomicReference.class);
        assertEquals(AtomicReference.class, specialized.getRawClass());
        assertTrue(specialized.isReferenceType());
    }

    // Provenance: JavaTypeTest#testConstructReferenceType().
    void constructReferenceTypeVpack() {
        TypeFactory types = MAPPER.getTypeFactory();
        JavaType referred = types.constructType(Long.class);
        JavaType type = types.constructReferenceType(AtomicReference.class, referred);
        assertTrue(type.isReferenceType());
        assertTrue(type.hasContentType());
        assertEquals(Long.class, type.getContentType().getRawClass());
        assertEquals(1, type.containedTypeCount());
        TypeBindings bindings = type.getBindings();
        assertEquals(1, bindings.size());
        assertEquals(referred, bindings.getBoundType(0));
        assertEquals("V", bindings.getBoundName(0));
    }

    // Provenance: JavaTypeTest#testIterationTypesDirect().
    void iterationTypesDirectVpack() {
        TypeFactory types = MAPPER.getTypeFactory();
        verifyIteration(types.constructType(Iterator.class), Iterator.class, Object.class);
        verifyIteration(types.constructType(Stream.class), Stream.class, Object.class);
        JavaType type = verifyIteration(types.constructType(new TypeReference<Iterator<String>>() { }),
                Iterator.class, String.class);
        assertEquals("java.util.Iterator<java.lang.String>", type.toCanonical());
        assertEquals("Ljava/util/Iterator;", type.getErasedSignature());
        assertEquals("Ljava/util/Iterator<Ljava/lang/String;>;", type.getGenericSignature());
        verifyIteration(types.constructType(new TypeReference<Stream<Long>>() { }), Stream.class, Long.class);
        verifyIteration(types.constructType(DoubleStream.class), DoubleStream.class, Double.TYPE);
        verifyIteration(types.constructType(IntStream.class), IntStream.class, Integer.TYPE);
        verifyIteration(types.constructType(LongStream.class), LongStream.class, Long.TYPE);
    }

    // Provenance: JavaTypeTest#testIterationTypesFromValues().
    void iterationTypesFromValuesVpack() {
        List<String> strings = List.of("foo", "bar");
        Iterator<String> iterator = strings.iterator();
        verifyIteration(MAPPER.constructType(iterator.getClass()), iterator.getClass(), Object.class);
        Stream<String> stream = strings.stream();
        verifyIteration(MAPPER.constructType(stream.getClass()), stream.getClass(), Object.class);
    }

    // Provenance: JavaTypeTest#testIterationSubTypes().
    void iterationSubTypesVpack() {
        TypeFactory types = MAPPER.getTypeFactory();
        verifyIteration(types.constructType(StringIterator.class), StringIterator.class, String.class);
        verifyIteration(types.constructType(StringStream.class), StringStream.class, String.class);
    }
private static JavaType verifyIteration(JavaType type, Class<?> rawType, Class<?> contentType) {
        assertTrue(type.isIterationType());
        assertEquals(IterationType.class, type.getClass());
        assertEquals(rawType, type.getRawClass());
        assertEquals(contentType, type.getContentType().getRawClass());
        return type;
    }
static class BaseType { }
static class SubType extends BaseType { }
enum MyEnum { A, B }
enum MyEnum2 { A(1), B(2); MyEnum2(int value) { } }
enum MyEnumSub {
        A { @Override public String toString() { return "a"; } },
        B { @Override public String toString() { return "b"; } }
    }
static class GenericCharSequence {
        public <C extends CharSequence> C method(C input) { return null; }
    }
interface GenericTypes {
        AtomicReference<String> getGeneric();
        List<String> getList();
        Map<String, String> getMap();
    }
interface StringStream extends Stream<String> { }
interface StringIterator extends Iterator<String> { }

    void __invoke_localType728Vpack() throws Exception {
        try {
            localType728Vpack();
        } finally {
        }
    }


    void __invoke_simpleClassVpack() throws Exception {
        try {
            simpleClassVpack();
        } finally {
        }
    }


    void __invoke_mapTypeVpack() throws Exception {
        try {
            mapTypeVpack();
        } finally {
        }
    }


    void __invoke_enumTypeVpack() throws Exception {
        try {
            enumTypeVpack();
        } finally {
        }
    }


    void __invoke_classKeyVpack() throws Exception {
        try {
            classKeyVpack();
        } finally {
        }
    }


    void __invoke_javaTypeAsJlrTypeVpack() throws Exception {
        try {
            javaTypeAsJlrTypeVpack();
        } finally {
        }
    }


    void __invoke_genericSignature1194Vpack() throws Exception {
        try {
            genericSignature1194Vpack();
        } finally {
        }
    }


    void __invoke_objectToReferenceSpecializationVpack() throws Exception {
        try {
            objectToReferenceSpecializationVpack();
        } finally {
        }
    }


    void __invoke_constructReferenceTypeVpack() throws Exception {
        try {
            constructReferenceTypeVpack();
        } finally {
        }
    }


    void __invoke_iterationTypesDirectVpack() throws Exception {
        try {
            iterationTypesDirectVpack();
        } finally {
        }
    }


    void __invoke_iterationTypesFromValuesVpack() throws Exception {
        try {
            iterationTypesFromValuesVpack();
        } finally {
        }
    }


    void __invoke_iterationSubTypesVpack() throws Exception {
        try {
            iterationSubTypesVpack();
        } finally {
        }
    }

}
