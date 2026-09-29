package tools.jackson.databind.ser;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.BeanDescription;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationConfig;
import tools.jackson.databind.exc.InvalidDefinitionException;
import tools.jackson.databind.introspect.AnnotatedMember;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.BeanPropertyWriter;
import tools.jackson.databind.ser.ValueSerializerModifier;
import tools.jackson.databind.ser.std.DelegatingSerializer;
import tools.jackson.databind.ser.std.StdSerializer;
import tools.jackson.databind.util.NameTransformer;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0543F1 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] PRIVATE_ANY = VPackWireFixtureTest.hex(
            "0b 45 04 4d 66 69 72 73 74 50 72 6f 70 65 72 74 79 31 "
          + "4d 74 68 69 72 64 50 72 6f 70 65 72 74 79 33 "
          + "4e 66 6f 75 72 74 68 50 72 6f 70 65 72 74 79 34 "
          + "4e 73 65 63 6f 6e 64 50 72 6f 70 65 72 74 79 32 "
          + "03 21 31 12");
private static final byte[] PRIVATE_ANY_SORTED = VPackWireFixtureTest.hex(
            "0b 45 04 4d 66 69 72 73 74 50 72 6f 70 65 72 74 79 31 "
          + "4e 73 65 63 6f 6e 64 50 72 6f 70 65 72 74 79 32 "
          + "4d 74 68 69 72 64 50 72 6f 70 65 72 74 79 33 "
          + "4e 66 6f 75 72 74 68 50 72 6f 70 65 72 74 79 34 "
          + "03 31 12 22");
private static final byte[] SIMPLE_ANY = VPackWireFixtureTest.hex(
            "0b 0b 02 41 78 33 41 61 1a 06 03");
private static final byte[] ANY_SORTED = VPackWireFixtureTest.hex(
            "0b 17 04 42 7a 61 31 42 7a 62 32 42 7a 63 33 42 7a 64 34 "
          + "03 07 0b 0f");
private static final byte[] CLASS_SORT_NOT_PROPAGATED = VPackWireFixtureTest.hex(
            "0b 1e 06 41 61 31 41 62 32 41 63 33 42 7a 63 33 42 7a 61 31 "
          + "42 7a 62 32 03 06 09 10 14 0c");
private static final byte[] ORDER_V1 = VPackWireFixtureTest.hex(
            "0b 48 06 46 63 68 69 6c 64 31 33 46 63 68 69 6c 64 32 33 "
          + "48 65 6e 74 69 74 79 49 64 31 4a 74 6f 74 61 6c 54 65 73 74 73 32 "
          + "4a 65 6e 74 69 74 79 4e 61 6d 65 43 42 6f 62 48 70 72 6f 64 75 63 74 31 34 "
          + "03 0b 13 29 38 1d");
private static final byte[] ORDER_V2 = VPackWireFixtureTest.hex(
            "0b 48 06 48 65 6e 74 69 74 79 49 64 31 4a 74 6f 74 61 6c 54 65 73 74 73 32 "
          + "46 63 68 69 6c 64 31 33 46 63 68 69 6c 64 32 33 48 70 72 6f 64 75 63 74 31 34 "
          + "4a 65 6e 74 69 74 79 4e 61 6d 65 43 42 6f 62 "
          + "19 21 03 33 29 0d");
private static final byte[] ORDER_UNWRAPPED_V1 = ORDER_V1;
private static final byte[] ORDER_UNWRAPPED_V2 = VPackWireFixtureTest.hex(
            "0b 48 06 48 65 6e 74 69 74 79 49 64 31 4a 74 6f 74 61 6c 54 65 73 74 73 32 "
          + "46 63 68 69 6c 64 31 33 46 63 68 69 6c 64 32 33 "
          + "4a 65 6e 74 69 74 79 4e 61 6d 65 43 42 6f 62 48 70 72 6f 64 75 63 74 31 34 "
          + "19 21 03 29 38 0d");
