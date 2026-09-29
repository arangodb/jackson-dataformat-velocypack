package tools.jackson.databind.ext.jdk8;

import java.util.Optional;
import java.util.OptionalLong;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.StdScalarSerializer;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import tools.jackson.dataformat.velocypack.*;

class T32_0375F2 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] NULL = VPackWireFixtureTest.hex("18");
private static final byte[] OPTIONAL_LONG_MAX = VPackWireFixtureTest.hex(
            "2f ff ff ff ff ff ff ff 7f");
private static final byte[] QUOTED_DOUBLE = VPackWireFixtureTest.hex(
            "02 04 41 31");
private static final byte[] LONG_VALUE_123 = VPackWireFixtureTest.hex(
            "0b 0c 01 45 76 61 6c 75 65 28 7b 03");
private static final byte[] LONG_VALUE_NULL = VPackWireFixtureTest.hex(
            "0b 0b 01 45 76 61 6c 75 65 18 03");
private static final byte[] LONG_VALUE_456 = VPackWireFixtureTest.hex(
            "0b 0d 01 45 76 61 6c 75 65 29 c8 01 03");
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] BEAN_PRESENT = VPackWireFixtureTest.hex(
            "0b 27 02 43 66 6f 6f 2b ff ff ff 7f "
          + "43 62 61 72 54 77 6f 6f 70 77 6f 6f 70 77 6f 6f 70 77 6f 6f 70 77 6f 6f 70 "
          + "0c 03");
private static final byte[] CREATOR_EMPTY = VPackWireFixtureTest.hex(
            "0b 0a 01 44 64 61 74 61 18 03");
private static final byte[] CUSTOM_SERIALIZED = VPackWireFixtureTest.hex(
            "0b 11 01 45 76 61 6c 75 65 46 46 4f 4f 42 41 52 03");
private static final byte[] SUBTYPE_OBJECT = VPackWireFixtureTest.hex(
            "0b 21 02 45 40 74 79 70 65 47 73 75 62 74 79 70 65 "
          + "47 63 6f 6e 74 65 6e 74 45 68 65 6c 6c 6f 03 11");
private static final byte[] SUBTYPE_LIST = VPackWireFixtureTest.hex(
            "02 23 0b 21 02 45 40 74 79 70 65 47 73 75 62 74 79 70 65 "
          + "47 63 6f 6e 74 65 6e 74 45 68 65 6c 6c 6f 03 11");
private static final byte[] SUBTYPE_RUNTIME_OBJECT = VPackWireFixtureTest.hex(
            "0b 12 01 47 63 6f 6e 74 65 6e 74 45 68 65 6c 6c 6f 03");

    // Provenance: OptionalTest#testBeanAbsent().
    void testBeanAbsentVpack() throws Exception {
        TypeReference<Optional<TestBean>> type = new TypeReference<Optional<TestBean>>() { };
        assertArrayEquals(NULL, MAPPER.writeValueAsBytes(Optional.empty()));
        assertFalse(MAPPER.readValue(NULL, type).isPresent());
    }

    // Provenance: OptionalTest#testBeanPresent().
    void testBeanPresentVpack() throws Exception {
        TypeReference<Optional<TestBean>> type = new TypeReference<Optional<TestBean>>() { };
        TestBean expected = new TestBean(Integer.MAX_VALUE,
                "woopwoopwoopwoopwoop");
        assertArrayEquals(BEAN_PRESENT, MAPPER.writeValueAsBytes(Optional.of(expected)));
        assertEquals(expected, MAPPER.readValue(BEAN_PRESENT, type).get());
    }

    // Provenance: OptionalTest#testBeanWithCreator().
    void testBeanWithCreatorVpack() throws Exception {
        Issue4Entity expected = new Issue4Entity(Optional.empty());
        assertArrayEquals(CREATOR_EMPTY, MAPPER.writeValueAsBytes(expected));
        assertEquals(expected, MAPPER.readValue(CREATOR_EMPTY, Issue4Entity.class));
    }

    // Provenance: OptionalTest#testCustomSerializer().
    void testCustomSerializerVpack() throws Exception {
        assertArrayEquals(CUSTOM_SERIALIZED,
                MAPPER.writeValueAsBytes(new CaseChangingStringWrapper("fooBAR")));
    }
static class OptionalLongBean {
        public OptionalLong value;

        public OptionalLongBean() {
            value = OptionalLong.empty();
        }

        OptionalLongBean(long value) {
            this.value = OptionalLong.of(value);
        }
    }
public static class TestBean {
        public int foo;
        public String bar;

        @JsonCreator
        public TestBean(@JsonProperty("foo") int foo, @JsonProperty("bar") String bar) {
            this.foo = foo;
            this.bar = bar;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || obj.getClass() != getClass()) {
                return false;
            }
            TestBean other = (TestBean) obj;
            return foo == other.foo && bar.equals(other.bar);
        }

        @Override
        public int hashCode() {
            return foo ^ bar.hashCode();
        }
    }
static class Issue4Entity {
        private final Optional<String> data;

        @JsonCreator
        public Issue4Entity(@JsonProperty("data") Optional<String> data) {
            this.data = java.util.Objects.requireNonNull(data, "data");
        }

        @JsonProperty("data")
        public Optional<String> data() {
            return data;
        }

        @Override
        public boolean equals(Object obj) {
            return obj != null && obj.getClass() == getClass()
                    && data.equals(((Issue4Entity) obj).data);
        }
    }
static class CaseChangingStringWrapper {
        @JsonSerialize(contentUsing = UpperCasingSerializer.class)
        public Optional<String> value;

        CaseChangingStringWrapper() { }

        CaseChangingStringWrapper(String value) {
            this.value = Optional.ofNullable(value);
        }
    }
public static class UpperCasingSerializer extends StdScalarSerializer<String> {
        public UpperCasingSerializer() {
            super(String.class);
        }

        @Override
        public void serialize(String value, JsonGenerator gen,
                tools.jackson.databind.SerializationContext provider) {
            gen.writeString(value.toUpperCase());
        }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
    @JsonSubTypes(@JsonSubTypes.Type(value = Supertype.Subtype.class, name = "subtype"))
    interface Supertype {
        class Subtype implements Supertype {
            public String content = "hello";
        }
    }

    void __invoke_testBeanAbsentVpack() throws Exception {
        try {
            testBeanAbsentVpack();
        } finally {
        }
    }


    void __invoke_testBeanPresentVpack() throws Exception {
        try {
            testBeanPresentVpack();
        } finally {
        }
    }


    void __invoke_testBeanWithCreatorVpack() throws Exception {
        try {
            testBeanWithCreatorVpack();
        } finally {
        }
    }


    void __invoke_testCustomSerializerVpack() throws Exception {
        try {
            testCustomSerializerVpack();
        } finally {
        }
    }

}
