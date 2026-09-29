package tools.jackson.databind.node;

import java.io.ByteArrayInputStream;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0504F2 {
private static final byte[] STRING_FIELD = VPackWireFixtureTest.hex(
            "14 1f 45 66 69 65 6c 64 55 7b 22 6e 61 6d 65 22 3a 22 4a 6f 68 6e 20 53 6d 69 74 68 22 7d 01");
private static final byte[] INCOMPLETE_OBJECT = VPackWireFixtureTest.hex("14");
private static final byte[] EOF_ROOT = VPackWireFixtureTest.hex(
            "14 1c 43 6b 65 79 31 44 6e 61 6d 65 43 78 79 7a 44 74 79 70 65 31 43 75 72 6c 18 04");
private static final byte[] MIXED_ROOT = VPackWireFixtureTest.hex(
            "14 11 44 6e 6f 64 65 14 06 41 61 33 01 41 78 39 02");
private static final byte[] MULTIPLE_ROOTS = VPackWireFixtureTest.hex(
            "28 0c 46 73 74 72 69 6e 67 02 05 31 32 33");
private static final byte[] NULL_ROOT = VPackWireFixtureTest.hex("18");
private static final byte[] SAMPLE_ROOT = VPackWireFixtureTest.hex(
            "14 98 01 45 49 6d 61 67 65 14 8e 01 "
          + "45 57 69 64 74 68 29 20 03 "
          + "46 48 65 69 67 68 74 29 58 02 "
          + "45 54 69 74 6c 65 54 56 69 65 77 20 66 72 6f 6d 20 31 35 74 68 20 46 6c 6f 6f 72 "
          + "49 54 68 75 6d 62 6e 61 69 6c 14 41 "
          + "43 55 72 6c 66 68 74 74 70 3a 2f 2f 77 77 77 2e 65 78 61 6d 70 6c 65 2e 63 6f 6d 2f 69 6d 61 67 65 2f 34 38 31 39 38 39 39 34 33 "
          + "46 48 65 69 67 68 74 28 7d 45 57 69 64 74 68 43 31 30 30 03 "
          + "43 49 44 73 13 0d 28 74 29 af 03 28 ea 29 89 97 04 05 01");
private final VPackMapper mapper = new VPackMapper();

    // Provenance: TreeReadViaMapperTest#testEOF().
    void testEOFVpack() throws Exception {
        try (JsonParser parser = mapper.createParser(new ByteArrayInputStream(EOF_ROOT))) {
            JsonNode result = mapper.readTree(parser);
            assertTrue(result.isObject());
            assertEquals(4, result.size());
            assertEquals(1, result.path("key").intValue());
            assertEquals("xyz", result.path("name").asString());
            assertEquals(1, result.path("type").intValue());
            assertTrue(result.path("url").isNull());
            assertNull(mapper.readTree(parser));
        }
    }

    // Provenance: TreeReadViaMapperTest#testMixed().
    void testMixedVpack() throws Exception {
        MixedBean bean = mapper.readValue(MIXED_ROOT, MixedBean.class);

        assertEquals(9, bean.x);
        assertNotNull(bean.node);
        assertEquals(1, bean.node.size());
        assertEquals(3, bean.node.path("a").intValue());
    }

    // Provenance: TreeReadViaMapperTest#testMultiple().
    void testMultipleVpack() throws Exception {
        ObjectMapper multiMapper = VPackMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
                .build();
        try (JsonParser parser = multiMapper.createParser(MULTIPLE_ROOTS)) {
            JsonNode result = multiMapper.readTree(parser);
            assertTrue(result.isIntegralNumber());
            assertTrue(result.isInt());
            assertFalse(result.isString());
            assertEquals(12, result.intValue());

            result = multiMapper.readTree(parser);
            assertTrue(result.isString());
            assertFalse(result.isIntegralNumber());
            assertFalse(result.isInt());
            assertEquals("string", result.stringValue());

            result = multiMapper.readTree(parser);
            assertTrue(result.isArray());
            assertEquals(3, result.size());
            assertNull(multiMapper.readTree(parser));
        }
    }

    // Provenance: TreeReadViaMapperTest#testNullViaParser().
    void testNullViaParserVpack() throws Exception {
        try (JsonParser parser = mapper.createParser(new ByteArrayInputStream(NULL_ROOT))) {
            JsonNode result = mapper.readTree(parser);
            assertTrue(result.isNull());
        }
    }

    // Provenance: TreeReadViaMapperTest#testSimple().
    void testSimpleVpack() throws Exception {
        JsonNode result = mapper.readTree(SAMPLE_ROOT);
        assertTrue(result.isObject());
        assertEquals(1, result.size());

        ObjectNode image = (ObjectNode) result.path("Image");
        assertEquals(5, image.size());
        assertEquals(800, image.path("Width").intValue());
        assertEquals(600, image.path("Height").intValue());
        assertEquals("View from 15th Floor", image.path("Title").stringValue());

        ObjectNode thumbnail = (ObjectNode) image.path("Thumbnail");
        assertEquals("http://www.example.com/image/481989943", thumbnail.path("Url").stringValue());
        assertEquals(125, thumbnail.path("Height").intValue());
        assertEquals("100", thumbnail.path("Width").stringValue());

        JsonNode ids = image.path("IDs");
        assertTrue(ids.isArray());
        assertEquals(4, ids.size());
        assertEquals(116, ids.get(0).intValue());
        assertEquals(943, ids.get(1).intValue());
        assertEquals(234, ids.get(2).intValue());
        assertEquals(38793, ids.get(3).intValue());
    }
public static class MixedBean {
        public int x;
        public JsonNode node;
    }

    void __invoke_testEOFVpack() throws Exception {
        try {
            testEOFVpack();
        } finally {
        }
    }


    void __invoke_testMixedVpack() throws Exception {
        try {
            testMixedVpack();
        } finally {
        }
    }


    void __invoke_testMultipleVpack() throws Exception {
        try {
            testMultipleVpack();
        } finally {
        }
    }


    void __invoke_testNullViaParserVpack() throws Exception {
        try {
            testNullViaParserVpack();
        } finally {
        }
    }


    void __invoke_testSimpleVpack() throws Exception {
        try {
            testSimpleVpack();
        } finally {
        }
    }

}
