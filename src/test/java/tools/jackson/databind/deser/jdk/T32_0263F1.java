package tools.jackson.databind.deser.jdk;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.deser.std.StdScalarDeserializer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0263F1 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] TRUE_PROPERTY = VPackWireFixtureTest.hex(
            "0b 0b 01 45 76 61 6c 75 65 1a 03");
private static final byte[] NESTED_TWO = VPackWireFixtureTest.hex(
            "14 0b 46 72 65 66 52 65 66 32 01");
private static final byte[] NESTED_NULL = VPackWireFixtureTest.hex(
            "14 0b 46 72 65 66 52 65 66 18 01");
private static final byte[] POLYMORPHIC_WRAPPER = VPackWireFixtureTest.hex(
            "0b 1b 01 41 77 0b 15 02 45 40 74 79 70 65 41 49 "
            + "45 76 61 6c 75 65 28 0d 03 0b 03");
private static final byte[] REFINED_DECIMAL = VPackWireFixtureTest.hex(
            "14 10 45 76 61 6c 75 65 c8 01 fe ff ff ff 25 01");
private static final byte[] CUSTOM_DESERIALIZER = VPackWireFixtureTest.hex(
            "14 10 45 76 61 6c 75 65 46 46 6f 6f 62 61 52 01");
private static final byte[] UNWRAPPED = VPackWireFixtureTest.hex(
            "0b 10 01 47 58 58 2e 6e 61 6d 65 43 42 6f 62 03");
private static final byte[] BIG_DECIMAL_4917 = VPackWireFixtureTest.hex(
            "14 23 4d 64 65 63 69 6d 61 6c 48 6f 6c 64 65 72 "
            + "c8 03 fe ff ff ff 01 00 00 46 6e 75 6d 62 65 72 28 32 02");
private static final BigDecimal BIG_DECIMAL_4694 =
            new BigDecimal("-1234." + "0".repeat(520));
private static final byte[] BIG_DECIMAL_4694_VPACK = bigDecimal4694Fixture();

    // Provenance: JDKNumberDeserTest#bigDecimal4694FromBytes().
    void bigDecimal4694FromBytes() throws Exception {
        assertEquals(BIG_DECIMAL_4694,
                MAPPER.readValue(BIG_DECIMAL_4694_VPACK, 0,
                        BIG_DECIMAL_4694_VPACK.length, BigDecimal.class));
    }

    // Provenance: JDKNumberDeserTest#bigDecimal4694FromString().
    void bigDecimal4694FromString() throws Exception {
        // VPack has no textual-number input; the logical long-decimal assertion
        // is retained through the same independently authored literal BCD value.
        assertEquals(BIG_DECIMAL_4694,
                MAPPER.readValue(BIG_DECIMAL_4694_VPACK, BigDecimal.class));
    }

    // Provenance: JDKNumberDeserTest#bigDecimal4917().
    void bigDecimal4917() throws Exception {
        DeserializationIssue4917 value = MAPPER.readValue(
                BIG_DECIMAL_4917, DeserializationIssue4917.class);
        assertEquals(new BigDecimal("100.00"), value.decimalHolder.value);
        assertEquals(50.0, value.number);
    }
private static ObjectMapper inclusionMapper(JsonInclude.Include inclusion) {
        return VPackMapper.builder()
                .changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(inclusion))
                .build();
    }
private static byte[] bigDecimal4694Fixture() {
        byte[] result = new byte[1 + 2 + 4 + 262];
        result[0] = (byte) 0xd1;
        result[1] = 0x06;
        result[2] = 0x01;
        result[3] = (byte) 0xf8;
        result[4] = (byte) 0xfd;
        result[5] = (byte) 0xff;
        result[6] = (byte) 0xff;
        result[7] = 0x12;
        result[8] = 0x34;
        return result;
    }
static class SimpleWrapper {
        public AtomicReference<Object> value;

        SimpleWrapper(Object value) {
            this.value = new AtomicReference<>(value);
        }
    }
static class MyBean2303 {
        public AtomicReference<AtomicReference<Integer>> refRef;
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
    @JsonSubTypes(@JsonSubTypes.Type(Impl.class))
    static abstract class Base { }
@JsonTypeName("I")
    static class Impl extends Base {
        public int value;

        public Impl() { }
        Impl(int value) { this.value = value; }
    }
static class RefWrapper {
        public AtomicReference<Base> w;

        public RefWrapper() { }
        RefWrapper(int value) {
            this.w = new AtomicReference<>(new Impl(value));
        }
    }
static class RefiningWrapper {
        @JsonDeserialize(contentAs = BigDecimal.class)
        public AtomicReference<Serializable> value;
    }
static class LCStringWrapper {
        @JsonDeserialize(contentUsing = LowerCasingDeserializer.class)
        public AtomicReference<String> value;
    }
static class LowerCasingDeserializer extends StdScalarDeserializer<String> {
        LowerCasingDeserializer() { super(String.class); }

        @Override
        public String deserialize(JsonParser parser, DeserializationContext ctxt) {
            return parser.getString().toLowerCase();
        }
    }
static class UnwrappingRefParent {
        @JsonUnwrapped(prefix = "XX.")
        public AtomicReference<Child> child = new AtomicReference<>(new Child());
    }
static class Child {
        public String name = "Bob";
    }
static class DeserializationIssue4917 {
        public DecimalHolder4917 decimalHolder;
        public double number;
    }
static class DecimalHolder4917 {
        public BigDecimal value;

        private DecimalHolder4917(BigDecimal value) {
            this.value = value;
        }

        @com.fasterxml.jackson.annotation.JsonCreator(
                mode = com.fasterxml.jackson.annotation.JsonCreator.Mode.DELEGATING)
        static DecimalHolder4917 of(BigDecimal value) {
            return new DecimalHolder4917(value);
        }
    }

    void __invoke_bigDecimal4694FromBytes() throws Exception {
        try {
            bigDecimal4694FromBytes();
        } finally {
        }
    }


    void __invoke_bigDecimal4694FromString() throws Exception {
        try {
            bigDecimal4694FromString();
        } finally {
        }
    }


    void __invoke_bigDecimal4917() throws Exception {
        try {
            bigDecimal4917();
        } finally {
        }
    }

}
