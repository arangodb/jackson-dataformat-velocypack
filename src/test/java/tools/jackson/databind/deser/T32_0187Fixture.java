package tools.jackson.databind.deser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonDeserializeAs;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.deser.std.StdDeserializer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import tools.jackson.dataformat.velocypack.*;

class T32_0187Fixture {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] ROOT_INTERFACE = VPackWireFixtureTest.hex(
            "14 09 41 61 43 61 62 63 01");
private static final byte[] ROOT_STRING = VPackWireFixtureTest.hex(
            "43 78 78 78");
private static final byte[] ROOT_MAP_CONTENT = VPackWireFixtureTest.hex(
            "14 07 41 61 41 62 01");
private static final byte[] ROOT_LIST_CONTENT = VPackWireFixtureTest.hex(
            "14 07 41 61 41 62 01");
private static final byte[] MAP_KEY = VPackWireFixtureTest.hex(
            "14 12 43 6d 61 70 14 0b 43 78 78 78 43 79 79 79 01 01");
private static final byte[] MAP_CONTENT = VPackWireFixtureTest.hex(
            "14 0d 43 6d 61 70 14 06 41 61 39 01 01");
private static final byte[] MAP_VALID = VPackWireFixtureTest.hex(
            "14 11 47 73 74 72 69 6e 67 73 14 06 41 61 33 01 01");
private static final byte[] RAW_LIST = VPackWireFixtureTest.hex(
            "14 1a 45 69 74 65 6d 73 13 11 14 0e 44 6e 61 6d 65 "
          + "45 69 74 65 6d 31 01 01 01");

    void testOverrideKeyClassValidOld() throws Exception {
        MapKeyHolder result = MAPPER.readValue(MAP_KEY, MapKeyHolder.class);
        Map<?, String> map = result.map;
        Map.Entry<?, String> entry = map.entrySet().iterator().next();

        assertEquals(1, map.size());
        StringWrapper key = (StringWrapper) entry.getKey();
        assertEquals(StringWrapper.class, key.getClass());
        assertEquals("xxx", key.value);
        assertEquals("yyy", entry.getValue());
    }

    void testOverrideKeyClassValidNew() throws Exception {
        MapKeyHolderNew result = MAPPER.readValue(MAP_KEY, MapKeyHolderNew.class);
        Map<?, String> map = result.map;
        Map.Entry<?, String> entry = map.entrySet().iterator().next();

        assertEquals(1, map.size());
        StringWrapper key = (StringWrapper) entry.getKey();
        assertEquals(StringWrapper.class, key.getClass());
        assertEquals("xxx", key.value);
        assertEquals("yyy", entry.getValue());
    }

    void testOverrideMapContents() throws Exception {
        MapContentHolder result = MAPPER.readValue(MAP_CONTENT, MapContentHolder.class);
        Object value = result.map.values().iterator().next();

        assertEquals(1, result.map.size());
        assertEquals(Integer.class, value.getClass());
        assertEquals(Integer.valueOf(9), value);
    }

    void testOverrideMapContentsNew() throws Exception {
        MapContentHolderNew result = MAPPER.readValue(MAP_CONTENT,
                MapContentHolderNew.class);
        Object value = result.map.values().iterator().next();

        assertEquals(1, result.map.size());
        assertEquals(Integer.class, value.getClass());
        assertEquals(Integer.valueOf(9), value);
    }

    void testOverrideMapValid() throws Exception {
        MapHolder result = MAPPER.readValue(MAP_VALID, MapHolder.class);

        assertEquals(TreeMap.class, result.data.getClass());
        assertEquals("3", result.data.get("a"));
    }

    void testOverrideMapValidNew() throws Exception {
        MapHolderNew result = MAPPER.readValue(MAP_VALID, MapHolderNew.class);

        assertEquals(TreeMap.class, result.data.getClass());
        assertEquals("3", result.data.get("a"));
    }

    void testRawListTypeContentAs() throws Exception {
        List2553 result = MAPPER.readValue(RAW_LIST, List2553.class);
        Object value = result.items.get(0);

        assertEquals(1, result.items.size());
        assertEquals(Item2553.class, value.getClass());
        assertEquals("item1", ((Item2553) value).name);
    }

    void testRootInterfaceAsNew() throws Exception {
        RootInterface2 value = MAPPER.readValue(ROOT_INTERFACE, RootInterface2.class);

        assertInstanceOf(RootInterfaceImpl.class, value);
        assertEquals("abc", value.getA());
    }

    void testRootInterfaceAsOld() throws Exception {
        RootInterface value = MAPPER.readValue(ROOT_INTERFACE, RootInterface.class);

        assertInstanceOf(RootInterfaceImpl.class, value);
        assertEquals("abc", value.getA());
    }

    void testRootInterfaceUsing() throws Exception {
        RootString value = MAPPER.readValue(ROOT_STRING, RootString.class);

        assertInstanceOf(RootString.class, value);
        assertEquals("xxx", value.contents());
    }

    void testRootListAsNew() throws Exception {
        RootMap2 value = MAPPER.readValue(ROOT_MAP_CONTENT, RootMap2.class);
        Object content = value.get("a");

        assertEquals(1, value.size());
        assertEquals(RootStringImpl.class, content.getClass());
        assertEquals("b", ((RootString) content).contents());
    }

    void testRootListAsOld() throws Exception {
        RootMap value = MAPPER.readValue(ROOT_LIST_CONTENT, RootMap.class);
        Object content = value.get("a");

        assertEquals(1, value.size());
        assertEquals(RootStringImpl.class, content.getClass());
        assertEquals("b", ((RootString) content).contents());
    }
