package tools.jackson.databind.jsontype;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.exc.InvalidTypeIdException;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.testutil.DatabindTestUtil;
import tools.jackson.databind.testutil.NoCheckSubTypeValidator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestSubtypes extends DatabindTestUtil
{
    @JsonTypeInfo(use=JsonTypeInfo.Id.NAME)
    static abstract class SuperType {
    }

    @JsonTypeName("TypeB")
    static class SubB extends SuperType {
        public int b = 1;
    }

    static class SubC extends SuperType {
        public int c = 2;
    }

    static class SubD extends SuperType {
        public int d;
    }

    // "Empty" bean
    @JsonTypeInfo(use=JsonTypeInfo.Id.NAME)
    static abstract class BaseBean { }

    static class EmptyBean extends BaseBean { }

    static class EmptyNonFinal { }

    // Verify combinations

    static class PropertyBean
    {
        @JsonTypeInfo(use=JsonTypeInfo.Id.NAME)
        public SuperType value;

        public PropertyBean() { this(null); }
        protected PropertyBean(SuperType v) { value = v; }
    }

    @JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=As.PROPERTY, property="type")
    @JsonSubTypes({ @JsonSubTypes.Type(ImplX.class),
        @JsonSubTypes.Type(ImplY.class),
        @JsonSubTypes.Type(ImplAbs.class)
    })
    static abstract class BaseX { }

    @JsonTypeName("x")
    static class ImplX extends BaseX {
        public int x;

        public ImplX() { }
        protected ImplX(int x) { this.x = x; }
    }

    @JsonTypeName("y")
    static class ImplY extends BaseX {
        public int y;
    }

    // for [databind#919] testing
    @JsonTypeName("abs")
    abstract static class ImplAbs extends BaseX {
    }

    // [databind#663]
    static class AtomicWrapper {
        public BaseX value;

        public AtomicWrapper() { }
        protected AtomicWrapper(int x) { value = new ImplX(x); }
    }

    // Verifying limits on sub-class ids

    static class DateWrapper {
        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = As.WRAPPER_ARRAY)
        public Date value;
    }

    static class TheBomb {
        public int a;
        public TheBomb() {
            throw new Error("Ka-boom!");
        }
    }

    // [databind#1125]

    static class Issue1125Wrapper {
        public Base1125 value;

        public Issue1125Wrapper() { }
        public Issue1125Wrapper(Base1125 v) { value = v; }
    }

    @JsonTypeInfo(use=JsonTypeInfo.Id.NAME, defaultImpl=Default1125.class)
    @JsonSubTypes({ @JsonSubTypes.Type(Interm1125.class) })
    static class Base1125 {
        public int a;
    }

    @JsonSubTypes({ @JsonSubTypes.Type(value=Impl1125.class, name="impl") })
    static class Interm1125 extends Base1125 {
        public int b;
    }

    static class Impl1125 extends Interm1125 {
        public int c;

        public Impl1125() { }
        protected Impl1125(int a0, int b0, int c0) {
            a = a0;
            b = b0;
            c = c0;
        }
    }

    static class Default1125 extends Interm1125 {
        public int def;

        Default1125() { }
        protected Default1125(int a0, int b0, int def0) {
            a = a0;
            b = b0;
            def = def0;
        }
    }

    // [databind#1311]
    @JsonTypeInfo(property = "type", use = JsonTypeInfo.Id.NAME, defaultImpl = Factory1311ImplA.class)
    interface Factory1311 { }

    @JsonTypeName("implA")
    static class Factory1311ImplA implements Factory1311 { }

    @JsonTypeName("implB")
    static class Factory1311ImplB implements Factory1311 { }

    // [databind#2515]
    @JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=As.PROPERTY, property="#type")
    static abstract class SuperTypeWithoutDefault { }

    static class Sub extends SuperTypeWithoutDefault {
        public int a;

        public Sub(){}
        public Sub(int a) {
            this.a = a;
        }
    }

    static class POJOWrapper {
        @JsonProperty
        Sub sub1;
        @JsonProperty
        Sub sub2;

        public POJOWrapper(){}
        public POJOWrapper(Sub sub1, Sub sub2) {
            this.sub1 = sub1;
            this.sub2 = sub2;
        }
    }

    /*
    /**********************************************************************
    /* Unit tests
    /**********************************************************************
     */

    private final ObjectMapper MAPPER = newVPackMapper();

    @Test
    public void testPropertyWithSubtypes() throws Exception
    {
        // must register subtypes
        ObjectMapper mapper = vpackMapperBuilder()
                .registerSubtypes(SubB.class, SubC.class, SubD.class)
                .build();
        String json = VPackUtils.toJson(mapper.writeValueAsBytes(new PropertyBean(new SubC())));
        PropertyBean result = mapper.readValue(VPackUtils.toVPack(json), PropertyBean.class);
        assertSame(SubC.class, result.value.getClass());
    }

    // also works via modules
    @Test
    public void testSubtypesViaModule() throws Exception
    {
        SimpleModule module = new SimpleModule();
        module.registerSubtypes(SubB.class, SubC.class, SubD.class);
        ObjectMapper mapper = vpackMapperBuilder()
                .addModule(module)
                .build();
        String json = VPackUtils.toJson(mapper.writeValueAsBytes(new PropertyBean(new SubC())));
        PropertyBean result = mapper.readValue(VPackUtils.toVPack(json), PropertyBean.class);
        assertSame(SubC.class, result.value.getClass());

        // and as per [databind#1653]:
        module = new SimpleModule();
        List<Class<?>> l = new ArrayList<>();
        l.add(SubB.class);
        l.add(SubC.class);
        l.add(SubD.class);
        module.registerSubtypes(l);
        mapper = vpackMapperBuilder()
                .addModule(module)
                .build();
        json = VPackUtils.toJson(mapper.writeValueAsBytes(new PropertyBean(new SubC())));
        result = mapper.readValue(VPackUtils.toVPack(json), PropertyBean.class);
        assertSame(SubC.class, result.value.getClass());
    }

    @Test
    public void testSerialization() throws Exception
    {
        // serialization can detect type name ok without anything extra:
        SubB bean = new SubB();
        assertEquals("{\"@type\":\"TypeB\",\"b\":1}", VPackUtils.toJson(MAPPER.writeValueAsBytes(bean)));

        // but we can override type name here too
        ObjectMapper mapper = vpackMapperBuilder()
                .registerSubtypes(new NamedType(SubB.class, "typeB"))
                .build();
        assertEquals("{\"@type\":\"typeB\",\"b\":1}", VPackUtils.toJson(mapper.writeValueAsBytes(bean)));

        // and default name ought to be simple class name; with context
        assertEquals("{\"@type\":\"TestSubtypes$SubD\",\"d\":0}", VPackUtils.toJson(mapper.writeValueAsBytes(new SubD())));
    }

    @Test
    public void testDeserializationNonNamed() throws Exception
    {
        ObjectMapper mapper = vpackMapperBuilder()
                .registerSubtypes(SubC.class)
                .build();
        // default name should be unqualified class name
        SuperType bean = mapper.readValue(VPackUtils.toVPack("{\"@type\":\"TestSubtypes$SubC\", \"c\":1}"), SuperType.class);
        assertSame(SubC.class, bean.getClass());
        assertEquals(1, ((SubC) bean).c);
    }

    @Test
    public void testDeserializatioNamed() throws Exception
    {
        ObjectMapper mapper = vpackMapperBuilder()
                .registerSubtypes(SubB.class)
                .registerSubtypes(new NamedType(SubD.class, "TypeD"))
                .build();

        SuperType bean = mapper.readValue(VPackUtils.toVPack("{\"@type\":\"TypeB\", \"b\":13}"), SuperType.class);
        assertSame(SubB.class, bean.getClass());
        assertEquals(13, ((SubB) bean).b);

        // but we can also explicitly register name too
        bean = mapper.readValue(VPackUtils.toVPack("{\"@type\":\"TypeD\", \"d\":-4}"), SuperType.class);
        assertSame(SubD.class, bean.getClass());
        assertEquals(-4, ((SubD) bean).d);
    }

    @Test
    public void testEmptyBean() throws Exception
    {
        // First, with annotations
        ObjectMapper mapper = vpackMapperBuilder()
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, true)
                .build();
        String json = VPackUtils.toJson(mapper.writeValueAsBytes(new EmptyBean()));
        assertEquals("{\"@type\":\"TestSubtypes$EmptyBean\"}", json);

        mapper = vpackMapperBuilder()
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                .build();
        json = VPackUtils.toJson(mapper.writeValueAsBytes(new EmptyBean()));
        assertEquals("{\"@type\":\"TestSubtypes$EmptyBean\"}", json);

        // and then with defaults
        mapper = vpackMapperBuilder()
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            .activateDefaultTyping(NoCheckSubTypeValidator.instance, DefaultTyping.NON_FINAL)
            .build();
        json = VPackUtils.toJson(mapper.writeValueAsBytes(new EmptyNonFinal()));
        assertEquals("[\"tools.jackson.databind.jsontype.TestSubtypes$EmptyNonFinal\",{}]", json);
    }

    @Test
    public void testErrorMessage() throws Exception {
        ObjectMapper mapper = newVPackMapper();
        InvalidTypeIdException e = assertThrows(InvalidTypeIdException.class,
                () -> mapper.readValue(VPackUtils.toVPack("{ \"type\": \"z\"}"), BaseX.class));
        verifyException(e, "Could not resolve type id 'z' as a subtype of");
        verifyException(e, "known type ids = [x, y]");
    }

    @Test
    public void testViaAtomic() throws Exception {
        AtomicWrapper input = new AtomicWrapper(3);
        String json = VPackUtils.toJson(MAPPER.writeValueAsBytes(input));

        AtomicWrapper output = MAPPER.readValue(VPackUtils.toVPack(json), AtomicWrapper.class);
        assertNotNull(output);
        assertEquals(ImplX.class, output.value.getClass());
        assertEquals(3, ((ImplX) output.value).x);
    }

    // Test to verify that base/impl restriction is applied to polymorphic handling
    // even if class name is used as the id
    @Test
    public void testSubclassLimits() throws Exception
    {
        InvalidTypeIdException e = assertThrows(InvalidTypeIdException.class,
                () -> MAPPER.readValue(VPackUtils.toVPack(a2q("{'value':['"
                    +TheBomb.class.getName()+"',{'a':13}] }")), DateWrapper.class));
        verifyException(e, "not a subtype");
        verifyException(e, TheBomb.class.getName());
    }

    // [databind#1125]: properties from base class too

    @Test
    public void testIssue1125NonDefault() throws Exception
    {
        String json = VPackUtils.toJson(MAPPER.writeValueAsBytes(new Issue1125Wrapper(new Impl1125(1, 2, 3))));

        Issue1125Wrapper result = MAPPER.readValue(VPackUtils.toVPack(json), Issue1125Wrapper.class);
        assertNotNull(result.value);
        assertEquals(Impl1125.class, result.value.getClass());
        Impl1125 impl = (Impl1125) result.value;
        assertEquals(1, impl.a);
        assertEquals(2, impl.b);
        assertEquals(3, impl.c);
    }

    @Test
    public void testIssue1125WithDefault() throws Exception
    {
        Issue1125Wrapper result = MAPPER.readValue(VPackUtils.toVPack(a2q("{'value':{'a':3,'def':9,'b':5}}")),
        		Issue1125Wrapper.class);
        assertNotNull(result.value);
        assertEquals(Default1125.class, result.value.getClass());
        Default1125 impl = (Default1125) result.value;
        assertEquals(3, impl.a);
        assertEquals(5, impl.b);
        assertEquals(9, impl.def);
    }

    // [databind#2525]
    public void testSerializationWithDuplicateRegisteredSubtypes() throws Exception {
        ObjectMapper mapper = vpackMapperBuilder()
                .registerSubtypes(new NamedType(Sub.class, "sub1"))
                .registerSubtypes(new NamedType(Sub.class, "sub2"))
                .build();

        // the first registered type name is used for serialization
        Sub sub = new Sub(15);
        assertEquals("{\"#type\":\"sub1\",\"a\":15}", VPackUtils.toJson(mapper.writeValueAsBytes(sub)));
    }

    // [databind#2525]
    public void testDeserializationWithDuplicateRegisteredSubtypes() throws Exception {
        ObjectMapper mapper = vpackMapperBuilder()
        // We can register the same class with different names
        .registerSubtypes(new NamedType(Sub.class, "sub1"))
        .registerSubtypes(new NamedType(Sub.class, "sub2"))
        .build();

        // fields of a POJO will be deserialized correctly according to their field name
        POJOWrapper pojoWrapper = mapper.readValue(VPackUtils.toVPack("{\"sub1\":{\"#type\":\"sub1\",\"a\":10},\"sub2\":{\"#type\":\"sub2\",\"a\":50}}"), POJOWrapper.class);
        assertEquals(10, pojoWrapper.sub1.a);
        assertEquals(50, pojoWrapper.sub2.a);

        // Instances of the same object can be deserialized with multiple names
        SuperTypeWithoutDefault sub1 = mapper.readValue(VPackUtils.toVPack("{\"#type\":\"sub1\", \"a\":20}"), SuperTypeWithoutDefault.class);
        assertSame(Sub.class, sub1.getClass());
        assertEquals(20, ((Sub) sub1).a);
        SuperTypeWithoutDefault sub2 = mapper.readValue(VPackUtils.toVPack("{\"#type\":\"sub2\", \"a\":30}"), SuperTypeWithoutDefault.class);
        assertSame(Sub.class, sub2.getClass());
        assertEquals(30, ((Sub) sub2).a);
    }
}
