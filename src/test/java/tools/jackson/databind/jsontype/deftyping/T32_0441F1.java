package tools.jackson.databind.jsontype.deftyping;

import java.util.Map;

import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.DatabindContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import tools.jackson.dataformat.velocypack.*;

class T32_0441F1 {
private static final byte[] STRING_BEAN = VPackWireFixtureTest.hex(
            "14 0a 44 6e 61 6d 65 41 78 01");

    // Provenance: TestDefaultForRecords#testNonFinalExcludesRecord().
    void testNonFinalExcludesRecordVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .activateDefaultTyping(NoCheckSubTypeValidator.instance,
                        DefaultTyping.NON_FINAL)
                .build();
        Map<?, ?> result = VPackMapper.builder().build().readValue(
                mapper.writeValueAsBytes(new TestRecord("test")), Map.class);
        assertEquals("test", result.get("value"));
        assertFalse(result.containsKey("@class"));
    }

    // Provenance: TestDefaultForRecords#testRecordAsObjectField().
    void testRecordAsObjectFieldVpack() throws Exception {
        ObjectMapper mapper = recordTypingMapper();
        TestRecord record = new TestRecord("test");
        RecordHolder holder = mapper.readValue(mapper.writeValueAsBytes(
                new RecordHolder(record)), RecordHolder.class);
        assertEquals(record, holder.value);
    }

    // Provenance: TestDefaultForRecords#testRecordDeserializeAsObject().
    void testRecordDeserializeAsObjectVpack() throws Exception {
        ObjectMapper mapper = recordTypingMapper();
        Object value = mapper.readValue(mapper.writeValueAsBytes(
                new TestRecord("test")), Object.class);
        assertInstanceOf(TestRecord.class, value);
    }

    // Provenance: TestDefaultForRecords#testRecordInObjectArray().
    void testRecordInObjectArrayVpack() throws Exception {
        ObjectMapper mapper = recordTypingMapper();
        Object[] value = mapper.readValue(mapper.writeValueAsBytes(
                new Object[] { new TestRecord("test") }), Object[].class);
        assertEquals(1, value.length);
        assertEquals(new TestRecord("test"), value[0]);
    }
private static ObjectMapper recordTypingMapper() {
        return VPackMapper.builder()
                .activateDefaultTyping(NoCheckSubTypeValidator.instance,
                        DefaultTyping.NON_FINAL_AND_RECORDS)
                .build();
    }
static abstract class AbstractBean { }
static class StringBean extends AbstractBean {
        public String name;
        public StringBean() { }
        StringBean(String name) { this.name = name; }
    }
static final class FinalStringBean extends StringBean {
        FinalStringBean(String name) { super(name); }
    }
static final class BeanHolder {
        public AbstractBean bean;
        BeanHolder() { }
    }
static final class ObjectHolder {
        public Object value;
        ObjectHolder() { }
        ObjectHolder(Object value) { this.value = value; }
    }
static class Point1093 {
        public int x;
        public int y;
        Point1093() { }
        Point1093(int x, int y) { this.x = x; this.y = y; }
    }
static class FooTreeNode {
        public String bar;
        FooTreeNode() { }
        FooTreeNode(String bar) { this.bar = bar; }
    }
record TestRecord(String value) { }
static final class RecordHolder {
        public Object value;
        RecordHolder() { }
        RecordHolder(TestRecord value) { this.value = value; }
    }
static final class NoCheckSubTypeValidator extends PolymorphicTypeValidator.Base {
        private static final long serialVersionUID = 1L;
        static final NoCheckSubTypeValidator instance = new NoCheckSubTypeValidator();

        @Override
        public Validity validateBaseType(DatabindContext ctxt, JavaType baseType) {
            return Validity.ALLOWED;
        }
    }

    void __invoke_testNonFinalExcludesRecordVpack() throws Exception {
        try {
            testNonFinalExcludesRecordVpack();
        } finally {
        }
    }


    void __invoke_testRecordAsObjectFieldVpack() throws Exception {
        try {
            testRecordAsObjectFieldVpack();
        } finally {
        }
    }


    void __invoke_testRecordDeserializeAsObjectVpack() throws Exception {
        try {
            testRecordDeserializeAsObjectVpack();
        } finally {
        }
    }


    void __invoke_testRecordInObjectArrayVpack() throws Exception {
        try {
            testRecordInObjectArrayVpack();
        } finally {
        }
    }

}
