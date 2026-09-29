package tools.jackson.databind.ext.jdk8;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import tools.jackson.core.JsonParser.NumberType;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import tools.jackson.databind.jsonFormatVisitors.JsonIntegerFormatVisitor;
import tools.jackson.databind.jsonFormatVisitors.JsonNumberFormatVisitor;
import tools.jackson.databind.ser.BeanSerializerFactory;
import tools.jackson.databind.ser.SerializationContextExt;
import tools.jackson.databind.ser.SerializerCache;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0378F0 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] VALUE_X = VPackWireFixtureTest.hex(
            "0b 0c 01 45 76 61 6c 75 65 41 78 03");
private static final byte[] VALUE_TRUE = VPackWireFixtureTest.hex(
            "0b 0c 01 46 6d 79 44 61 74 61 1a 03");
private static final byte[] POLYMORPHIC_CONTAINED = VPackWireFixtureTest.hex(
            "0b 26 01 49 63 6f 6e 74 61 69 6e 65 64"
          + "0b 18 01 45 40 74 79 70 65 4d 43 6f 6e 74 61 69 6e 65 64 49 6d 70 6c 03 03");

    // Provenance: OptionalnclusionTest#testSerOptNonEmpty().
    void testSerOptNonEmptyVpack() throws Exception {
        assertArrayEquals(EMPTY_OBJECT,
                inclusionMapper(JsonInclude.Include.NON_EMPTY)
                        .writeValueAsBytes(new OptionalData()));
    }

    // Provenance: OptionalnclusionTest#testSerOptNonDefault().
    void testSerOptNonDefaultVpack() throws Exception {
        assertArrayEquals(EMPTY_OBJECT,
                inclusionMapper(JsonInclude.Include.NON_DEFAULT)
                        .writeValueAsBytes(new OptionalData()));
    }

    // Provenance: OptionalnclusionTest#testSerOptNonAbsent().
    void testSerOptNonAbsentVpack() throws Exception {
        assertArrayEquals(EMPTY_OBJECT,
                inclusionMapper(JsonInclude.Include.NON_ABSENT)
                        .writeValueAsBytes(new OptionalData()));
    }

    // Provenance: OptionalnclusionTest#testExcludeEmptyStringViaOptional().
    void testExcludeEmptyStringViaOptionalVpack() throws Exception {
        OptionalNonEmptyStringBean bean = new OptionalNonEmptyStringBean("x");
        assertArrayEquals(VALUE_X, MAPPER.writeValueAsBytes(bean));

        bean = new OptionalNonEmptyStringBean(null);
        assertArrayEquals(EMPTY_OBJECT, MAPPER.writeValueAsBytes(bean));

        bean = new OptionalNonEmptyStringBean("");
        assertArrayEquals(EMPTY_OBJECT, MAPPER.writeValueAsBytes(bean));
    }

    // Provenance: OptionalnclusionTest#testSerPropInclusionAlways().
    void testSerPropInclusionAlwaysVpack() throws Exception {
        assertArrayEquals(VALUE_TRUE,
                propertyInclusionMapper(JsonInclude.Include.ALWAYS)
                        .writeValueAsBytes(OptionalGenericData.construct(Boolean.TRUE)));
    }

    // Provenance: OptionalnclusionTest#testSerPropInclusionNonNull().
    void testSerPropInclusionNonNullVpack() throws Exception {
        assertArrayEquals(VALUE_TRUE,
                propertyInclusionMapper(JsonInclude.Include.NON_NULL)
                        .writeValueAsBytes(OptionalGenericData.construct(Boolean.TRUE)));
    }

    // Provenance: OptionalnclusionTest#testSerPropInclusionNonAbsent().
    void testSerPropInclusionNonAbsentVpack() throws Exception {
        assertArrayEquals(VALUE_TRUE,
                propertyInclusionMapper(JsonInclude.Include.NON_ABSENT)
                        .writeValueAsBytes(OptionalGenericData.construct(Boolean.TRUE)));
    }

    // Provenance: OptionalnclusionTest#testSerPropInclusionNonEmpty().
    void testSerPropInclusionNonEmptyVpack() throws Exception {
        assertArrayEquals(VALUE_TRUE,
                propertyInclusionMapper(JsonInclude.Include.NON_EMPTY)
                        .writeValueAsBytes(OptionalGenericData.construct(Boolean.TRUE)));
    }
