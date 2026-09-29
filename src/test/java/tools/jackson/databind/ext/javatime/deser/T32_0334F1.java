package tools.jackson.databind.ext.javatime.deser;

import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0334F1 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final TypeReference<Map<String, YearMonth>> YEAR_MONTH_MAP =
            new TypeReference<Map<String, YearMonth>>() { };
private static final TypeReference<Map<String, ZoneId>> ZONE_ID_MAP =
            new TypeReference<Map<String, ZoneId>>() { };
private static final byte[] YEAR_MONTH_2000 = VPackWireFixtureTest.hex(
            "47 32 30 30 30 2d 30 31");
private static final byte[] YEAR_MONTH_SINGLE_ARRAY = VPackWireFixtureTest.hex(
            "13 0b 47 32 30 30 30 2d 30 31 01");
private static final byte[] EMPTY_ARRAY = VPackWireFixtureTest.hex("01");
private static final byte[] YEAR_MONTH_ABOVE_10K = VPackWireFixtureTest.hex(
            "48 31 30 30 30 30 2d 30 31");
private static final byte[] YEAR_MONTH_NULL_PROPERTY = VPackWireFixtureTest.hex(
            "14 0e 49 79 65 61 72 4d 6f 6e 74 68 18 01");
private static final byte[] YEAR_MONTH_EMPTY_PROPERTY = VPackWireFixtureTest.hex(
            "14 0e 49 79 65 61 72 4d 6f 6e 74 68 40 01");
private static final byte[] ZONE_ID_CHICAGO = VPackWireFixtureTest.hex(
            "4f 41 6d 65 72 69 63 61 2f 43 68 69 63 61 67 6f");
private static final byte[] ZONE_ID_ANCHORAGE = VPackWireFixtureTest.hex(
            "51 41 6d 65 72 69 63 61 2f 41 6e 63 68 6f 72 61 67 65");
private static final byte[] TYPED_ZONE_ID = VPackWireFixtureTest.hex(
            "06 25 02 50 6a 61 76 61 2e 74 69 6d 65 2e 5a 6f 6e 65 49 64 "
          + "4e 41 6d 65 72 69 63 61 2f 44 65 6e 76 65 72 03 14");
private static final byte[] TYPED_ZONE_REGION = VPackWireFixtureTest.hex(
            "06 29 02 54 6a 61 76 61 2e 74 69 6d 65 2e 5a 6f 6e 65 52 65 67 69 6f 6e "
          + "4e 41 6d 65 72 69 63 61 2f 44 65 6e 76 65 72 03 18");
private static final byte[] ZONE_ID_NULL_PROPERTY = VPackWireFixtureTest.hex(
            "14 0b 46 7a 6f 6e 65 49 64 18 01");
private static final byte[] ZONE_ID_EMPTY_PROPERTY = VPackWireFixtureTest.hex(
            "14 0b 46 7a 6f 6e 65 49 64 40 01");

    // Provenance: ZoneIdDeserTest#testDeserialization01.
    void testZoneIdDeserialization01Vpack() throws Exception {
        assertEquals(ZoneId.of("America/Chicago"),
                MAPPER.readValue(ZONE_ID_CHICAGO, ZoneId.class));
    }

    // Provenance: ZoneIdDeserTest#testDeserialization02.
    void testZoneIdDeserialization02Vpack() throws Exception {
        assertEquals(ZoneId.of("America/Anchorage"),
                MAPPER.readValue(ZONE_ID_ANCHORAGE, ZoneId.class));
    }

    // Provenance: ZoneIdDeserTest#testDeserializationWithTypeInfo02.
    void testZoneIdDeserializationWithTypeInfo02Vpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addMixIn(ZoneId.class, ZoneIdTypeInfo.class)
                .build();
        assertEquals(ZoneId.of("America/Denver"),
                mapper.readValue(TYPED_ZONE_ID, ZoneId.class));
    }

    // Provenance: ZoneIdDeserTest#testLenientDeserializeFromEmptyString.
    void testZoneIdLenientDeserializeFromEmptyStringVpack() throws Exception {
        ObjectReader reader = MAPPER.readerFor(ZONE_ID_MAP);

        Map<String, ZoneId> fromNull = reader.readValue(ZONE_ID_NULL_PROPERTY);
        Map<String, ZoneId> fromEmpty = reader.readValue(ZONE_ID_EMPTY_PROPERTY);
        assertNull(fromNull.get("zoneId"));
        assertNull(fromEmpty.get("zoneId"));
    }

    // Provenance: ZoneIdDeserTest#testPolymorphicZoneIdConcreteSubtypeDeser.
    void testPolymorphicZoneIdConcreteSubtypeDeserVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addMixIn(ZoneId.class, ZoneIdTypeInfo.class)
                .build();
        ZoneId value = mapper.readValue(TYPED_ZONE_REGION, ZoneId.class);

        assertEquals(ZoneId.of("America/Denver"), value);
        assertEquals("java.time.ZoneRegion", value.getClass().getName());
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY,
            property = "@class")
    private interface ZoneIdTypeInfo { }

    void __invoke_testZoneIdDeserialization01Vpack() throws Exception {
        try {
            testZoneIdDeserialization01Vpack();
        } finally {
        }
    }


    void __invoke_testZoneIdDeserialization02Vpack() throws Exception {
        try {
            testZoneIdDeserialization02Vpack();
        } finally {
        }
    }


    void __invoke_testZoneIdDeserializationWithTypeInfo02Vpack() throws Exception {
        try {
            testZoneIdDeserializationWithTypeInfo02Vpack();
        } finally {
        }
    }


    void __invoke_testZoneIdLenientDeserializeFromEmptyStringVpack() throws Exception {
        try {
            testZoneIdLenientDeserializeFromEmptyStringVpack();
        } finally {
        }
    }


    void __invoke_testPolymorphicZoneIdConcreteSubtypeDeserVpack() throws Exception {
        try {
            testPolymorphicZoneIdConcreteSubtypeDeserVpack();
        } finally {
        }
    }

}
