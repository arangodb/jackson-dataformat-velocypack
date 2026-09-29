package tools.jackson.databind.introspect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.PropertyNamingStrategy;
import tools.jackson.databind.annotation.JsonNaming;
import tools.jackson.databind.cfg.MapperConfig;
import tools.jackson.databind.introspect.AnnotatedField;
import tools.jackson.databind.introspect.AnnotatedMethod;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0404F0 {
private static final byte[] ISSUE_193 = VPackWireFixtureTest.hex(
            "0b 11 02 44 76 61 6c 31 31 44 76 61 6c 32 32 03 09");
private static final byte[] NON_CONFLICT = VPackWireFixtureTest.hex(
            "0b 13 02 45 70 72 6f 70 31 32 45 70 72 6f 70 32 31 03 0a");
private static final byte[] HYPOTHETICAL = VPackWireFixtureTest.hex(
            "0b 07 01 41 61 33 03");
private static final byte[] OVERRIDE_X = VPackWireFixtureTest.hex(
            "0b 0a 01 43 62 61 72 41 78 03");
private static final byte[] OVERRIDE_Y = VPackWireFixtureTest.hex(
            "0b 0a 01 43 62 61 72 41 79 03");
private static final byte[] GETTER = VPackWireFixtureTest.hex(
            "0b 0e 01 47 47 65 74 2d 6b 65 79 28 7b 03");
private static final byte[] SETTER = VPackWireFixtureTest.hex(
            "0b 0e 01 47 53 65 74 2d 6b 65 79 28 0d 03");
private static final byte[] FIELD = VPackWireFixtureTest.hex(
            "0b 11 01 49 46 69 65 6c 64 2d 6b 65 79 29 e7 03 03");
private static final byte[] PERSON = VPackWireFixtureTest.hex(
            "0b 2d 03 4a 66 69 72 73 74 5f 6e 61 6d 65 43 4a 6f 65 "
          + "49 6c 61 73 74 5f 6e 61 6d 65 47 53 69 78 70 61 63 6b "
          + "43 61 67 65 28 2a 24 03 12");
private static final byte[] GETTER_AS_SETTER = VPackWireFixtureTest.hex(
            "0b 20 01 4a 76 61 6c 75 65 5f 6c 69 73 74 02 11 "
          + "0b 0f 01 49 69 6e 74 5f 76 61 6c 75 65 33 03 03");
private static final byte[] LOWER_CASE = VPackWireFixtureTest.hex(
            "0b 14 01 49 74 68 65 76 61 6c 75 65 73 06 06 01 41 61 03 03");
private static final byte[] GET_A = VPackWireFixtureTest.hex(
            "0b 0b 01 45 47 65 74 2d 61 33 03");
private static final byte[] SET_A = VPackWireFixtureTest.hex(
            "0b 0b 01 45 53 65 74 2d 61 37 03");
private static final byte[] DEFAULT_NAMING = VPackWireFixtureTest.hex(
            "0b 0f 01 49 73 6f 6d 65 56 61 6c 75 65 33 03");

    // Provenance: TestNameConflicts#testIssue193().
    void testIssue193Vpack() throws Exception {
        byte[] encoded = new VPackMapper().writeValueAsBytes(new Bean193(1, 2));
        assertVpack(ISSUE_193, encoded);
    }

    // Provenance: TestNameConflicts#testNonConflict().
    void testNonConflictVpack() throws Exception {
        assertVpack(NON_CONFLICT,
                new VPackMapper().writeValueAsBytes(new BogusConflictBean()));
    }

    // Provenance: TestNameConflicts#testHypotheticalGetters().
    void testHypotheticalGettersVpack() throws Exception {
        assertVpack(HYPOTHETICAL,
                new VPackMapper().writeValueAsBytes(new MultipleTheoreticalGetters()));
    }

    // Provenance: TestNameConflicts#testOverrideName().
    void testOverrideNameVpack() throws Exception {
        ObjectMapper mapper = new VPackMapper();
        assertVpack(OVERRIDE_X, mapper.writeValueAsBytes(new CoreBean158()));

        CoreBean158 result = mapper.readValue(OVERRIDE_Y, CoreBean158.class);
        assertNotNull(result);
        assertEquals("y", result.bar);
    }
private static void assertVpack(byte[] expected, byte[] actual) {
        assertArrayEquals(expected, actual,
                () -> "actual=" + Arrays.toString(actual));
    }
private static ObjectMapper prefixedMapper() {
        return VPackMapper.builder()
                .propertyNamingStrategy(new PrefixStrategy())
                .build();
    }
private static ObjectMapper cStyleMapper() {
        return VPackMapper.builder()
                .propertyNamingStrategy(new CStyleStrategy())
                .build();
    }
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE,
            getterVisibility = JsonAutoDetect.Visibility.NONE,
            setterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE)
    static class CoreBean158 {
        protected String bar = "x";

        @JsonProperty
        public String getBar() { return bar; }

        @JsonProperty
        public void setBar(String bar) { this.bar = bar; }

        public void setBar(java.io.Serializable bar) { this.bar = bar.toString(); }
    }
