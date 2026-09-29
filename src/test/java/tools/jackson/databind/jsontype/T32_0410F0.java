package tools.jackson.databind.jsontype;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.module.SimpleModule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0410F0 {
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] CONTAINER_FIXTURE = VPackWireFixtureTest.hex(
            "0b 19 01 42 74 73 06 12 01 0b 0e 01 43 6d 73 67 "
            + "45 68 65 6c 6c 6f 03 03 03");

    // Provenance: AbstractTypeNamesTest#testDeserializeMyContainer().
    void testDeserializeMyContainerVpack() throws Exception {
        SimpleModule module = new SimpleModule()
                .addAbstractTypeMapping(IContainer.class, MyContainer.class);
        VPackMapper mapper = VPackMapper.builder().addModule(module).build();
        Object value = mapper.readValue(CONTAINER_FIXTURE,
                mapper.getTypeFactory().constructParametricType(
                        IContainer.class, MyObject.class));

        assertEquals(MyContainer.class, value.getClass());
        MyContainer<?> container = (MyContainer<?>) value;
        assertEquals(1, container.ts.size());
        assertEquals(MyObject.class, container.ts.get(0).getClass());
        assertEquals("hello", ((MyObject) container.ts.get(0)).msg);
    }

    // Provenance: AbstractTypeNamesTest#testEmptyCollection().
    void testEmptyCollectionVpack() throws Exception {
        List<User> friends = new ArrayList<>();
        friends.add(new DefaultUser("Joe Hildebrandt", null));
        friends.add(new DefaultEmployee("Richard Nasr", null, "MDA"));
        User input = new DefaultEmployee("John Vanspronssen", friends, "MDA");

        VPackMapper mapper = VPackMapper.builder()
                .registerSubtypes(DefaultEmployee.class, DefaultUser.class)
                .build();
        User result = mapper.readValue(mapper.writeValueAsBytes(input), User.class);

        assertNotNull(result);
        assertEquals(DefaultEmployee.class, result.getClass());
        assertEquals(2, result.getFriends().size());
        assertEquals(DefaultUser.class, result.getFriends().get(0).getClass());
        assertEquals(DefaultEmployee.class, result.getFriends().get(1).getClass());
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

    void __invoke_testDeserializeMyContainerVpack() throws Exception {
        try {
            testDeserializeMyContainerVpack();
        } finally {
        }
    }


    void __invoke_testEmptyCollectionVpack() throws Exception {
        try {
            testEmptyCollectionVpack();
        } finally {
        }
    }

}
