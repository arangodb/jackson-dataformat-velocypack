package tools.jackson.databind.deser.creators;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0208F1 {
private static final byte[] INNER_CREATOR_FAILURE = VPackWireFixtureTest.hex(
            "14 06 41 61 0a 01");
private static final byte[] INNER_CREATOR_NULL = VPackWireFixtureTest.hex(
            "0b 07 01 41 61 18 03");
private static final byte[] INNER_CLASS_NULL = VPackWireFixtureTest.hex(
            "14 0f 4a 69 6e 6e 65 72 43 6c 61 73 73 18 01");
private static final byte[] DEFAULTS_PROPERTIES = VPackWireFixtureTest.hex(
            "14 15 44 6e 61 6d 65 45 68 65 6c 6c 6f 45 63 6f 75 6e 74 37 02");
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] TYPE_A = VPackWireFixtureTest.hex(
            "14 0e 44 74 79 70 65 45 41 54 79 70 65 01");
private static final byte[] DELEGATING_ENUM_WRAPPER = VPackWireFixtureTest.hex(
            "14 1b 45 65 6e 75 6d 41 45 41 54 79 70 65 "
          + "45 65 6e 75 6d 42 45 42 54 79 70 65 02");
private static final byte[] PROPERTIES_ENUM_WRAPPER = VPackWireFixtureTest.hex(
            "14 2b "
          + "45 65 6e 75 6d 41 14 0e 44 74 79 70 65 45 41 54 79 70 65 01 "
          + "45 65 6e 75 6d 43 14 0e 44 74 79 70 65 45 43 54 79 70 65 01 02");
private static final byte[] A_TYPE = VPackWireFixtureTest.hex(
            "45 41 54 79 70 65");
private static final byte[] PROPERTIES = VPackWireFixtureTest.hex(
            "4a 70 72 6f 70 65 72 74 69 65 73");
private static final ObjectMapper MAPPER = VPackMapper.builder().build();

    // Provenance: JsonCreatorDefaultAndPropertiesCtors5840Test#testDeserWithPropertiesCreator5840.
    void testDeserWithPropertiesCreator5840() throws Exception {
        AllDefaultsBean result = MAPPER.readValue(DEFAULTS_PROPERTIES, AllDefaultsBean.class);
        assertNotNull(result);
        assertEquals("hello", result.getName());
        assertEquals(7, result.getCount());
    }

    // Provenance: JsonCreatorDefaultAndPropertiesCtors5840Test#testDeserWithEmptyJson5840.
    void testDeserWithEmptyJson5840() throws Exception {
        AllDefaultsBean result = MAPPER.readValue(EMPTY_OBJECT, AllDefaultsBean.class);
        assertNotNull(result);
        assertNull(result.getName());
        assertNull(result.getCount());
    }
static class Something1501 {
        public InnerSomething1501 a;

        @JsonCreator
        public Something1501(@JsonProperty("a") InnerSomething1501 a) { this.a = a; }

        public Something1501(boolean bogus) { a = new InnerSomething1501(); }

        class InnerSomething1501 {
            @JsonCreator
            public InnerSomething1501() { }
        }
    }
static class Something1502 {
        @JsonProperty
        public InnerSomething1502 a;

        @JsonCreator
        public Something1502(@JsonProperty("a") InnerSomething1502 a) { }

        class InnerSomething1502 {
            @JsonCreator
            public InnerSomething1502() { }
        }
    }
static class Outer1503 {
        public InnerClass1503 innerClass;

        class InnerClass1503 {
            public Generic<?> generic;
            public InnerClass1503(@JsonProperty("generic") Generic<?> generic) { }
        }

        static class Generic<T> {
            public int ignored;
        }
    }
static final class AllDefaultsBean {
        private final String name;
        private final Integer count;

        @JsonCreator
        public AllDefaultsBean(@JsonProperty("name") String name,
                @JsonProperty("count") Integer count) {
            this.name = name;
            this.count = count;
        }

        @JsonCreator
        public AllDefaultsBean() {
            this("default-name", 42);
        }

        public String getName() { return name; }
        public Integer getCount() { return count; }
    }
static class PojoA {
        final String name;

        PojoA(String name) { this.name = name; }

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public static PojoA create(@JsonProperty("type") String name) {
            return new PojoA(name);
        }

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        public static PojoA fromString(String name) {
            return new PojoA(name);
        }
    }
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
    enum EnumA {
        A("AType"), B("BType");

        private final String type;

        EnumA(String type) { this.type = type; }
        public String getType() { return type; }

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        public static EnumA fromString(String type) {
            for (EnumA value : values()) if (value.type.equals(type)) return value;
            throw new IllegalArgumentException(type);
        }

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public static EnumA create(@JsonProperty("type") String type) {
            return fromString(type);
        }
    }
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
    enum EnumB {
        A("AType"), B("BType");

        private final String type;

        EnumB(String type) { this.type = type; }
        public String getType() { return type; }

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        public static EnumB fromString(String type) {
            for (EnumB value : values()) if (value.type.equals(type)) return value;
            throw new IllegalArgumentException(type);
        }
    }
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
    enum EnumC {
        A("AType"), B("BType"), C("CType");

        private final String type;

        EnumC(String type) { this.type = type; }
        public String getType() { return type; }

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public static EnumC create(@JsonProperty("type") String type) {
            for (EnumC value : values()) if (value.type.equals(type)) return value;
            throw new IllegalArgumentException(type);
        }
    }
static class DelegatingCreatorEnumWrapper {
        public EnumA enumA;
        public EnumB enumB;
    }
static class PropertiesCreatorEnumWrapper {
        public EnumA enumA;
        public EnumC enumC;
    }

    void __invoke_testDeserWithPropertiesCreator5840() throws Exception {
        try {
            testDeserWithPropertiesCreator5840();
        } finally {
        }
    }


    void __invoke_testDeserWithEmptyJson5840() throws Exception {
        try {
            testDeserWithEmptyJson5840();
        } finally {
        }
    }

}
