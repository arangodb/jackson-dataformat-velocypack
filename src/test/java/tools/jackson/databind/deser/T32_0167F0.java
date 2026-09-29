package tools.jackson.databind.deser;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonDeserialize;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0167F0 {
private static final byte[] SIMPLE_SETTER = VPackWireFixtureTest.hex(
            "0b 1d 03 45 6f 74 68 65 72 33 44 73 69 7a 65 32 "
          + "46 6c 65 6e 67 74 68 21 19 fc 10 03 0a");
private static final byte[] SIMPLE_SETTER_2 = VPackWireFixtureTest.hex(
            "0b 07 01 41 78 3d 03");
private static final byte[] SIMPLE_SETTER_3 = VPackWireFixtureTest.hex(
            "0b 08 01 41 78 28 80 03");
private static final byte[] SETTER_INHERITANCE = VPackWireFixtureTest.hex(
            "0b 0f 03 41 78 31 41 7a 33 41 79 32 03 09 06");
private static final byte[] IMPLIED_PROPERTY = VPackWireFixtureTest.hex(
            "0b 07 01 41 61 33 03");
private static final byte[] PRIVATE_UNWRAPPED = VPackWireFixtureTest.hex(
            "0b 07 01 41 69 35 03");
private static final byte[] LIST_SUBCLASS = VPackWireFixtureTest.hex(
            "02 06 43 31 32 33");
private static final byte[] ENUM_B = VPackWireFixtureTest.hex("41 42");
private static final byte[] NO_ACCESS_OVERRIDES = VPackWireFixtureTest.hex(
            "0b 0b 02 41 78 31 41 79 32 03 06");
private static final byte[] ISSUE_2692 = VPackWireFixtureTest.hex(
            "0b 18 02 44 6c 69 73 74 02 05 42 31 31 43 76 61 6c "
          + "44 56 41 4c 32 03 0d");
private static final byte[] CONFLICT_RESOLUTION = VPackWireFixtureTest.hex(
            "0b 0e 01 45 76 61 6c 75 65 43 61 62 63 03");
private static final byte[] FAILING_DUPS = VPackWireFixtureTest.hex(
            "0b 0a 01 43 66 6f 6f 28 1c 03");
private static final ObjectMapper MAPPER = new VPackMapper();

    void testSimpleSetter() throws Exception {
        SizeClassSetter result = MAPPER.readValue(SIMPLE_SETTER,
                SizeClassSetter.class);
        assertEquals(3, result.other);
        assertEquals(2, result.size);
        assertEquals(-999, result.length);
    }

    void testSimpleSetter2() throws Exception {
        SizeClassSetter2 result = MAPPER.readValue(SIMPLE_SETTER_2,
                SizeClassSetter2.class);
        assertEquals(-3, result.x);
    }

    void testSimpleSetter3() throws Exception {
        SizeClassSetter3 result = MAPPER.readValue(SIMPLE_SETTER_3,
                SizeClassSetter3.class);
        assertEquals(128, result.x);
    }

    void testSetterInheritance() throws Exception {
        BeanSubClass result = MAPPER.readValue(SETTER_INHERITANCE,
                BeanSubClass.class);
        assertEquals(1, result.x);
        assertEquals(2, result.y);
        assertEquals(3, result.z);
    }

    void testImpliedProperty() throws Exception {
        BeanWithDeserialize bean = MAPPER.readValue(IMPLIED_PROPERTY,
                BeanWithDeserialize.class);
        assertNotNull(bean);
        assertEquals(3, bean.a);
    }

    void testIssue442PrivateUnwrapped() throws Exception {
        Issue442Bean bean = MAPPER.readValue(PRIVATE_UNWRAPPED,
                Issue442Bean.class);
        assertEquals(5, bean.w.i);
    }

    void testListSubClass() throws Exception {
        ListSubClass result = MAPPER.readValue(LIST_SUBCLASS,
                ListSubClass.class);
        assertEquals(1, result.size());
        assertEquals(StringWrapper.class, result.get(0).getClass());
        assertEquals("123", result.get(0).str);
    }

    void testEnumsWhenDisabled() throws Exception {
        assertEquals(Alpha.B, MAPPER.readValue(ENUM_B, Alpha.class));

        ObjectMapper disabled = VPackMapper.builder()
                .disable(MapperFeature.USE_ANNOTATIONS)
                .build();
        assertEquals(Alpha.B, disabled.readValue(ENUM_B, Alpha.class));
    }

    void testNoAccessOverrides() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .disable(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS)
                .build();
        SimpleBean bean = mapper.readValue(NO_ACCESS_OVERRIDES, SimpleBean.class);
        assertEquals(1, bean.x);
        assertEquals(2, bean.y);
    }
