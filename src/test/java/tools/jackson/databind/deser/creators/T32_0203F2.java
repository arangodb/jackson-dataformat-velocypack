package tools.jackson.databind.deser.creators;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.cfg.MapperConfig;
import tools.jackson.databind.introspect.AnnotatedMember;
import tools.jackson.databind.introspect.AnnotatedMethod;
import tools.jackson.databind.introspect.AnnotatedParameter;
import tools.jackson.databind.introspect.AnnotatedWithParams;
import tools.jackson.databind.introspect.JacksonAnnotationIntrospector;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0203F2 {
private static final byte[] EMPTY_ARRAY = VPackWireFixtureTest.hex("01");
private static final byte[] BAG_OF_STRINGS = VPackWireFixtureTest.hex(
            "14 14 47 73 74 72 69 6e 67 73 13 09 41 61 41 62 41 63 03 01");
private static final byte[] BAG_OF_VALUES = VPackWireFixtureTest.hex(
            "14 13 46 76 61 6c 75 65 73 13 09 41 61 41 62 41 63 03 01");
private static final byte[] TYPED_EMPTY_UNMODIFIABLE_SET = VPackWireFixtureTest.hex(
            "13 2a 65 6a 61 76 61 2e 75 74 69 6c 2e 43 6f 6c 6c 65 63 74 69 6f 6e 73 24 "
          + "55 6e 6d 6f 64 69 66 69 61 62 6c 65 53 65 74 01 02");
private static final byte[] ANNOTATED_AS_DATE = VPackWireFixtureTest.hex("28 7b");
private static final byte[] ANNOTATED_CONTENT_AS_DATE = VPackWireFixtureTest.hex(
            "13 05 28 7b 01");
private static final byte[] CUSTOM_DELEGATING_VALUE = VPackWireFixtureTest.hex("1a");
private static final byte[] D_VALUE = VPackWireFixtureTest.hex(
            "47 61 62 63 3a 64 65 66");
private static final byte[] DATA2543_PROPERTIES = VPackWireFixtureTest.hex(
            "14 13 45 70 61 72 74 31 41 61 45 70 61 72 74 32 41 62 02");
private static final byte[] DATA2543_DELEGATING = VPackWireFixtureTest.hex(
            "43 61 20 62");
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final BasicPolymorphicTypeValidator ALLOW_ALL_TYPES =
            BasicPolymorphicTypeValidator.builder().allowIfSubType("").build();

    // Provenance: DelegatingCreatorImplicitNamesTest#testWithoutNamedParameters1001.
    void testWithoutNamedParameters1001() throws Exception {
        D d = D.make("abc:def");
        byte[] actualBytes = MAPPER.writeValueAsBytes(d);
        D actualD = MAPPER.readValue(actualBytes, D.class);
        assertArrayEquals(D_VALUE, actualBytes);
        assertEquals(d, actualD);
    }

    // Provenance: DelegatingCreatorImplicitNamesTest#testWithNamedParameters1001.
    void testWithNamedParameters1001() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .annotationIntrospector(new CreatorNameIntrospector1001())
                .build();
        D d = D.make("abc:def");
        byte[] actualBytes = mapper.writeValueAsBytes(d);
        D actualD = mapper.readValue(actualBytes, D.class);
        assertArrayEquals(D_VALUE, actualBytes);
        assertEquals(d, actualD);
    }

    // Provenance: DelegatingCreatorImplicitNamesTest#testDeserialization2543.
    void testDeserialization2543() throws Exception {
        Data2543 data = MAPPER_2543.readValue(DATA2543_PROPERTIES, Data2543.class);
        assertEquals("a", data.part1);
        assertEquals("b", data.part2);
    }

    // Provenance: DelegatingCreatorImplicitNamesTest#testDelegatingDeserialization2543.
    void testDelegatingDeserialization2543() throws Exception {
        Data2543 data = MAPPER_2543.readValue(DATA2543_DELEGATING, Data2543.class);
        assertEquals("a", data.part1);
        assertEquals("b", data.part2);
    }
private static final ObjectMapper MAPPER_2543 = VPackMapper.builder()
            .annotationIntrospector(new DelegatingCreatorNamedArgumentIntrospector2543())
            .build();
static class MyTypeImpl extends MyType {
        private final List<Integer> values;

        MyTypeImpl(List<Integer> values) { this.values = values; }

        @Override
        public List<Integer> getValues() { return values; }
    }
static abstract class MyType {
        @JsonValue
        public abstract List<Integer> getValues();

        @JsonCreator(mode = Mode.DELEGATING)
        public static MyType of(List<Integer> values) { return new MyTypeImpl(values); }
    }
@JsonDeserialize(as = ImmutableBag2324.class)
    public interface Bag2324<T> extends Collection<T> { }
public static class ImmutableBag2324<T> extends AbstractCollection<T>
            implements Bag2324<T> {
        private final Collection<T> elements;

        @JsonCreator(mode = Mode.DELEGATING)
        private ImmutableBag2324(Collection<T> elements) {
            this.elements = Collections.unmodifiableCollection(elements);
        }

        @Override
        public Iterator<T> iterator() { return elements.iterator(); }

        @Override
        public int size() { return elements.size(); }
    }
