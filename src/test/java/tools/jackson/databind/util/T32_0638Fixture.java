package tools.jackson.databind.util;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.Map;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.exc.StreamWriteException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.node.ObjectNode;
import tools.jackson.databind.ser.std.StdSerializer;
import tools.jackson.databind.util.TokenBuffer;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0638Fixture {
private final VPackFactory factory = new VPackFactory();
private final ObjectMapper mapper = new VPackMapper();

    void streamStringFromCustomSerializerConvertsToMapThroughVpack() {
        Map<String, String> map = mapper.convertValue(new ReaderStringBean(),
                new TypeReference<Map<String, String>>() { });
        assertEquals(Map.of("field", "foobar"), map);
    }

    void writeTreeNullRetainsNullTokenAndVpackNullMarker() throws Exception {
        TokenBuffer buffer = TokenBuffer.forGeneration();
        buffer.writeTree(null);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonParser source = buffer.asParser();
                JsonGenerator generator = factory.createGenerator(output)) {
            assertEquals(JsonToken.VALUE_NULL, source.nextToken());
            generator.copyCurrentEvent(source);
            assertNull(source.nextToken());
        }
        assertArrayEquals(new byte[] { 0x18 }, output.toByteArray());
        buffer.close();
    }

    void treeWithoutWriteContextRemainsEmbeddedAndHasNoVpackWireForm() throws Exception {
        TokenBuffer buffer = new TokenBuffer(null, false);
        ObjectNode node = mapper.createObjectNode();
        node.put("x", 42);
        buffer.writeTree(node);
        try (JsonParser source = buffer.asParser()) {
            assertEquals(JsonToken.VALUE_EMBEDDED_OBJECT, source.nextToken());
            assertSame(node, source.getEmbeddedObject());
        }

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        JsonGenerator generator = factory.createGenerator(output);
        try (JsonParser source = buffer.asParser()) {
            assertEquals(JsonToken.VALUE_EMBEDDED_OBJECT, source.nextToken());
            assertThrows(StreamWriteException.class,
                    () -> generator.copyCurrentEvent(source));
        } finally {
            assertThrows(StreamWriteException.class, generator::close);
            buffer.close();
        }
    }
@JsonSerialize(using = ReaderStringSerializer.class)
    static class ReaderStringBean { }
static class ReaderStringSerializer extends StdSerializer<ReaderStringBean> {
        ReaderStringSerializer() {
            super(ReaderStringBean.class);
        }

        @Override
        public void serialize(ReaderStringBean value, JsonGenerator generator,
                SerializationContext context) {
            generator.writeStartObject();
            generator.writeName("field");
            generator.writeString(new StringReader("foobar"), 6);
            generator.writeEndObject();
        }
    }

    void __invoke_streamStringFromCustomSerializerConvertsToMapThroughVpack() throws Exception {
        try {
            streamStringFromCustomSerializerConvertsToMapThroughVpack();
        } finally {
        }
    }


    void __invoke_writeTreeNullRetainsNullTokenAndVpackNullMarker() throws Exception {
        try {
            writeTreeNullRetainsNullTokenAndVpackNullMarker();
        } finally {
        }
    }


    void __invoke_treeWithoutWriteContextRemainsEmbeddedAndHasNoVpackWireForm() throws Exception {
        try {
            treeWithoutWriteContextRemainsEmbeddedAndHasNoVpackWireForm();
        } finally {
        }
    }

}
