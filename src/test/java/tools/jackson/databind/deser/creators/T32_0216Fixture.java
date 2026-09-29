package tools.jackson.databind.deser.creators;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.BeanDescription;
import tools.jackson.databind.DeserializationConfig;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.deser.SettableBeanProperty;
import tools.jackson.databind.deser.ValueInstantiator;
import tools.jackson.databind.deser.ValueInstantiators;
import tools.jackson.databind.deser.bean.PropertyValueBuffer;
import tools.jackson.databind.deser.std.StdValueInstantiator;
import tools.jackson.databind.module.SimpleModule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0216Fixture {
private static final VPackAttributeNameCodec NUMERIC_KEY_CODEC =
            new VPackAttributeNameCodec() {
                @Override
                public String decode(BigInteger unsignedId) {
                    return unsignedId.toString();
                }

                @Override
                public BigInteger encode(String name) {
                    try {
                        return new BigInteger(name);
                    } catch (NumberFormatException e) {
                        return null;
                    }
                }
            };
private static final ObjectMapper MAPPER = VPackMapper.builder(
            VPackFactory.builder().attributeNameCodec(NUMERIC_KEY_CODEC).build()).build();
private static final byte[] PRESENT_ZERO = VPackWireFixtureTest.hex(
            "14 06 41 61 30 01");
private static final byte[] PRESENT_NULL = VPackWireFixtureTest.hex(
            "14 06 41 63 18 01");

    // Provenance: TestCustomValueInstDefaults#testPresentZeroPrimitive.
    void testPresentZeroPrimitive() throws Exception {
        Bucket value = MAPPER.rebuild().addModule(new BucketModule()).build()
                .readValue(PRESENT_ZERO, Bucket.class);
        assertEquals(0, value.a);
        assertEquals(Bucket.DEFAULT_B, value.b);
        assertEquals(Bucket.DEFAULT_C, value.c);
        assertEquals(Bucket.DEFAULT_D, value.d);
    }

    // Provenance: TestCustomValueInstDefaults#testPresentNullReference.
    void testPresentNullReference() throws Exception {
        Bucket value = MAPPER.rebuild().addModule(new BucketModule()).build()
                .readValue(PRESENT_NULL, Bucket.class);
        assertEquals(Bucket.DEFAULT_A, value.a);
        assertEquals(Bucket.DEFAULT_B, value.b);
        assertNull(value.c);
        assertEquals(Bucket.DEFAULT_D, value.d);
    }
private static final byte[] BIGGER_DATA = VPackWireFixtureTest.hex(
            "14 d4 0b 49 61 72 65 61 4e 61 6d 65 73 14 4e 41 30 41 78 41 31 41 78 41" +
            "32 41 78 41 33 41 78 41 34 41 78 41 35 41 78 41 36 41 78 41 37 41 78 41" +
            "38 41 78 41 39 41 78 42 31 30 41 78 42 31 31 41 78 42 31 32 41 78 42 31" +
            "33 41 78 42 31 34 41 78 42 31 35 41 78 42 31 36 41 78 11 58 61 75 64 69" +
            "65 6e 63 65 53 75 62 43 61 74 65 67 6f 72 79 4e 61 6d 65 73 0a 4a 62 6c" +
            "6f 63 6b 4e 61 6d 65 73 0a 51 73 65 61 74 43 61 74 65 67 6f 72 79 4e 61" +
            "6d 65 73 14 ba 02 41 30 41 78 41 31 41 78 41 32 41 78 41 33 41 78 41 34" +
            "41 78 41 35 41 78 41 36 41 78 41 37 41 78 41 38 41 78 41 39 41 78 42 31" +
            "30 41 78 42 31 31 41 78 42 31 32 41 78 42 31 33 41 78 42 31 34 41 78 42" +
            "31 35 41 78 42 31 36 41 78 42 31 37 41 78 42 31 38 41 78 42 31 39 41 78" +
            "42 32 30 41 78 42 32 31 41 78 42 32 32 41 78 42 32 33 41 78 42 32 34 41" +
            "78 42 32 35 41 78 42 32 36 41 78 42 32 37 41 78 42 32 38 41 78 42 32 39" +
            "41 78 42 33 30 41 78 42 33 31 41 78 42 33 32 41 78 42 33 33 41 78 42 33" +
            "34 41 78 42 33 35 41 78 42 33 36 41 78 42 33 37 41 78 42 33 38 41 78 42" +
            "33 39 41 78 42 34 30 41 78 42 34 31 41 78 42 34 32 41 78 42 34 33 41 78" +
            "42 34 34 41 78 42 34 35 41 78 42 34 36 41 78 42 34 37 41 78 42 34 38 41" +
            "78 42 34 39 41 78 42 35 30 41 78 42 35 31 41 78 42 35 32 41 78 42 35 33" +
            "41 78 42 35 34 41 78 42 35 35 41 78 42 35 36 41 78 42 35 37 41 78 42 35" +
            "38 41 78 42 35 39 41 78 42 36 30 41 78 42 36 31 41 78 42 36 32 41 78 42" +
            "36 33 41 78 40 4d 73 75 62 54 6f 70 69 63 4e 61 6d 65 73 14 58 41 30 41" +
            "78 41 31 41 78 41 32 41 78 41 33 41 78 41 34 41 78 41 35 41 78 41 36 41" +
            "78 41 37 41 78 41 38 41 78 41 39 41 78 42 31 30 41 78 42 31 31 41 78 42" +
            "31 32 41 78 42 31 33 41 78 42 31 34 41 78 42 31 35 41 78 42 31 36 41 78" +
            "42 31 37 41 78 42 31 38 41 78 13 4c 73 75 62 6a 65 63 74 4e 61 6d 65 73" +
            "0a 4a 74 6f 70 69 63 4e 61 6d 65 73 14 13 41 30 41 78 41 31 41 78 41 32" +
            "41 78 41 33 41 78 04 4e 74 6f 70 69 63 53 75 62 54 6f 70 69 63 73 14 0f" +
            "41 30 01 41 31 01 41 32 01 41 33 01 04 4a 76 65 6e 75 65 4e 61 6d 65 73" +
            "14 07 41 30 41 78 01 46 65 76 65 6e 74 73 14 af 06 41 30 0a 41 31 0a 41" +
            "32 0a 41 33 0a 41 34 0a 41 35 0a 41 36 0a 41 37 0a 41 38 0a 41 39 0a 42" +
            "31 30 0a 42 31 31 0a 42 31 32 0a 42 31 33 0a 42 31 34 0a 42 31 35 0a 42" +
            "31 36 0a 42 31 37 0a 42 31 38 0a 42 31 39 0a 42 32 30 0a 42 32 31 0a 42" +
            "32 32 0a 42 32 33 0a 42 32 34 0a 42 32 35 0a 42 32 36 0a 42 32 37 0a 42" +
            "32 38 0a 42 32 39 0a 42 33 30 0a 42 33 31 0a 42 33 32 0a 42 33 33 0a 42" +
            "33 34 0a 42 33 35 0a 42 33 36 0a 42 33 37 0a 42 33 38 0a 42 33 39 0a 42" +
            "34 30 0a 42 34 31 0a 42 34 32 0a 42 34 33 0a 42 34 34 0a 42 34 35 0a 42" +
            "34 36 0a 42 34 37 0a 42 34 38 0a 42 34 39 0a 42 35 30 0a 42 35 31 0a 42" +
            "35 32 0a 42 35 33 0a 42 35 34 0a 42 35 35 0a 42 35 36 0a 42 35 37 0a 42" +
            "35 38 0a 42 35 39 0a 42 36 30 0a 42 36 31 0a 42 36 32 0a 42 36 33 0a 42" +
            "36 34 0a 42 36 35 0a 42 36 36 0a 42 36 37 0a 42 36 38 0a 42 36 39 0a 42" +
            "37 30 0a 42 37 31 0a 42 37 32 0a 42 37 33 0a 42 37 34 0a 42 37 35 0a 42" +
            "37 36 0a 42 37 37 0a 42 37 38 0a 42 37 39 0a 42 38 30 0a 42 38 31 0a 42" +
            "38 32 0a 42 38 33 0a 42 38 34 0a 42 38 35 0a 42 38 36 0a 42 38 37 0a 42" +
            "38 38 0a 42 38 39 0a 42 39 30 0a 42 39 31 0a 42 39 32 0a 42 39 33 0a 42" +
            "39 34 0a 42 39 35 0a 42 39 36 0a 42 39 37 0a 42 39 38 0a 42 39 39 0a 43" +
            "31 30 30 0a 43 31 30 31 0a 43 31 30 32 0a 43 31 30 33 0a 43 31 30 34 0a" +
            "43 31 30 35 0a 43 31 30 36 0a 43 31 30 37 0a 43 31 30 38 0a 43 31 30 39" +
            "0a 43 31 31 30 0a 43 31 31 31 0a 43 31 31 32 0a 43 31 31 33 0a 43 31 31" +
            "34 0a 43 31 31 35 0a 43 31 31 36 0a 43 31 31 37 0a 43 31 31 38 0a 43 31" +
            "31 39 0a 43 31 32 30 0a 43 31 32 31 0a 43 31 32 32 0a 43 31 32 33 0a 43" +
            "31 32 34 0a 43 31 32 35 0a 43 31 32 36 0a 43 31 32 37 0a 43 31 32 38 0a" +
            "43 31 32 39 0a 43 31 33 30 0a 43 31 33 31 0a 43 31 33 32 0a 43 31 33 33" +
            "0a 43 31 33 34 0a 43 31 33 35 0a 43 31 33 36 0a 43 31 33 37 0a 43 31 33" +
            "38 0a 43 31 33 39 0a 43 31 34 30 0a 43 31 34 31 0a 43 31 34 32 0a 43 31" +
            "34 33 0a 43 31 34 34 0a 43 31 34 35 0a 43 31 34 36 0a 43 31 34 37 0a 43" +
            "31 34 38 0a 43 31 34 39 0a 43 31 35 30 0a 43 31 35 31 0a 43 31 35 32 0a" +
            "43 31 35 33 0a 43 31 35 34 0a 43 31 35 35 0a 43 31 35 36 0a 43 31 35 37" +
            "0a 43 31 35 38 0a 43 31 35 39 0a 43 31 36 30 0a 43 31 36 31 0a 43 31 36" +
            "32 0a 43 31 36 33 0a 43 31 36 34 0a 43 31 36 35 0a 43 31 36 36 0a 43 31" +
            "36 37 0a 43 31 36 38 0a 43 31 36 39 0a 43 31 37 30 0a 43 31 37 31 0a 43" +
            "31 37 32 0a 43 31 37 33 0a 43 31 37 34 0a 43 31 37 35 0a 43 31 37 36 0a" +
            "43 31 37 37 0a 43 31 37 38 0a 43 31 37 39 0a 43 31 38 30 0a 43 31 38 31" +
            "0a 43 31 38 32 0a 43 31 38 33 0a 01 b8 4c 70 65 72 66 6f 72 6d 61 6e 63" +
            "65 73 01 0b");
private static byte[] nestedEqualArrays(int depth) {
        int length = Math.addExact(Math.multiplyExact(depth, 9), 1);
        byte[] result = new byte[length];
        int offset = 0;
        int containerLength = length;
        for (int i = 0; i < depth; ++i) {
            result[offset++] = 0x05;
            putLittleEndian(result, offset, containerLength);
            offset += 8;
            containerLength -= 9;
        }
        result[offset] = 0x0a;
        return result;
    }
private static void putLittleEndian(byte[] target, int offset, long value) {
        for (int i = 0; i < 8; ++i) {
            target[offset + i] = (byte) (value >>> (8 * i));
        }
    }
static class Bucket {
        static final int DEFAULT_A = 111, DEFAULT_B = 222;
        static final String DEFAULT_C = "defaultC", DEFAULT_D = "defaultD";
        final int a, b;
        final String c, d;

        @JsonCreator
        Bucket(@JsonProperty("a") int a, @JsonProperty("b") int b,
                @JsonProperty("c") String c, @JsonProperty("d") String d) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
        }
    }
