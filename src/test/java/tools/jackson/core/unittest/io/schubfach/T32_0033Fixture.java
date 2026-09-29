package tools.jackson.core.unittest.io.schubfach;

import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0033Fixture {

    void schubfachTextSpellingIsNotVpackWire() {
        VPackFactory factory = new VPackFactory();

        // Representative source spellings: fixed-point boundary, exponent
        // boundary, a shortest-decimal regression, and a non-finite value.
        assertThrows(UnsupportedOperationException.class,
                () -> factory.createParser("1.0E7"));
        assertThrows(UnsupportedOperationException.class,
                () -> factory.createParser("0.0009999999"));
        assertThrows(UnsupportedOperationException.class,
                () -> factory.createParser("4.7223665E21"));
        assertThrows(UnsupportedOperationException.class,
                () -> factory.createParser("NaN"));
    }

    void __invoke_schubfachTextSpellingIsNotVpackWire() throws Exception {
        try {
            schubfachTextSpellingIsNotVpackWire();
        } finally {
        }
    }

}
