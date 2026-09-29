package tools.jackson.databind.deser.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0223F1 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] INTEGER_7 = VPackWireFixtureTest.hex("37");
private static final byte[] QUOTED_7 = VPackWireFixtureTest.hex("41 37");
private static final byte[] INTEGER_42 = VPackWireFixtureTest.hex("28 2a");
private static final byte[] QUOTED_42 = VPackWireFixtureTest.hex("42 34 32");
private static final byte[] QUOTED_NOT_A_NUMBER = VPackWireFixtureTest.hex(
            "4c 4e 4f 54 5f 41 5f 4e 55 4d 42 45 52");
private static final byte[] INTEGER_0 = VPackWireFixtureTest.hex("30");
private static final byte[] ENUM_CREATOR_A = VPackWireFixtureTest.hex(
            "14 1d 45 76 61 6c 75 65 41 61 46 70 65 72 73 6f 6e 44 4a 65 66 66 43 61 67 65 28 1e 03");
private static final byte[] ENUM_CREATOR_UNKNOWN = VPackWireFixtureTest.hex(
            "14 1d 45 76 61 6c 75 65 41 65 46 70 65 72 73 6f 6e 44 4a 65 66 66 43 61 67 65 28 1e 03");
private static final byte[] QUOTED_ORDINAL_PROPERTY = VPackWireFixtureTest.hex(
            "14 0f 49 65 6e 75 6d 56 61 6c 75 65 41 31 01");

    // Provenance: EnumDeserialization3369Test#testReadEnums3369.
    void testReadEnums3369Vpack() throws Exception {
        Data3369 data = MAPPER.readValue(ENUM_CREATOR_A, Data3369.class);
        verify3369(data, Enum3369.A);

        data = MAPPER.readValue(ENUM_CREATOR_UNKNOWN, Data3369.class);
        verify3369(data, null);
    }
private static void verify3369(Data3369 data, Enum3369 expected) {
        assertEquals("Jeff", data.person);
        assertEquals(Integer.valueOf(30), data.age);
        assertEquals(expected, data.value);
    }
@JsonFormat(shape = JsonFormat.Shape.NUMBER)
    enum MyEnum {
        @JsonProperty("7")
        FOO,
        @JsonProperty("42")
        BAR
    }
enum MyEnumNoFormat {
        @JsonProperty("7")
        FOO,
        @JsonProperty("42")
        BAR
    }
@JsonFormat(shape = JsonFormat.Shape.NUMBER)
    enum NonNumericEnum {
        @JsonProperty("NOT_A_NUMBER")
        VALUE
    }
@JsonIgnoreProperties(ignoreUnknown = true)
    static class Data3369 {
        public Enum3369 value;
        public String person;
        public Integer age;
    }
enum Enum3369 {
        A("ENUM_A"), B("ENUM_B"), C("ENUM_C"), D("ENUM_D");

        final String name;

        Enum3369(String name) {
            this.name = name;
        }

        @com.fasterxml.jackson.annotation.JsonCreator
        public static Enum3369 fromName(String name) {
            if (name != null) {
                switch (name) {
                case "a": return A;
                case "b": return B;
                case "c": return C;
                case "d": return D;
                default: break;
                }
            }
            return null;
        }
    }
enum Member {
        FIRST_MEMBER,
        SECOND_MEMBER
    }
static class SensitiveBean {
        @JsonFormat(without = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
        public Member enumValue;
    }
static class InsensitiveBean {
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
        public Member enumValue;
    }

    void __invoke_testReadEnums3369Vpack() throws Exception {
        try {
            testReadEnums3369Vpack();
        } finally {
        }
    }

}
