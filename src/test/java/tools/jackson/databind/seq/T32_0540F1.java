package tools.jackson.databind.seq;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeName;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.MappingIterator;
import tools.jackson.databind.SequenceWriter;
import tools.jackson.databind.SerializationFeature;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0540F1 {
private static final VPackMapper MAPPER = new VPackMapper();
private static final byte[] BEAN_A3 = {
            0x14, 0x06, 0x41, 0x61, 0x33, 0x01
    };
private static final byte[] BEAN_A27 = {
            0x14, 0x07, 0x41, 0x61, 0x28, 0x1B, 0x01
    };
private static final byte[] BEAN_A6 = {
            0x14, 0x06, 0x41, 0x61, 0x36, 0x01
    };
private static final byte[] BEAN_A_NEG7 = {
            0x14, 0x07, 0x41, 0x61, 0x20, (byte) 0xF9, 0x01
    };
private static final byte[] BEAN_A3_A27_ARRAY = {
            0x13, 0x10,
            0x14, 0x06, 0x41, 0x61, 0x36, 0x01,
            0x14, 0x07, 0x41, 0x61, 0x20, (byte) 0xF9, 0x01,
            0x02
    };
private static final byte[] ARRAY_ROOTS = {
            0x13, 0x04, 0x31, 0x01,
            0x13, 0x04, 0x33, 0x01
    };
private static final byte[] DECIMAL_ARRAY = {
            0x06, 0x3F, 0x02,
            0x0B, 0x19, 0x02, 0x44, 0x76, 0x61, 0x6C, 0x32, 0x35,
            0x44, 0x76, 0x61, 0x6C, 0x31, 0x1B,
            0x38, 0x32, (byte) 0x8F, (byte) 0xFC, (byte) 0xC1, (byte) 0xC0, (byte) 0xF3, 0x3F,
            0x09, 0x03,
            0x0B, 0x21, 0x02, 0x44, 0x76, 0x61, 0x6C, 0x32, 0x1B,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x1A, (byte) 0xC0,
            0x44, 0x76, 0x61, 0x6C, 0x31, 0x1B,
            0x1F, (byte) 0x85, (byte) 0xEB, 0x51, (byte) 0xB8, 0x1E, 0x09, 0x40,
            0x11, 0x03,
            0x03, 0x1C
    };

    void testSimpleNonArrayVpack() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (SequenceWriter writer = MAPPER.writer()
                .withRootValueSeparator((String) null).writeValues(output)) {
            writer.write(new Bean(13)).write(new Bean(-6))
                    .writeAll(new Bean[] { new Bean(3), new Bean(1) })
                    .writeAll(Arrays.asList(new Bean(5), new Bean(7)));
        }

        MappingIterator<Bean> valuesIterator = MAPPER.readerFor(Bean.class)
                .readValues(output.toByteArray());
        List<Bean> values = valuesIterator.readAll();
        assertEquals(List.of(new Bean(13), new Bean(-6), new Bean(3),
                new Bean(1), new Bean(5), new Bean(7)), values);
    }

    void testSimpleArrayVpack() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (SequenceWriter writer = MAPPER.writer().writeValuesAsArray(output)) {
            writer.write(new Bean(1)).write(new Bean(2))
                    .writeAll(new Bean[] { new Bean(-7), new Bean(2) });
        }
        assertEquals(List.of(new Bean(1), new Bean(2), new Bean(-7), new Bean(2)),
                MAPPER.readValue(output.toByteArray(), new TypeReference<List<Bean>>() { }));

        output.reset();
        try (SequenceWriter writer = MAPPER.writer().writeValuesAsArray(output)) {
            writer.write(new Bean(1)).write(null)
                    .writeAll((Iterable<Bean>) List.of(new Bean(3)));
        }
        assertEquals(Arrays.asList(new Bean(1), null, new Bean(3)),
                MAPPER.readValue(output.toByteArray(), new TypeReference<List<Bean>>() { }));
    }

    void testPolymorphicNonArrayWithoutTypeVpack() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (SequenceWriter writer = MAPPER.writer().withRootValueSeparator((String) null)
                .writeValues(output)) {
            writer.write(new ImplA(3)).write(new ImplA(4));
        }
        try (MappingIterator<PolyBase> it = MAPPER.readerFor(PolyBase.class)
                .readValues(output.toByteArray())) {
            assertEquals(3, ((ImplA) it.next()).value);
            assertEquals(4, ((ImplA) it.next()).value);
            assertFalse(it.hasNext());
        }
    }

    void testPolymorphicArrayWithoutTypeVpack() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (SequenceWriter writer = MAPPER.writer().writeValuesAsArray(output)) {
            writer.write(new ImplA(-1)).write(new ImplA(6));
        }
        List<PolyBase> values = MAPPER.readValue(output.toByteArray(),
                new TypeReference<List<PolyBase>>() { });
        assertEquals(2, values.size());
        assertEquals(-1, ((ImplA) values.get(0)).value);
        assertEquals(6, ((ImplA) values.get(1)).value);
    }

    void testPolymorphicArrayWithTypeVpack() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (SequenceWriter writer = MAPPER.writerFor(PolyBase.class)
                .writeValuesAsArray(output)) {
            writer.write(new ImplA(-1)).write(new ImplB(3)).write(new ImplA(7));
            writer.flush();
        }
        List<PolyBase> values = MAPPER.readValue(output.toByteArray(),
                new TypeReference<List<PolyBase>>() { });
        assertEquals(3, values.size());
        assertEquals(-1, ((ImplA) values.get(0)).value);
        assertEquals(3, ((ImplB) values.get(1)).b);
        assertEquals(7, ((ImplA) values.get(2)).value);
    }

    void testSimpleCloseableVpack() throws Exception {
        CloseableValue input = new CloseableValue();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (SequenceWriter writer = MAPPER.writer()
                .with(SerializationFeature.CLOSE_CLOSEABLE)
                .withRootValueSeparator((String) null).writeValues(output)) {
            writer.write(input);
            assertTrue(input.closed);
        }
        CloseableValue decoded = MAPPER.readValue(output.toByteArray(), CloseableValue.class);
        assertFalse(decoded.closed);
        assertEquals(0, decoded.x);
    }
