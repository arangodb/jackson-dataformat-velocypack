package tools.jackson.core.unittest.jsonptr;

import tools.jackson.core.JsonPointer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class T32_0062F1 {
private static final int DEEP_HEAD_DEPTH = 10_000;

    void deepHeadRetainsAllButTheFinalSegmentAndSupportsTailTraversal() {
        final String input = repeat("/a", DEEP_HEAD_DEPTH);
        JsonPointer origPtr = JsonPointer.compile(input);
        JsonPointer head = origPtr.head();
        final String fullHead = head.toString();
        assertEquals(repeat("/a", DEEP_HEAD_DEPTH - 1), fullHead);

        // Also traverse the hierarchy to make sure every tail remains intact.
        for (JsonPointer current = head; current != JsonPointer.empty();
                current = current.tail()) {
            assertTrue(fullHead.endsWith(current.toString()));
        }
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

    void __invoke_deepHeadRetainsAllButTheFinalSegmentAndSupportsTailTraversal() throws Exception {
        try {
            deepHeadRetainsAllButTheFinalSegmentAndSupportsTailTraversal();
        } finally {
        }
    }

}
