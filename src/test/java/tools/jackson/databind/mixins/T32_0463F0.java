package tools.jackson.databind.mixins;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0463F0 {
private static final byte[] FACTORY_MIXIN_INPUT = VPackWireFixtureTest.hex(
            "14 46 47 70 72 6f 66 69 6c 65 14 3b 45 76 61 6c 75 65 "
          + "14 27 49 66 69 72 73 74 4e 61 6d 65 47 4a 61 63 6b 73 6f 6e "
          + "48 6c 61 73 74 4e 61 6d 65 48 44 61 74 61 62 69 6e 64 02 "
          + "49 74 69 6d 65 73 74 61 6d 70 31 02 01");
private static final byte[] SECOND_MIXIN_INPUT = VPackWireFixtureTest.hex(
            "14 1d 4c 73 65 63 6f 6e 64 2d 6d 69 78 69 6e "
          + "4c 73 65 63 6f 6e 64 2d 6d 69 78 69 6e 01");
private static final byte[] THIRD_MIXIN_INPUT = VPackWireFixtureTest.hex(
            "14 1b 4b 74 68 69 72 64 2d 6d 69 78 69 6e "
          + "4b 74 68 69 72 64 2d 6d 69 78 69 6e 01");
private static final byte[] FIRST_MIXIN_INPUT = VPackWireFixtureTest.hex(
            "14 1b 4b 66 69 72 73 74 2d 6d 69 78 69 6e "
          + "4b 66 69 72 73 74 2d 6d 69 78 69 6e 01");
private static final byte[] SECOND_MIXIN_OUTPUT = VPackWireFixtureTest.hex(
            "14 16 4c 73 65 63 6f 6e 64 2d 6d 69 78 69 6e "
          + "45 76 61 6c 75 65 01");
private static final byte[] THIRD_MIXIN_OUTPUT = VPackWireFixtureTest.hex(
            "14 15 4b 74 68 69 72 64 2d 6d 69 78 69 6e "
          + "45 76 61 6c 75 65 01");
private static final byte[] FIRST_MIXIN_OUTPUT = VPackWireFixtureTest.hex(
            "14 15 4b 66 69 72 73 74 2d 6d 69 78 69 6e "
          + "45 76 61 6c 75 65 01");
private static final byte[] BUNDLE_INPUT = VPackWireFixtureTest.hex(
            "14 0e 43 62 61 72 46 72 65 73 75 6c 74 01");

    // Provenance: MixinForFactoryMethod3220Test#testMixin3220().
    void testMixin3220Vpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addMixIn(Timestamped.class, TimestampedMixin.class)
                .build();

        User user = mapper.readValue(FACTORY_MIXIN_INPUT, User.class);
        Profile profile = user.getProfile().getValue();
        assertEquals(new Profile("Jackson", "Databind"), profile);
        assertEquals(1, user.getProfile().getTimestamp());

        JsonNode expected = mapper.readTree(FACTORY_MIXIN_INPUT);
        assertEquals(expected, mapper.readTree(mapper.writeValueAsBytes(user)));
    }
private static ObjectMapper circularMapper() {
        return VPackMapper.builder()
                .addMixIn(First.class, Second.class)
                .addMixIn(Second.class, Third.class)
                .addMixIn(Third.class, First.class)
                .build();
    }
static class Timestamped<T> {
        private final T value;
        private final int timestamp;

        Timestamped(T value, int timestamp) {
            this.value = value;
            this.timestamp = timestamp;
        }

        public static <T> Timestamped<T> stamp(T value, int timestamp) {
            return new Timestamped<>(value, timestamp);
        }

        public T getValue() {
            return value;
        }

        public int getTimestamp() {
            return timestamp;
        }

        @Override
        public boolean equals(Object value) {
            if (this == value) return true;
            if (value == null || getClass() != value.getClass()) return false;
            Timestamped<?> that = (Timestamped<?>) value;
            return timestamp == that.timestamp && Objects.equals(this.value, that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value, timestamp);
        }
    }
abstract static class TimestampedMixin<T> {
        @JsonCreator
        public static <T> void stamp(
                @JsonProperty("value") T value,
                @JsonProperty("timestamp") int timestamp) {
        }

        @JsonGetter("value")
        abstract T getValue();

        @JsonGetter("timestamp")
        abstract int getTimestamp();
    }
static class Profile {
        private final String firstName;
        private final String lastName;

        @JsonCreator
        public Profile(@JsonProperty("firstName") String firstName,
                @JsonProperty("lastName") String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        @JsonGetter("firstName")
        public String getFirstName() {
            return firstName;
        }

        @JsonGetter("lastName")
        public String getLastName() {
            return lastName;
        }

        @Override
        public boolean equals(Object value) {
            if (this == value) return true;
            if (value == null || getClass() != value.getClass()) return false;
            Profile profile = (Profile) value;
            return Objects.equals(firstName, profile.firstName)
                    && Objects.equals(lastName, profile.lastName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(firstName, lastName);
        }
    }
static class User {
        private final Timestamped<Profile> profile;

        @JsonCreator
        User(@JsonProperty("profile") Timestamped<Profile> profile) {
            this.profile = profile;
        }

        @JsonGetter("profile")
        public Timestamped<Profile> getProfile() {
            return profile;
        }
    }
static class First {
        @JsonProperty("first-mixin")
        public String value;
    }
static class Second {
        @JsonProperty("second-mixin")
        public String value;
    }
static class Third {
        @JsonProperty("third-mixin")
        public String value;
    }
@JacksonAnnotationsInside
    @JsonProperty("bar")
    @java.lang.annotation.Target({ java.lang.annotation.ElementType.CONSTRUCTOR,
            java.lang.annotation.ElementType.FIELD, java.lang.annotation.ElementType.METHOD })
    @java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
    public @interface ExposeStuff { }
abstract class FooMixin {
        @ExposeStuff
        public abstract String getStuff();
    }
static class Foo {
        private final String stuff;

        Foo(String stuff) {
            this.stuff = stuff;
        }

        public String getStuff() {
            return stuff;
        }
    }

    void __invoke_testMixin3220Vpack() throws Exception {
        try {
            testMixin3220Vpack();
        } finally {
        }
    }

}
