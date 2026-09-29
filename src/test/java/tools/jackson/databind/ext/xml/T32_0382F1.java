package tools.jackson.databind.ext.xml;

import java.nio.charset.StandardCharsets;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import tools.jackson.core.StreamReadConstraints;
import tools.jackson.core.exc.StreamConstraintsException;
import tools.jackson.databind.DatabindContext;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;
import tools.jackson.databind.exc.MismatchedInputException;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0382F1 {
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

    // Provenance: MiscJavaXMLTypesReadWriteTest#testDurationSer().
    void testDurationSerVpack() throws Exception {
        Duration duration = DatatypeFactory.newInstance()
                .newDurationDayTime(false, 15, 19, 58, 1);
        assertArrayEquals(DURATION, MAPPER.writeValueAsBytes(duration));
    }

    // Provenance: MiscJavaXMLTypesReadWriteTest#testDurationDeser().
    void testDurationDeserVpack() throws Exception {
        Duration expected = DatatypeFactory.newInstance()
                .newDurationDayTime(true, 27, 5, 15, 59);
        assertEquals(expected, MAPPER.readValue(DURATION_DESER, Duration.class));
    }

    // Provenance: MiscJavaXMLTypesReadWriteTest#testQNameDeser().
    void testQNameDeserVpack() throws Exception {
        QName expected = new QName("http://abc", "tag", "prefix");
        assertEquals(expected, MAPPER.readValue(QNAME_VALUE, QName.class));
        QName empty = MAPPER.readValue(EMPTY_STRING, QName.class);
        assertNotNull(empty);
        assertEquals("", empty.getLocalPart());
    }

    // Provenance: MiscJavaXMLTypesReadWriteTest#testQNameDeserFromObject().
    void testQNameDeserFromObjectVpack() throws Exception {
        QName result = MAPPER.readValue(QNAME_OBJECT, QName.class);
        assertEquals("http://abc", result.getNamespaceURI());
        assertEquals("tag", result.getLocalPart());
        assertEquals("prefix", result.getPrefix());
    }

    // Provenance: MiscJavaXMLTypesReadWriteTest#testQNameDeserFail().
    void testQNameDeserFailVpack() throws Exception {
        MismatchedInputException missing = assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue(VPackWireFixtureTest.hex("0a"), QName.class));
        assertTrue(missing.getMessage().contains("localPart"));

        MismatchedInputException wrongType = assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue(QNAME_BAD_NUMBER, QName.class));
        assertTrue(wrongType.getMessage().contains("localPart"));
    }

    // Provenance: MiscJavaXMLTypesReadWriteTest#testPolymorphicXMLGregorianCalendar().
    void testPolymorphicXMLGregorianCalendarVpack() throws Exception {
        XMLGregorianCalendar expected = DatatypeFactory.newInstance()
                .newXMLGregorianCalendar(1974, 10, 10, 18, 15, 17, 123, 0);
        ObjectMapper mapper = VPackMapper.builder()
                .activateDefaultTyping(new NoCheckSubTypeValidator(), DefaultTyping.NON_FINAL)
                .build();
        byte[] encoded = mapper.writeValueAsBytes(expected);
        Object result = mapper.readValue(encoded, Object.class);
        assertInstanceOf(XMLGregorianCalendar.class, result);
        assertEquals(expected, result);
    }

    // Provenance: MiscJavaXMLTypesReadWriteTest#testDurationNumberLengthConstraint().
    void testDurationNumberLengthConstraintVpack() throws Exception {
        ObjectMapper constrained = mapperWithNumberLength(100);
        byte[] oversized = literalUtf8Fixture("P" + "9".repeat(120) + "Y");
        StreamConstraintsException error = assertThrows(StreamConstraintsException.class,
                () -> constrained.readValue(oversized, Duration.class));
        assertTrue(error.getMessage().contains("exceeds the maximum allowed"));

        Duration normal = constrained.readValue(literalUtf8Fixture("P1Y2M3D"), Duration.class);
        assertEquals(1, normal.getYears());
        assertEquals(2, normal.getMonths());
        assertEquals(3, normal.getDays());
    }

    // Provenance: MiscJavaXMLTypesReadWriteTest#testDefaultNumberLengthConstraints().
    void testDefaultNumberLengthConstraintsVpack() throws Exception {
        int length = StreamReadConstraints.DEFAULT_MAX_NUM_LEN + 100;
        byte[] duration = literalUtf8Fixture("P" + "9".repeat(length) + "Y");
        byte[] calendar = literalUtf8Fixture("00:00:00." + "9".repeat(length));

        StreamConstraintsException durationError = assertThrows(StreamConstraintsException.class,
                () -> MAPPER.readValue(duration, Duration.class));
        assertTrue(durationError.getMessage().contains("exceeds the maximum allowed"));
        StreamConstraintsException calendarError = assertThrows(StreamConstraintsException.class,
                () -> MAPPER.readValue(calendar, XMLGregorianCalendar.class));
        assertTrue(calendarError.getMessage().contains("exceeds the maximum allowed"));
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

    void __invoke_testDurationSerVpack() throws Exception {
        try {
            testDurationSerVpack();
        } finally {
        }
    }


    void __invoke_testDurationDeserVpack() throws Exception {
        try {
            testDurationDeserVpack();
        } finally {
        }
    }


    void __invoke_testQNameDeserVpack() throws Exception {
        try {
            testQNameDeserVpack();
        } finally {
        }
    }


    void __invoke_testQNameDeserFromObjectVpack() throws Exception {
        try {
            testQNameDeserFromObjectVpack();
        } finally {
        }
    }


    void __invoke_testQNameDeserFailVpack() throws Exception {
        try {
            testQNameDeserFailVpack();
        } finally {
        }
    }


    void __invoke_testPolymorphicXMLGregorianCalendarVpack() throws Exception {
        try {
            testPolymorphicXMLGregorianCalendarVpack();
        } finally {
        }
    }


    void __invoke_testDurationNumberLengthConstraintVpack() throws Exception {
        try {
            testDurationNumberLengthConstraintVpack();
        } finally {
        }
    }


    void __invoke_testDefaultNumberLengthConstraintsVpack() throws Exception {
        try {
            testDefaultNumberLengthConstraintsVpack();
        } finally {
        }
    }

}
