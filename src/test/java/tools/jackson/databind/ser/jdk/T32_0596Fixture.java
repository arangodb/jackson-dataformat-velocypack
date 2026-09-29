package tools.jackson.databind.ser.jdk;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonSerialize;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0596Fixture {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final ObjectMapper ACCEPT_SINGLE_MAPPER = VPackMapper.builder()
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .build();

    // Provenance: NumberSerTest#testLongArray().
    void testLongArrayVpack() throws Exception {
        byte[] primitive = VPackWireFixtureTest.hex("02 06 20 85 28 2a");
        byte[] boxed = VPackWireFixtureTest.hex(
                "06 0a 02 28 7b 21 19 fc 03 05");

        assertArrayEquals(primitive, MAPPER.writeValueAsBytes(new long[] { -123, 42 }));
        assertArrayEquals(boxed, MAPPER.writeValueAsBytes(new Long[] { 123L, -999L }));
        assertArrayEquals(new long[] { -123, 42 }, MAPPER.readValue(primitive, long[].class));
        assertArrayEquals(new Long[] { 123L, -999L }, MAPPER.readValue(boxed, Long[].class));
    }

    // Provenance: NumberSerTest#testNumberType().
    void testNumberTypeVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0b 01 45 76 61 6c 75 65 31 03"),
                MAPPER.writeValueAsBytes(new NumberWrapper(Byte.valueOf((byte) 1))));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0b 01 45 76 61 6c 75 65 32 03"),
                MAPPER.writeValueAsBytes(new NumberWrapper(Short.valueOf((short) 2))));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0b 01 45 76 61 6c 75 65 33 03"),
                MAPPER.writeValueAsBytes(new NumberWrapper(Integer.valueOf(3))));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0b 01 45 76 61 6c 75 65 34 03"),
                MAPPER.writeValueAsBytes(new NumberWrapper(Long.valueOf(4L))));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 13 01 45 76 61 6c 75 65 1b 00 00 00 00 00 00 e0 3f 03"),
                MAPPER.writeValueAsBytes(new NumberWrapper(Float.valueOf(0.5f))));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 13 01 45 76 61 6c 75 65 1b 9a 99 99 99 99 99 a9 3f 03"),
                MAPPER.writeValueAsBytes(new NumberWrapper(Double.valueOf(0.05))));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0c 01 45 76 61 6c 75 65 28 7b 03"),
                MAPPER.writeValueAsBytes(new NumberWrapper(BigInteger.valueOf(123))));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 11 01 45 76 61 6c 75 65 c8 01 fd ff ff ff 25 03"),
                MAPPER.writeValueAsBytes(new NumberWrapper(BigDecimal.valueOf(0.025))));
    }

    // Provenance: NumberSerTest#testNumbersAsString().
    void testNumbersAsStringVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0c 01 45 76 61 6c 75 65 41 33 03"),
                MAPPER.writeValueAsBytes(new IntAsString()));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0c 01 45 76 61 6c 75 65 41 34 03"),
                MAPPER.writeValueAsBytes(new LongAsString()));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0f 01 45 76 61 6c 75 65 44 2d 30 2e 35 03"),
                MAPPER.writeValueAsBytes(new DoubleAsString()));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0f 01 45 76 61 6c 75 65 44 30 2e 32 35 03"),
                MAPPER.writeValueAsBytes(new BigDecimalAsString()));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 11 01 45 76 61 6c 75 65 46 31 32 33 34 35 36 03"),
                MAPPER.writeValueAsBytes(new BigIntegerAsString()));
    }

    // Provenance: NumberSerTest#testNumbersAsStringNonEmpty().
    void testNumbersAsStringNonEmptyVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .changeDefaultPropertyInclusion(v -> v.withValueInclusion(
                        com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY))
                .build();

        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0c 01 45 76 61 6c 75 65 41 33 03"),
                mapper.writeValueAsBytes(new IntAsString()));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0c 01 45 76 61 6c 75 65 41 34 03"),
                mapper.writeValueAsBytes(new LongAsString()));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0f 01 45 76 61 6c 75 65 44 2d 30 2e 35 03"),
                mapper.writeValueAsBytes(new DoubleAsString()));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0f 01 45 76 61 6c 75 65 44 30 2e 32 35 03"),
                mapper.writeValueAsBytes(new BigDecimalAsString()));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 11 01 45 76 61 6c 75 65 46 31 32 33 34 35 36 03"),
                mapper.writeValueAsBytes(new BigIntegerAsString()));
    }

    // Provenance: NumberSerTest#testShortArray().
    void testShortArrayVpack() throws Exception {
        byte[] primitive = VPackWireFixtureTest.hex("02 04 30 31");
        byte[] boxed = VPackWireFixtureTest.hex("02 04 32 33");

        assertArrayEquals(primitive, MAPPER.writeValueAsBytes(new short[] { 0, 1 }));
        assertArrayEquals(boxed, MAPPER.writeValueAsBytes(new Short[] { 2, 3 }));
        assertArrayEquals(new short[] { 0, 1 }, MAPPER.readValue(primitive, short[].class));
        assertArrayEquals(new Short[] { 2, 3 }, MAPPER.readValue(boxed, Short[].class));
    }
