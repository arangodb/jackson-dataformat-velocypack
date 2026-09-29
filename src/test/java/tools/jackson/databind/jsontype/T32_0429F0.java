package tools.jackson.databind.jsontype;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import tools.jackson.dataformat.velocypack.*;

class T32_0429F0 {
private static final ObjectMapper MAPPER = new VPackMapper();

    // Provenance: TestSubtypesSubPackage#testSubPackage().
    void testSubPackageVpack() throws Exception {
        Map<?, ?> encoded = MAPPER.readValue(
                MAPPER.writeValueAsBytes(new SubPackageType429()), Map.class);
        assertEquals(".T32_0429F0$SubPackageType429", encoded.get("@c"));
        assertEquals(2, encoded.get("c"));
    }

    // Provenance: TestSubtypesSubPackage#testSubPackageRoundTrip().
    void testSubPackageRoundTripVpack() throws Exception {
        SubPackageType429 original = new SubPackageType429();
        SuperType429 result = MAPPER.readValue(MAPPER.writeValueAsBytes(original), SuperType429.class);
        SubPackageType429 decoded = assertInstanceOf(SubPackageType429.class, result);
        assertEquals(original.c, decoded.c);
    }

    // Provenance: TestSubtypesSubPackage#testInnerRoundTrip().
    void testInnerRoundTripVpack() throws Exception {
        SuperType429.InnerType429 original = new SuperType429.InnerType429();
        SuperType429 result = MAPPER.readValue(MAPPER.writeValueAsBytes(original), SuperType429.class);
        SuperType429.InnerType429 decoded = assertInstanceOf(SuperType429.InnerType429.class, result);
        assertEquals(original.b, decoded.b);
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS)
    static abstract class SuperType429 {
        static class InnerType429 extends SuperType429 {
            public int b = 2;
        }
    }
static class SubPackageType429 extends SuperType429 {
        public int c = 2;
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = A1616_429.class, name = "A"),
            @JsonSubTypes.Type(value = B1616_429.class)
    })
    static abstract class Base1616_429 { }
static class A1616_429 extends Base1616_429 { }
@JsonTypeName("B")
    static class B1616_429 extends Base1616_429 { }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
    @JsonSubTypes({
            @JsonSubTypes.Type(value = Dog429.class, name = "doggy"),
            @JsonSubTypes.Type(Cat429.class)
    })
    static abstract class Animal429 {
        public String name;
        Animal429() { }
        Animal429(String name) { this.name = name; }
    }
static class Dog429 extends Animal429 {
        public int ageInYears;
        Dog429() { }
        Dog429(String name, int age) { super(name); ageInYears = age; }
    }
@JsonSubTypes({
            @JsonSubTypes.Type(MaineCoon429.class),
            @JsonSubTypes.Type(Persian429.class)
    })
    static abstract class Cat429 extends Animal429 {
        public boolean purrs;
        Cat429() { }
        Cat429(String name, boolean purrs) { super(name); this.purrs = purrs; }
    }
static class MaineCoon429 extends Cat429 {
        MaineCoon429() { }
        MaineCoon429(String name, boolean purrs) { super(name, purrs); }
    }
@JsonTypeName("persialaisKissa")
    static class Persian429 extends Cat429 {
        Persian429() { }
        Persian429(String name, boolean purrs) { super(name, purrs); }
    }

    void __invoke_testSubPackageVpack() throws Exception {
        try {
            testSubPackageVpack();
        } finally {
        }
    }


    void __invoke_testSubPackageRoundTripVpack() throws Exception {
        try {
            testSubPackageRoundTripVpack();
        } finally {
        }
    }


    void __invoke_testInnerRoundTripVpack() throws Exception {
        try {
            testInnerRoundTripVpack();
        } finally {
        }
    }

}
