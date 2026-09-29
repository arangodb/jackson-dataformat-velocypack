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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0608F0 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] ADMIN_AND_NAME = VPackWireFixtureTest.hex(
            "14 1d 45 61 64 6d 69 6e 48 49 4e 4a 45 43 54 45 44 44 6e 61 6d 65 45 61 6c 69 63 65 02");
private static final byte[] SECRET_AND_OK = VPackWireFixtureTest.hex(
            "14 11 46 73 65 63 72 65 74 41 58 42 6f 6b 41 59 02");
private static final byte[] NAME_ALICE = VPackWireFixtureTest.hex(
            "0b 0f 01 44 6e 61 6d 65 45 61 6c 69 63 65 03");

    // Provenance: UnwrappedPerPropIgnoreProperties5985Test#ignorePropertiesPerPropOnUnwrapped_deser().
    void ignorePropertiesPerPropOnUnwrappedDeserVpack() throws Exception {
        OuterIgnore result = MAPPER.readValue(ADMIN_AND_NAME, OuterIgnore.class);
        assertEquals("alice", result.inner.name);
        assertNull(result.inner.admin);
    }

    // Provenance: UnwrappedPerPropIgnoreProperties5985Test#includePropertiesPerPropOnUnwrapped_deser().
    void includePropertiesPerPropOnUnwrappedDeserVpack() throws Exception {
        OuterInclude result = MAPPER.readValue(ADMIN_AND_NAME, OuterInclude.class);
        assertEquals("alice", result.inner.name);
        assertNull(result.inner.admin);
    }

    // Provenance: UnwrappedPerPropIgnoreProperties5985Test#ignorePerPropBlocksInnerAnySetter_deser().
    void ignorePerPropBlocksInnerAnySetterDeserVpack() throws Exception {
        OuterIgnoreAnySetter result = MAPPER.readValue(SECRET_AND_OK, OuterIgnoreAnySetter.class);
        assertNotNull(result.inner.extra);
        assertFalse(result.inner.extra.containsKey("secret"));
        assertEquals("Y", result.inner.extra.get("ok"));
    }

    // Provenance: UnwrappedPerPropIgnoreProperties5985Test#ignorePropertiesPerPropOnUnwrapped_ser().
    void ignorePropertiesPerPropOnUnwrappedSerVpack() throws Exception {
        OuterIgnore value = new OuterIgnore();
        value.inner.admin = "secret";
        value.inner.name = "alice";
        assertArrayEquals(NAME_ALICE, MAPPER.writeValueAsBytes(value));
    }

    // Provenance: UnwrappedPerPropIgnoreProperties5985Test#includePropertiesPerPropOnUnwrapped_ser().
    void includePropertiesPerPropOnUnwrappedSerVpack() throws Exception {
        OuterInclude value = new OuterInclude();
        value.inner.admin = "secret";
        value.inner.name = "alice";
        assertArrayEquals(NAME_ALICE, MAPPER.writeValueAsBytes(value));
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

    void __invoke_ignorePropertiesPerPropOnUnwrappedDeserVpack() throws Exception {
        try {
            ignorePropertiesPerPropOnUnwrappedDeserVpack();
        } finally {
        }
    }


    void __invoke_includePropertiesPerPropOnUnwrappedDeserVpack() throws Exception {
        try {
            includePropertiesPerPropOnUnwrappedDeserVpack();
        } finally {
        }
    }


    void __invoke_ignorePerPropBlocksInnerAnySetterDeserVpack() throws Exception {
        try {
            ignorePerPropBlocksInnerAnySetterDeserVpack();
        } finally {
        }
    }


    void __invoke_ignorePropertiesPerPropOnUnwrappedSerVpack() throws Exception {
        try {
            ignorePropertiesPerPropOnUnwrappedSerVpack();
        } finally {
        }
    }


    void __invoke_includePropertiesPerPropOnUnwrappedSerVpack() throws Exception {
        try {
            includePropertiesPerPropOnUnwrappedSerVpack();
        } finally {
        }
    }

}
