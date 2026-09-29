package tools.jackson.databind.introspect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.BeanDescription;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.introspect.TypeResolutionContext;
import tools.jackson.databind.type.TypeBindings;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0400F0 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final TypeResolutionContext EMPTY_CONTEXT =
            new TypeResolutionContext.Empty(MAPPER.getTypeFactory());
public static <T> AtomicReference<T> simple(T input) {
        throw new UnsupportedOperationException();
    }
public static AtomicReference<?> noGenerics(String input) {
        throw new UnsupportedOperationException();
    }
public static <T> Map<T, T> mapWithSameKeysAndValues(java.util.List<T> input) {
        throw new UnsupportedOperationException();
    }
public static <T> Map<?, ?> disconnected(java.util.List<T> input) {
        throw new UnsupportedOperationException();
    }
public static <A, B> Map<A, B> multipleTypeVariables(Map<A, B> input) {
        throw new UnsupportedOperationException();
    }
public static <A, B> Map<? extends A, ? extends B> multipleTypeVariablesWithUpperBound(
            Map<A, B> input) {
        throw new UnsupportedOperationException();
    }

    // Provenance: MethodGenericTypeResolverTest#testWithoutGenerics().
    void testWithoutGenericsVpack() {
        TypeBindings bindings = bindMethodTypeParameters(
                method("noGenerics"), type(String.class));
        assertNull(bindings);
    }

    // Provenance: MethodGenericTypeResolverTest#testWithoutGenericsInResult().
    void testWithoutGenericsInResultVpack() {
        TypeBindings bindings = bindMethodTypeParameters(
                method("simple"), type(AtomicReference.class));
        assertNull(bindings);
    }

    // Provenance: MethodGenericTypeResolverTest#testResultDoesNotUseTypeVariables().
    void testResultDoesNotUseTypeVariablesVpack() {
        TypeBindings bindings = bindMethodTypeParameters(
                method("disconnected"), type(new TypeReference<Map<String, String>>() { }));
        assertNull(bindings);
    }

    // Provenance: MethodGenericTypeResolverTest#testWithoutGenericsInMethod().
    void testWithoutGenericsInMethodVpack() {
        TypeBindings bindings = bindMethodTypeParameters(
                method("noGenerics"), type(new TypeReference<Map<String, String>>() { }));
        assertNull(bindings);
    }

    // Provenance: MethodGenericTypeResolverTest#testWithRepeatedGenericInReturn().
    void testWithRepeatedGenericInReturnVpack() {
        TypeBindings bindings = bindMethodTypeParameters(
                method("mapWithSameKeysAndValues"),
                type(new TypeReference<Map<String, String>>() { }));
        assertEquals(Collections.singletonMap("T", type(String.class)), asMap(bindings));
    }

    // Provenance: MethodGenericTypeResolverTest#testWithRepeatedGenericInReturnWithIncreasingSpecificity().
    void testWithRepeatedGenericInReturnWithIncreasingSpecificityVpack() {
        Method method = method("mapWithSameKeysAndValues");
        TypeBindings bindingsAb = bindMethodTypeParameters(
                method, type(new TypeReference<Map<StubA, StubB>>() { }));
        TypeBindings bindingsBa = bindMethodTypeParameters(
                method, type(new TypeReference<Map<StubB, StubA>>() { }));
        assertEquals(asMap(bindingsBa), asMap(bindingsAb));
        assertEquals(Collections.singletonMap("T", type(StubB.class)), asMap(bindingsBa));
    }

    // Provenance: MethodGenericTypeResolverTest#testMultipleTypeVariables().
    void testMultipleTypeVariablesVpack() {
        TypeBindings bindings = bindMethodTypeParameters(
                method("multipleTypeVariables"),
                type(new TypeReference<Map<Integer, Long>>() { }));
        assertEquals(asMap("A", type(Integer.class), "B", type(Long.class)), asMap(bindings));
    }

    // Provenance: MethodGenericTypeResolverTest#testMultipleTypeVariablesWithUpperBounds().
    void testMultipleTypeVariablesWithUpperBoundsVpack() {
        TypeBindings bindings = bindMethodTypeParameters(
                method("multipleTypeVariablesWithUpperBound"),
                type(new TypeReference<Map<Integer, Long>>() { }));
        assertEquals(asMap("A", type(Integer.class), "B", type(Long.class)), asMap(bindings));
    }

    // Provenance: MethodGenericTypeResolverTest#testResultTypeDoesNotExactlyMatch().
    void testResultTypeDoesNotExactlyMatchVpack() {
        TypeBindings bindings = bindMethodTypeParameters(
                method("multipleTypeVariables"),
                type(new TypeReference<java.util.HashMap<Integer, Long>>() { }));
        // Mapping a result to a common supertype is not supported.
        assertNull(bindings);
    }
private static BeanDescription serializationDescription(Class<?> type) {
        return MAPPER._serializationContext().introspectBeanDescription(MAPPER.constructType(type));
    }
private static BeanDescription deserializationDescription(Class<?> type) {
        return MAPPER._deserializationContext().introspectBeanDescription(MAPPER.constructType(type));
    }
