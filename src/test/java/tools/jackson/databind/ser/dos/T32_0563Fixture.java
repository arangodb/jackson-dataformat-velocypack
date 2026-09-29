package tools.jackson.databind.ser.dos;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonIgnore;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.exc.InvalidDefinitionException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0563Fixture {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final ObjectMapper STATIC_MAPPER = VPackMapper.builder()
            .enable(MapperFeature.USE_STATIC_TYPING)
            .build();
private static final byte[] SIMPLE_AUTO_DETECT = VPackWireFixtureTest.hex(
            "0b 0c 02 41 78 28 0d 41 79 30 03 07");
private static final byte[] STRING_ARRAY = VPackWireFixtureTest.hex(
            "06 0f 03 41 61 45 22 66 6f 6f 22 18 03 05 0b");
private static final byte[] EMPTY_STRING_ARRAY = VPackWireFixtureTest.hex("01");
private static final byte[] TRANSIENT_ONLY = VPackWireFixtureTest.hex(
            "0b 07 01 41 61 30 03");
private static final byte[] STATIC_FIELD_ONLY = VPackWireFixtureTest.hex(
            "0b 07 01 41 78 31 03");
private static final byte[] STATIC_METHOD_ONLY = VPackWireFixtureTest.hex(
            "0b 07 01 41 78 33 03");
private static final byte[] TOKEN_BUFFER_INPUT = VPackWireFixtureTest.hex(
            "0b 1b 02 44 6e 61 6d 65 45 76 70 61 63 6b "
          + "45 69 74 65 6d 73 02 05 31 1a 18 0e 03");

    // Provenance: CyclicDataSerTest#testLinkedAndCyclic().
    void testLinkedAndCyclicVpack() {
        CyclicDataBean bean = new CyclicDataBean(null, "last");
        bean.assignNext(bean);

        InvalidDefinitionException error = assertThrows(InvalidDefinitionException.class,
                () -> MAPPER.writeValueAsBytes(bean));
        assertTrue(error.getMessage().startsWith("Direct self-reference leading to cycle"));
    }
static class SimpleFieldBean {
        public int x, y;
        int z;
        @SuppressWarnings("unused")
        @JsonIgnore
        public int a;
    }
static class TransientBean {
        public int a;
        public transient int b;
        public static int c;
    }
static class FieldBeanWithStatic {
        public int x = 1;
        public static int y = 2;
        public static int z = 3;
    }
static class GetterBeanWithStatic {
        public int getX() { return 3; }
        public static int getA() { return -3; }
        public static int getFoo() { return 123; }
    }
static class SimpleCyclicBean {
        SimpleCyclicBean _next;
        final String _name;

        SimpleCyclicBean(SimpleCyclicBean next, String name) {
            _next = next;
            _name = name;
        }

        public SimpleCyclicBean getNext() { return _next; }
        public String getName() { return _name; }
        public void assignNext(SimpleCyclicBean next) { _next = next; }
    }
static class CyclicDataBean {
        CyclicDataBean _next;
        final String _name;

        CyclicDataBean(CyclicDataBean next, String name) {
            _next = next;
            _name = name;
        }

        public CyclicDataBean getNext() { return _next; }
        public String getName() { return _name; }
        public void assignNext(CyclicDataBean next) { _next = next; }
    }
static abstract class Base {
        public int a = 1;
    }
static class Derived extends Base {
        public int b = 2;
    }
@JsonSerialize(typing = JsonSerialize.Typing.DYNAMIC)
    static abstract class BaseDynamic {
        public int a = 3;
    }
static class DerivedDynamic extends BaseDynamic {
        public int b = 4;
    }
@JsonPropertyOrder({"value", "aValue", "dValue"})
    static class Issue1515Singles {
        public Base value = new Derived();
        @JsonSerialize(typing = JsonSerialize.Typing.DYNAMIC)
        public Base aValue = new Derived();
        public BaseDynamic dValue = new DerivedDynamic();
    }
@JsonPropertyOrder({"map", "aMap", "dMap"})
    static class Issue1515Maps {
        public Map<String, Base> map = Map.of("x", new Derived());
        @JsonSerialize(typing = JsonSerialize.Typing.DYNAMIC)
        public Map<String, Base> aMap = Map.of("x", new Derived());
        public Map<String, BaseDynamic> dMap = Map.of("x", new DerivedDynamic());
    }
@JsonPropertyOrder({"list", "aList", "dList"})
    static class Issue1515Lists {
        public List<Base> list = List.of(new Derived());
        @JsonSerialize(typing = JsonSerialize.Typing.DYNAMIC)
        public List<Base> aList = List.of(new Derived());
        public List<BaseDynamic> dList = List.of(new DerivedDynamic());
    }
@JsonPropertyOrder({"array", "aArray", "dArray"})
    static class Issue1515Arrays {
        public Base[] array = new Base[] { new Derived() };
        @JsonSerialize(typing = JsonSerialize.Typing.DYNAMIC)
        public Base[] aArray = new Base[] { new Derived() };
        public BaseDynamic[] dArray = new BaseDynamic[] { new DerivedDynamic() };
    }

    void __invoke_testLinkedAndCyclicVpack() throws Exception {
        try {
            testLinkedAndCyclicVpack();
        } finally {
        }
    }

}
