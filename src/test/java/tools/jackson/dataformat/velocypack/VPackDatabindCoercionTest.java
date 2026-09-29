package tools.jackson.dataformat.velocypack;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.cfg.CoercionAction;
import tools.jackson.databind.cfg.CoercionInputShape;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.exc.UnrecognizedPropertyException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Effective read/write context behavior for ordinary creators and coercions. */
class VPackDatabindCoercionTest {
    private final VPackMapper mapper = new VPackMapper();

    @Test
    // Equivalent coverage for CreatorWithNamingStrategyTest#testRenameViaCtor.
    void propertiesCreatorUsesNamingAndWriterPreservesDeclaredPropertyOrder() throws Exception {
        VPackMapper snakeCase = VPackMapper.builder()
                .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
                .build();
        CreatedPojo input = new CreatedPojo("Ada", 7);

        CreatedPojo output = snakeCase.readValue(snakeCase.writeValueAsBytes(input),
                CreatedPojo.class);
        assertEquals(input, output);
        assertEquals(new NamingRecord("Ada", 7),
                snakeCase.readValue(bytes("first_name", "Ada", "count", 7),
                        NamingRecord.class));

        byte[] bytes = snakeCase.writeValueAsBytes(input);
        try (VPackParser parser = (VPackParser) snakeCase.tokenStreamFactory().createParser(bytes)) {
            assertEquals(tools.jackson.core.JsonToken.START_OBJECT, parser.nextToken());
            assertEquals("first_name", parser.nextName());
            assertEquals(tools.jackson.core.JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("count", parser.nextName());
            assertEquals(tools.jackson.core.JsonToken.VALUE_NUMBER_INT, parser.nextToken());
        }
    }

    @Test
    // Equivalent coverage for ObjectReaderTest#testUnknownFields.
    void unknownFieldsFollowEffectiveReadContext() throws Exception {
        byte[] bytes = bytes("first_name", "Ada", "count", 7, "ignored", true);

        assertEquals(new CreatedPojo("Ada", 7), mapper.readValue(bytes, CreatedPojo.class));

        VPackMapper strict = VPackMapper.builder()
                .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();
        assertThrows(UnrecognizedPropertyException.class,
                () -> strict.readValue(bytes, CreatedPojo.class));
    }

    @Test
    // Equivalent coverage for CreatorNullPrimitivesTest#testCreatorNullPrimitive and
    // CoerceContainersTest#testIntArray, narrowed to ordinary VPack scalar coercion.
    void creatorNullsAndStringCoercionAreConfigurable() throws Exception {
        byte[] nullCount = bytes("first_name", "Ada", "count", null);
        assertThrows(MismatchedInputException.class,
                () -> mapper.readValue(nullCount, CreatedPojo.class));

        VPackMapper allowPrimitiveNull = VPackMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
                .build();
        assertEquals(new CreatedPojo("Ada", 0),
                allowPrimitiveNull.readValue(nullCount, CreatedPojo.class));

        byte[] stringCount = bytes("first_name", "Ada", "count", "8");
        assertEquals(new CreatedPojo("Ada", 8), mapper.readValue(stringCount, CreatedPojo.class));

        VPackMapper rejectStringCoercion = VPackMapper.builder()
                .withCoercionConfigDefaults(cfg -> cfg.setCoercion(
                        CoercionInputShape.String, CoercionAction.Fail))
                .build();
        assertThrows(MismatchedInputException.class,
                () -> rejectStringCoercion.readValue(stringCount, CreatedPojo.class));
    }

    private byte[] bytes(Object... fields) throws Exception {
        Map<String, Object> values = new LinkedHashMap<>();
        for (int i = 0; i < fields.length; i += 2) values.put((String) fields[i], fields[i + 1]);
        return mapper.writeValueAsBytes(values);
    }

    @JsonPropertyOrder({ "first_name", "count" })
    static final class CreatedPojo {
        private final String firstName;
        private final int count;

        @JsonCreator
        CreatedPojo(@JsonProperty("first_name") String firstName,
                @JsonProperty("count") int count) {
            this.firstName = firstName;
            this.count = count;
        }

        @JsonProperty("first_name")
        public String firstName() { return firstName; }

        @JsonProperty("count")
        public int count() { return count; }

        @Override
        public boolean equals(Object other) {
            return other instanceof CreatedPojo that
                    && count == that.count && java.util.Objects.equals(firstName, that.firstName);
        }

        @Override
        public int hashCode() { return java.util.Objects.hash(firstName, count); }
    }

    record NamingRecord(String firstName, int count) { }
}
