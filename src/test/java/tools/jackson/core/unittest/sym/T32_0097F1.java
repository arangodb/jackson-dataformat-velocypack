package tools.jackson.core.unittest.sym;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.sym.BinaryNameMatcher;
import tools.jackson.core.util.Named;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0097F1 {
private final VPackFactory factory = new VPackFactory();

    void mediumMatcherMatchesEveryNameAndRejectsSuffixes() throws Exception {
        assertMatching(List.of("a", "bcd", "Fittipaldi", "goober"));
        assertMatching(List.of("foo", "bar", "foobar", "fubar", "bizzbah", "grimagnoefwemp"));
        assertMatching(List.of("a", "b", "c", "d", "E", "f", "G", "h"));
        assertMatching(List.of("a", "b", "d", "E", "f", "G"));
        assertMatching(List.of("areaNames", "audienceSubCategoryNames", "blockNames",
                "seatCategoryNames", "subTopicNames", "subjectNames", "topicNames",
                "topicSubTopics", "venueNames", "events", "performances"));
    }

    void largeMatcherMatchesEveryNameAndRejectsSuffixes() throws Exception {
        assertMatching(generateSuffix("base", 39));
        assertMatching(generateSuffix("Of ", 139));
        assertMatching(generateSuffix("ACE-", 499));
        assertMatching(generatePrefix("-longer", 999));
        assertMatching(generateSuffix("slartibartfast-", 3000));
    }
private void assertHashDistribution(List<String> names, int primary, int secondary,
            int tertiary, int spillover) {
        BinaryNameMatcher matcher = construct(names, false);
        assertEquals(names.size(), matcher.totalCount());
        assertEquals(primary, matcher.primaryQuadCount(), "Primary count not matching");
        assertEquals(secondary, matcher.secondaryQuadCount(), "Secondary count not matching");
        assertEquals(tertiary, matcher.tertiaryQuadCount(), "Tertiary count not matching");
        assertEquals(spillover, matcher.spilloverQuadCount(), "Spill count not matching");
    }
private void assertMatching(List<String> names) throws Exception {
        BinaryNameMatcher matcher = construct(names, true);
        byte[] document = compactObject(names);
        try (JsonParser parser = factory.createParser(document)) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            for (int i = 0; i < names.size(); ++i) {
                String name = names.get(i);
                assertEquals(i, parser.nextNameMatch(matcher));
                assertEquals(JsonToken.PROPERTY_NAME, parser.currentToken());
                assertEquals(i, parser.currentNameMatch(matcher));
                assertEquals(name, parser.currentName());
                assertEquals(JsonToken.VALUE_NULL, parser.nextToken());
                assertEquals(-1, matchByQuad(matcher, name + "FOOBAR"));
            }
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }
private BinaryNameMatcher construct(List<String> names, boolean alreadyInterned) {
        List<Named> named = names.stream().map(Named::fromString).toList();
        return (BinaryNameMatcher) factory.constructNameMatcher(named, alreadyInterned);
    }
private static int matchByQuad(BinaryNameMatcher matcher, String name) {
        int[] quads = BinaryNameMatcher._quads(name);
        return switch (quads.length) {
        case 1 -> matcher.matchByQuad(quads[0]);
        case 2 -> matcher.matchByQuad(quads[0], quads[1]);
        case 3 -> matcher.matchByQuad(quads[0], quads[1], quads[2]);
        default -> matcher.matchByQuad(quads, quads.length);
        };
    }
private static List<String> generateSuffix(String base, int count) {
        List<String> result = new ArrayList<>(count);
        while (--count >= 0) {
            result.add((base + count).intern());
        }
        return result;
    }
private static List<String> generatePrefix(String base, int count) {
        List<String> result = new ArrayList<>(count);
        while (--count >= 0) {
            result.add((count + base).intern());
        }
        return result;
    }
private static byte[] compactObject(List<String> names) {
        ByteArrayOutputStream body = new ByteArrayOutputStream();
        for (String name : names) {
            writeString(body, name.getBytes(StandardCharsets.UTF_8));
            body.write(0x18); // null value
        }

        int bodyLength = body.size();
        int countLength = varintLength(names.size());
        int length = 1 + 1 + bodyLength + countLength;
        while (true) {
            int adjusted = 1 + varintLength(length) + bodyLength + countLength;
            if (adjusted == length) {
                break;
            }
            length = adjusted;
        }

        byte[] result = new byte[length];
        result[0] = 0x14;
        int headerLength = writeForward(result, 1, length);
        byte[] bodyBytes = body.toByteArray();
        System.arraycopy(bodyBytes, 0, result, 1 + headerLength, bodyBytes.length);
        writeReverse(result, 1 + headerLength + bodyBytes.length, names.size());
        return result;
    }
private static void writeString(ByteArrayOutputStream output, byte[] value) {
        if (value.length <= 126) {
            output.write(0x40 + value.length);
        } else {
            output.write(0xBF);
            long length = value.length;
            for (int i = 0; i < 8; ++i) {
                output.write((int) (length & 0xFF));
                length >>>= 8;
            }
        }
        output.writeBytes(value);
    }
private static int varintLength(int value) {
        int length = 1;
        while ((value >>>= 7) != 0) {
            ++length;
        }
        return length;
    }
private static int writeForward(byte[] output, int offset, int value) {
        int at = offset;
        do {
            int group = value & 0x7F;
            value >>>= 7;
            output[at++] = (byte) (group | (value == 0 ? 0 : 0x80));
        } while (value != 0);
        return at - offset;
    }
private static void writeReverse(byte[] output, int offset, int value) {
        int at = offset;
        do {
            int group = value & 0x7F;
            value >>>= 7;
            output[at++] = (byte) (group | (value == 0 ? 0 : 0x80));
        } while (value != 0);
        for (int left = offset, right = at - 1; left < right; ++left, --right) {
            byte swap = output[left];
            output[left] = output[right];
            output[right] = swap;
        }
    }

    void __invoke_mediumMatcherMatchesEveryNameAndRejectsSuffixes() throws Exception {
        try {
            mediumMatcherMatchesEveryNameAndRejectsSuffixes();
        } finally {
        }
    }


    void __invoke_largeMatcherMatchesEveryNameAndRejectsSuffixes() throws Exception {
        try {
            largeMatcherMatchesEveryNameAndRejectsSuffixes();
        } finally {
        }
    }

}
