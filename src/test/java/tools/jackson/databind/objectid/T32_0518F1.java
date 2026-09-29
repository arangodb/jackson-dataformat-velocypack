package tools.jackson.databind.objectid;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.annotation.SimpleObjectIdResolver;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.annotation.JsonPOJOBuilder;
import tools.jackson.databind.cfg.EnumFeature;
import tools.jackson.databind.exc.InvalidDefinitionException;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0518F1 {
private static final ObjectMapper MAPPER = new VPackMapper();

    // Provenance: ObjectIdWithPolymorphicTest#testPolymorphicRoundtrip().
    void testPolymorphicRoundtripVpack() throws Exception {
        Impl input = new Impl(123, 456);
        input.next = new Impl(111, 222);
        input.next.next = input;

        Base result0 = MAPPER.readValue(MAPPER.writeValueAsBytes(input), Base.class);
        assertNotNull(result0);
        assertInstanceOf(Impl.class, result0);
        Impl result = (Impl) result0;
        assertEquals(123, result.value);
        assertEquals(456, result.extra);
        Impl result2 = (Impl) result.next;
        assertEquals(111, result2.value);
        assertEquals(222, result2.extra);
        assertSame(result, result2.next);
    }

    // Provenance: ObjectIdWithPolymorphicTest#testIssue811().
    void testIssue811Vpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .activateDefaultTypingAsProperty(BasicPolymorphicTypeValidator.builder()
                                .allowIfSubType("").build(),
                        DefaultTyping.NON_FINAL, "@class")
                .enable(SerializationFeature.INDENT_OUTPUT)
                .enable(EnumFeature.WRITE_ENUMS_USING_INDEX)
                .build();

        Process input = new Process();
        new Base811(input);
        new Base811(input);
        new Base811(input);

        Process result = mapper.readValue(mapper.writeValueAsBytes(input), Process.class);
        assertNotNull(result);
        assertEquals(0, result.id);
        assertEquals(3, result.children.size());
        assertSame(result, result.children.get(0).owner);
        assertSame(result, result.children.get(1).owner);
        assertSame(result, result.children.get(2).owner);
    }

    // Provenance: ObjectIdWithPolymorphicTest#testIssue877().
    void testIssue877Vpack() throws Exception {
        BaseInterfaceImpl877 one = new BaseInterfaceImpl877();
        BaseInterfaceImpl877 two = new BaseInterfaceImpl877();
        one.addInstance(two);
        two.addInstance(one);
        ListWrapper877<BaseInterfaceImpl877> input = new ListWrapper877<>();
        input.add(one);
        input.add(two);

        ObjectMapper mapper = VPackMapper.builder()
                .activateDefaultTypingAsProperty(BasicPolymorphicTypeValidator.builder()
                                .allowIfSubType("").build(),
                        DefaultTyping.NON_FINAL, "@class")
                .build();
        ListWrapper877<BaseInterfaceImpl877> result = mapper.readValue(
                mapper.writeValueAsBytes(input), new TypeReference<ListWrapper877<BaseInterfaceImpl877>>() { });
        assertNotNull(result);
        assertEquals(2, result.size());
        assertSame(result.get(0), ((BaseInterfaceImpl877) result.get(1)).firstInstance());
    }

    // Provenance: ObjectIdWithPolymorphicTest#testObjectAndTypeId().
    void testObjectAndTypeIdVpack() throws Exception {
        PolyBarEntity input = new PolyBarEntity();
        PolyFooEntity child = new PolyFooEntity();
        input.next = child;
        child.ref = input;

        PolyBaseEntity result0 = MAPPER.readValue(MAPPER.writeValueAsBytes(input), PolyBaseEntity.class);
        assertInstanceOf(PolyBarEntity.class, result0);
        PolyBarEntity result = (PolyBarEntity) result0;
        assertInstanceOf(PolyFooEntity.class, result.next);
        assertSame(result, ((PolyFooEntity) result.next).ref);
    }

    // Provenance: ObjectIdWithPolymorphicTest#testWithAbstractUsingProp1551().
    void testWithAbstractUsingProp1551Vpack() throws Exception {
        Car car = new Car();
        car.vehicleId = "123";
        car.numberOfDoors = 2;
        VehicleOwnerViaProp first = new VehicleOwnerViaProp();
        first.ownedVehicle = car;
        VehicleOwnerViaProp second = new VehicleOwnerViaProp();
        second.ownedVehicle = car;

        VehicleOwnerViaProp[] result = MAPPER.readValue(
                MAPPER.writeValueAsBytes(new VehicleOwnerViaProp[] { first, second }),
                VehicleOwnerViaProp[].class);
        assertEquals(2, result.length);
        assertSame(result[0].ownedVehicle, result[1].ownedVehicle);
    }

    // Provenance: ObjectIdWithPolymorphicTest#testFailingAbstractUsingProp1551().
    void testFailingAbstractUsingProp1551Vpack() throws Exception {
        Car car = new Car();
        car.vehicleId = "123";
        VehicleOwnerBroken first = new VehicleOwnerBroken();
        first.ownedVehicle = car;
        VehicleOwnerBroken second = new VehicleOwnerBroken();
        second.ownedVehicle = car;

        assertThrows(InvalidDefinitionException.class,
                () -> MAPPER.writeValueAsBytes(new VehicleOwnerBroken[] { first, second }));

        VehicleOwnerViaProp validFirst = new VehicleOwnerViaProp();
        validFirst.ownedVehicle = car;
        VehicleOwnerViaProp validSecond = new VehicleOwnerViaProp();
        validSecond.ownedVehicle = car;
        byte[] vpackInput = MAPPER.writeValueAsBytes(
                new VehicleOwnerViaProp[] { validFirst, validSecond });
        assertThrows(InvalidDefinitionException.class,
                () -> MAPPER.readValue(vpackInput, VehicleOwnerBroken[].class));
    }
