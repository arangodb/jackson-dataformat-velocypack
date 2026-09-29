package tools.jackson.core.unittest.read;

import org.junit.jupiter.api.Test;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.core.unittest.read.DataInputTest}
 */
class DataInputTest {
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.DataInputTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.DataInputTest#eofAfterArray()}.
 */
    @Test
    void eofAfterArray() throws Exception {
            new T32_0066Fixture().__invoke_eofAfterArray();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.DataInputTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.DataInputTest#eofAfterObject()}.
 */
    @Test
    void eofAfterObject() throws Exception {
            new T32_0066Fixture().__invoke_eofAfterObject();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.DataInputTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.DataInputTest#eofAfterScalar()}.
 */
    @Test
    void eofAfterScalar() throws Exception {
            new T32_0066Fixture().__invoke_eofAfterScalar();
        }
}
