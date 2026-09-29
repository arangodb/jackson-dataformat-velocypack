package tools.jackson.databind.objectid;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonPOJOBuilder;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.exc.InvalidDefinitionException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0516F0 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] PURE_BACK_REFERENCE = VPackWireFixtureTest.hex(
            "14 2e 48 65 6e 74 69 74 69 65 73 13 22 "
          + "14 0d 42 69 64 31 44 72 65 66 73 01 02 "
          + "14 12 42 69 64 32 43 72 65 66 31 44 72 65 66 73 01 03 02 01");
private static final byte[] FORWARD_REFERENCE = VPackWireFixtureTest.hex(
            "14 2e 48 65 6e 74 69 74 69 65 73 13 22 "
          + "14 12 42 69 64 31 43 72 65 66 32 44 72 65 66 73 01 03 "
          + "14 0d 42 69 64 32 44 72 65 66 73 01 02 02 01");
private static final byte[] FORWARD_COLLECTION = VPackWireFixtureTest.hex(
            "14 31 48 65 6e 74 69 74 69 65 73 13 25 "
          + "14 10 42 69 64 31 44 72 65 66 73 13 04 32 01 02 "
          + "14 12 42 69 64 32 43 72 65 66 31 44 72 65 66 73 01 03 02 01");
private static final byte[] DUPLICATE_FORWARD_LIST = VPackWireFixtureTest.hex(
            "14 2e 48 65 6e 74 69 74 69 65 73 13 22 "
          + "14 12 42 69 64 31 44 72 65 66 73 13 06 32 32 32 03 02 "
          + "14 0d 42 69 64 32 44 72 65 66 73 01 02 02 01");
private static final byte[] MULTIPLE_FORWARD_LIST = VPackWireFixtureTest.hex(
            "14 3a 48 65 6e 74 69 74 69 65 73 13 2e "
          + "14 11 42 69 64 31 44 72 65 66 73 13 05 32 33 02 02 "
          + "14 0d 42 69 64 32 44 72 65 66 73 01 02 "
          + "14 0d 42 69 64 33 44 72 65 66 73 01 02 03 01");
private static final byte[] FORWARD_MAP = VPackWireFixtureTest.hex(
            "14 2e 48 65 6e 74 69 74 69 65 73 13 22 "
          + "14 12 42 69 64 31 44 72 65 66 73 14 06 41 6b 32 01 02 "
          + "14 0d 42 69 64 32 44 72 65 66 73 0a 02 02 01");
private static final byte[] MULTIPLE_FORWARD_MAP = VPackWireFixtureTest.hex(
            "14 41 48 65 6e 74 69 74 69 65 73 13 35 "
          + "14 18 42 69 64 31 44 72 65 66 73 14 0c "
          + "41 61 32 41 62 33 41 63 32 03 02 "
          + "14 0d 42 69 64 32 44 72 65 66 73 0a 02 "
          + "14 0d 42 69 64 33 44 72 65 66 73 0a 02 03 01");
private static final byte[] MULTIPLE_FORWARD_SET = MULTIPLE_FORWARD_LIST;
private static final byte[] PROPERTY_CREATOR = FORWARD_COLLECTION;
private static final byte[] TYPED_ARRAY = FORWARD_COLLECTION;
private static final byte[] DELEGATING_CREATOR = VPackWireFixtureTest.hex(
            "14 38 48 65 6e 74 69 74 69 65 73 13 2c "
          + "14 16 43 40 69 64 31 42 69 64 28 0a 44 72 65 66 73 13 04 32 01 03 "
          + "14 13 43 40 69 64 32 42 69 64 28 14 44 72 65 66 73 01 03 02 01");

    // Provenance: ObjectIdWithBuilder1496Test#testPureBackReference().
    void testPureBackReferenceVpack() throws Exception {
        Entity1496Container container = MAPPER.readValue(PURE_BACK_REFERENCE,
                Entity1496Container.class);
        assertNotNull(container);
        assertEquals(2, container.entities.size());
        Entity1496 first = container.entities.get(0);
        Entity1496 second = container.entities.get(1);
        assertEquals(1, first.getId());
        assertEquals(2, second.getId());
        assertSame(first, second.getRef());
    }

    // Provenance: ObjectIdWithBuilder1496Test#forwardReferenceReportsClearError().
    void forwardReferenceReportsClearErrorVpack() {
        InvalidDefinitionException exception = assertThrows(InvalidDefinitionException.class,
                () -> MAPPER.readValue(FORWARD_REFERENCE, Entity1496Container.class));
        assertTrue(exception.getMessage().contains("Cannot resolve forward Object Id references"));
        assertTrue(exception.getMessage().contains("Builder-based"));
    }
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonDeserialize(builder = Entity1496Builder.class)
    static class Entity1496 {
        private final long id;
        private final Entity1496 ref;
        private final List<Entity1496> refs;
        Entity1496(long id, Entity1496 ref, List<Entity1496> refs) {
            this.id = id; this.ref = ref; this.refs = refs;
        }
        public long getId() { return id; }
        public Entity1496 getRef() { return ref; }
    }
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonPOJOBuilder(withPrefix = "")
    static class Entity1496Builder {
        private long id;
        private Entity1496 ref;
        private List<Entity1496> refs;
        public Entity1496Builder id(long value) { id = value; return this; }
        public Entity1496Builder ref(Entity1496 value) { ref = value; return this; }
        public Entity1496Builder refs(List<Entity1496> value) { refs = value; return this; }
        public Entity1496 build() { return new Entity1496(id, ref, refs); }
    }
