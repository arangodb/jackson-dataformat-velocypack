package tools.jackson.databind.deser.jdk;

import org.junit.jupiter.api.Test;
import tools.jackson.core.*;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.jsontype.TypeSerializer;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.testutil.DatabindTestUtil;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This unit test suite tries to verify that the "Native" java type
 * mapper can properly re-construct Java array objects from Json arrays.
 */
public class ArrayDeserializationTest
    extends DatabindTestUtil
{
    public final static class Bean1
    {
        int _x, _y;
        List<Bean2> _beans;

        // Just for deserialization:
        @SuppressWarnings("unused")
        private Bean1() { }

        public Bean1(int x, int y, List<Bean2> beans)
        {
            _x = x;
            _y = y;
            _beans = beans;
        }

        public int getX() { return _x; }
        public int getY() { return _y; }
        public List<Bean2> getBeans() { return _beans; }

        public void setX(int x) { _x = x; }
        public void setY(int y) { _y = y; }
        public void setBeans(List<Bean2> b) { _beans = b; }

        @Override public boolean equals(Object o) {
            if (!(o instanceof Bean1)) return false;
            Bean1 other = (Bean1) o;
            return (_x == other._x)
                && (_y == other._y)
                && _beans.equals(other._beans)
                ;
        }
    }

    /**
     * Simple bean that just gets serialized as a String value.
     * Deserialization from String value will be done via single-arg
     * constructor.
     */
    public final static class Bean2
        implements JacksonSerializable // so we can output as simple String
    {
        final String _desc;

        public Bean2(String d)
        {
            _desc = d;
        }

        @Override
        public void serialize(JsonGenerator gen, SerializationContext provider)
        {
            gen.writeString(_desc);
        }

        @Override public String toString() { return _desc; }

        @Override public boolean equals(Object o) {
            if (!(o instanceof Bean2)) return false;
            Bean2 other = (Bean2) o;
            return _desc.equals(other._desc);
        }

        @Override
        public void serializeWithType(JsonGenerator gen,
                SerializationContext provider, TypeSerializer typeSer) {
        }
    }

    static class ObjectWrapper {
        public Object wrapped;
    }

    static class ObjectArrayWrapper {
        public Object[] wrapped;
    }

    static class CustomNonDeserArrayDeserializer extends ValueDeserializer<NonDeserializable[]>
    {
        @Override
        public NonDeserializable[] deserialize(JsonParser p, DeserializationContext ctxt)
        {
            List<NonDeserializable> list = new ArrayList<NonDeserializable>();
            while (p.nextToken() != JsonToken.END_ARRAY) {
                list.add(new NonDeserializable(p.getString(), false));
            }
            return list.toArray(new NonDeserializable[0]);
        }
    }

    static class NonDeserializable {
        protected String value;

        public NonDeserializable(String v, boolean bogus) {
            value = v;
        }
    }

    static class Product {
        public String name;
        public List<Things> thelist;
    }

    static class Things {
        public String height;
        public String width;
    }

    static class HiddenBinaryBean890 {
        @JsonDeserialize(as=byte[].class)
        public Object someBytes;
    }

    /*
    /**********************************************************
    /* Tests for "untyped" arrays, Object[]
    /**********************************************************
     */

    private final ObjectMapper MAPPER = newVPackMapper();

    @Test
    public void testUntypedArray() throws Exception
    {

        // to get "untyped" default map-to-map, pass Object[].class
        String JSON = "[ 1, null, \"x\", true, 2.0 ]";

        Object[] result = MAPPER.readValue(VPackUtils.toVPack(JSON), Object[].class);
        assertNotNull(result);

        assertEquals(5, result.length);

        assertEquals(Integer.valueOf(1), result[0]);
        assertNull(result[1]);
        assertEquals("x", result[2]);
        assertEquals(Boolean.TRUE, result[3]);
        assertEquals(Double.valueOf(2.0), result[4]);
    }

    @Test
    public void testIntegerArray() throws Exception
    {
        final int LEN = 90000;

        // Let's construct array to get it big enough

        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < LEN; ++i) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(i);
        }
        sb.append(']');

        Integer[] result = MAPPER.readValue(VPackUtils.toVPack(sb.toString()), Integer[].class);
        assertNotNull(result);

        assertEquals(LEN, result.length);
        for (int i = 0; i < LEN; ++i) {
            assertEquals(i, result[i].intValue());
        }
    }

    // allow "" to mean 'null' for Arrays, List and Maps
    @Test
    public void testFromEmptyString() throws Exception
    {
        ObjectReader r = MAPPER.reader()
                .with(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        assertNull(r.forType(Object[].class).readValue(VPackUtils.toVPack(q(""))));
        assertNull(r.forType(String[].class).readValue(VPackUtils.toVPack(q(""))));
        assertNull(r.forType(int[].class).readValue(VPackUtils.toVPack(q(""))));
    }

    // allow "" to mean 'null' for Arrays, List and Maps
    @Test
    public void testFromEmptyString2() throws Exception
    {
        ObjectMapper m = vpackMapperBuilder()
                .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT,
                        DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                .build();
        Product p = m.readValue(VPackUtils.toVPack("{\"thelist\":\"\"}"), Product.class);
        assertNotNull(p);
        assertNull(p.thelist);
    }

    /*
    /**********************************************************
    /* Arrays of arrays...
    /**********************************************************
     */

    @Test
    public void testUntypedArrayOfArrays() throws Exception
    {
        // to get "untyped" default map-to-map, pass Object[].class
        final String JSON = "[[[-0.027512,51.503221],[-0.008497,51.503221],[-0.008497,51.509744],[-0.027512,51.509744]]]";

        Object result = MAPPER.readValue(VPackUtils.toVPack(JSON), Object.class);
        assertEquals(ArrayList.class, result.getClass());
        assertNotNull(result);

        // Should be able to get it as an Object array as well

        Object[] array = MAPPER.readValue(VPackUtils.toVPack(JSON), Object[].class);
        assertNotNull(array);
        assertEquals(Object[].class, array.getClass());

        // and as wrapped variants too
        ObjectWrapper w = MAPPER.readValue(VPackUtils.toVPack("{\"wrapped\":"+JSON+"}"), ObjectWrapper.class);
        assertNotNull(w);
        assertNotNull(w.wrapped);
        assertEquals(ArrayList.class, w.wrapped.getClass());

        ObjectArrayWrapper aw = MAPPER.readValue(VPackUtils.toVPack("{\"wrapped\":"+JSON+"}"), ObjectArrayWrapper.class);
        assertNotNull(aw);
        assertNotNull(aw.wrapped);
    }

    /*
    /**********************************************************
    /* Tests for String arrays, char[]
    /**********************************************************
     */

    @Test
    public void testStringArray() throws Exception
    {
        final String[] STRS = new String[] {
            "a", "b", "abcd", "", "???", "\"quoted\"", "lf: \n",
        };
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JsonGenerator g = MAPPER.createGenerator(out);
        g.writeStartArray();
        for (String str : STRS) {
            g.writeString(str);
        }
        g.writeEndArray();
        g.close();

        String[] result = MAPPER.readValue(out.toByteArray(), String[].class);
        assertNotNull(result);

        assertEquals(STRS.length, result.length);
        for (int i = 0; i < STRS.length; ++i) {
            assertEquals(STRS[i], result[i]);
        }

        // [#479]: null handling was busted in 2.4.0
        result = MAPPER.readValue(VPackUtils.toVPack(" [ null ]"), String[].class);
        assertNotNull(result);
        assertEquals(1, result.length);
        assertNull(result[0]);
    }

    @Test
    public void testCharArray() throws Exception
    {
        final String TEST_STR = "Let's just test it? Ok!";
        char[] result = MAPPER.readValue(VPackUtils.toVPack("\""+TEST_STR+"\""), char[].class);
        assertEquals(TEST_STR, new String(result));

        // And just for [JACKSON-289], let's verify that fluffy arrays work too
        result = MAPPER.readValue(VPackUtils.toVPack("[\"a\",\"b\",\"c\"]"), char[].class);
        assertEquals("abc", new String(result));
    }

    /*
    /**********************************************************
    /* Tests for primitive arrays
    /**********************************************************
     */

    @Test
    public void testBooleanArray() throws Exception
    {
        boolean[] result = MAPPER.readValue(VPackUtils.toVPack("[ true, false, false ]"), boolean[].class);
        assertNotNull(result);
        assertEquals(3, result.length);
        assertTrue(result[0]);
        assertFalse(result[1]);
        assertFalse(result[2]);
    }

    @Test
    public void testByteArrayAsNumbers() throws Exception
    {
        final int LEN = 37000;
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < LEN; ++i) {
            int value = i - 128;
            sb.append((value < 256) ? value : (value & 0x7F));
            sb.append(',');
        }
        sb.append("0]");
        byte[] result = MAPPER.readValue(VPackUtils.toVPack(sb.toString()), byte[].class);
        assertNotNull(result);
        assertEquals(LEN+1, result.length);
        for (int i = 0; i < LEN; ++i) {
            int value = i - 128;
            byte exp = (byte) ((value < 256) ? value : (value & 0x7F));
            if (exp != result[i]) {
                fail("At offset #"+i+" ("+result.length+"), expected "+exp+", got "+result[i]);
            }
            assertEquals(exp, result[i]);
        }
        assertEquals(0, result[LEN]);
    }

    @Test
    public void testByteArrayAsBase64() throws Exception
    {
        /* Hmmh... let's use JsonGenerator here, to hopefully ensure we
         * get proper base64 encoding. Plus, not always using that
         * silly sample from Wikipedia.
         */
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        int LEN = 9000;
        byte[] TEST = new byte[LEN];
        for (int i = 0; i < LEN; ++i) {
            TEST[i] = (byte) i;
        }

        JsonGenerator g = MAPPER.createGenerator(out);
        g.writeBinary(TEST);
        g.close();

        byte[] result = MAPPER.readValue(out.toByteArray(), byte[].class);
        assertNotNull(result);
        assertArrayEquals(TEST, result);
    }

    /**
     * And then bit more challenging case; let's try decoding
     * multiple byte arrays from an array...
     */
    @Test
    public void testByteArraysAsBase64() throws Exception
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        final int entryCount = 15;

        JsonGenerator g = MAPPER.createGenerator(out);
        g.writeStartArray();

        byte[][] entries = new byte[entryCount][];
        for (int i = 0; i < entryCount; ++i) {
            byte[] b = new byte[1000 - i * 20];
            for (int x = 0; x < b.length; ++x) {
                b[x] = (byte) (i + x);
            }
            entries[i] = b;
            g.writeBinary(b);
        }
        g.writeEndArray();
        g.close();

        byte[][] result = MAPPER.readValue(out.toByteArray(), byte[][].class);
        assertNotNull(result);

        assertEquals(entryCount, result.length);
        for (int i = 0; i < entryCount; ++i) {
            byte[] b = result[i];
            assertArrayEquals(entries[i], b, "Comparing entry #"+i+"/"+entryCount);
        }
    }

    // [JACKSON-763]
    @Test
    public void testByteArraysWith763() throws Exception
    {
        String[] input = new String[] { "YQ==", "Yg==", "Yw==" };
        byte[][] data = MAPPER.convertValue(input, byte[][].class);
        assertEquals("a", new String(data[0], "US-ASCII"));
        assertEquals("b", new String(data[1], "US-ASCII"));
        assertEquals("c", new String(data[2], "US-ASCII"));
    }

    @Test
    public void testShortArray() throws Exception
    {
        final int LEN = 31001; // fits in signed 16-bit
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < LEN; ++i) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(i);
        }
        sb.append(']');

        short[] result = MAPPER.readValue(VPackUtils.toVPack(sb.toString()), short[].class);
        assertNotNull(result);

        assertEquals(LEN, result.length);
        for (int i = 0; i < LEN; ++i) {
            short exp = (short) i;
            assertEquals(exp, result[i]);
        }
    }

    @Test
    public void testIntArray() throws Exception
    {
        final int LEN = 70000;

        // Let's construct array to get it big enough

        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < LEN; ++i) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(-i);
        }
        sb.append(']');

        int[] result = MAPPER.readValue(VPackUtils.toVPack(sb.toString()), int[].class);
        assertNotNull(result);

        assertEquals(LEN, result.length);
        for (int i = 0; i < LEN; ++i) {
            assertEquals(-i, result[i]);
        }
    }

    @Test
    public void testLongArray() throws Exception
    {
        final int LEN = 12300;
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < LEN; ++i) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(i);
        }
        sb.append(']');

        long[] result = MAPPER.readValue(VPackUtils.toVPack(sb.toString()), long[].class);
        assertNotNull(result);

        assertEquals(LEN, result.length);
        for (int i = 0; i < LEN; ++i) {
            long exp = (long) i;
            assertEquals(exp, result[i]);
        }
    }

    @Test
    public void testDoubleArray() throws Exception
    {
        final int LEN = 7000;
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < LEN; ++i) {
            // not ideal, but has to do...
            if (i > 0) {
                sb.append(',');
            }
            sb.append(i).append('.').append(i % 10);
        }
        sb.append(']');

        double[] result = MAPPER.readValue(VPackUtils.toVPack(sb.toString()), double[].class);
        assertNotNull(result);

        assertEquals(LEN, result.length);
        for (int i = 0; i < LEN; ++i) {
            String expStr = String.valueOf(i) + "." + String.valueOf(i % 10);
            String actStr = String.valueOf(result[i]);
            if (!expStr.equals(actStr)) {
                fail("Entry #"+i+"/"+LEN+"; exp '"+expStr+"', got '"+actStr+"'");
            }
        }
    }

    @Test
    public void testFloatArray() throws Exception
    {
        final int LEN = 7000;
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < LEN; ++i) {
            if (i > 0) {
                sb.append(',');
            }
            // not ideal, but has to do...
            sb.append(i).append('.').append(i % 10);
        }
        sb.append(']');

        float[] result = MAPPER.readValue(VPackUtils.toVPack(sb.toString()), float[].class);
        assertNotNull(result);

        assertEquals(LEN, result.length);
        for (int i = 0; i < LEN; ++i) {
            String expStr = String.valueOf(i) + "." + String.valueOf(i % 10);
            assertEquals(expStr, String.valueOf(result[i]));
        }
    }

    @Test
    public void testSingleStringToPrimitiveArray() throws Exception {
       final ObjectMapper mapper = vpackMapperBuilder()
                .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                .build();
        assertLengthValue(mapper.readValue(VPackUtils.toVPack("\"true\""), boolean[].class), true);
        assertLengthValue(mapper.readValue(VPackUtils.toVPack("\"a\""), char[].class), 'a');
        assertLengthValue(mapper.readValue(VPackUtils.toVPack("\"1\""), short[].class), (short) 1);
        assertLengthValue(mapper.readValue(VPackUtils.toVPack("\"1\""), int[].class), 1);
        assertLengthValue(mapper.readValue(VPackUtils.toVPack("\"1\""), long[].class), 1L);
       // 06-Aug-2025, tatu: with [databind#5242] will try to access String
        //   as Base64-encoded bytes. Need to think of whether to try to 
        //   support Float/Double-as-single-String case or not; for now, not
        //assertLengthValue(MAPPER.readValue(VPackUtils.toVPack("\"7.038531e-26\""), float[].class), 7.038531e-26f);
        //assertLengthValue(MAPPER.readValue(VPackUtils.toVPack("\"1.5555\""), double[].class), 1.5555d);
    }

    private void assertLengthValue(boolean[] arr, boolean expt) {
        assertEquals(1, arr.length);
        assertEquals(expt, arr[0]);
    }

    private void assertLengthValue(char[] arr, char expt) {
        assertEquals(1, arr.length);
        assertEquals(expt, arr[0]);
    }

    private void assertLengthValue(short[] arr, short expt) {
        assertEquals(1, arr.length);
        assertEquals(expt, arr[0]);
    }

    private void assertLengthValue(int[] arr, int expt) {
        assertEquals(1, arr.length);
        assertEquals(expt, arr[0]);
    }

    private void assertLengthValue(long[] arr, long expt) {
        assertEquals(1, arr.length);
        assertEquals(expt, arr[0]);
    }

    void assertLengthValue(float[] arr, float expt) {
        assertEquals(1, arr.length);
        assertEquals(expt, arr[0]);
    }

    void assertLengthValue(double[] arr, double expt) {
        assertEquals(1, arr.length);
        assertEquals(expt, arr[0]);
    }

    /*
    /**********************************************************
    /* Tests for Bean arrays
    /**********************************************************
     */

    @Test
    public void testBeanArray()
        throws Exception
    {
        List<Bean1> src = new ArrayList<Bean1>();

        List<Bean2> b2 = new ArrayList<Bean2>();
        b2.add(new Bean2("a"));
        b2.add(new Bean2("foobar"));
        src.add(new Bean1(1, 2, b2));

        b2 = new ArrayList<Bean2>();
        b2.add(null);
        src.add(new Bean1(4, 5, b2));

        // Ok: let's assume bean serializer works ok....
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        MAPPER.writeValue(out, src);

        // And then test de-serializer
        List<Bean1> result = MAPPER.readValue(out.toByteArray(), new TypeReference<List<Bean1>>() { });
        assertNotNull(result);
        assertEquals(src, result);
    }

    /*
    /**********************************************************
    /* And special cases for byte array (base64 encoded)
    /**********************************************************
     */

    // for [databind#890]
    @Test
    public void testByteArrayTypeOverride890() throws Exception
    {
        HiddenBinaryBean890 result = MAPPER.readValue(
                VPackUtils.toVPack(a2q("{'someBytes':'AQIDBA=='}")), HiddenBinaryBean890.class);
        assertNotNull(result);
        assertNotNull(result.someBytes);
        assertEquals(byte[].class, result.someBytes.getClass());
    }

    /*
    /**********************************************************
    /* And custom deserializers too
    /**********************************************************
     */

    @Test
    public void testCustomDeserializers() throws Exception
    {
        SimpleModule testModule = new SimpleModule("test", Version.unknownVersion());
        testModule.addDeserializer(NonDeserializable[].class, new CustomNonDeserArrayDeserializer());
        ObjectMapper mapper = vpackMapperBuilder()
                .addModule(testModule)
                .build();

        NonDeserializable[] result = mapper.readValue(VPackUtils.toVPack("[\"a\"]"), NonDeserializable[].class);
        assertNotNull(result);
        assertEquals(1, result.length);
        assertEquals("a", result[0].value);
    }
}
