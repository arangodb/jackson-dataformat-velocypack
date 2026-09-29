package tools.jackson.databind.objectid;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonPOJOBuilder;
import tools.jackson.databind.exc.InvalidDefinitionException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0515F0 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] TYPE_NUMBER = VPackWireFixtureTest.hex(
            "14 10 45 40 74 79 70 65 46 6e 75 6d 62 65 72 01");
private static final byte[] OPAQUE_LITERAL = VPackWireFixtureTest.hex(
            "14 37 42 69 64 46 66 6f 6f 62 61 72 45 76 61 6c 75 65 33 44 6e 65 78 74 "
          + "14 1e 42 69 64 44 62 61 72 66 45 76 61 6c 75 65 35 44 6e 65 78 74 46 66 6f 6f 62 61 72 03 03");
private static final byte[] BUILDER_INPUT = VPackWireFixtureTest.hex(
            "14 0f 42 69 64 28 7b 43 76 61 72 29 c8 01 02");
private static final byte[] MIXED_REFS = VPackWireFixtureTest.hex(
            "0b 66 01 49 65 6d 70 6c 6f 79 65 65 73 06 58 02 "
          + "0b 2a 04 42 69 64 31 47 6d 61 6e 61 67 65 72 18 44 6e 61 6d 65 45 46 69 72 73 74 "
          + "47 72 65 70 6f 72 74 73 02 03 32 03 07 10 1b "
          + "0b 29 04 42 69 64 32 47 6d 61 6e 61 67 65 72 31 44 6e 61 6d 65 46 53 65 63 6f 6e 64 "
          + "47 72 65 70 6f 72 74 73 01 03 07 10 1c 03 2d 03");

    // Provenance: ObjectIdSerializationTest#testSimpleSerializationClass().
    void testSimpleSerializationClassVpack() throws Exception {
        Identifiable source = new Identifiable(13);
        source.next = source;
        byte[] expected = VPackWireFixtureTest.hex(
                "0b 18 03 42 69 64 31 44 6e 65 78 74 31 45 76 61 6c 75 65 28 0d 03 07 0d");
        byte[] first = MAPPER.writeValueAsBytes(source);
        byte[] second = MAPPER.writeValueAsBytes(source);
        assertVPack(expected, first);
        assertVPack(expected, second);
        assertEquals(java.util.Arrays.toString(first), java.util.Arrays.toString(second));
        assertTrue(first.length > 0);
    }

    // Provenance: ObjectIdSerializationTest#testSimpleSerializationProperty().
    void testSimpleSerializationPropertyVpack() throws Exception {
        IdWrapper source = new IdWrapper(7);
        source.node.next = source;
        byte[] actual = MAPPER.writeValueAsBytes(source);
        assertVPack(VPackWireFixtureTest.hex(
                "0b 2a 01 44 6e 6f 64 65 0b 21 03 43 40 69 64 31 44 6e 65 78 74 "
              + "0b 0a 01 44 6e 6f 64 65 31 03 45 76 61 6c 75 65 37 03 08 17 03"), actual);
        assertNotNull(actual);
        assertTrue(actual.length > 0);
    }

    // Provenance: ObjectIdSerializationTest#testEmptyObjectWithId().
    void testEmptyObjectWithIdVpack() throws Exception {
        assertVPack(VPackWireFixtureTest.hex("0b 09 01 43 40 69 64 31 03"),
                MAPPER.writeValueAsBytes(new EmptyObject()));
    }

    // Provenance: ObjectIdSerializationTest#testSerializeWithOpaqueStringId().
    void testSerializeWithOpaqueStringIdVpack() throws Exception {
        StringIdentifiable first = new StringIdentifiable(12);
        StringIdentifiable second = new StringIdentifiable(34);
        first.next = second;
        second.next = first;
        byte[] encoded = MAPPER.writeValueAsBytes(first);
        assertTrue(encoded.length > 0);

        StringIdentifiable output = MAPPER.readValue(OPAQUE_LITERAL, StringIdentifiable.class);
        assertEquals(3, output.value);
        assertNotNull(output.next);
        assertEquals(5, output.next.value);
        assertSame(output.next.next, output);
    }

    // Provenance: ObjectIdSerializationTest#testMixedRefsIssue188().
    void testMixedRefsIssue188Vpack() throws Exception {
        Company company = new Company();
        Employee first = new Employee(1, "First", null);
        Employee second = new Employee(2, "Second", first);
        first.addReport(second);
        company.add(first);
        company.add(second);
        byte[] encoded = MAPPER.writeValueAsBytes(company);
        assertVPack(MIXED_REFS, encoded);
        assertTrue(encoded.length > 0);

        Company result = MAPPER.readValue(MIXED_REFS, Company.class);
        assertEquals(2, result.employees.size());
        assertSame(result.employees.get(0), result.employees.get(1).manager);
        assertSame(result.employees.get(1), result.employees.get(0).reports.get(0));
    }

    // Provenance: ObjectIdSerializationTest#testNoDuplicateKeysWithFieldLevelAnnotation().
    void testNoDuplicateKeysWithFieldLevelAnnotationVpack() throws Exception {
        Hive hive = new Hive(100500L, "main hive");
        hive.addBee(new Bee(1L, hive));
        byte[] encoded = MAPPER.writeValueAsBytes(hive);
        JsonNode tree = MAPPER.readerFor(JsonNode.class)
                .with(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY)
                .readValue(encoded);
        assertNotNull(tree);
        assertEquals(100500L, tree.path("id").longValue());
    }

    // Provenance: ObjectIdSerializationTest#testIncludePropertiesWithIdentityInfo3169().
    void testIncludePropertiesWithIdentityInfo3169Vpack() throws Exception {
        Device3169 device = new Device3169();
        device.deviceId = java.util.UUID.fromString("b16c3254-ee2e-11e7-8c3f-fa085a82f01f");
        device.name = "Thermostat";
        device.category = "HVAC";
        Config3169 config = new Config3169();
        config.device = device;
        config.deviceAgain = device;
        JsonNode tree = MAPPER.readTree(MAPPER.writeValueAsBytes(config));
        assertEquals("Thermostat", tree.path("device").path("name").textValue());
        assertTrue(tree.path("device").path("deviceId").isMissingNode());
    }

    // Provenance: ObjectIdSerializationTest#testInvalidProp().
    void testInvalidPropVpack() {
        InvalidDefinitionException exception = assertThrows(InvalidDefinitionException.class,
                () -> MAPPER.writeValueAsBytes(new Broken()));
        assertTrue(exception.getMessage().contains("cannot find property with name 'id'"));
    }
