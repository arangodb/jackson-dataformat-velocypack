package tools.jackson.databind.ext.xml;

import java.nio.charset.StandardCharsets;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import tools.jackson.core.StreamReadConstraints;
import tools.jackson.databind.DatabindContext;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0382F0 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] SIMPLE_XML = VPackWireFixtureTest.hex(
            "81 3c 72 6f 6f 74 20 61 74 74 72 3d 22 33 22 3e 3c 6c 65 61 66 3e 52 6f 63 6b 20 26 61 6d 70 3b 20 52 6f 6c 6c 21 3c 2f 6c 65 61 66 3e 3c 3f 70 72 6f 63 20 69 6e 73 74 72 3f 3e 3c 2f 72 6f 6f 74 3e");
private static final byte[] SIMPLE_XML_DEFAULT_NS = VPackWireFixtureTest.hex(
            "5a 3c 72 6f 6f 74 20 78 6d 6c 6e 73 3d 22 68 74 74 70 3a 2f 2f 66 6f 6f 22 2f 3e");
private static final byte[] SIMPLE_XML_NS = VPackWireFixtureTest.hex(
            "6c 3c 72 6f 6f 74 20 6e 73 3a 61 74 74 72 3d 22 61 62 63 22 20 78 6d 6c 6e 73 3a 6e 73 3d 22 68 74 74 70 3a 2f 2f 66 6f 6f 22 20 2f 3e");
private static final byte[] QNAME_VALUE = VPackWireFixtureTest.hex(
            "4f 7b 68 74 74 70 3a 2f 2f 61 62 63 7d 74 61 67");
private static final byte[] EMPTY_STRING = VPackWireFixtureTest.hex("40");
private static final byte[] QNAME_OBJECT = VPackWireFixtureTest.hex(
            "14 37 4c 6e 61 6d 65 73 70 61 63 65 55 52 49 4a 68 74 74 70 3a 2f 2f 61 62 63 49 6c 6f 63 61 6c 50 61 72 74 43 74 61 67 46 70 72 65 66 69 78 46 70 72 65 66 69 78 03");
private static final byte[] QNAME_BAD_NUMBER = VPackWireFixtureTest.hex(
            "14 0f 49 6c 6f 63 61 6c 50 61 72 74 28 7b 01");
private static final byte[] DURATION = VPackWireFixtureTest.hex(
            "4e 2d 50 31 35 44 54 31 39 48 35 38 4d 31 53");
private static final byte[] DURATION_DESER = VPackWireFixtureTest.hex(
            "4d 50 32 37 44 54 35 48 31 35 4d 35 39 53");

    // Provenance: DOMTypeReadWriteTest#testSerializeSimpleNonNS().
    void testSerializeSimpleNonNSVpack() throws Exception {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(new InputSource(new java.io.StringReader(
                        "<root attr='3'><leaf>Rock &amp; Roll!</leaf><?proc instr?></root>")));
        assertArrayEquals(SIMPLE_XML, MAPPER.writeValueAsBytes(doc));
    }

    // Provenance: DOMTypeReadWriteTest#testSerializeSimpleDefaultNS().
    void testSerializeSimpleDefaultNSVpack() throws Exception {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(new InputSource(new java.io.StringReader(
                        "<root xmlns='http://foo'/>")));
        assertArrayEquals(SIMPLE_XML_DEFAULT_NS, MAPPER.writeValueAsBytes(doc));
    }

    // Provenance: DOMTypeReadWriteTest#testDeserializeNonNS().
    void testDeserializeNonNSVpack() throws Exception {
        for (int i = 0; i < 2; ++i) {
            Document doc;
            if (i == 0) {
                doc = MAPPER.readValue(SIMPLE_XML, Document.class);
            } else {
                Node node = MAPPER.readValue(SIMPLE_XML, Node.class);
                doc = (Document) node;
            }
            Element root = doc.getDocumentElement();
            assertNotNull(root);
            assertEquals("root", root.getTagName());
            assertEquals("3", root.getAttribute("attr"));
            assertEquals(1, root.getAttributes().getLength());
            NodeList nodes = root.getChildNodes();
            assertEquals(2, nodes.getLength());
            Element leaf = (Element) nodes.item(0);
            assertEquals("leaf", leaf.getTagName());
            assertEquals(0, leaf.getAttributes().getLength());
            org.w3c.dom.ProcessingInstruction pi =
                    (org.w3c.dom.ProcessingInstruction) nodes.item(1);
            assertEquals("proc", pi.getTarget());
            assertEquals("instr", pi.getData());
        }
    }

    // Provenance: DOMTypeReadWriteTest#testDeserializeNS().
    void testDeserializeNSVpack() throws Exception {
        Document doc = MAPPER.readValue(SIMPLE_XML_NS, Document.class);
        Element root = doc.getDocumentElement();
        assertNotNull(root);
        assertEquals("root", root.getTagName());
        String uri = root.getNamespaceURI();
        assertTrue(uri == null || uri.isEmpty());
        assertEquals(0, root.getChildNodes().getLength());
        assertEquals(2, root.getAttributes().getLength());
        assertEquals("abc", root.getAttributeNS("http://foo", "attr"));
    }
private static ObjectMapper mapperWithNumberLength(int maxNumberLength) {
        return VPackMapper.builder(VPackFactory.builder()
                .streamReadConstraints(StreamReadConstraints.builder()
                        .maxNumberLength(maxNumberLength).build())
                .build()).build();
    }
private static byte[] literalUtf8Fixture(String value) {
        byte[] text = value.getBytes(StandardCharsets.UTF_8);
        if (text.length <= 126) {
            byte[] result = new byte[text.length + 1];
            result[0] = (byte) (0x40 + text.length);
            System.arraycopy(text, 0, result, 1, text.length);
            return result;
        }
        byte[] result = new byte[text.length + 9];
        result[0] = (byte) 0xbf;
        long length = text.length;
        for (int i = 0; i < 8; ++i) {
            result[1 + i] = (byte) (length >>> (8 * i));
        }
        System.arraycopy(text, 0, result, 9, text.length);
        return result;
    }
static class NoCheckSubTypeValidator extends PolymorphicTypeValidator.Base {
        private static final long serialVersionUID = 1L;

        @Override
        public Validity validateBaseType(DatabindContext ctxt,
                tools.jackson.databind.JavaType baseType) {
            return Validity.ALLOWED;
        }
    }

    void __invoke_testSerializeSimpleNonNSVpack() throws Exception {
        try {
            testSerializeSimpleNonNSVpack();
        } finally {
        }
    }


    void __invoke_testSerializeSimpleDefaultNSVpack() throws Exception {
        try {
            testSerializeSimpleDefaultNSVpack();
        } finally {
        }
    }


    void __invoke_testDeserializeNonNSVpack() throws Exception {
        try {
            testDeserializeNonNSVpack();
        } finally {
        }
    }


    void __invoke_testDeserializeNSVpack() throws Exception {
        try {
            testDeserializeNSVpack();
        } finally {
        }
    }

}
