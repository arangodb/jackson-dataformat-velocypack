package tools.jackson.core.unittest.util;

import java.nio.charset.StandardCharsets;

import tools.jackson.core.util.BufferRecycler;
import tools.jackson.core.util.TextBuffer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class T32_0116F0 {

    void simpleVpack() throws Exception {
        TextBuffer textBuffer = new TextBuffer(new BufferRecycler());
        textBuffer.append('a');
        textBuffer.append(new char[] { 'X', 'b' }, 1, 1);
        textBuffer.append("c", 0, 1);

        assertTrue(textBuffer.hasTextAsCharacters());
        assertEquals(3, textBuffer.contentsAsArray().length);
        assertEquals("abc", textBuffer.toString());
        assertNotNull(textBuffer.expandCurrentSegment());
    }

    void longerVpack() throws Exception {
        TextBuffer textBuffer = new TextBuffer(null);
        for (int i = 0; i < 2000; ++i) {
            textBuffer.append("abc", 0, 3);
        }

        String value = textBuffer.contentsAsString();
        assertEquals(6000, value.length());
        assertEquals(6000, textBuffer.contentsAsArray().length);

        textBuffer.resetWithShared(new char[] { 'a' }, 0, 1);
        assertEquals(1, textBuffer.toString().length());
        assertTrue(textBuffer.hasTextAsCharacters());
    }

    void resetWithAndSetCurrentAndReturnVpack() throws Exception {
        TextBuffer textBuffer = new TextBuffer(null);
        textBuffer.resetWith('l');
        textBuffer.setCurrentAndReturn(349);
    }

    void resetWithAsciiBytesVpack() throws Exception {
        TextBuffer textBuffer = new TextBuffer(new BufferRecycler());

        assertEquals("abc", textBuffer.resetWithASCII(new byte[] { 'a', 'b', 'c' }, 0, 3));
        assertEquals(0, textBuffer.getTextOffset());
        assertEquals("abc", textBuffer.contentsAsString());
    }

    void resetWithStringVpack() throws Exception {
        TextBuffer textBuffer = new TextBuffer(new BufferRecycler());
        textBuffer.ensureNotShared();
        textBuffer.finishCurrentSegment();

        assertEquals(200, textBuffer.size());

        textBuffer.resetWithString("asdf");

        assertEquals(0, textBuffer.getTextOffset());
    }

    void resetWithUTF8BytesVpack() throws Exception {
        TextBuffer textBuffer = new TextBuffer(new BufferRecycler());
        final String value = "\u00E9\u00E8\u00E0";
        final byte[] bytes = value.getBytes(StandardCharsets.UTF_8);

        assertEquals(6, bytes.length);
        assertEquals(value, textBuffer.resetWithUTF8(bytes, 0, bytes.length));
        assertEquals(value, textBuffer.contentsAsString());
    }

    void __invoke_simpleVpack() throws Exception {
        try {
            simpleVpack();
        } finally {
        }
    }


    void __invoke_longerVpack() throws Exception {
        try {
            longerVpack();
        } finally {
        }
    }


    void __invoke_resetWithAndSetCurrentAndReturnVpack() throws Exception {
        try {
            resetWithAndSetCurrentAndReturnVpack();
        } finally {
        }
    }


    void __invoke_resetWithAsciiBytesVpack() throws Exception {
        try {
            resetWithAsciiBytesVpack();
        } finally {
        }
    }


    void __invoke_resetWithStringVpack() throws Exception {
        try {
            resetWithStringVpack();
        } finally {
        }
    }


    void __invoke_resetWithUTF8BytesVpack() throws Exception {
        try {
            resetWithUTF8BytesVpack();
        } finally {
        }
    }

}
