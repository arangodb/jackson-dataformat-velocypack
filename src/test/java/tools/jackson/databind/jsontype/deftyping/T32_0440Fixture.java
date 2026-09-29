package tools.jackson.databind.jsontype.deftyping;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.LinkedList;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.InvalidDefinitionException;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;
import tools.jackson.databind.jsontype.impl.DefaultTypeResolverBuilder;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator.Validity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0440Fixture {
private static final byte[] BEAN_ARRAY = VPackWireFixtureTest.hex(
            "13 0f 14 0c 44 6e 61 6d 65 43 61 62 63 01 01");
private static final byte[] ABSTRACT_BEAN_ARRAY = VPackWireFixtureTest.hex(
            "13 0f 14 0c 44 6e 61 6d 65 43 78 79 7a 01 01");
private static final byte[] CONCRETE_BASKETBALL = VPackWireFixtureTest.hex(
            "14 0a 44 73 69 7a 65 28 2a 01");
private static final byte[] ENUM_ARRAY = VPackWireFixtureTest.hex(
            "13 07 43 59 45 53 01");
private static final byte[] ABSTRACT_MAPPING = VPackWireFixtureTest.hex(
            "14 20 48 6d 61 70 46 69 65 6c 64 14 07 41 61 41 61 01 "
          + "4b 6f 62 6a 65 63 74 46 69 65 6c 64 0a 02");

    // Provenance: TestDefaultForObject#testBeanAsObject().
    void testBeanAsObjectVpack() throws Exception {
        ObjectMapper plain = VPackMapper.builder().build();
        Object[] literal = plain.readValue(BEAN_ARRAY, Object[].class);
        assertEquals(1, literal.length);
        assertEquals("abc", ((Map<?, ?>) literal[0]).get("name"));

        ObjectMapper mapper = defaultTypingMapper().build();
        Object[] result = mapper.readValue(mapper.writeValueAsBytes(
                new Object[] { new StringBean("abc") }), Object[].class);
        assertEquals(1, result.length);
        StringBean bean = assertInstanceOf(StringBean.class, result[0]);
        assertEquals("abc", bean.name);
    }

    // Provenance: TestDefaultForObject#testBeanAsObjectUsingAsProperty().
    void testBeanAsObjectUsingAsPropertyVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .activateDefaultTypingAsProperty(NoCheckSubTypeValidator.INSTANCE,
                        DefaultTyping.NON_FINAL, ".hype")
                .build();
        byte[] encoded = mapper.writeValueAsBytes(new StringBean("abc"));
        Map<?, ?> wire = VPackMapper.builder().build().readValue(encoded, Map.class);
        assertEquals("abc", wire.get("name"));
        assertEquals(StringBean.class.getName(), wire.get(".hype"));

        StringBean result = (StringBean) mapper.readValue(encoded, Object.class);
        assertEquals("abc", result.name);
    }

    // Provenance: TestDefaultForObject#testAsPropertyWithPTV().
    void testAsPropertyWithPTVVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .activateDefaultTypingAsProperty(new BlockAllPTV(),
                        DefaultTyping.NON_FINAL, "@classy")
                .build();
        byte[] encoded = mapper.writeValueAsBytes(new StringBean("abc"));
        InvalidDefinitionException e = assertThrows(InvalidDefinitionException.class,
                () -> mapper.readValue(encoded, Object.class));
        assertNotNull(e.getMessage());
        assertEquals(true, e.getMessage().contains("PolymorphicTypeValidator"));
        assertEquals(true, e.getMessage().contains("denied resolution"));
    }

    // Provenance: TestDefaultForObject#testAbstractBean().
    void testAbstractBeanVpack() throws Exception {
        ObjectMapper plain = VPackMapper.builder().build();
        assertThrows(InvalidDefinitionException.class,
                () -> plain.readValue(ABSTRACT_BEAN_ARRAY, AbstractBean[].class));

        ObjectMapper mapper = VPackMapper.builder()
                .activateDefaultTyping(NoCheckSubTypeValidator.INSTANCE,
                        DefaultTyping.OBJECT_AND_NON_CONCRETE).build();
        AbstractBean[] result = mapper.readValue(mapper.writeValueAsBytes(
                new AbstractBean[] { new StringBean("xyz") }), AbstractBean[].class);
        assertEquals(1, result.length);
        StringBean bean = assertInstanceOf(StringBean.class, result[0]);
        assertEquals("xyz", bean.name);
    }

    // Provenance: TestDefaultForObject#testEnumAsObject().
    void testEnumAsObjectVpack() throws Exception {
        ObjectMapper plain = VPackMapper.builder().build();
        Choice[] literal = plain.readValue(ENUM_ARRAY, Choice[].class);
        assertEquals(1, literal.length);
        assertSame(Choice.YES, literal[0]);

        ObjectMapper mapper = defaultTypingMapper().build();
        Object[] result = mapper.readValue(mapper.writeValueAsBytes(
                new Object[] { Choice.YES, ComplexChoice.MAYBE }), Object[].class);
        assertSame(Choice.YES, result[0]);
        assertSame(ComplexChoice.MAYBE, result[1]);
    }

    // Provenance: TestDefaultForObject#testEnumSet().
    
    void testEnumSetVpack() throws Exception {
        ObjectMapper mapper = defaultTypingMapper().build();
        Object[] result = mapper.readValue(mapper.writeValueAsBytes(
                new Object[] { EnumSet.of(Choice.NO) }), Object[].class);
        EnumSet<Choice> set = assertInstanceOf(EnumSet.class, result[0]);
        assertEquals(EnumSet.of(Choice.NO), set);
        assertFalse(set.contains(Choice.YES));
    }

    // Provenance: TestDefaultForObject#testEnumMap().
    
    void testEnumMapVpack() throws Exception {
        EnumMap<Choice, String> input = new EnumMap<>(Choice.class);
        input.put(Choice.NO, "maybe");
        ObjectMapper mapper = defaultTypingMapper().build();
        Object[] result = mapper.readValue(mapper.writeValueAsBytes(
                new Object[] { input }), Object[].class);
        EnumMap<Choice, String> map = assertInstanceOf(EnumMap.class, result[0]);
        assertEquals("maybe", map.get(Choice.NO));
        assertNull(map.get(Choice.YES));
    }

    // Provenance: TestDefaultForObject#testJackson311().
    void testJackson311Vpack() throws Exception {
        ObjectMapper mapper = defaultTypingMapper().build();
        PolymorphicType result = mapper.readValue(mapper.writeValueAsBytes(
                new PolymorphicType("hello", 2)), PolymorphicType.class);
        assertEquals("hello", result.foo);
        assertEquals(Integer.valueOf(2), result.bar);
    }

    // Provenance: TestDefaultForObject#testIssue352().
    void testIssue352Vpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .activateDefaultTyping(NoCheckSubTypeValidator.INSTANCE,
                        DefaultTyping.OBJECT_AND_NON_CONCRETE, JsonTypeInfo.As.PROPERTY)
                .build();
        DiscussBean discuss = new DiscussBean();
        discuss.subject = "mouse";
        discuss.weight = 88;
        DomainBeanWrapper input = new DomainBeanWrapper();
        input.name = "mickey";
        input.myBean = discuss;

        DomainBeanWrapper result = mapper.readValue(mapper.writeValueAsBytes(input),
                DomainBeanWrapper.class);
        assertNotNull(result);
        assertSame(DiscussBean.class, result.myBean.getClass());
        assertEquals("mouse", ((DiscussBean) result.myBean).subject);
    }

    // Provenance: TestDefaultForObject#testFeature432().
    void testFeature432Vpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .activateDefaultTypingAsProperty(NoCheckSubTypeValidator.INSTANCE,
                        DefaultTyping.OBJECT_AND_NON_CONCRETE, "*CLASS*")
                .build();
        byte[] encoded = mapper.writeValueAsBytes(new BeanHolder(new StringBean("punny")));
        Map<?, ?> wire = VPackMapper.builder().build().readValue(encoded, Map.class);
        Map<?, ?> bean = assertInstanceOf(Map.class, wire.get("bean"));
        assertEquals(StringBean.class.getName(), bean.get("*CLASS*"));
        assertEquals("punny", bean.get("name"));
        BeanHolder result = mapper.readValue(encoded, BeanHolder.class);
        assertEquals("punny", ((StringBean) result.bean).name);
    }

    // Provenance: TestDefaultForObject#testDeserializationConcreteClassWithDefaultTyping().
    void testDeserializationConcreteClassWithDefaultTypingVpack() throws Exception {
        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                .allowIfBaseType(SimpleBall.class).build();
        ObjectMapper mapper = VPackMapper.builder()
                .activateDefaultTyping(ptv, DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY)
                .disable(MapperFeature.REQUIRE_TYPE_ID_FOR_SUBTYPES).build();
        BasketBall result = mapper.readValue(CONCRETE_BASKETBALL, BasketBall.class);
        assertEquals(42, result.size);
    }

    // Provenance: TestDefaultForObject#testForAbstractTypeMapping3235().
    void testForAbstractTypeMapping3235Vpack() throws Exception {
        Map<?, ?> literal = VPackMapper.builder().build().readValue(ABSTRACT_MAPPING, Map.class);
        assertEquals(2, literal.size());
        ObjectMapper mapper = VPackMapper.builder()
                .enable(MapperFeature.USE_BASE_TYPE_AS_DEFAULT_IMPL)
                .addModule(new SimpleModule()
                        .addAbstractTypeMapping(AbstractParentWithoutDefault3235.class,
                                ChildOfParentWithoutDefault3235.class)
                        .addAbstractTypeMapping(Map.class, TreeMap.class)
                        .addAbstractTypeMapping(List.class, LinkedList.class))
                .registerSubtypes(TreeMap.class, LinkedList.class,
                        ChildOfParentWithoutDefault3235.class)
                .setDefaultTyping(new DefaultTypeResolverBuilder(
                        NoCheckSubTypeValidator.INSTANCE, DefaultTyping.NON_FINAL,
                        JsonTypeInfo.As.PROPERTY, JsonTypeInfo.Id.CLASS, "foo"))
                .build();
        AbstractParentWithoutDefault3235 base = mapper.readValue(ABSTRACT_MAPPING,
                AbstractParentWithoutDefault3235.class);
        assertEquals(ChildOfParentWithoutDefault3235.class, base.getClass());
        ChildOfParentWithoutDefault3235 result = (ChildOfParentWithoutDefault3235) base;
        assertEquals(TreeMap.class, result.mapField.getClass());
        assertEquals(Parent3235.class, result.objectField.getClass());
    }