private static ContainerWithClass5872 newClassContainer() {
        ContainerWithClass5872 container = new ContainerWithClass5872();
        DerivedFromClass5872 value = new DerivedFromClass5872();
        value.a = "foo";
        container.list = new ArrayList<>();
        container.list.add(value);
        container.list.add(value);
        return container;
    }
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id", scope = Foo.class)
    static class Foo {
        public int id;
        public List<Bar> bars = new ArrayList<>();
        public List<Bar> otherBars = new ArrayList<>();
        Foo() { }
        Foo(int id) { this.id = id; }
    }
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id", scope = Bar.class)
    static class Bar {
        public int id;
        Bar() { }
        Bar(int id) { this.id = id; }
        @Override public int hashCode() { return id; }
        @Override public boolean equals(Object value) {
            return value instanceof Bar && ((Bar) value).id == id;
        }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
    @JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "id")
    static abstract class Base {
        public int value;
        public Base next;
        Base() { }
        Base(int value) { this.value = value; }
    }
static class Impl extends Base {
        public int extra;
        Impl() { }
        Impl(int value, int extra) { super(value); this.extra = extra; }
    }
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    static class Base811 {
        public int id;
        public Base811 owner;
        Base811() { }
        Base811(Process owner) {
            this.owner = owner;
            if (owner == null) id = 0;
            else { id = ++owner.childIdCounter; owner.children.add(this); }
        }
    }
static class Process extends Base811 {
        protected int childIdCounter;
        public List<Base811> children = new ArrayList<>();
        Process() { super(null); }
    }
static abstract class Activity extends Base811 {
        protected Activity() { }
        Activity(Process owner, Activity parent) { super(owner); this.parent = parent; }
        protected Activity parent;
    }
static class Scope extends Activity {
        public final List<FaultHandler> faultHandlers = new ArrayList<>();
        Scope() { }
        Scope(Process owner, Activity parent) { super(owner, parent); }
    }
static class FaultHandler extends Base811 {
        public final List<Catch> catchBlocks = new ArrayList<>();
        FaultHandler() { }
        FaultHandler(Process owner) { super(owner); }
    }
static class Catch extends Scope {
        Catch() { }
        Catch(Process owner, Activity parent) { super(owner, parent); }
    }
interface BaseInterface877 { }
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
    static class BaseInterfaceImpl877 implements BaseInterface877 {
        @JsonProperty private List<BaseInterfaceImpl877> myInstances = new ArrayList<>();
        void addInstance(BaseInterfaceImpl877 value) { myInstances.add(value); }
        BaseInterfaceImpl877 firstInstance() { return myInstances.get(0); }
    }
static class ListWrapper877<T extends BaseInterface877> {
        @JsonProperty private List<T> myList = new ArrayList<>();
        void add(T value) { myList.add(value); }
        int size() { return myList.size(); }
        T get(int index) { return myList.get(index); }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY,
            property = "@class")
    static abstract class Vehicle { public String vehicleId; }
static class Car extends Vehicle { public int numberOfDoors; }
static class VehicleOwnerViaProp {
        @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "vehicleId")
        @JsonIdentityReference(alwaysAsId = false)
        public Vehicle ownedVehicle;
    }
static class VehicleOwnerBroken {
        @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "bogus")
        @JsonIdentityReference(alwaysAsId = false)
        public Vehicle ownedVehicle;
    }
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
    interface PolyBaseEntity { }
static class PolyFooEntity implements PolyBaseEntity {
        public int value;
        public PolyBaseEntity ref;
    }
