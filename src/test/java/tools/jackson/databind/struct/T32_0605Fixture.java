package tools.jackson.databind.struct;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.UnrecognizedPropertyException;
import tools.jackson.databind.json.JsonMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0605Fixture {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final ObjectMapper JSON = JsonMapper.builder().build();
private static final ObjectMapper EMPTY_AS_NULL = VPackMapper.builder()
            .enable(DeserializationFeature.USE_NULL_FOR_EMPTY_UNWRAPPED).build();
private static final ObjectMapper EMPTY_AS_OBJECT = VPackMapper.builder()
            .disable(DeserializationFeature.USE_NULL_FOR_EMPTY_UNWRAPPED).build();
private static final ObjectMapper STRICT = VPackMapper.builder()
            .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).build();
private static final byte[] CREATOR_UNKNOWN = VPackWireFixtureTest.hex(
            "14 27 44 6e 61 6d 65 44 74 65 73 74 45 66 69 65 6c 64 45 76 61 6c 75 65 "
          + "43 62 61 64 49 62 61 64 20 76 61 6c 75 65 03");
private static final byte[] CREATOR_PREFIX_UNKNOWN = VPackWireFixtureTest.hex(
            "14 2e 44 6e 61 6d 65 44 74 65 73 74 4c 6e 65 73 74 65 64 2e 66 69 65 6c 64 "
          + "45 76 61 6c 75 65 43 62 61 64 49 62 61 64 20 76 61 6c 75 65 03");
private static final byte[] HIERARCHIC = VPackWireFixtureTest.hex(
            "14 26 52 67 65 6e 65 72 61 6c 2e 6e 61 6d 65 73 2e 6e 61 6d 65 43 42 6f 62 "
          + "4a 6d 69 73 63 2e 76 61 6c 75 65 33 02");
private static final byte[] ALTERNATE = VPackWireFixtureTest.hex(
            "14 24 42 69 64 28 7b 4a 6e 61 6d 65 73 2e 6e 61 6d 65 43 4a 6f 65 "
          + "4a 6d 69 73 63 2e 76 61 6c 75 65 28 2a 03");
private static final byte[] ISSUE_2088 = VPackWireFixtureTest.hex(
            "14 0f 41 78 31 41 61 32 41 79 33 41 62 34 04");
private static final byte[] ISSUE_226 = VPackWireFixtureTest.hex(
            "14 21 4c 63 31 2e 73 63 32 2e 76 61 6c 75 65 41 61 "
          + "4c 63 32 2e 73 63 32 2e 76 61 6c 75 65 41 62 02");
private static final byte[] ISSUE_615 = VPackWireFixtureTest.hex(
            "14 0e 45 66 69 65 6c 64 44 6e 61 6d 65 01");
private static final byte[] PRESERVED = VPackWireFixtureTest.hex(
            "14 15 44 6e 61 6d 65 44 74 65 73 74 41 73 45 76 61 6c 75 65 02");
private static final byte[] PARTIAL = VPackWireFixtureTest.hex(
            "14 0b 41 73 45 76 61 6c 75 65 01");
