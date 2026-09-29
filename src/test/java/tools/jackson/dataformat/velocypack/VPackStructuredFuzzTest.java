package tools.jackson.dataformat.velocypack;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/** Fixed-seed bounded legal trees; wire assembly is deliberately test-local. */
@Timeout(20)
class VPackStructuredFuzzTest {
    private static final long SEED = 0x34_2026_0924L;
    private static final VPackFactory FACTORY = new VPackFactory();
    private static final JsonMapper JSON = JsonMapper.builder().build();
    private static final VPackMapper VPACK = VPackMapper.builder(FACTORY).build();

    @Test
    void fixedSeedIndependentTreesAgreeWithPortableJsonAndAllInputSources() throws Exception {
        Random random = new Random(SEED);
        for (int caseId = 0; caseId < 64; ++caseId) {
            Tree tree = tree(random, 0);
            byte[] wire = tree.wire();
            JsonNode expected = JSON.readTree(tree.json());
            assertEquals(expected, VPACK.readTree(wire), "seed=" + SEED + " case=" + caseId);
            for (int source = 0; source < 4; ++source) {
                assertEquals(tree.tokens(), tokens(parser(wire, source)),
                        "seed=" + SEED + " case=" + caseId + " source=" + source);
            }
        }
    }

    @Test
    void productionWriterRoundTripsAreKeptSeparateFromIndependentAssembly() throws Exception {
        byte[] literal = { 0x13, 0x06, 0x31, 0x28, 0x10, 0x02 };
        assertEquals(List.of(JsonToken.START_ARRAY, JsonToken.VALUE_NUMBER_INT,
                JsonToken.VALUE_NUMBER_INT, JsonToken.END_ARRAY), tokens(FACTORY.createParser(literal)));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (tools.jackson.core.JsonGenerator generator = FACTORY.createGenerator(out)) {
            generator.writeStartArray();
            generator.writeNumber(1);
            generator.writeNumber(16);
            generator.writeEndArray();
        }
        try (JsonParser parser = FACTORY.createParser(out.toByteArray())) {
            assertEquals(List.of(JsonToken.START_ARRAY, JsonToken.VALUE_NUMBER_INT,
                    JsonToken.VALUE_NUMBER_INT, JsonToken.END_ARRAY), tokens(parser));
        }
    }

    @Test
    void nativeExactCopyUsesPhysicalMarkerOracle() throws Exception {
        byte[] literal = { 0x1c, 1, 0, 0, 0, 0, 0, 0, 0, 0x1f };
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonParser parser = FACTORY.createParser(literal);
                VPackGenerator generator = (VPackGenerator) FACTORY.createGenerator(out)) {
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            generator.copyCurrentEventExact(parser);
            assertEquals(JsonToken.VALUE_EMBEDDED_OBJECT, parser.nextToken());
            generator.copyCurrentEvent(parser);
        }
        assertArrayEquals(literal, out.toByteArray());
    }

    private static JsonParser parser(byte[] bytes, int source) throws Exception {
        return switch (source) {
            case 0 -> FACTORY.createParser(bytes);
            case 1 -> FACTORY.createParser(wrap(bytes), 1, bytes.length);
            case 2 -> FACTORY.createParser(new ChoppyInput(bytes));
            default -> FACTORY.createParser(ObjectReadContext.empty(),
                    (java.io.DataInput) new DataInputStream(new ByteArrayInputStream(bytes)));
        };
    }

    private static byte[] wrap(byte[] bytes) {
        byte[] result = new byte[bytes.length + 2];
        result[0] = 0x55;
        System.arraycopy(bytes, 0, result, 1, bytes.length);
        result[result.length - 1] = 0x66;
        return result;
    }

    private static List<JsonToken> tokens(JsonParser parser) throws Exception {
        try (parser) {
            List<JsonToken> result = new ArrayList<>();
            JsonToken token;
            while ((token = parser.nextToken()) != null) {
                if (token == JsonToken.VALUE_NUMBER_INT) parser.getIntValue();
                result.add(token);
            }
            return result;
        }
    }

    private static Tree tree(Random random, int depth) {
        if (depth < 3 && random.nextInt(4) == 0) {
            int count = random.nextInt(5);
            List<Tree> children = new ArrayList<>();
            for (int i = 0; i < count; ++i) children.add(tree(random, depth + 1));
            return new Tree(children);
        }
        return new Tree(random.nextInt(5));
    }

    private static byte[] compactArray(List<Tree> children) {
        ByteArrayOutputStream body = new ByteArrayOutputStream();
        for (Tree child : children) body.writeBytes(child.wire());
        byte[] values = body.toByteArray();
        // Total length is at most 128 for these capped trees, so its encoded size is stable.
        int total = 1 + 1 + values.length + 1;
        total = 1 + varint(total).length + values.length + 1;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(0x13);
        out.writeBytes(varint(total));
        out.writeBytes(values);
        out.write(children.size()); // independent reverse count: one group under this bound
        return out.toByteArray();
    }

    private static byte[] varint(int value) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        do {
            int group = value & 0x7f;
            value >>>= 7;
            out.write(group | (value == 0 ? 0 : 0x80));
        } while (value != 0);
        return out.toByteArray();
    }

    private static final class Tree {
        final Integer scalar;
        final List<Tree> children;
        Tree(int value) { scalar = value; children = null; }
        Tree(List<Tree> values) { scalar = null; children = values; }
        byte[] wire() {
            if (children != null) return compactArray(children);
            if (scalar == 0) return new byte[] { 0x18 };
            if (scalar == 1) return new byte[] { 0x19 };
            if (scalar == 2) return new byte[] { 0x1a };
            if (scalar == 3) return new byte[] { 0x33 };
            return new byte[] { 0x41, 0x61 };
        }
        String json() {
            if (children == null) return scalar == 0 ? "null" : scalar == 1 ? "false" :
                    scalar == 2 ? "true" : scalar == 3 ? "3" : "\"a\"";
            List<String> values = new ArrayList<>();
            for (Tree child : children) values.add(child.json());
            return "[" + String.join(",", values) + "]";
        }
        List<JsonToken> tokens() {
            if (children == null) return List.of(scalar == 0 ? JsonToken.VALUE_NULL :
                    scalar == 1 ? JsonToken.VALUE_FALSE : scalar == 2 ? JsonToken.VALUE_TRUE :
                    scalar == 3 ? JsonToken.VALUE_NUMBER_INT : JsonToken.VALUE_STRING);
            List<JsonToken> result = new ArrayList<>();
            result.add(JsonToken.START_ARRAY);
            for (Tree child : children) result.addAll(child.tokens());
            result.add(JsonToken.END_ARRAY);
            return result;
        }
    }

    private static final class ChoppyInput extends InputStream {
        private final ByteArrayInputStream delegate;
        private boolean zero = true;
        ChoppyInput(byte[] input) { delegate = new ByteArrayInputStream(input); }
        @Override public int read() { return delegate.read(); }
        @Override public int read(byte[] b, int off, int len) {
            if (zero) { zero = false; return 0; }
            zero = true;
            return delegate.read(b, off, Math.min(1, len));
        }
    }
}
