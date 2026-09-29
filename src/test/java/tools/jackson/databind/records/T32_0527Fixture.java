package tools.jackson.databind.records;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonValue;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.ConstructorDetector;
import tools.jackson.databind.exc.MismatchedInputException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0527Fixture {
private static final ObjectMapper DEFAULT_MAPPER = new VPackMapper();
private static final ObjectMapper DELEGATING_MAPPER = VPackMapper.builder()
            .constructorDetector(ConstructorDetector.USE_DELEGATING)
            .build();
private static final ObjectMapper PROPERTIES_MAPPER = VPackMapper.builder()
            .constructorDetector(ConstructorDetector.USE_PROPERTIES_BASED)
            .build();
private static final byte[] VALUE_123 = VPackWireFixtureTest.hex("28 7b");
private static final byte[] ID_123 = VPackWireFixtureTest.hex(
            "14 08 42 69 64 28 7b 01");
private static final byte[] ID_NAME_BOB = VPackWireFixtureTest.hex(
            "14 11 42 69 64 28 7b 44 6e 61 6d 65 43 42 6f 62 02");
private static final byte[] DOUBLE_ID_CANONICAL_NAME = VPackWireFixtureTest.hex(
            "14 29 42 69 64 1b 9a 99 99 99 99 d9 5e 40 "
          + "44 6e 61 6d 65 54 43 61 6e 6f 6e 69 63 61 6c 43 6f 6e 73 74 72 75 63 74 6f 72 02");

    // Provenance: RecordImplicitCreatorsTest#testDeserializeMultipleConstructors_WillIgnoreSingleValueAltConstructor().
    void testDeserializeMultipleConstructorsWillIgnoreSingleValueAltConstructorVpack() throws Exception {
        RecordWithMultiValueCanonAndSingleValueAltConstructor value = PROPERTIES_MAPPER.readValue(
                ID_123, RecordWithMultiValueCanonAndSingleValueAltConstructor.class);

        assertEquals(new RecordWithMultiValueCanonAndSingleValueAltConstructor(123, null), value);
    }

    // Provenance: RecordImplicitCreatorsTest#testDeserializeSingleValueConstructor_WithDelegatingConstructorDetector_WillFail().
    void testDeserializeSingleValueConstructorWithDelegatingConstructorDetectorWillFailVpack() throws Exception {
        assertThrows(MismatchedInputException.class,
                () -> DELEGATING_MAPPER.readValue(VALUE_123, RecordWithSingleValueConstructor.class));

        assertEquals(new RecordWithSingleValueConstructor(123),
                DEFAULT_MAPPER.readValue(ID_123, RecordWithSingleValueConstructor.class));
    }

    // Provenance: RecordImplicitCreatorsTest#testDeserializeSingleValueConstructor_WithPropertiesBasedConstructorDetector_WillFail().
    void testDeserializeSingleValueConstructorWithPropertiesBasedConstructorDetectorWillFailVpack() throws Exception {
        assertThrows(MismatchedInputException.class,
                () -> PROPERTIES_MAPPER.readValue(VALUE_123, RecordWithSingleValueConstructor.class));

        assertEquals(new RecordWithSingleValueConstructor(123),
                DEFAULT_MAPPER.readValue(ID_123, RecordWithSingleValueConstructor.class));
    }

    // Provenance: RecordImplicitCreatorsTest#testDeserializeUsingImplicitCanonicalConstructor_WhenImplicitFactoryMethodsExist().
    void testDeserializeUsingImplicitCanonicalConstructorWhenImplicitFactoryMethodsExistVpack() throws Exception {
        RecordWithImplicitFactoryMethods value = DEFAULT_MAPPER.readValue(
                DOUBLE_ID_CANONICAL_NAME, RecordWithImplicitFactoryMethods.class);

        assertEquals(new RecordWithImplicitFactoryMethods(
                BigDecimal.valueOf(123.4), "CanonicalConstructor"), value);
    }

    // Provenance: RecordImplicitCreatorsTest#testDeserializeUsingImplicitDelegatingConstructor().
    void testDeserializeUsingImplicitDelegatingConstructorVpack() throws Exception {
        RecordWithAltSingleValueConstructor value = DEFAULT_MAPPER.readValue(
                VALUE_123, RecordWithAltSingleValueConstructor.class);

        assertEquals(new RecordWithAltSingleValueConstructor(123, "SingleValueConstructor"), value);
    }

    // Provenance: RecordImplicitCreatorsTest#testDeserializeUsingImplicitDoubleFactoryMethod().
    void testDeserializeUsingImplicitDoubleFactoryMethodVpack() throws Exception {
        RecordWithImplicitFactoryMethods value = DEFAULT_MAPPER.readValue(
                VPackWireFixtureTest.hex("1b 9a 99 99 99 99 d9 5e 40"),
                RecordWithImplicitFactoryMethods.class);

        assertEquals(new RecordWithImplicitFactoryMethods(
                BigDecimal.valueOf(123.4), "DoubleFactoryMethod"), value);
    }

    // Provenance: RecordImplicitCreatorsTest#testDeserializeUsingImplicitFactoryMethod_WithAutoDetectCreatorsDisabled_WillFail().
    void testDeserializeUsingImplicitFactoryMethodWithAutoDetectCreatorsDisabledWillFailVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .changeDefaultVisibility(vc -> vc
                        .withScalarConstructorVisibility(JsonAutoDetect.Visibility.NONE)
                        .withCreatorVisibility(JsonAutoDetect.Visibility.NONE))
                .build();

        assertThrows(DatabindException.class,
                () -> mapper.readValue(VALUE_123, RecordWithImplicitFactoryMethods.class));
    }

    // Provenance: RecordImplicitCreatorsTest#testDeserializeUsingImplicitIntegerFactoryMethod().
    void testDeserializeUsingImplicitIntegerFactoryMethodVpack() throws Exception {
        RecordWithImplicitFactoryMethods value = DEFAULT_MAPPER.readValue(
                VALUE_123, RecordWithImplicitFactoryMethods.class);

        assertEquals(new RecordWithImplicitFactoryMethods(
                BigDecimal.valueOf(123), "IntFactoryMethod"), value);
    }

    // Provenance: RecordImplicitCreatorsTest#testDeserializeUsingImplicitPropertiesBasedConstructor().
    void testDeserializeUsingImplicitPropertiesBasedConstructorVpack() throws Exception {
        RecordWithAltSingleValueConstructor value = DEFAULT_MAPPER.readValue(
                ID_NAME_BOB, RecordWithAltSingleValueConstructor.class);

        assertEquals(new RecordWithAltSingleValueConstructor(123, "Bob"), value);
    }

    // Provenance: RecordImplicitCreatorsTest#testDeserializeUsingImplicitSingleValueConstructor().
    void testDeserializeUsingImplicitSingleValueConstructorVpack() throws Exception {
        assertThrows(MismatchedInputException.class,
                () -> DEFAULT_MAPPER.readValue(VALUE_123, RecordWithSingleValueConstructor.class));

        assertEquals(new RecordWithSingleValueConstructor(123),
                DEFAULT_MAPPER.readValue(ID_123, RecordWithSingleValueConstructor.class));
    }

    // Provenance: RecordImplicitCreatorsTest#testDeserializeUsingImplicitSingleValueConstructor_WithJsonValue().
    void testDeserializeUsingImplicitSingleValueConstructorWithJsonValueVpack() throws Exception {
        assertEquals(new RecordWithSingleValueConstructorWithJsonValue(123),
                DEFAULT_MAPPER.readValue(VALUE_123, RecordWithSingleValueConstructorWithJsonValue.class));

        assertThrows(MismatchedInputException.class,
                () -> DEFAULT_MAPPER.readValue(ID_123, RecordWithSingleValueConstructorWithJsonValue.class));
    }

    // Provenance: RecordImplicitCreatorsTest#testDeserializeUsingImplicitSingleValueConstructor_WithJsonValueAccessor().
    void testDeserializeUsingImplicitSingleValueConstructorWithJsonValueAccessorVpack() throws Exception {
        assertEquals(new RecordWithSingleValueConstructorWithJsonValueAccessor(123),
                DEFAULT_MAPPER.readValue(VALUE_123, RecordWithSingleValueConstructorWithJsonValueAccessor.class));

        assertThrows(MismatchedInputException.class,
                () -> DEFAULT_MAPPER.readValue(ID_123,
                        RecordWithSingleValueConstructorWithJsonValueAccessor.class));
    }
