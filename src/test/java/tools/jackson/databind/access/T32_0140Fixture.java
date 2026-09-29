package tools.jackson.databind.access;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import tools.jackson.core.Version;
import tools.jackson.core.Versioned;
import tools.jackson.databind.DeserializationConfig;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.cfg.CacheProvider;
import tools.jackson.databind.cfg.DefaultCacheProvider;
import tools.jackson.databind.util.LookupCache;
import tools.jackson.databind.util.SimpleLookupCache;
import tools.jackson.databind.util.TypeKey;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import tools.jackson.dataformat.velocypack.*;

class T32_0140Fixture {
private static final byte[] DYNAMIC_BEAN = VPackWireFixtureTest.hex(
            "0b 15 02 42 69 64 28 7b 44 6e 61 6d 65 45 42 69 6c 6c 79 03 08");
private static final byte[] DYNAMIC_BEAN_READ = VPackWireFixtureTest.hex(
            "0b 12 02 42 69 64 32 44 6e 61 6d 65 43 4a 6f 65 03 07");
private static final byte[] PRIVATE_THING = VPackWireFixtureTest.hex(
            "0b 08 01 41 61 41 41 03");
private static final byte[] POINT = VPackWireFixtureTest.hex(
            "0b 0c 01 45 70 6f 69 6e 74 28 18 03");
private static final byte[] HEIGHT = VPackWireFixtureTest.hex(
            "0b 0d 01 46 68 65 69 67 68 74 28 18 03");
private static final byte[] SLIDE = VPackWireFixtureTest.hex(
            "0b 0c 01 45 73 6c 69 64 65 28 7b 03");

    void testDynaBean() throws Exception {
        DynaBean bean = new DynaBean();
        bean.id = 123;
        bean.set("name", "Billy");

        VPackMapper mapper = new VPackMapper();
        assertArrayEquals(DYNAMIC_BEAN, mapper.writeValueAsBytes(bean));

        DynaBean result = mapper.readValue(DYNAMIC_BEAN_READ, DynaBean.class);
        assertEquals(2, result.id);
        assertEquals("Joe", result.other.get("name"));
    }

    void testPrivate() throws Exception {
        assertArrayEquals(PRIVATE_THING,
                new VPackMapper().writeValueAsBytes(new PrivateThing()));
    }
private static void assertVersion(Versioned value, Version expected) {
        Version actual = value.version();
        assertFalse(actual.isUnknownVersion(), "Should find version information (got " + actual + ")");
        assertEquals(expected.toFullString(), actual.toFullString());
        assertEquals(expected, actual);
    }
private static int cacheLimit(DefaultCacheProvider provider, String fieldName) {
        try {
            Field field = DefaultCacheProvider.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.getInt(provider);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError("DefaultCacheProvider API changed: " + fieldName, e);
        }
    }
private static CacheProvider defaultProviderWithSerializerCache(int size) {
        return DefaultCacheProvider.builder().maxSerializerCacheSize(size).build();
    }
private static void verifySerializeSuccess(CacheProvider cacheProvider) throws Exception {
        VPackMapper mapper = VPackMapper.builder().cacheProvider(cacheProvider).build();
        assertArrayEquals(SLIDE, mapper.writeValueAsBytes(new SerBean()));
    }
static class DynaBean {
        public int id;
        protected Map<String, String> other = new HashMap<>();

        @JsonAnyGetter
        public Map<String, String> any() {
            return other;
        }

        @JsonAnySetter
        public void set(String name, String value) {
            other.put(name, value);
        }
    }
static class PrivateThing {
        @JsonAnyGetter
        public Map<?, ?> getProperties() {
            HashMap<String, String> map = new HashMap<>();
            map.put("a", "A");
            return map;
        }
    }
static class RandomBean {
        public int point;
    }
static class AnotherBean {
        public int height;
    }
static class SerBean {
        public int slide = 123;
    }
static class SimpleTestCache implements LookupCache<JavaType, ValueDeserializer<Object>> {
        final HashMap<JavaType, ValueDeserializer<Object>> cached = new HashMap<>();
        boolean invoked;

        SimpleTestCache(int cacheSize) { }

