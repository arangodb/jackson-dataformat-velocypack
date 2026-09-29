package tools.jackson.databind.contextual;

import java.util.List;

import com.fasterxml.jackson.annotation.JacksonAnnotation;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.Version;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.ObjectWriter;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.cfg.ContextAttributes;
import tools.jackson.databind.deser.std.StdScalarDeserializer;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.StdScalarSerializer;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0142F0 {
private static final String KEY = "foobar";
private static final byte[] SINGLE_X = VPackWireFixtureTest.hex(
            "14 0b 45 76 61 6c 75 65 41 78 01");
private static final byte[] ARRAY_A_B = VPackWireFixtureTest.hex(
            "13 19 14 0b 45 76 61 6c 75 65 41 61 01 "
            + "14 0b 45 76 61 6c 75 65 41 62 01 02");
private static final byte[] CONTEXT_ARRAY_X = VPackWireFixtureTest.hex(
            "14 0e 45 62 65 61 6e 73 13 05 41 78 01 01");
private static final byte[] CONTEXT_ARRAY_A_B = VPackWireFixtureTest.hex(
            "14 10 45 62 65 61 6e 73 13 07 41 61 41 62 02 01");
private static final byte[] EXPECTED_0_A_1_B = VPackWireFixtureTest.hex(
            "13 1d "
            + "14 0d 45 76 61 6c 75 65 43 30 3a 61 01 "
            + "14 0d 45 76 61 6c 75 65 43 31 3a 62 01 02");
private static final byte[] EXPECTED_2_A_3_B = VPackWireFixtureTest.hex(
            "13 1d "
            + "14 0d 45 76 61 6c 75 65 43 32 3a 61 01 "
            + "14 0d 45 76 61 6c 75 65 43 33 3a 62 01 02");
private static final byte[] EXPECTED_72_A_73_B = VPackWireFixtureTest.hex(
            "13 1f "
            + "14 0e 45 76 61 6c 75 65 44 37 32 3a 61 01 "
            + "14 0e 45 76 61 6c 75 65 44 37 33 3a 62 01 02");
private static final byte[] EXPECTED_13_A_14_B = VPackWireFixtureTest.hex(
            "13 1f "
            + "14 0e 45 76 61 6c 75 65 44 31 33 3a 61 01 "
            + "14 0e 45 76 61 6c 75 65 44 31 34 3a 62 01 02");
private static final byte[] EXPECTED_3_XYZ = VPackWireFixtureTest.hex(
            "14 0f 45 76 61 6c 75 65 45 33 3a 78 79 7a 01");
private static final ObjectMapper MAPPER = new VPackMapper();

    void contextAttributeDeserSimplePerCall() throws Exception {
        ContextAttributeDeserPOJO[] values = MAPPER.readerFor(ContextAttributeDeserPOJO[].class)
                .readValue(ARRAY_A_B);
        assertEquals("a/0", values[0].value);
        assertEquals("b/1", values[1].value);

        ContextAttributeDeserPOJO[] valuesAgain = MAPPER.readerFor(ContextAttributeDeserPOJO[].class)
                .readValue(ARRAY_A_B);
        assertEquals("a/0", valuesAgain[0].value);
        assertEquals("b/1", valuesAgain[1].value);
    }

    void contextAttributeDeserSimpleDefaults() throws Exception {
        ContextAttributeDeserPOJO value = MAPPER.readerFor(ContextAttributeDeserPOJO.class)
                .withAttribute(KEY, Integer.valueOf(3)).readValue(SINGLE_X);
        assertEquals("x/3", value.value);

        ContextAttributeDeserPOJO valueAgain = MAPPER.readerFor(ContextAttributeDeserPOJO.class)
                .withAttribute(KEY, Integer.valueOf(5)).readValue(SINGLE_X);
        assertEquals("x/5", valueAgain.value);
    }

    void contextAttributeDeserHierarchic() throws Exception {
        ObjectReader reader = MAPPER.readerFor(ContextAttributeDeserPOJO[].class)
                .withAttribute(KEY, Integer.valueOf(2));
        ContextAttributeDeserPOJO[] values = reader.readValue(ARRAY_A_B);
        assertEquals("a/2", values[0].value);
        assertEquals("b/3", values[1].value);

        ContextAttributeDeserPOJO[] valuesAgain = reader.readValue(ARRAY_A_B);
        assertEquals("a/2", valuesAgain[0].value);
        assertEquals("b/3", valuesAgain[1].value);
    }

    void contextAttributeDeserDefaultsViaMapper() throws Exception {
        ContextAttributes attrs = ContextAttributes.getEmpty()
                .withSharedAttribute(KEY, Integer.valueOf(72));
        ObjectMapper mapper = VPackMapper.builder().defaultAttributes(attrs).build();
        ContextAttributeDeserPOJO value = mapper.readerFor(ContextAttributeDeserPOJO.class)
                .readValue(SINGLE_X);
        assertEquals("x/72", value.value);
        ContextAttributeDeserPOJO valueAgain = mapper.readerFor(ContextAttributeDeserPOJO.class)
                .readValue(SINGLE_X);
        assertEquals("x/72", valueAgain.value);
        ContextAttributeDeserPOJO overridden = mapper.readerFor(ContextAttributeDeserPOJO.class)
                .withAttribute(KEY, Integer.valueOf(19)).readValue(SINGLE_X);
        assertEquals("x/19", overridden.value);
    }

    void contextAttributeDeserDefaultsViaReader() throws Exception {
        ContextAttributes attrs = ContextAttributes.getEmpty()
                .withSharedAttribute(KEY, Integer.valueOf(28));
        ContextAttributeDeserPOJO value = MAPPER.reader(attrs)
                .forType(ContextAttributeDeserPOJO.class).readValue(SINGLE_X);
        assertEquals("x/28", value.value);
    }
private static void assertWrittenTreeEquals(byte[] expected, Object value) throws Exception {
        assertWrittenTreeEquals(expected, MAPPER.writer(), value);
    }
private static void assertWrittenTreeEquals(byte[] expected, ObjectWriter writer,
            Object value) throws Exception {
        assertEquals(MAPPER.readTree(expected), MAPPER.readTree(writer.writeValueAsBytes(value)));
    }
private static ObjectMapper annotatedContextualMapper() {
        SimpleModule module = new SimpleModule("test", Version.unknownVersion())
                .addDeserializer(StringValue.class, new AnnotatedContextualDeserializer());
        return VPackMapper.builder().addModule(module).build();
    }
private static final byte[] CONTEXT_CTOR_FOO_BAR = VPackWireFixtureTest.hex(
            "14 0f 41 61 43 66 6f 6f 41 62 43 62 61 72 02");
private static final byte[] CONTEXT_CTOR_1_0 = VPackWireFixtureTest.hex(
            "14 0b 41 61 41 31 41 62 41 30 02");
private static final byte[] CONTEXT_LIST_X = CONTEXT_ARRAY_X;
private static final byte[] CONTEXT_LIST_XYZ = VPackWireFixtureTest.hex(
            "14 12 45 62 65 61 6e 73 13 09 41 78 41 79 41 7a 03 01");
static class ContextAttributeDeserPOJO {
        @JsonDeserialize(using = PrefixStringDeserializer.class)
        public String value;
    }
static class PrefixStringDeserializer extends StdScalarDeserializer<String> {
        protected PrefixStringDeserializer() {
            super(String.class);
        }

        @Override
        public String deserialize(JsonParser parser, DeserializationContext context) {
            Integer value = (Integer) context.getAttribute(KEY);
            int number = (value == null) ? 0 : value.intValue();
            context.setAttribute(KEY, Integer.valueOf(number + 1));
            return parser.getString() + "/" + number;
        }
    }
static class ContextAttributeSerPOJO {
        @JsonSerialize(using = PrefixStringSerializer.class)
        public String value;

        ContextAttributeSerPOJO(String value) {
            this.value = value;
        }
    }
static class PrefixStringSerializer extends StdScalarSerializer<String> {
        protected PrefixStringSerializer() {
            super(String.class);
        }

        @Override
        public void serialize(String value, JsonGenerator generator,
                SerializationContext provider) {
            Integer current = (Integer) provider.getAttribute(KEY);
            int number = (current == null) ? 0 : current.intValue();
            provider.setAttribute(KEY, Integer.valueOf(number + 1));
            generator.writeString(number + ":" + value);
        }
    }
@java.lang.annotation.Target({ java.lang.annotation.ElementType.FIELD,
            java.lang.annotation.ElementType.PARAMETER,
            java.lang.annotation.ElementType.TYPE })
    @java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
    @JacksonAnnotation
    public @interface Name {
        String value();
    }
static class StringValue {
        protected String value;

        StringValue(String value) {
            this.value = value;
        }
    }
static class ContextualCtorBean {
        protected String a, b;

        @JsonCreator
        ContextualCtorBean(@Name("CtorA") @JsonProperty("a") StringValue a,
                @Name("CtorB") @JsonProperty("b") StringValue b) {
            this.a = a.value;
            this.b = b.value;
        }
    }
static class ContextualArrayBean {
        @Name("array")
        public StringValue[] beans;
    }
static class ContextualListBean {
        @Name("list")
        public List<StringValue> beans;
    }
static class AnnotatedContextualDeserializer extends ValueDeserializer<StringValue> {
        @Override
        public StringValue deserialize(JsonParser parser, DeserializationContext context) {
            return new StringValue("=" + parser.getString());
        }

        @Override
        public ValueDeserializer<?> createContextual(DeserializationContext context,
                BeanProperty property) {
            Name annotation = property.getAnnotation(Name.class);
            String name = (annotation == null) ? "UNKNOWN" : annotation.value();
            return new PrefixContextualDeserializer(name);
        }
    }
static class PrefixContextualDeserializer extends ValueDeserializer<StringValue> {
        private final String name;

        PrefixContextualDeserializer(String name) {
            this.name = name;
        }

        @Override
        public StringValue deserialize(JsonParser parser, DeserializationContext context) {
            return new StringValue(name + "=" + parser.getString());
        }
    }

    void __invoke_contextAttributeDeserSimplePerCall() throws Exception {
        try {
            contextAttributeDeserSimplePerCall();
        } finally {
        }
    }


    void __invoke_contextAttributeDeserSimpleDefaults() throws Exception {
        try {
            contextAttributeDeserSimpleDefaults();
        } finally {
        }
    }


    void __invoke_contextAttributeDeserHierarchic() throws Exception {
        try {
            contextAttributeDeserHierarchic();
        } finally {
        }
    }


    void __invoke_contextAttributeDeserDefaultsViaMapper() throws Exception {
        try {
            contextAttributeDeserDefaultsViaMapper();
        } finally {
        }
    }


    void __invoke_contextAttributeDeserDefaultsViaReader() throws Exception {
        try {
            contextAttributeDeserDefaultsViaReader();
        } finally {
        }
    }

}
