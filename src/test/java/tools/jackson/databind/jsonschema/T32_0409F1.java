package tools.jackson.databind.jsonschema;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;
import tools.jackson.core.JsonParser.NumberType;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.cfg.EnumFeature;
import tools.jackson.databind.jsonFormatVisitors.JsonAnyFormatVisitor;
import tools.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
import tools.jackson.databind.jsonFormatVisitors.JsonBooleanFormatVisitor;
import tools.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import tools.jackson.databind.jsonFormatVisitors.JsonIntegerFormatVisitor;
import tools.jackson.databind.jsonFormatVisitors.JsonMapFormatVisitor;
import tools.jackson.databind.jsonFormatVisitors.JsonNullFormatVisitor;
import tools.jackson.databind.jsonFormatVisitors.JsonNumberFormatVisitor;
import tools.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import tools.jackson.databind.jsonFormatVisitors.JsonStringFormatVisitor;
import tools.jackson.databind.jsonFormatVisitors.JsonValueFormat;
import tools.jackson.databind.ser.BeanPropertyWriter;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import tools.jackson.dataformat.velocypack.*;

class T32_0409F1 {

    // Provenance: NewSchemaTest#testBasicTraversal().
    void testBasicTraversalVpack() throws Exception {
        ObjectMapper mapper = new VPackMapper();
        mapper.acceptJsonFormatVisitor(POJO.class, new BogusJsonFormatVisitorWrapper());
        mapper.acceptJsonFormatVisitor(new TypeReference<POJO>() { },
                new BogusJsonFormatVisitorWrapper());
        mapper.acceptJsonFormatVisitor(POJOWithScalars.class,
                new BogusJsonFormatVisitorWrapper());
        mapper.acceptJsonFormatVisitor(LinkedHashMap.class,
                new BogusJsonFormatVisitorWrapper());
        mapper.acceptJsonFormatVisitor(ArrayList.class,
                new BogusJsonFormatVisitorWrapper());
        mapper.acceptJsonFormatVisitor(EnumSet.class,
                new BogusJsonFormatVisitorWrapper());
        mapper.acceptJsonFormatVisitor(POJOWithRefs.class,
                new BogusJsonFormatVisitorWrapper());
        mapper.acceptJsonFormatVisitor(POJOWithJsonValue.class,
                new BogusJsonFormatVisitorWrapper());
    }

    // Provenance: NewSchemaTest#testSimpleEnum().
    void testSimpleEnumVpack() throws Exception {
        final Set<String> values = new TreeSet<>();
        ObjectWriter writer = new VPackMapper().writer(
                EnumFeature.WRITE_ENUMS_USING_TO_STRING);
        writer.acceptJsonFormatVisitor(TestEnum.class,
                new JsonFormatVisitorWrapper.Base() {
                    @Override
                    public JsonStringFormatVisitor expectStringFormat(JavaType type) {
                        return new JsonStringFormatVisitor() {
                            @Override
                            public void enumTypes(Set<String> enums) {
                                values.addAll(enums);
                            }

                            @Override
                            public void format(JsonValueFormat format) { }
                        };
                    }
                });
        assertEquals(new TreeSet<>(Arrays.asList(
                "ToString:A", "ToString:B", "ToString:C")), values);
    }

    // Provenance: NewSchemaTest#testEnumWithJsonValue().
    void testEnumWithJsonValueVpack() throws Exception {
        final Set<String> values = new TreeSet<>();
        new VPackMapper().acceptJsonFormatVisitor(TestEnumWithJsonValue.class,
                new JsonFormatVisitorWrapper.Base() {
                    @Override
                    public JsonStringFormatVisitor expectStringFormat(JavaType type) {
                        return new JsonStringFormatVisitor() {
                            @Override
                            public void enumTypes(Set<String> enums) {
                                values.addAll(enums);
                            }

                            @Override
                            public void format(JsonValueFormat format) { }
                        };
                    }
                });
        assertEquals(new TreeSet<>(Arrays.asList(
                "value-A", "value-B", "value-C")), values);
    }

