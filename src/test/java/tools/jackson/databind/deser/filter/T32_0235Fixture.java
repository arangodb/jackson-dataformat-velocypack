package tools.jackson.databind.deser.filter;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.DeserializationProblemHandler;
import tools.jackson.databind.deser.ValueInstantiator;
import tools.jackson.databind.exc.InvalidDefinitionException;
import tools.jackson.databind.exc.InvalidFormatException;
import tools.jackson.databind.exc.InvalidTypeIdException;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.jsontype.TypeIdResolver;
import tools.jackson.databind.JsonNode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0235Fixture {
private static final byte[] UNKNOWN_TYPE = VPackWireFixtureTest.hex(
            "14 18 45 76 61 6c 75 65 14 0f 44 74 79 70 65 43 66 6f 6f "
          + "41 61 34 02 01");
private static final byte[] MISSING_TYPE = VPackWireFixtureTest.hex(
            "14 0f 45 76 61 6c 75 65 14 06 41 61 34 01 01");
private static final byte[] MISSING_INSTANTIATOR = VPackWireFixtureTest.hex(
            "14 06 41 78 1a 01");
private static final byte[] INVALID_PRIMITIVE = VPackWireFixtureTest.hex(
            "14 12 41 61 4c 6e 6f 74 2d 61 2d 6e 75 6d 62 65 72 01");
private static final byte[] EMPTY_LONG = VPackWireFixtureTest.hex(
            "14 0b 46 6d 79 4c 6f 6e 67 40 01");
private static final byte[] INVALID_LONG = VPackWireFixtureTest.hex(
            "14 14 46 6d 79 4c 6f 6e 67 49 6e 6f 74 53 6f 4c 6f 6e 67 01");
private static final byte[] WEIRD_KEY = VPackWireFixtureTest.hex(
            "14 14 45 73 74 75 66 66 14 0b 43 66 6f 6f 43 61 62 63 01 01");
private static final byte[] NULL_AGE = VPackWireFixtureTest.hex(
            "14 19 42 69 64 44 31 32 61 62 44 6e 61 6d 65 43 42 6f 62 "
          + "43 61 67 65 18 03");
private static final byte[] NUMBER_LONG_AGE = VPackWireFixtureTest.hex(
            "14 2a 42 69 64 44 31 32 61 62 44 6e 61 6d 65 43 42 6f 62 "
          + "43 61 67 65 14 12 4b 24 6e 75 6d 62 65 72 4c 6f 6e 67 "
          + "42 31 30 01 03");
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");

    // Provenance: DeserializationProblemHandlerTest#testInvalidTypeIdFail.
    void testInvalidTypeIdFailVpack() {
        InvalidTypeIdException exception = assertThrows(InvalidTypeIdException.class,
                () -> VPackMapper.builder().build().readValue(UNKNOWN_TYPE, BaseWrapper.class));

        assertEquals(Base.class, exception.getBaseType().getRawClass());
        assertEquals("foo", exception.getTypeId());
        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().contains("Could not resolve type id 'foo'"));
    }

    // Provenance: DeserializationProblemHandlerTest#testLongCoercion3450.
    void testLongCoercion3450Vpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addHandler(new LenientDeserializationProblemHandler()).build();

        TestPojo3450Long empty = mapper.readValue(EMPTY_LONG, TestPojo3450Long.class);
        assertNull(empty.myLong);
        TestPojo3450Long invalid = mapper.readValue(INVALID_LONG, TestPojo3450Long.class);
        assertNull(invalid.myLong);
    }

    // Provenance: DeserializationProblemHandlerTest#testMissingClassAsId.
    void testMissingClassAsIdVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addHandler(new MissingTypeIdHandler(Base2Impl.class)).build();

        Base2Wrapper value = mapper.readValue(MISSING_TYPE, Base2Wrapper.class);
        assertNotNull(value);
        assertEquals(Base2Impl.class, value.value.getClass());
        assertEquals(4, ((Base2Impl) value.value).a);
    }

    // Provenance: DeserializationProblemHandlerTest#testMissingInstantiatorHandling.
    void testMissingInstantiatorHandlingVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
                .addHandler(new MissingInstantiationHandler(new NoDefaultCtor(13))).build();

        NoDefaultCtor value = mapper.readValue(MISSING_INSTANTIATOR, NoDefaultCtor.class);
        assertNotNull(value);
        assertEquals(13, value.value);

        ObjectMapper badMapper = VPackMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
                .addHandler(new MissingInstantiationHandler("foo")).build();
        InvalidDefinitionException exception = assertThrows(InvalidDefinitionException.class,
                () -> badMapper.readValue(MISSING_INSTANTIATOR, NoDefaultCtor.class));
        assertTrue(exception.getMessage().contains("returned value of type"));
    }

    // Provenance: DeserializationProblemHandlerTest#testMissingTypeId.
    void testMissingTypeIdVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addHandler(new MissingTypeIdHandler(BaseImpl.class)).build();

        BaseWrapper value = mapper.readValue(MISSING_TYPE, BaseWrapper.class);
        assertNotNull(value);
        assertEquals(BaseImpl.class, value.value.getClass());
        assertEquals(4, ((BaseImpl) value.value).a);

        ObjectMapper badMapper = VPackMapper.builder()
                .addHandler(new MissingTypeIdHandler(String.class)).build();
        InvalidTypeIdException exception = assertThrows(InvalidTypeIdException.class,
                () -> badMapper.readValue(MISSING_TYPE, BaseWrapper.class));
        assertTrue(exception.getMessage().contains("into non-subtype"));
    }

    // Provenance: DeserializationProblemHandlerTest#testNullForPrimitivesBadImpl5469.
    void testNullForPrimitivesBadImpl5469Vpack() {
        MoreProblemHandler5469 handler = new MoreProblemHandler5469();
        ObjectMapper mapper = VPackMapper.builder()
                .enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
                .addHandler(handler).build();

        InvalidFormatException exception = assertThrows(InvalidFormatException.class,
                () -> mapper.readValue(NULL_AGE, Person5469.class));
        assertTrue(exception.getMessage().contains("handleNullForPrimitives"));
        assertEquals(1, handler.hitCount);
    }

    // Provenance: DeserializationProblemHandlerTest#testNullForPrimitivesHappyCase5469.
    void testNullForPrimitivesHappyCase5469Vpack() throws Exception {
        ProblemHandler5469 handler = new ProblemHandler5469();
        ObjectMapper mapper = VPackMapper.builder()
                .enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
                .addHandler(handler).build();

        Person5469 person = mapper.readValue(NULL_AGE, Person5469.class);
        assertNotNull(person);
        assertEquals("12ab", person.id);
        assertEquals("Bob", person.name);
        assertEquals(5469L, person.age);
        assertEquals(1, handler.hitCount);
    }

    // Provenance: DeserializationProblemHandlerTest#testPrimitivePropertyWithHandler1767.
    void testPrimitivePropertyWithHandler1767Vpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder().addHandler(new IntHandler()).build();

        TestBean1767 result = mapper.readValue(INVALID_PRIMITIVE, TestBean1767.class);
        assertNotNull(result);
        assertEquals(1, result.a);
    }

    // Provenance: DeserializationProblemHandlerTest#testUnexpectedToken2973.
    void testUnexpectedToken2973Vpack() throws Exception {
        ObjectMapper defaultMapper = VPackMapper.builder().build();
        MismatchedInputException baseline = assertThrows(MismatchedInputException.class,
                () -> defaultMapper.readValue(EMPTY_OBJECT, String.class));
        assertTrue(baseline.getMessage().contains(
                "Cannot deserialize value of type `java.lang.String`"));

        ObjectMapper mapper = VPackMapper.builder()
                .addHandler(new WeirdTokenHandler2973()).build();

        String value = mapper.readValue(EMPTY_OBJECT, String.class);
        assertEquals("START_OBJECT", value);
    }

    // Provenance: DeserializationProblemHandlerTest#testUnexpectedToken4656.
    void testUnexpectedToken4656Vpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addHandler(new ProblemHandler4656()).build();

        Person4656 person = mapper.readValue(NUMBER_LONG_AGE, Person4656.class);
        assertNotNull(person);
        assertEquals("12ab", person.id);
        assertEquals("Bob", person.name);
        assertEquals(10L, person.age);
    }

    // Provenance: DeserializationProblemHandlerTest#testUnexpectedTokenHandling.
    void testUnexpectedTokenHandlingVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addHandler(new WeirdTokenHandler(Integer.valueOf(13))).build();
        assertEquals(Integer.valueOf(13), mapper.readValue(
                VPackWireFixtureTest.hex("1a"), Integer.class));

        ObjectMapper badMapper = VPackMapper.builder()
                .addHandler(new WeirdTokenHandler("foo")).build();
        InvalidDefinitionException exception = assertThrows(InvalidDefinitionException.class,
                () -> badMapper.readValue(VPackWireFixtureTest.hex("1a"), Integer.class));
        assertTrue(exception.getMessage().contains("returned value of type"));
    }

    // Provenance: DeserializationProblemHandlerTest#testWeirdKeyHandling.
    void testWeirdKeyHandlingVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addHandler(new WeirdKeyHandler()).build();

        IntKeyMapWrapper wrapper = mapper.readValue(WEIRD_KEY, IntKeyMapWrapper.class);
        Map<Integer, String> map = wrapper.stuff;
        assertEquals(1, map.size());
        assertEquals("abc", map.values().iterator().next());
        assertEquals(Integer.valueOf(7), map.keySet().iterator().next());
    }
