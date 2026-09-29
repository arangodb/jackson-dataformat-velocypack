package tools.jackson.databind.objectid;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import tools.jackson.core.JsonParser;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.deser.UnresolvedForwardReference;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0513Fixture {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] SIMPLE_CLASS = VPackWireFixtureTest.hex(
            "14 15 42 69 64 31 45 76 61 6c 75 65 28 0d "
          + "44 6e 65 78 74 31 03");
private static final byte[] SIMPLE_PROPERTY = VPackWireFixtureTest.hex(
            "14 25 44 6e 6f 64 65 14 1d 43 40 69 64 31 45 76 61 6c 75 65 37 "
          + "44 6e 65 78 74 14 09 44 6e 6f 64 65 31 01 03 01");
private static final byte[] SIMPLE_FORWARD = VPackWireFixtureTest.hex(
            "14 25 44 6e 6f 64 65 14 1d 45 76 61 6c 75 65 37 "
          + "44 6e 65 78 74 14 09 44 6e 6f 64 65 31 01 "
          + "43 40 69 64 31 03 01");
private static final byte[] RESOLVED_WRAPPER = VPackWireFixtureTest.hex(
            "14 25 44 6e 6f 64 65 14 1d 43 40 69 64 31 45 76 61 6c 75 65 37 "
          + "44 6e 65 78 74 14 09 44 6e 6f 64 65 31 01 03 01");
private static final byte[] UUID_1 = VPackWireFixtureTest.hex(
            "64 30 30 30 30 30 30 30 30 2d 30 30 30 30 2d 30 30 30 30 "
          + "2d 30 30 30 30 2d 30 30 30 30 30 30 30 30 30 30 30 31");
private static final byte[] UUID_2 = VPackWireFixtureTest.hex(
            "64 30 30 30 30 30 30 30 30 2d 30 30 30 30 2d 30 30 30 30 "
          + "2d 30 30 30 30 2d 30 30 30 30 30 30 30 30 30 30 30 32");
private static final byte[] UUID_3 = VPackWireFixtureTest.hex(
            "64 30 30 30 30 30 30 30 30 2d 30 30 30 30 2d 30 30 30 30 "
          + "2d 30 30 30 30 2d 30 30 30 30 30 30 30 30 30 30 30 33");
private static final byte[] UUID_GRAPH_ASSEMBLED = concat(
            VPackWireFixtureTest.hex("14 a5 02 41 23"), UUID_1,
            VPackWireFixtureTest.hex("45 76 61 6c 75 65 31 45 66 69 72 73 74"),
            VPackWireFixtureTest.hex("14 c1 01 41 23"), UUID_2,
            VPackWireFixtureTest.hex("45 76 61 6c 75 65 32 46 70 61 72 65 6e 74"), UUID_1,
            VPackWireFixtureTest.hex("45 66 69 72 73 74 14 5d 41 23"), UUID_3,
            VPackWireFixtureTest.hex("45 76 61 6c 75 65 33 46 70 61 72 65 6e 74"), UUID_1,
            VPackWireFixtureTest.hex("03 04 46 73 65 63 6f 6e 64"), UUID_3,
            VPackWireFixtureTest.hex("04"));
private static final byte[] UNRESOLVED_WRAPPER = VPackWireFixtureTest.hex(
            "14 25 44 6e 6f 64 65 14 1d 43 40 69 64 31 45 76 61 6c 75 65 37 "
          + "44 6e 65 78 74 14 09 44 6e 6f 64 65 32 01 03 01");
private static final byte[] UNRESOLVED_SCALAR = VPackWireFixtureTest.hex(
            "14 0a 44 6e 6f 64 65 28 7b 01");
private static final byte[] UNRESOLVED_COMPANY = VPackWireFixtureTest.hex(
            "14 5c 49 65 6d 70 6c 6f 79 65 65 73 13 4f "
          + "14 27 42 69 64 31 44 6e 61 6d 65 45 46 69 72 73 74 "
          + "47 6d 61 6e 61 67 65 72 18 47 72 65 70 6f 72 74 73 13 04 33 01 04 "
          + "14 25 42 69 64 32 44 6e 61 6d 65 46 53 65 63 6f 6e 64 "
          + "47 6d 61 6e 61 67 65 72 33 47 72 65 70 6f 72 74 73 01 04 02 01");