static class BucketInstantiator extends StdValueInstantiator {
        BucketInstantiator(StdValueInstantiator source) { super(source); }

        @Override
        public Object createFromObjectWith(DeserializationContext ctxt,
                SettableBeanProperty[] props, PropertyValueBuffer buffer) {
            int a = Bucket.DEFAULT_A, b = Bucket.DEFAULT_B;
            String c = Bucket.DEFAULT_C, d = Bucket.DEFAULT_D;
            for (SettableBeanProperty prop : props) {
                if (!buffer.hasParameter(prop)) {
                    continue;
                }
                Object value = buffer.getParameter(ctxt, prop);
                switch (prop.getName()) {
                case "a" -> a = (Integer) value;
                case "b" -> b = (Integer) value;
                case "c" -> c = (String) value;
                case "d" -> d = (String) value;
                default -> { }
                }
            }
            return new Bucket(a, b, c, d);
        }
    }
static class BucketModule extends SimpleModule {
        @Override
        public void setupModule(SetupContext context) {
            context.addValueInstantiators(new ValueInstantiators.Base() {
                @Override
                public ValueInstantiator modifyValueInstantiator(DeserializationConfig config,
                        BeanDescription.Supplier beanDescRef,
                        ValueInstantiator defaultInstantiator) {
                    if (defaultInstantiator instanceof StdValueInstantiator source
                            && beanDescRef.getBeanClass() == Bucket.class) {
                        return new BucketInstantiator(source);
                    }
                    return defaultInstantiator;
                }
            });
        }
    }
static class Citm {
        public Map<Integer,String> areaNames;
        public Map<Integer,String> audienceSubCategoryNames;
        public Map<Integer,String> blockNames;
        public Map<Integer,String> seatCategoryNames;
        public Map<Integer,String> subTopicNames;
        public Map<Integer,String> subjectNames;
        public Map<Integer,String> topicNames;
        public Map<Integer,int[]> topicSubTopics;
        public Map<String,String> venueNames;
        public Map<Integer,Event> events;
        public List<Performance> performances;
    }
static class Event {
        public int id;
    }
static class Performance { }
static class Point { }

    void __invoke_testPresentZeroPrimitive() throws Exception {
        try {
            testPresentZeroPrimitive();
        } finally {
        }
    }


    void __invoke_testPresentNullReference() throws Exception {
        try {
            testPresentNullReference();
        } finally {
        }
    }

}
