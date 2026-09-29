package tools.jackson.core.unittest.json.async;

import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0053Fixture {

    void assignedJsonSpellingsHaveNoVpackWireSurface() {
        VPackFactory factory = new VPackFactory();
        String[][] sourceSpellings = {
                {"negativeHexadecimal", "[ -0xc0ffee ]"},
                {"rootMinusZeroAtEOF", "-0"},
                {"rootPlainZeroAtEOF", "0"},
                {"rootPlusZeroAtEOF", "+0"},
                {"test2DecimalPoints", "[ -0.123.456 ]"},
                {"trailingDotInDecimal", "[ 123. ]"},
                {"trailingDotInDecimalEnabled", "[ 123. ]"},
                {"leadingZeroesInt", "00003"},
                {"leadingZeroesFloat", "00.25"},
                {"leadingPeriodFloat", ".25"},
                {"aposQuotingDisabled-array", "[ 'text' ]"},
                {"aposQuotingDisabled-object", "{ 'a':1 }"}
        };

        for (String[] sourceSpelling : sourceSpellings) {
            assertThrows(UnsupportedOperationException.class,
                    () -> factory.createParser(sourceSpelling[1]), sourceSpelling[0]);
        }
    }

    void __invoke_assignedJsonSpellingsHaveNoVpackWireSurface() throws Exception {
        try {
            assignedJsonSpellingsHaveNoVpackWireSurface();
        } finally {
        }
    }

}
