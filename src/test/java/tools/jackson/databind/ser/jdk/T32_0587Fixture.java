package tools.jackson.databind.ser.jdk;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.core.type.TypeReference;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0587Fixture {
private static final ObjectMapper MAPPER = new VPackMapper();

    // Provenance: JDKTypeSerializationTest#testClass().
    void testClassVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "50 6a 61 76 61 2e 6c 61 6e 67 2e 53 74 72 69 6e 67"),
                MAPPER.writeValueAsBytes(String.class));
        assertArrayEquals(VPackWireFixtureTest.hex("43 69 6e 74"),
                MAPPER.writeValueAsBytes(Integer.TYPE));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "47 62 6f 6f 6c 65 61 6e"),
                MAPPER.writeValueAsBytes(Boolean.TYPE));
        assertArrayEquals(VPackWireFixtureTest.hex("44 76 6f 69 64"),
                MAPPER.writeValueAsBytes(Void.TYPE));
    }

    // Provenance: JDKTypeSerializationTest#testCharset().
    void testCharsetVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex("45 55 54 46 2d 38"),
                MAPPER.writeValueAsBytes(Charset.forName("UTF-8")));
    }

    // Provenance: JDKTypeSerializationTest#testCharSequenceSerialization().
    void testCharSequenceSerializationVpack() throws Exception {
        AppId appId = AppId.valueOf("3074457345618296002");
        assertArrayEquals(VPackWireFixtureTest.hex(
                "53 33 30 37 34 34 35 37 33 34 35 36 31 38 32 39 36 30 30 32"),
                MAPPER.writeValueAsBytes(appId));
    }

    // Provenance: JDKTypeSerializationTest#testByteBuffer().
    void testByteBufferVpack() throws Exception {
        byte[] input = { 1, 2, 3, 4, 5 };
        byte[] expected = VPackWireFixtureTest.hex("c0 05 01 02 03 04 05");
        assertArrayEquals(expected, MAPPER.writeValueAsBytes(ByteBuffer.wrap(input)));

        ByteBuffer direct = ByteBuffer.allocateDirect(input.length);
        direct.put(input).flip();
        assertArrayEquals(expected, MAPPER.writeValueAsBytes(direct));
    }

    // Provenance: JDKTypeSerializationTest#testByteArrayOutputStreamSerialization().
    void testByteArrayOutputStreamSerializationVpack() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        output.write(new byte[] { 1, 11, 111 });
        assertArrayEquals(VPackWireFixtureTest.hex("c0 03 01 0b 6f"),
                MAPPER.writeValueAsBytes(output));
    }

    // Provenance: JDKTypeSerializationTest#testBasicUUIDs().
    void testBasicUUIDsVpack() throws Exception {
        String[] values = {
                "76e6d183-5f68-4afa-b94a-922c1fdb83f8",
                "540a88d1-e2d8-4fb1-9396-9212280d0a7f",
                "2c9e441d-1cd0-472d-9bab-69838f877574",
                "591b2869-146e-41d7-8048-e8131f1fdec5",
                "82994ac2-7b23-49f2-8cc5-e24cf6ed77be",
                "00000007-0000-0000-0000-000000000000"
        };
        for (String value : values) {
            UUID uuid = UUID.fromString(value);
            assertEquals(uuid, MAPPER.readValue(MAPPER.writeValueAsBytes(uuid), UUID.class));
            assertEquals(value, MAPPER.convertValue(uuid, String.class));
        }

        String template = "00000000-0000-0000-0000-000000000000";
        for (char digit : "123456789abcdef".toCharArray()) {
            UUID uuid = UUID.fromString(template.replace('0', digit));
            assertEquals(uuid, MAPPER.readValue(MAPPER.writeValueAsBytes(uuid), UUID.class));
        }

        assertArrayEquals(VPackWireFixtureTest.hex(
                "c0 10 54 0a 88 d1 e2 d8 4f b1 93 96 92 12 28 0d 0a 7f"),
                MAPPER.writeValueAsBytes(UUID.fromString(values[1])));
    }

    // Provenance: JDKTypeSerializationTest#testAtomicBoolean().
    void testAtomicBooleanVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex("1a"),
                MAPPER.writeValueAsBytes(new AtomicBoolean(true)));
        assertArrayEquals(VPackWireFixtureTest.hex("19"),
                MAPPER.writeValueAsBytes(new AtomicBoolean(false)));
    }

    // Provenance: JDKTypeSerializationTest#testAtomicInteger().
    void testAtomicIntegerVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex("31"),
                MAPPER.writeValueAsBytes(new AtomicInteger(1)));
        assertArrayEquals(VPackWireFixtureTest.hex("20 f7"),
                MAPPER.writeValueAsBytes(new AtomicInteger(-9)));
    }

    // Provenance: JDKTypeSerializationTest#testAtomicLong().
    void testAtomicLongVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex("30"),
                MAPPER.writeValueAsBytes(new AtomicLong(0)));
    }

    // Provenance: JDKTypeSerializationTest#testAtomicReference().
    void testAtomicReferenceVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex("02 06 43 61 62 63"),
                MAPPER.writeValueAsBytes(new AtomicReference<>(new String[] { "abc" })));
    }

    // Provenance: JDKTypeSerializationTest#testContextualAtomicReference().
    void testContextualAtomicReferenceVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .defaultDateFormat(utcDateFormat("yyyy/MM/dd"))
                .build();
        ContextualOptionals input = new ContextualOptionals();
        input.date = new AtomicReference<>(new java.util.Date(0L));
        input.date1 = new AtomicReference<>(new java.util.Date(0L));
        input.date2 = new AtomicReference<>(new java.util.Date(0L));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 38 03"
              + "45 64 61 74 65 31 4a 31 39 37 30 2b 30 31 2b 30 31"
              + "45 64 61 74 65 32 4a 31 39 37 30 2a 30 31 2a 30 31"
              + "44 64 61 74 65 4a 31 39 37 30 2f 30 31 2f 30 31"
              + "25 03 14"),
                mapper.writeValueAsBytes(input));
    }
