package tools.jackson.databind.ext.jdk8;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0371Fixture {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] SIMPLE_STRING = VPackWireFixtureTest.hex(
            "4c 73 69 6d 70 6c 65 53 74 72 69 6e 67");
private static final byte[] STRING_PROPERTY = VPackWireFixtureTest.hex(
            "0b 1a 01 48 6d 79 53 74 72 69 6e 67 "
          + "4c 73 69 6d 70 6c 65 53 74 72 69 6e 67 03");
private static final byte[] GENERIC_PROPERTY = VPackWireFixtureTest.hex(
            "0b 18 01 46 6d 79 44 61 74 61 "
          + "4c 73 69 6d 70 6c 65 53 74 72 69 6e 67 03");
private static final byte[] EMPTY_OPTIONAL_PROPERTY = VPackWireFixtureTest.hex(
            "0b 0e 01 48 6d 79 53 74 72 69 6e 67 18 03");
private static final byte[] OPTIONAL_COLLECTION = VPackWireFixtureTest.hex(
            "06 1b 03 "
          + "49 32 30 31 34 2d 31 2d 32 32 18 "
          + "49 32 30 31 34 2d 31 2d 32 33 "
          + "03 0d 0e");
private static final byte[] OBJECT_ID_SELF = VPackWireFixtureTest.hex(
            "0b 14 02 43 40 69 64 31 48 62 61 73 65 55 6e 69 74 31 03 08");
private static final byte[] POLYMORPHIC_OPTIONAL = VPackWireFixtureTest.hex(
            "0b 26 01 49 63 6f 6e 74 61 69 6e 65 64 "
          + "0b 18 01 45 40 74 79 70 65 4d 43 6f 6e 74 61 69 6e 65 64 49 6d 70 6c 03 "
          + "03");
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");

    // Provenance: OptionalBasicTest#testOptionalTypeResolution().
    void testOptionalTypeResolutionVpack() {
        JavaType type = MAPPER.constructType(Optional.class);
        assertNotNull(type);
        assertEquals(Optional.class, type.getRawClass());
        assertTrue(type.isReferenceType());
    }

    // Provenance: OptionalBasicTest#testDeserSimpleString().
    void testDeserSimpleStringVpack() throws Exception {
        Optional<?> value = MAPPER.readValue(SIMPLE_STRING,
                new TypeReference<Optional<String>>() { });
        assertTrue(value.isPresent());
        assertEquals("simpleString", value.get());
    }

    // Provenance: OptionalBasicTest#testObjectId().
    void testObjectIdVpack() throws Exception {
        Unit input = new Unit();
        input.link(input);
        assertArrayEquals(OBJECT_ID_SELF, MAPPER.writeValueAsBytes(input));

        Unit result = MAPPER.readValue(OBJECT_ID_SELF, Unit.class);
        assertNotNull(result);
        assertNotNull(result.baseUnit);
        assertTrue(result.baseUnit.isPresent());
        assertSame(result, result.baseUnit.get());
    }

    // Provenance: OptionalBasicTest#testOptionalCollection().
    void testOptionalCollectionVpack() throws Exception {
        TypeReference<List<Optional<String>>> type =
                new TypeReference<List<Optional<String>>>() { };
        List<Optional<String>> expected = new ArrayList<>();
        expected.add(Optional.of("2014-1-22"));
        expected.add(Optional.empty());
        expected.add(Optional.of("2014-1-23"));

        assertArrayEquals(OPTIONAL_COLLECTION, MAPPER.writeValueAsBytes(expected));
        assertEquals(expected, MAPPER.readValue(OPTIONAL_COLLECTION, type));
    }

    // Provenance: OptionalBasicTest#testPolymorphic().
    void testPolymorphicVpack() throws Exception {
        Container input = new Container();
        input.contained = Optional.of((Contained) new ContainedImpl());

        assertArrayEquals(POLYMORPHIC_OPTIONAL, MAPPER.writeValueAsBytes(input));
        Container result = MAPPER.readValue(POLYMORPHIC_OPTIONAL, Container.class);
        assertNotNull(result.contained);
        assertTrue(result.contained.isPresent());
        assertSame(ContainedImpl.class, result.contained.get().getClass());
    }

    // Provenance: OptionalBasicTest#testSerAbsent().
    void testSerAbsentVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex("18"),
                MAPPER.writeValueAsBytes(Optional.empty()));
    }

    // Provenance: OptionalBasicTest#testSerComplexObject().
    void testSerComplexObjectVpack() throws Exception {
        OptionalData data = new OptionalData();
        data.myString = Optional.of("simpleString");
        assertArrayEquals(STRING_PROPERTY, MAPPER.writeValueAsBytes(Optional.of(data)));
    }

    // Provenance: OptionalBasicTest#testSerGeneric().
    void testSerGenericVpack() throws Exception {
        OptionalGenericData<String> data = new OptionalGenericData<>();
        data.myData = Optional.of("simpleString");
        assertArrayEquals(GENERIC_PROPERTY, MAPPER.writeValueAsBytes(Optional.of(data)));
    }

    // Provenance: OptionalBasicTest#testSerInsideObject().
    void testSerInsideObjectVpack() throws Exception {
        OptionalData data = new OptionalData();
        data.myString = Optional.of("simpleString");
        assertArrayEquals(STRING_PROPERTY, MAPPER.writeValueAsBytes(data));
    }

    // Provenance: OptionalBasicTest#testSerOptDefault().
    void testSerOptDefaultVpack() throws Exception {
        OptionalData data = new OptionalData();
        data.myString = Optional.empty();
        ObjectMapper mapper = VPackMapper.builder()
                .changeDefaultPropertyInclusion(
                        incl -> incl.withValueInclusion(JsonInclude.Include.ALWAYS))
                .build();
        assertArrayEquals(EMPTY_OPTIONAL_PROPERTY, mapper.writeValueAsBytes(data));
    }

    // Provenance: OptionalBasicTest#testSerOptNonEmpty().
    void testSerOptNonEmptyVpack() throws Exception {
        OptionalData data = new OptionalData();
        data.myString = null;
        ObjectMapper mapper = VPackMapper.builder()
                .changeDefaultPropertyInclusion(
                        incl -> incl.withValueInclusion(JsonInclude.Include.NON_EMPTY))
                .build();
        assertArrayEquals(EMPTY_OBJECT, mapper.writeValueAsBytes(data));
    }

    // Provenance: OptionalBasicTest#testSerOptNull().
    void testSerOptNullVpack() throws Exception {
        OptionalData data = new OptionalData();
        data.myString = null;
        ObjectMapper mapper = VPackMapper.builder()
                .changeDefaultPropertyInclusion(
                        incl -> incl.withValueInclusion(JsonInclude.Include.NON_NULL))
                .build();
        assertArrayEquals(EMPTY_OBJECT, mapper.writeValueAsBytes(data));
    }
