package tools.jackson.databind.introspect;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.PropertyName;
import tools.jackson.databind.cfg.MapperConfig;
import tools.jackson.databind.introspect.AnnotatedClass;
import tools.jackson.databind.introspect.AnnotatedField;
import tools.jackson.databind.introspect.JacksonAnnotationIntrospector;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0398F0 {
private static final byte[] INT_ENABLED = VPackWireFixtureTest.hex(
            "0b 0e 01 47 65 6e 61 62 6c 65 64 28 0c 03");
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] ATOMIC_TRUE = VPackWireFixtureTest.hex(
            "0b 0c 01 46 61 74 6f 6d 69 63 1a 03");
private static final byte[] IS_ENABLED_TRUE = VPackWireFixtureTest.hex(
            "0b 0f 01 49 69 73 45 6e 61 62 6c 65 64 1a 03");
private static final byte[] VALUE1 = VPackWireFixtureTest.hex(
            "46 76 61 6c 75 65 31");

    // Provenance: IsGetterBooleanTest#testAllowIntIsGetter().
    void testAllowIntIsGetterVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(MapperFeature.ALLOW_IS_GETTERS_FOR_NON_BOOLEAN)
                .build();

        POJO3609 input = new POJO3609(12);
        assertArrayEquals(INT_ENABLED, mapper.writeValueAsBytes(input));

        Map<?, ?> props = mapper.readValue(INT_ENABLED, Map.class);
        assertEquals(Collections.singletonMap("enabled", 12), props);

        POJO3609 output = mapper.readValue(INT_ENABLED, POJO3609.class);
        assertEquals(input.isEnabled, output.isEnabled);
    }

    // Provenance: IsGetterBooleanTest#testDisallowIntIsGetter().
    void testDisallowIntIsGetterVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .disable(MapperFeature.ALLOW_IS_GETTERS_FOR_NON_BOOLEAN)
                .disable(tools.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .build();

        POJO3609 input = new POJO3609(12);
        assertArrayEquals(EMPTY_OBJECT, mapper.writeValueAsBytes(input));
    }

    // Provenance: IsGetterBooleanTest#testBooleanReference().
    void testBooleanReferenceVpack() throws Exception {
        ObjectMapper mapper = new VPackMapper();
        assertArrayEquals(ATOMIC_TRUE,
                mapper.writeValueAsBytes(new POJO3836_AR()));
    }

    // Provenance: IsGetterBooleanTest#testAtomicBoolean().
    void testAtomicBooleanVpack() throws Exception {
        ObjectMapper mapper = new VPackMapper();
        assertArrayEquals(ATOMIC_TRUE,
                mapper.writeValueAsBytes(new POJO3836_AB()));
    }

    // Provenance: IsGetterBooleanTest#testOptionalBoolean().
    void testOptionalBooleanVpack() throws Exception {
        ObjectMapper mapper = new VPackMapper();
        assertArrayEquals(ATOMIC_TRUE,
                mapper.writeValueAsBytes(new POJO3836_OB()));
    }

    // Provenance: IsGetterBooleanTest#testIsPropertiesStdKotlin().
    void testIsPropertiesStdKotlinVpack() throws Exception {
        ObjectMapper mapper = renamedIsPropertyMapper();
        POJO2527 input = new POJO2527(true);

        assertArrayEquals(IS_ENABLED_TRUE, mapper.writeValueAsBytes(input));
        Map<?, ?> props = mapper.readValue(IS_ENABLED_TRUE, Map.class);
        assertEquals(Collections.singletonMap("isEnabled", Boolean.TRUE), props);

        POJO2527 output = mapper.readValue(IS_ENABLED_TRUE, POJO2527.class);
        assertEquals(input.isEnabled, output.isEnabled);
    }

    // Provenance: IsGetterBooleanTest#testIsPropertiesWithPublicField().
    void testIsPropertiesWithPublicFieldVpack() throws Exception {
        ObjectMapper mapper = renamedIsPropertyMapper();
        POJO2527PublicField input = new POJO2527PublicField(true);

        assertArrayEquals(IS_ENABLED_TRUE, mapper.writeValueAsBytes(input));
        Map<?, ?> props = mapper.readValue(IS_ENABLED_TRUE, Map.class);
        assertEquals(Collections.singletonMap("isEnabled", Boolean.TRUE), props);

        POJO2527PublicField output = mapper.readValue(IS_ENABLED_TRUE,
                POJO2527PublicField.class);
        assertEquals(input.isEnabled, output.isEnabled);
    }

    // Provenance: IsGetterBooleanTest#testIsPropertiesViaCreator().
    void testIsPropertiesViaCreatorVpack() throws Exception {
        ObjectMapper mapper = renamedIsPropertyMapper();
        POJO2527Creator input = new POJO2527Creator(true);

        assertArrayEquals(IS_ENABLED_TRUE, mapper.writeValueAsBytes(input));
        Map<?, ?> props = mapper.readValue(IS_ENABLED_TRUE, Map.class);
        assertEquals(Collections.singletonMap("isEnabled", Boolean.TRUE), props);

        POJO2527Creator output = mapper.readValue(IS_ENABLED_TRUE,
                POJO2527Creator.class);
        assertEquals(input.isEnabled, output.isEnabled);
    }