private static final byte[] ORDER_FIRST = VPackWireFixtureTest.hex(
            "13 40 14 3b 43 40 69 64 31 42 69 64 64 61 35 39 61 61 30 32 63 "
          + "2d 66 65 33 63 2d 34 33 66 38 2d 39 62 35 61 2d 35 66 65 30 31 38 37 38 61 38 31 38 "
          + "44 6e 61 6d 65 45 48 65 6c 6c 6f 03 31 31 03");
private static final byte[] ORDER_FORWARD = VPackWireFixtureTest.hex(
            "13 40 31 31 14 3b 43 40 69 64 31 42 69 64 64 61 35 39 61 61 30 32 63 "
          + "2d 66 65 33 63 2d 34 33 66 38 2d 39 62 35 61 2d 35 66 65 30 31 38 37 38 61 38 31 38 "
          + "44 6e 61 6d 65 45 48 65 6c 6c 6f 03 03");
private static final byte[] ORDER_ID_MIDDLE = VPackWireFixtureTest.hex(
            "13 40 14 3b 42 69 64 64 61 35 39 61 61 30 32 63 2d 66 65 33 63 "
          + "2d 34 33 66 38 2d 39 62 35 61 2d 35 66 65 30 31 38 37 38 61 38 31 38 "
          + "43 40 69 64 31 44 6e 61 6d 65 45 48 65 6c 6c 6f 03 31 31 03");
private static final byte[] ORDER_ID_LAST = VPackWireFixtureTest.hex(
            "13 40 14 3b 42 69 64 64 61 35 39 61 61 30 32 63 2d 66 65 33 63 "
          + "2d 34 33 66 38 2d 39 62 35 61 2d 35 66 65 30 31 38 37 38 61 38 31 38 "
          + "44 6e 61 6d 65 45 48 65 6c 6c 6f 43 40 69 64 31 03 31 31 03");
