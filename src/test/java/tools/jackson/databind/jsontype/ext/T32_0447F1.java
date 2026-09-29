package tools.jackson.databind.jsontype.ext;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import tools.jackson.databind.DatabindContext;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonTypeIdResolver;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;
import tools.jackson.databind.jsontype.TypeIdResolver;
import tools.jackson.databind.jsontype.impl.TypeIdResolverBase;

import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0447F1 {
private static final byte[] EXTERNAL_CREATOR = VPackWireFixtureTest.hex(
            "14 15 44 74 79 70 65 43 66 6f 6f 47 70 61 79 6c 6f 61 64 0a 02");
private static final byte[] EXTERNAL_CREATOR_REVERSED = VPackWireFixtureTest.hex(
            "14 15 47 70 61 79 6c 6f 61 64 0a 44 74 79 70 65 43 66 6f 6f 02");
private static final byte[] DEFAULT_ATTACK = VPackWireFixtureTest.hex(
            "14 19 44 6e 61 6d 65 43 66 6f 6f 46 61 74 74 61 63 6b 45 72 69 67 68 74 02");
private static final byte[] EXPLICIT_ATTACK = VPackWireFixtureTest.hex(
            "14 2e 44 6e 61 6d 65 43 66 6f 6f 4f 70 72 65 66 65 72 72 65 64 41 74 74 61 63 6b "
          + "44 4b 49 43 4b 46 61 74 74 61 63 6b 45 72 69 67 68 74 03");
private static final byte[] ENUM_EXTERNAL = VPackWireFixtureTest.hex(
            "13 17 14 14 44 74 79 70 65 43 44 6f 67 46 61 6e 69 6d 61 6c 0a 02 01");
private static final byte[] ANY_SETTER_TYPE_DATA_TIME = VPackWireFixtureTest.hex(
            "14 31 44 74 79 70 65 45 74 72 61 63 6b 44 64 61 74 61 "
          + "14 16 4d 64 61 74 61 2d 69 6e 74 65 72 6e 61 6c 44 74 6f 74 6f 01 "
          + "44 74 69 6d 65 29 59 01 03");
private static final byte[] ANY_SETTER_DATA_TYPE_TIME = VPackWireFixtureTest.hex(
            "14 31 44 64 61 74 61 14 16 4d 64 61 74 61 2d 69 6e 74 65 72 6e 61 6c 44 74 6f 74 6f 01 "
          + "44 74 79 70 65 45 74 72 61 63 6b 44 74 69 6d 65 29 59 01 03");
private static final byte[] ANY_SETTER_DATA_TIME_TYPE = VPackWireFixtureTest.hex(
            "14 31 44 64 61 74 61 14 16 4d 64 61 74 61 2d 69 6e 74 65 72 6e 61 6c 44 74 6f 74 6f 01 "
          + "44 74 69 6d 65 29 59 01 44 74 79 70 65 45 74 72 61 63 6b 03");
private static final byte[] ANY_SETTER_TIME_TYPE_DATA = VPackWireFixtureTest.hex(
            "14 31 44 74 69 6d 65 29 59 01 44 74 79 70 65 45 74 72 61 63 6b 44 64 61 74 61 "
          + "14 16 4d 64 61 74 61 2d 69 6e 74 65 72 6e 61 6c 44 74 6f 74 6f 01 03");
private static final byte[] UNWRAPPED_EXTERNAL = VPackWireFixtureTest.hex(
            "14 38 44 74 65 78 74 48 74 68 69 73 20 69 73 20 41 47 77 72 61 70 70 65 64 43 79 65 73 "
          + "47 73 75 62 74 79 70 65 44 53 75 62 41 43 73 75 62 14 06 44 62 6f 6f 6c 1a 01 04");
private static final byte[] INVITE_CONTACT = VPackWireFixtureTest.hex(
            "14 1f 44 6b 69 6e 64 47 43 4f 4e 54 41 43 54 42 74 6f 14 0c 44 6e 61 6d 65 43 46 6f 6f 01 02");
private static final byte[] INVITE_EMAIL = VPackWireFixtureTest.hex(
            "14 3d 4d 6b 69 6e 64 46 6f 72 4d 61 70 70 65 72 45 45 4d 41 49 4c 42 74 6f "
          + "14 23 44 6e 61 6d 65 43 42 61 72 45 65 6d 61 69 6c 50 74 65 73 74 40 65 78 61 6d 70 6c 65 2e 63 6f 6d 02 02");
private final ObjectMapper mapper = new VPackMapper();

    // Provenance: ExternalTypeIdWithUnwrapped2039Test#externalWithUnwrapped2039().
    void externalWithUnwrapped2039Vpack() throws Exception {
        DatabindException ex = assertThrows(DatabindException.class,
                () -> mapper.readValue(UNWRAPPED_EXTERNAL, MainType2039.class));
        assertExceptionContains(ex, "Cannot (yet) use @JsonUnwrapped");
        assertExceptionContains(ex, "EXTERNAL_PROPERTY");
    }
private static ObjectMapper mapperForAnySetter() {
        return VPackMapper.builder()
                .polymorphicTypeValidator(NoCheckSubTypeValidator.INSTANCE).build();
    }
private static void assertExceptionContains(Exception ex, String value) {
        String message = ex.getMessage();
        if (message == null || !message.contains(value)) {
            throw new AssertionError("Expected exception message to contain '" + value
                    + "' but was: " + message, ex);
        }
    }
static interface Payload999 { }
@JsonTypeName("foo")
    static class FooPayload999 implements Payload999 { }
@JsonTypeName("bar")
    static class BarPayload999 implements Payload999 { }
static class Message<P extends Payload999> {
        final String type;
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, visible = true,
                include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type")
        @JsonSubTypes({ @JsonSubTypes.Type(FooPayload999.class),
                @JsonSubTypes.Type(BarPayload999.class) })
        final P payload;

        @JsonCreator
        Message(@JsonProperty("type") String type, @JsonProperty("payload") P payload) {
            this.type = type;
            this.payload = payload;
        }
    }
enum Attacks1198 { KICK, PUNCH }
static class Character1198 {
        public String name;
        public Attacks1198 preferredAttack;
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, defaultImpl = Kick1198.class,
                include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "preferredAttack")
        @JsonSubTypes({ @JsonSubTypes.Type(value = Kick1198.class, name = "KICK"),
                @JsonSubTypes.Type(value = Punch1198.class, name = "PUNCH") })
        public Attack1198 attack;
    }
