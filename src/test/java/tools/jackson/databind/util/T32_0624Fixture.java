package tools.jackson.databind.util;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.LinkedHashMap;

import tools.jackson.databind.util.ClassUtil;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class T32_0624Fixture {

    // Provenance: ClassUtilTest#testFailedCreateInstance().
    void classUtilFailedCreateInstanceVpack() {
        IllegalArgumentException noDefault = assertThrows(IllegalArgumentException.class,
                () -> ClassUtil.createInstance(BaseClass.class, true));
        assertTrue(noDefault.getMessage().contains("has no default"));

        IllegalArgumentException inaccessible = assertThrows(IllegalArgumentException.class,
                () -> ClassUtil.createInstance(ThrowingConstructor.class, false));
        assertTrue(inaccessible.getMessage().contains("is not accessible"));

        IllegalStateException thrown = assertThrows(IllegalStateException.class,
                () -> ClassUtil.createInstance(ThrowingConstructor.class, true));
        assertEquals("test", thrown.getMessage());

    }

    // Provenance: ClassUtilTest#testFindEnumMapTypeJDK().
    void classUtilFindEnumMapTypeJdkVpack() {
        EnumMap<ClassUtilEnum, Integer> values = new EnumMap<>(ClassUtilEnum.class);
        assertEquals(ClassUtilEnum.class, ClassUtil.findEnumType(values));
    }

    // Provenance: ClassUtilTest#testFindEnumSetTypeJDK().
    void classUtilFindEnumSetTypeJdkVpack() {
        assertEquals(ClassUtilEnum.class, ClassUtil.findEnumType(EnumSet.allOf(ClassUtilEnum.class)));
        assertEquals(ClassUtilEnum.class, ClassUtil.findEnumType(EnumSet.noneOf(ClassUtilEnum.class)));
    }

    // Provenance: ClassUtilTest#testFindEnumTypeNonJdk().
    void classUtilFindEnumTypeNonJdkVpack() {
        assertEquals(ClassUtilEnum.class, ClassUtil.findEnumType(ClassUtilEnum.A));
        assertEquals(ClassUtilEnum.class, ClassUtil.findEnumType(ClassUtilEnum.B));
    }

    // Provenance: ClassUtilTest#testGetDeclaringClass().
    void classUtilGetDeclaringClassVpack() {
        assertNull(ClassUtil.getDeclaringClass(String.class));
        assertEquals(T32_0624Fixture.class,
                ClassUtil.getDeclaringClass(BaseClass.class));
    }

    // Provenance: ClassUtilTest#testHasEnclosingMethod().
    void classUtilHasEnclosingMethodVpack() {
        class LocalClass { }
        assertTrue(ClassUtil.hasEnclosingMethod(LocalClass.class));
        assertFalse(ClassUtil.hasEnclosingMethod(String.class));
        assertFalse(ClassUtil.hasEnclosingMethod(getClass()));
    }

    // Provenance: ClassUtilTest#testIsConcrete().
    void classUtilIsConcreteVpack() throws Exception {
        assertTrue(ClassUtil.isConcrete(getClass()));
        assertFalse(ClassUtil.isConcrete(BaseClass.class));
        assertFalse(ClassUtil.isConcrete(Runnable.class));
        assertFalse(ClassUtil.isConcrete(AbstractAndConcreteMethods.class
                .getDeclaredMethod("abstractMethod")));
        assertTrue(ClassUtil.isConcrete(AbstractAndConcreteMethods.class
                .getDeclaredMethod("concreteMethod")));
    }

    // Provenance: ClassUtilTest#testIsXxxType().
    void classUtilIsXxxTypeVpack() {
        assertTrue(ClassUtil.isCollectionMapOrArray(String[].class));
        assertTrue(ClassUtil.isCollectionMapOrArray(ArrayList.class));
        assertTrue(ClassUtil.isCollectionMapOrArray(LinkedHashMap.class));
        assertFalse(ClassUtil.isCollectionMapOrArray(java.net.URL.class));
        assertTrue(ClassUtil.isBogusClass(Void.class));
        assertTrue(ClassUtil.isBogusClass(Void.TYPE));
        assertFalse(ClassUtil.isBogusClass(String.class));
    }

    // Provenance: ClassUtilTest#testJDKChecks().
    void classUtilJdkChecksVpack() {
        int version = ClassUtil.getJDKMajorVersion();
        assertTrue(version > 0);
        assertEquals(version >= 17, ClassUtil.isJDK17OrAbove());
    }

    // Provenance: ClassUtilTest#testNonNullString().
    void classUtilNonNullStringVpack() {
        assertEquals("test", ClassUtil.nonNullString("test"));
        assertEquals("", ClassUtil.nonNullString(null));
        assertEquals("", ClassUtil.nonNullString(""));
    }

    // Provenance: ClassUtilTest#testPrimitiveDefaultValue().
    void classUtilPrimitiveDefaultValueVpack() {
        assertEquals(Integer.valueOf(0), ClassUtil.defaultValue(Integer.TYPE));
        assertEquals(Long.valueOf(0L), ClassUtil.defaultValue(Long.TYPE));
        assertEquals(Character.valueOf('\0'), ClassUtil.defaultValue(Character.TYPE));
        assertEquals(Short.valueOf((short) 0), ClassUtil.defaultValue(Short.TYPE));
        assertEquals(Byte.valueOf((byte) 0), ClassUtil.defaultValue(Byte.TYPE));
        assertEquals(Double.valueOf(0.0), ClassUtil.defaultValue(Double.TYPE));
        assertEquals(Float.valueOf(0.0f), ClassUtil.defaultValue(Float.TYPE));
        assertEquals(Boolean.FALSE, ClassUtil.defaultValue(Boolean.TYPE));
        try {
            ClassUtil.defaultValue(String.class);
        } catch (IllegalArgumentException failure) {
            assertTrue(failure.getMessage().contains("String is not a primitive type"));
        }
    }

    // Provenance: ClassUtilTest#testPrimitiveWrapperType().
    void classUtilPrimitiveWrapperTypeVpack() {
        assertEquals(Byte.class, ClassUtil.wrapperType(Byte.TYPE));
        assertEquals(Short.class, ClassUtil.wrapperType(Short.TYPE));
        assertEquals(Character.class, ClassUtil.wrapperType(Character.TYPE));
        assertEquals(Integer.class, ClassUtil.wrapperType(Integer.TYPE));
        assertEquals(Long.class, ClassUtil.wrapperType(Long.TYPE));
        assertEquals(Double.class, ClassUtil.wrapperType(Double.TYPE));
        assertEquals(Float.class, ClassUtil.wrapperType(Float.TYPE));
        assertEquals(Boolean.class, ClassUtil.wrapperType(Boolean.TYPE));
        assertNull(ClassUtil.wrapperType(String.class));
    }
static abstract class BaseClass implements Comparable<BaseClass> {
        BaseClass(String value) { }

        @Override
        public int compareTo(BaseClass other) { return 0; }
    }
static class ThrowingConstructor {
        protected ThrowingConstructor() {
            throw new IllegalStateException("test");
        }
    }
static abstract class AbstractAndConcreteMethods {
        public abstract void abstractMethod();
        public void concreteMethod() { }
    }
enum ClassUtilEnum {
        A,
        B {
            @Override
            public String toString() { return "ClassUtilEnum{B}"; }
        }
    }

    void __invoke_classUtilFailedCreateInstanceVpack() throws Exception {
        try {
            classUtilFailedCreateInstanceVpack();
        } finally {
        }
    }


    void __invoke_classUtilFindEnumMapTypeJdkVpack() throws Exception {
        try {
            classUtilFindEnumMapTypeJdkVpack();
        } finally {
        }
    }


    void __invoke_classUtilFindEnumSetTypeJdkVpack() throws Exception {
        try {
            classUtilFindEnumSetTypeJdkVpack();
        } finally {
        }
    }


    void __invoke_classUtilFindEnumTypeNonJdkVpack() throws Exception {
        try {
            classUtilFindEnumTypeNonJdkVpack();
        } finally {
        }
    }


    void __invoke_classUtilGetDeclaringClassVpack() throws Exception {
        try {
            classUtilGetDeclaringClassVpack();
        } finally {
        }
    }


    void __invoke_classUtilHasEnclosingMethodVpack() throws Exception {
        try {
            classUtilHasEnclosingMethodVpack();
        } finally {
        }
    }


    void __invoke_classUtilIsConcreteVpack() throws Exception {
        try {
            classUtilIsConcreteVpack();
        } finally {
        }
    }


    void __invoke_classUtilIsXxxTypeVpack() throws Exception {
        try {
            classUtilIsXxxTypeVpack();
        } finally {
        }
    }


    void __invoke_classUtilJdkChecksVpack() throws Exception {
        try {
            classUtilJdkChecksVpack();
        } finally {
        }
    }


    void __invoke_classUtilNonNullStringVpack() throws Exception {
        try {
            classUtilNonNullStringVpack();
        } finally {
        }
    }


    void __invoke_classUtilPrimitiveDefaultValueVpack() throws Exception {
        try {
            classUtilPrimitiveDefaultValueVpack();
        } finally {
        }
    }


    void __invoke_classUtilPrimitiveWrapperTypeVpack() throws Exception {
        try {
            classUtilPrimitiveWrapperTypeVpack();
        } finally {
        }
    }

}
