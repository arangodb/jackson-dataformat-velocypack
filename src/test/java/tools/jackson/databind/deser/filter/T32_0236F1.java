package tools.jackson.databind.deser.filter;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.deser.DeserializationProblemHandler;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.jsontype.TypeIdResolver;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0236F1 {
private static final byte[] ENUM_NUMBER_3 = VPackWireFixtureTest.hex("33");
private static final byte[] ENUM_STRING_B = VPackWireFixtureTest.hex("41 42");
private static final byte[] INVALID_UUID = VPackWireFixtureTest.hex(
            "4c 6e 6f 74 2d 61 2d 6e 75 6d 62 65 72");
private static final byte[] BASE64_TEXT = VPackWireFixtureTest.hex(
            "46 66 6f 6f 62 61 72");
private static final byte[] ADMIN_CASE_EXACT = VPackWireFixtureTest.hex(
            "14 2b 45 61 64 6d 69 6e 14 22 "
          + "48 61 64 6d 69 6e 4b 65 79 46 48 41 43 4b 45 44 "
          + "48 75 73 65 72 6e 61 6d 65 45 61 6c 69 63 65 02 01");
private static final byte[] ADMIN_CASE_MIXED = VPackWireFixtureTest.hex(
            "14 2a 45 61 64 6d 69 6e 14 21 "
          + "48 41 64 6d 69 6e 4b 65 79 47 48 41 43 4b 45 44 32 "
          + "48 75 73 65 72 6e 61 6d 65 43 62 6f 62 02 01");
private static final byte[] UNKNOWN_PROPERTIES = VPackWireFixtureTest.hex(
            "14 0f 41 61 31 41 62 32 41 78 33 41 79 34 04");

    // Provenance: IgnorePropertiesCaseInsensitive5962Test#test5962_negativeControl_withoutCaseInsensitivity.
    void test5962_negativeControl_withoutCaseInsensitivityVpack() throws Exception {
        BaselineContainer value = VPackMapper.builder().build()
                .readValue(ADMIN_CASE_EXACT, BaselineContainer.class);
        assertNotNull(value.admin);
        assertTrue(!"HACKED".equals(value.admin.adminKey));
        assertEquals("alice", value.admin.username);
    }

    // Provenance: IgnorePropertiesCaseInsensitive5962Test#test5962_caseInsensitiveRebuildRestoresIgnoredProperty.
    void test5962_caseInsensitiveRebuildRestoresIgnoredPropertyVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder().build();

        Container exact = mapper.readValue(ADMIN_CASE_EXACT, Container.class);
        assertNotNull(exact.admin);
        assertTrue(!"HACKED".equals(exact.admin.adminKey));
        assertEquals("alice", exact.admin.username);

        Container mixed = mapper.readValue(ADMIN_CASE_MIXED, Container.class);
        assertNotNull(mixed.admin);
        assertTrue(!"HACKED2".equals(mixed.admin.adminKey));
    }
private static VPackMapper.Builder defaultTypingMapper() {
        return VPackMapper.builder().activateDefaultTyping(BasicPolymorphicTypeValidator.builder()
                        .allowIfBaseType(Object.class).build(),
                DefaultTyping.NON_FINAL);
    }
private static final byte[] DEFAULT_TYPED_CONTENT = defaultTypedContent();
private static byte[] defaultTypedContent() {
        byte[] dummy = compactObject(pair("aField", string("some value")));
        byte[] unknown = compactObject(pair("aField", string("some value")));
        byte[] typedItems = compactArray(
                compactArray(string(DummyContent2221.class.getName()), dummy),
                compactArray(string("no.such.ClassBeingNotOnTheClasspath"), unknown));
        byte[] typedCollection = compactArray(string("java.util.ArrayList"), typedItems);
        return compactObject(
                pair("_class", string(GenericContent2221.class.getName())),
                pair("innerObjects", typedCollection));
    }
private static byte[] pair(String name, byte[] value) {
        return concat(string(name), value);
    }
private static byte[] compactObject(byte[]... pairs) {
        return compactContainer(0x14, pairs);
    }
private static byte[] compactArray(byte[]... values) {
        return compactContainer(0x13, values);
    }
private static byte[] compactContainer(int marker, byte[][] values) {
        int bodyLength = 0;
        for (byte[] value : values) {
            bodyLength += value.length;
        }
        if (values.length > 126) {
            throw new AssertionError("test fixture exceeds compact count layout");
        }
        byte[] count = new byte[] { (byte) values.length };
        int lengthWidth = 1;
        int length = bodyLength + 1 + lengthWidth + count.length;
        while (lengthWidth != forwardVarint(length).length) {
            lengthWidth = forwardVarint(length).length;
            length = bodyLength + 1 + lengthWidth + count.length;
        }
        byte[] lengthBytes = forwardVarint(length);
        byte[] result = new byte[bodyLength + 1 + lengthBytes.length + count.length];
        result[0] = (byte) marker;
        int offset = 1;
        System.arraycopy(lengthBytes, 0, result, offset, lengthBytes.length);
        offset += lengthBytes.length;
        for (byte[] value : values) {
            System.arraycopy(value, 0, result, offset, value.length);
            offset += value.length;
        }
        System.arraycopy(count, 0, result, offset, count.length);
        return result;
    }
