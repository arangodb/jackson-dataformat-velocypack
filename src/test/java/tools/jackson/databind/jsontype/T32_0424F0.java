package tools.jackson.databind.jsontype;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0424F0 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] DO_FALSE = VPackWireFixtureTest.hex(
            "14 12 45 40 74 79 70 65 48 64 6f 2d 66 61 6c 73 65 01");
private static final byte[] DO_TRUE = VPackWireFixtureTest.hex(
            "14 11 45 40 74 79 70 65 47 64 6f 2d 74 72 75 65 01");
private static final byte[] DO_DEFAULT = VPackWireFixtureTest.hex(
            "14 14 45 40 74 79 70 65 4a 64 6f 2d 64 65 66 61 75 6c 74 01");
private static final byte[] DOUBLE_TYPE_FIRST = VPackWireFixtureTest.hex(
            "14 1f 44 74 79 70 65 46 64 6f 75 62 6c 65 "
          + "46 64 6f 75 62 6c 65 1b 00 00 00 00 00 00 00 40 02");
private static final byte[] DOUBLE_TYPE_LAST = VPackWireFixtureTest.hex(
            "14 1f 46 64 6f 75 62 6c 65 1b 00 00 00 00 00 00 00 40 "
          + "44 74 79 70 65 46 64 6f 75 62 6c 65 02");
private static final byte[] FP_AS_OBJECT = VPackWireFixtureTest.hex(
            "14 31 4d 61 6c 6c 6f 77 65 64 56 61 6c 75 65 73 "
          + "13 15 1b 00 00 00 00 00 00 f8 3f "
          + "1b 00 00 00 00 00 00 04 40 02 "
          + "44 74 79 70 65 45 74 79 70 65 31 02");

    // Provenance: StrictJsonTypeInfoHandling3853Test#testSuccessWhenTypeIdIsProvided().
    void testSuccessWhenTypeIdIsProvidedVpack() throws Exception {
        ObjectMapper enabled = VPackMapper.builder()
                .enable(MapperFeature.REQUIRE_TYPE_ID_FOR_SUBTYPES).build();
        ObjectMapper disabled = VPackMapper.builder()
                .disable(MapperFeature.REQUIRE_TYPE_ID_FOR_SUBTYPES).build();
        ObjectMapper defaults = VPackMapper.builder().build();

        for (ObjectMapper mapper : new ObjectMapper[] { enabled, defaults, disabled }) {
            assertSuccess(mapper, DO_FALSE, FalseCommand424.class);
            assertSuccess(mapper, DO_FALSE, DoFalseCommand424.class);
            assertSuccess(mapper, DO_TRUE, TrueCommand424.class);
            assertSuccess(mapper, DO_TRUE, DoTrueCommand424.class);
            assertSuccess(mapper, DO_DEFAULT, DefaultCommand424.class);
            assertSuccess(mapper, DO_DEFAULT, DoDefaultCommand424.class);
        }
    }
