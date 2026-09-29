package tools.jackson.databind.deser.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.DeserializationProblemHandler;
import tools.jackson.databind.exc.InvalidDefinitionException;
import tools.jackson.databind.exc.InvalidTypeIdException;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.exc.ValueInstantiationException;
import tools.jackson.databind.jsontype.TypeIdResolver;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0234Fixture {
private static final byte[] STRING_VALUE = VPackWireFixtureTest.hex(
            "4a 73 6f 6d 65 53 74 72 69 6e 67");
private static final byte[] ARRAY_PROPERTY = VPackWireFixtureTest.hex(
            "14 0b 44 70 72 6f 70 13 03 00 01");
private static final byte[] INVALID_CONTEXT = VPackWireFixtureTest.hex(
            "14 4c 45 61 63 74 6f 72 14 0e 49 69 6e 76 61 6c 69 64 5f 31 40 01 "
          + "46 6f 62 6a 65 63 74 14 0e 49 69 6e 76 61 6c 69 64 5f 32 40 01 "
          + "46 74 61 72 67 65 74 14 19 49 69 6e 76 61 6c 69 64 5f 33 40 "
          + "49 69 6e 76 61 6c 69 64 5f 34 40 02 03");
private static final byte[] UNKNOWN_TYPE = VPackWireFixtureTest.hex(
            "14 18 45 76 61 6c 75 65 14 0f 44 74 79 70 65 43 66 6f 6f "
          + "41 61 34 02 01");
private static final byte[] UNKNOWN_CLASS = VPackWireFixtureTest.hex(
            "14 1e 45 76 61 6c 75 65 14 15 45 63 6c 61 7a 7a 48 63 6f 6d "
          + "2e 66 69 7a 7a 41 61 34 02 01");
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] EMPTY_INTEGER = VPackWireFixtureTest.hex(
            "14 0e 49 6d 79 49 6e 74 65 67 65 72 40 01");