private static byte[] forwardVarint(int value) {
        byte[] result = new byte[4];
        int offset = 0;
        do {
            int group = value & 0x7f;
            value >>>= 7;
            result[offset++] = (byte) (group | (value == 0 ? 0 : 0x80));
        } while (value != 0);
        return java.util.Arrays.copyOf(result, offset);
    }
private static byte[] string(String value) {
        byte[] payload = value.getBytes(StandardCharsets.UTF_8);
        if (payload.length > 126) {
            throw new AssertionError("test fixture string exceeds short-string layout");
        }
        byte[] result = new byte[payload.length + 1];
        result[0] = (byte) (0x40 + payload.length);
        System.arraycopy(payload, 0, result, 1, payload.length);
        return result;
    }
private static byte[] concat(byte[]... parts) {
        int length = 0;
        for (byte[] part : parts) {
            length += part.length;
        }
        byte[] result = new byte[length];
        int offset = 0;
        for (byte[] part : parts) {
            System.arraycopy(part, 0, result, offset, part.length);
            offset += part.length;
        }
        return result;
    }
static class WeirdNumberHandler extends DeserializationProblemHandler {
        private final Object value;

        WeirdNumberHandler(Object value) {
            this.value = value;
        }

        @Override
        public Object handleWeirdNumberValue(DeserializationContext ctxt, Class<?> targetType,
                Number value, String failureMsg) {
            return this.value;
        }
    }
static class WeirdStringHandler extends DeserializationProblemHandler {
        private final Object value;

        WeirdStringHandler(Object value) {
            this.value = value;
        }

        @Override
        public Object handleWeirdStringValue(DeserializationContext ctxt, Class<?> targetType,
                String value, String failureMsg) {
            return this.value;
        }
    }
static class UnknownTypeIdHandler extends DeserializationProblemHandler {
        @Override
        public tools.jackson.databind.JavaType handleUnknownTypeId(DeserializationContext ctxt,
                tools.jackson.databind.JavaType baseType, String subTypeId,
                TypeIdResolver idResolver, String failureMsg) {
            return ctxt.constructType(Void.class);
        }
    }
enum SingleValuedEnum {
        A
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "_class")
    @SuppressWarnings("rawtypes")
    static class GenericContent2221 {
        private java.util.Collection innerObjects;

        public java.util.Collection getInnerObjects() {
            return innerObjects;
        }

        public void setInnerObjects(java.util.Collection innerObjects) {
            this.innerObjects = innerObjects;
        }
    }
static class DummyContent2221 {
        private String aField;

        public DummyContent2221() { }

        public String getaField() {
            return aField;
        }

        public void setaField(String aField) {
            this.aField = aField;
        }
    }
static class Container {
        @JsonIgnoreProperties("adminKey")
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
        public AdminDto admin;
    }
static class BaselineContainer {
        @JsonIgnoreProperties("adminKey")
        public AdminDto admin;
    }
static class AdminDto {
        public String adminKey = "DEFAULT";
        public String username;
    }
@JsonIgnoreProperties(ignoreUnknown = true)
    static class IgnoreUnknownAnySetter {
        int a;
        int b;
        Map<String, Object> props = new java.util.HashMap<>();

        @JsonCreator
        IgnoreUnknownAnySetter(@JsonProperty("a") int a, @JsonProperty("b") int b) {
            this.a = a;
            this.b = b;
        }

        @JsonAnySetter
        public void addProperty(String key, Object value) {
            props.put(key, value);
        }

        @JsonAnyGetter
        public Map<String, Object> getProperties() {
            return props;
        }
    }
@JsonIgnoreProperties(ignoreUnknown = true)
    static class IgnoreUnknownUnwrapped {
        int a;
        int b;
        @JsonUnwrapped
        UnwrappedChild child;

        @JsonCreator
        IgnoreUnknownUnwrapped(@JsonProperty("a") int a, @JsonProperty("b") int b) {
            this.a = a;
            this.b = b;
        }

        static class UnwrappedChild {
            public int x;
            public int y;
        }
    }

    void __invoke_test5962_negativeControl_withoutCaseInsensitivityVpack() throws Exception {
        try {
            test5962_negativeControl_withoutCaseInsensitivityVpack();
        } finally {
        }
    }


    void __invoke_test5962_caseInsensitiveRebuildRestoresIgnoredPropertyVpack() throws Exception {
        try {
            test5962_caseInsensitiveRebuildRestoresIgnoredPropertyVpack();
        } finally {
        }
    }

}
