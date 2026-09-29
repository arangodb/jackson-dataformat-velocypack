package tools.jackson.databind.node;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonPointer;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.exc.JsonNodeException;
import tools.jackson.databind.node.ObjectNode;
import tools.jackson.databind.node.POJONode;
import tools.jackson.databind.ser.std.StdSerializer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import tools.jackson.dataformat.velocypack.*;

class T32_0502F1 {
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] REQUIRED_OBJECT = VPackWireFixtureTest.hex(
            "14 66 "
            + "44 64 61 74 61 "
            + "14 26 "
            + "47 70 72 69 6d 61 72 79 28 0f "
            + "46 76 65 63 74 6f 72 13 08 43 79 65 73 19 02 "
            + "48 6e 75 6c 6c 61 62 6c 65 18 03 "
            + "45 61 72 72 61 79 13 32 "
            + "1a "
            + "14 24 "
            + "48 6d 65 73 73 73 61 67 65 45 68 65 6c 6c 6f "
            + "45 76 61 6c 75 65 28 2a "
            + "44 6d 69 73 63 13 05 31 32 02 03 "
            + "18 "
            + "1b 00 00 00 00 00 00 d0 3f "
            + "04 02");
private static final byte[] REQUIRED_ARRAY = VPackWireFixtureTest.hex(
            "13 36 "
            + "1a "
            + "14 24 "
            + "44 64 61 74 61 "
            + "14 1c "
            + "47 70 72 69 6d 61 72 79 28 0f "
            + "46 76 65 63 74 6f 72 13 08 43 79 65 73 19 02 02 01 "
            + "1b 00 00 00 00 00 00 d0 3f "
            + "44 6c 61 73 74 "
            + "04");
private static final byte[] CUSTOM_SERIALIZER_RESULT = VPackWireFixtureTest.hex(
            "0b 27 01 44 64 61 74 61 "
            + "0b 1e 01 44 61 53 74 72 "
            + "54 54 68 65 20 76 61 6c 75 65 20 69 73 3a 20 48 65 6c 6c 6f 21 03 "
            + "03");
private final ObjectMapper mapper = new VPackMapper();
private POJONode pojo(Object value) throws Exception {
        ObjectNode root = (ObjectNode) mapper.readTree(EMPTY_OBJECT);
        root.putPOJO("value", value);
        return (POJONode) root.get("value");
    }

    // Provenance: RequiredAccessorTest#testIMPORTANT().
    void testIMPORTANTVpack() throws Exception {
        assertRequiredAtFailure(mapper.readTree(REQUIRED_OBJECT),
                "/data/weird/and/more", "/weird/and/more");
    }

    // Provenance: RequiredAccessorTest#testRequiredAtArrayOk().
    void testRequiredAtArrayOkVpack() throws Exception {
        JsonNode array = mapper.readTree(REQUIRED_ARRAY);
        assertTrue(array.requiredAt("/0").isBoolean());
        assertTrue(array.requiredAt("/1").isObject());
        assertNotNull(array.requiredAt("/1/data/primary"));
        assertNotNull(array.requiredAt("/1/data/vector/1"));
    }

    // Provenance: RequiredAccessorTest#testRequiredAtFailOnArray().
    void testRequiredAtFailOnArrayVpack() throws Exception {
        JsonNode array = mapper.readTree(REQUIRED_ARRAY);
        assertRequiredAtFailure(array, "/1/data/vector/25", "/25");
        assertRequiredAtFailure(array, "/0/data/x", "/data/x");
    }

    // Provenance: RequiredAccessorTest#testRequiredAtFailOnObjectBasic().
    void testRequiredAtFailOnObjectBasicVpack() throws Exception {
        JsonNode object = mapper.readTree(REQUIRED_OBJECT);
        assertRequiredAtFailure(object, "/0", "/0");
        assertRequiredAtFailure(object, "/bogus", "/bogus");
        assertRequiredAtFailure(object, "/data/weird/and/more", "/weird/and/more");
        assertRequiredAtFailure(object, "/data/vector/other/3", "/other/3");
        assertRequiredAtFailure(object, "/data/primary/more", "/more");
    }

    // Provenance: RequiredAccessorTest#testRequiredAtFailOnObjectScalar3005().
    void testRequiredAtFailOnObjectScalar3005Vpack() throws Exception {
        JsonNode object = mapper.readTree(VPackWireFixtureTest.hex(
                "14 0b 46 73 69 6d 70 6c 65 35 01"));
        assertRequiredAtFailure(object, "/simple/property", "/property");
    }

    // Provenance: RequiredAccessorTest#testRequiredAtObjectOk().
    void testRequiredAtObjectOkVpack() throws Exception {
        JsonNode object = mapper.readTree(REQUIRED_OBJECT);
        assertNotNull(object.requiredAt("/array"));
        assertNotNull(object.requiredAt("/array/0"));
        assertTrue(object.requiredAt("/array/0").isBoolean());
        assertNotNull(object.requiredAt("/array/1/misc/1"));
        assertEquals(2, object.requiredAt("/array/1/misc/1").intValue());
    }

    // Provenance: RequiredAccessorTest#testSimpleRequireAtFailure().
    void testSimpleRequireAtFailureVpack() throws Exception {
        JsonNode object = mapper.readTree(REQUIRED_OBJECT);
        JsonNode array = mapper.readTree(REQUIRED_ARRAY);
        assertThrows(JsonNodeException.class, () -> object.requiredAt("/some-random-path"));
        assertThrows(JsonNodeException.class, () -> array.requiredAt("/some-random-path"));
        assertThrows(JsonNodeException.class,
                () -> object.requiredAt(JsonPointer.compile("/some-random-path")));
        assertThrows(JsonNodeException.class,
                () -> array.requiredAt(JsonPointer.compile("/some-random-path")));
    }

    // Provenance: RequiredAccessorTest#testSimpleRequireFail().
    void testSimpleRequireFailVpack() throws Exception {
        JsonNode object = mapper.readTree(REQUIRED_OBJECT);
        JsonNode array = mapper.readTree(REQUIRED_ARRAY);
        assertThrows(JsonNodeException.class, () -> object.required("bogus"));
        assertThrows(JsonNodeException.class, () -> array.required("bogus"));
        assertThrows(JsonNodeException.class, () -> object.required(-1));
        assertThrows(JsonNodeException.class, () -> array.required(-1));
    }

    // Provenance: RequiredAccessorTest#testSimpleRequireOk().
    void testSimpleRequireOkVpack() throws Exception {
        JsonNode object = mapper.readTree(REQUIRED_OBJECT);
        JsonNode array = mapper.readTree(REQUIRED_ARRAY);
        assertSame(object, object.require());
        assertSame(object, object.requireNonNull());
        assertSame(object, object.requiredAt(""));
        assertSame(object, object.requiredAt(JsonPointer.compile("")));
        assertSame(object.get("data"), object.required("data"));
        assertSame(array.get(0), array.required(0));
        assertSame(array.get(3), array.required(3));
        object.path("data").path("nullable").require();
        assertThrows(DatabindException.class,
                () -> object.path("data").path("nullable").requireNonNull());
    }
