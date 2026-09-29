package tools.jackson.core.unittest.read.loc;

import org.junit.jupiter.api.Test;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.core.unittest.read.loc.LocationOffsetsTest}
 */
class LocationOffsetsTest {
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.loc.LocationOffsetsTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.loc.LocationOffsetsTest#simpleInitialOffsets()}.
 */
    @Test
    void simpleInitialOffsetsForEmptyObject() throws Exception {
            new T32_0096Fixture().__invoke_simpleInitialOffsetsForEmptyObject();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.loc.LocationOffsetsTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.loc.LocationOffsetsTest#bigPayload()}, {@link tools.jackson.core.unittest.read.loc.LocationOffsetsTest#withLazyStringReadDataInput()}, {@link tools.jackson.core.unittest.read.loc.LocationOffsetsTest#withLazyStringReadStreaming()}.
 */
    @Test
    void largeLiteralStringKeepsByteLocations() throws Exception {
            new T32_0096Fixture().__invoke_largeLiteralStringKeepsByteLocations();
        }
}
