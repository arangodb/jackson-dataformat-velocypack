package tools.jackson.databind.convert;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.deser.std.StdDeserializer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import tools.jackson.dataformat.velocypack.*;

class T32_0163F0 {
private static final byte[] BEAN_UPDATE = VPackWireFixtureTest.hex(
            "0b 1f 03 41 62 41 78 41 63 13 05 34 35 02 "
          + "45 63 68 69 6c 64 0b 08 01 41 61 41 79 03 03 07 0e");
private static final byte[] BEAN_ABC = VPackWireFixtureTest.hex(
            "0b 0a 01 41 62 43 61 62 63 03");
private static final byte[] BEAN_XYZ = VPackWireFixtureTest.hex(
            "0b 0a 01 41 62 43 78 79 7a 03");
private static final byte[] LIST_BCD = VPackWireFixtureTest.hex(
            "02 08 41 62 41 63 41 64");
private static final byte[] DATA_B = VPackWireFixtureTest.hex(
            "0b 18 02 42 64 61 0b 0c 02 41 69 20 0b 41 6a 32 03 07 "
          + "41 6b 28 0d 03 12");
private static final byte[] POLYMORPHIC_CAT = VPackWireFixtureTest.hex(
            "06 0c 02 45 63 68 69 6c 64 0a 03 09");
private static final byte[] ANONYMOUS_VALUE = VPackWireFixtureTest.hex(
            "0b 08 01 41 76 28 2a 03");
private static final byte[] LOCAL_VALUE = VPackWireFixtureTest.hex(
            "0b 08 01 41 78 28 2a 03");
private final ObjectMapper MAPPER = new VPackMapper();

    void testMapUpdate() throws Exception {
        Map<String, Object> base = new LinkedHashMap<>();
        base.put("a", 345);
        Map<String, Object> overrides = new LinkedHashMap<>();
        overrides.put("xyz", Boolean.TRUE);
        overrides.put("foo", "bar");

        Map<String, Object> ob = MAPPER.updateValue(base, overrides);
        assertSame(base, ob);
        assertEquals(3, ob.size());
        assertEquals(Integer.valueOf(345), ob.get("a"));
        assertEquals("bar", ob.get("foo"));
        assertEquals(Boolean.TRUE, ob.get("xyz"));
    }

    void testListUpdate() throws Exception {
        List<Object> base = new ArrayList<>();
        base.add(123456);
        base.add(Boolean.FALSE);
        Object[] overrides = new Object[] { Boolean.TRUE, "zoink!" };

        List<Object> ob = MAPPER.updateValue(base, overrides);
        assertSame(base, ob);
        assertEquals(4, ob.size());
        assertEquals(Integer.valueOf(123456), ob.get(0));
        assertEquals(Boolean.FALSE, ob.get(1));
        assertEquals(overrides[0], ob.get(2));
        assertEquals(overrides[1], ob.get(3));
    }

    void testArrayUpdate() throws Exception {
        Object[] base = new Object[] { Boolean.FALSE, Integer.valueOf(3) };
        Object[] overrides = new Object[] { Boolean.TRUE, "zoink!" };

        Object[] ob = MAPPER.updateValue(base, overrides);
        assertEquals(4, ob.length);
        assertEquals(base[0], ob[0]);
        assertEquals(base[1], ob[1]);
        assertEquals(overrides[0], ob[2]);
        assertEquals(overrides[1], ob[3]);
    }

    void testPOJO() throws Exception {
        Point base = new Point(42, 28);
        Map<String, Object> overrides = new LinkedHashMap<>();
        overrides.put("y", 1234);
        Point result = MAPPER.updateValue(base, overrides);
        assertSame(base, result);
        assertEquals(42, result.x);
        assertEquals(1234, result.y);
    }

    void testMisc() throws Exception {
        assertNull(MAPPER.updateValue(null, "foo"));
        List<String> input = new ArrayList<>();
        assertSame(input, MAPPER.updateValue(input, null));
    }
static class Bean {
        public String a = "a";
        public String b = "b";
        public int[] c = new int[] { 1, 2, 3 };
        public Bean child;
    }
static class Point {
        public int x;
        public int y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
static class DataA {
        public int i = 1;
        public int j = 2;
    }
static class DataB {
        public DataA da = new DataA();
        public int k = 3;
    }
static class DataADeserializer extends StdDeserializer<DataA> {
        DataADeserializer() {
            super(DataA.class);
        }

        @Override
        public DataA deserialize(JsonParser p, tools.jackson.databind.DeserializationContext ctxt) {
            ctxt.readTree(p);
            DataA da = new DataA();
            da.i = 5;
            return da;
        }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
    @JsonSubTypes(@JsonSubTypes.Type(value = Cat.class))
    abstract static class AbstractAnimal { }
@JsonTypeName("child")
    static class Cat extends AbstractAnimal { }
@JsonDeserialize(using = AnimalWrapperDeserializer.class)
    static class AnimalWrapper {
        @com.fasterxml.jackson.annotation.JsonUnwrapped
        protected AbstractAnimal animal;

        public void setAnimal(AbstractAnimal animal) {
            this.animal = animal;
        }
    }
static class AnimalWrapperDeserializer extends StdDeserializer<AnimalWrapper> {
        AnimalWrapperDeserializer() {
            super(AnimalWrapper.class);
        }

        @Override
        public AnimalWrapper deserialize(JsonParser p,
                tools.jackson.databind.DeserializationContext ctxt) {
            AnimalWrapper value = new AnimalWrapper();
            value.setAnimal(p.readValueAs(AbstractAnimal.class));
            return value;
        }

        @Override
        public AnimalWrapper deserialize(JsonParser p,
                tools.jackson.databind.DeserializationContext ctxt, AnimalWrapper intoValue) {
            intoValue.setAnimal(p.readValueAs(AbstractAnimal.class));
            return intoValue;
        }
    }

    void __invoke_testMapUpdate() throws Exception {
        try {
            testMapUpdate();
        } finally {
        }
    }


    void __invoke_testListUpdate() throws Exception {
        try {
            testListUpdate();
        } finally {
        }
    }


    void __invoke_testArrayUpdate() throws Exception {
        try {
            testArrayUpdate();
        } finally {
        }
    }


    void __invoke_testPOJO() throws Exception {
        try {
            testPOJO();
        } finally {
        }
    }


    void __invoke_testMisc() throws Exception {
        try {
            testMisc();
        } finally {
        }
    }

}
