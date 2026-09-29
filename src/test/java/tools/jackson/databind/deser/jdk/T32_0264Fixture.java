package tools.jackson.databind.deser.jdk;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import tools.jackson.core.JsonParser;
import tools.jackson.core.exc.InputCoercionException;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.annotation.JsonDeserialize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0264Fixture {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] FLOAT_ARRAY = VPackWireFixtureTest.hex(
            "13 27 "
          + "1b 00 00 00 a0 7f c8 b5 3a "
          + "1b 00 00 00 20 33 33 f3 3f "
          + "1b 00 00 00 e0 ff ff ef 47 "
          + "1b 00 00 00 00 00 00 a0 36 04");
private static final byte[] BIG_FLOAT_ARRAY = largeFloatArrayFixture();
private static final byte[] BIG_DECIMAL_HOLDER_INT = VPackWireFixtureTest.hex(
            "14 23 4d 64 65 63 69 6d 61 6c 48 6f 6c 64 65 72 "
          + "c8 03 fe ff ff ff 01 00 00 "
          + "46 6e 75 6d 62 65 72 28 32 02");
private static final byte[] BIG_DECIMAL_DIRECT = VPackWireFixtureTest.hex(
            "14 1d 47 64 65 63 69 6d 61 6c "
          + "c8 03 fe ff ff ff 01 00 00 "
          + "46 6e 75 6d 62 65 72 28 32 02");
private static final byte[] BIG_DECIMAL_SUBTYPE = VPackWireFixtureTest.hex(
            "14 31 44 74 79 70 65 4a 4e 6f 64 65 50 61 72 65 6e 74 "
          + "44 6e 6f 64 65 14 19 46 61 6d 6f 75 6e 74 "
          + "c8 09 fe ff ff ff 99 99 99 99 99 99 99 99 99 01 02");
private static final byte[] UNWRAPPED_DECIMAL = VPackWireFixtureTest.hex(
            "14 11 45 76 61 6c 75 65 c8 02 fe ff ff ff 05 00 01");
private static final byte[] BIG_INTEGER = VPackWireFixtureTest.hex(
            "c8 14 00 00 00 00 "
          + "12 34 56 78 90 12 34 56 78 90 12 34 56 78 90 12 34 56 78 90");
private static final byte[] DECIMAL_HAPPY = VPackWireFixtureTest.hex(
            "14 1b 4c 64 65 66 61 75 6c 74 56 61 6c 75 65 "
          + "14 0b 45 76 61 6c 75 65 28 7b 01 01");
private static final byte[] DECIMAL_TEXT = VPackWireFixtureTest.hex(
            "14 1d 4c 64 65 66 61 75 6c 74 56 61 6c 75 65 "
          + "14 0d 45 76 61 6c 75 65 43 31 32 33 01 01");
