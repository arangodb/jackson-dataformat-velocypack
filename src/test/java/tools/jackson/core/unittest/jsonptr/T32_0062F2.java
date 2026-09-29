package tools.jackson.core.unittest.jsonptr;

import tools.jackson.core.JsonPointer;
import static org.junit.jupiter.api.Assertions.assertEquals;

class T32_0062F2 {
private static final int DEEP_HEAD_DEPTH = 10_000;

    void basicHeadProgressionReachesEmptyPointer() {
        JsonPointer ptr = JsonPointer.compile("/a/b/0/qwerty");
        JsonPointer head = ptr.head();
        assertEquals("/a/b/0", head.toString());
        head = head.head();
        assertEquals("/a/b", head.toString());
        head = head.head();
        assertEquals("/a", head.toString());
        head = head.head();
        assertEquals("", head.toString());
    }

    void variationTailThenHeadProgressionReachesEmptyPointer() {
        JsonPointer ptr = JsonPointer.compile("/a/b/0/qwerty");
        JsonPointer tail = ptr.tail();

        assertEquals("/b/0/qwerty", tail.toString());
        JsonPointer head = tail.head();
        assertEquals("/b/0", head.toString());
        head = head.head();
        assertEquals("/b", head.toString());
        head = head.head();
        assertEquals("", head.toString());
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

    void __invoke_basicHeadProgressionReachesEmptyPointer() throws Exception {
        try {
            basicHeadProgressionReachesEmptyPointer();
        } finally {
        }
    }


    void __invoke_variationTailThenHeadProgressionReachesEmptyPointer() throws Exception {
        try {
            variationTailThenHeadProgressionReachesEmptyPointer();
        } finally {
        }
    }

}
