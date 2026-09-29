package tools.jackson.core.unittest.util;

import tools.jackson.core.util.BufferRecycler;
import tools.jackson.core.util.TextBuffer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class T32_0115F1 {

    void appendTakingTwoAndThreeIntsVpack() throws Exception {
        BufferRecycler bufferRecycler = new BufferRecycler();
        TextBuffer textBuffer = new TextBuffer(bufferRecycler);
        textBuffer.ensureNotShared();
        char[] charArray = textBuffer.getTextBuffer();
        textBuffer.append(charArray, 0, 200);
        textBuffer.append("5rmk0rx(C@aVYGN@Q", 2, 3);

        assertEquals(3, textBuffer.getCurrentSegmentSize());
    }

    void emptyVpack() throws Exception {
        TextBuffer textBuffer = new TextBuffer(new BufferRecycler());
        textBuffer.resetWithEmpty();

        assertEquals(0, textBuffer.getTextBuffer().length);
        textBuffer.contentsAsString();
        assertEquals(0, textBuffer.getTextBuffer().length);
    }

    void ensureNotSharedAndResetWithStringVpack() throws Exception {
        BufferRecycler bufferRecycler = new BufferRecycler();
        TextBuffer textBuffer = new TextBuffer(bufferRecycler);
        textBuffer.resetWithString("");

        assertFalse(textBuffer.hasTextAsCharacters());

        textBuffer.ensureNotShared();

        assertEquals(0, textBuffer.getCurrentSegmentSize());
    }

    void expandVpack() throws Exception {
        TextBuffer textBuffer = new TextBuffer(new BufferRecycler());
        char[] buffer = textBuffer.getCurrentSegment();

        while (buffer.length < 500 * 1000) {
            char[] old = buffer;
            buffer = textBuffer.expandCurrentSegment();
            if (old.length >= buffer.length) {
                fail("Expected buffer of " + old.length
                        + " to expand, did not, length now " + buffer.length);
            }
        }
        textBuffer.resetWithString("Foobar");
        assertEquals("Foobar", textBuffer.contentsAsString());
    }

    void getCurrentSegmentVpack() throws Exception {
        TextBuffer textBuffer = new TextBuffer(null);
        textBuffer.emptyAndGetCurrentSegment();
        textBuffer.setCurrentAndReturn(500);
        textBuffer.getCurrentSegment();

        assertEquals(500, textBuffer.size());
    }

    void getCurrentSegmentSizeResetWithVpack() {
        TextBuffer textBuffer = new TextBuffer(null);
        textBuffer.resetWith('.');
        textBuffer.resetWith('q');

        assertEquals(1, textBuffer.getCurrentSegmentSize());
    }

    void getSizeFinishCurrentSegmentAndResetWithVpack() throws Exception {
        TextBuffer textBuffer = new TextBuffer(null);
        textBuffer.resetWith('.');
        textBuffer.finishCurrentSegment();
        textBuffer.resetWith('q');

        assertEquals(2, textBuffer.size());
    }

    void getTextBufferAndAppendTakingCharAndContentsAsArrayVpack() throws Exception {
        BufferRecycler bufferRecycler = new BufferRecycler();
        TextBuffer textBuffer = new TextBuffer(bufferRecycler);
        textBuffer.append('(');
        textBuffer.contentsAsArray();
        textBuffer.getTextBuffer();

        assertEquals(1, textBuffer.getCurrentSegmentSize());
    }

    void getTextBufferAndEmptyAndGetCurrentSegmentAndFinishCurrentSegmentVpack()
            throws Exception {
        BufferRecycler bufferRecycler = new BufferRecycler();
        TextBuffer textBuffer = new TextBuffer(bufferRecycler);
        textBuffer.emptyAndGetCurrentSegment();
        textBuffer.finishCurrentSegment();
        textBuffer.getTextBuffer();

        assertEquals(200, textBuffer.size());
    }

    void getTextBufferAndResetWithStringVpack() throws Exception {
        BufferRecycler bufferRecycler = new BufferRecycler();
        TextBuffer textBuffer = new TextBuffer(bufferRecycler);
        textBuffer.resetWithString("");

        assertFalse(textBuffer.hasTextAsCharacters());

        textBuffer.getTextBuffer();

        assertTrue(textBuffer.hasTextAsCharacters());
    }

    void longAppendVpack() throws Exception {
        final int len = TextBuffer.MAX_SEGMENT_LEN * 3 / 2;
        StringBuilder stringBuilder = new StringBuilder(len);
        for (int i = 0; i < len; ++i) {
            stringBuilder.append('x');
        }
        final String string = stringBuilder.toString();
        final String expected = "a" + string + "c";

        TextBuffer textBuffer = new TextBuffer(new BufferRecycler());
        textBuffer.append('a');
        textBuffer.append(string, 0, len);
        textBuffer.append('c');
        assertEquals(len + 2, textBuffer.size());
        assertEquals(expected, textBuffer.contentsAsString());

        textBuffer = new TextBuffer(new BufferRecycler());
        textBuffer.append('a');
        textBuffer.append(string.toCharArray(), 0, len);
        textBuffer.append('c');
        assertEquals(len + 2, textBuffer.size());
        assertEquals(expected, textBuffer.contentsAsString());
    }

    void __invoke_appendTakingTwoAndThreeIntsVpack() throws Exception {
        try {
            appendTakingTwoAndThreeIntsVpack();
        } finally {
        }
    }


    void __invoke_emptyVpack() throws Exception {
        try {
            emptyVpack();
        } finally {
        }
    }


    void __invoke_ensureNotSharedAndResetWithStringVpack() throws Exception {
        try {
            ensureNotSharedAndResetWithStringVpack();
        } finally {
        }
    }


    void __invoke_expandVpack() throws Exception {
        try {
            expandVpack();
        } finally {
        }
    }


    void __invoke_getCurrentSegmentVpack() throws Exception {
        try {
            getCurrentSegmentVpack();
        } finally {
        }
    }


    void __invoke_getCurrentSegmentSizeResetWithVpack() throws Exception {
        try {
            getCurrentSegmentSizeResetWithVpack();
        } finally {
        }
    }


    void __invoke_getSizeFinishCurrentSegmentAndResetWithVpack() throws Exception {
        try {
            getSizeFinishCurrentSegmentAndResetWithVpack();
        } finally {
        }
    }


    void __invoke_getTextBufferAndAppendTakingCharAndContentsAsArrayVpack() throws Exception {
        try {
            getTextBufferAndAppendTakingCharAndContentsAsArrayVpack();
        } finally {
        }
    }


    void __invoke_getTextBufferAndEmptyAndGetCurrentSegmentAndFinishCurrentSegmentVpack() throws Exception {
        try {
            getTextBufferAndEmptyAndGetCurrentSegmentAndFinishCurrentSegmentVpack();
        } finally {
        }
    }


    void __invoke_getTextBufferAndResetWithStringVpack() throws Exception {
        try {
            getTextBufferAndResetWithStringVpack();
        } finally {
        }
    }


    void __invoke_longAppendVpack() throws Exception {
        try {
            longAppendVpack();
        } finally {
        }
    }

}
