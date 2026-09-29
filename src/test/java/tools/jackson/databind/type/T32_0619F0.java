package tools.jackson.databind.type;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.type.CollectionType;
import tools.jackson.databind.type.MapType;
import tools.jackson.databind.type.TypeBindings;
import tools.jackson.databind.type.TypeFactory;
import tools.jackson.databind.type.TypeModifier;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0619F0 {
private static final VPackMapper MAPPER = new VPackMapper();

    // Provenance: TypeFactoryTest#testSimpleTypes().
    void simpleTypesVpack() {
        Class<?>[] classes = {
                boolean.class, byte.class, char.class, short.class, int.class, long.class,
                float.class, double.class, Boolean.class, Byte.class, Character.class,
                Short.class, Integer.class, Long.class, Float.class, Double.class,
                String.class, Object.class, Calendar.class, Date.class
        };
        for (Class<?> raw : classes) {
            assertSame(raw, MAPPER.getTypeFactory().constructType(raw).getRawClass());
            assertSame(raw, MAPPER.getTypeFactory().constructType(raw).getRawClass());
        }
    }

    // Provenance: TypeFactoryTest#testSneakyBeanProperties().
    void sneakyBeanPropertiesVpack() throws Exception {
        StringLongMapBean mapBean = new StringLongMapBean();
        mapBean.value = new LongValuedMap<>();
        mapBean.value.put("a", 123L);
        StringLongMapBean mapOut = MAPPER.readValue(MAPPER.writeValueAsBytes(mapBean),
                StringLongMapBean.class);
        assertNotNull(mapOut);
        assertEquals(Map.of("a", 123L), mapOut.value);

        StringListBean listBean = new StringListBean();
        listBean.value = new GenericList<>();
        listBean.value.add("...");
        StringListBean listOut = MAPPER.readValue(MAPPER.writeValueAsBytes(listBean),
                StringListBean.class);
        assertNotNull(listOut);
        assertSame(GenericList.class, listOut.value.getClass());
        assertEquals(List.of("..."), listOut.value);
    }

    // Provenance: TypeFactoryTest#testSneakyFieldTypes().
    void sneakyFieldTypesVpack() throws Exception {
        TypeFactory types = MAPPER.getTypeFactory();
        Field field = SneakyBean.class.getDeclaredField("intMap");
        JavaType type = types.constructType(field.getGenericType());
        assertEquals(MapType.class, type.getClass());
        MapType map = (MapType) type;
        assertSame(Integer.class, map.getKeyType().getRawClass());
        assertSame(Long.class, map.getContentType().getRawClass());

        field = SneakyBean.class.getDeclaredField("longList");
        type = types.constructType(field.getGenericType());
        assertTrue(type instanceof CollectionType);
        assertSame(Long.class, type.getContentType().getRawClass());
    }

    // Provenance: TypeFactoryTest#testSneakySelfRefs().
    void sneakySelfRefsVpack() throws Exception {
        // Independently derived 0b object: one key "foobar" with the null marker.
        byte[] literal = { 0x0b, 0x0c, 0x01, 0x46, 0x66, 0x6f, 0x6f, 0x62, 0x61, 0x72,
                0x18, 0x03 };
        Map<?, ?> fromLiteral = MAPPER.readValue(literal, Map.class);
        assertEquals(1, fromLiteral.size());
        assertTrue(fromLiteral.containsKey("foobar"));
        assertNull(fromLiteral.get("foobar"));

        Map<?, ?> fromBean = MAPPER.readValue(MAPPER.writeValueAsBytes(new SneakyBean2()),
                Map.class);
        assertEquals(fromLiteral, fromBean);
    }

    // Provenance: TypeFactoryTest#testTwoParamSneakyCustom().
    void twoParamSneakyCustomVpack() {
        TypeFactory types = MAPPER.getTypeFactory();
        JavaType base = types.constructType(new TypeReference<TwoParam1604<String, List<Long>>>() { });
        assertSame(TwoParam1604.class, base.getRawClass());
        assertSame(String.class, base.containedType(0).getRawClass());
        assertSame(Long.class, base.containedType(1).getContentType().getRawClass());

        JavaType subtype = types.constructSpecializedType(base, SneakyTwoParam1604.class);
        assertSame(SneakyTwoParam1604.class, subtype.getRawClass());
        assertSame(TwoParam1604.class, subtype.getSuperClass().getRawClass());
        assertEquals(2, subtype.containedTypeCount());
        assertSame(Long.class, subtype.containedType(0).getRawClass());
        assertSame(String.class, subtype.containedType(1).getRawClass());
    }

    // Provenance: TypeFactoryTest#testTypeGeneralization().
    void typeGeneralizationVpack() {
        TypeFactory types = MAPPER.getTypeFactory();
        MapType concrete = types.constructMapType(HashMap.class, String.class, Long.class);
        JavaType generalized = types.constructGeneralizedType(concrete, Map.class);
        assertSame(String.class, generalized.getKeyType().getRawClass());
        assertSame(Long.class, generalized.getContentType().getRawClass());
        assertSame(concrete, types.constructGeneralizedType(concrete, HashMap.class));
        IllegalArgumentException failure = assertThrows(IllegalArgumentException.class,
                () -> types.constructGeneralizedType(concrete, java.util.TreeMap.class));
        assertTrue(failure.getMessage().contains("not a super-type of"));
    }
private static TypeModifier noopModifier() {
        return new TypeModifier() {
            @Override
            public JavaType modifyType(JavaType type, Type jdkType, TypeBindings context,
                    TypeFactory typeFactory) {
                return type;
            }
        };
    }
private static final class ClassLoaderProbe extends ClassLoader {
        private String requested;

        ClassLoaderProbe(ClassLoader parent) {
            super(parent);
        }

        @Override
        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            requested = name;
            return super.loadClass(name, resolve);
        }

        boolean saw(String name) {
            return name.equals(requested);
        }
    }
public static class AClass { }
static class IntLongMap extends XLongMap<Integer> { }
static class XLongMap<K> extends XXMap<K, Long> { }
static class XXMap<K, V> extends HashMap<K, V> { }
static class MyList extends ArrayList<Long> { }
static class SneakyBean { public IntLongMap intMap; public MyList longList; }
static class LongValuedMap<K> extends HashMap<K, Long> { }
static class GenericList<T> extends ArrayList<T> { }
static class StringLongMapBean { public LongValuedMap<String> value; }
static class StringListBean { public GenericList<String> value; }
static class SneakyBean2 { public <T extends Comparable<T>> T getFoobar() { return null; } }
static class TwoParam1604<K, V> { }
static class SneakyTwoParam1604<V, K> extends TwoParam1604<K, List<V>> { }

    void __invoke_simpleTypesVpack() throws Exception {
        try {
            simpleTypesVpack();
        } finally {
        }
    }


    void __invoke_sneakyBeanPropertiesVpack() throws Exception {
        try {
            sneakyBeanPropertiesVpack();
        } finally {
        }
    }


    void __invoke_sneakyFieldTypesVpack() throws Exception {
        try {
            sneakyFieldTypesVpack();
        } finally {
        }
    }


    void __invoke_sneakySelfRefsVpack() throws Exception {
        try {
            sneakySelfRefsVpack();
        } finally {
        }
    }


    void __invoke_twoParamSneakyCustomVpack() throws Exception {
        try {
            twoParamSneakyCustomVpack();
        } finally {
        }
    }


    void __invoke_typeGeneralizationVpack() throws Exception {
        try {
            typeGeneralizationVpack();
        } finally {
        }
    }

}
