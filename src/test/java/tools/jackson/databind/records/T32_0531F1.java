package tools.jackson.databind.records;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DatabindContext;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.DeserializationProblemHandler;
import tools.jackson.databind.exc.UnrecognizedPropertyException;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0531F1 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final ObjectMapper CLASS_MAPPER = VPackMapper.builder()
            .polymorphicTypeValidator(new NoCheckSubTypeValidator531())
            .build();
private static final byte[] LOW_EXAMPLE = VPackWireFixtureTest.hex(
            "14 26 45 6c 65 76 65 6c 43 4c 4f 57 49 74 6f 6c 65 72 61 6e 63 65 "
          + "14 0f 44 66 6f 6f 64 46 54 6f 6d 61 74 6f 01 02");
private static final byte[] HIGH_EXAMPLE = VPackWireFixtureTest.hex(
            "14 27 45 6c 65 76 65 6c 44 48 49 47 48 49 74 6f 6c 65 72 61 6e 63 65 "
          + "14 0f 44 66 6f 6f 64 46 43 68 69 6c 6c 69 01 02");
private static final byte[] ABSTRACT_MEMBER = VPackWireFixtureTest.hex(
            "14 37 46 6d 65 6d 62 65 72 14 2d 46 40 63 6c 61 73 73 46 73 74 72 69 6e 67 "
          + "43 76 61 6c 57 48 65 6c 6c 6f 2c 20 61 62 73 74 72 61 63 74 20 6d 65 6d 62 65 72 21 02 01");
private static final byte[] ALIAS_Y = VPackWireFixtureTest.hex("14 06 41 59 32 01");
private static final byte[] ALIAS_YY = VPackWireFixtureTest.hex("14 07 42 79 79 32 01");
private static final byte[] ALIAS_FF = VPackWireFixtureTest.hex("14 07 42 66 66 32 01");
private static final byte[] ALIAS_X = VPackWireFixtureTest.hex("14 06 41 58 32 01");
private static final byte[] UNKNOWN_PROPERTIES = VPackWireFixtureTest.hex(
            "14 25 41 78 31 45 65 78 74 72 61 02 05 31 32 33 41 79 32 "
          + "48 74 72 61 69 6c 69 6e 67 47 69 67 6e 6f 72 65 64 04");
private static final byte[] POLYMORPHIC_UNKNOWN = VPackWireFixtureTest.hex(
            "14 22 45 62 72 65 65 64 46 70 6f 6f 64 6c 65 44 6e 61 6d 65 43 52 65 78 "
          + "44 6b 69 6e 64 43 64 6f 67 03");

    // Provenance: RecordUnknownProps5897Test#testSkipUnknowns_defaultConfig().
    void testSkipUnknownsDefaultConfigVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();
        Point5897 value = mapper.readValue(UNKNOWN_PROPERTIES, Point5897.class);
        assertEquals(1, value.x());
        assertEquals(2, value.y());
    }

    // Provenance: RecordUnknownProps5897Test#testFailOnUnknown_stillThrows().
    void testFailOnUnknownStillThrowsVpack() {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();
        assertThrows(UnrecognizedPropertyException.class,
                () -> mapper.readValue(UNKNOWN_PROPERTIES, Point5897.class));
    }

    // Provenance: RecordUnknownProps5897Test#testProblemHandler_stillInvoked().
    void testProblemHandlerStillInvokedVpack() throws Exception {
        List<String> seen = new ArrayList<>();
        ObjectMapper mapper = VPackMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addHandler(new DeserializationProblemHandler() {
                    @Override
                    public boolean handleUnknownProperty(
                            tools.jackson.databind.DeserializationContext ctxt,
                            JsonParser parser,
                            ValueDeserializer<?> deserializer,
                            Object beanOrClass, String propertyName) {
                        seen.add(propertyName);
                        try {
                            parser.skipChildren();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        return true;
                    }
                })
                .build();
        Point5897 value = mapper.readValue(UNKNOWN_PROPERTIES, Point5897.class);
        assertEquals(1, value.x());
        assertEquals(2, value.y());
        assertTrue(seen.contains("extra"), "handler saw: " + seen);
        assertTrue(seen.contains("trailing"), "handler saw: " + seen);
    }

    // Provenance: RecordUnknownProps5897Test#testNonFinalPolymorphic_subPropertyPreserved().
    void testNonFinalPolymorphicSubPropertyPreservedVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();
        Animal5897 value = mapper.readValue(POLYMORPHIC_UNKNOWN, Animal5897.class);
        Dog5897 dog = assertInstanceOf(Dog5897.class, value);
        assertEquals("Rex", dog.name);
        assertEquals("poodle", dog.breed);
    }
