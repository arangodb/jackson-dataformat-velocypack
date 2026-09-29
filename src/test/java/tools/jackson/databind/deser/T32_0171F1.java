package tools.jackson.databind.deser;

import java.io.ByteArrayOutputStream;

import tools.jackson.databind.DatabindException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.DeferredBindingException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0171F1 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] UNKNOWN_TILDE = VPackWireFixtureTest.hex(
            "0b 17 01 4a 66 69 65 6c 64 7e 6e 61 6d 65"
            + "47 69 6e 76 61 6c 69 64 03");
private static final byte[] UNKNOWN_SLASH = VPackWireFixtureTest.hex(
            "0b 17 01 4a 66 69 65 6c 64 2f 6e 61 6d 65"
            + "47 69 6e 76 61 6c 69 64 03");
private static final byte[] UNKNOWN_BOTH = VPackWireFixtureTest.hex(
            "0b 18 01 4b 66 69 65 6c 64 7e 2f 6e 61 6d 65"
            + "47 69 6e 76 61 6c 69 64 03");
private static final byte[] ARRAY_INDICES = VPackWireFixtureTest.hex(
            "14 5f 47 6f 72 64 65 72 49 64 28 7b 45 69 74 65 6d 73"
            + "13 4c 14 23 43 73 6b 75 43 41 42 43 45 70 72 69 63 65"
            + "47 69 6e 76 61 6c 69 64 48 71 75 61 6e 74 69 74 79 35 03"
            + "14 26 43 73 6b 75 43 44 45 46 45 70 72 69 63 65"
            + "c8 02 fe ff ff ff 99 99 48 71 75 61 6e 74 69 74 79 43 62 61 64 03"
            + "02 02");

    void suppressedProblems() {
        DeferredBindingException exception = expectCollected(
                invalidOrderFixture(101), Order.class);

        assertEquals(100, exception.getProblems().size());
        assertTrue(exception.isLimitReached());
        assertTrue(exception.getSuppressed().length >= 1);
        assertTrue(exception.getSuppressed()[0] instanceof DatabindException);
    }
private static void assertPointer(byte[] fixture, String expectedPath) {
        DeferredBindingException exception = expectCollected(fixture, JsonPointerTestBean.class);
        assertEquals(1, exception.getProblems().size());
        assertEquals(expectedPath, exception.getProblems().get(0).getPath().toString());
    }
private static DeferredBindingException expectCollected(byte[] fixture,
            Class<?> valueType) {
        try {
            MAPPER.readerFor(valueType).problemCollectingReader()
                    .readValueCollectingProblems(fixture);
        } catch (DeferredBindingException exception) {
            return exception;
        } catch (Exception exception) {
            throw new AssertionError("Unexpected exception", exception);
        }
        throw new AssertionError("Expected DeferredBindingException");
    }
private static byte[] invalidOrderFixture(int itemCount) {
        byte[] item = VPackWireFixtureTest.hex(
                "14 11 45 70 72 69 63 65 47 69 6e 76 61 6c 69 64 01");
        ByteArrayOutputStream itemsBody = new ByteArrayOutputStream(item.length * itemCount);
        for (int i = 0; i < itemCount; ++i) {
            itemsBody.writeBytes(item);
        }
        byte[] items = compact(0x13, itemsBody.toByteArray(), itemCount);
        byte[] rootBody = concat(VPackWireFixtureTest.hex("45 69 74 65 6d 73"), items);
        return compact(0x14, rootBody, 1);
    }
private static byte[] compact(int marker, byte[] body, int count) {
        int length = 1 + body.length + 2;
        while (length != 1 + forwardVarint(length).length + body.length + 1) {
            length = 1 + forwardVarint(length).length + body.length + 1;
        }
        ByteArrayOutputStream result = new ByteArrayOutputStream(length);
        result.write(marker);
        result.writeBytes(forwardVarint(length));
        result.writeBytes(body);
        result.writeBytes(forwardVarint(count));
        return result.toByteArray();
    }
private static byte[] forwardVarint(int value) {
        ByteArrayOutputStream result = new ByteArrayOutputStream(3);
        while (value >= 128) {
            result.write((value & 0x7f) | 0x80);
            value >>>= 7;
        }
        result.write(value);
        return result.toByteArray();
    }
private static byte[] concat(byte[] first, byte[] second) {
        byte[] result = new byte[first.length + second.length];
        System.arraycopy(first, 0, result, 0, first.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
static class Person {
        public String name;
        public int age;
        public boolean active;
    }
static class Order {
        public int orderId;
        public java.util.List<Item> items;
    }
static class Item {
        public String sku;
        public double price;
        public int quantity;
    }
static class JsonPointerTestBean {
        public String normalField;
        public String fieldWithSlash;
        public String fieldWithTilde;
        public String fieldWithBoth;
    }

    void __invoke_suppressedProblems() throws Exception {
        try {
            suppressedProblems();
        } finally {
        }
    }

}
