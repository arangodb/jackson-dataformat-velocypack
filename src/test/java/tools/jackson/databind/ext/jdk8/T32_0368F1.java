package tools.jackson.databind.ext.jdk8;

import java.util.Date;
import java.util.Optional;
import java.util.stream.DoubleStream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0368F1 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] CONTEXTUAL_OPTIONALS = VPackWireFixtureTest.hex(
            "0b 38 03 44 64 61 74 65 4a 31 39 37 30 2f 30 31 2f 30 31 "
            + "45 64 61 74 65 31 4a 31 39 37 30 2b 30 31 2b 30 31 "
            + "45 64 61 74 65 32 4a 31 39 37 30 2a 30 31 2a 30 31 03 13 24");
private static final byte[] CREATOR_WITH_OPTIONAL = VPackWireFixtureTest.hex(
            "0b 0a 01 41 61 43 66 6f 6f 03");
private static final byte[] EMPTY_DOUBLE_STREAM = VPackWireFixtureTest.hex("01");
private static final byte[] SINGLE_DOUBLE_STREAM = VPackWireFixtureTest.hex(
            "02 0b 1b 00 00 00 00 00 00 f0 3f");
private static final byte[] MULTI_DOUBLE_STREAM = VPackWireFixtureTest.hex(
            "02 38 "
            + "1b 01 00 00 00 00 00 00 00 "
            + "1b ff ff ff ff ff ff ef 7f "
            + "1b 00 00 00 00 00 00 f0 3f "
            + "1b 00 00 00 00 00 00 00 00 "
            + "1b 00 00 00 00 00 00 18 40 "
            + "1b 00 00 00 00 00 00 08 c0");
private static final byte[] SINGLE_NEGATIVE_DOUBLE_STREAM = VPackWireFixtureTest.hex(
            "02 0b 1b 00 00 00 00 00 00 f8 bf");
private static final byte[] BOUNDARY_DOUBLE_STREAM = VPackWireFixtureTest.hex(
            "02 1d "
            + "1b 01 00 00 00 00 00 00 00 "
            + "1b 00 00 00 00 00 00 00 00 "
            + "1b ff ff ff ff ff ff ef 7f");
private static final byte[] WRAPPED_DOUBLE_STREAM = VPackWireFixtureTest.hex(
            "0b 27 01 45 76 61 6c 75 65 02 1d "
            + "1b 9a 99 99 99 99 99 f1 3f "
            + "1b 9a 99 99 99 99 99 01 40 "
            + "1b 66 66 66 66 66 66 0a 40 03");
private static final byte[] WRAPPED_EMPTY_DOUBLE_STREAM = VPackWireFixtureTest.hex(
            "0b 0b 01 45 76 61 6c 75 65 01 03");

    // Provenance: CreatorForOptionalTest#testCreatorWithOptional().
    void testCreatorWithOptionalVpack() throws Exception {
        ObjectReader reader = MAPPER.readerFor(CreatorWithOptionalStrings.class);
        CreatorWithOptionalStrings bean = reader.readValue(CREATOR_WITH_OPTIONAL);
        assertNotNull(bean);
        assertNotNull(bean.a);
        assertTrue(bean.a.isPresent());
        assertEquals("foo", bean.a.get());
        assertEquals(Optional.empty(), bean.b);

        bean = reader.with(DeserializationFeature.USE_NULL_FOR_MISSING_REFERENCE_VALUES)
                .readValue(CREATOR_WITH_OPTIONAL);
        assertNotNull(bean);
        assertNotNull(bean.a);
        assertTrue(bean.a.isPresent());
        assertNull(bean.b);
    }
@JsonPropertyOrder({ "date", "date1", "date2" })
    static class ContextualOptionals {
        public Optional<Date> date;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy+MM+dd")
        public Optional<Date> date1;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy*MM*dd")
        public Optional<Date> date2;
    }
static class CreatorWithOptionalStrings {
        Optional<String> a;
        Optional<String> b;

        @JsonCreator
        public CreatorWithOptionalStrings(@JsonProperty("a") Optional<String> a,
                @JsonProperty("b") Optional<String> b) {
            this.a = a;
            this.b = b;
        }
    }
static class DoubleStreamWrapper {
        public DoubleStream value;

        public DoubleStreamWrapper() { }

        DoubleStreamWrapper(DoubleStream value) {
            this.value = value;
        }
    }

    void __invoke_testCreatorWithOptionalVpack() throws Exception {
        try {
            testCreatorWithOptionalVpack();
        } finally {
        }
    }

}
