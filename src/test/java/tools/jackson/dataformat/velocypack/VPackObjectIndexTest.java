package tools.jackson.dataformat.velocypack;

import org.junit.jupiter.api.Test;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.exc.StreamReadException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VPackObjectIndexTest {
    @Test
    void rejectsDanglingKeysAndPointersToValues() {
        byte[] dangling = VPackObjectParserTest.object(1, false,
                VPackObjectParserTest.pair("a", new byte[0]), new long[] { 3 });
        assertRejected(dangling);

        byte[] body = VPackObjectParserTest.pair("a", new byte[] { 0x31 });
        byte[] valuePointer = VPackObjectParserTest.object(1, false, body, new long[] { 5 });
        assertRejected(valuePointer);
    }

    @Test
    void rejectsDuplicateOrMissingKeyStarts() {
        byte[] body = VPackObjectParserTest.body(
                VPackObjectParserTest.pair("a", new byte[] { 0x31 }),
                VPackObjectParserTest.pair("b", new byte[] { 0x32 }));
        assertRejected(VPackObjectParserTest.object(1, false, body, new long[] { 3, 3 }));
        assertRejected(VPackObjectParserTest.object(1, false, body, new long[] { 3, 8 }));
    }

    @Test
    void validatesSortedUtf8OrderButAllowsObsoletePermutations() {
        byte[] body = VPackObjectParserTest.body(
                VPackObjectParserTest.pair("b", new byte[] { 0x31 }),
                VPackObjectParserTest.pair("a", new byte[] { 0x32 }));
        assertRejected(VPackObjectParserTest.object(1, true, body, new long[] { 3, 6 }));

        byte[] obsolete = VPackObjectParserTest.object(1, false, body,
                new long[] { 6, 3 });
        try (JsonParser parser = new VPackFactory().createParser(obsolete)) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            while (parser.nextToken() != JsonToken.END_OBJECT) { }
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    @Test
    void sortedOrderUsesUtf8BytesNotUtf16CodeUnits() {
        String supplementary = "\uD800\uDC00";
        String privateUse = "\uE000";
        byte[] body = VPackObjectParserTest.body(
                VPackObjectParserTest.pair(supplementary, new byte[] { 0x31 }),
                VPackObjectParserTest.pair(privateUse, new byte[] { 0x32 }));
        // UTF-16 would put the supplementary name first; UTF-8 puts U+E000 first.
        byte[] valid = VPackObjectParserTest.object(1, true, body,
                new long[] { 9, 3 });
        try (JsonParser parser = new VPackFactory().createParser(valid)) {
            while (parser.nextToken() != null) { }
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    private static void assertRejected(byte[] input) {
        try (JsonParser parser = new VPackFactory().createParser(input)) {
            assertThrows(StreamReadException.class, () -> {
                while (parser.nextToken() != null) { }
            });
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }
}
