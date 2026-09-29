package tools.jackson.databind.deser;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.exc.IgnoredPropertyException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0183F1 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] SIMPLE_ALIASES_FIELD = VPackWireFixtureTest.hex(
            "14 18 44 4e 61 6d 65 46 46 6f 6f 62 61 72 "
          + "41 61 33 43 78 79 7a 28 25 03");
private static final byte[] SIMPLE_ALIASES_METHOD = VPackWireFixtureTest.hex(
            "14 18 44 6e 61 6d 65 46 46 6f 6f 62 61 72 "
          + "41 61 33 43 58 79 7a 28 25 03");
private static final byte[] SIMPLE_ALIASES_CREATOR = VPackWireFixtureTest.hex(
            "14 18 44 6e 61 6d 65 46 46 6f 6f 62 61 72 "
          + "41 41 33 43 78 79 7a 28 25 03");
private static final byte[] POLYMORPHIC_ALIAS = VPackWireFixtureTest.hex(
            "14 1d 45 76 61 6c 75 65 13 14 42 61 62 "
          + "14 0e 42 6e 6d 43 42 6f 62 41 41 28 11 02 02 01");
private static final byte[] FACTORY_ALIAS = VPackWireFixtureTest.hex(
            "14 1c 4b 70 61 72 74 69 74 69 6f 6e 49 64 41 61 "
          + "46 75 73 65 72 49 64 43 31 32 33 02");
private static final byte[] CASE_INSENSITIVE_ALIAS = VPackWireFixtureTest.hex(
            "14 0d 44 4e 41 4d 45 44 74 65 73 74 01");
private static final byte[] FALLBACK_ALIASES = VPackWireFixtureTest.hex(
            "14 28 48 66 75 6c 6c 4e 61 6d 65 4e 46 61 73 74 65 72 20 4a 61 63 6b 73 6f 6e "
          + "44 6e 61 6d 65 47 4a 61 63 6b 73 6f 6e 02");
private static final byte[] OLD_NAME = VPackWireFixtureTest.hex(
            "14 11 47 6f 6c 64 4e 61 6d 65 45 68 65 6c 6c 6f 01");
private static final byte[] NEW_NAME = VPackWireFixtureTest.hex(
            "14 11 47 6e 65 77 4e 61 6d 65 45 68 65 6c 6c 6f 01");
private static final byte[] MULTI_ALIAS_A1 = VPackWireFixtureTest.hex(
            "14 0f 42 61 31 42 41 41 42 62 31 42 42 42 02");
private static final byte[] MULTI_ALIAS_A2 = VPackWireFixtureTest.hex(
            "14 0f 42 61 32 42 41 41 42 62 31 42 42 42 02");
private static final byte[] MULTI_ALIAS_PRIMARY = VPackWireFixtureTest.hex(
            "14 15 45 61 4e 61 6d 65 42 41 41 45 62 4e 61 6d 65 42 42 42 02");
private static final byte[] X_ONLY = VPackWireFixtureTest.hex(
            "14 06 41 78 33 01");
private static final byte[] X_AND_IGNORED_Y = VPackWireFixtureTest.hex(
            "14 09 41 78 33 41 79 34 02");
private static final byte[] IGNORED_Z = VPackWireFixtureTest.hex(
            "14 06 41 7a 32 01");
private static final byte[] FORWARD_REFERENCE_ANY_SETTER = VPackWireFixtureTest.hex(
            "14 24 43 40 69 64 31 43 66 6f 6f 32 44 66 6f 6f 32 32 "
          + "43 62 61 72 14 0d 43 40 69 64 32 43 66 6f 6f 31 02 04");

    void testFailOnIgnore() {
        ObjectReader reader = MAPPER.readerFor(NoYOrZ.class)
                .with(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES);

        NoYOrZ result = MAPPER.readValue(X_ONLY, NoYOrZ.class);
        assertEquals(3, result.x);
        assertEquals(1, result.y);

        IgnoredPropertyException y = assertThrows(IgnoredPropertyException.class,
                () -> reader.readValue(X_AND_IGNORED_Y));
        assertEquals(true, y.getMessage().contains("Ignored field"));
        IgnoredPropertyException z = assertThrows(IgnoredPropertyException.class,
                () -> reader.readValue(IGNORED_Z));
        assertEquals(true, z.getMessage().contains("Ignored field"));
    }

    void testForwardReferenceAnySetterComboWithInclude() {
        AnySetterObjectId value = MAPPER.readValue(FORWARD_REFERENCE_ANY_SETTER,
                AnySetterObjectId.class);
        assertSame(value.values.get("bar"), value.values.get("foo"));
        assertFalse(value.values.containsKey("foo2"));
    }
