package tools.jackson.databind.deser.creators;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonValue;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.util.TokenBuffer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import tools.jackson.dataformat.velocypack.*;

class T32_0204F1 {
private static final byte[] UNSUPPORTED_MAP_CONTAINER = VPackWireFixtureTest.hex(
            "0b 1d 02 44 6b 65 79 73 02 08 02 06 41 61 41 62 "
          + "46 76 61 6c 75 65 73 02 04 41 63 03 10");
private static final byte[] BOOLEAN_TRUE = VPackWireFixtureTest.hex("1a");
private static final byte[] BOOLEAN_STRING = VPackWireFixtureTest.hex("44 74 72 75 65");
private static final byte[] INTEGER_NEGATIVE = VPackWireFixtureTest.hex("20 f3");
private static final byte[] INTEGER_STRING = VPackWireFixtureTest.hex("43 31 32 37");
private static final byte[] LONG_POSITIVE = VPackWireFixtureTest.hex("28 0b");
private static final byte[] LONG_STRING = VPackWireFixtureTest.hex("43 2d 39 39");
private static final byte[] TOKEN_BUFFER_OBJECT = VPackWireFixtureTest.hex(
            "0b 0b 02 41 61 31 41 62 32 03 06");
private static final byte[] ISSUE465_OBJECT = VPackWireFixtureTest.hex(
            "0b 08 01 41 41 28 0c 03");
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] MULTIPLE_CREATORS_OBJECT = VPackWireFixtureTest.hex(
            "0b 17 02 44 6e 61 6d 65 45 42 69 6c 6c 79 "
          + "44 74 69 6d 65 28 7b 03 0e");
private static final byte[] MULTIPLE_CREATORS_STRING = VPackWireFixtureTest.hex(
            "43 42 6f 62");
private static final byte[] ABSTRACT_DELEGATE = VPackWireFixtureTest.hex(
            "0b 4f 02 46 40 63 6c 61 73 73 7d 74 6f 6f 6c 73" +
                "2e 6a 61 63 6b 73 6f 6e 2e 64 61 74 61 62 69 6e" +
                "64 2e 64 65 73 65 72 2e 63 72 65 61 74 6f 72 73" +
                "2e 54 33 32 5f 30 32 30 34 46 31 24 49 73 73 75" +
                "65 35 38 30 49 6d 70 6c 42 69 64 28 0d 03 48");
private static final byte[] EXTERNAL_DELEGATE = VPackWireFixtureTest.hex(
            "0b 63 02 44 68 65 72 6f 0b 12 01 44 6e 61 6d 65" +
                "48 73 75 70 65 72 6d 61 6e 03 48 68 65 72 6f 54" +
                "79 70 65 7d 74 6f 6f 6c 73 2e 6a 61 63 6b 73 6f" +
                "6e 2e 64 61 74 61 62 69 6e 64 2e 64 65 73 65 72" +
                "2e 63 72 65 61 74 6f 72 73 2e 54 33 32 5f 30 32" +
                "30 34 46 31 24 53 75 70 65 72 6d 61 6e 31 30 30" +
                "33 03 1a");
