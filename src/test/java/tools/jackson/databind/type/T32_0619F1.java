package tools.jackson.databind.type;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tools.jackson.databind.JavaType;
import tools.jackson.databind.type.TypeBindings;
import tools.jackson.databind.type.TypeFactory;
import tools.jackson.databind.type.TypeModifier;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0619F1 {
private static final VPackMapper MAPPER = new VPackMapper();

    // Provenance: TypeFactoryWithClassLoaderTest#testCallingOnlyWithModifierGivesExpectedResults().
    void onlyWithModifierVpack() {
        TypeFactory types = MAPPER.getTypeFactory().withModifier(noopModifier());
        assertNull(types.getClassLoader());
    }

    // Provenance: TypeFactoryWithClassLoaderTest#testCallingOnlyWithClassLoaderGivesExpectedResults().
    void onlyWithClassLoaderVpack() {
        ClassLoader loader = new ClassLoaderProbe(getClass().getClassLoader());
        TypeFactory types = MAPPER.getTypeFactory().withClassLoader(loader);
        assertSame(loader, types.getClassLoader());
        assertEquals(MAPPER.getTypeFactory().constructType(String.class),
                types.constructType(String.class));
    }

    // Provenance: TypeFactoryWithClassLoaderTest#testDefaultTypeFactoryNotAffectedByWithConstructors().
    void defaultTypeFactoryNotAffectedVpack() {
        TypeFactory original = MAPPER.getTypeFactory();
        ClassLoader loader = new ClassLoaderProbe(getClass().getClassLoader());
        TypeFactory derived = original.withModifier(noopModifier()).withClassLoader(loader);
        assertSame(loader, derived.getClassLoader());
        assertNull(original.getClassLoader());
        assertEquals(original.constructType(String.class), derived.constructType(String.class));
    }

    // Provenance: TypeFactoryWithClassLoaderTest#testSetsTheCorrectClassLoderIfUsingWithModifierFollowedByWithClassLoader().
    void modifierThenClassLoaderVpack() {
        ClassLoader loader = new ClassLoaderProbe(getClass().getClassLoader());
        TypeFactory types = MAPPER.getTypeFactory().withModifier(noopModifier()).withClassLoader(loader);
        assertSame(loader, types.getClassLoader());
    }

    // Provenance: TypeFactoryWithClassLoaderTest#testSetsTheCorrectClassLoderIfUsingWithClassLoaderFollowedByWithModifier().
    void classLoaderThenModifierVpack() {
        ClassLoader loader = new ClassLoaderProbe(getClass().getClassLoader());
        TypeFactory types = MAPPER.getTypeFactory().withClassLoader(loader).withModifier(noopModifier());
        assertSame(loader, types.getClassLoader());
    }

    // Provenance: TypeFactoryWithClassLoaderTest#testThreadContextClassLoaderIsUsedIfNotUsingWithClassLoader().
    void threadContextClassLoaderUsedVpack() throws Exception {
        ClassLoaderProbe loader = new ClassLoaderProbe(getClass().getClassLoader());
        ClassLoader previous = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(loader);
            TypeFactory types = MAPPER.getTypeFactory();
            assertNull(types.getClassLoader());
            assertSame(AClass.class, types.findClass(AClass.class.getName()));
            assertTrue(loader.saw(AClass.class.getName()));
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
        private String requested;

        ClassLoaderProbe(ClassLoader parent) {
            super(parent);
        }

        @Override
        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            requested = name;
            return super.loadClass(name, resolve);
        }

        boolean saw(String name) {
            return name.equals(requested);
        }
    }
public static class AClass { }
static class IntLongMap extends XLongMap<Integer> { }
static class XLongMap<K> extends XXMap<K, Long> { }
static class XXMap<K, V> extends HashMap<K, V> { }
static class MyList extends ArrayList<Long> { }
static class SneakyBean { public IntLongMap intMap; public MyList longList; }
static class LongValuedMap<K> extends HashMap<K, Long> { }
static class GenericList<T> extends ArrayList<T> { }
static class StringLongMapBean { public LongValuedMap<String> value; }
static class StringListBean { public GenericList<String> value; }
static class SneakyBean2 { public <T extends Comparable<T>> T getFoobar() { return null; } }
static class TwoParam1604<K, V> { }
static class SneakyTwoParam1604<V, K> extends TwoParam1604<K, List<V>> { }

    void __invoke_onlyWithModifierVpack() throws Exception {
        try {
            onlyWithModifierVpack();
        } finally {
        }
    }


    void __invoke_onlyWithClassLoaderVpack() throws Exception {
        try {
            onlyWithClassLoaderVpack();
        } finally {
        }
    }


    void __invoke_defaultTypeFactoryNotAffectedVpack() throws Exception {
        try {
            defaultTypeFactoryNotAffectedVpack();
        } finally {
        }
    }


    void __invoke_modifierThenClassLoaderVpack() throws Exception {
        try {
            modifierThenClassLoaderVpack();
        } finally {
        }
    }


    void __invoke_classLoaderThenModifierVpack() throws Exception {
        try {
            classLoaderThenModifierVpack();
        } finally {
        }
    }


    void __invoke_threadContextClassLoaderUsedVpack() throws Exception {
        try {
            threadContextClassLoaderUsedVpack();
        } finally {
        }
    }

}