static class PolyBarEntity implements PolyBaseEntity {
        public PolyFooEntity next;
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@c")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "@id")
    interface BaseEntity4014 {
        @JsonProperty("@id") Integer getId();
    }
static class CreatorFoo implements BaseEntity4014 {
        private final Integer id;
        private CreatorBar bar;
        @JsonCreator CreatorFoo(@JsonProperty("@id") Integer id) { this.id = id; }
        @JsonProperty("@id") public Integer getId() { return id; }
        public CreatorBar getBar() { return bar; }
        public void setBar(CreatorBar value) { bar = value; }
    }
static class CreatorBar implements BaseEntity4014 {
        private final Integer id;
        private CreatorFoo foo;
        @JsonCreator CreatorBar(@JsonProperty("@id") Integer id) { this.id = id; }
        @JsonProperty("@id") public Integer getId() { return id; }
        public CreatorFoo getFoo() { return foo; }
        public void setFoo(CreatorFoo value) { foo = value; }
    }
static class FooWithSetter implements BaseEntity4014 {
        private Integer id;
        private BarWithSetter bar;
        public FooWithSetter() { }
        @JsonProperty("@id") public Integer getId() { return id; }
        @JsonProperty("@id") public void setId(Integer value) { id = value; }
        public BarWithSetter getBar() { return bar; }
        public void setBar(BarWithSetter value) { bar = value; }
    }
static class BarWithSetter implements BaseEntity4014 {
        private Integer id;
        private FooWithSetter foo;
        public BarWithSetter() { }
        @JsonProperty("@id") public Integer getId() { return id; }
        @JsonProperty("@id") public void setId(Integer value) { id = value; }
        public FooWithSetter getFoo() { return foo; }
        public void setFoo(FooWithSetter value) { foo = value; }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@c")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "@id")
    interface WrapperArrayEntity { @JsonProperty("@id") Integer getId(); }
static class WrapFoo implements WrapperArrayEntity {
        private final Integer id;
        private WrapBar bar;
        @JsonCreator WrapFoo(@JsonProperty("@id") Integer id) { this.id = id; }
        @JsonProperty("@id") public Integer getId() { return id; }
        public WrapBar getBar() { return bar; }
        public void setBar(WrapBar value) { bar = value; }
    }
static class WrapBar implements WrapperArrayEntity {
        private final Integer id;
        private WrapFoo foo;
        @JsonCreator WrapBar(@JsonProperty("@id") Integer id) { this.id = id; }
        @JsonProperty("@id") public Integer getId() { return id; }
        public WrapFoo getFoo() { return foo; }
        public void setFoo(WrapFoo value) { foo = value; }
    }
static class ContainerWithClass5872 { @JsonProperty public List<BaseClass5872> list; }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
    @JsonSubTypes(@JsonSubTypes.Type(name = "DerivedFromClass", value = DerivedFromClass5872.class))
    @JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
    static class BaseClass5872 { }
static class DerivedFromClass5872 extends BaseClass5872 { @JsonProperty public String a; }
static class ContainerClassWithBuilder5872 { @JsonProperty public List<BaseClassWithBuilder5872> list; }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
    @JsonSubTypes(@JsonSubTypes.Type(name = "DerivedClassBuilder", value = DerivedClassWithBuilder5872.class))
    @JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class,
            resolver = SimpleObjectIdResolver.class)
    static class BaseClassWithBuilder5872 { }
@tools.jackson.databind.annotation.JsonDeserialize(builder = DerivedClassWithBuilder5872.DerivedBuilder.class)
    static class DerivedClassWithBuilder5872 extends BaseClassWithBuilder5872 {
        @JsonProperty public String a;
        DerivedClassWithBuilder5872(String a) { this.a = a; }
        @JsonPOJOBuilder(withPrefix = "", buildMethodName = "build")
        static class DerivedBuilder {
            private String a;
            @JsonProperty DerivedBuilder a(String value) { this.a = value; return this; }
            DerivedClassWithBuilder5872 build() { return new DerivedClassWithBuilder5872(a); }
        }
    }

    void __invoke_testPolymorphicRoundtripVpack() throws Exception {
        try {
            testPolymorphicRoundtripVpack();
        } finally {
        }
    }


    void __invoke_testIssue811Vpack() throws Exception {
        try {
            testIssue811Vpack();
        } finally {
        }
    }


    void __invoke_testIssue877Vpack() throws Exception {
        try {
            testIssue877Vpack();
        } finally {
        }
    }


    void __invoke_testObjectAndTypeIdVpack() throws Exception {
        try {
            testObjectAndTypeIdVpack();
        } finally {
        }
    }


    void __invoke_testWithAbstractUsingProp1551Vpack() throws Exception {
        try {
            testWithAbstractUsingProp1551Vpack();
        } finally {
        }
    }


    void __invoke_testFailingAbstractUsingProp1551Vpack() throws Exception {
        try {
            testFailingAbstractUsingProp1551Vpack();
        } finally {
        }
    }

}
