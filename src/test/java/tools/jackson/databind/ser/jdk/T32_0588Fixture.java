package tools.jackson.databind.ser.jdk;

import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Currency;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ser.std.StdScalarSerializer;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0588Fixture {
private static final ObjectMapper MAPPER = new VPackMapper();

    // Provenance: JDKTypeSerializationTest#testCurrency().
    void testCurrencyVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex("43 55 53 44"),
                MAPPER.writeValueAsBytes(Currency.getInstance("USD")));
    }

    // Provenance: JDKTypeSerializationTest#testCustomSerializer().
    void testCustomSerializerVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 11 01 45 76 61 6c 75 65 46 46 4f 4f 42 41 52 03"),
                MAPPER.writeValueAsBytes(new UCStringWrapper("fooBAR")));
    }

    // Provenance: JDKTypeSerializationTest#testDuplicatedByteBufferWithCustomPosition().
    void testDuplicatedByteBufferWithCustomPositionVpack() throws Exception {
        byte[] input = { 1, 2, 3, 4, 5 };
        ByteBuffer buffer = ByteBuffer.wrap(input);
        buffer.position(2);
        assertArrayEquals(VPackWireFixtureTest.hex("c0 03 03 04 05"),
                MAPPER.writeValueAsBytes(buffer.duplicate()));

        buffer = ByteBuffer.wrap(input, 1, 3);
        assertArrayEquals(VPackWireFixtureTest.hex("c0 03 02 03 04"),
                MAPPER.writeValueAsBytes(buffer.duplicate()));
    }

    // Provenance: JDKTypeSerializationTest#testDuplicatedByteBufferWithCustomPositionDirect().
    void testDuplicatedByteBufferWithCustomPositionDirectVpack() throws Exception {
        byte[] input = { 1, 2, 3, 4, 5 };
        ByteBuffer buffer = ByteBuffer.allocateDirect(input.length);
        buffer.put(input).position(2);
        assertArrayEquals(VPackWireFixtureTest.hex("c0 03 03 04 05"),
                MAPPER.writeValueAsBytes(buffer.duplicate()));
    }

    // Provenance: JDKTypeSerializationTest#testFile().
    void testFileVpack() throws Exception {
        File file = new File(new File("/tmp"), "foo.text");
        assertArrayEquals(VPackWireFixtureTest.hex(
                "4d 2f 74 6d 70 2f 66 6f 6f 2e 74 65 78 74"),
                MAPPER.writeValueAsBytes(file));
    }

    // Provenance: JDKTypeSerializationTest#testFromArray().
    void testFromArrayVpack() throws Exception {
        ArrayList<Object> document = new ArrayList<>();
        document.add("Elem1");
        document.add(Integer.valueOf(3));
        Map<String, Object> struct = new LinkedHashMap<>();
        struct.put("first", Boolean.TRUE);
        struct.put("Second", new ArrayList<>());
        document.add(struct);
        document.add(Boolean.FALSE);

        byte[] expected = VPackWireFixtureTest.hex(
                "06 23 04 45 45 6c 65 6d 31 33"
              + "0b 14 02 45 66 69 72 73 74 1a 46 53 65 63 6f 6e 64 01 0a 03"
              + "19 03 09 0a 1e");
        for (int i = 0; i < 3; ++i) {
            assertArrayEquals(expected, MAPPER.writeValueAsBytes(document));
        }
        try (JsonParser parser = MAPPER.createParser(expected)) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("Elem1", parser.getString());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(3, parser.getIntValue());
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("first", parser.currentName());
            assertEquals(JsonToken.VALUE_TRUE, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("Second", parser.currentName());
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertEquals(JsonToken.VALUE_FALSE, parser.nextToken());
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            assertEquals(null, parser.nextToken());
        }
    }

    // Provenance: JDKTypeSerializationTest#testFromMap().
    void testFromMapVpack() throws Exception {
        LinkedHashMap<String, Object> document = new LinkedHashMap<>();
        document.put("a1", "\"text\"");
        document.put("int", Integer.valueOf(137));
        document.put("foo bar", Long.valueOf(1234567890L));

        byte[] expected = VPackWireFixtureTest.hex(
                "0b 23 03 42 61 31 46 22 74 65 78 74 22"
              + "43 69 6e 74 28 89 47 66 6f 6f 20 62 61 72 2b d2 02 96 49"
              + "03 13 0d");
        for (int i = 0; i < 3; ++i) {
            assertArrayEquals(expected, MAPPER.writeValueAsBytes(document));
        }
        try (JsonParser parser = MAPPER.createParser(expected)) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("a1", parser.currentName());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("\"text\"", parser.getString());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("int", parser.currentName());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(137, parser.getIntValue());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("foo bar", parser.currentName());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(1234567890L, parser.getLongValue());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertEquals(null, parser.nextToken());
        }
    }

    // Provenance: JDKTypeSerializationTest#testInetAddress().
    void testInetAddressVpack() throws Exception {
        InetAddress input = InetAddress.getByName("127.0.0.1");
        assertArrayEquals(VPackWireFixtureTest.hex("49 31 32 37 2e 30 2e 30 2e 31"),
                MAPPER.writeValueAsBytes(input));

        InetAddress named = InetAddress.getByAddress("myhost.example.com",
                new byte[] { 1, 2, 3, 4 });
        assertArrayEquals(VPackWireFixtureTest.hex(
                "52 6d 79 68 6f 73 74 2e 65 78 61 6d 70 6c 65 2e 63 6f 6d"),
                MAPPER.writeValueAsBytes(named));
        assertArrayEquals(VPackWireFixtureTest.hex("47 31 2e 32 2e 33 2e 34"),
                VPackMapper.builder()
                        .withConfigOverride(InetAddress.class, o -> o.setFormat(
                                com.fasterxml.jackson.annotation.JsonFormat.Value.forShape(
                                        com.fasterxml.jackson.annotation.JsonFormat.Shape.NUMBER)))
                        .build().writeValueAsBytes(named));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 12 01 45 76 61 6c 75 65 47 31 2e 32 2e 33 2e 34 03"),
                VPackMapper.builder()
                        .withConfigOverride(InetAddress.class, o -> o.setFormat(
                                com.fasterxml.jackson.annotation.JsonFormat.Value.forShape(
                                        com.fasterxml.jackson.annotation.JsonFormat.Shape.NUMBER)))
                        .build().writeValueAsBytes(new InetAddressBean(named)));
    }

    // Provenance: JDKTypeSerializationTest#testInetSocketAddress().
    void testInetSocketAddressVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex("4e 31 32 37 2e 30 2e 30 2e 31 3a 38 30 38 30"),
                MAPPER.writeValueAsBytes(new InetSocketAddress("127.0.0.1", 8080)));
        assertArrayEquals(VPackWireFixtureTest.hex("4e 31 32 37 2e 30 2e 30 2e 31 3a 38 30 38 30"),
                MAPPER.writeValueAsBytes(InetSocketAddress.createUnresolved("127.0.0.1", 8080)));
        assertArrayEquals(VPackWireFixtureTest.hex("4f 67 6f 6f 67 6c 65 2e 63 6f 6d 3a 36 36 36 37"),
                MAPPER.writeValueAsBytes(new InetSocketAddress("google.com", 6667)));
        assertArrayEquals(VPackWireFixtureTest.hex("4f 67 6f 6f 67 6c 65 2e 63 6f 6d 3a 36 36 36 37"),
                MAPPER.writeValueAsBytes(InetSocketAddress.createUnresolved("google.com", 6667)));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "6a 5b 32 30 30 31 3a 64 62 38 3a 38 35 61 33 3a 38 64 33 3a 31 33 31 39 3a 38 61 32 65 3a 33 37 30 3a 37 33 34 38 5d 3a 34 34 33"),
                MAPPER.writeValueAsBytes(new InetSocketAddress(
                        "2001:db8:85a3:8d3:1319:8a2e:370:7348", 443)));
    }

    // Provenance: JDKTypeSerializationTest#testLocale().
    void testLocaleVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex("42 65 6e"),
                MAPPER.writeValueAsBytes(new Locale("en")));
        assertArrayEquals(VPackWireFixtureTest.hex("45 65 73 2d 45 53"),
                MAPPER.writeValueAsBytes(new Locale("es", "ES")));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "55 66 69 2d 46 49 2d 78 2d 6c 76 61 72 69 61 6e 74 2d 73 61 76 6f"),
                MAPPER.writeValueAsBytes(new Locale("FI", "fi", "savo")));
        assertArrayEquals(VPackWireFixtureTest.hex("45 65 6e 2d 55 53"),
                MAPPER.writeValueAsBytes(Locale.US));
        assertArrayEquals(VPackWireFixtureTest.hex("40"),
                MAPPER.writeValueAsBytes(Locale.ROOT));
    }

    // Provenance: JDKTypeSerializationTest#testNonStandardProperties().
    void testNonStandardPropertiesVpack() throws Exception {
        Properties properties = new Properties();
        properties.put("key", 1);
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 09 01 43 6b 65 79 31 03"),
                MAPPER.writeValueAsBytes(properties));
    }

    // Provenance: JDKTypeSerializationTest#testPolymorphicReferenceListOf().
    void testPolymorphicReferenceListOfVpack() throws Exception {
        byte[] expected = VPackWireFixtureTest.hex(
                "0b 23 01 48 73 74 72 61 74 65 67 79"
              + "02 16 0b 14 02 44 74 79 70 65 43 46 6f 6f"
              + "43 66 6f 6f 28 2a 0c 03 03");
        assertArrayEquals(expected, MAPPER.writeValueAsBytes(new ContainerB()));
    }