static class OptionalData {
        public Optional<String> myString;
    }
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
    static class OptionalGenericData<T> {
        Optional<T> myData;
    }
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
    static class Unit {
        public Optional<Unit> baseUnit;

        public Unit() { }

        public void link(Unit unit) {
            baseUnit = Optional.of(unit);
        }
    }
static class Container {
        public Optional<Contained> contained;
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
    @JsonSubTypes(@JsonSubTypes.Type(name = "ContainedImpl", value = ContainedImpl.class))
    interface Contained { }
static class ContainedImpl implements Contained { }

    void __invoke_testOptionalTypeResolutionVpack() throws Exception {
        try {
            testOptionalTypeResolutionVpack();
        } finally {
        }
    }


    void __invoke_testDeserSimpleStringVpack() throws Exception {
        try {
            testDeserSimpleStringVpack();
        } finally {
        }
    }


    void __invoke_testObjectIdVpack() throws Exception {
        try {
            testObjectIdVpack();
        } finally {
        }
    }


    void __invoke_testOptionalCollectionVpack() throws Exception {
        try {
            testOptionalCollectionVpack();
        } finally {
        }
    }


    void __invoke_testPolymorphicVpack() throws Exception {
        try {
            testPolymorphicVpack();
        } finally {
        }
    }


    void __invoke_testSerAbsentVpack() throws Exception {
        try {
            testSerAbsentVpack();
        } finally {
        }
    }


    void __invoke_testSerComplexObjectVpack() throws Exception {
        try {
            testSerComplexObjectVpack();
        } finally {
        }
    }


    void __invoke_testSerGenericVpack() throws Exception {
        try {
            testSerGenericVpack();
        } finally {
        }
    }


    void __invoke_testSerInsideObjectVpack() throws Exception {
        try {
            testSerInsideObjectVpack();
        } finally {
        }
    }


    void __invoke_testSerOptDefaultVpack() throws Exception {
        try {
            testSerOptDefaultVpack();
        } finally {
        }
    }


    void __invoke_testSerOptNonEmptyVpack() throws Exception {
        try {
            testSerOptNonEmptyVpack();
        } finally {
        }
    }


    void __invoke_testSerOptNullVpack() throws Exception {
        try {
            testSerOptNullVpack();
        } finally {
        }
    }

}