private static VPackMapper.Builder defaultTypingMapper() {
        return VPackMapper.builder()
                .activateDefaultTyping(NoCheckSubTypeValidator.INSTANCE);
    }
static abstract class AbstractBean { }
static class StringBean extends AbstractBean {
        public String name;
        public StringBean() { }
        StringBean(String name) { this.name = name; }
    }
enum Choice { YES, NO }
enum ComplexChoice {
        MAYBE(true), PROBABLY_NOT(false);
        private final boolean state;
        ComplexChoice(boolean state) { this.state = state; }
        @Override public String toString() { return String.valueOf(state); }
    }
static class PolymorphicType {
        public String foo;
        public Object bar;
        PolymorphicType() { }
        PolymorphicType(String foo, int bar) { this.foo = foo; this.bar = bar; }
    }
static class BeanHolder {
        public AbstractBean bean;
        BeanHolder() { }
        BeanHolder(AbstractBean bean) { this.bean = bean; }
    }
static class DomainBean { public int weight; }
static class DiscussBean extends DomainBean { public String subject; }
static class DomainBeanWrapper { public String name; public Object myBean; }
static abstract class SimpleBall { public int size = 3; }
static class BasketBall extends SimpleBall {
        BasketBall() { }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
    static class Parent3235 { }
static abstract class AbstractParentWithoutDefault3235 { }
static class ChildOfParentWithoutDefault3235 extends AbstractParentWithoutDefault3235 {
        public Map<String, String> mapField;
        public Parent3235 objectField;
    }
static class BlockAllPTV extends PolymorphicTypeValidator.Base {
        @Override
        public Validity validateBaseType(tools.jackson.databind.DatabindContext ctxt,
                tools.jackson.databind.JavaType baseType) {
            return Validity.DENIED;
        }
    }
static final class NoCheckSubTypeValidator extends PolymorphicTypeValidator.Base {
        private static final long serialVersionUID = 1L;
        static final NoCheckSubTypeValidator INSTANCE = new NoCheckSubTypeValidator();

