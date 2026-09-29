package tools.jackson.databind.objectid;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerator.IdKey;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.ContextAttributes;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0511F1 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] CONTAINER_FIRST = VPackWireFixtureTest.hex(
            "13 46 "
          + "14 41 46 40 63 6c 61 73 73 49 63 6f 6e 74 61 69 6e 65 72 "
          + "42 69 64 28 01 44 75 73 65 72 "
          + "14 23 46 40 63 6c 61 73 73 44 75 73 65 72 "
          + "42 69 64 28 2a 45 6c 6f 67 69 6e 48 63 6f 6f 6c 5f 6d 61 6e 03 03 "
          + "28 2a 02");
private static final byte[] USER_FIRST = VPackWireFixtureTest.hex(
            "13 46 "
          + "14 23 46 40 63 6c 61 73 73 44 75 73 65 72 "
          + "42 69 64 28 2a 45 6c 6f 67 69 6e 48 63 6f 6f 6c 5f 6d 61 6e 03 "
          + "14 20 46 40 63 6c 61 73 73 49 63 6f 6e 74 61 69 6e 65 72 "
          + "42 69 64 28 01 44 75 73 65 72 28 2a 03 02");
private static final byte[] SIMPLE_825 = VPackWireFixtureTest.hex(
            "14 2c 46 40 63 6c 61 73 73 44 72 6f 6f 74 "
          + "41 64 14 1b 46 40 63 6c 61 73 73 41 64 "
          + "49 6f 69 64 53 74 72 69 6e 67 44 6f 69 64 44 02 02");
private static final byte[] ATOMIC_REFERENCE = VPackWireFixtureTest.hex(
            "14 36 45 66 69 72 73 74 14 2d 42 69 64 31 "
          + "44 6e 61 6d 65 45 41 6c 69 63 65 44 6e 65 78 74 "
          + "14 16 42 69 64 32 44 6e 61 6d 65 43 42 6f 62 "
          + "44 6e 65 78 74 31 03 03 01");
private static final byte[] CUSTOM_CLASS = VPackWireFixtureTest.hex(
            "14 1e 48 63 75 73 74 6f 6d 49 64 28 7b 45 76 61 6c 75 65 "
          + "21 7c fc 44 6e 65 78 74 28 7b 03");
private static final byte[] CUSTOM_PROPERTY = VPackWireFixtureTest.hex(
            "14 2b 44 6e 6f 64 65 14 23 48 63 75 73 74 6f 6d 49 64 33 "
          + "45 76 61 6c 75 65 28 63 44 6e 65 78 74 14 09 44 6e 6f 64 65 33 01 03 01");
private static final byte[] CUSTOM_POOL = VPackWireFixtureTest.hex(
            "14 10 44 64 61 74 61 13 08 31 32 33 34 35 05 01");
private static final byte[] COMPANY_FORWARD = VPackWireFixtureTest.hex(
            "14 5d 49 65 6d 70 6c 6f 79 65 65 73 13 50 "
          + "14 25 42 69 64 31 44 6e 61 6d 65 45 46 69 72 73 74 "
          + "47 6d 61 6e 61 67 65 72 28 02 47 72 65 70 6f 72 74 73 01 04 "
          + "14 28 42 69 64 32 44 6e 61 6d 65 46 53 65 63 6f 6e 64 "
          + "47 6d 61 6e 61 67 65 72 18 47 72 65 70 6f 72 74 73 13 04 31 01 04 02 01");
