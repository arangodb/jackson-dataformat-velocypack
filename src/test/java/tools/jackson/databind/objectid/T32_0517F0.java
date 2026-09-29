package tools.jackson.databind.objectid;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import tools.jackson.core.JsonParser;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.InjectableValues;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.module.SimpleModule;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import tools.jackson.dataformat.velocypack.*;

class T32_0517F0 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] CREATOR_GRAPH = VPackWireFixtureTest.hex(
            "14 52 45 6c 61 62 65 6c 4a 65 6e 63 6c 6f 73 69 6e 67 31 "
          + "47 62 61 73 65 52 65 66 "
          + "14 10 45 6c 61 62 65 6c 46 6c 61 62 65 6c 31 01 47 6e 65 78 74 52 65 66 "
          + "14 1e 45 6c 61 62 65 6c 45 74 65 73 74 31 44 72 65 66 73 "
          + "13 0a 46 6c 61 62 65 6c 31 01 02 03");
private static final byte[] NO_CREATOR_GRAPH = VPackWireFixtureTest.hex(
            "14 52 45 6c 61 62 65 6c 4a 65 6e 63 6c 6f 73 69 6e 67 32 "
          + "47 62 61 73 65 52 65 66 "
          + "14 10 45 6c 61 62 65 6c 46 6c 61 62 65 6c 32 01 47 6e 65 78 74 52 65 66 "
          + "14 1e 45 6c 61 62 65 6c 45 74 65 73 74 32 44 72 65 66 73 "
          + "13 0a 46 6c 61 62 65 6c 32 01 02 03");
private static final byte[] CREATOR_ID = VPackWireFixtureTest.hex(
            "14 19 42 69 64 44 6d 79 49 64 45 76 61 6c 75 65 47 6d 79 56 61 6c 75 65 02");
private static final byte[] CREATOR_VALUE = VPackWireFixtureTest.hex(
            "14 14 42 69 64 4d 76 61 6c 75 65 46 72 6f 6d 4a 73 6f 6e 01");
private static final byte[] NO_FORWARD_REFERENCE = VPackWireFixtureTest.hex(
            "14 31 42 62 73 13 15 14 09 42 69 64 42 62 31 01 "
          + "14 09 42 69 64 42 62 32 01 02 "
          + "42 63 73 13 13 14 08 41 62 42 62 31 01 "
          + "14 08 41 62 42 62 32 01 02 02");
private static final byte[] FORWARD_REFERENCE = VPackWireFixtureTest.hex(
            "14 31 42 63 73 13 13 14 08 41 62 42 62 31 01 "
          + "14 08 41 62 42 62 32 01 02 "
          + "42 62 73 13 15 14 09 42 69 64 42 62 31 01 "
          + "14 09 42 69 64 42 62 32 01 02 02");
private static final byte[] DELEGATING_CREATOR = VPackWireFixtureTest.hex(
            "13 1a 14 16 43 40 69 64 31 42 69 64 31 44 6e 61 6d 65 44 74 65 73 74 03 31 02");
private static final byte[] CUSTOM_ENTITY = VPackWireFixtureTest.hex(
            "14 0c 46 65 6e 74 69 74 79 28 63 01");
private static final byte[] CUSTOM_ENTITY_LIST = VPackWireFixtureTest.hex(
            "14 12 48 65 6e 74 69 74 69 65 73 13 06 31 32 33 03 01");
