package tools.jackson.databind.deser.jdk;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0275F0 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] SCRIPT_AND_EXTENSION_1 = VPackWireFixtureTest.hex(
            "5a 65 6e 2d 4c 61 74 6e 2d 47 42 2d 56 41 52 49 41 4e 54 2d 78 2d 64 75 6d 6d 79");
private static final byte[] SCRIPT_AND_EXTENSION_2 = VPackWireFixtureTest.hex(
            "4f 65 6e 2d 4c 61 74 6e 2d 78 2d 64 75 6d 6d 79");
private static final byte[] SCRIPT_AND_EXTENSION_3 = VPackWireFixtureTest.hex(
            "53 75 6e 64 2d 4c 61 74 6e 2d 49 4e 2d 78 2d 64 75 6d 6d 79");
private static final byte[] SCRIPT_AND_EXTENSION_4 = VPackWireFixtureTest.hex(
            "52 66 72 2d 4c 61 74 6e 2d 43 41 2d 78 2d 64 75 6d 6d 79");
private static final byte[] SCRIPT_AND_EXTENSION_5 = VPackWireFixtureTest.hex(
            "5b 75 6e 64 2d 4c 61 74 6e 2d 43 41 2d 56 41 52 49 41 4e 54 2d 78 2d 64 75 6d 6d 79");
private static final byte[] SCRIPT_AND_EXTENSION_6 = VPackWireFixtureTest.hex(
            "57 69 74 2d 4c 61 74 6e 2d 56 41 52 49 41 4e 54 2d 78 2d 64 75 6d 6d 79");
private static final byte[] FUZZ_47034 = VPackWireFixtureTest.hex(
            "45 5f 5f 23 5f 5f 17");
private static final byte[] FUZZ_47036 = VPackWireFixtureTest.hex(
            "44 5f 5f 23 5f 2f");
private static final byte[] LOCALE_KEY_MAP = VPackWireFixtureTest.hex(
            "14 0a 45 7a 68 5f 43 4e 34 01");
private static final byte[] EMPTY_STRING = VPackWireFixtureTest.hex("40");
private static final byte[] SPECIAL_CASE_JA = VPackWireFixtureTest.hex(
            "48 6a 61 5f 4a 50 5f 4a 50");
private static final byte[] SPECIAL_CASE_TH = VPackWireFixtureTest.hex(
            "48 74 68 5f 54 48 5f 54 48");
private static final byte[] VARARGS_MULTIPLE = VPackWireFixtureTest.hex(
            "14 1d 47 6c 6f 63 61 6c 65 73 13 12 45 65 6e 2d 55 53 45 65 6e 2d 47 42 42 65 6e 03 01");
private static final byte[] VARARGS_SINGLE = VPackWireFixtureTest.hex(
            "14 11 47 6c 6f 63 61 6c 65 73 13 06 42 6a 61 01 01");
