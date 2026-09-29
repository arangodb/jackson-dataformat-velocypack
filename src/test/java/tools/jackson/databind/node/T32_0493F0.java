package tools.jackson.databind.node;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import tools.jackson.core.JacksonException;
import tools.jackson.core.Version;
import tools.jackson.databind.BeanDescription;
import tools.jackson.databind.DeserializationConfig;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.ObjectWriter;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.cfg.JsonNodeFeature;
import tools.jackson.databind.deser.BeanDeserializerBuilder;
import tools.jackson.databind.deser.SettableBeanProperty;
import tools.jackson.databind.deser.ValueDeserializerModifier;
import tools.jackson.databind.deser.ValueInstantiator;
import tools.jackson.databind.deser.jdk.CollectionDeserializer;
import tools.jackson.databind.deser.std.DelegatingDeserializer;
import tools.jackson.databind.node.MissingNode;
import tools.jackson.databind.type.CollectionLikeType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0493F0 {
private static final byte[] MISSING_ROOT = VPackWireFixtureTest.hex(
            "06 07 02 0a 01 03 04");
private static final byte[] PARENT_ROOT = VPackWireFixtureTest.hex(
            "14 56"
          + "48 63 68 69 6c 64 72 65 6e"
          + "06 2b 02"
          + "14 13 48 70 72 6f 70 65 72 74 79 46 76 61 6c 75 65 31 01"
          + "14 13 48 70 72 6f 70 65 72 74 79 46 76 61 6c 75 65 32 01"
          + "03 16"
          + "4b 73 69 6e 67 6c 65 43 68 69 6c 64"
          + "14 13 48 70 72 6f 70 65 72 74 79 46 76 61 6c 75 65 33 01"
          + "02");
private static final byte[] NULL_PROPERTY = VPackWireFixtureTest.hex(
            "14 08 43 6e 76 6c 18 01");
private static final byte[] NULL_MIXED = VPackWireFixtureTest.hex(
            "14 0c 41 61 31 41 62 18 41 63 1a 03");
private static final byte[] PRECISE_DECIMAL = VPackWireFixtureTest.hex(
            "c8 11 de ff ff ff 12 34 56 78 90 12 34 56 78 90 12 34 56 78 91 23 45");
private final VPackMapper mapper = new VPackMapper();
private final ObjectReader reader = mapper.reader();
private final ObjectWriter writer = mapper.writer();

    // Provenance: MissingNodeTest#testMissing().
    void testMissingVpack() {
        MissingNode n = MissingNode.getInstance();
        assertTrue(n.isMissingNode());
        assertEquals(tools.jackson.core.JsonToken.NOT_AVAILABLE, n.asToken());
        assertEquals("", n.asString());
        assertEquals("default", n.asString("default"));
        assertFalse(n.asStringOpt().isPresent());
        assertTrue(n.equals(n));
        assertFalse(n.equals(null));
        assertFalse(n.equals(new Object()));
        n.hashCode();
        assertEquals("", n.toString());
        assertFalse(n.isNumber());
        assertFalse(n.canConvertToInt());
        assertFalse(n.canConvertToLong());
        assertFalse(n.canConvertToExactIntegral());
        assertEquals(-42, n.asInt(-42));
        assertEquals(12345678901L, n.asLong(12345678901L));
        assertEquals(-19.25, n.asDouble(-19.25));
        assertTrue(n.asBoolean(true));
        assertEquals(4, n.asInt(4));
        assertEquals(5L, n.asLong(5));
        assertEquals(0.25, n.asDouble(0.25));
    }

    // Provenance: MissingNodeTest#testMissingNodeNullLikeBehavior().
    void testMissingNodeNullLikeBehaviorVpack() {
        MissingNode n = MissingNode.getInstance();
        assertFalse(n.asBoolean());
        assertTrue(n.asBoolean(true));
        assertFalse(n.asBooleanOpt().isPresent());
        assertEquals("", n.asString());
        assertEquals("default", n.asString("default"));
        assertFalse(n.asStringOpt().isPresent());
        assertEquals((short) 0, n.asShort());
        assertEquals((short) 5, n.asShort((short) 5));
        assertFalse(n.asShortOpt().isPresent());
        assertEquals(0, n.asInt());
        assertEquals(5, n.asInt(5));
        assertFalse(n.asIntOpt().isPresent());
        assertEquals(0L, n.asLong());
        assertEquals(5L, n.asLong(5L));
        assertEquals(BigInteger.ZERO, n.asBigInteger());
        assertEquals(BigInteger.TEN, n.asBigInteger(BigInteger.TEN));
        assertFalse(n.asBigIntegerOpt().isPresent());
        assertEquals(0.0f, n.asFloat());
        assertEquals(1.5f, n.asFloat(1.5f));
        assertFalse(n.asFloatOpt().isPresent());
        assertEquals(0.0d, n.asDouble());
        assertEquals(1.5d, n.asDouble(1.5d));
        assertFalse(n.asDoubleOpt().isPresent());
        assertEquals(BigDecimal.ZERO, n.asDecimal());
        assertEquals(BigDecimal.TEN, n.asDecimal(BigDecimal.TEN));
        assertFalse(n.asDecimalOpt().isPresent());
    }

    // Provenance: MissingNodeTest#testMissingViaMapper().
    void testMissingViaMapperVpack() throws Exception {
        JsonNode result = mapper.readTree(MISSING_ROOT);
        assertTrue(result.isArray());
        assertEquals(2, result.size());

        Iterator<JsonNode> it = result.iterator();
        JsonNode onode = it.next();
        assertTrue(onode.isObject());
        assertEquals(0, onode.size());
        assertFalse(onode.isMissingNode());
        assertTrue(onode.asOptional().isPresent());
        assertNull(onode.get(0));
        JsonNode dummyNode = onode.path(0);
        assertNotNull(dummyNode);
        assertTrue(dummyNode.isMissingNode());
        assertNull(dummyNode.get(3));
        assertNull(dummyNode.get("whatever"));
        assertFalse(dummyNode.asOptional().isPresent());
        assertTrue(dummyNode.path(98).isMissingNode());
        assertTrue(dummyNode.path("field").isMissingNode());

        JsonNode anode = it.next();
        assertTrue(anode.isArray());
        assertEquals(0, anode.size());
        assertFalse(anode.isMissingNode());
        assertNull(anode.get(0));
        dummyNode = anode.path(0);
        assertTrue(dummyNode.isMissingNode());
        assertNull(dummyNode.get(0));
        assertNull(dummyNode.get("myfield"));
        assertFalse(dummyNode.asOptional().isPresent());
        assertTrue(dummyNode.path(98).isMissingNode());
        assertTrue(dummyNode.path("f").isMissingNode());
    }
private BigDecimal readPreciseDecimal(boolean global, Boolean node) throws Exception {
        VPackMapper.Builder builder = VPackMapper.builder();
        builder.configure(tools.jackson.databind.DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, global);
        if (node != null) {
            builder.configure(JsonNodeFeature.USE_BIG_DECIMAL_FOR_FLOATS, node);
        }
        return builder.build().readTree(PRECISE_DECIMAL).decimalValue();
    }
private static ObjectMapper parentMapper() {
        return VPackMapper.builder().addModule(new tools.jackson.databind.JacksonModule() {
            @Override
            public String getModuleName() { return "parentSetting"; }

            @Override
            public Version version() { return Version.unknownVersion(); }

            @Override
            public void setupModule(SetupContext context) {
                context.addDeserializerModifier(new ParentSettingDeserializerModifier());
            }
        }).build();
    }
interface HasParent {
        void setParent(Parent parent);
        Parent getParent();
    }
static class Child implements HasParent {
        public Parent parent;
        public String property;
        @Override public void setParent(Parent p) { parent = p; }
        @Override public Parent getParent() { return parent; }
    }
static class Parent {
        public List<Child> children;
        public Child singleChild;
    }
static class ListValueInstantiator extends ValueInstantiator {
        @Override public String getValueTypeDesc() { return List.class.getName(); }
        @Override public Object createUsingDefault(tools.jackson.databind.DeserializationContext ctxt)
                throws JacksonException { return new ArrayList<>(); }
        @Override public ValueInstantiator createContextual(tools.jackson.databind.DeserializationContext ctxt,
                BeanDescription.Supplier beanDescRef) { return this; }
        @Override public Class<?> getValueClass() { return List.class; }
    }
static class ParentSettingDeserializerModifier extends ValueDeserializerModifier {
        private static final long serialVersionUID = 1L;
        @Override
        public BeanDeserializerBuilder updateBuilder(DeserializationConfig config,
                BeanDescription.Supplier beanDescRef, BeanDeserializerBuilder builder) {
            for (Iterator<SettableBeanProperty> properties = builder.getProperties(); properties.hasNext();) {
                SettableBeanProperty property = properties.next();
                builder.addOrReplaceProperty(property.withValueDeserializer(
                        new ParentSettingDeserializerContextual()), false);
            }
            return builder;
        }
    }
static class ParentSettingDeserializer extends DelegatingDeserializer {
        ParentSettingDeserializer(ValueDeserializer<?> delegatee) { super(delegatee); }
        @Override
        public Object deserialize(tools.jackson.core.JsonParser p,
                tools.jackson.databind.DeserializationContext ctxt) throws JacksonException {
            Object value = super.deserialize(p, ctxt);
            if (value instanceof HasParent obj) {
                Parent parent = null;
                tools.jackson.core.TokenStreamContext context = p.streamReadContext();
                while (parent == null && context != null) {
                    Object current = context.currentValue();
                    if (current instanceof Parent currentParent) parent = currentParent;
                    context = context.getParent();
                }
                if (parent != null) obj.setParent(parent);
            }
            return value;
        }
        @Override
        protected ValueDeserializer<?> newDelegatingInstance(ValueDeserializer<?> newDelegatee) {
            return new ParentSettingDeserializer(newDelegatee);
        }
    }
static class ParentSettingDeserializerContextual extends ValueDeserializer<Object> {
        @Override
        public ValueDeserializer<?> createContextual(tools.jackson.databind.DeserializationContext ctxt,
                tools.jackson.databind.BeanProperty property) throws JacksonException {
            tools.jackson.databind.JavaType propertyType = property.getType();
            tools.jackson.databind.JavaType contentType = propertyType;
            if (propertyType.isCollectionLikeType()) contentType = propertyType.getContentType();
            ValueDeserializer<Object> delegatee = ctxt.findNonContextualValueDeserializer(contentType);
            ValueDeserializer<Object> objectDeserializer = new ParentSettingDeserializer(delegatee);
            if (!propertyType.isCollectionLikeType()) return objectDeserializer;
            CollectionLikeType collectionType = ctxt.getTypeFactory().constructCollectionLikeType(
                    propertyType.getRawClass(), contentType);
            CollectionDeserializer collectionDeserializer = new CollectionDeserializer(
                    collectionType, objectDeserializer, null, new ListValueInstantiator(), null);
            return collectionDeserializer.createContextual(ctxt, property);
        }

        @Override
        public Object deserialize(tools.jackson.core.JsonParser p,
                tools.jackson.databind.DeserializationContext ctxt) {
            throw new UnsupportedOperationException();
        }
    }

    void __invoke_testMissingVpack() throws Exception {
        try {
            testMissingVpack();
        } finally {
        }
    }


    void __invoke_testMissingNodeNullLikeBehaviorVpack() throws Exception {
        try {
            testMissingNodeNullLikeBehaviorVpack();
        } finally {
        }
    }


    void __invoke_testMissingViaMapperVpack() throws Exception {
        try {
            testMissingViaMapperVpack();
        } finally {
        }
    }

}