private static final byte[] EQUALITY_LITERAL = VPackWireFixtureTest.hex(
            "06 5f 02 0b 56 03 46 40 63 6c 61 73 73 72 74 6f" +
                "6f 6c 73 2e 6a 61 63 6b 73 6f 6e 2e 64 61 74 61" +
                "62 69 6e 64 2e 6f 62 6a 65 63 74 69 64 2e 54 33" +
                "32 5f 30 35 31 37 46 30 24 45 6c 65 6d 65 6e 74" +
                "43 75 72 69 43 55 52 49 44 6e 61 6d 65 48 45 6c" +
                "65 6d 65 6e 74 31 03 45 3d 43 55 52 49 03 59");

    // Provenance: ObjectIdWithCreatorTest#testSerializeDeserializeWithCreator().
    void testSerializeDeserializeWithCreatorVpack() throws Exception {
        EnclosingForRefsWithCreator result = MAPPER.readValue(CREATOR_GRAPH,
                EnclosingForRefsWithCreator.class);
        assertEquals("enclosing1", result.label);
        assertEquals(1, result.nextRef.refs.size());
        assertSame(result.baseRef, result.nextRef.refs.get(0));

        EnclosingForRefsWithCreator roundTrip = MAPPER.readValue(
                MAPPER.writeValueAsBytes(result), EnclosingForRefsWithCreator.class);
        assertEquals(result.label, roundTrip.label);
        assertSame(roundTrip.baseRef, roundTrip.nextRef.refs.get(0));
    }

    // Provenance: ObjectIdWithCreatorTest#testSerializeDeserializeNoCreator().
    void testSerializeDeserializeNoCreatorVpack() throws Exception {
        EnclosingForRefWithNoCreator result = MAPPER.readValue(NO_CREATOR_GRAPH,
                EnclosingForRefWithNoCreator.class);
        assertEquals("enclosing2", result.label);
        assertEquals(1, result.nextRef.refs.size());
        assertSame(result.baseRef, result.nextRef.refs.get(0));

        EnclosingForRefWithNoCreator roundTrip = MAPPER.readValue(
                MAPPER.writeValueAsBytes(result), EnclosingForRefWithNoCreator.class);
        assertEquals(result.label, roundTrip.label);
        assertSame(roundTrip.baseRef, roundTrip.nextRef.refs.get(0));
    }

    // Provenance: ObjectIdWithCreatorTest#testObjectIds1261().
    void testObjectIds1261Vpack() throws Exception {
        Answer initial = createInitialAnswer();
        Answer result = MAPPER.readValue(MAPPER.writeValueAsBytes(initial), Answer.class);
        assertEquals(2, result.parents.size());
        Parent parent1 = result.parents.get("parent1");
        Parent parent2 = result.parents.get("parent2");
        assertSame(parent1, parent1.children.get("child1").parent);
        assertSame(parent1, parent1.children.get("child1").parentAsList.get(0));
        assertSame(parent2, parent1.children.get("child2").parent);
    }

    // Provenance: ObjectIdWithCreatorTest#testObjectIdWithCreator().
    void testObjectIdWithCreatorVpack() throws Exception {
        JsonBean2944 result = MAPPER.readValue(CREATOR_ID, JsonBean2944.class);
        assertNotNull(result);
        assertEquals("myId", result.id);
        assertEquals("myValue", result.value);
        assertEquals(null, result.setterId);
    }

    // Provenance: ObjectIdWithCreatorTest#testCreatorValuePreservedWithIdentityInfo3185().
    void testCreatorValuePreservedWithIdentityInfo3185Vpack() throws Exception {
        Pojo3185 result = MAPPER.readValue(CREATOR_VALUE, Pojo3185.class);
        assertEquals("valueFromJson-from-constructor", result.getFieldForId());
    }

    // Provenance: ObjectIdWithCreatorTest#testNoForwardReferenceWithCreator3030().
    void testNoForwardReferenceWithCreator3030Vpack() throws Exception {
        ContainerABC3030 result = MAPPER.readValue(NO_FORWARD_REFERENCE,
                ContainerABC3030.class);
        assertEquals(2, result.bs.size());
        assertEquals(2, result.cs.size());
        assertEquals("b1", result.bs.get(0).id);
        assertEquals("b2", result.bs.get(1).id);
        assertSame(result.bs.get(0), result.cs.get(0).getB());
        assertSame(result.bs.get(1), result.cs.get(1).getB());
    }

    // Provenance: ObjectIdWithCreatorTest#testForwardReferenceWithCreator3030().
    void testForwardReferenceWithCreator3030Vpack() throws Exception {
        ContainerABC3030 result = MAPPER.readValue(FORWARD_REFERENCE,
                ContainerABC3030.class);
        assertEquals(2, result.bs.size());
        assertEquals(2, result.cs.size());
        assertSame(result.bs.get(0), result.cs.get(0).getB());
        assertSame(result.bs.get(1), result.cs.get(1).getB());
    }

    // Provenance: ObjectIdWithCreatorTest#testObjectIdWithDelegatingCreator().
    void testObjectIdWithDelegatingCreatorVpack() throws Exception {
        List<Item1706> result = MAPPER.readValue(DELEGATING_CREATOR,
                new TypeReference<List<Item1706>>() { });
        assertEquals(2, result.size());
        assertInstanceOf(ImmutableItem1706.class, result.get(0));
        assertInstanceOf(ImmutableItem1706.class, result.get(1));
        assertSame(result.get(0), result.get(1));
    }

    // Provenance: ObjectIdWithCreatorTest#testObjectIdWithInjectable639().
    void testObjectIdWithInjectable639Vpack() throws Exception {
        InjectableValues.Std injectable = new InjectableValues.Std()
                .addValue("context", "Stuff");
        InjectParent639 source = new InjectParent639("Stuff");
        source.child = new InjectChild639(source);
        InjectParent639 result = MAPPER.reader(injectable)
                .forType(InjectParent639.class)
                .readValue(MAPPER.writeValueAsBytes(source));
        assertNotNull(result);
        assertNotNull(result.child);
        assertSame(result, result.child.parent);
    }