static class AliasBean {
        @JsonAlias({ "nm", "Name" })
        public String name;
        int _xyz;
        int _a;

        @JsonCreator
        public AliasBean(@JsonProperty("a") @JsonAlias("A") int a) {
            _a = a;
        }

        @JsonAlias({ "Xyz" })
        public void setXyz(int x) {
            _xyz = x;
        }
    }
static class PolyWrapperForAlias {
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_ARRAY)
        @JsonSubTypes(@JsonSubTypes.Type(value = AliasBean.class, name = "ab"))
        public Object value;
    }
static class AliasBean2378 {
        String partitionId;
        String _id;

        private AliasBean2378(boolean bogus, String partId, String userId) {
            partitionId = partId;
            _id = userId;
        }

        @JsonCreator
        public static AliasBean2378 create(@JsonProperty("partitionId") String partId,
                @JsonProperty("id") @JsonAlias("userId") String userId) {
            return new AliasBean2378(false, partId, userId);
        }
    }
static class Pojo2669 {
        @JsonAlias({ "nick", "name" })
        private String name;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
static class AliasTestBeanA {
        @JsonAlias("fullName")
        public String name;
        @JsonAlias("fullName")
        public String fullName;
    }
static class Bean6031 {
        @JsonProperty("newName")
        private final String value;

        @JsonCreator
        public Bean6031(@JsonProperty("newName") @JsonAlias("oldName") String value) {
            this.value = value;
        }

        public String getValue() { return value; }

        @JsonIgnore
        public String getOldName() { return value; }
    }
public record Record6031(@JsonProperty("newName") @JsonAlias("oldName") String value) {
        @JsonIgnore
        public String getOldName() { return value; }
    }
@JsonIgnoreProperties("oldName")
    static class ClassIgnoreBean6031 {
        @JsonProperty("newName")
        private final String value;

        @JsonCreator
        public ClassIgnoreBean6031(@JsonProperty("newName") @JsonAlias("oldName") String value) {
            this.value = value;
        }

        public String getValue() { return value; }
    }
static class MultiAliasBean6031 {
        @JsonProperty("aName")
        private final String a;
        @JsonProperty("bName")
        private final String b;

        @JsonCreator
        public MultiAliasBean6031(
                @JsonProperty("aName") @JsonAlias({ "a1", "a2" }) String a,
                @JsonProperty("bName") @JsonAlias("b1") String b) {
            this.a = a;
            this.b = b;
        }

        public String getA() { return a; }
        public String getB() { return b; }

        @JsonIgnore public String getA1() { return a; }
        @JsonIgnore public String getA2() { return a; }
        @JsonIgnore public String getB1() { return b; }
    }
@JsonIgnoreProperties({ "z" })
    static class NoYOrZ {
        public int x;
        @JsonIgnore public int y = 1;
    }
@JsonIncludeProperties({ "foo", "bar" })
    @JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
    static class AnySetterObjectId {
        protected Map<String, AnySetterObjectId> values = new java.util.HashMap<>();

        @com.fasterxml.jackson.annotation.JsonAnySetter
        public void anySet(String field, AnySetterObjectId value) {
            values.put(field, value);
        }
    }

    void __invoke_testFailOnIgnore() throws Exception {
        try {
            testFailOnIgnore();
        } finally {
        }
    }


    void __invoke_testForwardReferenceAnySetterComboWithInclude() throws Exception {
        try {
            testForwardReferenceAnySetterComboWithInclude();
        } finally {
        }
    }

}
