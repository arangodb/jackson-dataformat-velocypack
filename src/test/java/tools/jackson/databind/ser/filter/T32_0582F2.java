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

class T32_0582F2 {
private static final ObjectMapper MAPPER = VPackMapper.builder()
            .enable(SerializationFeature.APPLY_JSON_INCLUDE_FOR_CONTAINERS)
            .build();

    void testSimpleIgnoreVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 07 01 41 78 31 03"),
                MAPPER.writeValueAsBytes(new SizeClassEnabledIgnore()));
    }

    void testDisabledIgnoreVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0b 02 41 78 33 41 79 34 03 06"),
                MAPPER.writeValueAsBytes(new SizeClassDisabledIgnore()));
    }

    void testIgnoreOverVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 07 01 41 79 32 03"),
                MAPPER.writeValueAsBytes(new BaseClassIgnore()));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0b 02 41 78 33 41 79 32 03 06"),
                MAPPER.writeValueAsBytes(new SubClassNonIgnore()));
    }

    void testIgnoreTypeVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0c 01 45 76 61 6c 75 65 28 0d 03"),
                MAPPER.writeValueAsBytes(new NonIgnoredType()));
    }

    void testPropertyVsIgnore3357Vpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0f 01 49 74 6f 49 6e 63 6c 75 64 65 32 03"),
                MAPPER.writeValueAsBytes(new IgnoreAndProperty3357()));
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

    void __invoke_testSimpleIgnoreVpack() throws Exception {
        try {
            testSimpleIgnoreVpack();
        } finally {
        }
    }


    void __invoke_testDisabledIgnoreVpack() throws Exception {
        try {
            testDisabledIgnoreVpack();
        } finally {
        }
    }


    void __invoke_testIgnoreOverVpack() throws Exception {
        try {
            testIgnoreOverVpack();
        } finally {
        }
    }


    void __invoke_testIgnoreTypeVpack() throws Exception {
        try {
            testIgnoreTypeVpack();
        } finally {
        }
    }


    void __invoke_testPropertyVsIgnore3357Vpack() throws Exception {
        try {
            testPropertyVsIgnore3357Vpack();
        } finally {
        }
    }

}
