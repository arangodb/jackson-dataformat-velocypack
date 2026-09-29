package tools.jackson.databind.ext.sql;

import java.sql.Timestamp;

import org.w3c.dom.Element;
import com.fasterxml.jackson.annotation.JsonFormat;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.StdSerializer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0381F1 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] SQL_DATE_1999 = VPackWireFixtureTest.hex(
            "2c 00 d8 9a e2 d6");
private static final byte[] SQL_DATE_ZERO = VPackWireFixtureTest.hex("30");
private static final byte[] SQL_DATE_ZERO_OBJECT = VPackWireFixtureTest.hex(
            "0b 0a 01 44 64 61 74 65 30 03");
private static final byte[] SQL_DATE_1999_STRING = VPackWireFixtureTest.hex(
            "58 31 39 39 39 2d 30 34 2d 30 31 54 30 30 3a 30 30 3a 30 30 2e 30 30 30 5a");
private static final byte[] SQL_DATE_ZERO_STRING = VPackWireFixtureTest.hex(
            "58 31 39 37 30 2d 30 31 2d 30 31 54 30 30 3a 30 30 3a 30 30 2e 30 30 30 5a");
private static final byte[] SQL_DATE_ZERO_STRING_OBJECT = VPackWireFixtureTest.hex(
            "0b 22 01 44 64 61 74 65 58 31 39 37 30 2d 30 31 2d 30 31 54 30 30 3a 30 30 3a 30 30 2e 30 30 30 5a 03");
private static final byte[] SQL_TIME = VPackWireFixtureTest.hex(
            "48 31 32 3a 33 34 3a 35 36");
private static final byte[] SQL_TIMESTAMP_ZERO = SQL_DATE_ZERO_STRING;
private static final byte[] SQL_DATE_PATTERN = VPackWireFixtureTest.hex(
            "0b 1b 01 4b 64 61 74 65 4f 66 42 69 72 74 68 4a 31 39 38 30 2e 30 34 2e 31 34 03");
private static final byte[] SQL_DATE_CONFIG_OVERRIDE = VPackWireFixtureTest.hex(
            "4a 31 39 38 30 2b 30 34 2b 31 34");
private static final byte[] TIMESTAMP_MILLIS = VPackWireFixtureTest.hex(
            "2b 15 cd 5b 07");
private static final byte[] TIMESTAMP_STRING = VPackWireFixtureTest.hex(
            "5c 31 39 37 30 2d 30 31 2d 30 32 54 31 30 3a 31 37 3a 33 36 2e 37 38 39 2b 30 30 30 30");
private static final byte[] TIMESTAMP_MILLIS_ARRAY = VPackWireFixtureTest.hex(
            "02 07 2b 15 cd 5b 07");
private static final byte[] TIMESTAMP_STRING_ARRAY = VPackWireFixtureTest.hex(
            "02 1f 5c 31 39 37 30 2d 30 31 2d 30 32 54 31 30 3a 31 37 3a 33 36 2e 37 38 39 2b 30 30 30 30");
private static final byte[] CUSTOM_ELEMENT = VPackWireFixtureTest.hex(
            "47 65 6c 65 6d 65 6e 74");

    // Provenance: SqlTimestampDeserializationTest#testTimestampUtil().
    void testTimestampUtilVpack() throws Exception {
        long now = 123456789L;
        Timestamp expected = new Timestamp(now);
        assertEquals(expected, MAPPER.readValue(TIMESTAMP_MILLIS, Timestamp.class));
        assertEquals(expected, MAPPER.readValue(TIMESTAMP_STRING, Timestamp.class));
    }

    // Provenance: SqlTimestampDeserializationTest#testTimestampUtilSingleElementArray().
    void testTimestampUtilSingleElementArrayVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(tools.jackson.databind.DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
                .build();
        Timestamp expected = new Timestamp(123456789L);
        assertEquals(expected, mapper.readValue(TIMESTAMP_MILLIS_ARRAY, Timestamp.class));
        assertEquals(expected, mapper.readValue(TIMESTAMP_STRING_ARRAY, Timestamp.class));
    }
static class Person {
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd")
        public java.sql.Date dateOfBirth;
    }
static class SqlDateAsDefaultBean {
        public java.sql.Date date;
        SqlDateAsDefaultBean(long value) { date = new java.sql.Date(value); }
    }
static class SqlDateAsNumberBean {
        @JsonFormat(shape = JsonFormat.Shape.NUMBER)
        public java.sql.Date date;
        SqlDateAsNumberBean(long value) { date = new java.sql.Date(value); }
    }
static class ElementSerializer extends StdSerializer<Element> {
        ElementSerializer() { super(Element.class); }

        @Override
        public void serialize(Element value, JsonGenerator generator,
                tools.jackson.databind.SerializationContext ctxt) {
            generator.writeString("element");
        }
    }
@JsonSerialize(using = ElementSerializer.class)
    static class ElementMixin { }

    void __invoke_testTimestampUtilVpack() throws Exception {
        try {
            testTimestampUtilVpack();
        } finally {
        }
    }


    void __invoke_testTimestampUtilSingleElementArrayVpack() throws Exception {
        try {
            testTimestampUtilSingleElementArrayVpack();
        } finally {
        }
    }

}
