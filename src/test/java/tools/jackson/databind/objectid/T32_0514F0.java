package tools.jackson.databind.objectid;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.deser.UnresolvedForwardReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0514F0 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] UNRESOLVED_SCALAR_2955 = VPackWireFixtureTest.hex(
            "14 27 42 69 64 31 44 6e 61 6d 65 41 61 43 72 65 66 "
          + "14 15 42 69 64 32 44 6e 61 6d 65 41 62 43 72 65 66 "
          + "29 e7 03 03 03");
private static final byte[] EMPLOYEES_FORWARD = VPackWireFixtureTest.hex(
            "14 5d 49 65 6d 70 6c 6f 79 65 65 73 13 50 "
          + "14 27 42 69 64 31 44 6e 61 6d 65 45 46 69 72 73 74 "
          + "47 6d 61 6e 61 67 65 72 18 47 72 65 70 6f 72 74 73 "
          + "13 04 32 01 04 32 "
          + "14 25 42 69 64 32 44 6e 61 6d 65 46 53 65 63 6f 6e 64 "
          + "47 6d 61 6e 61 67 65 72 31 47 72 65 70 6f 72 74 73 01 04 03 01");
private static final byte[] DRAW_FORWARD = VPackWireFixtureTest.hex(
            "14 7a 47 41 53 68 61 70 65 73 13 24 "
          + "14 11 46 70 6f 69 6e 74 73 13 07 31 32 33 34 04 01 "
          + "14 10 46 70 6f 69 6e 74 73 13 06 32 35 33 03 01 02 "
          + "46 70 6f 69 6e 74 73 13 44 "
          + "14 0d 42 69 64 31 41 78 30 41 79 30 03 "
          + "14 0d 42 69 64 32 41 78 30 41 79 32 03 "
          + "14 0d 42 69 64 33 41 78 32 41 79 32 03 "
          + "14 0d 42 69 64 34 41 78 32 41 79 30 03 "
          + "14 0d 42 69 64 35 41 78 31 41 79 33 03 05 02");
private static final byte[] NULL_ARRAYS = VPackWireFixtureTest.hex(
            "14 1a 47 41 53 68 61 70 65 73 13 04 18 01 "
          + "46 70 6f 69 6e 74 73 13 04 18 01 02");
private static final byte[] ALWAYS_AS_ID = VPackWireFixtureTest.hex(
            "0b 0b 02 41 61 31 41 62 32 03 06");
private static final byte[] CUSTOM_PROPERTY = VPackWireFixtureTest.hex(
            "0b 20 03 48 63 75 73 74 6f 6d 49 64 28 7b "
          + "44 6e 65 78 74 28 7b 45 76 61 6c 75 65 20 ed "
          + "03 0e 15");
private static final byte[] CUSTOM_PROPERTY_VIA_PROPERTY = VPackWireFixtureTest.hex(
            "0b 2b 01 44 6e 6f 64 65 0b 22 03 42 69 64 28 7b "
          + "44 6e 65 78 74 0b 0b 01 44 6e 6f 64 65 28 7b 03 "
          + "45 76 61 6c 75 65 37 03 08 18 03");
private static final byte[] COLUMN_METADATA = VPackWireFixtureTest.hex(
            "14 39 41 61 14 31 43 40 69 64 31 44 6e 61 6d 65 45 42 69 6c 6c 79 "
          + "44 74 79 70 65 48 65 6d 70 6c 6f 79 65 65 47 63 6f 6d 6d 65 6e 74 "
          + "47 63 6f 6d 6d 65 6e 74 04 41 62 31 02");

    // Provenance: ObjectIdDeserializationTest#testUnresolvedScalarObjectIdAsNull2955().
    void testUnresolvedScalarObjectIdAsNull2955Vpack() throws Exception {
        ObjectMapper disabled = VPackMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS).build();
        Node2955 result = disabled.readValue(UNRESOLVED_SCALAR_2955, Node2955.class);
        assertNotNull(result);
        assertEquals("a", result.name);
        assertNotNull(result.ref);
        assertEquals("b", result.ref.name);
        assertNull(result.ref.ref);
    }

    // Provenance: ObjectIdDeserializationTest#testUnresolvedScalarObjectIdFails2955().
    void testUnresolvedScalarObjectIdFails2955Vpack() {
        UnresolvedForwardReference exception = assertThrows(UnresolvedForwardReference.class,
                () -> MAPPER.readValue(UNRESOLVED_SCALAR_2955, Node2955.class));
        assertEquals(999, exception.getUnresolvedIds().get(0).getId());
    }

    // Provenance: ObjectIdDeserializationTest#testWithFieldsInBaseClass1083().
    void testWithFieldsInBaseClass1083Vpack() throws Exception {
        JsonRoot1083 result = MAPPER.readValue(VPackWireFixtureTest.hex(
                "14 1f 47 73 63 68 65 6d 61 73 13 14 14 11 44 6e 61 6d 65 48 46 6f 6f 64 4d 61 72 74 01 01 01"),
                JsonRoot1083.class);
        assertEquals(1, result.schemas.size());
        assertEquals("FoodMart", result.schemas.get(0).name);
        assertEquals(JsonMapSchema.class, result.schemas.get(0).getClass());
    }
