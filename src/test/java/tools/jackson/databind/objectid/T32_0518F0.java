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
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.annotation.JsonPOJOBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import tools.jackson.dataformat.velocypack.*;

class T32_0518F0 {
private static final ObjectMapper MAPPER = new VPackMapper();

    // Provenance: ObjectIdWithEqualsTest#testSimpleEquals().
    void testSimpleEqualsVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(SerializationFeature.USE_EQUALITY_FOR_OBJECT_ID)
                .configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true)
                .build();

        Foo foo = new Foo(1);
        Bar bar1 = new Bar(1);
        Bar bar2 = new Bar(2);
        Bar anotherBar1 = new Bar(1);
        foo.bars.add(bar1);
        foo.bars.add(bar2);
        foo.otherBars.add(anotherBar1);
        foo.otherBars.add(bar2);

        Foo result = mapper.readValue(mapper.writeValueAsBytes(foo), Foo.class);
        assertNotNull(result);
        assertEquals(1, result.id);
        assertEquals(2, result.bars.size());
        assertEquals(2, result.otherBars.size());
        assertSame(result.bars.get(0), result.otherBars.get(0));
        assertSame(result.bars.get(1), result.otherBars.get(1));
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

    void __invoke_testSimpleEqualsVpack() throws Exception {
        try {
            testSimpleEqualsVpack();
        } finally {
        }
    }

}
