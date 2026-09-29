package tools.jackson.databind.jsontype;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.annotation.JsonDeserialize;

import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0414F1 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] INNER = VPackWireFixtureTest.hex(
            "0b 18 01 45 40 74 79 70 65 4d 49 6e 6e 65 72 53 75 62 34 30 36 31 41 03");
private static final byte[] MINIMAL_INNER = VPackWireFixtureTest.hex(
            "0b 28 01 42 40 63 60 2e 54 33 32 5f 30 34 31 34" +
                "46 31 24 4d 69 6e 69 6d 61 6c 49 6e 6e 65 72 53" +
                "75 62 34 30 36 31 41 03");
private static final byte[] BASIC = VPackWireFixtureTest.hex(
            "0b 18 01 45 40 74 79 70 65 4d 42 61 73 69 63 53 75 62 34 30 36 31 41 03");
private static final byte[] MIXED = VPackWireFixtureTest.hex(
            "0b 18 01 45 40 74 79 70 65 4d 4d 69 78 65 64 53 75 62 34 30 36 31 41 03");
private static final byte[] MIXED_MINIMAL = VPackWireFixtureTest.hex(
            "0b 28 01 42 40 63 60 2e 54 33 32 5f 30 34 31 34" +
                "46 31 24 4d 69 78 65 64 4d 69 6e 69 6d 61 6c 53" +
                "75 62 34 30 36 31 41 03");
private static final byte[] MERGE_CHILD = VPackWireFixtureTest.hex(
            "14 2f 45 63 68 69 6c 64 14 26 45 40 74 79 70 65 4b 4d 65 72 67 65 43 68 69 6c 64 41 44 6e 61 6d 65 4b 49 27 6d 20 63 68 69 6c 64 20 41 02 01");
private static final byte[] MERGE_CHILD_CASE_INSENSITIVE = VPackWireFixtureTest.hex(
            "14 2f 45 63 68 69 6c 64 14 26 45 40 74 79 70 65 4b 6d 65 72 67 65 63 68 69 6c 64 61 44 6e 61 6d 65 4b 49 27 6d 20 63 68 69 6c 64 20 41 02 01");
private static final byte[] MERGE_CHILD_UNKNOWN = VPackWireFixtureTest.hex(
            "14 31 45 63 68 69 6c 64 14 28 45 40 74 79 70 65 4d 55 6e 6b 6e 6f 77 6e 43 68 69 6c 64 41 44 6e 61 6d 65 4b 49 27 6d 20 63 68 69 6c 64 20 41 02 01");
private static final byte[] ALIAS_POLYMORPHIC = VPackWireFixtureTest.hex(
            "14 1d 45 76 61 6c 75 65 13 14 42 61 62 14 0e 42 6e 6d 43 42 6f 62 41 41 28 11 02 02 01");
private static final byte[] NO_TYPE_INFO = VPackWireFixtureTest.hex(
            "14 10 45 76 61 6c 75 65 14 07 41 76 28 2a 01 01" );

    // Provenance: NoTypeInfoTest#singleWithNoTypeInfoOverrideDeser().
    void singleWithNoTypeInfoOverrideDeserVpack() throws Exception {
        Value1654UsingCustomSerDeserUntyped4061 result = MAPPER.readValue(
                NO_TYPE_INFO, Value1654UsingCustomSerDeserUntyped4061.class);
        assertEquals(42, result.value.x);
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.SIMPLE_NAME)
    @JsonSubTypes({
            @JsonSubTypes.Type(value = InnerSub4061A.class),
            @JsonSubTypes.Type(value = InnerSub4061B.class)
    })
    static class InnerSuper4061 { }
static class InnerSub4061A extends InnerSuper4061 { }
static class InnerSub4061B extends InnerSuper4061 { }
@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS)
    @JsonSubTypes({
            @JsonSubTypes.Type(value = MinimalInnerSub4061A.class),
            @JsonSubTypes.Type(value = MinimalInnerSub4061B.class)
    })
    static class MinimalInnerSuper4061 { }
