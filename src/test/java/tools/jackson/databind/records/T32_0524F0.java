package tools.jackson.databind.records;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0524F0 {
private static final byte[] ID_ONLY_123 = VPackWireFixtureTest.hex(
            "0b 0e 01 47 69 64 5f 6f 6e 6c 79 28 7b 03");
private static final byte[] TWO_PROPERTIES_123 = VPackWireFixtureTest.hex(
            "0b 28 02 46 74 68 65 5f 69 64 28 7b "
          + "49 74 68 65 5f 65 6d 61 69 6c 4f 62 6f 62 40 65 78 61 6d 70 6c 65 2e 63 6f 6d "
          + "0c 03");
private static final byte[] CANONICAL_PROPERTIES = VPackWireFixtureTest.hex(
            "0b 15 02 42 69 64 28 7b 44 6e 61 6d 65 45 42 6f 62 62 79 03 08");
private static final byte[] CANONICAL_PROPERTIES_WITH_EMAIL = VPackWireFixtureTest.hex(
            "0b 2e 03 42 69 64 28 7b 44 6e 61 6d 65 45 42 6f 62 62 79 "
          + "45 65 6d 61 69 6c 51 62 6f 62 62 79 40 65 78 61 6d 70 6c 65 2e 63 6f 6d "
          + "13 03 08");
private static final byte[] DECIMAL_123_4 = VPackWireFixtureTest.hex(
            "c8 02 ff ff ff ff 12 34");
private static final byte[] CANONICAL_DECIMAL_RECORD = VPackWireFixtureTest.hex(
            "0b 2a 02 42 69 64 c8 02 ff ff ff ff 12 34 "
          + "44 6e 61 6d 65 54 43 61 6e 6f 6e 69 63 61 6c 43 6f 6e 73 74 72 75 63 74 6f 72 "
          + "03 0e");
private static final byte[] STRING_123 = VPackWireFixtureTest.hex(
            "43 31 32 33");
private static final byte[] STRING_123_4 = VPackWireFixtureTest.hex(
            "45 31 32 33 2e 34");
private static final byte[] PERSON_WITH_HELPER = VPackWireFixtureTest.hex(
            "14 22 44 6e 61 6d 65 43 62 6f 62 43 61 67 65 28 1e "
          + "4b 64 69 73 70 6c 61 79 4e 61 6d 65 43 42 4f 42 03");
private static final ObjectMapper CREATOR_MAPPER = VPackMapper.builder()
            .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();

    // Provenance: RecordExplicitCreatorsTest#testDeserializeUsingJsonPropertyConstructor_WithoutJsonCreator().
    void testDeserializeUsingJsonPropertyConstructorWithoutJsonCreatorVpack() throws Exception {
        assertEquals(new RecordWithOneJsonPropertyWithoutJsonCreator(123),
                CREATOR_MAPPER.readValue(ID_ONLY_123,
                        RecordWithOneJsonPropertyWithoutJsonCreator.class));
        assertEquals(new RecordWithTwoJsonPropertyWithoutJsonCreator(123, "bob@example.com"),
                CREATOR_MAPPER.readValue(TWO_PROPERTIES_123,
                        RecordWithTwoJsonPropertyWithoutJsonCreator.class));
    }

    // Provenance: RecordExplicitCreatorsTest#testDeserializeUsingCanonicalConstructor_WhenJsonPropertyConstructorExists_WillFail().
    void testDeserializeUsingCanonicalConstructorWhenJsonPropertyConstructorExistsWillFailVpack() {
        assertThrows(DatabindException.class, () -> CREATOR_MAPPER.readValue(
                CANONICAL_PROPERTIES, RecordWithOneJsonPropertyWithoutJsonCreator.class));
        assertThrows(DatabindException.class, () -> CREATOR_MAPPER.readValue(
                CANONICAL_PROPERTIES_WITH_EMAIL, RecordWithTwoJsonPropertyWithoutJsonCreator.class));
    }

    // Provenance: RecordExplicitCreatorsTest#testDeserializeUsingImplicitFactoryMethod_WhenJsonPropertyConstructorExists().
    void testDeserializeUsingImplicitFactoryMethodWhenJsonPropertyConstructorExistsVpack()
            throws Exception {
        assertEquals(RecordWithOneJsonPropertyWithoutJsonCreator.valueOf(123),
                CREATOR_MAPPER.readValue(VPackWireFixtureTest.hex("28 7b"),
                        RecordWithOneJsonPropertyWithoutJsonCreator.class));
        assertEquals(RecordWithTwoJsonPropertyWithoutJsonCreator.valueOf(123),
                CREATOR_MAPPER.readValue(VPackWireFixtureTest.hex("28 7b"),
                        RecordWithTwoJsonPropertyWithoutJsonCreator.class));
    }

    // Provenance: RecordExplicitCreatorsTest#testDeserializeUsingJsonCreatorConstructor().
    void testDeserializeUsingJsonCreatorConstructorVpack() throws Exception {
        assertEquals(new RecordWithJsonPropertyWithJsonCreator(123),
                CREATOR_MAPPER.readValue(ID_ONLY_123,
                        RecordWithJsonPropertyWithJsonCreator.class));
    }

    // Provenance: RecordExplicitCreatorsTest#testDeserializeUsingCanonicalConstructor_WhenJsonCreatorConstructorExists_WillFail().
    void testDeserializeUsingCanonicalConstructorWhenJsonCreatorConstructorExistsWillFailVpack() {
        assertThrows(DatabindException.class, () -> CREATOR_MAPPER.readValue(
                CANONICAL_PROPERTIES, RecordWithJsonPropertyWithJsonCreator.class));
    }

    // Provenance: RecordExplicitCreatorsTest#testDeserializeUsingImplicitFactoryMethod_WhenJsonCreatorConstructorExists_WillFail().
    void testDeserializeUsingImplicitFactoryMethodWhenJsonCreatorConstructorExistsWillFailVpack()
            throws Exception {
        RecordWithJsonPropertyWithJsonCreator value = CREATOR_MAPPER.readValue(
                VPackWireFixtureTest.hex("28 7b"), RecordWithJsonPropertyWithJsonCreator.class);
        assertEquals(123, value.id());
        assertEquals("JsonCreatorConstructor", value.name());
    }

    // Provenance: RecordExplicitCreatorsTest#testDeserializeUsingExplicitDelegatingConstructors().
    void testDeserializeUsingExplicitDelegatingConstructorsVpack() throws Exception {
        assertEquals(new RecordWithMultiExplicitDelegatingConstructor(123, "IntConstructor"),
                CREATOR_MAPPER.readValue(VPackWireFixtureTest.hex("28 7b"),
                        RecordWithMultiExplicitDelegatingConstructor.class));
        assertEquals(new RecordWithMultiExplicitDelegatingConstructor(123, "StringConstructor"),
                CREATOR_MAPPER.readValue(STRING_123,
                        RecordWithMultiExplicitDelegatingConstructor.class));
    }

    // Provenance: RecordExplicitCreatorsTest#testDeserializeUsingDisabledConstructors_WillFail().
    void testDeserializeUsingDisabledConstructorsWillFailVpack() {
        assertThrows(DatabindException.class, () -> CREATOR_MAPPER.readValue(
                CANONICAL_PROPERTIES, RecordWithDisabledJsonCreator.class));
    }

    // Provenance: RecordExplicitCreatorsTest#testDeserializeUsingExplicitFactoryMethods().
    void testDeserializeUsingExplicitFactoryMethodsVpack() throws Exception {
        assertEquals(new RecordWithExplicitFactoryMethod(BigDecimal.valueOf(123), "IntFactoryMethod"),
                CREATOR_MAPPER.readValue(VPackWireFixtureTest.hex("28 7b"),
                        RecordWithExplicitFactoryMethod.class));
        assertEquals(new RecordWithExplicitFactoryMethod(BigDecimal.valueOf(123.4), "StringFactoryMethod"),
                CREATOR_MAPPER.readValue(STRING_123_4, RecordWithExplicitFactoryMethod.class));
    }

    // Provenance: RecordExplicitCreatorsTest#testDeserializeUsingImplicitFactoryMethods_WhenExplicitFactoryMethodsExist_WillFail().
    void testDeserializeUsingImplicitFactoryMethodsWhenExplicitFactoryMethodsExistWillFailVpack() {
        assertThrows(DatabindException.class, () -> CREATOR_MAPPER.readValue(
                DECIMAL_123_4, RecordWithExplicitFactoryMethod.class));
    }

    // Provenance: RecordExplicitCreatorsTest#testDeserializeUsingImplicitCanonicalConstructor_WhenFactoryMethodsExist().
    void testDeserializeUsingImplicitCanonicalConstructorWhenFactoryMethodsExistVpack()
            throws Exception {
        assertEquals(new RecordWithExplicitFactoryMethod(
                        BigDecimal.valueOf(123.4), "CanonicalConstructor"),
                CREATOR_MAPPER.readValue(CANONICAL_DECIMAL_RECORD,
                        RecordWithExplicitFactoryMethod.class));
    }
record RecordWithOneJsonPropertyWithoutJsonCreator(int id, String name) {
        public RecordWithOneJsonPropertyWithoutJsonCreator(@JsonProperty("id_only") int id) {
            this(id, "JsonPropertyConstructor");
        }

        public static RecordWithOneJsonPropertyWithoutJsonCreator valueOf(int id) {
            return new RecordWithOneJsonPropertyWithoutJsonCreator(id);
        }
    }
record RecordWithTwoJsonPropertyWithoutJsonCreator(int id, String name, String email) {
        public RecordWithTwoJsonPropertyWithoutJsonCreator(
                @JsonProperty("the_id") int id, @JsonProperty("the_email") String email) {
            this(id, "TwoJsonPropertyConstructor", email);
        }

        public static RecordWithTwoJsonPropertyWithoutJsonCreator valueOf(int id) {
            return new RecordWithTwoJsonPropertyWithoutJsonCreator(id, "factory@example.com");
        }
    }
record RecordWithJsonPropertyWithJsonCreator(int id, String name) {
        @JsonCreator
        public RecordWithJsonPropertyWithJsonCreator(@JsonProperty("id_only") int id) {
            this(id, "JsonCreatorConstructor");
        }

        public static RecordWithJsonPropertyWithJsonCreator valueOf(int id) {
            return new RecordWithJsonPropertyWithJsonCreator(id);
        }
    }
record RecordWithMultiExplicitDelegatingConstructor(int id, String name) {
        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        public RecordWithMultiExplicitDelegatingConstructor(int id) {
            this(id, "IntConstructor");
        }

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        public RecordWithMultiExplicitDelegatingConstructor(String id) {
            this(Integer.parseInt(id), "StringConstructor");
        }
    }
record RecordWithDisabledJsonCreator(int id, String name) {
        @JsonCreator(mode = JsonCreator.Mode.DISABLED)
        RecordWithDisabledJsonCreator { }
    }
record RecordWithExplicitFactoryMethod(BigDecimal id, String name) {
        @JsonCreator
        public static RecordWithExplicitFactoryMethod valueOf(int value) {
            return new RecordWithExplicitFactoryMethod(BigDecimal.valueOf(value), "IntFactoryMethod");
        }

        public static RecordWithExplicitFactoryMethod valueOf(double value) {
            return new RecordWithExplicitFactoryMethod(BigDecimal.valueOf(value), "DoubleFactoryMethod");
        }

        @JsonCreator
        public static RecordWithExplicitFactoryMethod valueOf(String value) {
            return new RecordWithExplicitFactoryMethod(
                    BigDecimal.valueOf(Double.parseDouble(value)), "StringFactoryMethod");
        }
    }
record PersonRecord(String name, int age) {
        public String getDisplayName() {
            return name.toUpperCase();
        }
    }
record MixedRecord(int value) {
        @Override
        public int value() {
            return value;
        }

        public int getDoubleValue() {
            return value * 2;
        }
    }

    void __invoke_testDeserializeUsingJsonPropertyConstructorWithoutJsonCreatorVpack() throws Exception {
        try {
            testDeserializeUsingJsonPropertyConstructorWithoutJsonCreatorVpack();
        } finally {
        }
    }


    void __invoke_testDeserializeUsingCanonicalConstructorWhenJsonPropertyConstructorExistsWillFailVpack() throws Exception {
        try {
            testDeserializeUsingCanonicalConstructorWhenJsonPropertyConstructorExistsWillFailVpack();
        } finally {
        }
    }


    void __invoke_testDeserializeUsingImplicitFactoryMethodWhenJsonPropertyConstructorExistsVpack() throws Exception {
        try {
            testDeserializeUsingImplicitFactoryMethodWhenJsonPropertyConstructorExistsVpack();
        } finally {
        }
    }


    void __invoke_testDeserializeUsingJsonCreatorConstructorVpack() throws Exception {
        try {
            testDeserializeUsingJsonCreatorConstructorVpack();
        } finally {
        }
    }


    void __invoke_testDeserializeUsingCanonicalConstructorWhenJsonCreatorConstructorExistsWillFailVpack() throws Exception {
        try {
            testDeserializeUsingCanonicalConstructorWhenJsonCreatorConstructorExistsWillFailVpack();
        } finally {
        }
    }


    void __invoke_testDeserializeUsingImplicitFactoryMethodWhenJsonCreatorConstructorExistsWillFailVpack() throws Exception {
        try {
            testDeserializeUsingImplicitFactoryMethodWhenJsonCreatorConstructorExistsWillFailVpack();
        } finally {
        }
    }


    void __invoke_testDeserializeUsingExplicitDelegatingConstructorsVpack() throws Exception {
        try {
            testDeserializeUsingExplicitDelegatingConstructorsVpack();
        } finally {
        }
    }


    void __invoke_testDeserializeUsingDisabledConstructorsWillFailVpack() throws Exception {
        try {
            testDeserializeUsingDisabledConstructorsWillFailVpack();
        } finally {
        }
    }


    void __invoke_testDeserializeUsingExplicitFactoryMethodsVpack() throws Exception {
        try {
            testDeserializeUsingExplicitFactoryMethodsVpack();
        } finally {
        }
    }


    void __invoke_testDeserializeUsingImplicitFactoryMethodsWhenExplicitFactoryMethodsExistWillFailVpack() throws Exception {
        try {
            testDeserializeUsingImplicitFactoryMethodsWhenExplicitFactoryMethodsExistWillFailVpack();
        } finally {
        }
    }


    void __invoke_testDeserializeUsingImplicitCanonicalConstructorWhenFactoryMethodsExistVpack() throws Exception {
        try {
            testDeserializeUsingImplicitCanonicalConstructorWhenFactoryMethodsExistVpack();
        } finally {
        }
    }

}
