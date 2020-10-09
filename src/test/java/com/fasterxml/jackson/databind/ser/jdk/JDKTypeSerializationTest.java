package com.fasterxml.jackson.databind.ser.jdk;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.velocypack.TestVelocypackMapper;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Unit tests for JDK types not covered by other tests (i.e. things
 * that are not Enums, Collections, Maps, or standard Date/Time types)
 */
public class JDKTypeSerializationTest
    extends com.fasterxml.jackson.databind.BaseMapTest
{
    private final ObjectMapper MAPPER = objectMapper();

    static class InetAddressBean {
        public InetAddress value;

        public InetAddressBean(InetAddress i) { value = i; }
    }

    // [databind#2197]
    static class VoidBean {
        public Void value;
    }

    public void testBigDecimal() throws Exception
    {
        Map<String, Object> map = new HashMap<String, Object>();
        String PI_STR = "3.14159265";
        map.put("pi", new BigDecimal(PI_STR));
        String str = com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(map));
        assertEquals("{\"pi\":3.14159265}", str);
    }
    
    public void testBigDecimalAsPlainString() throws Exception
    {
        final ObjectMapper mapper = new TestVelocypackMapper();

        mapper.enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);
        Map<String, Object> map = new HashMap<String, Object>();
        String PI_STR = "3.00000001";
        map.put("pi", new BigDecimal(PI_STR));
        String str = com.fasterxml.jackson.VPackUtils.toJson( mapper.writeValueAsBytes(map));
        assertEquals("{\"pi\":3.00000001}", str);
    }

    public void testFile() throws IOException
    {
        // this may get translated to different representation on Windows, maybe Mac:
        File f = new File(new File("/tmp"), "foo.text");
        String str = com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(f));
        // escape backslashes (for portability with windows)
        String escapedAbsPath = f.getAbsolutePath().replaceAll("\\\\", "\\\\\\\\"); 
        assertEquals(quote(escapedAbsPath), str);
    }

    public void testRegexps() throws IOException
    {
        final String PATTERN_STR = "\\s+([a-b]+)\\w?";
        Pattern p = Pattern.compile(PATTERN_STR);
        Map<String,Object> input = new HashMap<String,Object>();
        input.put("p", p);
        Map<String,Object> result = writeAndMap(MAPPER, input);
        assertEquals(p.pattern(), result.get("p"));
    }

    public void testCurrency() throws IOException
    {
        Currency usd = Currency.getInstance("USD");
        assertEquals(quote("USD"), com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(usd)));
    }

    public void testLocale() throws IOException
    {
        assertEquals(quote("en"), com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(new Locale("en"))));
        assertEquals(quote("es_ES"), com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(new Locale("es", "ES"))));
        assertEquals(quote("fi_FI_savo"), com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(new Locale("FI", "fi", "savo"))));

        assertEquals(quote("en_US"), com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(Locale.US)));

        // [databind#1123]
        assertEquals(quote(""), com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(Locale.ROOT)));
    }

    public void testInetAddress() throws IOException
    {
        assertEquals(quote("127.0.0.1"), com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(InetAddress.getByName("127.0.0.1"))));
        InetAddress input = InetAddress.getByName("google.com");
        assertEquals(quote("google.com"), com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(input)));

        ObjectMapper mapper = new TestVelocypackMapper();
        mapper.configOverride(InetAddress.class)
            .setFormat(JsonFormat.Value.forShape(JsonFormat.Shape.NUMBER));
        String json = com.fasterxml.jackson.VPackUtils.toJson( mapper.writeValueAsBytes(input));
        assertEquals(quote(input.getHostAddress()), json);

        assertEquals(String.format("{\"value\":\"%s\"}", input.getHostAddress()), com.fasterxml.jackson.VPackUtils.toJson(
                mapper.writeValueAsBytes(new InetAddressBean(input))));
    }

    public void testInetSocketAddress() throws IOException
    {
        assertEquals(quote("127.0.0.1:8080"), com.fasterxml.jackson.VPackUtils.toJson(
                MAPPER.writeValueAsBytes(new InetSocketAddress("127.0.0.1", 8080))));
        assertEquals(quote("google.com:6667"), com.fasterxml.jackson.VPackUtils.toJson(
                MAPPER.writeValueAsBytes(new InetSocketAddress("google.com", 6667))));
        assertEquals(quote("[2001:db8:85a3:8d3:1319:8a2e:370:7348]:443"), com.fasterxml.jackson.VPackUtils.toJson(
                MAPPER.writeValueAsBytes(new InetSocketAddress("2001:db8:85a3:8d3:1319:8a2e:370:7348", 443))));
    }

    // [JACKSON-597]
    public void testClass() throws IOException
    {
        assertEquals(quote("java.lang.String"), com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(String.class)));
        assertEquals(quote("int"), com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(Integer.TYPE)));
        assertEquals(quote("boolean"), com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(Boolean.TYPE)));
        assertEquals(quote("void"), com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(Void.TYPE)));
    }

    public void testCharset() throws IOException
    {
        assertEquals(quote("UTF-8"), com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(Charset.forName("UTF-8"))));
    }

    // [databind#239]: Support serialization of ByteBuffer
    public void testByteBuffer() throws IOException
    {
        final byte[] INPUT_BYTES = new byte[] { 1, 2, 3, 4, 5 };
        String exp = com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(INPUT_BYTES));
        ByteBuffer bbuf = ByteBuffer.wrap(INPUT_BYTES);
        assertEquals(exp, com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(bbuf)));
    }

    // Verify that efficient UUID codec won't mess things up:
    public void testUUIDs() throws IOException
    {
        // first, couple of generated UUIDs:
        for (String value : new String[] {
                "76e6d183-5f68-4afa-b94a-922c1fdb83f8",
                "540a88d1-e2d8-4fb1-9396-9212280d0a7f",
                "2c9e441d-1cd0-472d-9bab-69838f877574",
                "591b2869-146e-41d7-8048-e8131f1fdec5",
                "82994ac2-7b23-49f2-8cc5-e24cf6ed77be",
                "00000007-0000-0000-0000-000000000000"
        }) {
            UUID uuid = UUID.fromString(value);
            String v = MAPPER.readValue(MAPPER.writeValueAsBytes(uuid), UUID.class).toString();
            assertEquals(uuid.toString(), v);

            // Also, wrt [#362], should convert cleanly
            String str = MAPPER.convertValue(uuid, String.class);
            assertEquals(value, str);
        }
        
        // then use templating; note that these are not exactly valid UUIDs
        // wrt spec (type bits etc), but JDK UUID should deal ok
        final String TEMPL = "00000000-0000-0000-0000-000000000000";
        final String chars = "123456789abcdef";

        for (int i = 0; i < chars.length(); ++i) {
            String value = TEMPL.replace('0', chars.charAt(i));
            UUID uuid = UUID.fromString(value);
            String v = MAPPER.readValue(MAPPER.writeValueAsBytes(uuid), UUID.class).toString();
            assertEquals(uuid.toString(), v);
        }
    }

    // [databind#2197]
    public void testVoidSerialization() throws Exception
    {
        assertEquals(aposToQuotes("{'value':null}"), com.fasterxml.jackson.VPackUtils.toJson(
                MAPPER.writeValueAsBytes(new VoidBean())));
    }
}
