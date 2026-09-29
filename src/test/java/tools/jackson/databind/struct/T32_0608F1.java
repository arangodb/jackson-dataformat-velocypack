package tools.jackson.databind.struct;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.exc.InvalidDefinitionException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0608F1 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] ADMIN_AND_NAME = VPackWireFixtureTest.hex(
            "14 1d 45 61 64 6d 69 6e 48 49 4e 4a 45 43 54 45 44 44 6e 61 6d 65 45 61 6c 69 63 65 02");
private static final byte[] SECRET_AND_OK = VPackWireFixtureTest.hex(
            "14 11 46 73 65 63 72 65 74 41 58 42 6f 6b 41 59 02");
private static final byte[] NAME_ALICE = VPackWireFixtureTest.hex(
            "0b 0f 01 44 6e 61 6d 65 45 61 6c 69 63 65 03");

    // Provenance: UnwrappedPropertyConflict2883Test#testConflictViaJsonPropertyRename().
    void conflictViaJsonPropertyRenameVpack() {
        assertThrows(InvalidDefinitionException.class,
                () -> MAPPER.writeValueAsBytes(new OuterRenameConflict()));
    }

    // Provenance: UnwrappedPropertyConflict2883Test#testConflictViaNamingStrategy().
    void conflictViaNamingStrategyVpack() {
        ObjectMapper mapper = VPackMapper.builder()
                .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE).build();
        assertThrows(InvalidDefinitionException.class,
                () -> mapper.writeValueAsBytes(new OuterNamingStrategyConflict()));
    }

    // Provenance: UnwrappedPropertyConflict2883Test#testNestedUnwrappedNoConflict().
    void nestedUnwrappedNoConflictVpack() throws Exception {
        byte[] expected = VPackWireFixtureTest.hex(
                "0b 12 03 41 61 41 61 41 62 41 62 41 63 41 63 03 07 0b");
        byte[] actual = MAPPER.writeValueAsBytes(new Level1());
        assertArrayEquals(expected, actual, java.util.Arrays.toString(actual));
    }

    // Provenance: UnwrappedPropertyConflict2883Test#testNoConflictWhenUnwrappedPropertyIsIgnored().
    void noConflictWhenUnwrappedPropertyIsIgnoredVpack() throws Exception {
        byte[] expected = VPackWireFixtureTest.hex(
                "0b 10 02 42 69 64 30 45 6f 74 68 65 72 37 03 07");
        assertArrayEquals(expected, MAPPER.writeValueAsBytes(new OuterConflictResolvedByIgnore()));
    }

    // Provenance: UnwrappedPropertyConflict2883Test#testNoFalseConflictWithNestedUnwrappedFieldName().
    void noFalseConflictWithNestedUnwrappedFieldNameVpack() throws Exception {
        byte[] bytes = MAPPER.writeValueAsBytes(new OuterWithPhantomCollision());
        JsonNode tree = MAPPER.readTree(bytes);
        assertEquals("outer", tree.get("l3").stringValue());
        assertEquals("b", tree.get("b").stringValue());
        assertEquals("c", tree.get("c").stringValue());
    }

    // Provenance: UnwrappedPropertyConflict2883Test#testSelfReferentialUnwrapDoesNotHang().
    void selfReferentialUnwrapDoesNotHangVpack() throws Exception {
        SelfUnwrapped value = new SelfUnwrapped();
        value.self = null;
        JsonNode tree = MAPPER.readTree(MAPPER.writeValueAsBytes(value));
        assertEquals(1, tree.get("id").intValue());
    }

    // Provenance: UnwrappedPropertyConflict2883Test#testTwoUnwrappedConflictDetected().
    void twoUnwrappedConflictDetectedVpack() {
        assertThrows(InvalidDefinitionException.class,
                () -> MAPPER.writeValueAsBytes(new TwoUnwrappedConflict()));
    }
static class Inner { public String admin; public String name; }
static class OuterIgnore { @JsonUnwrapped @JsonIgnoreProperties({"admin"}) public Inner inner = new Inner(); }
static class OuterInclude { @JsonUnwrapped @JsonIncludeProperties({"name"}) public Inner inner = new Inner(); }
static class InnerWithAnySetter {
        public Map<String, Object> extra = new LinkedHashMap<>();
        @JsonAnySetter public void set(String key, Object value) { extra.put(key, value); }
    }
static class OuterIgnoreAnySetter {
        @JsonUnwrapped @JsonIgnoreProperties({"secret"}) public InnerWithAnySetter inner = new InnerWithAnySetter();
    }
static class InnerB { public int ba = 3; }
static class InnerD { public int da = 4; }
static class InnerC { public InnerD b = new InnerD(); }
static class TwoUnwrappedConflict {
        @JsonUnwrapped public HasId1 first = new HasId1();
        @JsonUnwrapped public HasId2 second = new HasId2();
    }
static class HasId1 { public int id = 1; }
static class HasId2 { public int id = 2; }
static class OuterRenameConflict {
        public int id = 0;
        @JsonUnwrapped public RenamedToId inner = new RenamedToId();
    }
static class RenamedToId { @JsonProperty("id") public int internalKey = 7; }
static class OuterNamingStrategyConflict {
        public int fooBar = 1;
        @JsonUnwrapped public HasFooBar inner = new HasFooBar();
    }
static class HasFooBar { public int fooBar = 2; }
static class SelfUnwrapped { public int id = 1; @JsonUnwrapped public SelfUnwrapped self; }
static class Level1 { public String a = "a"; @JsonUnwrapped public Level2 l2 = new Level2(); }
static class Level2 { public String b = "b"; @JsonUnwrapped public Level3 l3 = new Level3(); }
static class Level3 { public String c = "c"; }
static class IgnoredIdHolder { @JsonIgnore public int id = 99; public int other = 7; }
static class OuterConflictResolvedByIgnore {
        public int id = 0;
        @JsonUnwrapped public IgnoredIdHolder inner = new IgnoredIdHolder();
    }
static class OuterWithPhantomCollision {
        public String l3 = "outer";
        @JsonUnwrapped public Level2 inner = new Level2();
    }

    void __invoke_conflictViaJsonPropertyRenameVpack() throws Exception {
        try {
            conflictViaJsonPropertyRenameVpack();
        } finally {
        }
    }


    void __invoke_conflictViaNamingStrategyVpack() throws Exception {
        try {
            conflictViaNamingStrategyVpack();
        } finally {
        }
    }


    void __invoke_nestedUnwrappedNoConflictVpack() throws Exception {
        try {
            nestedUnwrappedNoConflictVpack();
        } finally {
        }
    }


    void __invoke_noConflictWhenUnwrappedPropertyIsIgnoredVpack() throws Exception {
        try {
            noConflictWhenUnwrappedPropertyIsIgnoredVpack();
        } finally {
        }
    }


    void __invoke_noFalseConflictWithNestedUnwrappedFieldNameVpack() throws Exception {
        try {
            noFalseConflictWithNestedUnwrappedFieldNameVpack();
        } finally {
        }
    }


    void __invoke_selfReferentialUnwrapDoesNotHangVpack() throws Exception {
        try {
            selfReferentialUnwrapDoesNotHangVpack();
        } finally {
        }
    }


    void __invoke_twoUnwrappedConflictDetectedVpack() throws Exception {
        try {
            twoUnwrappedConflictDetectedVpack();
        } finally {
        }
    }

}