static class Entity1496Container { public List<Entity1496> entities; }
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonDeserialize(builder = Entity5909Builder.class)
    static class Entity5909 {
        private final long id;
        private final Entity5909 ref;
        private final List<Entity5909> refs;
        Entity5909(long id, Entity5909 ref, List<Entity5909> refs) {
            this.id = id; this.ref = ref; this.refs = refs;
        }
        public long getId() { return id; }
        public Entity5909 getRef() { return ref; }
        public List<Entity5909> getRefs() { return refs; }
    }
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonPOJOBuilder(withPrefix = "")
    static class Entity5909Builder {
        private long id; private Entity5909 ref; private List<Entity5909> refs;
        public Entity5909Builder id(long value) { id = value; return this; }
        public Entity5909Builder ref(Entity5909 value) { ref = value; return this; }
        public Entity5909Builder refs(List<Entity5909> value) { refs = value; return this; }
        public Entity5909 build() { return new Entity5909(id, ref, refs); }
    }
static class Entity5909Container { public List<Entity5909> entities; }
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonDeserialize(builder = EntitySetBuilder.class)
    static class EntitySet {
        private final long id; private final Set<EntitySet> refs;
        EntitySet(long id, Set<EntitySet> refs) { this.id = id; this.refs = refs; }
        public long getId() { return id; }
        public Set<EntitySet> getRefs() { return refs; }
    }
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonPOJOBuilder(withPrefix = "")
    static class EntitySetBuilder {
        private long id; private Set<EntitySet> refs;
        public EntitySetBuilder id(long value) { id = value; return this; }
        public EntitySetBuilder refs(Set<EntitySet> value) { refs = value; return this; }
        public EntitySet build() { return new EntitySet(id, refs == null ? new LinkedHashSet<>() : refs); }
    }
static class EntitySetContainer { public List<EntitySet> entities; }
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonDeserialize(builder = EntityMapBuilder.class)
    static class EntityMap {
        private final long id; private final Map<String, EntityMap> refs;
        EntityMap(long id, Map<String, EntityMap> refs) { this.id = id; this.refs = refs; }
        public long getId() { return id; }
        public Map<String, EntityMap> getRefs() { return refs; }
    }
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonPOJOBuilder(withPrefix = "")
    static class EntityMapBuilder {
        private long id; private Map<String, EntityMap> refs;
        public EntityMapBuilder id(long value) { id = value; return this; }
        public EntityMapBuilder refs(Map<String, EntityMap> value) { refs = value; return this; }
        public EntityMap build() { return new EntityMap(id, refs == null ? new LinkedHashMap<>() : refs); }
    }
static class EntityMapContainer { public List<EntityMap> entities; }
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonDeserialize(builder = CreatorBuilder.class)
    static class CreatorEntity { public final long id; public final List<CreatorEntity> refs;
        CreatorEntity(long id, List<CreatorEntity> refs) { this.id = id; this.refs = refs; } }
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonPOJOBuilder(withPrefix = "")
    static class CreatorBuilder {
        private final long id; private final List<CreatorEntity> refs;
        @JsonCreator CreatorBuilder(@JsonProperty("id") long id, @JsonProperty("refs") List<CreatorEntity> refs) {
            this.id = id; this.refs = refs;
        }
        public CreatorEntity build() { return new CreatorEntity(id, refs); }
    }
static class CreatorContainer { public List<CreatorEntity> entities; }
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonDeserialize(builder = ArrayBuilder.class)
    static class ArrayEntity { public final long id; public final ArrayEntity[] refs;
        ArrayEntity(long id, ArrayEntity[] refs) { this.id = id; this.refs = refs; } }
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonPOJOBuilder(withPrefix = "")
    static class ArrayBuilder {
        private long id; private ArrayEntity[] refs;
        public ArrayBuilder id(long value) { id = value; return this; }
        public ArrayBuilder refs(ArrayEntity[] value) { refs = value; return this; }
        public ArrayEntity build() { return new ArrayEntity(id, refs); }
    }
static class ArrayContainer { public List<ArrayEntity> entities; }
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
    @JsonSerialize(as = ImmutableEntity.class)
    @JsonDeserialize(as = ImmutableEntity.class)
    interface DelegatingEntity { long getId(); List<DelegatingEntity> getRefs(); }
static class ImmutableEntity implements DelegatingEntity {
        private final long id; private final List<DelegatingEntity> refs;
        ImmutableEntity(long id, List<DelegatingEntity> refs) { this.id = id; this.refs = refs; }
        public long getId() { return id; }
        public List<DelegatingEntity> getRefs() { return refs; }
        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        static ImmutableEntity fromJson(MutableEntity value) { return new ImmutableEntity(value.id, value.refs); }
    }
@JsonDeserialize
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    static class MutableEntity implements DelegatingEntity {
        public long id; public List<DelegatingEntity> refs;
        public long getId() { throw new UnsupportedOperationException(); }
        public List<DelegatingEntity> getRefs() { throw new UnsupportedOperationException(); }
    }
static class DelegatingContainer { public List<DelegatingEntity> entities; }

    void __invoke_testPureBackReferenceVpack() throws Exception {
        try {
            testPureBackReferenceVpack();
        } finally {
        }
    }


    void __invoke_forwardReferenceReportsClearErrorVpack() throws Exception {
        try {
            forwardReferenceReportsClearErrorVpack();
        } finally {
        }
    }

}
