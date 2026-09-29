package tools.jackson.databind.objectid;

import java.io.ByteArrayOutputStream;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.core.exc.StreamWriteException;
import tools.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0509F1 {
private static final byte[] ORIGINAL_BACK_REFERENCE = VPackWireFixtureTest.hex(
            "14 38 42 69 64 31 46 66 72 75 69 74 73 13 2a 14 27 "
          + "42 69 64 32 44 74 72 65 65 31 48 63 61 6c 6f 72 69 65 73 "
          + "13 11 14 0e 42 69 64 33 45 66 72 75 69 74 32 02 01 03 01 02");
private static final byte[] LEAN_BACK_REFERENCE = VPackWireFixtureTest.hex(
            "0b 3c 02 42 69 64 31 44 63 61 74 73 06 2e 01 0b 2a 03 "
          + "42 69 64 32 46 61 6e 69 6d 61 6c 31 45 66 6f 6f 64 73 "
          + "06 12 01 0b 0e 02 42 69 64 33 43 63 61 74 32 07 03 03 "
          + "07 0f 03 03 07 03");
private static final byte[] LEAN_NO_CREATOR_BACK_REFERENCE = VPackWireFixtureTest.hex(
            "0b 40 02 42 69 64 31 46 73 71 75 69 64 73 06 30 01 0b 2c 03 "
          + "42 69 64 32 44 66 69 73 68 31 47 73 68 72 69 6d 70 73 06 14 01 "
          + "0b 10 02 42 69 64 33 45 73 71 75 69 64 32 03 07 03 07 03 0d 03 03 07");
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final ObjectMapper MAPPER = new VPackMapper();

    // Provenance: NativeObjectIdAndTypeIdTest native-ID declarations.
    void nativeObjectAndTypeIdsAreExplicitlyUnsupportedByVpack() throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(
                ObjectReadContext.empty(), EMPTY_OBJECT)) {
            assertFalse(parser.canReadObjectId());
            assertFalse(parser.canReadTypeId());
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
        }

        try (JsonGenerator generator = new VPackFactory().createGenerator(
                ObjectWriteContext.empty(), new ByteArrayOutputStream())) {
            assertFalse(generator.canWriteObjectId());
            assertFalse(generator.canWriteTypeId());
            assertThrows(StreamWriteException.class, () -> generator.writeObjectId(1));
            assertThrows(StreamWriteException.class, () -> generator.writeTypeId("dog"));
        }
    }
@JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id",
            scope = Tree.class)
    static class Tree {
        protected final int id;
        protected List<Fruit> fruits;

        @JsonCreator
        public Tree(@JsonProperty("id") int id, @JsonProperty("fruits") List<Fruit> fruits) {
            this.id = id;
            this.fruits = fruits;
        }
    }
@JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id",
            scope = Fruit.class)
    static class Fruit {
        protected final int id;
        protected List<Calories> calories;

        @JsonBackReference("id")
        protected Tree tree;

        @JsonCreator
        public Fruit(@JsonProperty("id") int id, @JsonProperty("calories") List<Calories> calories) {
            this.id = id;
            this.calories = calories;
        }
    }
@JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id",
            scope = Calories.class)
    static class Calories {
        protected final int id;

        @JsonCreator
        public Calories(@JsonProperty("id") int id) {
            this.id = id;
        }
    }
@JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id",
            scope = Animal.class)
    static class Animal {
        public final int id;
        public List<Cat> cats;

        @JsonCreator
        public Animal(@JsonProperty("id") int id, @JsonProperty("cats") List<Cat> cats) {
            this.id = id;
            this.cats = cats;
        }
    }
@JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id",
            scope = Cat.class)
    static class Cat {
        public int id;
        public List<Food> foods;

        @JsonBackReference("id")
        public Animal animal;

        @JsonCreator
        public Cat(@JsonProperty("id") int id, @JsonProperty("foods") List<Food> foods) {
            this.id = id;
            this.foods = foods;
        }
    }
@JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id",
            scope = Food.class)
    static class Food {
        public int id;
        public Cat cat;

        @JsonCreator
        public Food(@JsonProperty("id") int id) {
            this.id = id;
        }
    }
@JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id",
            scope = Fish.class)
    static class Fish {
        public int id;
        public List<Squid> squids;
    }
@JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id",
            scope = Squid.class)
    static class Squid {
        public int id;
        public List<Shrimp> shrimps;

        @JsonBackReference("id")
        public Fish fish;
    }
@JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id",
            scope = Shrimp.class)
    static class Shrimp {
        public int id;
        public Squid squid;
    }

    void __invoke_nativeObjectAndTypeIdsAreExplicitlyUnsupportedByVpack() throws Exception {
        try {
            nativeObjectAndTypeIdsAreExplicitlyUnsupportedByVpack();
        } finally {
        }
    }

}
