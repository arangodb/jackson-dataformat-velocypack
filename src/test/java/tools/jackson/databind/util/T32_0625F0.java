package tools.jackson.databind.util;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.EnumFeature;
import tools.jackson.databind.util.ClassUtil;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0625F0 {
private static final ObjectMapper MAPPER = VPackMapper.builder()
            .disable(EnumFeature.WRITE_ENUMS_USING_TO_STRING)
            .build();

    // Provenance: ClassUtilTest#testQuotedOr().
    void classUtilQuotedOrVpack() throws Exception {
        String ordinary = ClassUtil.quotedOr("test", "default");
        String fallback = ClassUtil.quotedOr(null, "default");
        String number = ClassUtil.quotedOr(42, "N/A");
        assertEquals("\"test\"", ordinary);
        assertEquals("default", fallback);
        assertEquals("\"42\"", number);
        assertArrayEquals(VPackWireFixtureTest.hex("46 22 74 65 73 74 22"),
                MAPPER.writeValueAsBytes(ordinary));
        assertEquals(ordinary, MAPPER.readValue(MAPPER.writeValueAsBytes(ordinary), String.class));
    }

    // Provenance: ClassUtilTest#testWrapperToPrimitiveType().
    void classUtilWrapperToPrimitiveTypeVpack() throws Exception {
        Class<?>[] wrappers = { Integer.class, Long.class, Character.class, Short.class,
                Byte.class, Float.class, Double.class, Boolean.class };
        Class<?>[] primitives = { Integer.TYPE, Long.TYPE, Character.TYPE, Short.TYPE,
                Byte.TYPE, Float.TYPE, Double.TYPE, Boolean.TYPE };
        for (int i = 0; i < wrappers.length; ++i) {
            assertEquals(primitives[i], ClassUtil.primitiveType(wrappers[i]));
        }
        assertNull(ClassUtil.primitiveType(String.class));
        assertArrayEquals(VPackWireFixtureTest.hex("31"), MAPPER.writeValueAsBytes(1));
    }
enum ABC {
        A("A"), B("b"), C("C");

        private final String desc;

        ABC(String desc) { this.desc = desc; }

        @Override
        public String toString() { return desc; }
    }
enum LocaleSensitiveABC { IS_ADMIN }

    void __invoke_classUtilQuotedOrVpack() throws Exception {
        try {
            classUtilQuotedOrVpack();
        } finally {
        }
    }


    void __invoke_classUtilWrapperToPrimitiveTypeVpack() throws Exception {
        try {
            classUtilWrapperToPrimitiveTypeVpack();
        } finally {
        }
    }

}