private static final byte[] UNRESOLVED_REORDERING = VPackWireFixtureTest.hex(
            "13 05 28 7b 01");

    // Provenance: ObjectIdDeserializationTest#testSimpleDeserializationClass().
    void testSimpleDeserializationClassVpack() throws Exception {
        Identifiable result = MAPPER.readValue(SIMPLE_CLASS, Identifiable.class);
        assertEquals(13, result.value);
        assertSame(result, result.next);
    }

    // Provenance: ObjectIdDeserializationTest#testSimpleUUIDForClassRoundTrip().
    void testSimpleUUIDForClassRoundTripVpack() throws Exception {
        UUIDNode result = MAPPER.readValue(UUID_GRAPH_ASSEMBLED, UUIDNode.class);
        assertEquals(1, result.value);
        UUIDNode result2 = result.first;
        UUIDNode result3 = result.second;
        assertNotNull(result2);
        assertNotNull(result3);
        assertEquals(2, result2.value);
        assertEquals(3, result3.value);
        assertSame(result, result2.parent);
        assertSame(result, result3.parent);
        assertSame(result3, result2.first);
    }

    // Provenance: ObjectIdDeserializationTest#testSimpleDeserializationProperty().
    void testSimpleDeserializationPropertyVpack() throws Exception {
        IdWrapper result = MAPPER.readValue(SIMPLE_PROPERTY, IdWrapper.class);
        assertEquals(7, result.node.value);
        assertSame(result.node, result.node.next.node);
    }

    // Provenance: ObjectIdDeserializationTest#testSimpleDeserWithForwardRefs().
    void testSimpleDeserWithForwardRefsVpack() throws Exception {
        IdWrapper result = MAPPER.readValue(SIMPLE_FORWARD, IdWrapper.class);
        assertEquals(7, result.node.value);
        assertSame(result.node, result.node.next.node);
    }

    // Provenance: ObjectIdDeserializationTest#testUnresolvedForwardReference().
    void testUnresolvedForwardReferenceVpack() throws Exception {
        UnresolvedForwardReference exception = assertThrows(UnresolvedForwardReference.class,
                () -> MAPPER.readValue(UNRESOLVED_COMPANY, Company.class));
        assertEquals(2, exception.getUnresolvedIds().size());
        assertEquals(3, exception.getUnresolvedIds().get(0).getId());
        assertEquals(3, exception.getUnresolvedIds().get(1).getId());
        assertEquals(Employee.class, exception.getUnresolvedIds().get(0).getType());
    }

    // Provenance: ObjectIdDeserializationTest#testUnresolvableAsNull().
    void testUnresolvableAsNullVpack() throws Exception {
        IdWrapper wrapper = VPackMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS).build()
                .readerFor(IdWrapper.class).readValue(UNRESOLVED_SCALAR);
        assertNotNull(wrapper);
        assertNull(wrapper.node);
    }

    // Provenance: ObjectIdDeserializationTest#testSuccessResolvedObjectIds().
    void testSuccessResolvedObjectIdsVpack() throws Exception {
        SomeWrapper wrapper = MAPPER.readValue(RESOLVED_WRAPPER, SomeWrapper.class);
        assertSame(wrapper.node, wrapper.node.next.node);
        assertSame(wrapper.node.next.node, wrapper.node.next.node.next.node);
    }

    // Provenance: ObjectIdDeserializationTest#testUnresolvedObjectIdsFailure().
    void testUnresolvedObjectIdsFailureVpack() throws Exception {
        ObjectMapper disabled = VPackMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS).build();
        SomeWrapper wrapper = disabled.readValue(UNRESOLVED_WRAPPER, SomeWrapper.class);
        assertNull(wrapper.node.next.node);
        assertThrows(UnresolvedForwardReference.class,
                () -> MAPPER.readValue(UNRESOLVED_WRAPPER, SomeWrapper.class));
        ObjectMapper enabled = VPackMapper.builder()
                .enable(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS).build();
        assertThrows(UnresolvedForwardReference.class,
                () -> enabled.readValue(UNRESOLVED_WRAPPER, SomeWrapper.class));
    }

    // Provenance: ObjectIdDeserializationTest#testOrdering1388().
    void testOrdering1388Vpack() throws Exception {
        assertOrdered(ORDER_FIRST);
        assertOrdered(ORDER_FORWARD);
        assertOrdered(ORDER_ID_MIDDLE);
        assertOrdered(ORDER_ID_LAST);
    }

    // Provenance: ObjectIdDeserializationTest#testUnresolvedObjectIdReordering().
    void testUnresolvedObjectIdReorderingVpack() {
        assertThrows(UnresolvedForwardReference.class,
                () -> MAPPER.readValue(UNRESOLVED_REORDERING,
                        new TypeReference<List<NamedThing>>() { }));
    }

    // Provenance: ObjectIdDeserializationTest#testObjectReaderWithDisabledFeature5542().
    void testObjectReaderWithDisabledFeature5542Vpack() throws Exception {
        ObjectReader reader = MAPPER.readerFor(ReaderWrapper5542.class)
                .without(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS);
        try (JsonParser parser = MAPPER.createParser(UNRESOLVED_WRAPPER)) {
            ReaderWrapper5542 wrapper = reader.readValue(parser);
            assertNotNull(wrapper);
            assertNotNull(wrapper.node);
            assertNull(wrapper.node.next.node);
        }
    }

    // Provenance: ObjectIdDeserializationTest#testObjectReaderWithResolvedObjectIds5542().
    void testObjectReaderWithResolvedObjectIds5542Vpack() throws Exception {
        ObjectReader reader = MAPPER.readerFor(ReaderWrapper5542.class);
        try (JsonParser parser = MAPPER.createParser(RESOLVED_WRAPPER)) {
            ReaderWrapper5542 wrapper = reader.readValue(parser);
            assertNotNull(wrapper);
            assertNotNull(wrapper.node);
            assertSame(wrapper.node, wrapper.node.next.node);
        }
    }
