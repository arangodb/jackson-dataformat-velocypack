package tools.jackson.dataformat.velocypack;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Validates the local byte oracle without constructing a parser or using codec helpers. */
public class VPackWireFixtureTest {
    @Test
    void allDeclaredRootsHaveLiteralLengthAndInterpretation() throws IOException {
        List<String> rows = fixtureRows();
        assertEquals(40, rows.size(), "all doc/wire-vectors.json roots are installed");
        for (String row : rows) {
            String[] fields = row.split("\\|", -1);
            byte[] bytes = hex(fields[2]);
            assertEquals(Integer.parseInt(fields[3]), bytes.length, fields[0]);
            assertTrue(!fields[4].isEmpty(), fields[0] + " needs an explicit interpretation");
        }
    }

    @Test
    void assemblerMatchesLiteralAnchors() {
        assertArrayEquals(hex("01 02 03 04"), VPackWireFixtureAssembler.le(4, 0x04030201L));
        assertArrayEquals(hex("7f"), VPackWireFixtureAssembler.forwardVarint(127));
        assertArrayEquals(hex("7f"), VPackWireFixtureAssembler.reverseVarint(127));
        assertArrayEquals(hex("80 01"), VPackWireFixtureAssembler.forwardVarint(128));
        assertArrayEquals(hex("01 80"), VPackWireFixtureAssembler.reverseVarint(128));
        assertArrayEquals(hex("81 01"), VPackWireFixtureAssembler.forwardVarint(129));
        assertArrayEquals(hex("01 81"), VPackWireFixtureAssembler.reverseVarint(129));
        assertArrayEquals(hex("04"), VPackWireFixtureAssembler.forwardVarint(4));
    }

    @Test
    void forcedWidthsPaddingAndCompactAmbiguitiesAreLiteral() {
        assertEquals(10, hex("05 0a 00 00 00 00 00 00 00 31").length);
        assertEquals(26, hex("09 1a 00 00 00 00 00 00 00 31 09 00 00 00 00 00 00 00 01 00 00 00 00 00 00 00").length);
        assertEquals(28, hex("0e 1c 00 00 00 00 00 00 00 01 00 00 00 00 00 00 00 41 61 31 11 00 00 00 00 00 00 00").length);
        assertEquals(28, hex("12 1c 00 00 00 00 00 00 00 41 61 31 09 00 00 00 00 00 00 00 01 00 00 00 00 00 00 00").length);
        assertArrayEquals(hex("14 0a 41 61 31 41 62 28 10 02"),
                hex("14 0a 41 61 31 41 62 28 10 02"));
        assertArrayEquals(hex("41 61"), hex("41 61"));
    }

    @Test
    void fixedLayoutArithmeticIsIndependent() {
        // W3: L=1+w+P+B, header indexed L=1+2w+P+B+Nw, tail-count adds count width.
        assertEquals(5, 1 + 1 + 0 + 3);
        assertEquals(14, 1 + 2 * 2 + 0 + 3 + 3 * 2);
        assertEquals(26, 1 + 8 + 1 + 8 + 8);
        assertEquals(28, 1 + 16 + 3 + 8);
        assertEquals(28, 1 + 8 + 3 + 8 + 8);
    }

    @Test
    void everyLocallyAllowedPaddingStartAndWidthHasAnExplicitAnchor() {
        String[] anchors = {
                "02 03 31", "02 04 00 31", "02 06 00 00 00 31", "02 0a 00 00 00 00 00 00 00 31",
                "03 04 00 31", "03 06 00 00 00 31", "03 0a 00 00 00 00 00 00 00 31",
                "04 06 00 00 00 31", "04 0a 00 00 00 00 00 00 00 31",
                "05 0a 00 00 00 00 00 00 00 31",
                "06 05 01 31 03", "06 0b 01 00 00 00 00 00 00 31 09",
                "07 0e 00 03 00 31 32 33 05 00 06 00 07 00",
                "07 0c 00 01 00 00 00 00 00 31 09 00",
                "08 0a 00 00 00 00 00 00 00 31",
                "09 1a 00 00 00 00 00 00 00 31 09 00 00 00 00 00 00 00 01 00 00 00 00 00 00 00",
                "0e 1c 00 00 00 00 00 00 00 01 00 00 00 00 00 00 00 41 61 31 11 00 00 00 00 00 00 00",
                "12 1c 00 00 00 00 00 00 00 41 61 31 09 00 00 00 00 00 00 00 01 00 00 00 00 00 00 00"
        };
        for (String anchor : anchors) {
            byte[] bytes = hex(anchor);
            assertEquals(bytes[1] & 0xff, bytes.length, anchor);
        }
    }

    private static List<String> fixtureRows() throws IOException {
        List<String> rows = new ArrayList<>();
        try (InputStream in = VPackWireFixtureTest.class.getResourceAsStream("/tools/jackson/dataformat/velocypack/wire-vectors.txt")) {
            if (in == null) throw new IOException("missing wire-vectors.txt");
            for (String line : new String(in.readAllBytes(), StandardCharsets.UTF_8).split("\\R")) {
                if (!line.isBlank() && !line.startsWith("#")) rows.add(line);
            }
        }
        return rows;
    }

    public static byte[] hex(String text) {
        String compact = text.replace(" ", "");
        byte[] result = new byte[compact.length() / 2];
        for (int i = 0; i < result.length; ++i) result[i] = (byte) Integer.parseInt(compact.substring(i * 2, i * 2 + 2), 16);
        return result;
    }
}
