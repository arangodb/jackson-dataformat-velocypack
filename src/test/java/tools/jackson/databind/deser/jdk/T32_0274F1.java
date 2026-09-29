package tools.jackson.databind.deser.jdk;

import java.util.Locale;

import tools.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0274F1 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] PROPERTIES = VPackWireFixtureTest.hex(
            "14 10 41 61 43 66 6f 6f 41 62 28 7b 41 63 1a 03");
private static final byte[] EN = VPackWireFixtureTest.hex("42 65 6e");
private static final byte[] EN_US = VPackWireFixtureTest.hex("45 65 6e 2d 55 53");
private static final byte[] EN_US_VARIANT = VPackWireFixtureTest.hex(
            "4d 65 6e 2d 55 53 2d 56 41 52 49 41 4e 54");
private static final byte[] EN_VARIANT = VPackWireFixtureTest.hex(
            "4a 65 6e 2d 56 41 52 49 41 4e 54");
private static final byte[] UND_US_VARIANT = VPackWireFixtureTest.hex(
            "4e 75 6e 64 2d 55 53 2d 56 41 52 49 41 4e 54");
private static final byte[] UND_US = VPackWireFixtureTest.hex(
            "46 75 6e 64 2d 55 53");
private static final byte[] EN_LATN_GB_VARIANT = VPackWireFixtureTest.hex(
            "52 65 6e 2d 4c 61 74 6e 2d 47 42 2d 56 41 52 49 41 4e 54");
private static final byte[] UND_LATN_IN = VPackWireFixtureTest.hex(
            "4b 75 6e 64 2d 4c 61 74 6e 2d 49 4e");
private static final byte[] UND_LATN_CA_VARIANT = VPackWireFixtureTest.hex(
            "53 75 6e 64 2d 4c 61 74 6e 2d 43 41 2d 56 41 52 49 41 4e 54");
private static final byte[] EN_LATN = VPackWireFixtureTest.hex(
            "47 65 6e 2d 4c 61 74 6e");
private static final byte[] FR_LATN_CA = VPackWireFixtureTest.hex(
            "4a 66 72 2d 4c 61 74 6e 2d 43 41");
private static final byte[] IT_LATN_VARIANT = VPackWireFixtureTest.hex(
            "4f 69 74 2d 4c 61 74 6e 2d 56 41 52 49 41 4e 54");
private static final byte[] EN_GB_VARIANT_X_DUMMY = VPackWireFixtureTest.hex(
            "55 65 6e 2d 47 42 2d 56 41 52 49 41 4e 54 2d 78 2d 64 75 6d 6d 79");
private static final byte[] UND_IN_X_DUMMY = VPackWireFixtureTest.hex(
            "4e 75 6e 64 2d 49 4e 2d 78 2d 64 75 6d 6d 79");
private static final byte[] FR_CA_X_DUMMY = VPackWireFixtureTest.hex(
            "4d 66 72 2d 43 41 2d 78 2d 64 75 6d 6d 79");
private static final byte[] UND_CA_VARIANT_X_DUMMY = VPackWireFixtureTest.hex(
            "56 75 6e 64 2d 43 41 2d 56 41 52 49 41 4e 54 2d 78 2d 64 75 6d 6d 79");
private static final byte[] IT_VARIANT_X_DUMMY = VPackWireFixtureTest.hex(
            "52 69 74 2d 56 41 52 49 41 4e 54 2d 78 2d 64 75 6d 6d 79");
private static final byte[] EN_X_DUMMY = VPackWireFixtureTest.hex(
            "4a 65 6e 2d 78 2d 64 75 6d 6d 79");
private static final byte[] EN_US_X_DEBUG = VPackWireFixtureTest.hex(
            "4d 65 6e 2d 55 53 2d 78 2d 64 65 62 75 67");
private static final byte[] EN_US_POSIX = VPackWireFixtureTest.hex(
            "4b 65 6e 2d 55 53 2d 50 4f 53 49 58");
private static final byte[] DE_POSIX_ABCDEF_X_URP = VPackWireFixtureTest.hex(
            "55 64 65 2d 50 4f 53 49 58 2d 41 62 63 44 65 66 2d 78 2d 75 72 70");
private static final byte[] AAO = VPackWireFixtureTest.hex("43 61 61 6f");
private static final byte[] ABC_US = VPackWireFixtureTest.hex(
            "46 61 62 63 2d 55 53");
