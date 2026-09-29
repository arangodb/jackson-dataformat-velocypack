package tools.jackson.databind;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.util.List;
import java.util.Map;

import tools.jackson.core.StreamReadFeature;
import tools.jackson.core.StreamWriteFeature;
import tools.jackson.core.TokenStreamFactory;
import tools.jackson.databind.DeserializationConfig;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.SerializationConfig;
import tools.jackson.databind.cfg.DeserializationContexts;
import tools.jackson.databind.cfg.SerializationContexts;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0127F1 {
private static final byte[] POJO_X_9 = VPackWireFixtureTest.hex(
            "0b 07 01 41 78 39 03");
private static final byte[] POJO_X_7 = VPackWireFixtureTest.hex(
            "0b 07 01 41 78 37 03");
private static final byte[] DATA_INPUT_OBJECT = VPackWireFixtureTest.hex(
            "14 0e 41 61 31 41 62 13 06 31 32 33 03 02");
private static final byte[] DATA_OUTPUT_OBJECT = VPackWireFixtureTest.hex(
            "0b 07 01 41 61 31 03");

    void vpackMapperFeatureDefaultsAndOverridesRemainVisible() {
        VPackMapper mapper = new VPackMapper();
        assertTrue(mapper.isEnabled(TokenStreamFactory.Feature.CANONICALIZE_PROPERTY_NAMES));
        assertTrue(mapper.isEnabled(StreamReadFeature.AUTO_CLOSE_SOURCE));
        assertTrue(mapper.isEnabled(StreamWriteFeature.AUTO_CLOSE_TARGET));

        VPackMapper overridden = VPackMapper.builder()
                .disable(StreamWriteFeature.FLUSH_PASSED_TO_STREAM)
                .build();
        assertFalse(overridden.isEnabled(StreamWriteFeature.FLUSH_PASSED_TO_STREAM));
    }

    void vpackMapperPropertySortingConfigurationMatchesDatabind() {
        VPackMapper mapper = new VPackMapper();
        assertEquals(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY.enabledByDefault(),
                mapper.isEnabled(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY));
        assertEquals(MapperFeature.SORT_CREATOR_PROPERTIES_FIRST.enabledByDefault(),
                mapper.isEnabled(MapperFeature.SORT_CREATOR_PROPERTIES_FIRST));

        SerializationConfig serialization = mapper.serializationConfig();
        assertEquals(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY.enabledByDefault(),
                serialization.isEnabled(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY));
        assertEquals(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY.enabledByDefault(),
                serialization.shouldSortPropertiesAlphabetically());
        assertEquals(MapperFeature.SORT_CREATOR_PROPERTIES_FIRST.enabledByDefault(),
                serialization.isEnabled(MapperFeature.SORT_CREATOR_PROPERTIES_FIRST));

        DeserializationConfig deserialization = mapper.deserializationConfig();
        assertEquals(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY.enabledByDefault(),
                deserialization.isEnabled(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY));
        assertEquals(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY.enabledByDefault(),
                deserialization.shouldSortPropertiesAlphabetically());
        assertEquals(MapperFeature.SORT_CREATOR_PROPERTIES_FIRST.enabledByDefault(),
                deserialization.isEnabled(MapperFeature.SORT_CREATOR_PROPERTIES_FIRST));

        mapper = VPackMapper.builder()
                .enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
                .disable(MapperFeature.SORT_CREATOR_PROPERTIES_FIRST)
                .build();
        assertTrue(mapper.isEnabled(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY));
        assertFalse(mapper.isEnabled(MapperFeature.SORT_CREATOR_PROPERTIES_FIRST));
        assertTrue(mapper.serializationConfig().shouldSortPropertiesAlphabetically());
        assertTrue(mapper.deserializationConfig().isEnabled(
                MapperFeature.SORT_PROPERTIES_ALPHABETICALLY));
        assertFalse(mapper.deserializationConfig().isEnabled(
                MapperFeature.SORT_CREATOR_PROPERTIES_FIRST));
    }

    void dataInputViaMapperUsesLiteralVpackForMapperReaderAndTree() throws Exception {
        VPackMapper mapper = new VPackMapper();
        DataInputStream input = new DataInputStream(
                new ByteArrayInputStream(DATA_INPUT_OBJECT));
        Map<?, ?> map = mapper.readValue((DataInput) input, Map.class);
        assertEquals(Integer.valueOf(1), map.get("a"));

        input = new DataInputStream(new ByteArrayInputStream(DATA_INPUT_OBJECT));
        map = mapper.readerFor(Map.class).readValue((DataInput) input);
        assertEquals(Integer.valueOf(1), map.get("a"));

        input = new DataInputStream(new ByteArrayInputStream(DATA_INPUT_OBJECT));
        JsonNode tree = mapper.readerFor(Map.class).readTree((DataInput) input);
        assertNotNull(tree);
        assertEquals(1, tree.get("a").intValue());
        assertEquals(3, tree.get("b").size());
        assertEquals(1, tree.get("b").get(0).intValue());
        assertEquals(2, tree.get("b").get(1).intValue());
        assertEquals(3, tree.get("b").get(2).intValue());
    }

    void dataOutputViaMapperWritesCanonicalLiteralVpack() throws Exception {
        VPackMapper mapper = new VPackMapper();
        tools.jackson.databind.node.ObjectNode input = mapper.createObjectNode();
        input.put("a", 1);

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (DataOutputStream data = new DataOutputStream(bytes)) {
            mapper.writeValue((DataOutput) data, input);
        }
        assertArrayEquals(DATA_OUTPUT_OBJECT, bytes.toByteArray());

        bytes.reset();
        try (DataOutputStream data = new DataOutputStream(bytes)) {
            mapper.writer().writeValue((DataOutput) data, input);
        }
        assertArrayEquals(DATA_OUTPUT_OBJECT, bytes.toByteArray());
    }

    void deserializationContextCacheIsFilledAndFlushedForVpack() throws Exception {
        CacheMapper mapper = new CacheMapper();
        assertEquals(0, mapper.deserializerCount());

        Pojo pojo = mapper.readValue(POJO_X_9, Pojo.class);
        assertNotNull(pojo);
        assertEquals(2, mapper.deserializerCount());
        mapper.flushDeserializers();
        assertEquals(0, mapper.deserializerCount());

        mapper = new CacheMapper();
        List<?> values = mapper.readValue(new byte[] { 0x01 }, List.class);
        assertNotNull(values);
        assertEquals(4, mapper.deserializerCount());
    }

    void clearCachesDropsVpackMapperCaches() throws Exception {
        CacheMapper mapper = new CacheMapper();
        assertEquals(0, mapper.deserializerCount());
        assertEquals(0, mapper.rootDeserializerCount());
        assertEquals(0, mapper.serializerCount());

        assertNotNull(mapper.readValue(POJO_X_9, Pojo.class));
        mapper.writeValueAsBytes("test");
        assertNotEquals(0, mapper.rootDeserializerCount());
        assertNotEquals(0, mapper.deserializerCount());
        assertNotEquals(0, mapper.serializerCount());

        mapper.clearCaches();
        assertEquals(0, mapper.deserializerCount());
        assertEquals(0, mapper.rootDeserializerCount());
        assertEquals(0, mapper.serializerCount());
    }
private static final class CacheMapper extends VPackMapper {
        int deserializerCount() {
            return ((DeserializationContexts.DefaultImpl) _deserializationContexts)
                    .cacheForTests().cachedDeserializersCount();
        }

        int rootDeserializerCount() {
            return _rootDeserializers.size();
        }

        int serializerCount() {
            return ((SerializationContexts.DefaultImpl) _serializationContexts)
                    .cacheForTests().size();
        }

        void flushDeserializers() {
            ((DeserializationContexts.DefaultImpl) _deserializationContexts)
                    .cacheForTests().flushCachedDeserializers();
        }
    }
static class Pojo {
        int x;

        public void setX(int value) {
            x = value;
        }
    }

    void __invoke_vpackMapperFeatureDefaultsAndOverridesRemainVisible() throws Exception {
        try {
            vpackMapperFeatureDefaultsAndOverridesRemainVisible();
        } finally {
        }
    }


    void __invoke_vpackMapperPropertySortingConfigurationMatchesDatabind() throws Exception {
        try {
            vpackMapperPropertySortingConfigurationMatchesDatabind();
        } finally {
        }
    }


    void __invoke_dataInputViaMapperUsesLiteralVpackForMapperReaderAndTree() throws Exception {
        try {
            dataInputViaMapperUsesLiteralVpackForMapperReaderAndTree();
        } finally {
        }
    }


    void __invoke_dataOutputViaMapperWritesCanonicalLiteralVpack() throws Exception {
        try {
            dataOutputViaMapperWritesCanonicalLiteralVpack();
        } finally {
        }
    }


    void __invoke_deserializationContextCacheIsFilledAndFlushedForVpack() throws Exception {
        try {
            deserializationContextCacheIsFilledAndFlushedForVpack();
        } finally {
        }
    }


    void __invoke_clearCachesDropsVpackMapperCaches() throws Exception {
        try {
            clearCachesDropsVpackMapperCaches();
        } finally {
        }
    }

}
