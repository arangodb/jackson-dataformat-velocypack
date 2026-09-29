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
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0263F0 {
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

    // Provenance: JDKAtomicTypesDeserTest#testNullWithinNested().
    void testNullWithinNested() throws Exception {
        MyBean2303 value = MAPPER.readValue(NESTED_TWO, MyBean2303.class);
        assertNotNull(value.refRef);
        assertNotNull(value.refRef.get());
        assertEquals(Integer.valueOf(2), value.refRef.get().get());

        value = MAPPER.readValue(NESTED_NULL, MyBean2303.class);
        assertNotNull(value.refRef);
        assertNotNull(value.refRef.get());
        assertNull(value.refRef.get().get());
    }

    // Provenance: JDKAtomicTypesDeserTest#testPolymorphicAtomicReference().
    void testPolymorphicAtomicReference() throws Exception {
        RefWrapper value = MAPPER.readValue(POLYMORPHIC_WRAPPER, RefWrapper.class);
        assertNotNull(value.w);
        assertEquals(Impl.class, value.w.get().getClass());
        assertEquals(13, ((Impl) value.w.get()).value);
        assertArrayEquals(POLYMORPHIC_WRAPPER,
                MAPPER.writeValueAsBytes(new RefWrapper(13)));
    }

    // Provenance: JDKAtomicTypesDeserTest#testSerPropInclusionAlways().
    void testSerPropInclusionAlways() throws Exception {
        ObjectMapper mapper = inclusionMapper(JsonInclude.Include.ALWAYS);
        assertArrayEquals(TRUE_PROPERTY, mapper.writeValueAsBytes(new SimpleWrapper(true)));
    }

    // Provenance: JDKAtomicTypesDeserTest#testSerPropInclusionNonAbsent().
    void testSerPropInclusionNonAbsent() throws Exception {
        ObjectMapper mapper = inclusionMapper(JsonInclude.Include.NON_ABSENT);
        assertArrayEquals(TRUE_PROPERTY, mapper.writeValueAsBytes(new SimpleWrapper(true)));
    }

    // Provenance: JDKAtomicTypesDeserTest#testSerPropInclusionNonEmpty().
    void testSerPropInclusionNonEmpty() throws Exception {
        ObjectMapper mapper = inclusionMapper(JsonInclude.Include.NON_EMPTY);
        assertArrayEquals(TRUE_PROPERTY, mapper.writeValueAsBytes(new SimpleWrapper(true)));
    }

    // Provenance: JDKAtomicTypesDeserTest#testSerPropInclusionNonNull().
    void testSerPropInclusionNonNull() throws Exception {
        ObjectMapper mapper = inclusionMapper(JsonInclude.Include.NON_NULL);
        assertArrayEquals(TRUE_PROPERTY, mapper.writeValueAsBytes(new SimpleWrapper(true)));
    }

    // Provenance: JDKAtomicTypesDeserTest#testTypeRefinement().
    void testTypeRefinement() throws Exception {
        RefiningWrapper value = MAPPER.readValue(REFINED_DECIMAL, RefiningWrapper.class);
        assertNotNull(value.value);
        assertEquals(BigDecimal.class, value.value.get().getClass());
        assertEquals(new BigDecimal("0.25"), value.value.get());
    }

    // Provenance: JDKAtomicTypesDeserTest#testWithCustomDeserializer().
    void testWithCustomDeserializer() throws Exception {
        LCStringWrapper value = MAPPER.readValue(CUSTOM_DESERIALIZER, LCStringWrapper.class);
        assertEquals("foobar", value.value.get());
    }

    // Provenance: JDKAtomicTypesDeserTest#testWithUnwrapping().
    void testWithUnwrapping() throws Exception {
        assertArrayEquals(UNWRAPPED, MAPPER.writeValueAsBytes(new UnwrappingRefParent()));
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

    void __invoke_testNullWithinNested() throws Exception {
        try {
            testNullWithinNested();
        } finally {
        }
    }


    void __invoke_testPolymorphicAtomicReference() throws Exception {
        try {
            testPolymorphicAtomicReference();
        } finally {
        }
    }


    void __invoke_testSerPropInclusionAlways() throws Exception {
        try {
            testSerPropInclusionAlways();
        } finally {
        }
    }


    void __invoke_testSerPropInclusionNonAbsent() throws Exception {
        try {
            testSerPropInclusionNonAbsent();
        } finally {
        }
    }


    void __invoke_testSerPropInclusionNonEmpty() throws Exception {
        try {
            testSerPropInclusionNonEmpty();
        } finally {
        }
    }


    void __invoke_testSerPropInclusionNonNull() throws Exception {
        try {
            testSerPropInclusionNonNull();
        } finally {
        }
    }


    void __invoke_testTypeRefinement() throws Exception {
        try {
            testTypeRefinement();
        } finally {
        }
    }


    void __invoke_testWithCustomDeserializer() throws Exception {
        try {
            testWithCustomDeserializer();
        } finally {
        }
    }


    void __invoke_testWithUnwrapping() throws Exception {
        try {
            testWithUnwrapping();
        } finally {
        }
    }

}
