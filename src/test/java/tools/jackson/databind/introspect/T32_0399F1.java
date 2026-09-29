package tools.jackson.databind.introspect;

import java.util.List;
import javax.xml.namespace.QName;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.deser.std.StdDeserializer;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0399F1 {
private static final byte[] RENAMED = VPackWireFixtureTest.hex(
            "0b 1a 01 4b 72 65 6e 61 6d 65 64 50 72 6f 70 49 73 6f 6d 65 56 61 6c 75 65 03");
private static final byte[] STANDARD = VPackWireFixtureTest.hex(
            "0b 13 01 44 70 72 6f 70 49 73 6f 6d 65 56 61 6c 75 65 03");
private static final byte[] QNAME_INPUT = VPackWireFixtureTest.hex(
            "0b 82 05 4c 65 6e 75 6d 50 72 6f 70 65 72 74 79 46 56 41 4c 55 45 31 "
            + "4b 6d 79 61 74 74 72 69 62 75 74 65 4e 61 74 74 72 69 62 75 74 65 56 61 6c 75 65 "
            + "49 6d 79 65 6c 65 6d 65 6e 74 4c 65 6c 65 6d 65 6e 74 56 61 6c 75 65 "
            + "49 6d 79 77 72 61 70 70 65 64 02 16 53 77 72 61 70 70 65 64 45 6c 65 6d 65 6e 74 56 61 6c 75 65 "
            + "45 71 6e 61 6d 65 4d 7b 75 72 6e 3a 68 69 7d 68 65 6c 6c 6f "
            + "03 17 32 49 69");

    // Provenance: JsonIgnoreSetterFieldBypass5398Test#renamedGetterStillSerializes().
    void renamedGetterStillSerializes() throws Exception {
        RenamedGetterIgnoredSetter bean = new RenamedGetterIgnoredSetter();
        bean.setProp("someValue");
        assertArrayEquals(RENAMED, new VPackMapper().writeValueAsBytes(bean));
    }

    // Provenance: JsonIgnoreSetterFieldBypass5398Test#standardGetterStillSerializes().
    void standardGetterStillSerializes() throws Exception {
        StandardGetterIgnoredSetter bean = new StandardGetterIgnoredSetter();
        bean.setProp("someValue");
        assertArrayEquals(STANDARD, new VPackMapper().writeValueAsBytes(bean));
    }

    // Provenance: JsonIgnoreSetterFieldBypass5398Test#renamedIgnoredSetterDoesNotBypassViaField().
    void renamedIgnoredSetterDoesNotBypassViaField() throws Exception {
        RenamedGetterIgnoredSetter result = new VPackMapper().readValue(RENAMED,
                RenamedGetterIgnoredSetter.class);
        assertNotNull(result);
        assertNull(result.getProp());
    }

    // Provenance: JsonIgnoreSetterFieldBypass5398Test#standardIgnoredSetterDoesNotBypassViaField().
    void standardIgnoredSetterDoesNotBypassViaField() throws Exception {
        StandardGetterIgnoredSetter result = new VPackMapper().readValue(STANDARD,
                StandardGetterIgnoredSetter.class);
        assertNotNull(result);
        assertNull(result.getProp());
    }

    // Provenance: JsonIgnoreSetterFieldBypass5398Test#implicitFieldNameDoesNotWriteWhenSetterIgnored().
    void implicitFieldNameDoesNotWriteWhenSetterIgnored() throws Exception {
        RenamedGetterIgnoredSetter result = VPackMapper.builder()
                .build().readerFor(RenamedGetterIgnoredSetter.class)
                .without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .readValue(STANDARD);
        assertNotNull(result);
        assertNull(result.getProp());
    }

    // Provenance: JsonIgnoreSetterFieldBypass5398Test#inferMutatorsDisabledStillBlocksWrite().
    void inferMutatorsDisabledStillBlocksWrite() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .disable(MapperFeature.INFER_PROPERTY_MUTATORS)
                .build();
        RenamedGetterIgnoredSetter result = mapper.readValue(RENAMED,
                RenamedGetterIgnoredSetter.class);
        assertNotNull(result);
        assertNull(result.getProp());
    }

    // Provenance: JsonIgnoreSetterFieldBypass5398Test#readOnlyRenamedGetterIsSerializeOnly().
    void readOnlyRenamedGetterIsSerializeOnly() throws Exception {
        ReadOnlyRenamedGetter bean = new ReadOnlyRenamedGetter();
        bean.setProp("someValue");
        assertArrayEquals(RENAMED, new VPackMapper().writeValueAsBytes(bean));

        ReadOnlyRenamedGetter result = new VPackMapper().readValue(RENAMED,
                ReadOnlyRenamedGetter.class);
        assertNotNull(result);
        assertNull(result.getProp());
    }
