package tools.jackson.databind.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.databind.type.TypeFactory;
import tools.jackson.databind.util.BeanUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0621F1 {
private static final VPackMapper MAPPER = new VPackMapper();

    // Provenance: BeanUtilTest#testGetDefaultValueGeneral().
    void getDefaultValueGeneralVpack() {
        TypeFactory types = MAPPER.getTypeFactory();
        assertEquals(JsonInclude.Include.NON_EMPTY,
                BeanUtil.propertyDefaultValue(types.constructType(Map.class), true));
        assertEquals(JsonInclude.Include.NON_EMPTY,
                BeanUtil.propertyDefaultValue(types.constructType(List.class), true));
        assertEquals(JsonInclude.Include.NON_EMPTY,
                BeanUtil.propertyDefaultValue(types.constructType(Object[].class), true));
        assertEquals(JsonInclude.Include.NON_EMPTY,
                BeanUtil.propertyDefaultValue(types.constructType(AtomicReference.class), true));
        assertEquals("", BeanUtil.propertyDefaultValue(types.constructType(String.class), true));
        assertEquals(Boolean.FALSE,
                BeanUtil.propertyDefaultValue(types.constructType(Boolean.TYPE), true));
        assertEquals(Integer.valueOf(0),
                BeanUtil.propertyDefaultValue(types.constructType(Integer.TYPE), true));
        assertEquals(Double.valueOf(0.0),
                BeanUtil.propertyDefaultValue(types.constructType(Double.TYPE), true));
        assertNull(BeanUtil.propertyDefaultValue(types.constructType(getClass()), true));
    }

    // Provenance: BeanUtilTest#testGetDefaultValueWrappers().
    void getDefaultValueWrappersVpack() {
        TypeFactory types = MAPPER.getTypeFactory();
        assertNull(BeanUtil.propertyDefaultValue(types.constructType(Boolean.class), true));
        assertNull(BeanUtil.propertyDefaultValue(types.constructType(Integer.class), true));
        assertNull(BeanUtil.propertyDefaultValue(types.constructType(Double.class), true));
        assertEquals(Boolean.FALSE,
                BeanUtil.propertyDefaultValue(types.constructType(Boolean.class), false));
        assertEquals(Integer.valueOf(0),
                BeanUtil.propertyDefaultValue(types.constructType(Integer.class), false));
        assertEquals(Double.valueOf(0.0),
                BeanUtil.propertyDefaultValue(types.constructType(Double.class), false));
    }

    
    // Provenance: BeanUtilTest#testGetDefaultValueDeprecated().
    void getDefaultValueDeprecatedVpack() {
        TypeFactory types = MAPPER.getTypeFactory();
        assertNull(BeanUtil.getDefaultValue(types.constructType(Boolean.class)));
        assertNull(BeanUtil.getDefaultValue(types.constructType(Integer.class)));
        assertNull(BeanUtil.getDefaultValue(types.constructType(Double.class)));
    }

    // Provenance: BeanUtilTest#testGetDefaultValuesForJDKTypes().
    void getDefaultValuesForJdkTypesVpack() {
        TypeFactory types = MAPPER.getTypeFactory();
        Object result = BeanUtil.propertyDefaultValue(types.constructType(Date.class), true);
        assertNotNull(result);
        assertEquals(Date.class, result.getClass());
        assertEquals(0L, ((Date) result).getTime());
        result = BeanUtil.propertyDefaultValue(types.constructType(Calendar.class), true);
        assertNotNull(result);
        assertTrue(result instanceof Calendar);
        assertEquals(0L, ((Calendar) result).getTimeInMillis());
        result = BeanUtil.propertyDefaultValue(types.constructType(GregorianCalendar.class), true);
        assertNotNull(result);
        assertTrue(result instanceof Calendar);
        assertEquals(0L, ((Calendar) result).getTimeInMillis());
        assertEquals(JsonInclude.Include.NON_EMPTY,
                BeanUtil.propertyDefaultValue(types.constructType(UUID.class), true));
    }

    // Provenance: BeanUtilTest#testDeprecatedStdManglePropertyName().
    void deprecatedStdManglePropertyNameVpack() {
        assertNull(BeanUtil.stdManglePropertyName("get", 3));
        assertEquals("value", BeanUtil.stdManglePropertyName("getValue", 3));
        assertEquals("x", BeanUtil.stdManglePropertyName("getX", 3));
        assertEquals("URL", BeanUtil.stdManglePropertyName("getURL", 3));
        assertEquals("name", BeanUtil.stdManglePropertyName("getName", 3));
        assertEquals("value", BeanUtil.stdManglePropertyName("Value", 0));
    }

    // Provenance: BeanUtilTest#testCheckUnsupportedTypeForSupportedType().
    void checkSupportedTypesVpack() {
        TypeFactory types = MAPPER.getTypeFactory();
        assertNull(BeanUtil.checkUnsupportedType(null, types.constructType(String.class)));
        assertNull(BeanUtil.checkUnsupportedType(null, types.constructType(Integer.class)));
        assertNull(BeanUtil.checkUnsupportedType(null, types.constructType(List.class)));
    }
interface Settings { }
record MessageWrapper<T extends Settings>(T settings) { }
static class WrapperHolder { MessageWrapper<?> wrapper; }
static class NumberBox<T extends Number> { public T value; NumberBox() { } NumberBox(T value) { this.value = value; } }
static class NumberBoxHolder { NumberBox<?> box; }

    void __invoke_getDefaultValueGeneralVpack() throws Exception {
        try {
            getDefaultValueGeneralVpack();
        } finally {
        }
    }


    void __invoke_getDefaultValueWrappersVpack() throws Exception {
        try {
            getDefaultValueWrappersVpack();
        } finally {
        }
    }


    void __invoke_getDefaultValueDeprecatedVpack() throws Exception {
        try {
            getDefaultValueDeprecatedVpack();
        } finally {
        }
    }


    void __invoke_getDefaultValuesForJdkTypesVpack() throws Exception {
        try {
            getDefaultValuesForJdkTypesVpack();
        } finally {
        }
    }


    void __invoke_deprecatedStdManglePropertyNameVpack() throws Exception {
        try {
            deprecatedStdManglePropertyNameVpack();
        } finally {
        }
    }


    void __invoke_checkSupportedTypesVpack() throws Exception {
        try {
            checkSupportedTypesVpack();
        } finally {
        }
    }

}
