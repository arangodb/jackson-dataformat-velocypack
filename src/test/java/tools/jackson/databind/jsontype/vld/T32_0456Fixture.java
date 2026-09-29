package tools.jackson.databind.jsontype.vld;

import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.DatabindContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.InvalidTypeIdException;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0456Fixture {

    // Provenance: ValidatePolymSubTypeTest#testWithAnnotationNameAccept().
    void testWithAnnotationNameAcceptVpack() throws Exception {
        AnnotatedWrapper result = nameMapper().readValue(
                annotatedValue(GoodValue.class, false), AnnotatedWrapper.class);
        assertGood(result.value);
    }

    // Provenance: ValidatePolymSubTypeTest#testWithAnnotationNameDenyExplicit().
    void testWithAnnotationNameDenyExplicitVpack() {
        assertDenied(nameMapper(), annotatedValue(BadValue.class, false), AnnotatedWrapper.class);
    }

    // Provenance: ValidatePolymSubTypeTest#testWithAnnotationNameDenyDefault().
    void testWithAnnotationNameDenyDefaultVpack() {
        assertDenied(nameMapper(), annotatedValue(MehValue.class, false), AnnotatedWrapper.class);
    }

    // Provenance: ValidatePolymSubTypeTest#testWithAnnotationClassAccept().
    void testWithAnnotationClassAcceptVpack() throws Exception {
        AnnotatedWrapper result = classMapper().readValue(
                annotatedValue(GoodValue.class, false), AnnotatedWrapper.class);
        assertGood(result.value);
    }

    // Provenance: ValidatePolymSubTypeTest#testWithAnnotationClassDenyExplicit().
    void testWithAnnotationClassDenyExplicitVpack() {
        assertDenied(classMapper(), annotatedValue(BadValue.class, false), AnnotatedWrapper.class);
    }

    // Provenance: ValidatePolymSubTypeTest#testWithAnnotationClassDenyDefault().
    void testWithAnnotationClassDenyDefaultVpack() {
        assertDenied(classMapper(), annotatedValue(MehValue.class, false), AnnotatedWrapper.class);
    }

    // Provenance: ValidatePolymSubTypeTest#testWithAnnotationMinClassNameAccept().
    void testWithAnnotationMinClassNameAcceptVpack() throws Exception {
        AnnotatedMinimalWrapper result = nameMapper().readValue(
                annotatedValue(GoodValue.class, true), AnnotatedMinimalWrapper.class);
        assertGood(result.value);
    }

    // Provenance: ValidatePolymSubTypeTest#testWithAnnotationMinClassNameDenyExplicit().
    void testWithAnnotationMinClassNameDenyExplicitVpack() {
        assertDenied(nameMapper(), annotatedValue(BadValue.class, true),
                AnnotatedMinimalWrapper.class);
    }

    // Provenance: ValidatePolymSubTypeTest#testWithAnnotationMinClassNameDenyDefault().
    void testWithAnnotationMinClassNameDenyDefaultVpack() {
        assertDenied(nameMapper(), annotatedValue(MehValue.class, true),
                AnnotatedMinimalWrapper.class);
    }

    // Provenance: ValidatePolymSubTypeTest#testWithAnnotationMinClassClassAccept().
    void testWithAnnotationMinClassClassAcceptVpack() throws Exception {
        AnnotatedMinimalWrapper result = classMapper().readValue(
                annotatedValue(GoodValue.class, true), AnnotatedMinimalWrapper.class);
        assertGood(result.value);
    }

    // Provenance: ValidatePolymSubTypeTest#testWithAnnotationMinClassClassDenyExplicit().
    void testWithAnnotationMinClassClassDenyExplicitVpack() {
        assertDenied(classMapper(), annotatedValue(BadValue.class, true),
                AnnotatedMinimalWrapper.class);
    }

    // Provenance: ValidatePolymSubTypeTest#testWithAnnotationMinClassClassDenyDefault().
    void testWithAnnotationMinClassClassDenyDefaultVpack() {
        assertDenied(classMapper(), annotatedValue(MehValue.class, true),
                AnnotatedMinimalWrapper.class);
    }
private static ObjectMapper nameMapper() {
        return VPackMapper.builder()
                .polymorphicTypeValidator(new SimpleNameBasedValidator()).build();
    }
private static ObjectMapper classMapper() {
        return VPackMapper.builder()
                .polymorphicTypeValidator(new SimpleClassBasedValidator()).build();
    }
private static byte[] annotatedValue(Class<?> type, boolean minimal) {
        String property = minimal ? "@c" : "@class";
        String typeId = minimal ? minimalName(type) : type.getName();
        return indexedObject("value", indexedObject(property, string(typeId), "x", integer(3)));
    }
private static String minimalName(Class<?> type) {
        String packageName = T32_0456Fixture.class.getPackageName();
        return type.getName().substring(packageName.length());
    }
private static void assertGood(BaseValue value) {
        assertNotNull(value);
        assertEquals(GoodValue.class, value.getClass());
        assertEquals(3, value.x);
    }
private static void assertDenied(ObjectMapper mapper, byte[] input,
            Class<?> targetType) {
        InvalidTypeIdException exception = assertThrows(InvalidTypeIdException.class,
                () -> mapper.readValue(input, targetType));
        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().contains("Could not resolve type id"));
        assertTrue(exception.getMessage().contains("PolymorphicTypeValidator"));
        assertTrue(exception.getMessage().contains("denied resolution"));
    }
