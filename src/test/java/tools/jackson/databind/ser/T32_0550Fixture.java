package tools.jackson.databind.ser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0550Fixture {
private static final byte[] GENERIC_WRAPPER = VPackWireFixtureTest.hex(
            "14 25 45 66 69 72 73 74 14 11 42 69 64 31 44 6e 61 6d 65 44 6e 61 6d 65 02 "
          + "46 73 65 63 6f 6e 64 43 73 74 72 02");
private static final byte[] SPECIFICITY_WRAPPER = VPackWireFixtureTest.hex(
            "14 23 45 66 69 72 73 74 41 31 46 73 65 63 6f 6e 64 "
          + "14 11 42 69 64 31 44 6e 61 6d 65 44 6e 61 6d 65 02 02");
private static final byte[] SIMPLE_WRAPPER = VPackWireFixtureTest.hex(
            "14 1c 46 6f 62 6a 65 63 74 14 12 42 69 64 31 44 6e 61 6d 65 45 6e 61 6d 65 31 02 01");
private static final byte[] WRAPPED_STRING = VPackWireFixtureTest.hex(
            "0b 1e 01 4d 53 74 72 69 6e 67 57 72 61 70 70 65 72 "
          + "0b 0c 01 43 73 74 72 43 61 62 63 03 03");
private static final byte[] WRAPPED_ROOT_NAME = VPackWireFixtureTest.hex(
            "0b 10 01 44 72 6f 6f 74 0b 07 01 41 61 33 03 03");
private static final byte[] WRAPPED_EXPLICIT_TYPE = VPackWireFixtureTest.hex(
            "0b 2b 01 51 54 65 73 74 43 6f 6d 6d 61 6e 64 50 61 72 65 6e 74 "
          + "0b 15 02 44 75 75 69 64 44 31 32 33 34 44 74 79 70 65 31 0d 03 03");

    // Provenance: GenericTypeSerializationTest#testIssue468a().
    void testIssue468aVpack() throws Exception {
        ObjectMapper mapper = new VPackMapper();
        Person1 person = new Person1("John");
        person.setAccount(new Key<>(new Account("something", 42L)));

        @SuppressWarnings("unchecked")
        Map<String, Object> map = mapper.readValue(mapper.writeValueAsBytes(person), Map.class);
        assertEquals("John", map.get("name"));
        Map<?, ?> account = assertMap(map.get("account"));
        Map<?, ?> key = assertMap(account.get("id"));
        assertEquals("something", key.get("name"));
        assertEquals(Integer.valueOf(42), key.get("id"));
    }

    // Provenance: GenericTypeSerializationTest#testIssue468b().
    void testIssue468bVpack() throws Exception {
        ObjectMapper mapper = new VPackMapper();
        Person2 person = new Person2("John");
        person.setAccounts(List.of(new Key<>(new Account("a", 42L)),
                new Key<>(new Account("b", 43L)), new Key<>(new Account("c", 44L))));

        @SuppressWarnings("unchecked")
        Map<String, Object> map = mapper.readValue(mapper.writeValueAsBytes(person), Map.class);
        assertEquals("John", map.get("name"));
        Object value = map.get("accounts");
        assertNotNull(value);
        assertEquals(3, assertList(value).size());
    }

    // Provenance: GenericTypeSerializationTest#testRootTypeForCollections727().
    void testRootTypeForCollections727Vpack() throws Exception {
        ObjectMapper mapper = new VPackMapper();
        List<Base727> input = new ArrayList<>();
        input.add(new Impl727(1, 2));
        byte[] expected = VPackWireFixtureTest.hex(
                "02 0d 0b 0b 02 41 61 31 41 62 32 03 06");

        assertEquals(List.of(Map.of("a", 1, "b", 2)),
                mapper.readValue(mapper.writeValueAsBytes(input), List.class));
        assertEquals(List.of(Map.of("a", 1, "b", 2)),
                mapper.readValue(expected, List.class));
        TypeReference<List<Base727>> typeRef = new TypeReference<>() { };
        assertEquals(List.of(Map.of("a", 1, "b", 2)),
                mapper.readValue(mapper.writer().forType(typeRef).writeValueAsBytes(input), List.class));
    }

    // Provenance: GenericTypeSerializationTest#testStaticDelegateDeserialization().
    void testStaticDelegateDeserializationVpack() throws Exception {
        GenericWrapper<Account, String> wrapper = new VPackMapper().readValue(
                GENERIC_WRAPPER, new TypeReference<GenericWrapper<Account, String>>() { });
        assertEquals(new Account("name", 1L), wrapper.first());
        assertEquals("str", wrapper.second());
    }

    // Provenance: GenericTypeSerializationTest#testStaticDelegateDeserialization_factoryProvidesSpecificity0().
    void testStaticDelegateDeserializationFactoryProvidesSpecificity0Vpack() throws Exception {
        GenericSpecificityWrapper0<Object, Account> wrapper = new VPackMapper().readValue(
                SPECIFICITY_WRAPPER,
                new TypeReference<GenericSpecificityWrapper0<Object, Account>>() { });
        assertEquals(Long.valueOf(1L), wrapper.first());
        assertEquals(new Account("name", 1L), wrapper.second());
    }

    // Provenance: GenericTypeSerializationTest#testStaticDelegateDeserialization_factoryProvidesSpecificity1().
    void testStaticDelegateDeserializationFactoryProvidesSpecificity1Vpack() throws Exception {
        GenericSpecificityWrapper1<StringStub, Account> wrapper = new VPackMapper().readValue(
                SPECIFICITY_WRAPPER,
                new TypeReference<GenericSpecificityWrapper1<StringStub, Account>>() { });
        assertEquals("1", wrapper.first().value);
        assertEquals(new Account("name", 1L), wrapper.second());
    }

    // Provenance: GenericTypeSerializationTest#testStaticDelegateDeserialization_factoryProvidesSpecificity2().
    void testStaticDelegateDeserializationFactoryProvidesSpecificity2Vpack() throws Exception {
        GenericSpecificityWrapper2<Stub<Object>, Account> wrapper = new VPackMapper().readValue(
                SPECIFICITY_WRAPPER,
                new TypeReference<GenericSpecificityWrapper2<Stub<Object>, Account>>() { });
        StringStub value = (StringStub) wrapper.first().value;
        assertEquals("1", value.value);
        assertEquals(new Account("name", 1L), wrapper.second());
    }

    // Provenance: GenericTypeSerializationTest#testSimpleStaticJsonCreator().
    void testSimpleStaticJsonCreatorVpack() throws Exception {
        SimpleWrapper<Account> wrapper = new VPackMapper().readValue(
                SIMPLE_WRAPPER, new TypeReference<SimpleWrapper<Account>>() { });
        assertEquals(new Account("name1", 1L), wrapper.value);
    }

    // Provenance: GenericTypeSerializationTest#testJackson398().
    void testJackson398Vpack() throws Exception {
        ObjectMapper mapper = new VPackMapper();
        JavaType collectionType = mapper.getTypeFactory()
                .constructCollectionType(ArrayList.class, BaseClass398.class);
        List<TestClass398> input = new ArrayList<>(List.of(new TestClass398()));

        byte[] encoded = mapper.writerFor(collectionType).writeValueAsBytes(input);
        Map<?, ?> value = assertMap(assertList(mapper.readValue(encoded, List.class)).get(0));
        assertEquals("T32_0550Fixture$TestClass398", value.get("beanClass"));
        assertEquals("aa", value.get("property"));

        java.io.ByteArrayOutputStream output = new java.io.ByteArrayOutputStream();
        mapper.writerFor(collectionType).writeValue(mapper.createGenerator(output), input);
        Map<?, ?> generated = assertMap(assertList(mapper.readValue(output.toByteArray(), List.class)).get(0));
        assertEquals(value, generated);
    }

    // Provenance: GenericTypeSerializationTest#testRootWrapping().
    void testRootWrappingVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(SerializationFeature.WRAP_ROOT_VALUE).build();
        byte[] encoded = mapper.writeValueAsBytes(new StringWrapper("abc"));
        assertArrayEquals(WRAPPED_STRING, encoded);
        assertEquals(Map.of("StringWrapper", Map.of("str", "abc")),
                mapper.readValue(encoded, Map.class));
    }

    // Provenance: GenericTypeSerializationTest#testRootNameAnnotation().
    void testRootNameAnnotationVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(SerializationFeature.WRAP_ROOT_VALUE).build();
        byte[] encoded = mapper.writeValueAsBytes(new WithRootName());
        assertArrayEquals(WRAPPED_ROOT_NAME, encoded);
        assertEquals(Map.of("root", Map.of("a", 3)), mapper.readValue(encoded, Map.class));
    }

    // Provenance: GenericTypeSerializationTest#testRootNameWithExplicitType().
    void testRootNameWithExplicitTypeVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(SerializationFeature.WRAP_ROOT_VALUE).build();
        TestCommandChild command = new TestCommandChild();
        command.uuid = "1234";
        command.type = 1;

        ObjectWriter writer = mapper.writerFor(TestCommandParent.class);
        byte[] encoded = writer.writeValueAsBytes(command);
        assertArrayEquals(WRAPPED_EXPLICIT_TYPE, encoded);
        assertEquals(Map.of("TestCommandParent", Map.of("uuid", "1234", "type", 1)),
                mapper.readValue(encoded, Map.class));
    }