private static final byte[] DOUBLE = VPackWireFixtureTest.hex(
            "14 23 46 76 61 6c 75 65 31 1b 00 00 00 00 00 00 4e c0 "
          + "46 76 61 6c 75 65 32 1b 00 00 00 00 00 00 4e c0 02");

    // Provenance: UnwrappedBasicTest#testFailOnUnknownPropertyWithCreator().
    void testFailOnUnknownPropertyWithCreatorVpack() {
        assertThrows(UnrecognizedPropertyException.class,
                () -> STRICT.readValue(CREATOR_UNKNOWN, CreatorOuter.class));
    }

    // Provenance: UnwrappedBasicTest#testFailOnUnknownPropertyWithCreatorAndPrefix().
    void testFailOnUnknownPropertyWithCreatorAndPrefixVpack() {
        assertThrows(UnrecognizedPropertyException.class,
                () -> STRICT.readValue(CREATOR_PREFIX_UNKNOWN, CreatorPrefixOuter.class));
    }

    // Provenance: UnwrappedBasicTest#testHierarchicConfigDeserialize().
    void testHierarchicConfigDeserializeVpack() throws Exception {
        ConfigRoot root = MAPPER.readValue(HIERARCHIC, ConfigRoot.class);
        assertNotNull(root.general);
        assertNotNull(root.general.names);
        assertNotNull(root.misc);
        assertEquals(3, root.misc.value);
        assertEquals("Bob", root.general.names.name);
    }

    // Provenance: UnwrappedBasicTest#testHierarchicConfigRoundTrip().
    void testHierarchicConfigRoundTripVpack() throws Exception {
        ConfigAlternate fromLiteral = MAPPER.readValue(ALTERNATE, ConfigAlternate.class);
        assertEquals(123, fromLiteral.id);
        assertEquals("Joe", fromLiteral.general.names.name);
        assertEquals(42, fromLiteral.misc.value);

        byte[] encoded = MAPPER.writeValueAsBytes(fromLiteral);
        ConfigAlternate decoded = MAPPER.readValue(encoded, ConfigAlternate.class);
        assertEquals(123, decoded.id);
        assertNotNull(decoded.general);
        assertNotNull(decoded.general.names);
        assertNotNull(decoded.misc);
        assertEquals("Joe", decoded.general.names.name);
        assertEquals(42, decoded.misc.value);
    }

    // Provenance: UnwrappedBasicTest#testHierarchicConfigSerialize().
    void testHierarchicConfigSerializeVpack() throws Exception {
        assertEquals(JSON.readTree("{\"general.names.name\":\"Fred\",\"misc.value\":25}"),
                MAPPER.readTree(MAPPER.writeValueAsBytes(new ConfigRoot("Fred", 25))));
    }

    // Provenance: UnwrappedBasicTest#testIsInstanceOfDouble().
    void testIsInstanceOfDoubleVpack() throws Exception {
        Holder holder = MAPPER.readValue(DOUBLE, Holder.class);
        assertEquals(Double.class, holder.value1.getClass());
        assertEquals(Double.class, holder.holder2.data.get("value2").getClass());
        assertEquals(-60.0, holder.value1);
        assertEquals(-60.0, holder.holder2.data.get("value2"));
    }

    // Provenance: UnwrappedBasicTest#testIssue2088UnwrappedFieldsAfterLastCreatorProp().
    void testIssue2088UnwrappedFieldsAfterLastCreatorPropVpack() throws Exception {
        Issue2088Outer bean = MAPPER.readValue(ISSUE_2088, Issue2088Outer.class);
        assertEquals(1, bean.x);
        assertEquals(2, bean.w.a);
        assertEquals(3, bean.y);
        assertEquals(4, bean.w.b);
    }

    // Provenance: UnwrappedBasicTest#testIssue226().
    void testIssue226Vpack() throws Exception {
        Parent226 bean = MAPPER.readValue(ISSUE_226, Parent226.class);
        assertNotNull(bean.c1);
        assertNotNull(bean.c2);
        assertNotNull(bean.c1.sc1);
        assertNotNull(bean.c2.sc1);
        assertEquals("a", bean.c1.sc1.value);
        assertEquals("b", bean.c2.sc1.value);
    }

    // Provenance: UnwrappedBasicTest#testIssue615().
    void testIssue615Vpack() throws Exception {
        Parent bean = MAPPER.readValue(ISSUE_615, Parent.class);
        assertEquals("name", bean.c1.field);
        assertEquals(JSON.readTree("{\"field\":\"name\"}"),
                MAPPER.readTree(MAPPER.writeValueAsBytes(bean)));
    }

    // Provenance: UnwrappedBasicTest#testNonNullUnwrappedPreserved().
    void testNonNullUnwrappedPreservedVpack() throws Exception {
        Container value = EMPTY_AS_NULL.readValue(PRESERVED, Container.class);
        assertNotNull(value);
        assertEquals("test", value.name);
        assertNotNull(value.u);
        assertEquals("value", value.u.s);
    }

    // Provenance: UnwrappedBasicTest#testNonNullUnwrappedPreservedWhenDisabled().
    void testNonNullUnwrappedPreservedWhenDisabledVpack() throws Exception {
        Container value = EMPTY_AS_OBJECT.readValue(PRESERVED, Container.class);
        assertNotNull(value);
        assertEquals("test", value.name);
        assertNotNull(value.u);
        assertEquals("value", value.u.s);
    }

    // Provenance: UnwrappedBasicTest#testPartialNonNullUnwrappedPreserved().
    void testPartialNonNullUnwrappedPreservedVpack() throws Exception {
        Container value = EMPTY_AS_NULL.readValue(PARTIAL, Container.class);
        assertNotNull(value);
        assertNotNull(value.u);
        assertEquals("value", value.u.s);
        assertNull(value.u.n);
    }
static class CreatorOuter {
        public String name;
        @JsonUnwrapped public BasicValue b;
        @com.fasterxml.jackson.annotation.JsonCreator
        CreatorOuter(@JsonProperty("name") String name) { this.name = name; }
    }
static class CreatorPrefixOuter {
        public String name;
        @JsonUnwrapped(prefix = "nested.") public BasicValue b;
        @com.fasterxml.jackson.annotation.JsonCreator
        CreatorPrefixOuter(@JsonProperty("name") String name) { this.name = name; }
    }