private static byte[] indexedObject(String firstName, byte[] firstValue,
            String secondName, byte[] secondValue) {
        return indexedObject(new String[] { firstName, secondName },
                new byte[][] { firstValue, secondValue });
    }
private static byte[] indexedObject(String name, byte[] value) {
        return indexedObject(new String[] { name }, new byte[][] { value });
    }
private static byte[] indexedObject(String[] names, byte[][] values) {
        int bodyLength = 0;
        byte[][] encodedNames = new byte[names.length][];
        for (int i = 0; i < names.length; ++i) {
            encodedNames[i] = string(names[i]);
            bodyLength += encodedNames[i].length + values[i].length;
        }
        int length = 3 + bodyLength + names.length;
        if (length > 255 || names.length > 255) {
            throw new IllegalArgumentException("fixture exceeds one-byte layout");
        }
        byte[] result = new byte[length];
        result[0] = 0x0b;
        result[1] = (byte) length;
        result[2] = (byte) names.length;
        int cursor = 3;
        int index = length - names.length;
        for (int i = 0; i < names.length; ++i) {
            result[index++] = (byte) cursor;
            System.arraycopy(encodedNames[i], 0, result, cursor, encodedNames[i].length);
            cursor += encodedNames[i].length;
            System.arraycopy(values[i], 0, result, cursor, values[i].length);
            cursor += values[i].length;
        }
        return result;
    }
private static byte[] string(String value) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        if (bytes.length > 126) {
            throw new IllegalArgumentException("fixture string exceeds compact layout");
        }
        byte[] result = new byte[bytes.length + 1];
        result[0] = (byte) (0x40 + bytes.length);
        System.arraycopy(bytes, 0, result, 1, bytes.length);
        return result;
    }
private static byte[] integer(int value) {
        if (value >= 0 && value <= 9) {
            return new byte[] { (byte) (0x30 + value) };
        }
        throw new IllegalArgumentException("fixture integer outside helper range");
    }
static abstract class BaseValue {
        public int x = 3;
    }
static class BadValue extends BaseValue { }
static class GoodValue extends BaseValue { }
static class MehValue extends BaseValue { }
static class AnnotatedWrapper {
        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
        public BaseValue value;
    }
static class AnnotatedMinimalWrapper {
        @JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS)
        public BaseValue value;
    }
