package tools.jackson.databind.jsontype;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.DatabindContext;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.exc.InvalidTypeIdException;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0427F0 {
private static final ObjectMapper MAPPER = VPackMapper.builder()
            .polymorphicTypeValidator(new NoCheckSubTypeValidator427())
            .build();
private static final byte[] UNKNOWN_RECOVERY_FIRST = compactObject(
            pair("version", doubleZero()),
            pair("application", string("123")),
            pair("item", compactObject(
                    pair("type", string("xevent")), pair("location", string("location1")))),
            pair("item2", compactObject(
                    pair("type", string("event")), pair("location", string("location1")))));
private static final byte[] UNKNOWN_RECOVERY_SECOND = compactObject(
            pair("item", compactObject(
                    pair("type", string("xevent")), pair("location", string("location1")))),
            pair("version", doubleZero()), pair("application", string("123")));
private static final byte[] EMPTY_STRING_VALUE = compactObject(pair("value", string("")));
private static final byte[] INCOMPATIBLE_TARGET = compactObject(
            pair("type", string("a")), pair("base", string("foo")), pair("valueA", integer(3)));

    // Provenance: TestPolymorphicWithDefaultImpl#testUnknownTypeIDRecovery().
    void testUnknownTypeIDRecoveryVpack() throws Exception {
        ObjectReader reader = MAPPER.readerFor(CallRecord427.class)
                .without(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE);

        CallRecord427 first = reader.readValue(UNKNOWN_RECOVERY_FIRST);
        assertNull(first.item);
        assertNotNull(first.item2);

        CallRecord427 second = reader.readValue(UNKNOWN_RECOVERY_SECOND);
        assertNull(second.item);
        assertEquals("123", second.application);
    }

    // Provenance: TestPolymorphicWithDefaultImpl#testWithoutEmptyStringAsNullObject1533().
    void testWithoutEmptyStringAsNullObject1533Vpack() throws Exception {
        ObjectReader reader = MAPPER.readerFor(AsPropertyWrapper427.class)
                .without(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        assertThrows(InvalidTypeIdException.class, () -> reader.readValue(EMPTY_STRING_VALUE));
    }

    // Provenance: TestPolymorphicWithDefaultImpl#testWithEmptyStringAsNullObject1533().
    void testWithEmptyStringAsNullObject1533Vpack() throws Exception {
        ObjectReader reader = MAPPER.readerFor(AsPropertyWrapper427.class)
                .with(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        AsPropertyWrapper427 wrapper = reader.readValue(EMPTY_STRING_VALUE);
        assertNull(wrapper.value);
    }

    // Provenance: TestPolymorphicWithDefaultImpl#testWithIncompatibleTargetType1861().
    void testWithIncompatibleTargetType1861Vpack() throws Exception {
        Impl1861A427 result = MAPPER.readValue(INCOMPATIBLE_TARGET, Impl1861A427.class);
        assertNotNull(result);
        assertEquals("foo", result.base);
        assertEquals(3, result.valueA);
    }
private static byte[] methodBean(byte[] value) {
        return compactObject(pair("value", value));
    }
private static byte[] compactObject(byte[]... pairs) {
        return compact(0x14, pairs);
    }
private static byte[] compactArray(byte[]... values) {
        return compact(0x13, values);
    }
private static byte[] compact(int marker, byte[][] values) {
        ByteArrayOutputStream body = new ByteArrayOutputStream();
        for (byte[] value : values) {
            body.writeBytes(value);
        }
        byte[] reverseCount = reverseVarint(values.length);
        int lengthWidth = 1;
        byte[] length;
        do {
            int totalLength = 1 + lengthWidth + body.size() + reverseCount.length;
            length = forwardVarint(totalLength);
            if (length.length == lengthWidth) {
                break;
            }
            lengthWidth = length.length;
        } while (true);

        ByteArrayOutputStream result = new ByteArrayOutputStream();
        result.write(marker);
        result.writeBytes(length);
        result.writeBytes(body.toByteArray());
        result.writeBytes(reverseCount);
        return result.toByteArray();
    }
private static byte[] pair(String name, byte[] value) {
        byte[] key = name.getBytes(StandardCharsets.UTF_8);
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        result.write(0x40 + key.length);
        result.writeBytes(key);
        result.writeBytes(value);
        return result.toByteArray();
    }
private static byte[] string(String value) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        if (bytes.length > 126) {
            throw new IllegalArgumentException("fixture string too long");
        }
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        result.write(0x40 + bytes.length);
        result.writeBytes(bytes);
        return result.toByteArray();
    }
private static byte[] typedObject(Class<?> type, byte[]... properties) {
        return typedValue(type, compactObject(properties));
    }
private static byte[] typedValue(Class<?> type, byte[] value) {
        return compactArray(string(type.getName()), value);
    }
private static byte[] integer(int value) {
        if (value >= 0 && value <= 9) {
            return new byte[] { (byte) (0x30 + value) };
        }
        if (value >= 0 && value <= 255) {
            return new byte[] { 0x28, (byte) value };
        }
        throw new IllegalArgumentException("fixture integer out of range");
    }
private static byte[] doubleZero() {
        return new byte[] { 0x1b, 0, 0, 0, 0, 0, 0, 0, 0 };
    }
private static byte[] forwardVarint(int value) {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        do {
            int group = value & 0x7f;
            value >>>= 7;
            result.write(group | (value == 0 ? 0 : 0x80));
        } while (value != 0);
        return result.toByteArray();
    }
private static byte[] reverseVarint(int value) {
        List<Integer> groups = new ArrayList<>();
        do {
            groups.add(value & 0x7f);
            value >>>= 7;
        } while (value != 0);
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        for (int i = groups.size() - 1; i >= 0; --i) {
            result.write(groups.get(i) | (groups.size() > 1 && i == 0 ? 0x80 : 0));
        }
        return result.toByteArray();
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
    @JsonSubTypes(@JsonSubTypes.Type(name = "event", value = Event427.class))
    private interface Item427 { }
private static class Event427 implements Item427 {
        public String location;
    }
private static class CallRecord427 {
        public double version;
        public String application;
        public Item427 item;
        public Item427 item2;
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = DefaultImpl1861_427.class)
    @JsonSubTypes(@JsonSubTypes.Type(name = "a", value = Impl1861A427.class))
    private static abstract class Bean1861_427 {
        public String base;
    }
private static class DefaultImpl1861_427 extends Bean1861_427 {
        public int id;
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
    private static class AsProperty427 { }
private static class AsPropertyWrapper427 {
        public AsProperty427 value;
    }
private static class Impl1861A427 extends Bean1861_427 {
        public int valueA;
    }
private static class NoCheckSubTypeValidator427 extends PolymorphicTypeValidator.Base {
        private static final long serialVersionUID = 1L;

        @Override
        public Validity validateBaseType(DatabindContext ctxt, JavaType baseType) {
            return Validity.ALLOWED;
        }
    }
private static class FieldWrapperBean427 {
        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY)
        public Object value;
    }
private static class FieldWrapperBeanList427 extends ArrayList<FieldWrapperBean427> { }
private static class FieldWrapperBeanMap427 extends HashMap<String, FieldWrapperBean427> { }
private static class FieldWrapperBeanArray427 {
        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY)
        public FieldWrapperBean427[] beans;
    }
private static class MethodWrapperBean427 {
        protected Object value;

        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY)
        public Object getValue() { return value; }

        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY)
        public void setValue(Object value) { this.value = value; }
    }
