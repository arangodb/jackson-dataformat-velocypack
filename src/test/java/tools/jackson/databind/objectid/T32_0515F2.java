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
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonPOJOBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0515F2 {
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

    // Provenance: ObjectIdWithBuilder1496Test#builderId1496().
    void builderId1496Vpack() throws Exception {
        POJO result = MAPPER.readValue(BUILDER_INPUT, POJO.class);
        assertNotNull(result);
        assertEquals(123L, result.getId());
        assertEquals(456, result.getVar());
        POJO source = new POJOBuilder().id(123L).var(456).build();
        POJO roundTrip = MAPPER.readValue(MAPPER.writeValueAsBytes(source), POJO.class);
        assertEquals(123L, roundTrip.getId());
        assertEquals(456, roundTrip.getVar());
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

    void __invoke_builderId1496Vpack() throws Exception {
        try {
            builderId1496Vpack();
        } finally {
        }
    }

}
