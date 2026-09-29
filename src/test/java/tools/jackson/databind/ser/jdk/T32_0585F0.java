package tools.jackson.databind.ser.jdk;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0585F0 {
private static final ObjectMapper ORDERED_MAPPER = VPackMapper.builder()
            .enable(SerializationFeature.ORDER_SET_ELEMENTS)
            .build();
private static final ObjectMapper MAPPER = new VPackMapper();

    void testOrderedHashSetWithIntegersVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex("02 05 31 32 33"),
                ORDERED_MAPPER.writeValueAsBytes(
                        new LinkedHashSet<>(Arrays.asList(3, 1, 2))));
    }

    void testPolymorphicSetSortedVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(SerializationFeature.ORDER_SET_ELEMENTS)
                .activateDefaultTyping(new AllowAllTypes(), DefaultTyping.NON_FINAL)
                .build();
        byte[] encoded = mapper.writeValueAsBytes(
                new LinkedHashSet<>(Arrays.asList("c", "a", "b")));
        List<?> wire = MAPPER.readValue(encoded, List.class);
        assertEquals("java.util.LinkedHashSet", wire.get(0));
        assertEquals(List.of("a", "b", "c"), wire.get(1));
    }

    void testSetStringBeanPropertyVpack() throws Exception {
        Set<String> values = new LinkedHashSet<>(Arrays.asList("z", "a", "m"));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 13 01 46 76 61 6c 75 65 73 02 08 41 61 41 6d 41 7a 03"),
                ORDERED_MAPPER.writeValueAsBytes(new StringSetBean(values)));
    }

    void testSetStringBeanPropertyWithNullVpack() throws Exception {
        Set<String> values = new LinkedHashSet<>(Arrays.asList(null, "b", "a"));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 16 01 46 76 61 6c 75 65 73 06 0b 03 "
              + "41 61 41 62 18 03 05 07 03"),
                ORDERED_MAPPER.writeValueAsBytes(new StringSetBean(values)));
    }

    void testSetStringWithCustomSerializerFallbackVpack() throws Exception {
        Set<String> values = new LinkedHashSet<>(Arrays.asList("c", "a", "b"));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 19 01 46 76 61 6c 75 65 73 02 0e "
              + "43 3c 61 3e 43 3c 62 3e 43 3c 63 3e 03"),
                ORDERED_MAPPER.writeValueAsBytes(new CustomSerStringSetBean(values)));
    }

    void testSetWithNullElementsVpack() throws Exception {
        Set<String> values = new LinkedHashSet<>(Arrays.asList(null, "b", "a"));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "06 0b 03 41 61 41 62 18 03 05 07"),
                ORDERED_MAPPER.writeValueAsBytes(values));
    }

    void testSortedSetUnchangedVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "02 08 41 61 41 62 41 63"),
                ORDERED_MAPPER.writeValueAsBytes(new TreeSet<>(Arrays.asList("c", "a", "b"))));
    }

    void testSortedSetWithCustomComparatorPreservedVpack() throws Exception {
        Set<String> values = new TreeSet<>(Comparator.reverseOrder());
        values.addAll(Arrays.asList("a", "b", "c"));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "02 08 41 63 41 62 41 61"),
                ORDERED_MAPPER.writeValueAsBytes(values));
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

    void __invoke_testOrderedHashSetWithIntegersVpack() throws Exception {
        try {
            testOrderedHashSetWithIntegersVpack();
        } finally {
        }
    }


    void __invoke_testPolymorphicSetSortedVpack() throws Exception {
        try {
            testPolymorphicSetSortedVpack();
        } finally {
        }
    }


    void __invoke_testSetStringBeanPropertyVpack() throws Exception {
        try {
            testSetStringBeanPropertyVpack();
        } finally {
        }
    }


    void __invoke_testSetStringBeanPropertyWithNullVpack() throws Exception {
        try {
            testSetStringBeanPropertyWithNullVpack();
        } finally {
        }
    }


    void __invoke_testSetStringWithCustomSerializerFallbackVpack() throws Exception {
        try {
            testSetStringWithCustomSerializerFallbackVpack();
        } finally {
        }
    }


    void __invoke_testSetWithNullElementsVpack() throws Exception {
        try {
            testSetWithNullElementsVpack();
        } finally {
        }
    }


    void __invoke_testSortedSetUnchangedVpack() throws Exception {
        try {
            testSortedSetUnchangedVpack();
        } finally {
        }
    }


    void __invoke_testSortedSetWithCustomComparatorPreservedVpack() throws Exception {
        try {
            testSortedSetWithCustomComparatorPreservedVpack();
        } finally {
        }
    }

}
