package tools.jackson.databind.jsontype.deduct;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.DateTimeFeature;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0435F2 {
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

    // Provenance: PolymorphicDeductionObjectVsArrayTest#testDeserialization().
    void testDeserializationVpack() throws Exception {
        Container435 container = MAPPER.readValue(CONTAINER_OBJECT, Container435.class);
        assertInstanceOf(DataObject435.class, container.data);
        assertSame(DataObject435.class, container.data.getClass());
        assertTrue(container.data.isObject());
        assertEquals("#1", ((DataItem435) container.data).id);

        container = MAPPER.readValue(CONTAINER_ARRAY, Container435.class);
        assertInstanceOf(DataArray435.class, container.data);
        assertEquals(DataArray435.class, container.data.getClass());
        assertFalse(container.data.isObject());
        Iterator<DataItem435> iterator = ((DataArray435) container.data).iterator();
        assertTrue(iterator.hasNext());
        assertEquals("#1", iterator.next().id);
    }

    // Provenance: PolymorphicDeductionObjectVsArrayTest#testSerialization().
    void testSerializationVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0a 01 42 69 64 42 23 31 03"),
                MAPPER.writeValueAsBytes(new DataObject435("#1")));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "02 0c 0b 0a 01 42 69 64 42 23 31 03"),
                MAPPER.writeValueAsBytes(new DataArray435(java.util.List.of(new DataItem435("#1")))));
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

    void __invoke_testDeserializationVpack() throws Exception {
        try {
            testDeserializationVpack();
        } finally {
        }
    }


    void __invoke_testSerializationVpack() throws Exception {
        try {
            testSerializationVpack();
        } finally {
        }
    }

}
