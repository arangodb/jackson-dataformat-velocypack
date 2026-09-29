package tools.jackson.databind.ser.filter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0582F0 {
private static final ObjectMapper MAPPER = VPackMapper.builder()
            .enable(SerializationFeature.APPLY_JSON_INCLUDE_FOR_CONTAINERS)
            .build();

    void testNonEmptyValueMapViaPropVpack() throws Exception {
        NoEmptiesMapContainer bean = new NoEmptiesMapContainer()
                .add("a", null).add("b", "");
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                MAPPER.writeValueAsBytes(bean));
    }

    void testNoNullsMapVpack() throws Exception {
        NoNullsMapContainer bean = new NoNullsMapContainer()
                .add("a", null).add("b", "");
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 11 01 45 73 74 75 66 66 0b 07 01 41 62 40 03 03"),
                MAPPER.writeValueAsBytes(bean));
    }

    void testNonEmptyNoNullsMapVpack() throws Exception {
        NoNullsNotEmptyMapContainer bean = new NoNullsNotEmptyMapContainer()
                .add("a", null).add("b", "");
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 11 01 45 73 74 75 66 66 0b 07 01 41 62 40 03 03"),
                MAPPER.writeValueAsBytes(bean));

        bean = new NoNullsNotEmptyMapContainer().add("a", null).add("b", null);
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                MAPPER.writeValueAsBytes(bean));
    }

    void testMapViaJsonValueVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                MAPPER.writeValueAsBytes(new TopLevel2909()));
    }

    void test2572MapOverrideUseDefaultsVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(SerializationFeature.APPLY_JSON_INCLUDE_FOR_CONTAINERS)
                .changeDefaultPropertyInclusion(incl -> JsonInclude.Value.construct(
                        JsonInclude.Include.NON_NULL, JsonInclude.Include.NON_NULL))
                .withConfigOverride(Map.class, o -> o.setInclude(JsonInclude.Value.construct(
                        JsonInclude.Include.USE_DEFAULTS, JsonInclude.Include.USE_DEFAULTS)))
                .build();
        Map<String, Integer> properties = new LinkedHashMap<>();
        properties.put("Speed", 100);
        properties.put("Weight", null);
        Car car = new Car();
        car.model = "F60";
        car.properties = properties;

        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0c 01 45 53 70 65 65 64 28 64 03"),
                mapper.writeValueAsBytes(properties));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 26 02 45 6d 6f 64 65 6c 43 46 36 30 4a 70 72 6f 70 65 72 74 69 65 73 "
              + "0b 0c 01 45 53 70 65 65 64 28 64 03 03 0d"),
                mapper.writeValueAsBytes(car));
    }

    void testNonEmptyViaClassVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 10 01 43 6d 61 70 0b 08 01 41 61 41 62 03 03"),
                MAPPER.writeValueAsBytes(new Bean1649("a", "b")));
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                MAPPER.writeValueAsBytes(new Bean1649("a", null)));
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                MAPPER.writeValueAsBytes(new Bean1649("a", "")));
    }
@JsonInclude(value = JsonInclude.Include.NON_EMPTY,
            content = JsonInclude.Include.NON_EMPTY)
    static class NoEmptiesMapContainer {
        public Map<String, String> stuff = new LinkedHashMap<>();

        NoEmptiesMapContainer add(String key, String value) {
            stuff.put(key, value);
            return this;
        }
    }
@JsonInclude(value = JsonInclude.Include.NON_NULL,
            content = JsonInclude.Include.NON_NULL)
    static class NoNullsMapContainer {
        public Map<String, String> stuff = new LinkedHashMap<>();

        NoNullsMapContainer add(String key, String value) {
            stuff.put(key, value);
            return this;
        }
    }
@JsonInclude(value = JsonInclude.Include.NON_EMPTY,
            content = JsonInclude.Include.NON_NULL)
    static class NoNullsNotEmptyMapContainer {
        public Map<String, String> stuff = new LinkedHashMap<>();

        NoNullsNotEmptyMapContainer add(String key, String value) {
            stuff.put(key, value);
            return this;
        }
    }
static class Wrapper2909 {
        @JsonValue
        public Map<String, String> values = new LinkedHashMap<>();
    }
static class TopLevel2909 {
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public Wrapper2909 nested = new Wrapper2909();
    }
@JsonPropertyOrder({ "model", "properties" })
    static class Car {
        public String model;
        public Map<String, Integer> properties;
    }
@JsonInclude(value = JsonInclude.Include.NON_EMPTY,
            content = JsonInclude.Include.NON_EMPTY)
    static class Bean1649 {
        public Map<String, String> map;

        Bean1649(String key, String value) {
            map = new LinkedHashMap<>();
            map.put(key, value);
        }
    }
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id", scope = Parent.class)
    static final class Parent {
        public Long id = 1L;

        @JsonUnwrapped
        public Child child;
    }
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id", scope = Child.class)
    static final class Child {
        public Long id = 2L;
        public final String name;

        Child(String name) {
            this.name = name;
        }
    }
static final class ListOfParents {
        public List<Parent> parents = new ArrayList<>();

        void addParent(Parent parent) {
            parents.add(parent);
        }
    }
static class SizeClassEnabledIgnore {
        @JsonIgnore public int getY() { return 9; }
        public int getX() { return 1; }
        @JsonIgnore public int getY2() { return 1; }
        @JsonIgnore public int getY3() { return 2; }
    }
static class SizeClassDisabledIgnore {
        public int getX() { return 3; }
        @JsonIgnore(false) public int getY() { return 4; }
    }
static class BaseClassIgnore {
        @JsonProperty("x")
        @JsonIgnore
        public int x() { return 1; }
        public int getY() { return 2; }
    }
static class SubClassNonIgnore extends BaseClassIgnore {
        @Override
        @JsonIgnore(false)
        public int x() { return 3; }
    }
@JsonIgnoreType
    static class IgnoredType { }
@JsonIgnoreType(false)
    static class NonIgnoredType {
        public int value = 13;
        public IgnoredType ignored = new IgnoredType();
    }
static class IgnoreAndProperty3357 {
        public int toInclude = 2;
        @JsonIgnore
        @JsonProperty
        int toIgnore = 3;
        public int getToIgnore() { return toIgnore; }
    }

    void __invoke_testNonEmptyValueMapViaPropVpack() throws Exception {
        try {
            testNonEmptyValueMapViaPropVpack();
        } finally {
        }
    }


    void __invoke_testNoNullsMapVpack() throws Exception {
        try {
            testNoNullsMapVpack();
        } finally {
        }
    }


    void __invoke_testNonEmptyNoNullsMapVpack() throws Exception {
        try {
            testNonEmptyNoNullsMapVpack();
        } finally {
        }
    }


    void __invoke_testMapViaJsonValueVpack() throws Exception {
        try {
            testMapViaJsonValueVpack();
        } finally {
        }
    }


    void __invoke_test2572MapOverrideUseDefaultsVpack() throws Exception {
        try {
            test2572MapOverrideUseDefaultsVpack();
        } finally {
        }
    }


    void __invoke_testNonEmptyViaClassVpack() throws Exception {
        try {
            testNonEmptyViaClassVpack();
        } finally {
        }
    }

}