private static final byte[] ILL_FORMED_VARIANT = VPackWireFixtureTest.hex(
            "5f 64 65 2d 50 4f 53 49 58 2d 78 2d 75 72 70 2d 6c 76 61 72 69 61 6e 74 2d 41 62 63 2d 44 65 66");
private static final byte[] EN_CA = VPackWireFixtureTest.hex(
            "45 65 6e 2d 43 41");
private static final byte[] EMPTY = VPackWireFixtureTest.hex("40");
private static final byte[] DE = VPackWireFixtureTest.hex("42 64 65");
private static final byte[] ZH = VPackWireFixtureTest.hex("42 7a 68");
private static final byte[] KO_KR = VPackWireFixtureTest.hex(
            "45 6b 6f 2d 4b 52");
private static final byte[] ZH_TW = VPackWireFixtureTest.hex(
            "45 7a 68 2d 54 57");

    // Provenance: LocaleDeserializationTest#testLocale().
    void testLocale() throws Exception {
        assertEquals(new Locale("en"), read(EN));
    }

    // Provenance: LocaleDeserializationTest#testLocaleDeserializeNonBCPFormat1().
    void testLocaleDeserializeNonBCPFormat1() throws Exception {
        assertBaseValues(new Locale("en", "US"), read(EN_US));
        assertBaseValues(new Locale("en"), read(EN));
        assertBaseValues(new Locale("en", "US", "VARIANT"), read(EN_US_VARIANT));
    }

    // Provenance: LocaleDeserializationTest#testLocaleDeserializeNonBCPFormat2().
    void testLocaleDeserializeNonBCPFormat2() throws Exception {
        assertBaseValues(new Locale("en", "", "VARIANT"), read(EN_VARIANT));
        assertBaseValues(new Locale("", "US", "VARIANT"), read(UND_US_VARIANT));
        assertBaseValues(new Locale("", "US", ""), read(UND_US));
    }

    // Provenance: LocaleDeserializationTest#testLocaleDeserializeWithScript().
    void testLocaleDeserializeWithScript() throws Exception {
        assertLocaleWithScript(new Locale.Builder().setLanguage("en").setRegion("GB")
                .setVariant("VARIANT").setScript("Latn").build(), read(EN_LATN_GB_VARIANT));
        assertLocaleWithScript(new Locale.Builder().setRegion("IN").setScript("Latn").build(),
                read(UND_LATN_IN));
        assertLocaleWithScript(new Locale.Builder().setRegion("CA").setVariant("VARIANT")
                .setScript("Latn").build(), read(UND_LATN_CA_VARIANT));
    }

    // Provenance: LocaleDeserializationTest#testLocaleDeserializeWithScript2().
    void testLocaleDeserializeWithScript2() throws Exception {
        assertLocaleWithScript(new Locale.Builder().setLanguage("en").setScript("Latn").build(),
                read(EN_LATN));
        assertLocaleWithScript(new Locale.Builder().setLanguage("fr").setRegion("CA")
                .setScript("Latn").build(), read(FR_LATN_CA));
        assertLocaleWithScript(new Locale.Builder().setLanguage("it").setVariant("VARIANT")
                .setScript("Latn").build(), read(IT_LATN_VARIANT));
    }

    // Provenance: LocaleDeserializationTest#testLocaleDeserializeWithExtension().
    void testLocaleDeserializeWithExtension() throws Exception {
        assertLocaleWithExtension(new Locale.Builder().setLanguage("en").setRegion("GB")
                .setVariant("VARIANT").setExtension('x', "dummy").build(),
                read(EN_GB_VARIANT_X_DUMMY));
        assertLocaleWithScript(new Locale.Builder().setRegion("IN").setExtension('x', "dummy")
                .build(), read(UND_IN_X_DUMMY));
        assertLocaleWithScript(new Locale.Builder().setLanguage("fr").setRegion("CA")
                .setExtension('x', "dummy").build(), read(FR_CA_X_DUMMY));
        assertLocaleWithScript(new Locale.Builder().setRegion("CA").setVariant("VARIANT")
                .setExtension('x', "dummy").build(), read(UND_CA_VARIANT_X_DUMMY));
        assertLocaleWithScript(new Locale.Builder().setLanguage("it").setVariant("VARIANT")
                .setExtension('x', "dummy").build(), read(IT_VARIANT_X_DUMMY));
    }

    // Provenance: LocaleDeserializationTest#testLocaleDeserializeWithExtension2().
    void testLocaleDeserializeWithExtension2() throws Exception {
        assertLocaleWithScript(new Locale.Builder().setLanguage("en").setExtension('x', "dummy")
                .build(), read(EN_X_DUMMY));
    }

    // Provenance: LocaleDeserializationTest#testLocaleDeserializeWithLanguageTag().
    void testLocaleDeserializeWithLanguageTag() throws Exception {
        Locale locale = Locale.forLanguageTag("en-US-x-debug");
        assertLocale(locale, read(EN_US_X_DEBUG));

        locale = Locale.forLanguageTag("en-US-x-lvariant-POSIX");
        assertLocale(locale, read(EN_US_POSIX));

        locale = Locale.forLanguageTag("de-POSIX-x-URP-lvariant-AbcDef");
        assertBaseValues(locale, read(DE_POSIX_ABCDEF_X_URP));

        locale = Locale.forLanguageTag("ar-aao");
        assertLocale(locale, read(AAO));

        locale = Locale.forLanguageTag("en-abc-def-us");
        assertLocale(locale, read(ABC_US));
    }

    // Provenance: LocaleDeserializationTest#testIllFormedVariant().
    void testIllFormedVariant() throws Exception {
        Locale locale = Locale.forLanguageTag("de-POSIX-x-URP-lvariant-Abc-Def");
        assertBaseValues(locale, read(ILL_FORMED_VARIANT));
    }

    // Provenance: LocaleDeserializationTest#testLocaleDeserializeWithLocaleConstants().
    void testLocaleDeserializeWithLocaleConstants() throws Exception {
        Locale[] locales = {
                Locale.CANADA, Locale.ROOT, Locale.GERMAN,
                Locale.CHINESE, Locale.KOREA, Locale.TAIWAN
        };
        byte[][] fixtures = { EN_CA, EMPTY, DE, ZH, KO_KR, ZH_TW };
        for (int i = 0; i < locales.length; ++i) {
            assertLocale(locales[i], read(fixtures[i]));
        }
    }
