package tools.jackson.databind.struct;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.MapperConfig;
import tools.jackson.databind.introspect.Annotated;
import tools.jackson.databind.introspect.AnnotatedField;
import tools.jackson.databind.introspect.NopAnnotationIntrospector;
import tools.jackson.databind.PropertyName;

import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0611F0 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] INNER_FIELDS = VPackWireFixtureTest.hex(
            "14 11 42 69 64 31 44 6e 61 6d 65 44 4a 6f 68 6e 02");
private static final byte[] CREATOR_FIELDS = VPackWireFixtureTest.hex(
            "14 16 43 74 61 67 37 42 69 64 31 44 6e 61 6d 65 44 4a 6f 68 6e 03");
private static final byte[] CREATOR_FIELDS_TAG_FIRST = VPackWireFixtureTest.hex(
            "14 16 43 74 61 67 37 42 69 64 31 44 6e 61 6d 65 44 4a 6f 68 6e 03");
private static final byte[] WRAPPED_FIELDS = VPackWireFixtureTest.hex(
            "0b 24 02 4b 77 72 61 70 70 65 64 4e 61 6d 65 43 42 6f 62 " +
            "4c 77 72 61 70 70 65 64 56 61 6c 75 65 28 2a 03 13");
private static final byte[] ORDINARY_FIELDS = VPackWireFixtureTest.hex(
            "0b 16 02 44 6e 61 6d 65 43 42 6f 62 45 76 61 6c 75 65 28 2a 03 0c");
private static final byte[] BUILDER_FIELDS_TAG_LAST = VPackWireFixtureTest.hex(
            "14 16 42 69 64 31 44 6e 61 6d 65 44 4a 6f 68 6e 43 74 61 67 37 03");

    // Provenance: UnwrappedWithIgnore1075Test#jsonUnwrappedShouldDeserializeFieldsWithGetterInOuterClass().
    void unwrappedIgnoredGetterNameDoesNotHideInnerFieldVpack() throws Exception {
        Outer outer = MAPPER.readValue(INNER_FIELDS, Outer.class);
        assertEquals(Long.valueOf(1), outer.getId());
    }

    // Provenance: UnwrappedWithIgnore1075Test#jsonUnwrappedShouldDeserializeFieldsWithGetterInOuterClassViaCreator().
    void unwrappedIgnoredGetterNameWithCreatorVpack() throws Exception {
        OuterWithCreator outer = MAPPER.readValue(CREATOR_FIELDS, OuterWithCreator.class);
        assertEquals(Long.valueOf(1), outer.getId());
        assertEquals("John", outer.inner.name);
        assertEquals(7, outer.getTag());
    }

    // Provenance: UnwrappedWithIgnore1075Test#jsonUnwrappedShouldDeserializeIgnoredNameViaBuilderCreator().
    void unwrappedIgnoredGetterNameWithBuilderCreatorVpack() throws Exception {
        OuterFromBuilder outer = MAPPER.readValue(BUILDER_FIELDS_TAG_LAST, OuterFromBuilder.class);
        assertEquals(Long.valueOf(1), outer.getId());
        assertEquals("John", outer.inner.name);
        assertEquals(7, outer.tag);

        OuterFromBuilder tagFirst = MAPPER.readValue(CREATOR_FIELDS_TAG_FIRST, OuterFromBuilder.class);
        assertEquals(Long.valueOf(1), tagFirst.getId());
        assertEquals("John", tagFirst.inner.name);
        assertEquals(7, tagFirst.tag);
    }
private static ObjectMapper wrapperMapper(boolean enabled) {
        VPackMapper.Builder builder = VPackMapper.builder()
                .annotationIntrospector(new WrapperNameIntrospector());
        if (enabled) builder.enable(MapperFeature.USE_WRAPPER_NAME_AS_PROPERTY_NAME);
        else builder.disable(MapperFeature.USE_WRAPPER_NAME_AS_PROPERTY_NAME);
        return builder.build();
    }
static class Outer {
        @JsonUnwrapped private Inner inner;
        @JsonIgnore public Long getId() { return inner.id; }
    }
static class Inner {
        @JsonProperty Long id;
        @JsonProperty String name;
    }
static class OuterWithCreator {
        @JsonUnwrapped private Inner inner;
        private final int tag;
        @JsonCreator public OuterWithCreator(@JsonProperty("tag") int tag) { this.tag = tag; }
        @JsonIgnore public Long getId() { return inner.id; }
        public int getTag() { return tag; }
    }
@tools.jackson.databind.annotation.JsonDeserialize(builder = OuterBuilder.class)
    static class OuterFromBuilder {
        final Inner inner;
        final int tag;
        OuterFromBuilder(Inner inner, int tag) { this.inner = inner; this.tag = tag; }
        public Long getId() { return inner.id; }
    }
@tools.jackson.databind.annotation.JsonPOJOBuilder(withPrefix = "with")
    @JsonIgnoreProperties({"id"})
    static class OuterBuilder {
        @JsonUnwrapped Inner inner;
        final int tag;
        @JsonCreator OuterBuilder(@JsonProperty("tag") int tag) { this.tag = tag; }
        public OuterFromBuilder build() { return new OuterFromBuilder(inner, tag); }
    }
static class WrapperBean {
        public int value;
        public String name;
        public WrapperBean() { }
        public WrapperBean(int value, String name) { this.value = value; this.name = name; }
    }
@SuppressWarnings("serial")
    static class WrapperNameIntrospector extends NopAnnotationIntrospector {
        @Override public PropertyName findWrapperName(MapperConfig<?> config, Annotated ann) {
            if (ann instanceof AnnotatedField field) {
                if ("value".equals(field.getName())) return PropertyName.construct("wrappedValue");
                if ("name".equals(field.getName())) return PropertyName.construct("wrappedName");
            }
            return null;
        }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
    @JsonSubTypes(@JsonSubTypes.Type(name = "a_impl", value = A_Impl.class))
    private interface A { }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
    @JsonSubTypes(@JsonSubTypes.Type(name = "b_impl", value = B_Impl.class))
    private interface B { }
private interface C extends B, A { }
private static class A_Impl implements C { }
private static class B_Impl implements B { }
private static class WrapperC { @JsonProperty public C c; }

    void __invoke_unwrappedIgnoredGetterNameDoesNotHideInnerFieldVpack() throws Exception {
        try {
            unwrappedIgnoredGetterNameDoesNotHideInnerFieldVpack();
        } finally {
        }
    }


    void __invoke_unwrappedIgnoredGetterNameWithCreatorVpack() throws Exception {
        try {
            unwrappedIgnoredGetterNameWithCreatorVpack();
        } finally {
        }
    }


    void __invoke_unwrappedIgnoredGetterNameWithBuilderCreatorVpack() throws Exception {
        try {
            unwrappedIgnoredGetterNameWithBuilderCreatorVpack();
        } finally {
        }
    }

}
