package tools.jackson.databind.ser.jdk;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonFormat;
import tools.jackson.core.JsonEncoding;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0589Fixture {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final UUID NULL_UUID = UUID.fromString(
            "00000000-0000-0000-0000-000000000000");

    // Provenance: JDKTypeSerializationTest#testRegexps().
    void testRegexpsVpack() throws Exception {
        final String pattern = "\\s+([a-b]+)\\w?";
        Map<String, Object> input = new LinkedHashMap<>();
        input.put("p", Pattern.compile(pattern));

        byte[] expected = VPackWireFixtureTest.hex(
                "0b 15 01 41 70 4e 5c 73 2b 28 5b 61 2d 62 5d 2b 29 5c 77 3f 03");
        assertArrayEquals(expected, MAPPER.writeValueAsBytes(input));
        Map<?, ?> result = MAPPER.readValue(expected, Map.class);
        assertEquals(pattern, result.get("p"));
    }

    // Provenance: JDKTypeSerializationTest#testSlicedByteBuffer().
    void testSlicedByteBufferVpack() throws Exception {
        ByteBuffer buffer = ByteBuffer.wrap(new byte[] { 1, 2, 3, 4, 5 });
        buffer.position(2);
        ByteBuffer slice = buffer.slice();

        assertArrayEquals(VPackWireFixtureTest.hex("c0 03 03 04 05"),
                MAPPER.writeValueAsBytes(slice));
        slice.position(1);
        assertArrayEquals(VPackWireFixtureTest.hex("c0 02 04 05"),
                MAPPER.writeValueAsBytes(slice));
    }

    // Provenance: JDKTypeSerializationTest#testThreadSerialization().
    void testThreadSerializationVpack() throws Exception {
        Map<?, ?> result = MAPPER.convertValue(Thread.currentThread(), Map.class);
        Map<?, ?> classLoader = (Map<?, ?>) result.get("contextClassLoader");
        assertInstanceOf(Map.class, classLoader);
        assertEquals(0, classLoader.size());
    }

    // Provenance: JDKTypeSerializationTest#testShapeOverrides().
    void testShapeOverridesVpack() throws Exception {
        byte[] expected = VPackWireFixtureTest.hex(
                "0b 1b 01 44 75 75 69 64 c0 10 00 00 00 00 00 00 00 00"
              + "00 00 00 00 00 00 00 00 03");
        assertArrayEquals(expected, MAPPER.writeValueAsBytes(new UUIDWrapperBinary(NULL_UUID)));
        assertArrayEquals(expected, MAPPER.writeValueAsBytes(new UUIDWrapperVanilla(NULL_UUID)));

        ObjectMapper binaryByType = VPackMapper.builder()
                .withConfigOverride(UUID.class, cfg -> cfg.setFormat(
                        JsonFormat.Value.forShape(JsonFormat.Shape.BINARY)))
                .build();
        assertArrayEquals(expected,
                binaryByType.writeValueAsBytes(new UUIDWrapperVanilla(NULL_UUID)));
    }

    // Provenance: JDKTypeSerializationTest#testTreeConversion().
    void testTreeConversionVpack() throws Exception {
        byte[] expected = VPackWireFixtureTest.hex(
                "64 30 30 30 30 30 30 30 30 2d 30 30 30 30 2d 30 30 30 30"
              + "2d 30 30 30 30 2d 30 30 30 30 30 30 30 30 30 30 30 30");
        JsonNode node = MAPPER.valueToTree(NULL_UUID);
        assertEquals(MAPPER.readTree(expected), node);
        assertEquals(NULL_UUID.toString(), node.asString());
        assertEquals(String.class, MAPPER.convertValue(NULL_UUID, Object.class).getClass());
    }

    // Provenance: JDKTypeSerializationTest#testSerialization5323Mapper1().
    void testSerialization5323Mapper1WriterTargetUnsupportedVpack() {
        assertThrows(UnsupportedOperationException.class,
                () -> MAPPER.createGenerator(new StringWriter()));
    }

    // Provenance: JDKTypeSerializationTest#testSerialization5323Mapper2().
    void testSerialization5323Mapper2Vpack() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        write5323(MAPPER.createGenerator(output));
        assertArrayEquals(uuidObjectFixture(), output.toByteArray());
    }

    // Provenance: JDKTypeSerializationTest#testSerialization5323Mapper2b().
    void testSerialization5323Mapper2bVpack() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        write5323(MAPPER.createGenerator(output, JsonEncoding.UTF8));
        assertArrayEquals(uuidObjectFixture(), output.toByteArray());
    }

    // Provenance: JDKTypeSerializationTest#testSerialization5323ObjectWriter1().
    void testSerialization5323ObjectWriter1WriterTargetUnsupportedVpack() {
        ObjectWriter writer = MAPPER.writer();
        assertThrows(UnsupportedOperationException.class,
                () -> writer.createGenerator(new StringWriter()));
    }

    // Provenance: JDKTypeSerializationTest#testSerialization5323ObjectWriter2().
    void testSerialization5323ObjectWriter2Vpack() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        write5323(MAPPER.writer().createGenerator(output));
        assertArrayEquals(uuidObjectFixture(), output.toByteArray());
    }

    // Provenance: JDKTypeSerializationTest#testSerialization5323ObjectWriter2b().
    void testSerialization5323ObjectWriter2bVpack() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        write5323(MAPPER.writer().createGenerator(output, JsonEncoding.UTF8));
        assertArrayEquals(uuidObjectFixture(), output.toByteArray());
    }

    // Provenance: JDKTypeSerializationTest#testPolymorphicReferenceSimple().
    void testPolymorphicReferenceSimpleVpack() throws Exception {
        byte[] expected = VPackWireFixtureTest.hex(
                "0b 21 01 48 73 74 72 61 74 65 67 79"
              + "0b 14 02 44 74 79 70 65 43 46 6f 6f 43 66 6f 6f 28 2a 0c 03 03");
        assertArrayEquals(expected, MAPPER.writeValueAsBytes(new ContainerA()));
    }