private static Locale read(byte[] fixture) throws Exception {
        return MAPPER.readValue(fixture, Locale.class);
    }
private static void assertBaseValues(Locale expected, Locale actual) {
        assertEquals(expected.getLanguage(), actual.getLanguage(), "Language mismatch");
        assertEquals(expected.getCountry(), actual.getCountry(), "Country mismatch");
        assertEquals(expected.getVariant(), actual.getVariant(), "Variant mismatch");
    }
private static void assertLocaleWithScript(Locale expected, Locale actual) {
        assertBaseValues(expected, actual);
        assertEquals(expected.getScript(), actual.getScript(), "Script mismatch");
    }
private static void assertLocaleWithExtension(Locale expected, Locale actual) {
        assertBaseValues(expected, actual);
        assertEquals(expected.getExtension('x'), actual.getExtension('x'), "Extension mismatch");
    }
private static void assertLocale(Locale expected, Locale actual) {
        assertBaseValues(expected, actual);
        assertEquals(expected.getExtension('x'), actual.getExtension('x'), "Extension mismatch");
        assertEquals(expected.getScript(), actual.getScript(), "Script mismatch");
    }

    void __invoke_testLocale() throws Exception {
        try {
            testLocale();
        } finally {
        }
    }


    void __invoke_testLocaleDeserializeNonBCPFormat1() throws Exception {
        try {
            testLocaleDeserializeNonBCPFormat1();
        } finally {
        }
    }


    void __invoke_testLocaleDeserializeNonBCPFormat2() throws Exception {
        try {
            testLocaleDeserializeNonBCPFormat2();
        } finally {
        }
    }


    void __invoke_testLocaleDeserializeWithScript() throws Exception {
        try {
            testLocaleDeserializeWithScript();
        } finally {
        }
    }


    void __invoke_testLocaleDeserializeWithScript2() throws Exception {
        try {
            testLocaleDeserializeWithScript2();
        } finally {
        }
    }


    void __invoke_testLocaleDeserializeWithExtension() throws Exception {
        try {
            testLocaleDeserializeWithExtension();
        } finally {
        }
    }


    void __invoke_testLocaleDeserializeWithExtension2() throws Exception {
        try {
            testLocaleDeserializeWithExtension2();
        } finally {
        }
    }


    void __invoke_testLocaleDeserializeWithLanguageTag() throws Exception {
        try {
            testLocaleDeserializeWithLanguageTag();
        } finally {
        }
    }


    void __invoke_testIllFormedVariant() throws Exception {
        try {
            testIllFormedVariant();
        } finally {
        }
    }


    void __invoke_testLocaleDeserializeWithLocaleConstants() throws Exception {
        try {
            testLocaleDeserializeWithLocaleConstants();
        } finally {
        }
    }

}
