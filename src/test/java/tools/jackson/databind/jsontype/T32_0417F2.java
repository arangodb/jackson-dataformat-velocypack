package tools.jackson.databind.jsontype;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.InvalidTypeIdException;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0417F2 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final ObjectMapper REGISTERED_MAPPER = VPackMapper.builder()
            .enable(DeserializationFeature.FAIL_ON_SUBTYPE_CLASS_NOT_REGISTERED)
            .build();
private static final byte[] UNKNOWN_CLASS = VPackWireFixtureTest.hex(
            "14 25 45 76 61 6c 75 65 14 1c 45 63 6c 61 7a 7a 52 "
          + "63 6f 6d 2e 66 6f 6f 62 61 72 2e 4e 6f 74 68 69 6e 67 01 01");
private static final byte[] WRONG_NAMED_SUBTYPE = VPackWireFixtureTest.hex(
            "14 15 44 74 79 70 65 46 63 68 69 6c 64 32 43 62 61 7a 41 31 02");
private static final byte[] ATOMIC_NAMED_SUBTYPE = VPackWireFixtureTest.hex(
            "14 1c 45 76 61 6c 75 65 14 13 45 24 74 79 70 65 45 69 6d 70 6c 35 "
          + "41 78 28 2a 02 01");

    // Provenance: RegisteredClassDeser5027Test#testDeserializationIdClass().
    void testDeserializationIdClassVpack() throws Exception {
        FooClass registered = REGISTERED_MAPPER.readValue(
                classObjectFixture(FooClassImpl.class.getName()),
                FooClass.class);
        assertInstanceOf(FooClassImpl.class, registered);
        assertThrows(InvalidTypeIdException.class,
                () -> REGISTERED_MAPPER.readValue(
                        classObjectFixture(FooClassImpl2.class.getName()),
                        FooClass.class));
    }

    // Provenance: RegisteredClassDeser5027Test#testDeserializationIdClassNoReg().
    void testDeserializationIdClassNoRegVpack() throws Exception {
        FooClassNoRegSubTypes value = MAPPER.readValue(
                classObjectFixture(FooClassNoRegSubTypesImpl.class.getName()),
                FooClassNoRegSubTypes.class);
        assertInstanceOf(FooClassNoRegSubTypesImpl.class, value);
    }

    // Provenance: RegisteredClassDeser5027Test#testDefaultDeserializationIdClassNoReg().
    void testDefaultDeserializationIdClassNoRegVpack() {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(DeserializationFeature.FAIL_ON_SUBTYPE_CLASS_NOT_REGISTERED)
                .build();
        assertThrows(InvalidTypeIdException.class,
                () -> mapper.readValue(classObjectFixture(FooClassNoRegSubTypesImpl.class.getName()),
                        FooClassNoRegSubTypes.class));
    }

    // Provenance: RegisteredClassDeser5027Test#testDeserializationIdMinimalClass().
    void testDeserializationIdMinimalClassVpack() throws Exception {
        FooMinClass value = REGISTERED_MAPPER.readValue(
                minimalClassObjectFixture(".T32_0417F2$FooMinClassImpl"),
                FooMinClass.class);
        assertInstanceOf(FooMinClassImpl.class, value);
        assertThrows(InvalidTypeIdException.class,
                () -> REGISTERED_MAPPER.readValue(
                        minimalClassObjectFixture(".T32_0417F2$FooMinClassImpl2"),
                        FooMinClass.class));
    }
private static byte[] classPropertyFixture(String className) {
        return compactObject(pair("thisType",
                compactObject(pair("@class", string(className)))));
    }
private static byte[] classObjectFixture(String className) {
        return compactObject(pair("@class", string(className)));
    }
private static byte[] minimalClassObjectFixture(String className) {
        return compactObject(pair("@c", string(className)));
    }
private static byte[] pair(String name, byte[] value) {
        byte[] nameBytes = name.getBytes(StandardCharsets.UTF_8);
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        result.write(0x40 + nameBytes.length);
        result.writeBytes(nameBytes);
        result.writeBytes(value);
        return result.toByteArray();
    }
