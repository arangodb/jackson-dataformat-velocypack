package tools.jackson.databind.deser.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import tools.jackson.databind.BeanDescription;
import tools.jackson.databind.DeserializationConfig;
import tools.jackson.databind.PropertyMetadata;
import tools.jackson.databind.PropertyName;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.deser.BeanDeserializerBuilder;
import tools.jackson.databind.deser.SettableBeanProperty;
import tools.jackson.databind.deser.ValueDeserializerModifier;
import tools.jackson.databind.deser.bean.BeanPropertyMap;
import tools.jackson.databind.deser.impl.ObjectIdReader;
import tools.jackson.databind.deser.impl.ObjectIdValueProperty;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.type.TypeFactory;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0190F1 {
private static final byte[] PROPERTY_REMOVAL_OBJECT = VPackWireFixtureTest.hex(
            "14 07 41 62 41 32 01");
private static final byte[] INJECTABLE_BUILDER_OBJECT = VPackWireFixtureTest.hex(
            "14 09 41 79 33 41 78 37 02");
private static final byte[] EXTERNAL_TYPE_ID_OBJECT = VPackWireFixtureTest.hex(
            "14 22 45 76 61 6c 75 65 14 0b 45 76 61 6c 75 65 28 0d 01 "
          + "47 65 78 74 54 79 70 65 45 76 62 65 61 6e 02");

    void testArrayOutOfBounds884() {
        List<SettableBeanProperty> props = new ArrayList<>();
        PropertyMetadata metadata = PropertyMetadata.STD_REQUIRED;
        props.add(new ObjectIdValueProperty(new MyObjectIdReader("pk"), metadata, false));
        props.add(new ObjectIdValueProperty(new MyObjectIdReader("firstName"), metadata, false));

        BeanPropertyMap propertyMap = new TestBeanPropertyMap(props,
                null, Locale.getDefault(), false, true);
        propertyMap = propertyMap.withProperty(new ObjectIdValueProperty(
                new MyObjectIdReader("@id"), metadata, false));

        assertNotNull(propertyMap);
    }
static class Bean {
        public String b = "b";
        public String a = "a";
    }
static class BeanModule extends SimpleModule {
        BeanModule(ValueDeserializerModifier modifier) {
            setDeserializerModifier(modifier);
        }
    }
static class RemovingModifier extends ValueDeserializerModifier {
        private final String removedProperty;

        RemovingModifier(String property) {
            removedProperty = property;
        }

        @Override
        public BeanDeserializerBuilder updateBuilder(DeserializationConfig config,
                BeanDescription.Supplier beanDescRef, BeanDeserializerBuilder builder) {
            builder.addIgnorable(removedProperty);
            return builder;
        }
    }
@SuppressWarnings("serial")
    static class MyObjectIdReader extends ObjectIdReader {
        MyObjectIdReader(String name) {
            super(TypeFactory.unknownType(), new PropertyName(name), null,
                    null, null, null);
        }
    }
static class TestBeanPropertyMap extends BeanPropertyMap {
        TestBeanPropertyMap(List<SettableBeanProperty> properties,
                PropertyName[][] aliases, Locale locale, boolean caseInsensitive,
                boolean assignIndexes) {
            super(properties, aliases, locale, caseInsensitive, assignIndexes);
        }
    }
@JsonDeserialize(builder = InjectableBuilderXY.class)
    static class InjectableXY {
        final int x;
        final int y;
        final String stuff;

        InjectableXY(int x, int y, String stuff) {
            this.x = x + 1;
            this.y = y + 1;
            this.stuff = stuff;
        }
    }
static class InjectableBuilderXY {
        public int x;
        public int y;

        @JacksonInject
        protected String stuff;

        public InjectableBuilderXY withX(int value) {
            x = value;
            return this;
        }

        public InjectableBuilderXY withY(int value) {
            y = value;
            return this;
        }

        public InjectableXY build() {
            return new InjectableXY(x, y, stuff);
        }
    }
@JsonDeserialize(builder = ExternalBuilder2580.class)
    static class ExternalBean2580 {
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
                include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "extType")
        public Object value;

        ExternalBean2580(Object value) {
            this.value = value;
        }
    }
@JsonSubTypes(@JsonSubTypes.Type(ValueBean2580.class))
    static class BaseBean2580 { }
@JsonTypeName("vbean")
    static class ValueBean2580 extends BaseBean2580 {
        public int value;
    }
static class ExternalBuilder2580 {
        BaseBean2580 value;

        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
                include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "extType")
        public ExternalBuilder2580 withValue(BaseBean2580 value) {
            this.value = value;
            return this;
        }

        public ExternalBean2580 build() {
            return new ExternalBean2580(value);
        }
    }

    void __invoke_testArrayOutOfBounds884() throws Exception {
        try {
            testArrayOutOfBounds884();
        } finally {
        }
    }

}