static class SizeClassSetter {
        int size;
        int length;
        int other;

        @JsonProperty
        public void size(int value) { size = value; }

        @JsonProperty("length")
        public void foobar(int value) { length = value; }

        @JsonProperty
        protected void other(int value) { other = value; }

        public void errorOut(int value) { throw new AssertionError(); }
    }
static class SizeClassSetter2 {
        int x;

        @JsonProperty
        public void setX(int value) { x = value; }

        public void setXandY(int x, int y) { throw new AssertionError(); }
    }
static class SizeClassSetter3 {
        int x;

        @JsonDeserialize
        public void x(int value) { x = value; }
    }
static class BaseBean {
        int x;
        int y;

        public void setX(int value) { x = value; }

        @JsonProperty("y")
        void foobar(int value) { y = value; }
    }
static class BeanSubClass extends BaseBean {
        int z;

        public void setZ(int value) { z = value; }
    }
static class BeanWithDeserialize {
        @JsonDeserialize
        protected int a;
    }
static class IntWrapper {
        public int i;

        IntWrapper() { }

        IntWrapper(int value) { i = value; }
    }
static class Issue442Bean {
        @JsonUnwrapped
        protected IntWrapper w = new IntWrapper(13);
    }
static class StringWrapper {
        public String str;

        public StringWrapper() { }

        public StringWrapper(String value) { str = value; }
    }
static class ListSubClass extends ArrayList<StringWrapper> { }
enum Alpha { A, B, C }
public static class SimpleBean {
        public int x;
        public int y;
    }
static class DataBean2692 {
        final String val;

        @JsonCreator
        public DataBean2692(@JsonProperty("val") String val) {
            this.val = val;
        }

        public String getVal() { return val; }

        public java.util.List<String> getList() { return new ArrayList<>(); }
    }
static class Overloaded739 {
        protected Object value;

        @JsonProperty
        public void setValue(String value) { this.value = value; }

        public void setValue(Object value) { throw new AssertionError(); }
    }
public static class DupFieldBean2 {
        @JsonProperty("foo")
        public int z;

        @JsonDeserialize
        int foo;
    }

    void __invoke_testSimpleSetter() throws Exception {
        try {
            testSimpleSetter();
        } finally {
        }
    }


    void __invoke_testSimpleSetter2() throws Exception {
        try {
            testSimpleSetter2();
        } finally {
        }
    }


    void __invoke_testSimpleSetter3() throws Exception {
        try {
            testSimpleSetter3();
        } finally {
        }
    }


    void __invoke_testSetterInheritance() throws Exception {
        try {
            testSetterInheritance();
        } finally {
        }
    }


    void __invoke_testImpliedProperty() throws Exception {
        try {
            testImpliedProperty();
        } finally {
        }
    }


    void __invoke_testIssue442PrivateUnwrapped() throws Exception {
        try {
            testIssue442PrivateUnwrapped();
        } finally {
        }
    }


    void __invoke_testListSubClass() throws Exception {
        try {
            testListSubClass();
        } finally {
        }
    }


    void __invoke_testEnumsWhenDisabled() throws Exception {
        try {
            testEnumsWhenDisabled();
        } finally {
        }
    }


    void __invoke_testNoAccessOverrides() throws Exception {
        try {
            testNoAccessOverrides();
        } finally {
        }
    }

}
