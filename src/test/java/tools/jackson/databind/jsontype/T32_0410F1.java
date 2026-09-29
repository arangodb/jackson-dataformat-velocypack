package tools.jackson.databind.jsontype;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.exc.InvalidTypeIdException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0410F1 {
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] CONTAINER_FIXTURE = VPackWireFixtureTest.hex(
            "0b 19 01 42 74 73 06 12 01 0b 0e 01 43 6d 73 67 "
            + "45 68 65 6c 6c 6f 03 03 03");

    // Provenance: BaseTypeAsDefaultTest#testConversionForAbstractWithDefault().
    void testConversionForAbstractWithDefaultVpack() throws Exception {
        Object value = mapperWithBase().readerFor(AbstractParentWithDefault.class)
                .readValue(EMPTY_OBJECT);
        assertEquals(ChildOfChild.class, value.getClass());
    }

    // Provenance: BaseTypeAsDefaultTest#testNegativeForChild().
    void testNegativeForChildVpack() throws Exception {
        InvalidTypeIdException error = assertThrows(InvalidTypeIdException.class,
                () -> mapperWithoutBase().readerFor(Child.class)
                        .readValue(EMPTY_OBJECT));
        assertNotNull(error.getMessage());
    }

    // Provenance: BaseTypeAsDefaultTest#testNegativeForChildWithoutRequiringTypeId().
    void testNegativeForChildWithoutRequiringTypeIdVpack() throws Exception {
        Child child = mapperWithoutBaseOrSubtypeId().readerFor(Child.class)
                .readValue(EMPTY_OBJECT);
        assertEquals(Child.class, child.getClass());
    }

    // Provenance: BaseTypeAsDefaultTest#testNegativeForParent().
    void testNegativeForParentVpack() throws Exception {
        InvalidTypeIdException error = assertThrows(InvalidTypeIdException.class,
                () -> mapperWithoutBase().readerFor(Parent.class)
                        .readValue(EMPTY_OBJECT));
        assertNotNull(error.getMessage());
    }

    // Provenance: BaseTypeAsDefaultTest#testPositiveForChild().
    void testPositiveForChildVpack() throws Exception {
        Object value = mapperWithBase().readerFor(Child.class).readValue(EMPTY_OBJECT);
        assertEquals(Child.class, value.getClass());
    }

    // Provenance: BaseTypeAsDefaultTest#testPositiveForParent().
    void testPositiveForParentVpack() throws Exception {
        Object value = mapperWithBase().readerFor(Parent.class).readValue(EMPTY_OBJECT);
        assertEquals(Parent.class, value.getClass());
    }

    // Provenance: BaseTypeAsDefaultTest#testPositiveWithManualDefault().
    void testPositiveWithManualDefaultVpack() throws Exception {
        Object value = mapperWithBase().readerFor(ChildOfAbstract.class)
                .readValue(EMPTY_OBJECT);
        assertEquals(ChildOfChild.class, value.getClass());
    }

    // Provenance: BaseTypeAsDefaultTest#testPositiveWithTypeSpecification().
    void testPositiveWithTypeSpecificationVpack() throws Exception {
        VPackMapper mapper = mapperWithBase();
        java.util.Map<String, String> typeObject = java.util.Map.of(
                "@class", Child.class.getName());
        Object value = mapper.readerFor(Parent.class).readValue(
                mapper.writeValueAsBytes(typeObject));
        assertEquals(Child.class, value.getClass());
    }
private static VPackMapper mapperWithBase() {
        return VPackMapper.builder()
                .enable(MapperFeature.USE_BASE_TYPE_AS_DEFAULT_IMPL)
                .build();
    }
private static VPackMapper mapperWithoutBase() {
        return VPackMapper.builder()
                .disable(MapperFeature.USE_BASE_TYPE_AS_DEFAULT_IMPL)
                .build();
    }
private static VPackMapper mapperWithoutBaseOrSubtypeId() {
        return VPackMapper.builder()
                .disable(MapperFeature.USE_BASE_TYPE_AS_DEFAULT_IMPL)
                .disable(MapperFeature.REQUIRE_TYPE_ID_FOR_SUBTYPES)
                .build();
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY,
            property = "userType")
    @JsonTypeName("User")
    @JsonSubTypes(@JsonSubTypes.Type(value = Employee.class, name = "Employee"))
    interface User {
        String getName();
        List<User> getFriends();
    }
@JsonTypeName("Employee")
    interface Employee extends User {
        String getEmployer();
    }
@JsonTypeName("Employee")
    static class DefaultEmployee extends DefaultUser implements Employee {
        private String employer;

        @JsonCreator
        DefaultEmployee(@JsonProperty("name") String name,
                @JsonProperty("friends") List<User> friends,
                @JsonProperty("employer") String employer) {
            super(name, friends);
            this.employer = employer;
        }

        @Override
        public String getEmployer() { return employer; }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY,
            property = "userType")
    @JsonTypeName("User")
    @JsonSubTypes(@JsonSubTypes.Type(value = DefaultEmployee.class, name = "Employee"))
    static class DefaultUser implements User {
        private String name;
        private List<User> friends;

        @JsonCreator
        DefaultUser(@JsonProperty("name") String name,
                @JsonProperty("friends") List<User> friends) {
            this.name = name;
            this.friends = friends;
        }

        @Override
        public String getName() { return name; }

        @Override
        public List<User> getFriends() { return friends; }
    }
interface IContainer<T> {
        @JsonProperty("ts")
        List<T> getTs();
    }
static class MyContainer<T> implements IContainer<T> {
        final List<T> ts;

        @JsonCreator
        MyContainer(@JsonProperty("ts") List<T> ts) { this.ts = ts; }

        @Override
        public List<T> getTs() { return ts; }
    }
public static class MyObject {
        public String msg;
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
    static class Parent { }
static class Child extends Parent { }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class",
            defaultImpl = ChildOfChild.class)
    static abstract class AbstractParentWithDefault { }
static class ChildOfAbstract extends AbstractParentWithDefault { }
static class ChildOfChild extends ChildOfAbstract { }

    void __invoke_testConversionForAbstractWithDefaultVpack() throws Exception {
        try {
            testConversionForAbstractWithDefaultVpack();
        } finally {
        }
    }


    void __invoke_testNegativeForChildVpack() throws Exception {
        try {
            testNegativeForChildVpack();
        } finally {
        }
    }


    void __invoke_testNegativeForChildWithoutRequiringTypeIdVpack() throws Exception {
        try {
            testNegativeForChildWithoutRequiringTypeIdVpack();
        } finally {
        }
    }


    void __invoke_testNegativeForParentVpack() throws Exception {
        try {
            testNegativeForParentVpack();
        } finally {
        }
    }


    void __invoke_testPositiveForChildVpack() throws Exception {
        try {
            testPositiveForChildVpack();
        } finally {
        }
    }


    void __invoke_testPositiveForParentVpack() throws Exception {
        try {
            testPositiveForParentVpack();
        } finally {
        }
    }


    void __invoke_testPositiveWithManualDefaultVpack() throws Exception {
        try {
            testPositiveWithManualDefaultVpack();
        } finally {
        }
    }


    void __invoke_testPositiveWithTypeSpecificationVpack() throws Exception {
        try {
            testPositiveWithTypeSpecificationVpack();
        } finally {
        }
    }

}
