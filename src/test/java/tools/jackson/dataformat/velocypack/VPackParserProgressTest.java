package tools.jackson.dataformat.velocypack;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.ObjectReadContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Fixed-seed, bounded progress checks for every source adapter.  The fixtures
 * are written here rather than obtained from an encoder so truncation tests
 * cannot share a framing bug with production code.
 */
@Timeout(15)
class VPackParserProgressTest {
    private static final int MAX_TOKENS = 4096;
    private static final long MUTATION_SEED = 0x5EED19L;

    private final VPackFactory factory = new VPackFactory();

    @Test
    void everyShortFixtureTruncatedAtEveryBoundaryFailsAsJacksonAcrossSources() {
        for (byte[] fixture : shortFixtures()) {
            consume(fixture, 0);
            for (int length = 1; length < fixture.length; ++length) {
                byte[] truncated = Arrays.copyOf(fixture, length);
                for (int source = 0; source < 3; ++source) {
                    assertJacksonFailure(truncated, source,
                            "truncated at " + length + '/' + fixture.length
                                    + " source=" + source);
                }
            }
        }
    }

    @Test
    void fixedSeedMutationsNeverLeakUncheckedParserFailures() {
        long seed = MUTATION_SEED;
        int cases = 0;
        for (byte[] fixture : shortFixtures()) {
            for (int mutation = 0; mutation < 16; ++mutation) {
                byte[] candidate = fixture.clone();
                seed = next(seed);
                int index = (int) Math.floorMod(seed, candidate.length);
                seed = next(seed);
                candidate[index] ^= (byte) seed;
                for (int source = 0; source < 3; ++source) {
                    assertJacksonOrComplete(candidate, source,
                            "seed=0x" + Long.toHexString(MUTATION_SEED)
                                    + " case=" + cases + " source=" + source);
                }
                ++cases;
            }
        }
        assertEquals(shortFixtures().size() * 16, cases);
    }

    @Test
    void boundaryMutationsNeverLeakUncheckedParserFailures() {
        int cases = 0;
        for (byte[] fixture : shortFixtures()) {
            for (int index = 0; index < fixture.length; ++index) {
                for (int replacement : new int[] { 0x00, 0x7F, 0x80, 0xFF }) {
                    byte[] candidate = fixture.clone();
                    candidate[index] = (byte) replacement;
                    for (int source = 0; source < 3; ++source) {
                        assertJacksonOrComplete(candidate, source,
                                "boundary case=" + cases + " index=" + index
                                        + " replacement=0x" + Integer.toHexString(replacement)
                                        + " source=" + source);
                    }
                    ++cases;
                }
            }
        }
        assertTrue(cases > 100);
    }

    private void assertJacksonFailure(byte[] input, int source, String description) {
        try (JsonParser parser = parser(input, source)) {
            try {
                consume(parser);
                fail(description + " unexpectedly completed");
            } catch (RuntimeException e) {
                assertInstanceOf(JacksonException.class, e, description);
            }
        }
    }

    private void assertJacksonOrComplete(byte[] input, int source, String description) {
        try (JsonParser parser = parser(input, source)) {
            try {
                consume(parser);
            } catch (RuntimeException e) {
                assertInstanceOf(JacksonException.class, e, description);
            }
        }
    }

    private void consume(byte[] input, int source) {
        try (JsonParser parser = parser(input, source)) {
            consume(parser);
        }
    }

    private static void consume(JsonParser parser) {
        int tokens = 0;
        while (parser.nextToken() != null) {
            assertTrue(++tokens <= MAX_TOKENS, "parser did not make bounded progress");
        }
    }

    private JsonParser parser(byte[] input, int source) {
        return switch (source) {
        case 0 -> factory.createParser(input);
        case 1 -> factory.createParser(new ByteArrayInputStream(input));
        case 2 -> factory.createParser(ObjectReadContext.empty(),
                (DataInput) new DataInputStream(new ByteArrayInputStream(input)));
        default -> throw new AssertionError("source " + source);
        };
    }

    private static long next(long value) {
        return value * 6364136223846793005L + 1442695040888963407L;
    }

    private static List<byte[]> shortFixtures() {
        return List.of(
                new byte[] { 0x41, 'a' },
                new byte[] { (byte) 0xBF, 1, 0, 0, 0, 0, 0, 0, 0, 'a' },
                new byte[] { (byte) 0xC0, 2, 0x11, 0x22 },
                new byte[] { (byte) 0xC8, 1, 0, 0, 0, 0, 0x12 },
                new byte[] { 0x20, (byte) 0x80 },
                new byte[] { 0x1B, 0, 0, 0, 0, 0, 0, (byte) 0xF0, 0x3F },
                new byte[] { 0x02, 0x04, 0x31, 0x32 },
                new byte[] { 0x03, 0x0A, 0, 0, 0, 0, 0, 0, 0, 0x31 },
                new byte[] { 0x06, 0x07, 0x02, 0x31, 0x32, 0x03, 0x04 },
                new byte[] { 0x07, 0x0B, 0, 0x02, 0, 0x31, 0x32, 0x05, 0, 0x06, 0 },
                new byte[] { 0x0B, 0x07, 0x01, 0x41, 0x61, 0x31, 0x03 },
                new byte[] { 0x0C, 0x0A, 0, 0x01, 0, 0x41, 0x61, 0x31, 0x05, 0 },
                trailingArrayFixture(),
                new byte[] { 0x13, 0x04, 0x31, 0x01 },
                new byte[] { 0x14, 0x06, 0x41, 0x61, 0x31, 0x01 }
        );
    }

    private static byte[] trailingArrayFixture() {
        byte[] result = new byte[26];
        result[0] = 0x09;
        put(result, 1, 8, result.length);
        result[9] = 0x31;
        put(result, 10, 8, 9);
        put(result, 18, 8, 1);
        return result;
    }

    private static void put(byte[] output, int offset, int width, long value) {
        for (int i = 0; i < width; ++i) {
            output[offset + i] = (byte) (value >>> (8 * i));
        }
    }
}
