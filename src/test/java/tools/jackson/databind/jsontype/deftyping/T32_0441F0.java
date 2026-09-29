package tools.jackson.databind.jsontype.deftyping;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.DatabindContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;
import tools.jackson.databind.util.TokenBuffer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0441F0 {
private static final byte[] STRING_BEAN = VPackWireFixtureTest.hex(
            "14 0a 44 6e 61 6d 65 41 78 01");

    // Provenance: TestDefaultForObject#testNoGoWithExternalProperty().
    void testNoGoWithExternalPropertyVpack() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> VPackMapper.builder()
                        .activateDefaultTyping(NoCheckSubTypeValidator.instance,
                                DefaultTyping.JAVA_LANG_OBJECT,
                                JsonTypeInfo.As.EXTERNAL_PROPERTY)
                        .build());
        assertEquals(true, e.getMessage().contains("Cannot use includeAs of EXTERNAL_PROPERTY"));
    }

    // Provenance: TestDefaultForObject#testNonFinalBean().
    void testNonFinalBeanVpack() throws Exception {
        ObjectMapper objectAndNonConcrete = VPackMapper.builder()
                .activateDefaultTyping(NoCheckSubTypeValidator.instance,
                        DefaultTyping.OBJECT_AND_NON_CONCRETE)
                .build();
        Map<?, ?> plain = VPackMapper.builder().build()
                .readValue(STRING_BEAN, Map.class);
        assertEquals("x", plain.get("name"));
        Map<?, ?> nonTyped = VPackMapper.builder().build().readValue(
                objectAndNonConcrete.writeValueAsBytes(new StringBean("x")), Map.class);
        assertEquals("x", nonTyped.get("name"));

        ObjectMapper nonFinal = VPackMapper.builder()
                .activateDefaultTyping(NoCheckSubTypeValidator.instance,
                        DefaultTyping.NON_FINAL)
                .build();
        Object[] wrapped = VPackMapper.builder().build().readValue(
                nonFinal.writeValueAsBytes(new StringBean("x")), Object[].class);
        assertEquals(StringBean.class.getName(), wrapped[0]);
        assertEquals("x", assertInstanceOf(Map.class, wrapped[1]).get("name"));
    }

    // Provenance: TestDefaultForObject#testNullValue().
    void testNullValueVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .activateDefaultTyping(NoCheckSubTypeValidator.instance,
                        DefaultTyping.NON_FINAL)
                .build();
        BeanHolder result = mapper.readValue(mapper.writeValueAsBytes(new BeanHolder()),
                BeanHolder.class);
        assertNotNull(result);
        assertNull(result.bean);
    }

    // Provenance: TestDefaultForObject#testTokenBuffer().
    void testTokenBufferVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .activateDefaultTyping(NoCheckSubTypeValidator.instance,
                        DefaultTyping.NON_FINAL)
                .build();
        TokenBuffer buffer = TokenBuffer.forGeneration();
        buffer.writeStartObject();
        buffer.writeNumberProperty("num", 42);
        buffer.writeEndObject();

        ObjectHolder holder = mapper.readValue(mapper.writeValueAsBytes(
                new ObjectHolder(buffer)), ObjectHolder.class);
        assertNotNull(holder.value);
        assertSame(TokenBuffer.class, holder.value.getClass());
        try (JsonParser parser = ((TokenBuffer) holder.value).asParser()) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(42, parser.getIntValue());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertNull(parser.nextToken());
        }

        buffer = TokenBuffer.forGeneration();
        buffer.writeStartArray();
        buffer.writeBoolean(true);
        buffer.writeEndArray();
        holder = mapper.readValue(mapper.writeValueAsBytes(
                new ObjectHolder(buffer)), ObjectHolder.class);
        assertNotNull(holder.value);
        assertSame(TokenBuffer.class, holder.value.getClass());
        try (JsonParser parser = ((TokenBuffer) holder.value).asParser()) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(JsonToken.VALUE_TRUE, parser.nextToken());
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }

    // Provenance: TestDefaultForObject#testValueAsStringWithDefaultTyping().
    void testValueAsStringWithDefaultTypingVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .activateDefaultTyping(NoCheckSubTypeValidator.instance,
                        DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY)
                .build();
        FooTreeNode foo = new FooTreeNode("baz");
        JsonNode tree = mapper.readTree(mapper.writeValueAsBytes(foo));
        assertEquals(foo.bar, tree.get("bar").stringValue());
    }

    // Provenance: TestDefaultForObject#testValueToTreeWithDefaultTyping().
    void testValueToTreeWithDefaultTypingVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .activateDefaultTyping(NoCheckSubTypeValidator.instance,
                        DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY)
                .build();
        FooTreeNode foo = new FooTreeNode("baz");
        JsonNode tree = mapper.valueToTree(foo);
        assertEquals(foo.bar, tree.get("bar").stringValue());
    }

    // Provenance: TestDefaultForObject#testWithDefaultTyping1093().
    void testWithDefaultTyping1093Vpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .activateDefaultTyping(NoCheckSubTypeValidator.instance,
                        DefaultTyping.JAVA_LANG_OBJECT)
                .build();
        Point1093 input = new Point1093(28, 12);
        Point1093 first = (Point1093) mapper.readerFor(Object.class).readValue(
                mapper.writer().forType(Object.class).writeValueAsBytes(input));
        Point1093 second = (Point1093) mapper.readerFor(Object.class).readValue(
                mapper.writerFor(Object.class).writeValueAsBytes(input));
        assertEquals(input.x, first.x);
        assertEquals(input.y, first.y);
        assertEquals(input.x, second.x);
        assertEquals(input.y, second.y);
    }

    // Provenance: TestDefaultForObject#testWithFinalClass_NonFinal().
    void testWithFinalClassNonFinalVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .activateDefaultTyping(NoCheckSubTypeValidator.instance,
                        DefaultTyping.NON_FINAL)
                .build();
        Map<?, ?> result = VPackMapper.builder().build().readValue(
                mapper.writeValueAsBytes(new FinalStringBean("abc")), Map.class);
        assertEquals("abc", result.get("name"));
        assertFalse(result.containsKey("@class"));
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

    void __invoke_testNoGoWithExternalPropertyVpack() throws Exception {
        try {
            testNoGoWithExternalPropertyVpack();
        } finally {
        }
    }


    void __invoke_testNonFinalBeanVpack() throws Exception {
        try {
            testNonFinalBeanVpack();
        } finally {
        }
    }


    void __invoke_testNullValueVpack() throws Exception {
        try {
            testNullValueVpack();
        } finally {
        }
    }


    void __invoke_testTokenBufferVpack() throws Exception {
        try {
            testTokenBufferVpack();
        } finally {
        }
    }


    void __invoke_testValueAsStringWithDefaultTypingVpack() throws Exception {
        try {
            testValueAsStringWithDefaultTypingVpack();
        } finally {
        }
    }


    void __invoke_testValueToTreeWithDefaultTypingVpack() throws Exception {
        try {
            testValueToTreeWithDefaultTypingVpack();
        } finally {
        }
    }


    void __invoke_testWithDefaultTyping1093Vpack() throws Exception {
        try {
            testWithDefaultTyping1093Vpack();
        } finally {
        }
    }


    void __invoke_testWithFinalClassNonFinalVpack() throws Exception {
        try {
            testWithFinalClassNonFinalVpack();
        } finally {
        }
    }

}