static class InetAddressBean {
        public InetAddress value;
        InetAddressBean(InetAddress value) { this.value = value; }
    }
static class UpperCasingSerializer extends StdScalarSerializer<String> {
        UpperCasingSerializer() { super(String.class); }

        @Override
        public void serialize(String value, JsonGenerator generator,
                SerializationContext provider) {
            generator.writeString(value.toUpperCase(Locale.ROOT));
        }
    }
static class UCStringWrapper {
        @JsonSerialize(contentUsing = UpperCasingSerializer.class)
        public AtomicReference<String> value;
        UCStringWrapper(String value) { this.value = new AtomicReference<>(value); }
    }
static class ContainerB {
        public AtomicReference<java.util.List<Strategy>> strategy;
        { strategy = new AtomicReference<>(java.util.List.of(new Foo(42))); }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY,
            property = "type")
    @JsonSubTypes({ @JsonSubTypes.Type(name = "Foo", value = Foo.class) })
    interface Strategy { }
static class Foo implements Strategy {
        public int foo;
        Foo(@JsonProperty("foo") int foo) { this.foo = foo; }
    }

    void __invoke_testCurrencyVpack() throws Exception {
        try {
            testCurrencyVpack();
        } finally {
        }
    }


    void __invoke_testCustomSerializerVpack() throws Exception {
        try {
            testCustomSerializerVpack();
        } finally {
        }
    }


    void __invoke_testDuplicatedByteBufferWithCustomPositionVpack() throws Exception {
        try {
            testDuplicatedByteBufferWithCustomPositionVpack();
        } finally {
        }
    }


    void __invoke_testDuplicatedByteBufferWithCustomPositionDirectVpack() throws Exception {
        try {
            testDuplicatedByteBufferWithCustomPositionDirectVpack();
        } finally {
        }
    }


    void __invoke_testFileVpack() throws Exception {
        try {
            testFileVpack();
        } finally {
        }
    }


    void __invoke_testFromArrayVpack() throws Exception {
        try {
            testFromArrayVpack();
        } finally {
        }
    }


    void __invoke_testFromMapVpack() throws Exception {
        try {
            testFromMapVpack();
        } finally {
        }
    }


    void __invoke_testInetAddressVpack() throws Exception {
        try {
            testInetAddressVpack();
        } finally {
        }
    }


    void __invoke_testInetSocketAddressVpack() throws Exception {
        try {
            testInetSocketAddressVpack();
        } finally {
        }
    }


    void __invoke_testLocaleVpack() throws Exception {
        try {
            testLocaleVpack();
        } finally {
        }
    }


    void __invoke_testNonStandardPropertiesVpack() throws Exception {
        try {
            testNonStandardPropertiesVpack();
        } finally {
        }
    }


    void __invoke_testPolymorphicReferenceListOfVpack() throws Exception {
        try {
            testPolymorphicReferenceListOfVpack();
        } finally {
        }
    }

}
