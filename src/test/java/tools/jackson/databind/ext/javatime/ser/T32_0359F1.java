package tools.jackson.databind.ext.javatime.ser;

import java.time.Year;
import java.time.ZoneOffset;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0359F1 {
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

    // Provenance: ZoneOffsetSerTest#testSerialization01.
    void testZoneOffsetSerialization01Vpack() throws Exception {
        assertArrayEquals(OFFSET_Z,
                new VPackMapper().writeValueAsBytes(ZoneOffset.of("Z")));
    }

    // Provenance: ZoneOffsetSerTest#testSerialization02.
    void testZoneOffsetSerialization02Vpack() throws Exception {
        assertArrayEquals(OFFSET_PLUS_0300,
                new VPackMapper().writeValueAsBytes(ZoneOffset.of("+0300")));
    }

    // Provenance: ZoneOffsetSerTest#testSerialization03.
    void testZoneOffsetSerialization03Vpack() throws Exception {
        assertArrayEquals(OFFSET_MINUS_0630,
                new VPackMapper().writeValueAsBytes(ZoneOffset.of("-0630")));
    }

    // Provenance: ZoneOffsetSerTest#testSerializationWithTypeInfo03.
    void testZoneOffsetSerializationWithTypeInfo03Vpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addMixIn(java.time.ZoneId.class, ZoneOffsetTypeInfo.class)
                .build();
        assertArrayEquals(OFFSET_TYPE_INFO,
                mapper.writeValueAsBytes(ZoneOffset.of("+0415")));
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

    void __invoke_testZoneOffsetSerialization01Vpack() throws Exception {
        try {
            testZoneOffsetSerialization01Vpack();
        } finally {
        }
    }


    void __invoke_testZoneOffsetSerialization02Vpack() throws Exception {
        try {
            testZoneOffsetSerialization02Vpack();
        } finally {
        }
    }


    void __invoke_testZoneOffsetSerialization03Vpack() throws Exception {
        try {
            testZoneOffsetSerialization03Vpack();
        } finally {
        }
    }


    void __invoke_testZoneOffsetSerializationWithTypeInfo03Vpack() throws Exception {
        try {
            testZoneOffsetSerializationWithTypeInfo03Vpack();
        } finally {
        }
    }

}
