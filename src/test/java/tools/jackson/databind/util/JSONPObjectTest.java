package tools.jackson.databind.util;

import org.junit.jupiter.api.Test;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.databind.util.JSONPObjectTest}
 */
class JSONPObjectTest {
/**
 * VPack adaptation of {@link tools.jackson.databind.util.JSONPObjectTest}.
 * Original test methods: {@link tools.jackson.databind.util.JSONPObjectTest#testU2028Escaped()}.
 */
    @Test
    // Provenance: JSONPObjectTest#testU2028Escaped(); text escaping is not a VPack wire property.
    void vpackStoresU2028AsUtf8() throws Exception {
            new T32_0626F1().__invoke_vpackStoresU2028AsUtf8();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.util.JSONPObjectTest}.
 * Original test methods: {@link tools.jackson.databind.util.JSONPObjectTest#testU2029Escaped()}.
 */
    @Test
    // Provenance: JSONPObjectTest#testU2029Escaped(); text escaping is not a VPack wire property.
    void vpackStoresU2029AsUtf8() throws Exception {
            new T32_0626F1().__invoke_vpackStoresU2029AsUtf8();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.util.JSONPObjectTest}.
 * Original test methods: {@link tools.jackson.databind.util.JSONPObjectTest#testU2030NotEscaped()}.
 */
    @Test
    // Provenance: JSONPObjectTest#testU2030NotEscaped(); text escaping is not a VPack wire property.
    void vpackStoresU2030AsUtf8() throws Exception {
            new T32_0626F1().__invoke_vpackStoresU2030AsUtf8();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.util.JSONPObjectTest}.
 * Original test methods: {@link tools.jackson.databind.util.JSONPObjectTest#testU2028Escaped()}, {@link tools.jackson.databind.util.JSONPObjectTest#testU2029Escaped()}, {@link tools.jackson.databind.util.JSONPObjectTest#testU2030NotEscaped()}.
 */
    @Test
    // Provenance: JSONPObjectTest#testU2028Escaped(), testU2029Escaped(), testU2030NotEscaped().
    // JSONP emits raw JSON text, a capability not represented by the VPack data model.
    void jsonpRawTextOutputIsUnsupportedByVpack() throws Exception {
            new T32_0626F1().__invoke_jsonpRawTextOutputIsUnsupportedByVpack();
        }
}