private static final byte[] CALENDAR_MAP = VPackWireFixtureTest.hex(
            "14 25 5d 46 72 69 2c 20 30 32 20 4a 61 6e 20 31 39 37 30 20 31 30 3a 31 37 3a 33 36 20 55 54 43 40 41 30 18 02");

    // Provenance: LocaleDeserializationTest#testLocaleDeserializeWithScriptAndExtension().
    void testLocaleDeserializeWithScriptAndExtensionVpack() throws Exception {
        assertLocale(new Locale.Builder().setLanguage("en").setRegion("GB")
                .setVariant("VARIANT").setExtension('x', "dummy").setScript("latn").build(),
                read(SCRIPT_AND_EXTENSION_1));
        assertLocale(new Locale.Builder().setLanguage("en").setExtension('x', "dummy")
                .setScript("latn").build(), read(SCRIPT_AND_EXTENSION_2));
        assertLocale(new Locale.Builder().setRegion("IN").setExtension('x', "dummy")
                .setScript("latn").build(), read(SCRIPT_AND_EXTENSION_3));
        assertLocale(new Locale.Builder().setLanguage("fr").setRegion("CA")
                .setExtension('x', "dummy").setScript("latn").build(),
                read(SCRIPT_AND_EXTENSION_4));
        assertLocale(new Locale.Builder().setRegion("CA").setVariant("VARIANT")
                .setExtension('x', "dummy").setScript("latn").build(),
                read(SCRIPT_AND_EXTENSION_5));
        assertLocale(new Locale.Builder().setLanguage("it").setVariant("VARIANT")
                .setExtension('x', "dummy").setScript("latn").build(),
                read(SCRIPT_AND_EXTENSION_6));
    }

    // Provenance: LocaleDeserializationTest#testLocaleFuzz47034().
    void testLocaleFuzz47034Vpack() throws Exception {
        Locale value = MAPPER.readerFor(Locale.class)
                .without(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
                .readValue(FUZZ_47034);
        assertNotNull(value);
    }

    // Provenance: LocaleDeserializationTest#testLocaleFuzz47036().
    void testLocaleFuzz47036Vpack() throws Exception {
        Locale value = MAPPER.readerFor(Locale.class)
                .without(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
                .readValue(FUZZ_47036);
        assertNotNull(value);
    }

    // Provenance: LocaleDeserializationTest#testLocaleKeyMap().
    void testLocaleKeyMapVpack() throws Exception {
        Map<Locale, Object> result = MAPPER.readValue(LOCALE_KEY_MAP,
                new TypeReference<Map<Locale, Object>>() { });
        assertNotNull(result);
        assertEquals(1, result.size());
        Object key = result.keySet().iterator().next();
        assertNotNull(key);
        assertEquals(Locale.class, key.getClass());
        assertEquals(Locale.CHINA, key);
        assertEquals(4, result.get(Locale.CHINA));
    }

    // Provenance: LocaleDeserializationTest#testLocaleVarargMultiple5231().
    void testLocaleVarargMultiple5231Vpack() throws Exception {
        DateTimeParserConfig5231 result = MAPPER.readValue(VARARGS_MULTIPLE,
                DateTimeParserConfig5231.class);
        assertNotNull(result);
        assertEquals(List.of(Locale.US, Locale.UK, Locale.ENGLISH), List.of(result.locales));
    }

    // Provenance: LocaleDeserializationTest#testLocaleVarargSingle5231().
    void testLocaleVarargSingle5231Vpack() throws Exception {
        DateTimeParserConfig5231 result = MAPPER.readValue(VARARGS_SINGLE,
                DateTimeParserConfig5231.class);
        assertNotNull(result);
        assertEquals(List.of(Locale.JAPANESE), List.of(result.locales));
    }

    // Provenance: LocaleDeserializationTest#testLocaleWithFeatureDisabled().
    void testLocaleWithFeatureDisabledVpack() throws Exception {
        assertEquals(Locale.ROOT, MAPPER.readerFor(Locale.class)
                .without(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
                .readValue(EMPTY_STRING));
    }

    // Provenance: LocaleDeserializationTest#testLocaleWithFeatureEnabled().
    void testLocaleWithFeatureEnabledVpack() throws Exception {
        assertEquals(Locale.ROOT, MAPPER.readerFor(Locale.class)
                .with(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
                .readValue(EMPTY_STRING));
    }

    // Provenance: LocaleDeserializationTest#testSpecialCases().
    void testSpecialCasesVpack() throws Exception {
        assertEquals(new Locale("ja", "JP", "JP"), read(SPECIAL_CASE_JA));
        assertEquals(new Locale("th", "TH", "TH"), read(SPECIAL_CASE_TH));
    }
private static Locale read(byte[] fixture) throws Exception {
        return MAPPER.readValue(fixture, Locale.class);
    }
private static void assertLocale(Locale expected, Locale actual) {
        assertEquals(expected.getLanguage(), actual.getLanguage(), "Language mismatch");
        assertEquals(expected.getCountry(), actual.getCountry(), "Country mismatch");
        assertEquals(expected.getVariant(), actual.getVariant(), "Variant mismatch");
        assertEquals(expected.getExtension('x'), actual.getExtension('x'), "Extension mismatch");
        assertEquals(expected.getScript(), actual.getScript(), "Script mismatch");
    }
private static byte[] bigUntypedMapFixture() {
        byte[][] fields = new byte[2200][];
        int field = 0;
        for (int i = 0; i < 1100; ++i) {
            byte[] value = ((i & 1) == 0)
                    ? integer(i)
                    : compactObject(field("x", integer(i)));
            fields[field++] = concat(asciiString(String.valueOf(i)), value);
        }
        return compactObject(fields, 1100);
    }
private static byte[] field(String name, byte[] value) {
        return concat(asciiString(name), value);
    }
private static byte[] asciiString(String value) {
        byte[] result = new byte[value.length() + 1];
        result[0] = (byte) (0x40 + value.length());
        for (int i = 0; i < value.length(); ++i) {
            if (value.charAt(i) > 0x7f) throw new IllegalArgumentException("non-ASCII fixture key");
            result[i + 1] = (byte) value.charAt(i);
        }
        return result;
    }
private static byte[] integer(int value) {
        if (value >= 0 && value <= 9) return new byte[] { (byte) (0x30 + value) };
        if (value <= 255) return new byte[] { 0x28, (byte) value };
        return new byte[] { 0x29, (byte) value, (byte) (value >>> 8) };
    }
private static byte[] compactObject(byte[]... fields) {
        return compactObject(fields, fields.length);
    }
private static byte[] compactObject(byte[][] fields, int count) {
        int bodyLength = 0;
        for (byte[] field : fields) {
            if (field != null) bodyLength += field.length;
        }
        int length = 1 + varintLength(bodyLength + 3) + bodyLength + varintLength(count);
        while (length != 1 + varintLength(length) + bodyLength + varintLength(count)) {
            length = 1 + varintLength(length) + bodyLength + varintLength(count);
        }
        ByteArrayOutputStream output = new ByteArrayOutputStream(length);
        output.write(0x14);
        writeForward(output, length);
        for (byte[] field : fields) {
            if (field != null) output.writeBytes(field);
        }
        writeReverse(output, count);
        return output.toByteArray();
    }
private static int varintLength(int value) {
        int length = 1;
        while ((value >>>= 7) != 0) ++length;
        return length;
    }
private static void writeForward(ByteArrayOutputStream output, int value) {
        do {
            int group = value & 0x7f;
            value >>>= 7;
            output.write(group | (value == 0 ? 0 : 0x80));
        } while (value != 0);
    }
private static void writeReverse(ByteArrayOutputStream output, int value) {
        byte[] groups = new byte[varintLength(value)];
        int offset = 0;
        do {
            int group = value & 0x7f;
            value >>>= 7;
            groups[offset++] = (byte) (group | (value == 0 ? 0 : 0x80));
        } while (value != 0);
        for (int i = groups.length - 1; i >= 0; --i) output.write(groups[i]);
    }
private static byte[] concat(byte[]... values) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        for (byte[] value : values) output.writeBytes(value);
        return output.toByteArray();
    }
static class AbstractMapWrapper {
        public Map<String, Integer> values;
    }
public static class DateTimeParserConfig5231 {
        public Locale[] locales;
        private Locale locale;

        public Locale[] getLocales() {
            return locales;
        }

        public void setLocales(Locale... locales) {
            this.locales = locales;
            if (locales != null && locales.length == 1) {
                this.locale = locales[0];
            }
        }

        public Locale getLocale() {
            return locale;
        }
    }

    void __invoke_testLocaleDeserializeWithScriptAndExtensionVpack() throws Exception {
        try {
            testLocaleDeserializeWithScriptAndExtensionVpack();
        } finally {
        }
    }


    void __invoke_testLocaleFuzz47034Vpack() throws Exception {
        try {
            testLocaleFuzz47034Vpack();
        } finally {
        }
    }


    void __invoke_testLocaleFuzz47036Vpack() throws Exception {
        try {
            testLocaleFuzz47036Vpack();
        } finally {
        }
    }


    void __invoke_testLocaleKeyMapVpack() throws Exception {
        try {
            testLocaleKeyMapVpack();
        } finally {
        }
    }


    void __invoke_testLocaleVarargMultiple5231Vpack() throws Exception {
        try {
            testLocaleVarargMultiple5231Vpack();
        } finally {
        }
    }


    void __invoke_testLocaleVarargSingle5231Vpack() throws Exception {
        try {
            testLocaleVarargSingle5231Vpack();
        } finally {
        }
    }


    void __invoke_testLocaleWithFeatureDisabledVpack() throws Exception {
        try {
            testLocaleWithFeatureDisabledVpack();
        } finally {
        }
    }


    void __invoke_testLocaleWithFeatureEnabledVpack() throws Exception {
        try {
            testLocaleWithFeatureEnabledVpack();
        } finally {
        }
    }


    void __invoke_testSpecialCasesVpack() throws Exception {
        try {
            testSpecialCasesVpack();
        } finally {
        }
    }

}
