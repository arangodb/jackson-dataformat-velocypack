package tools.jackson.databind.ext.xml;

import javax.xml.namespace.QName;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.databind.ext.xml.QNameAsObjectReadWrite4771Test}
 */
class QNameAsObjectReadWrite4771Test {
/**
 * VPack adaptation of {@link tools.jackson.databind.ext.xml.QNameAsObjectReadWrite4771Test}.
 * Original test methods: {@link tools.jackson.databind.ext.xml.QNameAsObjectReadWrite4771Test#testQNameWithObjectSerialization(QName)}.
 */
    @ParameterizedTest(name = "testQNameWithObjectSerializationVpack[{index}]")
    @MethodSource("tools.jackson.databind.ext.xml.T32_0383F1#provideAllPerumtationsOfQNameConstructor")
    // Provenance: QNameAsObjectReadWrite4771Test#testQNameWithObjectSerialization(QName).
    void testQNameWithObjectSerializationVpack(QName originalQName) throws Exception {
            new T32_0383F1().__invoke_testQNameWithObjectSerializationVpack(originalQName);
        }
}