@JsonDeserialize(as = RootInterfaceImpl.class)
    interface RootInterface {
        String getA();
    }
@JsonDeserializeAs(value = RootInterfaceImpl.class)
    interface RootInterface2 {
        String getA();
    }
static class RootInterfaceImpl implements RootInterface, RootInterface2 {
        public String a;

        @Override
        public String getA() {
            return a;
        }
    }
@JsonDeserialize(using = RootStringDeserializer.class)
    interface RootString {
        String contents();
    }
static class RootStringImpl implements RootString {
        private final String value;

        RootStringImpl(String value) {
            this.value = value;
        }

        @Override
        public String contents() {
            return value;
        }
    }
static class RootStringDeserializer extends StdDeserializer<RootString> {
        RootStringDeserializer() {
            super(RootString.class);
        }

        @Override
        public RootString deserialize(JsonParser parser, DeserializationContext ctxt) {
            if (parser.hasToken(JsonToken.VALUE_STRING)) {
                return new RootStringImpl(parser.getString());
            }
            return (RootString) ctxt.handleUnexpectedToken(getValueType(ctxt), parser);
        }
    }
@JsonDeserialize(contentAs = RootStringImpl.class)
    static class RootMap extends HashMap<String, RootStringImpl> { }
@JsonDeserializeAs(content = RootStringImpl.class)
    static class RootMap2 extends HashMap<String, RootStringImpl> { }
static class StringWrapper {
        final String value;

        StringWrapper(String value) {
            this.value = value;
        }
    }
static class MapKeyHolder {
        Map<Object, String> map;

        @JsonDeserialize(keyAs = StringWrapper.class)
        public void setMap(Map<Object, String> value) {
            map = value;
        }
    }
static class MapKeyHolderNew {
        Map<Object, String> map;

        @JsonDeserializeAs(keys = StringWrapper.class)
        public void setMap(Map<Object, String> value) {
            map = value;
        }
    }
static class MapHolder {
        Map<String, String> data;

        @JsonDeserialize(as = TreeMap.class)
        public void setStrings(Map<String, String> value) {
            data = value;
        }
    }
static class MapHolderNew {
        Map<String, String> data;

        @JsonDeserializeAs(TreeMap.class)
        public void setStrings(Map<String, String> value) {
            data = value;
        }
    }
static class MapContentHolder {
        Map<Object, Object> map;

        @JsonDeserialize(contentAs = Integer.class)
        public void setMap(Map<Object, Object> value) {
            map = value;
        }
    }
static class MapContentHolderNew {
        Map<Object, Object> map;

        @JsonDeserializeAs(content = Integer.class)
        public void setMap(Map<Object, Object> value) {
            map = value;
        }
    }
static class List2553 {
        @JsonDeserialize(contentAs = Item2553.class)
        public List items;
    }
static class Item2553 {
        public String name;
    }

    void __invoke_testOverrideKeyClassValidOld() throws Exception {
        try {
            testOverrideKeyClassValidOld();
        } finally {
        }
    }


    void __invoke_testOverrideKeyClassValidNew() throws Exception {
        try {
            testOverrideKeyClassValidNew();
        } finally {
        }
    }


    void __invoke_testOverrideMapContents() throws Exception {
        try {
            testOverrideMapContents();
        } finally {
        }
    }


    void __invoke_testOverrideMapContentsNew() throws Exception {
        try {
            testOverrideMapContentsNew();
        } finally {
        }
    }


    void __invoke_testOverrideMapValid() throws Exception {
        try {
            testOverrideMapValid();
        } finally {
        }
    }


    void __invoke_testOverrideMapValidNew() throws Exception {
        try {
            testOverrideMapValidNew();
        } finally {
        }
    }


    void __invoke_testRawListTypeContentAs() throws Exception {
        try {
            testRawListTypeContentAs();
        } finally {
        }
    }


    void __invoke_testRootInterfaceAsNew() throws Exception {
        try {
            testRootInterfaceAsNew();
        } finally {
        }
    }


    void __invoke_testRootInterfaceAsOld() throws Exception {
        try {
            testRootInterfaceAsOld();
        } finally {
        }
    }


    void __invoke_testRootInterfaceUsing() throws Exception {
        try {
            testRootInterfaceUsing();
        } finally {
        }
    }


    void __invoke_testRootListAsNew() throws Exception {
        try {
            testRootListAsNew();
        } finally {
        }
    }


    void __invoke_testRootListAsOld() throws Exception {
        try {
            testRootListAsOld();
        } finally {
        }
    }

}