private static byte[] aliasFixture(String field) {
        return switch (field) {
        case "Y" -> ALIAS_Y;
        case "yy" -> ALIAS_YY;
        case "ff" -> ALIAS_FF;
        case "X" -> ALIAS_X;
        default -> throw new IllegalArgumentException(field);
        };
    }
enum SpiceLevel3342 { LOW, HIGH }
interface SpiceTolerance3342 { }
record LowSpiceTolerance3342(String food) implements SpiceTolerance3342 { }
record HighSpiceTolerance3342(String food) implements SpiceTolerance3342 { }
record Example3342(
            SpiceLevel3342 level,
            @JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
                    include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "level")
            @JsonSubTypes({
                @JsonSubTypes.Type(value = LowSpiceTolerance3342.class, name = "LOW"),
                @JsonSubTypes.Type(value = HighSpiceTolerance3342.class, name = "HIGH")
            })
            SpiceTolerance3342 tolerance) { }
record RootRecord249_3342(AbstractMember249_3342 member) { }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@class")
    @JsonSubTypes({
        @JsonSubTypes.Type(value = StringMember3342.class, name = "string"),
        @JsonSubTypes.Type(value = IntMember3342.class, name = "int")
    })
    static abstract class AbstractMember249_3342 { }
static final class StringMember3342 extends AbstractMember249_3342 {
        final String val;

        @JsonCreator
        public StringMember3342(@JsonProperty("val") String val) {
            this.val = val;
        }
    }
static final class IntMember3342 extends AbstractMember249_3342 {
        final int val;

        @JsonCreator
        public IntMember3342(@JsonProperty("val") int val) {
            this.val = val;
        }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
    @JsonSubTypes({
        @JsonSubTypes.Type(value = DeductionBean1_3342.class),
        @JsonSubTypes.Type(value = DeductionBean2_3342.class)
    })
    interface Deduction4327_3342 { }
record DeductionBean1_3342(int x) implements Deduction4327_3342 { }
record DeductionBean2_3342(
            @JsonAlias(value = { "Y", "yy", "ff", "X" }) int y)
            implements Deduction4327_3342 { }
record Container3786_3342<T>(
            int id,
            @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS,
                    include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type")
            T value) { }
record MyObject3786_3342(String foo, String bar) { }
record Point5897(int x, int y) { }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "kind")
    @JsonSubTypes({ @JsonSubTypes.Type(value = Dog5897.class, name = "dog") })
    static class Animal5897 {
        public final String name;

        @JsonCreator
        public Animal5897(@JsonProperty("name") String name) {
            this.name = name;
        }
    }
static class Dog5897 extends Animal5897 {
        public String breed;

        @JsonCreator
        public Dog5897(@JsonProperty("name") String name,
                @JsonProperty("breed") String breed) {
            super(name);
            this.breed = breed;
        }
    }
private static final class NoCheckSubTypeValidator531 extends PolymorphicTypeValidator.Base {
        private static final long serialVersionUID = 1L;

        @Override
        public Validity validateBaseType(DatabindContext ctxt, JavaType baseType) {
            return Validity.ALLOWED;
        }
    }

    void __invoke_testSkipUnknownsDefaultConfigVpack() throws Exception {
        try {
            testSkipUnknownsDefaultConfigVpack();
        } finally {
        }
    }


    void __invoke_testFailOnUnknownStillThrowsVpack() throws Exception {
        try {
            testFailOnUnknownStillThrowsVpack();
        } finally {
        }
    }


    void __invoke_testProblemHandlerStillInvokedVpack() throws Exception {
        try {
            testProblemHandlerStillInvokedVpack();
        } finally {
        }
    }


    void __invoke_testNonFinalPolymorphicSubPropertyPreservedVpack() throws Exception {
        try {
            testNonFinalPolymorphicSubPropertyPreservedVpack();
        } finally {
        }
    }

}
