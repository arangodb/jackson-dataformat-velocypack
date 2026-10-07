package com.arangodb.jackson.dataformat.velocypack;

import org.junit.jupiter.api.Test;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.ObjectMapper;

import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;

public class VPackParserBufTest {

    private static final ObjectMapper MAPPER = new VPackMapper();

    @Test
    public void testGetString_writer_nullString() throws Exception {
        byte[] vpack = MAPPER.writeValueAsBytes(null);
        try (JsonParser p = MAPPER.createParser(vpack)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NULL);
            StringWriter sw = new StringWriter();
            int len = p.getString(sw);
            assertThat(len).isEqualTo(0);
            assertThat(sw.toString()).isEmpty();
        }
    }

    @Test
    public void testGetString_writer_stringValue() throws Exception {
        byte[] vpack = MAPPER.writeValueAsBytes("abc");
        try (JsonParser p = MAPPER.createParser(vpack)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_STRING);
            StringWriter sw = new StringWriter();
            int len = p.getString(sw);
            assertThat(len).isEqualTo(3);
            assertThat(sw.toString()).isEqualTo("abc");
        }
    }

    @Test
    public void testGetString_writer_intValue() throws Exception {
        byte[] vpack = MAPPER.writeValueAsBytes(42);
        try (JsonParser p = MAPPER.createParser(vpack)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_INT);
            StringWriter sw = new StringWriter();
            int len = p.getString(sw);
            assertThat(len).isEqualTo(2);
            assertThat(sw.toString()).isEqualTo("42");
        }
    }
}