static abstract class Attack1198 {
        public String side;
        protected Attack1198(String side) { this.side = side; }
    }
static class Kick1198 extends Attack1198 {
        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        Kick1198(String side) { super(side); }
    }
static class Punch1198 extends Attack1198 {
        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        Punch1198(String side) { super(side); }
    }
interface Animal1328 { }
static class Dog1328 implements Animal1328 { public String dogStuff; }
enum AnimalType1328 { Dog }
static class AnimalAndType1328 {
        public AnimalType1328 type;
        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS,
                include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type")
        @JsonTypeIdResolver(AnimalResolver1328.class)
        private Animal1328 animal;

        @java.beans.ConstructorProperties({ "type", "animal" })
        AnimalAndType1328(AnimalType1328 type, Animal1328 animal) {
            this.type = type;
            this.animal = animal;
        }
    }
static class AnimalResolver1328 implements TypeIdResolver {
        @Override public void init(JavaType bt) { }
        @Override public String idFromValue(DatabindContext ctxt, Object value) { return null; }
        @Override public String idFromValueAndType(DatabindContext ctxt, Object value,
                Class<?> suggestedType) { return null; }
        @Override public String idFromBaseType(DatabindContext ctxt) {
            throw new UnsupportedOperationException("Missing action type information - Cannot construct");
        }
        @Override public JavaType typeFromId(DatabindContext context, String id) {
            if (AnimalType1328.Dog.toString().equals(id)) {
                return context.constructType(Dog1328.class);
            }
            throw new IllegalArgumentException("What is a " + id);
        }
        @Override public String getDescForKnownTypeIds() { return null; }
        @Override public JsonTypeInfo.Id getMechanism() { return JsonTypeInfo.Id.CUSTOM; }
    }