private static final byte[] FORWARD_AND_UNRESOLVED = VPackWireFixtureTest.hex(
            "14 ad 01 45 6f 77 6e 65 64 13 85 01 "
          + "14 20 44 6e 61 6d 65 44 76 46 6f 6f 4d 6f 70 74 69 6f 6e 61 6c 56 61 6c 75 65 44 76 46 6f 6f 02 "
          + "14 27 44 6e 61 6d 65 43 62 61 72 4d 6f 70 74 69 6f 6e 61 6c 56 61 6c 75 65 4c 6e 6f 74 41 56 61 6c 69 64 52 65 66 02 "
          + "14 0c 44 6e 61 6d 65 43 62 61 7a 01 "
          + "14 2e 44 6e 61 6d 65 43 71 75 78 4d 6f 70 74 69 6f 6e 61 6c 56 61 6c 75 65 "
          + "14 14 44 6e 61 6d 65 44 76 51 75 78 45 76 61 6c 75 65 33 02 02 "
          + "04 46 76 61 6c 75 65 73 13 17 "
          + "14 14 44 6e 61 6d 65 44 76 46 6f 6f 45 76 61 6c 75 65 31 02 01 02");

    // Provenance: ObjectIdDeserializationTest#testAtomicReferenceWithObjectId().
    void testAtomicReferenceWithObjectIdVpack() throws Exception {
        LinkedEmployeeList result = MAPPER.readValue(ATOMIC_REFERENCE,
                LinkedEmployeeList.class);
        LinkedEmployee first = result.first.get();
        assertEquals("Alice", first.name);
        assertEquals("Bob", first.next.get().name);
        assertSame(first, first.next.get().next.get());
    }

    // Provenance: ObjectIdDeserializationTest#testCustomDeserializationClass().
    void testCustomDeserializationClassVpack() throws Exception {
        IdentifiableCustom result = MAPPER.readValue(CUSTOM_CLASS, IdentifiableCustom.class);
        assertEquals(-900, result.value);
        assertSame(result, result.next);
    }

    // Provenance: ObjectIdDeserializationTest#testCustomDeserializationProperty().
    void testCustomDeserializationPropertyVpack() throws Exception {
        IdWrapperExt result = MAPPER.readValue(CUSTOM_PROPERTY, IdWrapperExt.class);
        assertEquals(99, result.node.value);
        assertSame(result.node, result.node.next.node);
        assertEquals(3, result.node.customId);
    }

    // Provenance: ObjectIdDeserializationTest#testCustomPoolResolver().
    void testCustomPoolResolverVpack() throws Exception {
        Map<Object, WithCustomResolution> pool = Map.of(
                1, new WithCustomResolution(1, 1), 2, new WithCustomResolution(2, 2),
                3, new WithCustomResolution(3, 3), 4, new WithCustomResolution(4, 4),
                5, new WithCustomResolution(5, 5));
        ContextAttributes attrs = MAPPER.deserializationConfig().getAttributes()
                .withSharedAttribute("POOL", pool);
        CustomResolutionWrapper wrapper = MAPPER.readerFor(CustomResolutionWrapper.class)
                .with(attrs).readValue(CUSTOM_POOL);
        assertEquals(5, wrapper.data.size());
        for (WithCustomResolution value : wrapper.data) {
            assertSame(pool.get(value.id), value);
        }
    }

    // Provenance: ObjectIdDeserializationTest#testDefaultSetting().
    void testDefaultSettingVpack() {
        ObjectMapper enabled = new VPackMapper();
        ObjectMapper disabled = VPackMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS).build();
        assertTrue(enabled.isEnabled(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS));
        assertFalse(disabled.isEnabled(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS));
    }

    // Provenance: ObjectIdDeserializationTest#testForwardReference().
    void testForwardReferenceVpack() throws Exception {
        Company company = MAPPER.readValue(COMPANY_FORWARD, Company.class);
        Employee first = company.employees.get(0);
        Employee second = company.employees.get(1);
        assertEquals(2, company.employees.size());
        assertSame(second, first.manager);
        assertSame(first, second.reports.get(0));
    }

    // Provenance: ObjectIdDeserializationTest#testForwardAndUnresolvedScalarObjectIds2955().
    void testForwardAndUnresolvedScalarObjectIds2955Vpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS).build();
        Owner2955 owner = mapper.readValue(FORWARD_AND_UNRESOLVED, Owner2955.class);
        assertEquals(4, owner.owned.size());
        assertEquals(Integer.valueOf(1), owner.owned.get(0).optionalValue.value);
        assertNull(owner.owned.get(1).optionalValue);
        assertNull(owner.owned.get(2).optionalValue);
        assertEquals(Integer.valueOf(3), owner.owned.get(3).optionalValue.value);
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY,
            property = "@class")
    @JsonSubTypes({
        @JsonSubTypes.Type(value = User2780.class, name = "user"),
        @JsonSubTypes.Type(value = Container2780.class, name = "container") })
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    static abstract class Entity2780 {
        public int id;
    }