static class SimpleNameBasedValidator extends PolymorphicTypeValidator {
        private static final long serialVersionUID = 1L;

        @Override
        public Validity validateBaseType(DatabindContext ctxt, JavaType baseType) {
            return Validity.INDETERMINATE;
        }

        @Override
        public Validity validateSubClassName(DatabindContext ctxt, JavaType baseType,
                String subClassName) {
            if (subClassName.equals(BadValue.class.getName())) {
                return Validity.DENIED;
            }
            if (subClassName.equals(GoodValue.class.getName())) {
                return Validity.ALLOWED;
            }
            return Validity.INDETERMINATE;
        }

        @Override
        public Validity validateSubType(DatabindContext ctxt, JavaType baseType,
                JavaType subType) {
            return Validity.DENIED;
        }
    }
static class SimpleClassBasedValidator extends PolymorphicTypeValidator {
        private static final long serialVersionUID = 1L;

        @Override
        public Validity validateBaseType(DatabindContext ctxt, JavaType baseType) {
            return Validity.INDETERMINATE;
        }

        @Override
        public Validity validateSubClassName(DatabindContext ctxt, JavaType baseType,
                String subClassName) {
            return Validity.INDETERMINATE;
        }

        @Override
        public Validity validateSubType(DatabindContext ctxt, JavaType baseType,
                JavaType subType) {
            if (subType.hasRawClass(BadValue.class)) {
                return Validity.DENIED;
            }
            if (subType.hasRawClass(GoodValue.class)) {
                return Validity.ALLOWED;
            }
            return Validity.INDETERMINATE;
        }
    }

    void __invoke_testWithAnnotationNameAcceptVpack() throws Exception {
        try {
            testWithAnnotationNameAcceptVpack();
        } finally {
        }
    }


    void __invoke_testWithAnnotationNameDenyExplicitVpack() throws Exception {
        try {
            testWithAnnotationNameDenyExplicitVpack();
        } finally {
        }
    }


    void __invoke_testWithAnnotationNameDenyDefaultVpack() throws Exception {
        try {
            testWithAnnotationNameDenyDefaultVpack();
        } finally {
        }
    }


    void __invoke_testWithAnnotationClassAcceptVpack() throws Exception {
        try {
            testWithAnnotationClassAcceptVpack();
        } finally {
        }
    }


    void __invoke_testWithAnnotationClassDenyExplicitVpack() throws Exception {
        try {
            testWithAnnotationClassDenyExplicitVpack();
        } finally {
        }
    }


    void __invoke_testWithAnnotationClassDenyDefaultVpack() throws Exception {
        try {
            testWithAnnotationClassDenyDefaultVpack();
        } finally {
        }
    }


    void __invoke_testWithAnnotationMinClassNameAcceptVpack() throws Exception {
        try {
            testWithAnnotationMinClassNameAcceptVpack();
        } finally {
        }
    }


    void __invoke_testWithAnnotationMinClassNameDenyExplicitVpack() throws Exception {
        try {
            testWithAnnotationMinClassNameDenyExplicitVpack();
        } finally {
        }
    }


    void __invoke_testWithAnnotationMinClassNameDenyDefaultVpack() throws Exception {
        try {
            testWithAnnotationMinClassNameDenyDefaultVpack();
        } finally {
        }
    }


    void __invoke_testWithAnnotationMinClassClassAcceptVpack() throws Exception {
        try {
            testWithAnnotationMinClassClassAcceptVpack();
        } finally {
        }
    }


    void __invoke_testWithAnnotationMinClassClassDenyExplicitVpack() throws Exception {
        try {
            testWithAnnotationMinClassClassDenyExplicitVpack();
        } finally {
        }
    }


    void __invoke_testWithAnnotationMinClassClassDenyDefaultVpack() throws Exception {
        try {
            testWithAnnotationMinClassClassDenyDefaultVpack();
        } finally {
        }
    }

}
