package tools.jackson.dataformat.velocypack;

import org.junit.jupiter.api.Test;

import tools.jackson.core.JsonToken;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VPackNestedArrayTest {
    @Test
    void nestedArraysKeepParentLinkedContextsAndScalarParity() throws Exception {
        byte[] input = { 0x02, 0x06, 0x02, 0x04, 0x31, 0x32 };
        try (VPackParser parser = (VPackParser) new VPackFactory().createParser(input)) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(1, parser.streamReadContext().getNestingDepth());
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(2, parser.streamReadContext().getNestingDepth());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(1, parser.getIntValue());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(2, parser.getIntValue());
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            assertEquals(1, parser.streamReadContext().getNestingDepth());
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
        }
    }
}
