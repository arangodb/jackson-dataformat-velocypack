package com.arangodb.jackson.dataformat.velocypack;

import org.junit.jupiter.api.Test;
import java.util.HexFormat;
import java.util.function.Consumer;
import static org.junit.jupiter.api.Assertions.*;
import static com.arangodb.jackson.dataformat.velocypack.WriterReferenceWire.*;
import static com.arangodb.jackson.dataformat.velocypack.VPackWriterDifferentialTest.DEFAULTS;

/** Independent bug-fix vectors: deliberately do not change or compare to the oracle. */
class VPackWriterTagRegressionTest {
    private static void corrected(String hex, Consumer<WriterReplay> calls) {
        assertArrayEquals(HexFormat.of().parseHex(hex), WriterReplay.encode(false, DEFAULTS, calls));
    }

    @Test void tagBeforeObjectScalarIsRetained() {
        corrected("14084161ee071801", w -> {
            w.g.writeStartObject(); w.g.writeName("a"); w.tag(7); w.g.writeNull(); w.g.writeEndObject();
        });
    }

    @Test void tagBeforeObjectContainerIsRetained() {
        corrected("14084161ee070101", w -> {
            w.g.writeStartObject(); w.g.writeName("a"); w.tag(7);
            w.g.writeStartArray(); w.g.writeEndArray(); w.g.writeEndObject();
        });
    }

    @Test void tagBeforeArrayContainerIsRetained() {
        corrected("1306ee070101", w -> {
            w.g.writeStartArray(); w.tag(7); w.g.writeStartArray(); w.g.writeEndArray(); w.g.writeEndArray();
        });
    }

    @Test void tagDoesNotDriftFromContainerToNextScalar() {
        corrected("130818ee07011a03", w -> {
            w.g.writeStartArray(); w.g.writeNull(); w.tag(7);
            w.g.writeStartArray(); w.g.writeEndArray(); w.g.writeBoolean(true); w.g.writeEndArray();
        });
    }

    @Test void tagsBelongToParentItemOrPairInEveryLayout() {
        byte[] first = HexFormat.of().parseHex("ef000100000000000001");
        byte[] second = HexFormat.of().parseHex("eeffee0718");
        byte[] third = HexFormat.of().parseHex("efffffffffffffffff1a");
        for (int mask = 0; mask < 16; ++mask) {
            byte[] array = (mask & 2) != 0 ? compact(false, 3, cat(first, second, third))
                    : indexed(false, false, first, second, third);
            assertArrayEquals(array, WriterReplay.encode(false, mask, w -> {
                w.g.writeStartArray(); w.tag(256); w.g.writeStartArray(); w.g.writeEndArray();
                w.tag(255); w.tag(7); w.g.writeNull(); w.tag(-1); w.g.writeBoolean(true); w.g.writeEndArray();
            }));
            byte[] z = cat(new byte[]{0x41, 0x7a}, first), a = cat(new byte[]{0x41, 0x61}, second);
            boolean sorted = (mask & 1) != 0;
            byte[][] pairs = sorted ? new byte[][]{a, z} : new byte[][]{z, a};
            byte[] object = (mask & 4) != 0 ? compact(true, 2, cat(pairs)) : indexed(true, sorted, pairs);
            assertArrayEquals(object, WriterReplay.encode(false, mask, w -> {
                w.g.writeStartObject(); w.g.writeName("z"); w.tag(256); w.g.writeStartArray(); w.g.writeEndArray();
                w.g.writeName("a"); w.tag(255); w.tag(7); w.g.writeNull(); w.g.writeEndObject();
            }));
        }
    }

    @Test void taggedPrimitiveArrayIsOneParentItem() {
        corrected("130bee0713053132020102", w -> {
            w.g.writeStartArray(); w.tag(7); w.g.writeArray(new int[]{1, 2}, 0, 2);
            w.g.writeStartArray(); w.g.writeEndArray(); w.g.writeEndArray();
        });
    }

    @Test void consecutiveTaggedEmptyAndNonemptyContainersWithOrdinarySiblings() {
        for (int mask = 0; mask < 16; ++mask) {
            byte[] arrayChild = (mask & 2) != 0 ? compact(false, 1, new byte[]{0x18})
                    : noIndex(new byte[]{0x18});
            byte[] objectChild = (mask & 4) != 0 ? compact(true, 1, new byte[]{0x41, 0x6e, 0x18})
                    : indexed(true, (mask & 1) != 0, new byte[]{0x41, 0x6e, 0x18});
            byte[][] items = {new byte[]{0x18}, new byte[]{(byte) 0xee, 7, 1},
                    cat(new byte[]{(byte) 0xee, 8}, arrayChild), new byte[]{(byte) 0xee, 9, 0x0a},
                    cat(new byte[]{(byte) 0xef, 0, 1, 0, 0, 0, 0, 0, 0}, objectChild), new byte[]{0x1a}};
            byte[] array = (mask & 2) != 0 ? compact(false, 6, cat(items)) : indexed(false, false, items);
            assertArrayEquals(cat(new byte[]{(byte) 0xee, 9}, array, new byte[]{0x18}),
                    WriterReplay.encode(false, mask, w -> {
                        w.tag(9); w.g.writeStartArray();
                        emitSiblings(w, false);
                        assertEquals(5, w.g.streamWriteContext().getCurrentIndex());
                        assertEquals(6, ((VPackGenerator) w.g)._frameCounts[0]);
                        w.g.writeEndArray(); w.g.writeNull();
                    }));
            String[] names = {"z", "d", "c", "b", "a", "y"};
            byte[][] pairs = new byte[6][];
            for (int i = 0; i < 6; ++i) pairs[i] = cat(new byte[]{0x41, (byte) names[i].charAt(0)}, items[i]);
            boolean sorted = (mask & 1) != 0;
            if (sorted) pairs = new byte[][]{pairs[4], pairs[3], pairs[2], pairs[1], pairs[5], pairs[0]};
            byte[] object = (mask & 4) != 0 ? compact(true, 6, cat(pairs)) : indexed(true, sorted, pairs);
            assertArrayEquals(object, WriterReplay.encode(false, mask, w -> {
                w.g.writeStartObject(); emitSiblings(w, true); w.g.writeEndObject();
            }));
        }
    }

    private static void emitSiblings(WriterReplay w, boolean object) {
        if (object) w.g.writeName("z");
        w.g.writeNull();
        if (object) w.g.writeName("d");
        w.tag(7); w.g.writeStartArray(); w.g.writeEndArray();
        if (object) w.g.writeName("c");
        w.tag(8); w.g.writeStartArray(); w.g.writeNull(); w.g.writeEndArray();
        if (object) w.g.writeName("b");
        w.tag(9); w.g.writeStartObject(); w.g.writeEndObject();
        if (object) w.g.writeName("a");
        w.tag(256); w.g.writeStartObject(); w.g.writeName("n"); w.g.writeNull(); w.g.writeEndObject();
        if (object) w.g.writeName("y");
        w.g.writeBoolean(true);
    }
}
