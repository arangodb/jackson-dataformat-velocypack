package tools.jackson.databind.introspect;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;
import tools.jackson.databind.BeanDescription;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.PropertyMetadata;
import tools.jackson.databind.PropertyName;
import com.fasterxml.jackson.annotation.Nulls;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0402F1 {
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

    // Provenance: PropertyMetadataTest#testPropertyName().
    void testPropertyNameVpack() {
        PropertyName name = PropertyName.NO_NAME;
        assertFalse(name.hasSimpleName());
        assertFalse(name.hasNamespace());
        assertSame(name, name.internSimpleName());
        assertSame(name, name.withSimpleName(null));
        assertSame(name, name.withSimpleName(""));
        assertSame(name, name.withNamespace(null));
        assertEquals("", name.toString());
        assertTrue(name.isEmpty());
        assertFalse(name.hasSimpleName("foo"));
        name.hashCode();

        PropertyName newName = name.withNamespace("");
        assertNotSame(name, newName);
        assertTrue(name.equals(name));
        assertFalse(name.equals(newName));
        assertFalse(newName.equals(name));

        name = name.withSimpleName("foo");
        assertEquals("foo", name.toString());
        assertTrue(name.hasSimpleName("foo"));
        assertFalse(name.isEmpty());
        newName = name.withNamespace("ns");
        assertEquals("{ns}foo", newName.toString());
        assertFalse(newName.equals(name));
        assertFalse(name.equals(newName));
        name.hashCode();
    }

    // Provenance: PropertyMetadataTest#testPropertyMetadata().
    void testPropertyMetadataVpack() {
        PropertyMetadata md = PropertyMetadata.STD_OPTIONAL;
        assertNull(md.getValueNulls());
        assertNull(md.getContentNulls());
        assertNull(md.getDefaultValue());
        assertEquals(Boolean.FALSE, md.getRequired());

        md = md.withNulls(Nulls.AS_EMPTY, Nulls.FAIL);
        assertEquals(Nulls.AS_EMPTY, md.getValueNulls());
        assertEquals(Nulls.FAIL, md.getContentNulls());
        assertFalse(md.hasDefaultValue());
        assertSame(md, md.withDefaultValue(null));
        assertSame(md, md.withDefaultValue(""));
        md = md.withDefaultValue("foo");
        assertEquals("foo", md.getDefaultValue());
        assertTrue(md.hasDefaultValue());
        assertSame(md, md.withDefaultValue("foo"));
        md = md.withDefaultValue(null);
        assertFalse(md.hasDefaultValue());
        assertNull(md.getDefaultValue());

        md = md.withRequired(null);
        assertNull(md.getRequired());
        assertFalse(md.isRequired());
        md = md.withRequired(Boolean.TRUE);
        assertTrue(md.isRequired());
        assertSame(md, md.withRequired(Boolean.TRUE));
        md = md.withRequired(null);
        assertNull(md.getRequired());
        assertFalse(md.isRequired());
        assertFalse(md.hasIndex());
        md = md.withIndex(Integer.valueOf(3));
        assertTrue(md.hasIndex());
        assertEquals(Integer.valueOf(3), md.getIndex());
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

    void __invoke_testPropertyNameVpack() throws Exception {
        try {
            testPropertyNameVpack();
        } finally {
        }
    }


    void __invoke_testPropertyMetadataVpack() throws Exception {
        try {
            testPropertyMetadataVpack();
        } finally {
        }
    }

}
