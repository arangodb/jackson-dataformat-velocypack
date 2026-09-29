package tools.jackson.core.unittest.sym;

import java.util.List;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.sym.BinaryNameMatcher;
import tools.jackson.core.util.Named;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0098F0 {
private final VPackFactory factory = new VPackFactory();

    void smallMatcherMatchesEveryNameAndRejectsSuffixes() throws Exception {
        assertSmallMatching(new String[] { "single" }, new byte[] {
                0x14, 0x0B, 0x46, 's', 'i', 'n', 'g', 'l', 'e', 0x18, 0x01
        });
        assertSmallMatching(new String[] { "1", "2a" }, new byte[] {
                0x14, 0x0A, 0x41, '1', 0x18, 0x42, '2', 'a', 0x18, 0x02
        });
        assertSmallMatching(new String[] { "aaaabbbbcccc", "aaaabbbbcccc2" }, new byte[] {
                0x14, 0x20, 0x4C, 'a', 'a', 'a', 'a', 'b', 'b', 'b', 'b', 'c', 'c', 'c', 'c',
                0x18, 0x4D, 'a', 'a', 'a', 'a', 'b', 'b', 'b', 'b', 'c', 'c', 'c', 'c', '2',
                0x18, 0x02
        });
        assertSmallMatching(new String[] { "first", "secondlong", "3rd" }, new byte[] {
                0x14, 0x1B, 0x45, 'f', 'i', 'r', 's', 't', 0x18,
                0x4A, 's', 'e', 'c', 'o', 'n', 'd', 'l', 'o', 'n', 'g', 0x18,
                0x43, '3', 'r', 'd', 0x18, 0x03
        });
    }
private void assertSmallMatching(String[] names, byte[] document) throws Exception {
        List<Named> named = java.util.Arrays.stream(names)
                .map(String::intern)
                .map(Named::fromString)
                .toList();
        BinaryNameMatcher matcher = (BinaryNameMatcher) factory.constructNameMatcher(named, true);
        assertNotNull(matcher.toString());

        try (JsonParser parser = factory.createParser(document)) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            for (int i = 0; i < names.length; ++i) {
                assertEquals(i, parser.nextNameMatch(matcher));
                assertEquals(JsonToken.PROPERTY_NAME, parser.currentToken());
                assertEquals(i, parser.currentNameMatch(matcher));
                assertEquals(names[i], parser.currentName());
                assertEquals(JsonToken.VALUE_NULL, parser.nextToken());
                assertEquals(-1, matchByQuad(matcher, names[i] + "FOOBAR"));
                assertEquals(i, matcher.matchName(names[i]));
            }
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertNull(parser.nextToken());
        }
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
private static void assertPlaceholderAddRejected(Runnable addition) {
        IllegalStateException exception = assertThrows(IllegalStateException.class, addition::run);
        assertTrue(exception.getMessage().contains("Cannot add names to Placeholder"));
    }

    void __invoke_smallMatcherMatchesEveryNameAndRejectsSuffixes() throws Exception {
        try {
            smallMatcherMatchesEveryNameAndRejectsSuffixes();
        } finally {
        }
    }

}
