package tools.jackson.databind.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.JsonNode;

import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0461F0 {
private static final VPackMapper MAPPER = new VPackMapper();
private static final byte[] ONE_ELEMENT_ARRAY = VPackWireFixtureTest.hex(
            "02 04 28 01");
private static final byte[] WRAPPED_SUBTYPE = VPackWireFixtureTest.hex(
            "14 21 4b 68 61 73 53 75 62 54 79 70 65 73 "
          + "14 12 43 6f 6e 65 14 0b 42 69 64 44 74 65 73 74 01 01 01");
private static final byte[] DATE_1 = VPackWireFixtureTest.hex(
            "54 32 30 31 37 2d 30 31 2d 30 31 54 31 36 3a 33 30 3a 34 39 5a");
private static final byte[] DATE_2 = VPackWireFixtureTest.hex(
            "54 32 30 31 37 2d 30 31 2d 30 32 54 31 36 3a 33 30 3a 34 39 5a");
private static final byte[] DATE_3 = VPackWireFixtureTest.hex(
            "54 32 30 31 37 2d 30 31 2d 30 33 54 31 36 3a 33 30 3a 34 39 5a");
private static final byte[] DATE_4 = VPackWireFixtureTest.hex(
            "54 32 30 31 37 2d 30 31 2d 30 34 54 31 36 3a 33 30 3a 34 39 5a");

    // Provenance: RaceCondition738Test#testRepeatedly().
    void testRepeatedlyVpack() throws Exception {
        JsonNode literal = MAPPER.readTree(WRAPPED_SUBTYPE);
        assertTrue(literal.get("hasSubTypes").has("one"));

        final int count = 3000;
        for (int i = 0; i < count; ++i) {
            runSerializationRound(i, count);
        }
    }
private void runSerializationRound(int round, int max) throws Exception {
        Callable<byte[]> writeVPack = () -> MAPPER.writeValueAsBytes(
                new Wrapper(new TypeOne("test")));

        ExecutorService executor = Executors.newFixedThreadPool(4);
        List<Future<byte[]>> futures = new ArrayList<>();
        for (int i = 0; i < 4; ++i) {
            futures.add(executor.submit(writeVPack));
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS),
                "serialization round timed out: " + round + "/" + max);

        for (Future<byte[]> future : futures) {
            JsonNode tree = MAPPER.readTree(future.get());
            JsonNode wrapped = tree.get("hasSubTypes");
            if (!wrapped.has("one")) {
                throw new IllegalStateException("Round #" + round + "/" + max
                        + "; missing property 'one'");
            }
        }
    }
static abstract class AbstractHasSubTypes implements HasSubTypes { }
static final class TypeOne extends AbstractHasSubTypes {
        private final String id;

        TypeOne(String id) {
            this.id = id;
        }

        @JsonProperty
        public String getId() {
            return id;
        }

        @Override
        public String getType() {
            return TypeOne.class.getSimpleName();
        }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
    @JsonSubTypes(@JsonSubTypes.Type(value = TypeOne.class, name = "one"))
    interface HasSubTypes {
        String getType();
    }
static final class Wrapper {
        private final HasSubTypes hasSubTypes;

        Wrapper(HasSubTypes hasSubTypes) {
            this.hasSubTypes = hasSubTypes;
        }

        @JsonProperty
        public HasSubTypes getHasSubTypes() {
            return hasSubTypes;
        }
    }

    void __invoke_testRepeatedlyVpack() throws Exception {
        try {
            testRepeatedlyVpack();
        } finally {
        }
    }

}
