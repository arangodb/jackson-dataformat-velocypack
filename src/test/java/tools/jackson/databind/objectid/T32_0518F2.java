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
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonPOJOBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;

import tools.jackson.dataformat.velocypack.*;

class T32_0518F2 {
private static final ObjectMapper MAPPER = new VPackMapper();

    // Provenance: ObjectIdWithTypeInfo4014Test#testCircularReferenceWithSetters().
    void testCircularReferenceWithSettersVpack() throws Exception {
        FooWithSetter foo = new FooWithSetter();
        foo.setId(1);
        BarWithSetter bar = new BarWithSetter();
        bar.setId(2);
        foo.setBar(bar);
        bar.setFoo(foo);

        BaseEntity4014 result0 = MAPPER.readValue(MAPPER.writeValueAsBytes(foo), BaseEntity4014.class);
        FooWithSetter result = assertInstanceOf(FooWithSetter.class, result0);
        assertEquals(1, result.getId());
        assertEquals(2, result.getBar().getId());
        assertSame(result, result.getBar().getFoo());
    }

    // Provenance: ObjectIdWithTypeInfo4014Test#testCircularReferenceWithCreator().
    void testCircularReferenceWithCreatorVpack() throws Exception {
        CreatorFoo foo = new CreatorFoo(1);
        CreatorBar bar = new CreatorBar(2);
        foo.setBar(bar);
        bar.setFoo(foo);

        BaseEntity4014 result0 = MAPPER.readValue(MAPPER.writeValueAsBytes(foo), BaseEntity4014.class);
        CreatorFoo result = assertInstanceOf(CreatorFoo.class, result0);
        assertEquals(1, result.getId());
        assertEquals(2, result.getBar().getId());
        assertSame(result, result.getBar().getFoo());
    }

    // Provenance: ObjectIdWithTypeInfo4014Test#testCircularReferenceWrapperArray().
    void testCircularReferenceWrapperArrayVpack() throws Exception {
        WrapFoo foo = new WrapFoo(1);
        WrapBar bar = new WrapBar(2);
        foo.setBar(bar);
        bar.setFoo(foo);

        WrapperArrayEntity result0 = MAPPER.readValue(
                MAPPER.writeValueAsBytes(foo), WrapperArrayEntity.class);
        WrapFoo result = assertInstanceOf(WrapFoo.class, result0);
        assertEquals(1, result.getId());
        assertEquals(2, result.getBar().getId());
        assertSame(result, result.getBar().getFoo());
    }

    // Provenance: ObjectIdWithTypeInfo4014Test#testObjectIdWithClassBaseType5872().
    void testObjectIdWithClassBaseType5872Vpack() throws Exception {
        ContainerWithClass5872 result = MAPPER.readValue(
                MAPPER.writeValueAsBytes(newClassContainer()), ContainerWithClass5872.class);
        assertEquals(2, result.list.size());
        assertInstanceOf(DerivedFromClass5872.class, result.list.get(0));
        assertSame(result.list.get(0), result.list.get(1));
        assertEquals("foo", ((DerivedFromClass5872) result.list.get(0)).a);
    }

    // Provenance: ObjectIdWithTypeInfo4014Test#testObjectIdWithClassAndBuilder5872().
    void testObjectIdWithClassAndBuilder5872Vpack() throws Exception {
        // Independent compact fixture: {list:[{type,id,a},"id1"]}; count suffixes
        // and lengths are hand-calculated, rather than obtained from the writer.
        byte[] fixture = VPackWireFixtureTest.hex(
                "14 3a 44 6c 69 73 74 13 32 14 2b "
              + "45 40 74 79 70 65 53 44 65 72 69 76 65 64 43 6c 61 73 73 42 75 69 6c 64 65 72 "
              + "43 40 69 64 43 69 64 31 41 61 43 66 6f 6f 03 43 69 64 31 02 01");
        ContainerClassWithBuilder5872 result = MAPPER.readValue(fixture,
                ContainerClassWithBuilder5872.class);
        assertEquals(2, result.list.size());
        assertInstanceOf(DerivedClassWithBuilder5872.class, result.list.get(0));
        assertSame(result.list.get(0), result.list.get(1));
        assertEquals("foo", ((DerivedClassWithBuilder5872) result.list.get(0)).a);
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

    void __invoke_testCircularReferenceWithSettersVpack() throws Exception {
        try {
            testCircularReferenceWithSettersVpack();
        } finally {
        }
    }


    void __invoke_testCircularReferenceWithCreatorVpack() throws Exception {
        try {
            testCircularReferenceWithCreatorVpack();
        } finally {
        }
    }


    void __invoke_testCircularReferenceWrapperArrayVpack() throws Exception {
        try {
            testCircularReferenceWrapperArrayVpack();
        } finally {
        }
    }


    void __invoke_testObjectIdWithClassBaseType5872Vpack() throws Exception {
        try {
            testObjectIdWithClassBaseType5872Vpack();
        } finally {
        }
    }


    void __invoke_testObjectIdWithClassAndBuilder5872Vpack() throws Exception {
        try {
            testObjectIdWithClassAndBuilder5872Vpack();
        } finally {
        }
    }

}
