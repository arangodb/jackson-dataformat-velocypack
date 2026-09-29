package tools.jackson.core.unittest.util;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.json.DupDetector;
import tools.jackson.core.util.SimpleStreamWriteContext;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class T32_0113Fixture {

    void createRootContextVpack() {
        SimpleStreamWriteContext root = SimpleStreamWriteContext.createRootContext(null);

        assertTrue(root.inRoot());
        assertEquals("root", root.typeDesc());
        assertNull(root.getParent());
        assertEquals(0, root.getNestingDepth());
        assertEquals(0, root.getCurrentIndex());
        assertNull(root.currentName());
        assertFalse(root.hasCurrentName());
        assertNull(root.currentValue());
    }

    void createRootContextWithDupDetectorVpack() {
        DupDetector detector = DupDetector.rootDetector((JsonGenerator) null);
        SimpleStreamWriteContext root = SimpleStreamWriteContext.createRootContext(detector);

        assertNotNull(root.getDupDetector());
        assertSame(detector, root.getDupDetector());
    }

    void createChildArrayContextVpack() {
        SimpleStreamWriteContext root = SimpleStreamWriteContext.createRootContext(null);
        Object arrayValue = new Object();
        SimpleStreamWriteContext array = root.createChildArrayContext(arrayValue);

        assertTrue(array.inArray());
        assertEquals("Array", array.typeDesc());
        assertSame(root, array.getParent());
        assertEquals(1, array.getNestingDepth());
        assertEquals(0, array.getCurrentIndex());
        assertSame(arrayValue, array.currentValue());
    }

    void createChildObjectContextVpack() {
        SimpleStreamWriteContext root = SimpleStreamWriteContext.createRootContext(null);
        Object objectValue = new Object();
        SimpleStreamWriteContext object = root.createChildObjectContext(objectValue);

        assertTrue(object.inObject());
        assertEquals("Object", object.typeDesc());
        assertSame(root, object.getParent());
        assertEquals(1, object.getNestingDepth());
        assertEquals(0, object.getCurrentIndex());
        assertSame(objectValue, object.currentValue());
    }

    void childContextRecyclingVpack() {
        SimpleStreamWriteContext root = SimpleStreamWriteContext.createRootContext(null);

        SimpleStreamWriteContext array1 = root.createChildArrayContext("value1");
        assertNotNull(array1);
        assertEquals("value1", array1.currentValue());

        SimpleStreamWriteContext parent = array1.clearAndGetParent();
        assertSame(root, parent);

        SimpleStreamWriteContext array2 = root.createChildArrayContext("value2");
        assertSame(array1, array2);
        assertTrue(array2.inArray());
        assertEquals(0, array2.getCurrentIndex());
        assertEquals("value2", array2.currentValue());
    }

    void clearAndGetParentVpack() {
        SimpleStreamWriteContext root = SimpleStreamWriteContext.createRootContext(null);
        SimpleStreamWriteContext array = root.createChildArrayContext("arrayValue");

        assertEquals("arrayValue", array.currentValue());

        SimpleStreamWriteContext parent = array.clearAndGetParent();
        assertSame(root, parent);
        assertNull(array.currentValue());
    }

    void contextTypeDescriptionsVpack() {
        SimpleStreamWriteContext root = SimpleStreamWriteContext.createRootContext(null);
        assertEquals("root", root.typeDesc());

        SimpleStreamWriteContext array = root.createChildArrayContext(null);
        assertEquals("Array", array.typeDesc());

        SimpleStreamWriteContext object = root.createChildObjectContext(null);
        assertEquals("Object", object.typeDesc());
    }

    void contextWithoutDupDetectorVpack() throws Exception {
        SimpleStreamWriteContext object = SimpleStreamWriteContext.createRootContext(null)
                .createChildObjectContext(null);
        assertNull(object.getDupDetector());

        object.writeName("field");
        object.writeValue();
        object.writeName("field");
        object.writeValue();
    }

    void childContextWithNoDupDetectorInParentVpack() {
        SimpleStreamWriteContext root = SimpleStreamWriteContext.createRootContext(null);
        SimpleStreamWriteContext child = root.createChildObjectContext(null);

        assertNull(child.getDupDetector());
    }

    void childContextWithDupDetectorInParentVpack() {
        DupDetector detector = DupDetector.rootDetector((JsonGenerator) null);
        SimpleStreamWriteContext root = SimpleStreamWriteContext.createRootContext(detector);
        SimpleStreamWriteContext child = root.createChildObjectContext(null);

        assertNotNull(child.getDupDetector());
        assertNotSame(detector, child.getDupDetector());
    }

    void childContextRecyclingResetsStateVpack() throws Exception {
        SimpleStreamWriteContext root = SimpleStreamWriteContext.createRootContext(null);

        SimpleStreamWriteContext object1 = root.createChildObjectContext("value1");
        object1.writeName("name1");
        object1.writeValue();

        assertEquals("name1", object1.currentName());
        assertEquals("value1", object1.currentValue());
        assertFalse(object1.hasCurrentName());
        assertEquals(0, object1.getCurrentIndex());

        object1.clearAndGetParent();
        SimpleStreamWriteContext object2 = root.createChildObjectContext("value2");

        assertNull(object2.currentName());
        assertEquals("value2", object2.currentValue());
        assertFalse(object2.hasCurrentName());
        assertEquals(0, object2.getCurrentIndex());
    }

    void currentNameAccessibleAfterNewScopeVpack() throws Exception {
        SimpleStreamWriteContext object = SimpleStreamWriteContext.createRootContext(null)
                .createChildObjectContext(null);

        object.writeName("outerField");
        assertEquals("outerField", object.currentName());

        object.createChildArrayContext(null);
        assertEquals("outerField", object.currentName());
    }

    void __invoke_createRootContextVpack() throws Exception {
        try {
            createRootContextVpack();
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


    void __invoke_childContextRecyclingVpack() throws Exception {
        try {
            childContextRecyclingVpack();
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


    void __invoke_contextWithoutDupDetectorVpack() throws Exception {
        try {
            contextWithoutDupDetectorVpack();
        } finally {
        }
    }


    void __invoke_childContextWithNoDupDetectorInParentVpack() throws Exception {
        try {
            childContextWithNoDupDetectorInParentVpack();
        } finally {
        }
    }


    void __invoke_childContextWithDupDetectorInParentVpack() throws Exception {
        try {
            childContextWithDupDetectorInParentVpack();
        } finally {
        }
    }


    void __invoke_childContextRecyclingResetsStateVpack() throws Exception {
        try {
            childContextRecyclingResetsStateVpack();
        } finally {
        }
    }


    void __invoke_currentNameAccessibleAfterNewScopeVpack() throws Exception {
        try {
            currentNameAccessibleAfterNewScopeVpack();
        } finally {
        }
    }

}
