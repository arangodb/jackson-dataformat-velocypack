package tools.jackson.databind.cfg;

import org.junit.jupiter.api.Test;
import tools.jackson.core.StreamWriteFeature;
import tools.jackson.databind.*;
import com.arangodb.jackson.dataformat.velocypack.VPackMapper;
import com.arangodb.jackson.dataformat.velocypack.VPackWriteFeature;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class SerializationConfigTest
{
    private final ObjectMapper MAPPER = new VPackMapper();

    @Test
    public void testSerConfig() throws Exception
    {
        SerializationConfig config = MAPPER.serializationConfig();
        assertFalse(config.hasSerializationFeatures(SerializationFeature.FAIL_ON_EMPTY_BEANS.getMask()));
        assertFalse(config.hasSerializationFeatures(SerializationFeature.CLOSE_CLOSEABLE.getMask()));
        assertEquals(ConfigOverrides.INCLUDE_DEFAULT, config.getDefaultPropertyInclusion());
        assertEquals(ConfigOverrides.INCLUDE_DEFAULT, config.getDefaultPropertyInclusion(String.class));
        assertFalse(config.useRootWrapping());

        assertNotSame(config, config.with(SerializationFeature.INDENT_OUTPUT,
                SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS));

        assertSame(config, config.withRootName((PropertyName) null)); // defaults to 'none'

        SerializationConfig newConfig = config.withRootName(PropertyName.construct("foobar"));
        assertNotSame(config, newConfig);
        assertTrue(newConfig.useRootWrapping());

        assertSame(config, config.with(config.getAttributes()));
        assertNotSame(config, config.with(new ContextAttributes.Impl(Collections.singletonMap("a", "b"))));

//        assertNotNull(config.introspectDirectClassAnnotations(getClass()));
    }

    @Test
    public void testStreamWriteFeatures() throws Exception
    {
        SerializationConfig config = MAPPER.serializationConfig();
        // REMOVED: JSON-only feature not applicable to VelocyPack
        // assertFalse(config.hasFormatFeature(VPackWriteFeature.ESCAPE_NON_ASCII));
        // REMOVED: JSON-only feature not applicable to VelocyPack
        // assertNotSame(config, config.with(VPackWriteFeature.ESCAPE_NON_ASCII));
        SerializationConfig newConfig = config.withFeatures(StreamWriteFeature.IGNORE_UNKNOWN);
        assertNotSame(config, newConfig);
        assertTrue(newConfig.isEnabled(StreamWriteFeature.IGNORE_UNKNOWN));

        // no change to settings, same object:
        // REMOVED: JSON-only feature not applicable to VelocyPack
        // assertSame(config, config.without(VPackWriteFeature.ESCAPE_NON_ASCII));
        assertSame(config, config.withoutFeatures(StreamWriteFeature.IGNORE_UNKNOWN));
    }

    @Test
    public void testFormatFeatures() throws Exception
    {
        final VPackWriteFeature LENIENT_UTF_ENCODING = VPackWriteFeature.LENIENT_UTF_ENCODING;
        final VPackWriteFeature WRITE_OBJECT_KEYS_SORTED = VPackWriteFeature.WRITE_OBJECT_KEYS_SORTED;
        SerializationConfig config = MAPPER.serializationConfig();
        // feature that is NOT enabled by default
        SerializationConfig config2 = config.with(LENIENT_UTF_ENCODING);
        assertNotSame(config, config2);
        // and then with one that IS enabled by default:
        SerializationConfig config3 = config.withFeatures(LENIENT_UTF_ENCODING, WRITE_OBJECT_KEYS_SORTED);
        assertNotSame(config, config3);

        assertNotSame(config3, config3.without(WRITE_OBJECT_KEYS_SORTED));
        assertNotSame(config3, config3.withoutFeatures(LENIENT_UTF_ENCODING,
                WRITE_OBJECT_KEYS_SORTED));
    }
}