private static final byte[] INVALID_INTEGER = VPackWireFixtureTest.hex(
            "14 14 49 6d 79 49 6e 74 65 67 65 72 46 6e 6f 74 49 6e 74 01");

    // Provenance: DeserializationProblemHandlerTest#testHandleUnexpectedTokenForStringProp3349.
    void testHandleUnexpectedTokenForStringProp3349Vpack() {
        TrackingProblemHandler handler = new TrackingProblemHandler();
        ObjectMapper mapper = VPackMapper.builder().addHandler(handler).build();

        assertThrows(MismatchedInputException.class,
                () -> mapper.readValue(ARRAY_PROPERTY, StringHolder3349.class));
        verifyUnexpectedTokenCalled(handler);
    }

    // Provenance: DeserializationProblemHandlerTest#testHandleUnexpectedTokenForStringCollection3349.
    void testHandleUnexpectedTokenForStringCollection3349Vpack() throws Exception {
        TrackingProblemHandler handler = new TrackingProblemHandler();
        ObjectMapper mapper = VPackMapper.builder().addHandler(handler).build();

        Object value = mapper.readValue(STRING_VALUE,
                mapper.getTypeFactory().constructCollectionType(ArrayList.class, String.class));

        assertEquals(List.of(), value);
        verifyUnexpectedTokenCalled(handler);
    }

    // Provenance: DeserializationProblemHandlerTest#testHandleUnexpectedTokenForObjectCollection3349.
    void testHandleUnexpectedTokenForObjectCollection3349Vpack() throws Exception {
        TrackingProblemHandler handler = new TrackingProblemHandler();
        ObjectMapper mapper = VPackMapper.builder().addHandler(handler).build();

        Object value = mapper.readValue(STRING_VALUE,
                mapper.getTypeFactory().constructCollectionType(ArrayList.class, Integer.class));

        assertEquals(List.of(), value);
        verifyUnexpectedTokenCalled(handler);
    }

    // Provenance: DeserializationProblemHandlerTest#testHandleUnexpectedTokenForMap3349.
    void testHandleUnexpectedTokenForMap3349Vpack() throws Exception {
        TrackingProblemHandler handler = new TrackingProblemHandler();
        ObjectMapper mapper = VPackMapper.builder().addHandler(handler).build();

        Object value = mapper.readValue(STRING_VALUE,
                mapper.getTypeFactory().constructMapType(HashMap.class, String.class, String.class));

        assertEquals(Map.of(), value);
        verifyUnexpectedTokenCalled(handler);
    }

    // Provenance: DeserializationProblemHandlerTest#testHandleUnexpectedTokenForObjectArray3349.
    void testHandleUnexpectedTokenForObjectArray3349Vpack() throws Exception {
        TrackingProblemHandler handler = new TrackingProblemHandler();
        ObjectMapper mapper = VPackMapper.builder().addHandler(handler).build();

        Object value = mapper.readValue(STRING_VALUE, Object[].class);

        assertArrayEquals(new Object[0], (Object[]) value);
        verifyUnexpectedTokenCalled(handler);
    }

    // Provenance: DeserializationProblemHandlerTest#testHandleUnexpectedTokenForStringArray3349.
    void testHandleUnexpectedTokenForStringArray3349Vpack() throws Exception {
        TrackingProblemHandler handler = new TrackingProblemHandler();
        ObjectMapper mapper = VPackMapper.builder().addHandler(handler).build();

        Object value = mapper.readValue(STRING_VALUE, String[].class);

        assertArrayEquals(new String[0], (String[]) value);
        verifyUnexpectedTokenCalled(handler);
    }

    // Provenance: DeserializationProblemHandlerTest#testHandleUnexpectedTokenForLongArray3349.
    void testHandleUnexpectedTokenForLongArray3349Vpack() throws Exception {
        TrackingProblemHandler handler = new TrackingProblemHandler();
        ObjectMapper mapper = VPackMapper.builder().addHandler(handler).build();

        Object value = mapper.readValue(STRING_VALUE, long[].class);

        assertArrayEquals(new long[0], (long[]) value);
        verifyUnexpectedTokenCalled(handler);
    }

    // Provenance: DeserializationProblemHandlerTest#testInstantiationExceptionHandling.
    void testInstantiationExceptionHandlingVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addHandler(new InstantiationProblemHandler(BustedCtor.INSTANCE)).build();
        BustedCtor value = mapper.readValue(EMPTY_OBJECT, BustedCtor.class);
        assertNotNull(value);

        ObjectMapper badMapper = VPackMapper.builder()
                .addHandler(new InstantiationProblemHandler("bad")).build();
        InvalidDefinitionException exception = assertThrows(InvalidDefinitionException.class,
                () -> badMapper.readValue(EMPTY_OBJECT, BustedCtor.class));
        assertTrue(exception.getMessage().contains("returned value of type"));
    }

    // Provenance: DeserializationProblemHandlerTest#testIntegerCoercion3450.
    void testIntegerCoercion3450Vpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addHandler(new LenientDeserializationProblemHandler()).build();

        TestPojo3450Int empty = mapper.readValue(EMPTY_INTEGER, TestPojo3450Int.class);
        assertNull(empty.myInteger);
        TestPojo3450Int invalid = mapper.readValue(INVALID_INTEGER, TestPojo3450Int.class);
        assertNull(invalid.myInteger);
    }

    // Provenance: DeserializationProblemHandlerTest#testIncorrectContext1440.
    void testIncorrectContext1440Vpack() throws Exception {
        DeserializationProblemLogger logger = new DeserializationProblemLogger();
        ObjectMapper mapper = VPackMapper.builder()
                .disable(tools.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addHandler(logger).build();

        mapper.readValue(INVALID_CONTEXT, Activity1440.class);

        assertEquals(List.of(
                "actor.invalid_1#invalid_1",
                "object.invalid_2#invalid_2",
                "target.invalid_3#invalid_3",
                "target.invalid_4#invalid_4"), logger.problems());
    }

    // Provenance: DeserializationProblemHandlerTest#testInvalidTypeId.
    void testInvalidTypeIdVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addHandler(new UnknownTypeIdHandler(BaseImpl.class)).build();
        BaseWrapper value = mapper.readValue(UNKNOWN_TYPE, BaseWrapper.class);
        assertNotNull(value);
        assertEquals(BaseImpl.class, value.value.getClass());

        ObjectMapper badMapper = VPackMapper.builder()
                .addHandler(new UnknownTypeIdHandler(String.class)).build();
        InvalidTypeIdException exception = assertThrows(InvalidTypeIdException.class,
                () -> badMapper.readValue(UNKNOWN_TYPE, BaseWrapper.class));
        assertTrue(exception.getMessage().contains("into non-subtype"));
    }

    // Provenance: DeserializationProblemHandlerTest#testInvalidClassAsId.
    void testInvalidClassAsIdVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addHandler(new UnknownTypeIdHandler(Base2Impl.class)).build();
        Base2Wrapper value = mapper.readValue(UNKNOWN_CLASS, Base2Wrapper.class);
        assertNotNull(value);
        assertEquals(Base2Impl.class, value.value.getClass());
    }
