package tools.jackson.databind.ext.sql;

import java.sql.Blob;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;

import tools.jackson.databind.DatabindContext;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0380F1 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] SQL_BLOB_OBJECT = VPackWireFixtureTest.hex(
            "0b 1a 01 48 73 71 6c 42 6c 6f 62 31 c0 0b "
          + "54 65 73 74 4f 62 6a 65 63 74 31 03");
private static final byte[] SQL_DATE_MILLIS = VPackWireFixtureTest.hex(
            "2d 00 50 4d 3f d7 00");
private static final byte[] SQL_DATE_STRING = VPackWireFixtureTest.hex(
            "4a 31 39 39 39 2d 30 34 2d 31 39");
private static final byte[] EMPTY_STRING = VPackWireFixtureTest.hex("40");

    // Provenance: SqlDateDeserializationTest#testDateSql().
    void testDateSqlVpack() throws Exception {
        java.sql.Date fromMillis = MAPPER.readValue(SQL_DATE_MILLIS, java.sql.Date.class);
        assertEquals(new java.sql.Date(924480000000L), fromMillis);

        java.sql.Date fromDefaultString = MAPPER.readValue(SQL_DATE_STRING, java.sql.Date.class);
        Calendar calendar = gmtCalendar(fromDefaultString.getTime());
        assertEquals(1999, calendar.get(Calendar.YEAR));
        assertEquals(Calendar.APRIL, calendar.get(Calendar.MONTH));
        assertEquals(19, calendar.get(Calendar.DAY_OF_MONTH));

        java.sql.Date fromRegularString = MAPPER.readValue(
                VPackWireFixtureTest.hex("4a 31 39 38 31 2d 30 37 2d 31 33"),
                java.sql.Date.class);
        calendar.setTimeInMillis(fromRegularString.getTime());
        assertEquals(1981, calendar.get(Calendar.YEAR));
        assertEquals(Calendar.JULY, calendar.get(Calendar.MONTH));
        assertEquals(13, calendar.get(Calendar.DAY_OF_MONTH));
    }

    // Provenance: SqlDateDeserializationTest#testDatesWithEmptyStrings().
    void testDatesWithEmptyStringsVpack() throws Exception {
        assertNull(MAPPER.readValue(EMPTY_STRING, java.sql.Date.class));
    }
private static ObjectMapper typedMapper() {
        return VPackMapper.builder()
                .activateDefaultTypingAsProperty(new NoCheckSubTypeValidator(),
                        DefaultTyping.NON_FINAL, "@class")
                .build();
    }
private static void assertMapSize(ObjectMapper mapper, ObjectWriter writer,
            Map<String, String> input, int size) throws Exception {
        Map<?, ?> output = mapper.readValue(writer.writeValueAsBytes(input), Map.class);
        assertEquals(size, output.size());
    }
private static Calendar gmtCalendar(long time) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar.setTimeInMillis(time);
        return calendar;
    }
static class BlobObject {
        Blob sqlBlob1;

        public Blob getSqlBlob1() {
            return sqlBlob1;
        }

        public void setSqlBlob1(Blob sqlBlob1) {
            this.sqlBlob1 = sqlBlob1;
        }
    }
static class NoCheckSubTypeValidator extends PolymorphicTypeValidator.Base {
        private static final long serialVersionUID = 1L;

        @Override
        public Validity validateBaseType(DatabindContext ctxt, JavaType baseType) {
            return Validity.ALLOWED;
        }
    }

    void __invoke_testDateSqlVpack() throws Exception {
        try {
            testDateSqlVpack();
        } finally {
        }
    }


    void __invoke_testDatesWithEmptyStringsVpack() throws Exception {
        try {
            testDatesWithEmptyStringsVpack();
        } finally {
        }
    }

}
