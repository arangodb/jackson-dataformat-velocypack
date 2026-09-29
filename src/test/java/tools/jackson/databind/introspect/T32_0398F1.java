package tools.jackson.databind.introspect;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.PropertyName;
import tools.jackson.databind.SerializationConfig;
import tools.jackson.databind.cfg.MapperConfig;
import tools.jackson.databind.introspect.AnnotatedClass;
import tools.jackson.databind.introspect.AnnotatedField;
import tools.jackson.databind.introspect.AnnotatedClassResolver;
import tools.jackson.databind.introspect.JacksonAnnotationIntrospector;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0398F1 {
private static final byte[] INT_ENABLED = VPackWireFixtureTest.hex(
            "0b 0e 01 47 65 6e 61 62 6c 65 64 28 0c 03");
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] ATOMIC_TRUE = VPackWireFixtureTest.hex(
            "0b 0c 01 46 61 74 6f 6d 69 63 1a 03");
private static final byte[] IS_ENABLED_TRUE = VPackWireFixtureTest.hex(
            "0b 0f 01 49 69 73 45 6e 61 62 6c 65 64 1a 03");
private static final byte[] VALUE1 = VPackWireFixtureTest.hex(
            "46 76 61 6c 75 65 31");

    // Provenance: JacksonAnnotationIntrospectorTest#testEnumHandling().
    void testEnumHandlingVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .annotationIntrospector(new LcEnumIntrospector())
                .build();
        assertArrayEquals(VALUE1, mapper.writeValueAsBytes(EnumExample.VALUE1));
        assertEquals(EnumExample.VALUE1, mapper.readValue(VALUE1, EnumExample.class));
    }

    // Provenance: JacksonAnnotationIntrospectorTest#testFindPolymorphicBaseTypeNoAnnotation().
    void testFindPolymorphicBaseTypeNoAnnotationVpack() {
        ObjectMapper mapper = new VPackMapper();
        JacksonAnnotationIntrospector introspector = new JacksonAnnotationIntrospector();
        SerializationConfig config = mapper.serializationConfig();

        JavaType type = mapper.constructType(UnannotatedSub.class);
        AnnotatedClass ac = AnnotatedClassResolver.resolve(config, type, config);
        JavaType result = introspector.findPolymorphicBaseType(config, ac, null, type);
        assertNull(result);
    }

    // Provenance: JacksonAnnotationIntrospectorTest#testFindPolymorphicBaseTypeOnAnnotatedClassItself().
    void testFindPolymorphicBaseTypeOnAnnotatedClassItselfVpack() {
        ObjectMapper mapper = new VPackMapper();
        JacksonAnnotationIntrospector introspector = new JacksonAnnotationIntrospector();
        SerializationConfig config = mapper.serializationConfig();

        JavaType type = mapper.constructType(AnnotatedBase.class);
        AnnotatedClass ac = AnnotatedClassResolver.resolve(config, type, config);
        JavaType result = introspector.findPolymorphicBaseType(config, ac, null, type);
        assertNull(result);
    }

    // Provenance: JacksonAnnotationIntrospectorTest#testFindPolymorphicBaseTypeWithAnnotatedInterface().
    void testFindPolymorphicBaseTypeWithAnnotatedInterfaceVpack() {
        ObjectMapper mapper = new VPackMapper();
        JacksonAnnotationIntrospector introspector = new JacksonAnnotationIntrospector();
        SerializationConfig config = mapper.serializationConfig();

        JavaType type = mapper.constructType(ImplOfAnnotatedIface.class);
        AnnotatedClass ac = AnnotatedClassResolver.resolve(config, type, config);
        JavaType result = introspector.findPolymorphicBaseType(config, ac, null, type);
        assertNotNull(result);
        assertEquals(AnnotatedIface.class, result.getRawClass());
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

    void __invoke_testEnumHandlingVpack() throws Exception {
        try {
            testEnumHandlingVpack();
        } finally {
        }
    }


    void __invoke_testFindPolymorphicBaseTypeNoAnnotationVpack() throws Exception {
        try {
            testFindPolymorphicBaseTypeNoAnnotationVpack();
        } finally {
        }
    }


    void __invoke_testFindPolymorphicBaseTypeOnAnnotatedClassItselfVpack() throws Exception {
        try {
            testFindPolymorphicBaseTypeOnAnnotatedClassItselfVpack();
        } finally {
        }
    }


    void __invoke_testFindPolymorphicBaseTypeWithAnnotatedInterfaceVpack() throws Exception {
        try {
            testFindPolymorphicBaseTypeWithAnnotatedInterfaceVpack();
        } finally {
        }
    }

}
