package tools.jackson.databind.ext.javatime.deser;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.core.type.TypeReference;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0335F1 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final ObjectReader OFFSET_READER = MAPPER.readerFor(ZoneOffset.class);
private static final TypeReference<Map<String, ZoneOffset>> OFFSET_MAP =
            new TypeReference<Map<String, ZoneOffset>>() { };
private static final byte[] ZONE_OFFSET_Z = VPackWireFixtureTest.hex("41 5a");
private static final byte[] ZONE_OFFSET_PLUS_0300 = VPackWireFixtureTest.hex(
            "45 2b 30 33 30 30");
private static final byte[] ZONE_OFFSET_MINUS_0630 = VPackWireFixtureTest.hex(
            "46 2d 30 36 3a 33 30");
private static final byte[] INVALID_ZONE_OFFSET = VPackWireFixtureTest.hex(
            "4f 6e 6f 74 61 7a 6f 6e 65 64 6f 66 66 73 65 74");
private static final byte[] SINGLE_ZONE_OFFSET_ARRAY = VPackWireFixtureTest.hex(
            "06 0a 01 45 2b 30 33 30 30 03");
private static final byte[] EMPTY_ARRAY = VPackWireFixtureTest.hex("01");
private static final byte[] TYPED_ZONE_OFFSET = VPackWireFixtureTest.hex(
            "06 20 02 54 6a 61 76 61 2e 74 69 6d 65 2e 5a 6f 6e 65 4f 66 66 73 65 74 "
          + "45 2b 30 34 31 35 03 18");
private static final byte[] ZONE_OFFSET_NULL_PROPERTY = VPackWireFixtureTest.hex(
            "14 0f 4a 7a 6f 6e 65 4f 66 66 73 65 74 18 01");
private static final byte[] ZONE_OFFSET_EMPTY_PROPERTY = VPackWireFixtureTest.hex(
            "14 0f 4a 7a 6f 6e 65 4f 66 66 73 65 74 40 01");
private static final byte[] TWO_SPACES = VPackWireFixtureTest.hex("42 20 20");

    // Provenance: ZoneOffsetDeserTest#testSimpleZoneOffsetDeser.
    void testSimpleZoneOffsetDeserVpack() throws Exception {
        assertEquals(ZoneOffset.of("Z"), OFFSET_READER.readValue(ZONE_OFFSET_Z));
        assertEquals(ZoneOffset.of("+0300"), OFFSET_READER.readValue(ZONE_OFFSET_PLUS_0300));
        assertEquals(ZoneOffset.of("-0630"), OFFSET_READER.readValue(ZONE_OFFSET_MINUS_0630));
    }

    // Provenance: ZoneOffsetDeserTest#testPolymorphicZoneOffsetDeser.
    void testPolymorphicZoneOffsetDeserVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addMixIn(ZoneId.class, ZoneIdTypeInfo.class)
                .build();
        ZoneId value = mapper.readValue(TYPED_ZONE_OFFSET, ZoneId.class);
        assertInstanceOf(ZoneOffset.class, value);
        assertEquals(ZoneOffset.of("+0415"), value);
    }

    // Provenance: ZoneOffsetDeserTest#testDeserializationWithTypeInfo03.
    void testDeserializationWithTypeInfo03Vpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addMixIn(ZoneId.class, ZoneIdTypeInfo.class)
                .build();
        ZoneId value = mapper.readValue(TYPED_ZONE_OFFSET, ZoneId.class);
        assertInstanceOf(ZoneOffset.class, value, "The value should be a ZoneOffset.");
        assertEquals(ZoneOffset.of("+0415"), value, "The value is not correct.");
    }

    // Provenance: ZoneOffsetDeserTest#testBadDeserializationAsString01.
    void testBadDeserializationAsString01Vpack() {
        assertThrows(MismatchedInputException.class,
                () -> OFFSET_READER.readValue(INVALID_ZONE_OFFSET));
    }

    // Provenance: ZoneOffsetDeserTest#testDeserializationAsArrayDisabled.
    void testDeserializationAsArrayDisabledVpack() {
        assertThrows(MismatchedInputException.class,
                () -> OFFSET_READER.readValue(SINGLE_ZONE_OFFSET_ARRAY));
    }

    // Provenance: ZoneOffsetDeserTest#testDeserializationAsEmptyArrayDisabled.
    void testDeserializationAsEmptyArrayDisabledVpack() {
        assertThrows(MismatchedInputException.class,
                () -> OFFSET_READER.readValue(EMPTY_ARRAY));
        assertThrows(MismatchedInputException.class,
                () -> OFFSET_READER.with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
                        .readValue(EMPTY_ARRAY));
    }

    // Provenance: ZoneOffsetDeserTest#testDeserializationAsArrayEnabled.
    void testDeserializationAsArrayEnabledVpack() throws Exception {
        ZoneOffset value = OFFSET_READER
                .with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
                .readValue(SINGLE_ZONE_OFFSET_ARRAY);
        assertEquals(ZoneOffset.of("+0300"), value);
    }

    // Provenance: ZoneOffsetDeserTest#testDeserializationAsEmptyArrayEnabled.
    void testDeserializationAsEmptyArrayEnabledVpack() throws Exception {
        ZoneOffset value = OFFSET_READER
                .with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS,
                        DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
                .readValue(EMPTY_ARRAY);
        assertNull(value);
    }

    // Provenance: ZoneOffsetDeserTest#testLenientDeserializeFromEmptyString.
    void testLenientDeserializeFromEmptyStringVpack() throws Exception {
        ObjectReader reader = MAPPER.readerFor(OFFSET_MAP);
        Map<String, ZoneOffset> fromNull = reader.readValue(ZONE_OFFSET_NULL_PROPERTY);
        Map<String, ZoneOffset> fromEmpty = reader.readValue(ZONE_OFFSET_EMPTY_PROPERTY);
        assertNull(fromNull.get("zoneOffset"));
        assertNull(fromEmpty.get("zoneOffset"));
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY,
            property = "@class")
    private interface ZoneIdTypeInfo { }

    void __invoke_testSimpleZoneOffsetDeserVpack() throws Exception {
        try {
            testSimpleZoneOffsetDeserVpack();
        } finally {
        }
    }


    void __invoke_testPolymorphicZoneOffsetDeserVpack() throws Exception {
        try {
            testPolymorphicZoneOffsetDeserVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationWithTypeInfo03Vpack() throws Exception {
        try {
            testDeserializationWithTypeInfo03Vpack();
        } finally {
        }
    }


    void __invoke_testBadDeserializationAsString01Vpack() throws Exception {
        try {
            testBadDeserializationAsString01Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsArrayDisabledVpack() throws Exception {
        try {
            testDeserializationAsArrayDisabledVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsEmptyArrayDisabledVpack() throws Exception {
        try {
            testDeserializationAsEmptyArrayDisabledVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsArrayEnabledVpack() throws Exception {
        try {
            testDeserializationAsArrayEnabledVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsEmptyArrayEnabledVpack() throws Exception {
        try {
            testDeserializationAsEmptyArrayEnabledVpack();
        } finally {
        }
    }


    void __invoke_testLenientDeserializeFromEmptyStringVpack() throws Exception {
        try {
            testLenientDeserializeFromEmptyStringVpack();
        } finally {
        }
    }

}