private static final byte[] OBJECT_ID_INPUT = VPackWireFixtureTest.hex(
            "0b 29 02 4a 63 6f 6c 6c 65 63 74 69 6f 6e 31"
          + "45 76 61 6c 75 65 0b 12 02 43 40 69 64 31"
          + "45 65 6e 74 72 79 41 73 03 08 03 0f");
private static final byte[] OBJECT_ID_ARRAY_INPUT = VPackWireFixtureTest.hex(
            "0b 24 02 45 61 72 72 61 79 31 45 76 61 6c 75 65"
          + "0b 12 02 43 40 69 64 31 45 65 6e 74 72 79 41 73 03 08 03 0a");
private static final byte[] ROLES_INPUT = VPackWireFixtureTest.hex(
            "0b 20 01 45 72 6f 6c 65 73 0b 16 02 44 4e 61 6d 65"
          + "44 55 73 65 72 42 49 44 43 33 33 33 0d 03 03");
static class IntAsString {
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        @JsonProperty("value")
        public int foo = 3;
    }
static class LongAsString {
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        public long value = 4;
    }
static class DoubleAsString {
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        public double value = -0.5;
    }
static class BigDecimalAsString {
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        public BigDecimal value = BigDecimal.valueOf(0.25);
    }
static class BigIntegerAsString {
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        public BigInteger value = BigInteger.valueOf(123456L);
    }
static class NumberWrapper {
        @JsonSerialize(as = Number.class)
        public Number value;

        NumberWrapper(Number value) { this.value = value; }
    }
static class BooleanArrayWrapper {
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        public boolean[] values;
    }
static class DoubleArrayWrapper {
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        public double[] values;
    }
static class RolesInArray {
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        public Role[] roles;
    }
static class RolesInList {
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        public List<Role> roles;
    }
static class Role {
        public String ID;
        public String Name;
    }
static class Bean5537 {
        Collection<IdentifiedType5537> collection;
        IdentifiedType5537 value;

        public void setValue(IdentifiedType5537 value) { this.value = value; }
        public void setCollection(Collection<IdentifiedType5537> collection) {
            this.collection = collection;
        }
    }
static class Bean5541 {
        IdentifiedType5537[] array;
        IdentifiedType5537 value;

        public void setValue(IdentifiedType5537 value) { this.value = value; }
        public void setArray(IdentifiedType5537[] array) { this.array = array; }
    }
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
    static class IdentifiedType5537 {
        String entry;

        @com.fasterxml.jackson.annotation.JsonCreator
        IdentifiedType5537(@JsonProperty("entry") String entry) {
            this.entry = entry;
        }
    }

    void __invoke_testLongArrayVpack() throws Exception {
        try {
            testLongArrayVpack();
        } finally {
        }
    }


    void __invoke_testNumberTypeVpack() throws Exception {
        try {
            testNumberTypeVpack();
        } finally {
        }
    }


    void __invoke_testNumbersAsStringVpack() throws Exception {
        try {
            testNumbersAsStringVpack();
        } finally {
        }
    }


    void __invoke_testNumbersAsStringNonEmptyVpack() throws Exception {
        try {
            testNumbersAsStringNonEmptyVpack();
        } finally {
        }
    }


    void __invoke_testShortArrayVpack() throws Exception {
        try {
            testShortArrayVpack();
        } finally {
        }
    }

}
