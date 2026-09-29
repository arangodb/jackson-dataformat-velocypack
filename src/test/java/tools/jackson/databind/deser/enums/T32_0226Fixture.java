package tools.jackson.databind.deser.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.cfg.EnumFeature;
import tools.jackson.databind.exc.InvalidFormatException;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0226Fixture {
private static final ObjectMapper MAPPER = VPackMapper.builder()
            .disable(EnumFeature.READ_ENUMS_USING_TO_STRING)
            .build();
private static final byte[] FOO = VPackWireFixtureTest.hex("43 66 6f 6f");
private static final byte[] EMPTY_STRING = VPackWireFixtureTest.hex("40");
private static final byte[] INDEX_ONE = VPackWireFixtureTest.hex("31");
private static final byte[] INDEX_TWO = VPackWireFixtureTest.hex("32");
private static final byte[] INDEX_THREE = VPackWireFixtureTest.hex("33");
private static final byte[] INDEX_FOUR = VPackWireFixtureTest.hex("34");
private static final byte[] MIXED_CASE_ENUM_MAP_FIELD = VPackWireFixtureTest.hex(
            "14 16 43 6d 61 70 14 0f 47 4a 41 43 6b 73 6f 6e 43 76 61 6c 01 01");
private static final byte[] RENAMED_VALUES = VPackWireFixtureTest.hex(
            "02 06 41 62 41 61");
private static final byte[] MIXIN_VALUES = VPackWireFixtureTest.hex(
            "02 12 47 62 5f 6d 69 78 69 6e 47 61 5f 6d 69 78 69 6e");

    // Provenance: EnumDeserializationTest#testEnumValuesCaseSensitivity.
    void testEnumValuesCaseSensitivityVpack() {
        InvalidFormatException exception = assertThrows(InvalidFormatException.class,
                () -> MAPPER.readValue(MIXED_CASE_ENUM_MAP_FIELD,
                        ClassWithEnumMapKey.class));
        assertEquals(TestEnum.class, exception.getTargetType());
    }

    // Provenance: EnumDeserializationTest#testEnumWithDefaultAnnotation.
    void testEnumWithDefaultAnnotationVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(EnumFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .build();
        assertSame(EnumWithDefaultAnno.OTHER,
                mapper.readValue(FOO, EnumWithDefaultAnno.class));
    }

    // Provenance: EnumDeserializationTest#testEnumWithDefaultAnnotationUsingIndexInBound1.
    void testEnumWithDefaultAnnotationUsingIndexInBound1Vpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(EnumFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .build();
        assertSame(EnumWithDefaultAnno.B,
                mapper.readValue(INDEX_ONE, EnumWithDefaultAnno.class));
    }

    // Provenance: EnumDeserializationTest#testEnumWithDefaultAnnotationUsingIndexInBound2.
    void testEnumWithDefaultAnnotationUsingIndexInBound2Vpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(EnumFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .build();
        assertSame(EnumWithDefaultAnno.OTHER,
                mapper.readValue(INDEX_TWO, EnumWithDefaultAnno.class));
    }

    // Provenance: EnumDeserializationTest#testEnumWithDefaultAnnotationUsingIndexOutOfBound.
    void testEnumWithDefaultAnnotationUsingIndexOutOfBoundVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(EnumFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .build();
        assertSame(EnumWithDefaultAnno.OTHER,
                mapper.readValue(INDEX_FOUR, EnumWithDefaultAnno.class));
    }

    // Provenance: EnumDeserializationTest#testEnumWithDefaultAnnotationUsingIndexSameAsLength.
    void testEnumWithDefaultAnnotationUsingIndexSameAsLengthVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(EnumFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .build();
        assertSame(EnumWithDefaultAnno.OTHER,
                mapper.readValue(INDEX_THREE, EnumWithDefaultAnno.class));
    }

    // Provenance: EnumDeserializationTest#testEnumWithDefaultAnnotationWithConstructor.
    void testEnumWithDefaultAnnotationWithConstructorVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(EnumFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .build();
        assertNull(mapper.readValue(FOO, EnumWithDefaultAnnoAndConstructor.class));
    }

    // Provenance: EnumDeserializationTest#testEnumWithJsonPropertyRename.
    void testEnumWithJsonPropertyRenameVpack() throws Exception {
        assertArrayEquals(RENAMED_VALUES,
                MAPPER.writeValueAsBytes(new EnumWithPropertyAnno[] {
                        EnumWithPropertyAnno.B, EnumWithPropertyAnno.A
                }));
        EnumWithPropertyAnno[] result = MAPPER.readValue(RENAMED_VALUES,
                EnumWithPropertyAnno[].class);
        assertEquals(2, result.length);
        assertSame(EnumWithPropertyAnno.B, result[0]);
        assertSame(EnumWithPropertyAnno.A, result[1]);
    }

    // Provenance: EnumDeserializationTest#testEnumWithJsonPropertyRenameMixin.
    void testEnumWithJsonPropertyRenameMixinVpack() throws Exception {
        ObjectMapper mixinMapper = VPackMapper.builder()
                .addMixIn(EnumWithPropertyAnnoBase.class, EnumWithPropertyAnnoMixin.class)
                .build();
        assertArrayEquals(MIXIN_VALUES,
                mixinMapper.writeValueAsBytes(new EnumWithPropertyAnnoBase[] {
                        EnumWithPropertyAnnoBase.B, EnumWithPropertyAnnoBase.A
                }));
        EnumWithPropertyAnnoBase[] result = mixinMapper.readValue(MIXIN_VALUES,
                EnumWithPropertyAnnoBase[].class);
        assertEquals(2, result.length);
        assertSame(EnumWithPropertyAnnoBase.B, result[0]);
        assertSame(EnumWithPropertyAnnoBase.A, result[1]);
    }

    // Provenance: EnumDeserializationTest#testEnumWithJsonPropertyRenameWithToString.
    void testEnumWithJsonPropertyRenameWithToStringVpack() throws Exception {
        ObjectReader reader = MAPPER.readerFor(EnumWithPropertyAnno.class)
                .with(EnumFeature.READ_ENUMS_USING_TO_STRING)
                .with(EnumFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);
        assertSame(EnumWithPropertyAnno.A, reader.readValue(
                VPackWireFixtureTest.hex("41 61")));
        assertSame(EnumWithPropertyAnno.B, reader.readValue(
                VPackWireFixtureTest.hex("41 62")));
        assertNull(reader.readValue(VPackWireFixtureTest.hex("42 62 62")));
        assertSame(EnumWithPropertyAnno.C, reader.readValue(
                VPackWireFixtureTest.hex("42 63 63")));
    }

    // Provenance: EnumDeserializationTest#testEnumsWithEmpty.
    void testEnumsWithEmptyVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
                .build();
        assertNull(mapper.readValue(EMPTY_STRING, TestEnum.class));
    }

    // Provenance: EnumDeserializationTest#testEnumsWithIndex.
    void testEnumsWithIndexVpack() throws Exception {
        ObjectMapper writer = VPackMapper.builder()
                .enable(EnumFeature.WRITE_ENUMS_USING_INDEX)
                .build();
        assertArrayEquals(INDEX_ONE, writer.writeValueAsBytes(TestEnum.RULES));
        assertSame(TestEnum.RULES, MAPPER.readValue(INDEX_ONE, TestEnum.class));
    }
