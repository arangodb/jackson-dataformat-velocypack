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
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0508F2 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] JSOG_REFERENCE = VPackWireFixtureTest.hex(
            "14 1e 43 40 69 64 41 31 43 66 6f 6f 28 42 44 6e 65 78 74 "
          + "14 0a 44 40 72 65 66 41 31 01 03");

    // Provenance: JsonIdentityHashCodeIssue1546Test#testHashSetCorruptionWithIdentityReferences().
    void testHashSetCorruptionWithIdentityReferencesVpack() throws Exception {
        Product p1 = new Product("product-1", "Apple");
        Product p2 = new Product("product-2", "Cherry");
        Product p3 = new Product("product-3", "Strawberry");

        Order order1 = new Order("order-1");
        order1.getItems().add(p1);
        order1.getItems().add(p2);
        order1.getItems().add(p3);
        Order order2 = new Order("order-2");
        order2.getItems().add(p2);
        order2.getItems().add(p3);

        Root root = new Root();
        root.getProducts().add(p1);
        root.getProducts().add(p2);
        root.getProducts().add(p3);
        root.getOrders().add(order1);
        root.getOrders().add(order2);

        Root result = MAPPER.readValue(MAPPER.writeValueAsBytes(root), Root.class);
        assertEquals(3, result.getProducts().size());
        assertEquals(2, result.getOrders().size());

        Order resultOrder = result.getOrders().stream()
                .filter(order -> !order.getItems().isEmpty())
                .findFirst().orElseThrow();
        Product product = resultOrder.getItems().iterator().next();
        assertTrue(resultOrder.getItems().contains(product));
        assertTrue(resultOrder.getItems().stream().anyMatch(product::equals));
        assertTrue(new HashSet<>(resultOrder.getItems()).remove(product));
    }

    // Provenance: JsonIdentityHashCodeIssue1546Test#testHashSetCorruptionWithBackReferences().
    void testHashSetCorruptionWithBackReferencesVpack() throws Exception {
        Parent parent = new Parent();
        parent.setId("parent-1");
        parent.setName("Parent");

        Child child1 = new Child();
        child1.setId("child-1");
        child1.setName("Child 1");
        child1.setParent(parent);
        Child child2 = new Child();
        child2.setId("child-2");
        child2.setName("Child 2");
        child2.setParent(parent);
        parent.getChildren().add(child1);
        parent.getChildren().add(child2);

        Parent result = MAPPER.readValue(MAPPER.writeValueAsBytes(parent), Parent.class);
        assertEquals(2, result.getChildren().size());
        Child child = result.getChildren().iterator().next();
        assertTrue(result.getChildren().contains(child));
        assertTrue(new HashSet<>(result.getChildren()).remove(child));
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

    void __invoke_testHashSetCorruptionWithIdentityReferencesVpack() throws Exception {
        try {
            testHashSetCorruptionWithIdentityReferencesVpack();
        } finally {
        }
    }


    void __invoke_testHashSetCorruptionWithBackReferencesVpack() throws Exception {
        try {
            testHashSetCorruptionWithBackReferencesVpack();
        } finally {
        }
    }

}
