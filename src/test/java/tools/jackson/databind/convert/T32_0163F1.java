package tools.jackson.databind.convert;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.module.SimpleModule;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0163F1 {
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

    void testBeanUpdate() throws Exception {
        Bean bean = new Bean();
        assertEquals("b", bean.b);
        assertEquals(3, bean.c.length);
        assertNull(bean.child);

        Object ob = MAPPER.readerForUpdating(bean).readValue(BEAN_UPDATE);
        assertSame(ob, bean);
        assertEquals("a", bean.a);
        assertEquals("x", bean.b);
        assertArrayEquals(new int[] { 4, 5 }, bean.c);

        Bean child = bean.child;
        assertNotNull(child);
        assertEquals("y", child.a);
        assertEquals("b", child.b);
        assertArrayEquals(new int[] { 1, 2, 3 }, child.c);
        assertNull(child.child);

        Bean b2 = MAPPER.readerForUpdating(null).forType(Bean.class)
                .readValue(BEAN_ABC);
        assertEquals("abc", b2.b);

        Bean b3 = MAPPER.readerForUpdating(b2).withValueToUpdate(null)
                .readValue(BEAN_XYZ);
        assertEquals("xyz", b3.b);
        assertFalse(b2 == b3);
    }

    void testListUpdateViaReader() throws Exception {
        List<String> strs = new ArrayList<>();
        strs.add("a");
        Object ob = MAPPER.readerForUpdating(strs).readValue(LIST_BCD);
        assertSame(strs, ob);
        assertEquals(4, strs.size());
        assertEquals("a", strs.get(0));
        assertEquals("b", strs.get(1));
        assertEquals("c", strs.get(2));
        assertEquals("d", strs.get(3));
    }

    void testIssue744() throws Exception {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(DataA.class, new DataADeserializer());
        ObjectMapper mapper = VPackMapper.builder().addModule(module).build();

        DataB dbNewViaBytes = mapper.readValue(DATA_B, DataB.class);
        assertEquals(5, dbNewViaBytes.da.i);
        assertEquals(13, dbNewViaBytes.k);

        JsonNode vpackNode = mapper.readTree(DATA_B);
        DataB dbNewViaNode = mapper.treeToValue(vpackNode, DataB.class);
        assertEquals(5, dbNewViaNode.da.i);
        assertEquals(13, dbNewViaNode.k);

        DataB dbUpdViaBytes = new DataB();
        DataB dbUpdViaNode = new DataB();
        assertEquals(1, dbUpdViaBytes.da.i);
        assertEquals(3, dbUpdViaBytes.k);
        mapper.readerForUpdating(dbUpdViaBytes).readValue(DATA_B);
        assertEquals(5, dbUpdViaBytes.da.i);
        assertEquals(13, dbUpdViaBytes.k);

        assertEquals(1, dbUpdViaNode.da.i);
        assertEquals(3, dbUpdViaNode.k);
        mapper.readerForUpdating(dbUpdViaNode).readValue(vpackNode);
        assertEquals(5, dbUpdViaNode.da.i);
        assertEquals(13, dbUpdViaNode.k);
    }

    void test1831UsingNode() throws Exception {
        JsonNode jsonNode = MAPPER.readTree(POLYMORPHIC_CAT);
        AnimalWrapper optionalCat = new AnimalWrapper();
        Object result = MAPPER.readerForUpdating(optionalCat).readValue(jsonNode);
        assertSame(optionalCat, result);
        assertNotNull(optionalCat.animal);
        assertEquals(Cat.class, optionalCat.animal.getClass());
    }

    void test1831UsingStringIsUnsupportedForBinaryVpack() {
        AnimalWrapper optionalCat = new AnimalWrapper();
        assertThrows(UnsupportedOperationException.class,
                () -> MAPPER.readerForUpdating(optionalCat)
                        .readValue("[\"child\",{}]"));
    }

    void testDeserializeAnonymousClassFails3229() {
        Object anonBean = new Object() {
            @SuppressWarnings("unused")
            public int value = 1;
        };
        DatabindException e = assertThrows(DatabindException.class,
                () -> MAPPER.readValue(ANONYMOUS_VALUE, anonBean.getClass()));
        assertEquals(true, e.getMessage().contains("Cannot construct instance of"));
        assertEquals(true, e.getMessage().contains("local/anonymous class"));
    }

    void testDeserializeLocalClassFails3229() {
        class LocalBean {
            @SuppressWarnings("unused")
            public int x = 1;
        }
        DatabindException e = assertThrows(DatabindException.class,
                () -> MAPPER.readValue(LOCAL_VALUE, LocalBean.class));
        assertEquals(true, e.getMessage().contains("Cannot construct instance of"));
        assertEquals(true, e.getMessage().contains("local/anonymous class"));
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

    void __invoke_testBeanUpdate() throws Exception {
        try {
            testBeanUpdate();
        } finally {
        }
    }


    void __invoke_testListUpdateViaReader() throws Exception {
        try {
            testListUpdateViaReader();
        } finally {
        }
    }


    void __invoke_testIssue744() throws Exception {
        try {
            testIssue744();
        } finally {
        }
    }


    void __invoke_test1831UsingNode() throws Exception {
        try {
            test1831UsingNode();
        } finally {
        }
    }


    void __invoke_test1831UsingStringIsUnsupportedForBinaryVpack() throws Exception {
        try {
            test1831UsingStringIsUnsupportedForBinaryVpack();
        } finally {
        }
    }


    void __invoke_testDeserializeAnonymousClassFails3229() throws Exception {
        try {
            testDeserializeAnonymousClassFails3229();
        } finally {
        }
    }


    void __invoke_testDeserializeLocalClassFails3229() throws Exception {
        try {
            testDeserializeLocalClassFails3229();
        } finally {
        }
    }

}
