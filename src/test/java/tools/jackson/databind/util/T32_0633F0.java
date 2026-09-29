package tools.jackson.databind.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

import tools.jackson.core.JsonGenerator;

import static org.junit.jupiter.api.Assertions.*;

import tools.jackson.dataformat.velocypack.*;

class T32_0633F0 {
private final VPackFactory factory = new VPackFactory();
private final VPackMapper mapper = new VPackMapper();

    void shortNegativeTimestampParsesFromVPackIntegers() throws Exception {
        assertEquals(-1L, mapper.readValue(new byte[] { 0x3f }, Date.class).getTime());
        // Independent signed little-endian 4-byte wire value for -1,234,567,890.
        assertEquals(-1_234_567_890L, mapper.readValue(
                new byte[] { 0x23, 0x2e, (byte) 0xfd, 0x69, (byte) 0xb6 }, Date.class).getTime());
    }
private byte[] writeNumberText(String text) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = factory.createGenerator(output)) {
            generator.writeNumber(text);
        }
        return output.toByteArray();
    }

    void shortNegativeTimestampTextParsingIsStillDatabindBehavior() throws Exception {
        // Preserve the source assertion on StdDateFormat itself; VPack coverage above
        // independently checks the equivalent negative numeric timestamp values.
        var format = tools.jackson.databind.util.StdDateFormat.instance.clone();
        format.setLenient(false);
        assertEquals(-1L, format.parse("-1").getTime());
        assertEquals(-1_234_567_890L, format.parse("-1234567890").getTime());
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

    void __invoke_shortNegativeTimestampParsesFromVPackIntegers() throws Exception {
        try {
            shortNegativeTimestampParsesFromVPackIntegers();
        } finally {
        }
    }


    void __invoke_shortNegativeTimestampTextParsingIsStillDatabindBehavior() throws Exception {
        try {
            shortNegativeTimestampTextParsingIsStillDatabindBehavior();
        } finally {
        }
    }

}
