package tools.jackson.core.unittest.sym;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.sym.BinaryNameMatcher;
import tools.jackson.core.util.Named;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0097F0 {
private final VPackFactory factory = new VPackFactory();

    void suffix1HashDistribution() {
        assertHashDistribution(generateSuffix("", 99), 77, 16, 6, 0);
    }

    void suffix2HashDistribution() {
        assertHashDistribution(generateSuffix("base", 39), 33, 6, 0, 0);
    }

    void suffix3HashDistribution() {
        assertHashDistribution(generateSuffix("Of ", 139), 122, 16, 1, 0);
    }

    void suffix4HashDistribution() {
        assertHashDistribution(generateSuffix("ACE-", 499), 422, 66, 11, 0);
    }

    void suffix5HashDistribution() {
        assertHashDistribution(generateSuffix("SlartiBartFast#", 3000), 1112, 761, 897, 230);
    }

    void prefix1HashDistribution() {
        assertHashDistribution(generatePrefix("", 99), 77, 16, 6, 0);
    }

    void prefix2HashDistribution() {
        assertHashDistribution(generatePrefix("base", 39), 29, 8, 2, 0);
    }

    void prefix3HashDistribution() {
        assertHashDistribution(generatePrefix("Of ", 139), 116, 16, 7, 0);
    }

    void prefix4HashDistribution() {
        assertHashDistribution(generatePrefix("ACE-", 499), 384, 92, 23, 0);
    }

    void misc11HashDistribution() {
        assertHashDistribution(Arrays.asList(
                "player", "uri", "title", "width", "height", "format",
                "duration", "size", "bitrate", "copyright", "persons"),
                11, 0, 0, 0);
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

    void __invoke_suffix1HashDistribution() throws Exception {
        try {
            suffix1HashDistribution();
        } finally {
        }
    }


    void __invoke_suffix2HashDistribution() throws Exception {
        try {
            suffix2HashDistribution();
        } finally {
        }
    }


    void __invoke_suffix3HashDistribution() throws Exception {
        try {
            suffix3HashDistribution();
        } finally {
        }
    }


    void __invoke_suffix4HashDistribution() throws Exception {
        try {
            suffix4HashDistribution();
        } finally {
        }
    }


    void __invoke_suffix5HashDistribution() throws Exception {
        try {
            suffix5HashDistribution();
        } finally {
        }
    }


    void __invoke_prefix1HashDistribution() throws Exception {
        try {
            prefix1HashDistribution();
        } finally {
        }
    }


    void __invoke_prefix2HashDistribution() throws Exception {
        try {
            prefix2HashDistribution();
        } finally {
        }
    }


    void __invoke_prefix3HashDistribution() throws Exception {
        try {
            prefix3HashDistribution();
        } finally {
        }
    }


    void __invoke_prefix4HashDistribution() throws Exception {
        try {
            prefix4HashDistribution();
        } finally {
        }
    }


    void __invoke_misc11HashDistribution() throws Exception {
        try {
            misc11HashDistribution();
        } finally {
        }
    }

}
