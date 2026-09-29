package tools.jackson.core.unittest.io;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import tools.jackson.core.JsonGenerator;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0030F0 {

    void longCharSequenceUsesLiteralVpackPayload() throws Exception {
        StringBuilder input = new StringBuilder();
        for (int i = 0; i < 1111; ++i) {
            input.append('"');
        }

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = new VPackFactory().createGenerator(output)) {
            generator.writeString(input.toString());
        }

        // Independently assemble the long-string header and payload. VPack
        // stores quote characters literally; it does not add JSON escapes.
        byte[] expected = new byte[9 + 1111];
        expected[0] = (byte) 0xBF;
        expected[1] = 0x57;
        expected[2] = 0x04;
        Arrays.fill(expected, 9, expected.length, (byte) '"');
        assertArrayEquals(expected, output.toByteArray());
    }

    void __invoke_longCharSequenceUsesLiteralVpackPayload() throws Exception {
        try {
            longCharSequenceUsesLiteralVpackPayload();
        } finally {
        }
    }

}
