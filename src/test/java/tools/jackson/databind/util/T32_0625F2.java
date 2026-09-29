package tools.jackson.databind.util;

import java.util.Locale;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.EnumFeature;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0625F2 {
private static final ObjectMapper MAPPER = VPackMapper.builder()
            .disable(EnumFeature.WRITE_ENUMS_USING_TO_STRING)
            .build();

    // Provenance: EnumValuesTest#testConstructFromName().
    void enumValuesConstructFromNameVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .disable(EnumFeature.WRITE_ENUMS_USING_TO_STRING).build();
        String[] names = { "A", "B", "C" };
        String[] fixtures = { "41 41", "41 42", "41 43" };
        for (int i = 0; i < ABC.values().length; ++i) {
            byte[] expected = VPackWireFixtureTest.hex(fixtures[i]);
            assertArrayEquals(expected, mapper.writeValueAsBytes(ABC.values()[i]));
            if (i != 1) {
                assertEquals(ABC.values()[i], mapper.readValue(expected, ABC.class));
            }
            assertEquals(names[i], ABC.values()[i].name());
        }
        assertEquals(3, ABC.values().length);
    }

    // Provenance: EnumValuesTest#testConstructWithToString().
    void enumValuesConstructWithToStringVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(EnumFeature.WRITE_ENUMS_USING_TO_STRING).build();
        String[] values = { "A", "b", "C" };
        for (int i = 0; i < ABC.values().length; ++i) {
            assertArrayEquals(MAPPER.writeValueAsBytes(values[i]),
                    mapper.writeValueAsBytes(ABC.values()[i]));
            assertEquals(values[i], ABC.values()[i].toString());
        }
    }

    // Provenance: EnumValuesTest#testEnumResolverNew().
    void enumResolverNewVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(EnumFeature.WRITE_ENUMS_USING_INDEX).build();
        ABC[] values = ABC.values();
        assertEquals(2, values.length - 1);
        for (int index = 0; index < values.length; ++index) {
            byte[] encoded = mapper.writeValueAsBytes(values[index]);
            assertEquals(values[index], mapper.readValue(encoded, ABC.class));
        }
        assertArrayEquals(VPackWireFixtureTest.hex("31"), mapper.writeValueAsBytes(ABC.B));
    }

    // Provenance: EnumValuesTest#testConstructFromNameLowerCased().
    void enumValuesConstructFromNameLowerCasedVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(EnumFeature.WRITE_ENUMS_TO_LOWERCASE).build();
        assertArrayEquals(VPackWireFixtureTest.hex("41 61"), mapper.writeValueAsBytes(ABC.A));
        assertArrayEquals(VPackWireFixtureTest.hex("41 62"), mapper.writeValueAsBytes(ABC.B));
        assertArrayEquals(VPackWireFixtureTest.hex("41 63"), mapper.writeValueAsBytes(ABC.C));
    }

    
    // Provenance: EnumValuesTest#testConstructFromNameLowerCasedWithRootLocale().
    void enumValuesConstructFromNameLowerCasedWithRootLocaleVpack() throws Exception {
        Locale previous = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            assertEquals("\u0131s_adm\u0131n", "IS_ADMIN".toLowerCase());
            ObjectMapper mapper = VPackMapper.builder()
                    .enable(EnumFeature.WRITE_ENUMS_TO_LOWERCASE).build();
            assertArrayEquals(VPackWireFixtureTest.hex(
                    "48 69 73 5f 61 64 6d 69 6e"),
                    mapper.writeValueAsBytes(LocaleSensitiveABC.IS_ADMIN));
        } finally {
            Locale.setDefault(previous);
        }
    }
enum ABC {
        A("A"), B("b"), C("C");

        private final String desc;

        ABC(String desc) { this.desc = desc; }

        @Override
        public String toString() { return desc; }
    }
enum LocaleSensitiveABC { IS_ADMIN }

    void __invoke_enumValuesConstructFromNameVpack() throws Exception {
        try {
            enumValuesConstructFromNameVpack();
        } finally {
        }
    }


    void __invoke_enumValuesConstructWithToStringVpack() throws Exception {
        try {
            enumValuesConstructWithToStringVpack();
        } finally {
        }
    }


    void __invoke_enumResolverNewVpack() throws Exception {
        try {
            enumResolverNewVpack();
        } finally {
        }
    }


    void __invoke_enumValuesConstructFromNameLowerCasedVpack() throws Exception {
        try {
            enumValuesConstructFromNameLowerCasedVpack();
        } finally {
        }
    }


    void __invoke_enumValuesConstructFromNameLowerCasedWithRootLocaleVpack() throws Exception {
        try {
            enumValuesConstructFromNameLowerCasedWithRootLocaleVpack();
        } finally {
        }
    }

}
