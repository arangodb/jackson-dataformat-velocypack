package tools.jackson.databind.ext.sql;

import java.nio.charset.StandardCharsets;
import java.sql.Blob;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;
import javax.sql.rowset.serial.SerialBlob;

import tools.jackson.databind.DatabindContext;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0380F0 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] SQL_BLOB_OBJECT = VPackWireFixtureTest.hex(
            "0b 1a 01 48 73 71 6c 42 6c 6f 62 31 c0 0b "
          + "54 65 73 74 4f 62 6a 65 63 74 31 03");
private static final byte[] SQL_DATE_MILLIS = VPackWireFixtureTest.hex(
            "2d 00 50 4d 3f d7 00");
private static final byte[] SQL_DATE_STRING = VPackWireFixtureTest.hex(
            "4a 31 39 39 39 2d 30 34 2d 31 39");
private static final byte[] EMPTY_STRING = VPackWireFixtureTest.hex("40");

    // Provenance: SqlBlobSerializationTest#testSqlBlobSerializer().
    void testSqlBlobSerializerVpack() throws Exception {
        Blob blob = new SerialBlob("TestObject1".getBytes(StandardCharsets.US_ASCII));
        BlobObject input = new BlobObject();
        input.sqlBlob1 = blob;
        assertArrayEquals(SQL_BLOB_OBJECT, MAPPER.writeValueAsBytes(input));
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

    void __invoke_testSqlBlobSerializerVpack() throws Exception {
        try {
            testSqlBlobSerializerVpack();
        } finally {
        }
    }

}
