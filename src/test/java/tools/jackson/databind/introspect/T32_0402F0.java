package tools.jackson.databind.introspect;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;
import tools.jackson.databind.BeanDescription;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.introspect.BeanPropertyDefinition;
import tools.jackson.databind.introspect.AnnotatedMember;
import tools.jackson.databind.introspect.AnnotatedMethod;
import tools.jackson.databind.introspect.AnnotatedParameter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0402F0 {
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

    // Provenance: POJOPropertiesCollectorTest#testSimpleGetterVisibility().
    void testSimpleGetterVisibilityVpack() {
        List<BeanPropertyDefinition> props =
                serializationDescription(SimpleGetterVisibility.class).findProperties();
        assertEquals(1, props.size());
        BeanPropertyDefinition prop = props.get(0);
        assertEquals("a", prop.getName());
        assertFalse(prop.hasSetter());
        assertTrue(prop.hasGetter());
        assertFalse(prop.hasField());
    }

    // Provenance: POJOPropertiesCollectorTest#testSimpleIgnoreAndRename().
    void testSimpleIgnoreAndRenameVpack() {
        List<BeanPropertyDefinition> props =
                serializationDescription(IgnoredRenamedSetter.class).findProperties();
        assertEquals(1, props.size());
        BeanPropertyDefinition prop = props.get(0);
        assertEquals("y", prop.getName());
        assertTrue(prop.hasSetter());
        assertFalse(prop.hasGetter());
        assertFalse(prop.hasField());
    }

    // Provenance: POJOPropertiesCollectorTest#testSimpleOrderingForDeserialization().
    void testSimpleOrderingForDeserializationVpack() {
        List<BeanPropertyDefinition> props =
                deserializationDescription(SortedProperties.class).findProperties();
        assertEquals(4, props.size());
        assertEquals("a", props.get(0).getName());
        assertEquals("b", props.get(1).getName());
        assertEquals("c", props.get(2).getName());
        assertEquals("d", props.get(3).getName());
    }

    // Provenance: POJOPropertiesCollectorTest#testSimpleRenamed().
    void testSimpleRenamedVpack() {
        List<BeanPropertyDefinition> props =
                serializationDescription(RenamedProperties.class).findProperties();
        assertEquals(1, props.size());
        BeanPropertyDefinition prop = props.get(0);
        assertEquals("x", prop.getName());
        assertTrue(prop.hasSetter());
        assertTrue(prop.hasGetter());
        assertTrue(prop.hasField());
    }

    // Provenance: POJOPropertiesCollectorTest#testSimpleRenamed2().
    void testSimpleRenamed2Vpack() {
        List<BeanPropertyDefinition> props =
                serializationDescription(RenamedProperties2.class).findProperties();
        assertEquals(1, props.size());
        BeanPropertyDefinition prop = props.get(0);
        assertEquals("renamed", prop.getName());
        assertTrue(prop.hasSetter());
        assertTrue(prop.hasGetter());
        assertFalse(prop.hasField());
    }

    // Provenance: POJOPropertiesCollectorTest#testSimpleWithType().
    void testSimpleWithTypeVpack() {
        List<BeanPropertyDefinition> props =
                serializationDescription(TypeTestBean.class).findProperties();
        assertEquals(1, props.size());
        assertEquals("value", props.get(0).getName());
        AnnotatedMember accessor = props.get(0).getAccessor();
        assertTrue(accessor instanceof AnnotatedMethod);
        assertEquals(Integer.class, accessor.getRawType());

        props = deserializationDescription(TypeTestBean.class).findProperties();
        assertEquals(1, props.size());
        assertEquals("value", props.get(0).getName());
        AnnotatedMember mutator = props.get(0).getMutator();
        assertTrue(mutator instanceof AnnotatedParameter);
        assertEquals(String.class, mutator.getRawType());
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

    void __invoke_testSimpleGetterVisibilityVpack() throws Exception {
        try {
            testSimpleGetterVisibilityVpack();
        } finally {
        }
    }


    void __invoke_testSimpleIgnoreAndRenameVpack() throws Exception {
        try {
            testSimpleIgnoreAndRenameVpack();
        } finally {
        }
    }


    void __invoke_testSimpleOrderingForDeserializationVpack() throws Exception {
        try {
            testSimpleOrderingForDeserializationVpack();
        } finally {
        }
    }


    void __invoke_testSimpleRenamedVpack() throws Exception {
        try {
            testSimpleRenamedVpack();
        } finally {
        }
    }


    void __invoke_testSimpleRenamed2Vpack() throws Exception {
        try {
            testSimpleRenamed2Vpack();
        } finally {
        }
    }


    void __invoke_testSimpleWithTypeVpack() throws Exception {
        try {
            testSimpleWithTypeVpack();
        } finally {
        }
    }

}