private static JsonFormatVisitorWrapper numericVisitor(AtomicReference<Object> result,
            boolean integer) {
        return new JsonFormatVisitorWrapper.Base(new SerializationContextExt.Impl(
                new VPackFactory(), MAPPER.serializationConfig(), null,
                BeanSerializerFactory.instance, new SerializerCache())) {
            @Override
            public JsonIntegerFormatVisitor expectIntegerFormat(JavaType type) {
                return new JsonIntegerFormatVisitor.Base() {
                    @Override
                    public void numberType(NumberType type) {
                        if (integer) {
                            result.set(type);
                        }
                    }
                };
            }

            @Override
            public JsonNumberFormatVisitor expectNumberFormat(JavaType type) {
                return new JsonNumberFormatVisitor.Base() {
                    @Override
                    public void numberType(NumberType type) {
                        if (!integer) {
                            result.set(type);
                        }
                    }
                };
            }
        };
    }
private static ObjectMapper inclusionMapper(JsonInclude.Include inclusion) {
        return VPackMapper.builder()
                .changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(inclusion))
                .build();
    }
private static ObjectMapper propertyInclusionMapper(JsonInclude.Include contentInclusion) {
        return VPackMapper.builder()
                .changeDefaultPropertyInclusion(incl -> JsonInclude.Value.construct(
                        JsonInclude.Include.NON_ABSENT, contentInclusion))
                .build();
    }
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
    static final class OptionalData {
        public Optional<String> myString;

        OptionalData() {
            myString = null;
        }
    }
static class OptionalNonEmptyStringBean {
        @JsonInclude(value = JsonInclude.Include.NON_EMPTY,
                content = JsonInclude.Include.NON_EMPTY)
        public Optional<String> value;

        OptionalNonEmptyStringBean(String value) {
            this.value = Optional.ofNullable(value);
        }
    }
static final class OptionalGenericData<T> {
        public Optional<T> myData;

        static <T> OptionalGenericData<T> construct(T data) {
            OptionalGenericData<T> result = new OptionalGenericData<>();
            result.myData = Optional.of(data);
            return result;
        }
    }
static class Container {
        public Optional<Contained> contained;
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
    @JsonSubTypes({ @JsonSubTypes.Type(name = "ContainedImpl", value = ContainedImpl.class) })
    interface Contained { }
static class ContainedImpl implements Contained { }

    void __invoke_testSerOptNonEmptyVpack() throws Exception {
        try {
            testSerOptNonEmptyVpack();
        } finally {
        }
    }


    void __invoke_testSerOptNonDefaultVpack() throws Exception {
        try {
            testSerOptNonDefaultVpack();
        } finally {
        }
    }


    void __invoke_testSerOptNonAbsentVpack() throws Exception {
        try {
            testSerOptNonAbsentVpack();
        } finally {
        }
    }


    void __invoke_testExcludeEmptyStringViaOptionalVpack() throws Exception {
        try {
            testExcludeEmptyStringViaOptionalVpack();
        } finally {
        }
    }


    void __invoke_testSerPropInclusionAlwaysVpack() throws Exception {
        try {
            testSerPropInclusionAlwaysVpack();
        } finally {
        }
    }


    void __invoke_testSerPropInclusionNonNullVpack() throws Exception {
        try {
            testSerPropInclusionNonNullVpack();
        } finally {
        }
    }


    void __invoke_testSerPropInclusionNonAbsentVpack() throws Exception {
        try {
            testSerPropInclusionNonAbsentVpack();
        } finally {
        }
    }


    void __invoke_testSerPropInclusionNonEmptyVpack() throws Exception {
        try {
            testSerPropInclusionNonEmptyVpack();
        } finally {
        }
    }

}
