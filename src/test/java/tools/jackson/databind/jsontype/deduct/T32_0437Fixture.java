package tools.jackson.databind.jsontype.deduct;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonValue;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.jsontype.NamedType;
import tools.jackson.databind.jsontype.TypeDeserializer;
import tools.jackson.databind.jsontype.impl.DefaultTypeResolverBuilder;
import tools.jackson.databind.jsontype.impl.StdTypeResolverBuilder;
import tools.jackson.databind.annotation.JsonTypeResolver;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0437Fixture {
private static final ObjectMapper MAPPER = VPackMapper.builder()
            .enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
            .build();
private static final byte[] LIVE_CAT = VPackWireFixtureTest.hex(
            "14 15 45 61 6e 67 72 79 1a 44 6e 61 6d 65 45 46 65 6c 69 78 02");
private static final byte[] DEAD_CAT = VPackWireFixtureTest.hex(
            "14 23 4c 63 61 75 73 65 4f 66 44 65 61 74 68 47 65 6e 74 72 6f 70 79 "
          + "44 6e 61 6d 65 45 46 65 6c 69 78 02");
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] NULL = VPackWireFixtureTest.hex("18");
private static final byte[] FIRST_PARENT = VPackWireFixtureTest.hex(
            "14 13 43 6f 6e 65 4b 48 65 6c 6c 6f 20 57 6f 72 6c 64 01");
private static final byte[] SECOND_PARENT = VPackWireFixtureTest.hex(
            "14 13 43 74 77 6f 4b 48 65 6c 6c 6f 20 57 6f 72 6c 64 01");
private static final byte[] LONG_MAP = VPackWireFixtureTest.hex(
            "14 1b 49 6c 6f 6e 67 49 6e 4d 61 70 32 4b 6c 6f 6e 67 41 73 46 69 65 6c 64 "
          + "31 02");

    // Provenance: SealedTypesWithPolymorphicDeductionTest#testSimpleInference().
    void testSimpleInferenceVpack() throws Exception {
        Cat437 cat = MAPPER.readValue(LIVE_CAT, Cat437.class);
        assertInstanceOf(LiveCat437.class, cat);
        assertSame(LiveCat437.class, cat.getClass());
        assertEquals("Felix", cat.name);
        assertTrue(((LiveCat437) cat).angry);

        cat = MAPPER.readValue(DEAD_CAT, Cat437.class);
        assertInstanceOf(DeadCat437.class, cat);
        assertSame(DeadCat437.class, cat.getClass());
        assertEquals("Felix", cat.name);
        assertEquals("entropy", ((DeadCat437) cat).causeOfDeath);
    }

    // Provenance: SealedTypesWithPolymorphicDeductionTest#testSimpleInferenceOfEmptySubtype().
    void testSimpleInferenceOfEmptySubtypeVpack() throws Exception {
        assertInstanceOf(Fleabag437.class, MAPPER.readValue(EMPTY_OBJECT, Feline437.class));
    }

    // Provenance: SealedTypesWithPolymorphicDeductionTest#testSimpleInferenceOfEmptySubtypeDoesntMatchNull().
    void testSimpleInferenceOfEmptySubtypeDoesntMatchNullVpack() throws Exception {
        assertNull(MAPPER.readValue(NULL, Feline437.class));
    }

    // Provenance: SealedTypesWithPolymorphicDeductionTest#testSimpleSerialization().
    void testSimpleSerializationVpack() throws Exception {
        Cat437 cat = MAPPER.readValue(LIVE_CAT, Cat437.class);
        assertArrayEquals(LIVE_CAT_SERIALIZED, MAPPER.writeValueAsBytes(cat));
    }

    // Provenance: SealedTypesWithPolymorphicDeductionTest#testWithEnum().
    void testWithEnumVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex("41 42"),
                MAPPER.writeValueAsBytes(Enum437.B));
    }

    // Provenance: SealedTypesWithPolymorphicDeductionTest#testWithPojoAsJsonValue().
    void testWithPojoAsJsonValueVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex("45 76 61 6c 75 65"),
                MAPPER.writeValueAsBytes(new Bean437()));
    }
private static final byte[] LIVE_CAT_SERIALIZED = VPackWireFixtureTest.hex(
            "0b 17 02 45 61 6e 67 72 79 1a 44 6e 61 6d 65 45 46 65 6c 69 78 03 0a");
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
    sealed interface Feline437 permits Cat437, Fleabag437 { }
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
    static sealed class Cat437 implements Feline437 permits DeadCat437, LiveCat437 {
        public String name;
    }
static final class DeadCat437 extends Cat437 {
        public String causeOfDeath;
    }
static final class LiveCat437 extends Cat437 {
        public boolean angry;
    }
static final class Fleabag437 implements Feline437 { }
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
    static enum Enum437 { A, B }
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
    static class Bean437 {
        @JsonValue
        public String ser = "value";
    }
interface Parent437 {
        class ChildOne437 implements Parent437 {
            public String one;
        }

        class ChildTwo437 implements Parent437 {
            public String two;
        }
    }
@SuppressWarnings("serial")
    static final class AssertingTypeResolverBuilder437 extends DefaultTypeResolverBuilder {
        AssertingTypeResolverBuilder437() {
            super(BasicPolymorphicTypeValidator.builder()
                    .allowIfSubType(Parent437.class).build(),
                    DefaultTyping.NON_CONCRETE_AND_ARRAYS,
                    JsonTypeInfo.As.PROPERTY);
        }

        @Override
        public TypeDeserializer buildTypeDeserializer(DeserializationContext ctxt,
                JavaType baseType, Collection<NamedType> subtypes) {
            if (baseType.isAbstract()) {
                assertNotNull(subtypes);
                assertEquals(2, subtypes.size());
                assertTrue(subtypes.contains(new NamedType(Parent437.ChildOne437.class)));
                assertTrue(subtypes.contains(new NamedType(Parent437.ChildTwo437.class)));
            }
            return super.buildTypeDeserializer(ctxt, baseType, subtypes);
        }
    }
static class Data437 {
        private Long key;

        @JsonCreator
        Data437(@JsonProperty("key") Long key) {
            this.key = key;
        }

        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY,
                property = "@class")
        @JsonTypeResolver(MyTypeResolverBuilder437.class)
        public long key() {
            return key;
        }
    }
static class MyTypeResolverBuilder437 extends StdTypeResolverBuilder {
        @Override
        protected boolean allowPrimitiveTypes(tools.jackson.databind.DatabindContext ctxt,
                JavaType baseType) {
            return true;
        }
    }

    void __invoke_testSimpleInferenceVpack() throws Exception {
        try {
            testSimpleInferenceVpack();
        } finally {
        }
    }


    void __invoke_testSimpleInferenceOfEmptySubtypeVpack() throws Exception {
        try {
            testSimpleInferenceOfEmptySubtypeVpack();
        } finally {
        }
    }


    void __invoke_testSimpleInferenceOfEmptySubtypeDoesntMatchNullVpack() throws Exception {
        try {
            testSimpleInferenceOfEmptySubtypeDoesntMatchNullVpack();
        } finally {
        }
    }


    void __invoke_testSimpleSerializationVpack() throws Exception {
        try {
            testSimpleSerializationVpack();
        } finally {
        }
    }


    void __invoke_testWithEnumVpack() throws Exception {
        try {
            testWithEnumVpack();
        } finally {
        }
    }


    void __invoke_testWithPojoAsJsonValueVpack() throws Exception {
        try {
            testWithPojoAsJsonValueVpack();
        } finally {
        }
    }

}