private static Method method(String name) {
        Method result = null;
        for (Method method : T32_0400F0.class.getMethods()) {
            if (Modifier.isStatic(method.getModifiers()) && name.equals(method.getName())) {
                if (result != null) {
                    throw new AssertionError("Multiple methods discovered with name "
                            + name + ": " + result + " and " + method);
                }
                result = method;
            }
        }
        assertNotNull(result, "Failed to find method");
        return result;
    }
private static JavaType type(TypeReference<?> reference) {
        return type(reference.getType());
    }
private static JavaType type(Type type) {
        return EMPTY_CONTEXT.resolveType(type);
    }
private static TypeBindings bindMethodTypeParameters(Method candidate,
            JavaType requestedType) {
        try {
            Class<?> resolver = Class.forName(
                    "tools.jackson.databind.introspect.MethodGenericTypeResolver");
            Method bind = resolver.getDeclaredMethod("bindMethodTypeParameters",
                    Method.class, JavaType.class, TypeResolutionContext.class);
            bind.setAccessible(true);
            return (TypeBindings) bind.invoke(null, candidate, requestedType, EMPTY_CONTEXT);
        } catch (InvocationTargetException e) {
            throw new AssertionError("MethodGenericTypeResolver invocation failed", e.getCause());
        } catch (ReflectiveOperationException e) {
            throw new AssertionError("MethodGenericTypeResolver test seam unavailable", e);
        }
    }
private static Map<String, JavaType> asMap(TypeBindings bindings) {
        assertNotNull(bindings);
        Map<String, JavaType> result = new HashMap<>(bindings.size());
        for (int i = 0; i < bindings.size(); ++i) {
            result.put(bindings.getBoundName(i), bindings.getBoundType(i));
        }
        assertEquals(bindings.size(), result.size());
        return result;
    }
private static Map<String, JavaType> asMap(String name0, JavaType type0,
            String name1, JavaType type1) {
        Map<String, JavaType> result = new HashMap<>(2);
        result.put(name0, type0);
        result.put(name1, type1);
        return result;
    }
static class StubA {
        private final String value;

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        StubA(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
static class StubB extends StubA {
        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        public StubB(String value) {
            super(value);
        }
    }
@JsonIgnoreProperties("name")
    static class ClassIgnoredCreator5952 {
        final int id;
        final String name;

        @JsonCreator
        public ClassIgnoredCreator5952(@JsonProperty("id") int id,
                @JsonProperty("name") String name) {
            this.id = id;
            this.name = name;
        }
    }
static class ImplicitIgnores {
        @JsonIgnore public int a;
        @JsonIgnore public void setB(int b) { }
        public int c;
    }
@java.lang.annotation.Target({java.lang.annotation.ElementType.METHOD,
            java.lang.annotation.ElementType.FIELD,
            java.lang.annotation.ElementType.PARAMETER,
            java.lang.annotation.ElementType.ANNOTATION_TYPE})
    @java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
    @com.fasterxml.jackson.annotation.JacksonAnnotation
    @interface A { }
@java.lang.annotation.Target({java.lang.annotation.ElementType.METHOD,
            java.lang.annotation.ElementType.FIELD,
            java.lang.annotation.ElementType.PARAMETER,
            java.lang.annotation.ElementType.ANNOTATION_TYPE})
    @java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
    @com.fasterxml.jackson.annotation.JacksonAnnotation
    @interface B { }
static class DuplicateGetterBean {
        @A
        public boolean isBloop() { return true; }

        @B
        public boolean getBloop() { return true; }
    }

    void __invoke_testWithoutGenericsVpack() throws Exception {
        try {
            testWithoutGenericsVpack();
        } finally {
        }
    }


    void __invoke_testWithoutGenericsInResultVpack() throws Exception {
        try {
            testWithoutGenericsInResultVpack();
        } finally {
        }
    }


    void __invoke_testResultDoesNotUseTypeVariablesVpack() throws Exception {
        try {
            testResultDoesNotUseTypeVariablesVpack();
        } finally {
        }
    }


    void __invoke_testWithoutGenericsInMethodVpack() throws Exception {
        try {
            testWithoutGenericsInMethodVpack();
        } finally {
        }
    }


    void __invoke_testWithRepeatedGenericInReturnVpack() throws Exception {
        try {
            testWithRepeatedGenericInReturnVpack();
        } finally {
        }
    }


    void __invoke_testWithRepeatedGenericInReturnWithIncreasingSpecificityVpack() throws Exception {
        try {
            testWithRepeatedGenericInReturnWithIncreasingSpecificityVpack();
        } finally {
        }
    }


    void __invoke_testMultipleTypeVariablesVpack() throws Exception {
        try {
            testMultipleTypeVariablesVpack();
        } finally {
        }
    }


    void __invoke_testMultipleTypeVariablesWithUpperBoundsVpack() throws Exception {
        try {
            testMultipleTypeVariablesWithUpperBoundsVpack();
        } finally {
        }
    }


    void __invoke_testResultTypeDoesNotExactlyMatchVpack() throws Exception {
        try {
            testResultTypeDoesNotExactlyMatchVpack();
        } finally {
        }
    }

}