private static class MethodWrapperBeanList427 extends ArrayList<MethodWrapperBean427> { }
private static class MethodWrapperBeanMap427 extends HashMap<String, MethodWrapperBean427> { }
private static class MethodWrapperBeanArray427 {
        protected MethodWrapperBean427[] beans;

        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY)
        public MethodWrapperBean427[] getValue() { return beans; }

        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY)
        public void setValue(MethodWrapperBean427[] value) { beans = value; }
    }
private static class BooleanValue427 {
        public Boolean b;

        @JsonCreator
        public BooleanValue427(Boolean value) { b = value; }

        @com.fasterxml.jackson.annotation.JsonValue
        public Boolean value() { return b; }
    }
private static class StringWrapper427 {
        public String str;
    }
private static class IntWrapper427 {
        public int i;
    }
private static class OtherBean427 {
        public int x = 1;
        public int y = 1;
    }

    void __invoke_testUnknownTypeIDRecoveryVpack() throws Exception {
        try {
            testUnknownTypeIDRecoveryVpack();
        } finally {
        }
    }


    void __invoke_testWithoutEmptyStringAsNullObject1533Vpack() throws Exception {
        try {
            testWithoutEmptyStringAsNullObject1533Vpack();
        } finally {
        }
    }


    void __invoke_testWithEmptyStringAsNullObject1533Vpack() throws Exception {
        try {
            testWithEmptyStringAsNullObject1533Vpack();
        } finally {
        }
    }


    void __invoke_testWithIncompatibleTargetType1861Vpack() throws Exception {
        try {
            testWithIncompatibleTargetType1861Vpack();
        } finally {
        }
    }

}
