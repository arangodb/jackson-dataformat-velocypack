package tools.jackson.databind.ext.xml;

import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.junit.jupiter.params.provider.Arguments;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import tools.jackson.core.StreamReadConstraints;
import tools.jackson.core.exc.StreamConstraintsException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.DateTimeFeature;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0383F0 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] QNAME_VALUE = VPackWireFixtureTest.hex(
            "4f 7b 68 74 74 70 3a 2f 2f 61 62 63 7d 74 61 67");
private static final byte[] QNAME_OBJECT = VPackWireFixtureTest.hex(
            "0b 3a 03 "
          + "49 6c 6f 63 61 6c 50 61 72 74 43 74 61 67 "
          + "4c 6e 61 6d 65 73 70 61 63 65 55 52 49 4a 68 74 74 70 3a 2f 2f 61 62 63 "
          + "46 70 72 65 66 69 78 46 70 72 65 66 69 78 "
          + "03 11 29");
private static final byte[] XML_CALENDAR_TIMESTAMP = VPackWireFixtureTest.hex(
            "2c 83 27 17 14 23");
private static final byte[] XML_CALENDAR_STRING = VPackWireFixtureTest.hex(
            "58 31 39 37 34 2d 31 30 2d 31 30 54 31 38 3a 31 35 3a 31 37 2e 31 32 33 5a");
private static final byte[] BOOLEAN_TRUE_OBJECT = VPackWireFixtureTest.hex(
            "0b 07 01 41 62 1a 03");
private static final byte[] BOOLEAN_NUMBER_OBJECT = VPackWireFixtureTest.hex(
            "0b 07 01 41 62 31 03");
private static final byte[] BOOLEAN_STRING_OBJECT = VPackWireFixtureTest.hex(
            "0b 0b 01 41 62 44 74 72 75 65 03");
private static final byte[] BOOLEAN_SHAPED_PROPERTIES = VPackWireFixtureTest.hex(
            "0b 12 03 42 62 31 31 42 62 32 30 42 62 33 1a 03 07 0b");

    // Provenance: MiscJavaXMLTypesReadWriteTest#testQNameSerDefault().
    void testQNameSerDefaultVpack() throws Exception {
        QName qn = new QName("http://abc", "tag", "prefix");
        assertArrayEquals(QNAME_VALUE, MAPPER.writeValueAsBytes(qn));
    }

    // Provenance: MiscJavaXMLTypesReadWriteTest#testQNameSerToObject().
    void testQNameSerToObjectVpack() throws Exception {
        QName qn = new QName("http://abc", "tag", "prefix");
        ObjectMapper mapper = VPackMapper.builder()
                .withConfigOverride(QName.class, cfg -> cfg.setFormat(
                        JsonFormat.Value.forShape(JsonFormat.Shape.OBJECT)))
                .build();
        assertArrayEquals(QNAME_OBJECT, mapper.writeValueAsBytes(qn));
    }

    // Provenance: MiscJavaXMLTypesReadWriteTest#testXMLGregorianCalendarSerAndDeser().
    void testXMLGregorianCalendarSerAndDeserVpack() throws Exception {
        XMLGregorianCalendar calendar = calendar();
        ObjectMapper timestampMapper = VPackMapper.builder()
                .enable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();

        assertArrayEquals(XML_CALENDAR_TIMESTAMP,
                timestampMapper.writeValueAsBytes(calendar));
        XMLGregorianCalendar fromTimestamp = timestampMapper.readValue(
                XML_CALENDAR_TIMESTAMP, XMLGregorianCalendar.class);
        assertNotNull(fromTimestamp);
        assertEquals(calendar.toGregorianCalendar().getTimeInMillis(),
                fromTimestamp.toGregorianCalendar().getTimeInMillis());

        assertArrayEquals(XML_CALENDAR_STRING, MAPPER.writeValueAsBytes(calendar));
    }

    // Provenance: MiscJavaXMLTypesReadWriteTest#testXMLGregorianCalendarDeser().
    void testXMLGregorianCalendarDeserVpack() throws Exception {
        assertEquals(calendar(), MAPPER.readValue(XML_CALENDAR_STRING,
                XMLGregorianCalendar.class));
    }

    // Provenance: MiscJavaXMLTypesReadWriteTest#testXMLGregorianCalendarNumberLengthConstraint().
    void testXMLGregorianCalendarNumberLengthConstraintVpack() throws Exception {
        ObjectMapper constrained = VPackMapper.builder(VPackFactory.builder()
                .streamReadConstraints(StreamReadConstraints.builder()
                        .maxNumberLength(100).build())
                .build()).build();

        byte[] longFraction = literalUtf8Fixture("00:00:00." + "9".repeat(120));
        StreamConstraintsException fractionError = assertThrows(
                StreamConstraintsException.class,
                () -> constrained.readValue(longFraction, XMLGregorianCalendar.class));
        assertEquals(true, fractionError.getMessage().contains("exceeds the maximum allowed"));

        XMLGregorianCalendar normal = constrained.readValue(
                literalUtf8Fixture("2023-01-01T00:00:00"), XMLGregorianCalendar.class);
        assertNotNull(normal);
        assertEquals(2023, normal.getYear());
        assertEquals(1, normal.getMonth());
        assertEquals(1, normal.getDay());
        assertEquals(0, normal.getHour());
        assertEquals(0, normal.getMinute());
        assertEquals(0, normal.getSecond());

        byte[] longYear = literalUtf8Fixture("9".repeat(120) + "-01-01T00:00:00");
        StreamConstraintsException yearError = assertThrows(
                StreamConstraintsException.class,
                () -> constrained.readValue(longYear, XMLGregorianCalendar.class));
        assertEquals(true, yearError.getMessage().contains("exceeds the maximum allowed"));
    }

    // Provenance: MiscJavaXMLTypesReadWriteTest#testQNameNotConstrainedByNumberLength().
    void testQNameNotConstrainedByNumberLengthVpack() throws Exception {
        String localPart = "a".repeat(StreamReadConstraints.DEFAULT_MAX_NUM_LEN + 100);
        QName qn = MAPPER.readValue(literalUtf8Fixture(localPart), QName.class);
        assertEquals(localPart, qn.getLocalPart());
    }
