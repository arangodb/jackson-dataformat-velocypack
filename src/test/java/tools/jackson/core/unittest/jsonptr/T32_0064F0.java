package tools.jackson.core.unittest.jsonptr;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonPointer;
import tools.jackson.core.JsonToken;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class T32_0064F0 {
private static final JsonPointer EMPTY_PTR = JsonPointer.empty();

    void simplePath() {
        final String input = "/Image/15/name";

        JsonPointer ptr = JsonPointer.compile(input);
        assertFalse(ptr.matches());
        assertEquals(-1, ptr.getMatchingIndex());
        assertEquals("Image", ptr.getMatchingProperty());
        assertEquals("/Image/15", ptr.head().toString());
        assertEquals(input, ptr.toString());

        ptr = ptr.tail();
        assertNotNull(ptr);
        assertFalse(ptr.matches());
        assertEquals(15, ptr.getMatchingIndex());
        assertEquals("15", ptr.getMatchingProperty());
        assertEquals("/15/name", ptr.toString());
        assertEquals("/15", ptr.head().toString());

        assertEquals("", ptr.head().head().toString());
        assertNull(ptr.head().head().head());

        ptr = ptr.tail();
        assertNotNull(ptr);
        assertFalse(ptr.matches());
        assertEquals(-1, ptr.getMatchingIndex());
        assertEquals("name", ptr.getMatchingProperty());
        assertEquals("/name", ptr.toString());
        assertEquals("", ptr.head().toString());
        assertSame(EMPTY_PTR, ptr.head());

        ptr = ptr.tail();
        assertTrue(ptr.matches());
        assertNull(ptr.tail());
        assertNull(ptr.head());
        assertNull(ptr.getMatchingProperty());
        assertEquals(-1, ptr.getMatchingIndex());
    }

    void simplePathLonger() {
        final String input = "/a/b/c/d/e/f/0";
        JsonPointer ptr = JsonPointer.compile(input);
        assertFalse(ptr.matches());
        assertEquals(-1, ptr.getMatchingIndex());
        assertEquals("a", ptr.getMatchingProperty());
        assertEquals("/a/b/c/d/e/f", ptr.head().toString());
        assertEquals("/b/c/d/e/f/0", ptr.tail().toString());
        assertEquals("/0", ptr.last().toString());
        assertEquals(input, ptr.toString());
    }

    void simpleTail() {
        JsonPointer ptr = JsonPointer.compile("/root/leaf");

        assertEquals("/leaf", ptr.tail().toString());
        assertEquals("", ptr.tail().tail().toString());
    }

    void wonkyNumber173() {
        JsonPointer ptr = JsonPointer.compile("/1e0");
        assertFalse(ptr.matches());
    }

    void properties() {
        assertTrue(JsonPointer.compile("/foo").mayMatchProperty());
        assertFalse(JsonPointer.compile("/foo").mayMatchElement());
        assertTrue(JsonPointer.compile("/12").mayMatchElement());
        assertTrue(JsonPointer.compile("/12").mayMatchProperty());
    }

    void quotedPath() {
        final String input = "/w~1out/til~0de/~1ab";

        JsonPointer ptr = JsonPointer.compile(input);
        assertFalse(ptr.matches());
        assertEquals(-1, ptr.getMatchingIndex());
        assertEquals("w/out", ptr.getMatchingProperty());
        assertEquals("/w~1out/til~0de", ptr.head().toString());
        assertEquals(input, ptr.toString());

        ptr = ptr.tail();
        assertNotNull(ptr);
        assertFalse(ptr.matches());
        assertEquals(-1, ptr.getMatchingIndex());
        assertEquals("til~de", ptr.getMatchingProperty());
        assertEquals("/til~0de", ptr.head().toString());
        assertEquals("/til~0de/~1ab", ptr.toString());

        ptr = ptr.tail();
        assertNotNull(ptr);
        assertFalse(ptr.matches());
        assertEquals(-1, ptr.getMatchingIndex());
        assertEquals("/ab", ptr.getMatchingProperty());
        assertEquals("/~1ab", ptr.toString());
        assertEquals("", ptr.head().toString());

        ptr = ptr.tail();
        assertTrue(ptr.matches());
        assertNull(ptr.tail());
    }
private static void assertTokenAndPath(JsonParser parser, JsonToken token,
            String path) throws Exception {
        assertEquals(token, parser.nextToken());
        assertEquals(path, parser.streamReadContext().pathAsPointer(true).toString());
    }

    void __invoke_simplePath() throws Exception {
        try {
            simplePath();
        } finally {
        }
    }


    void __invoke_simplePathLonger() throws Exception {
        try {
            simplePathLonger();
        } finally {
        }
    }


    void __invoke_simpleTail() throws Exception {
        try {
            simpleTail();
        } finally {
        }
    }


    void __invoke_wonkyNumber173() throws Exception {
        try {
            wonkyNumber173();
        } finally {
        }
    }


    void __invoke_properties() throws Exception {
        try {
            properties();
        } finally {
        }
    }


    void __invoke_quotedPath() throws Exception {
        try {
            quotedPath();
        } finally {
        }
    }

}
