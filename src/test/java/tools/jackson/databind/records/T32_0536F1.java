package tools.jackson.databind.records;

import java.util.ArrayList;
import java.util.List;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0536F1 {
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

    void testUnwrappedWithRecordVpack() throws Exception {
        RecordWithJsonUnwrapped input = new RecordWithJsonUnwrapped(
                "unrelatedValue", new Inner("value1", "value2"));
        Map<?, ?> wire = MAPPER.readValue(MAPPER.writeValueAsBytes(input), Map.class);
        assertEquals(Map.of("unrelated", "unrelatedValue",
                "property1", "value1", "property2", "value2"), wire);

        RecordWithJsonUnwrapped output = MAPPER.readValue(UNWRAPPED_RECORD,
                RecordWithJsonUnwrapped.class);
        assertEquals(input, output);
    }

    void unwrappedPojoShouldRoundTripVpack() throws Exception {
        BarPojo5115 input = new BarPojo5115();
        input.a = new FooPojo5115();
        input.a.a = 1;
        input.a.b = 2;
        input.c = 4;

        Map<?, ?> wire = MAPPER.readValue(MAPPER.writeValueAsBytes(input), Map.class);
        assertEquals(Map.of("a", 1, "b", 2, "c", 4), wire);

        BarPojo5115 output = MAPPER.readValue(ABC, BarPojo5115.class);
        assertNotNull(output.a);
        assertEquals(1, output.a.a);
        assertEquals(2, output.a.b);
        assertEquals(3, output.c);
    }

    void unwrappedRecordShouldRoundTripVpack() throws Exception {
        BarRecordFail5115 input = new BarRecordFail5115(new FooRecord5115(1, 2), 3);
        Map<?, ?> wire = MAPPER.readValue(MAPPER.writeValueAsBytes(input), Map.class);
        assertEquals(Map.of("a", 1, "b", 2, "c", 3), wire);
        assertEquals(input, MAPPER.readValue(ABC, BarRecordFail5115.class));
    }

    void unwrappedRecordShouldRoundTripPassVpack() throws Exception {
        BarRecordPass5115 input = new BarRecordPass5115(new FooRecord5115(1, 2), 3);
        Map<?, ?> wire = MAPPER.readValue(MAPPER.writeValueAsBytes(input), Map.class);
        assertEquals(Map.of("a", 1, "b", 2, "c", 3), wire);
        assertEquals(input, MAPPER.readValue(ABC, BarRecordPass5115.class));
    }

    void unwrappedRecordShouldKeepDeclarationOrderVpack() throws Exception {
        Row5716 input = new Row5716(1L, new Name5716("a", "b"), 2.5d);
        List<String> names = new ArrayList<>();
        Map<?, ?> ordered = MAPPER_5716.readValue(
                MAPPER_5716.writeValueAsBytes(input), Map.class);
        for (Object key : ordered.keySet()) {
            names.add((String) key);
        }
        assertEquals(List.of("time", "first", "last", "score"), names);
    }

    void testPrefixedUnwrappingWithRecordVpack() throws Exception {
        WithPrefix3178 input = new WithPrefix3178(
                new Inner3178("Bubba", new Location3178(2, 3)));
        Map<?, ?> wire = MAPPER.readValue(MAPPER.writeValueAsBytes(input), Map.class);
        assertEquals(Map.of("_name", "Bubba", "_location", Map.of("x", 2, "y", 3)), wire);

        WithPrefix3178 output = MAPPER.readValue(PREFIXED_UNWRAPPED_RECORD,
                WithPrefix3178.class);
        assertEquals(input, output);
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

    void __invoke_testUnwrappedWithRecordVpack() throws Exception {
        try {
            testUnwrappedWithRecordVpack();
        } finally {
        }
    }


    void __invoke_unwrappedPojoShouldRoundTripVpack() throws Exception {
        try {
            unwrappedPojoShouldRoundTripVpack();
        } finally {
        }
    }


    void __invoke_unwrappedRecordShouldRoundTripVpack() throws Exception {
        try {
            unwrappedRecordShouldRoundTripVpack();
        } finally {
        }
    }


    void __invoke_unwrappedRecordShouldRoundTripPassVpack() throws Exception {
        try {
            unwrappedRecordShouldRoundTripPassVpack();
        } finally {
        }
    }


    void __invoke_unwrappedRecordShouldKeepDeclarationOrderVpack() throws Exception {
        try {
            unwrappedRecordShouldKeepDeclarationOrderVpack();
        } finally {
        }
    }


    void __invoke_testPrefixedUnwrappingWithRecordVpack() throws Exception {
        try {
            testPrefixedUnwrappingWithRecordVpack();
        } finally {
        }
    }

}
