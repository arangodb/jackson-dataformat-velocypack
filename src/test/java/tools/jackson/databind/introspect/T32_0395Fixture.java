package tools.jackson.databind.introspect;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import tools.jackson.core.Version;
import tools.jackson.databind.AnnotationIntrospector;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.cfg.MapperConfig;
import tools.jackson.databind.introspect.Annotated;
import tools.jackson.databind.introspect.AnnotatedClass;
import tools.jackson.databind.introspect.AnnotationIntrospectorPair;
import tools.jackson.databind.introspect.VisibilityChecker;
import tools.jackson.databind.deser.jdk.NumberDeserializers;
import tools.jackson.databind.deser.jdk.StringDeserializer;
import tools.jackson.databind.ser.jdk.StringSerializer;
import tools.jackson.databind.ser.std.ToStringSerializer;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class T32_0395Fixture {
private static final AnnotationIntrospector NO_ANNOTATIONS =
            AnnotationIntrospector.nopInstance();

    // Provenance: IntrospectorPairTest#testCreate().
    void testCreateVpack() {
        assertNotNull(AnnotationIntrospectorPair.create(NO_ANNOTATIONS, NO_ANNOTATIONS));
        assertNotNull(AnnotationIntrospectorPair.create(NO_ANNOTATIONS, null));
        assertNotNull(AnnotationIntrospectorPair.create(null, NO_ANNOTATIONS));
    }

    // Provenance: IntrospectorPairTest#testAccess().
    void testAccessVpack() {
        IntrospectorWithMap intr1 = new IntrospectorWithMap();
        AnnotationIntrospectorPair pair = new AnnotationIntrospectorPair(intr1,
                NO_ANNOTATIONS);
        Collection<AnnotationIntrospector> intrs = pair.allIntrospectors();
        assertEquals(2, intrs.size());
        Iterator<AnnotationIntrospector> it = intrs.iterator();
        assertSame(intr1, it.next());
        assertSame(NO_ANNOTATIONS, it.next());
    }

    // Provenance: IntrospectorPairTest#testAnnotationBundle().
    void testAnnotationBundleVpack() {
        IntrospectorWithMap isBundle = new IntrospectorWithMap()
                .add("isAnnotationBundle", true);
        assertTrue(new AnnotationIntrospectorPair(NO_ANNOTATIONS, isBundle)
                .isAnnotationBundle(null));
        assertTrue(new AnnotationIntrospectorPair(isBundle, NO_ANNOTATIONS)
                .isAnnotationBundle(null));
        assertFalse(new AnnotationIntrospectorPair(NO_ANNOTATIONS, NO_ANNOTATIONS)
                .isAnnotationBundle(null));
    }

    // Provenance: IntrospectorPairTest#testFindClassDescription().
    void testFindClassDescriptionVpack() {
        IntrospectorWithMap intr1 = new IntrospectorWithMap()
                .add("findClassDescription", "Desc1");
        IntrospectorWithMap intr2 = new IntrospectorWithMap()
                .add("findClassDescription", "Desc2");
        assertNull(new AnnotationIntrospectorPair(NO_ANNOTATIONS, NO_ANNOTATIONS)
                .findClassDescription(null, null));
        assertEquals("Desc1",
                new AnnotationIntrospectorPair(intr1, intr2)
                        .findClassDescription(null, null));
        assertEquals("Desc2",
                new AnnotationIntrospectorPair(intr2, intr1)
                        .findClassDescription(null, null));
    }

    // Provenance: IntrospectorPairTest#testFindContentSerializer().
    void testFindContentSerializerVpack() {
        final ValueSerializer<?> serString = new StringSerializer();
        final ValueSerializer<?> serToString = ToStringSerializer.instance;

        IntrospectorWithMap intr1 = new IntrospectorWithMap()
                .add("findContentSerializer", serString);
        IntrospectorWithMap intr2 = new IntrospectorWithMap()
                .add("findContentSerializer", serToString);
        IntrospectorWithMap nop2 = new IntrospectorWithMap()
                .add("findContentSerializer", ValueSerializer.None.class);

        assertSame(serString,
                new AnnotationIntrospectorPair(intr1, intr2)
                        .findContentSerializer(null, null));
        assertSame(serToString,
                new AnnotationIntrospectorPair(intr2, intr1)
                        .findContentSerializer(null, null));
        assertSame(serString,
                new AnnotationIntrospectorPair(NO_ANNOTATIONS, intr1)
                        .findContentSerializer(null, null));
        assertSame(serString,
                new AnnotationIntrospectorPair(nop2, intr1)
                        .findContentSerializer(null, null));
        assertNull(new AnnotationIntrospectorPair(NO_ANNOTATIONS, nop2)
                .findContentSerializer(null, null));
        assertNull(new AnnotationIntrospectorPair(nop2, NO_ANNOTATIONS)
                .findContentSerializer(null, null));
    }

    // Provenance: IntrospectorPairTest#testFindApplyView().
    void testFindApplyViewVpack() {
        IntrospectorWithMap intr1 = new IntrospectorWithMap()
                .add("findApplyView", Integer.class);
        IntrospectorWithMap intr2 = new IntrospectorWithMap()
                .add("findApplyView", String.class);

        assertNull(new AnnotationIntrospectorPair(NO_ANNOTATIONS, NO_ANNOTATIONS)
                .findApplyView(null, null));
        assertEquals(Integer.class,
                new AnnotationIntrospectorPair(intr1, intr2)
                        .findApplyView(null, null));
        assertEquals(String.class,
                new AnnotationIntrospectorPair(intr2, intr1)
                        .findApplyView(null, null));
        assertEquals(Integer.class,
                new AnnotationIntrospectorPair(NO_ANNOTATIONS, intr1)
                        .findApplyView(null, null));
        assertEquals(String.class,
                new AnnotationIntrospectorPair(NO_ANNOTATIONS, intr2)
                        .findApplyView(null, null));
    }

    // Provenance: IntrospectorPairTest#testFindDeserializer().
    void testFindDeserializerVpack() {
        final ValueDeserializer<?> deserString = StringDeserializer.instance;
        final ValueDeserializer<?> deserBoolean = NumberDeserializers.find(Boolean.TYPE);

        AnnotationIntrospector intr1 = new IntrospectorWithHandlers(deserString);
        AnnotationIntrospector intr2 = new IntrospectorWithHandlers(deserBoolean);
        AnnotationIntrospector nop2 = new IntrospectorWithHandlers(ValueDeserializer.None.class);

        assertSame(deserString,
                new AnnotationIntrospectorPair(intr1, intr2)
                        .findDeserializer(null, null));
        assertSame(deserBoolean,
                new AnnotationIntrospectorPair(intr2, intr1)
                        .findDeserializer(null, null));
        assertSame(deserString,
                new AnnotationIntrospectorPair(NO_ANNOTATIONS, intr1)
                        .findDeserializer(null, null));
        assertSame(deserString,
                new AnnotationIntrospectorPair(nop2, intr1)
                        .findDeserializer(null, null));
        assertNull(new AnnotationIntrospectorPair(NO_ANNOTATIONS, nop2)
                .findDeserializer(null, null));
        assertNull(new AnnotationIntrospectorPair(nop2, NO_ANNOTATIONS)
                .findDeserializer(null, null));
    }

    // Provenance: IntrospectorPairTest#testFindContentDeserializer().
    void testFindContentDeserializerVpack() {
        final ValueDeserializer<?> deserString = StringDeserializer.instance;
        final ValueDeserializer<?> deserBoolean = NumberDeserializers.find(Boolean.TYPE);

        IntrospectorWithMap intr1 = new IntrospectorWithMap()
                .add("findContentDeserializer", deserString);
        IntrospectorWithMap intr2 = new IntrospectorWithMap()
                .add("findContentDeserializer", deserBoolean);
        IntrospectorWithMap nop2 = new IntrospectorWithMap()
                .add("findContentDeserializer", ValueDeserializer.None.class);

        assertSame(deserString,
                new AnnotationIntrospectorPair(intr1, intr2)
                        .findContentDeserializer(null, null));
        assertSame(deserBoolean,
                new AnnotationIntrospectorPair(intr2, intr1)
                        .findContentDeserializer(null, null));
        assertSame(deserString,
                new AnnotationIntrospectorPair(NO_ANNOTATIONS, intr1)
                        .findContentDeserializer(null, null));
        assertSame(deserString,
                new AnnotationIntrospectorPair(nop2, intr1)
                        .findContentDeserializer(null, null));
        assertNull(new AnnotationIntrospectorPair(NO_ANNOTATIONS, nop2)
                .findContentDeserializer(null, null));
        assertNull(new AnnotationIntrospectorPair(nop2, NO_ANNOTATIONS)
                .findContentDeserializer(null, null));
    }

    // Provenance: IntrospectorPairTest#testFindAutoDetectVisibility().
    void testFindAutoDetectVisibilityVpack() {
        VisibilityChecker vc = VisibilityChecker.defaultInstance();
        IntrospectorWithMap intr1 = new IntrospectorWithMap()
                .add("findAutoDetectVisibility", vc);
        assertNull(new AnnotationIntrospectorPair(NO_ANNOTATIONS, NO_ANNOTATIONS)
                .findAutoDetectVisibility(null, null, null));
        assertSame(vc, new AnnotationIntrospectorPair(intr1, NO_ANNOTATIONS)
                .findAutoDetectVisibility(null, null, null));
        assertSame(vc, new AnnotationIntrospectorPair(NO_ANNOTATIONS, intr1)
                .findAutoDetectVisibility(null, null, null));
    }

    // Provenance: IntrospectorPairTest#testFindEnumAliases().
    void testFindEnumAliasesVpack() {
        IntrospectorWithMap intr1 = new IntrospectorWithMap()
                .add("findEnumAliases", new String[][] {
                        new String[] { "p_alias1" }, null, null });
        IntrospectorWithMap intr2 = new IntrospectorWithMap()
                .add("findEnumAliases", new String[][] {
                        new String[] { "s_alias1" }, new String[] { "s_alias2" }, null });

        String[][] aliases = new String[3][];
        new AnnotationIntrospectorPair(NO_ANNOTATIONS, NO_ANNOTATIONS)
                .findEnumAliases(null, null, null, aliases);
        assertNull(aliases[0]);
        assertNull(aliases[1]);
        assertNull(aliases[2]);

        aliases = new String[3][];
        new AnnotationIntrospectorPair(intr1, intr2)
                .findEnumAliases(null, null, null, aliases);
        assertArrayEquals(new String[] { "p_alias1" }, aliases[0]);
        assertArrayEquals(new String[] { "s_alias2" }, aliases[1]);
        assertNull(aliases[2]);

        aliases = new String[3][];
        new AnnotationIntrospectorPair(intr2, intr1)
                .findEnumAliases(null, null, null, aliases);
        assertArrayEquals(new String[] { "s_alias1" }, aliases[0]);
        assertArrayEquals(new String[] { "s_alias2" }, aliases[1]);
        assertNull(aliases[2]);
    }

    // Provenance: IntrospectorPairTest#testFindDefaultEnumValue().
    void testFindDefaultEnumValueVpack() {
        IntrospectorWithMap intr1 = new IntrospectorWithMap()
                .add("findDefaultEnumValue", SimpleEnum.ONE);
        IntrospectorWithMap intr2 = new IntrospectorWithMap()
                .add("findDefaultEnumValue", SimpleEnum.TWO);

        assertNull(new AnnotationIntrospectorPair(NO_ANNOTATIONS, NO_ANNOTATIONS)
                .findDefaultEnumValue(null, null, null));
        assertEquals(SimpleEnum.ONE,
                new AnnotationIntrospectorPair(intr1, intr2)
                        .findDefaultEnumValue(null, null, null));
        assertEquals(SimpleEnum.TWO,
                new AnnotationIntrospectorPair(intr2, intr1)
                        .findDefaultEnumValue(null, null, null));
        assertEquals(SimpleEnum.ONE,
                new AnnotationIntrospectorPair(NO_ANNOTATIONS, intr1)
                        .findDefaultEnumValue(null, null, null));
        assertEquals(SimpleEnum.TWO,
                new AnnotationIntrospectorPair(NO_ANNOTATIONS, intr2)
                        .findDefaultEnumValue(null, null, null));
    }

    // Provenance: IntrospectorPairTest#testFindEnumNamingStrategy().
    void testFindEnumNamingStrategyVpack() {
        IntrospectorWithMap intr1 = new IntrospectorWithMap()
                .add("findEnumNamingStrategy", Integer.class);
        IntrospectorWithMap intr2 = new IntrospectorWithMap()
                .add("findEnumNamingStrategy", String.class);

        assertNull(new AnnotationIntrospectorPair(NO_ANNOTATIONS, NO_ANNOTATIONS)
                .findEnumNamingStrategy(null, null));
        assertEquals(Integer.class,
                new AnnotationIntrospectorPair(intr1, intr2)
                        .findEnumNamingStrategy(null, null));
        assertEquals(String.class,
                new AnnotationIntrospectorPair(intr2, intr1)
                        .findEnumNamingStrategy(null, null));
        assertEquals(Integer.class,
                new AnnotationIntrospectorPair(NO_ANNOTATIONS, intr1)
                        .findEnumNamingStrategy(null, null));
        assertEquals(String.class,
                new AnnotationIntrospectorPair(NO_ANNOTATIONS, intr2)
                        .findEnumNamingStrategy(null, null));
    }
private static final class IntrospectorWithHandlers extends AnnotationIntrospector {
        private final Object deserializer;

        IntrospectorWithHandlers(Object deserializer) {
            this.deserializer = deserializer;
        }

        @Override
        public Version version() {
            return Version.unknownVersion();
        }

        @Override
        public Object findDeserializer(MapperConfig<?> config, Annotated annotated) {
            return deserializer;
        }
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
        public boolean isAnnotationBundle(Annotation ann) {
            return Boolean.TRUE.equals(values.get("isAnnotationBundle"));
        }

        @Override
        public String findClassDescription(MapperConfig<?> config, AnnotatedClass ac) {
            return (String) values.get("findClassDescription");
        }

        @Override
        public Object findContentSerializer(MapperConfig<?> config, Annotated annotated) {
            return values.get("findContentSerializer");
        }

        @Override
        public Class<?> findApplyView(MapperConfig<?> config, Annotated annotated) {
            return (Class<?>) values.get("findApplyView");
        }

        @Override
        public VisibilityChecker findAutoDetectVisibility(MapperConfig<?> config,
                AnnotatedClass ac, VisibilityChecker checker) {
            VisibilityChecker result = (VisibilityChecker) values.get("findAutoDetectVisibility");
            return result == null ? checker : result;
        }

        @Override
        public void findEnumAliases(MapperConfig<?> config, AnnotatedClass annotatedClass,
                Enum<?>[] enumValues, String[][] aliases) {
            String[][] overrides = (String[][]) values.get("findEnumAliases");
            if (overrides == null) {
                return;
            }
            for (int i = 0; i < overrides.length && i < aliases.length; ++i) {
                if (overrides[i] != null) {
                    aliases[i] = overrides[i];
                }
            }
        }

        @Override
        public Enum<?> findDefaultEnumValue(MapperConfig<?> config,
                AnnotatedClass ac, Enum<?>[] enumValues) {
            return (Enum<?>) values.get("findDefaultEnumValue");
        }

        @Override
        public Object findEnumNamingStrategy(MapperConfig<?> config, AnnotatedClass ac) {
            return values.get("findEnumNamingStrategy");
        }

        @Override
        public Object findContentDeserializer(MapperConfig<?> config, Annotated annotated) {
            return values.get("findContentDeserializer");
        }
    }
private enum SimpleEnum { ONE, TWO }

    void __invoke_testCreateVpack() throws Exception {
        try {
            testCreateVpack();
        } finally {
        }
    }


    void __invoke_testAccessVpack() throws Exception {
        try {
            testAccessVpack();
        } finally {
        }
    }


    void __invoke_testAnnotationBundleVpack() throws Exception {
        try {
            testAnnotationBundleVpack();
        } finally {
        }
    }


    void __invoke_testFindClassDescriptionVpack() throws Exception {
        try {
            testFindClassDescriptionVpack();
        } finally {
        }
    }


    void __invoke_testFindContentSerializerVpack() throws Exception {
        try {
            testFindContentSerializerVpack();
        } finally {
        }
    }


    void __invoke_testFindApplyViewVpack() throws Exception {
        try {
            testFindApplyViewVpack();
        } finally {
        }
    }


    void __invoke_testFindDeserializerVpack() throws Exception {
        try {
            testFindDeserializerVpack();
        } finally {
        }
    }


    void __invoke_testFindContentDeserializerVpack() throws Exception {
        try {
            testFindContentDeserializerVpack();
        } finally {
        }
    }


    void __invoke_testFindAutoDetectVisibilityVpack() throws Exception {
        try {
            testFindAutoDetectVisibilityVpack();
        } finally {
        }
    }


    void __invoke_testFindEnumAliasesVpack() throws Exception {
        try {
            testFindEnumAliasesVpack();
        } finally {
        }
    }


    void __invoke_testFindDefaultEnumValueVpack() throws Exception {
        try {
            testFindDefaultEnumValueVpack();
        } finally {
        }
    }


    void __invoke_testFindEnumNamingStrategyVpack() throws Exception {
        try {
            testFindEnumNamingStrategyVpack();
        } finally {
        }
    }

}
