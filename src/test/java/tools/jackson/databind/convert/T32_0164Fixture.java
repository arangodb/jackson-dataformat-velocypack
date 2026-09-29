package tools.jackson.databind.convert;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonView;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.KeyDeserializer;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.deser.std.StdNodeBasedDeserializer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertNotSame;

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

    void testMapUpdate() throws Exception {
        Map<String, String> strs = new HashMap<>();
        strs.put("a", "a");
        strs.put("b", "b");

        Object ob = MAPPER.readerForUpdating(strs).readValue(MAP_UPDATE);

        assertSame(strs, ob);
        assertEquals(3, strs.size());
        assertEquals("z", strs.get("a"));
        assertEquals("b", strs.get("b"));
        assertEquals("c", strs.get("c"));
    }

    void testReaderForUpdating3814() throws Exception {
        JsonNode root = MAPPER.readTree(AGE_30);
        Bean3814A obj = new Bean3814A(25);

        Bean3814A newObj = MAPPER.readerForUpdating(obj).readValue(root);

        assertSame(obj, newObj);
        assertEquals(30, newObj.age);
    }

    void testReaderForUpdating3814DoesNotOverride() throws Exception {
        JsonNode root = MAPPER.readTree(AGE_30);
        Bean3814B obj = new Bean3814B(25);

        Bean3814B newObj = MAPPER.readerForUpdating(obj).readValue(root);

        assertNotSame(obj, newObj);
        assertNull(newObj);
    }

    void testReaderForUpdatingAnonymousClass3229() throws Exception {
        Object anonBean = new Object() {
            @SuppressWarnings("unused")
            public int value = 1;
        };

        JsonNode root = MAPPER.readTree(ANONYMOUS_VALUE);
        assertEquals(42, root.get("value").asInt());
        Object result = MAPPER.readerForUpdating(anonBean)
                .readValue(ANONYMOUS_VALUE);

        assertSame(anonBean, result);
        assertEquals(42, anonBean.getClass().getField("value").getInt(anonBean));
    }

    void testReaderForUpdatingLocalClass3229() throws Exception {
        class LocalBean {
            public int x = 1;
            public String y = "original";
        }
        LocalBean localBean = new LocalBean();

        Object result = MAPPER.readerForUpdating(localBean).readValue(
                VPackWireFixtureTest.hex(
                        "0b 13 02 41 78 28 63 41 79 47 75 70 64 61 74 65 64 03 07"));

        assertSame(localBean, result);
        assertEquals(99, localBean.x);
        assertEquals("updated", localBean.y);
    }

    void testUpdateSequence() throws Exception {
        XYBean toUpdate = new XYBean();
        Iterator<XYBean> it = MAPPER.readerForUpdating(toUpdate)
                .readValues(UPDATE_SEQUENCE);

        assertTrueNext(it, toUpdate, 1, 2);
        assertTrueNext(it, toUpdate, 16, 2);
        assertTrueNext(it, toUpdate, 16, 37);
        assertFalse(it.hasNext());
    }

    void testUpdatingWithViews() throws Exception {
        Updateable bean = new Updateable();
        bean.num = 100;
        bean.str = "test";

        Updateable result = MAPPER.readerForUpdating(bean)
                .withView(TextView.class)
                .without(DeserializationFeature.FAIL_ON_UNEXPECTED_VIEW_PROPERTIES)
                .readValue(VIEW_UPDATE);

        assertSame(bean, result);
        assertEquals(100, bean.num);
        assertEquals("foobar", bean.str);
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

    void __invoke_testMapUpdate() throws Exception {
        try {
            testMapUpdate();
        } finally {
        }
    }


    void __invoke_testReaderForUpdating3814() throws Exception {
        try {
            testReaderForUpdating3814();
        } finally {
        }
    }


    void __invoke_testReaderForUpdating3814DoesNotOverride() throws Exception {
        try {
            testReaderForUpdating3814DoesNotOverride();
        } finally {
        }
    }


    void __invoke_testReaderForUpdatingAnonymousClass3229() throws Exception {
        try {
            testReaderForUpdatingAnonymousClass3229();
        } finally {
        }
    }


    void __invoke_testReaderForUpdatingLocalClass3229() throws Exception {
        try {
            testReaderForUpdatingLocalClass3229();
        } finally {
        }
    }


    void __invoke_testUpdateSequence() throws Exception {
        try {
            testUpdateSequence();
        } finally {
        }
    }


    void __invoke_testUpdatingWithViews() throws Exception {
        try {
            testUpdatingWithViews();
        } finally {
        }
    }

}
