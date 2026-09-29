package tools.jackson.core.unittest.read;

import org.junit.jupiter.api.Test;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.core.unittest.read.UTF8NamesParseTest}
 */
class UTF8NamesParseTest {
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.UTF8NamesParseTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.UTF8NamesParseTest#emptyName()}.
 */
    @Test
    void emptyFieldNameAndValueAreRetainedAcrossBinarySources() throws Exception {
            new T32_0092Fixture().__invoke_emptyFieldNameAndValueAreRetainedAcrossBinarySources();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.UTF8NamesParseTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.UTF8NamesParseTest#utf8Name2Bytes()}.
 */
    @Test
    void twoByteUtf8FieldNamesAreDecodedExactly() throws Exception {
            new T32_0092Fixture().__invoke_twoByteUtf8FieldNamesAreDecodedExactly();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.UTF8NamesParseTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.UTF8NamesParseTest#utf8Name3Bytes()}.
 */
    @Test
    void threeByteUtf8FieldNamesAreDecodedExactly() throws Exception {
            new T32_0092Fixture().__invoke_threeByteUtf8FieldNamesAreDecodedExactly();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.UTF8NamesParseTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.UTF8NamesParseTest#utf8StringTrivial()}.
 */
    @Test
    void utf8StringTrivial() throws Exception {
            new T32_0093F0().__invoke_utf8StringTrivial();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.UTF8NamesParseTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.UTF8NamesParseTest#utf8StringValue()}.
 */
    @Test
    void utf8StringValue() throws Exception {
            new T32_0093F0().__invoke_utf8StringValue();
        }
}
