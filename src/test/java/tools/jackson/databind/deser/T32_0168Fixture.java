package tools.jackson.databind.deser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.exc.InvalidDefinitionException;
import tools.jackson.databind.exc.UnrecognizedPropertyException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0168Fixture {
private static final byte[] SIMPLE_AUTO_DETECT = VPackWireFixtureTest.hex(
            "0b 08 01 41 78 20 f3 03");
private static final byte[] SIMPLE_ANNOTATION = VPackWireFixtureTest.hex(
            "0b 11 01 46 76 61 6c 75 65 73 02 06 41 78 41 79 03");
private static final byte[] NO_AUTO_DETECT = VPackWireFixtureTest.hex(
            "0b 07 01 41 7a 37 03");
private static final byte[] RESOLVED_DUPS = VPackWireFixtureTest.hex(
            "0b 07 01 41 7a 33 03");
private static final byte[] OK_FIELD_OVERRIDE = VPackWireFixtureTest.hex(
            "0b 0b 02 41 79 32 41 78 31 06 03");
private static final byte[] SETTERLESS_COLLECTION = VPackWireFixtureTest.hex(
            "0b 15 01 46 76 61 6c 75 65 73 02 0a 43 61 62 63 43 64 65 66 03");
private static final byte[] SETTERLESS_MAP = VPackWireFixtureTest.hex(
            "0b 17 01 46 76 61 6c 75 65 73 0b 0c 02 41 61 28 0f 41 62 3d 03 07 03");
private static final byte[] SETTERLESS_PRECEDENCE = VPackWireFixtureTest.hex(
            "0b 0e 01 44 6c 69 73 74 02 05 31 32 33 03");
private static final byte[] OVERRIDE = VPackWireFixtureTest.hex(
            "0b 0e 01 45 76 61 6c 75 65 43 78 79 7a 03");
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final ObjectMapper MAPPER = new VPackMapper();

    void testSimpleAutoDetect() throws Exception {
        SimpleFieldBean result = MAPPER.readValue(SIMPLE_AUTO_DETECT,
                SimpleFieldBean.class);
        assertEquals(-13, result.x);
        assertEquals(0, result.y);
    }

    void testSimpleAnnotation() throws Exception {
        SimpleFieldBean2 bean = MAPPER.readValue(SIMPLE_ANNOTATION,
                SimpleFieldBean2.class);
        assertNotNull(bean.values);
        assertEquals(2, bean.values.length);
        assertEquals("x", bean.values[0]);
        assertEquals("y", bean.values[1]);
    }

    void testNoAutoDetect() throws Exception {
        NoAutoDetectBean bean = MAPPER.readValue(NO_AUTO_DETECT,
                NoAutoDetectBean.class);
        assertEquals(7, bean._z);
    }

    void testResolvedDups1() throws Exception {
        DupFieldBean result = MAPPER.readValue(RESOLVED_DUPS, DupFieldBean.class);
        assertEquals(3, result._z);
        assertEquals(0, result.z);
    }

    void testOkFieldOverride() throws Exception {
        OkDupFieldBean result = MAPPER.readValue(OK_FIELD_OVERRIDE,
                OkDupFieldBean.class);
        assertEquals(1, result.myX);
        assertEquals(2, result.y);
    }

    void testSimpleSetterlessCollectionOk() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(MapperFeature.USE_GETTERS_AS_SETTERS)
                .build();
        CollectionBean result = mapper.readValue(SETTERLESS_COLLECTION,
                CollectionBean.class);
        assertEquals(List.of("abc", "def"), result._values);
    }

    void testSimpleSetterlessCollectionFailure() {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();
        assertThrows(UnrecognizedPropertyException.class,
                () -> mapper.readValue(SETTERLESS_COLLECTION, CollectionBean.class));
    }

    void testSimpleSetterlessMapOk() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(MapperFeature.USE_GETTERS_AS_SETTERS)
                .build();
        MapBean result = mapper.readValue(SETTERLESS_MAP, MapBean.class);
        assertEquals(Map.of("a", 15, "b", -3), result._values);
    }

    void testSimpleSetterlessMapFailure() {
        ObjectMapper mapper = VPackMapper.builder()
                .disable(MapperFeature.USE_GETTERS_AS_SETTERS)
                .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();
        assertThrows(UnrecognizedPropertyException.class,
                () -> mapper.readValue(SETTERLESS_MAP, MapBean.class));
    }

    void testSetterlessPrecedence() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(MapperFeature.USE_GETTERS_AS_SETTERS)
                .build();
        Dual value = mapper.readValue(SETTERLESS_PRECEDENCE, Dual.class);
        assertNotNull(value);
        assertEquals(List.of(1, 2, 3), value.values);
    }

    void testOverride() throws Exception {
        WasNumberBean bean = MAPPER.readValue(OVERRIDE, WasNumberBean.class);
        assertNotNull(bean);
        assertEquals("xyz", bean.value);
    }

    void testSetterConflict() {
        InvalidDefinitionException exception = assertThrows(
                InvalidDefinitionException.class,
                () -> MAPPER.readValue(EMPTY_OBJECT, ConflictBean.class));
        assertEquals(true, exception.getMessage().contains(
                "Conflicting setter definitions"));
    }