private static void assertRequiredAtFailure(JsonNode document, String fullPath,
            String mismatchPart) {
        try {
            JsonNode node = document.requiredAt(fullPath);
            fail("Should NOT pass: got node (" + node.getClass().getSimpleName() + ") -> {"
                    + node + "}");
        } catch (DatabindException e) {
            assertTrue(e.getMessage().contains("No node at '" + fullPath
                    + "' (unmatched part: '" + mismatchPart + "')"));
        }
    }
@JsonSerialize(using = CustomSer.class)
    public static class Data {
        public String aStr;
    }
public static class CustomSer extends StdSerializer<Data> {
        public CustomSer() {
            super(Data.class);
        }

        @Override
        public void serialize(Data value, JsonGenerator generator, SerializationContext provider) {
            String attrStr = (String) provider.getAttribute("myAttr");
            generator.writeStartObject();
            generator.writeStringProperty("aStr", "The value is: "
                    + (attrStr == null ? "NULL" : attrStr));
            generator.writeEndObject();
        }
    }

    void __invoke_testIMPORTANTVpack() throws Exception {
        try {
            testIMPORTANTVpack();
        } finally {
        }
    }


    void __invoke_testRequiredAtArrayOkVpack() throws Exception {
        try {
            testRequiredAtArrayOkVpack();
        } finally {
        }
    }


    void __invoke_testRequiredAtFailOnArrayVpack() throws Exception {
        try {
            testRequiredAtFailOnArrayVpack();
        } finally {
        }
    }


    void __invoke_testRequiredAtFailOnObjectBasicVpack() throws Exception {
        try {
            testRequiredAtFailOnObjectBasicVpack();
        } finally {
        }
    }


    void __invoke_testRequiredAtFailOnObjectScalar3005Vpack() throws Exception {
        try {
            testRequiredAtFailOnObjectScalar3005Vpack();
        } finally {
        }
    }


    void __invoke_testRequiredAtObjectOkVpack() throws Exception {
        try {
            testRequiredAtObjectOkVpack();
        } finally {
        }
    }


    void __invoke_testSimpleRequireAtFailureVpack() throws Exception {
        try {
            testSimpleRequireAtFailureVpack();
        } finally {
        }
    }


    void __invoke_testSimpleRequireFailVpack() throws Exception {
        try {
            testSimpleRequireFailVpack();
        } finally {
        }
    }


    void __invoke_testSimpleRequireOkVpack() throws Exception {
        try {
            testSimpleRequireOkVpack();
        } finally {
        }
    }

}
