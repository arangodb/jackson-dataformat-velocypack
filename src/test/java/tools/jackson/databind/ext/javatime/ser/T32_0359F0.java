package tools.jackson.databind.ext.javatime.ser;

import java.time.Year;
import java.time.temporal.Temporal;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0359F0 {
private static final byte[] YEAR_1986 = VPackWireFixtureTest.hex(
            "44 31 39 38 36");
private static final byte[] YEAR_2025 = VPackWireFixtureTest.hex(
            "44 32 30 32 35");
private static final byte[] YEAR_1972_AS_STRING = VPackWireFixtureTest.hex(
            "0b 0f 01 45 76 61 6c 75 65 44 31 39 37 32 03");
private static final byte[] YEAR_TYPE_INFO = VPackWireFixtureTest.hex(
            "06 17 02 4e 6a 61 76 61 2e 74 69 6d 65 2e 59 65 61 72 "
          + "29 d5 07 03 12");
private static final byte[] OFFSET_Z = VPackWireFixtureTest.hex(
            "41 5a");
private static final byte[] OFFSET_PLUS_0300 = VPackWireFixtureTest.hex(
            "46 2b 30 33 3a 30 30");
private static final byte[] OFFSET_MINUS_0630 = VPackWireFixtureTest.hex(
            "46 2d 30 36 3a 33 30");
private static final byte[] OFFSET_TYPE_INFO = VPackWireFixtureTest.hex(
            "06 21 02 54 6a 61 76 61 2e 74 69 6d 65 2e 5a 6f 6e 65 4f 66 66 73 65 74 "
          + "46 2b 30 34 3a 31 35 03 18");

    // Provenance: YearSerTest#testDefaultSerialization.
    void testDefaultSerializationVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .withConfigOverride(Year.class, o -> o.setFormat(
                        JsonFormat.Value.forShape(JsonFormat.Shape.STRING)))
                .build();
        assertArrayEquals(YEAR_1986, mapper.writeValueAsBytes(Year.of(1986)));
        assertArrayEquals(VPackWireFixtureTest.hex("44 32 30 31 33"),
                mapper.writeValueAsBytes(Year.of(2013)));
    }

    // Provenance: YearSerTest#testAsStringSerializationViaAnnotation.
    void testAsStringSerializationViaAnnotationVpack() throws Exception {
        assertArrayEquals(YEAR_1972_AS_STRING,
                new VPackMapper().writeValueAsBytes(new YearAsStringWrapper(Year.of(1972))));
    }

    // Provenance: YearSerTest#testAsStringSerializationViaFormatConfig.
    void testAsStringSerializationViaFormatConfigVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .withConfigOverride(Year.class, o -> o.setFormat(
                        JsonFormat.Value.forShape(JsonFormat.Shape.STRING)))
                .build();
        assertArrayEquals(YEAR_2025, mapper.writeValueAsBytes(Year.of(2025)));
    }

    // Provenance: YearSerTest#testSerializationWithTypeInfo.
    void testSerializationWithTypeInfoVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addMixIn(Temporal.class, YearTypeInfo.class)
                .build();
        assertArrayEquals(YEAR_TYPE_INFO, mapper.writeValueAsBytes(Year.of(2005)));
    }
private static final class YearAsStringWrapper {
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        public Year value;

        YearAsStringWrapper(Year value) {
            this.value = value;
        }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY,
            property = "@class")
    private interface YearTypeInfo { }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY,
            property = "@class")
    private interface ZoneOffsetTypeInfo { }

    void __invoke_testDefaultSerializationVpack() throws Exception {
        try {
            testDefaultSerializationVpack();
        } finally {
        }
    }


    void __invoke_testAsStringSerializationViaAnnotationVpack() throws Exception {
        try {
            testAsStringSerializationViaAnnotationVpack();
        } finally {
        }
    }


    void __invoke_testAsStringSerializationViaFormatConfigVpack() throws Exception {
        try {
            testAsStringSerializationViaFormatConfigVpack();
        } finally {
        }
    }


    void __invoke_testSerializationWithTypeInfoVpack() throws Exception {
        try {
            testSerializationWithTypeInfoVpack();
        } finally {
        }
    }

}
