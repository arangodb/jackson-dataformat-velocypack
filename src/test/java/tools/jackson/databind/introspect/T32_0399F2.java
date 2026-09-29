package tools.jackson.databind.introspect;

import java.util.List;
import javax.xml.namespace.QName;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.deser.std.StdDeserializer;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0399F2 {
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

    // Provenance: JsonPropertyRename5398Test#testRenamedPropertyWithIgnoredSetter5398().
    void testRenamedPropertyWithIgnoredSetter5398() throws Exception {
        TestRename5398 original = new TestRename5398();
        original.setProp("someValue");
        assertArrayEquals(RENAMED, new VPackMapper().writeValueAsBytes(original));

        TestRename5398 result = new VPackMapper().readValue(RENAMED,
                TestRename5398.class);
        assertNotNull(result);
        assertNull(result.getProp());
    }

    // Provenance: JsonPropertyRename5398Test#testStandardPropertyWithIgnoredSetter5398().
    void testStandardPropertyWithIgnoredSetter5398() throws Exception {
        TestStd5398 original = new TestStd5398();
        original.setProp("someValue");
        assertArrayEquals(STANDARD, new VPackMapper().writeValueAsBytes(original));

        TestStd5398 result = new VPackMapper().readValue(STANDARD, TestStd5398.class);
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

    void __invoke_testRenamedPropertyWithIgnoredSetter5398() throws Exception {
        try {
            testRenamedPropertyWithIgnoredSetter5398();
        } finally {
        }
    }


    void __invoke_testStandardPropertyWithIgnoredSetter5398() throws Exception {
        try {
            testStandardPropertyWithIgnoredSetter5398();
        } finally {
        }
    }

}