    // Provenance: NewSchemaTest#testJsonValueFormatHandling().
    void testJsonValueFormatHandlingVpack() throws Exception {
        byte[] expected = VPackWireFixtureTest.hex(
                "49 68 6f 73 74 2d 6e 61 6d 65");
        VPackMapper mapper = new VPackMapper();
        assertArrayEquals(expected, mapper.writeValueAsBytes(JsonValueFormat.HOST_NAME));
        assertSame(JsonValueFormat.HOST_NAME,
                mapper.readValue(expected, JsonValueFormat.class));
    }

    // Provenance: NewSchemaTest#testSimpleNumbers().
    void testSimpleNumbersVpack() throws Exception {
        final StringBuilder sb = new StringBuilder();
        new VPackMapper().acceptJsonFormatVisitor(Numbers.class,
                new JsonFormatVisitorWrapper.Base() {
                    @Override
                    public JsonObjectFormatVisitor expectObjectFormat(JavaType type) {
                        return new JsonObjectFormatVisitor.Base(getContext()) {
                            @Override
                            public void optionalProperty(BeanProperty prop) {
                                sb.append("[optProp ").append(prop.getName()).append("(");
                                ValueSerializer<Object> serializer = null;
                                if (prop instanceof BeanPropertyWriter bpw) {
                                    serializer = bpw.getSerializer();
                                }
                                SerializationContext context = getContext();
                                if (serializer == null) {
                                    serializer = context.findPrimaryPropertySerializer(
                                            prop.getType(), prop);
                                }
                                serializer.acceptJsonFormatVisitor(
                                        new JsonFormatVisitorWrapper.Base() {
                                            @Override
                                            public JsonNumberFormatVisitor expectNumberFormat(
                                                    JavaType propertyType) {
                                                return new JsonNumberFormatVisitor() {
                                                    @Override
                                                    public void format(JsonValueFormat format) {
                                                        sb.append("[numberFormat=")
                                                                .append(format).append("]");
                                                    }

                                                    @Override
                                                    public void enumTypes(Set<String> enums) { }

                                                    @Override
                                                    public void numberType(NumberType numberType) {
                                                        sb.append("[numberType=")
                                                                .append(numberType).append("]");
                                                    }
                                                };
                                            }

                                            @Override
                                            public JsonIntegerFormatVisitor expectIntegerFormat(
                                                    JavaType propertyType) {
                                                return new JsonIntegerFormatVisitor() {
                                                    @Override
                                                    public void format(JsonValueFormat format) {
                                                        sb.append("[integerFormat=")
                                                                .append(format).append("]");
                                                    }

                                                    @Override
                                                    public void enumTypes(Set<String> enums) { }

                                                    @Override
                                                    public void numberType(NumberType numberType) {
                                                        sb.append("[numberType=")
                                                                .append(numberType).append("]");
                                                    }
                                                };
                                            }
                                        }, prop.getType());
                                sb.append(")]");
                            }
                        };
                    }
                });
        assertEquals("[optProp dec([numberType=BIG_DECIMAL])][optProp bigInt([numberType=BIG_INTEGER])]",
                sb.toString());
    }
static class TestJsonIgnoredProperties {
        @JsonIgnore
        public String ignoredProp;
        public String normalProperty;
        @JsonProperty("renamedProperty")
        public String someProperty;
        @JsonAnyGetter
        public Map<String, Object> anyProperties() {
            return new java.util.TreeMap<>();
        }
    }
enum TestEnum {
        A, B, C;

        @Override
        public String toString() {
            return "ToString:" + name();
        }
    }
enum TestEnumWithJsonValue {
        A, B, C;

        @JsonValue
        public String forSerialize() {
            return "value-" + name();
        }
    }
static class POJO {
        public List<POJO> children;
        public POJO[] childOrdering;
        public Map<String, java.util.Date> times;
        public Map<String, Integer> conversions;
        public EnumMap<TestEnum, Double> weights;
    }
static class POJOWithScalars {
        public boolean boo;
        public byte b;
        public char c;
        public short s;
        public int i;
        public long l;
        public float f;
        public double d;
        public byte[] arrayBoo;
        public byte[] arrayb;
        public char[] arrayc;
        public short[] arrays;
        public int[] arrayi;
        public long[] arrayl;
        public float[] arrayf;
        public double[] arrayd;
        public Boolean Boo;
        public Byte B;
        public Character C;
        public Short S;
        public Integer I;
        public Long L;
        public Float F;
        public Double D;
        public TestEnum en;
        public String str;
        public String[] strs;
        public java.util.Date date;
        public java.util.Calendar calendar;
    }
static class POJOWithRefs {
        public AtomicReference<POJO> maybePOJO;
        public AtomicReference<String> maybeString;
    }
static class POJOWithJsonValue {
        private Point[] value;

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        public POJOWithJsonValue(Point[] v) {
            value = v;
        }