@SuppressWarnings("unchecked")
    private static Map<?, ?> assertMap(Object value) {
        assertTrue(value instanceof Map, () -> "expected map, got " + value);
        return (Map<?, ?>) value;
    }
@SuppressWarnings("unchecked")
    private static List<?> assertList(Object value) {
        assertTrue(value instanceof List, () -> "expected list, got " + value);
        return (List<?>) value;
    }
static class Account {
        private final Long id;
        private final String name;

        @JsonCreator
        Account(@JsonProperty("name") String name, @JsonProperty("id") Long id) {
            this.id = id;
            this.name = name;
        }

        public Long getId() { return id; }
        public String getName() { return name; }

        @Override
        public boolean equals(Object other) {
            return other instanceof Account account && Objects.equals(id, account.id)
                    && Objects.equals(name, account.name);
        }

        @Override
        public int hashCode() { return Objects.hash(id, name); }
    }
static class Key<T> {
        private final T id;

        Key(T id) { this.id = id; }
        public T getId() { return id; }
        public <V> Key<V> getParent() { return null; }
    }
static class Person1 {
        private final String name;
        private Key<Account> account;

        Person1(String name) { this.name = name; }
        public String getName() { return name; }
        public Key<Account> getAccount() { return account; }
        public void setAccount(Key<Account> account) { this.account = account; }
    }
