package tools.jackson.databind.deser;

import java.io.ByteArrayOutputStream;
import java.util.List;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.exc.DeferredBindingException;

import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0172F1 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] UNDER_LIMIT = VPackWireFixtureTest.hex(
            "14 19 44 6e 61 6d 65 44 4a 6f 68 6e 43 61 67 65 47 69 6e 76 61 6c 69 64 02");
private static final byte[] ROOT_STRING = VPackWireFixtureTest.hex(
            "4d 6e 6f 74 2d 61 6e 2d 6f 62 6a 65 63 74");

    void multipleErrors() {
        DeferredBindingException exception = expectCollected(
                invalidOrderFixture(10), Order.class);

        assertTrue(exception.getMessage().contains("10 deserialization problems"));
        assertTrue(exception.getMessage().contains("showing first 5"));
        assertTrue(exception.getMessage().contains("... and 5 more"));
    }

    void singleError() {
        DeferredBindingException exception = expectCollected(
                UNDER_LIMIT, Person.class);

        assertTrue(exception.getMessage().contains("1 deserialization problem"));
    }
private static DeferredBindingException expectCollected(byte[] fixture,
            Class<?> valueType) {
        return expectCollected(fixture, valueType, null);
    }
private static DeferredBindingException expectCollected(byte[] fixture,
            Class<?> valueType, Integer maxProblems) {
        try {
            ObjectReader reader = MAPPER.readerFor(valueType).problemCollectingReader();
            if (maxProblems != null) {
                reader = MAPPER.readerFor(valueType).problemCollectingReader(maxProblems);
            }
            reader.readValueCollectingProblems(fixture);
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
        public List<Item> items;
    }
static class Item {
        public String sku;
        public double price;
        public int quantity;
    }

    void __invoke_multipleErrors() throws Exception {
        try {
            multipleErrors();
        } finally {
        }
    }


    void __invoke_singleError() throws Exception {
        try {
            singleError();
        } finally {
        }
    }

}