static Stream<Arguments> provideAllPerumtationsOfQNameConstructor() {
        return Stream.of(
                Arguments.of(new QName("test-local-part")),
                Arguments.of(new QName("test-namespace-uri", "test-local-part")),
                Arguments.of(new QName("test-namespace-uri", "test-local-part", "test-prefix"))
        );
    }
private static XMLGregorianCalendar calendar() throws Exception {
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(
                1974, 10, 10, 18, 15, 17, 123, 0);
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
@JsonPropertyOrder({ "b1", "b2", "b3" })
    static class BeanWithBoolean {
        @JsonFormat(shape = JsonFormat.Shape.NUMBER)
        public boolean b1;

        @JsonFormat(shape = JsonFormat.Shape.NUMBER)
        public Boolean b2;

        public boolean b3;

        BeanWithBoolean() { }

        BeanWithBoolean(boolean b1, Boolean b2, boolean b3) {
            this.b1 = b1;
            this.b2 = b2;
            this.b3 = b3;
        }
    }
static class PrimitiveBooleanWrapper {
        public boolean b;

        PrimitiveBooleanWrapper() { }

        PrimitiveBooleanWrapper(boolean value) {
            b = value;
        }
    }
static class BeanWithQName {
        @JsonFormat(shape = JsonFormat.Shape.OBJECT)
        public QName qname;

        BeanWithQName() { }

        BeanWithQName(QName qname) {
            this.qname = qname;
        }
    }

    void __invoke_testQNameSerDefaultVpack() throws Exception {
        try {
            testQNameSerDefaultVpack();
        } finally {
        }
    }


    void __invoke_testQNameSerToObjectVpack() throws Exception {
        try {
            testQNameSerToObjectVpack();
        } finally {
        }
    }


    void __invoke_testXMLGregorianCalendarSerAndDeserVpack() throws Exception {
        try {
            testXMLGregorianCalendarSerAndDeserVpack();
        } finally {
        }
    }


    void __invoke_testXMLGregorianCalendarDeserVpack() throws Exception {
        try {
            testXMLGregorianCalendarDeserVpack();
        } finally {
        }
    }


    void __invoke_testXMLGregorianCalendarNumberLengthConstraintVpack() throws Exception {
        try {
            testXMLGregorianCalendarNumberLengthConstraintVpack();
        } finally {
        }
    }


    void __invoke_testQNameNotConstrainedByNumberLengthVpack() throws Exception {
        try {
            testQNameNotConstrainedByNumberLengthVpack();
        } finally {
        }
    }

}
