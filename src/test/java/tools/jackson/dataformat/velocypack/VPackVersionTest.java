package tools.jackson.dataformat.velocypack;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VPackVersionTest {
    @Test
    void generatedVersionAndMarkerAnchor() {
        assertEquals("5.0.0", PackageVersion.VERSION.toString());
        assertEquals(0x01, VPackConstants.EMPTY_ARRAY);
    }
}
