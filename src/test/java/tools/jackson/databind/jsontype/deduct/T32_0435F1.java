package tools.jackson.databind.jsontype.deduct;

import java.util.ArrayList;
import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.exc.InvalidDefinitionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0435F1 {
private static final ObjectMapper MAPPER = VPackMapper.builder()
            .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build();
private static final byte[] MONTH_DAY = VPackWireFixtureTest.hex(
            "0b 12 01 45 76 61 6c 75 65 47 2d 2d 30 31 2d 31 37 03");
private static final byte[] OFFSET_TIME = VPackWireFixtureTest.hex(
            "0b 16 01 45 76 61 6c 75 65 4b 31 35 3a 34 33 2b 30 33 3a 30 30 03");
private static final byte[] YEAR_MONTH = VPackWireFixtureTest.hex(
            "0b 12 01 45 76 61 6c 75 65 47 31 39 38 36 2d 30 31 03");
private static final byte[] ZONE_ID = VPackWireFixtureTest.hex(
            "0b 19 01 45 76 61 6c 75 65 4e 41 6d 65 72 69 63 61 2f 44 65 6e 76 65 72 03");
private static final byte[] ITEM_64 = VPackWireFixtureTest.hex(
            "14 20 44 69 74 65 6d 4f 6d 69 6e 65 63 72 61 66 74 3a 73 74 6f 6e 65 "
          + "45 63 6f 75 6e 74 28 40 02");
private static final byte[] TAG_32 = VPackWireFixtureTest.hex(
            "14 1e 43 74 61 67 4e 6d 69 6e 65 63 72 61 66 74 3a 6c 6f 67 73 "
          + "45 63 6f 75 6e 74 28 20 02");
private static final byte[] ITEM_1 = VPackWireFixtureTest.hex(
            "14 14 44 69 74 65 6d 44 74 65 73 74 45 63 6f 75 6e 74 31 02");
private static final byte[] FIDO_5 = VPackWireFixtureTest.hex(
            "14 12 44 6e 61 6d 65 44 46 69 64 6f 43 61 67 65 35 02");
private static final byte[] DOG = VPackWireFixtureTest.hex(
            "14 12 45 62 72 65 65 64 48 4c 61 62 72 61 64 6f 72 01");
private static final byte[] CAT = VPackWireFixtureTest.hex(
            "14 0b 46 69 6e 64 6f 6f 72 1a 01");
private static final byte[] DATA_OBJECT = VPackWireFixtureTest.hex(
            "14 09 42 69 64 42 23 31 01");
private static final byte[] CONTAINER_OBJECT = VPackWireFixtureTest.hex(
            "14 11 44 64 61 74 61 14 09 42 69 64 42 23 31 01 01");
private static final byte[] DATA_ARRAY = VPackWireFixtureTest.hex(
            "13 0c 14 09 42 69 64 42 23 31 01 01");
private static final byte[] CONTAINER_ARRAY = VPackWireFixtureTest.hex(
            "14 14 44 64 61 74 61 13 0c 14 09 42 69 64 42 23 31 01 01 01");

    // Provenance: DeductionWithAbstractSubtype4708Test#testDeductionWithAbstractIntermediateClass().
    void testDeductionWithAbstractIntermediateClassVpack() throws Exception {
        Ingredient435 result = MAPPER.readValue(ITEM_64, Ingredient435.class);
        assertNotNull(result);
        assertInstanceOf(Ingredient435.ItemById435.class, result);
        Ingredient435.ItemById435 item = (Ingredient435.ItemById435) result;
        assertEquals("minecraft:stone", item.id);
        assertEquals(64, item.count);

        result = MAPPER.readValue(TAG_32, Ingredient435.class);
        assertNotNull(result);
        assertInstanceOf(Ingredient435.ItemByTag435.class, result);
        Ingredient435.ItemByTag435 tag = (Ingredient435.ItemByTag435) result;
        assertEquals("minecraft:logs", tag.tag);
        assertEquals(32, tag.count);
    }

    // Provenance: DeductionWithAbstractSubtype4708Test#testDeductionWithItemInterface().
    void testDeductionWithItemInterfaceVpack() throws Exception {
        JavaType itemType = MAPPER.constructType(Ingredient435.Item.class);
        Ingredient435.Item result = MAPPER.readValue(ITEM_1, itemType);
        assertNotNull(result);
        assertInstanceOf(Ingredient435.ItemById435.class, result);
        assertEquals("test", ((Ingredient435.ItemById435) result).id);
    }

    // Provenance: DeductionWithAbstractSubtype4708Test#testFeatureCanBeDisabled().
    void testFeatureCanBeDisabledVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .disable(DeserializationFeature.IGNORE_ABSTRACT_TYPES_FOR_DEDUCTION)
                .build();
        InvalidDefinitionException e = assertThrows(InvalidDefinitionException.class,
                () -> mapper.readValue(ITEM_64, Ingredient435.class));
        assertTrue(e.getMessage().contains("Subtypes"));
        assertTrue(e.getMessage().contains("have the same signature"));
        assertTrue(e.getMessage().contains("cannot be uniquely deduced"));
        assertTrue(e.getMessage().contains("AbstractItemById435"));
        assertTrue(e.getMessage().contains("ItemById435"));
    }

    // Provenance: DeductionWithAbstractSubtype4708Test#testFeatureEnabledByDefault().
    void testFeatureEnabledByDefaultVpack() {
        assertTrue(MAPPER.isEnabled(DeserializationFeature.IGNORE_ABSTRACT_TYPES_FOR_DEDUCTION));
    }

    // Provenance: DeductionWithAbstractSubtype4708Test#testInterfaceIgnored().
    void testInterfaceIgnoredVpack() throws Exception {
        Pet435 result = MAPPER.readValue(DOG, Pet435.class);
        assertNotNull(result);
        assertInstanceOf(Dog435.class, result);
        assertEquals("Labrador", ((Dog435) result).breed);

        result = MAPPER.readValue(CAT, Pet435.class);
        assertNotNull(result);
        assertInstanceOf(Cat435.class, result);
        assertTrue(((Cat435) result).indoor);
    }

    // Provenance: DeductionWithAbstractSubtype4708Test#testSimpleAbstractClassIgnored().
    void testSimpleAbstractClassIgnoredVpack() throws Exception {
        Animal435 result = MAPPER.readValue(FIDO_5, Animal435.class);
        assertNotNull(result);
        assertInstanceOf(ConcreteAnimal435.class, result);
        assertEquals("Fido", result.name);
        assertEquals(5, ((ConcreteAnimal435) result).age);
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
    static class Wrapper435 {
        @JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
        public Object value;

        Wrapper435(Object value) {
            this.value = value;
        }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
    @JsonSubTypes({
        @JsonSubTypes.Type(Ingredient435.AbstractItemById435.class),
        @JsonSubTypes.Type(Ingredient435.ItemById435.class),
        @JsonSubTypes.Type(Ingredient435.ItemByTag435.class)
    })
    interface Ingredient435 {
        @JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
        @JsonSubTypes({
            @JsonSubTypes.Type(AbstractItemById435.class),
            @JsonSubTypes.Type(ItemById435.class),
            @JsonSubTypes.Type(ItemByTag435.class)
        })
        interface Item extends Ingredient435 { }

        abstract class AbstractItemById435 implements Item {
            @JsonProperty("item")
            public String id;
            public int count = 1;
        }

        class ItemById435 extends AbstractItemById435 {
            public ItemById435() { }
        }

        class ItemByTag435 implements Item {
            @JsonProperty("tag")
            public String tag;
            public int count = 1;
        }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
    @JsonSubTypes({
        @JsonSubTypes.Type(Animal435.class), @JsonSubTypes.Type(ConcreteAnimal435.class)
    })
    abstract static class Animal435 {
        public String name;
    }
static class ConcreteAnimal435 extends Animal435 {
        public int age;
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
    @JsonSubTypes({ @JsonSubTypes.Type(Dog435.class), @JsonSubTypes.Type(Cat435.class) })
    interface Pet435 { }
static class Dog435 implements Pet435 {
        public String breed;
    }
static class Cat435 implements Pet435 {
        public boolean indoor;
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION, defaultImpl = DataArray435.class)
    @JsonSubTypes({ @JsonSubTypes.Type(DataObject435.class), @JsonSubTypes.Type(DataArray435.class) })
    interface Data435 {
        @JsonIgnore
        boolean isObject();
    }
static class DataItem435 {
        final String id;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        DataItem435(@JsonProperty("id") String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }
static class DataObject435 extends DataItem435 implements Data435 {
        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        DataObject435(@JsonProperty("id") String id) {
            super(id);
        }

        @Override
        public boolean isObject() {
            return true;
        }
    }
static class DataArray435 extends ArrayList<DataItem435> implements Data435 {
        private static final long serialVersionUID = 1L;

        @JsonCreator
        DataArray435(Collection<DataItem435> items) {
            super(new ArrayList<>(items));
        }

        @Override
        public boolean isObject() {
            return false;
        }
    }
static class Container435 {
        @JsonProperty("data")
        Data435 data;
    }

    void __invoke_testDeductionWithAbstractIntermediateClassVpack() throws Exception {
        try {
            testDeductionWithAbstractIntermediateClassVpack();
        } finally {
        }
    }


    void __invoke_testDeductionWithItemInterfaceVpack() throws Exception {
        try {
            testDeductionWithItemInterfaceVpack();
        } finally {
        }
    }


    void __invoke_testFeatureCanBeDisabledVpack() throws Exception {
        try {
            testFeatureCanBeDisabledVpack();
        } finally {
        }
    }


    void __invoke_testFeatureEnabledByDefaultVpack() throws Exception {
        try {
            testFeatureEnabledByDefaultVpack();
        } finally {
        }
    }


    void __invoke_testInterfaceIgnoredVpack() throws Exception {
        try {
            testInterfaceIgnoredVpack();
        } finally {
        }
    }


    void __invoke_testSimpleAbstractClassIgnoredVpack() throws Exception {
        try {
            testSimpleAbstractClassIgnoredVpack();
        } finally {
        }
    }

}
