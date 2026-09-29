package tools.jackson.databind.ext.jdk9;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.DatabindContext;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0379Fixture {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] EMPTY_STREAM = VPackWireFixtureTest.hex("01");
private static final byte[] SINGLE_ELEMENT = VPackWireFixtureTest.hex(
            "02 14 0b 12 02 43 66 6f 6f 31 43 62 61 72 43 6f 6e 65 08 03");
private static final byte[] MULTI_ELEMENTS = VPackWireFixtureTest.hex(
            "02 26 "
          + "0b 12 02 43 66 6f 6f 31 43 62 61 72 43 6f 6e 65 08 03 "
          + "0b 12 02 43 66 6f 6f 32 43 62 61 72 43 74 77 6f 08 03");
private static final byte[] NESTED_EMPTY_ELEMENT = VPackWireFixtureTest.hex(
            "02 0e 0b 0c 01 46 76 61 6c 75 65 73 01 03");
private static final byte[] NESTED_SINGLE_ELEMENT = VPackWireFixtureTest.hex(
            "02 13 0b 11 01 46 76 61 6c 75 65 73 02 06 43 66 6f 6f 03");
private static final byte[] NESTED_MULTI_ELEMENTS = VPackWireFixtureTest.hex(
            "02 24 "
          + "0b 11 01 46 76 61 6c 75 65 73 02 06 43 66 6f 6f 03 "
          + "0b 11 01 46 76 61 6c 75 65 73 02 06 43 62 61 72 03");
private static final byte[] WRAPPED_STATIC_STREAM = VPackWireFixtureTest.hex(
            "0b 12 01 45 76 61 6c 75 65 02 08 41 61 41 62 41 63 03");
private static final byte[] FINAL_ELEMENT_STREAM = VPackWireFixtureTest.hex(
            "02 10 "
          + "0b 07 01 41 78 31 03 "
          + "0b 07 01 41 78 32 03");
private static final byte[] STRING_STREAM = VPackWireFixtureTest.hex(
            "02 0e 45 68 65 6c 6c 6f 45 77 6f 72 6c 64");
private static final byte[] INTEGER_STREAM = VPackWireFixtureTest.hex(
            "02 05 31 32 33");

    // Provenance: Java9ListsTest#testJava9ListOf().
    void testJava9ListOfVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .activateDefaultTypingAsProperty(new NoCheckSubTypeValidator(),
                        DefaultTyping.NON_FINAL, "@class")
                .build();
        ObjectWriter writer = mapper.writerFor(List.class);

        assertListSize(mapper, writer, List.of("a"), 1);
        assertListSize(mapper, writer, List.of("a", "b"), 2);
        assertListSize(mapper, writer, List.of("a", "b", "c"), 3);
        assertListSize(mapper, writer, List.of(), 0);
    }
private static void assertListSize(ObjectMapper mapper, ObjectWriter writer,
            List<String> input, int size) throws Exception {
        byte[] encoded = writer.writeValueAsBytes(input);
        List<?> output = mapper.readValue(encoded, List.class);
        assertEquals(size, output.size());
    }
static class TestBean {
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
            return foo == other.foo && Objects.equals(bar, other.bar);
        }

        @Override
        public int hashCode() {
            return foo ^ Objects.hashCode(bar);
        }
    }
static class StringStreamWrapper {
        public Stream<String> value;

        public StringStreamWrapper() { }

        StringStreamWrapper(Stream<String> value) {
            this.value = value;
        }
    }
static final class FinalValueBean {
        public int x;

        public FinalValueBean() { }

        FinalValueBean(int x) {
            this.x = x;
        }
    }
static class NestedStream<T, C extends Collection<T>> {
        C values;

        NestedStream() { }

        NestedStream(C values) {
            this.values = values;
        }

        public Stream<T> getValues() {
            return values.stream();
        }

        protected void setValues(C values) {
            this.values = values;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || obj.getClass() != getClass()) {
                return false;
            }
            NestedStream<?, ?> other = (NestedStream<?, ?>) obj;
            return Objects.equals(values, other.values);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(values);
        }
    }
static class NoCheckSubTypeValidator extends PolymorphicTypeValidator.Base {
        private static final long serialVersionUID = 1L;

        @Override
        public Validity validateBaseType(DatabindContext ctxt, JavaType baseType) {
            return Validity.ALLOWED;
        }
    }

    void __invoke_testJava9ListOfVpack() throws Exception {
        try {
            testJava9ListOfVpack();
        } finally {
        }
    }

}
