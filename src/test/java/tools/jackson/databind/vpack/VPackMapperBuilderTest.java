package tools.jackson.databind.vpack;

import org.junit.jupiter.api.Test;
import tools.jackson.core.StreamReadFeature;
import tools.jackson.core.StreamWriteFeature;
import tools.jackson.databind.*;
import tools.jackson.databind.testutil.DatabindTestUtil;
import com.arangodb.jackson.dataformat.velocypack.*;
import com.arangodb.jackson.dataformat.velocypack.VPackMapper;

import static org.junit.jupiter.api.Assertions.*;

// Test(s) to verify behaviors in VPackMapper.Builder
public class VPackMapperBuilderTest extends DatabindTestUtil
{
    @Test
    public void testBuilderWithJackson2Defaults()
    {
        // NOTE: VPackMapper.builderWithJackson2Defaults() not available for VelocyPack
        // This test is not applicable to VelocyPack format
        fail("Not applicable to VelocyPack: builderWithJackson2Defaults() not available");
    }

    // Test 1: Builder with stream read features
    @Test
    public void testBuilderWithStreamReadFeatures() {
        VPackMapper mapper = VPackMapper.builder()
            .enable(StreamReadFeature.STRICT_DUPLICATE_DETECTION)
            .disable(StreamReadFeature.AUTO_CLOSE_SOURCE)
            .configure(StreamReadFeature.IGNORE_UNDEFINED, true)
            .build();

        assertNotNull(mapper);
        assertTrue(mapper.isEnabled(StreamReadFeature.STRICT_DUPLICATE_DETECTION));
        assertFalse(mapper.isEnabled(StreamReadFeature.AUTO_CLOSE_SOURCE));
        assertTrue(mapper.isEnabled(StreamReadFeature.IGNORE_UNDEFINED));
    }

    @Test
    public void testBuilderWithStreamWriteFeatures() {
        VPackMapper mapper = VPackMapper.builder()
            .enable(StreamWriteFeature.STRICT_DUPLICATE_DETECTION)
            .disable(StreamWriteFeature.AUTO_CLOSE_TARGET)
            .configure(StreamWriteFeature.IGNORE_UNKNOWN, true)
            .build();

        assertNotNull(mapper);
        assertTrue(mapper.isEnabled(StreamWriteFeature.STRICT_DUPLICATE_DETECTION));
        assertFalse(mapper.isEnabled(StreamWriteFeature.AUTO_CLOSE_TARGET));
        assertTrue(mapper.isEnabled(StreamWriteFeature.IGNORE_UNKNOWN));
    }

    @Test
    public void testBuilderWithJsonReadFeatures() {
        VPackMapper mapper = VPackMapper.builder()
        // REMOVED: JSON-only feature not applicable to VelocyPack
        // .enable(VPackReadFeature.ALLOW_JAVA_COMMENTS)
        // REMOVED: JSON-only feature not applicable to VelocyPack
        // .disable(VPackReadFeature.ALLOW_MISSING_VALUES)
        // REMOVED: JSON-only feature not applicable to VelocyPack
        // .configure(VPackReadFeature.ALLOW_TRAILING_COMMA, false)
        // REMOVED: JSON-only feature not applicable to VelocyPack
        // .configure(VPackReadFeature.ALLOW_SINGLE_QUOTES, true)
            .build();

        assertNotNull(mapper);
        // REMOVED: JSON-only feature not applicable to VelocyPack
        // assertTrue(mapper.isEnabled(VPackReadFeature.ALLOW_JAVA_COMMENTS));
        // REMOVED: JSON-only feature not applicable to VelocyPack
        // assertFalse(mapper.isEnabled(VPackReadFeature.ALLOW_MISSING_VALUES));
        // REMOVED: JSON-only feature not applicable to VelocyPack
        // assertFalse(mapper.isEnabled(VPackReadFeature.ALLOW_TRAILING_COMMA));
        // REMOVED: JSON-only feature not applicable to VelocyPack
        // assertTrue(mapper.isEnabled(VPackReadFeature.ALLOW_SINGLE_QUOTES));
    }

    @Test
    public void testBuilderWithJsonWriteFeatures() {
        // NOTE: JSON-specific write features (ESCAPE_NON_ASCII, QUOTE_PROPERTY_NAMES, ESCAPE_FORWARD_SLASHES,
        // WRITE_HEX_UPPER_CASE) do not exist in VPackWriteFeature. Test is not applicable to VelocyPack.
        fail("Not applicable to VelocyPack: JSON-specific write features not available");
    }

    // Test 2: Builder with mapper features
    @Test
    public void testBuilderWithMapperFeatures() {
        VPackMapper mapper = VPackMapper.builder()
            .enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
            .disable(MapperFeature.USE_GETTERS_AS_SETTERS)
            .build();

        assertNotNull(mapper);
        assertTrue(mapper.isEnabled(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY));
        assertFalse(mapper.isEnabled(MapperFeature.USE_GETTERS_AS_SETTERS));
    }

    // Test 3: Builder with multiple feature configurations
    @Test
    public void testBuilderWithMultipleFeatures() {
        VPackMapper mapper = VPackMapper.builder()
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .enable(SerializationFeature.INDENT_OUTPUT)
            .build();

        assertNotNull(mapper);
        assertTrue(mapper.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY));
        assertFalse(mapper.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertTrue(mapper.isEnabled(SerializationFeature.INDENT_OUTPUT));
    }

    // Test 4: Builder configuration inheritance
    @Test
    public void testBuilderConfigurationChaining() {
        VPackMapper.Builder builder = VPackMapper.builder();
        builder.enable(SerializationFeature.INDENT_OUTPUT);
        builder.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        VPackMapper mapper = builder.build();
        assertTrue(mapper.isEnabled(SerializationFeature.INDENT_OUTPUT));
        assertFalse(mapper.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
    }

    // Test 5: Builder creates independent mappers
    @Test
    public void testBuilderCreatesIndependentMappers() {
        VPackMapper.Builder builder = VPackMapper.builder();
        VPackMapper mapper1 = builder.build();
        VPackMapper mapper2 = builder.build();

        // Mappers should be different instances
        assertNotSame(mapper1, mapper2);

        // But should have same configuration
        assertEquals(mapper1.isEnabled(SerializationFeature.INDENT_OUTPUT),
                     mapper2.isEnabled(SerializationFeature.INDENT_OUTPUT));
    }
}
