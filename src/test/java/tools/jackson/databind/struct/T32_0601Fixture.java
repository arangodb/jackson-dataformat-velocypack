package tools.jackson.databind.struct;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import tools.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import tools.jackson.dataformat.velocypack.*;

class T32_0601Fixture {
private static final ObjectMapper MAPPER = new VPackMapper();

    // Provenance: ParentChildReferencesTest#testSimpleRefs().
    void testSimpleRefsVpack() throws Exception {
        SimpleNode root = new SimpleNode("root");
        root.child = new SimpleNode("kid");
        root.child.parent = root;
        SimpleNode result = roundTrip(root, SimpleNode.class);
        assertEquals("root", result.name);
        assertNotNull(result.child);
        assertEquals("kid", result.child.name);
        assertSame(result, result.child.parent);
    }

    // Provenance: ParentChildReferencesTest#testSimpleRefsWithGetter().
    void testSimpleRefsWithGetterVpack() throws Exception {
        GetterNode root = new GetterNode("root");
        root.setChild(new GetterNode("kid"));
        root.getChild().setParent(root);
        GetterNode result = roundTrip(root, GetterNode.class);
        assertEquals("root", result.name);
        assertNotNull(result.getChild());
        assertEquals("kid", result.getChild().name);
        assertSame(result, result.getChild().getParent());
    }

    // Provenance: ParentChildReferencesTest#testFullRefs().
    void testFullRefsVpack() throws Exception {
        FullNode root = new FullNode("root");
        FullNode first = new FullNode("kid1");
        FullNode second = new FullNode("kid2");
        root.firstChild = first;
        first.parent = root;
        first.next = second;
        second.prev = first;
        FullNode result = roundTrip(root, FullNode.class);
        assertEquals("root", result.name);
        FullNode resultFirst = result.firstChild;
        assertNotNull(resultFirst);
        assertEquals("kid1", resultFirst.name);
        assertSame(result, resultFirst.parent);
        assertNull(resultFirst.prev);
        FullNode resultSecond = resultFirst.next;
        assertNotNull(resultSecond);
        assertEquals("kid2", resultSecond.name);
        assertSame(resultFirst, resultSecond.prev);
        assertNull(resultSecond.next);
    }

    // Provenance: ParentChildReferencesTest#testListOfRefs().
    void testListOfRefsVpack() throws Exception {
        ListParent root = new ListParent();
        root.nodes = Arrays.asList(new ListChild("a"), new ListChild("b"));
        ListParent result = roundTrip(root, ListParent.class);
        assertNotNull(result.nodes);
        assertEquals(2, result.nodes.size());
        assertEquals("a", result.nodes.get(0).name);
        assertEquals("b", result.nodes.get(1).name);
        assertSame(result, result.nodes.get(0).parent);
        assertSame(result, result.nodes.get(1).parent);
    }

    // Provenance: ParentChildReferencesTest#testMapOfRefs().
    void testMapOfRefsVpack() throws Exception {
        MapParent root = new MapParent();
        root.nodes = new HashMap<>();
        root.nodes.put("a1", new MapChild("a"));
        root.nodes.put("b2", new MapChild("b"));
        MapParent result = roundTrip(root, MapParent.class);
        assertNotNull(result.nodes);
        assertEquals(2, result.nodes.size());
        assertNotNull(result.nodes.get("a1"));
        assertNotNull(result.nodes.get("b2"));
        assertEquals("a", result.nodes.get("a1").name);
        assertEquals("b", result.nodes.get("b2").name);
        assertSame(result, result.nodes.get("a1").parent);
        assertSame(result, result.nodes.get("b2").parent);
    }

    // Provenance: ParentChildReferencesTest#testIssue693().
    void testIssue693Vpack() throws Exception {
        Issue693Parent root = new Issue693Parent();
        root.addChild(new Issue693Child("foo"));
        root.addChild(new Issue693Child("bar"));
        Issue693Parent result = roundTrip(root, Issue693Parent.class);
        assertEquals(2, result.children.size());
        for (Issue693Child child : result.children) {
            assertSame(result, child.getParent());
        }
    }