        @Override
        public Validity validateBaseType(tools.jackson.databind.DatabindContext ctxt,
                tools.jackson.databind.JavaType baseType) {
            return Validity.ALLOWED;
        }
    }

    void __invoke_testBeanAsObjectVpack() throws Exception {
        try {
            testBeanAsObjectVpack();
        } finally {
        }
    }


    void __invoke_testBeanAsObjectUsingAsPropertyVpack() throws Exception {
        try {
            testBeanAsObjectUsingAsPropertyVpack();
        } finally {
        }
    }


    void __invoke_testAsPropertyWithPTVVpack() throws Exception {
        try {
            testAsPropertyWithPTVVpack();
        } finally {
        }
    }


    void __invoke_testAbstractBeanVpack() throws Exception {
        try {
            testAbstractBeanVpack();
        } finally {
        }
    }


    void __invoke_testEnumAsObjectVpack() throws Exception {
        try {
            testEnumAsObjectVpack();
        } finally {
        }
    }


    void __invoke_testEnumSetVpack() throws Exception {
        try {
            testEnumSetVpack();
        } finally {
        }
    }


    void __invoke_testEnumMapVpack() throws Exception {
        try {
            testEnumMapVpack();
        } finally {
        }
    }


    void __invoke_testJackson311Vpack() throws Exception {
        try {
            testJackson311Vpack();
        } finally {
        }
    }


    void __invoke_testIssue352Vpack() throws Exception {
        try {
            testIssue352Vpack();
        } finally {
        }
    }


    void __invoke_testFeature432Vpack() throws Exception {
        try {
            testFeature432Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializationConcreteClassWithDefaultTypingVpack() throws Exception {
        try {
            testDeserializationConcreteClassWithDefaultTypingVpack();
        } finally {
        }
    }


    void __invoke_testForAbstractTypeMapping3235Vpack() throws Exception {
        try {
            testForAbstractTypeMapping3235Vpack();
        } finally {
        }
    }

}