static class SimpleFieldBean {
        public int x, y;

        int z;

        @JsonIgnore
        public int a;
    }
static class SimpleFieldBean2 {
        @JsonDeserialize
        String[] values;
    }
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE)
    static class NoAutoDetectBean {
        public int z;

        @JsonProperty("z")
        public int _z;
    }
static class DupFieldBean {
        public int z;

        @JsonProperty("z")
        public int _z;
    }
static class OkDupFieldBean extends SimpleFieldBean {
        @JsonProperty("x")
        protected int myX = 10;

        @SuppressWarnings("hiding")
        public int y = 11;
    }
static class CollectionBean {
        List<String> _values = new ArrayList<>();

        public List<String> getValues() { return _values; }
    }
static class MapBean {
        Map<String, Integer> _values = new java.util.HashMap<>();

        public Map<String, Integer> getValues() { return _values; }
    }
static class Dual {
        @JsonProperty("list")
        protected List<Integer> values = new ArrayList<>();

        public List<Integer> getList() {
            throw new IllegalStateException("Should not get called");
        }
    }
static class BaseNumberBean {
        protected Object value;

        public void setValue(Number number) { value = number; }
    }
static class WasNumberBean extends BaseNumberBean {
        public void setValue(String string) { value = string; }
    }
static class ConflictBean {
        public void setA(ArrayList<Object> value) { }
        public void setA(LinkedList<Object> value) { }
    }

    void __invoke_testSimpleAutoDetect() throws Exception {
        try {
            testSimpleAutoDetect();
        } finally {
        }
    }


    void __invoke_testSimpleAnnotation() throws Exception {
        try {
            testSimpleAnnotation();
        } finally {
        }
    }


    void __invoke_testNoAutoDetect() throws Exception {
        try {
            testNoAutoDetect();
        } finally {
        }
    }


    void __invoke_testResolvedDups1() throws Exception {
        try {
            testResolvedDups1();
        } finally {
        }
    }


    void __invoke_testOkFieldOverride() throws Exception {
        try {
            testOkFieldOverride();
        } finally {
        }
    }


    void __invoke_testSimpleSetterlessCollectionOk() throws Exception {
        try {
            testSimpleSetterlessCollectionOk();
        } finally {
        }
    }


    void __invoke_testSimpleSetterlessCollectionFailure() throws Exception {
        try {
            testSimpleSetterlessCollectionFailure();
        } finally {
        }
    }


    void __invoke_testSimpleSetterlessMapOk() throws Exception {
        try {
            testSimpleSetterlessMapOk();
        } finally {
        }
    }


    void __invoke_testSimpleSetterlessMapFailure() throws Exception {
        try {
            testSimpleSetterlessMapFailure();
        } finally {
        }
    }


    void __invoke_testSetterlessPrecedence() throws Exception {
        try {
            testSetterlessPrecedence();
        } finally {
        }
    }


    void __invoke_testOverride() throws Exception {
        try {
            testOverride();
        } finally {
        }
    }


    void __invoke_testSetterConflict() throws Exception {
        try {
            testSetterConflict();
        } finally {
        }
    }

}
