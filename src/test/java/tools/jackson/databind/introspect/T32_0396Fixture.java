package tools.jackson.databind.introspect;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.core.Version;
import tools.jackson.databind.AnnotationIntrospector;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.KeyDeserializer;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.PropertyName;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.cfg.MapperConfig;
import tools.jackson.databind.deser.jdk.NumberDeserializers;
import tools.jackson.databind.deser.jdk.StringDeserializer;
import tools.jackson.databind.introspect.Annotated;
import tools.jackson.databind.introspect.AnnotatedClass;
import tools.jackson.databind.introspect.AnnotationIntrospectorPair;
import tools.jackson.databind.jsontype.NamedType;
import tools.jackson.databind.ser.jdk.StringSerializer;
import tools.jackson.databind.ser.std.ToStringSerializer;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import tools.jackson.dataformat.velocypack.*;

class T32_0396Fixture {
private static final AnnotationIntrospector NO_ANNOTATIONS =
            AnnotationIntrospector.nopInstance();

    // Provenance: IntrospectorPairTest#testFindEnumValues().
    void testFindEnumValuesVpack() {
        IntrospectorWithMap intr1 = new IntrospectorWithMap()
                .add("findEnumValues", new String[] { "PRIMARY_A", null, "PRIMARY_C" });
        IntrospectorWithMap intr2 = new IntrospectorWithMap()
                .add("findEnumValues", new String[] { "SECONDARY_A", "SECONDARY_B", null });
        String[] defaultNames = new String[] { "A", "B", "C" };

        String[] result = new AnnotationIntrospectorPair(NO_ANNOTATIONS, NO_ANNOTATIONS)
                .findEnumValues(null, null, null, defaultNames.clone());
        assertArrayEquals(new String[] { "A", "B", "C" }, result);

        result = new AnnotationIntrospectorPair(intr1, intr2)
                .findEnumValues(null, null, null, defaultNames.clone());
        assertArrayEquals(new String[] { "PRIMARY_A", "SECONDARY_B", "PRIMARY_C" }, result);

        result = new AnnotationIntrospectorPair(intr2, intr1)
                .findEnumValues(null, null, null, defaultNames.clone());
        assertArrayEquals(new String[] { "SECONDARY_A", "SECONDARY_B", "PRIMARY_C" }, result);
    }

    // Provenance: IntrospectorPairTest#testFindFilterId().
    void testFindFilterIdVpack() {
        IntrospectorWithMap intr1 = new IntrospectorWithMap().add("findFilterId", "a");
        IntrospectorWithMap intr2 = new IntrospectorWithMap().add("findFilterId", "b");
        assertNull(new AnnotationIntrospectorPair(NO_ANNOTATIONS, NO_ANNOTATIONS)
                .findFilterId(null, null));
        assertEquals("a", new AnnotationIntrospectorPair(intr1, intr2)
                .findFilterId(null, null));
        assertEquals("b", new AnnotationIntrospectorPair(intr2, intr1)
                .findFilterId(null, null));
    }

    // Provenance: IntrospectorPairTest#testFindKeyDeserializer().
    void testFindKeyDeserializerVpack() {
        ValueDeserializer<?> deserString = StringDeserializer.instance;
        ValueDeserializer<?> deserBoolean = NumberDeserializers.find(Boolean.TYPE);
        IntrospectorWithMap intr1 = new IntrospectorWithMap().add("findKeyDeserializer", deserString);
        IntrospectorWithMap intr2 = new IntrospectorWithMap().add("findKeyDeserializer", deserBoolean);
        IntrospectorWithMap nop2 = new IntrospectorWithMap()
                .add("findKeyDeserializer", KeyDeserializer.None.class);

        assertSame(deserString, new AnnotationIntrospectorPair(intr1, intr2)
                .findKeyDeserializer(null, null));
        assertSame(deserBoolean, new AnnotationIntrospectorPair(intr2, intr1)
                .findKeyDeserializer(null, null));
        assertSame(deserString, new AnnotationIntrospectorPair(NO_ANNOTATIONS, intr1)
                .findKeyDeserializer(null, null));
        assertSame(deserString, new AnnotationIntrospectorPair(nop2, intr1)
                .findKeyDeserializer(null, null));
        assertNull(new AnnotationIntrospectorPair(NO_ANNOTATIONS, nop2)
                .findKeyDeserializer(null, null));
        assertNull(new AnnotationIntrospectorPair(nop2, NO_ANNOTATIONS)
                .findKeyDeserializer(null, null));
    }

