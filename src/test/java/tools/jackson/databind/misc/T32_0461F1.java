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
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0461F1 {
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

    // Provenance: TestBlocking#testEagerAdvance().
    void testEagerAdvanceVpack() throws Exception {
        VPackMapper mapper = VPackMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
                .build();
        try (JsonParser parser = mapper.tokenStreamFactory().createParser(ONE_ELEMENT_ARRAY)) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());

            assertEquals(Integer.valueOf(1), mapper.readValue(parser, Integer.class));

            // VPack is self-delimiting: mapping the current value must leave the
            // enclosing array's END token available to the caller.
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            assertEquals(null, parser.nextToken());
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

    void __invoke_testEagerAdvanceVpack() throws Exception {
        try {
            testEagerAdvanceVpack();
        } finally {
        }
    }

}
