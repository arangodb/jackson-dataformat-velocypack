package tools.jackson.databind.deser.creators;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.MapperConfig;
import tools.jackson.databind.exc.UnrecognizedPropertyException;
import tools.jackson.databind.introspect.AnnotatedMember;
import tools.jackson.databind.introspect.AnnotatedParameter;
import tools.jackson.databind.introspect.JacksonAnnotationIntrospector;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0199F0 {
private static final byte[] KEY_RENAME_INPUT = VPackWireFixtureTest.hex(
            "14 17 44 6b 65 79 31 44 76 61 6c 31 44 6b 65 79 32 44 76 61 6c 32 02");
private static final byte[] BEAN2_OUTPUT = VPackWireFixtureTest.hex(
            "0b 0b 01 45 73 74 75 66 66 33 03");
private static final byte[] WRITE_ONLY_INPUT = VPackWireFixtureTest.hex(
            "14 17 45 76 61 6c 75 65 37 48 70 61 73 73 77 6f 72 64 43 66 6f 6f 02");
private static final byte[] WRITE_ONLY_OUTPUT = VPackWireFixtureTest.hex(
            "0b 0b 01 45 76 61 6c 75 65 37 03");
private static final byte[] RENAMED_OUTPUT = VPackWireFixtureTest.hex(
            "0b 0b 01 43 62 61 72 42 34 32 03");
private static final byte[] RENAMED_INPUT = VPackWireFixtureTest.hex(
            "0b 0b 01 43 62 61 72 42 34 32 03");
private static final byte[] NULL_CREATOR = VPackWireFixtureTest.hex(
            "14 09 41 78 32 41 79 18 02");
private static final byte[] NULL_CREATOR_NESTED = VPackWireFixtureTest.hex(
            "14 13 46 65 6e 74 69 74 79 14 09 41 78 32 41 79 18 02 01");
private static final byte[] ABSENT_CREATOR = VPackWireFixtureTest.hex(
            "14 06 41 78 32 01");
private static final byte[] ABSENT_CREATOR_NESTED = VPackWireFixtureTest.hex(
            "14 10 46 65 6e 74 69 74 79 14 06 41 78 32 01 01");
private static final byte[] PERSON_NAME_AND_NULL_AGE = VPackWireFixtureTest.hex(
            "14 12 44 6e 61 6d 65 44 4a 6f 68 6e 43 61 67 65 18 02");
private static final byte[] PERSON_AGE_AND_NULL_NAME = VPackWireFixtureTest.hex(
            "14 0e 43 61 67 65 35 44 6e 61 6d 65 18 02");
private static final byte[] DEFAULTING_2977 = VPackWireFixtureTest.hex(
            "14 07 42 61 61 38 01");
private static final byte[] RECORD_ABSENT = VPackWireFixtureTest.hex(
            "14 14 44 69 6e 74 32 28 2a 48 62 6f 6f 6c 65 61 6e 31 1a 02");
private static final byte[] RECORD_NULL = VPackWireFixtureTest.hex(
            "14 25 44 69 6e 74 31 28 6f 44 69 6e 74 32 28 de 48 62 6f 6f 6c 65 61 6e 31 1a 48 62 6f 6f 6c 65 61 6e 32 18 04");
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final ObjectMapper IMPLICIT_NAME_MAPPER = VPackMapper.builder()
            .annotationIntrospector(new ImplicitNameIntrospector())
            .build();

    // Provenance: CreatorImplicitNameTest#testCreatorWithRename4545.
    void testCreatorWithRename4545() {
        ObjectMapper mapper4545 = VPackMapper.builder()
                .disable(MapperFeature.ALLOW_FINAL_FIELDS_AS_MUTATORS)
                .annotationIntrospector(new ImplicitNameIntrospector())
                .build();
        UnrecognizedPropertyException exception = assertThrows(
                UnrecognizedPropertyException.class,
                () -> mapper4545.readerFor(Payload4545.class)
                        .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                        .readValue(KEY_RENAME_INPUT));
        assertTrue(exception.getMessage().contains("Unrecognized"));
        assertTrue(exception.getMessage().contains("key1"));
    }

    // Provenance: CreatorImplicitNameTest#testImplicitWithSetterGetter.
    void testImplicitWithSetterGetter() throws Exception {
        assertArrayEquals(BEAN2_OUTPUT, MAPPER.writeValueAsBytes(new Bean2()));
    }

    // Provenance: CreatorImplicitNameTest#testShouldSupportPropertyRenaming4810.
    void testShouldSupportPropertyRenaming4810() throws Exception {
        byte[] serialized = IMPLICIT_NAME_MAPPER.writeValueAsBytes(DataClass4810.create("42"));
        assertArrayEquals(RENAMED_OUTPUT, serialized);
        DataClass4810 deserialized = IMPLICIT_NAME_MAPPER.readValue(RENAMED_INPUT,
                DataClass4810.class);
        assertEquals("42", deserialized.getFoo());
    }

    // Provenance: CreatorImplicitNameTest#testWriteOnly.
    void testWriteOnly() throws Exception {
        PasswordBean bean = MAPPER.readValue(WRITE_ONLY_INPUT, PasswordBean.class);
        assertEquals("[password='foo',value=7]", bean.asString());
        assertArrayEquals(WRITE_ONLY_OUTPUT, MAPPER.writeValueAsBytes(bean));
    }
private static tools.jackson.databind.ObjectReader nullFailingReader(Class<?> type) {
        return MAPPER.readerFor(type).with(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES);
    }
@Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.RUNTIME)
    private @interface ImplicitName {
        String value();
    }