        @Override public int size() { return cached.size(); }
        @Override public ValueDeserializer<Object> get(JavaType key) {
            invoked = true;
            return cached.get(key);
        }
        @Override public ValueDeserializer<Object> put(JavaType key, ValueDeserializer<Object> value) {
            invoked = true;
            return cached.put(key, value);
        }
        @Override public ValueDeserializer<Object> putIfAbsent(JavaType key, ValueDeserializer<Object> value) {
            invoked = true;
            return cached.putIfAbsent(key, value);
        }
        @Override public void clear() { cached.clear(); }
        @Override public LookupCache<JavaType, ValueDeserializer<Object>> snapshot() { return this; }
        @Override public LookupCache<JavaType, ValueDeserializer<Object>> emptyCopy() { return this; }
        boolean isInvokedAtLeastOnce() { return invoked; }
    }
static class CustomCacheProvider implements CacheProvider {
        private static final long serialVersionUID = 1L;
        final SimpleTestCache cache;
        int createCacheCount;

        CustomCacheProvider(SimpleTestCache cache) { this.cache = cache; }
        @Override public LookupCache<JavaType, ValueDeserializer<Object>> forDeserializerCache(
                DeserializationConfig config) {
            createCacheCount++;
            return cache;
        }
        @Override public LookupCache<TypeKey, ValueSerializer<Object>> forSerializerCache(
                tools.jackson.databind.SerializationConfig config) {
            return new SimpleLookupCache<>(8, 64);
        }
        @Override public LookupCache<Object, JavaType> forTypeFactory() {
            return new SimpleLookupCache<>(16, 64);
        }
        int createCacheCount() { return createCacheCount; }
    }
static class CustomSerializerCacheProvider implements CacheProvider {
        private static final long serialVersionUID = 1L;
        final SerializerCacheProbe cache = new SerializerCacheProbe();

        @Override public LookupCache<JavaType, ValueDeserializer<Object>> forDeserializerCache(
                DeserializationConfig config) {
            return new SimpleLookupCache<>(16, 64);
        }
        @Override public LookupCache<TypeKey, ValueSerializer<Object>> forSerializerCache(
                tools.jackson.databind.SerializationConfig config) {
            return cache;
        }
        @Override public LookupCache<Object, JavaType> forTypeFactory() {
            return new SimpleLookupCache<>(16, 64);
        }
    }
static class SerializerCacheProbe extends SimpleLookupCache<TypeKey, ValueSerializer<Object>> {
        private static final long serialVersionUID = 1L;
        boolean invoked;

        SerializerCacheProbe() { super(8, 64); }
        @Override public ValueSerializer<Object> put(TypeKey key, ValueSerializer<Object> value) {
            invoked = true;
            return super.put(key, value);
        }
    }
static class CustomTypeFactoryCacheProvider implements CacheProvider {
        private static final long serialVersionUID = 1L;
        final TypeFactoryCacheProbe cache = new TypeFactoryCacheProbe();

        @Override public LookupCache<JavaType, ValueDeserializer<Object>> forDeserializerCache(
                DeserializationConfig config) {
            return new SimpleLookupCache<>(16, 64);
        }
        @Override public LookupCache<Object, JavaType> forTypeFactory() { return cache; }
        @Override public LookupCache<TypeKey, ValueSerializer<Object>> forSerializerCache(
                tools.jackson.databind.SerializationConfig config) {
            return new SimpleLookupCache<>(16, 64);
        }
    }
static class TypeFactoryCacheProbe extends SimpleLookupCache<Object, JavaType> {
        private static final long serialVersionUID = 1L;
        boolean invoked;

        TypeFactoryCacheProbe() { super(8, 16); }
        @Override public JavaType putIfAbsent(Object key, JavaType value) {
            invoked = true;
            return super.putIfAbsent(key, value);
        }
        @Override public TypeFactoryCacheProbe snapshot() { return this; }
        @Override public TypeFactoryCacheProbe emptyCopy() { return this; }
    }

    void __invoke_testDynaBean() throws Exception {
        try {
            testDynaBean();
        } finally {
        }
    }


    void __invoke_testPrivate() throws Exception {
        try {
            testPrivate();
        } finally {
        }
    }

}
