package tools.jackson.databind.deser.creators;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;
import com.arangodb.jackson.dataformat.velocypack.VPackMapper;

import java.beans.ConstructorProperties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

// for [databind#1367]
public class CreatorWithObjectIdTest
{
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
//            resolver = SimpleObjectIdResolver.class)
    public static class A {
        String id;
        String name;

        public A() { }

        @ConstructorProperties({"id", "name"})
        public A(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
    }

    // from TestCreatorsWithIdentity
    @JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id", scope=Parent.class)
    public static class Parent {
        @JsonProperty("id")
        String id;

        @JsonProperty
        String parentProp;

        @JsonCreator
        public Parent(@JsonProperty("parentProp") String parentProp) {
            this.parentProp = parentProp;
        }
    }

    public static class Child {
        @JsonProperty
        Parent parent;

        @JsonProperty
        String childProp;

        @JsonCreator
        public Child(@JsonProperty("parent") Parent parent, @JsonProperty("childProp") String childProp) {
            this.parent = parent;
            this.childProp = childProp;
        }
    }

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    // [databind#1367]
    @Test
    public void testObjectIdWithCreator() throws Exception
    {
        A a = new A("123", "A");

        ObjectMapper om = new VPackMapper();
        String json = VPackUtils.toJson(om.writeValueAsBytes(a));
        A deser = om.readValue(VPackUtils.toVPack(json), A.class);
        assertEquals(a.name, deser.name);
    }

    @Test
    public void testCreatorWithIdentityInfo() throws Exception
    {
        ObjectMapper mapper = new VPackMapper();
        String parentStr = "{\"id\" : \"1\", \"parentProp\" : \"parent\"}";
        String childStr = "{\"childProp\" : \"child\", \"parent\" : " + parentStr + "}";
        Parent parent = mapper.readValue(VPackUtils.toVPack(parentStr), Parent.class);
        assertNotNull(parent);
        Child child = mapper.readValue(VPackUtils.toVPack(childStr), Child.class);
        assertNotNull(child);
        assertNotNull(child.parent);
    }
}