private static ObjectMapper customMapper() {
        SimpleModule module = new SimpleModule("test");
        module.addDeserializer(Entity2245.class, new Entity2245Deserializer());
        return VPackMapper.builder().addModule(module).build();
    }
private static Answer createInitialAnswer() {
        Answer answer = new Answer();
        Parent parent1 = new Parent("parent1", false);
        answer.parents.put("parent1", parent1);
        Child child1 = new Child("child1");
        child1.parent = parent1;
        child1.parentAsList = Collections.singletonList(parent1);
        Parent parent2 = new Parent("parent2", false);
        Child child2 = new Child("child2");
        child2.parent = parent2;
        child2.parentAsList = Collections.singletonList(parent2);
        parent1.children.put("child1", child1);
        parent1.children.put("child2", child2);
        answer.parents.put("parent2", parent2);
        return answer;
    }
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "label")
    static class ReferredWithCreator {
        public String label;
        @JsonCreator ReferredWithCreator(@JsonProperty("label") String label) { this.label = label; }
    }
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "label")
    static class ReferringToObjWithCreator {
        public String label = "test1";
        public List<ReferredWithCreator> refs = new ArrayList<>();
    }
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "label")
    static class EnclosingForRefsWithCreator {
        public String label = "enclosing1";
        public ReferredWithCreator baseRef;
        public ReferringToObjWithCreator nextRef;
    }
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "label")
    static class ReferredWithNoCreator { public String label = "label2"; }
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "label")
    static class ReferringToObjWithNoCreator {
        public String label = "test2";
        public List<ReferredWithNoCreator> refs = new ArrayList<>();
    }
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "label")
    static class EnclosingForRefWithNoCreator {
        public String label = "enclosing2";
        public ReferredWithNoCreator baseRef;
        public ReferringToObjWithNoCreator nextRef;
    }
static class Answer { public SortedMap<String, Parent> parents = new TreeMap<>(); }
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
    static class Parent {
        public Map<String, Child> children = new TreeMap<>();
        @JsonIdentityReference(alwaysAsId = true) public Child favoriteChild;
        public String name;
        protected Parent() { }
        protected Parent(String name, boolean ignored) { this.name = name; }
    }
@JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
    static class Child {
        public String name;
        @JsonIdentityReference(alwaysAsId = true) public Parent parent;
        @JsonIdentityReference(alwaysAsId = true) public List<Parent> parentAsList;
        public String someNullProperty;
        protected Child() { }
        @JsonCreator Child(@JsonProperty("name") String name,
                @JsonProperty("someNullProperty") String ignored) { this.name = name; }
        Child(String name) { this.name = name; }
    }
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    static class JsonBean2944 {
        String id;
        String value;
        String setterId;
        @JsonCreator JsonBean2944(@JsonProperty("id") String id, @JsonProperty("value") String value) {
            this.id = id; this.value = value;
        }
        public void setId(String value) { setterId = value; }
    }
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    static class Pojo3185 {
        private final String fieldForId;
        @JsonCreator Pojo3185(@JsonProperty("id") String value) { fieldForId = value + "-from-constructor"; }
        @JsonGetter("id") public String getFieldForId() { return fieldForId; }
    }
