package tools.jackson.databind.type;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.type.TypeBindings;
import tools.jackson.databind.type.TypeFactory;
import tools.jackson.databind.type.TypeModifier;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import tools.jackson.dataformat.velocypack.*;

class T32_0620F0 {
private static final VPackMapper MAPPER = new VPackMapper();

    // Provenance: TypeFactoryWithClassLoaderTest#testUsesCorrectClassLoaderWhenThreadClassLoaderIsNotNull().
    void explicitClassLoaderWithThreadLoaderVpack() throws Exception {
        ClassLoaderProbe explicit = new ClassLoaderProbe(getClass().getClassLoader());
        ClassLoader previous = Thread.currentThread().getContextClassLoader();
        ClassLoaderProbe context = new ClassLoaderProbe(previous);
        try {
            Thread.currentThread().setContextClassLoader(context);
            TypeFactory types = MAPPER.getTypeFactory().withModifier(noopModifier())
                    .withClassLoader(explicit);
            assertSame(AClass.class, types.findClass(AClass.class.getName()));
            assertSame(explicit, types.getClassLoader());
            assertEquals(AClass.class.getName(), explicit.requested);
            assertNull(context.requested);
        } finally {
            Thread.currentThread().setContextClassLoader(previous);
        }
    }

    // Provenance: TypeFactoryWithClassLoaderTest#testUsesCorrectClassLoaderWhenThreadClassLoaderIsNull().
    void explicitClassLoaderWithNullThreadLoaderVpack() throws Exception {
        ClassLoaderProbe explicit = new ClassLoaderProbe(getClass().getClassLoader());
        ClassLoader previous = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(null);
            TypeFactory types = MAPPER.getTypeFactory().withModifier(noopModifier())
                    .withClassLoader(explicit);
            assertSame(AClass.class, types.findClass(AClass.class.getName()));
            assertSame(explicit, types.getClassLoader());
            assertEquals(AClass.class.getName(), explicit.requested);
            assertNull(Thread.currentThread().getContextClassLoader());
        } finally {
            Thread.currentThread().setContextClassLoader(previous);
        }
    }

    // Provenance: TypeFactoryWithClassLoaderTest#testUsesFallBackClassLoaderIfNoThreadClassLoaderAndNoWithClassLoader().
    void fallbackClassLoaderVpack() throws Exception {
        ClassLoader previous = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(null);
            TypeFactory types = MAPPER.getTypeFactory();
            assertNull(types.getClassLoader());
            assertSame(AClass.class, types.findClass(AClass.class.getName()));
            assertNull(Thread.currentThread().getContextClassLoader());
        } finally {
            Thread.currentThread().setContextClassLoader(previous);
        }
    }
private static TypeModifier noopModifier() {
        return new TypeModifier() {
            @Override
            public JavaType modifyType(JavaType type, Type jdkType, TypeBindings context,
                    TypeFactory typeFactory) {
                return type;
            }
        };
    }
private static final class ClassLoaderProbe extends ClassLoader {
        String requested;

        ClassLoaderProbe(ClassLoader parent) { super(parent); }

        @Override
        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            requested = name;
            return super.loadClass(name, resolve);
        }
    }
static class AClass { }
static class LongValuedMap<K> extends HashMap<K, Long> { }
static class GenericList<X> extends ArrayList<X> { }
static class GenericList2<Y> extends GenericList<Y> { }
static class LongList extends GenericList2<Long> { }
static class MyLongList<T> extends LongList { }
static class Range<E extends Comparable<E>> { Range(E start, E end) { } }
static class DoubleRange extends Range<Double> { DoubleRange() { super(null, null); } }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
    @JsonSubTypes({ @JsonSubTypes.Type(value = EmailSettings.class, name = "EMAIL") })
    interface Settings { }
record EmailSettings(String email) implements Settings { }
record MessageWrapper<T extends Settings>(T settings, String message) { }
static class Holder {
        MessageWrapper<?> wildcardWrapper;
        MessageWrapper<EmailSettings> specificWrapper;
    }
static class Pair<L, R> { }
static class PairHolder { Pair<String, ?> wildcardPair; }
abstract static class RuleSetRevision<RS extends RuleSet<?>,
            RSR extends RuleSetRule<?, ?>> { }
abstract static class RuleSet<RSRV extends RuleSetRevision<?, ?>> { }
abstract static class RuleSetRule<RR, RS extends RuleSetRevision<?, ?>> { }

    void __invoke_explicitClassLoaderWithThreadLoaderVpack() throws Exception {
        try {
            explicitClassLoaderWithThreadLoaderVpack();
        } finally {
        }
    }


    void __invoke_explicitClassLoaderWithNullThreadLoaderVpack() throws Exception {
        try {
            explicitClassLoaderWithNullThreadLoaderVpack();
        } finally {
        }
    }


    void __invoke_fallbackClassLoaderVpack() throws Exception {
        try {
            fallbackClassLoaderVpack();
        } finally {
        }
    }

}
