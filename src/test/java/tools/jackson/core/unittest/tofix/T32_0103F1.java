package tools.jackson.core.unittest.tofix;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.List;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.exc.StreamReadException;
import tools.jackson.core.sym.SimpleNameMatcher;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0103F1 {

    void malformedNestedDoubleIsRejectedFromByteArrayAndInputStream() {
        assertMalformedAfterArrayStart(MALFORMED_DOUBLE, 0);
        assertMalformedAfterArrayStart(MALFORMED_DOUBLE, 1);
    }

    void malformedNestedIntegerIsRejectedFromByteArrayAndInputStream() {
        assertMalformedAfterArrayStart(MALFORMED_INTEGER, 0);
        assertMalformedAfterArrayStart(MALFORMED_INTEGER, 1);
    }
private static final byte[] MALFORMED_DOUBLE = {
            0x02, 0x03, 0x1B
    };
private static final byte[] MALFORMED_INTEGER = {
            0x02, 0x03, 0x21
    };
private static void assertTextualDistribution(List<String> names,
            int expectedSecondary, int expectedSpills) {
        SimpleNameMatcher matcher = SimpleNameMatcher.construct(null, names);
        assertEquals(expectedSecondary, matcher.secondaryCount());
        assertEquals(expectedSpills, matcher.spillCount());
    }
private static List<String> generateSuffix(String base, int count) {
        List<String> result = new ArrayList<>(count);
        while (--count >= 0) {
            result.add((base + count).intern());
        }
        return result;
    }
private static void assertMalformedAfterArrayStart(byte[] input, int source) {
        try (JsonParser parser = parser(input, source)) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertThrows(StreamReadException.class, parser::nextToken);
        }
    }
private static JsonParser parser(byte[] input, int source) {
        VPackFactory factory = new VPackFactory();
        return switch (source) {
        case 0 -> factory.createParser(input);
        case 1 -> factory.createParser(new ByteArrayInputStream(input));
        case 2 -> factory.createParser(ObjectReadContext.empty(),
                (DataInput) new DataInputStream(new ByteArrayInputStream(input)));
        default -> throw new IllegalArgumentException("source " + source);
        };
    }

    void __invoke_malformedNestedDoubleIsRejectedFromByteArrayAndInputStream() throws Exception {
        try {
            malformedNestedDoubleIsRejectedFromByteArrayAndInputStream();
        } finally {
        }
    }


    void __invoke_malformedNestedIntegerIsRejectedFromByteArrayAndInputStream() throws Exception {
        try {
            malformedNestedIntegerIsRejectedFromByteArrayAndInputStream();
        } finally {
        }
    }

}
