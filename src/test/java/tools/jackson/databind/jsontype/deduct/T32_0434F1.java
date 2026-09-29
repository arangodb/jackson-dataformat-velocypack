package tools.jackson.databind.jsontype.deduct;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonValue;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.DateTimeFeature;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0434F1 {
private static final ObjectMapper MAPPER = VPackMapper.builder()
            .enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
            .build();
private static final byte[] LIVE_CAT = VPackWireFixtureTest.hex(
            "14 15 45 61 6e 67 72 79 1a 44 6e 61 6d 65 45 46 65 6c 69 78 02");
private static final byte[] DEAD_CAT = VPackWireFixtureTest.hex(
            "14 23 4c 63 61 75 73 65 4f 66 44 65 61 74 68 47 65 6e 74 72 6f 70 79 "
          + "44 6e 61 6d 65 45 46 65 6c 69 78 02");
private static final byte[] CAT_ARRAY = VPackWireFixtureTest.hex(
            "13 3b "
          + "14 15 45 61 6e 67 72 79 1a 44 6e 61 6d 65 45 46 65 6c 69 78 02 "
          + "14 23 4c 63 61 75 73 65 4f 66 44 65 61 74 68 47 65 6e 74 72 6f 70 79 "
          + "44 6e 61 6d 65 45 46 65 6c 69 78 02 02");
private static final byte[] CAT_ARRAY_SERIALIZED = VPackWireFixtureTest.hex(
            "06 41 02 "
          + "0b 17 02 45 61 6e 67 72 79 1a 44 6e 61 6d 65 45 46 65 6c 69 78 03 0a "
          + "0b 25 02 4c 63 61 75 73 65 4f 66 44 65 61 74 68 47 65 6e 74 72 6f 70 79 "
          + "44 6e 61 6d 65 45 46 65 6c 69 78 03 18 03 1a");
private static final byte[] LIVE_CAT_SERIALIZED = VPackWireFixtureTest.hex(
            "0b 17 02 45 61 6e 67 72 79 1a 44 6e 61 6d 65 45 46 65 6c 69 78 03 0a");
private static final byte[] CAT_MAP = VPackWireFixtureTest.hex(
            "14 1d 44 6c 69 76 65 "
          + "14 15 45 61 6e 67 72 79 1a 44 6e 61 6d 65 45 46 65 6c 69 78 02 01");
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] NULL = VPackWireFixtureTest.hex("18");
private static final byte[] LOCAL_DATE = VPackWireFixtureTest.hex(
            "0b 15 01 45 76 61 6c 75 65 4a 31 39 38 36 2d 30 31 2d 31 37 03");
private static final byte[] LOCAL_DATE_TIME = VPackWireFixtureTest.hex(
            "0b 28 01 45 76 61 6c 75 65 5d 32 30 31 33 2d 30 38 2d 32 31 54 30 39 3a 32 32 3a "
          + "30 30 2e 30 30 30 30 30 30 30 35 37 03");
private static final byte[] LOCAL_TIME = VPackWireFixtureTest.hex(
            "0b 13 01 45 76 61 6c 75 65 48 30 39 3a 32 32 3a 35 37 03");

    // Provenance: DeductionTypeSerialization296Test#testLocalDate().
    void testLocalDateVpack() throws Exception {
        assertArrayEquals(LOCAL_DATE, MAPPER.writeValueAsBytes(
                new Wrapper434(LocalDate.of(1986, Month.JANUARY, 17))));
    }

    // Provenance: DeductionTypeSerialization296Test#testLocalDateTime().
    void testLocalDateTimeVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
        assertArrayEquals(LOCAL_DATE_TIME, mapper.writeValueAsBytes(new Wrapper434(
                LocalDateTime.of(2013, Month.AUGUST, 21, 9, 22, 0, 57))));
    }

    // Provenance: DeductionTypeSerialization296Test#testLocalTime().
    void testLocalTimeVpack() throws Exception {
        assertArrayEquals(LOCAL_TIME, MAPPER.writeValueAsBytes(
                new Wrapper434(LocalTime.of(9, 22, 57))));
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
    @JsonSubTypes({ @JsonSubTypes.Type(LiveCat434.class), @JsonSubTypes.Type(DeadCat434.class),
            @JsonSubTypes.Type(Fleabag434.class) })
    interface Feline434 { }
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
    @JsonSubTypes({ @JsonSubTypes.Type(LiveCat434.class), @JsonSubTypes.Type(DeadCat434.class) })
    static class Cat434 implements Feline434 { public String name; }
static class DeadCat434 extends Cat434 { public String causeOfDeath; }
static class LiveCat434 extends Cat434 { public boolean angry; }
static class Fleabag434 implements Feline434 { }
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
    static enum Enum434 { A, B }
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
    static class Bean434 {
        @JsonValue
        public String ser = "value";
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
    static class Wrapper434 {
        public Object value;

        Wrapper434(Object value) { this.value = value; }
    }

    void __invoke_testLocalDateVpack() throws Exception {
        try {
            testLocalDateVpack();
        } finally {
        }
    }


    void __invoke_testLocalDateTimeVpack() throws Exception {
        try {
            testLocalDateTimeVpack();
        } finally {
        }
    }


    void __invoke_testLocalTimeVpack() throws Exception {
        try {
            testLocalTimeVpack();
        } finally {
        }
    }

}