static class MinimalInnerSub4061A extends MinimalInnerSuper4061 { }
static class MinimalInnerSub4061B extends MinimalInnerSuper4061 { }
@JsonTypeInfo(use = JsonTypeInfo.Id.SIMPLE_NAME)
    @JsonSubTypes({
            @JsonSubTypes.Type(value = MixedSub4061A.class),
            @JsonSubTypes.Type(value = MixedSub4061B.class)
    })
    static class MixedSuper4061 { }
@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS)
    @JsonSubTypes({
            @JsonSubTypes.Type(value = MixedMinimalSub4061A.class),
            @JsonSubTypes.Type(value = MixedMinimalSub4061B.class)
    })
    static class MixedMinimalSuper4061 { }
static class Root4061 {
        public MergeChild4061 child;
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.SIMPLE_NAME)
    @JsonSubTypes({
            @JsonSubTypes.Type(value = MergeChildA4061.class, name = "MergeChildA"),
            @JsonSubTypes.Type(value = MergeChildB4061.class, name = "MergeChildB")
    })
    static abstract class MergeChild4061 { }
static class MergeChildA4061 extends MergeChild4061 {
        public String name;
    }
static class MergeChildB4061 extends MergeChild4061 {
        public String code;
    }
static class PolyWrapperForAlias4061 {
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_ARRAY)
        @JsonSubTypes(@JsonSubTypes.Type(value = AliasBean4061.class, name = "ab"))
        public Object value;
    }
static class AliasBean4061 {
        @JsonAlias({ "nm", "Name" })
        public String name;
        int _a;

        @JsonCreator
        public AliasBean4061(@JsonProperty("a") @JsonAlias("A") int a) {
            _a = a;
        }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.SIMPLE_NAME)
    @JsonSubTypes({
            @JsonSubTypes.Type(value = DuplicateSubClassA4061.class, name = "DuplicateSubClass"),
            @JsonSubTypes.Type(value = DuplicateSubClassB4061.class, name = "DuplicateSubClass")
    })
    static class DuplicateSuper4061 { }
static class DuplicateSubClassA4061 extends DuplicateSuper4061 { }
static class DuplicateSubClassB4061 extends DuplicateSuper4061 { }
static class Value1654UsingCustomSerDeserUntyped4061 {
        @JsonDeserialize(using = Value1654Deserializer4061.class)
        @JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
        public Value1654_4061 value;
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
    static class Value1654_4061 {
        public int x;

        protected Value1654_4061() { }

        Value1654_4061(int x) {
            this.x = x;
        }
    }
static class Value1654Deserializer4061 extends ValueDeserializer<Value1654_4061> {
        @Override
        public Value1654_4061 deserialize(JsonParser p, DeserializationContext ctxt)
                throws JacksonException {
            JsonNode node = ctxt.readTree(p);
            if (!node.has("v")) {
                ctxt.reportInputMismatch(Value1654_4061.class,
                        "Bad VPack input (no 'v'): " + node);
            }
            return new Value1654_4061(node.path("v").intValue());
        }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.SIMPLE_NAME)
@JsonSubTypes({
        @JsonSubTypes.Type(value = BasicSub4061A.class),
        @JsonSubTypes.Type(value = BasicSub4061B.class)
})
static class BasicSuper4061 {
}
static class BasicSub4061A extends BasicSuper4061 { }
static class BasicSub4061B extends BasicSuper4061 { }
static class MixedSub4061A extends T32_0414F1.MixedSuper4061 { }
static class MixedSub4061B extends T32_0414F1.MixedSuper4061 { }
static class MixedMinimalSub4061A extends T32_0414F1.MixedMinimalSuper4061 { }
static class MixedMinimalSub4061B extends T32_0414F1.MixedMinimalSuper4061 { }

    void __invoke_singleWithNoTypeInfoOverrideDeserVpack() throws Exception {
        try {
            singleWithNoTypeInfoOverrideDeserVpack();
        } finally {
        }
    }

}
