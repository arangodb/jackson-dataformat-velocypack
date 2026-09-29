package tools.jackson.databind.ext.jdk8;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

import com.fasterxml.jackson.annotation.JsonMerge;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import tools.jackson.dataformat.velocypack.*;

class T32_0373F0 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final ObjectMapper MAPPER_WITHOUT_COERCION = MAPPER.rebuild()
            .disable(MapperFeature.ALLOW_COERCION_OF_SCALARS)
            .build();
private static final byte[] MERGE_A = VPackWireFixtureTest.hex(
            "14 0d 44 6c 69 73 74 13 05 41 61 01 01");
private static final byte[] MERGE_B = VPackWireFixtureTest.hex(
            "14 0d 44 6c 69 73 74 13 05 41 62 01 01");
private static final byte[] OPTIONAL_DOUBLE_EMPTY = VPackWireFixtureTest.hex("18");
private static final byte[] OPTIONAL_DOUBLE_NULL = VPackWireFixtureTest.hex("18");
private static final byte[] OPTIONAL_DOUBLE_STRING_025 = VPackWireFixtureTest.hex(
            "44 30 2e 32 35");
private static final byte[] EMPTY_STRING = VPackWireFixtureTest.hex("40");
private static final byte[] OPTIONAL_DOUBLE_BEAN_NULL = VPackWireFixtureTest.hex(
            "0b 0b 01 45 76 61 6c 75 65 18 03");
private static final byte[] OPTIONAL_DOUBLE_BEAN_STRING_05 = VPackWireFixtureTest.hex(
            "0b 0e 01 45 76 61 6c 75 65 43 30 2e 35 03");
private static final byte[] OPTIONAL_DOUBLE_ARRAY_ABSENT = VPackWireFixtureTest.hex(
            "02 03 18");
private static final byte[] OPTIONAL_DOUBLE_ARRAY_SPECIAL_VALUES = VPackWireFixtureTest.hex(
            "13 22 18 "
          + "1b 00 00 00 00 00 00 f8 7f "
          + "1b 00 00 00 00 00 00 f0 7f "
          + "1b 00 00 00 00 00 00 f0 ff "
          + "31 41 32 06");
private static final byte[] OPTIONAL_DOUBLE_ARRAY_SPECIAL_STRINGS = VPackWireFixtureTest.hex(
            "13 1c 18 "
          + "43 4e 61 4e "
          + "48 49 6e 66 69 6e 69 74 79 "
          + "49 2d 49 6e 66 69 6e 69 74 79 "
          + "31 05");
private static final byte[] TRUE = VPackWireFixtureTest.hex("1a");

    // Provenance: OptionalMergeTest#testMergeToListViaRef().
    void testMergeToListViaRefVpack() throws Exception {
        OptionalListWrapper base = MAPPER.readValue(MERGE_A, OptionalListWrapper.class);
        assertNotNull(base.list);
        assertEquals(Arrays.asList("a"), base.list.get());

        OptionalListWrapper merged = MAPPER.readerForUpdating(base).readValue(MERGE_B);
        assertSame(base, merged);
        assertEquals(Arrays.asList("a", "b"), base.list.get());
    }
static class OptionalListWrapper {
        @JsonMerge
        public Optional<List<String>> list = Optional.empty();
    }
static class OptionalDoubleBean {
        public OptionalDouble value;

        public OptionalDoubleBean() {
            value = OptionalDouble.empty();
        }
    }

    void __invoke_testMergeToListViaRefVpack() throws Exception {
        try {
            testMergeToListViaRefVpack();
        } finally {
        }
    }

}