private static ObjectMapper renamedIsPropertyMapper() {
        return VPackMapper.builder()
                .annotationIntrospector(new IsGetterRenamingIntrospector())
                .disable(tools.jackson.databind.DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
                .build();
    }
static class POJO3609 {
        int isEnabled;

        protected POJO3609() { }
        POJO3609(int b) {
            isEnabled = b;
        }

        public int isEnabled() { return isEnabled; }
        public void setEnabled(int b) { isEnabled = b; }
    }
static class POJO3836_AR {
        public AtomicReference<Boolean> isAtomic() {
            return new AtomicReference<>(true);
        }
    }
static class POJO3836_AB {
        public AtomicBoolean isAtomic() {
            return new AtomicBoolean(true);
        }
    }
static class POJO3836_OB {
        public Optional<Boolean> isAtomic() {
            return Optional.of(true);
        }
    }
static class POJO2527 {
        boolean isEnabled;

        protected POJO2527() { }
        POJO2527(boolean b) {
            isEnabled = b;
        }

        public boolean getEnabled() { return isEnabled; }
        public void setEnabled(boolean b) { isEnabled = b; }
    }
static class POJO2527PublicField {
        public boolean isEnabled;

        protected POJO2527PublicField() { }
        POJO2527PublicField(boolean b) {
            isEnabled = b;
        }

        public boolean getEnabled() { return isEnabled; }
        public void setEnabled(boolean b) { isEnabled = b; }
    }
static class POJO2527Creator {
        boolean isEnabled;

        POJO2527Creator(@JsonProperty("enabled") boolean b) {
            isEnabled = b;
        }

        public boolean getEnabled() { return isEnabled; }
    }
@SuppressWarnings("serial")
    static class IsGetterRenamingIntrospector extends JacksonAnnotationIntrospector {
        @Override
        public PropertyName findRenameByField(MapperConfig<?> config,
                AnnotatedField f, PropertyName implName) {
            final String origSimple = implName.getSimpleName();
            if (origSimple.startsWith("is")) {
                String mangledName = stdManglePropertyName(origSimple, 2);
                if ((mangledName != null) && !mangledName.equals(origSimple)) {
                    return PropertyName.construct(mangledName);
                }
            }
            return null;
        }

        protected String stdManglePropertyName(final String basename, final int offset) {
            final int end = basename.length();
            char c0 = basename.charAt(offset);
            char c1 = Character.toLowerCase(c0);
            if (c0 == c1) {
                return basename.substring(offset);
            }
            if ((offset + 1) < end) {
                if (Character.isUpperCase(basename.charAt(offset + 1))) {
                    return basename.substring(offset);
                }
            }
            StringBuilder sb = new StringBuilder(end - offset);
            sb.append(c1);
            sb.append(basename, offset + 1, end);
            return sb.toString();
        }
    }
enum EnumExample {
        VALUE1;
    }
static class LcEnumIntrospector extends JacksonAnnotationIntrospector {
        @Override
        public String[] findEnumValues(MapperConfig<?> config, AnnotatedClass annotatedClass,
                Enum<?>[] enumValues, String[] names) {
            for (int i = 0; i < enumValues.length; ++i) {
                names[i] = enumValues[i].name().toLowerCase();
            }
            return names;
        }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
    static class AnnotatedBase { }
static class UnannotatedBase { }
static class UnannotatedSub extends UnannotatedBase { }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
    interface AnnotatedIface { }
static class ImplOfAnnotatedIface implements AnnotatedIface { }

    void __invoke_testAllowIntIsGetterVpack() throws Exception {
        try {
            testAllowIntIsGetterVpack();
        } finally {
        }
    }


    void __invoke_testDisallowIntIsGetterVpack() throws Exception {
        try {
            testDisallowIntIsGetterVpack();
        } finally {
        }
    }


    void __invoke_testBooleanReferenceVpack() throws Exception {
        try {
            testBooleanReferenceVpack();
        } finally {
        }
    }


    void __invoke_testAtomicBooleanVpack() throws Exception {
        try {
            testAtomicBooleanVpack();
        } finally {
        }
    }


    void __invoke_testOptionalBooleanVpack() throws Exception {
        try {
            testOptionalBooleanVpack();
        } finally {
        }
    }


    void __invoke_testIsPropertiesStdKotlinVpack() throws Exception {
        try {
            testIsPropertiesStdKotlinVpack();
        } finally {
        }
    }


    void __invoke_testIsPropertiesWithPublicFieldVpack() throws Exception {
        try {
            testIsPropertiesWithPublicFieldVpack();
        } finally {
        }
    }


    void __invoke_testIsPropertiesViaCreatorVpack() throws Exception {
        try {
            testIsPropertiesViaCreatorVpack();
        } finally {
        }
    }

}