private static final ObjectMapper MAPPER = VPackMapper.builder().build();

    // Provenance: DelegatingCreatorsTest#testAbstractDelegateWithCreator.
    void testAbstractDelegateWithCreator() throws Exception {
        Issue580Bean result = MAPPER.readValue(ABSTRACT_DELEGATE, Issue580Bean.class);
        assertNotNull(result);
        assertNotNull(result.value);
        assertEquals(13, ((Issue580Impl) result.value).id);
    }

    // Provenance: DelegatingCreatorsTest#testBooleanDelegate.
    void testBooleanDelegate() throws Exception {
        BooleanBean result = MAPPER.readValue(BOOLEAN_TRUE, BooleanBean.class);
        assertEquals(Boolean.TRUE, result.value);
        result = MAPPER.readValue(BOOLEAN_STRING, BooleanBean.class);
        assertEquals(Boolean.TRUE, result.value);
    }

    // Provenance: DelegatingCreatorsTest#testDelegateWithTokenBuffer.
    void testDelegateWithTokenBuffer() throws Exception {
        Value592 value = MAPPER.readValue(TOKEN_BUFFER_OBJECT, Value592.class);
        assertNotNull(value);
        assertInstanceOf(TokenBuffer.class, value.stuff);
        try (JsonParser parser = ((TokenBuffer) value.stuff).asParser(
                tools.jackson.core.ObjectReadContext.empty())) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("a", parser.currentName());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(1, parser.getIntValue());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("b", parser.currentName());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(2, parser.getIntValue());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
        }
    }

    // Provenance: DelegatingCreatorsTest#testExtrnalPropertyDelegatingCreator.
    void testExtrnalPropertyDelegatingCreator() throws Exception {
        HeroBattle1003 battle = MAPPER.readValue(EXTERNAL_DELEGATE, HeroBattle1003.class);
        assertInstanceOf(Superman1003.class, battle.getHero());
    }

    // Provenance: DelegatingCreatorsTest#testIntegerDelegate.
    void testIntegerDelegate() throws Exception {
        IntegerBean result = MAPPER.readValue(INTEGER_NEGATIVE, IntegerBean.class);
        assertEquals(Integer.valueOf(-13), result.value);
        result = MAPPER.readValue(INTEGER_STRING, IntegerBean.class);
        assertEquals(Integer.valueOf(127), result.value);
    }

    // Provenance: DelegatingCreatorsTest#testIssue465.
    void testIssue465() throws Exception {
        @SuppressWarnings("unchecked")
        Map<String, Long> map = MAPPER.readValue(ISSUE465_OBJECT, Map.class);
        assertEquals(1, map.size());
        assertEquals(Integer.valueOf(12), map.get("A"));

        MapBean bean = MAPPER.readValue(ISSUE465_OBJECT, MapBean.class);
        assertEquals(1, bean.map.size());
        assertEquals(Long.valueOf(12L), bean.map.get("A"));

        map = MAPPER.readValue(EMPTY_OBJECT, Map.class);
        assertEquals(0, map.size());
        bean = MAPPER.readValue(EMPTY_OBJECT, MapBean.class);
        assertEquals(0, bean.map.size());
    }

    // Provenance: DelegatingCreatorsTest#testLongDelegate.
    void testLongDelegate() throws Exception {
        LongBean result = MAPPER.readValue(LONG_POSITIVE, LongBean.class);
        assertEquals(Long.valueOf(11L), result.value);
        result = MAPPER.readValue(LONG_STRING, LongBean.class);
        assertEquals(Long.valueOf(-99L), result.value);
    }

    // Provenance: DelegatingCreatorsTest#testMultipleCreators2353.
    void testMultipleCreators2353() throws Exception {
        SuperToken2353 result = MAPPER.readValue(MULTIPLE_CREATORS_STRING,
                SuperToken2353.class);
        assertEquals("Bob", result.username);

        result = MAPPER.readValue(MULTIPLE_CREATORS_OBJECT, SuperToken2353.class);
        assertEquals("Billy", result.username);
        assertEquals(123L, result.time);
    }

    // Provenance: DelegatingCreatorsTest#testNoFieldSingletonWithDefaultCreator.
    void testNoFieldSingletonWithDefaultCreator() throws Exception {
        NoFieldSingletonWithDefaultCreator result = MAPPER.readValue(EMPTY_OBJECT,
                NoFieldSingletonWithDefaultCreator.class);
        assertSame(NoFieldSingletonWithDefaultCreator.INSTANCE, result);
    }

    // Provenance: DelegatingCreatorsTest#testNoFieldSingletonWithDelegatingCreator.
    void testNoFieldSingletonWithDelegatingCreator() throws Exception {
        NoFieldSingletonWithDelegatingCreator result = MAPPER.readValue(EMPTY_OBJECT,
                NoFieldSingletonWithDelegatingCreator.class);
        assertSame(NoFieldSingletonWithDelegatingCreator.INSTANCE, result);
    }
static class Data3216 {
        @JsonIgnore
        public Map<String[], String> map = new HashMap<>();

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        public static Data3216 create(Container3216 container) {
            Data3216 result = new Data3216();
            for (int i = 0; i < container.keys.size(); ++i) {
                result.map.put(container.keys.get(i), container.values.get(i));
            }
            return result;
        }
    }
static class DataNoIgnore3216 {
        public Map<String[], String> map = new HashMap<>();

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        public static DataNoIgnore3216 create(Container3216 container) {
            DataNoIgnore3216 result = new DataNoIgnore3216();
            for (int i = 0; i < container.keys.size(); ++i) {
                result.map.put(container.keys.get(i), container.values.get(i));
            }
            return result;
        }
    }
static class Container3216 {
        public List<String[]> keys;
        public List<String> values;
    }
static class BooleanBean {
        protected Boolean value;

        public BooleanBean(Boolean value) { this.value = value; }

