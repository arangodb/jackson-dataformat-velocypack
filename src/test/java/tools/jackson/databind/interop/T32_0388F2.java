package tools.jackson.databind.interop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0388F2 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] ACCOUNT = VPackWireFixtureTest.hex(
            "0b 12 02 42 69 64 31 44 6e 61 6d 65 43 66 6f 6f 03 07");
private static final byte[] KEY_ACCOUNT = VPackWireFixtureTest.hex(
            "0b 19 01 42 69 64 0b 12 02 42 69 64 31 44 6e 61 6d 65 43 66 6f 6f 03 07 03");
private static final byte[] ENTRY = VPackWireFixtureTest.hex(
            "0b 3a 02 43 6b 65 79 0b 19 01 42 69 64 0b 12 02 42 69 64 31 "
          + "44 6e 61 6d 65 43 66 6f 6f 03 07 03 45 76 61 6c 75 65 0b 12 "
          + "02 42 69 64 32 44 6e 61 6d 65 43 62 61 72 03 07 03 20");
private static final byte[] PLANET = VPackWireFixtureTest.hex(
            "0b 0d 01 44 6e 61 6d 65 43 46 6f 6f 03");
private static final byte[] PROXY_ANNOTATIONS = VPackWireFixtureTest.hex(
            "0b 13 01 45 76 61 6c 75 65 48 67 65 74 56 61 6c 75 65 03");
private static final byte[] UNPACK_ID = VPackWireFixtureTest.hex(
            "0b 0a 01 42 69 64 21 39 30 03");

    // Provenance: KotlinIssueGH308JsonIgnoreTest#testJsonIgnoreWithJsonPropertyUnpacker().
    void testJsonIgnoreWithJsonPropertyUnpackerVpack() throws Exception {
        TestDto dto = MAPPER.readValue(UNPACK_ID, TestDto.class);

        assertNotNull(dto);
        assertNull(dto.id);
        assertEquals(Integer.valueOf(12345), dto.cityId);
    }
private static Account account(long id, String name) {
        return ImmutableAccount.builder().id(id).name(name).build();
    }
private static <T> Key<T> key(T id) {
        return ImmutableKey.<T>builder().id(id).build();
    }
private static <K, V> Entry<K, V> entry(K key, V value) {
        return ImmutableEntry.<K, V>builder().key(key).value(value).build();
    }
static <T> T getProxy(Class<T> type, Object value) {
        class ProxyUtil implements InvocationHandler {
            private final Object target = value;
            @Override public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return method.invoke(target, args);
            }
        }
        @SuppressWarnings("unchecked")
        T result = (T) Proxy.newProxyInstance(type.getClassLoader(), new Class[] { type },
                new ProxyUtil());
        return result;
    }
@JsonDeserialize(as = ImmutableAccount.class)
    @JsonSerialize(as = ImmutableAccount.class)
    interface Account {
        Long getId();
        String getName();
    }
@JsonDeserialize(as = ImmutableKey.class)
    @JsonSerialize(as = ImmutableKey.class)
    interface Key<T> {
        T getId();
    }
@JsonDeserialize(as = ImmutableEntry.class)
    @JsonSerialize(as = ImmutableEntry.class)
    interface Entry<K, V> {
        K getKey();
        V getValue();
    }
static final class ImmutableAccount implements Account {
        private final Long id;
        private final String name;

        private ImmutableAccount(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        @JsonProperty("id")
        @Override public Long getId() { return id; }

        @JsonProperty("name")
        @Override public String getName() { return name; }

        @Override public boolean equals(Object other) {
            return other instanceof ImmutableAccount account
                    && id.equals(account.id) && name.equals(account.name);
        }

        @Override public int hashCode() { return Objects.hash(id, name); }

        @JsonDeserialize
        @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE)
        static final class Json implements Account {
            Long id;
            String name;

            @JsonProperty("id") public void setId(Long value) { id = value; }
            @JsonProperty("name") public void setName(String value) { name = value; }
            @Override public Long getId() { throw new UnsupportedOperationException(); }
            @Override public String getName() { throw new UnsupportedOperationException(); }
        }

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        static ImmutableAccount fromJson(Json json) {
            return builder().id(json.id).name(json.name).build();
        }

