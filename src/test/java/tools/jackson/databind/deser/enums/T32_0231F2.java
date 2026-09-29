package tools.jackson.databind.deser.enums;

import java.util.EnumSet;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.EnumNaming;
import tools.jackson.databind.EnumNamingStrategies;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;
import tools.jackson.databind.jsontype.impl.DefaultTypeResolverBuilder;
import tools.jackson.databind.jsontype.impl.StdTypeResolverBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0231F2 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] SAUCE_KETCH_UP = VPackWireFixtureTest.hex(
            "14 11 45 73 61 75 63 65 47 6b 65 74 63 68 55 70 01");
private static final byte[] SAUCE_MAYO_NEZZ = VPackWireFixtureTest.hex(
            "0b 13 01 45 73 61 75 63 65 48 6d 61 79 6f 4e 65 7a 7a 03");
private static final byte[] REAL_NAME = VPackWireFixtureTest.hex(
            "48 72 65 61 6c 4e 61 6d 65");
private static final byte[] FOO = VPackWireFixtureTest.hex("43 46 4f 4f");
private static final byte[] BAR = VPackWireFixtureTest.hex("43 42 41 52");
private static final byte[] CAT = VPackWireFixtureTest.hex("43 43 41 54");
private static final byte[] WRAPPED_FOO = VPackWireFixtureTest.hex(
            "0b 10 01 47 77 72 61 70 70 65 64 43 46 4f 4f 03");
private static final String ENUM_TYPE =
            "java.util.EnumSet<tools.jackson.dataformat.velocypack."
          + "T32_0231F2$TestEnum4849>";
private static final byte[] PREVIOUS_ENUM_SET = VPackWireFixtureTest.hex(
            "06 65 02 8d 6a 61 76 61 2e 75 74 69 6c 2e 45 6e" +
                "75 6d 53 65 74 3c 74 6f 6f 6c 73 2e 6a 61 63 6b" +
                "73 6f 6e 2e 64 61 74 61 62 69 6e 64 2e 64 65 73" +
                "65 72 2e 65 6e 75 6d 73 2e 54 33 32 5f 30 32 33" +
                "31 46 32 24 54 65 73 74 45 6e 75 6d 34 38 34 39" +
                "3e 02 12 4f 54 45 53 54 5f 45 4e 55 4d 5f 56 41" +
                "4c 55 45 03 51");

    // Provenance: EnumSetDeserializationWithDefaultTyping4849Test#testHardCodedDeserializationFromPreviousJackson4849.
    void testHardCodedDeserializationFromPreviousJackson4849Vpack() throws Exception {
        Object value = defaultTypingMapper().readValue(PREVIOUS_ENUM_SET, Object.class);
        assertEquals(EnumSet.of(TestEnum4849.TEST_ENUM_VALUE), value);
    }

    // Provenance: EnumSetDeserializationWithDefaultTyping4849Test#testSerializationDeserializationRoundTrip4849.
    void testSerializationDeserializationRoundTrip4849Vpack() throws Exception {
        ObjectMapper mapper = defaultTypingMapper();
        EnumSet<TestEnum4849> input = EnumSet.of(TestEnum4849.TEST_ENUM_VALUE);
        byte[] encoded = mapper.writeValueAsBytes(input);
        Object value = mapper.readValue(encoded, Object.class);
        assertEquals(input, value);
    }
private static ObjectMapper defaultTypingMapper() {
        PolymorphicTypeValidator validator = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType("")
                .build();
        DefaultTypeResolverBuilder resolverBuilder = new DefaultTypeResolverBuilder(
                validator, DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY) {
            @Override
            public boolean useForType(JavaType type) {
                return true;
            }
        };
        StdTypeResolverBuilder typeResolver = resolverBuilder.init(
                JsonTypeInfo.Value.construct(JsonTypeInfo.Id.CLASS,
                        JsonTypeInfo.As.PROPERTY, "", Object.class, false, null, null),
                null);
        return VPackMapper.builder().setDefaultTyping(typeResolver).build();
    }
@EnumNaming(EnumNamingStrategies.LowerCamelCaseStrategy.class)
    enum EnumSauceC { KETCH_UP, MAYO_NEZZ }
static class EnumSauceWrapperBean {
        public EnumSauceC sauce;

        @JsonCreator
        EnumSauceWrapperBean(@JsonProperty("sce") EnumSauceC sce) {
            sauce = sce;
        }
    }
enum BaseEnum { REAL_NAME }
enum Field4302Enum {
        FOO(0);
        public final int foo;
        Field4302Enum(int foo) { this.foo = foo; }
    }
enum Getter4302Enum {
        BAR("bar");
        public String bar;
        Getter4302Enum(String bar) { this.bar = bar; }
        public String getBar() { return "bar"; }
    }
enum Setter4302Enum {
        CAT("dog");
        public String cat;
        Setter4302Enum(String cat) { this.cat = cat; }
        public void setCat(String cat) { this.cat = cat; }
    }
static class Field4302Wrapper {
        public Field4302Enum wrapped;
        Field4302Wrapper() { }
        Field4302Wrapper(Field4302Enum value) { wrapped = value; }
    }
enum TestEnum4849 { TEST_ENUM_VALUE }

    void __invoke_testHardCodedDeserializationFromPreviousJackson4849Vpack() throws Exception {
        try {
            testHardCodedDeserializationFromPreviousJackson4849Vpack();
        } finally {
        }
    }


    void __invoke_testSerializationDeserializationRoundTrip4849Vpack() throws Exception {
        try {
            testSerializationDeserializationRoundTrip4849Vpack();
        } finally {
        }
    }

}
