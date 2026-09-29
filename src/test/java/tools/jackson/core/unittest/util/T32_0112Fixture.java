package tools.jackson.core.unittest.util;

import tools.jackson.core.JsonParser;
import tools.jackson.core.TokenStreamLocation;
import tools.jackson.core.exc.StreamReadException;
import tools.jackson.core.io.ContentReference;
import tools.jackson.core.json.DupDetector;
import tools.jackson.core.util.SimpleStreamReadContext;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class T32_0112Fixture {

    void createRootContextVpack() {
        SimpleStreamReadContext root = SimpleStreamReadContext.createRootContext(null);

        assertTrue(root.inRoot());
        assertEquals("root", root.typeDesc());
        assertNull(root.getParent());
        assertEquals(0, root.getNestingDepth());
        assertEquals(0, root.getCurrentIndex());
        assertNull(root.currentName());
        assertFalse(root.hasCurrentName());
        assertNull(root.currentValue());
    }

    void createRootContextWithLineAndColumnVpack() {
        SimpleStreamReadContext root = SimpleStreamReadContext.createRootContext(5, 10, null);

        assertTrue(root.inRoot());
        assertNull(root.getParent());

        TokenStreamLocation location = root.startLocation(ContentReference.unknown());
        assertEquals(5, location.getLineNr());
        assertEquals(10, location.getColumnNr());
        assertEquals(-1L, location.getByteOffset());
    }

    void createRootContextWithDupDetectorVpack() {
        DupDetector detector = DupDetector.rootDetector((JsonParser) null);
        SimpleStreamReadContext root = SimpleStreamReadContext.createRootContext(1, 0, detector);

        assertNotNull(root.getDupDetector());
        assertSame(detector, root.getDupDetector());
    }

    void createChildArrayContextVpack() {
        SimpleStreamReadContext root = SimpleStreamReadContext.createRootContext(null);
        SimpleStreamReadContext array = root.createChildArrayContext(2, 5);

        assertTrue(array.inArray());
        assertEquals("Array", array.typeDesc());
        assertSame(root, array.getParent());
        assertEquals(1, array.getNestingDepth());
        assertEquals(0, array.getCurrentIndex());

        TokenStreamLocation location = array.startLocation(ContentReference.unknown());
        assertEquals(2, location.getLineNr());
        assertEquals(5, location.getColumnNr());
    }

    void createChildObjectContextVpack() {
        SimpleStreamReadContext root = SimpleStreamReadContext.createRootContext(null);
        SimpleStreamReadContext object = root.createChildObjectContext(3, 7);

        assertTrue(object.inObject());
        assertEquals("Object", object.typeDesc());
        assertSame(root, object.getParent());
        assertEquals(1, object.getNestingDepth());
        assertEquals(0, object.getCurrentIndex());

        TokenStreamLocation location = object.startLocation(ContentReference.unknown());
        assertEquals(3, location.getLineNr());
        assertEquals(7, location.getColumnNr());
    }

    void valueReadVpack() {
        SimpleStreamReadContext root = SimpleStreamReadContext.createRootContext(null);

        assertEquals(0, root.getCurrentIndex());
        assertEquals(0, root.valueRead());
        assertEquals(0, root.getCurrentIndex());
        assertEquals(1, root.valueRead());
        assertEquals(1, root.getCurrentIndex());
    }

    void setCurrentNameVpack() throws Exception {
        SimpleStreamReadContext context = SimpleStreamReadContext.createRootContext(null);

        assertNull(context.currentName());
        assertFalse(context.hasCurrentName());

        context.setCurrentName("field1");
        assertEquals("field1", context.currentName());
        assertTrue(context.hasCurrentName());

        context.setCurrentName("field2");
        assertEquals("field2", context.currentName());

        context.setCurrentName(null);
        assertNull(context.currentName());
        assertFalse(context.hasCurrentName());
    }

    void duplicateDetectionVpack() {
        DupDetector detector = DupDetector.rootDetector((JsonParser) null);
        SimpleStreamReadContext context =
                SimpleStreamReadContext.createRootContext(1, 0, detector);

        context.setCurrentName("field1");
        assertEquals("field1", context.currentName());

        StreamReadException exception = assertThrows(StreamReadException.class,
                () -> context.setCurrentName("field1"));
        assertTrue(exception.getMessage().contains("Duplicate Object property"));
        assertTrue(exception.getMessage().contains("field1"));
    }

    void duplicateDetectionInChildContextVpack() {
        DupDetector detector = DupDetector.rootDetector((JsonParser) null);
        SimpleStreamReadContext root =
                SimpleStreamReadContext.createRootContext(1, 0, detector);
        SimpleStreamReadContext object = root.createChildObjectContext(2, 0);

        assertNotNull(object.getDupDetector());
        assertNotSame(detector, object.getDupDetector());

        object.setCurrentName("prop1");
        StreamReadException exception = assertThrows(StreamReadException.class,
                () -> object.setCurrentName("prop1"));
        assertTrue(exception.getMessage().contains("Duplicate Object property"));
        assertTrue(exception.getMessage().contains("prop1"));
    }

    void currentValueVpack() {
        SimpleStreamReadContext context = SimpleStreamReadContext.createRootContext(null);

        assertNull(context.currentValue());

        Object value = new Object();
        context.assignCurrentValue(value);
        assertSame(value, context.currentValue());

        context.assignCurrentValue(null);
        assertNull(context.currentValue());
    }

    void nestedContextsVpack() {
        SimpleStreamReadContext root = SimpleStreamReadContext.createRootContext(null);
        assertEquals(0, root.getNestingDepth());

        SimpleStreamReadContext array = root.createChildArrayContext(1, 0);
        assertEquals(1, array.getNestingDepth());
        assertSame(root, array.getParent());

        SimpleStreamReadContext object = array.createChildObjectContext(2, 0);
        assertEquals(2, object.getNestingDepth());
        assertSame(array, object.getParent());

        SimpleStreamReadContext innerArray = object.createChildArrayContext(3, 0);
        assertEquals(3, innerArray.getNestingDepth());
        assertSame(object, innerArray.getParent());
    }

    void contextWithoutDupDetectorVpack() throws Exception {
        SimpleStreamReadContext root = SimpleStreamReadContext.createRootContext(null);

        assertNull(root.getDupDetector());
        root.setCurrentName("field");
        root.setCurrentName("field");
    }

    void __invoke_createRootContextVpack() throws Exception {
        try {
            createRootContextVpack();
        } finally {
        }
    }


    void __invoke_createRootContextWithLineAndColumnVpack() throws Exception {
        try {
            createRootContextWithLineAndColumnVpack();
        } finally {
        }
    }


    void __invoke_createRootContextWithDupDetectorVpack() throws Exception {
        try {
            createRootContextWithDupDetectorVpack();
        } finally {
        }
    }


    void __invoke_createChildArrayContextVpack() throws Exception {
        try {
            createChildArrayContextVpack();
        } finally {
        }
    }


    void __invoke_createChildObjectContextVpack() throws Exception {
        try {
            createChildObjectContextVpack();
        } finally {
        }
    }


    void __invoke_valueReadVpack() throws Exception {
        try {
            valueReadVpack();
        } finally {
        }
    }


    void __invoke_setCurrentNameVpack() throws Exception {
        try {
            setCurrentNameVpack();
        } finally {
        }
    }


    void __invoke_duplicateDetectionVpack() throws Exception {
        try {
            duplicateDetectionVpack();
        } finally {
        }
    }


    void __invoke_duplicateDetectionInChildContextVpack() throws Exception {
        try {
            duplicateDetectionInChildContextVpack();
        } finally {
        }
    }


    void __invoke_currentValueVpack() throws Exception {
        try {
            currentValueVpack();
        } finally {
        }
    }


    void __invoke_nestedContextsVpack() throws Exception {
        try {
            nestedContextsVpack();
        } finally {
        }
    }


    void __invoke_contextWithoutDupDetectorVpack() throws Exception {
        try {
            contextWithoutDupDetectorVpack();
        } finally {
        }
    }

}
