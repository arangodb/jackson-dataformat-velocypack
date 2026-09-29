package tools.jackson.databind.records;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0537Fixture {
private static final VPackMapper MAPPER = new VPackMapper();
private static final byte[] READ_ONLY_ID_NAME = VPackWireFixtureTest.hex(
            "14 11 42 69 64 28 7b 44 6e 61 6d 65 43 42 6f 62 02");
private static final byte[] READ_ONLY_ID = VPackWireFixtureTest.hex(
            "14 08 42 69 64 28 7b 01");
private static final byte[] READ_ONLY_ID_NULL_NAME = VPackWireFixtureTest.hex(
            "14 0e 42 69 64 28 7b 44 6e 61 6d 65 18 02");
private static final byte[] READ_ONLY_AB = VPackWireFixtureTest.hex(
            "14 13 41 61 45 68 65 6c 6c 6f 41 62 45 77 6f 72 6c 64 02");

    void testDeserializeReadOnlyPropertyVpack() throws Exception {
        assertEquals(new RecordWithReadOnly(123, null),
                MAPPER.readValue(READ_ONLY_ID_NAME, RecordWithReadOnly.class));
    }

    void testSerializeReadOnlyNamedPropertyVpack() throws Exception {
        Map<?, ?> actual = MAPPER.readValue(MAPPER.writeValueAsBytes(
                new RecordWithReadOnlyNamedProperty(123, "Bob")), Map.class);
        assertEquals(Map.of("id", 123, "name", "Bob"), actual);
    }

    void testDeserializeReadOnlyNamedPropertyVpack() throws Exception {
        assertEquals(new RecordWithReadOnlyNamedProperty(123, null),
                MAPPER.readValue(READ_ONLY_ID_NAME, RecordWithReadOnlyNamedProperty.class));
    }

    void testRoundtripPOJO5049Vpack() throws Exception {
        Map<?, ?> wire = MAPPER.readValue(MAPPER.writeValueAsBytes(
                new ReadOnly5049Pojo("hello", "world")), Map.class);
        assertEquals(Map.of("a", "hello", "b", "world"), wire);

        ReadOnly5049Pojo pojo = MAPPER.readValue(READ_ONLY_AB, ReadOnly5049Pojo.class);
        assertNotNull(pojo);
        assertNull(pojo.a);
        assertNull(pojo.b);
    }

    void testRoundtripRecord5049Vpack() throws Exception {
        Map<?, ?> wire = MAPPER.readValue(MAPPER.writeValueAsBytes(
                new ReadOnly5049Record("hello", "world")), Map.class);
        assertEquals(Map.of("a", "hello", "b", "world"), wire);

        ReadOnly5049Record record = MAPPER.readValue(READ_ONLY_AB, ReadOnly5049Record.class);
        assertNotNull(record);
        assertNull(record.a());
        assertNull(record.b());
    }

    void testSerializeReadOnlyAccessorVpack() throws Exception {
        Map<?, ?> actual = MAPPER.readValue(MAPPER.writeValueAsBytes(
                new RecordWithReadOnlyAccessor(123, "Bob")), Map.class);
        assertEquals(Map.of("id", 123, "name", "Bob"), actual);
    }

    void testSerializeReadOnlyAllPropertiesVpack() throws Exception {
        Map<?, ?> actual = MAPPER.readValue(MAPPER.writeValueAsBytes(
                new RecordWithReadOnlyAll(123, "Bob")), Map.class);
        assertEquals(Map.of("id", 123, "name", "Bob"), actual);
    }

    void testSerializeReadOnlyAllProperties_WithNoArgConstructorVpack() throws Exception {
        Map<?, ?> actual = MAPPER.readValue(MAPPER.writeValueAsBytes(
                new RecordWithReadOnlyAllAndNoArgConstructor(123, "Bob")), Map.class);
        assertEquals(Map.of("id", 123, "name", "Bob"), actual);
    }

    void testSerializeReadOnlyComponentOverrideAccessorVpack() throws Exception {
        Map<?, ?> actual = MAPPER.readValue(MAPPER.writeValueAsBytes(
                new RecordWithReadOnlyComponentOverriddenAccessor(123, "Bob")), Map.class);
        assertEquals(Map.of("id", 123, "name", "Bob"), actual);
    }

    void testDeserializeReadOnlyPrimitiveTypePropertyVpack() throws Exception {
        assertEquals(new RecordWithReadOnlyPrimitiveType(0, "Bob"),
                MAPPER.readValue(READ_ONLY_ID_NAME, RecordWithReadOnlyPrimitiveType.class));
    }

    void testSerializeReadOnlyPrimitiveTypePropertyVpack() throws Exception {
        Map<?, ?> actual = MAPPER.readValue(MAPPER.writeValueAsBytes(
                new RecordWithReadOnlyPrimitiveType(123, "Bob")), Map.class);
        assertEquals(Map.of("id", 123, "name", "Bob"), actual);
    }

    void testDeserializeReadOnlyComponentOverrideAccessorVpack() throws Exception {
        RecordWithReadOnlyComponentOverriddenAccessor expected =
                new RecordWithReadOnlyComponentOverriddenAccessor(123, null);
        assertEquals(expected, MAPPER.readValue(READ_ONLY_ID,
                RecordWithReadOnlyComponentOverriddenAccessor.class));
        assertEquals(expected, MAPPER.readValue(READ_ONLY_ID_NULL_NAME,
                RecordWithReadOnlyComponentOverriddenAccessor.class));
        assertEquals(expected, MAPPER.readValue(READ_ONLY_ID_NAME,
                RecordWithReadOnlyComponentOverriddenAccessor.class));
    }
record RecordWithReadOnly(int id,
            @JsonProperty(access = JsonProperty.Access.READ_ONLY) String name) { }
record RecordWithReadOnlyNamedProperty(int id,
            @JsonProperty(value = "name", access = JsonProperty.Access.READ_ONLY) String name) { }
record RecordWithReadOnlyAccessor(int id, String name) {
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        @Override
        public String name() {
            return name;
        }
    }
record RecordWithReadOnlyComponentOverriddenAccessor(int id,
            @JsonProperty(access = JsonProperty.Access.READ_ONLY) String name) {
        @Override
        public String name() {
            return name;
        }
    }
record RecordWithReadOnlyPrimitiveType(
            @JsonProperty(access = JsonProperty.Access.READ_ONLY) int id, String name) { }
record RecordWithReadOnlyAll(
            @JsonProperty(access = JsonProperty.Access.READ_ONLY) int id,
            @JsonProperty(access = JsonProperty.Access.READ_ONLY) String name) { }
record RecordWithReadOnlyAllAndNoArgConstructor(
            @JsonProperty(access = JsonProperty.Access.READ_ONLY) int id,
            @JsonProperty(access = JsonProperty.Access.READ_ONLY) String name) {
        public RecordWithReadOnlyAllAndNoArgConstructor() {
            this(-1, "no-arg");
        }
    }
static class ReadOnly5049Pojo {
        protected String a, b;

        ReadOnly5049Pojo(
                @JsonProperty(value = "a", access = JsonProperty.Access.READ_ONLY) String a,
                @JsonProperty(value = "b", access = JsonProperty.Access.READ_ONLY) String b) {
            this.a = a;
            this.b = b;
        }

        public String getA() { return a; }
        public String getB() { return b; }
    }
record ReadOnly5049Record(
            @JsonProperty(value = "a", access = JsonProperty.Access.READ_ONLY) String a,
            @JsonProperty(value = "b", access = JsonProperty.Access.READ_ONLY) String b) { }

    void __invoke_testDeserializeReadOnlyPropertyVpack() throws Exception {
        try {
            testDeserializeReadOnlyPropertyVpack();
        } finally {
        }
    }


    void __invoke_testSerializeReadOnlyNamedPropertyVpack() throws Exception {
        try {
            testSerializeReadOnlyNamedPropertyVpack();
        } finally {
        }
    }


    void __invoke_testDeserializeReadOnlyNamedPropertyVpack() throws Exception {
        try {
            testDeserializeReadOnlyNamedPropertyVpack();
        } finally {
        }
    }


    void __invoke_testRoundtripPOJO5049Vpack() throws Exception {
        try {
            testRoundtripPOJO5049Vpack();
        } finally {
        }
    }


    void __invoke_testRoundtripRecord5049Vpack() throws Exception {
        try {
            testRoundtripRecord5049Vpack();
        } finally {
        }
    }


    void __invoke_testSerializeReadOnlyAccessorVpack() throws Exception {
        try {
            testSerializeReadOnlyAccessorVpack();
        } finally {
        }
    }


    void __invoke_testSerializeReadOnlyAllPropertiesVpack() throws Exception {
        try {
            testSerializeReadOnlyAllPropertiesVpack();
        } finally {
        }
    }


    void __invoke_testSerializeReadOnlyAllProperties_WithNoArgConstructorVpack() throws Exception {
        try {
            testSerializeReadOnlyAllProperties_WithNoArgConstructorVpack();
        } finally {
        }
    }


    void __invoke_testSerializeReadOnlyComponentOverrideAccessorVpack() throws Exception {
        try {
            testSerializeReadOnlyComponentOverrideAccessorVpack();
        } finally {
        }
    }


    void __invoke_testDeserializeReadOnlyPrimitiveTypePropertyVpack() throws Exception {
        try {
            testDeserializeReadOnlyPrimitiveTypePropertyVpack();
        } finally {
        }
    }


    void __invoke_testSerializeReadOnlyPrimitiveTypePropertyVpack() throws Exception {
        try {
            testSerializeReadOnlyPrimitiveTypePropertyVpack();
        } finally {
        }
    }


    void __invoke_testDeserializeReadOnlyComponentOverrideAccessorVpack() throws Exception {
        try {
            testDeserializeReadOnlyComponentOverrideAccessorVpack();
        } finally {
        }
    }

}