        static Builder builder() { return new Builder(); }

        static final class Builder {
            private Long id;
            private String name;

            Builder id(Long value) { id = Objects.requireNonNull(value); return this; }
            Builder name(String value) { name = Objects.requireNonNull(value); return this; }
            ImmutableAccount build() { return new ImmutableAccount(id, name); }
        }
    }
static final class ImmutableKey<T> implements Key<T> {
        private final T id;

        private ImmutableKey(T id) { this.id = id; }

        @JsonProperty("id")
        @Override public T getId() { return id; }

        @Override public boolean equals(Object other) {
            return other instanceof ImmutableKey<?> key && id.equals(key.id);
        }

        @Override public int hashCode() { return Objects.hashCode(id); }

        @JsonDeserialize
        @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE)
        static final class Json<T> implements Key<T> {
            T id;

            @JsonProperty("id") public void setId(T value) { id = value; }
            @Override public T getId() { throw new UnsupportedOperationException(); }
        }

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        static <T> ImmutableKey<T> fromJson(Json<T> json) {
            return ImmutableKey.<T>builder().id(json.id).build();
        }

        static <T> Builder<T> builder() { return new Builder<>(); }

        static final class Builder<T> {
            private T id;

            Builder<T> id(T value) { id = Objects.requireNonNull(value); return this; }
            ImmutableKey<T> build() { return new ImmutableKey<>(id); }
        }
    }
static final class ImmutableEntry<K, V> implements Entry<K, V> {
        private final K key;
        private final V value;

        private ImmutableEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @JsonProperty("key")
        @Override public K getKey() { return key; }

        @JsonProperty("value")
        @Override public V getValue() { return value; }

        @Override public boolean equals(Object other) {
            return other instanceof ImmutableEntry<?, ?> entry
                    && key.equals(entry.key) && value.equals(entry.value);
        }

        @Override public int hashCode() { return Objects.hash(key, value); }

        @JsonDeserialize
        @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE)
        static final class Json<K, V> implements Entry<K, V> {
            K key;
            V value;

            @JsonProperty("key") public void setKey(K input) { key = input; }
            @JsonProperty("value") public void setValue(V input) { value = input; }
            @Override public K getKey() { throw new UnsupportedOperationException(); }
            @Override public V getValue() { throw new UnsupportedOperationException(); }
        }

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        static <K, V> ImmutableEntry<K, V> fromJson(Json<K, V> json) {
            return ImmutableEntry.<K, V>builder().key(json.key).value(json.value).build();
        }

        static <K, V> Builder<K, V> builder() { return new Builder<>(); }

        static final class Builder<K, V> {
            private K key;
            private V value;

            Builder<K, V> key(K input) { key = Objects.requireNonNull(input); return this; }
            Builder<K, V> value(V input) { value = Objects.requireNonNull(input); return this; }
            ImmutableEntry<K, V> build() { return new ImmutableEntry<>(key, value); }
        }
    }
public interface IPlanet {
        String getName();
        String setName(String value);
    }
static class Planet implements IPlanet {
        private String name;
        public Planet() { }
        Planet(String value) { name = value; }
        @Override public String getName() { return name; }
        @Override public String setName(String value) { name = value; return name; }
    }
public interface Example5416 {
        String getValue();
        @JsonIgnore String getIgnoredValue();
    }
static class TestDto {
        @JsonIgnore Integer id;
        Integer cityId;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public TestDto(@JsonProperty(value = "id", access = JsonProperty.Access.READ_ONLY) Integer id,
                @JsonProperty("cityId") Integer cityId) {
            this.id = id;
            this.cityId = cityId;
        }

        @JsonProperty("id")
        void unpackId(Integer idObj) { cityId = idObj; }
    }

    void __invoke_testJsonIgnoreWithJsonPropertyUnpackerVpack() throws Exception {
        try {
            testJsonIgnoreWithJsonPropertyUnpackerVpack();
        } finally {
        }
    }

}
