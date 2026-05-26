package tools.jackson.databind.jsontype.vld;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.exc.InvalidDefinitionException;
import tools.jackson.databind.exc.InvalidTypeIdException;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;
import tools.jackson.databind.testutil.DatabindTestUtil;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the main user-configurable {@code PolymorphicTypeValidator},
 * {@link BasicPolymorphicTypeValidator}.
 */
public class BasicPTVTest extends DatabindTestUtil
{
    // // // Value types

    static abstract class BaseValue {
        public int x = 3;
    }

    static class ValueA extends BaseValue {
        protected ValueA() { }
        public ValueA(int x) {
            super();
            this.x = x;
        }
    }

    static class ValueB extends BaseValue {
        protected ValueB() { }
        public ValueB(int x) {
            super();
            this.x = x;
        }
    }

    // // // Value types

    // make this type `final` to avoid polymorphic handling
    static final class BaseValueWrapper {
        public BaseValue value;

        protected BaseValueWrapper() { }

        public static BaseValueWrapper withA(int x) {
            BaseValueWrapper w = new BaseValueWrapper();
            w.value = new ValueA(x);
            return w;
        }

        public static BaseValueWrapper withB(int x) {
            BaseValueWrapper w = new BaseValueWrapper();
            w.value = new ValueB(x);
            return w;
        }
    }

    static final class ObjectWrapper {
        public Object value;

        protected ObjectWrapper() { }
        public ObjectWrapper(Object v) { value = v; }
    }

    static final class NumberWrapper {
        public Number value;

        protected NumberWrapper() { }
        public NumberWrapper(Number v) { value = v; }
    }

    // [databind#2539]
    static class Dangerous2539 {
        public int x;
    }

    /*
    /**********************************************************************
    /* Test methods: by base type, pass
    /**********************************************************************
     */

    // First: test simple Base-type-as-class allowing
    @Test
    public void testAllowByBaseClass() throws Exception {
        final PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                .allowIfBaseType(BaseValue.class)
                .build();
        ObjectMapper mapper = vpackMapperBuilder()
                .activateDefaultTyping(ptv, DefaultTyping.NON_FINAL)
                .build();

        // First, test accepted case
        final String json = VPackUtils.toJson(mapper.writeValueAsBytes(BaseValueWrapper.withA(42)));
        BaseValueWrapper w = mapper.readValue(VPackUtils.toVPack(json), BaseValueWrapper.class);
        assertEquals(42, w.value.x);

        // then non-accepted
        final String json2 = VPackUtils.toJson(mapper.writeValueAsBytes(new NumberWrapper(Byte.valueOf((byte) 4))));
        InvalidTypeIdException e = assertThrows(InvalidTypeIdException.class,
                () -> mapper.readValue(VPackUtils.toVPack(json2), NumberWrapper.class));
        verifyException(e, "Could not resolve type id 'java.lang.Byte'");
        verifyException(e, "as a subtype of");

        // and then yet again accepted one with different config
        ObjectMapper mapper2 = vpackMapperBuilder()
                .activateDefaultTyping(BasicPolymorphicTypeValidator.builder()
                        .allowIfBaseType(Number.class)
                        .build(), DefaultTyping.NON_FINAL)
                .build();
        NumberWrapper nw = mapper2.readValue(VPackUtils.toVPack(json2), NumberWrapper.class);
        assertNotNull(nw);
        assertEquals(Byte.valueOf((byte) 4), nw.value);
    }