private static void write5323(JsonGenerator generator) throws Exception {
        generator.writeStartObject();
        generator.writePOJOProperty("id", NULL_UUID);
        generator.writeEndObject();
        generator.close();
    }
private static byte[] uuidObjectFixture() {
        return VPackWireFixtureTest.hex(
                "0b 19 01 42 69 64 c0 10 00 00 00 00 00 00 00 00"
              + "00 00 00 00 00 00 00 00 03");
    }
static class UUIDWrapperVanilla {
        public UUID uuid;
        UUIDWrapperVanilla(UUID uuid) { this.uuid = uuid; }
    }
static class UUIDWrapperBinary {
        @JsonFormat(shape = JsonFormat.Shape.BINARY)
        public UUID uuid;
        UUIDWrapperBinary(UUID uuid) { this.uuid = uuid; }
    }
static class ContainerA {
        public AtomicReference<Strategy> strategy =
                new AtomicReference<>((Strategy) new Foo(42));
    }
@com.fasterxml.jackson.annotation.JsonTypeInfo(
            use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME,
            include = com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY,
            property = "type")
    @com.fasterxml.jackson.annotation.JsonSubTypes({
            @com.fasterxml.jackson.annotation.JsonSubTypes.Type(name = "Foo", value = Foo.class)
    })
    interface Strategy { }
static class Foo implements Strategy {
        public int foo;
        Foo(@com.fasterxml.jackson.annotation.JsonProperty("foo") int foo) {
            this.foo = foo;
        }
    }

    void __invoke_testRegexpsVpack() throws Exception {
        try {
            testRegexpsVpack();
        } finally {
        }
    }


    void __invoke_testSlicedByteBufferVpack() throws Exception {
        try {
            testSlicedByteBufferVpack();
        } finally {
        }
    }


    void __invoke_testThreadSerializationVpack() throws Exception {
        try {
            testThreadSerializationVpack();
        } finally {
        }
    }


    void __invoke_testShapeOverridesVpack() throws Exception {
        try {
            testShapeOverridesVpack();
        } finally {
        }
    }


    void __invoke_testTreeConversionVpack() throws Exception {
        try {
            testTreeConversionVpack();
        } finally {
        }
    }


    void __invoke_testSerialization5323Mapper1WriterTargetUnsupportedVpack() throws Exception {
        try {
            testSerialization5323Mapper1WriterTargetUnsupportedVpack();
        } finally {
        }
    }


    void __invoke_testSerialization5323Mapper2Vpack() throws Exception {
        try {
            testSerialization5323Mapper2Vpack();
        } finally {
        }
    }


    void __invoke_testSerialization5323Mapper2bVpack() throws Exception {
        try {
            testSerialization5323Mapper2bVpack();
        } finally {
        }
    }


    void __invoke_testSerialization5323ObjectWriter1WriterTargetUnsupportedVpack() throws Exception {
        try {
            testSerialization5323ObjectWriter1WriterTargetUnsupportedVpack();
        } finally {
        }
    }


    void __invoke_testSerialization5323ObjectWriter2Vpack() throws Exception {
        try {
            testSerialization5323ObjectWriter2Vpack();
        } finally {
        }
    }


    void __invoke_testSerialization5323ObjectWriter2bVpack() throws Exception {
        try {
            testSerialization5323ObjectWriter2bVpack();
        } finally {
        }
    }


    void __invoke_testPolymorphicReferenceSimpleVpack() throws Exception {
        try {
            testPolymorphicReferenceSimpleVpack();
        } finally {
        }
    }

}
