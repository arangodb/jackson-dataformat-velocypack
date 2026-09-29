package tools.jackson.dataformat.velocypack;

import java.lang.reflect.Field;

import org.junit.jupiter.api.Test;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.exc.StreamReadException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VPackArrayIndexTest {
    private final VPackFactory factory = new VPackFactory();

    @Test
    void rejectsWrongIndexAndMiddleOfChildBeforeEnd() throws Exception {
        assertRejected(new byte[] { 0x06, 0x07, 0x02, 0x31, 0x32, 0x04, 0x04 });
        assertRejected(new byte[] { 0x06, 0x07, 0x02, 0x31, 0x32, 0x03, 0x03 });
    }

    @Test
    void rejectsCountMismatchAndBodyLeftovers() throws Exception {
        assertRejected(new byte[] { 0x06, 0x06, 0x02, 0x31, 0x03, 0x04 });
        assertRejected(new byte[] { 0x06, 0x08, 0x02, 0x31, 0x32, 0x33, 0x04, 0x05 });
    }

    @Test
    void observedStartsGrowAfterEntriesInsteadOfDeclaredCount() throws Exception {
        try (VPackParser parser = (VPackParser) factory.createParser(indexedValues(17))) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertNull(observedStarts(parser));
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            int capacity = observedStarts(parser).length;
            org.junit.jupiter.api.Assertions.assertTrue(capacity < 17,
                    "observed-start capacity must not be derived from the declared count");
        }
    }

    private static long[] observedStarts(VPackParser parser) throws Exception {
        Field frames = VPackParser.class.getDeclaredField("_arrayFrames");
        frames.setAccessible(true);
        Object frame = ((java.util.ArrayDeque<?>) frames.get(parser)).peek();
        Field starts = frame.getClass().getDeclaredField("observedStarts");
        starts.setAccessible(true);
        return (long[]) starts.get(frame);
    }

    private static byte[] indexedValues(int count) {
        int bodyStart = 3;
        int indexStart = bodyStart + count;
        byte[] result = new byte[indexStart + count];
        result[0] = 0x06;
        result[1] = (byte) result.length;
        result[2] = (byte) count;
        for (int i = 0; i < count; ++i) {
            result[bodyStart + i] = 0x31;
            result[indexStart + i] = (byte) (bodyStart + i);
        }
        return result;
    }

    private void assertRejected(byte[] input) throws Exception {
        try (JsonParser parser = factory.createParser(input)) {
            assertThrows(StreamReadException.class, () -> {
                while (parser.nextToken() != null) { }
            });
        }
    }
}
