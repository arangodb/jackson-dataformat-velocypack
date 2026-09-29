package tools.jackson.databind.ser.filter;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0576F0 {
private static final ObjectMapper MAPPER = VPackMapper.builder()
            .enable(SerializationFeature.APPLY_JSON_INCLUDE_FOR_CONTAINERS)
            .build();
private static final ObjectMapper TYPED_MAPPER = VPackMapper.builder()
            .enable(SerializationFeature.APPLY_JSON_INCLUDE_FOR_CONTAINERS)
            .activateDefaultTyping(new AllowAllTypes(), DefaultTyping.NON_FINAL)
            .build();

    void testNonNullContentInclusionVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 10 01 45 69 74 65 6d 73 02 06 41 31 41 32 03"),
                MAPPER.writeValueAsBytes(new NonNullListBean().add("1").add(null).add("2")));
    }

    void testNonEmptyContentInclusionVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 10 01 45 69 74 65 6d 73 02 06 41 31 41 32 03"),
                MAPPER.writeValueAsBytes(new NonEmptyListBean().add("1").add("").add("2")));
    }

    void testNonDefaultContentInclusionVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 10 01 45 69 74 65 6d 73 02 06 41 31 41 32 03"),
                MAPPER.writeValueAsBytes(new NonDefaultListBean().add("1").add(null).add("2")));
    }

    void testTypedStringCollectionNonEmptyVpack() throws Exception {
        byte[] encoded = TYPED_MAPPER.writeValueAsBytes(new TypedStringCollectionNonEmpty(
                new LinkedHashSet<>(Arrays.asList("1", "", "2"))));
        List<?> wire = (List<?>) new VPackMapper().readValue(encoded, Object.class);
        assertEquals(TypedStringCollectionNonEmpty.class.getName(), wire.get(0));
        assertEquals(List.of("java.util.LinkedHashSet", List.of("1", "2")),
                ((java.util.Map<?, ?>) wire.get(1)).get("values"));
    }

    void testTypedStringCollectionNonNullVpack() throws Exception {
        byte[] encoded = TYPED_MAPPER.writeValueAsBytes(new TypedStringCollectionNonNull(
                new LinkedHashSet<>(Arrays.asList("1", null, "2"))));
        List<?> wire = (List<?>) new VPackMapper().readValue(encoded, Object.class);
        assertEquals(TypedStringCollectionNonNull.class.getName(), wire.get(0));
        assertEquals(List.of("java.util.LinkedHashSet", List.of("1", "2")),
                ((java.util.Map<?, ?>) wire.get(1)).get("values"));
    }
static class NonNullListBean {
        @JsonInclude(content = JsonInclude.Include.NON_NULL)
        public List<String> items = new java.util.ArrayList<>();
        NonNullListBean add(String value) { items.add(value); return this; }
    }
static class NonEmptyListBean {
        @JsonInclude(content = JsonInclude.Include.NON_EMPTY)
        public List<String> items = new java.util.ArrayList<>();
        NonEmptyListBean add(String value) { items.add(value); return this; }
    }
static class NonDefaultListBean {
        @JsonInclude(content = JsonInclude.Include.NON_DEFAULT)
        public List<String> items = new java.util.ArrayList<>();
        NonDefaultListBean add(String value) { items.add(value); return this; }
    }
static class TypedStringCollectionNonEmpty {
        @JsonInclude(content = JsonInclude.Include.NON_EMPTY)
        public Collection<String> values;
        TypedStringCollectionNonEmpty(Collection<String> values) { this.values = values; }
    }
static class TypedStringCollectionNonNull {
        @JsonInclude(content = JsonInclude.Include.NON_NULL)
        public Collection<String> values;
        TypedStringCollectionNonNull(Collection<String> values) { this.values = values; }
    }
@JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_EMPTY)
    static class ContainerBean {
        public String myString;
        public List<String> myList;
        public java.util.Map<String, String> myMap;
    }
@JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_EMPTY)
    static class IntListBean {
        public List<Integer> values;
    }
enum Size { SMALL, LARGE }
static class SuppressSmallFilter {
        @Override public boolean equals(Object other) { return other == Size.SMALL; }
        @Override public int hashCode() { return 0; }
    }
@JsonInclude(value = JsonInclude.Include.NON_EMPTY,
            content = JsonInclude.Include.CUSTOM, contentFilter = SuppressSmallFilter.class)
    static class SuppressedEnumBean {
        public EnumSet<Size> values;
    }
static final class AllowAllTypes extends PolymorphicTypeValidator.Base {
        private static final long serialVersionUID = 1L;

        @Override
        public Validity validateBaseType(tools.jackson.databind.DatabindContext context,
                tools.jackson.databind.JavaType baseType) {
            return Validity.ALLOWED;
        }
    }

    void __invoke_testNonNullContentInclusionVpack() throws Exception {
        try {
            testNonNullContentInclusionVpack();
        } finally {
        }
    }


    void __invoke_testNonEmptyContentInclusionVpack() throws Exception {
        try {
            testNonEmptyContentInclusionVpack();
        } finally {
        }
    }


    void __invoke_testNonDefaultContentInclusionVpack() throws Exception {
        try {
            testNonDefaultContentInclusionVpack();
        } finally {
        }
    }


    void __invoke_testTypedStringCollectionNonEmptyVpack() throws Exception {
        try {
            testTypedStringCollectionNonEmptyVpack();
        } finally {
        }
    }


    void __invoke_testTypedStringCollectionNonNullVpack() throws Exception {
        try {
            testTypedStringCollectionNonNullVpack();
        } finally {
        }
    }

}
