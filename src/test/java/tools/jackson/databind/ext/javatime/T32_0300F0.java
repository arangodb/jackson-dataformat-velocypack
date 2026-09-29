package tools.jackson.databind.ext.javatime;

import tools.jackson.databind.cfg.DateTimeFeature;

import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0300F0 {
private static final byte[] INSTANT_FULL = VPackWireFixtureTest.hex(
            "5e 32 30 32 33 2d 30 31 2d 31 35 54 31 30 3a 33 30 3a 34 35 2e 31 32 33 34 35 36 37 38 39 5a");
private static final byte[] INSTANT_MILLIS = VPackWireFixtureTest.hex(
            "58 32 30 32 33 2d 30 31 2d 31 35 54 31 30 3a 33 30 3a 34 35 2e 31 32 33 5a");
private static final byte[] LOCAL_DATE_TIME_FULL = VPackWireFixtureTest.hex(
            "5d 32 30 32 33 2d 30 31 2d 31 35 54 31 30 3a 33 30 3a 34 35 2e 31 32 33 34 35 36 37 38 39");
private static final byte[] LOCAL_DATE_TIME_MILLIS = VPackWireFixtureTest.hex(
            "57 32 30 32 33 2d 30 31 2d 31 35 54 31 30 3a 33 30 3a 34 35 2e 31 32 33");
private static final byte[] LOCAL_TIME_FULL = VPackWireFixtureTest.hex(
            "52 31 30 3a 33 30 3a 34 35 2e 31 32 33 34 35 36 37 38 39");
private static final byte[] LOCAL_TIME_MILLIS = VPackWireFixtureTest.hex(
            "4c 31 30 3a 33 30 3a 34 35 2e 31 32 33");
private static final byte[] DURATION_FULL = VPackWireFixtureTest.hex(
            "50 50 54 32 4d 33 2e 34 35 36 37 38 39 30 31 32 53");
private static final byte[] DURATION_MILLIS = VPackWireFixtureTest.hex(
            "4a 50 54 32 4d 33 2e 34 35 36 53");
private static final VPackMapper MAPPER = new VPackMapper();
private static final VPackMapper MAPPER_TRUNCATE_WRITE = VPackMapper.builder()
            .enable(DateTimeFeature.TRUNCATE_TO_MSECS_ON_WRITE)
            .build();
private static final VPackMapper MAPPER_TRUNCATE_READ = VPackMapper.builder()
            .enable(DateTimeFeature.TRUNCATE_TO_MSECS_ON_READ)
            .build();
private static final VPackMapper MAPPER_TRUNCATE_BOTH = VPackMapper.builder()
            .enable(DateTimeFeature.TRUNCATE_TO_MSECS_ON_WRITE,
                    DateTimeFeature.TRUNCATE_TO_MSECS_ON_READ)
            .build();

    // Provenance: TestFeatures#testWriteDateTimestampsAsNanosecondsSettingEnabledByDefault.
    void testWriteDateTimestampsAsNanosecondsSettingEnabledByDefaultVpack() {
        assertTrue(DateTimeFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS.enabledByDefault(),
                "Write date timestamps as nanoseconds setting should be enabled by default.");
    }

    void __invoke_testWriteDateTimestampsAsNanosecondsSettingEnabledByDefaultVpack() throws Exception {
        try {
            testWriteDateTimestampsAsNanosecondsSettingEnabledByDefaultVpack();
        } finally {
        }
    }

}
