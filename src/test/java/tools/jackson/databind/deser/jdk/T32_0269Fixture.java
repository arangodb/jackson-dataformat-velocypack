package tools.jackson.databind.deser.jdk;

import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Currency;
import java.util.regex.Pattern;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.exc.InvalidFormatException;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.exc.ValueInstantiationException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0269Fixture {
private static final ObjectMapper MAPPER = new VPackMapper();

    // Provenance: JDKStringLikeTypeDeserTest#testCharset().
    void testCharset() throws Exception {
        Charset utf8 = Charset.forName("UTF-8");
        assertSame(utf8, MAPPER.readValue(text("UTF-8"), Charset.class));
    }

    // Provenance: JDKStringLikeTypeDeserTest#testClass().
    void testClass() throws Exception {
        ObjectReader reader = MAPPER.readerFor(Class.class);
        assertSame(String.class, reader.readValue(text("java.lang.String")));

        assertSame(Boolean.TYPE, reader.readValue(text("boolean")));
        assertSame(Byte.TYPE, reader.readValue(text("byte")));
        assertSame(Short.TYPE, reader.readValue(text("short")));
        assertSame(Character.TYPE, reader.readValue(text("char")));
        assertSame(Integer.TYPE, reader.readValue(text("int")));
        assertSame(Long.TYPE, reader.readValue(text("long")));
        assertSame(Float.TYPE, reader.readValue(text("float")));
        assertSame(Double.TYPE, reader.readValue(text("double")));
        assertSame(Void.TYPE, reader.readValue(text("void")));

        ValueInstantiationException failure = assertThrows(ValueInstantiationException.class,
                () -> reader.readValue(text("UNKNOWN")));
        assertEquals(true, failure.getMessage().contains("java.lang.Class"));
        assertEquals(true, failure.getMessage().contains("UNKNOWN"));
    }

    // Provenance: JDKStringLikeTypeDeserTest#testClassWithParams().
    void testClassWithParams() throws Exception {
        ParamClassBean result = MAPPER.readValue(compactObject(
                "name", text("Foobar"),
                "clazz", text("java.lang.String")), ParamClassBean.class);
        assertEquals("Foobar", result.name);
        assertSame(String.class, result.clazz);
    }

    // Provenance: JDKStringLikeTypeDeserTest#testCurrency().
    void testCurrency() throws Exception {
        ObjectReader reader = MAPPER.readerFor(Currency.class);
        assertEquals(Currency.getInstance("USD"), reader.readValue(text("USD")));

        InvalidFormatException failure = assertThrows(InvalidFormatException.class,
                () -> reader.readValue(text("poobah")));
        assertEquals(true, failure.getMessage().contains("Currency"));
        assertEquals(true, failure.getMessage().contains("Unrecognized currency"));
    }

    // Provenance: JDKStringLikeTypeDeserTest#testFile().
    void testFile() throws Exception {
        File source = new File("/test").getAbsoluteFile();
        File result = MAPPER.readValue(text(source.getAbsolutePath()), File.class);
        assertEquals(source.getAbsolutePath(), result.getAbsolutePath());
    }

    // Provenance: JDKStringLikeTypeDeserTest#testInetAddress().
    void testInetAddress() throws Exception {
        InetAddress address = MAPPER.readValue(text("127.0.0.1"), InetAddress.class);
        assertEquals("127.0.0.1", address.getHostAddress());

        InetAddress ip6 = MAPPER.readValue(
                text("2001:db8:85a3:8d3:1319:8a2e:370:7348"), InetAddress.class);
        assertEquals("2001:db8:85a3:8d3:1319:8a2e:370:7348", ip6.getHostAddress());

        InetAddress loopback6 = MAPPER.readValue(text("::1"), InetAddress.class);
        assertEquals("0:0:0:0:0:0:0:1", loopback6.getHostAddress());
    }

    // Provenance: JDKStringLikeTypeDeserTest#testInetAddressNoDNSLookup().
    void testInetAddressNoDNSLookup() throws Exception {
        assertInvalidInetAddress("localhost");
        assertInvalidInetAddress("google.com");
        assertInvalidInetAddress("1.2.3.4.example.com");
        assertInvalidInetAddress("xn--bcher-kva.example.com");
    }

    // Provenance: JDKStringLikeTypeDeserTest#testInetAddressNonAsciiDigits().
    void testInetAddressNonAsciiDigits() throws Exception {
        assertInvalidInetAddress("\uFF11\uFF12\uFF17.0.0.1");
        assertInvalidInetAddress("\u0661\u0662\u0667.0.0.1");
        assertInvalidInetAddress("\uFF12001:db8::1");
        assertInvalidInetAddress("fe80::\uFF11");
        assertInvalidInetAddress("\uFF41bcd::1");
    }

    // Provenance: JDKStringLikeTypeDeserTest#testInetSocketAddress().
    void testInetSocketAddress() throws Exception {
        ObjectReader reader = MAPPER.readerFor(InetSocketAddress.class);
        InetSocketAddress address = reader.readValue(text("127.0.0.1"));
        assertEquals("127.0.0.1", address.getHostName());

        InetSocketAddress ip6 = reader.readValue(
                text("2001:db8:85a3:8d3:1319:8a2e:370:7348"));
        assertEquals("2001:db8:85a3:8d3:1319:8a2e:370:7348", ip6.getHostName());

        InetSocketAddress ip6port = reader.readValue(
                text("[2001:db8:85a3:8d3:1319:8a2e:370:7348]:443"));
        assertEquals("[2001:db8:85a3:8d3:1319:8a2e:370:7348]", ip6port.getHostName());
        assertEquals(443, ip6port.getPort());

        InetSocketAddress host = reader.readValue(text("www.google.com"));
        assertEquals("www.google.com", host.getHostName());
        InetSocketAddress hostAndPort = reader.readValue(text("www.google.com:80"));
        assertEquals("www.google.com", hostAndPort.getHostName());
        assertEquals(80, hostAndPort.getPort());

        InvalidFormatException failure = assertThrows(InvalidFormatException.class,
                () -> reader.readValue(text("[2001:")));
        assertEquals(true, failure.getMessage().contains("InetSocketAddress"));
        assertEquals(true, failure.getMessage().contains("closing bracket"));
    }

    // Provenance: JDKStringLikeTypeDeserTest#testPattern().
    void testPattern() throws Exception {
        Pattern result = MAPPER.readValue(text("abc:\\s?(\\d+)"), Pattern.class);
        assertEquals("abc:\\s?(\\d+)", result.pattern());

        result = MAPPER.readValue(text("^WIN\\ "), Pattern.class);
        assertEquals("^WIN\\ ", result.pattern());

        InvalidFormatException failure = assertThrows(InvalidFormatException.class,
                () -> MAPPER.readValue(text("[abc"), Pattern.class));
        assertEquals(true, failure.getMessage().contains("Pattern"));
        assertEquals(true, failure.getMessage().contains("Invalid"));
    }

    // Provenance: JDKStringLikeTypeDeserTest#testStringBuffer().
    void testStringBuffer() throws Exception {
        ObjectReader reader = MAPPER.readerFor(StringBuffer.class);
        assertEquals("def", reader.readValue(text("def")).toString());
        assertThrows(MismatchedInputException.class,
                () -> reader.readValue(VPackWireFixtureTest.hex("01")));
    }

    // Provenance: JDKStringLikeTypeDeserTest#testStringBuilder().
    void testStringBuilder() throws Exception {
        ObjectReader reader = MAPPER.readerFor(StringBuilder.class);
        assertEquals("abc", reader.readValue(text("abc")).toString());
        assertThrows(MismatchedInputException.class,
                () -> reader.readValue(VPackWireFixtureTest.hex("01")));
    }
private static void assertInvalidInetAddress(String address) throws Exception {
        InvalidFormatException failure = assertThrows(InvalidFormatException.class,
                () -> MAPPER.readValue(text(address), InetAddress.class));
        assertEquals(true, failure.getMessage().contains("Not a valid IP address string literal"));
    }
private static byte[] text(String value) {
        byte[] payload = value.getBytes(StandardCharsets.UTF_8);
        if (payload.length > 126) {
            throw new IllegalArgumentException("fixture is not a short string");
        }
        byte[] result = new byte[payload.length + 1];
        result[0] = (byte) (0x40 + payload.length);
        System.arraycopy(payload, 0, result, 1, payload.length);
        return result;
    }
private static byte[] compactObject(Object... keyValues) {
        if ((keyValues.length & 1) != 0) {
            throw new IllegalArgumentException("object requires key/value pairs");
        }
        byte[][] fields = new byte[keyValues.length][];
        int bodyLength = 0;
        for (int i = 0; i < keyValues.length; ++i) {
            fields[i] = i % 2 == 0
                    ? text((String) keyValues[i]) : (byte[]) keyValues[i];
            bodyLength += fields[i].length;
        }
        int pairCount = keyValues.length / 2;
        int length = 1 + 1 + bodyLength + 1;
        byte[] result = new byte[length];
        result[0] = 0x14;
        result[1] = (byte) length;
        int offset = 2;
        for (byte[] field : fields) {
            System.arraycopy(field, 0, result, offset, field.length);
            offset += field.length;
        }
        result[length - 1] = (byte) pairCount;
        return result;
    }
static class ParamClassBean {
        public String name = "bar";
        public Class<String> clazz;

        public ParamClassBean() { }

        public ParamClassBean(String name) {
            this.name = name;
            clazz = String.class;
        }
    }

    void __invoke_testCharset() throws Exception {
        try {
            testCharset();
        } finally {
        }
    }


    void __invoke_testClass() throws Exception {
        try {
            testClass();
        } finally {
        }
    }


    void __invoke_testClassWithParams() throws Exception {
        try {
            testClassWithParams();
        } finally {
        }
    }


    void __invoke_testCurrency() throws Exception {
        try {
            testCurrency();
        } finally {
        }
    }


    void __invoke_testFile() throws Exception {
        try {
            testFile();
        } finally {
        }
    }


    void __invoke_testInetAddress() throws Exception {
        try {
            testInetAddress();
        } finally {
        }
    }


    void __invoke_testInetAddressNoDNSLookup() throws Exception {
        try {
            testInetAddressNoDNSLookup();
        } finally {
        }
    }


    void __invoke_testInetAddressNonAsciiDigits() throws Exception {
        try {
            testInetAddressNonAsciiDigits();
        } finally {
        }
    }


    void __invoke_testInetSocketAddress() throws Exception {
        try {
            testInetSocketAddress();
        } finally {
        }
    }


    void __invoke_testPattern() throws Exception {
        try {
            testPattern();
        } finally {
        }
    }


    void __invoke_testStringBuffer() throws Exception {
        try {
            testStringBuffer();
        } finally {
        }
    }


    void __invoke_testStringBuilder() throws Exception {
        try {
            testStringBuilder();
        } finally {
        }
    }

}