private static final byte[] HIDDEN_FIELD = VPackWireFixtureTest.hex(
            "0b 0e 01 44 6e 61 6d 65 44 4a 6f 68 6e 03");

    void testBasicDelegatingSerializerVpack() throws Exception {
        DelegatingSerializer5630Impl serializer = new DelegatingSerializer5630Impl();
        assertEquals(String.class, serializer.handledType());
        tools.jackson.databind.ValueSerializer<?> delegatee = serializer.getDelegatee();
        assertNotNull(delegatee);
        assertEquals(serializer.usesObjectId(), delegatee.usesObjectId());
        assertEquals(serializer.isUnwrappingSerializer(), delegatee.isUnwrappingSerializer());
        assertFalse(serializer.properties().hasNext());
        assertFalse(serializer.isEmpty(null, "foo"));
        assertSame(serializer, serializer.withFilterId("abc"));
        assertSame(serializer, serializer.withIgnoredProperties(java.util.Collections.emptySet()));
        assertSame(serializer, serializer.withFormatOverrides(null,
                com.fasterxml.jackson.annotation.JsonFormat.Value.empty()));
        assertSame(serializer, serializer.replaceDelegatee(delegatee));
        assertNotSame(serializer, serializer.replaceDelegatee(new QuotingStringSerializer5630Impl()));
        tools.jackson.databind.ValueSerializer<?> unwrapping = serializer.unwrappingSerializer(null);
        assertNotNull(unwrapping);
        assertNotSame(serializer, unwrapping);
    }

    void modifierIssue1612Vpack() {
        SimpleModule module = new SimpleModule();
        module.setSerializerModifier(new Modifier1612());
        ObjectMapper mapper = VPackMapper.builder().addModule(module).build();
        InvalidDefinitionException failure = assertThrows(InvalidDefinitionException.class,
                () -> mapper.writeValueAsBytes(new Bean1612(0, 1, 2d)));
        assertEquals(true, failure.getMessage().contains("Failed to construct BeanSerializer"));
        assertEquals(true, failure.getMessage().contains(Bean1612.class.getName()));
    }

    void annotationsAccessIssue5414Vpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addModule(new HiddenFieldModule5414()).build();
        assertVpack(HIDDEN_FIELD, mapper.writeValueAsBytes(new User5414("John", "123456")));
    }
private static BaseWithProperties configure(BaseWithProperties base) {
        base.entityId = 1;
        base.entityName = "Bob";
        base.totalTests = 2;
        base.childEntities = new Location();
        base.childEntities.child1 = 3;
        base.childEntities.child2 = 3;
        base.products = new HashMap<>();
        base.products.put("product1", 4);
        return base;
    }
private static void assertVpack(byte[] expected, byte[] actual) {
        assertArrayEquals(expected, actual, "actual=" + toHex(actual));
    }
private static String toHex(byte[] value) {
        StringBuilder result = new StringBuilder();
        for (byte b : value) {
            if (result.length() != 0) result.append(' ');
            result.append(String.format("%02x", b & 0xff));
        }
        return result.toString();
    }
static class Bean {
        static final Map<String, Boolean> EXTRA = Map.of("a", Boolean.TRUE);
        public int getX() { return 3; }
        @JsonAnyGetter public Map<String, Boolean> getExtra() { return EXTRA; }
    }
static class BaseWithProperties {
        public String entityName;
        public int entityId;
        public Integer totalTests;
        @JsonAnyGetter public Map<String, Object> products;
        @JsonUnwrapped public Location childEntities;
    }
@JsonPropertyOrder({"childEntities", "entityId", "totalTests", "entityName", "products"})
    static class PojoPropertyVersion1 extends BaseWithProperties { }
@JsonPropertyOrder({"entityId", "totalTests", "childEntities", "products", "entityName"})
    static class PojoPropertyVersion2 extends BaseWithProperties { }
@JsonPropertyOrder({"childEntities", "entityId", "totalTests", "entityName", "products"})
    static class PojoUnwrappedVersion1 extends BaseWithProperties { }
@JsonPropertyOrder({"entityId", "totalTests", "childEntities", "entityName", "products"})
    static class PojoUnwrappedVersion2 extends BaseWithProperties { }
@JsonPropertyOrder({"child1", "child2"})
    static class Location { public int child1; public int child2; }
static class AlphabeticOrderOnAnyGetterBean {
        @JsonPropertyOrder(alphabetic = true)
        @JsonAnyGetter public Map<String, Object> map = new LinkedHashMap<>();
    }
@JsonPropertyOrder(alphabetic = true)
    static class AlphabeticOrderOnClassBean {
        public int c = 3, a = 1, b = 2;
        @JsonAnyGetter public Map<String, Object> map = new LinkedHashMap<>();
    }
