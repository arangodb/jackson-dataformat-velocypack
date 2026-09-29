package tools.jackson.core.unittest.util;

import tools.jackson.core.util.SimpleStreamWriteContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class T32_0115F0 {

    void writeValueInObjectContextVpack() throws Exception {
        SimpleStreamWriteContext object = SimpleStreamWriteContext.createRootContext(null)
                .createChildObjectContext(null);

        assertFalse(object.writeValue(), "writeValue should return false without property name");
        assertEquals(0, object.getCurrentIndex());

        assertTrue(object.writeName("field1"));
        assertTrue(object.writeValue());
        assertEquals(0, object.getCurrentIndex());
        assertFalse(object.hasCurrentName());

        assertFalse(object.writeValue());
        assertEquals(0, object.getCurrentIndex());

        assertTrue(object.writeName("field2"));
        assertTrue(object.writeValue());
        assertEquals(1, object.getCurrentIndex());
    }

    void __invoke_writeValueInObjectContextVpack() throws Exception {
        try {
            writeValueInObjectContextVpack();
        } finally {
        }
    }

}