private static void assertEmployees(Employee first, Employee second) {
        assertEquals(1, first.id);
        assertEquals(2, second.id);
        assertEquals(1, first.reports.size());
        assertSame(second, first.reports.get(0));
        assertSame(first, second.manager);
    }
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    static class Node2955 {
        public int id;
        public String name;
        public Node2955 ref;
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = JsonMapSchema.class)
    @JsonSubTypes({
        @JsonSubTypes.Type(value = JsonMapSchema.class, name = "map"),
        @JsonSubTypes.Type(value = JsonJdbcSchema.class, name = "jdbc")
    })
    static abstract class JsonSchema {
        public String name;
    }
static class JsonMapSchema extends JsonSchema { }
static class JsonJdbcSchema extends JsonSchema { }
static class JsonRoot1083 {
        public List<JsonSchema> schemas;
    }
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    static class Employee {
        public int id;
        public String name;
        @JsonIdentityReference(alwaysAsId = true)
        public Employee manager;
        @JsonIdentityReference(alwaysAsId = true)
        public List<Employee> reports;
    }
static class ArrayCompany {
        public Employee[] employees;
    }
static class ArrayBlockingQueueCompany {
        public ArrayBlockingQueue<Employee> employees;
    }
static class Draw {
        @JsonProperty("AShapes")
        public Shape[] ashapes;
        public Point[] points;
    }
static class Shape {
        @JsonIdentityReference(alwaysAsId = true)
        public Point[] points;
    }
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    static class Point {
        public int id;
        public int x;
        public int y;
    }
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "id")
    static class AlwaysAsId {
        public int value;
        AlwaysAsId(int value) { this.value = value; }
    }
@JsonPropertyOrder(alphabetic = true)
    static class AlwaysContainer {
        @JsonIdentityReference(alwaysAsId = true)
        public AlwaysAsId a = new AlwaysAsId(13);
        @JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "id")
        @JsonIdentityReference(alwaysAsId = true)
        public Value b = new Value();
    }
static class Value { public int x = 3; }
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    static class TreeNode {
        public int id;
        public String name;
        @JsonIdentityReference(alwaysAsId = true)
        public TreeNode parent;
        public TreeNode child;
        TreeNode() { }
        TreeNode(TreeNode parent, int id, String name) {
            this.parent = parent;
            this.id = id;
            this.name = name;
        }
    }
@JsonPropertyOrder({"a", "b"})
    static class Wrapper {
        public ColumnMetadata a;
        public ColumnMetadata b;
    }
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
    static class ColumnMetadata {
        private final String name;
        private final String type;
        private final String comment;

        @JsonCreator
        ColumnMetadata(@JsonProperty("name") String name,
                @JsonProperty("type") String type,
                @JsonProperty("comment") String comment) {
            this.name = name;
            this.type = type;
            this.comment = comment;
        }

        @JsonProperty("name") public String getName() { return name; }
        @JsonProperty("type") public String getType() { return type; }
        @JsonProperty("comment") public String getComment() { return comment; }
    }
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "customId")
    static class IdentifiableWithProp {
        public int value;
        public int customId;
        public IdentifiableWithProp next;
        IdentifiableWithProp(int customId, int value) {
            this.customId = customId;
            this.value = value;
        }
    }
static class IdWrapperCustom {
        @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
        public ValueNodeCustom node;
        IdWrapperCustom(int id, int value) { node = new ValueNodeCustom(id, value); }
    }
static class ValueNodeCustom {
        public int value;
        private int id;
        public IdWrapperCustom next;
        ValueNodeCustom(int id, int value) { this.id = id; this.value = value; }
        @JsonProperty("id") public int getId() { return id; }
    }

    void __invoke_testUnresolvedScalarObjectIdAsNull2955Vpack() throws Exception {
        try {
            testUnresolvedScalarObjectIdAsNull2955Vpack();
        } finally {
        }
    }


    void __invoke_testUnresolvedScalarObjectIdFails2955Vpack() throws Exception {
        try {
            testUnresolvedScalarObjectIdFails2955Vpack();
        } finally {
        }
    }


    void __invoke_testWithFieldsInBaseClass1083Vpack() throws Exception {
        try {
            testWithFieldsInBaseClass1083Vpack();
        } finally {
        }
    }

}