static class ChildBaseByParentTypeResolver3045 extends TypeIdResolverBase {
        private static final long serialVersionUID = 1L;
        private JavaType superType;
        @Override public void init(JavaType baseType) { superType = baseType; }
        @Override public JsonTypeInfo.Id getMechanism() { return JsonTypeInfo.Id.NAME; }
        @Override public JavaType typeFromId(DatabindContext context, String id) {
            if ("track".equals(id)) {
                return context.constructSpecializedType(superType, MyData3045.class);
            }
            throw new IllegalArgumentException("No type with id '" + id + "'");
        }
        @Override public String idFromValue(DatabindContext ctxt, Object value) { return null; }
        @Override public String idFromValueAndType(DatabindContext ctxt, Object value,
                Class<?> suggestedType) { return null; }
    }
static class MyData3045 {
        @JsonAnySetter public java.util.HashMap<String, Object> data = new java.util.HashMap<>();
        public int size() { return data.size(); }
        public Object find(String key) { return data.get(key); }
    }
static class MyJson3045 {
        public final long time;
        public String type;
        public Object data;
        @JsonCreator MyJson3045(@JsonProperty("time") long t) { time = t; }
        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property = "type", visible = true)
        @JsonTypeIdResolver(ChildBaseByParentTypeResolver3045.class)
        public void setData(Object data) { this.data = data; }
    }
static class MainType2039 {
        public String text;
        @JsonUnwrapped public Wrapped2039 wrapped;
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property = "subtype")
        @JsonSubTypes(@JsonSubTypes.Type(value = SubA2039.class, name = "SubA"))
        public SubType2039 sub;
        public void setSub(SubType2039 s) { sub = s; }
        public void setWrapped(Wrapped2039 w) { wrapped = w; }
    }
static class Wrapped2039 { public String wrapped; }
static class SubType2039 { }
static class SubA2039 extends SubType2039 { @JsonProperty public boolean bool; }
enum InviteKind1329 { CONTACT, EMAIL }
static abstract class InviteTo1329 { public String name; }
static class InviteToContact1329 extends InviteTo1329 { }
static class InviteToEmail1329 extends InviteTo1329 { public String email; }
static class Invite1329 {
        public InviteKind1329 kind;
        public InviteKind1329 kindForMapper;
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property = "kind", visible = false)
        @JsonSubTypes({ @JsonSubTypes.Type(value = InviteToContact1329.class, name = "CONTACT"),
                @JsonSubTypes.Type(value = InviteToEmail1329.class, name = "EMAIL") })
        public InviteTo1329 to;
    }
static class Invite21329 {
        public InviteKind1329 kindForMapper;
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property = "kindForMapper", visible = false)
        @JsonSubTypes({ @JsonSubTypes.Type(value = InviteToContact1329.class, name = "CONTACT"),
                @JsonSubTypes.Type(value = InviteToEmail1329.class, name = "EMAIL") })
        public InviteTo1329 to;
    }
static final class NoCheckSubTypeValidator extends PolymorphicTypeValidator.Base {
        private static final long serialVersionUID = 1L;
        static final NoCheckSubTypeValidator INSTANCE = new NoCheckSubTypeValidator();
        @Override public Validity validateBaseType(DatabindContext ctxt, JavaType baseType) {
            return Validity.ALLOWED;
        }
    }

    void __invoke_externalWithUnwrapped2039Vpack() throws Exception {
        try {
            externalWithUnwrapped2039Vpack();
        } finally {
        }
    }

}
