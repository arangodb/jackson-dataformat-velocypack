package tools.jackson.databind.objectid;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.DatabindContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import tools.jackson.dataformat.velocypack.*;

class T32_0508F1 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] JSOG_REFERENCE = VPackWireFixtureTest.hex(
            "14 1e 43 40 69 64 41 31 43 66 6f 6f 28 42 44 6e 65 78 74 "
          + "14 0a 44 40 72 65 66 41 31 01 03");

    // Provenance: JSOGDeserialize622Test#testStructJSOGRef().
    void testStructJSOGRefVpack() throws Exception {
        IdentifiableExampleJSOG result = MAPPER.readValue(JSOG_REFERENCE,
                IdentifiableExampleJSOG.class);
        assertEquals(66, result.foo);
        assertSame(result, result.next);
    }

    // Provenance: JSOGDeserialize622Test#testPolymorphicRoundTrip().
    void testPolymorphicRoundTripVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .polymorphicTypeValidator(new NoCheckSubTypeValidator())
                .build();

        JSOGWrapper value = new JSOGWrapper(15);
        IdentifiableExampleJSOG example = new IdentifiableExampleJSOG(123);
        example.next = example;
        value.jsog = example;

        JSOGWrapper result = mapper.readValue(mapper.writeValueAsBytes(value),
                JSOGWrapper.class);
        assertNotNull(result);
        assertEquals(15, result.value);
        assertInstanceOf(IdentifiableExampleJSOG.class, result.jsog);
        IdentifiableExampleJSOG jsog = (IdentifiableExampleJSOG) result.jsog;
        assertEquals(123, jsog.foo);
        assertSame(jsog, jsog.next);
    }

    // Provenance: JSOGDeserialize622Test#testAlterativePolymorphicRoundTrip669().
    void testAlterativePolymorphicRoundTrip669Vpack() throws Exception {
        Outer value = new Outer();
        value.foo = "foo";
        value.inner1 = value.inner2 = new SubInner("bar", "extra");

        Outer result = MAPPER.readValue(MAPPER.writeValueAsBytes(value), Outer.class);
        assertSame(result.inner1, result.inner2);
    }
@JsonPropertyOrder({ "bar1", "bar2" })
    static class Foo {
        @JsonIdentityReference(alwaysAsId = true)
        public Bar bar1;
        @JsonIdentityReference
        public Bar bar2;
    }
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
    static class Bar {
        public int value = 3;
    }
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "id")
    static class Value1607 {
        public int value;
        public Value1607() { this(0); }
        public Value1607(int value) { this.value = value; }
    }
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    static class Value1607ViaClass {
        public int value;
        public Value1607ViaClass() { this(0); }
        public Value1607ViaClass(int value) { this.value = value; }
    }
@JsonPropertyOrder(alphabetic = true)
    static class ReallyAlwaysContainer {
        public Value1607ViaClass alwaysClass = new Value1607ViaClass(13);
        @JsonIdentityReference(alwaysAsId = true)
        public Value1607 alwaysProp = new Value1607(13);
    }
static class JSOGWrapper {
        public int value;
        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
        public Object jsog;
        JSOGWrapper() { }
        JSOGWrapper(int value) { this.value = value; }
    }
@JsonDeserialize(using = JSOGRefDeserializer.class)
    static class JSOGRef {
        @JsonProperty("@ref")
        public int ref;
        JSOGRef() { }
        JSOGRef(int value) { ref = value; }
        @Override public String toString() { return "[JSOGRef#" + ref + "]"; }
        @Override public int hashCode() { return ref; }
        @Override public boolean equals(Object other) {
            return other instanceof JSOGRef that && that.ref == ref;
        }
    }
static class JSOGRefDeserializer extends ValueDeserializer<JSOGRef> {
        @Override
        public JSOGRef deserialize(JsonParser parser, DeserializationContext ctxt) {
            JsonNode node = ctxt.readTree(parser);
            if (node.isString()) {
                return new JSOGRef(node.asInt());
            }
            JsonNode reference = node.get("@ref");
            if (reference == null) {
                ctxt.reportInputMismatch(JSOGRef.class,
                        "Could not find key '@ref' from (" + node.getClass().getName()
                                + "): " + node);
            }
            return new JSOGRef(reference.asInt());
        }
    }
static class JSOGGenerator extends ObjectIdGenerator<JSOGRef> {
        private static final long serialVersionUID = 1L;
        protected transient int nextValue;
        protected final Class<?> scope;

