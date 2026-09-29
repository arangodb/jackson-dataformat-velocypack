package tools.jackson.databind.records;

import java.util.List;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.annotation.OptBoolean;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.MismatchedInputException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0533F1 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] SECRET_INPUT = VPackWireFixtureTest.hex(
            "14 1c 44 6e 61 6d 65 45 61 6c 69 63 65 46 73 65 63 72 65 74 46 73 33 63 72 33 74 02");
private static final byte[] SECRET_ATTACK = VPackWireFixtureTest.hex(
            "14 1c 44 6e 61 6d 65 45 61 6c 69 63 65 46 73 65 63 72 65 74 46 48 41 43 4b 45 44 02");
private static final byte[] PASSWORD_ATTACK = VPackWireFixtureTest.hex(
            "14 22 48 75 73 65 72 6e 61 6d 65 45 61 6c 69 63 65 48 70 61 73 73 77 6f 72 64 46 48 41 43 4b 45 44 02");
private static final byte[] VIEW_ATTACK = VPackWireFixtureTest.hex(
            "14 32 4b 70 75 62 6c 69 63 46 69 65 6c 64 4a 6e 65 77 2d 70 75 62 6c 69 63 4a 61 64 6d 69 6e 46 69 65 6c 64 4c 48 41 43 4b 45 44 2d 41 44 4d 49 4e 02");
private static final byte[] INJECTION_ATTACK = VPackWireFixtureTest.hex(
            "14 1e 44 6e 61 6d 65 45 61 6c 69 63 65 48 69 6e 6a 65 63 74 65 64 46 48 41 43 4b 45 44 02");
private static final byte[] MANAGED_BACK_INPUT = VPackWireFixtureTest.hex(
            "14 12 48 63 68 69 6c 64 72 65 6e 13 06 14 03 00 01 01");
private static final byte[] ENUM_17 = VPackWireFixtureTest.hex(
            "14 0b 45 73 74 61 74 65 28 11 01");
private static final byte[] ENUM_31 = VPackWireFixtureTest.hex(
            "14 0b 45 73 74 61 74 65 28 1f 01");
private static final byte[] ENUM_99 = VPackWireFixtureTest.hex(
            "14 0b 45 73 74 61 74 65 28 63 01");
private static final byte[] ENUM_0 = VPackWireFixtureTest.hex(
            "14 0a 45 73 74 61 74 65 30 01");

    void testEnumNumberFormatShapeRecord3580Vpack() throws Exception {
        assertEquals(RecordState3580.OFF, MAPPER.readValue(ENUM_17, RecordNumber3580.class).state());
        assertEquals(RecordState3580.ON, MAPPER.readValue(ENUM_31, RecordNumber3580.class).state());
        assertEquals(RecordState3580.UNKNOWN, MAPPER.readValue(ENUM_99, RecordNumber3580.class).state());
        assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue(ENUM_0, RecordNumber3580.class));
        assertEquals(RecordState3580.OFF,
                MAPPER.readValue(MAPPER.writeValueAsBytes(new RecordNumber3580(RecordState3580.OFF)),
                        RecordNumber3580.class).state());
        assertEquals(RecordState3580.ON,
                MAPPER.readValue(MAPPER.writeValueAsBytes(new RecordNumber3580(RecordState3580.ON)),
                        RecordNumber3580.class).state());
        assertEquals(RecordState3580.UNKNOWN,
                MAPPER.readValue(MAPPER.writeValueAsBytes(new RecordNumber3580(RecordState3580.UNKNOWN)),
                        RecordNumber3580.class).state());
    }

    void testEnumNumberIntFormatShapeRecord3580Vpack() throws Exception {
        assertEquals(RecordState3580.OFF, MAPPER.readValue(ENUM_17, RecordNumberInt3580.class).state());
        assertEquals(RecordState3580.ON, MAPPER.readValue(ENUM_31, RecordNumberInt3580.class).state());
        assertEquals(RecordState3580.UNKNOWN, MAPPER.readValue(ENUM_99, RecordNumberInt3580.class).state());
        assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue(ENUM_0, RecordNumberInt3580.class));
        assertEquals(RecordState3580.OFF,
                MAPPER.readValue(MAPPER.writeValueAsBytes(new RecordNumberInt3580(RecordState3580.OFF)),
                        RecordNumberInt3580.class).state());
        assertEquals(RecordState3580.ON,
                MAPPER.readValue(MAPPER.writeValueAsBytes(new RecordNumberInt3580(RecordState3580.ON)),
                        RecordNumberInt3580.class).state());
        assertEquals(RecordState3580.UNKNOWN,
                MAPPER.readValue(MAPPER.writeValueAsBytes(new RecordNumberInt3580(RecordState3580.UNKNOWN)),
                        RecordNumberInt3580.class).state());
    }
static class PublicView { }
static class AdminView extends PublicView { }
public record SecretRecord(String name, @JsonIgnore String secret) { }
@JsonIgnoreProperties({ "password" })
    public record IgnoredPropsRecord(String username, String password) { }
public record ViewRecord(@JsonView(PublicView.class) String publicField,
            @JsonView(AdminView.class) String adminField) { }
public record InjectRecord(String name,
            @JacksonInject(value = "injected-key", useInput = OptBoolean.FALSE) String injected) { }
public record RecordNumber3580(@JsonFormat(shape = JsonFormat.Shape.NUMBER) RecordState3580 state) { }
public record RecordNumberInt3580(@JsonFormat(shape = JsonFormat.Shape.NUMBER_INT) RecordState3580 state) { }
public enum RecordState3580 {
        OFF(17), ON(31), UNKNOWN(99);
        private final int value;
        RecordState3580(int value) { this.value = value; }
        @com.fasterxml.jackson.annotation.JsonValue
        public int value() { return value; }
    }
@JsonIdentityInfo(property = "id", generator = ObjectIdGenerators.PropertyGenerator.class)
    record ThingRecord(int id, String name) { }
public record ExampleRecord(List<ThingRecord> allThings, ThingRecord selected) { }
@JsonIdentityInfo(property = "id", generator = ObjectIdGenerators.PropertyGenerator.class)
    static class ThingPojo {
        public final int id;
        public final String name;
        @JsonProperty("id") public int id() { return id; }
        @JsonProperty("name") public String name() { return name; }
        @com.fasterxml.jackson.annotation.JsonCreator
        ThingPojo(@JsonProperty("id") int id, @JsonProperty("name") String name) {
            this.id = id;
            this.name = name;
        }
    }
static class ExamplePojo {
        public List<ThingPojo> allThings;
        public ThingPojo selected;
        @com.fasterxml.jackson.annotation.JsonCreator
        ExamplePojo(@JsonProperty("allThings") List<ThingPojo> allThings,
                @JsonProperty("selected") ThingPojo selected) {
            this.allThings = allThings;
            this.selected = selected;
        }
    }
public record Child5188(@JsonBackReference Parent5188 parent) { }
public record Parent5188(@JsonManagedReference List<Child5188> children) { }

    void __invoke_testEnumNumberFormatShapeRecord3580Vpack() throws Exception {
        try {
            testEnumNumberFormatShapeRecord3580Vpack();
        } finally {
        }
    }


    void __invoke_testEnumNumberIntFormatShapeRecord3580Vpack() throws Exception {
        try {
            testEnumNumberIntFormatShapeRecord3580Vpack();
        } finally {
        }
    }

}
