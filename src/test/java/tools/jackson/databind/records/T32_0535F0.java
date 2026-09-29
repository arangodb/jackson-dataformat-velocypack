package tools.jackson.databind.records;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import tools.jackson.databind.ObjectMapper;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0535F0 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final ObjectMapper MAPPER_5312 = VPackMapper.builder()
            .changeDefaultPropertyInclusion(incl -> JsonInclude.Value.construct(NON_DEFAULT, NON_DEFAULT))
            .withConfigOverride(String.class,
                    o -> o.setInclude(JsonInclude.Value.construct(NON_NULL, NON_NULL)))
            .build();
private static final byte[] CHILD_WITH_ID_AND_NAME = VPackWireFixtureTest.hex(
            "14 1a 45 63 68 69 6c 64 14 11 42 69 64 28 7b "
          + "44 6e 61 6d 65 43 42 6f 62 02 01");

    void testSerializeJsonIgnoreRecordVpack() throws Exception {
        Map<?, ?> wire = MAPPER.readValue(
                MAPPER.writeValueAsBytes(new RecordWithIgnore(123, "Bob")), Map.class);
        assertEquals(Map.of("id", 123), wire);
    }

    void testSerializeJsonIgnoreAndJsonPropertyRecordVpack() throws Exception {
        Map<?, ?> wire = MAPPER.readValue(
                MAPPER.writeValueAsBytes(new RecordWithIgnoreJsonProperty(123, "Bob")), Map.class);
        assertEquals(Map.of("id", 123), wire);
    }

    void testSerializeJsonIgnoreAccessorRecordVpack() throws Exception {
        Map<?, ?> wire = MAPPER.readValue(
                MAPPER.writeValueAsBytes(new RecordWithIgnoreAccessor(123, "Bob")), Map.class);
        assertEquals(Map.of("id", 123), wire);
    }

    void testSerializeJsonIgnorePrimitiveTypeRecordVpack() throws Exception {
        Map<?, ?> wire = MAPPER.readValue(
                MAPPER.writeValueAsBytes(new RecordWithIgnorePrimitiveType(123, "Bob")), Map.class);
        assertEquals(Map.of("name", "Bob"), wire);
    }

    void testJsonIgnoreWithOverriddenAccessor3992Vpack() throws Exception {
        Recursion beanWithRecursion = new Recursion();
        beanWithRecursion.add(beanWithRecursion);

        byte[] encoded = MAPPER.writeValueAsBytes(new HelloRecord("hello", beanWithRecursion));
        assertEquals(Map.of("text", "hello"), MAPPER.readValue(encoded, Map.class));

        HelloRecord result = MAPPER.readValue(encoded, HelloRecord.class);
        assertNotNull(result);
    }
public record AnnotatedParamRecordClass(
            @JsonInclude(JsonInclude.Include.NON_NULL) String omitFieldIfNull,
            String standardField) { }
public record AnnotatedGetterRecordClass(String omitFieldIfNull, String standardField) {
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @Override
        public String omitFieldIfNull() {
            return omitFieldIfNull;
        }
    }
public record Id2Name(int id, String name) { }
public record RecordWithInclude4629(
            @JsonIncludeProperties("id") Id2Name child) { }
public record RecordWithIgnore4629(
            @JsonIgnoreProperties("name") Id2Name child) { }
record StringValue5312(String value) {
        @Override
        @JsonValue
        public String value() {
            return value;
        }
    }
record Pojo1_5312(StringValue5312 value) { }
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
    record Pojo2_5312(StringValue5312 value) { }
record Pojo4_5312(StringValue5312 value) {
        Pojo4_5312() { this(null); }
    }
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
    record Pojo5_5312(StringValue5312 value) {
        Pojo5_5312() { this(null); }
    }
record RecordWithIgnore(int id, @JsonIgnore String name) { }
record RecordWithIgnoreJsonProperty(int id,
            @JsonIgnore @JsonProperty("name") String name) { }
record RecordWithIgnoreAccessor(int id, String name) {
        @JsonIgnore
        @Override
        public String name() {
            return name;
        }
    }
record RecordWithIgnorePrimitiveType(@JsonIgnore int id, String name) { }
public record HelloRecord(String text, @JsonIgnore Recursion hidden) {
        @Override
        public Recursion hidden() {
            return hidden;
        }
    }
static class Recursion {
        public List<Recursion> all = new ArrayList<>();

        void add(Recursion recursion) {
            all.add(recursion);
        }
    }

    void __invoke_testSerializeJsonIgnoreRecordVpack() throws Exception {
        try {
            testSerializeJsonIgnoreRecordVpack();
        } finally {
        }
    }


    void __invoke_testSerializeJsonIgnoreAndJsonPropertyRecordVpack() throws Exception {
        try {
            testSerializeJsonIgnoreAndJsonPropertyRecordVpack();
        } finally {
        }
    }


    void __invoke_testSerializeJsonIgnoreAccessorRecordVpack() throws Exception {
        try {
            testSerializeJsonIgnoreAccessorRecordVpack();
        } finally {
        }
    }


    void __invoke_testSerializeJsonIgnorePrimitiveTypeRecordVpack() throws Exception {
        try {
            testSerializeJsonIgnorePrimitiveTypeRecordVpack();
        } finally {
        }
    }


    void __invoke_testJsonIgnoreWithOverriddenAccessor3992Vpack() throws Exception {
        try {
            testJsonIgnoreWithOverriddenAccessor3992Vpack();
        } finally {
        }
    }

}
