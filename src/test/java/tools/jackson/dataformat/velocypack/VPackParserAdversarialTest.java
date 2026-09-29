package tools.jackson.dataformat.velocypack;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.time.Duration;
import java.util.Arrays;
import java.lang.reflect.Field;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.StreamReadConstraints;
import tools.jackson.core.exc.StreamConstraintsException;
import tools.jackson.core.exc.StreamReadException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Bounded hostile-wire checks for parser structure and resource boundaries. */
@Timeout(15)
class VPackParserAdversarialTest {
    private final VPackFactory factory = new VPackFactory();

    @Test
    void enormousDeclaredLengthsAndCountsFailBeforeUncontrolledAllocation() {
        byte[] longLength = new byte[] {
                (byte) 0xBF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 0x7F
        };
        byte[] compactLength = new byte[] {
                0x13, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 0x7F
        };
        for (byte[] input : new byte[][] { longLength, compactLength }) {
            for (int source = 0; source < 3; ++source) {
                assertJacksonFailure(input, source);
            }
        }

        for (int width : new int[] { 1, 2, 4, 8 }) {
            int indexedArrayMarker = 0x06 + widthIndex(width);
            int indexedObjectMarker = 0x0B + widthIndex(width);
            assertJacksonFailure(declaredCount(indexedArrayMarker, width, false), 0);
            assertJacksonFailure(declaredCount(indexedObjectMarker, width, true), 0);
        }
        assertJacksonFailure(new byte[] {
                0x13, 0x0A, 0x7F, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF
        }, 0);
    }

    @Test
    void deeplyNestedContainersAreIterativeAndDepthBounded() {
        byte[] nested = new byte[] { 0x01 };
        for (int i = 0; i < 450; ++i) {
            nested = compactArray(nested);
        }
        assertTimeoutAndComplete(nested);

        VPackFactory shallow = VPackFactory.builder()
                .streamReadConstraints(StreamReadConstraints.builder()
                        .maxNestingDepth(8).build())
                .build();
        try (JsonParser parser = shallow.createParser(nested)) {
            assertThrows(StreamConstraintsException.class, () -> {
                while (parser.nextToken() != null) { }
            });
        }
    }

