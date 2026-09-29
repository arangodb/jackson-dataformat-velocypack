package tools.jackson.core.unittest.json;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.List;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.StreamReadFeature;
import tools.jackson.core.io.SerializedString;
import tools.jackson.core.json.DupDetector;
import tools.jackson.core.json.JsonReadContext;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0038F0 {
private static final byte[] EMPTY_OBJECT = { 0x0a };
private static final byte[] ONE_ELEMENT_ARRAY = { 0x02, 0x03, 0x31 };

    void closedVpackParsersReturnNullForNextFieldName() throws Exception {
        for (JsonParser parser : closedParsers()) {
            assertNull(parser.nextName());
        }
    }

    void closedVpackParsersReturnFalseForSerializedNextFieldName() throws Exception {
        for (JsonParser parser : closedParsers()) {
            assertFalse(parser.nextName(new SerializedString("")));
        }
    }

    void closedVpackParsersReturnNullForNextToken() throws Exception {
        for (JsonParser parser : closedParsers()) {
            assertNull(parser.nextToken());
        }
    }

    void closedVpackParsersReturnNullForNextValue() throws Exception {
        for (JsonParser parser : closedParsers()) {
            assertNull(parser.nextValue());
        }
    }

    void clearCurrentTokenOnCloseEnabledForBinarySources() throws Exception {
        VPackFactory factory = VPackFactory.builder()
                .enable(StreamReadFeature.CLEAR_CURRENT_TOKEN_ON_CLOSE)
                .build();
        assertCurrentTokenAfterClose(factory, null);
    }

    void clearCurrentTokenOnCloseDisabledForBinarySources() throws Exception {
        VPackFactory factory = VPackFactory.builder()
                .disable(StreamReadFeature.CLEAR_CURRENT_TOKEN_ON_CLOSE)
                .build();
        assertCurrentTokenAfterClose(factory, JsonToken.START_ARRAY);
    }
private static List<JsonParser> closedParsers() throws Exception {
        VPackFactory factory = new VPackFactory();
        List<JsonParser> parsers = new ArrayList<>();
        parsers.add(factory.createParser(ObjectReadContext.empty(), EMPTY_OBJECT));
        parsers.add(factory.createParser(ObjectReadContext.empty(),
                new ByteArrayInputStream(EMPTY_OBJECT)));
        DataInput input = new DataInputStream(new ByteArrayInputStream(EMPTY_OBJECT));
        parsers.add(factory.createParser(ObjectReadContext.empty(), input));
        for (JsonParser parser : parsers) {
            parser.close();
        }
        return parsers;
    }
private static void assertCurrentTokenAfterClose(VPackFactory factory,
            JsonToken expected) throws Exception {
        try (JsonParser parser = factory.createParser(ObjectReadContext.empty(),
                ONE_ELEMENT_ARRAY)) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            parser.close();
            assertEquals(expected, parser.currentToken());
        }
        try (JsonParser parser = factory.createParser(ObjectReadContext.empty(),
                new ByteArrayInputStream(ONE_ELEMENT_ARRAY))) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            parser.close();
            assertEquals(expected, parser.currentToken());
        }
        DataInput input = new DataInputStream(new ByteArrayInputStream(ONE_ELEMENT_ARRAY));
        try (JsonParser parser = factory.createParser(ObjectReadContext.empty(), input)) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            parser.close();
            assertEquals(expected, parser.currentToken());
        }
    }
private static final class MyContext extends JsonReadContext {
        MyContext(JsonReadContext parent, int nestingDepth, DupDetector dups,
                int type, int lineNr, int colNr) {
            super(parent, nestingDepth, dups, type, lineNr, colNr);
        }
    }

    void __invoke_closedVpackParsersReturnNullForNextFieldName() throws Exception {
        try {
            closedVpackParsersReturnNullForNextFieldName();
        } finally {
        }
    }


    void __invoke_closedVpackParsersReturnFalseForSerializedNextFieldName() throws Exception {
        try {
            closedVpackParsersReturnFalseForSerializedNextFieldName();
        } finally {
        }
    }


    void __invoke_closedVpackParsersReturnNullForNextToken() throws Exception {
        try {
            closedVpackParsersReturnNullForNextToken();
        } finally {
        }
    }


    void __invoke_closedVpackParsersReturnNullForNextValue() throws Exception {
        try {
            closedVpackParsersReturnNullForNextValue();
        } finally {
        }
    }


    void __invoke_clearCurrentTokenOnCloseEnabledForBinarySources() throws Exception {
        try {
            clearCurrentTokenOnCloseEnabledForBinarySources();
        } finally {
        }
    }


    void __invoke_clearCurrentTokenOnCloseDisabledForBinarySources() throws Exception {
        try {
            clearCurrentTokenOnCloseDisabledForBinarySources();
        } finally {
        }
    }

}