private static byte[] string(String value) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        if (bytes.length > 126) {
            throw new IllegalArgumentException("fixture string too long");
        }
        result.write(0x40 + bytes.length);
        result.writeBytes(bytes);
        return result.toByteArray();
    }
private static byte[] compactObject(byte[]... pairs) {
        int bodyLength = 0;
        for (byte[] pair : pairs) {
            bodyLength += pair.length;
        }
        int length = 2 + bodyLength + 1;
        if (length > 126 || pairs.length > 126) {
            throw new IllegalArgumentException("fixture object too large");
        }
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        result.write(0x14);
        result.write(length);
        for (byte[] pair : pairs) {
            result.writeBytes(pair);
        }
        result.write(pairs.length);
        return result.toByteArray();
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY,
            property = "clazz")
    abstract static class BaseForUnknownClass { }
static class BaseUnknownWrapper {
        public BaseForUnknownClass value;
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
    @JsonSubTypes({
        @JsonSubTypes.Type(value = Child1.class, name = "child1"),
        @JsonSubTypes.Type(value = Child2.class, name = "child2")
    })
    static class Parent2668 { }
static class Child1 extends Parent2668 {
        public String bar;
    }
static class Child2 extends Parent2668 {
        public String baz;
    }
static abstract class Animal5016 {
        public String name = "animal";
    }
static abstract class Plant {
        public String name = "plant";
    }
static class Tree extends Plant {
        public String name = "tree";
    }
static class AnimalInfo {
        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY,
                property = "@class")
        public Animal5016 thisType;
    }
static class PlantInfo {
        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY,
                property = "@class")
        public Plant thisType;
    }
@JsonSubTypes({
        @JsonSubTypes.Type(name = "impl5", value = ImplForAtomic.class)
    })
    static class BaseForAtomic { }
static class ImplForAtomic extends BaseForAtomic {
        public int x;
    }
static class TypeInfoAtomic {
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "$type")
        public AtomicReference<BaseForAtomic> value;
    }
static class AtomicStringWrapper {
        public AtomicReference<StringWrapper> wrapper;

        AtomicStringWrapper() { }

        AtomicStringWrapper(String value) {
            wrapper = new AtomicReference<>(new StringWrapper(value));
        }
    }
static class StringWrapper {
        public String str;

        StringWrapper() { }

        StringWrapper(String value) {
            str = value;
        }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
    @JsonSubTypes({@JsonSubTypes.Type(value = FooClassImpl.class)})
    abstract static class FooClass { }
static class FooClassImpl extends FooClass { }
static class FooClassImpl2 extends FooClass { }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
    abstract static class FooClassNoRegSubTypes { }
static class FooClassNoRegSubTypesImpl extends FooClassNoRegSubTypes { }
@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS)
    @JsonSubTypes({@JsonSubTypes.Type(value = FooMinClassImpl.class)})
    abstract static class FooMinClass { }
static class FooMinClassImpl extends FooMinClass { }
static class FooMinClassImpl2 extends FooMinClass { }
private static final class ObjectReaderHolder {
        private final tools.jackson.databind.ObjectReader reader;

        ObjectReaderHolder(tools.jackson.databind.ObjectReader reader) {
            this.reader = reader;
        }

        BaseUnknownWrapper read(byte[] input) throws Exception {
            return reader.readValue(input);
        }
    }

    void __invoke_testDeserializationIdClassVpack() throws Exception {
        try {
            testDeserializationIdClassVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationIdClassNoRegVpack() throws Exception {
        try {
            testDeserializationIdClassNoRegVpack();
        } finally {
        }
    }


    void __invoke_testDefaultDeserializationIdClassNoRegVpack() throws Exception {
        try {
            testDefaultDeserializationIdClassNoRegVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationIdMinimalClassVpack() throws Exception {
        try {
            testDeserializationIdMinimalClassVpack();
        } finally {
        }
    }

}