    @Test
    void rootEntryBudgetIncludesNestedEntriesAndChecksBeforeRetention() {
        byte[] inner = new byte[] { 0x02, 0x03, 0x31 };
        byte[] outer = new byte[] { 0x02, 0x05, inner[0], inner[1], inner[2] };
        VPackFactory oneEntry = VPackFactory.builder()
                .vpackReadConstraints(VPackReadConstraints.builder()
                        .maxRootEntries(1).build())
                .build();
        try (JsonParser parser = oneEntry.createParser(outer)) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertThrows(StreamConstraintsException.class, parser::nextToken);
        }
    }

    @Test
    void unindexedFramesDoNotRetainIndexValidationTables() throws Exception {
        byte[] equal = new byte[2 + 32];
        equal[0] = 0x02;
        equal[1] = (byte) equal.length;
        Arrays.fill(equal, 2, equal.length, (byte) 0x31);
        try (VPackParser parser = (VPackParser) factory.createParser(equal)) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertNullField(parser, "observedStarts");
        }

        byte[] compact = new byte[1 + 1 + 32 + 1];
        compact[0] = 0x13;
        compact[1] = (byte) compact.length;
        Arrays.fill(compact, 2, compact.length - 1, (byte) 0x31);
        compact[compact.length - 1] = 32;
        try (VPackParser parser = (VPackParser) factory.createParser(compact)) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertNullField(parser, "observedStarts");
        }

        byte[] object = new byte[1 + 1 + 32 * 3 + 1];
        object[0] = 0x14;
        object[1] = (byte) object.length;
        for (int i = 0; i < 32; ++i) {
            int offset = 2 + i * 3;
            object[offset] = 0x41;
            object[offset + 1] = 'a';
            object[offset + 2] = 0x31;
        }
        object[object.length - 1] = 32;
        try (VPackParser parser = (VPackParser) factory.createParser(object)) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertNullField(parser, "keyStarts");
        }
    }

    @Test
    void skippedChildrenReceiveTheSameIndexAndScalarValidation() {
        byte[] badArray = new byte[] { 0x06, 0x05, 0x01, 0x31, 0x04 };
        byte[] outerArray = new byte[] { 0x02, 0x07,
                badArray[0], badArray[1], badArray[2], badArray[3], badArray[4] };
        assertRejectedByTraversal(outerArray);
        assertRejectedBySkip(outerArray);

        byte[] invalidString = new byte[] { 0x02, 0x04, 0x41, (byte) 0xFF };
        assertRejectedByTraversal(invalidString);
        assertRejectedBySkip(invalidString);

        byte[] body = VPackObjectParserTest.body(
                VPackObjectParserTest.pair("a", new byte[] { 0x31 }),
                VPackObjectParserTest.pair("b", new byte[] { 0x32 }));
        byte[] badObject = VPackObjectParserTest.object(1, false, body,
                new long[] { 3, 3 });
        byte[] outerObject = new byte[2 + badObject.length];
        outerObject[0] = 0x02;
        outerObject[1] = (byte) outerObject.length;
        System.arraycopy(badObject, 0, outerObject, 2, badObject.length);
        assertRejectedBySkip(outerObject);

        byte[] invalidBcd = new byte[] {
                (byte) 0xC8, 1, 0, 0, 0, 0, (byte) 0xFA
        };
        byte[] outerBcd = new byte[2 + invalidBcd.length];
        outerBcd[0] = 0x02;
        outerBcd[1] = (byte) outerBcd.length;
        System.arraycopy(invalidBcd, 0, outerBcd, 2, invalidBcd.length);
        assertRejectedByTraversal(outerBcd);
        assertRejectedBySkip(outerBcd);
    }

    @Test
    void resolvedAttributeNameByteBudgetIsCheckedBeforeEncoding() {
        byte[] body = VPackObjectParserTest.body(
                new byte[] { 0x30 }, new byte[] { 0x31 });
        byte[] input = VPackObjectParserTest.object(1, true, body, new long[] { 3 });
        VPackFactory constrained = VPackFactory.builder()
                .attributeNameCodec(new VPackAttributeNameCodec() {
                    @Override public String decode(java.math.BigInteger id) { return "é"; }
                    @Override public java.math.BigInteger encode(String name) { return null; }
                })
                .vpackReadConstraints(VPackReadConstraints.builder()
                        .maxRootNameBytes(1L).build())
                .build();
        try (JsonParser parser = constrained.createParser(input)) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertThrows(StreamConstraintsException.class, parser::nextToken);
        }
    }

    @Test
    void duplicatePolicyAndObjectIndexBijectionRemainSeparate() {
        byte[] body = VPackObjectParserTest.body(
                VPackObjectParserTest.pair("a", new byte[] { 0x31 }),
                VPackObjectParserTest.pair("a", new byte[] { 0x32 }));
        byte[] duplicate = VPackObjectParserTest.object(1, true, body,
                new long[] { 6, 3 });
        try (JsonParser parser = factory.createParser(duplicate)) {
            while (parser.nextToken() != null) { }
        }
        VPackFactory strict = VPackFactory.builder()
                .enable(tools.jackson.core.StreamReadFeature.STRICT_DUPLICATE_DETECTION)
                .build();
        try (JsonParser parser = strict.createParser(duplicate)) {
            assertThrows(StreamReadException.class, () -> {
                while (parser.nextToken() != null) { }
            });
        }

        byte[] omitted = VPackObjectParserTest.object(1, false, body,
                new long[] { 3, 8 });
        assertRejectedByTraversal(omitted);
    }

    private void assertTimeoutAndComplete(byte[] input) {
        org.junit.jupiter.api.Assertions.assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
            try (JsonParser parser = factory.createParser(input)) {
                int tokens = 0;
                while (parser.nextToken() != null) {
                    assertTrue(++tokens < 4096, "deep fixture made insufficient progress");
                }
            }
        });
    }

    private void assertRejectedByTraversal(byte[] input) {
        try (JsonParser parser = factory.createParser(input)) {
            assertThrows(StreamReadException.class, () -> {
                while (parser.nextToken() != null) { }
            });
        }
    }

    private void assertRejectedBySkip(byte[] input) {
        try (JsonParser parser = factory.createParser(input)) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertThrows(StreamReadException.class, parser::skipChildren);
        }
    }

    private static void assertNullField(VPackParser parser, String name) throws Exception {
        Field frames = VPackParser.class.getDeclaredField("_arrayFrames");
        frames.setAccessible(true);
        Object frame = ((java.util.ArrayDeque<?>) frames.get(parser)).peek();
        Field field = frame.getClass().getDeclaredField(name);
        field.setAccessible(true);
        assertTrue(field.get(frame) == null, name + " must not be retained for an unindexed frame");
    }

    private void assertJacksonFailure(byte[] input, int source) {
        try (JsonParser parser = parser(input, source)) {
            RuntimeException failure = assertThrows(RuntimeException.class, () -> {
                while (parser.nextToken() != null) { }
            });
            assertTrue(failure instanceof JacksonException,
                    () -> "unchecked parser failure: " + failure);
        }
    }

    private JsonParser parser(byte[] input, int source) {
        return switch (source) {
        case 0 -> factory.createParser(input);
        case 1 -> factory.createParser(new ByteArrayInputStream(input));
        case 2 -> factory.createParser(ObjectReadContext.empty(),
                (DataInput) new DataInputStream(new ByteArrayInputStream(input)));
        default -> throw new AssertionError("source " + source);
        };
    }

    private static byte[] declaredCount(int marker, int width, boolean object) {
        int length = 1 + 2 * width;
        byte[] result = new byte[length];
        result[0] = (byte) marker;
        put(result, 1, width, length);
        int countOffset = marker == 0x09 || marker == 0x12 ? length - width : 1 + width;
        for (int i = 0; i < width; ++i) {
            result[countOffset + i] = (byte) (i == width - 1 ? 0x7F : 0xFF);
        }
        return result;
    }

    private static byte[] compactArray(byte[] child) {
        int lengthWidth = 1;
        int length;
        while (true) {
            length = 1 + lengthWidth + child.length + 1;
            int required = varintLength(length);
            if (required == lengthWidth) break;
            lengthWidth = required;
        }
        byte[] result = new byte[length];
        result[0] = 0x13;
        writeForward(result, 1, length);
        System.arraycopy(child, 0, result, 1 + lengthWidth, child.length);
        result[length - 1] = 1;
        return result;
    }

    private static int varintLength(long value) {
        int groups = 1;
        while ((value >>>= 7) != 0L) ++groups;
        return groups;
    }

    private static void writeForward(byte[] output, int offset, long value) {
        int at = offset;
        do {
            int group = (int) (value & 0x7F);
            value >>>= 7;
            output[at++] = (byte) (group | (value == 0L ? 0 : 0x80));
        } while (value != 0L);
    }

    private static int widthIndex(int width) {
        return switch (width) {
        case 1 -> 0;
        case 2 -> 1;
        case 4 -> 2;
        case 8 -> 3;
        default -> throw new AssertionError(width);
        };
    }

    private static void put(byte[] output, int offset, int width, long value) {
        for (int i = 0; i < width; ++i) output[offset + i] = (byte) (value >>> (8 * i));
    }
}