    // Provenance: ParentChildReferencesTest#testIssue708().
    void testIssue708Vpack() throws Exception {
        // Independently specified object/array/scalar values: {title:"Hroch",photos:[{id:3}]}.
        Advertisement708 value = MAPPER.readValue(ISSUE_708_INPUT, Advertisement708.class);
        assertNotNull(value);
    }

    // Provenance: ParentChildReferencesTest#testForwardRef().
    void testForwardRefVpack() throws Exception {
        ForwardContainer result = MAPPER.readValue(FORWARD_REF_INPUT, ForwardContainer.class);
        assertNotNull(result);
        assertEquals("ForwardReferenceContainerClass1", result.id);
        assertEquals("willBeForwardReferenced", result.frc);
        assertNotNull(result.yac);
        assertEquals("anId", result.yac.id);
        assertNotNull(result.yac.frc);
        assertInstanceOf(ForwardOne.class, result.yac.frc);
        assertEquals("willBeForwardReferenced", result.yac.frc.id);
    }

    // Provenance: ParentChildReferencesTest#testSerialize().
    void testSerializeVpack() throws Exception {
        Sheet sheet = new Sheet();
        sheet.add(new StringSheetProperty("p1name", "p1value"));
        sheet.add(new StringSheetProperty("p2name", "p2value"));
        byte[] encoded = MAPPER.writeValueAsBytes(sheet);
        Sheet result = MAPPER.readValue(encoded, Sheet.class);
        assertEquals(2, result.properties.size());
        assertEquals("p1value", result.properties.get("p1name").getValue());
        assertEquals("p2value", result.properties.get("p2name").getValue());
        assertInstanceOf(StringSheetProperty.class, result.properties.get("p1name"));
        assertInstanceOf(StringSheetProperty.class, result.properties.get("p2name"));
        assertSame(result, result.properties.get("p1name").getParent());
        assertSame(result, result.properties.get("p2name").getParent());
    }

    // Provenance: ParentChildReferencesTest#testWithParentCreator().
    void testWithParentCreatorVpack() throws Exception {
        ParentWithCreator result = MAPPER.readValue(PARENT_CREATOR_INPUT, ParentWithCreator.class);
        assertNotNull(result);
        assertNotNull(result.child);
        assertSame(result, result.child.parent);
    }

    // Provenance: ParentChildReferencesTest#testWithParentNoCreator().
    void testWithParentNoCreatorVpack() throws Exception {
        ParentWithoutCreator result = MAPPER.readValue(PARENT_CREATOR_INPUT, ParentWithoutCreator.class);
        assertNotNull(result);
        assertNotNull(result.child);
        assertSame(result, result.child.parent);
    }

