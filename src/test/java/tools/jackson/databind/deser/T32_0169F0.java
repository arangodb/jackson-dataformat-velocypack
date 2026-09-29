package tools.jackson.databind.deser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.exc.DeferredBindingException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0169F0 {
private static final byte[] TYPE_ANNOTATION = VPackWireFixtureTest.hex(
            "0b 0e 01 45 76 61 6c 75 65 43 61 62 63 03");
private static final byte[] SPECIALIZATION = VPackWireFixtureTest.hex(
            "0b 11 01 44 6c 69 73 74 02 08 41 61 41 62 41 63 03");
private static final byte[] STATIC_SETTER = VPackWireFixtureTest.hex(
            "0b 07 01 41 78 33 03");
private static final byte[] MAP_INPUT = VPackWireFixtureTest.hex(
            "0b 0f 01 43 6d 61 70 0b 07 01 41 61 31 03 03");
private static final byte[] LIST_INPUT = VPackWireFixtureTest.hex(
            "0b 0c 01 44 6c 69 73 74 02 03 31 03");
private static final byte[] SUCCESSIVE_ALICE = VPackWireFixtureTest.hex(
            "0b 20 02 44 6e 61 6d 65 48 69 6e 76 61 6c 69 64 31 43 61 67 65 48 69 6e 76 61 6c 69 64 31 11 03");
private static final byte[] SUCCESSIVE_BOB = VPackWireFixtureTest.hex(
            "0b 20 02 44 6e 61 6d 65 48 69 6e 76 61 6c 69 64 32 43 61 67 65 48 69 6e 76 61 6c 69 64 32 11 03");
private static final byte[][] CONCURRENT_INPUTS = {
        VPackWireFixtureTest.hex("0b 20 02 44 6e 61 6d 65 48 69 6e 76 61 6c 69 64 30 43 61 67 65 48 69 6e 76 61 6c 69 64 30 11 03"),
        VPackWireFixtureTest.hex("0b 20 02 44 6e 61 6d 65 48 69 6e 76 61 6c 69 64 31 43 61 67 65 48 69 6e 76 61 6c 69 64 31 11 03"),
        VPackWireFixtureTest.hex("0b 20 02 44 6e 61 6d 65 48 69 6e 76 61 6c 69 64 32 43 61 67 65 48 69 6e 76 61 6c 69 64 32 11 03"),
        VPackWireFixtureTest.hex("0b 20 02 44 6e 61 6d 65 48 69 6e 76 61 6c 69 64 33 43 61 67 65 48 69 6e 76 61 6c 69 64 33 11 03"),
        VPackWireFixtureTest.hex("0b 20 02 44 6e 61 6d 65 48 69 6e 76 61 6c 69 64 34 43 61 67 65 48 69 6e 76 61 6c 69 64 34 11 03"),
        VPackWireFixtureTest.hex("0b 20 02 44 6e 61 6d 65 48 69 6e 76 61 6c 69 64 35 43 61 67 65 48 69 6e 76 61 6c 69 64 35 11 03"),
        VPackWireFixtureTest.hex("0b 20 02 44 6e 61 6d 65 48 69 6e 76 61 6c 69 64 36 43 61 67 65 48 69 6e 76 61 6c 69 64 36 11 03"),
        VPackWireFixtureTest.hex("0b 20 02 44 6e 61 6d 65 48 69 6e 76 61 6c 69 64 37 43 61 67 65 48 69 6e 76 61 6c 69 64 37 11 03"),
        VPackWireFixtureTest.hex("0b 20 02 44 6e 61 6d 65 48 69 6e 76 61 6c 69 64 38 43 61 67 65 48 69 6e 76 61 6c 69 64 38 11 03"),
        VPackWireFixtureTest.hex("0b 20 02 44 6e 61 6d 65 48 69 6e 76 61 6c 69 64 39 43 61 67 65 48 69 6e 76 61 6c 69 64 39 11 03")
    };
private static final ObjectMapper MAPPER = new VPackMapper();

    void testTypeAnnotation() throws Exception {
        AbstractWrapper wrapper = MAPPER.readValue(TYPE_ANNOTATION,
                AbstractWrapper.class);
        assertNotNull(wrapper.value);
        assertEquals(Concrete.class, wrapper.value.getClass());
        assertEquals("abc", ((Concrete) wrapper.value).value);
    }

    void testSpecialization() throws Exception {
        ArrayListBean bean = MAPPER.readValue(SPECIALIZATION, ArrayListBean.class);
        assertNotNull(bean.list);
        assertEquals(3, bean.list.size());
        assertEquals(ArrayList.class, bean.list.getClass());
        assertEquals(List.of("a", "b", "c"), bean.list);
    }

    void testStaticSetterIgnored() throws Exception {
        StaticSetterBean bean = MAPPER.readValue(STATIC_SETTER,
                StaticSetterBean.class);
        assertEquals(3, bean.x);
    }
private static DeferredBindingException expectDeferredBinding(ObjectReader reader,
            byte[] input) {
        try {
            reader.readValueCollectingProblems(input);
        } catch (DeferredBindingException e) {
            return e;
        } catch (Exception e) {
            throw new AssertionError("Unexpected exception", e);
        }
        throw new AssertionError("Expected DeferredBindingException");
    }
static class Abstract { }
static class Concrete extends Abstract {
        String value;

        public Concrete(String value) {
            this.value = value;
        }
    }
static class AbstractWrapper {
        @JsonDeserialize(as = Concrete.class)
        public Abstract value;
    }
static class BaseListBean {
        List<String> list;

        public void setList(List<String> value) {
            list = value;
        }
    }
static class ArrayListBean extends BaseListBean {
        public void setList(ArrayList<String> value) {
            super.setList(value);
        }
    }
static class StaticSetterBean {
        int x;

        public static void setX(int value) {
            throw new AssertionError("Static setter must not be called");
        }

        @com.fasterxml.jackson.annotation.JsonProperty("x")
        public void assignX(int value) {
            x = value;
        }
    }
public static class TestListWithCustom {
        @tools.jackson.databind.annotation.JsonDeserialize(
                contentUsing = CustomDeserializer.class)
        public List<Integer> list;
    }
public static class TestListNoCustom {
        public List<Integer> list;
    }
public static class TestMapWithCustom {
        @JsonDeserialize(contentUsing = CustomDeserializer.class)
        public Map<String, Integer> map;
    }
public static class TestMapNoCustom {
        public Map<String, Integer> map;
    }
public static class CustomDeserializer extends StdDeserializer<Integer> {
        public CustomDeserializer() {
            super(Integer.class);
        }

        @Override
        public Integer deserialize(JsonParser parser, DeserializationContext context) {
            return 100 * parser.getValueAsInt();
        }
    }
static class Person {
        public String name;
        public int age;
    }

    void __invoke_testTypeAnnotation() throws Exception {
        try {
            testTypeAnnotation();
        } finally {
        }
    }


    void __invoke_testSpecialization() throws Exception {
        try {
            testSpecialization();
        } finally {
        }
    }


    void __invoke_testStaticSetterIgnored() throws Exception {
        try {
            testStaticSetterIgnored();
        } finally {
        }
    }

}
