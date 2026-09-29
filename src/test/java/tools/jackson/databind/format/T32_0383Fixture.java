package tools.jackson.databind.format;

import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.junit.jupiter.params.provider.Arguments;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0383Fixture {
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
static Stream<Arguments> provideAllPerumtationsOfQNameConstructor() {
        return Stream.of(
                Arguments.of(new QName("test-local-part")),
                Arguments.of(new QName("test-namespace-uri", "test-local-part")),
                Arguments.of(new QName("test-namespace-uri", "test-local-part", "test-prefix"))
        );
    }

    // Provenance: BooleanFormatTest#testPrimitiveShapeViaDefaults().
    void testPrimitiveShapeViaDefaultsVpack() throws Exception {
        assertArrayEquals(BOOLEAN_TRUE_OBJECT,
                MAPPER.writeValueAsBytes(new PrimitiveBooleanWrapper(true)));

        ObjectMapper numberMapper = VPackMapper.builder()
                .withConfigOverride(Boolean.TYPE, cfg -> cfg.setFormat(
                        JsonFormat.Value.forShape(JsonFormat.Shape.NUMBER)))
                .build();
        assertArrayEquals(BOOLEAN_NUMBER_OBJECT,
                numberMapper.writeValueAsBytes(new PrimitiveBooleanWrapper(true)));

        ObjectMapper stringMapper = VPackMapper.builder()
                .withConfigOverride(Boolean.TYPE, cfg -> cfg.setFormat(
                        JsonFormat.Value.forShape(JsonFormat.Shape.STRING)))
                .build();
        assertArrayEquals(BOOLEAN_STRING_OBJECT,
                stringMapper.writeValueAsBytes(new PrimitiveBooleanWrapper(true)));
    }

    // Provenance: BooleanFormatTest#testShapeOnProperty().
    void testShapeOnPropertyVpack() throws Exception {
        assertArrayEquals(BOOLEAN_SHAPED_PROPERTIES,
                MAPPER.writeValueAsBytes(new BeanWithBoolean(true, false, true)));
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

    void __invoke_testPrimitiveShapeViaDefaultsVpack() throws Exception {
        try {
            testPrimitiveShapeViaDefaultsVpack();
        } finally {
        }
    }


    void __invoke_testShapeOnPropertyVpack() throws Exception {
        try {
            testShapeOnPropertyVpack();
        } finally {
        }
    }

}
