package tools.jackson.databind.deser.filter;

import java.beans.ConstructorProperties;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonDeserialize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0237F1 {
private static final byte[] PARENT_WITH_CHILD = VPackWireFixtureTest.hex(
            "14 2b 44 6e 61 6d 65 47 50 61 72 65 6e 74 31 "
          + "48 63 68 69 6c 64 72 65 6e 13 12 "
          + "14 0f 44 6e 61 6d 65 46 43 68 69 6c 64 31 01 01 02");
private static final byte[] CHILD_WITH_PARENT = VPackWireFixtureTest.hex(
            "14 41 44 6e 61 6d 65 46 43 68 69 6c 64 31 "
          + "46 70 61 72 65 6e 74 "
          + "14 2b 44 6e 61 6d 65 47 50 61 72 65 6e 74 31 "
          + "48 63 68 69 6c 64 72 65 6e 13 12 "
          + "14 0f 44 6e 61 6d 65 46 43 68 69 6c 64 31 01 01 02 02");
private static final byte[] MULTI_LEVEL_PARENT = VPackWireFixtureTest.hex(
            "14 71 44 6e 61 6d 65 47 50 61 72 65 6e 74 31 "
          + "4b 67 72 61 6e 64 50 61 72 65 6e 74 14 3a "
          + "44 6e 61 6d 65 4c 47 72 61 6e 64 50 61 72 65 6e 74 31 "
          + "47 70 61 72 65 6e 74 73 13 1d "
          + "14 1a 44 6e 61 6d 65 47 50 61 72 65 6e 74 31 "
          + "48 63 68 69 6c 64 72 65 6e 01 02 01 02 "
          + "48 63 68 69 6c 64 72 65 6e 13 12 "
          + "14 0f 44 6e 61 6d 65 46 43 68 69 6c 64 31 01 01 03");
private static final byte[] ID_AND_NAME = VPackWireFixtureTest.hex(
            "14 12 42 69 64 28 7b 44 6e 61 6d 65 44 6a 61 63 6b 02");
private static final byte[] TWO_IGNORED_OBJECTS = VPackWireFixtureTest.hex(
            "14 22 43 6f 62 6a 14 0b 41 78 28 0a 41 79 28 14 02 "
          + "44 6f 62 6a 32 14 0b 41 78 28 0a 41 79 28 14 02 02");
private static final byte[] TWO_IGNORED_OBJECTS_SECOND = VPackWireFixtureTest.hex(
            "14 22 43 6f 62 6a 14 0b 41 78 28 14 41 79 28 1e 02 "
          + "44 6f 62 6a 32 14 0b 41 78 28 14 41 79 28 28 02 02");
private static final byte[] IGNORED_AND_ANY_SETTER = VPackWireFixtureTest.hex(
            "14 25 44 6e 61 6d 65 44 74 65 73 74 "
          + "44 74 79 70 65 46 61 6e 69 6d 61 6c "
          + "45 65 78 74 72 61 45 76 61 6c 75 65 03");
private static final byte[] IGNORED_TYPE = VPackWireFixtureTest.hex(
            "14 19 44 6e 61 6d 65 44 74 65 73 74 "
          + "44 74 79 70 65 46 61 6e 69 6d 61 6c 02");

    // Provenance: JsonIgnoreProperties1622Test#noRaceConditionWithParentFirst.
    void noRaceConditionWithParentFirstVpack() throws Exception {
        Parent1622 parent = new VPackMapper().readValue(PARENT_WITH_CHILD, Parent1622.class);
        assertNotNull(parent);
        assertEquals("Parent1", parent.getName());
        assertEquals(1, parent.getChildren().size());
        assertEquals("Child1", parent.getChildren().get(0).getName());
    }

    // Provenance: JsonIgnoreProperties1622Test#raceConditionWithChildFirst.
    void raceConditionWithChildFirstVpack() throws Exception {
        ObjectMapper mapper = new VPackMapper();
        Child1622 child = mapper.readValue(CHILD_WITH_PARENT, Child1622.class);
        assertNotNull(child);
        assertEquals("Child1", child.getName());

        Parent1622 parent = mapper.readValue(PARENT_WITH_CHILD, Parent1622.class);
        assertNotNull(parent);
        assertEquals("Parent1", parent.getName());
        assertEquals(1, parent.getChildren().size());
        assertEquals("Child1", parent.getChildren().get(0).getName());
    }

    // Provenance: JsonIgnoreProperties1622Test#testBuilderWithIgnoreProperties.
    void testBuilderWithIgnorePropertiesVpack() throws Exception {
        ChildWithBuilder1622 child = new VPackMapper().readValue(
                CHILD_WITH_PARENT, ChildWithBuilder1622.class);
        assertNotNull(child);
        assertEquals("Child1", child.getName());
        assertNotNull(child.getParent());
        assertEquals(1, child.getParent().getChildren().size());
        assertNull(child.getParent().getChildren().get(0).getParent());
    }

    // Provenance: JsonIgnoreProperties1622Test#testJsonCreatorWithIgnoreProperties.
    void testJsonCreatorWithIgnorePropertiesVpack() throws Exception {
        ChildWithCreator1622 child = new VPackMapper().readValue(
                CHILD_WITH_PARENT, ChildWithCreator1622.class);
        assertNotNull(child);
        assertEquals("Child1", child.getName());
        assertNotNull(child.getParent());
        assertEquals(1, child.getParent().getChildren().size());
        assertNull(child.getParent().getChildren().get(0).getParent());
    }

    // Provenance: JsonIgnoreProperties1622Test#testMultiLevelNestedWithIgnoreProperties.
    void testMultiLevelNestedWithIgnorePropertiesVpack() throws Exception {
        ParentNested1622 parent = new VPackMapper().readValue(
                MULTI_LEVEL_PARENT, ParentNested1622.class);
        assertNotNull(parent);
        assertEquals("Parent1", parent.getName());
        assertNotNull(parent.getGrandParent());
        assertEquals(1, parent.getGrandParent().getParents().size());
        assertNull(parent.getGrandParent().getParents().get(0).getGrandParent());
        assertEquals(1, parent.getChildren().size());
        assertNull(parent.getChildren().get(0).getParent());
    }

    // Provenance: JsonIgnoreProperties1622Test#testRecordWithIgnoreProperties.
    void testRecordWithIgnorePropertiesVpack() throws Exception {
        ChildRecord1622 child = new VPackMapper().readValue(
                CHILD_WITH_PARENT, ChildRecord1622.class);
        assertNotNull(child);
        assertEquals("Child1", child.name());
        assertNotNull(child.parent());
        assertEquals(1, child.parent().children().size());
        assertNull(child.parent().children().get(0).parent());
    }

    // Provenance: JsonIgnoreProperties1622Test#workaroundWithAllowSetters.
    void workaroundWithAllowSettersVpack() throws Exception {
        ObjectMapper mapper = new VPackMapper();
        ChildForWorkaround1622 child = mapper.readValue(
                CHILD_WITH_PARENT, ChildForWorkaround1622.class);
        assertNotNull(child);
        assertEquals("Child1", child.getName());
        ParentWithWorkaround1622 parent = mapper.readValue(
                PARENT_WITH_CHILD, ParentWithWorkaround1622.class);
        assertNotNull(parent);
        assertEquals("Parent1", parent.getName());
        assertEquals(1, parent.getChildren().size());
        assertEquals("Child1", parent.getChildren().get(0).getName());
    }
static class Testing {
        @JsonIgnore
        public String ignore;
        String notIgnore;

        public Testing() { }

        @ConstructorProperties({ "ignore", "notIgnore" })
        public Testing(String ignore, String notIgnore) {
            this.ignore = ignore;
            this.notIgnore = notIgnore;
        }

        public String getIgnore() { return ignore; }
        public void setIgnore(String ignore) { this.ignore = ignore; }
        public String getNotIgnore() { return notIgnore; }
        public void setNotIgnore(String notIgnore) { this.notIgnore = notIgnore; }
    }
static class Parent1622 {
        private String name;
        @JsonIgnoreProperties("parent")
        private List<Child1622> children;
        public Parent1622() { }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public List<Child1622> getChildren() { return children; }
        public void setChildren(List<Child1622> children) { this.children = children; }
    }
static class Child1622 {
        private String name;
        private Parent1622 parent;
        public Child1622() { }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Parent1622 getParent() { return parent; }
        public void setParent(Parent1622 parent) { this.parent = parent; }
    }
static class ParentWithWorkaround1622 {
        private String name;
        @JsonIgnoreProperties(value = "parent", allowSetters = true)
        private List<ChildForWorkaround1622> children;
        public ParentWithWorkaround1622() { }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public List<ChildForWorkaround1622> getChildren() { return children; }
        public void setChildren(List<ChildForWorkaround1622> children) { this.children = children; }
    }
static class ChildForWorkaround1622 {
        private String name;
        private ParentWithWorkaround1622 parent;
        public ChildForWorkaround1622() { }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public ParentWithWorkaround1622 getParent() { return parent; }
        public void setParent(ParentWithWorkaround1622 parent) { this.parent = parent; }
    }
static class ParentWithCreator1622 {
        private String name;
        @JsonIgnoreProperties("parent")
        private List<ChildWithCreator1622> children;
        public ParentWithCreator1622() { }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public List<ChildWithCreator1622> getChildren() { return children; }
        public void setChildren(List<ChildWithCreator1622> children) { this.children = children; }
    }
static class ChildWithCreator1622 {
        private final String name;
        private final ParentWithCreator1622 parent;
        @JsonCreator
        public ChildWithCreator1622(@JsonProperty("name") String name,
                @JsonProperty("parent") ParentWithCreator1622 parent) {
            this.name = name;
            this.parent = parent;
        }
        public String getName() { return name; }
        public ParentWithCreator1622 getParent() { return parent; }
    }
@JsonDeserialize(builder = ChildWithBuilder1622.Builder.class)
    static class ChildWithBuilder1622 {
        private final String name;
        private final ParentWithBuilder1622 parent;
        private ChildWithBuilder1622(String name, ParentWithBuilder1622 parent) {
            this.name = name;
            this.parent = parent;
        }
        public String getName() { return name; }
        public ParentWithBuilder1622 getParent() { return parent; }
        static class Builder {
            private String name;
            private ParentWithBuilder1622 parent;
            @JsonProperty("name")
            public Builder withName(String name) { this.name = name; return this; }
            @JsonProperty("parent")
            public Builder withParent(ParentWithBuilder1622 parent) { this.parent = parent; return this; }
            public ChildWithBuilder1622 build() { return new ChildWithBuilder1622(name, parent); }
        }
    }
static class ParentWithBuilder1622 {
        private String name;
        @JsonIgnoreProperties("parent")
        private List<ChildWithBuilder1622> children;
        public ParentWithBuilder1622() { }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public List<ChildWithBuilder1622> getChildren() { return children; }
        public void setChildren(List<ChildWithBuilder1622> children) { this.children = children; }
    }
static class GrandParent1622 {
        private String name;
        @JsonIgnoreProperties("grandParent")
        private List<ParentNested1622> parents;
        public GrandParent1622() { }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public List<ParentNested1622> getParents() { return parents; }
        public void setParents(List<ParentNested1622> parents) { this.parents = parents; }
    }
static class ParentNested1622 {
        private String name;
        private GrandParent1622 grandParent;
        @JsonIgnoreProperties("parent")
        private List<ChildNested1622> children;
        @JsonCreator
        public ParentNested1622(@JsonProperty("name") String name,
                @JsonProperty("grandParent") GrandParent1622 grandParent,
                @JsonProperty("children") List<ChildNested1622> children) {
            this.name = name;
            this.grandParent = grandParent;
            this.children = children;
        }
        public String getName() { return name; }
        public GrandParent1622 getGrandParent() { return grandParent; }
        public List<ChildNested1622> getChildren() { return children; }
    }
static class ChildNested1622 {
        private final String name;
        private final ParentNested1622 parent;
        @JsonCreator
        public ChildNested1622(@JsonProperty("name") String name,
                @JsonProperty("parent") ParentNested1622 parent) {
            this.name = name;
            this.parent = parent;
        }
        public String getName() { return name; }
        public ParentNested1622 getParent() { return parent; }
    }
record ParentRecord1622(String name,
            @JsonIgnoreProperties("parent") List<ChildRecord1622> children) { }
record ChildRecord1622(String name, ParentRecord1622 parent) { }
@JsonIgnoreProperties(value = { "name" }, allowSetters = true)
    @JsonPropertyOrder(alphabetic = true)
    static class Simple1595 {
        private int id;
        private String name;
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
static class IgnoreObject1217 {
        public int x = 1;
        public int y = 2;
    }
static class TestIgnoreObject1217 {
        @JsonIgnoreProperties({ "x" })
        public IgnoreObject1217 obj;
        @JsonIgnoreProperties({ "y" })
        public IgnoreObject1217 obj2;
    }
@JsonIgnoreProperties({ "type" })
    static class AnySetter5865Pojo {
        public String name;
        public Map<String, Object> other = new HashMap<>();
        @com.fasterxml.jackson.annotation.JsonAnySetter
        public void setOther(String key, Object value) { other.put(key, value); }
    }
@JsonIgnoreProperties({ "type" })
    record Simple5865Record(String name) { }
@JsonIgnoreProperties({ "type" })
    static class Simple5865Pojo {
        public String name;
    }

    void __invoke_noRaceConditionWithParentFirstVpack() throws Exception {
        try {
            noRaceConditionWithParentFirstVpack();
        } finally {
        }
    }


    void __invoke_raceConditionWithChildFirstVpack() throws Exception {
        try {
            raceConditionWithChildFirstVpack();
        } finally {
        }
    }


    void __invoke_testBuilderWithIgnorePropertiesVpack() throws Exception {
        try {
            testBuilderWithIgnorePropertiesVpack();
        } finally {
        }
    }


    void __invoke_testJsonCreatorWithIgnorePropertiesVpack() throws Exception {
        try {
            testJsonCreatorWithIgnorePropertiesVpack();
        } finally {
        }
    }


    void __invoke_testMultiLevelNestedWithIgnorePropertiesVpack() throws Exception {
        try {
            testMultiLevelNestedWithIgnorePropertiesVpack();
        } finally {
        }
    }


    void __invoke_testRecordWithIgnorePropertiesVpack() throws Exception {
        try {
            testRecordWithIgnorePropertiesVpack();
        } finally {
        }
    }


    void __invoke_workaroundWithAllowSettersVpack() throws Exception {
        try {
            workaroundWithAllowSettersVpack();
        } finally {
        }
    }

}