private static SimpleDateFormat utcDateFormat(String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format;
    }

    // Provenance: JDKTypeSerializationTest#testAtomicReferenceWithSubtypeProperties().
    void testAtomicReferenceWithSubtypePropertiesVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 14 02 44 74 79 70 65 43 46 6f 6f"
              + "43 66 6f 6f 28 63 0c 03"),
                MAPPER.writerFor(new TypeReference<AtomicReference<Strategy>>() { })
                        .writeValueAsBytes(new AtomicReference<>(new Foo(99))));
    }
static final class AppId implements CharSequence {
        private final long value;

        private AppId(long value) { this.value = value; }

        static AppId valueOf(String value) { return new AppId(Long.parseLong(value)); }

        @Override public int length() { return toString().length(); }
        @Override public char charAt(int index) { return toString().charAt(index); }
        @Override public CharSequence subSequence(int start, int end) {
            return toString().subSequence(start, end);
        }
        @Override public String toString() { return Long.toString(value); }
    }
@JsonPropertyOrder({ "date1", "date2", "date" })
    static class ContextualOptionals {
        public AtomicReference<java.util.Date> date;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy+MM+dd")
        public AtomicReference<java.util.Date> date1;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy*MM*dd")
        public AtomicReference<java.util.Date> date2;
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY,
            property = "type")
    @JsonSubTypes({ @JsonSubTypes.Type(name = "Foo", value = Foo.class) })
    interface Strategy { }
static class Foo implements Strategy {
        public int foo;

        Foo(@JsonProperty("foo") int foo) { this.foo = foo; }
    }

    void __invoke_testClassVpack() throws Exception {
        try {
            testClassVpack();
        } finally {
        }
    }


    void __invoke_testCharsetVpack() throws Exception {
        try {
            testCharsetVpack();
        } finally {
        }
    }


    void __invoke_testCharSequenceSerializationVpack() throws Exception {
        try {
            testCharSequenceSerializationVpack();
        } finally {
        }
    }


    void __invoke_testByteBufferVpack() throws Exception {
        try {
            testByteBufferVpack();
        } finally {
        }
    }


    void __invoke_testByteArrayOutputStreamSerializationVpack() throws Exception {
        try {
            testByteArrayOutputStreamSerializationVpack();
        } finally {
        }
    }


    void __invoke_testBasicUUIDsVpack() throws Exception {
        try {
            testBasicUUIDsVpack();
        } finally {
        }
    }


    void __invoke_testAtomicBooleanVpack() throws Exception {
        try {
            testAtomicBooleanVpack();
        } finally {
        }
    }


    void __invoke_testAtomicIntegerVpack() throws Exception {
        try {
            testAtomicIntegerVpack();
        } finally {
        }
    }


    void __invoke_testAtomicLongVpack() throws Exception {
        try {
            testAtomicLongVpack();
        } finally {
        }
    }


    void __invoke_testAtomicReferenceVpack() throws Exception {
        try {
            testAtomicReferenceVpack();
        } finally {
        }
    }


    void __invoke_testContextualAtomicReferenceVpack() throws Exception {
        try {
            testContextualAtomicReferenceVpack();
        } finally {
        }
    }


    void __invoke_testAtomicReferenceWithSubtypePropertiesVpack() throws Exception {
        try {
            testAtomicReferenceWithSubtypePropertiesVpack();
        } finally {
        }
    }

}