static class BasicValue { public String field; }
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
    static class Container { public String name; @JsonUnwrapped public EmptyValue u; }
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
    static class EmptyValue { public String s; public Integer n; }
static class ConfigRoot {
        @JsonUnwrapped(prefix = "general.") public ConfigGeneral general = new ConfigGeneral();
        @JsonUnwrapped(prefix = "misc.") public ConfigMisc misc = new ConfigMisc();
        ConfigRoot() { }
        ConfigRoot(String name, int value) { general = new ConfigGeneral(name); misc.value = value; }
    }
static class ConfigAlternate {
        @JsonUnwrapped public ConfigGeneral general = new ConfigGeneral();
        @JsonUnwrapped(prefix = "misc.") public ConfigMisc misc = new ConfigMisc();
        public int id;
    }
static class ConfigGeneral {
        @JsonUnwrapped(prefix = "names.") public ConfigNames names = new ConfigNames();
        ConfigGeneral() { }
        ConfigGeneral(String name) { names.name = name; }
    }
static class ConfigNames { public String name = "x"; }
static class ConfigMisc { public int value; }
static class Holder {
        public Object value1;
        @JsonUnwrapped public Holder2 holder2;
    }
static class Holder2 {
        public Map<String, Object> data = new HashMap<>();
        @com.fasterxml.jackson.annotation.JsonAnyGetter
        public Map<String, Object> getData() { return data; }
        @com.fasterxml.jackson.annotation.JsonAnySetter
        public void setAny(String key, Object value) { data.put(key, value); }
    }
static class Issue2088Outer {
        int x;
        int y;
        @JsonUnwrapped Issue2088Inner w;
        Issue2088Outer(@JsonProperty("x") int x, @JsonProperty("y") int y) { this.x=x; this.y=y; }
    }
static class Issue2088Inner {
        int a;
        int b;
        Issue2088Inner(@JsonProperty("a") int a, @JsonProperty("b") int b) { this.a=a; this.b=b; }
    }
static class Parent226 {
        @JsonUnwrapped(prefix = "c1.") public Child226 c1;
        @JsonUnwrapped(prefix = "c2.") public Child226 c2;
    }
static class Child226 { @JsonUnwrapped(prefix = "sc2.") public SubChild sc1; }
static class SubChild { public String value; }
static class Parent { @JsonUnwrapped public Child c1; }
static class Child { public String field; }

    void __invoke_testFailOnUnknownPropertyWithCreatorVpack() throws Exception {
        try {
            testFailOnUnknownPropertyWithCreatorVpack();
        } finally {
        }
    }


    void __invoke_testFailOnUnknownPropertyWithCreatorAndPrefixVpack() throws Exception {
        try {
            testFailOnUnknownPropertyWithCreatorAndPrefixVpack();
        } finally {
        }
    }


    void __invoke_testHierarchicConfigDeserializeVpack() throws Exception {
        try {
            testHierarchicConfigDeserializeVpack();
        } finally {
        }
    }


    void __invoke_testHierarchicConfigRoundTripVpack() throws Exception {
        try {
            testHierarchicConfigRoundTripVpack();
        } finally {
        }
    }


    void __invoke_testHierarchicConfigSerializeVpack() throws Exception {
        try {
            testHierarchicConfigSerializeVpack();
        } finally {
        }
    }


    void __invoke_testIsInstanceOfDoubleVpack() throws Exception {
        try {
            testIsInstanceOfDoubleVpack();
        } finally {
        }
    }


    void __invoke_testIssue2088UnwrappedFieldsAfterLastCreatorPropVpack() throws Exception {
        try {
            testIssue2088UnwrappedFieldsAfterLastCreatorPropVpack();
        } finally {
        }
    }


    void __invoke_testIssue226Vpack() throws Exception {
        try {
            testIssue226Vpack();
        } finally {
        }
    }


    void __invoke_testIssue615Vpack() throws Exception {
        try {
            testIssue615Vpack();
        } finally {
        }
    }


    void __invoke_testNonNullUnwrappedPreservedVpack() throws Exception {
        try {
            testNonNullUnwrappedPreservedVpack();
        } finally {
        }
    }


    void __invoke_testNonNullUnwrappedPreservedWhenDisabledVpack() throws Exception {
        try {
            testNonNullUnwrappedPreservedWhenDisabledVpack();
        } finally {
        }
    }


    void __invoke_testPartialNonNullUnwrappedPreservedVpack() throws Exception {
        try {
            testPartialNonNullUnwrappedPreservedVpack();
        } finally {
        }
    }

}