static class WeirdKeyHandler extends DeserializationProblemHandler {
        @Override
        public Object handleWeirdKey(DeserializationContext ctxt, Class<?> rawKeyType,
                String keyValue, String failureMsg) {
            return 7;
        }
    }
static class MissingTypeIdHandler extends DeserializationProblemHandler {
        private final Class<?> raw;

        MissingTypeIdHandler(Class<?> raw) {
            this.raw = raw;
        }

        @Override
        public JavaType handleMissingTypeId(DeserializationContext ctxt, JavaType baseType,
                TypeIdResolver idResolver, String failureMsg) {
            return ctxt.constructType(raw);
        }
    }
static class MissingInstantiationHandler extends DeserializationProblemHandler {
        private final Object value;

        MissingInstantiationHandler(Object value) {
            this.value = value;
        }

        @Override
        public Object handleMissingInstantiator(DeserializationContext ctxt, Class<?> instClass,
                ValueInstantiator inst, JsonParser parser, String message) {
            parser.skipChildren();
            return value;
        }
    }
static class WeirdTokenHandler extends DeserializationProblemHandler {
        private final Object value;

        WeirdTokenHandler(Object value) {
            this.value = value;
        }

        @Override
        public Object handleUnexpectedToken(DeserializationContext ctxt, JavaType targetType,
                JsonToken token, JsonParser parser, String failureMsg) {
            parser.skipChildren();
            return value;
        }
    }
