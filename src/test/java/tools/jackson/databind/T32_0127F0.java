package tools.jackson.databind;

import tools.jackson.core.JsonParser;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.cfg.DeserializationContexts;
import tools.jackson.databind.cfg.SerializationContexts;

import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0127F0 {
private static final byte[] POJO_X_9 = VPackWireFixtureTest.hex(
            "0b 07 01 41 78 39 03");
private static final byte[] POJO_X_7 = VPackWireFixtureTest.hex(
            "0b 07 01 41 78 37 03");
private static final byte[] DATA_INPUT_OBJECT = VPackWireFixtureTest.hex(
            "14 0e 41 61 31 41 62 13 06 31 32 33 03 02");
private static final byte[] DATA_OUTPUT_OBJECT = VPackWireFixtureTest.hex(
            "0b 07 01 41 61 31 03");

    void pojoReadingViaParserClassUsesLiteralVpack() throws Exception {
        VPackMapper mapper = new VPackMapper();
        try (JsonParser parser = mapper.createParser(POJO_X_9)) {
            Pojo pojo = parser.readValueAs(Pojo.class);
            assertEquals(9, pojo.x);
            assertEquals(null, parser.nextToken());
        }
    }

    void pojoReadingViaParserTypeReferenceUsesLiteralVpack() throws Exception {
        VPackMapper mapper = new VPackMapper();
        try (JsonParser parser = mapper.createParser(POJO_X_7)) {
            Pojo pojo = parser.readValueAs(new TypeReference<Pojo>() { });
            assertEquals(7, pojo.x);
        }
    }

    void pojoReadingViaParserJavaTypeUsesLiteralVpack() throws Exception {
        VPackMapper mapper = new VPackMapper();
        try (JsonParser parser = mapper.createParser(
                VPackWireFixtureTest.hex("0b 08 01 41 78 28 2a 03"))) {
            Pojo pojo = parser.readValueAs(mapper.constructType(Pojo.class));
            assertEquals(42, pojo.x);
        }
    }

    void treeReadingViaParserUsesLiteralVpack() throws Exception {
        VPackMapper mapper = new VPackMapper();
        try (JsonParser parser = mapper.createParser(POJO_X_9)) {
            JsonNode tree = parser.readValueAsTree();
            assertEquals(mapper.createObjectNode().put("x", 9), tree);
            assertEquals(null, parser.nextToken());
        }
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

    void __invoke_pojoReadingViaParserClassUsesLiteralVpack() throws Exception {
        try {
            pojoReadingViaParserClassUsesLiteralVpack();
        } finally {
        }
    }


    void __invoke_pojoReadingViaParserTypeReferenceUsesLiteralVpack() throws Exception {
        try {
            pojoReadingViaParserTypeReferenceUsesLiteralVpack();
        } finally {
        }
    }


    void __invoke_pojoReadingViaParserJavaTypeUsesLiteralVpack() throws Exception {
        try {
            pojoReadingViaParserJavaTypeUsesLiteralVpack();
        } finally {
        }
    }


    void __invoke_treeReadingViaParserUsesLiteralVpack() throws Exception {
        try {
            treeReadingViaParserUsesLiteralVpack();
        } finally {
        }
    }

}
