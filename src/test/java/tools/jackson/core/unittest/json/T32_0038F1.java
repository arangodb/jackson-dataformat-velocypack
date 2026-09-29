package tools.jackson.core.unittest.json;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.List;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.exc.StreamReadException;
import tools.jackson.core.io.ContentReference;
import tools.jackson.core.json.DupDetector;
import tools.jackson.core.json.JsonReadContext;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0038F1 {
private static final byte[] EMPTY_OBJECT = { 0x0a };
private static final byte[] ONE_ELEMENT_ARRAY = { 0x02, 0x03, 0x31 };

    void jsonReadContextSetCurrentNameRetainsAndClearsName() throws Exception {
        JsonReadContext context = JsonReadContext.createRootContext(0, 0, null);
        context.setCurrentName("abc");
        assertEquals("abc", context.currentName());
        context.setCurrentName(null);
        assertNull(context.currentName());
    }

    void jsonReadContextResetRestoresConfiguredTypeAndLocation() {
        DupDetector duplicateDetector = DupDetector.rootDetector((JsonGenerator) null);
        JsonReadContext context = JsonReadContext.createRootContext(duplicateDetector);
        ContentReference source = ContentReference.unknown();

        assertTrue(context.inRoot());
        assertEquals("root", context.typeDesc());
        assertEquals(1, context.startLocation(source).getLineNr());
        assertEquals(0, context.startLocation(source).getColumnNr());

        context.reset(200, 500, 200);

        assertFalse(context.inRoot());
        assertEquals("?", context.typeDesc());
        assertEquals(500, context.startLocation(source).getLineNr());
        assertEquals(200, context.startLocation(source).getColumnNr());
    }

    void jsonReadContextDuplicateNameRaisesLocatedStreamReadException() throws Exception {
        final String propertyName = "dupField";
        DupDetector duplicateDetector = DupDetector.rootDetector((JsonGenerator) null);
        JsonReadContext context = new JsonReadContext(null, 0, duplicateDetector,
                2441, 2441, 2441);
        context.setCurrentName(propertyName);

        StreamReadException failure = assertThrows(StreamReadException.class,
                () -> context.setCurrentName(propertyName));
        assertTrue(failure.getMessage().contains("Duplicate Object property \"dupField\""));
        assertTrue(failure.getMessage().contains(propertyName));
    }

    void jsonReadContextExtensionCanBeInstantiated() {
        MyContext context = new MyContext(null, 0, null, 0, 0, 0);
        assertNotNull(context);
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

    void __invoke_jsonReadContextSetCurrentNameRetainsAndClearsName() throws Exception {
        try {
            jsonReadContextSetCurrentNameRetainsAndClearsName();
        } finally {
        }
    }


    void __invoke_jsonReadContextResetRestoresConfiguredTypeAndLocation() throws Exception {
        try {
            jsonReadContextResetRestoresConfiguredTypeAndLocation();
        } finally {
        }
    }


    void __invoke_jsonReadContextDuplicateNameRaisesLocatedStreamReadException() throws Exception {
        try {
            jsonReadContextDuplicateNameRaisesLocatedStreamReadException();
        } finally {
        }
    }


    void __invoke_jsonReadContextExtensionCanBeInstantiated() throws Exception {
        try {
            jsonReadContextExtensionCanBeInstantiated();
        } finally {
        }
    }

}
