package tools.jackson.databind.util;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.util.JSONPObject;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0626F1 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();

    // Provenance: JSONPObjectTest#testU2028Escaped(); text escaping is not a VPack wire property.
    void vpackStoresU2028AsUtf8() throws Exception {
        String value = "This string contains \u2028 char";
        byte[] fixture = VPackWireFixtureTest.hex(
                "5d 54 68 69 73 20 73 74 72 69 6e 67 20 63 6f 6e 74 61 69 6e 73 20 e2 80 a8 20 63 68 61 72");
        assertArrayEquals(fixture, MAPPER.writeValueAsBytes(value));
        assertEquals(value, MAPPER.readValue(fixture, String.class));
    }

    // Provenance: JSONPObjectTest#testU2029Escaped(); text escaping is not a VPack wire property.
    void vpackStoresU2029AsUtf8() throws Exception {
        String value = "This string contains \u2029 char";
        byte[] fixture = VPackWireFixtureTest.hex(
                "5d 54 68 69 73 20 73 74 72 69 6e 67 20 63 6f 6e 74 61 69 6e 73 20 e2 80 a9 20 63 68 61 72");
        assertArrayEquals(fixture, MAPPER.writeValueAsBytes(value));
        assertEquals(value, MAPPER.readValue(fixture, String.class));
    }

    // Provenance: JSONPObjectTest#testU2030NotEscaped(); text escaping is not a VPack wire property.
    void vpackStoresU2030AsUtf8() throws Exception {
        String value = "This string contains \u2030 char";
        byte[] fixture = VPackWireFixtureTest.hex(
                "5d 54 68 69 73 20 73 74 72 69 6e 67 20 63 6f 6e 74 61 69 6e 73 20 e2 80 b0 20 63 68 61 72");
        assertArrayEquals(fixture, MAPPER.writeValueAsBytes(value));
        assertEquals(value, MAPPER.readValue(fixture, String.class));
    }

    // Provenance: JSONPObjectTest#testU2028Escaped(), testU2029Escaped(), testU2030NotEscaped().
    // JSONP emits raw JSON text, a capability not represented by the VPack data model.
    void jsonpRawTextOutputIsUnsupportedByVpack() {
        JSONPObject jsonp = new JSONPObject("callback", "value");
        assertThrows(UnsupportedOperationException.class,
                () -> MAPPER.writeValueAsBytes(jsonp));
    }

    void __invoke_vpackStoresU2028AsUtf8() throws Exception {
        try {
            vpackStoresU2028AsUtf8();
        } finally {
        }
    }


    void __invoke_vpackStoresU2029AsUtf8() throws Exception {
        try {
            vpackStoresU2029AsUtf8();
        } finally {
        }
    }


    void __invoke_vpackStoresU2030AsUtf8() throws Exception {
        try {
            vpackStoresU2030AsUtf8();
        } finally {
        }
    }


    void __invoke_jsonpRawTextOutputIsUnsupportedByVpack() throws Exception {
        try {
            jsonpRawTextOutputIsUnsupportedByVpack();
        } finally {
        }
    }

}
