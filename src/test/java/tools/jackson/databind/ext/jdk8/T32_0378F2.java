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

import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0378F2 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] VALUE_X = VPackWireFixtureTest.hex(
            "0b 0c 01 45 76 61 6c 75 65 41 78 03");
private static final byte[] VALUE_TRUE = VPackWireFixtureTest.hex(
            "0b 0c 01 46 6d 79 44 61 74 61 1a 03");
private static final byte[] POLYMORPHIC_CONTAINED = VPackWireFixtureTest.hex(
            "0b 26 01 49 63 6f 6e 74 61 69 6e 65 64"
          + "0b 18 01 45 40 74 79 70 65 4d 43 6f 6e 74 61 69 6e 65 64 49 6d 70 6c 03 03");

    // Provenance: SchemaVisitorTest#testOptionalInteger().
    void testOptionalIntegerVpack() throws Exception {
        AtomicReference<Object> result = new AtomicReference<>();
        MAPPER.acceptJsonFormatVisitor(java.util.OptionalInt.class,
                numericVisitor(result, true));
        assertEquals(NumberType.INT, result.get());
    }

    // Provenance: SchemaVisitorTest#testOptionalLong().
    void testOptionalLongVpack() throws Exception {
        AtomicReference<Object> result = new AtomicReference<>();
        MAPPER.acceptJsonFormatVisitor(java.util.OptionalLong.class,
                numericVisitor(result, true));
        assertEquals(NumberType.LONG, result.get());
    }

    // Provenance: SchemaVisitorTest#testOptionalDouble().
    void testOptionalDoubleVpack() throws Exception {
        AtomicReference<Object> result = new AtomicReference<>();
        MAPPER.acceptJsonFormatVisitor(java.util.OptionalDouble.class,
                numericVisitor(result, false));
        assertEquals(NumberType.DOUBLE, result.get());
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

    void __invoke_testOptionalIntegerVpack() throws Exception {
        try {
            testOptionalIntegerVpack();
        } finally {
        }
    }


    void __invoke_testOptionalLongVpack() throws Exception {
        try {
            testOptionalLongVpack();
        } finally {
        }
    }


    void __invoke_testOptionalDoubleVpack() throws Exception {
        try {
            testOptionalDoubleVpack();
        } finally {
        }
    }

}
