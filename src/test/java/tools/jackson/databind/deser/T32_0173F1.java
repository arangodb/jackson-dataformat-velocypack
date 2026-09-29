package tools.jackson.databind.deser;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.exc.DeferredBindingException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0173F1 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] UNKNOWN_PROPERTY = VPackWireFixtureTest.hex(
            "14 27 44 6e 61 6d 65 45 41 6c 69 63 65"
          + "4c 75 6e 6b 6e 6f 77 6e 46 69 65 6c 64 45 76 61 6c 75 65"
          + "43 61 67 65 28 1e 03");
private static final byte[] UNKNOWN_OBJECT = VPackWireFixtureTest.hex(
            "14 30 44 6e 61 6d 65 43 42 6f 62"
          + "4d 75 6e 6b 6e 6f 77 6e 4f 62 6a 65 63 74"
          + "14 10 46 6e 65 73 74 65 64 45 76 61 6c 75 65 01"
          + "43 61 67 65 28 19 03");
private static final byte[] COMMON_VALUE_ONE = VPackWireFixtureTest.hex(
            "14 15 48 70 72 6f 70 65 72 74 79 48 76 61 6c 75 65 4f 6e 65 01");
private static final byte[] COMMON_VALUE_TWO_CONTAINER = VPackWireFixtureTest.hex(
            "14 1f 46 63 6f 6d 6d 6f 6e"
          + "14 15 48 70 72 6f 70 65 72 74 79 48 76 61 6c 75 65 54 77 6f 01 01");
private static final byte[] NULL_LIST = VPackWireFixtureTest.hex(
            "14 0b 46 6d 79 4c 69 73 74 18 01");

    void testDeserFailing() throws Exception {
        Common3355 object = MAPPER.readValue(COMMON_VALUE_ONE, Common3355.class);
        ContainerFail3355 container = MAPPER.readValue(
                COMMON_VALUE_TWO_CONTAINER, ContainerFail3355.class);

        assertNotNull(object);
        assertNotNull(container);
    }

    void testDeserPassing() throws Exception {
        ContainerFail3355 container = MAPPER.readValue(
                COMMON_VALUE_TWO_CONTAINER, ContainerFail3355.class);
        Common3355 object = MAPPER.readValue(COMMON_VALUE_ONE, Common3355.class);

        assertNotNull(object);
        assertNotNull(container);
    }
private static DeferredBindingException expectCollected(byte[] fixture,
            Class<?> valueType) {
        try {
            ObjectReader reader = MAPPER.readerFor(valueType)
                    .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .problemCollectingReader();
            reader.readValueCollectingProblems(fixture);
        } catch (DeferredBindingException exception) {
            return exception;
        } catch (Exception exception) {
            throw new AssertionError("Unexpected exception", exception);
        }
        throw new AssertionError("Expected DeferredBindingException");
    }
private static void verifyGetNullValueInvokedTimes(ObjectMapper mapper,
            int times) throws Exception {
        Bean4225 bean = mapper.readValue(NULL_LIST, Bean4225.class);

        assertEquals(1, bean.myList.size());
        assertEquals("nullVal_" + times, bean.myList.get(0));
        assertEquals(times, CustomListDeserializer.getNullValueInvocationCount);
    }
static class Person {
        public String name;
        public int age;
        public boolean active;
    }
static class Common3355 {
        private final String property;
        private final ContainerFail3355 container;

        @JsonCreator
        public Common3355(@JsonProperty("property") String property,
                @JsonProperty("container") ContainerFail3355 container) {
            this.property = property;
            this.container = container;
        }

        public String getProperty() {
            return property;
        }

        public ContainerFail3355 getContainer() {
            return container;
        }
    }
static class ContainerFail3355 {
        private final Common3355 common;

        @JsonCreator
        public ContainerFail3355(@JsonProperty("common") Common3355 common) {
            this.common = common;
        }

        @JsonIgnoreProperties("container")
        public Common3355 getCommon() {
            return common;
        }
    }
static class CustomListDeserializer extends ValueDeserializer<List<String>> {
        static int getNullValueInvocationCount;

        @Override
        public List<String> deserialize(JsonParser parser, tools.jackson.databind.DeserializationContext ctxt)
                throws JacksonException {
            return makeList("regular");
        }

        @Override
        public List<String> getNullValue(tools.jackson.databind.DeserializationContext ctxt)
                throws JacksonException {
            ++getNullValueInvocationCount;
            return makeList("nullVal_" + getNullValueInvocationCount);
        }

        private List<String> makeList(String content) {
            List<String> result = new ArrayList<>();
            result.add(content);
            return result;
        }
    }
static class Bean4225 {
        @JsonDeserialize(using = CustomListDeserializer.class)
        public List<String> myList;
    }

    void __invoke_testDeserFailing() throws Exception {
        try {
            testDeserFailing();
        } finally {
        }
    }


    void __invoke_testDeserPassing() throws Exception {
        try {
            testDeserPassing();
        } finally {
        }
    }

}
