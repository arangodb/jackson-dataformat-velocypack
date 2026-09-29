package tools.jackson.databind.objectid;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import tools.jackson.core.JsonParser;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.InjectableValues;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.deser.UnresolvedForwardReference;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0512Fixture {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] FORWARD_COLLECTION = VPackWireFixtureTest.hex(
            "14 5c 49 65 6d 70 6c 6f 79 65 65 73 13 4f "
          + "14 27 42 69 64 31 44 6e 61 6d 65 45 46 69 72 73 74 "
          + "47 6d 61 6e 61 67 65 72 18 47 72 65 70 6f 72 74 73 "
          + "13 04 32 01 04 "
          + "14 25 42 69 64 32 44 6e 61 6d 65 46 53 65 63 6f 6e 64 "
          + "47 6d 61 6e 61 67 65 72 31 47 72 65 70 6f 72 74 73 01 04 02 01");
private static final byte[] FORWARD_MAP = VPackWireFixtureTest.hex(
            "14 63 49 65 6d 70 6c 6f 79 65 65 73 14 56 41 31 "
          + "14 27 42 69 64 31 44 6e 61 6d 65 45 46 69 72 73 74 "
          + "47 6d 61 6e 61 67 65 72 18 47 72 65 70 6f 72 74 73 "
          + "13 04 32 01 04 "
          + "41 32 32 41 33 "
          + "14 25 42 69 64 32 44 6e 61 6d 65 46 53 65 63 6f 6e 64 "
          + "47 6d 61 6e 61 67 65 72 31 47 72 65 70 6f 72 74 73 01 04 03 01");
private static final byte[] FORWARD_ENUM_MAP = VPackWireFixtureTest.hex(
            "14 63 49 65 6d 70 6c 6f 79 65 65 73 14 56 41 41 "
          + "14 27 42 69 64 31 44 6e 61 6d 65 45 46 69 72 73 74 "
          + "47 6d 61 6e 61 67 65 72 18 47 72 65 70 6f 72 74 73 "
          + "13 04 32 01 04 "
          + "41 42 32 41 43 "
          + "14 25 42 69 64 32 44 6e 61 6d 65 46 53 65 63 6f 6e 64 "
          + "47 6d 61 6e 61 67 65 72 31 47 72 65 70 6f 72 74 73 01 04 03 01");
private static final byte[] KEEP_COLLECTION_ORDER = VPackWireFixtureTest.hex(
            "14 5e 49 65 6d 70 6c 6f 79 65 65 73 13 51 32 31 "
          + "14 27 42 69 64 31 44 6e 61 6d 65 45 46 69 72 73 74 "
          + "47 6d 61 6e 61 67 65 72 18 47 72 65 70 6f 72 74 73 "
          + "13 04 32 01 04 "
          + "14 25 42 69 64 32 44 6e 61 6d 65 46 53 65 63 6f 6e 64 "
          + "47 6d 61 6e 61 67 65 72 31 47 72 65 70 6f 72 74 73 01 04 04 01");
private static final byte[] KEEP_MAP_ORDER = VPackWireFixtureTest.hex(
            "14 66 49 65 6d 70 6c 6f 79 65 65 73 14 59 41 31 32 41 32 31 41 33 "
          + "14 27 42 69 64 31 44 6e 61 6d 65 45 46 69 72 73 74 "
          + "47 6d 61 6e 61 67 65 72 18 47 72 65 70 6f 72 74 73 "
          + "13 04 32 01 04 41 34 "
          + "14 25 42 69 64 32 44 6e 61 6d 65 46 53 65 63 6f 6e 64 "
          + "47 6d 61 6e 61 67 65 72 31 47 72 65 70 6f 72 74 73 01 04 04 01");
private static final byte[] MISSING_ID = VPackWireFixtureTest.hex(
            "14 1b 45 76 61 6c 75 65 28 1c 44 6e 65 78 74 "
          + "14 0b 45 76 61 6c 75 65 28 1d 01 02");
private static final byte[] NULL_ID = VPackWireFixtureTest.hex(
            "14 14 45 76 61 6c 75 65 33 44 6e 65 78 74 18 "
          + "42 69 64 18 03");
private static final byte[] NULLS_NO_ID = VPackWireFixtureTest.hex("13 04 18 01");
private static final byte[] INJECTABLE_CYCLE = VPackWireFixtureTest.hex(
            "14 15 43 40 69 64 31 41 62 "
          + "14 0b 43 40 69 64 32 41 61 31 02 02");
