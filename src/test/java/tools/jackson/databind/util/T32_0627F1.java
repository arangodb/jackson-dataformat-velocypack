package tools.jackson.databind.util;

import java.util.Locale;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.util.NameTransformer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0627F1 {
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

    // Provenance: NameTransformerTest#testSimpleTransformer().
    void simpleNameTransformerPreservesPrefixSuffixAndReverse() {
        NameTransformer transformer = NameTransformer.simpleTransformer("a", null);
        assertEquals("aFoo", transformer.transform("Foo"));
        assertEquals("Foo", transformer.reverse("aFoo"));

        transformer = NameTransformer.simpleTransformer(null, "++");
        assertEquals("foo++", transformer.transform("foo"));
        assertEquals("foo", transformer.reverse("foo++"));

        transformer = NameTransformer.simpleTransformer("(", ")");
        assertEquals("(foo)", transformer.transform("foo"));
        assertEquals("foo", transformer.reverse("(foo)"));
    }

    void __invoke_simpleNameTransformerPreservesPrefixSuffixAndReverse() throws Exception {
        switchToTurkishLocale();
        try {
            simpleNameTransformerPreservesPrefixSuffixAndReverse();
        } finally {
            restoreLocale();
        }
    }

}