    // Provenance: IntrospectorPairTest#testFindKeySerializer().
    void testFindKeySerializerVpack() {
        ValueSerializer<?> serString = new StringSerializer();
        ValueSerializer<?> serToString = ToStringSerializer.instance;
        IntrospectorWithMap intr1 = new IntrospectorWithMap().add("findKeySerializer", serString);
        IntrospectorWithMap intr2 = new IntrospectorWithMap().add("findKeySerializer", serToString);
        IntrospectorWithMap nop2 = new IntrospectorWithMap()
                .add("findKeySerializer", ValueSerializer.None.class);

        assertSame(serString, new AnnotationIntrospectorPair(intr1, intr2)
                .findKeySerializer(null, null));
        assertSame(serToString, new AnnotationIntrospectorPair(intr2, intr1)
                .findKeySerializer(null, null));
        assertSame(serString, new AnnotationIntrospectorPair(NO_ANNOTATIONS, intr1)
                .findKeySerializer(null, null));
        assertSame(serString, new AnnotationIntrospectorPair(nop2, intr1)
                .findKeySerializer(null, null));
        assertNull(new AnnotationIntrospectorPair(NO_ANNOTATIONS, nop2)
                .findKeySerializer(null, null));
        assertNull(new AnnotationIntrospectorPair(nop2, NO_ANNOTATIONS)
                .findKeySerializer(null, null));
    }

    // Provenance: IntrospectorPairTest#testFindNamingStrategy().
    void testFindNamingStrategyVpack() {
        IntrospectorWithMap intr1 = new IntrospectorWithMap().add("findNamingStrategy", Integer.class);
        IntrospectorWithMap intr2 = new IntrospectorWithMap().add("findNamingStrategy", String.class);
        assertNull(new AnnotationIntrospectorPair(NO_ANNOTATIONS, NO_ANNOTATIONS)
                .findNamingStrategy(null, null));
        assertEquals(Integer.class, new AnnotationIntrospectorPair(intr1, intr2)
                .findNamingStrategy(null, null));
        assertEquals(String.class, new AnnotationIntrospectorPair(intr2, intr1)
                .findNamingStrategy(null, null));
    }

    // Provenance: IntrospectorPairTest#testFindNullSerializer().
    void testFindNullSerializerVpack() {
        ValueSerializer<?> serString = new StringSerializer();
        ValueSerializer<?> serToString = ToStringSerializer.instance;
        IntrospectorWithMap intr1 = new IntrospectorWithMap().add("findNullSerializer", serString);
        IntrospectorWithMap intr2 = new IntrospectorWithMap().add("findNullSerializer", serToString);
        IntrospectorWithMap nop2 = new IntrospectorWithMap()
                .add("findNullSerializer", ValueSerializer.None.class);

        assertSame(serString, new AnnotationIntrospectorPair(intr1, intr2)
                .findNullSerializer(null, null));
        assertSame(serToString, new AnnotationIntrospectorPair(intr2, intr1)
                .findNullSerializer(null, null));
        assertSame(serString, new AnnotationIntrospectorPair(NO_ANNOTATIONS, intr1)
                .findNullSerializer(null, null));
        assertSame(serString, new AnnotationIntrospectorPair(nop2, intr1)
                .findNullSerializer(null, null));
        assertNull(new AnnotationIntrospectorPair(NO_ANNOTATIONS, nop2)
                .findNullSerializer(null, null));
        assertNull(new AnnotationIntrospectorPair(nop2, NO_ANNOTATIONS)
                .findNullSerializer(null, null));
    }

    // Provenance: IntrospectorPairTest#testFindPolymorphicBaseType().
    void testFindPolymorphicBaseTypeVpack() {
        ObjectMapper mapper = new VPackMapper();
        JavaType stringType = mapper.constructType(String.class);
        JavaType intType = mapper.constructType(Integer.class);
        IntrospectorWithMap intr1 = new IntrospectorWithMap()
                .add("findPolymorphicBaseType", stringType);
        IntrospectorWithMap intr2 = new IntrospectorWithMap()
                .add("findPolymorphicBaseType", intType);

        assertNull(new AnnotationIntrospectorPair(NO_ANNOTATIONS, NO_ANNOTATIONS)
                .findPolymorphicBaseType(null, null, null, null));
        assertSame(stringType, new AnnotationIntrospectorPair(intr1, intr2)
                .findPolymorphicBaseType(null, null, null, null));
        assertSame(intType, new AnnotationIntrospectorPair(intr2, intr1)
                .findPolymorphicBaseType(null, null, null, null));
        assertSame(intType, new AnnotationIntrospectorPair(NO_ANNOTATIONS, intr2)
                .findPolymorphicBaseType(null, null, null, null));
        assertSame(stringType, new AnnotationIntrospectorPair(intr1, NO_ANNOTATIONS)
                .findPolymorphicBaseType(null, null, null, null));
    }