private static final byte[] UNRESOLVED_WRAPPER = VPackWireFixtureTest.hex(
            "14 25 44 6e 6f 64 65 "
          + "14 1d 43 40 69 64 31 45 76 61 6c 75 65 37 "
          + "44 6e 65 78 74 14 09 44 6e 6f 64 65 32 01 03 01");

    // Provenance: ObjectIdDeserializationTest#testForwardReferenceInCollection().
    void testForwardReferenceInCollectionVpack() throws Exception {
        Company company = MAPPER.readValue(FORWARD_COLLECTION, Company.class);
        assertEquals(2, company.employees.size());
        assertEmployees(company.employees.get(0), company.employees.get(1));
    }

    // Provenance: ObjectIdDeserializationTest#testForwardReferenceInMap().
    void testForwardReferenceInMapVpack() throws Exception {
        MappedCompany company = MAPPER.readValue(FORWARD_MAP, MappedCompany.class);
        assertEquals(3, company.employees.size());
        assertEmployees(company.employees.get(1), company.employees.get(3));
    }

    // Provenance: ObjectIdDeserializationTest#testForwardReferenceInEnumMap().
    void testForwardReferenceInEnumMapVpack() throws Exception {
        EnumMapCompany company = MAPPER.readValue(FORWARD_ENUM_MAP, EnumMapCompany.class);
        assertEquals(3, company.employees.size());
        assertEmployees(company.employees.get(FooEnum.A), company.employees.get(FooEnum.B));
    }

    // Provenance: ObjectIdDeserializationTest#testKeepCollectionOrdering().
    void testKeepCollectionOrderingVpack() throws Exception {
        Company company = MAPPER.readValue(KEEP_COLLECTION_ORDER, Company.class);
        assertEquals(4, company.employees.size());
        Employee first = company.employees.get(1);
        Employee second = company.employees.get(0);
        assertSame(first, company.employees.get(2));
        assertSame(second, company.employees.get(3));
        assertEmployees(first, second);
    }

    // Provenance: ObjectIdDeserializationTest#testKeepMapOrdering().
    void testKeepMapOrderingVpack() throws Exception {
        MappedCompany company = MAPPER.readValue(KEEP_MAP_ORDER, MappedCompany.class);
        assertEquals(4, company.employees.size());
        assertEmployees(company.employees.get(2), company.employees.get(1));
    }

    // Provenance: ObjectIdDeserializationTest#testMissingObjectId().
    void testMissingObjectIdVpack() throws Exception {
        Identifiable value = MAPPER.readValue(MISSING_ID, Identifiable.class);
        assertNotNull(value);
        assertEquals(28, value.value);
        assertNotNull(value.next);
        assertEquals(29, value.next.value);
    }

    // Provenance: ObjectIdDeserializationTest#testNullObjectId().
    void testNullObjectIdVpack() throws Exception {
        Identifiable value = MAPPER.readValue(NULL_ID, Identifiable.class);
        assertNotNull(value);
        assertEquals(3, value.value);
    }

    // Provenance: ObjectIdDeserializationTest#testNullStringPropertyId().
    void testNullStringPropertyIdVpack() throws Exception {
        IdentifiableStringId value = MAPPER.readValue(NULL_ID, IdentifiableStringId.class);
        assertNotNull(value);
        assertEquals(3, value.value);
    }

    // Provenance: ObjectIdDeserializationTest#testNullsNoObjectId().
    void testNullsNoObjectIdVpack() throws Exception {
        List<NamedThing> value = MAPPER.readValue(NULLS_NO_ID,
                new TypeReference<List<NamedThing>>() { });
        assertEquals(1, value.size());
        assertNull(value.get(0));
    }

    // Provenance: ObjectIdDeserializationTest#testObjectIdWithInjectables().
    void testObjectIdWithInjectablesVpack() throws Exception {
        InjectableValues.Std inject = new InjectableValues.Std()
                .addValue("i1", "e1")
                .addValue("i2", "e2");
        InjectableA output = MAPPER.reader(inject).forType(InjectableA.class)
                .readValue(INJECTABLE_CYCLE);
        assertNotNull(output);
        assertNotNull(output.b);
        assertSame(output, output.b.a);
    }

    // Provenance: ObjectIdDeserializationTest#testObjectMapperFailsOnUnresolvedObjectIds5542().
    void testObjectMapperFailsOnUnresolvedObjectIds5542Vpack() {
        assertThrows(UnresolvedForwardReference.class,
                () -> MAPPER.readValue(UNRESOLVED_WRAPPER, ReaderWrapper5542.class));
    }

    // Provenance: ObjectIdDeserializationTest#testObjectReaderFailsOnUnresolvedObjectIds5542().
    void testObjectReaderFailsOnUnresolvedObjectIds5542Vpack() {
        ObjectReader reader = MAPPER.readerFor(ReaderWrapper5542.class);
        assertThrows(UnresolvedForwardReference.class, () -> {
            try (JsonParser parser = MAPPER.createParser(UNRESOLVED_WRAPPER)) {
                reader.readValue(parser);
            }
        });
    }
