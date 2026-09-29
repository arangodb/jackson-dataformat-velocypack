package tools.jackson.databind.jsontype;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import tools.jackson.core.JsonParser;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.jsontype.TypeDeserializer;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.exc.InvalidTypeIdException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0432F0 {
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

    // Provenance: TestWithGenerics#testWrapperWithGetter().
    void testWrapperWithGetterVpack() throws Exception {
        Map<?, ?> encoded = MAPPER.readValue(MAPPER.writeValueAsBytes(
                new ContainerWithGetter<Animal>(new Dog("Fluffy", 3))), Map.class);
        Map<?, ?> animal = assertInstanceOf(Map.class, encoded.get("animal"));
        assertEquals("doggy", animal.get("object-type"));
        assertEquals("Fluffy", animal.get("name"));
        assertEquals(3, animal.get("boneCount"));
    }

    // Provenance: TestWithGenerics#testWrapperWithField().
    void testWrapperWithFieldVpack() throws Exception {
        Map<?, ?> encoded = MAPPER.readValue(MAPPER.writeValueAsBytes(
                new ContainerWithField<Animal>(new Dog("Fluffy", 3))), Map.class);
        Map<?, ?> animal = assertInstanceOf(Map.class, encoded.get("animal"));
        assertEquals("doggy", animal.get("object-type"));
        assertEquals("Fluffy", animal.get("name"));
        assertEquals(3, animal.get("boneCount"));
    }

    // Provenance: TestWithGenerics#testWrapperWithExplicitType().
    void testWrapperWithExplicitTypeVpack() throws Exception {
        ContainerWithGetter<Animal> input = new ContainerWithGetter<>(new Dog("Fluffy", 3));
        JavaType type = MAPPER.getTypeFactory().constructParametricType(
                ContainerWithGetter.class, Animal.class);
        Map<?, ?> encoded = MAPPER.readValue(MAPPER.writerFor(type).writeValueAsBytes(input), Map.class);
        Map<?, ?> animal = assertInstanceOf(Map.class, encoded.get("animal"));
        assertEquals("doggy", animal.get("object-type"));
    }

    // Provenance: TestWithGenerics#testJackson387().
    void testJackson387Vpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .activateDefaultTyping(BasicPolymorphicTypeValidator.builder()
                                .allowIfSubType("").build(),
                        DefaultTyping.JAVA_LANG_OBJECT, As.PROPERTY)
                .changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(JsonInclude.Include.NON_NULL))
                .build();

        MyClass input = new MyClass();
        input.params.add(new MyParam<>(1));
        input.params.add(new MyParam<>("valueX"));
        SomeObject some = new SomeObject();
        some.someValue = "xxxxxx";
        input.params.add(new MyParam<>(some));
        List<SomeObject> values = new ArrayList<>();
        values.add(new SomeObject());
        values.add(new SomeObject());
        values.add(new SomeObject());
        input.params.add(new MyParam<>(values));

        byte[] encoded = mapper.writeValueAsBytes(input);
        MyClass output = mapper.readValue(encoded, MyClass.class);
        assertNotNull(output);
        assertNotNull(output.params);
        assertEquals(4, output.params.size());
    }

    // Provenance: TestWithGenerics#testValueWithMoreGenericParameters().
    void testValueWithMoreGenericParametersVpack() throws Exception {
        WrappedContainerWithField input = new WrappedContainerWithField();
        input.animalContainer = new ContainerWithTwoAnimals<>(
                new Dog("d1", 1), new Dog("d2", 2));
        Map<?, ?> encoded = MAPPER.readValue(MAPPER.writeValueAsBytes(input), Map.class);
        assertNotNull(encoded.get("animalContainer"));
    }

    // Provenance: TestWithGenerics#testIssue1128().
    void testIssue1128Vpack() throws Exception {
        DevMContainer output = MAPPER.readValue(ISSUE_1128, DevMContainer.class);
        assertEquals(2L, output.entity.parent.id);
    }

    // Provenance: TestWithGenerics#testGeneric2331().
    void testGeneric2331Vpack() throws Exception {
        Node root = new Node();
        root.children.add(new Node());
        Map<?, ?> encoded = MAPPER.readValue(MAPPER.writeValueAsBytes(root), Map.class);
        assertNotNull(encoded.get("children"));
        assertEquals(1, ((List<?>) encoded.get("children")).size());
    }

    // Provenance: TestWithGenerics#testSimpleTypeCheck1735().
    void testSimpleTypeCheck1735Vpack() {
        InvalidTypeIdException error = assertThrows(InvalidTypeIdException.class,
                () -> MAPPER.readValue(SIMPLE_TYPE_ID_1735, Wrapper1735.class));
        assertTrue(error.getMessage().contains("Could not resolve type id"), error::getMessage);
        assertTrue(error.getMessage().contains("Not a subtype"), error::getMessage);
    }

    // Provenance: TestWithGenerics#testNestedTypeCheck1735().
    void testNestedTypeCheck1735Vpack() {
        InvalidTypeIdException error = assertThrows(InvalidTypeIdException.class,
                () -> MAPPER.readValue(NESTED_TYPE_ID_1735, Wrapper1735.class));
        assertTrue(error.getMessage().contains("Could not resolve type id"), error::getMessage);
        assertTrue(error.getMessage().contains("Not a subtype"), error::getMessage);
    }

    // Provenance: TestWithGenerics#testSubTypesFor356().
    void testSubTypesFor356Vpack() throws Exception {
        JSONResponse<List<Parent356>> input = new JSONResponse<>();
        input.setResult(List.of(new Child356_1(), new Child356_2()));
        ObjectMapper mapper = VPackMapper.builder()
                .configure(MapperFeature.USE_STATIC_TYPING, true).build();
        JavaType rootType = mapper.getTypeFactory().constructType(
                new TypeReference<JSONResponse<List<Parent356>>>() { });
        byte[] encoded = mapper.writerFor(rootType).writeValueAsBytes(input);
        JSONResponse<List<Parent356>> output = mapper.readValue(encoded, rootType);
        List<Parent356> values = output.getResult();
        assertEquals(2, values.size());
        assertInstanceOf(Child356_1.class, values.get(0));
        assertInstanceOf(Child356_2.class, values.get(1));
        assertFalse(values.get(0) instanceof Child356_2);
        assertFalse(values.get(1) instanceof Child356_1);
        assertEquals("CHILD1", ((Child356_1) values.get(0)).childContent1);
        assertEquals("CHILD2", ((Child356_2) values.get(1)).childContent2);
    }
