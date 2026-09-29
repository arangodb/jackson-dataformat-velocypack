package tools.jackson.core.unittest.sym;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tools.jackson.core.json.JsonFactory;
import tools.jackson.core.sym.ByteQuadsCanonicalizer;
import tools.jackson.core.sym.SimpleNameMatcher;
import static org.junit.jupiter.api.Assertions.assertEquals;

class T32_0102F2 {
private static final int DEFAULT_FEATURES = JsonFactory.Feature.collectDefaults();
private static final int MAX_ENTRIES_FOR_REUSE = 6000;
private static final byte[] MEDIA_SYMBOL_OBJECT = {
            0x14, (byte) 0xB7, 0x01,
            0x45, 'm', 'e', 'd', 'i', 'a',
            0x14, 0x5B,
            0x43, 'u', 'r', 'i', 0x18,
            0x45, 't', 'i', 't', 'l', 'e', 0x18,
            0x45, 'w', 'i', 'd', 't', 'h', 0x18,
            0x46, 'h', 'e', 'i', 'g', 'h', 't', 0x18,
            0x46, 'f', 'o', 'r', 'm', 'a', 't', 0x18,
            0x48, 'd', 'u', 'r', 'a', 't', 'i', 'o', 'n', 0x18,
            0x44, 's', 'i', 'z', 'e', 0x18,
            0x47, 'b', 'i', 't', 'r', 'a', 't', 'e', 0x18,
            0x47, 'p', 'e', 'r', 's', 'o', 'n', 's', 0x01,
            0x46, 'p', 'l', 'a', 'y', 'e', 'r', 0x18,
            0x49, 'c', 'o', 'p', 'y', 'r', 'i', 'g', 'h', 't', 0x18,
            0x0B,
            0x46, 'i', 'm', 'a', 'g', 'e', 's',
            0x13, 0x4B,
            0x14, 0x24,
            0x43, 'u', 'r', 'i', 0x18,
            0x45, 't', 'i', 't', 'l', 'e', 0x18,
            0x45, 'w', 'i', 'd', 't', 'h', 0x18,
            0x46, 'h', 'e', 'i', 'g', 'h', 't', 0x18,
            0x44, 's', 'i', 'z', 'e', 0x18, 0x05,
            0x14, 0x24,
            0x43, 'u', 'r', 'i', 0x18,
            0x45, 't', 'i', 't', 'l', 'e', 0x18,
            0x45, 'w', 'i', 'd', 't', 'h', 0x18,
            0x46, 'h', 'e', 'i', 'g', 'h', 't', 0x18,
            0x44, 's', 'i', 'z', 'e', 0x18, 0x05,
            0x02, 0x02
    };

    void textualMisc11RetainsSourceHashDistribution() {
        assertTextualDistribution(Arrays.asList(
                "player", "uri", "title", "width", "height", "format",
                "duration", "size", "bitrate", "copyright", "persons"), 1, 0);
    }

    void textualMisc5RetainsSourceHashDistribution() {
        assertTextualDistribution(Arrays.asList("uri", "title", "width", "height", "size"),
                2, 0);
    }

    void textualMisc2RetainsSourceHashDistribution() {
        assertTextualDistribution(Arrays.asList("content", "images"), 0, 0);
    }

    void textualPrefix1RetainsSourceHashDistribution() {
        assertTextualDistribution(generatePrefix("", 99), 0, 0);
    }

    void textualPrefix2RetainsSourceHashDistribution() {
        assertTextualDistribution(generatePrefix("base", 39), 5, 0);
    }

    void textualPrefix3RetainsSourceHashDistribution() {
        assertTextualDistribution(generatePrefix("Of ", 139), 15, 0);
    }

    void textualPrefix4RetainsSourceHashDistribution() {
        assertTextualDistribution(generatePrefix("ACE-", 499), 54, 3);
    }

    void textualSuffix1RetainsSourceHashDistribution() {
        assertTextualDistribution(generateSuffix("", 99), 0, 0);
    }
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
private static List<String> generatePrefix(String base, int count) {
        List<String> result = new ArrayList<>(count);
        while (--count >= 0) {
            result.add((count + base).intern());
        }
        return result;
    }
private static ByteQuadsCanonicalizer seededChildRoot(int seed) {
        return new SeededCanonicalizer(seed);
    }
private static int[] calcQuads(byte[] bytes) {
        int[] result = new int[(bytes.length + 3) / 4];
        for (int i = 0; i < bytes.length; ++i) {
            int value = bytes[i] & 0xFF;
            if (++i < bytes.length) {
                value = (value << 8) | (bytes[i] & 0xFF);
                if (++i < bytes.length) {
                    value = (value << 8) | (bytes[i] & 0xFF);
                    if (++i < bytes.length) {
                        value = (value << 8) | (bytes[i] & 0xFF);
                    }
                }
            }
            result[i >> 2] = value;
        }
        return result;
    }
private static final class SeededCanonicalizer extends ByteQuadsCanonicalizer {
        SeededCanonicalizer(int seed) {
            super(64, seed);
        }
    }

    void __invoke_textualMisc11RetainsSourceHashDistribution() throws Exception {
        try {
            textualMisc11RetainsSourceHashDistribution();
        } finally {
        }
    }


    void __invoke_textualMisc5RetainsSourceHashDistribution() throws Exception {
        try {
            textualMisc5RetainsSourceHashDistribution();
        } finally {
        }
    }


    void __invoke_textualMisc2RetainsSourceHashDistribution() throws Exception {
        try {
            textualMisc2RetainsSourceHashDistribution();
        } finally {
        }
    }


    void __invoke_textualPrefix1RetainsSourceHashDistribution() throws Exception {
        try {
            textualPrefix1RetainsSourceHashDistribution();
        } finally {
        }
    }


    void __invoke_textualPrefix2RetainsSourceHashDistribution() throws Exception {
        try {
            textualPrefix2RetainsSourceHashDistribution();
        } finally {
        }
    }


    void __invoke_textualPrefix3RetainsSourceHashDistribution() throws Exception {
        try {
            textualPrefix3RetainsSourceHashDistribution();
        } finally {
        }
    }


    void __invoke_textualPrefix4RetainsSourceHashDistribution() throws Exception {
        try {
            textualPrefix4RetainsSourceHashDistribution();
        } finally {
        }
    }


    void __invoke_textualSuffix1RetainsSourceHashDistribution() throws Exception {
        try {
            textualSuffix1RetainsSourceHashDistribution();
        } finally {
        }
    }

}
