package tools.jackson.core.unittest;

import tools.jackson.core.ErrorReportConfiguration;
import tools.jackson.core.io.ContentReference;
import tools.jackson.core.TokenStreamLocation;
import static org.junit.jupiter.api.Assertions.assertEquals;

class T32_0002F1 {

    void testBasicToStringErrorConfig() {
        byte[] source = { 0x18, 0x1a, 0x1b };
        for (int maxRawContentLength : new int[] { 0, 2, 3, 4 }) {
            ErrorReportConfiguration config = ErrorReportConfiguration.builder()
                    .maxRawContentLength(maxRawContentLength).build();
            ContentReference reference = ContentReference.construct(
                    false, source, 0, source.length, config);
            String location = new TokenStreamLocation(reference, 10L, 10L, 1, 1).toString();
            assertEquals("[Source: (byte[])[3 bytes]; byte offset: #10]", location);
        }
    }

    void __invoke_testBasicToStringErrorConfig() throws Exception {
        try {
            testBasicToStringErrorConfig();
        } finally {
        }
    }

}
