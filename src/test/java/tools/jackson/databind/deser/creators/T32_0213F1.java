package tools.jackson.databind.deser.creators;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0213F1 {
private static final byte[] SIMPLE_CONSTRUCTOR = VPackWireFixtureTest.hex(
            "14 07 41 78 28 2a 01");
private static final byte[] NO_ARGS_FACTORY = VPackWireFixtureTest.hex(
            "14 07 41 79 28 0d 01");
private static final byte[] DOUBLE_025 = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 d0 3f");
private static final byte[] BOOLEAN_TRUE = VPackWireFixtureTest.hex("1a");
private static final byte[] BIG_INTEGER = VPackWireFixtureTest.hex(
            "c8 0a 00 00 00 00 09 22 33 72 03 68 54 77 58 17");
private static final byte[] BIG_DECIMAL = VPackWireFixtureTest.hex(
            "c8 02 ff ff ff ff 04 25");
private static final byte[] SIMPLE_FACTORY = VPackWireFixtureTest.hex(
            "14 0e 41 66 1b 00 00 00 00 00 00 d0 3f 01");
private static final byte[] STRING_ABC = VPackWireFixtureTest.hex(
            "43 61 62 63");
private static final byte[] STRING_XYZ = VPackWireFixtureTest.hex(
            "43 78 79 7a");
private static final byte[] ABSTRACT_FACTORY = VPackWireFixtureTest.hex(
            "14 06 41 61 33 01");
private static final byte[] CONSTRUCTOR_CHOICE = VPackWireFixtureTest.hex(
            "14 1d 48 69 6e 74 46 69 65 6c 64 31 4b 73 74 72 69 6e 67 46 69 65 6c 64 "
          + "43 66 6f 6f 02");
private static final byte[] MULTI_ARGUMENT = VPackWireFixtureTest.hex(
            "14 13 43 62 61 72 43 62 61 72 43 66 6f 6f 43 66 6f 6f 02");
private static final ObjectMapper MAPPER = VPackMapper.builder().build();

    // Provenance: TestCreators2#testAbstractFactory.
    void testAbstractFactory() throws Exception {
        AbstractBase bean = MAPPER.readValue(ABSTRACT_FACTORY, AbstractBase.class);
        assertNotNull(bean);
        AbstractBaseImpl impl = (AbstractBaseImpl) bean;
        assertEquals(1, impl.props.size());
        assertEquals(Integer.valueOf(3), impl.props.get("a"));
    }

    // Provenance: TestCreators2#testConstructorChoice.
    void testConstructorChoice() throws Exception {
        MultiPropCreator1476 pojo = MAPPER.readValue(CONSTRUCTOR_CHOICE,
                MultiPropCreator1476.class);
        assertEquals(1, pojo.getIntField());
        assertEquals("foo", pojo.getStringField());
    }

    // Provenance: TestCreators2#testCreatorMultipleArgumentWithoutAnnotation.
    void testCreatorMultipleArgumentWithoutAnnotation() throws Exception {
        AutoDetectConstructorBean value = MAPPER.readValue(MULTI_ARGUMENT,
                AutoDetectConstructorBean.class);
        assertEquals("bar", value.bar);
        assertEquals("foo", value.foo);
    }
static class ConstructorBean {
        int x;

        @JsonCreator
        protected ConstructorBean(@JsonProperty("x") int x) {
            this.x = x;
        }
    }
static class NoArgFactoryBean {
        public int x;
        public int y;

        public NoArgFactoryBean(int value) {
            x = value;
        }

        @JsonCreator
        public static NoArgFactoryBean create() {
            return new NoArgFactoryBean(123);
        }
    }
static class DoubleConstructorBean {
        Double d;

        @JsonCreator
        protected DoubleConstructorBean(Double d) {
            this.d = d;
        }
    }
static class BooleanConstructorBean {
        Boolean b;

        protected BooleanConstructorBean(Boolean b) {
            this.b = b;
        }
    }
static class BooleanConstructorBean2 {
        boolean b;

        protected BooleanConstructorBean2(boolean b) {
            this.b = b;
        }
    }
protected static class BigIntegerWrapper {
        BigInteger _value;

        public BigIntegerWrapper() { }

        public BigIntegerWrapper(BigInteger value) {
            _value = value;
        }
    }
protected static class BigDecimalWrapper {
        BigDecimal _value;

        public BigDecimalWrapper() { }

        public BigDecimalWrapper(BigDecimal value) {
            _value = value;
        }
    }
static class FactoryBean {
        double d;

        private FactoryBean(double value, boolean dummy) {
            d = value;
        }

        @JsonCreator
        protected static FactoryBean createIt(@JsonProperty("f") double value) {
            return new FactoryBean(value, true);
        }
    }
static class StringFactoryBean {
        String value;

        private StringFactoryBean(String value, boolean dummy) {
            this.value = value;
        }

        @JsonCreator
        static StringFactoryBean valueOf(String value) {
            return new StringFactoryBean(value, true);
        }
    }
static class FromStringBean {
        protected String value;

        private FromStringBean(String value, boolean dummy) {
            this.value = value;
        }

        public static FromStringBean fromString(String value) {
            return new FromStringBean(value, false);
        }
    }
abstract static class AbstractBase {
        @JsonCreator
        public static AbstractBase create(Map<String, Object> props) {
            return new AbstractBaseImpl(props);
        }
    }
static class AbstractBaseImpl extends AbstractBase {
        protected Map<String, Object> props;

        public AbstractBaseImpl(Map<String, Object> props) {
            this.props = props;
        }
    }
static final class MultiPropCreator1476 {
        private final int intField;
        private final String stringField;

        public MultiPropCreator1476(@JsonProperty("intField") int intField) {
            this(intField, "empty");
        }

        public MultiPropCreator1476(@JsonProperty("stringField") String stringField) {
            this(-1, stringField);
        }

        @JsonCreator
        public MultiPropCreator1476(@JsonProperty("intField") int intField,
                @JsonProperty("stringField") String stringField) {
            this.intField = intField;
            this.stringField = stringField;
        }

        public int getIntField() {
            return intField;
        }

        public String getStringField() {
            return stringField;
        }
    }
static class AutoDetectConstructorBean {
        protected final String foo;
        protected final String bar;

        public AutoDetectConstructorBean(@JsonProperty("bar") String bar,
                @JsonProperty("foo") String foo) {
            this.bar = bar;
            this.foo = foo;
        }
    }

    void __invoke_testAbstractFactory() throws Exception {
        try {
            testAbstractFactory();
        } finally {
        }
    }


    void __invoke_testConstructorChoice() throws Exception {
        try {
            testConstructorChoice();
        } finally {
        }
    }


    void __invoke_testCreatorMultipleArgumentWithoutAnnotation() throws Exception {
        try {
            testCreatorMultipleArgumentWithoutAnnotation();
        } finally {
        }
    }

}