private static class ImplicitNameIntrospector extends JacksonAnnotationIntrospector {
        @Override
        public String findImplicitPropertyName(MapperConfig<?> config, AnnotatedMember member) {
            ImplicitName annotation = member.getAnnotation(ImplicitName.class);
            return annotation == null ? null : annotation.value();
        }
    }
private static class Payload4545 {
        private final String key1;
        private final String key2;

        @JsonCreator
        public Payload4545(@ImplicitName("key1") @JsonProperty("key") String key1,
                @ImplicitName("key2") @JsonProperty("key2") String key2) {
            this.key1 = key1;
            this.key2 = key2;
        }

        public String getKey1() { return key1; }
        public String getKey2() { return key2; }
    }
private static class Bean2 {
        int x = 3;

        @JsonProperty("stuff")
        private void setValue(int value) { x = value; }

        public int getValue() { return x; }
    }
private static class PasswordBean {
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        private String password;

        private int value;

        public int getValue() { return value; }
        public String getPassword() { return password; }
        public String asString() {
            return "[password='%s',value=%d]".formatted(password, value);
        }
    }
private static class DataClass4810 {
        private String x;

        private DataClass4810(String x) { this.x = x; }

        @JsonProperty("bar")
        public String getFoo() { return x; }

        @JsonCreator
        public static DataClass4810 create(@ImplicitName("bar") String bar) {
            return new DataClass4810(bar);
        }
    }
private static class JsonEntity {
        protected final int x;
        protected final int y;

        @JsonCreator
        private JsonEntity(@JsonProperty("x") int x, @JsonProperty("y") int y) {
            this.x = x;
            this.y = y;
        }
    }
private static class NestedJsonEntity {
        protected final JsonEntity entity;

        @JsonCreator
        private NestedJsonEntity(@JsonProperty("entity") JsonEntity entity) {
            this.entity = entity;
        }
    }
private static class Person {
        String name;
        Integer age;

        @JsonCreator
        public Person(@JsonProperty("name") String name, @JsonProperty("age") int age) {
            this.name = name;
            this.age = age;
        }
    }
private static class ABCParamIntrospector extends JacksonAnnotationIntrospector {
        @Override
        public String findImplicitPropertyName(MapperConfig<?> config, AnnotatedMember member) {
            if (member instanceof AnnotatedParameter parameter) {
                return switch (parameter.getIndex()) {
                case 0 -> "a";
                case 1 -> "b";
                case 2 -> "c";
                default -> "param" + parameter.getIndex();
                };
            }
            return super.findImplicitPropertyName(config, member);
        }
    }
private record PrimitiveRecord(int int1, int int2, boolean boolean1, boolean boolean2) { }
private static class TestClass2977 {
        @JsonProperty("aa")
        final int a;

        public TestClass2977(int a) { this.a = a; }
    }

    void __invoke_testCreatorWithRename4545() throws Exception {
        try {
            testCreatorWithRename4545();
        } finally {
        }
    }


    void __invoke_testImplicitWithSetterGetter() throws Exception {
        try {
            testImplicitWithSetterGetter();
        } finally {
        }
    }


    void __invoke_testShouldSupportPropertyRenaming4810() throws Exception {
        try {
            testShouldSupportPropertyRenaming4810();
        } finally {
        }
    }


    void __invoke_testWriteOnly() throws Exception {
        try {
            testWriteOnly();
        } finally {
        }
    }

}