private static void verifyUnexpectedTokenCalled(TrackingProblemHandler handler) {
        assertTrue(handler.handleUnexpectedTokenCalled,
                "handleUnexpectedToken should have been called");
        assertFalse(handler.handleInstantiationProblemCalled,
                "handleInstantiationProblem should NOT have been called");
        assertFalse(handler.handleMissingInstantiatorCalled,
                "handleMissingInstantiator should NOT have been called");
    }
static class TrackingProblemHandler extends DeserializationProblemHandler {
        boolean handleUnexpectedTokenCalled;
        boolean handleInstantiationProblemCalled;
        boolean handleMissingInstantiatorCalled;

        @Override
        public Object handleUnexpectedToken(DeserializationContext ctxt, JavaType targetType,
                JsonToken token, JsonParser parser, String failureMsg) {
            handleUnexpectedTokenCalled = true;
            if (targetType.isMapLikeType()) {
                return new HashMap<>();
            }
            if (targetType.isCollectionLikeType()) {
                return new ArrayList<>();
            }
            if (targetType.isArrayType()) {
                return java.lang.reflect.Array.newInstance(
                        targetType.getContentType().getRawClass(), 0);
            }
            return NOT_HANDLED;
        }

        @Override
        public Object handleInstantiationProblem(DeserializationContext ctxt, Class<?> instClass,
                Object argument, Throwable throwable) {
            handleInstantiationProblemCalled = true;
            return NOT_HANDLED;
        }

        @Override
        public Object handleMissingInstantiator(DeserializationContext ctxt, Class<?> instClass,
                tools.jackson.databind.deser.ValueInstantiator instantiator,
                JsonParser parser, String message) {
            handleMissingInstantiatorCalled = true;
            return NOT_HANDLED;
        }
    }
static class InstantiationProblemHandler extends DeserializationProblemHandler {
        private final Object value;

        InstantiationProblemHandler(Object value) {
            this.value = value;
        }

        @Override
        public Object handleInstantiationProblem(DeserializationContext ctxt, Class<?> instClass,
                Object argument, Throwable throwable) {
            assertInstanceOf(ValueInstantiationException.class, throwable);
            return value;
        }
    }
static class LenientDeserializationProblemHandler extends DeserializationProblemHandler {
        @Override
        public Object handleWeirdStringValue(DeserializationContext ctxt, Class<?> targetType,
                String valueToConvert, String failureMsg) {
            return null;
        }
    }
static class UnknownTypeIdHandler extends DeserializationProblemHandler {
        private final Class<?> raw;

        UnknownTypeIdHandler(Class<?> raw) {
            this.raw = raw;
        }

        @Override
        public JavaType handleUnknownTypeId(DeserializationContext ctxt, JavaType baseType,
                String subTypeId, TypeIdResolver idResolver, String failureMsg) {
            return ctxt.constructType(raw);
        }
    }
static class StringHolder3349 {
        private final String prop;

        @JsonCreator
        StringHolder3349(@JsonProperty("prop") String prop) {
            this.prop = prop;
        }

        @JsonProperty("prop")
        public String getProp() {
            return prop;
        }
    }
static class BustedCtor {
        static final BustedCtor INSTANCE = new BustedCtor(true);

        BustedCtor() {
            throw new RuntimeException("fail");
        }