private static byte[] concat(byte[]... values) {
        int length = 0;
        for (byte[] value : values) {
            length += value.length;
        }
        byte[] result = new byte[length];
        int offset = 0;
        for (byte[] value : values) {
            System.arraycopy(value, 0, result, offset, value.length);
            offset += value.length;
        }
        return result;
    }
static class Bean {
        public int a;

        Bean() { }

        Bean(int value) { a = value; }

        @Override
        public boolean equals(Object other) {
            return other instanceof Bean that && a == that.a;
        }

        @Override
        public int hashCode() { return a; }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY,
            property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = ImplA.class, name = "A"),
            @JsonSubTypes.Type(value = ImplB.class, name = "B")
    })
    static class PolyBase { }
@JsonTypeName("A")
    static class ImplA extends PolyBase {
        public int value;

        ImplA() { }

        ImplA(int value) { this.value = value; }
    }
@JsonTypeName("B")
    static class ImplB extends PolyBase {
        public int b;

        ImplB() { }

        ImplB(int value) { b = value; }
    }
static class CloseableValue implements Closeable {
        public int x;
        public boolean closed;

        @Override
        public void close() throws IOException {
            closed = true;
        }
    }

    void __invoke_testSimpleNonArrayVpack() throws Exception {
        try {
            testSimpleNonArrayVpack();
        } finally {
        }
    }


    void __invoke_testSimpleArrayVpack() throws Exception {
        try {
            testSimpleArrayVpack();
        } finally {
        }
    }


    void __invoke_testPolymorphicNonArrayWithoutTypeVpack() throws Exception {
        try {
            testPolymorphicNonArrayWithoutTypeVpack();
        } finally {
        }
    }


    void __invoke_testPolymorphicArrayWithoutTypeVpack() throws Exception {
        try {
            testPolymorphicArrayWithoutTypeVpack();
        } finally {
        }
    }


    void __invoke_testPolymorphicArrayWithTypeVpack() throws Exception {
        try {
            testPolymorphicArrayWithTypeVpack();
        } finally {
        }
    }


    void __invoke_testSimpleCloseableVpack() throws Exception {
        try {
            testSimpleCloseableVpack();
        } finally {
        }
    }

}