    // Provenance: IntrospectorPairTest#testFindRootName().
    void testFindRootNameVpack() {
        PropertyName name = new PropertyName("test");
        IntrospectorWithMap intr = new IntrospectorWithMap().add("findRootName", name);
        assertNull(new AnnotationIntrospectorPair(NO_ANNOTATIONS, NO_ANNOTATIONS)
                .findRootName(null, null));
        assertEquals(name, new AnnotationIntrospectorPair(NO_ANNOTATIONS, intr)
                .findRootName(null, null));
        assertEquals(name, new AnnotationIntrospectorPair(intr, NO_ANNOTATIONS)
                .findRootName(null, null));
    }

    // Provenance: IntrospectorPairTest#testFindSerializationTyping().
    void testFindSerializationTypingVpack() {
        IntrospectorWithMap intr1 = new IntrospectorWithMap()
                .add("findSerializationTyping", JsonSerialize.Typing.STATIC);
        IntrospectorWithMap intr2 = new IntrospectorWithMap()
                .add("findSerializationTyping", JsonSerialize.Typing.DYNAMIC);
        assertNull(new AnnotationIntrospectorPair(NO_ANNOTATIONS, NO_ANNOTATIONS)
                .findSerializationTyping(null, null));
        assertEquals(JsonSerialize.Typing.STATIC, new AnnotationIntrospectorPair(intr1, intr2)
                .findSerializationTyping(null, null));
        assertEquals(JsonSerialize.Typing.DYNAMIC, new AnnotationIntrospectorPair(intr2, intr1)
                .findSerializationTyping(null, null));
        assertEquals(JsonSerialize.Typing.STATIC, new AnnotationIntrospectorPair(NO_ANNOTATIONS, intr1)
                .findSerializationTyping(null, null));
        assertEquals(JsonSerialize.Typing.DYNAMIC, new AnnotationIntrospectorPair(NO_ANNOTATIONS, intr2)
                .findSerializationTyping(null, null));
    }

    // Provenance: IntrospectorPairTest#testFindSerializer().
    void testFindSerializerVpack() {
        ValueSerializer<?> serString = new StringSerializer();
        ValueSerializer<?> serToString = ToStringSerializer.instance;
        IntrospectorWithMap intr1 = new IntrospectorWithMap().add("findSerializer", serString);
        IntrospectorWithMap intr2 = new IntrospectorWithMap().add("findSerializer", serToString);
        IntrospectorWithMap nop2 = new IntrospectorWithMap()
                .add("findSerializer", ValueSerializer.None.class);

        assertSame(serString, new AnnotationIntrospectorPair(intr1, intr2)
                .findSerializer(null, null));
        assertSame(serToString, new AnnotationIntrospectorPair(intr2, intr1)
                .findSerializer(null, null));
        assertSame(serString, new AnnotationIntrospectorPair(NO_ANNOTATIONS, intr1)
                .findSerializer(null, null));
        assertSame(serString, new AnnotationIntrospectorPair(nop2, intr1)
                .findSerializer(null, null));
        assertNull(new AnnotationIntrospectorPair(NO_ANNOTATIONS, nop2)
                .findSerializer(null, null));
        assertNull(new AnnotationIntrospectorPair(nop2, NO_ANNOTATIONS)
                .findSerializer(null, null));
    }

    // Provenance: IntrospectorPairTest#testFindSubtypes().
    void testFindSubtypesVpack() {
        NamedType type1 = new NamedType(String.class, "string");
        NamedType type2 = new NamedType(Integer.class, "integer");
        List<NamedType> list1 = Arrays.asList(type1);
        List<NamedType> list2 = Arrays.asList(type2);
        IntrospectorWithMap intr1 = new IntrospectorWithMap().add("findSubtypes", list1);
        IntrospectorWithMap intr2 = new IntrospectorWithMap().add("findSubtypes", list2);

        assertNull(new AnnotationIntrospectorPair(NO_ANNOTATIONS, NO_ANNOTATIONS)
                .findSubtypes(null, null));
        assertEquals(list1, new AnnotationIntrospectorPair(intr1, NO_ANNOTATIONS)
                .findSubtypes(null, null));
        assertEquals(list2, new AnnotationIntrospectorPair(NO_ANNOTATIONS, intr2)
                .findSubtypes(null, null));

        List<NamedType> merged = new AnnotationIntrospectorPair(intr1, intr2)
                .findSubtypes(null, null);
        assertEquals(2, merged.size());
        assertEquals(type1, merged.get(0));
        assertEquals(type2, merged.get(1));

        List<NamedType> mergedReverse = new AnnotationIntrospectorPair(intr2, intr1)
                .findSubtypes(null, null);
        assertEquals(2, mergedReverse.size());
        assertEquals(type2, mergedReverse.get(0));
        assertEquals(type1, mergedReverse.get(1));
    }

