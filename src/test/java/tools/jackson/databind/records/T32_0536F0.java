package tools.jackson.databind.records;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.JsonValue;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0536F0 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final ObjectMapper MAPPER_5312 = VPackMapper.builder()
            .changeDefaultPropertyInclusion(incl -> JsonInclude.Value.construct(NON_DEFAULT, NON_DEFAULT))
            .withConfigOverride(String.class,
                    o -> o.setInclude(JsonInclude.Value.construct(NON_NULL, NON_NULL)))
            .build();
private static final ObjectMapper MAPPER_5716 = VPackMapper.builder()
            .disable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
            .enable(MapperFeature.SORT_CREATOR_PROPERTIES_FIRST)
            .build();
private static final byte[] ABC = VPackWireFixtureTest.hex(
            "14 0c 41 61 31 41 62 32 41 63 33 03");
private static final byte[] UNWRAPPED_RECORD = VPackWireFixtureTest.hex(
            "14 3e 49 75 6e 72 65 6c 61 74 65 64 4e 75 6e 72 65 6c 61 74 65 64 56 61 6c 75 65 "
          + "49 70 72 6f 70 65 72 74 79 31 46 76 61 6c 75 65 31 "
          + "49 70 72 6f 70 65 72 74 79 32 46 76 61 6c 75 65 32 03");
private static final byte[] PREFIXED_UNWRAPPED_RECORD = VPackWireFixtureTest.hex(
            "14 22 45 5f 6e 61 6d 65 45 42 75 62 62 61 49 5f 6c 6f 63 61 74 69 6f 6e "
          + "14 09 41 78 32 41 79 33 02 02");
private static final byte[] READ_ONLY_ID = VPackWireFixtureTest.hex(
            "14 08 42 69 64 28 7b 01");
private static final byte[] READ_ONLY_ID_NAME = VPackWireFixtureTest.hex(
            "14 11 42 69 64 28 7b 44 6e 61 6d 65 43 42 6f 62 02");

    void testSerialization5312_3Vpack() throws Exception {
        assertEquals(Map.of("value", ""), MAPPER_5312.readValue(
                MAPPER_5312.writeValueAsBytes(new Pojo3_5312(new StringValue5312(""))), Map.class));
    }

    void testSerializeJsonIgnoreProperties4630Vpack() throws Exception {
        Map<?, ?> actual = MAPPER.readValue(MAPPER.writeValueAsBytes(
                new RecordWithJsonIgnoreProperties(new Id2Name(123, "Bob"))), Map.class);
        assertEquals(Map.of("child", Map.of("id", 123)), actual);
    }

    void testSerializeJsonIncludeProperties4630Vpack() throws Exception {
        Map<?, ?> actual = MAPPER.readValue(MAPPER.writeValueAsBytes(
                new RecordWithJsonIncludeProperties(new Id2Name(123, "Bob"))), Map.class);
        assertEquals(Map.of("child", Map.of("id", 123)), actual);
    }
public record Id2Name(int id, String name) { }
public record RecordWithJsonIncludeProperties(@JsonIncludeProperties("id") Id2Name child) {
        @Override
        public Id2Name child() {
            return child;
        }
    }
public record RecordWithJsonIgnoreProperties(@JsonIgnoreProperties("name") Id2Name child) {
        @Override
        public Id2Name child() {
            return child;
        }
    }
record StringValue5312(String value) {
        @Override
        @JsonValue
        public String value() {
            return value;
        }
    }
record Pojo3_5312(@JsonInclude(JsonInclude.Include.NON_DEFAULT) StringValue5312 value) { }
record RecordWithJsonUnwrapped(String unrelated, @JsonUnwrapped Inner inner) { }
record Inner(String property1, String property2) { }
record FooRecord5115(int a, int b) { }
record BarRecordFail5115(@JsonUnwrapped FooRecord5115 a, int c) { }
record BarRecordPass5115(@JsonUnwrapped FooRecord5115 foo, int c) { }
static class FooPojo5115 {
        public int a;
        public int b;
    }
static class BarPojo5115 {
        @JsonUnwrapped
        public FooPojo5115 a;
        public int c;
    }
record Name5716(String first, String last) { }
record Row5716(long time, @JsonUnwrapped Name5716 name, double score) { }
record Location3178(int x, int y) { }
record Inner3178(String name, Location3178 location) { }
record WithPrefix3178(@JsonUnwrapped(prefix = "_") Inner3178 unwrapped) { }
record RecordWithReadOnlyAccessor(int id, String name) {
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        @Override
        public String name() {
            return name;
        }
    }
record RecordWithReadOnlyAll(@JsonProperty(access = JsonProperty.Access.READ_ONLY) int id,
            @JsonProperty(access = JsonProperty.Access.READ_ONLY) String name) { }
record RecordWithReadOnlyAllAndNoArgConstructor(
            @JsonProperty(access = JsonProperty.Access.READ_ONLY) int id,
            @JsonProperty(access = JsonProperty.Access.READ_ONLY) String name) {
        public RecordWithReadOnlyAllAndNoArgConstructor() {
            this(-1, "no-arg");
        }
    }

    void __invoke_testSerialization5312_3Vpack() throws Exception {
        try {
            testSerialization5312_3Vpack();
        } finally {
        }
    }


    void __invoke_testSerializeJsonIgnoreProperties4630Vpack() throws Exception {
        try {
            testSerializeJsonIgnoreProperties4630Vpack();
        } finally {
        }
    }


    void __invoke_testSerializeJsonIncludeProperties4630Vpack() throws Exception {
        try {
            testSerializeJsonIncludeProperties4630Vpack();
        } finally {
        }
    }

}