static class User2780 extends Entity2780 {
        public String login;
    }
static class Container2780 extends Entity2780 {
        public User2780 user;
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY,
            property = "@class")
    @JsonSubTypes({
        @JsonSubTypes.Type(value = Root825.class, name = "root"),
        @JsonSubTypes.Type(value = D825.class, name = "d") })
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "oidString")
    static abstract class Base825 {
        public String oidString;
    }
static class Root825 extends Base825 {
        public D825 d;
    }
static class D825 extends Base825 { }
@JsonIdentityInfo(property = "id", generator = ObjectIdGenerators.PropertyGenerator.class)
    static class LinkedEmployee {
        public int id;
        public String name;
        public AtomicReference<LinkedEmployee> next;
    }
static class LinkedEmployeeList {
        public AtomicReference<LinkedEmployee> first;
    }
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "customId")
    static class IdentifiableCustom {
        public int value;
        public int customId;
        public IdentifiableCustom next;
    }
static class IdWrapperExt {
        @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "customId")
        public ValueNodeExt node;
    }
static class ValueNodeExt {
        public int value;
        protected int customId;
        public IdWrapperExt next;
        public void setCustomId(int value) { customId = value; }
    }
static class CustomResolutionWrapper {
        public List<WithCustomResolution> data;
    }
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id",
            resolver = PoolResolver.class)
    @JsonIdentityReference(alwaysAsId = true)
    static class WithCustomResolution {
        public int id;
        public int data;
        WithCustomResolution(int id, int data) { this.id = id; this.data = data; }
    }
public static class PoolResolver implements com.fasterxml.jackson.annotation.ObjectIdResolver {
        private Map<Object, WithCustomResolution> pool;
        @Override public void bindItem(IdKey id, Object pojo) { }
        @Override public Object resolveId(IdKey id) { return pool.get(id.key); }
        @Override public boolean canUseFor(com.fasterxml.jackson.annotation.ObjectIdResolver other) {
            return other.getClass() == getClass() && pool != null && !pool.isEmpty();
        }
        @Override public com.fasterxml.jackson.annotation.ObjectIdResolver newForDeserialization(Object context) {
            tools.jackson.databind.DeserializationContext ctxt =
                    (tools.jackson.databind.DeserializationContext) context;
            @SuppressWarnings("unchecked")
            Map<Object, WithCustomResolution> value =
                    (Map<Object, WithCustomResolution>) ctxt.getAttribute("POOL");
            PoolResolver resolver = new PoolResolver();
            resolver.pool = value;
            return resolver;
        }
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
static class Company {
        public List<Employee> employees;
    }
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "name",
            scope = Value2955.class)
    static class Value2955 {
        public String name;
        public Integer value;
    }
static class Owned2955 {
        public String name;
        public Value2955 optionalValue;
    }
static class Owner2955 {
        public List<Owned2955> owned = new ArrayList<>();
        public List<Value2955> values = new ArrayList<>();
    }

    void __invoke_testAtomicReferenceWithObjectIdVpack() throws Exception {
        try {
            testAtomicReferenceWithObjectIdVpack();
        } finally {
        }
    }


    void __invoke_testCustomDeserializationClassVpack() throws Exception {
        try {
            testCustomDeserializationClassVpack();
        } finally {
        }
    }


    void __invoke_testCustomDeserializationPropertyVpack() throws Exception {
        try {
            testCustomDeserializationPropertyVpack();
        } finally {
        }
    }


    void __invoke_testCustomPoolResolverVpack() throws Exception {
        try {
            testCustomPoolResolverVpack();
        } finally {
        }
    }


    void __invoke_testDefaultSettingVpack() throws Exception {
        try {
            testDefaultSettingVpack();
        } finally {
        }
    }


    void __invoke_testForwardReferenceVpack() throws Exception {
        try {
            testForwardReferenceVpack();
        } finally {
        }
    }


    void __invoke_testForwardAndUnresolvedScalarObjectIds2955Vpack() throws Exception {
        try {
            testForwardAndUnresolvedScalarObjectIds2955Vpack();
        } finally {
        }
    }

}
