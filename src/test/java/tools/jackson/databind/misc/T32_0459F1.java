package tools.jackson.databind.misc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.MapperConfig;
import tools.jackson.databind.introspect.AnnotatedMember;
import tools.jackson.databind.introspect.AnnotatedParameter;
import tools.jackson.databind.introspect.JacksonAnnotationIntrospector;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0459F1 {
private static final byte[] REGULAR_INPUT = VPackWireFixtureTest.hex(
            "14 1c 4b 70 68 6f 6e 65 4e 75 6d 62 65 72 4c 31 32 33 2d 34 35 36 2d 37 38 39 30 01");
private static final byte[] REGULAR_OUTPUT = VPackWireFixtureTest.hex(
            "0b 1d 01 4b 70 68 6f 6e 65 4e 75 6d 62 65 72 4c 31 32 33 2d 34 35 36 2d 37 38 39 30 03");
private static final byte[] CREATOR_DOUBLE_INPUT = VPackWireFixtureTest.hex(
            "14 1d 46 69 50 68 6f 6e 65 49 69 50 68 6f 6e 65 20 31 35 44 6e 61 6d 65 43 4a 61 79 02");
private static final byte[] CREATOR_DOUBLE_OUTPUT = VPackWireFixtureTest.hex(
            "0b 1f 02 46 69 50 68 6f 6e 65 49 69 50 68 6f 6e 65 20 31 35 44 6e 61 6d 65 43 4a 61 79 03 14");
private static final byte[] CREATOR_SINGLE_INPUT = VPackWireFixtureTest.hex(
            "14 14 46 69 50 68 6f 6e 65 49 69 50 68 6f 6e 65 20 31 35 01");
private static final byte[] CREATOR_SINGLE_OUTPUT = VPackWireFixtureTest.hex(
            "0b 15 01 46 69 50 68 6f 6e 65 49 69 50 68 6f 6e 65 20 31 35 03");
private static final byte[] CREATOR_NAME_INPUT = VPackWireFixtureTest.hex(
            "14 0c 44 6e 61 6d 65 43 4a 61 79 01");
private static final byte[] CREATOR_NAME_OUTPUT = VPackWireFixtureTest.hex(
            "0b 0d 01 44 6e 61 6d 65 43 4a 61 79 03");
private static final byte[] ACCESSOR_INPUT = VPackWireFixtureTest.hex(
            "14 25 45 61 50 72 6f 70 4a 61 50 72 6f 70 56 61 6c 75 65"
          + "45 70 72 6f 70 31 4a 70 72 6f 70 31 56 61 6c 75 65 02");
private static final byte[] ACCESSOR_OUTPUT = VPackWireFixtureTest.hex(
            "0b 23 02 45 61 50 72 6f 70 4a 61 50 72 6f 70 56 61 6c 75 65"
          + "4b 61 6e 6f 74 68 65 72 50 72 6f 70 18 03 14");
private static final ObjectMapper UPPER_PREFIX_MAPPER = VPackMapper.builder()
            .annotationIntrospector(new ImplicitNameIntrospector())
            .enable(MapperFeature.FIX_FIELD_NAME_UPPER_CASE_PREFIX)
            .build();

    // Provenance: IPhoneStyleProperty5292Test#testDeserDouble().
    void testDeserDoubleVpack() throws Exception {
        AppleDouble459 apple = new AppleDouble459("iPhone 15", "Jay");
        assertArrayEquals(CREATOR_DOUBLE_OUTPUT, UPPER_PREFIX_MAPPER.writeValueAsBytes(apple));

        AppleDouble459 result = UPPER_PREFIX_MAPPER.readValue(
                CREATOR_DOUBLE_INPUT, AppleDouble459.class);
        assertEquals("Jay", result.getName());
        assertEquals("iPhone 15", result.getIPhone());
    }

    // Provenance: IPhoneStyleProperty5292Test#testHappyCaseSingleArgString().
    void testHappyCaseSingleArgStringVpack() throws Exception {
        AppleSingleNonTarget459 apple = new AppleSingleNonTarget459("Jay");
        assertArrayEquals(CREATOR_NAME_OUTPUT, UPPER_PREFIX_MAPPER.writeValueAsBytes(apple));

        AppleSingleNonTarget459 result = UPPER_PREFIX_MAPPER.readValue(
                CREATOR_NAME_INPUT, AppleSingleNonTarget459.class);
        assertEquals("Jay", result.getName());
    }

    // Provenance: IPhoneStyleProperty5292Test#testSingleArgCase().
    void testSingleArgCaseVpack() throws Exception {
        AppleSingleIsTarget459 apple = new AppleSingleIsTarget459("iPhone 15");
        assertArrayEquals(CREATOR_SINGLE_OUTPUT, UPPER_PREFIX_MAPPER.writeValueAsBytes(apple));

        AppleSingleIsTarget459 result = UPPER_PREFIX_MAPPER.readValue(
                CREATOR_SINGLE_INPUT, AppleSingleIsTarget459.class);
        assertEquals("iPhone 15", result.getIPhone());
    }
static class RegularBean459 {
        private String phoneNumber;

        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String value) { phoneNumber = value; }
    }
static class AppleSingleNonTarget459 {
        private final String name;

        public AppleSingleNonTarget459(@ImplicitName("name") String name) {
            this.name = name;
        }

        public String getName() { return name; }
    }
static class AppleSingleIsTarget459 {
        private final String iPhone;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public AppleSingleIsTarget459(@ImplicitName("iPhone") String iPhone) {
            this.iPhone = iPhone;
        }

        public String getIPhone() { return iPhone; }
    }
@JsonPropertyOrder({ "iPhone", "name" })
    static class AppleDouble459 {
        private final String _iphone;
        private final String name;

        public AppleDouble459(@ImplicitName("iPhone") String iPhone,
                @ImplicitName("name") String name) {
            _iphone = iPhone;
            this.name = name;
        }

        public String getIPhone() { return _iphone; }
        public String getName() { return name; }
    }
@JsonPropertyOrder({ "aProp" })
    static class AccessorPojo459 {
        private String aProp;
        private String anotherProp;

        public String getaProp() { return aProp; }
        public void setaProp(String value) { aProp = value; }
        public String getAnotherProp() { return anotherProp; }
        public void setAnotherProp(String value) { anotherProp = value; }
    }
@Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.RUNTIME)
    private @interface ImplicitName {
        String value();
    }
private static class ImplicitNameIntrospector extends JacksonAnnotationIntrospector {
        @Override
        public String findImplicitPropertyName(MapperConfig<?> config, AnnotatedMember member) {
            if (member instanceof AnnotatedParameter parameter) {
                ImplicitName annotation = parameter.getAnnotation(ImplicitName.class);
                if (annotation != null) {
                    return annotation.value();
                }
            }
            return super.findImplicitPropertyName(config, member);
        }
    }

    void __invoke_testDeserDoubleVpack() throws Exception {
        try {
            testDeserDoubleVpack();
        } finally {
        }
    }


    void __invoke_testHappyCaseSingleArgStringVpack() throws Exception {
        try {
            testHappyCaseSingleArgStringVpack();
        } finally {
        }
    }


    void __invoke_testSingleArgCaseVpack() throws Exception {
        try {
            testSingleArgCaseVpack();
        } finally {
        }
    }

}
