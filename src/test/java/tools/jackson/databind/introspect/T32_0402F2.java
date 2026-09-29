package tools.jackson.databind.introspect;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;
import tools.jackson.databind.BeanDescription;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.InvalidDefinitionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import tools.jackson.dataformat.velocypack.*;

class T32_0402F2 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] VALUE_42 = {
            0x14, 0x0B, 0x45, 0x76, 0x61, 0x6C, 0x75, 0x65, 0x28, 0x2A, 0x01
    };
private static final byte[] BLOOP_TRUE = {
            0x14, 0x0A, 0x45, 0x62, 0x6C, 0x6F, 0x6F, 0x70, 0x1A, 0x01
    };
private static final byte[] VALUE_FOO = {
            0x14, 0x0D, 0x45, 0x76, 0x61, 0x6C, 0x75, 0x65,
            0x43, 0x66, 0x6F, 0x6F, 0x01
    };

    // Provenance: SetterConflictTest#testConflictingSetters().
    void testConflictingSettersVpack() throws Exception {
        DuplicateSetterBean2979 result = MAPPER.readValue(BLOOP_TRUE,
                DuplicateSetterBean2979.class);
        assertEquals(Boolean.TRUE, result.value);
    }

    // Provenance: SetterConflictTest#testDuplicateSetterResolutionFail().
    void testDuplicateSetterResolutionFailVpack() throws Exception {
        try {
            MAPPER.readValue(VALUE_FOO, DupSetter3125BeanFail.class);
            fail("Should not pass");
        } catch (InvalidDefinitionException e) {
            assertTrue(e.getMessage().contains(
                    "Conflicting setter definitions for property \"value\""));
        }
    }

    // Provenance: SetterConflictTest#testDuplicateSetterResolutionOk().
    void testDuplicateSetterResolutionOkVpack() throws Exception {
        DupSetter3125Bean value = MAPPER.readValue(VALUE_FOO, DupSetter3125Bean.class);
        assertEquals("foo", value.str);
    }

    // Provenance: SetterConflictTest#testSetterPriority().
    void testSetterPriorityVpack() throws Exception {
        Issue1033Bean bean = MAPPER.readValue(VALUE_42, Issue1033Bean.class);
        assertEquals(42, bean.value);
    }
private static BeanDescription serializationDescription(Class<?> type) {
        return MAPPER._serializationContext().introspectBeanDescription(MAPPER.constructType(type));
    }
private static BeanDescription deserializationDescription(Class<?> type) {
        return MAPPER._deserializationContext().introspectBeanDescription(MAPPER.constructType(type));
    }
static class SimpleGetterVisibility {
        public int getA() { return 0; }
        protected int getB() { return 1; }
        @SuppressWarnings("unused")
        private int getC() { return 2; }
    }
static class RenamedProperties {
        @JsonProperty("x")
        public int value;

        public void setValue(int v) { value = v; }
        public int getX() { return value; }
    }
static class RenamedProperties2 {
        @JsonProperty("renamed")
        public int getValue() { return 1; }
        public void setValue(int x) { }
    }
static class IgnoredRenamedSetter {
        @JsonIgnore public void setY(int value) { }
        @JsonProperty("y") void foobar(int value) { }
    }
@JsonPropertyOrder({"a", "b", "c", "d"})
    static class SortedProperties {
        public int b;
        public int c;
        public void setD(int value) { }
        public void setA(int value) { }
    }
static class TypeTestBean {
        protected Long value;

        @JsonCreator
        public TypeTestBean(@JsonProperty("value") String value) { }

        public Integer getValue() { return 0; }
    }
static class Issue1033Bean {
        public int value;

        public void setValue(int v) { value = v; }
        public void setValue(Issue1033Bean foo) {
            throw new Error("Should not get called");
        }
    }
static class DuplicateSetterBean2979 {
        Object value;

        public void setBloop(Boolean bloop) {
            throw new Error("Wrong setter!");
        }

        @JsonSetter
        public void setBloop(Object bloop) { value = bloop; }
    }
static class DupSetter3125Bean {
        String str;

        public void setValue(Integer value) {
            throw new RuntimeException("Integer: wrong!");
        }
        public void setValue(Boolean value) {
            throw new RuntimeException("Boolean: wrong!");
        }
        public void setValue(String value) { str = value; }
    }
static class DupSetter3125BeanFail {
        public void setValue(Integer value) {
            throw new RuntimeException("Integer: wrong!");
        }
        public void setValue(Boolean value) {
            throw new RuntimeException("Boolean: wrong!");
        }
        public void setValue(List<String> value) {
            throw new RuntimeException("List: wrong!");
        }
    }

    void __invoke_testConflictingSettersVpack() throws Exception {
        try {
            testConflictingSettersVpack();
        } finally {
        }
    }


    void __invoke_testDuplicateSetterResolutionFailVpack() throws Exception {
        try {
            testDuplicateSetterResolutionFailVpack();
        } finally {
        }
    }


    void __invoke_testDuplicateSetterResolutionOkVpack() throws Exception {
        try {
            testDuplicateSetterResolutionOkVpack();
        } finally {
        }
    }


    void __invoke_testSetterPriorityVpack() throws Exception {
        try {
            testSetterPriorityVpack();
        } finally {
        }
    }

}