    // Then subtype-prefix
    @Test
    public void testAllowByBaseClassPrefix() throws Exception {
        final PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                .allowIfBaseType("tools.jackson.")
                .build();
        ObjectMapper mapper = vpackMapperBuilder()
                .activateDefaultTyping(ptv, DefaultTyping.NON_FINAL)
                .build();

        // First, test accepted case
        final String json = VPackUtils.toJson(mapper.writeValueAsBytes(BaseValueWrapper.withA(42)));
        BaseValueWrapper w = mapper.readValue(VPackUtils.toVPack(json), BaseValueWrapper.class);
        assertEquals(42, w.value.x);

        // then non-accepted
        final String json2 = VPackUtils.toJson(mapper.writeValueAsBytes(new NumberWrapper(Byte.valueOf((byte) 4))));
        InvalidTypeIdException e = assertThrows(InvalidTypeIdException.class,
                () -> mapper.readValue(VPackUtils.toVPack(json2), NumberWrapper.class));
        verifyException(e, "Could not resolve type id 'java.lang.Byte'");
        verifyException(e, "as a subtype of");
    }

    // Then subtype-pattern
    @Test
    public void testAllowByBaseClassPattern() throws Exception {
        final PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                .allowIfBaseType(Pattern.compile("\\w+\\.jackson\\..+"))
                .build();
        ObjectMapper mapper = vpackMapperBuilder()
                .activateDefaultTyping(ptv, DefaultTyping.NON_FINAL)
                .build();

        // First, test accepted case
        final String json = VPackUtils.toJson(mapper.writeValueAsBytes(BaseValueWrapper.withA(42)));
        BaseValueWrapper w = mapper.readValue(VPackUtils.toVPack(json), BaseValueWrapper.class);
        assertEquals(42, w.value.x);

        // then non-accepted
        final String json2 = VPackUtils.toJson(mapper.writeValueAsBytes(new NumberWrapper(Byte.valueOf((byte) 4))));
        InvalidTypeIdException e = assertThrows(InvalidTypeIdException.class,
                () -> mapper.readValue(VPackUtils.toVPack(json2), NumberWrapper.class));
        verifyException(e, "Could not resolve type id 'java.lang.Byte'");
        verifyException(e, "as a subtype of");
    }

    // And finally, block by specific direct-match base type
    @Test
    public void testDenyByBaseClass() throws Exception {
        final PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                // indicate that all subtypes `BaseValue` would be fine
                .allowIfBaseType(BaseValue.class)
                // but that nominal base type MUST NOT be `Object.class`
                .denyForExactBaseType(Object.class)
                .build();
        ObjectMapper mapper = vpackMapperBuilder()
                .activateDefaultTyping(ptv, DefaultTyping.NON_FINAL)
                .build();
        final String json = VPackUtils.toJson(mapper.writeValueAsBytes(new ObjectWrapper(new ValueA(15))));
        // NOTE: different exception type since denial was for whole property, not just specific values
        InvalidDefinitionException e = assertThrows(InvalidDefinitionException.class,
                () -> mapper.readValue(VPackUtils.toVPack(json), ObjectWrapper.class));
        verifyException(e, "denied resolution of all subtypes of base type `java.lang.Object`");
    }

    /*
    /**********************************************************************
    /* Test methods: by sub type
    /**********************************************************************
     */

    @Test
    public void testAllowBySubClass() throws Exception {
        final PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType(ValueB.class)
                .build();
        ObjectMapper mapper = vpackMapperBuilder()
                .activateDefaultTyping(ptv, DefaultTyping.NON_FINAL)
                .build();

        // First, test accepted case
        final String json = VPackUtils.toJson(mapper.writeValueAsBytes(BaseValueWrapper.withB(42)));
        BaseValueWrapper w = mapper.readValue(VPackUtils.toVPack(json), BaseValueWrapper.class);
        assertEquals(42, w.value.x);

        // then non-accepted
        final String json2 = VPackUtils.toJson(mapper.writeValueAsBytes(BaseValueWrapper.withA(43)));
        InvalidTypeIdException e = assertThrows(InvalidTypeIdException.class,
                () -> mapper.readValue(VPackUtils.toVPack(json2), BaseValueWrapper.class));
        verifyException(e, "Could not resolve type id 'tools.jackson.");
        verifyException(e, "as a subtype of");
    }