private static void assertEmployees(Employee first, Employee second) {
        assertEquals(1, first.id);
        assertEquals(2, second.id);
        assertEquals(1, first.reports.size());
        assertSame(second, first.reports.get(0));
        assertSame(first, second.manager);
    }
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "id")
    static class Identifiable {
        public int value;
        public Identifiable next;
    }
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    static class IdentifiableStringId {
        public String id;
        public int value;
        public Identifiable next;
    }
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
    static class NamedThing { public String value; }
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    static class Employee {
        public int id;
        public String name;
        @JsonIdentityReference(alwaysAsId = true)
        public Employee manager;
        @JsonIdentityReference(alwaysAsId = true)
        public List<Employee> reports;
    }
static class Company { public List<Employee> employees; }
static class MappedCompany { public Map<Integer, Employee> employees; }
static class EnumMapCompany { public EnumMap<FooEnum, Employee> employees; }
enum FooEnum { A, B, C }
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
    static class InjectableA {
        public InjectableB b;
        public InjectableA(@JacksonInject("i1") String ignored) { }
    }
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
    static class InjectableB {
        public InjectableA a;
        @JsonCreator
        public InjectableB(@JacksonInject("i2") String ignored) { }
    }
static class ReaderWrapper5542 { public ReaderValueNode5542 node; }
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
    static class ReaderValueNode5542 {
        public int value;
        public ReaderWrapper5542 next;
    }

    void __invoke_testForwardReferenceInCollectionVpack() throws Exception {
        try {
            testForwardReferenceInCollectionVpack();
        } finally {
        }
    }


    void __invoke_testForwardReferenceInMapVpack() throws Exception {
        try {
            testForwardReferenceInMapVpack();
        } finally {
        }
    }


    void __invoke_testForwardReferenceInEnumMapVpack() throws Exception {
        try {
            testForwardReferenceInEnumMapVpack();
        } finally {
        }
    }


    void __invoke_testKeepCollectionOrderingVpack() throws Exception {
        try {
            testKeepCollectionOrderingVpack();
        } finally {
        }
    }


    void __invoke_testKeepMapOrderingVpack() throws Exception {
        try {
            testKeepMapOrderingVpack();
        } finally {
        }
    }


    void __invoke_testMissingObjectIdVpack() throws Exception {
        try {
            testMissingObjectIdVpack();
        } finally {
        }
    }


    void __invoke_testNullObjectIdVpack() throws Exception {
        try {
            testNullObjectIdVpack();
        } finally {
        }
    }


    void __invoke_testNullStringPropertyIdVpack() throws Exception {
        try {
            testNullStringPropertyIdVpack();
        } finally {
        }
    }


    void __invoke_testNullsNoObjectIdVpack() throws Exception {
        try {
            testNullsNoObjectIdVpack();
        } finally {
        }
    }


    void __invoke_testObjectIdWithInjectablesVpack() throws Exception {
        try {
            testObjectIdWithInjectablesVpack();
        } finally {
        }
    }


    void __invoke_testObjectMapperFailsOnUnresolvedObjectIds5542Vpack() throws Exception {
        try {
            testObjectMapperFailsOnUnresolvedObjectIds5542Vpack();
        } finally {
        }
    }


    void __invoke_testObjectReaderFailsOnUnresolvedObjectIds5542Vpack() throws Exception {
        try {
            testObjectReaderFailsOnUnresolvedObjectIds5542Vpack();
        } finally {
        }
    }

}
