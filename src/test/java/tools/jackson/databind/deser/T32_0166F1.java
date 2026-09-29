package tools.jackson.databind.deser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.JsonValue;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonDeserialize;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0166F1 {
private static final byte[] SIMPLE_MAP = VPackWireFixtureTest.hex(
            "0b 13 03 41 61 33 41 62 1a 41 63 02 05 31 32 33 03 06 09");
private static final byte[] TYPED_MAP = VPackWireFixtureTest.hex(
            "0b 0e 02 41 61 02 04 33 3f 41 62 01 03 09");
private static final byte[] NULL_MAP = VPackWireFixtureTest.hex(
            "0b 23 03 42 69 64 32 44 6e 61 6d 65 43 4a 6f 65 "
          + "44 63 69 74 79 4a 4e 65 77 20 4a 65 72 73 65 79 10 03 07");
private static final byte[] IGNORED_SENSITIVE = VPackWireFixtureTest.hex(
            "0b 31 03 44 6e 61 6d 65 45 61 6c 69 63 65 "
          + "4e 73 65 6e 73 69 74 69 76 65 46 69 65 6c 64 46 73 65 63 72 65 74 "
          + "45 6f 74 68 65 72 43 76 61 6c 03 24 0e");
private static final byte[] PROBLEM_744 = VPackWireFixtureTest.hex(
            "0b 18 02 44 6e 61 6d 65 43 42 6f 62 45 6f 74 68 65 72 43 76 61 6c "
          + "03 0c");
private static final byte[] POLYMORPHIC = VPackWireFixtureTest.hex(
            "0b 49 01 41 61 0b 43 02 46 40 63 6c 61 73 73 6c" +
                "74 6f 6f 6c 73 2e 6a 61 63 6b 73 6f 6e 2e 64 61" +
                "74 61 62 69 6e 64 2e 64 65 73 65 72 2e 54 33 32" +
                "5f 30 31 36 36 46 31 24 49 6d 70 6c 45 76 61 6c" +
                "75 65 43 78 79 7a 03 37 03");
private static final byte[] UNWRAPPED = VPackWireFixtureTest.hex(
            "0b 41 05 44 74 79 70 65 43 49 53 54 41 78 33 "
          + "49 5a 6f 6f 6d 4c 69 6e 6b 73 02 1c 4c 66 6f 6f 66 6f 6f 66 6f 6f 66 6f 6f "
          + "4c 62 61 72 62 61 72 62 61 72 62 61 72 41 79 34 41 7a 28 08 "
          + "0f 03 0c 35 38");
private static final byte[] WITH_ANY_SETTER = VPackWireFixtureTest.hex(
            "0b 0e 01 43 6b 65 79 45 76 61 6c 75 65 03");
private static final byte[] STRING_LIST = VPackWireFixtureTest.hex(
            "02 06 43 2e 2e 2e");
private static final byte[] BOOLEAN_LIST = VPackWireFixtureTest.hex(
            "02 03 19");
private static final byte[] RENAMED_Y = VPackWireFixtureTest.hex(
            "0b 07 01 41 79 30 03");
private static final byte[] DEFAULT_X = VPackWireFixtureTest.hex(
            "0b 07 01 41 78 30 03");
private static final ObjectMapper MAPPER = new VPackMapper();

    void testAnnotatedLStringList() throws Exception {
        AnnotatedStringList result = MAPPER.readValue(STRING_LIST,
                AnnotatedStringList.class);
        assertEquals(1, result.size());
        Object value = result.get(0);
        assertInstanceOf(StringWrapper.class, value);
        assertEquals("...", ((StringWrapper) value).str);
    }

    void testAnnotatedBooleanList() throws Exception {
        AnnotatedBooleanList result = MAPPER.readValue(BOOLEAN_LIST,
                AnnotatedBooleanList.class);
        assertEquals(1, result.size());
        Object value = result.get(0);
        assertInstanceOf(BooleanElement.class, value);
        assertFalse(((BooleanElement) value).b);
    }

    void testAnnotationsDisabled() throws Exception {
        assertTrue(MAPPER.deserializationConfig().isEnabled(MapperFeature.USE_ANNOTATIONS));
        AnnoBean result = MAPPER.readValue(RENAMED_Y, AnnoBean.class);
        assertEquals(0, result.value);

        ObjectMapper disabled = VPackMapper.builder()
                .disable(MapperFeature.USE_ANNOTATIONS)
                .build();
        result = disabled.readValue(DEFAULT_X, AnnoBean.class);
        assertEquals(0, result.value);
    }
static class MapImitator {
        final Map<String, Object> map = new HashMap<>();

        @JsonAnySetter
        void addEntry(String key, Object value) {
            map.put(key, value);
        }
    }
static class MapImitatorWithValue {
        final Map<String, int[]> map = new HashMap<>();

        @JsonAnySetter
        void addEntry(String key, int[] value) {
            map.put(key, value);
        }
    }
static class UserWithAnySetter5952 {
        public String name;

        @JsonIgnore
        public String sensitiveField;

        @JsonAnySetter
        public Map<String, Object> extras = new HashMap<>();
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
    static abstract class Base { }
static class Impl extends Base {
        public String value;
    }
static class PolyAnyBean {
        final Map<String, Base> props = new HashMap<>();

        @JsonAnyGetter
        public Map<String, Base> props() {
            return props;
        }

        @JsonAnySetter
        public void prop(String name, Base value) {
            props.put(name, value);
        }
    }
static class JsonAnySetterOnNullMap {
        public int id;

        @JsonAnySetter
        protected Map<String, String> other;
    }
static class JsonAnySetterOnCustomNullMap {
        @JsonAnySetter
        public CustomMap other;
    }
static class CustomMap extends java.util.LinkedHashMap<String, String> { }
static class Bean744 {
        protected Map<String, Object> additionalProperties;

        @JsonAnySetter
        public void addAdditionalProperty(String key, Object value) {
            if (additionalProperties == null) {
                additionalProperties = new HashMap<>();
            }
            additionalProperties.put(key, value);
        }

        @JsonAnyGetter
        public Map<String, Object> getAdditionalProperties() {
            return additionalProperties;
        }

        @JsonIgnore
        public String getName() {
            return (String) additionalProperties.get("name");
        }
    }
static class Bean349 {
        public String type;
        public int x, y;
        final Map<String, Object> props = new HashMap<>();

        @JsonAnySetter
        public void addProperty(String key, Object value) {
            props.put(key, value);
        }

        @JsonAnyGetter
        public Map<String, Object> getProperties() {
            return props;
        }

        @JsonUnwrapped
        public IdentityDTO349 identity;
    }
static class IdentityDTO349 {
        public int x, y;
    }
static class Problem4316 extends Exception {
        private static final long serialVersionUID = 1L;

        @JsonAnySetter
        @JsonAnyGetter
        Map<String, Object> additionalProperties = new HashMap<>();
    }
@JsonDeserialize(contentAs = StringWrapper.class)
    static class AnnotatedStringList extends ArrayList<Object> { }
@JsonDeserialize(contentAs = BooleanElement.class)
    static class AnnotatedBooleanList extends ArrayList<Object> { }
static class StringWrapper {
        public String str;

        public StringWrapper() { }
        public StringWrapper(String value) {
            str = value;
        }
    }
static class BooleanElement {
        public Boolean b;

        public BooleanElement() { }
        @JsonCreator
        public BooleanElement(Boolean value) {
            b = value;
        }

        @JsonValue
        public Boolean value() {
            return b;
        }
    }
static class AnnoBean {
        int value = 3;

        @JsonProperty("y")
        public void setX(int value) {
            this.value = value;
        }
    }

    void __invoke_testAnnotatedLStringList() throws Exception {
        try {
            testAnnotatedLStringList();
        } finally {
        }
    }


    void __invoke_testAnnotatedBooleanList() throws Exception {
        try {
            testAnnotatedBooleanList();
        } finally {
        }
    }


    void __invoke_testAnnotationsDisabled() throws Exception {
        try {
            testAnnotationsDisabled();
        } finally {
        }
    }

}
