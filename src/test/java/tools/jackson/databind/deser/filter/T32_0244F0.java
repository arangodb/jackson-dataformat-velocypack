package tools.jackson.databind.deser.filter;

import java.beans.ConstructorProperties;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0244F0 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final ObjectMapper GETTER_AS_SETTER_MAPPER = VPackMapper.builder()
            .enable(MapperFeature.USE_GETTERS_AS_SETTERS)
            .build();
private static final ObjectMapper INVERSE_ACCESS_MAPPER = VPackMapper.builder()
            .enable(MapperFeature.INVERSE_READ_WRITE_ACCESS)
            .build();
private static final byte[] SECURITY_GROUP = VPackWireFixtureTest.hex(
            "14 25 54 73 65 63 75 72 69 74 79 5f 67 72 6f 75 70 5f 72 75 6c 65 73 "
          + "13 0d 14 0a 42 69 64 43 69 64 31 01 01 01");
private static final byte[] LIST_INPUT = VPackWireFixtureTest.hex(
            "14 0f 44 6c 69 73 74 13 07 31 32 33 34 04 01");
private static final byte[] RENAMED_LIST_INPUT = VPackWireFixtureTest.hex(
            "14 16 4b 72 65 6e 61 6d 65 64 4c 69 73 74 13 07 31 32 33 34 04 01");
private static final byte[] READ_ONLY_LIST = VPackWireFixtureTest.hex(
            "14 0f 44 6c 69 73 74 13 07 31 32 33 34 04 01");
private static final byte[] READ_WRITE_INPUT = VPackWireFixtureTest.hex(
            "14 09 41 78 35 41 79 36 02");
private static final byte[] NAME_TEST = VPackWireFixtureTest.hex(
            "14 0d 44 6e 61 6d 65 44 74 65 73 74 01");
private static final byte[] POJO935 = VPackWireFixtureTest.hex(
            "14 2f 48 66 75 6c 6c 4e 61 6d 65 47 46 6f 6f 20 42 61 72 "
          + "49 66 69 72 73 74 4e 61 6d 65 43 46 6f 6f "
          + "48 6c 61 73 74 4e 61 6d 65 43 42 61 72 03");
private static final byte[] ROLES = VPackWireFixtureTest.hex(
            "14 23 44 6e 61 6d 65 43 66 6f 6f 45 72 6f 6c 65 73 "
          + "13 11 45 61 64 6d 69 6e 47 6d 6f 6e 69 74 6f 72 02 02");
private static final byte[] RENAMED_IGNORABLE = VPackWireFixtureTest.hex(
            "14 22 45 77 6f 72 6b 73 45 77 6f 72 6b 73 41 74 "
          + "50 70 6c 65 61 73 65 46 69 78 54 68 69 73 42 75 67 02");
private static final byte[] INVERSE_INPUT = VPackWireFixtureTest.hex(
            "14 09 41 61 35 41 62 36 02");
private static final byte[] READ_ONLY_OUTPUT = VPackWireFixtureTest.hex(
            "0b 07 01 41 78 31 03");
private static final byte[] INVERSE_OUTPUT = VPackWireFixtureTest.hex(
            "0b 07 01 41 62 32 03");
private static final byte[] EMPTY_LIST_OUTPUT = VPackWireFixtureTest.hex(
            "0b 0a 01 44 6c 69 73 74 01 03");
private static final byte[] RENAMED_LIST_OUTPUT = VPackWireFixtureTest.hex(
            "0b 11 01 4b 72 65 6e 61 6d 65 64 4c 69 73 74 01 03");

    // Provenance: ReadOnlyListDeserTest#testAccessReadOnly2118.
    void testAccessReadOnly2118() throws Exception {
        SecurityGroup value = GETTER_AS_SETTER_MAPPER.readValue(SECURITY_GROUP,
                SecurityGroup.class);
        assertEquals(Collections.emptyList(), value.securityGroupRules);
    }

    // Provenance: ReadOnlyListDeserTest#testRenamedToSameOnGetter2283.
    void testRenamedToSameOnGetter2283() throws Exception {
        assertArrayEquals(EMPTY_LIST_OUTPUT,
                GETTER_AS_SETTER_MAPPER.writeValueAsBytes(new RenamedToSameOnGetter()));
        RenamedToSameOnGetter value = GETTER_AS_SETTER_MAPPER.readValue(LIST_INPUT,
                RenamedToSameOnGetter.class);
        assertTrue(value.getList().isEmpty(), "List should be empty");
    }

    // Provenance: ReadOnlyListDeserTest#testRenamedToDifferentOnGetter2283.
    void testRenamedToDifferentOnGetter2283() throws Exception {
        assertArrayEquals(RENAMED_LIST_OUTPUT,
                GETTER_AS_SETTER_MAPPER.writeValueAsBytes(new RenamedToDifferentOnGetter()));
        RenamedToDifferentOnGetter value = GETTER_AS_SETTER_MAPPER.readValue(RENAMED_LIST_INPUT,
                RenamedToDifferentOnGetter.class);
        assertTrue(value.getList().isEmpty(), "List should be empty");
    }

    // Provenance: ReadOnlyListDeserTest#testRenamedOnClass2283.
    void testRenamedOnClass2283() throws Exception {
        assertArrayEquals(RENAMED_LIST_OUTPUT,
                GETTER_AS_SETTER_MAPPER.writeValueAsBytes(new RenamedOnClass()));
        RenamedOnClass value = GETTER_AS_SETTER_MAPPER.readValue(RENAMED_LIST_INPUT,
                RenamedOnClass.class);
        assertTrue(value.getList().isEmpty(), "List should be empty");
    }
