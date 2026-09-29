package tools.jackson.databind.jsontype;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import tools.jackson.core.JsonParser;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.jsontype.TypeDeserializer;
import tools.jackson.databind.exc.InvalidDefinitionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0432F2 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] ISSUE_1128 = VPackWireFixtureTest.hex(
            "14 1b 46 65 6e 74 69 74 79 14 11 46 70 61 72 65 6e 74 "
          + "14 07 42 69 64 32 01 01 01");
private static final byte[] SIMPLE_TYPE_ID_1735 = VPackWireFixtureTest.hex(
            "14 1e 41 77 14 19 44 74 79 70 65 50 6a 61 76 61 2e 6c 61 6e 67 "
          + "2e 53 74 72 69 6e 67 01 01");
private static final byte[] NESTED_TYPE_ID_1735 = VPackWireFixtureTest.hex(
            "14 42 41 77 14 3d 44 74 79 70 65 74 6a 61 76 61 2e 75 74 69 6c "
          + "2e 48 61 73 68 4d 61 70 3c 6a 61 76 61 2e 6c 61 6e 67 2e 53 74 "
          + "72 69 6e 67 2c 6a 61 76 61 2e 6c 61 6e 67 2e 53 74 72 69 6e 67 "
          + "3e 01 01");
private static void assertNatural(VPackFactory factory, byte[] input,
            Object before, Object expected) throws Exception {
        try (JsonParser parser = factory.createParser(ObjectReadContext.empty(), input)) {
            assertEquals(before, TypeDeserializer.deserializeIfNatural(parser, null, Object.class));
            parser.nextToken();
            assertEquals(expected, TypeDeserializer.deserializeIfNatural(parser, null, Object.class));
        }
    }

    // Provenance: TypeIdPropertyDup1410Test#dupPropsFailsWithHelpfulMessage().
    void dupPropsFailsWithHelpfulMessageVpack() {
        InvalidDefinitionException error = assertThrows(InvalidDefinitionException.class,
                () -> MAPPER.writeValueAsBytes(new BackendEvent("foo", "hello", "bar", null)));
        assertTrue(error.getMessage().contains("Conflict between type id property"));
        assertTrue(error.getMessage().contains("EXISTING_PROPERTY"));
    }
@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "object-type")
    @JsonSubTypes({ @JsonSubTypes.Type(value = Dog.class, name = "doggy") })
    static abstract class Animal { public String name; }
static class Dog extends Animal {
        public int boneCount;
        Dog(String name, int bones) { this.name = name; boneCount = bones; }
    }
static class ContainerWithGetter<T extends Animal> {
        private T animal;
        ContainerWithGetter(T value) { animal = value; }
        public T getAnimal() { return animal; }
    }
static class ContainerWithField<T extends Animal> {
        public T animal;
        ContainerWithField(T value) { animal = value; }
    }
static class WrappedContainerWithField { public ContainerWithField<?> animalContainer; }
@JsonTypeInfo(use = Id.CLASS, include = As.PROPERTY, property = "@classAttr1")
    static class MyClass { public List<MyParam<?>> params = new ArrayList<>(); }
@JsonTypeInfo(use = Id.CLASS, include = As.PROPERTY, property = "@classAttr2")
    static class MyParam<T> {
        public T value;
        MyParam() { }
        MyParam(T value) { this.value = value; }
    }
static class SomeObject { public String someValue; }
static class ContainerWithTwoAnimals<U extends Animal, V extends Animal>
            extends ContainerWithField<U> {
        public V otherAnimal;
        ContainerWithTwoAnimals(U first, V second) { super(first); otherAnimal = second; }
    }
@SuppressWarnings("rawtypes")
    static abstract class HObj<M extends HObj> {
        public long id;
        @JsonSerialize(typing = JsonSerialize.Typing.STATIC)
        public M parent;
    }
static class DevBase extends HObj<DevBase> { public String tag; }
static class Dev extends DevBase { public long p1; }
static class DevM extends Dev { private long m1; public long getM1() { return m1; } }
static class ContainerBase<T> { public T entity; }
static class DevMContainer extends ContainerBase<DevM> { }
static class SuperNode<T> { }
static class SuperTestClass { }
@SuppressWarnings("serial")
    static class Node<T extends SuperTestClass & Cloneable> extends SuperNode<Node<T>>
            implements java.io.Serializable {
        public List<Node<T>> children = new ArrayList<>();
        public List<? extends SuperNode<Node<T>>> getChildren() { return children; }
    }
static class Wrapper1735 {
        @JsonTypeInfo(use = Id.CLASS, property = "type")
        public Payload1735 w;
    }
static class Payload1735 { public void setValue(String value) { } }
public static class JSONResponse<T> {
        private T result;
        public T getResult() { return result; }
        public void setResult(T value) { result = value; }
    }
@JsonTypeInfo(use = Id.CLASS, include = As.PROPERTY, property = "@class")
    public static class Parent356 { public String parentContent = "PARENT"; }
public static class Child356_1 extends Parent356 { public String childContent1 = "CHILD1"; }
public static class Child356_2 extends Parent356 { public String childContent2 = "CHILD2"; }
@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "source")
    @JsonSubTypes({ @JsonSubTypes.Type(value = BackendEvent.class, name = "BACKEND") })
    static abstract class EnvironmentEvent {
        private String environmentName;
        private String message;
        EnvironmentEvent() { }
        EnvironmentEvent(String env, String msg) { environmentName = env; message = msg; }
        public String getEnvironmentName() { return environmentName; }
        public abstract EnvironmentEventSource getSource();
        public String getMessage() { return message; }
    }
enum EnvironmentEventSource { BACKEND }
static class BackendEvent extends EnvironmentEvent {
        private String status;
        private Object resultData;
        BackendEvent() { }
        BackendEvent(String env, String message, String status, Object result) {
            super(env, message); this.status = status; resultData = result;
        }
        @Override public EnvironmentEventSource getSource() { return EnvironmentEventSource.BACKEND; }
        public String getStatus() { return status; }
        public Object getResultData() { return resultData; }
    }

    void __invoke_dupPropsFailsWithHelpfulMessageVpack() throws Exception {
        try {
            dupPropsFailsWithHelpfulMessageVpack();
        } finally {
        }
    }

}