static class Person2 {
        private final String name;
        private List<Key<Account>> accounts;

        Person2(String name) { this.name = name; }
        public String getName() { return name; }
        public List<Key<Account>> getAccounts() { return accounts; }
        public void setAccounts(List<Key<Account>> accounts) { this.accounts = accounts; }
    }
static class Base727 { public int a; }
@JsonPropertyOrder(alphabetic = true)
    static class Impl727 extends Base727 {
        public int b;
        Impl727(int a, int b) { this.a = a; this.b = b; }
    }
@JsonSerialize(as = GenericWrapperImpl.class)
    @JsonDeserialize(as = GenericWrapperImpl.class)
    interface GenericWrapper<A, AA> {
        A first();
        AA second();
    }
static final class GenericWrapperImpl<B, BB> implements GenericWrapper<B, BB> {
        private final B first;
        private final BB second;

        GenericWrapperImpl(B first, BB second) { this.first = first; this.second = second; }
        public B first() { return first; }
        public BB second() { return second; }

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        public static <C, CC> GenericWrapperImpl<CC, C> fromJson(JsonGenericWrapper<CC, C> value) {
            return new GenericWrapperImpl<>(value.first(), value.second());
        }
    }
@JsonDeserialize
    static final class JsonGenericWrapper<D, DD> implements GenericWrapper<D, DD> {
        @JsonProperty("first") private D first;
        @JsonProperty("second") private DD second;
        @JsonProperty("first") public D first() { return first; }
        @JsonProperty("second") public DD second() { return second; }
    }
static final class GenericSpecificityWrapper0<E, EE> {
        private final E first;
        private final EE second;
        GenericSpecificityWrapper0(E first, EE second) { this.first = first; this.second = second; }
        public E first() { return first; }
        public EE second() { return second; }

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        public static <F> GenericSpecificityWrapper0<?, F> fromJson(JsonGenericWrapper<Long, F> value) {
            return new GenericSpecificityWrapper0<>(value.first(), value.second());
        }
    }
