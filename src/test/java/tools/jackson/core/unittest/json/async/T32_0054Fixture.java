package tools.jackson.core.unittest.json.async;

import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0054Fixture {

    void jsonNonStandardParsingHasNoVpackWireSurface() {
        VPackFactory factory = new VPackFactory();

        String[][] sourceSpellings = {
                {"aposQuotingEnabled", "{'a': 'b'}"},
                {"largeUnquotedNames", "[{abc1050:true}]"},
                {"nonStandarBackslashQuotingForValues", "'\\''"},
                {"nonStandardNameChars", "{@type: true}"},
                {"simpleUnquotedNames", "{a: 1}"},
                {"singleQuotesEscaped", "['16\\'']"}
        };

        for (String[] sourceSpelling : sourceSpellings) {
            assertThrows(UnsupportedOperationException.class,
                    () -> factory.createParser(sourceSpelling[1]), sourceSpelling[0]);
        }
    }

    void __invoke_jsonNonStandardParsingHasNoVpackWireSurface() throws Exception {
        try {
            jsonNonStandardParsingHasNoVpackWireSurface();
        } finally {
        }
    }

}
