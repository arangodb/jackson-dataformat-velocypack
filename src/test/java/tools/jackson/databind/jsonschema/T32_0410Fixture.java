package tools.jackson.databind.jsonschema;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import tools.jackson.databind.jsonFormatVisitors.JsonStringFormatVisitor;
import tools.jackson.databind.jsonFormatVisitors.JsonValueFormat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0410Fixture {
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] CONTAINER_FIXTURE = VPackWireFixtureTest.hex(
            "0b 19 01 42 74 73 06 12 01 0b 0e 01 43 6d 73 67 "
            + "45 68 65 6c 6c 6f 03 03 03");

    // Provenance: SchemaWithUUIDTest#testUUIDSchema().
    void testUUIDSchemaVpack() throws Exception {
        final AtomicReference<JsonValueFormat> format = new AtomicReference<>();
        new VPackMapper().acceptJsonFormatVisitor(UUID.class,
                new JsonFormatVisitorWrapper.Base() {
                    @Override
                    public JsonStringFormatVisitor expectStringFormat(
                            tools.jackson.databind.JavaType type) {
                        return new JsonStringFormatVisitor() {
                            @Override
                            public void enumTypes(Set<String> enums) { }

                            @Override
                            public void format(JsonValueFormat valueFormat) {
                                format.set(valueFormat);
                            }
                        };
                    }
                });
        assertEquals(JsonValueFormat.UUID, format.get());
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

    void __invoke_testUUIDSchemaVpack() throws Exception {
        try {
            testUUIDSchemaVpack();
        } finally {
        }
    }

}
