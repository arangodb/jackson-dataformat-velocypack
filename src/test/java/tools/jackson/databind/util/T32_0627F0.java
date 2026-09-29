package tools.jackson.databind.util;

import java.util.Locale;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.util.JsonParserSequence;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.util.TokenBuffer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0627F0 {
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

    // Provenance: JsonParserSequenceTest#testJsonParserSequenceOverridesSkipChildren().
    void parserSequenceSkipChildrenSwitchesAcrossTokenBuffers() throws Exception {
        TokenBuffer firstBuffer = TokenBuffer.forGeneration();
        firstBuffer.writeStartObject();
        firstBuffer.writeName("foo");
        firstBuffer.writeStartObject();
        TokenBuffer secondBuffer = TokenBuffer.forGeneration();
        secondBuffer.writeEndObject();
        secondBuffer.writeEndObject();
        try (JsonParser first = firstBuffer.asParser(ObjectReadContext.empty());
                JsonParser second = secondBuffer.asParser(ObjectReadContext.empty());
                JsonParserSequence sequence = JsonParserSequence.createFlattened(false, first, second)) {
            assertEquals(JsonToken.START_OBJECT, sequence.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, sequence.nextToken());
            assertEquals(JsonToken.START_OBJECT, sequence.nextToken());
            sequence.skipChildren();
            assertEquals(JsonToken.END_OBJECT, sequence.nextToken());
        }
    }

    void __invoke_parserSequenceSkipChildrenSwitchesAcrossTokenBuffers() throws Exception {
        switchToTurkishLocale();
        try {
            parserSequenceSkipChildrenSwitchesAcrossTokenBuffers();
        } finally {
            restoreLocale();
        }
    }

}