    // Provenance: ParentChildReferencesTest#testManagedReferenceOnCreator().
    void testManagedReferenceOnCreatorVpack() throws Exception {
        Car car = new Car(100, new ArrayList<>());
        Color color = new Color(100, "#FFFFF");
        color.setCar(car);
        car.getColors().add(color);
        Car result = roundTrip(car, Car.class);
        assertNotNull(result);
        assertEquals(100, result.getId());
        assertNotNull(result.getColors());
        assertEquals(1, result.getColors().size());
        Color resultColor = result.getColors().get(0);
        assertEquals(100, resultColor.getId());
        assertEquals("#FFFFF", resultColor.getCode());
        assertNotNull(resultColor.getCar());
        assertSame(result, resultColor.getCar());
    }
private static <T> T roundTrip(Object value, Class<T> type) throws Exception {
        return MAPPER.readValue(MAPPER.writeValueAsBytes(value), type);
    }
private static final byte[] ISSUE_708_INPUT = VPackWireFixtureTest.hex(
            "14 20 45 74 69 74 6c 65 45 48 72 6f 63 68 46 70 68 6f 74 6f 73"
          + "13 0a 14 07 42 69 64 33 01 01 02");
private static final byte[] FORWARD_REF_INPUT = VPackWireFixtureTest.hex(
            "14 7d 43 66 72 63 57 77 69 6c 6c 42 65 46 6f 72 77 61 72 64 52 65 66 65 72 65 6e 63 65 64"
          + "43 79 61 63 14 37 43 66 72 63 14 28 45 40 74 79 70 65 43 4f 6e 65 42 69 64 57 77 69 6c 6c 42 65 46 6f 72 77 61 72 64 52 65 66 65 72 65 6e 63 65 64 02"
          + "42 69 64 44 61 6e 49 64 02 42 69 64 5f 46 6f 72 77 61 72 64 52 65 66 65 72 65 6e 63 65 43 6f 6e 74 61 69 6e 65 72 43 6c 61 73 73 31 03");
private static final byte[] PARENT_CREATOR_INPUT = VPackWireFixtureTest.hex(
            "14 2d 42 69 64 43 61 62 63 44 6e 61 6d 65 43 42 6f 62 45 63 68 69 6c 64"
          + "14 14 42 69 64 43 64 65 66 44 6e 61 6d 65 44 42 65 72 74 02 03");
static class SimpleNode {
        public String name;
        @JsonBackReference public SimpleNode parent;
        @JsonManagedReference public SimpleNode child;
        SimpleNode() { }
        SimpleNode(String value) { name = value; }
    }
static class GetterNode {
        public String name;
        private GetterNode parent;
        private GetterNode child;
        GetterNode() { }
        GetterNode(String value) { name = value; }
        @JsonBackReference public GetterNode getParent() { return parent; }
        public void setParent(GetterNode value) { parent = value; }
        @JsonManagedReference public GetterNode getChild() { return child; }
        public void setChild(GetterNode value) { child = value; }
    }
static class FullNode {
        public String name;
        @JsonBackReference("parent") public FullNode parent;
        @JsonManagedReference("parent") public FullNode firstChild;
        @JsonManagedReference("sibling") public FullNode next;
        @JsonBackReference("sibling") public FullNode prev;
        FullNode() { }
        FullNode(String value) { name = value; }
    }
static class ListParent { @JsonManagedReference public List<ListChild> nodes; }
static class ListChild {
        public String name;
        @JsonBackReference public ListParent parent;
        ListChild() { }
        ListChild(String value) { name = value; }
    }
static class MapParent { @JsonManagedReference public Map<String, MapChild> nodes; }
static class MapChild {
        public String name;
        @JsonBackReference public MapParent parent;
        MapChild() { }
        MapChild(String value) { name = value; }
    }
public static class Issue693Parent {
        @JsonManagedReference protected final List<Issue693Child> children = new ArrayList<>();
        public List<Issue693Child> getChildren() { return children; }
        public void addChild(Issue693Child value) { children.add(value); value.setParent(this); }
    }
public static class Issue693Child {
        protected Issue693Parent parent;
        private final String value;
        @JsonCreator Issue693Child(@JsonProperty("value") String v) { value = v; }
        public String getValue() { return value; }
        @JsonBackReference public Issue693Parent getParent() { return parent; }
        public void setParent(Issue693Parent value) { parent = value; }
    }
static class Advertisement708 { public String title; @JsonManagedReference public List<Photo708> photos; }
static class Photo708 { public int id; @JsonBackReference public Advertisement708 advertisement; }
static class ForwardContainer {
        public String frc;
        public ForwardYetAnother yac;
        public String id;
    }
static class ForwardYetAnother {
        public ForwardBase frc;
        public String id;
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonSubTypes({@JsonSubTypes.Type(value = ForwardOne.class, name = "One")})
    static abstract class ForwardBase { public String id; }
@JsonTypeName("One")
    static class ForwardOne extends ForwardBase { }
static class Sheet {
        @JsonManagedReference public Map<String, SheetProperty> properties = new HashMap<>();
        void add(SheetProperty value) { value.setParent(this); properties.put(value.getName(), value); }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
    interface SheetProperty {
        String getName();
        String getValue();
        @JsonBackReference Sheet getParent();
        void setParent(Sheet value);
    }
static class StringSheetProperty implements SheetProperty {
        public String name;
        public String value;
        private Sheet parent;
        StringSheetProperty() { }
        StringSheetProperty(String n, String v) { name = n; value = v; }
        @Override public String getName() { return name; }
        @Override public String getValue() { return value; }
        @Override public Sheet getParent() { return parent; }
        @Override public void setParent(Sheet value) { parent = value; }
    }
static class ParentWithCreator {
        String id, name;
        @JsonManagedReference ChildWithCreator child;
        @ConstructorProperties({"id", "name", "child"})
        ParentWithCreator(String i, String n, ChildWithCreator c) { id = i; name = n; child = c; }
    }
static class ChildWithCreator {
        public String id, name;
        @JsonBackReference public ParentWithCreator parent;
        @ConstructorProperties({"id", "name", "parent"})
        ChildWithCreator(String i, String n, ParentWithCreator p) { id = i; name = n; parent = p; }
    }
static class ParentWithoutCreator {
        public String id, name;
        @JsonManagedReference public ChildWithoutCreator child;
    }
static class ChildWithoutCreator {
        public String id, name;
        @JsonBackReference public ParentWithoutCreator parent;
        @ConstructorProperties({"id", "name", "parent"})
        ChildWithoutCreator(String i, String n, ParentWithoutCreator p) { id = i; name = n; parent = p; }
    }
static class Car {
        private final long id;
        @JsonManagedReference private final List<Color> colors;
        @JsonCreator Car(@JsonProperty("id") long value, @JsonProperty("colors") List<Color> entries) {
            id = value; colors = entries == null ? new ArrayList<>() : entries;
        }
        public long getId() { return id; }
        public List<Color> getColors() { return colors; }
    }
static class Color {
        private final long id;
        private final String code;
        @JsonBackReference private Car car;
        @JsonCreator Color(@JsonProperty("id") long value, @JsonProperty("code") String text) { id = value; code = text; }
        public long getId() { return id; }
        public String getCode() { return code; }
        public Car getCar() { return car; }
        public void setCar(Car value) { car = value; }
    }

