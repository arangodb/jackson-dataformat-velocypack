package tools.jackson.databind.deser;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonView;
import tools.jackson.databind.KeyDeserializer;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.deser.std.StdNodeBasedDeserializer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import tools.jackson.dataformat.velocypack.*;

class T32_0164Fixture {
private static final byte[] MAP_UPDATE = VPackWireFixtureTest.hex(
            "0b 0d 02 41 63 41 63 41 61 41 7a 07 03");
private static final byte[] AGE_30 = VPackWireFixtureTest.hex(
            "0b 0a 01 43 61 67 65 28 1e 03");
private static final byte[] ANONYMOUS_VALUE = VPackWireFixtureTest.hex(
            "0b 0c 01 45 76 61 6c 75 65 28 2a 03");
private static final byte[] UPDATE_SEQUENCE = VPackWireFixtureTest.hex(
            "0b 0b 02 41 78 31 41 79 32 03 06 "
          + "0b 08 01 41 78 28 10 03 "
          + "0b 08 01 41 79 28 25 03");
private static final byte[] VIEW_UPDATE = VPackWireFixtureTest.hex(
            "0b 16 02 43 6e 75 6d 28 0a 43 73 74 72 46 66 6f 6f 62 61 72 03 09");
private static final byte[] VALUE_123 = VPackWireFixtureTest.hex("28 7b");
private static final byte[] VALUES_ARRAY = VPackWireFixtureTest.hex(
            "0b 10 01 46 76 61 6c 75 65 73 02 05 31 32 33 03");
private static final byte[] VALUES_MAP = VPackWireFixtureTest.hex(
            "0b 16 01 46 76 61 6c 75 65 73 "
          + "0b 0b 02 41 61 31 41 62 32 03 06 03");
private static final byte[] KEY_MAP = VPackWireFixtureTest.hex(
            "0b 12 01 46 76 61 6c 75 65 73 "
          + "0b 07 01 41 61 1a 03 03");
private final ObjectMapper MAPPER = new VPackMapper();

    void testArrayContentUsing() throws Exception {
        ArrayBean result = MAPPER.readValue(VALUES_ARRAY, ArrayBean.class);

        assertNotNull(result);
        assertEquals(3, result.values.length);
        assertValue(result.values[0], 1);
        assertValue(result.values[1], 2);
        assertValue(result.values[2], 3);
    }

    void testClassDeserializer() throws Exception {
        ValueClass result = MAPPER.readValue(VALUE_123, ValueClass.class);
        assertEquals(123, result.value);
    }

    void testListContentUsing() throws Exception {
        ListBean result = MAPPER.readValue(VALUES_ARRAY, ListBean.class);

        assertNotNull(result);
        assertEquals(3, result.values.size());
        assertValue(result.values.get(0), 1);
        assertValue(result.values.get(1), 2);
        assertValue(result.values.get(2), 3);
    }

    void testMapContentUsing() throws Exception {
        MapBean result = MAPPER.readValue(VALUES_MAP, MapBean.class);

        assertNotNull(result);
        assertEquals(2, result.values.size());
        assertValue(result.values.get("a"), 1);
        assertValue(result.values.get("b"), 2);
    }

    void testMapKeyUsing() throws Exception {
        MapKeyBean result = MAPPER.readValue(KEY_MAP, MapKeyBean.class);

        assertNotNull(result);
        assertEquals(1, result.values.size());
        Map.Entry<Object, Object> entry = result.values.entrySet().iterator().next();
        assertEquals(String[].class, entry.getKey().getClass());
        assertEquals(Boolean.TRUE, entry.getValue());
    }
private static void assertTrueNext(Iterator<XYBean> iterator, XYBean expected,
            int x, int y) {
        org.junit.jupiter.api.Assertions.assertTrue(iterator.hasNext());
        XYBean value = iterator.next();
        assertSame(expected, value);
        assertEquals(x, value.x);
        assertEquals(y, value.y);
    }
private static void assertValue(Object value, int expected) {
        assertEquals(ValueClass.class, value.getClass());
        assertEquals(expected, ((ValueClass) value).value);
    }
static class XYBean {
        public int x;
        public int y;
    }
static class TextView { }
static class NumView { }
static class Updateable {
        @JsonView(NumView.class)
        public int num;

        @JsonView(TextView.class)
        public String str;
    }
@JsonDeserialize(using = Custom3814DeserializerA.class)
    static class Bean3814A {
        public int age;

        Bean3814A(int age) {
            this.age = age;
        }

        void updateTo(JsonNode root) {
            age = root.get("age").asInt();
        }
    }
static class Custom3814DeserializerA extends StdNodeBasedDeserializer<Bean3814A> {
        Custom3814DeserializerA() {
            super(Bean3814A.class);
        }

        @Override
        public Bean3814A convert(JsonNode root, tools.jackson.databind.DeserializationContext ctxt) {
            return null;
        }

        @Override
        public Bean3814A convert(JsonNode root,
                tools.jackson.databind.DeserializationContext ctxt, Bean3814A oldValue) {
            oldValue.updateTo(root);
            return oldValue;
        }
    }
@JsonDeserialize(using = Custom3814DeserializerB.class)
    static class Bean3814B {
        public int age;

        Bean3814B(int age) {
            this.age = age;
        }
    }
static class Custom3814DeserializerB extends StdNodeBasedDeserializer<Bean3814B> {
        Custom3814DeserializerB() {
            super(Bean3814B.class);
        }

        @Override
        public Bean3814B convert(JsonNode root, tools.jackson.databind.DeserializationContext ctxt) {
            return null;
        }
    }
@JsonDeserialize(using = ValueDeserializer.class)
    static class ValueClass {
        int value;

        ValueClass(int value) {
            this.value = value;
        }
    }
static class ValueDeserializer extends StdDeserializer<ValueClass> {
        ValueDeserializer() {
            super(ValueClass.class);
        }

        @Override
        public ValueClass deserialize(tools.jackson.core.JsonParser parser,
                tools.jackson.databind.DeserializationContext ctxt) {
            int value = parser.getIntValue();
            return new ValueClass(value);
        }
    }
static class ArrayBean {
        @JsonDeserialize(contentUsing = ValueDeserializer.class)
        public Object[] values;
    }
static class ListBean {
        @JsonDeserialize(contentUsing = ValueDeserializer.class)
        public List<Object> values;
    }
static class MapBean {
        @JsonDeserialize(contentUsing = ValueDeserializer.class)
        public Map<String, Object> values;
    }
static class MapKeyBean {
        @JsonDeserialize(keyUsing = MapKeyDeserializer.class)
        public Map<Object, Object> values;
    }
static class MapKeyDeserializer extends KeyDeserializer {
        @Override
        public Object deserializeKey(String key, tools.jackson.databind.DeserializationContext ctxt) {
            return new String[] { key };
        }
    }

    void __invoke_testArrayContentUsing() throws Exception {
        try {
            testArrayContentUsing();
        } finally {
        }
    }


    void __invoke_testClassDeserializer() throws Exception {
        try {
            testClassDeserializer();
        } finally {
        }
    }


    void __invoke_testListContentUsing() throws Exception {
        try {
            testListContentUsing();
        } finally {
        }
    }


    void __invoke_testMapContentUsing() throws Exception {
        try {
            testMapContentUsing();
        } finally {
        }
    }


    void __invoke_testMapKeyUsing() throws Exception {
        try {
            testMapKeyUsing();
        } finally {
        }
    }

}
