package tools.jackson.databind.ext.jdk8;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.databind.DatabindContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0372F2 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] SIMPLE_STRING = VPackWireFixtureTest.hex(
            "4c 73 69 6d 70 6c 65 53 74 72 69 6e 67");
private static final byte[] BOOLEAN_TRUE = VPackWireFixtureTest.hex(
            "0b 0b 01 45 76 61 6c 75 65 1a 03");
private static final byte[] BOOLEAN_NULL = VPackWireFixtureTest.hex(
            "0b 0b 01 45 76 61 6c 75 65 18 03");
private static final byte[] BOOLEAN_FALSE = VPackWireFixtureTest.hex(
            "0b 0b 01 45 76 61 6c 75 65 19 03");
private static final byte[] BOOLEAN_EMPTY_STRING = VPackWireFixtureTest.hex(
            "0b 0b 01 45 76 61 6c 75 65 40 03");
private static final byte[] EMPTY_OPTIONAL_MAP = VPackWireFixtureTest.hex(
            "0b 0c 01 46 76 61 6c 75 65 73 0a 03");
private static final byte[] VALUE_OPTIONAL_MAP = VPackWireFixtureTest.hex(
            "0b 19 01 46 76 61 6c 75 65 73 "
          + "0b 0e 01 43 6b 65 79 45 76 61 6c 75 65 03 03");
private static final byte[] EMPTY_STRING_OPTIONAL_MAP = VPackWireFixtureTest.hex(
            "0b 14 01 46 76 61 6c 75 65 73 "
          + "0b 09 01 43 6b 65 79 40 03 03");

    // Provenance: OptionalMapsTest#testMapElementInclusion().
    void testMapElementInclusionVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder().changeDefaultPropertyInclusion(
                incl -> incl.withValueInclusion(JsonInclude.Include.NON_NULL)
                    .withContentInclusion(JsonInclude.Include.NON_ABSENT))
                .build();

        assertArrayEquals(EMPTY_OPTIONAL_MAP,
                mapper.writeValueAsBytes(new OptMapBean("key", Optional.empty())));
        assertArrayEquals(VALUE_OPTIONAL_MAP,
                mapper.writeValueAsBytes(new OptMapBean("key", Optional.of("value"))));
        assertArrayEquals(EMPTY_STRING_OPTIONAL_MAP,
                mapper.writeValueAsBytes(new OptMapBean("key", Optional.of(""))));
    }
static final class OptionalData {
        public Optional<String> myString;
    }
static class BooleanBean {
        public Optional<Boolean> value;

        public BooleanBean() { }

        BooleanBean(Boolean value) {
            this.value = Optional.ofNullable(value);
        }
    }
static final class OptMapBean {
        public Map<String, Optional<?>> values;

        OptMapBean(String key, Optional<?> value) {
            values = new LinkedHashMap<>();
            values.put(key, value);
        }
    }
static class NoCheckSubTypeValidator extends PolymorphicTypeValidator.Base {
        private static final long serialVersionUID = 1L;

        @Override
        public Validity validateBaseType(DatabindContext ctxt, JavaType baseType) {
            return Validity.ALLOWED;
        }
    }

    void __invoke_testMapElementInclusionVpack() throws Exception {
        try {
            testMapElementInclusionVpack();
        } finally {
        }
    }

}
