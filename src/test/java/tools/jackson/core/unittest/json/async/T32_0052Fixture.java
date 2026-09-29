package tools.jackson.core.unittest.json.async;

import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0052Fixture {

    void jsonNonStandardNumberSpellingsHaveNoVpackWireSurface() {
        VPackFactory factory = new VPackFactory();

        String[][] sourceSpellings = {
                {"hexadecimal", "[ 0xc0ffee ]"},
                {"hexadecimalBigX", "[ 0XC0FFEE ]"},
                {"leadingDotInDecimal", "[ .123 ]"},
                {"leadingDotInDecimalEnabled", "[ .123 ]"},
                {"leadingDotInNegativeDecimalEnabled", "[ -.123 ]"},
                {"leadingPlusSignInDecimalDefaultFail", "[ +123 ]"},
                {"leadingPlusSignInDecimalDefaultFail2", "[ +0.123 ]"},
                {"leadingPlusSignInDecimalEnabled", "[ +123 ]"},
                {"leadingPlusSignInDecimalEnabled2", "[ +0.123 ]"},
                {"leadingPlusSignInDecimalEnabled3", "[ +123.123 ]"},
                {"leadingPlusSignNoLeadingZeroDisabled", "[ +.123 ]"},
                {"leadingPlusSignNoLeadingZeroEnabled", "[ +.123 ]"}
        };

        for (String[] sourceSpelling : sourceSpellings) {
            assertThrows(UnsupportedOperationException.class,
                    () -> factory.createParser(sourceSpelling[1]), sourceSpelling[0]);
        }
    }

    void __invoke_jsonNonStandardNumberSpellingsHaveNoVpackWireSurface() throws Exception {
        try {
            jsonNonStandardNumberSpellingsHaveNoVpackWireSurface();
        } finally {
        }
    }

}