enum TestEnum { JACKSON, RULES, OK }
static class ClassWithEnumMapKey {
        @JsonProperty
        java.util.Map<TestEnum, String> map;
    }
enum EnumWithPropertyAnno {
        @JsonProperty("a")
        A,
        @JsonProperty("b")
        B {
            @Override
            public String toString() {
                return "bb";
            }
        },
        @JsonProperty("cc")
        C
    }
enum EnumWithPropertyAnnoBase {
        A,
        B {
            @Override
            public String toString() {
                return "bb";
            }
        }
    }
enum EnumWithPropertyAnnoMixin {
        @JsonProperty("a_mixin")
        A,
        @JsonProperty("b_mixin")
        B {
            @Override
            public String toString() {
                return "bb";
            }
        }
    }
enum EnumWithDefaultAnno {
        A, B,
        @JsonEnumDefaultValue
        OTHER
    }
enum EnumWithDefaultAnnoAndConstructor {
        A, B,
        @JsonEnumDefaultValue
        OTHER;

        @JsonCreator
        public static EnumWithDefaultAnnoAndConstructor fromId(String value) {
            for (EnumWithDefaultAnnoAndConstructor candidate : values()) {
                if (candidate.name().toLowerCase().equals(value)) {
                    return candidate;
                }
            }
            return null;
        }
    }

    void __invoke_testEnumValuesCaseSensitivityVpack() throws Exception {
        try {
            testEnumValuesCaseSensitivityVpack();
        } finally {
        }
    }


    void __invoke_testEnumWithDefaultAnnotationVpack() throws Exception {
        try {
            testEnumWithDefaultAnnotationVpack();
        } finally {
        }
    }


    void __invoke_testEnumWithDefaultAnnotationUsingIndexInBound1Vpack() throws Exception {
        try {
            testEnumWithDefaultAnnotationUsingIndexInBound1Vpack();
        } finally {
        }
    }


    void __invoke_testEnumWithDefaultAnnotationUsingIndexInBound2Vpack() throws Exception {
        try {
            testEnumWithDefaultAnnotationUsingIndexInBound2Vpack();
        } finally {
        }
    }


    void __invoke_testEnumWithDefaultAnnotationUsingIndexOutOfBoundVpack() throws Exception {
        try {
            testEnumWithDefaultAnnotationUsingIndexOutOfBoundVpack();
        } finally {
        }
    }


    void __invoke_testEnumWithDefaultAnnotationUsingIndexSameAsLengthVpack() throws Exception {
        try {
            testEnumWithDefaultAnnotationUsingIndexSameAsLengthVpack();
        } finally {
        }
    }


    void __invoke_testEnumWithDefaultAnnotationWithConstructorVpack() throws Exception {
        try {
            testEnumWithDefaultAnnotationWithConstructorVpack();
        } finally {
        }
    }


    void __invoke_testEnumWithJsonPropertyRenameVpack() throws Exception {
        try {
            testEnumWithJsonPropertyRenameVpack();
        } finally {
        }
    }


    void __invoke_testEnumWithJsonPropertyRenameMixinVpack() throws Exception {
        try {
            testEnumWithJsonPropertyRenameMixinVpack();
        } finally {
        }
    }


    void __invoke_testEnumWithJsonPropertyRenameWithToStringVpack() throws Exception {
        try {
            testEnumWithJsonPropertyRenameWithToStringVpack();
        } finally {
        }
    }


    void __invoke_testEnumsWithEmptyVpack() throws Exception {
        try {
            testEnumsWithEmptyVpack();
        } finally {
        }
    }


    void __invoke_testEnumsWithIndexVpack() throws Exception {
        try {
            testEnumsWithIndexVpack();
        } finally {
        }
    }

}