        @JsonCreator
        protected static BooleanBean create(Boolean value) { return new BooleanBean(value); }
    }
static class IntegerBean {
        protected Integer value;

        public IntegerBean(Integer value) { this.value = value; }

        @JsonCreator
        protected static IntegerBean create(Integer value) { return new IntegerBean(value); }
    }
static class LongBean {
        protected Long value;

        public LongBean(Long value) { this.value = value; }

        @JsonCreator
        protected static LongBean create(Long value) { return new LongBean(value); }
    }
static class Value592 {
        protected Object stuff;

        protected Value592(Object value, boolean ignored) { stuff = value; }

        @JsonCreator
        public static Value592 from(TokenBuffer buffer) { return new Value592(buffer, false); }
    }
static class MapBean {
        protected Map<String, Long> map;

        @JsonCreator
        public MapBean(Map<String, Long> map) { this.map = map; }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
    static abstract class Issue580Base { }
static class Issue580Impl extends Issue580Base {
        public int id;
    }
static class Issue580Bean {
        public Issue580Base value;

        @JsonCreator
        public Issue580Bean(Issue580Base value) { this.value = value; }

        @JsonValue
        public Issue580Base value() { return value; }
    }
static final class SuperToken2353 {
        public long time;
        public String username;

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        public static SuperToken2353 from(String username) {
            SuperToken2353 result = new SuperToken2353();
            result.username = username;
            return result;
        }

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public static SuperToken2353 create(@JsonProperty("name") String username,
                @JsonProperty("time") long time) {
            SuperToken2353 result = new SuperToken2353();
            result.username = username;
            result.time = time;
            return result;
        }
    }
static class HeroBattle1003 {
        private final Hero1003 hero;

        HeroBattle1003(Hero1003 hero) {
            if (hero == null) throw new IllegalArgumentException();
            this.hero = hero;
        }

        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS,
                include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "heroType")
        public Hero1003 getHero() { return hero; }

        @JsonCreator
        static HeroBattle1003 fromJson(Delegate1003 json) { return new HeroBattle1003(json.hero); }
    }
interface Hero1003 { }
static class Delegate1003 {
        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS,
                include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "heroType")
        public Hero1003 hero;
    }
static class Superman1003 implements Hero1003 {
        public String name = "superman";
    }
static final class NoFieldSingletonWithDefaultCreator {
        static final NoFieldSingletonWithDefaultCreator INSTANCE =
                new NoFieldSingletonWithDefaultCreator();

        private NoFieldSingletonWithDefaultCreator() { }

        @JsonCreator
        static NoFieldSingletonWithDefaultCreator of() { return INSTANCE; }
    }
static final class NoFieldSingletonWithDelegatingCreator {
        static final NoFieldSingletonWithDelegatingCreator INSTANCE =
                new NoFieldSingletonWithDelegatingCreator();

        private NoFieldSingletonWithDelegatingCreator() { }

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        static NoFieldSingletonWithDelegatingCreator of() { return INSTANCE; }
    }

    void __invoke_testAbstractDelegateWithCreator() throws Exception {
        try {
            testAbstractDelegateWithCreator();
        } finally {
        }
    }


    void __invoke_testBooleanDelegate() throws Exception {
        try {
            testBooleanDelegate();
        } finally {
        }
    }


    void __invoke_testDelegateWithTokenBuffer() throws Exception {
        try {
            testDelegateWithTokenBuffer();
        } finally {
        }
    }


    void __invoke_testExtrnalPropertyDelegatingCreator() throws Exception {
        try {
            testExtrnalPropertyDelegatingCreator();
        } finally {
        }
    }


    void __invoke_testIntegerDelegate() throws Exception {
        try {
            testIntegerDelegate();
        } finally {
        }
    }


    void __invoke_testIssue465() throws Exception {
        try {
            testIssue465();
        } finally {
        }
    }


    void __invoke_testLongDelegate() throws Exception {
        try {
            testLongDelegate();
        } finally {
        }
    }


    void __invoke_testMultipleCreators2353() throws Exception {
        try {
            testMultipleCreators2353();
        } finally {
        }
    }


    void __invoke_testNoFieldSingletonWithDefaultCreator() throws Exception {
        try {
            testNoFieldSingletonWithDefaultCreator();
        } finally {
        }
    }


    void __invoke_testNoFieldSingletonWithDelegatingCreator() throws Exception {
        try {
            testNoFieldSingletonWithDelegatingCreator();
        } finally {
        }
    }

}
