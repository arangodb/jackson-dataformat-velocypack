package tools.jackson.databind.ser.jdk;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0585F1 {
private static final ObjectMapper ORDERED_MAPPER = VPackMapper.builder()
            .enable(SerializationFeature.ORDER_SET_ELEMENTS)
            .build();
private static final ObjectMapper MAPPER = new VPackMapper();

    void testBigCollectionVpack() throws Exception {
        final int count = 9999;
        ArrayList<Integer> values = new ArrayList<>(count + 1);
        for (int i = 0; i <= count; ++i) {
            values.add(i);
        }

        assertCollectionSequence(MAPPER.writeValueAsBytes(values), count);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        MAPPER.writeValue(output, values);
        assertCollectionSequence(output.toByteArray(), count);

        List<?> decoded = MAPPER.readValue(output.toByteArray(), List.class);
        assertEquals(count + 1, decoded.size());
        assertEquals(0, decoded.get(0));
        assertEquals(count, decoded.get(count));
    }

    void testCollectionsVpack() throws Exception {
        // Independent literal input covers the VPack parser path before writer checks.
        assertArrayEquals(new int[] { 0, 1, 2 },
                MAPPER.readValue(VPackWireFixtureTest.hex("02 05 30 31 32"), int[].class));

        final int entryLen = 98;
        for (int type = 0; type < 4; ++type) {
            Object value;
            if (type == 0) {
                int[] ints = new int[entryLen];
                for (int i = 0; i < entryLen; ++i) {
                    ints[i] = i;
                }
                value = ints;
            } else {
                Collection<Integer> collection;
                switch (type) {
                case 1:
                    collection = new java.util.LinkedList<>();
                    break;
                case 2:
                    collection = new TreeSet<>();
                    break;
                default:
                    collection = new ArrayList<>();
                    break;
                }
                for (int i = 0; i < entryLen; ++i) {
                    collection.add(i);
                }
                value = collection;
            }
            assertCollectionSequence(MAPPER.writeValueAsBytes(value), entryLen - 1);
        }
    }

    void testEmptyBeanCollectionVpack() throws Exception {
        CollectionBean bean = new CollectionBean(new ArrayList<>(List.of("foobar")));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 14 01 46 76 61 6c 75 65 73 02 09 46 66 6f 6f 62 61 72 03"),
                MAPPER.writeValueAsBytes(bean));
        Map<?, ?> result = MAPPER.readValue(
                VPackWireFixtureTest.hex(
                        "0b 14 01 46 76 61 6c 75 65 73 02 09 46 66 6f 6f 62 61 72 03"),
                Map.class);
        assertEquals(List.of("foobar"), result.get("values"));
    }

    void testEmptyBeanEnumMapVpack() throws Exception {
        EnumMap<Key, String> values = new EnumMap<>(Key.class);
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 09 01 43 6d 61 70 0a 03"),
                MAPPER.writeValueAsBytes(new EnumMapBean(values)));
        Map<?, ?> result = MAPPER.readValue(
                VPackWireFixtureTest.hex("0b 09 01 43 6d 61 70 0a 03"), Map.class);
        assertEquals(Map.of(), result.get("map"));
    }
private static void assertCollectionSequence(byte[] encoded, int lastValue)
            throws Exception {
        try (JsonParser parser = MAPPER.createParser(new ByteArrayInputStream(encoded))) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            for (int i = 0; i <= lastValue; ++i) {
                assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
                assertEquals(i, parser.getIntValue());
            }
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
        }
    }
static class StringSetBean {
        @JsonProperty
        public Set<String> values;

        StringSetBean(Set<String> values) {
            this.values = values;
        }
    }
static class WrappingStringSerializer extends ValueSerializer<String> {
        @Override
        public void serialize(String value, JsonGenerator generator, SerializationContext context) {
            generator.writeString("<" + value + ">");
        }
    }
static class CustomSerStringSetBean {
        @JsonSerialize(contentUsing = WrappingStringSerializer.class)
        public Set<String> values;

        CustomSerStringSetBean(Set<String> values) {
            this.values = values;
        }
    }
static class CollectionBean {
        @JsonProperty
        public Collection<Object> values;

        CollectionBean(Collection<?> values) {
            @SuppressWarnings("unchecked")
            Collection<Object> cast = (Collection<Object>) values;
            this.values = cast;
        }
    }
enum Key { A, B, C }
static class EnumMapBean {
        EnumMap<Key, String> map;

        EnumMapBean(EnumMap<Key, String> map) {
            this.map = map;
        }

        public EnumMap<Key, String> getMap() {
            return map;
        }
    }
static final class AllowAllTypes extends PolymorphicTypeValidator.Base {
        private static final long serialVersionUID = 1L;

        @Override
        public Validity validateBaseType(tools.jackson.databind.DatabindContext context,
                tools.jackson.databind.JavaType baseType) {
            return Validity.ALLOWED;
        }
    }

    void __invoke_testBigCollectionVpack() throws Exception {
        try {
            testBigCollectionVpack();
        } finally {
        }
    }


    void __invoke_testCollectionsVpack() throws Exception {
        try {
            testCollectionsVpack();
        } finally {
        }
    }


    void __invoke_testEmptyBeanCollectionVpack() throws Exception {
        try {
            testEmptyBeanCollectionVpack();
        } finally {
        }
    }


    void __invoke_testEmptyBeanEnumMapVpack() throws Exception {
        try {
            testEmptyBeanEnumMapVpack();
        } finally {
        }
    }

}