record RecordWithImplicitFactoryMethods(BigDecimal id, String name) {
        public static RecordWithImplicitFactoryMethods valueOf(int id) {
            return new RecordWithImplicitFactoryMethods(BigDecimal.valueOf(id), "IntFactoryMethod");
        }

        public static RecordWithImplicitFactoryMethods valueOf(double id) {
            return new RecordWithImplicitFactoryMethods(BigDecimal.valueOf(id), "DoubleFactoryMethod");
        }
    }
record RecordWithSingleValueConstructor(int id) { }
record RecordWithSingleValueConstructorWithJsonValue(@JsonValue int id) { }
record RecordWithSingleValueConstructorWithJsonValueAccessor(int id) {
        @JsonValue
        @Override
        public int id() {
            return id;
        }
    }
record RecordWithAltSingleValueConstructor(int id, String name) {
        public RecordWithAltSingleValueConstructor(int id) {
            this(id, "SingleValueConstructor");
        }
    }
record RecordWithMultiValueCanonAndSingleValueAltConstructor(int id, String name) {
        public RecordWithMultiValueCanonAndSingleValueAltConstructor(int id) {
            this(id, "AltConstructor");
        }
    }

    void __invoke_testDeserializeMultipleConstructorsWillIgnoreSingleValueAltConstructorVpack() throws Exception {
        try {
            testDeserializeMultipleConstructorsWillIgnoreSingleValueAltConstructorVpack();
        } finally {
        }
    }


    void __invoke_testDeserializeSingleValueConstructorWithDelegatingConstructorDetectorWillFailVpack() throws Exception {
        try {
            testDeserializeSingleValueConstructorWithDelegatingConstructorDetectorWillFailVpack();
        } finally {
        }
    }


    void __invoke_testDeserializeSingleValueConstructorWithPropertiesBasedConstructorDetectorWillFailVpack() throws Exception {
        try {
            testDeserializeSingleValueConstructorWithPropertiesBasedConstructorDetectorWillFailVpack();
        } finally {
        }
    }


    void __invoke_testDeserializeUsingImplicitCanonicalConstructorWhenImplicitFactoryMethodsExistVpack() throws Exception {
        try {
            testDeserializeUsingImplicitCanonicalConstructorWhenImplicitFactoryMethodsExistVpack();
        } finally {
        }
    }


    void __invoke_testDeserializeUsingImplicitDelegatingConstructorVpack() throws Exception {
        try {
            testDeserializeUsingImplicitDelegatingConstructorVpack();
        } finally {
        }
    }


    void __invoke_testDeserializeUsingImplicitDoubleFactoryMethodVpack() throws Exception {
        try {
            testDeserializeUsingImplicitDoubleFactoryMethodVpack();
        } finally {
        }
    }


    void __invoke_testDeserializeUsingImplicitFactoryMethodWithAutoDetectCreatorsDisabledWillFailVpack() throws Exception {
        try {
            testDeserializeUsingImplicitFactoryMethodWithAutoDetectCreatorsDisabledWillFailVpack();
        } finally {
        }
    }


    void __invoke_testDeserializeUsingImplicitIntegerFactoryMethodVpack() throws Exception {
        try {
            testDeserializeUsingImplicitIntegerFactoryMethodVpack();
        } finally {
        }
    }


    void __invoke_testDeserializeUsingImplicitPropertiesBasedConstructorVpack() throws Exception {
        try {
            testDeserializeUsingImplicitPropertiesBasedConstructorVpack();
        } finally {
        }
    }


    void __invoke_testDeserializeUsingImplicitSingleValueConstructorVpack() throws Exception {
        try {
            testDeserializeUsingImplicitSingleValueConstructorVpack();
        } finally {
        }
    }


    void __invoke_testDeserializeUsingImplicitSingleValueConstructorWithJsonValueVpack() throws Exception {
        try {
            testDeserializeUsingImplicitSingleValueConstructorWithJsonValueVpack();
        } finally {
        }
    }


    void __invoke_testDeserializeUsingImplicitSingleValueConstructorWithJsonValueAccessorVpack() throws Exception {
        try {
            testDeserializeUsingImplicitSingleValueConstructorWithJsonValueAccessorVpack();
        } finally {
        }
    }

}
