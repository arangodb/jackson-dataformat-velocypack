package tools.jackson.databind.deser.creators;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0213F0 {
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

    // Provenance: TestCreators#testSimpleConstructor.
    void testSimpleConstructor() throws Exception {
        ConstructorBean bean = MAPPER.readValue(SIMPLE_CONSTRUCTOR, ConstructorBean.class);
        assertEquals(42, bean.x);
    }

    // Provenance: TestCreators#testNoArgsFactory.
    void testNoArgsFactory() throws Exception {
        NoArgFactoryBean value = MAPPER.readValue(NO_ARGS_FACTORY, NoArgFactoryBean.class);
        assertEquals(13, value.y);
        assertEquals(123, value.x);
    }

    // Provenance: TestCreators#testSimpleDoubleConstructor.
    void testSimpleDoubleConstructor() throws Exception {
        DoubleConstructorBean bean = MAPPER.readValue(DOUBLE_025, DoubleConstructorBean.class);
        assertEquals(Double.valueOf(0.25), bean.d);
    }

    // Provenance: TestCreators#testSimpleBooleanConstructor.
    void testSimpleBooleanConstructor() throws Exception {
        BooleanConstructorBean bean = MAPPER.readValue(BOOLEAN_TRUE, BooleanConstructorBean.class);
        assertEquals(Boolean.TRUE, bean.b);

        BooleanConstructorBean2 bean2 = MAPPER.readValue(BOOLEAN_TRUE, BooleanConstructorBean2.class);
        assertTrue(bean2.b);
    }

    // Provenance: TestCreators#testSimpleBigIntegerConstructor.
    void testSimpleBigIntegerConstructor() throws Exception {
        BigInteger expected = BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.TEN);
        BigIntegerWrapper result = MAPPER.readValue(BIG_INTEGER, BigIntegerWrapper.class);
        assertEquals(expected, result._value);
    }

    // Provenance: TestCreators#testSimpleBigDecimalConstructor.
    void testSimpleBigDecimalConstructor() throws Exception {
        BigDecimalWrapper result = MAPPER.readValue(BIG_DECIMAL, BigDecimalWrapper.class);
        assertEquals(new BigDecimal("42.5"), result._value);
    }

    // Provenance: TestCreators#testSimpleFactory.
    void testSimpleFactory() throws Exception {
        FactoryBean bean = MAPPER.readValue(SIMPLE_FACTORY, FactoryBean.class);
        assertEquals(0.25, bean.d);
    }

    // Provenance: TestCreators#testStringFactory.
    void testStringFactory() throws Exception {
        StringFactoryBean bean = MAPPER.readValue(STRING_ABC, StringFactoryBean.class);
        assertEquals("abc", bean.value);
    }

    // Provenance: TestCreators#testStringFactoryAlt.
    void testStringFactoryAlt() throws Exception {
        FromStringBean bean = MAPPER.readValue(STRING_XYZ, FromStringBean.class);
        assertEquals("xyz", bean.value);
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

    void __invoke_testSimpleConstructor() throws Exception {
        try {
            testSimpleConstructor();
        } finally {
        }
    }


    void __invoke_testNoArgsFactory() throws Exception {
        try {
            testNoArgsFactory();
        } finally {
        }
    }


    void __invoke_testSimpleDoubleConstructor() throws Exception {
        try {
            testSimpleDoubleConstructor();
        } finally {
        }
    }


    void __invoke_testSimpleBooleanConstructor() throws Exception {
        try {
            testSimpleBooleanConstructor();
        } finally {
        }
    }


    void __invoke_testSimpleBigIntegerConstructor() throws Exception {
        try {
            testSimpleBigIntegerConstructor();
        } finally {
        }
    }


    void __invoke_testSimpleBigDecimalConstructor() throws Exception {
        try {
            testSimpleBigDecimalConstructor();
        } finally {
        }
    }


    void __invoke_testSimpleFactory() throws Exception {
        try {
            testSimpleFactory();
        } finally {
        }
    }


    void __invoke_testStringFactory() throws Exception {
        try {
            testStringFactory();
        } finally {
        }
    }


    void __invoke_testStringFactoryAlt() throws Exception {
        try {
            testStringFactoryAlt();
        } finally {
        }
    }

}
