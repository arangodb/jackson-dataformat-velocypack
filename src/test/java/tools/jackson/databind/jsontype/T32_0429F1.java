package tools.jackson.databind.jsontype;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.jsontype.NamedType;
import tools.jackson.databind.jsontype.impl.StdSubtypeResolver;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0429F1 {
private static final ObjectMapper MAPPER = new VPackMapper();

    // Provenance: TestTypeNames#testBaseTypeId1616().
    void testBaseTypeId1616Vpack() {
        ObjectMapper mapper = new VPackMapper();
        Collection<NamedType> subtypes = new StdSubtypeResolver().collectAndResolveSubtypesByTypeId(
                mapper.deserializationConfig(), null, mapper.constructType(Base1616_429.class));
        assertEquals(2, subtypes.size());
        Set<String> ids = new HashSet<>();
        for (NamedType subtype : subtypes) {
            ids.add(subtype.getName());
        }
        assertEquals(Set.of("A", "B"), ids);
    }

    // Provenance: TestTypeNames#testSerialization().
    void testSerializationVpack() throws Exception {
        List<?> encoded = MAPPER.readValue(MAPPER.writeValueAsBytes(new Animal429[] {
                new Dog429("Spot", 3), new MaineCoon429("Belzebub", true)
        }), List.class);
        assertEquals(2, encoded.size());

        Map<?, ?> dog = assertInstanceOf(Map.class, encoded.get(0));
        Map<?, ?> dogValue = assertInstanceOf(Map.class, dog.get("doggy"));
        assertEquals("Spot", dogValue.get("name"));
        assertEquals(3, dogValue.get("ageInYears"));

        Map<?, ?> cat = assertInstanceOf(Map.class, encoded.get(1));
        assertNotNull(cat.get("T32_0429F1$MaineCoon429"));
        Map<?, ?> catValue = assertInstanceOf(Map.class,
                cat.get("T32_0429F1$MaineCoon429"));
        assertEquals("Belzebub", catValue.get("name"));
        assertEquals(true, catValue.get("purrs"));
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

    void __invoke_testBaseTypeId1616Vpack() throws Exception {
        try {
            testBaseTypeId1616Vpack();
        } finally {
        }
    }


    void __invoke_testSerializationVpack() throws Exception {
        try {
            testSerializationVpack();
        } finally {
        }
    }

}
