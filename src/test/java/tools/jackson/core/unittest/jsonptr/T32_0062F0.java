package tools.jackson.core.unittest.jsonptr;

import tools.jackson.core.JsonPointer;
import static org.junit.jupiter.api.Assertions.assertEquals;

class T32_0062F0 {
private static final int DEEP_HEAD_DEPTH = 10_000;

    void appendWithTailRetainsBothCompositionOrdersAndPropertyAppend() {
        JsonPointer original = JsonPointer.compile("/a1/b/c");
        JsonPointer tailPointer = original.tail();
        assertEquals("/b/c", tailPointer.toString());

        JsonPointer other = JsonPointer.compile("/a2");
        assertEquals("/a2", other.toString());

        assertEquals("/a2/b/c", other.append(tailPointer).toString());

        // And the other way around too
        assertEquals("/b/c/a2", tailPointer.append(other).toString());

        // And with appendProperty()
        assertEquals("/b/c/xyz", tailPointer.appendProperty("xyz").toString());
    }
private static String repeat(String part, int count) {
        StringBuilder sb = new StringBuilder(count * part.length());
        int index = 0;
        while (--count >= 0) {
            sb.append(part).append(index % 9);
            ++index;
        }
        return sb.toString();
    }

    void __invoke_appendWithTailRetainsBothCompositionOrdersAndPropertyAppend() throws Exception {
        try {
            appendWithTailRetainsBothCompositionOrdersAndPropertyAppend();
        } finally {
        }
    }

}