private static final byte[] DECIMAL_TEXT_WITH_ID = VPackWireFixtureTest.hex(
            "14 21 42 69 64 35 4c 64 65 66 61 75 6c 74 56 61 6c 75 65 "
          + "14 0d 45 76 61 6c 75 65 43 31 32 33 01 02");

    // Provenance: JDKNumberDeserTest#testArrayOfFloatPrimitives().
    void testArrayOfFloatPrimitives() throws Exception {
        float[] values = MAPPER.readValue(FLOAT_ARRAY, float[].class);
        assertEquals(4, values.length);
        assertEquals(7.038531e-26f, values[0]);
        assertEquals(1.1999999f, values[1]);
        assertEquals(3.4028235e38f, values[2]);
        assertEquals("1.4E-45", Float.toString(values[3]));
    }

    // Provenance: JDKNumberDeserTest#testBigArrayOfFloatPrimitives().
    void testBigArrayOfFloatPrimitives() throws Exception {
        float[] values = MAPPER.readValue(BIG_FLOAT_ARRAY, float[].class);
        assertEquals(1004, values.length);
        assertEquals(7.038531e-26f, values[0]);
        assertEquals(1.1999999f, values[1]);
        assertEquals(3.4028235e38f, values[2]);
        assertEquals(7.006492321624086e-46f, values[3]);
    }

    // Provenance: JDKNumberDeserTest#testArrayOfFloats().
    void testArrayOfFloats() throws Exception {
        Float[] values = MAPPER.readValue(FLOAT_ARRAY, Float[].class);
        assertEquals(4, values.length);
        assertEquals(Float.valueOf(7.038531e-26f), values[0]);
        assertEquals(Float.valueOf(1.1999999f), values[1]);
        assertEquals(Float.valueOf(3.4028235e38f), values[2]);
        assertEquals(Float.valueOf("1.4E-45"), values[3]);
    }

    // Provenance: JDKNumberDeserTest#testBigDecimalSubtypes().
    void testBigDecimalSubtypes() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .registerSubtypes(NodeParent2644.class)
                .build();
        NodeRoot2644 root = mapper.readValue(BIG_DECIMAL_SUBTYPE, NodeRoot2644.class);
        assertEquals(new BigDecimal("9999999999999999.99"), root.node.getVal());
    }

    // Provenance: JDKNumberDeserTest#testBigDecimalUnwrapped().
    void testBigDecimalUnwrapped() throws Exception {
        NestedBigDecimalHolder2784 result = MAPPER.readValue(
                UNWRAPPED_DECIMAL, NestedBigDecimalHolder2784.class);
        assertEquals(new BigDecimal("5.00"), result.holder.value);
    }

    // Provenance: JDKNumberDeserTest#testBigIntAsNumber().
    void testBigIntAsNumber() throws Exception {
        Number result = MAPPER.readValue(BIG_INTEGER, Number.class);
        assertEquals(BigInteger.class, result.getClass());
        assertEquals(new BigInteger("1234567890123456789012345678901234567890"), result);
    }

    // Provenance: JDKNumberDeserTest#testDeserializeDecimalHappyPath().
    void testDeserializeDecimalHappyPath() throws Exception {
        MyBeanHolder result = MAPPER.readValue(DECIMAL_HAPPY, MyBeanHolder.class);
        assertEquals(new BigDecimal("123"), result.defaultValue.value.decimal);
    }

    // Provenance: JDKNumberDeserTest#testDeserializeDecimalProperException().
    void testDeserializeDecimalProperException() {
        InputCoercionException exception = org.junit.jupiter.api.Assertions.assertThrows(
                InputCoercionException.class,
                () -> MAPPER.readValue(DECIMAL_TEXT, MyBeanHolder.class));
        assertTrue(exception.getMessage().contains("not numeric"));
    }

    // Provenance: JDKNumberDeserTest#testDeserializeDecimalProperExceptionWhenIdSet().
    void testDeserializeDecimalProperExceptionWhenIdSet() {
        InputCoercionException exception = org.junit.jupiter.api.Assertions.assertThrows(
                InputCoercionException.class,
                () -> MAPPER.readValue(DECIMAL_TEXT_WITH_ID, MyBeanHolder.class));
        assertTrue(exception.getMessage().contains("not numeric"));
    }

    // Provenance: JDKNumberDeserTest#bigDecimal4917V2().
    void bigDecimal4917V2() throws Exception {
        DeserializationIssue4917V2 result = MAPPER.readValue(
                BIG_DECIMAL_HOLDER_INT, DeserializationIssue4917V2.class);
        assertEquals(new BigDecimal("100.00"), result.decimalHolder.value);
        assertEquals(50, result.number);
    }

    // Provenance: JDKNumberDeserTest#bigDecimal4917V3().
    void bigDecimal4917V3() throws Exception {
        DeserializationIssue4917V3 result = MAPPER.readValue(
                BIG_DECIMAL_DIRECT, DeserializationIssue4917V3.class);
        assertEquals(new BigDecimal("100.00"), result.decimal);
        assertEquals(50.0, result.number);
    }
private static byte[] largeFloatArrayFixture() {
        final int count = 1004;
        final int childLength = 9;
        final int length = 1 + 8 + count * childLength;
        ByteArrayOutputStream out = new ByteArrayOutputStream(length);
        out.write(0x05);
        long wireLength = length;
        for (int shift = 0; shift < 64; shift += 8) {
            out.write((int) (wireLength >>> shift) & 0xff);
        }
        writeDouble(out, (double) 7.038531e-26f);
        writeDouble(out, (double) 1.1999999f);
        writeDouble(out, (double) Float.MAX_VALUE);
        writeDouble(out, (double) Float.MIN_VALUE);
        for (int i = 4; i < count; ++i) {
            writeDouble(out, 0.0);
        }
        return out.toByteArray();
    }
