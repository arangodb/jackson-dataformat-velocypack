package tools.jackson.databind.introspect;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import tools.jackson.core.JsonParser;
import tools.jackson.core.StreamReadCapability;
import tools.jackson.core.util.JacksonFeatureSet;
import tools.jackson.core.util.JsonParserDelegate;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonPOJOBuilder;
import tools.jackson.databind.cfg.MapperConfig;
import tools.jackson.databind.introspect.AccessorNamingStrategy;
import tools.jackson.databind.introspect.AnnotatedClass;
import tools.jackson.databind.introspect.DefaultAccessorNamingStrategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0390F1 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] DOC_WITH_DUPS = VPackWireFixtureTest.hex(
            "0b 6c 08 "
          + "45 68 65 6c 6c 6f 45 77 6f 72 6c 64 "
          + "45 6c 69 73 74 73 31 "
          + "45 6c 69 73 74 73 c8 01 ff ff ff ff 25 "
          + "45 6c 69 73 74 73 0b 1b 02 "
          + "45 69 6e 6e 65 72 48 69 6e 74 65 72 6e 61 6c "
          + "44 74 69 6d 65 28 7b 03 12 "
          + "45 6c 69 73 74 73 1a "
          + "46 73 69 6e 67 6c 65 43 6f 6e 65 "
          + "45 6c 69 73 74 73 19 "
          + "45 6c 69 73 74 73 18 "
          + "03 0f 16 23 44 56 5d 4b");
private static final byte[] SIMPLE_DUPLICATE = VPackWireFixtureTest.hex(
            "0b 11 02 43 6b 65 79 41 61 43 6b 65 79 41 62 03 09");
private static final byte[] SINGLE_KEY_B = VPackWireFixtureTest.hex(
            "0b 0a 01 43 6b 65 79 41 62 03");
private static final byte[] XY_INPUT = VPackWireFixtureTest.hex(
            "0b 0d 02 41 78 28 1c 41 79 28 48 03 07");
private static final byte[] AB_INPUT = VPackWireFixtureTest.hex(
            "0b 0d 02 41 61 28 0a 41 62 28 14 03 07");
private static final byte[] NAME_VALUE_INPUT = VPackWireFixtureTest.hex(
            "0b 17 02 44 6e 61 6d 65 44 74 65 73 74 "
          + "45 76 61 6c 75 65 28 2a 03 0d");
private static final byte[] X_INPUT = VPackWireFixtureTest.hex(
            "0b 08 01 41 78 28 63 03");
private static final byte[] Y_INPUT = VPackWireFixtureTest.hex(
            "0b 08 01 41 79 28 37 03");
private static void assertMerged(Object value) {
        Map<?, ?> map = (Map<?, ?>) value;
        assertEquals(3, map.size());
        assertEquals("world", map.get("hello"));
        assertEquals("one", map.get("single"));
        List<?> lists = (List<?>) map.get("lists");
        assertEquals(6, lists.size());
        assertEquals(1, lists.get(0));
        assertEquals(new BigDecimal("2.5"), lists.get(1));
        assertEquals(Map.of("inner", "internal", "time", 123), lists.get(2));
        assertEquals(true, lists.get(3));
        assertEquals(false, lists.get(4));
        assertNull(lists.get(5));
    }
private static JsonParser withDups(byte[] bytes) throws Exception {
        return new WithDupsParser(MAPPER.tokenStreamFactory().createParser(bytes));
    }

    // Provenance: AccessorNamingStrategyTest#testBaseAccessorNaming().
    void testBaseAccessorNamingVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .accessorNaming(new BaseNamingProvider()).build();
        Map<?, ?> wire = mapper.readValue(mapper.writeValueAsBytes(new MixedBean()), Map.class);
        assertEquals(Map.of("x", 72), wire);
    }

    // Provenance: AccessorNamingStrategyTest#testBaseAccessorCustomGetter().
    void testBaseAccessorCustomGetterVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .accessorNaming(new DefaultAccessorNamingStrategy.Provider()).build();
        assertEquals(Map.of("y", 5), mapper.readValue(
                mapper.writeValueAsBytes(new GetterBean()), Map.class));

        mapper = VPackMapper.builder()
                .accessorNaming(new DefaultAccessorNamingStrategy.Provider()
                        .withGetterPrefix("Get").withIsGetterPrefix("Is"))
                .build();
        assertEquals(Map.of("x", 3, "z", true), mapper.readValue(
                mapper.writeValueAsBytes(new GetterBean()), Map.class));
    }

    // Provenance: AccessorNamingStrategyTest#testBaseAccessorCustomSetter().
    void testBaseAccessorCustomSetterVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .accessorNaming(new DefaultAccessorNamingStrategy.Provider()
                        .withSetterPrefix("Put")).build();
        SetterBean value = mapper.readValue(
                VPackWireFixtureTest.hex("0b 08 01 41 79 28 2a 03"), SetterBean.class);
        assertEquals(42, value.yyy);
    }