private static void assertNatural(VPackFactory factory, byte[] input,
            Object before, Object expected) throws Exception {
        try (JsonParser parser = factory.createParser(ObjectReadContext.empty(), input)) {
            assertEquals(before, TypeDeserializer.deserializeIfNatural(parser, null, Object.class));
            parser.nextToken();
            assertEquals(expected, TypeDeserializer.deserializeIfNatural(parser, null, Object.class));
        }
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

    void __invoke_testWrapperWithGetterVpack() throws Exception {
        try {
            testWrapperWithGetterVpack();
        } finally {
        }
    }


    void __invoke_testWrapperWithFieldVpack() throws Exception {
        try {
            testWrapperWithFieldVpack();
        } finally {
        }
    }


    void __invoke_testWrapperWithExplicitTypeVpack() throws Exception {
        try {
            testWrapperWithExplicitTypeVpack();
        } finally {
        }
    }


    void __invoke_testJackson387Vpack() throws Exception {
        try {
            testJackson387Vpack();
        } finally {
        }
    }


    void __invoke_testValueWithMoreGenericParametersVpack() throws Exception {
        try {
            testValueWithMoreGenericParametersVpack();
        } finally {
        }
    }


    void __invoke_testIssue1128Vpack() throws Exception {
        try {
            testIssue1128Vpack();
        } finally {
        }
    }


    void __invoke_testGeneric2331Vpack() throws Exception {
        try {
            testGeneric2331Vpack();
        } finally {
        }
    }


    void __invoke_testSimpleTypeCheck1735Vpack() throws Exception {
        try {
            testSimpleTypeCheck1735Vpack();
        } finally {
        }
    }


    void __invoke_testNestedTypeCheck1735Vpack() throws Exception {
        try {
            testNestedTypeCheck1735Vpack();
        } finally {
        }
    }


    void __invoke_testSubTypesFor356Vpack() throws Exception {
        try {
            testSubTypesFor356Vpack();
        } finally {
        }
    }

}
