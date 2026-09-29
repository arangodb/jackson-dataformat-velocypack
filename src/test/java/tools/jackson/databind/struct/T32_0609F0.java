package tools.jackson.databind.struct;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.InvalidDefinitionException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0609F0 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] NAME_AND_LOCATION = VPackWireFixtureTest.hex(
            "14 13 44 6e 61 6d 65 44 74 65 73 74 41 78 31 41 79 32 03");
private static final byte[] PERSON_PRIMARY = VPackWireFixtureTest.hex(
            "14 2e 44 6e 61 6d 65 45 41 6c 69 63 65 47 6a 6f 62 4e 61 6d 65 " +
            "48 65 6e 67 69 6e 65 65 72 4a 6a 6f 62 41 64 64 72 65 73 73 " +
            "43 4e 59 43 03");
private static final byte[] PERSON_ALIAS = VPackWireFixtureTest.hex(
            "14 2b 41 6e 45 41 6c 69 63 65 47 6a 6f 62 4e 61 6d 65 " +
            "48 65 6e 67 69 6e 65 65 72 4a 6a 6f 62 41 64 64 72 65 73 73 " +
            "43 4e 59 43 03");
private static final byte[] ROOT_PERSON_PRIMARY = VPackWireFixtureTest.hex(
            "14 38 46 70 65 72 73 6f 6e 14 2e 44 6e 61 6d 65 45 41 6c 69 63 65 " +
            "47 6a 6f 62 4e 61 6d 65 48 65 6e 67 69 6e 65 65 72 " +
            "4a 6a 6f 62 41 64 64 72 65 73 73 43 4e 59 43 03 01");
private static final byte[] ROOT_PERSON_ALIAS = VPackWireFixtureTest.hex(
            "14 35 46 70 65 72 73 6f 6e 14 2b 41 6e 45 41 6c 69 63 65 " +
            "47 6a 6f 62 4e 61 6d 65 48 65 6e 67 69 6e 65 65 72 " +
            "4a 6a 6f 62 41 64 64 72 65 73 73 43 4e 59 43 03 01");
private static final byte[] RECORD_ALIAS = VPackWireFixtureTest.hex(
            "14 14 41 61 45 48 65 6c 6c 6f 41 62 46 57 6f 72 6c 64 21 02");
private static final byte[] SECOND_ALIAS = VPackWireFixtureTest.hex(
            "14 11 42 61 61 42 48 69 41 62 45 74 68 65 72 65 02");
private static final byte[] PRIMARY_NAME = VPackWireFixtureTest.hex(
            "14 10 41 63 46 64 69 72 65 63 74 41 62 41 78 02");

    // Provenance: UnwrappedPropertyConflict2883Test#testUnwrappedConflictDetected().
    void unwrappedConflictDetectedVpack() {
        InvalidDefinitionException exception = assertThrows(InvalidDefinitionException.class,
                () -> MAPPER.writeValueAsBytes(new OuterConflict()));
        assertTrue(exception.getMessage().contains("Conflict for type"));
        assertTrue(exception.getMessage().contains("'b'"));
        assertTrue(exception.getMessage().contains("InnerC"));
        assertTrue(exception.getMessage().contains("OuterConflict"));
    }

    // Provenance: UnwrappedPropertyConflict2883Test#testUnwrappedNoConflictDifferentNames().
    void unwrappedNoConflictDifferentNamesVpack() throws Exception {
        JsonNode tree = MAPPER.readTree(NAME_AND_LOCATION);
        assertEquals("test", tree.get("name").stringValue());
        assertEquals(1, tree.get("x").intValue());
        assertEquals(2, tree.get("y").intValue());
    }

    // Provenance: UnwrappedPropertyConflict2883Test#testUnwrappedNoConflictWithPrefix().
    void unwrappedNoConflictWithPrefixVpack() throws Exception {
        JsonNode tree = MAPPER.readTree(MAPPER.writeValueAsBytes(new OuterNoConflict()));
        assertEquals(3, tree.get("b").get("ba").intValue());
        assertEquals(4, tree.get("c_b").get("da").intValue());
    }
private static final ObjectMapper ROOT_MAPPER = VPackMapper.builder()
            .enable(tools.jackson.databind.DeserializationFeature.UNWRAP_ROOT_VALUE)
            .build();
static class OuterConflict {
        public InnerB b = new InnerB();
        @JsonUnwrapped public InnerC c = new InnerC();
    }
static class InnerB { public int ba = 3; }
static class InnerC { public InnerD b = new InnerD(); }
static class InnerD { public int da = 4; }
static class OuterNoConflict {
        public InnerB b = new InnerB();
        @JsonUnwrapped(prefix = "c_") public InnerC c = new InnerC();
    }
@JsonRootName("person")
    static class Person {
        @JsonAlias("n") public String name;
        @JsonUnwrapped public Job job;
    }
static class Job { public String jobName; public String jobAddress; }
record AorC(@JsonAlias("a") String c) { }
record OuterRecord(@JsonUnwrapped AorC aOrC, String b) { }
static class InnerAlias {
        @JsonAlias({"a", "aa"}) public String c;
    }
static class Outer {
        @JsonUnwrapped public InnerAlias inner;
        public String b;
    }
static class CreatorInner {
        final String c;
        @JsonCreator CreatorInner(@JsonProperty("c") @JsonAlias("a") String c) { this.c = c; }
    }
static class CreatorOuter {
        @JsonUnwrapped public CreatorInner inner;
        public String b;
    }

    void __invoke_unwrappedConflictDetectedVpack() throws Exception {
        try {
            unwrappedConflictDetectedVpack();
        } finally {
        }
    }


    void __invoke_unwrappedNoConflictDifferentNamesVpack() throws Exception {
        try {
            unwrappedNoConflictDifferentNamesVpack();
        } finally {
        }
    }


    void __invoke_unwrappedNoConflictWithPrefixVpack() throws Exception {
        try {
            unwrappedNoConflictWithPrefixVpack();
        } finally {
        }
    }

}