        @JsonValue
        public Point[] serialization() {
            return value;
        }
    }
@JsonPropertyOrder({ "x", "y" })
    static class Point {
        public int x, y;

        protected Point() { }

        @JsonIgnore
        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
@JsonPropertyOrder({ "dec", "bigInt" })
    static class Numbers {
        public BigDecimal dec;
        public BigInteger bigInt;
    }
static class BogusJsonFormatVisitorWrapper extends JsonFormatVisitorWrapper.Base {
        BogusJsonFormatVisitorWrapper() {
            super();
        }

        BogusJsonFormatVisitorWrapper(SerializationContext context) {
            super(context);
        }

        @Override
        public JsonObjectFormatVisitor expectObjectFormat(JavaType type) {
            return new JsonObjectFormatVisitor.Base(getContext()) {
                @Override
                public void property(BeanProperty prop) {
                    visit(prop);
                }

                @Override
                public void optionalProperty(BeanProperty prop) {
                    visit(prop);
                }

                private void visit(BeanProperty prop) {
                    if (!(prop instanceof BeanPropertyWriter bpw)) {
                        return;
                    }
                    ValueSerializer<?> serializer = bpw.getSerializer();
                    if (serializer == null) {
                        if (getContext() == null) {
                            throw new Error("SerializationContext missing");
                        }
                        serializer = getContext().findPrimaryPropertySerializer(
                                prop.getType(), prop);
                    }
                    serializer.acceptJsonFormatVisitor(
                            new BogusJsonFormatVisitorWrapper(getContext()), prop.getType());
                }
            };
        }

        @Override
        public JsonArrayFormatVisitor expectArrayFormat(JavaType type) {
            return new JsonArrayFormatVisitor.Base(getContext());
        }

        @Override
        public JsonStringFormatVisitor expectStringFormat(JavaType type) {
            return new JsonStringFormatVisitor.Base();
        }

        @Override
        public JsonNumberFormatVisitor expectNumberFormat(JavaType type) {
            return new JsonNumberFormatVisitor.Base();
        }

        @Override
        public JsonIntegerFormatVisitor expectIntegerFormat(JavaType type) {
            return new JsonIntegerFormatVisitor.Base();
        }

        @Override
        public JsonBooleanFormatVisitor expectBooleanFormat(JavaType type) {
            return new JsonBooleanFormatVisitor.Base();
        }

        @Override
        public JsonNullFormatVisitor expectNullFormat(JavaType type) {
            return new JsonNullFormatVisitor.Base();
        }

        @Override
        public JsonAnyFormatVisitor expectAnyFormat(JavaType type) {
            return new JsonAnyFormatVisitor.Base();
        }

        @Override
        public JsonMapFormatVisitor expectMapFormat(JavaType type) {
            return new JsonMapFormatVisitor.Base(getContext());
        }
    }

    void __invoke_testBasicTraversalVpack() throws Exception {
        try {
            testBasicTraversalVpack();
        } finally {
        }
    }


    void __invoke_testSimpleEnumVpack() throws Exception {
        try {
            testSimpleEnumVpack();
        } finally {
        }
    }


    void __invoke_testEnumWithJsonValueVpack() throws Exception {
        try {
            testEnumWithJsonValueVpack();
        } finally {
        }
    }


    void __invoke_testJsonValueFormatHandlingVpack() throws Exception {
        try {
            testJsonValueFormatHandlingVpack();
        } finally {
        }
    }


    void __invoke_testSimpleNumbersVpack() throws Exception {
        try {
            testSimpleNumbersVpack();
        } finally {
        }
    }

}
