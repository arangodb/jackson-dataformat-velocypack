package tools.jackson.core.unittest;

import tools.jackson.core.ErrorReportConfiguration;
import tools.jackson.core.TokenStreamLocation;
import tools.jackson.core.io.ContentReference;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class T32_0005F0 {

    void locationEquality() {
        byte[] source = { 'B', 'O', 'G', 'U', 'S' };
        ErrorReportConfiguration config = ErrorReportConfiguration.defaults();

        TokenStreamLocation first = new TokenStreamLocation(
                ContentReference.construct(false, source, 0, 5, config),
                5L, 0L, 1, 2);
        TokenStreamLocation same = new TokenStreamLocation(
                ContentReference.construct(false, source, 0, 5, config),
                5L, 0L, 1, 2);
        assertEquals(first, same);

        TokenStreamLocation differentSlice = new TokenStreamLocation(
                ContentReference.construct(false, source, 1, 4, config),
                5L, 0L, 1, 2);
        assertNotEquals(first, differentSlice);
        assertNotEquals(differentSlice, first);
    }

    void __invoke_locationEquality() throws Exception {
        try {
            locationEquality();
        } finally {
        }
    }

}