private static void assertVPack(byte[] expected, byte[] actual) {
        if (!java.util.Arrays.equals(expected, actual)) {
            throw new AssertionError("expected=" + toHex(expected) + " actual=" + toHex(actual));
        }
    }
private static String toHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte value : bytes) {
            if (result.length() != 0) result.append(' ');
            result.append(String.format("%02x", value & 0xff));
        }
        return result.toString();
    }
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "id")
    static class Identifiable { public int value; public Identifiable next; Identifiable() { } Identifiable(int value) { this.value = value; } }
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class, property = "id")
    static class StringIdentifiable { public int value; public StringIdentifiable next; StringIdentifiable() { } StringIdentifiable(int value) { this.value = value; } }
static class IdWrapper { @JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id") public ValueNode node; IdWrapper() { } IdWrapper(int value) { node = new ValueNode(value); } }
static class ValueNode { public int value; public IdWrapper next; ValueNode() { } ValueNode(int value) { this.value = value; } }
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
    static class EmptyObject { }
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    static class Broken { public int value; }
static class Company { public List<Employee> employees = new ArrayList<>(); void add(Employee employee) { employees.add(employee); } }
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    static class Employee { public int id; public String name; @JsonIdentityReference(alwaysAsId = true) public Employee manager; @JsonIdentityReference(alwaysAsId = true) public List<Employee> reports; Employee() { } Employee(int id, String name, Employee manager) { this.id = id; this.name = name; this.manager = manager; this.reports = new ArrayList<>(); } void addReport(Employee employee) { reports.add(employee); } }
static class Hive { public String name; public List<Bee> bees = new ArrayList<>(); public Long id; Hive() { } Hive(Long id, String name) { this.id = id; this.name = name; } void addBee(Bee bee) { bees.add(bee); } }
static class Bee { public Long id; @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id") @JsonIdentityReference(alwaysAsId = true) @JsonProperty("hiveId") Hive hive; Bee() { } Bee(Long id, Hive hive) { this.id = id; this.hive = hive; } }
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "deviceId")
    static class Device3169 { public java.util.UUID deviceId; public String name; public String category; }
static class Config3169 { @JsonIncludeProperties({"name"}) public Device3169 device; public Device3169 deviceAgain; }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
    @JsonSubTypes({ @JsonSubTypes.Type(value = EnumTypeDefinition.class, name = "enum"), @JsonSubTypes.Type(value = NumberTypeDefinition.class, name = "number") })
    @JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
    interface TypeDefinition { }
static class EnumTypeDefinition implements TypeDefinition { public List<String> values; }
static class NumberTypeDefinition implements TypeDefinition { }
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    static class POJO { private long id; private int var; public long getId() { return id; } public int getVar() { return var; } }
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonDeserialize(builder = POJOBuilder.class)
    @JsonPOJOBuilder(withPrefix = "", buildMethodName = "readFromCacheOrBuild")
    static class POJOBuilder { private long id; private int var; public POJOBuilder id(long id) { this.id = id; return this; } public POJOBuilder var(int var) { this.var = var; return this; } public POJO build() { POJO result = new POJO(); result.id = id; result.var = var; return result; } private static final ConcurrentHashMap<Long, POJO> cache = new ConcurrentHashMap<>(); public POJO readFromCacheOrBuild() { return cache.computeIfAbsent(id, key -> build()); } }

    void __invoke_testSimpleSerializationClassVpack() throws Exception {
        try {
            testSimpleSerializationClassVpack();
        } finally {
        }
    }


    void __invoke_testSimpleSerializationPropertyVpack() throws Exception {
        try {
            testSimpleSerializationPropertyVpack();
        } finally {
        }
    }


    void __invoke_testEmptyObjectWithIdVpack() throws Exception {
        try {
            testEmptyObjectWithIdVpack();
        } finally {
        }
    }


    void __invoke_testSerializeWithOpaqueStringIdVpack() throws Exception {
        try {
            testSerializeWithOpaqueStringIdVpack();
        } finally {
        }
    }


    void __invoke_testMixedRefsIssue188Vpack() throws Exception {
        try {
            testMixedRefsIssue188Vpack();
        } finally {
        }
    }


    void __invoke_testNoDuplicateKeysWithFieldLevelAnnotationVpack() throws Exception {
        try {
            testNoDuplicateKeysWithFieldLevelAnnotationVpack();
        } finally {
        }
    }


    void __invoke_testIncludePropertiesWithIdentityInfo3169Vpack() throws Exception {
        try {
            testIncludePropertiesWithIdentityInfo3169Vpack();
        } finally {
        }
    }


    void __invoke_testInvalidPropVpack() throws Exception {
        try {
            testInvalidPropVpack();
        } finally {
        }
    }

}
