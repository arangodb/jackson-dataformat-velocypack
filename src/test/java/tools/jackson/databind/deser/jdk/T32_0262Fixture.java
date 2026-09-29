package tools.jackson.databind.deser.jdk;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonMerge;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonDeserialize;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0262Fixture {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] TRUE = VPackWireFixtureTest.hex("1a");
private static final byte[] THIRTEEN = VPackWireFixtureTest.hex("28 0d");
private static final byte[] ATOMIC_LONG = VPackWireFixtureTest.hex(
            "2c 35 1c dc df 02");
private static final byte[] ATOMIC_LONG_FLOAT = VPackWireFixtureTest.hex(
            "1b 00 00 aa e1 e0 fe 06 42");
private static final byte[] ATOMIC_LONG_STRING = VPackWireFixtureTest.hex(
            "4a 39 39 39 39 39 39 39 39 39 39");
private static final byte[] LONG_ARRAY = VPackWireFixtureTest.hex(
            "02 04 31 32");
private static final byte[] CONTENT_AS = VPackWireFixtureTest.hex(
            "14 0d 45 76 61 6c 75 65 43 61 62 63 01");
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] ATOMIC_NULL_PROPERTY = VPackWireFixtureTest.hex(
            "0b 0b 01 45 76 61 6c 75 65 18 03");
private static final byte[] ATOMIC_NODE_NULL = VPackWireFixtureTest.hex(
            "14 0b 46 61 74 6f 6d 69 63 18 01");
private static final byte[] ATOMIC_NULL = VPackWireFixtureTest.hex("18");
private static final byte[] MERGE_A = VPackWireFixtureTest.hex(
            "14 0d 44 6c 69 73 74 13 05 41 61 01 01");
private static final byte[] MERGE_B = VPackWireFixtureTest.hex(
            "14 0d 44 6c 69 73 74 13 05 41 62 01 01");

    // Provenance: JDKAtomicTypesDeserTest#testAtomicBoolean().
    void testAtomicBoolean() throws Exception {
        AtomicBoolean value = MAPPER.readValue(TRUE, AtomicBoolean.class);
        assertTrue(value.get());
    }

    // Provenance: JDKAtomicTypesDeserTest#testAtomicInt().
    void testAtomicInt() throws Exception {
        AtomicInteger value = MAPPER.readValue(THIRTEEN, AtomicInteger.class);
        assertEquals(13, value.get());
    }

    // Provenance: JDKAtomicTypesDeserTest#testAtomicLong().
    void testAtomicLong() throws Exception {
        AtomicLong value = MAPPER.readValue(ATOMIC_LONG, AtomicLong.class);
        assertEquals(12345678901L, value.get());
    }

    // Provenance: JDKAtomicTypesDeserTest#testAtomicLongFromFloatAboveIntRange().
    void testAtomicLongFromFloatAboveIntRange() throws Exception {
        AtomicLong value = MAPPER.readValue(ATOMIC_LONG_FLOAT, AtomicLong.class);
        assertEquals(12345678901L, value.get());
    }

    // Provenance: JDKAtomicTypesDeserTest#testAtomicLongFromStringAboveIntRange().
    void testAtomicLongFromStringAboveIntRange() throws Exception {
        AtomicLong value = MAPPER.readValue(ATOMIC_LONG_STRING, AtomicLong.class);
        assertEquals(9999999999L, value.get());
    }

    // Provenance: JDKAtomicTypesDeserTest#testAtomicRefWithNodeViaCreator().
    void testAtomicRefWithNodeViaCreator() throws Exception {
        AtomicRefWithNodeBean bean = MAPPER.readValue(ATOMIC_NODE_NULL,
                AtomicRefWithNodeBean.class);
        assertNotNull(bean.atomic);
        assertNotNull(bean.atomic.get());
        assertTrue(bean.atomic.get().isNull());

        bean = MAPPER.readValue(EMPTY_OBJECT, AtomicRefWithNodeBean.class);
        assertNotNull(bean.atomic);
        assertTrue(bean.atomic.get().isNull());

        bean = MAPPER.readerFor(AtomicRefWithNodeBean.class)
                .with(tools.jackson.databind.DeserializationFeature.USE_NULL_FOR_MISSING_REFERENCE_VALUES)
                .readValue(EMPTY_OBJECT);
        assertNull(bean.atomic);
    }

    // Provenance: JDKAtomicTypesDeserTest#testAtomicReference().
    void testAtomicReference() throws Exception {
        AtomicReference<long[]> value = MAPPER.readValue(LONG_ARRAY,
                new TypeReference<AtomicReference<long[]>>() { });
        assertNotNull(value);
        assertNotNull(value.get());
        assertArrayEquals(new long[] { 1L, 2L }, value.get());
    }

    // Provenance: JDKAtomicTypesDeserTest#testDeserializeWithContentAs().
    void testDeserializeWithContentAs() throws Exception {
        AtomicRefReadWrapper result = MAPPER.readValue(CONTENT_AS,
                AtomicRefReadWrapper.class);
        assertNotNull(result.value);
        Object value = result.value.get();
        assertNotNull(value);
        assertEquals(WrappedString.class, value.getClass());
        assertEquals("abc", ((WrappedString) value).value);
    }

    // Provenance: JDKAtomicTypesDeserTest#testEmpty1256().
    void testEmpty1256() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .changeDefaultPropertyInclusion(
                        incl -> incl.withValueInclusion(JsonInclude.Include.NON_ABSENT))
                .build();
        assertArrayEquals(EMPTY_OBJECT, mapper.writeValueAsBytes(new Issue1256Bean()));
    }

    // Provenance: JDKAtomicTypesDeserTest#testFilteringOfAtomicReference().
    void testFilteringOfAtomicReference() throws Exception {
        SimpleWrapper input = new SimpleWrapper(null);
        assertArrayEquals(ATOMIC_NULL_PROPERTY, MAPPER.writeValueAsBytes(input));

        ObjectMapper mapper = VPackMapper.builder()
                .changeDefaultPropertyInclusion(
                        incl -> incl.withValueInclusion(JsonInclude.Include.NON_NULL))
                .build();
        assertArrayEquals(ATOMIC_NULL_PROPERTY, mapper.writeValueAsBytes(input));

        mapper = VPackMapper.builder()
                .changeDefaultPropertyInclusion(
                        incl -> incl.withValueInclusion(JsonInclude.Include.NON_EMPTY))
                .build();
        assertArrayEquals(EMPTY_OBJECT, mapper.writeValueAsBytes(input));
    }

    // Provenance: JDKAtomicTypesDeserTest#testMergeToListViaRef().
    void testMergeToListViaRef() throws Exception {
        ListWrapper base = MAPPER.readValue(MERGE_A, ListWrapper.class);
        assertNotNull(base.list);
        assertEquals(Arrays.asList("a"), base.list.get());

        ListWrapper merged = MAPPER.readerForUpdating(base).readValue(MERGE_B);
        assertSame(base, merged);
        assertEquals(Arrays.asList("a", "b"), base.list.get());
    }

    // Provenance: JDKAtomicTypesDeserTest#testNullValueHandling().
    
    void testNullValueHandling() throws Exception {
        AtomicReference<Double> input = new AtomicReference<>();
        assertArrayEquals(ATOMIC_NULL, MAPPER.writeValueAsBytes(input));
        AtomicReference<Double> result = (AtomicReference<Double>) MAPPER.readValue(
                ATOMIC_NULL, AtomicReference.class);
        assertNotNull(result);
        assertNull(result.get());
    }
