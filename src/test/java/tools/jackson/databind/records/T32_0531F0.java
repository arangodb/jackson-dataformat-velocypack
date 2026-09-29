package tools.jackson.databind.records;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DatabindContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0531F0 {
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

    // Provenance: RecordTypeInfo3342Test#testSerializeDeserializeJsonSubType_LOW().
    void testSerializeDeserializeJsonSubType_LOWVpack() throws Exception {
        Example3342 value = MAPPER.readValue(LOW_EXAMPLE, Example3342.class);
        assertEquals(SpiceLevel3342.LOW, value.level());
        LowSpiceTolerance3342 tolerance = assertInstanceOf(LowSpiceTolerance3342.class,
                value.tolerance());
        assertEquals("Tomato", tolerance.food());
    }

    // Provenance: RecordTypeInfo3342Test#testSerializeDeserializeJsonSubType_HIGH().
    void testSerializeDeserializeJsonSubType_HIGHVpack() throws Exception {
        Example3342 value = MAPPER.readValue(HIGH_EXAMPLE, Example3342.class);
        assertEquals(SpiceLevel3342.HIGH, value.level());
        HighSpiceTolerance3342 tolerance = assertInstanceOf(HighSpiceTolerance3342.class,
                value.tolerance());
        assertEquals("Chilli", tolerance.food());
    }

    // Provenance: RecordTypeInfo3342Test#testDeserializeRecordWithAbstractMember().
    void testDeserializeRecordWithAbstractMemberVpack() throws Exception {
        RootRecord249_3342 value = MAPPER.readValue(ABSTRACT_MEMBER, RootRecord249_3342.class);
        assertNotNull(value.member());
        assertEquals(StringMember3342.class, value.member().getClass());
        assertEquals("Hello, abstract member!", ((StringMember3342) value.member()).val);
    }

    
    // Provenance: RecordTypeInfo3342Test#testAliasWithPolymorphicDeduction(String).
    void testAliasWithPolymorphicDeductionVpack(String field) throws Exception {
        Deduction4327_3342 value = MAPPER.readValue(aliasFixture(field), Deduction4327_3342.class);
        assertNotNull(value);
        assertEquals(2, ((DeductionBean2_3342) value).y());
    }

    // Provenance: RecordTypeInfo3342Test#testCustomObjectRoundTrip3786().
    void testCustomObjectRoundTrip3786Vpack() throws Exception {
        Container3786_3342<MyObject3786_3342> input =
                new Container3786_3342<>(1, new MyObject3786_3342("foo", "bar"));
        Container3786_3342<?> result = CLASS_MAPPER.readValue(
                CLASS_MAPPER.writeValueAsBytes(input),
                new TypeReference<Container3786_3342<?>>() { });
        assertNotNull(result);
        assertEquals(1, result.id());
        assertNotNull(result.value());
    }

    // Provenance: RecordTypeInfo3342Test#testStringValueRoundTrip3786().
    void testStringValueRoundTrip3786Vpack() throws Exception {
        Container3786_3342<String> input = new Container3786_3342<>(1, "Hello");
        Container3786_3342<?> result = CLASS_MAPPER.readValue(
                CLASS_MAPPER.writeValueAsBytes(input),
                new TypeReference<Container3786_3342<?>>() { });
        assertNotNull(result);
        assertEquals(1, result.id());
        assertEquals("Hello", result.value());
    }

    // Provenance: RecordTypeInfo3342Test#testIntegerValueRoundTrip3786().
    void testIntegerValueRoundTrip3786Vpack() throws Exception {
        Container3786_3342<Integer> input = new Container3786_3342<>(1, 42);
        Container3786_3342<?> result = CLASS_MAPPER.readValue(
                CLASS_MAPPER.writeValueAsBytes(input),
                new TypeReference<Container3786_3342<?>>() { });
        assertNotNull(result);
        assertEquals(1, result.id());
        assertEquals(42, result.value());
    }

    // Provenance: RecordTypeInfo3342Test#testBooleanValueRoundTrip3786().
    void testBooleanValueRoundTrip3786Vpack() throws Exception {
        Container3786_3342<Boolean> input = new Container3786_3342<>(1, true);
        Container3786_3342<?> result = CLASS_MAPPER.readValue(
                CLASS_MAPPER.writeValueAsBytes(input),
                new TypeReference<Container3786_3342<?>>() { });
        assertNotNull(result);
        assertEquals(1, result.id());
        assertEquals(true, result.value());
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

    void __invoke_testSerializeDeserializeJsonSubType_LOWVpack() throws Exception {
        try {
            testSerializeDeserializeJsonSubType_LOWVpack();
        } finally {
        }
    }


    void __invoke_testSerializeDeserializeJsonSubType_HIGHVpack() throws Exception {
        try {
            testSerializeDeserializeJsonSubType_HIGHVpack();
        } finally {
        }
    }


    void __invoke_testDeserializeRecordWithAbstractMemberVpack() throws Exception {
        try {
            testDeserializeRecordWithAbstractMemberVpack();
        } finally {
        }
    }


    void __invoke_testAliasWithPolymorphicDeductionVpack(String field) throws Exception {
        try {
            testAliasWithPolymorphicDeductionVpack(field);
        } finally {
        }
    }


    void __invoke_testCustomObjectRoundTrip3786Vpack() throws Exception {
        try {
            testCustomObjectRoundTrip3786Vpack();
        } finally {
        }
    }


    void __invoke_testStringValueRoundTrip3786Vpack() throws Exception {
        try {
            testStringValueRoundTrip3786Vpack();
        } finally {
        }
    }


    void __invoke_testIntegerValueRoundTrip3786Vpack() throws Exception {
        try {
            testIntegerValueRoundTrip3786Vpack();
        } finally {
        }
    }


    void __invoke_testBooleanValueRoundTrip3786Vpack() throws Exception {
        try {
            testBooleanValueRoundTrip3786Vpack();
        } finally {
        }
    }

}
