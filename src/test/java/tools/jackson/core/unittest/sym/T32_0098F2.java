package tools.jackson.core.unittest.sym;

import java.util.List;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.sym.BinaryNameMatcher;
import tools.jackson.core.sym.ByteQuadsCanonicalizer;
import tools.jackson.core.util.Named;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0098F2 {
private final VPackFactory factory = new VPackFactory();

    void placeholderLookupsRemainEmptyAndNonCanonicalizing() {
        ByteQuadsCanonicalizer root = ByteQuadsCanonicalizer.createRoot();
        assertEquals(0, root.size());
        assertFalse(root.isCanonicalizing());

        ByteQuadsCanonicalizer placeholder = root.makeChildOrPlaceholder(0);
        assertEquals(-1, placeholder.size());
        assertFalse(placeholder.isCanonicalizing());

        int[] quads = BinaryNameMatcher._quads("abcd1234efgh5678");
        assertNull(placeholder.findName(quads[0]));
        assertNull(placeholder.findName(quads[0], quads[1]));
        assertNull(placeholder.findName(quads[0], quads[1], quads[2]));
        assertNull(placeholder.findName(quads, quads.length));
    }

    void placeholderRejectsAllNameAdditionWidthsAndCanRelease() {
        ByteQuadsCanonicalizer root = ByteQuadsCanonicalizer.createRoot();
        ByteQuadsCanonicalizer placeholder = root.makeChildOrPlaceholder(0);
        int[] quads = BinaryNameMatcher._quads("abcd1234efgh5678");

        assertPlaceholderAddRejected(() -> placeholder.addName(
                "abcd", placeholder.calcHash(quads[0])));
        assertPlaceholderAddRejected(() -> placeholder.addName(
                "abcd1234", placeholder.calcHash(quads[0], quads[1])));
        assertPlaceholderAddRejected(() -> placeholder.addName(
                "abcd1234efgh", placeholder.calcHash(quads[0], quads[1], quads[2])));
        assertPlaceholderAddRejected(() -> placeholder.addName(
                "abcd1234efgh5678", placeholder.calcHash(quads, quads.length)));

        assertEquals(-1, placeholder.size());
        assertFalse(placeholder.isCanonicalizing());
        placeholder.release();
        assertEquals(0, root.size());
        assertFalse(root.isCanonicalizing());
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

    void __invoke_placeholderLookupsRemainEmptyAndNonCanonicalizing() throws Exception {
        try {
            placeholderLookupsRemainEmptyAndNonCanonicalizing();
        } finally {
        }
    }


    void __invoke_placeholderRejectsAllNameAdditionWidthsAndCanRelease() throws Exception {
        try {
            placeholderRejectsAllNameAdditionWidthsAndCanRelease();
        } finally {
        }
    }

}