    // Provenance: IntrospectorPairTest#testFindTypeName().
    void testFindTypeNameVpack() {
        IntrospectorWithMap intr1 = new IntrospectorWithMap().add("findTypeName", "type1");
        IntrospectorWithMap intr2 = new IntrospectorWithMap().add("findTypeName", "type2");
        assertNull(new AnnotationIntrospectorPair(NO_ANNOTATIONS, NO_ANNOTATIONS)
                .findTypeName(null, null));
        assertEquals("type1", new AnnotationIntrospectorPair(intr1, intr2)
                .findTypeName(null, null));
        assertEquals("type2", new AnnotationIntrospectorPair(intr2, intr1)
                .findTypeName(null, null));
    }
private static final class IntrospectorWithMap extends AnnotationIntrospector {
        private final Map<String, Object> values = new HashMap<>();

        IntrospectorWithMap add(String key, Object value) {
            values.put(key, value);
            return this;
        }

        @Override
        public Version version() {
            return Version.unknownVersion();
        }

        @Override
        public PropertyName findRootName(MapperConfig<?> config,
                AnnotatedClass ac) {
            return (PropertyName) values.get("findRootName");
        }

        @Override
        public Object findFilterId(MapperConfig<?> config, Annotated ann) {
            return values.get("findFilterId");
        }

        @Override
        public Object findNamingStrategy(MapperConfig<?> config, AnnotatedClass ac) {
            return values.get("findNamingStrategy");
        }

        @Override
        public Object findSerializer(MapperConfig<?> config, Annotated ann) {
            return values.get("findSerializer");
        }

        @Override
        public Object findKeySerializer(MapperConfig<?> config, Annotated ann) {
            return values.get("findKeySerializer");
        }

        @Override
        public Object findNullSerializer(MapperConfig<?> config, Annotated ann) {
            return values.get("findNullSerializer");
        }

        @Override
        public JsonSerialize.Typing findSerializationTyping(MapperConfig<?> config,
                Annotated ann) {
            return (JsonSerialize.Typing) values.get("findSerializationTyping");
        }

        @Override
        public JavaType findPolymorphicBaseType(MapperConfig<?> config, AnnotatedClass ac,
                JsonTypeInfo.Value typeInfo, JavaType assumedBaseType) {
            return (JavaType) values.get("findPolymorphicBaseType");
        }

        @Override
        @SuppressWarnings("unchecked")
        public List<NamedType> findSubtypes(MapperConfig<?> config, Annotated ann) {
            return (List<NamedType>) values.get("findSubtypes");
        }

        @Override
        public String findTypeName(MapperConfig<?> config, AnnotatedClass ac) {
            return (String) values.get("findTypeName");
        }

        @Override
        public String[] findEnumValues(MapperConfig<?> config, AnnotatedClass ac,
                Enum<?>[] enumValues, String[] names) {
            String[] overrides = (String[]) values.get("findEnumValues");
            if (overrides != null) {
                for (int i = 0; i < overrides.length; ++i) {
                    if (overrides[i] != null) {
                        names[i] = overrides[i];
                    }
                }
            }
            return names;
        }

        @Override
        public Object findKeyDeserializer(MapperConfig<?> config, Annotated ann) {
            return values.get("findKeyDeserializer");
        }
    }

    void __invoke_testFindEnumValuesVpack() throws Exception {
        try {
            testFindEnumValuesVpack();
        } finally {
        }
    }


    void __invoke_testFindFilterIdVpack() throws Exception {
        try {
            testFindFilterIdVpack();
        } finally {
        }
    }


    void __invoke_testFindKeyDeserializerVpack() throws Exception {
        try {
            testFindKeyDeserializerVpack();
        } finally {
        }
    }


    void __invoke_testFindKeySerializerVpack() throws Exception {
        try {
            testFindKeySerializerVpack();
        } finally {
        }
    }


    void __invoke_testFindNamingStrategyVpack() throws Exception {
        try {
            testFindNamingStrategyVpack();
        } finally {
        }
    }


    void __invoke_testFindNullSerializerVpack() throws Exception {
        try {
            testFindNullSerializerVpack();
        } finally {
        }
    }


    void __invoke_testFindPolymorphicBaseTypeVpack() throws Exception {
        try {
            testFindPolymorphicBaseTypeVpack();
        } finally {
        }
    }


    void __invoke_testFindRootNameVpack() throws Exception {
        try {
            testFindRootNameVpack();
        } finally {
        }
    }


    void __invoke_testFindSerializationTypingVpack() throws Exception {
        try {
            testFindSerializationTypingVpack();
        } finally {
        }
    }


    void __invoke_testFindSerializerVpack() throws Exception {
        try {
            testFindSerializerVpack();
        } finally {
        }
    }


    void __invoke_testFindSubtypesVpack() throws Exception {
        try {
            testFindSubtypesVpack();
        } finally {
        }
    }


    void __invoke_testFindTypeNameVpack() throws Exception {
        try {
            testFindTypeNameVpack();
        } finally {
        }
    }

}
