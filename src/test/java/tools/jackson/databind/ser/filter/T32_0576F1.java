package tools.jackson.databind.ser.filter;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0576F1 {
private static final ObjectMapper MAPPER = VPackMapper.builder()
            .enable(SerializationFeature.APPLY_JSON_INCLUDE_FOR_CONTAINERS)
            .build();
private static final ObjectMapper TYPED_MAPPER = VPackMapper.builder()
            .enable(SerializationFeature.APPLY_JSON_INCLUDE_FOR_CONTAINERS)
            .activateDefaultTyping(new AllowAllTypes(), DefaultTyping.NON_FINAL)
            .build();

    void allNullVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                MAPPER.writeValueAsBytes(new ContainerBean()));
    }

    void containersEmptyAfterContentFilterVpack() throws Exception {
        ContainerBean bean = new ContainerBean();
        bean.myList = new java.util.ArrayList<>(Arrays.asList((String) null));
        bean.myMap = new java.util.HashMap<>();
        bean.myMap.put("1", null);
        assertArrayEquals(VPackWireFixtureTest.hex("0a"), MAPPER.writeValueAsBytes(bean));
    }

    void emptyContainersVpack() throws Exception {
        ContainerBean bean = new ContainerBean();
        bean.myList = List.of();
        bean.myMap = java.util.Map.of();
        assertArrayEquals(VPackWireFixtureTest.hex("0a"), MAPPER.writeValueAsBytes(bean));
    }

    void enumSetAllSuppressedVpack() throws Exception {
        SuppressedEnumBean bean = new SuppressedEnumBean();
        bean.values = EnumSet.of(Size.SMALL);
        assertArrayEquals(VPackWireFixtureTest.hex("0a"), MAPPER.writeValueAsBytes(bean));
    }

    void enumSetKeepsNonSuppressedVpack() throws Exception {
        SuppressedEnumBean bean = new SuppressedEnumBean();
        bean.values = EnumSet.of(Size.SMALL, Size.LARGE);
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 13 01 46 76 61 6c 75 65 73 02 08 45 4c 41 52 47 45 03"),
                MAPPER.writeValueAsBytes(bean));
    }

    void featureDisabledLeavesContainersVpack() throws Exception {
        ContainerBean bean = new ContainerBean();
        bean.myList = new java.util.ArrayList<>(Arrays.asList((String) null));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0e 01 46 6d 79 4c 69 73 74 02 03 18 03"),
                VPackMapper.builder().disable(SerializationFeature.APPLY_JSON_INCLUDE_FOR_CONTAINERS)
                        .build().writeValueAsBytes(bean));
    }

    void intListKeepsValuesVpack() throws Exception {
        IntListBean bean = new IntListBean();
        bean.values = Arrays.asList(null, 42);
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0f 01 46 76 61 6c 75 65 73 02 04 28 2a 03"),
                MAPPER.writeValueAsBytes(bean));
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

    void __invoke_allNullVpack() throws Exception {
        try {
            allNullVpack();
        } finally {
        }
    }


    void __invoke_containersEmptyAfterContentFilterVpack() throws Exception {
        try {
            containersEmptyAfterContentFilterVpack();
        } finally {
        }
    }


    void __invoke_emptyContainersVpack() throws Exception {
        try {
            emptyContainersVpack();
        } finally {
        }
    }


    void __invoke_enumSetAllSuppressedVpack() throws Exception {
        try {
            enumSetAllSuppressedVpack();
        } finally {
        }
    }


    void __invoke_enumSetKeepsNonSuppressedVpack() throws Exception {
        try {
            enumSetKeepsNonSuppressedVpack();
        } finally {
        }
    }


    void __invoke_featureDisabledLeavesContainersVpack() throws Exception {
        try {
            featureDisabledLeavesContainersVpack();
        } finally {
        }
    }


    void __invoke_intListKeepsValuesVpack() throws Exception {
        try {
            intListKeepsValuesVpack();
        } finally {
        }
    }

}