static class JacksonExample {
        protected String attributeProperty;
        protected String elementProperty;
        protected List<String> wrappedElementProperty;
        protected EnumExample enumProperty;
        protected QName qname;

        @JsonSerialize(using = QNameSerializer.class)
        public QName getQname() { return qname; }

        @JsonDeserialize(using = QNameDeserializer.class)
        public void setQname(QName value) { qname = value; }

        @JsonProperty("myattribute")
        public String getAttributeProperty() { return attributeProperty; }

        @JsonProperty("myattribute")
        public void setAttributeProperty(String value) { attributeProperty = value; }

        @JsonProperty("myelement")
        public String getElementProperty() { return elementProperty; }

        @JsonProperty("myelement")
        public void setElementProperty(String value) { elementProperty = value; }

        @JsonProperty("mywrapped")
        public List<String> getWrappedElementProperty() { return wrappedElementProperty; }

        @JsonProperty("mywrapped")
        public void setWrappedElementProperty(List<String> value) { wrappedElementProperty = value; }

        public EnumExample getEnumProperty() { return enumProperty; }
        public void setEnumProperty(EnumExample value) { enumProperty = value; }
    }
public static class QNameSerializer extends ValueSerializer<QName> {
        @Override
        public void serialize(QName value, tools.jackson.core.JsonGenerator generator,
                SerializationContext ctxt) {
            generator.writeString(value.toString());
        }
    }
public static class QNameDeserializer extends StdDeserializer<QName> {
        public QNameDeserializer() { super(QName.class); }

        @Override
        public QName deserialize(JsonParser parser, tools.jackson.databind.DeserializationContext ctxt) {
            if (!parser.hasToken(JsonToken.VALUE_STRING)) {
                throw new IllegalArgumentException("Unexpected token " + parser.currentToken());
            }
            return QName.valueOf(parser.getString());
        }
    }
enum EnumExample { VALUE1 }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
    static class AnnotatedBase { }
static class AnnotatedSub extends AnnotatedBase { }
static class AnnotatedSubSub extends AnnotatedSub { }
static class RenamedGetterIgnoredSetter {
        private String prop;

        @JsonProperty("renamedProp")
        public String getProp() { return prop; }

        @JsonIgnore
        public void setProp(String value) { prop = value; }
    }
static class StandardGetterIgnoredSetter {
        private String prop;

        @JsonProperty
        public String getProp() { return prop; }

        @JsonIgnore
        public void setProp(String value) { prop = value; }
    }
static class ReadOnlyRenamedGetter {
        private String prop;

        @JsonProperty(value = "renamedProp", access = JsonProperty.Access.READ_ONLY)
        public String getProp() { return prop; }

        public void setProp(String value) { prop = value; }
    }
static class TestRename5398 {
        private String prop;

        @JsonProperty("renamedProp")
        public String getProp() { return prop; }

        @JsonIgnore
        public void setProp(String value) { prop = value; }
    }
static class TestStd5398 {
        private String prop;

        @JsonProperty
        public String getProp() { return prop; }

        @JsonIgnore
        public void setProp(String value) { prop = value; }
    }

    void __invoke_renamedGetterStillSerializes() throws Exception {
        try {
            renamedGetterStillSerializes();
        } finally {
        }
    }


    void __invoke_standardGetterStillSerializes() throws Exception {
        try {
            standardGetterStillSerializes();
        } finally {
        }
    }


    void __invoke_renamedIgnoredSetterDoesNotBypassViaField() throws Exception {
        try {
            renamedIgnoredSetterDoesNotBypassViaField();
        } finally {
        }
    }


    void __invoke_standardIgnoredSetterDoesNotBypassViaField() throws Exception {
        try {
            standardIgnoredSetterDoesNotBypassViaField();
        } finally {
        }
    }


    void __invoke_implicitFieldNameDoesNotWriteWhenSetterIgnored() throws Exception {
        try {
            implicitFieldNameDoesNotWriteWhenSetterIgnored();
        } finally {
        }
    }


    void __invoke_inferMutatorsDisabledStillBlocksWrite() throws Exception {
        try {
            inferMutatorsDisabledStillBlocksWrite();
        } finally {
        }
    }


    void __invoke_readOnlyRenamedGetterIsSerializeOnly() throws Exception {
        try {
            readOnlyRenamedGetterIsSerializeOnly();
        } finally {
        }
    }

}
