package tools.jackson.core.unittest.jsonptr;

import tools.jackson.core.JsonPointer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class T32_0063F1 {
private static final JsonPointer EMPTY_PTR = JsonPointer.empty();
private static final int DEEP_ARRAY_DEPTH = 120_000;

    void iZeroIndex() {
        JsonPointer ptr = JsonPointer.compile("/0");
        assertEquals(0, ptr.getMatchingIndex());
        ptr = JsonPointer.compile("/00");
        assertEquals(-1, ptr.getMatchingIndex());
    }

    void last() {
        String input = "/Image/name";

        JsonPointer ptr = JsonPointer.compile(input);
        JsonPointer leaf = ptr.last();

        assertEquals("/name", leaf.toString());
        assertEquals("name", leaf.getMatchingProperty());

        input = "/Image/15/name";
        ptr = JsonPointer.compile(input);
        leaf = ptr.last();

        assertEquals("/name", leaf.toString());
        assertEquals("name", leaf.getMatchingProperty());
    }

    void emptyPointer() {
        assertSame(EMPTY_PTR, JsonPointer.compile(""));
        assertEquals("", EMPTY_PTR.toString());

        assertFalse(EMPTY_PTR.mayMatchProperty());
        assertFalse(EMPTY_PTR.mayMatchElement());
        assertEquals(-1, EMPTY_PTR.getMatchingIndex());
        assertNull(EMPTY_PTR.getMatchingProperty());
    }

    void pointerWithEmptyPropertyName() {
        JsonPointer ptr = JsonPointer.compile("/");
        assertNotNull(ptr);
        assertNotSame(EMPTY_PTR, ptr);

        assertEquals("/", ptr.toString());
        assertTrue(ptr.mayMatchProperty());
        assertFalse(ptr.mayMatchElement());
        assertEquals(-1, ptr.getMatchingIndex());
        assertEquals("", ptr.getMatchingProperty());
        assertTrue(ptr.matchesProperty(""));
        assertFalse(ptr.matchesElement(0));
        assertFalse(ptr.matchesElement(-1));
        assertFalse(ptr.matchesProperty("1"));
    }

    void equality() {
        assertNotEquals(JsonPointer.empty(), JsonPointer.compile("/"));

        assertEquals(JsonPointer.compile("/foo/3"), JsonPointer.compile("/foo/3"));
        assertNotEquals(JsonPointer.empty(), JsonPointer.compile("/12"));
        assertNotEquals(JsonPointer.compile("/12"), JsonPointer.empty());

        assertEquals(JsonPointer.compile("/a/b/c").tail(),
                JsonPointer.compile("/foo/b/c").tail());

        JsonPointer abcDef = JsonPointer.compile("/abc/def");
        JsonPointer def = JsonPointer.compile("/def");
        assertEquals(abcDef.tail(), def);
        assertEquals(def, abcDef.tail());

        assertNotEquals("/", JsonPointer.empty());
    }

    void append() {
        final String input = "/Image/15/name";
        final String append = "/extension";

        JsonPointer ptr = JsonPointer.compile(input);
        JsonPointer apd = JsonPointer.compile(append);
        JsonPointer appended = ptr.append(apd);

        assertEquals("extension", appended.last().getMatchingProperty());
        assertEquals("/Image/15/name/extension", appended.toString());
    }

    void appendIndex() {
        JsonPointer ptr = JsonPointer.compile("/Image/15/name");
        JsonPointer appended = ptr.appendIndex(12);

        assertEquals(12, appended.last().getMatchingIndex());
    }

    void appendProperty() {
        final String input = "/Image/15/name";
        final String appendNoSlash = "extension";
        final String appendWithSlash = "/extension~";

        JsonPointer ptr = JsonPointer.compile(input);
        JsonPointer appendedNoSlash = ptr.appendProperty(appendNoSlash);
        JsonPointer appendedWithSlash = ptr.appendProperty(appendWithSlash);

        assertEquals(appendNoSlash, appendedNoSlash.last().getMatchingProperty());
        assertEquals("/Image/15/name/extension", appendedNoSlash.toString());
        assertEquals(appendWithSlash, appendedWithSlash.last().getMatchingProperty());
        assertEquals("/Image/15/name/~1extension~0", appendedWithSlash.toString());
    }

    void appendPropertyEmpty() {
        final String base = "/Image/72/src";
        JsonPointer basePtr = JsonPointer.compile(base);

        assertSame(basePtr, basePtr.appendProperty(null));
        JsonPointer sub = basePtr.appendProperty("");
        assertNotSame(basePtr, sub);
        assertEquals(base + "/", sub.toString());
    }

    void appendWithFinalSlash() {
        final String input = "/Image/15/name/";
        final String append = "/extension";

        JsonPointer ptr = JsonPointer.compile(input);
        assertEquals(input, ptr.toString());

        JsonPointer appended = ptr.append(JsonPointer.compile(append));
        assertEquals("extension", appended.last().getMatchingProperty());
        assertEquals("/Image/15/name//extension", appended.toString());
    }

    void longNumbers() {
        final long longId = Integer.MAX_VALUE + 1L;
        final String input = "/User/" + longId;

        JsonPointer ptr = JsonPointer.compile(input);
        assertEquals("User", ptr.getMatchingProperty());
        assertEquals(input, ptr.toString());

        ptr = ptr.tail();
        assertNotNull(ptr);
        assertFalse(ptr.matches());
        assertEquals(-1, ptr.getMatchingIndex());
        assertEquals(String.valueOf(longId), ptr.getMatchingProperty());

        ptr = ptr.tail();
        assertTrue(ptr.matches());
        assertNull(ptr.tail());
    }
private static byte[] nestedEqualArrays(int depth) {
        int length = Math.addExact(Math.multiplyExact(depth, 9), 1);
        byte[] result = new byte[length];
        int offset = 0;
        int containerLength = length;
        for (int i = 0; i < depth; ++i) {
            result[offset++] = 0x05;
            putLittleEndian(result, offset, containerLength, 8);
            offset += 8;
            containerLength -= 9;
        }
        result[offset] = 0x31;
        return result;
    }
private static void putLittleEndian(byte[] target, int offset, long value, int width) {
        for (int i = 0; i < width; ++i) {
            target[offset + i] = (byte) (value >>> (8 * i));
        }
    }
private static String repeat(String value, int count) {
        StringBuilder result = new StringBuilder(value.length() * count);
        for (int i = 0; i < count; ++i) {
            result.append(value);
        }
        return result.toString();
    }

    void __invoke_iZeroIndex() throws Exception {
        try {
            iZeroIndex();
        } finally {
        }
    }


    void __invoke_last() throws Exception {
        try {
            last();
        } finally {
        }
    }


    void __invoke_emptyPointer() throws Exception {
        try {
            emptyPointer();
        } finally {
        }
    }


    void __invoke_pointerWithEmptyPropertyName() throws Exception {
        try {
            pointerWithEmptyPropertyName();
        } finally {
        }
    }


    void __invoke_equality() throws Exception {
        try {
            equality();
        } finally {
        }
    }


    void __invoke_append() throws Exception {
        try {
            append();
        } finally {
        }
    }


    void __invoke_appendIndex() throws Exception {
        try {
            appendIndex();
        } finally {
        }
    }


    void __invoke_appendProperty() throws Exception {
        try {
            appendProperty();
        } finally {
        }
    }


    void __invoke_appendPropertyEmpty() throws Exception {
        try {
            appendPropertyEmpty();
        } finally {
        }
    }


    void __invoke_appendWithFinalSlash() throws Exception {
        try {
            appendWithFinalSlash();
        } finally {
        }
    }


    void __invoke_longNumbers() throws Exception {
        try {
            longNumbers();
        } finally {
        }
    }

}