        private BustedCtor(boolean ignored) { }
    }
static class TestPojo3450Int {
        public Integer myInteger;
    }
static class DeserializationProblemLogger extends DeserializationProblemHandler {
        private final List<String> problems = new ArrayList<>();

        List<String> problems() {
            return problems;
        }

        @Override
        public boolean handleUnknownProperty(DeserializationContext ctxt, JsonParser parser,
                ValueDeserializer<?> deserializer, Object beanOrClass, String propertyName) {
            List<String> path = new ArrayList<>();
            addParent(parser.streamReadContext(), path);
            java.util.Collections.reverse(path);
            problems.add(String.join(".", path) + "#" + propertyName);
            parser.skipChildren();
            return true;
        }

        private void addParent(tools.jackson.core.TokenStreamContext context, List<String> path) {
            if (context != null && context.currentName() != null) {
                path.add(context.currentName());
                addParent(context.getParent(), path);
            }
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
static class Activity1440 {
        public ActivityEntity1440 actor;
        public String verb;
        public ActivityEntity1440 object;
        public ActivityEntity1440 target;

        @JsonCreator
        Activity1440(@JsonProperty("actor") ActivityEntity1440 actor,
                @JsonProperty("object") ActivityEntity1440 object,
                @JsonProperty("target") ActivityEntity1440 target,
                @JsonProperty("verb") String verb) {
            this.actor = actor;
            this.object = object;
            this.target = target;
            this.verb = verb;
        }
    }
static class ActivityEntity1440 {
        public String id;
        public String type;
        public String status;
        public String context;

        @JsonCreator
        ActivityEntity1440(@JsonProperty("id") String id,
                @JsonProperty("type") String type,
                @JsonProperty("status") String status,
                @JsonProperty("context") String context) {
            this.id = id;
            this.type = type;
            this.status = status;
            this.context = context;
        }
    }

    void __invoke_testHandleUnexpectedTokenForStringProp3349Vpack() throws Exception {
        try {
            testHandleUnexpectedTokenForStringProp3349Vpack();
        } finally {
        }
    }


    void __invoke_testHandleUnexpectedTokenForStringCollection3349Vpack() throws Exception {
        try {
            testHandleUnexpectedTokenForStringCollection3349Vpack();
        } finally {
        }
    }


    void __invoke_testHandleUnexpectedTokenForObjectCollection3349Vpack() throws Exception {
        try {
            testHandleUnexpectedTokenForObjectCollection3349Vpack();
        } finally {
        }
    }


    void __invoke_testHandleUnexpectedTokenForMap3349Vpack() throws Exception {
        try {
            testHandleUnexpectedTokenForMap3349Vpack();
        } finally {
        }
    }


    void __invoke_testHandleUnexpectedTokenForObjectArray3349Vpack() throws Exception {
        try {
            testHandleUnexpectedTokenForObjectArray3349Vpack();
        } finally {
        }
    }


    void __invoke_testHandleUnexpectedTokenForStringArray3349Vpack() throws Exception {
        try {
            testHandleUnexpectedTokenForStringArray3349Vpack();
        } finally {
        }
    }


    void __invoke_testHandleUnexpectedTokenForLongArray3349Vpack() throws Exception {
        try {
            testHandleUnexpectedTokenForLongArray3349Vpack();
        } finally {
        }
    }


    void __invoke_testInstantiationExceptionHandlingVpack() throws Exception {
        try {
            testInstantiationExceptionHandlingVpack();
        } finally {
        }
    }


    void __invoke_testIntegerCoercion3450Vpack() throws Exception {
        try {
            testIntegerCoercion3450Vpack();
        } finally {
        }
    }


    void __invoke_testIncorrectContext1440Vpack() throws Exception {
        try {
            testIncorrectContext1440Vpack();
        } finally {
        }
    }


    void __invoke_testInvalidTypeIdVpack() throws Exception {
        try {
            testInvalidTypeIdVpack();
        } finally {
        }
    }


    void __invoke_testInvalidClassAsIdVpack() throws Exception {
        try {
            testInvalidClassAsIdVpack();
        } finally {
        }
    }

}
