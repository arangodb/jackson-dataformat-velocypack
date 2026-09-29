package tools.jackson.databind.deser.creators;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.BeanDescription;
import tools.jackson.databind.DeserializationConfig;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.Deserializers;
import tools.jackson.databind.deser.jdk.EnumDeserializer;
import tools.jackson.databind.exc.InvalidDefinitionException;
import tools.jackson.databind.introspect.AnnotatedMethod;
import tools.jackson.databind.module.SimpleModule;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0205F1 {
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] CONSTRUCTOR_FROM_MAP = VPackWireFixtureTest.hex(
            "0b 0e 02 41 78 31 41 79 43 61 62 63 03 06");
private static final byte[] FACTORY_FROM_POINT = VPackWireFixtureTest.hex(
            "0b 0b 02 41 78 33 41 79 34 03 06");
private static final byte[] DECIMAL_TEXT = VPackWireFixtureTest.hex(
            "45 31 32 2e 35 37");
private static final byte[] DELEGATE_INTEGER_38 = VPackWireFixtureTest.hex(
            "28 26");
private static final byte[] TEXT_ABC = VPackWireFixtureTest.hex(
            "43 61 62 63");
private static final byte[] ENUM_A = VPackWireFixtureTest.hex(
            "45 65 6e 75 6d 41");
private static final byte[] ENUM_DECIMAL_8_0 = VPackWireFixtureTest.hex(
            "43 38 2e 30");
private static final byte[] ENUM_MAP = VPackWireFixtureTest.hex(
            "14 0f 45 65 6e 75 6d 41 45 76 61 6c 75 65 01");
private static final byte[] ENUM1291_NAME = VPackWireFixtureTest.hex(
            "42 56 32");
private static final byte[] DUAL_MODE_CODE = VPackWireFixtureTest.hex(
            "41 31");
private static final ObjectMapper MAPPER = VPackMapper.builder().build();

    // Provenance: DisablingCreatorsTest#testDisabling.
    void testDisabling() throws Exception {
        ObjectMapper mapper = MAPPER;

        NonConflictingCreators value = mapper.readValue(TEXT_ABC,
                NonConflictingCreators.class);
        assertNotNull(value);
        assertEquals("abc", value.value);

        InvalidDefinitionException exception = assertThrows(InvalidDefinitionException.class,
                () -> mapper.readValue(TEXT_ABC, ConflictingCreators.class));
        assertTrue(exception.getMessage().contains("Conflicting property-based creators"));
    }
static class CtorBean711 {
        protected String name;
        protected int age;

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        CtorBean711(@JacksonInject String name, int age) {
            this.name = name;
            this.age = age;
        }
    }
static class FactoryBean711 {
        protected String name1;
        protected String name2;
        protected int age;

        private FactoryBean711(int age, String name1, String name2) {
            this.age = age;
            this.name1 = name1;
            this.name2 = name2;
        }

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        static FactoryBean711 create(@JacksonInject String name1, int age,
                @JacksonInject String name2) {
            return new FactoryBean711(age, name1, name2);
        }
    }
static final class NoFieldSingletonWithPropertiesCreator {
        static final NoFieldSingletonWithPropertiesCreator INSTANCE =
                new NoFieldSingletonWithPropertiesCreator();

        private NoFieldSingletonWithPropertiesCreator() { }

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        static NoFieldSingletonWithPropertiesCreator of() { return INSTANCE; }
    }
static class ConstructorFromMap {
        int x;
        String y;

        @JsonCreator
        ConstructorFromMap(Map<?, ?> value) {
            x = ((Number) value.get("x")).intValue();
            y = (String) value.get("y");
        }
    }
static class Point {
        public int x;
        public int y;

        protected Point() { }
    }
static class FactoryFromPoint {
        int x;
        int y;

        private FactoryFromPoint(Point point) {
            x = point.x;
            y = point.y;
        }

        @JsonCreator
        static FactoryFromPoint createIt(Point point) {
            return new FactoryFromPoint(point);
        }
    }
static class FactoryFromDecimalString {
        int value;

        private FactoryFromDecimalString(BigDecimal decimal) {
            value = decimal.intValue();
        }

        @JsonCreator
        static FactoryFromDecimalString create(BigDecimal decimal) {
            return new FactoryFromDecimalString(decimal);
        }
    }
static class ConflictingCreators {
        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        ConflictingCreators(@JsonProperty("foo") String foo) { }

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        ConflictingCreators(@JsonProperty("foo") String foo,
                @JsonProperty("value") int value) { }
    }
static class NonConflictingCreators {
        String value;

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        NonConflictingCreators(String value) { this.value = value; }

        @JsonCreator(mode = JsonCreator.Mode.DISABLED)
        NonConflictingCreators(String value, int ignored) { }
    }
enum EnumWithCreator {
        A, B;

        @JsonCreator
        static EnumWithCreator fromEnum(String value) {
            if ("enumA".equals(value)) return A;
            if ("enumB".equals(value)) return B;
            return null;
        }
    }
enum EnumWithBDCreator {
        E5, E8;

        @JsonCreator
        static EnumWithBDCreator create(BigDecimal value) {
            if (value.longValue() == 5L) return E5;
            if (value.longValue() == 8L) return E8;
            return null;
        }
    }
enum Enum1291 {
        V1("val1"), V2("val2"), V3("val3"), V4("val4"), V5("val5"), V6("val6");

        private final String value;

        Enum1291(String value) { this.value = value; }

        @Override
        public String toString() { return value; }
    }
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @JsonIncludeProperties({ "code", "desc" })
    enum ChannelEnum {
        ALIPAY(0, "Alipay"), WECHAT(1, "WeChat");

        private final int code;
        private final String desc;

        ChannelEnum(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        @JsonProperty("code")
        public int getCode() { return code; }

        @JsonProperty("desc")
        public String getDesc() { return desc; }

        @JsonCreator
        private static ChannelEnum ofCode(@JsonProperty("code") String code) {
            if (code == null) return null;
            int intCode = Integer.parseInt(code);
            for (ChannelEnum value : values()) {
                if (value.code == intCode) return value;
            }
            return null;
        }
    }
static class DelegatingDeserializers extends Deserializers.Base {
        @Override
        public ValueDeserializer<?> findEnumDeserializer(JavaType type,
                DeserializationConfig config, BeanDescription.Supplier beanDescRef) {
            Collection<AnnotatedMethod> factoryMethods = beanDescRef.get().getFactoryMethods();
            if (factoryMethods != null) {
                for (AnnotatedMethod method : factoryMethods) {
                    JsonCreator creator = method.getAnnotation(JsonCreator.class);
                    if (creator != null) {
                        return EnumDeserializer.deserializerForCreator(config,
                                type.getRawClass(), method, null, null, null);
                    }
                }
            }
            return null;
        }

        @Override
        public boolean hasDeserializerFor(DeserializationConfig config, Class<?> valueType) {
            return false;
        }
    }
static class DelegatingDeserializersModule extends SimpleModule {
        private static final long serialVersionUID = 1L;

        @Override
        public void setupModule(SetupContext context) {
            context.addDeserializers(new DelegatingDeserializers());
        }
    }

    void __invoke_testDisabling() throws Exception {
        try {
            testDisabling();
        } finally {
        }
    }

}
