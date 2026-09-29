package tools.jackson.core.unittest.util;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.exc.StreamWriteException;
import tools.jackson.core.json.DupDetector;
import tools.jackson.core.util.SimpleStreamWriteContext;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class T32_0114Fixture {

    void withDupDetectorVpack() {
        SimpleStreamWriteContext root = SimpleStreamWriteContext.createRootContext(null);
        assertNull(root.getDupDetector());

        DupDetector detector = DupDetector.rootDetector((JsonGenerator) null);
        SimpleStreamWriteContext result = root.withDupDetector(detector);

        assertSame(root, result);
        assertSame(detector, root.getDupDetector());
    }

    void writeNameInObjectContextVpack() throws Exception {
        SimpleStreamWriteContext object = SimpleStreamWriteContext.createRootContext(null)
                .createChildObjectContext(null);

        assertTrue(object.writeName("field1"));
        assertEquals("field1", object.currentName());
        assertTrue(object.hasCurrentName());
    }

    void writeNameNotAllowedInArrayContextVpack() throws Exception {
        SimpleStreamWriteContext array = SimpleStreamWriteContext.createRootContext(null)
                .createChildArrayContext(null);

        assertFalse(array.writeName("field1"));
        assertNull(array.currentName());
        assertFalse(array.hasCurrentName());
    }

    void writeNameNotAllowedInRootContextVpack() throws Exception {
        SimpleStreamWriteContext root = SimpleStreamWriteContext.createRootContext(null);

        assertFalse(root.writeName("field1"));
    }

    void writeNameNotAllowedWhenAlreadyHavePropertyIdVpack() throws Exception {
        SimpleStreamWriteContext object = SimpleStreamWriteContext.createRootContext(null)
                .createChildObjectContext(null);

        assertTrue(object.writeName("field1"));
        assertFalse(object.writeName("field2"));
        assertEquals("field1", object.currentName());
    }

    void writeValueVpack() throws Exception {
        SimpleStreamWriteContext root = SimpleStreamWriteContext.createRootContext(null);

        assertEquals(0, root.getCurrentIndex());

        assertTrue(root.writeValue());
        assertEquals(0, root.getCurrentIndex());

        assertTrue(root.writeValue());
        assertEquals(1, root.getCurrentIndex());
    }

    void writeValueInArrayContextVpack() throws Exception {
        SimpleStreamWriteContext array = SimpleStreamWriteContext.createRootContext(null)
                .createChildArrayContext(null);

        assertTrue(array.writeValue());
        assertEquals(0, array.getCurrentIndex());

        assertTrue(array.writeValue());
        assertEquals(1, array.getCurrentIndex());
    }

    void duplicateDetectionVpack() throws Exception {
        DupDetector detector = DupDetector.rootDetector((JsonGenerator) null);
        SimpleStreamWriteContext object = SimpleStreamWriteContext.createRootContext(detector)
                .createChildObjectContext(null);

        object.writeName("field1");
        object.writeValue();

        try {
            object.writeName("field1");
            fail("Should have thrown StreamWriteException for duplicate field");
        } catch (StreamWriteException exception) {
            assertTrue(exception.getMessage().contains("Duplicate Object property"));
            assertTrue(exception.getMessage().contains("field1"));
        }
    }

    void currentValueVpack() {
        SimpleStreamWriteContext context = SimpleStreamWriteContext.createRootContext(null);

        assertNull(context.currentValue());

        Object value = new Object();
        context.assignCurrentValue(value);
        assertSame(value, context.currentValue());

        context.assignCurrentValue(null);
        assertNull(context.currentValue());
    }

    void nestedContextsVpack() {
        SimpleStreamWriteContext root = SimpleStreamWriteContext.createRootContext(null);
        assertEquals(0, root.getNestingDepth());

        SimpleStreamWriteContext array = root.createChildArrayContext(null);
        assertEquals(1, array.getNestingDepth());
        assertSame(root, array.getParent());

        SimpleStreamWriteContext object = array.createChildObjectContext(null);
        assertEquals(2, object.getNestingDepth());
        assertSame(array, object.getParent());

        SimpleStreamWriteContext innerArray = object.createChildArrayContext(null);
        assertEquals(3, innerArray.getNestingDepth());
        assertSame(object, innerArray.getParent());
    }

    void hasCurrentNameBehaviorVpack() throws Exception {
        SimpleStreamWriteContext object = SimpleStreamWriteContext.createRootContext(null)
                .createChildObjectContext(null);

        assertFalse(object.hasCurrentName());

        object.writeName("field1");
        assertTrue(object.hasCurrentName());

        object.writeValue();
        assertFalse(object.hasCurrentName());
        assertEquals("field1", object.currentName());
    }

    void dupDetectorResetOnRecycleVpack() throws Exception {
        DupDetector detector = DupDetector.rootDetector((JsonGenerator) null);
        SimpleStreamWriteContext root = SimpleStreamWriteContext.createRootContext(detector);

        SimpleStreamWriteContext object1 = root.createChildObjectContext(null);
        object1.writeName("field1");
        object1.writeValue();
        object1.clearAndGetParent();

        SimpleStreamWriteContext object2 = root.createChildObjectContext(null);
        assertTrue(object2.writeName("field1"));
        object2.writeValue();
    }

    void __invoke_withDupDetectorVpack() throws Exception {
        try {
            withDupDetectorVpack();
        } finally {
        }
    }


    void __invoke_writeNameInObjectContextVpack() throws Exception {
        try {
            writeNameInObjectContextVpack();
        } finally {
        }
    }


    void __invoke_writeNameNotAllowedInArrayContextVpack() throws Exception {
        try {
            writeNameNotAllowedInArrayContextVpack();
        } finally {
        }
    }


    void __invoke_writeNameNotAllowedInRootContextVpack() throws Exception {
        try {
            writeNameNotAllowedInRootContextVpack();
        } finally {
        }
    }


    void __invoke_writeNameNotAllowedWhenAlreadyHavePropertyIdVpack() throws Exception {
        try {
            writeNameNotAllowedWhenAlreadyHavePropertyIdVpack();
        } finally {
        }
    }


    void __invoke_writeValueVpack() throws Exception {
        try {
            writeValueVpack();
        } finally {
        }
    }


    void __invoke_writeValueInArrayContextVpack() throws Exception {
        try {
            writeValueInArrayContextVpack();
        } finally {
        }
    }


    void __invoke_duplicateDetectionVpack() throws Exception {
        try {
            duplicateDetectionVpack();
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


    void __invoke_hasCurrentNameBehaviorVpack() throws Exception {
        try {
            hasCurrentNameBehaviorVpack();
        } finally {
        }
    }


    void __invoke_dupDetectorResetOnRecycleVpack() throws Exception {
        try {
            dupDetectorResetOnRecycleVpack();
        } finally {
        }
    }

}