        protected JSOGGenerator() { this(null, -1); }
        protected JSOGGenerator(Class<?> scope, int nextValue) {
            this.scope = scope;
            this.nextValue = nextValue;
        }
        @Override public Class<?> getScope() { return scope; }
        @Override public boolean canUseFor(ObjectIdGenerator<?> gen) {
            return gen.getClass() == getClass() && gen.getScope() == scope;
        }
        @Override public ObjectIdGenerator<JSOGRef> forScope(Class<?> scope) {
            return this.scope == scope ? this : new JSOGGenerator(scope, nextValue);
        }
        @Override public ObjectIdGenerator<JSOGRef> newForSerialization(Object context) {
            return new JSOGGenerator(scope, 1);
        }
        @Override public com.fasterxml.jackson.annotation.ObjectIdGenerator.IdKey key(Object key) {
            return new IdKey(getClass(), scope, key);
        }
        @Override public boolean maySerializeAsObject() { return true; }
        @Override public boolean isValidReferencePropertyName(String name, Object parser) {
            return "@ref".equals(name);
        }
        @Override public JSOGRef generateId(Object forPojo) {
            return new JSOGRef(nextValue++);
        }
    }
@JsonIdentityInfo(generator = JSOGGenerator.class, property = "@id")
    static class IdentifiableExampleJSOG {
        public int foo;
        public IdentifiableExampleJSOG next;
        protected IdentifiableExampleJSOG() { }
        IdentifiableExampleJSOG(int value) { foo = value; }
    }
@JsonIdentityInfo(generator = JSOGGenerator.class)
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY,
            property = "@class")
    static class Inner {
        public String bar;
        protected Inner() { }
        Inner(String bar) { this.bar = bar; }
    }
static class SubInner extends Inner {
        public String extra;
        protected SubInner() { }
        SubInner(String bar, String extra) { super(bar); this.extra = extra; }
    }
@JsonIdentityInfo(generator = JSOGGenerator.class)
    static class Outer {
        public String foo;
        public Inner inner1;
        public Inner inner2;
    }
static abstract class AbstractIdBase {
        protected String id = UUID.randomUUID().toString();
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        @Override public int hashCode() { return Objects.hash(id); }
        @Override public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            return Objects.equals(id, ((AbstractIdBase) obj).id);
        }
    }
@JsonIdentityInfo(property = "id", generator = ObjectIdGenerators.PropertyGenerator.class)
    static class Product extends AbstractIdBase {
        private String name;
        public Product() { }
        Product(String id, String name) { this.id = id; this.name = name; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
static class Order extends AbstractIdBase {
        @JsonIdentityReference(alwaysAsId = true)
        private Set<Product> items = new HashSet<>();
        public Order() { }
        Order(String id) { this.id = id; }
        public Set<Product> getItems() { return items; }
        public void setItems(Set<Product> items) { this.items = items; }
    }
@JsonPropertyOrder({ "products", "orders" })
    static class Root {
        private Set<Order> orders = new HashSet<>();
        @JsonIdentityInfo(property = "id", generator = ObjectIdGenerators.PropertyGenerator.class)
        private Set<Product> products = new HashSet<>();
        public Set<Order> getOrders() { return orders; }
        public void setOrders(Set<Order> orders) { this.orders = orders; }
        public Set<Product> getProducts() { return products; }
        public void setProducts(Set<Product> products) { this.products = products; }
    }
@JsonIdentityInfo(property = "id", generator = ObjectIdGenerators.PropertyGenerator.class)
    static class Parent extends AbstractIdBase {
        private String name;
        @JsonManagedReference
        private Set<Child> children = new HashSet<>();
        public Parent() { }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Set<Child> getChildren() { return children; }
        public void setChildren(Set<Child> children) { this.children = children; }
    }
@JsonIdentityInfo(property = "id", generator = ObjectIdGenerators.PropertyGenerator.class)
    static class Child extends AbstractIdBase {
        private String name;
        @JsonBackReference
        private Parent parent;
        public Child() { }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Parent getParent() { return parent; }
        public void setParent(Parent parent) { this.parent = parent; }
        @Override public int hashCode() { return Objects.hash(id, parent); }
        @Override public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Child other = (Child) obj;
            return Objects.equals(id, other.id) && Objects.equals(parent, other.parent);
        }
    }
private static final class NoCheckSubTypeValidator
            extends PolymorphicTypeValidator.Base {
        private static final long serialVersionUID = 1L;
        @Override
        public Validity validateBaseType(DatabindContext ctxt, JavaType baseType) {
            return Validity.ALLOWED;
        }
    }

    void __invoke_testStructJSOGRefVpack() throws Exception {
        try {
            testStructJSOGRefVpack();
        } finally {
        }
    }


    void __invoke_testPolymorphicRoundTripVpack() throws Exception {
        try {
            testPolymorphicRoundTripVpack();
        } finally {
        }
    }


    void __invoke_testAlterativePolymorphicRoundTrip669Vpack() throws Exception {
        try {
            testAlterativePolymorphicRoundTrip669Vpack();
        } finally {
        }
    }

}
