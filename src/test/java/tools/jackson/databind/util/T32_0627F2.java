package tools.jackson.databind.util;

import java.util.Locale;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import tools.jackson.databind.EnumNamingStrategies;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.util.NamingStrategyImpls;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0627F2 {
private static final Locale TURKISH = Locale.forLanguageTag("tr-TR");
private final ObjectMapper mapper = VPackMapper.builder().build();
private Locale previousDefault;
@BeforeEach
    void switchToTurkishLocale() {
        previousDefault = Locale.getDefault();
        Locale.setDefault(TURKISH);
    }
@AfterEach
    void restoreLocale() {
        Locale.setDefault(previousDefault);
    }

    // Provenance: NamingStrategyLocaleTest#testEnumLowerCamelCaseUsesRootLocale().
    void enumLowerCamelCaseUsesRootLocale() {
        assertEquals("\u0131", "I".toLowerCase());
        assertEquals("isAdmin", EnumNamingStrategies.LOWER_CAMEL_CASE
                .convertEnumToExternalName(mapper.deserializationConfig(), null, "IS_ADMIN"));
    }

    // Provenance: NamingStrategyLocaleTest#testEnumUpperCamelCaseUsesRootLocale().
    void enumUpperCamelCaseUsesRootLocale() {
        assertEquals("\u0131", "I".toLowerCase());
        assertEquals("IsAdmin", EnumNamingStrategies.UPPER_CAMEL_CASE
                .convertEnumToExternalName(mapper.deserializationConfig(), null, "IS_ADMIN"));
    }

    // Provenance: NamingStrategyLocaleTest#testLowerCaseUsesRootLocale().
    void lowerCaseNamingUsesRootLocale() {
        assertEquals("\u0131", "I".toLowerCase());
        assertEquals("clientid", NamingStrategyImpls.LOWER_CASE.translate("ClientID"));
    }

    // Provenance: NamingStrategyLocaleTest#testUpperSnakeCaseUsesRootLocale().
    void upperSnakeCaseNamingUsesRootLocale() {
        assertEquals("\u0130", "i".toUpperCase());
        assertEquals("IS_ADMIN", NamingStrategyImpls.UPPER_SNAKE_CASE.translate("isAdmin"));
    }

    void __invoke_enumLowerCamelCaseUsesRootLocale() throws Exception {
        switchToTurkishLocale();
        try {
            enumLowerCamelCaseUsesRootLocale();
        } finally {
            restoreLocale();
        }
    }


    void __invoke_enumUpperCamelCaseUsesRootLocale() throws Exception {
        switchToTurkishLocale();
        try {
            enumUpperCamelCaseUsesRootLocale();
        } finally {
            restoreLocale();
        }
    }


    void __invoke_lowerCaseNamingUsesRootLocale() throws Exception {
        switchToTurkishLocale();
        try {
            lowerCaseNamingUsesRootLocale();
        } finally {
            restoreLocale();
        }
    }


    void __invoke_upperSnakeCaseNamingUsesRootLocale() throws Exception {
        switchToTurkishLocale();
        try {
            upperSnakeCaseNamingUsesRootLocale();
        } finally {
            restoreLocale();
        }
    }

}