static class ContainerABC3030 { public List<RefTarget3030> bs; public List<RefSource3030> cs; }
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    static class RefTarget3030 { public String id; }
static class RefSource3030 {
        private RefTarget3030 b;
        @JsonCreator RefSource3030(@JsonProperty("b") RefTarget3030 b) { this.b = b; }
        @JsonGetter("b") public RefTarget3030 getB() { return b; }
    }
@JsonSerialize(as = ImmutableItem1706.class)
    @JsonDeserialize(as = ImmutableItem1706.class)
    @JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
    interface Item1706 { Integer getId(); String getName(); }
static class ImmutableItem1706 implements Item1706 {
        private final Integer id;
        private final String name;
        ImmutableItem1706(Integer id, String name) { this.id = id; this.name = name; }
        @Override public Integer getId() { return id; }
        @Override public String getName() { return name; }
        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        static ImmutableItem1706 fromJson(MutableItem1706 json) { return new ImmutableItem1706(json.id, json.name); }
        @Override public boolean equals(Object o) {
            return o instanceof ImmutableItem1706 other && id.equals(other.id) && name.equals(other.name);
        }
        @Override public int hashCode() { return 31 * id.hashCode() + name.hashCode(); }
    }
@JsonDeserialize
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE)
    static class MutableItem1706 implements Item1706 {
        private String name;
        private Integer id;
        public void setId(Integer id) { this.id = id; }
        public void setName(String name) { this.name = name; }
        @Override public String getName() { throw new UnsupportedOperationException(); }
        @Override public Integer getId() { throw new UnsupportedOperationException(); }
    }
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
    static class InjectParent639 {
        @JsonProperty public InjectChild639 child;
        @JsonCreator InjectParent639(@JacksonInject("context") String ignored) { }
    }
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
    static class InjectChild639 {
        @JsonProperty private final InjectParent639 parent;
        @JsonCreator InjectChild639(@JsonProperty("parent") InjectParent639 parent) { this.parent = parent; }
    }
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    static class Entity2245 { public int id; public String name; Entity2245() { } Entity2245(int id, String name) { this.id = id; this.name = name; } }
static class Container2245 {
        @JsonIdentityReference(alwaysAsId = true) public Entity2245 entity;
    }
static class ContainerWithList2245 {
        @JsonIdentityReference(alwaysAsId = true) public List<Entity2245> entities;
    }
static class Entity2245Deserializer extends StdDeserializer<Entity2245> {
        Entity2245Deserializer() { super(Entity2245.class); }
        @Override public Entity2245 deserialize(JsonParser p, tools.jackson.databind.DeserializationContext ctxt) {
            int id = p.getIntValue();
            return new Entity2245(id, "Resolved-" + id);
        }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "uri")
    static class Element {
        public URI uri;
        public String name;
    }

    void __invoke_testSerializeDeserializeWithCreatorVpack() throws Exception {
        try {
            testSerializeDeserializeWithCreatorVpack();
        } finally {
        }
    }


    void __invoke_testSerializeDeserializeNoCreatorVpack() throws Exception {
        try {
            testSerializeDeserializeNoCreatorVpack();
        } finally {
        }
    }


    void __invoke_testObjectIds1261Vpack() throws Exception {
        try {
            testObjectIds1261Vpack();
        } finally {
        }
    }


    void __invoke_testObjectIdWithCreatorVpack() throws Exception {
        try {
            testObjectIdWithCreatorVpack();
        } finally {
        }
    }


    void __invoke_testCreatorValuePreservedWithIdentityInfo3185Vpack() throws Exception {
        try {
            testCreatorValuePreservedWithIdentityInfo3185Vpack();
        } finally {
        }
    }


    void __invoke_testNoForwardReferenceWithCreator3030Vpack() throws Exception {
        try {
            testNoForwardReferenceWithCreator3030Vpack();
        } finally {
        }
    }


    void __invoke_testForwardReferenceWithCreator3030Vpack() throws Exception {
        try {
            testForwardReferenceWithCreator3030Vpack();
        } finally {
        }
    }


    void __invoke_testObjectIdWithDelegatingCreatorVpack() throws Exception {
        try {
            testObjectIdWithDelegatingCreatorVpack();
        } finally {
        }
    }


    void __invoke_testObjectIdWithInjectable639Vpack() throws Exception {
        try {
            testObjectIdWithInjectable639Vpack();
        } finally {
        }
    }

}