@SuppressWarnings("serial")
    static class StringStringMap extends java.util.LinkedHashMap<String, String> { }
static class WithDupsParser extends JsonParserDelegate {
        WithDupsParser(JsonParser parser) {
            super(parser);
        }

        @Override
        public JacksonFeatureSet<StreamReadCapability> streamReadCapabilities() {
            return super.streamReadCapabilities().with(StreamReadCapability.DUPLICATE_PROPERTIES);
        }
    }
@JsonDeserialize(builder = ValueClassXY.Builder.class)
    static class ValueClassXY {
        final int x, y;

        ValueClassXY(int x, int y) {
            this.x = x + 1;
            this.y = y + 1;
        }

        static class Builder {
            protected int x, y;

            public Builder x(int value) { x = value; return this; }
            public Builder y(int value) { y = value; return this; }
            public ValueClassXY build() { return new ValueClassXY(x, y); }
        }
    }
@JsonDeserialize(builder = NoPrefixBuilderViaAnnotation.NoPrefixBuilder.class,
            builderPrefix = "")
    static class NoPrefixBuilderViaAnnotation {
        final int a, b;
        NoPrefixBuilderViaAnnotation(int a, int b) { this.a = a; this.b = b; }

        static class NoPrefixBuilder {
            protected int a, b;
            public NoPrefixBuilder a(int value) { a = value; return this; }
            public NoPrefixBuilder b(int value) { b = value; return this; }
            public NoPrefixBuilderViaAnnotation build() {
                return new NoPrefixBuilderViaAnnotation(a, b);
            }
        }
    }
@JsonDeserialize(builder = SetPrefixBuilderViaAnnotation.SetPrefixBuilder.class,
            builderPrefix = "set")
    static class SetPrefixBuilderViaAnnotation {
        final String name;
        final int value;
        SetPrefixBuilderViaAnnotation(String name, int value) {
            this.name = name; this.value = value;
        }

        static class SetPrefixBuilder {
            protected String name;
            protected int value;
            public SetPrefixBuilder setName(String value) { name = value; return this; }
            public SetPrefixBuilder setValue(int value) { this.value = value; return this; }
            public SetPrefixBuilderViaAnnotation build() {
                return new SetPrefixBuilderViaAnnotation(name, value);
            }
        }
    }
@JsonDeserialize(builder = AnnotationOverrideTest.OverriddenBuilder.class,
            builderPrefix = "")
    static class AnnotationOverrideTest {
        final int x;
        AnnotationOverrideTest(int x) { this.x = x; }

        @JsonPOJOBuilder(withPrefix = "with")
        static class OverriddenBuilder {
            protected int x;
            public OverriddenBuilder x(int value) { x = value; return this; }
            public AnnotationOverrideTest build() { return new AnnotationOverrideTest(x); }
        }
    }
@JsonDeserialize(builder = FallbackToPojoBuilderTest.PojoBuilder.class)
    static class FallbackToPojoBuilderTest {
        final int y;
        FallbackToPojoBuilderTest(int y) { this.y = y; }

        @JsonPOJOBuilder(withPrefix = "set")
        static class PojoBuilder {
            protected int y;
            public PojoBuilder setY(int value) { y = value; return this; }
            public FallbackToPojoBuilderTest build() { return new FallbackToPojoBuilderTest(y); }
        }
    }
static class GetterBean {
        public int GetX() { return 3; }
        public int getY() { return 5; }
        public boolean IsZ() { return true; }
    }
static class SetterBean {
        int yyy;
        public void PutY(int value) { yyy = value; }
        public void y(int value) { throw new Error(); }
        public void setY(int value) { throw new Error(); }
    }
static class MixedBean {
        public int x = 72;
        public int getY() { return 3; }
    }
static class BaseNamingProvider extends DefaultAccessorNamingStrategy.Provider {
        @Override
        public AccessorNamingStrategy forPOJO(MapperConfig<?> config, AnnotatedClass valueClass) {
            return new AccessorNamingStrategy.Base();
        }
    }

    void __invoke_testBaseAccessorNamingVpack() throws Exception {
        try {
            testBaseAccessorNamingVpack();
        } finally {
        }
    }


    void __invoke_testBaseAccessorCustomGetterVpack() throws Exception {
        try {
            testBaseAccessorCustomGetterVpack();
        } finally {
        }
    }


    void __invoke_testBaseAccessorCustomSetterVpack() throws Exception {
        try {
            testBaseAccessorCustomSetterVpack();
        } finally {
        }
    }

}