    void __invoke_testSimpleRefsVpack() throws Exception {
        try {
            testSimpleRefsVpack();
        } finally {
        }
    }


    void __invoke_testSimpleRefsWithGetterVpack() throws Exception {
        try {
            testSimpleRefsWithGetterVpack();
        } finally {
        }
    }


    void __invoke_testFullRefsVpack() throws Exception {
        try {
            testFullRefsVpack();
        } finally {
        }
    }


    void __invoke_testListOfRefsVpack() throws Exception {
        try {
            testListOfRefsVpack();
        } finally {
        }
    }


    void __invoke_testMapOfRefsVpack() throws Exception {
        try {
            testMapOfRefsVpack();
        } finally {
        }
    }


    void __invoke_testIssue693Vpack() throws Exception {
        try {
            testIssue693Vpack();
        } finally {
        }
    }


    void __invoke_testIssue708Vpack() throws Exception {
        try {
            testIssue708Vpack();
        } finally {
        }
    }


    void __invoke_testForwardRefVpack() throws Exception {
        try {
            testForwardRefVpack();
        } finally {
        }
    }


    void __invoke_testSerializeVpack() throws Exception {
        try {
            testSerializeVpack();
        } finally {
        }
    }


    void __invoke_testWithParentCreatorVpack() throws Exception {
        try {
            testWithParentCreatorVpack();
        } finally {
        }
    }


    void __invoke_testWithParentNoCreatorVpack() throws Exception {
        try {
            testWithParentNoCreatorVpack();
        } finally {
        }
    }


    void __invoke_testManagedReferenceOnCreatorVpack() throws Exception {
        try {
            testManagedReferenceOnCreatorVpack();
        } finally {
        }
    }

}
