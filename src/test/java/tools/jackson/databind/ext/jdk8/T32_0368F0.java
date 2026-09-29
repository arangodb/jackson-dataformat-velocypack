package tools.jackson.databind.ext.jdk8;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.TimeZone;
import java.util.stream.DoubleStream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0368F0 {
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

    // Provenance: ContextualOptionalTest#testContextualOptionals().
    void testContextualOptionalsVpack() throws Exception {
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        ObjectMapper mapper = VPackMapper.builder().defaultDateFormat(df).build();

        ContextualOptionals input = new ContextualOptionals();
        input.date = Optional.of(new Date(0L));
        input.date1 = Optional.of(new Date(0L));
        input.date2 = Optional.of(new Date(0L));

        assertArrayEquals(CONTEXTUAL_OPTIONALS, mapper.writeValueAsBytes(input));
        ContextualOptionals result = mapper.readValue(CONTEXTUAL_OPTIONALS,
                ContextualOptionals.class);
        assertEquals(new Date(0L), result.date.get());
        assertEquals(new Date(0L), result.date1.get());
        assertEquals(new Date(0L), result.date2.get());
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

    void __invoke_testContextualOptionalsVpack() throws Exception {
        try {
            testContextualOptionalsVpack();
        } finally {
        }
    }

}