static class AtomicRefReadWrapper {
        @JsonDeserialize(contentAs = WrappedString.class)
        public AtomicReference<Object> value;
    }
static class WrappedString {
        String value;

        public WrappedString(String value) {
            this.value = value;
        }
    }
@JsonPropertyOrder({ "a", "b" })
    static class Issue1256Bean {
        @tools.jackson.databind.annotation.JsonSerialize(as = AtomicReference.class)
        public Object a = new AtomicReference<>();
        public AtomicReference<Object> b = new AtomicReference<>();
    }
static class SimpleWrapper {
        public AtomicReference<Object> value;

        SimpleWrapper(Object value) {
            this.value = new AtomicReference<>(value);
        }
    }
static class ListWrapper {
        @JsonMerge
        public AtomicReference<List<String>> list = new AtomicReference<>();
    }
static class AtomicRefWithNodeBean {
        protected AtomicReference<JsonNode> atomic;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public AtomicRefWithNodeBean(@JsonProperty("atomic") AtomicReference<JsonNode> atomic) {
            this.atomic = atomic;
        }
    }

    void __invoke_testAtomicBoolean() throws Exception {
        try {
            testAtomicBoolean();
        } finally {
        }
    }


    void __invoke_testAtomicInt() throws Exception {
        try {
            testAtomicInt();
        } finally {
        }
    }


    void __invoke_testAtomicLong() throws Exception {
        try {
            testAtomicLong();
        } finally {
        }
    }


    void __invoke_testAtomicLongFromFloatAboveIntRange() throws Exception {
        try {
            testAtomicLongFromFloatAboveIntRange();
        } finally {
        }
    }


    void __invoke_testAtomicLongFromStringAboveIntRange() throws Exception {
        try {
            testAtomicLongFromStringAboveIntRange();
        } finally {
        }
    }


    void __invoke_testAtomicRefWithNodeViaCreator() throws Exception {
        try {
            testAtomicRefWithNodeViaCreator();
        } finally {
        }
    }


    void __invoke_testAtomicReference() throws Exception {
        try {
            testAtomicReference();
        } finally {
        }
    }


    void __invoke_testDeserializeWithContentAs() throws Exception {
        try {
            testDeserializeWithContentAs();
        } finally {
        }
    }


    void __invoke_testEmpty1256() throws Exception {
        try {
            testEmpty1256();
        } finally {
        }
    }


    void __invoke_testFilteringOfAtomicReference() throws Exception {
        try {
            testFilteringOfAtomicReference();
        } finally {
        }
    }


    void __invoke_testMergeToListViaRef() throws Exception {
        try {
            testMergeToListViaRef();
        } finally {
        }
    }


    void __invoke_testNullValueHandling() throws Exception {
        try {
            testNullValueHandling();
        } finally {
        }
    }

}