static final class GenericSpecificityWrapper1<E, EE> {
        private final E first;
        private final EE second;
        GenericSpecificityWrapper1(E first, EE second) { this.first = first; this.second = second; }
        public E first() { return first; }
        public EE second() { return second; }

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        public static <F extends StringStubSubclass, FF>
        GenericSpecificityWrapper1<F, FF> fromJson(JsonGenericWrapper<F, FF> value) {
            return new GenericSpecificityWrapper1<>(value.first(), value.second());
        }
    }
static class StringStub {
        final String value;
        StringStub(String value) { this.value = value; }

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        public static StringStub valueOf(String value) { return new StringStub(value); }
    }
static class StringStubSubclass extends StringStub {
        private StringStubSubclass(String value) { super(value); }

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        public static StringStubSubclass valueOf(String value) { return new StringStubSubclass(value); }
    }
static final class GenericSpecificityWrapper2<E, EE> {
        private final E first;
        private final EE second;
        GenericSpecificityWrapper2(E first, EE second) { this.first = first; this.second = second; }
        public E first() { return first; }
        public EE second() { return second; }

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        public static <F extends Stub<StringStubSubclass>, FF>
        GenericSpecificityWrapper2<F, FF> fromJson(JsonGenericWrapper<F, FF> value) {
            return new GenericSpecificityWrapper2<>(value.first(), value.second());
        }
    }
static class Stub<T> {
        final T value;
        private Stub(T value) { this.value = value; }

        @JsonCreator
        public static <T> Stub<T> valueOf(T value) { return new Stub<>(value); }
    }
static final class SimpleWrapper<T> {
        final T value;
        SimpleWrapper(T value) { this.value = value; }

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        public static <T> SimpleWrapper<T> fromJson(JsonSimpleWrapper<T> value) {
            return new SimpleWrapper<>(value.object);
        }
    }
@JsonDeserialize
    static final class JsonSimpleWrapper<T> {
        @JsonProperty("object") public T object;
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY,
            property = "beanClass")
    abstract static class BaseClass398 { }
static class TestClass398 extends BaseClass398 {
        public String property = "aa";
    }
static class StringWrapper {
        public String str;
        StringWrapper(String str) { this.str = str; }
    }
@JsonRootName("root")
    static class WithRootName {
        public int a = 3;
    }
@JsonPropertyOrder({ "uuid", "type" })
    static class TestCommandParent {
        public String uuid;
        public int type;
    }
static class TestCommandChild extends TestCommandParent { }

    void __invoke_testIssue468aVpack() throws Exception {
        try {
            testIssue468aVpack();
        } finally {
        }
    }


    void __invoke_testIssue468bVpack() throws Exception {
        try {
            testIssue468bVpack();
        } finally {
        }
    }


    void __invoke_testRootTypeForCollections727Vpack() throws Exception {
        try {
            testRootTypeForCollections727Vpack();
        } finally {
        }
    }


    void __invoke_testStaticDelegateDeserializationVpack() throws Exception {
        try {
            testStaticDelegateDeserializationVpack();
        } finally {
        }
    }


    void __invoke_testStaticDelegateDeserializationFactoryProvidesSpecificity0Vpack() throws Exception {
        try {
            testStaticDelegateDeserializationFactoryProvidesSpecificity0Vpack();
        } finally {
        }
    }


    void __invoke_testStaticDelegateDeserializationFactoryProvidesSpecificity1Vpack() throws Exception {
        try {
            testStaticDelegateDeserializationFactoryProvidesSpecificity1Vpack();
        } finally {
        }
    }


    void __invoke_testStaticDelegateDeserializationFactoryProvidesSpecificity2Vpack() throws Exception {
        try {
            testStaticDelegateDeserializationFactoryProvidesSpecificity2Vpack();
        } finally {
        }
    }


    void __invoke_testSimpleStaticJsonCreatorVpack() throws Exception {
        try {
            testSimpleStaticJsonCreatorVpack();
        } finally {
        }
    }


    void __invoke_testJackson398Vpack() throws Exception {
        try {
            testJackson398Vpack();
        } finally {
        }
    }


    void __invoke_testRootWrappingVpack() throws Exception {
        try {
            testRootWrappingVpack();
        } finally {
        }
    }


    void __invoke_testRootNameAnnotationVpack() throws Exception {
        try {
            testRootNameAnnotationVpack();
        } finally {
        }
    }


    void __invoke_testRootNameWithExplicitTypeVpack() throws Exception {
        try {
            testRootNameWithExplicitTypeVpack();
        } finally {
        }
    }

}
