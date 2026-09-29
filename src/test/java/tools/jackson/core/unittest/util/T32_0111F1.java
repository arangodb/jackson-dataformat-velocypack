package tools.jackson.core.unittest.util;

import tools.jackson.core.util.SimpleStreamReadContext;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class T32_0111F1 {
private static final String QUOTED = "\\\"quo\\\\ted\\\"";

    void childContextRecyclingVpack() {
        SimpleStreamReadContext root = SimpleStreamReadContext.createRootContext(null);
        SimpleStreamReadContext array1 = root.createChildArrayContext(1, 0);
        assertSame(root, array1.clearAndGetParent());

        SimpleStreamReadContext array2 = root.createChildArrayContext(2, 0);
        assertSame(array1, array2, "Context should be recycled");
        assertEquals(0, array2.getCurrentIndex());
        assertEquals(2, array2.startLocation(tools.jackson.core.io.ContentReference.unknown())
                .getLineNr());
    }

    void childContextRecyclingResetsStateVpack() {
        SimpleStreamReadContext root = SimpleStreamReadContext.createRootContext(null);
        SimpleStreamReadContext array1 = root.createChildArrayContext(1, 0);
        array1.setCurrentName("name1");
        array1.assignCurrentValue("value1");
        array1.valueRead();

        assertEquals("name1", array1.currentName());
        assertEquals("value1", array1.currentValue());
        assertEquals(0, array1.getCurrentIndex());

        array1.clearAndGetParent();
        SimpleStreamReadContext array2 = root.createChildArrayContext(2, 0);

        assertNull(array2.currentName());
        assertNull(array2.currentValue());
        assertEquals(0, array2.getCurrentIndex());
    }

    void childContextWithNoDupDetectorInParentVpack() {
        SimpleStreamReadContext root = SimpleStreamReadContext.createRootContext(null);
        SimpleStreamReadContext child = root.createChildObjectContext(1, 0);

        assertNull(child.getDupDetector());
    }

    void clearAndGetParentVpack() {
        SimpleStreamReadContext root = SimpleStreamReadContext.createRootContext(null);
        SimpleStreamReadContext array = root.createChildArrayContext(1, 0);
        Object value = new Object();
        array.assignCurrentValue(value);

        assertSame(value, array.currentValue());
        assertSame(root, array.clearAndGetParent());
        assertNull(array.currentValue(), "Value should be cleared");
    }

    void contextTypeDescriptionsVpack() {
        SimpleStreamReadContext root = SimpleStreamReadContext.createRootContext(null);
        assertEquals("root", root.typeDesc());

        SimpleStreamReadContext array = root.createChildArrayContext(1, 0);
        assertEquals("Array", array.typeDesc());

        SimpleStreamReadContext object = root.createChildObjectContext(1, 0);
        assertEquals("Object", object.typeDesc());
    }

    void __invoke_childContextRecyclingVpack() throws Exception {
        try {
            childContextRecyclingVpack();
        } finally {
        }
    }


    void __invoke_childContextRecyclingResetsStateVpack() throws Exception {
        try {
            childContextRecyclingResetsStateVpack();
        } finally {
        }
    }


    void __invoke_childContextWithNoDupDetectorInParentVpack() throws Exception {
        try {
            childContextWithNoDupDetectorInParentVpack();
        } finally {
        }
    }


    void __invoke_clearAndGetParentVpack() throws Exception {
        try {
            clearAndGetParentVpack();
        } finally {
        }
    }


    void __invoke_contextTypeDescriptionsVpack() throws Exception {
        try {
            contextTypeDescriptionsVpack();
        } finally {
        }
    }

}
