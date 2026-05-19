package tools.jackson.databind.deser.jdk;

import com.fasterxml.jackson.annotation.*;
import org.junit.jupiter.api.Test;
import tools.jackson.core.*;
import tools.jackson.core.exc.InputCoercionException;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.testutil.DatabindTestUtil;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class JDKNumberDeserTest
    extends DatabindTestUtil
{
    /*
    /**********************************************************************
    /* Helper classes, beans
    /**********************************************************************
     */

    static class MyBeanHolder {
        public Long id;
        public MyBeanDefaultValue defaultValue;
    }

    static class MyBeanDefaultValue
    {
        public MyBeanValue value;
    }

    @JsonDeserialize(using=MyBeanDeserializer.class)
    static class MyBeanValue {
        public BigDecimal decimal;
        public MyBeanValue() { this(null); }
        public MyBeanValue(BigDecimal d) { this.decimal = d; }
    }

    // [databind#2644]
    static class NodeRoot2644 {
        public String type;

        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type")
        @JsonSubTypes(value = {
                @JsonSubTypes.Type(value = NodeParent2644.class, name = "NodeParent")
        })
        public Node2644 node;
    }

    public static class NodeParent2644 extends Node2644 { }

    public static abstract class Node2644 {
        @JsonProperty("amount")
        BigDecimal val;

        public BigDecimal getVal() {
            return val;
        }

        public void setVal(BigDecimal val) {
            this.val = val;
        }
    }

    // [databind#2784]
    static class BigDecimalHolder2784 {
        public BigDecimal value;
    }

    static class NestedBigDecimalHolder2784 {
        @JsonUnwrapped
        public BigDecimalHolder2784 holder;
    }

    static class DeserializationIssue4917 {
        public DecimalHolder4917 decimalHolder;
        public double number;
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
        public BigDecimal value;

        private DecimalHolder4917(BigDecimal value) {
            this.value = value;
        }

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        static DecimalHolder4917 of(BigDecimal value) {
            return new DecimalHolder4917(value);
        }
    }

    static class Point {
        private Double x;
        private Double y;

        public Double getX() {
            return x;
        }

        public void setX(Double x) {
            this.x = x;
        }

        public Double getY() {
            return y;
        }

        public void setY(Double y) {
            this.y = y;
        }
    }

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXISTING_PROPERTY,
            property = "type",
            visible = true)
    @JsonSubTypes(@JsonSubTypes.Type(value = CenterResult.class, name = "center"))
    static abstract class Result {
        private String type;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    static class CenterResult extends Result {
        private Point center;

        private Double radius;

        public Double getRadius() {
            return radius;
        }

        public void setRadius(Double radius) {
            this.radius = radius;
        }

        public Point getCenter() {
            return center;
        }

        public void setCenter(Point center) {
            this.center = center;
        }
    }

    static class Root {
        private Result[] results;

        public Result[] getResults() {
            return results;
        }

        public void setResults(Result[] results) {
            this.results = results;
        }
    }

    /*
    /**********************************************************************
    /* Helper classes, serializers/deserializers/resolvers
    /**********************************************************************
     */

    static class MyBeanDeserializer extends ValueDeserializer<MyBeanValue>
    {
        @Override
        public MyBeanValue deserialize(JsonParser jp, DeserializationContext ctxt)
        {
            return new MyBeanValue(jp.getDecimalValue());
        }
    }

    /*
    /**********************************************************************
    /* Unit tests
    /**********************************************************************
     */

    private final ObjectMapper MAPPER = newVPackMapper();

    /*
    /**********************************************************************
    /* Float/float tests
    /**********************************************************************
     */

    @Test
    public void testFloatPrimitive() throws Exception
    {
        assertEquals(7.038531e-26f, MAPPER.readValue(VPackUtils.toVPack("\"7.038531e-26\""), float.class));
        assertEquals(1.1999999f, MAPPER.readValue(VPackUtils.toVPack("\"1.199999988079071\""), float.class));
        assertEquals(3.4028235e38f, MAPPER.readValue(VPackUtils.toVPack("\"3.4028235677973366e38\""), float.class));
        //this assertion fails unless toString is used
        assertEquals("1.4E-45", MAPPER.readValue(VPackUtils.toVPack("\"7.006492321624086e-46\""), float.class).toString());
    }

    @Test
    public void testFloatClass() throws Exception
    {
        assertEquals(Float.valueOf(7.038531e-26f), MAPPER.readValue(VPackUtils.toVPack("\"7.038531e-26\""), Float.class));
        assertEquals(Float.valueOf(1.1999999f), MAPPER.readValue(VPackUtils.toVPack("\"1.199999988079071\""), Float.class));
        assertEquals(Float.valueOf(3.4028235e38f), MAPPER.readValue(VPackUtils.toVPack("\"3.4028235677973366e38\""), Float.class));
        //this assertion fails unless toString is used
        assertEquals("1.4E-45", MAPPER.readValue(VPackUtils.toVPack("\"7.006492321624086e-46\""), Float.class).toString());
    }

    @Test
    public void testArrayOfFloatPrimitives() throws Exception
    {
        StringBuilder sb = new StringBuilder();
        sb.append('[')
                .append("\"7.038531e-26\",")
                .append("\"1.199999988079071\",")
                .append("\"3.4028235677973366e38\",")
                .append("\"7.006492321624086e-46\"")
                .append(']');
        float[] floats = MAPPER.readValue(VPackUtils.toVPack(sb.toString()), float[].class);
        assertEquals(4, floats.length);
        assertEquals(7.038531e-26f, floats[0]);
        assertEquals(1.1999999f, floats[1]);
        assertEquals(3.4028235e38f, floats[2]);
        assertEquals("1.4E-45", Float.toString(floats[3])); //this assertion fails unless toString is used
    }

    // for [jackson-core#757]
    @Test
    public void testBigArrayOfFloatPrimitives() throws Exception {
        try (InputStream stream = getClass().getResourceAsStream("/data/float-array-755.txt")) {
            float[] floats = MAPPER.readValue(stream, float[].class);
            assertEquals(1004, floats.length);
            assertEquals(7.038531e-26f, floats[0]);
            assertEquals(1.1999999f, floats[1]);
            assertEquals(3.4028235e38f, floats[2]);
            assertEquals(7.006492321624086e-46f, floats[3]); //this assertion fails unless toString is used
        }
    }

    @Test
    public void testArrayOfFloats() throws Exception
    {
        StringBuilder sb = new StringBuilder();
        sb.append('[')
                .append("\"7.038531e-26\",")
                .append("\"1.199999988079071\",")
                .append("\"3.4028235677973366e38\",")
                .append("\"7.006492321624086e-46\"")
                .append(']');
        Float[] floats = MAPPER.readValue(VPackUtils.toVPack(sb.toString()), Float[].class);
        assertEquals(4, floats.length);
        assertEquals(Float.valueOf(7.038531e-26f), floats[0]);
        assertEquals(Float.valueOf(1.1999999f), floats[1]);
        assertEquals(Float.valueOf(3.4028235e38f), floats[2]);
        assertEquals(Float.valueOf("1.4E-45"), floats[3]);
    }

    /*
    /**********************************************************************
    /* Double, NaN tests
    /**********************************************************************
     */

    @Test
    public void testNaN() throws Exception
    {
        Float result = MAPPER.readValue(VPackUtils.toVPack(" \"NaN\""), Float.class);
        assertEquals(Float.valueOf(Float.NaN), result);

        Double d = MAPPER.readValue(VPackUtils.toVPack(" \"NaN\""), Double.class);
        assertEquals(Double.valueOf(Double.NaN), d);

        Number num = MAPPER.readValue(VPackUtils.toVPack(" \"NaN\""), Number.class);
        assertEquals(Double.valueOf(Double.NaN), num);
    }

    @Test
    public void testDoubleInf() throws Exception
    {
        Double result = MAPPER.readValue(VPackUtils.toVPack(" \""+Double.POSITIVE_INFINITY+"\""), Double.class);
        assertEquals(Double.valueOf(Double.POSITIVE_INFINITY), result);

        result = MAPPER.readValue(VPackUtils.toVPack(" \""+Double.NEGATIVE_INFINITY+"\""), Double.class);
        assertEquals(Double.valueOf(Double.NEGATIVE_INFINITY), result);
    }

    // [databind#5898]: accept "+INF" and "+Infinity" as positive infinity
    @Test
    public void testDoublePlusInf5898() throws Exception
    {
        assertEquals(Double.POSITIVE_INFINITY, MAPPER.readValue(VPackUtils.toVPack(q("+Infinity")), Double.class));
        assertEquals(Double.POSITIVE_INFINITY, MAPPER.readValue(VPackUtils.toVPack(q("+Infinity")), Double.TYPE));
        assertEquals(Double.POSITIVE_INFINITY, MAPPER.readValue(VPackUtils.toVPack(q("+INF")), Double.class));
        assertEquals(Double.POSITIVE_INFINITY, MAPPER.readValue(VPackUtils.toVPack(q("+INF")), Double.TYPE));
    }

    // [databind#5898]: accept "+INF" and "+Infinity" as positive infinity
    @Test
    public void testFloatPlusInf5898() throws Exception
    {
        assertEquals(Float.POSITIVE_INFINITY, MAPPER.readValue(VPackUtils.toVPack(q("+Infinity")), Float.class));
        assertEquals(Float.POSITIVE_INFINITY, MAPPER.readValue(VPackUtils.toVPack(q("+Infinity")), Float.TYPE));
        assertEquals(Float.POSITIVE_INFINITY, MAPPER.readValue(VPackUtils.toVPack(q("+INF")), Float.class));
        assertEquals(Float.POSITIVE_INFINITY, MAPPER.readValue(VPackUtils.toVPack(q("+INF")), Float.TYPE));
    }

    /*
    /**********************************************************************
    /* Tests, other
    /**********************************************************************
     */

    // 01-Mar-2017, tatu: This is bit tricky... in some ways, mapping to "empty value"
    //    would be best; but due to legacy reasons becomes `null` at this point
    @Test
    public void testEmptyAsNumber() throws Exception
    {
        assertNull(MAPPER.readValue(VPackUtils.toVPack(q("")), Byte.class));
        assertNull(MAPPER.readValue(VPackUtils.toVPack(q("")), Short.class));
        assertNull(MAPPER.readValue(VPackUtils.toVPack(q("")), Character.class));
        assertNull(MAPPER.readValue(VPackUtils.toVPack(q("")), Integer.class));
        assertNull(MAPPER.readValue(VPackUtils.toVPack(q("")), Long.class));
        assertNull(MAPPER.readValue(VPackUtils.toVPack(q("")), Float.class));
        assertNull(MAPPER.readValue(VPackUtils.toVPack(q("")), Double.class));

        assertNull(MAPPER.readValue(VPackUtils.toVPack(q("")), BigInteger.class));
        assertNull(MAPPER.readValue(VPackUtils.toVPack(q("")), BigDecimal.class));
    }

    @Test
    public void testTextualNullAsNumber() throws Exception
    {
        final String NULL_JSON = q("null");
        assertNull(MAPPER.readValue(VPackUtils.toVPack(NULL_JSON), Byte.class));
        assertNull(MAPPER.readValue(VPackUtils.toVPack(NULL_JSON), Short.class));
        // Character is bit special, can't do:
//        assertNull(MAPPER.readValue(VPackUtils.toVPack(JSON), Character.class));
        assertNull(MAPPER.readValue(VPackUtils.toVPack(NULL_JSON), Integer.class));
        assertNull(MAPPER.readValue(VPackUtils.toVPack(NULL_JSON), Long.class));
        assertNull(MAPPER.readValue(VPackUtils.toVPack(NULL_JSON), Float.class));
        assertNull(MAPPER.readValue(VPackUtils.toVPack(NULL_JSON), Double.class));

        ObjectMapper nullOksMapper = vpackMapperBuilder()
                .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
                .build();

        assertEquals(Byte.valueOf((byte) 0), nullOksMapper.readValue(VPackUtils.toVPack(NULL_JSON), Byte.TYPE));
        assertEquals(Short.valueOf((short) 0), nullOksMapper.readValue(VPackUtils.toVPack(NULL_JSON), Short.TYPE));
        // Character is bit special, can't do:
//        assertEquals(Character.valueOf((char) 0), nullOksMapper.readValue(VPackUtils.toVPack(JSON), Character.TYPE));
        assertEquals(Integer.valueOf(0), nullOksMapper.readValue(VPackUtils.toVPack(NULL_JSON), Integer.TYPE));
        assertEquals(Long.valueOf(0L), nullOksMapper.readValue(VPackUtils.toVPack(NULL_JSON), Long.TYPE));
        assertEquals(Float.valueOf(0f), nullOksMapper.readValue(VPackUtils.toVPack(NULL_JSON), Float.TYPE));
        assertEquals(Double.valueOf(0d), nullOksMapper.readValue(VPackUtils.toVPack(NULL_JSON), Double.TYPE));

        assertNull(MAPPER.readValue(VPackUtils.toVPack(NULL_JSON), BigInteger.class));
        assertNull(MAPPER.readValue(VPackUtils.toVPack(NULL_JSON), BigDecimal.class));

        // Also: verify failure for at least some
        try {
            MAPPER.readerFor(Integer.TYPE).with(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
                .readValue(VPackUtils.toVPack(NULL_JSON));
            fail("Should not have passed");
        } catch (MismatchedInputException e) {
            verifyException(e, "Cannot coerce String \"null\"");
        }

        ObjectMapper noCoerceMapper = vpackMapperBuilder()
                .disable(MapperFeature.ALLOW_COERCION_OF_SCALARS)
                .build();
        try {
            noCoerceMapper.readValue(VPackUtils.toVPack(NULL_JSON), Integer.TYPE);
            fail("Should not have passed");
        } catch (MismatchedInputException e) {
            verifyException(e, "Cannot coerce String value");
        }
    }

    @Test
    public void testDeserializeDecimalHappyPath() throws Exception {
        String json = "{\"defaultValue\": { \"value\": 123 } }";
        MyBeanHolder result = MAPPER.readValue(VPackUtils.toVPack(json), MyBeanHolder.class);
        assertEquals(BigDecimal.valueOf(123), result.defaultValue.value.decimal);
    }

    @Test
    public void testDeserializeDecimalProperException() throws Exception {
        String json = "{\"defaultValue\": { \"value\": \"123\" } }";
        try {
            MAPPER.readValue(VPackUtils.toVPack(json), MyBeanHolder.class);
            fail("should have raised exception");
        } catch (InputCoercionException e) {
            verifyException(e, "not numeric");
        }
    }

    @Test
    public void testDeserializeDecimalProperExceptionWhenIdSet() throws Exception {
        String json = "{\"id\": 5, \"defaultValue\": { \"value\": \"123\" } }";
        try {
            MyBeanHolder result = MAPPER.readValue(VPackUtils.toVPack(json), MyBeanHolder.class);
            fail("should have raised exception instead value was set to " + result.defaultValue.value.decimal.toString());
        } catch (InputCoercionException e) {
            verifyException(e, "not numeric");
        }
    }

    // And then [databind#852]
    @Test
    public void testScientificNotationAsStringForNumber() throws Exception
    {
        Object ob = MAPPER.readValue(VPackUtils.toVPack("\"3E-8\""), Number.class);
        assertEquals(Double.class, ob.getClass());
        ob = MAPPER.readValue(VPackUtils.toVPack("\"3e-8\""), Number.class);
        assertEquals(Double.class, ob.getClass());
        ob = MAPPER.readValue(VPackUtils.toVPack("\"300000000\""), Number.class);
        assertEquals(Integer.class, ob.getClass());
        ob = MAPPER.readValue(VPackUtils.toVPack("\"123456789012\""), Number.class);
        assertEquals(Long.class, ob.getClass());
    }

    @Test
    public void testIntAsNumber() throws Exception
    {
        /* Even if declared as 'generic' type, should return using most
         * efficient type... here, Integer
         */
        Number result = MAPPER.readValue(VPackUtils.toVPack(" 123 "), Number.class);
        assertEquals(Integer.valueOf(123), result);
    }

    @Test
    public void testLongAsNumber() throws Exception
    {
        // And beyond int range, should get long
        long exp = 1234567890123L;
        Number result = MAPPER.readValue(VPackUtils.toVPack(String.valueOf(exp)), Number.class);
        assertEquals(Long.valueOf(exp), result);
    }

    @Test
    public void testBigIntAsNumber() throws Exception
    {
        // and after long, BigInteger
        BigInteger biggie = new BigInteger("1234567890123456789012345678901234567890");
        Number result = MAPPER.readValue(VPackUtils.toVPack(biggie.toString()), Number.class);
        assertEquals(BigInteger.class, biggie.getClass());
        assertEquals(biggie, result);
    }

    @Test
    public void testIntTypeOverride() throws Exception
    {
        /* Slight twist; as per [JACKSON-100], can also request binding
         * to BigInteger even if value would fit in Integer
         */
        ObjectReader r = MAPPER.reader(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS);

        BigInteger exp = BigInteger.valueOf(123L);

        // first test as any Number
        Number result = r.forType(Number.class).readValue(VPackUtils.toVPack(" 123 "));
        assertEquals(BigInteger.class, result.getClass());
        assertEquals(exp, result);

        // then as any Object
        /*Object value =*/ r.forType(Object.class).readValue(VPackUtils.toVPack("123"));
        assertEquals(BigInteger.class, result.getClass());
        assertEquals(exp, result);

        // and as JsonNode
        JsonNode node = r.readTree(VPackUtils.toVPack("  123"));
        assertTrue(node.isBigInteger());
        assertEquals(123, node.asInt());
    }

    @Test
    public void testDoubleAsNumber() throws Exception
    {
        Number result = MAPPER.readValue(VPackUtils.toVPack(" 1.0 "), Number.class);
        assertEquals(Double.valueOf(1.0), result);
    }

    @Test
    public void testFpTypeOverrideSimple() throws Exception
    {
        ObjectReader r = MAPPER.reader(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
        BigDecimal dec = new BigDecimal("0.1");

        // First test generic stand-alone Number
        Number result = r.forType(Number.class).readValue(VPackUtils.toVPack(dec.toString()));
        assertEquals(BigDecimal.class, result.getClass());
        assertEquals(dec, result);

        // Then plain old Object
        Object value = r.forType(Object.class).readValue(VPackUtils.toVPack(dec.toString()));
        assertEquals(BigDecimal.class, result.getClass());
        assertEquals(dec, value);

        JsonNode node = r.readTree(VPackUtils.toVPack(dec.toString()));
        assertTrue(node.isBigDecimal());
        assertEquals(dec.doubleValue(), node.asDouble());
    }

    @Test
    public void testFpTypeOverrideStructured() throws Exception
    {
        ObjectReader r = MAPPER.reader(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);

        BigDecimal dec = new BigDecimal("-19.37");
        // List element types
        @SuppressWarnings("unchecked")
        List<Object> list = (List<Object>) r.forType(List.class).readValue(VPackUtils.toVPack("[ "+dec.toString()+" ]"));
        assertEquals(1, list.size());
        Object val = list.get(0);
        assertEquals(BigDecimal.class, val.getClass());
        assertEquals(dec, val);

        // and a map
        Map<?,?> map = r.forType(Map.class).readValue(VPackUtils.toVPack("{ \"a\" : "+dec.toString()+" }"));
        assertEquals(1, map.size());
        val = map.get("a");
        assertEquals(BigDecimal.class, val.getClass());
        assertEquals(dec, val);
    }

    // [databind#504]
    @Test
    public void testForceIntsToLongs() throws Exception
    {
        ObjectReader r = MAPPER.reader(DeserializationFeature.USE_LONG_FOR_INTS);

        Object ob = r.forType(Object.class).readValue(VPackUtils.toVPack("42"));
        assertEquals(Long.class, ob.getClass());
        assertEquals(Long.valueOf(42L), ob);

        Number n = r.forType(Number.class).readValue(VPackUtils.toVPack("42"));
        assertEquals(Long.class, n.getClass());
        assertEquals(Long.valueOf(42L), n);

        // and one more: should get proper node as well
        JsonNode node = r.readTree(VPackUtils.toVPack("42"));
        if (!node.isLong()) {
            fail("Expected LongNode, got: "+node.getClass().getName());
        }
        assertEquals(42, node.asInt());
    }

    // [databind#2644]
    @Test
    public void testBigDecimalSubtypes() throws Exception
    {
        ObjectMapper mapper = vpackMapperBuilder()
                .registerSubtypes(NodeParent2644.class)
                .build();
        NodeRoot2644 root = mapper.readValue(
                VPackUtils.toVPack("{\"type\": \"NodeParent\",\"node\": {\"amount\": 9999999999999999.99} }"),
                NodeRoot2644.class
        );

        assertEquals(new BigDecimal("9999999999999999.99"), root.node.getVal());
    }

    // [databind#2784]
    @Test
    public void testBigDecimalUnwrapped() throws Exception
    {
        final ObjectMapper mapper = newVPackMapper();
        // mapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
        final String JSON = "{\"value\": 5.00}";
        NestedBigDecimalHolder2784 result = mapper.readValue(VPackUtils.toVPack(JSON), NestedBigDecimalHolder2784.class);
        assertEquals(new BigDecimal("5.00"), result.holder.value);
    }

    private final String BIG_DEC_STR;
    {
        StringBuilder sb = new StringBuilder("-1234.");
        // Above 500 chars we get a problem:
        for (int i = 520; --i >= 0; ) {
            sb.append('0');
        }
        BIG_DEC_STR = sb.toString();
    }
    private final BigDecimal BIG_DEC = new BigDecimal(BIG_DEC_STR);

    // [databind#4694]: decoded wrong by jackson-core/FDP for over 500 char numbers
    @Test
    public void bigDecimal4694FromString() throws Exception
    {
        assertEquals(BIG_DEC, MAPPER.readValue(VPackUtils.toVPack(BIG_DEC_STR), BigDecimal.class));
    }

    @Test
    public void bigDecimal4694FromBytes() throws Exception
    {
        byte[] b = vpackBytes(BIG_DEC_STR);
        assertEquals(BIG_DEC, MAPPER.readValue(b, 0, b.length, BigDecimal.class));
    }

    // [databind#4917]    
    @Test
    public void bigDecimal4917() throws Exception
    {
        DeserializationIssue4917 issue = MAPPER.readValue(
                VPackUtils.toVPack(a2q("{'decimalHolder':100.00,'number':50}")),
                DeserializationIssue4917.class);
        assertEquals(new BigDecimal("100.00"), issue.decimalHolder.value);
        assertEquals(50.0, issue.number);
    }

    @Test
    public void bigDecimal4917V2() throws Exception
    {
        DeserializationIssue4917V2 issue = MAPPER.readValue(
                VPackUtils.toVPack(a2q("{'decimalHolder':100.00,'number':50}")),
                DeserializationIssue4917V2.class);
        assertEquals(new BigDecimal("100.00"), issue.decimalHolder.value);
        assertEquals(50, issue.number);
    }

    @Test
    public void bigDecimal4917V3() throws Exception
    {
        DeserializationIssue4917V3 issue = MAPPER.readValue(
                VPackUtils.toVPack(a2q("{'decimal':100.00,'number':50}")),
                DeserializationIssue4917V3.class);
        assertEquals(new BigDecimal("100.00"), issue.decimal);
        assertEquals(50, issue.number);
    }

    // https://github.com/FasterXML/jackson-core/issues/1397
    @Test
    public void issue1397() throws Exception {
        final String dataString = a2q("{ 'results': [ { " +
                      "'radius': 179769313486231570000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, " +
                      "'type': 'center', " +
                      "'center': { " +
                      "'x': -11.0, " +
                      "'y': -2.0 } } ] }");

        Root object = MAPPER.readValue(VPackUtils.toVPack(dataString), Root.class);

        CenterResult result = (CenterResult) Arrays.stream(object.getResults()).findFirst().get();

        assertEquals(-11.0d, result.getCenter().getX());
        assertEquals(-2.0d, result.getCenter().getY());
    }
}
