package tools.jackson.databind.deser.creators;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.ConstructorDetector;
import tools.jackson.databind.cfg.MapperConfig;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.introspect.AnnotatedMember;
import tools.jackson.databind.introspect.AnnotatedParameter;
import tools.jackson.databind.introspect.JacksonAnnotationIntrospector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0199F1 {
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

    // Provenance: CreatorNullPrimitivesTest#defaultingWithNull2977.
    void defaultingWithNull2977() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
                .annotationIntrospector(new ABCParamIntrospector())
                .constructorDetector(ConstructorDetector.USE_PROPERTIES_BASED)
                .build();
        TestClass2977 result = mapper.readValue(DEFAULTING_2977, TestClass2977.class);
        assertEquals(8, result.a);
    }

    // Provenance: CreatorNullPrimitivesTest#testCreatorAbsentPrimitiveInNestedObjectShouldDefault.
    void testCreatorAbsentPrimitiveInNestedObjectShouldDefault() throws Exception {
        NestedJsonEntity result = nullFailingReader(NestedJsonEntity.class)
                .readValue(ABSENT_CREATOR_NESTED);
        assertEquals(2, result.entity.x);
        assertEquals(0, result.entity.y);
    }

    // Provenance: CreatorNullPrimitivesTest#testCreatorAbsentPrimitiveShouldDefault.
    void testCreatorAbsentPrimitiveShouldDefault() throws Exception {
        JsonEntity result = nullFailingReader(JsonEntity.class).readValue(ABSENT_CREATOR);
        assertEquals(2, result.x);
        assertEquals(0, result.y);
    }

    // Provenance: CreatorNullPrimitivesTest#testCreatorNullPrimitive.
    void testCreatorNullPrimitive() {
        MismatchedInputException exception = assertThrows(MismatchedInputException.class,
                () -> nullFailingReader(JsonEntity.class).readValue(NULL_CREATOR));
        assertTrue(exception.getMessage().contains("Cannot map `null` into type `int`"));
        assertEquals(1, exception.getPath().size());
        assertEquals("y", exception.getPath().get(0).getPropertyName());
    }

    // Provenance: CreatorNullPrimitivesTest#testCreatorNullPrimitiveInNestedObject.
    void testCreatorNullPrimitiveInNestedObject() {
        MismatchedInputException exception = assertThrows(MismatchedInputException.class,
                () -> nullFailingReader(NestedJsonEntity.class)
                        .readValue(NULL_CREATOR_NESTED));
        assertTrue(exception.getMessage().contains("Cannot map `null` into type `int`"));
        assertEquals(2, exception.getPath().size());
        assertEquals("y", exception.getPath().get(1).getPropertyName());
        assertEquals("entity", exception.getPath().get(0).getPropertyName());
    }

    // Provenance: CreatorNullPrimitivesTest#testRecordAbsentPrimitivesShouldDefault.
    void testRecordAbsentPrimitivesShouldDefault() throws Exception {
        PrimitiveRecord result = MAPPER.readValue(RECORD_ABSENT, PrimitiveRecord.class);
        assertEquals(0, result.int1());
        assertEquals(42, result.int2());
        assertTrue(result.boolean1());
        assertFalse(result.boolean2());
    }

    // Provenance: CreatorNullPrimitivesTest#testRecordExplicitNullPrimitiveShouldFail.
    void testRecordExplicitNullPrimitiveShouldFail() {
        MismatchedInputException exception = assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue(RECORD_NULL, PrimitiveRecord.class));
        assertTrue(exception.getMessage().contains("Cannot map `null` into type `boolean`"));
    }

    // Provenance: CreatorNullPrimitivesTest#testRequiredNonNullParam.
    void testRequiredNonNullParam() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
                .build();
        Person person = mapper.readValue(VPackWireFixtureTest.hex("0a"), Person.class);
        assertNull(person.name);
        assertEquals(Integer.valueOf(0), person.age);

        ObjectMapper requiredMapper = mapper.rebuild()
                .enable(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES)
                .build();
        person = requiredMapper.readValue(PERSON_NAME_AND_NULL_AGE, Person.class);
        assertEquals("John", person.name);
        assertEquals(Integer.valueOf(0), person.age);

        MismatchedInputException missing = assertThrows(MismatchedInputException.class,
                () -> requiredMapper.readValue(VPackWireFixtureTest.hex("0a"), Person.class));
        assertTrue(missing.getMessage().contains("Null value for creator property 'name'"));

        MismatchedInputException explicitNull = assertThrows(MismatchedInputException.class,
                () -> requiredMapper.readValue(PERSON_AGE_AND_NULL_NAME, Person.class));
        assertTrue(explicitNull.getMessage().contains("Null value for creator property 'name'"));
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

    void __invoke_defaultingWithNull2977() throws Exception {
        try {
            defaultingWithNull2977();
        } finally {
        }
    }


    void __invoke_testCreatorAbsentPrimitiveInNestedObjectShouldDefault() throws Exception {
        try {
            testCreatorAbsentPrimitiveInNestedObjectShouldDefault();
        } finally {
        }
    }


    void __invoke_testCreatorAbsentPrimitiveShouldDefault() throws Exception {
        try {
            testCreatorAbsentPrimitiveShouldDefault();
        } finally {
        }
    }


    void __invoke_testCreatorNullPrimitive() throws Exception {
        try {
            testCreatorNullPrimitive();
        } finally {
        }
    }


    void __invoke_testCreatorNullPrimitiveInNestedObject() throws Exception {
        try {
            testCreatorNullPrimitiveInNestedObject();
        } finally {
        }
    }


    void __invoke_testRecordAbsentPrimitivesShouldDefault() throws Exception {
        try {
            testRecordAbsentPrimitivesShouldDefault();
        } finally {
        }
    }


    void __invoke_testRecordExplicitNullPrimitiveShouldFail() throws Exception {
        try {
            testRecordExplicitNullPrimitiveShouldFail();
        } finally {
        }
    }


    void __invoke_testRequiredNonNullParam() throws Exception {
        try {
            testRequiredNonNullParam();
        } finally {
        }
    }

}
