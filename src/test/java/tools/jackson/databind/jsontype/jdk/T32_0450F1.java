package tools.jackson.databind.jsontype.jdk;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0450F1 {
private static final ObjectMapper MAPPER = VPackMapper.builder()
            .polymorphicTypeValidator(BasicPolymorphicTypeValidator.builder()
                    .allowIfBaseType(Object.class)
                    .allowIfBaseType(Serializable.class)
                    .allowIfSubType(T32_0450F1.class.getPackageName())
                    .build())
            .build();
private static final byte[] TAG_LIST = VPackWireFixtureTest.hex(
            "02 30 06 17 02 4f 2e 54 33 32 5f 30 34 35 30 46" +
                "31 24 54 61 67 41 41 03 13 06 17 02 4f 2e 54 33" +
                "32 5f 30 34 35 30 46 31 24 54 61 67 41 42 03 13");
private static final byte[] ENUM_INTERFACE = VPackWireFixtureTest.hex(
            "06 17 02 4f 2e 54 33 32 5f 30 34 35 30 46 31 24" +
                "54 61 67 41 42 03 13");
private static final byte[] A_CLASS_A1 = VPackWireFixtureTest.hex(
            "06 3f 02 76 74 6f 6f 6c 73 2e 6a 61 63 6b 73 6f" +
                "6e 2e 64 61 74 61 62 69 6e 64 2e 6a 73 6f 6e 74" +
                "79 70 65 2e 6a 64 6b 2e 54 33 32 5f 30 34 35 30" +
                "46 31 24 41 5f 43 4c 41 53 53 42 41 31 03 3a");
private static final byte[] A_MIN_CLASS_A1 = VPackWireFixtureTest.hex(
            "06 20 02 57 2e 54 33 32 5f 30 34 35 30 46 31 24" +
                "41 5f 4d 49 4e 5f 43 4c 41 53 53 42 41 31 03 1b");
private static final byte[] A_NAME_A1 = VPackWireFixtureTest.hex(
            "06 1a 02 51 54 33 32 5f 30 34 35 30 46 31 24 41" +
                "5f 4e 41 4d 45 42 41 31 03 15");
private static final byte[] A_SIMPLE_NAME_A1 = VPackWireFixtureTest.hex(
            "06 16 02 4d 41 5f 53 49 4d 50 4c 45 5f 4e 41 4d 45 42 41 31 03 11");

    // Provenance: ScalarTypingTest#testScalarsViaAbstractType().
    void testScalarsViaAbstractTypeVpack() throws Exception {
        assertAbstractScalar(Integer.valueOf(3));
        assertAbstractScalar("abc");
        assertAbstractScalar(Boolean.TRUE);
        assertAbstractScalar(Long.valueOf(7L));
        assertAbstractScalar(TestEnum.B);
    }

    // Provenance: ScalarTypingTest#testHeterogenousStringScalars().
    void testHeterogenousStringScalarsVpack() throws Exception {
        ScalarList input = new ScalarList()
                .add("Test")
                .add(Object.class)
                .add(java.util.UUID.fromString("00000000-0000-0000-0000-000000000000"));
        ScalarList result = MAPPER.readValue(MAPPER.writeValueAsBytes(input), ScalarList.class);
        assertNotNull(result.values);
        assertEquals(3, result.values.size());
        assertEquals("Test", result.values.get(0));
        assertEquals(Object.class, result.values.get(1));
        assertEquals(java.util.UUID.fromString("00000000-0000-0000-0000-000000000000"),
                result.values.get(2));
    }
private static void assertAbstractScalar(Serializable value) throws Exception {
        AbstractWrapper input = new AbstractWrapper(value);
        AbstractWrapper result = MAPPER.readValue(MAPPER.writeValueAsBytes(input),
                AbstractWrapper.class);
        assertEquals(value, result.value);
    }
private static byte[] replaceLastValue(byte[] source, String replacement) {
        byte[] value = VPackWireFixtureTest.hex(replacement);
        byte[] result = source.clone();
        result[result.length - 5] = value[0];
        result[result.length - 4] = value[1];
        result[result.length - 3] = value[2];
        return result;
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include = JsonTypeInfo.As.PROPERTY)
    interface EnumInterface { }
enum Tag implements EnumInterface { A, B }
static class EnumInterfaceList extends ArrayList<EnumInterface> { }
static class TagList extends ArrayList<Tag> { }
enum TestEnum { A, B, C }
static class UntypedEnumBean {
        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY,
                property = "__type")
        public Object value;
        UntypedEnumBean() { }
        UntypedEnumBean(TestEnum value) { this.value = value; }
    }
static class EnumContaintingClass<ENUM_TYPE extends Enum<ENUM_TYPE>> {
        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY,
                property = "@class")
        public ENUM_TYPE selected;
        EnumContaintingClass() { }
        EnumContaintingClass(ENUM_TYPE selected) { this.selected = selected; }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
    @JsonSubTypes(@JsonSubTypes.Type(TestEnum2775.class))
    interface Base2775 { }
@JsonTypeName("Test")
    enum TestEnum2775 implements Base2775 { VALUE }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
    @JsonSubTypes(@JsonSubTypes.Type(A_CLASS.class))
    interface InterClass { default void yes() { } }
enum A_CLASS implements InterClass { A1, A2 { @Override public void yes() { } } }
@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS)
    @JsonSubTypes(@JsonSubTypes.Type(A_MIN_CLASS.class))
    interface InterMinimalClass { default void yes() { } }
enum A_MIN_CLASS implements InterMinimalClass { A1, A2 { @Override public void yes() { } } }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
    @JsonSubTypes(@JsonSubTypes.Type(A_NAME.class))
    interface InterName { default void yes() { } }
enum A_NAME implements InterName { A1, A2 { @Override public void yes() { } } }
@JsonTypeInfo(use = JsonTypeInfo.Id.SIMPLE_NAME)
    @JsonSubTypes(@JsonSubTypes.Type(A_SIMPLE_NAME.class))
    interface InterSimpleName { default void yes() { } }
enum A_SIMPLE_NAME implements InterSimpleName { A1, A2 { @Override public void yes() { } } }
static class AbstractWrapper {
        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY)
        public Serializable value;
        AbstractWrapper() { }
        AbstractWrapper(Serializable value) { this.value = value; }
    }
static class ScalarList {
        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY)
        public List<Object> values = new ArrayList<>();
        ScalarList() { }
        ScalarList add(Object value) { values.add(value); return this; }
    }

    void __invoke_testScalarsViaAbstractTypeVpack() throws Exception {
        try {
            testScalarsViaAbstractTypeVpack();
        } finally {
        }
    }


    void __invoke_testHeterogenousStringScalarsVpack() throws Exception {
        try {
            testHeterogenousStringScalarsVpack();
        } finally {
        }
    }

}
