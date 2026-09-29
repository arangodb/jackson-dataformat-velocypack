package tools.jackson.databind.deser.merge;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonMerge;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.exc.MismatchedInputException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0290F1 {
private static final ObjectMapper MERGE_MAPPER = VPackMapper.builder()
            .disable(MapperFeature.IGNORE_MERGE_FOR_UNMERGEABLE)
            .build();
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] NO_SETTER_UPDATE = VPackWireFixtureTest.hex(
            "14 10 45 76 61 6c 75 65 14 07 41 62 28 63 01 01");
private static final byte[] REFERENCE_UPDATE = VPackWireFixtureTest.hex(
            "14 12 45 76 61 6c 75 65 48 6f 76 65 72 72 69 64 65 01");
private static final byte[] INVALID_MERGE = VPackWireFixtureTest.hex(
            "14 0a 45 76 61 6c 75 65 33 01");
private static final byte[] CREATOR_UPDATE = VPackWireFixtureTest.hex(
            "14 0f 41 61 43 67 68 69 41 62 43 6a 6b 6c 02");
private static final byte[] ONE_PROPERTY_UPDATE = VPackWireFixtureTest.hex(
            "14 07 41 61 41 78 01");
private static final byte[] ARRAY_ROOT = VPackWireFixtureTest.hex(
            "02 05 31 32 33");
private static final byte[] ARRAY_THEN_ARRAY = VPackWireFixtureTest.hex(
            "02 05 31 32 33 01");
private static final byte[] NULL_ROOT = VPackWireFixtureTest.hex("18");
private static final byte[] NULL_THEN_FALSE = VPackWireFixtureTest.hex("18 19");
private static final byte[] EMPTY_OBJECT_THEN_FALSE = VPackWireFixtureTest.hex("0a 19");

    // Provenance: UpdateValueTest#testValueUpdateOther().
    void testValueUpdateOtherVpack() throws Exception {
        ObjectReader reader = MAPPER.readerFor(Bean.class)
                .withValueToUpdate(new Bean("abc", "def"));
        Bean result = reader.withValueToUpdate(null)
                .readValue(ONE_PROPERTY_UPDATE);
        assertNotNull(result);
    }

    // Provenance: UpdateValueTest#testValueUpdateWithCreator().
    void testValueUpdateWithCreatorVpack() throws Exception {
        Bean bean = new Bean("abc", "def");
        assertSame(bean, MAPPER.readerFor(Bean.class)
                .withValueToUpdate(bean)
                .readValue(CREATOR_UPDATE));
        assertEquals("ghi", bean.getA());
        assertEquals("jkl", bean.getB());
    }
private static void verifyArray(Object value) {
        assertTrue(value instanceof JsonNode && ((JsonNode) value).isArray()
                || value instanceof List<?>);
        if (value instanceof JsonNode node) {
            assertEquals(3, node.size());
        } else {
            assertEquals(3, ((List<?>) value).size());
        }
    }
private static void assertTrailingArray(ThrowingRead action) {
        MismatchedInputException failure = assertThrows(MismatchedInputException.class, action::read);
        assertTrue(failure.getMessage().contains("Trailing token"));
        assertTrue(failure.getMessage().contains("START_ARRAY"));
    }
private static void assertTrailingFalse(ThrowingRead action) {
        MismatchedInputException failure = assertThrows(MismatchedInputException.class, action::read);
        assertTrue(failure.getMessage().contains("Trailing token"));
        assertTrue(failure.getMessage().contains("VALUE_FALSE"));
    }
@FunctionalInterface
    private interface ThrowingRead {
        Object read() throws Exception;
    }
static class NoSetterConfig {
        AB _value = new AB(1, 2);

        @JsonMerge
        public AB getValue() {
            return _value;
        }
    }
static class MergedReference {
        @JsonMerge
        public StringReference value = new StringReference("default");
    }
static class StringReference extends java.util.concurrent.atomic.AtomicReference<String> {
        StringReference(String value) {
            set(value);
        }
    }
static class CantMergeInts {
        @JsonMerge
        public int value;
    }
static class AB {
        public int a;
        public int b;

        protected AB() { }

        AB(int a, int b) {
            this.a = a;
            this.b = b;
        }
    }
static class Bean {
        private String a;
        private String b;

        @JsonCreator
        Bean(@JsonProperty("a") String a, @JsonProperty("b") String b) {
            this.a = a;
            this.b = b;
        }

        String getA() {
            return a;
        }

        void setA(String a) {
            this.a = a;
        }

        String getB() {
            return b;
        }

        void setB(String b) {
            this.b = b;
        }
    }

    void __invoke_testValueUpdateOtherVpack() throws Exception {
        try {
            testValueUpdateOtherVpack();
        } finally {
        }
    }


    void __invoke_testValueUpdateWithCreatorVpack() throws Exception {
        try {
            testValueUpdateWithCreatorVpack();
        } finally {
        }
    }

}
