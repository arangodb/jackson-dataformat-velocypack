package tools.jackson.databind.struct;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0596Fixture {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final ObjectMapper ACCEPT_SINGLE_MAPPER = VPackMapper.builder()
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .build();

    // Provenance: FormatFeatureAcceptSingleTest#testArrayWithObjectId().
    void testArrayWithObjectIdVpack() throws Exception {
        Bean5541 result = ACCEPT_SINGLE_MAPPER.readValue(OBJECT_ID_ARRAY_INPUT, Bean5541.class);
        assertNotNull(result);
        assertNotNull(result.value);
        assertEquals(1, result.array.length);
        assertNotNull(result.array[0]);
        assertEquals("s", result.array[0].entry);
    }

    // Provenance: FormatFeatureAcceptSingleTest#testCollectionWithObjectId().
    void testCollectionWithObjectIdVpack() throws Exception {
        Bean5537 result = ACCEPT_SINGLE_MAPPER.readValue(OBJECT_ID_INPUT, Bean5537.class);
        assertNotNull(result);
        assertNotNull(result.value);
        assertEquals(1, result.collection.size());
        assertNotNull(result.collection.iterator().next());
        assertEquals("s", result.collection.iterator().next().entry);
    }

    // Provenance: FormatFeatureAcceptSingleTest#testPrimitives().
    void testPrimitivesVpack() throws Exception {
        int[] i = ACCEPT_SINGLE_MAPPER.readValue(VPackWireFixtureTest.hex("28 10"), int[].class);
        assertEquals(1, i.length);
        assertEquals(16, i[0]);

        long[] l = ACCEPT_SINGLE_MAPPER.readValue(
                VPackWireFixtureTest.hex("29 d2 04"), long[].class);
        assertEquals(1, l.length);
        assertEquals(1234L, l[0]);

        double[] d = ACCEPT_SINGLE_MAPPER.readValue(VPackWireFixtureTest.hex(
                "1b 00 00 00 00 00 00 29 40"), double[].class);
        assertEquals(1, d.length);
        assertEquals(12.5, d[0]);

        boolean[] b = ACCEPT_SINGLE_MAPPER.readValue(
                VPackWireFixtureTest.hex("1a"), boolean[].class);
        assertEquals(1, b.length);
        assertTrue(b[0]);
    }

    // Provenance: FormatFeatureAcceptSingleTest#testSingleBooleanArrayRead().
    void testSingleBooleanArrayReadVpack() throws Exception {
        BooleanArrayWrapper result = MAPPER.readValue(
            VPackWireFixtureTest.hex("0b 0c 01 46 76 61 6c 75 65 73 1a 03"),
                BooleanArrayWrapper.class);
        assertNotNull(result.values);
        assertEquals(1, result.values.length);
        assertTrue(result.values[0]);
    }

    // Provenance: FormatFeatureAcceptSingleTest#testSingleDoubleArrayRead().
    void testSingleDoubleArrayReadVpack() throws Exception {
        DoubleArrayWrapper result = MAPPER.readValue(VPackWireFixtureTest.hex(
                "0b 14 01 46 76 61 6c 75 65 73 1b 00 00 00 00 00 00 e0 bf 03"),
                DoubleArrayWrapper.class);
        assertNotNull(result.values);
        assertEquals(1, result.values.length);
        assertEquals(-0.5, result.values[0]);
    }

    // Provenance: FormatFeatureAcceptSingleTest#testSingleElementArrayRead().
    void testSingleElementArrayReadVpack() throws Exception {
        RolesInArray result = MAPPER.readValue(ROLES_INPUT, RolesInArray.class);
        assertNotNull(result.roles);
        assertEquals(1, result.roles.length);
        assertEquals("333", result.roles[0].ID);
    }

    // Provenance: FormatFeatureAcceptSingleTest#testSingleElementListRead().
    void testSingleElementListReadVpack() throws Exception {
        RolesInList result = MAPPER.readValue(ROLES_INPUT, RolesInList.class);
        assertNotNull(result.roles);
        assertEquals(1, result.roles.size());
        assertEquals("333", result.roles.get(0).ID);
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

    void __invoke_testArrayWithObjectIdVpack() throws Exception {
        try {
            testArrayWithObjectIdVpack();
        } finally {
        }
    }


    void __invoke_testCollectionWithObjectIdVpack() throws Exception {
        try {
            testCollectionWithObjectIdVpack();
        } finally {
        }
    }


    void __invoke_testPrimitivesVpack() throws Exception {
        try {
            testPrimitivesVpack();
        } finally {
        }
    }


    void __invoke_testSingleBooleanArrayReadVpack() throws Exception {
        try {
            testSingleBooleanArrayReadVpack();
        } finally {
        }
    }


    void __invoke_testSingleDoubleArrayReadVpack() throws Exception {
        try {
            testSingleDoubleArrayReadVpack();
        } finally {
        }
    }


    void __invoke_testSingleElementArrayReadVpack() throws Exception {
        try {
            testSingleElementArrayReadVpack();
        } finally {
        }
    }


    void __invoke_testSingleElementListReadVpack() throws Exception {
        try {
            testSingleElementListReadVpack();
        } finally {
        }
    }

}
