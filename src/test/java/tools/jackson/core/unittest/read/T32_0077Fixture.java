package tools.jackson.core.unittest.read;

import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0077Fixture {

    void assignedDecimalTextSpellingsHaveNoVpackWireSurface() {
        VPackFactory factory = new VPackFactory();
        String[][] sourceSpellings = {
                {"test2DecimalPointsInArray", "[ 1.5.00 ]"},
                {"leadingDotInDecimalAllowed", ".125"},
                {"trailingDotInDecimalAllowed", "125."},
                {"simpleUnquotedBytes", "{ a : 1 }"},
                {"simpleUnquotedChars", "{ a : 1 }"},
                {"largeUnquoted", "[{abc1050:true}]"},
                {"nonStandardNameChars", "{@type:true}"},
                {"unquotedIssue510", "{" + "a".repeat(4000) + "}"}
        };

        for (String[] sourceSpelling : sourceSpellings) {
            assertThrows(UnsupportedOperationException.class,
                    () -> factory.createParser(sourceSpelling[1]), sourceSpelling[0]);
        }
    }

    void __invoke_assignedDecimalTextSpellingsHaveNoVpackWireSurface() throws Exception {
        try {
            assignedDecimalTextSpellingsHaveNoVpackWireSurface();
        } finally {
        }
    }

}