static class Value2324 {
        public String value;

        public Value2324(String v) { value = v; }

        @Override
        public boolean equals(Object o) {
            return o instanceof Value2324 other && value.equals(other.value);
        }
    }
static class WithBagOfStrings2324 {
        private Bag2324<String> bagOfStrings;

        public Bag2324<String> getStrings() { return bagOfStrings; }
        public void setStrings(Bag2324<String> value) { bagOfStrings = value; }
    }
static class WithBagOfValues2324 {
        private Bag2324<Value2324> bagOfValues;

        public Bag2324<Value2324> getValues() { return bagOfValues; }
        public void setValues(Bag2324<Value2324> value) { bagOfValues = value; }
    }
@JsonTypeInfo(use = Id.CLASS, include = As.PROPERTY)
    abstract static class UnmodifiableSetMixin {
        @JsonCreator
        public UnmodifiableSetMixin(Set<?> value) { }
    }
static class MultipleArrayDelegators {
        @JsonCreator(mode = Mode.DELEGATING)
        MultipleArrayDelegators(List<Integer> value) { }

        @JsonCreator(mode = Mode.DELEGATING)
        MultipleArrayDelegators(Set<Integer> value) { }
    }
static class Wrapper2016As {
        Object value;

        @JsonCreator(mode = Mode.DELEGATING)
        public Wrapper2016As(@JsonDeserialize(as = Date.class) Object value) {
            this.value = value;
        }
    }
static class Wrapper2016ContentAs {
        List<Object> value;

        @JsonCreator(mode = Mode.DELEGATING)
        public Wrapper2016ContentAs(
                @JsonDeserialize(contentAs = Date.class) List<Object> value) {
            this.value = value;
        }
    }
static class DelegatingWithCustomDeser2021 {
        static final Double DEFAULT = 0.25;
        Number value;

        @JsonCreator(mode = Mode.DELEGATING)
        public DelegatingWithCustomDeser2021(
                @JsonDeserialize(using = ValueDeser2021.class) Number value) {
            this.value = value;
        }
    }
static class ValueDeser2021 extends StdDeserializer<Number> {
        ValueDeser2021() { super(Number.class); }

        @Override
        public Number deserialize(JsonParser p, DeserializationContext ctxt)
                throws JacksonException {
            p.skipChildren();
            return DelegatingWithCustomDeser2021.DEFAULT;
        }
    }
static class D {
        private String raw1 = "";
        private String raw2 = "";

        private D(String raw1, String raw2) {
            this.raw1 = raw1;
            this.raw2 = raw2;
        }

        @JsonCreator
        public static D make(String value) {
            String[] split = value.split(":");
            return new D(split[0], split[1]);
        }

        @JsonValue
        public String getMyValue() { return raw1 + ":" + raw2; }

        @Override
        public boolean equals(Object o) {
            return o instanceof D other && other.raw1.equals(raw1) && other.raw2.equals(raw2);
        }
    }
static class CreatorNameIntrospector1001 extends JacksonAnnotationIntrospector {
        @Override
        public String findImplicitPropertyName(MapperConfig<?> config, AnnotatedMember member) {
            if (member instanceof AnnotatedParameter parameter) {
                AnnotatedWithParams owner = parameter.getOwner();
                if (owner instanceof AnnotatedMethod && parameter.getIndex() == 0) {
                    return "value";
                }
            }
            return super.findImplicitPropertyName(config, member);
        }
    }
static class Data2543 {
        final String part1;
        final String part2;

        @JsonCreator(mode = Mode.PROPERTIES)
        public Data2543(@com.fasterxml.jackson.annotation.JsonProperty("part1") String part1,
                @com.fasterxml.jackson.annotation.JsonProperty("part2") String part2) {
            this.part1 = part1;
            this.part2 = part2;
        }

        @JsonCreator(mode = Mode.DELEGATING)
        public static Data2543 fromFullData(String fullData) {
            String[] parts = fullData.split("\\s+", 2);
            return new Data2543(parts[0], parts[1]);
        }
    }
static class DelegatingCreatorNamedArgumentIntrospector2543
            extends JacksonAnnotationIntrospector {
        @Override
        public String findImplicitPropertyName(MapperConfig<?> config, AnnotatedMember member) {
            if (member instanceof AnnotatedParameter parameter) {
                AnnotatedWithParams owner = parameter.getOwner();
                if (owner instanceof AnnotatedMethod method) {
                    JsonCreator creator = method.getAnnotation(JsonCreator.class);
                    if (creator != null && creator.mode() == Mode.DELEGATING) {
                        return "fullData";
                    }
                }
            }
            return super.findImplicitPropertyName(config, member);
        }
    }

    void __invoke_testWithoutNamedParameters1001() throws Exception {
        try {
            testWithoutNamedParameters1001();
        } finally {
        }
    }


    void __invoke_testWithNamedParameters1001() throws Exception {
        try {
            testWithNamedParameters1001();
        } finally {
        }
    }


    void __invoke_testDeserialization2543() throws Exception {
        try {
            testDeserialization2543();
        } finally {
        }
    }


    void __invoke_testDelegatingDeserialization2543() throws Exception {
        try {
            testDelegatingDeserialization2543();
        } finally {
        }
    }

}