private static <T> void assertSuccess(ObjectMapper mapper, byte[] input,
            Class<T> type) throws Exception {
        T value = mapper.readValue(input, type);
        assertNotNull(value);
        assertInstanceOf(type, value);
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
    @JsonSubTypes({ @JsonSubTypes.Type(value = DoFalseCommand424.class, name = "do-false") })
    interface FalseCommand424 { }
static class DoFalseCommand424 implements FalseCommand424 { }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
    @JsonSubTypes({ @JsonSubTypes.Type(value = DoTrueCommand424.class, name = "do-true") })
    interface TrueCommand424 { }
static class DoTrueCommand424 implements TrueCommand424 { }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
    @JsonSubTypes({ @JsonSubTypes.Type(value = DoDefaultCommand424.class, name = "do-default") })
    interface DefaultCommand424 { }
static class DoDefaultCommand424 implements DefaultCommand424 { }
static class AccessModel424 {
        private Map<String, Collection<String>> repositoryPrivileges;

        AccessModel424() {
            repositoryPrivileges = new HashMap<>();
        }

        public Map<String, Collection<String>> getRepositoryPrivileges() {
            return repositoryPrivileges;
        }

        public void setRepositoryPrivileges(Map<String, Collection<String>> value) {
            repositoryPrivileges = value;
        }
    }
static class CustomMap424<T> extends LinkedHashMap<Object, T> { }
interface Dummy424 {
        List<String> getStrings();
    }
static class MetaModel424<M, B> extends AbstractMetaValue424<M, M, B> {
        @JsonProperty
        protected final Map<String, AbstractMetaValue424<M, ?, B>> attributes = new HashMap<>();

        public <V> ListMetaAttribute424<M, V, B> describeList(String attributeName) {
            ListMetaAttribute424<M, V, B> attribute = new ListMetaAttribute424<>();
            attributes.put(attributeName, attribute);
            return attribute;
        }
    }
static abstract class AbstractMetaValue424<M, V, B> {
        public int getBogus() { return 3; }
    }
static class ListMetaAttribute424<M, V, B> extends MetaAttribute424<M, List<V>, B> {
        public ListMetaAttribute424() { }
    }
static class MetaAttribute424<M, V, B> extends AbstractMetaValue424<M, V, B> {
        public MetaAttribute424() { }
    }
@SuppressWarnings("rawtypes")
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
    @JsonSubTypes({
        @JsonSubTypes.Type(value = Either424.Left.class, name = "left"),
        @JsonSubTypes.Type(value = Either424.Right.class, name = "right")
    })
    static class Either424<L, R> {
        static class Left<T> extends Either424 { }
        static class Right<T> extends Either424 { }
    }
static class Foo424 {
        @SuppressWarnings("unchecked")
        public Either424<String, String> getEither() {
            return new Either424.Right<String>();
        }
    }
static final class UnionExample424 {
        private final Base424 value;

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        private UnionExample424(Base424 value) {
            this.value = value;
        }

        static UnionExample424 double_(AliasDouble424 value) {
            return new UnionExample424(new DoubleWrapper424(value));
        }

        @Override
        public boolean equals(Object other) {
            return this == other || (other instanceof UnionExample424 ue && value.equals(ue.value));
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(value);
        }

        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true,
                defaultImpl = UnknownWrapper424.class)
        @JsonSubTypes(@JsonSubTypes.Type(UnionExample424.DoubleWrapper424.class))
        @JsonIgnoreProperties(ignoreUnknown = true)
        private interface Base424 { }

        @JsonTypeName("double")
        private static final class DoubleWrapper424 implements Base424 {
            private final AliasDouble424 value;

            @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
            DoubleWrapper424(@JsonSetter("double") AliasDouble424 value) {
                this.value = Objects.requireNonNull(value, "double cannot be null");
            }

            @JsonProperty("double")
            private AliasDouble424 getValue() {
                return value;
            }

            @Override
            public boolean equals(Object other) {
                return this == other || (other instanceof DoubleWrapper424 dw && value.equals(dw.value));
            }

            @Override
            public int hashCode() {
                return Objects.hashCode(value);
            }
        }

        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
                property = "type", visible = true)
        private static final class UnknownWrapper424 implements Base424 {
            private final String type;

            @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
            private UnknownWrapper424(@JsonProperty("type") String type) {
                this.type = Objects.requireNonNull(type, "type cannot be null");
            }

            @JsonProperty
            private String getType() {
                return type;
            }
        }
    }
static final class AliasDouble424 {
        private final double value;

        private AliasDouble424(double value) {
            this.value = value;
        }

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        public static AliasDouble424 of(double value) {
            return new AliasDouble424(value);
        }

        @JsonValue
        public double get() {
            return value;
        }

        @Override
        public boolean equals(Object other) {
            return this == other || (other instanceof AliasDouble424 ad
                    && Double.doubleToLongBits(value) == Double.doubleToLongBits(ad.value));
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(value);
        }
    }
@JsonTypeInfo(property = "type", use = JsonTypeInfo.Id.NAME)
    @JsonSubTypes({ @JsonSubTypes.Type(Value4138Impl424.class) })
    abstract static class Value4138Base424 {
        public abstract Object[] getAllowedValues();
    }
@JsonTypeName("type1")
    static class Value4138Impl424 extends Value4138Base424 {
        Object[] allowedValues;

        protected Value4138Impl424() { }

        @Override
        public Object[] getAllowedValues() {
            return allowedValues;
        }
    }

    void __invoke_testSuccessWhenTypeIdIsProvidedVpack() throws Exception {
        try {
            testSuccessWhenTypeIdIsProvidedVpack();
        } finally {
        }
    }

}