private static void assertOrdered(byte[] input) throws Exception {
        List<NamedThing> values = MAPPER.readValue(input,
                new TypeReference<List<NamedThing>>() { });
        assertEquals(3, values.size());
        assertSame(values.get(0), values.get(1));
        assertSame(values.get(0), values.get(2));
        assertEquals(UUID.fromString("a59aa02c-fe3c-43f8-9b5a-5fe01878a818"),
                values.get(0).getId());
        assertEquals("Hello", values.get(0).getName());
    }
private static byte[] concat(byte[]... parts) {
        int length = 0;
        for (byte[] part : parts) {
            length += part.length;
        }
        byte[] result = new byte[length];
        int offset = 0;
        for (byte[] part : parts) {
            System.arraycopy(part, 0, result, offset, part.length);
            offset += part.length;
        }
        return result;
    }
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class,
            property = "id")
    static class Identifiable {
        public int value;
        public Identifiable next;
    }
@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class, property = "#")
    static class UUIDNode {
        public int value;
        public UUIDNode parent;
        public UUIDNode first;
        public UUIDNode second;
    }
static class IdWrapper {
        @JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class,
                property = "@id")
        public ValueNode node;
    }
static class ValueNode {
        public int value;
        public IdWrapper next;
    }
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
    static class NamedThing {
        private final UUID id;
        private final String name;

        @JsonCreator
        public NamedThing(@JsonProperty("id") UUID id,
                @JsonProperty("name") String name) {
            this.id = id;
            this.name = name;
        }

        public UUID getId() { return id; }
        public String getName() { return name; }
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
static class Company { public List<Employee> employees; }
static class SomeWrapper {
        @JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class,
                property = "@id")
        public SomeNode node;
    }
static class SomeNode {
        public int value;
        public SomeWrapper next;
    }
static class ReaderWrapper5542 { public ReaderValueNode5542 node; }
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class,
            property = "@id")
    static class ReaderValueNode5542 {
        public int value;
        public ReaderWrapper5542 next;
    }

    void __invoke_testSimpleDeserializationClassVpack() throws Exception {
        try {
            testSimpleDeserializationClassVpack();
        } finally {
        }
    }


    void __invoke_testSimpleUUIDForClassRoundTripVpack() throws Exception {
        try {
            testSimpleUUIDForClassRoundTripVpack();
        } finally {
        }
    }


    void __invoke_testSimpleDeserializationPropertyVpack() throws Exception {
        try {
            testSimpleDeserializationPropertyVpack();
        } finally {
        }
    }


    void __invoke_testSimpleDeserWithForwardRefsVpack() throws Exception {
        try {
            testSimpleDeserWithForwardRefsVpack();
        } finally {
        }
    }


    void __invoke_testUnresolvedForwardReferenceVpack() throws Exception {
        try {
            testUnresolvedForwardReferenceVpack();
        } finally {
        }
    }


    void __invoke_testUnresolvableAsNullVpack() throws Exception {
        try {
            testUnresolvableAsNullVpack();
        } finally {
        }
    }


    void __invoke_testSuccessResolvedObjectIdsVpack() throws Exception {
        try {
            testSuccessResolvedObjectIdsVpack();
        } finally {
        }
    }


    void __invoke_testUnresolvedObjectIdsFailureVpack() throws Exception {
        try {
            testUnresolvedObjectIdsFailureVpack();
        } finally {
        }
    }


    void __invoke_testOrdering1388Vpack() throws Exception {
        try {
            testOrdering1388Vpack();
        } finally {
        }
    }


    void __invoke_testUnresolvedObjectIdReorderingVpack() throws Exception {
        try {
            testUnresolvedObjectIdReorderingVpack();
        } finally {
        }
    }


    void __invoke_testObjectReaderWithDisabledFeature5542Vpack() throws Exception {
        try {
            testObjectReaderWithDisabledFeature5542Vpack();
        } finally {
        }
    }


    void __invoke_testObjectReaderWithResolvedObjectIds5542Vpack() throws Exception {
        try {
            testObjectReaderWithResolvedObjectIds5542Vpack();
        } finally {
        }
    }

}
