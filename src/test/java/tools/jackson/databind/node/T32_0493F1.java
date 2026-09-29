package tools.jackson.databind.node;

import java.math.BigDecimal;
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
import tools.jackson.databind.type.CollectionLikeType;

import static org.junit.jupiter.api.Assertions.assertSame;

import tools.jackson.dataformat.velocypack.*;

class T32_0493F1 {
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

    // Provenance: NodeContext2049Test#testReadFromTree().
    void testReadFromTreeVpack() throws Exception {
        ObjectMapper objectMapper = parentMapper();
        JsonNode tree = objectMapper.readTree(PARENT_ROOT);
        Parent obj = objectMapper.reader().forType(Parent.class).readValue(tree);
        assertSame(obj, obj.singleChild.getParent());
        for (Child child : obj.children) {
            assertSame(obj, child.getParent());
        }
    }

    // Provenance: NodeContext2049Test#testReadNoBuffering().
    void testReadNoBufferingVpack() throws Exception {
        Parent obj = parentMapper().readerFor(Parent.class).readValue(PARENT_ROOT);
        assertSame(obj, obj.singleChild.getParent());
        for (Child child : obj.children) {
            assertSame(obj, child.getParent());
        }
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

    void __invoke_testReadFromTreeVpack() throws Exception {
        try {
            testReadFromTreeVpack();
        } finally {
        }
    }


    void __invoke_testReadNoBufferingVpack() throws Exception {
        try {
            testReadNoBufferingVpack();
        } finally {
        }
    }

}
