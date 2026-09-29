package tools.jackson.databind.ext.javatime.deser;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.cfg.CoercionAction;
import tools.jackson.databind.cfg.CoercionInputShape;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.type.LogicalType;
import tools.jackson.core.type.TypeReference;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0335F0 {
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

    // Provenance: ZoneIdDeserTest#testZoneIdDeserFromEmpty.
    void testZoneIdDeserFromEmptyVpack() throws Exception {
        assertNull(MAPPER.readValue(TWO_SPACES, ZoneId.class));

        ObjectMapper strictMapper = VPackMapper.builder()
                .withCoercionConfig(LogicalType.DateTime,
                        cfg -> cfg.setCoercion(CoercionInputShape.EmptyString,
                                CoercionAction.Fail))
                .build();
        assertThrows(MismatchedInputException.class,
                () -> strictMapper.readValue(TWO_SPACES, ZoneId.class));
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY,
            property = "@class")
    private interface ZoneIdTypeInfo { }

    void __invoke_testZoneIdDeserFromEmptyVpack() throws Exception {
        try {
            testZoneIdDeserFromEmptyVpack();
        } finally {
        }
    }

}