static class WeirdTokenHandler2973 extends DeserializationProblemHandler {
        @Override
        public Object handleUnexpectedToken(DeserializationContext ctxt, JavaType targetType,
                JsonToken token, JsonParser parser, String failureMsg) {
            String result = parser.currentToken().toString();
            parser.skipChildren();
            return result;
        }
    }
static class IntHandler extends DeserializationProblemHandler {
        @Override
        public Object handleWeirdStringValue(DeserializationContext ctxt, Class<?> targetType,
                String valueToConvert, String failureMsg) {
            return targetType == Integer.TYPE ? 1 : NOT_HANDLED;
        }
    }
static class LenientDeserializationProblemHandler extends DeserializationProblemHandler {
        @Override
        public Object handleWeirdStringValue(DeserializationContext ctxt, Class<?> targetType,
                String valueToConvert, String failureMsg) {
            return null;
        }
    }
static class ProblemHandler4656 extends DeserializationProblemHandler {
        @Override
        public Object handleUnexpectedToken(DeserializationContext ctxt, JavaType targetType,
                JsonToken token, JsonParser parser, String failureMsg) {
            if (targetType.getRawClass() == Long.class && token == JsonToken.START_OBJECT) {
                JsonNode tree = parser.readValueAsTree();
                JsonNode numberLong = tree.get("$numberLong");
                if (numberLong != null) {
                    try {
                        return Long.parseLong(numberLong.asString());
                    } catch (NumberFormatException e) {
                        // Fall through to the normal problem handling path.
                    }
                }
            }
            return NOT_HANDLED;
        }
    }
