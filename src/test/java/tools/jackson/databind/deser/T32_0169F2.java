package tools.jackson.databind.deser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.exc.DeferredBindingException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0169F2 {
private static final byte[] TYPE_ANNOTATION = VPackWireFixtureTest.hex(
            "0b 0e 01 45 76 61 6c 75 65 43 61 62 63 03");
private static final byte[] SPECIALIZATION = VPackWireFixtureTest.hex(
            "0b 11 01 44 6c 69 73 74 02 08 41 61 41 62 41 63 03");
private static final byte[] STATIC_SETTER = VPackWireFixtureTest.hex(
            "0b 07 01 41 78 33 03");
private static final byte[] MAP_INPUT = VPackWireFixtureTest.hex(
            "0b 0f 01 43 6d 61 70 0b 07 01 41 61 31 03 03");
private static final byte[] LIST_INPUT = VPackWireFixtureTest.hex(
            "0b 0c 01 44 6c 69 73 74 02 03 31 03");
private static final byte[] SUCCESSIVE_ALICE = VPackWireFixtureTest.hex(
            "0b 20 02 44 6e 61 6d 65 48 69 6e 76 61 6c 69 64 31 43 61 67 65 48 69 6e 76 61 6c 69 64 31 11 03");
private static final byte[] SUCCESSIVE_BOB = VPackWireFixtureTest.hex(
            "0b 20 02 44 6e 61 6d 65 48 69 6e 76 61 6c 69 64 32 43 61 67 65 48 69 6e 76 61 6c 69 64 32 11 03");
private static final byte[][] CONCURRENT_INPUTS = {
        VPackWireFixtureTest.hex("0b 20 02 44 6e 61 6d 65 48 69 6e 76 61 6c 69 64 30 43 61 67 65 48 69 6e 76 61 6c 69 64 30 11 03"),
        VPackWireFixtureTest.hex("0b 20 02 44 6e 61 6d 65 48 69 6e 76 61 6c 69 64 31 43 61 67 65 48 69 6e 76 61 6c 69 64 31 11 03"),
        VPackWireFixtureTest.hex("0b 20 02 44 6e 61 6d 65 48 69 6e 76 61 6c 69 64 32 43 61 67 65 48 69 6e 76 61 6c 69 64 32 11 03"),
        VPackWireFixtureTest.hex("0b 20 02 44 6e 61 6d 65 48 69 6e 76 61 6c 69 64 33 43 61 67 65 48 69 6e 76 61 6c 69 64 33 11 03"),
        VPackWireFixtureTest.hex("0b 20 02 44 6e 61 6d 65 48 69 6e 76 61 6c 69 64 34 43 61 67 65 48 69 6e 76 61 6c 69 64 34 11 03"),
        VPackWireFixtureTest.hex("0b 20 02 44 6e 61 6d 65 48 69 6e 76 61 6c 69 64 35 43 61 67 65 48 69 6e 76 61 6c 69 64 35 11 03"),
        VPackWireFixtureTest.hex("0b 20 02 44 6e 61 6d 65 48 69 6e 76 61 6c 69 64 36 43 61 67 65 48 69 6e 76 61 6c 69 64 36 11 03"),
        VPackWireFixtureTest.hex("0b 20 02 44 6e 61 6d 65 48 69 6e 76 61 6c 69 64 37 43 61 67 65 48 69 6e 76 61 6c 69 64 37 11 03"),
        VPackWireFixtureTest.hex("0b 20 02 44 6e 61 6d 65 48 69 6e 76 61 6c 69 64 38 43 61 67 65 48 69 6e 76 61 6c 69 64 38 11 03"),
        VPackWireFixtureTest.hex("0b 20 02 44 6e 61 6d 65 48 69 6e 76 61 6c 69 64 39 43 61 67 65 48 69 6e 76 61 6c 69 64 39 11 03")
    };
private static final ObjectMapper MAPPER = new VPackMapper();

    void successiveCalls() {
        ObjectReader reader = MAPPER.readerFor(Person.class)
                .problemCollectingReader();
        DeferredBindingException first = expectDeferredBinding(reader,
                SUCCESSIVE_ALICE);
        DeferredBindingException second = expectDeferredBinding(reader,
                SUCCESSIVE_BOB);
        assertNotNull(first);
        assertNotNull(second);
        assertEquals(1, first.getProblems().size());
        assertEquals(1, second.getProblems().size());
        assertEquals("invalid1", first.getProblems().get(0).getRawValue());
        assertEquals("invalid2", second.getProblems().get(0).getRawValue());
    }

    void concurrentCalls() throws Exception {
        ObjectReader reader = MAPPER.readerFor(Person.class)
                .problemCollectingReader();
        int threadCount = CONCURRENT_INPUTS.length;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        List<DeferredBindingException> exceptions =
                Collections.synchronizedList(new ArrayList<>());
        List<Throwable> unexpected =
                Collections.synchronizedList(new ArrayList<>());
        try {
            for (byte[] input : CONCURRENT_INPUTS) {
                executor.submit(() -> {
                    try {
                        reader.readValueCollectingProblems(input);
                        unexpected.add(new AssertionError(
                                "Expected DeferredBindingException"));
                    } catch (DeferredBindingException e) {
                        exceptions.add(e);
                    } catch (Throwable t) {
                        unexpected.add(t);
                    } finally {
                        latch.countDown();
                    }
                });
            }
            assertTrue(latch.await(5, TimeUnit.SECONDS));
        } finally {
            executor.shutdown();
            assertTrue(executor.awaitTermination(2, TimeUnit.SECONDS));
        }
        assertTrue(unexpected.isEmpty(), unexpected.toString());
        assertEquals(threadCount, exceptions.size());
        List<String> rawValues = new ArrayList<>();
        synchronized (exceptions) {
            for (DeferredBindingException exception : exceptions) {
                assertEquals(1, exception.getProblems().size());
                rawValues.add((String) exception.getProblems().get(0).getRawValue());
            }
        }
        assertEquals(List.of("invalid0", "invalid1", "invalid2", "invalid3",
                "invalid4", "invalid5", "invalid6", "invalid7", "invalid8",
                "invalid9"), rawValues.stream().sorted().toList());
    }
private static DeferredBindingException expectDeferredBinding(ObjectReader reader,
            byte[] input) {
        try {
            reader.readValueCollectingProblems(input);
        } catch (DeferredBindingException e) {
            return e;
        } catch (Exception e) {
            throw new AssertionError("Unexpected exception", e);
        }
        throw new AssertionError("Expected DeferredBindingException");
    }
static class Abstract { }
static class Concrete extends Abstract {
        String value;

        public Concrete(String value) {
            this.value = value;
        }
    }
static class AbstractWrapper {
        @JsonDeserialize(as = Concrete.class)
        public Abstract value;
    }
static class BaseListBean {
        List<String> list;

        public void setList(List<String> value) {
            list = value;
        }
    }
static class ArrayListBean extends BaseListBean {
        public void setList(ArrayList<String> value) {
            super.setList(value);
        }
    }
static class StaticSetterBean {
        int x;

        public static void setX(int value) {
            throw new AssertionError("Static setter must not be called");
        }

        @com.fasterxml.jackson.annotation.JsonProperty("x")
        public void assignX(int value) {
            x = value;
        }
    }
public static class TestListWithCustom {
        @tools.jackson.databind.annotation.JsonDeserialize(
                contentUsing = CustomDeserializer.class)
        public List<Integer> list;
    }
public static class TestListNoCustom {
        public List<Integer> list;
    }
public static class TestMapWithCustom {
        @JsonDeserialize(contentUsing = CustomDeserializer.class)
        public Map<String, Integer> map;
    }
public static class TestMapNoCustom {
        public Map<String, Integer> map;
    }
public static class CustomDeserializer extends StdDeserializer<Integer> {
        public CustomDeserializer() {
            super(Integer.class);
        }

        @Override
        public Integer deserialize(JsonParser parser, DeserializationContext context) {
            return 100 * parser.getValueAsInt();
        }
    }
static class Person {
        public String name;
        public int age;
    }

    void __invoke_successiveCalls() throws Exception {
        try {
            successiveCalls();
        } finally {
        }
    }


    void __invoke_concurrentCalls() throws Exception {
        try {
            concurrentCalls();
        } finally {
        }
    }

}
