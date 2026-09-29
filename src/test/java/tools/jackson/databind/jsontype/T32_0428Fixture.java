package tools.jackson.databind.jsontype;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0428Fixture {
private static final ObjectMapper MAPPER = new VPackMapper();

    // Provenance: TestSubtypesSubPackage#testInner().
    void testInnerVpack() throws Exception {
        SuperType428.InnerType428 bean = new SuperType428.InnerType428();
        Map<?, ?> encoded = MAPPER.readValue(MAPPER.writeValueAsBytes(bean), Map.class);
        assertEquals(".T32_0428Fixture$SuperType428$InnerType428", encoded.get("@c"));
        assertEquals(2, encoded.get("b"));
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS)
    static abstract class SuperType428 {
        static class InnerType428 extends SuperType428 {
            public int b = 2;
        }
    }

    void __invoke_testInnerVpack() throws Exception {
        try {
            testInnerVpack();
        } finally {
        }
    }

}