@JsonPropertyOrder({"firstProperty", "secondProperties", "thirdProperty", "fourthProperty"})
    static class PrivateAnyGetterPojo {
        public int firstProperty = 1, fourthProperty = 4, thirdProperty = 3;
        @JsonAnyGetter private Map<String, Object> secondProperties = new HashMap<>();
        public PrivateAnyGetterPojo add(String key, Object value) {
            secondProperties.put(key, value);
            return this;
        }
        public Map<String, Object> secondProperties() { return secondProperties; }
    }
@JsonPropertyOrder({"firstProperty", "secondProperties", "thirdProperty", "fourthProperty"})
    static class PrivateAnyGetterPojoSorted extends PrivateAnyGetterPojo {
        public Map<String, Object> getSecondProperties() { return secondProperties(); }
    }
static class DelegatingSerializer5630Impl extends DelegatingSerializer {
        DelegatingSerializer5630Impl() { this(new QuotingStringSerializer5630Impl()); }
        DelegatingSerializer5630Impl(tools.jackson.databind.ValueSerializer<?> valueSerializer) {
            super(valueSerializer);
        }
        @Override
        protected tools.jackson.databind.ValueSerializer<Object> newDelegatingInstance(
                tools.jackson.databind.ValueSerializer<?> newDelegatee) {
            return new DelegatingSerializer5630Impl(newDelegatee);
        }
    }
static class QuotingStringSerializer5630Impl extends StdSerializer<String> {
        QuotingStringSerializer5630Impl() { super(String.class); }
        @Override
        public void serialize(String value, JsonGenerator gen,
                tools.jackson.databind.SerializationContext ctxt) {
            gen.writeString("'" + value + "'");
        }
        @Override
        public boolean isEmpty(tools.jackson.databind.SerializationContext ctxt, String value) {
            return value.isEmpty();
        }
        @Override
        public tools.jackson.databind.ValueSerializer<String> unwrappingSerializer(
                NameTransformer unwrapper) {
            return new QuotingStringSerializer5630Impl();
        }
    }
static class Bean1612 {
        public Integer a;
        public Integer b;
        public Double c;
        Bean1612(Integer a, Integer b, Double c) { this.a = a; this.b = b; this.c = c; }
    }
static class Modifier1612 extends ValueSerializerModifier {
        @Override
        public tools.jackson.databind.ser.BeanSerializerBuilder updateBuilder(
                SerializationConfig config, BeanDescription.Supplier beanDescRef,
                tools.jackson.databind.ser.BeanSerializerBuilder builder) {
            List<BeanPropertyWriter> filtered = new java.util.ArrayList<>(2);
            List<BeanPropertyWriter> properties = builder.getProperties();
            builder.setFilteredProperties(new BeanPropertyWriter[] {
                    properties.get(0), properties.get(1), properties.get(2)});
            filtered.add(properties.get(1));
            filtered.add(properties.get(2));
            builder.setProperties(filtered);
            return builder;
        }
    }
record User5414(String name, @Hidden String password) { }
@Retention(RetentionPolicy.RUNTIME) @interface Hidden { }
static class HiddenFieldModule5414 extends SimpleModule {
        @Override public void setupModule(SetupContext context) {
            super.setupModule(context);
            context.addSerializerModifier(new HiddenFieldRemover5414());
        }
    }
static class HiddenFieldRemover5414 extends ValueSerializerModifier {
        @Override
        public List<BeanPropertyWriter> changeProperties(SerializationConfig config,
                BeanDescription.Supplier beanDesc, List<BeanPropertyWriter> beanProperties) {
            return beanProperties.stream()
                    .filter(writer -> !isHidden(writer.getMember()))
                    .toList();
        }
        private boolean isHidden(AnnotatedMember member) {
            if (member.annotations() == null) return false;
            return member.annotations().anyMatch(a -> a.annotationType().equals(Hidden.class));
        }
    }

    void __invoke_testBasicDelegatingSerializerVpack() throws Exception {
        try {
            testBasicDelegatingSerializerVpack();
        } finally {
        }
    }


    void __invoke_modifierIssue1612Vpack() throws Exception {
        try {
            modifierIssue1612Vpack();
        } finally {
        }
    }


    void __invoke_annotationsAccessIssue5414Vpack() throws Exception {
        try {
            annotationsAccessIssue5414Vpack();
        } finally {
        }
    }

}