    @Test
    public void testAllowBySubClassPrefix() throws Exception {
        final PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType(ValueB.class.getName())
                .build();
        ObjectMapper mapper = vpackMapperBuilder()
                .activateDefaultTyping(ptv, DefaultTyping.NON_FINAL)
                .build();

        // First, test accepted case
        final String json = VPackUtils.toJson(mapper.writeValueAsBytes(BaseValueWrapper.withB(42)));
        BaseValueWrapper w = mapper.readValue(VPackUtils.toVPack(json), BaseValueWrapper.class);
        assertEquals(42, w.value.x);

        // then non-accepted
        final String json2 = VPackUtils.toJson(mapper.writeValueAsBytes(BaseValueWrapper.withA(43)));
        InvalidTypeIdException e = assertThrows(InvalidTypeIdException.class,
                () -> mapper.readValue(VPackUtils.toVPack(json2), BaseValueWrapper.class));
        verifyException(e, "Could not resolve type id 'tools.jackson.");
        verifyException(e, "as a subtype of");
    }

    @Test
    public void testAllowBySubClassPattern() throws Exception {
        final PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType(Pattern.compile(Pattern.quote(ValueB.class.getName())))
                .build();
        ObjectMapper mapper = vpackMapperBuilder()
                .activateDefaultTyping(ptv, DefaultTyping.NON_FINAL)
                .build();

        // First, test accepted case
        final String json = VPackUtils.toJson(mapper.writeValueAsBytes(BaseValueWrapper.withB(42)));
        BaseValueWrapper w = mapper.readValue(VPackUtils.toVPack(json), BaseValueWrapper.class);
        assertEquals(42, w.value.x);

        // then non-accepted
        final String json2 = VPackUtils.toJson(mapper.writeValueAsBytes(BaseValueWrapper.withA(43)));
        InvalidTypeIdException e = assertThrows(InvalidTypeIdException.class,
                () -> mapper.readValue(VPackUtils.toVPack(json2), BaseValueWrapper.class));
        verifyException(e, "Could not resolve type id 'tools.jackson.");
        verifyException(e, "as a subtype of");
    }

    // [databind#2539]
    @Test
    public void testWithJDKBasicsOk() throws Exception
    {
        final ObjectMapper defaultingMapper = vpackMapperBuilder()
                .activateDefaultTyping(BasicPolymorphicTypeValidator.builder()
                        .allowSubTypesWithExplicitDeserializer()
                        .build(),
                        DefaultTyping.NON_FINAL_AND_ENUMS)
                .build();

        Object[] input = new Object[] {
                "test", 42, new java.net.URL("http://localhost"),
                java.util.UUID.nameUUIDFromBytes("abc".getBytes()),
                new Object[] { }
        };

        String json = VPackUtils.toJson(defaultingMapper.writeValueAsBytes(input));
        Object result = defaultingMapper.readValue(VPackUtils.toVPack(json), Object.class);
        assertEquals(Object[].class, result.getClass());

        // but then non-ok case:
        final String json2 = VPackUtils.toJson(defaultingMapper.writeValueAsBytes(new Object[] {
                new Dangerous2539()
        }));
        InvalidTypeIdException e = assertThrows(InvalidTypeIdException.class,
                () -> defaultingMapper.readValue(VPackUtils.toVPack(json2), Object.class));
        verifyException(e, "Could not resolve type id 'tools.jackson.");
        verifyException(e, "as a subtype of");

        // and another one within array
        final String json3 = VPackUtils.toJson(defaultingMapper.writeValueAsBytes(new Object[] {
                new Dangerous2539[] { new Dangerous2539() }
        }));
        InvalidTypeIdException e2 = assertThrows(InvalidTypeIdException.class,
                () -> defaultingMapper.readValue(VPackUtils.toVPack(json3), Object.class));
        verifyException(e2, "Could not resolve type id '[Ltools.jackson.");
        verifyException(e2, "as a subtype of");
    }
}