static class Bean193 {
        @JsonProperty("val1")
        private int x;
        @JsonIgnore
        private int value2;

        Bean193(@JsonProperty("val1") int value1,
                @JsonProperty("val2") int value2) {
            x = value1;
            this.value2 = value2;
        }

        @JsonProperty("val2")
        int x() { return value2; }
    }
@JsonPropertyOrder({"prop1", "prop2"})
    static class BogusConflictBean {
        @JsonProperty("prop1")
        public int a = 2;

        @JsonProperty("prop2")
        public int getA() { return 1; }
    }
static class MultipleTheoreticalGetters {
        public MultipleTheoreticalGetters() { }

        public MultipleTheoreticalGetters(@JsonProperty("a") int foo) { }

        @JsonProperty
        public int getA() { return 3; }

        public int a() { return 5; }
    }
static class PrefixStrategy extends PropertyNamingStrategy {
        @Override
        public String nameForField(MapperConfig<?> config, AnnotatedField field,
                String defaultName) {
            return "Field-" + defaultName;
        }

        @Override
        public String nameForGetterMethod(MapperConfig<?> config, AnnotatedMethod method,
                String defaultName) {
            return "Get-" + defaultName;
        }

        @Override
        public String nameForSetterMethod(MapperConfig<?> config, AnnotatedMethod method,
                String defaultName) {
            return "Set-" + defaultName;
        }
    }
static class CStyleStrategy extends PropertyNamingStrategy {
        @Override
        public String nameForField(MapperConfig<?> config, AnnotatedField field,
                String defaultName) {
            return convert(defaultName);
        }

        @Override
        public String nameForGetterMethod(MapperConfig<?> config, AnnotatedMethod method,
                String defaultName) {
            return convert(defaultName);
        }

        @Override
        public String nameForSetterMethod(MapperConfig<?> config, AnnotatedMethod method,
                String defaultName) {
            return convert(defaultName);
        }

        private String convert(String input) {
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < input.length(); ++i) {
                char c = input.charAt(i);
                if (Character.isUpperCase(c)) {
                    result.append('_');
                    c = Character.toLowerCase(c);
                }
                result.append(c);
            }
            return result.toString();
        }
    }
static class GetterBean {
        public int getKey() { return 123; }
    }
static class SetterBean {
        protected int value;
        public void setKey(int v) { value = v; }
    }
static class FieldBean {
        public int key;
        FieldBean() { this(0); }
        FieldBean(int v) { key = v; }
    }
@JsonPropertyOrder({"firstName", "lastName", "age"})
    static class PersonBean {
        public String firstName;
        public String lastName;
        public int age;

        PersonBean() { this(null, null, 0); }
        PersonBean(String f, String l, int a) {
            firstName = f;
            lastName = l;
            age = a;
        }
    }
static class Value {
        public int intValue;
        Value() { this(0); }
        Value(int v) { intValue = v; }
    }
static class SetterlessWithValue {
        protected ArrayList<Value> values = new ArrayList<>();
        public List<Value> getValueList() { return values; }
        public SetterlessWithValue add(int v) {
            values.add(new Value(v));
            return this;
        }
    }
static class LcStrategy extends PropertyNamingStrategies.NamingBase {
        @Override
        public String translate(String propertyName) {
            return propertyName.toLowerCase();
        }
    }
static class RenamedCollectionBean {
        @JsonProperty
        private List<String> theValues = List.of();
        public List<String> getTheValues() { return theValues; }
    }
@JsonNaming(PrefixStrategy.class)
    static class BeanWithPrefixNames {
        protected int a = 3;
        public int getA() { return a; }
        public void setA(int value) { a = value; }
    }
@JsonNaming
    static class DefaultNaming {
        public int someValue = 3;
    }

    void __invoke_testIssue193Vpack() throws Exception {
        try {
            testIssue193Vpack();
        } finally {
        }
    }


    void __invoke_testNonConflictVpack() throws Exception {
        try {
            testNonConflictVpack();
        } finally {
        }
    }


    void __invoke_testHypotheticalGettersVpack() throws Exception {
        try {
            testHypotheticalGettersVpack();
        } finally {
        }
    }


    void __invoke_testOverrideNameVpack() throws Exception {
        try {
            testOverrideNameVpack();
        } finally {
        }
    }

}
