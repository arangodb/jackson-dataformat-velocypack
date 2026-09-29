package tools.jackson.databind.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import tools.jackson.core.JsonGenerator;

import static org.junit.jupiter.api.Assertions.*;

import tools.jackson.dataformat.velocypack.*;

class T32_0633F1 {
private final VPackFactory factory = new VPackFactory();
private final VPackMapper mapper = new VPackMapper();

    void jsonpScalarPayloadsHaveEquivalentVPackValues() throws Exception {
        assertEquals("abc", mapper.readValue(mapper.writeValueAsBytes("abc"), String.class));
        assertEquals(123, mapper.readValue(mapper.writeValueAsBytes(123), Integer.class));
        assertNull(mapper.readValue(mapper.writeValueAsBytes(null), Object.class));
    }

    void jsonpBeanPayloadHasEquivalentVPackValue() throws Exception {
        Impl input = new Impl("123", "456");
        Impl result = mapper.readValue(mapper.writeValueAsBytes(input), Impl.class);
        assertEquals(input.a, result.a);
        assertEquals(input.b, result.b);
    }

    void jsonpStaticTypePayloadHasEquivalentVPackValue() throws Exception {
        Impl input = new Impl("abc", "def");
        byte[] encoded = mapper.writerFor(Base.class).writeValueAsBytes(input);
        Base result = mapper.readValue(encoded, Base.class);
        assertEquals("abc", result.a);
        assertFalse(mapper.readValue(encoded, java.util.Map.class).containsKey("b"));
    }
private byte[] writeNumberText(String text) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = factory.createGenerator(output)) {
            generator.writeNumber(text);
        }
        return output.toByteArray();
    }
static class Base { public String a; }
static class Impl extends Base {
        public String b;
        Impl() { }
        Impl(String a, String b) { this.a = a; this.b = b; }
    }
static class Point {
        public int x;
        public int y;
        Point() { }
        Point(int x, int y) { this.x = x; this.y = y; }
        @Override public boolean equals(Object other) {
            return other instanceof Point p && x == p.x && y == p.y;
        }
        @Override public int hashCode() { return 31 * x + y; }
    }

    void __invoke_jsonpScalarPayloadsHaveEquivalentVPackValues() throws Exception {
        try {
            jsonpScalarPayloadsHaveEquivalentVPackValues();
        } finally {
        }
    }


    void __invoke_jsonpBeanPayloadHasEquivalentVPackValue() throws Exception {
        try {
            jsonpBeanPayloadHasEquivalentVPackValue();
        } finally {
        }
    }


    void __invoke_jsonpStaticTypePayloadHasEquivalentVPackValue() throws Exception {
        try {
            jsonpStaticTypePayloadHasEquivalentVPackValue();
        } finally {
        }
    }

}