static class SecurityGroup {
        List<SecurityGroupRule> securityGroupRules = new java.util.ArrayList<>();

        @JsonProperty(value = "security_group_rules", access = JsonProperty.Access.READ_ONLY)
        public List<SecurityGroupRule> getSecurityGroupRules() {
            return securityGroupRules;
        }

        public SecurityGroup setSecurityGroupRules(List<SecurityGroupRule> value) {
            throw new AssertionError("Should not be called");
        }
    }
static class SecurityGroupRule {
        private String id;

        @JsonProperty
        public String getId() { return id; }
        public void setId(String value) { id = value; }
    }
static class RenamedToSameOnGetter {
        @JsonProperty(value = "list", access = JsonProperty.Access.READ_ONLY)
        List<Long> getList() { return Collections.emptyList(); }
    }
static class RenamedToDifferentOnGetter {
        @JsonProperty(value = "renamedList", access = JsonProperty.Access.READ_ONLY)
        List<Long> getList() { return Collections.emptyList(); }
    }
@JsonIgnoreProperties(value = { "renamedList" }, allowGetters = true)
    static class RenamedOnClass {
        @JsonProperty("renamedList")
        List<Long> getList() { return Collections.emptyList(); }
    }
static class ReadXWriteY {
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        public int x = 1;

        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        public int y = 2;

        public void setX(int value) { throw new AssertionError("Should not set x"); }
        public int getY() { throw new AssertionError("Should not get y"); }
    }
static class Pojo935 {
        private String firstName = "Foo";
        private String lastName = "Bar";

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        public String getFullName() { return firstName + " " + lastName; }
        public String getFirstName() { return firstName; }
        public void setFirstName(String value) { firstName = value; }
        public String getLastName() { return lastName; }
        public void setLastName(String value) { lastName = value; }
    }
static class Foo1345 {
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        public String id;
        public String name;

        @ConstructorProperties({ "id", "name" })
        public Foo1345(String id, String name) {
            this.id = id;
            this.name = name;
        }

        protected Foo1345() { }
    }
static class Foo1382 {
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private List<Long> list = new java.util.ArrayList<>();

        List<Long> getList() { return list; }
        public Foo1382 setList(List<Long> value) { list = value; return this; }
    }
static class UserWithReadOnly1805 {
        public String name;

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        public List<String> getRoles() { return List.of("admin", "monitor"); }
    }
@JsonIgnoreProperties(value = { "roles" }, allowGetters = true)
    static class UserAllowGetters1805 {
        public String name;
        public List<String> getRoles() { return List.of("admin", "monitor"); }
    }
static class Bean2779 {
        String works;

        @JsonProperty(value = "t", access = JsonProperty.Access.READ_ONLY)
        public String getDoesntWork() { return "pleaseFixThisBug"; }
        public String getWorks() { return works; }
        public void setWorks(String value) { works = value; }
    }
static class ReadAWriteB {
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        public int a = 1;

        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        public int b = 2;
    }

    void __invoke_testAccessReadOnly2118() throws Exception {
        try {
            testAccessReadOnly2118();
        } finally {
        }
    }


    void __invoke_testRenamedToSameOnGetter2283() throws Exception {
        try {
            testRenamedToSameOnGetter2283();
        } finally {
        }
    }


    void __invoke_testRenamedToDifferentOnGetter2283() throws Exception {
        try {
            testRenamedToDifferentOnGetter2283();
        } finally {
        }
    }


    void __invoke_testRenamedOnClass2283() throws Exception {
        try {
            testRenamedOnClass2283();
        } finally {
        }
    }

}
