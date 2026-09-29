package tools.jackson.databind.objectid;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.MismatchedInputException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0510Fixture {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] EMPTY_TYPED_OBJECT = VPackWireFixtureTest.hex(
            "0b 13 01 4d 63 6f 6e 63 72 65 74 65 5f 33 38 33 38 0a 03");
private static final byte[] ID_GREAT = VPackWireFixtureTest.hex(
            "0b 0d 01 42 69 64 45 67 72 65 61 74 03");
private static final byte[] ID_AND_VALUE_GREAT = VPackWireFixtureTest.hex(
            "0b 16 02 42 69 64 45 67 72 65 61 74 45 76 61 6c 75 65 28 2a 03 0c");
private static final byte[] TYPED_GREAT = VPackWireFixtureTest.hex(
            "0b 31 01 4d 63 6f 6e 63 72 65 74 65 5f 33 38 33 38 "
          + "0b 1f 02 42 69 64 45 67 72 65 61 74 48 6c 6f 63 61 74 69 6f 6e "
          + "47 42 61 6e 67 6b 6f 6b 03 0c 03");
private static final byte[] SEQUENCED = VPackWireFixtureTest.hex(
            "0b 15 02 42 69 64 3f 45 76 61 6c 75 65 45 67 72 65 61 74 03 07");

    // Provenance: ObjectId3838Test#testUniformHandlingForMissingObjectId().
    void uniformHandlingForMissingObjectIdVpack() throws Exception {
        assertMissingId(SetterBased.class);
        assertMissingId(CreatorBased.class);
        assertMissingId(DefaultConstructorBased.class);
        assertMissingId(StaticFactoryMethodBased.class);
        assertMissingId(MultiArgConstructorBased.class);
        assertMissingId(BaseType3838.class, EMPTY_TYPED_OBJECT);
        assertMissingId(IntSequencedBean.class);

        assertEquals("great", MAPPER.readValue(ID_GREAT, SetterBased.class).result());
        assertEquals("great", MAPPER.readValue(ID_GREAT, CreatorBased.class).result());
        assertEquals("great", MAPPER.readValue(ID_GREAT, DefaultConstructorBased.class).result());
        assertEquals("great", MAPPER.readValue(ID_GREAT, StaticFactoryMethodBased.class).result());
        MultiArgConstructorBased multi = MAPPER.readValue(ID_AND_VALUE_GREAT,
                MultiArgConstructorBased.class);
        assertEquals("great", multi.result());
        assertEquals(42, multi.getValue());

        BaseType3838 typed = MAPPER.readValue(TYPED_GREAT, BaseType3838.class);
        assertInstanceOf(Concrete3838.class, typed);
        assertEquals("great", typed.result());
        assertEquals("Bangkok", ((Concrete3838) typed).location);

        assertEquals("great", MAPPER.readValue(SEQUENCED, IntSequencedBean.class).result());
    }
private static void assertMissingId(Class<?> type) {
        assertMissingId(type, EMPTY_OBJECT);
    }
private static void assertMissingId(Class<?> type, byte[] input) {
        MismatchedInputException failure = assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue(input, type));
        assertEquals(true, failure.getMessage().contains("No Object Id found"),
                failure.getMessage());
    }
interface ResultGetter { String result(); }
@JsonIdentityInfo(property = "id", generator = ObjectIdGenerators.PropertyGenerator.class)
    static class SetterBased implements ResultGetter {
        private String id;
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        @Override public String result() { return id; }
    }
@JsonIdentityInfo(property = "id", generator = ObjectIdGenerators.PropertyGenerator.class)
    static class CreatorBased implements ResultGetter {
        private String id;
        @JsonCreator CreatorBased(@JsonProperty("id") String id) { this.id = id; }
        public String getId() { return id; }
        @Override public String result() { return id; }
    }
@JsonIdentityInfo(property = "id", generator = ObjectIdGenerators.PropertyGenerator.class)
    static class DefaultConstructorBased implements ResultGetter {
        public String id;
        @Override public String result() { return id; }
    }
@JsonIdentityInfo(property = "id", generator = ObjectIdGenerators.PropertyGenerator.class)
    static class StaticFactoryMethodBased implements ResultGetter {
        private String id;
        private StaticFactoryMethodBased(String id) { this.id = id; }
        public String getId() { return id; }
        @JsonCreator public static StaticFactoryMethodBased create(@JsonProperty("id") String id) {
            return new StaticFactoryMethodBased(id);
        }
        @Override public String result() { return id; }
    }
@JsonIdentityInfo(property = "id", generator = ObjectIdGenerators.PropertyGenerator.class)
    static class MultiArgConstructorBased implements ResultGetter {
        private String id;
        private final int value;
        @JsonCreator MultiArgConstructorBased(@JsonProperty("id") String id,
                @JsonProperty("value") int value) {
            this.id = id;
            this.value = value;
        }
        public String getId() { return id; }
        public int getValue() { return value; }
        @Override public String result() { return id; }
    }
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT,
            property = "type")
    @JsonSubTypes(@JsonSubTypes.Type(value = Concrete3838.class, name = "concrete_3838"))
    static class BaseType3838 implements ResultGetter {
        public String id;
        @Override public String result() { return id; }
    }
@JsonTypeName("concrete_3838")
    static class Concrete3838 extends BaseType3838 {
        public String location;
        protected Concrete3838() { }
    }
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "id")
    static class IntSequencedBean implements ResultGetter {
        public String value;
        @Override public String result() { return value; }
    }

    void __invoke_uniformHandlingForMissingObjectIdVpack() throws Exception {
        try {
            uniformHandlingForMissingObjectIdVpack();
        } finally {
        }
    }

}
