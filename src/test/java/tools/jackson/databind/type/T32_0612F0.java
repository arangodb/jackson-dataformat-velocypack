package tools.jackson.databind.type;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationConfig;
import tools.jackson.databind.introspect.AnnotatedClass;
import tools.jackson.databind.introspect.AnnotatedClassResolver;
import tools.jackson.databind.introspect.AnnotatedField;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

import tools.jackson.dataformat.velocypack.*;

class T32_0612F0 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] FILTERED_JSON_VALUE = VPackWireFixtureTest.hex(
            "0b 0e 01 47 70 72 65 73 65 6e 74 41 78 03");

    // Provenance: AnnotatedClassTest#testFieldIntrospection().
    void annotatedClassFieldIntrospectionVpack() {
        SerializationConfig config = MAPPER.serializationConfig();
        JavaType type = MAPPER.constructType(FieldBean.class);
        AnnotatedClass annotated = AnnotatedClassResolver.resolve(config, type, config);
        assertEquals(2, annotated.getFieldCount());
        for (AnnotatedField field : annotated.fields()) {
            String name = field.getName();
            if (!"bar".equals(name) && !"props".equals(name)) {
                fail("Unexpected field name '" + name + "'");
            }
        }
    }

    // Provenance: AnnotatedClassTest#testConstructorIntrospection().
    void annotatedClassConstructorIntrospectionVpack() {
        Bean1005 bean = new Bean1005(13);
        SerializationConfig config = MAPPER.serializationConfig();
        JavaType type = MAPPER.constructType(bean.getClass());
        AnnotatedClass annotated = AnnotatedClassResolver.resolve(config, type, config);
        assertEquals(1, annotated.getConstructors().size());
    }

    // Provenance: AnnotatedClassTest#testArrayTypeIntrospection().
    void annotatedArrayTypeIntrospectionVpack() {
        AnnotatedClass annotated = AnnotatedClassResolver.resolve(MAPPER.serializationConfig(),
                MAPPER.constructType(int[].class), null);
        assertFalse(annotated.memberMethods().iterator().hasNext());
        assertFalse(annotated.fields().iterator().hasNext());
    }

    // Provenance: AnnotatedClassTest#testIntrospectionWithRawClass().
    void annotatedRawClassIntrospectionVpack() {
        AnnotatedClass annotated = AnnotatedClassResolver.resolveWithoutSuperTypes(
                MAPPER.serializationConfig(), String.class, null);
        assertFalse(annotated.memberMethods().iterator().hasNext());
        assertFalse(annotated.fields().iterator().hasNext());
    }
static class JsonValueWithInclude {
        @JsonValue
        @JsonInclude(value = JsonInclude.Include.NON_NULL,
                content = JsonInclude.Include.NON_NULL)
        public final Map<String, Object> value;

        JsonValueWithInclude(Map<String, Object> value) { this.value = value; }
    }
@SuppressWarnings("unused")
    static class FieldBean {
        public static boolean DUMMY;
        private long bar;
        @JsonProperty private String props;
    }
static class Bean1005 {
        Bean1005(int ignored) { }
    }
static abstract class LongList implements List<Long> { }
static abstract class StringLongMap implements Map<String, Long> { }

    void __invoke_annotatedClassFieldIntrospectionVpack() throws Exception {
        try {
            annotatedClassFieldIntrospectionVpack();
        } finally {
        }
    }


    void __invoke_annotatedClassConstructorIntrospectionVpack() throws Exception {
        try {
            annotatedClassConstructorIntrospectionVpack();
        } finally {
        }
    }


    void __invoke_annotatedArrayTypeIntrospectionVpack() throws Exception {
        try {
            annotatedArrayTypeIntrospectionVpack();
        } finally {
        }
    }


    void __invoke_annotatedRawClassIntrospectionVpack() throws Exception {
        try {
            annotatedRawClassIntrospectionVpack();
        } finally {
        }
    }

}