private static void writeDouble(ByteArrayOutputStream out, double value) {
        long bits = Double.doubleToRawLongBits(value);
        out.write(0x1b);
        for (int shift = 0; shift < 64; shift += 8) {
            out.write((int) (bits >>> shift) & 0xff);
        }
    }
static class MyBeanHolder {
        public Long id;
        public MyBeanDefaultValue defaultValue;
    }
static class MyBeanDefaultValue {
        public MyBeanValue value;
    }
@JsonDeserialize(using = MyBeanDeserializer.class)
    static class MyBeanValue {
        public BigDecimal decimal;

        public MyBeanValue() { }
        public MyBeanValue(BigDecimal value) { decimal = value; }
    }
static class MyBeanDeserializer extends ValueDeserializer<MyBeanValue> {
        @Override
        public MyBeanValue deserialize(JsonParser parser, DeserializationContext ctxt) {
            return new MyBeanValue(parser.getDecimalValue());
        }
    }
static class NodeRoot2644 {
        public String type;
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property = "type")
        @JsonSubTypes(@JsonSubTypes.Type(value = NodeParent2644.class, name = "NodeParent"))
        public Node2644 node;
    }
public static class NodeParent2644 extends Node2644 { }
static abstract class Node2644 {
        @JsonProperty("amount")
        BigDecimal val;

        public BigDecimal getVal() { return val; }
    }
static class BigDecimalHolder2784 {
        public BigDecimal value;
    }
static class NestedBigDecimalHolder2784 {
        @JsonUnwrapped
        public BigDecimalHolder2784 holder;
    }
static class DeserializationIssue4917V2 {
        public DecimalHolder4917 decimalHolder;
        public int number;
    }
static class DeserializationIssue4917V3 {
        public BigDecimal decimal;
        public double number;
    }
static class DecimalHolder4917 {
        private final BigDecimal value;

        private DecimalHolder4917(BigDecimal value) { this.value = value; }

        @com.fasterxml.jackson.annotation.JsonCreator(
                mode = com.fasterxml.jackson.annotation.JsonCreator.Mode.DELEGATING)
        static DecimalHolder4917 of(BigDecimal value) {
            return new DecimalHolder4917(value);
        }
    }

    void __invoke_testArrayOfFloatPrimitives() throws Exception {
        try {
            testArrayOfFloatPrimitives();
        } finally {
        }
    }


    void __invoke_testBigArrayOfFloatPrimitives() throws Exception {
        try {
            testBigArrayOfFloatPrimitives();
        } finally {
        }
    }


    void __invoke_testArrayOfFloats() throws Exception {
        try {
            testArrayOfFloats();
        } finally {
        }
    }


    void __invoke_testBigDecimalSubtypes() throws Exception {
        try {
            testBigDecimalSubtypes();
        } finally {
        }
    }


    void __invoke_testBigDecimalUnwrapped() throws Exception {
        try {
            testBigDecimalUnwrapped();
        } finally {
        }
    }


    void __invoke_testBigIntAsNumber() throws Exception {
        try {
            testBigIntAsNumber();
        } finally {
        }
    }


    void __invoke_testDeserializeDecimalHappyPath() throws Exception {
        try {
            testDeserializeDecimalHappyPath();
        } finally {
        }
    }


    void __invoke_testDeserializeDecimalProperException() throws Exception {
        try {
            testDeserializeDecimalProperException();
        } finally {
        }
    }


    void __invoke_testDeserializeDecimalProperExceptionWhenIdSet() throws Exception {
        try {
            testDeserializeDecimalProperExceptionWhenIdSet();
        } finally {
        }
    }


    void __invoke_bigDecimal4917V2() throws Exception {
        try {
            bigDecimal4917V2();
        } finally {
        }
    }


    void __invoke_bigDecimal4917V3() throws Exception {
        try {
            bigDecimal4917V3();
        } finally {
        }
    }

}