static class ProblemHandler5469 extends DeserializationProblemHandler {
        int hitCount;

        @Override
        public Object handleNullForPrimitives(DeserializationContext ctxt, Class<?> targetType,
                JsonParser parser, ValueDeserializer<?> deser, String failureMsg)
                throws JacksonException {
            ++hitCount;
            return 5469L;
        }
    }
static class MoreProblemHandler5469 extends DeserializationProblemHandler {
        int hitCount;

        @Override
        public Object handleNullForPrimitives(DeserializationContext ctxt, Class<?> targetType,
                JsonParser parser, ValueDeserializer<?> deser, String failureMsg)
                throws JacksonException {
            ++hitCount;
            return "THIS IS AN ERROR";
        }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
    static class Base { }
static class BaseImpl extends Base {
        public int a;
    }
static class BaseWrapper {
        public Base value;
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "clazz")
    static class Base2 { }
static class Base2Impl extends Base2 {
        public int a;
    }
static class Base2Wrapper {
        public Base2 value;
    }
static class IntKeyMapWrapper {
        public Map<Integer, String> stuff;
    }
static class NoDefaultCtor {
        public int value;

        NoDefaultCtor(int value) {
            this.value = value;
        }
    }
static class TestBean1767 {
        int a;

        public int getA() {
            return a;
        }

        public void setA(int a) {
            this.a = a;
        }
    }
static class TestPojo3450Long {
        public Long myLong;
    }
static class Person4656 {
        public String id;
        public String name;
        public Long age;
    }
static class Person5469 {
        public String id;
        public String name;
        public long age;
    }

    void __invoke_testInvalidTypeIdFailVpack() throws Exception {
        try {
            testInvalidTypeIdFailVpack();
        } finally {
        }
    }


    void __invoke_testLongCoercion3450Vpack() throws Exception {
        try {
            testLongCoercion3450Vpack();
        } finally {
        }
    }


    void __invoke_testMissingClassAsIdVpack() throws Exception {
        try {
            testMissingClassAsIdVpack();
        } finally {
        }
    }


    void __invoke_testMissingInstantiatorHandlingVpack() throws Exception {
        try {
            testMissingInstantiatorHandlingVpack();
        } finally {
        }
    }


    void __invoke_testMissingTypeIdVpack() throws Exception {
        try {
            testMissingTypeIdVpack();
        } finally {
        }
    }


    void __invoke_testNullForPrimitivesBadImpl5469Vpack() throws Exception {
        try {
            testNullForPrimitivesBadImpl5469Vpack();
        } finally {
        }
    }


    void __invoke_testNullForPrimitivesHappyCase5469Vpack() throws Exception {
        try {
            testNullForPrimitivesHappyCase5469Vpack();
        } finally {
        }
    }


    void __invoke_testPrimitivePropertyWithHandler1767Vpack() throws Exception {
        try {
            testPrimitivePropertyWithHandler1767Vpack();
        } finally {
        }
    }


    void __invoke_testUnexpectedToken2973Vpack() throws Exception {
        try {
            testUnexpectedToken2973Vpack();
        } finally {
        }
    }


    void __invoke_testUnexpectedToken4656Vpack() throws Exception {
        try {
            testUnexpectedToken4656Vpack();
        } finally {
        }
    }


    void __invoke_testUnexpectedTokenHandlingVpack() throws Exception {
        try {
            testUnexpectedTokenHandlingVpack();
        } finally {
        }
    }


    void __invoke_testWeirdKeyHandlingVpack() throws Exception {
        try {
            testWeirdKeyHandlingVpack();
        } finally {
        }
    }

}
