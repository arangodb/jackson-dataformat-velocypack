package tools.jackson.databind.deser;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonDeserializeAs;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.node.ObjectNode;
import tools.jackson.databind.util.NameTransformer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0186F0 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] UNWRAPPED_RECORD = VPackWireFixtureTest.hex(
            "14 4f 49 76 65 72 73 69 6f 6e 49 64 4a 76 65 72 73 69 6f 6e 2d 69 64 "
          + "47 76 65 72 73 69 6f 6e 41 31 47 74 69 74 6c 65 45 6e 48 74 69 74 6c 65 "
          + "20 65 6e 4c 73 63 6f 70 65 43 6c 61 73 73 49 64 4e 73 63 6f 70 65 2d 63 "
          + "6c 61 73 73 2d 69 64 04");
private static final byte[] UNWRAPPED_WITH_UNKNOWN = VPackWireFixtureTest.hex(
            "14 44 49 76 65 72 73 69 6f 6e 49 64 4a 76 65 72 73 69 6f 6e 2d 69 64 "
          + "47 76 65 72 73 69 6f 6e 41 31 47 74 69 74 6c 65 45 6e 48 74 69 74 6c 65 "
          + "20 65 6e 4e 74 6f 74 61 6c 6c 79 55 6e 6b 6e 6f 77 6e 41 78 04");
private static final byte[] STRINGS_ARRAY = VPackWireFixtureTest.hex(
            "14 13 47 73 74 72 69 6e 67 73 13 08 44 74 65 73 74 01 01");
private static final byte[] EMPTY_STRINGS_ARRAY = VPackWireFixtureTest.hex(
            "14 0c 47 73 74 72 69 6e 67 73 01 01");
private static final byte[] INVALID_MAP_KEY = VPackWireFixtureTest.hex(
            "14 0b 43 31 32 33 43 78 78 78 01");
private static final byte[] LIST_CONTENT = VPackWireFixtureTest.hex(
            "14 0f 44 6c 69 73 74 13 07 43 61 62 63 01 01");
private static final byte[] ARRAY_CONTENT = VPackWireFixtureTest.hex(
            "14 0e 44 64 61 74 61 13 06 31 32 33 03 01");

    void unwrappedRecordCreatorPropertiesShouldReceiveFlattenedFields() throws Exception {
        ConsentVersion result = MAPPER.readValue(UNWRAPPED_RECORD, ConsentVersion.class);

        assertTrue(result.title().rawFields().containsKey("En"));
        assertEquals("title en", result.title().rawFields().get("En"));
        assertEquals("version-id", result.versionId());
        assertEquals("scope-class-id", result.scopeClassId());
    }

    void opaqueUnwrapperAbsorbsUnknownsEvenWithFailOnUnknown() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();

        ConsentVersion result = mapper.readValue(UNWRAPPED_WITH_UNKNOWN,
                ConsentVersion.class);
        assertEquals("title en", result.title().rawFields().get("En"));
    }
static record ConsentVersion(
            @JsonProperty String versionId,
            @JsonProperty String version,
            @JsonUnwrapped(prefix = "title")
            @JsonDeserialize(using = CapturingUnwrappedValueDeserializer.class)
            CapturingUnwrappedValue title,
            String scopeClassId) { }
static record CapturingUnwrappedValue(Map<String, Serializable> rawFields) { }
static final class CapturingUnwrappedValueDeserializer
            extends ValueDeserializer<CapturingUnwrappedValue> {
        private final NameTransformer unwrapper;

        CapturingUnwrappedValueDeserializer() {
            this(null);
        }

        private CapturingUnwrappedValueDeserializer(NameTransformer unwrapper) {
            this.unwrapper = unwrapper;
        }

        @Override
        public CapturingUnwrappedValue deserialize(JsonParser p, DeserializationContext ctxt) {
            var node = ctxt.readTree(p);
            if (!(node instanceof ObjectNode objectNode)) {
                return new CapturingUnwrappedValue(Map.of());
            }
            var values = new LinkedHashMap<String, Serializable>();
            for (var entry : objectNode.properties()) {
                var key = (unwrapper == null) ? entry.getKey()
                        : unwrapper.reverse(entry.getKey());
                if (key == null || key.isBlank()) {
                    continue;
                }
                var value = ctxt.readTreeAsValue(entry.getValue(), Object.class);
                if (value instanceof Serializable serializableValue) {
                    values.put(key, serializableValue);
                }
            }
            return new CapturingUnwrappedValue(values);
        }

        @Override
        public ValueDeserializer<?> createContextual(DeserializationContext context,
                BeanProperty property) {
            return this;
        }

        @Override
        public ValueDeserializer<CapturingUnwrappedValue> unwrappingDeserializer(
                DeserializationContext context, NameTransformer unwrapper) {
            return new CapturingUnwrappedValueDeserializer(unwrapper);
        }
    }
static final class ArrayHolder {
        String[] strings;

        @JsonDeserialize(as = String[].class)
        public void setStrings(Object[] value) {
            strings = (String[]) value;
        }
    }
static final class ArrayHolderNew {
        String[] strings;

        @JsonDeserializeAs(String[].class)
        public void setStrings(Object[] value) {
            strings = (String[]) value;
        }
    }
static final class BrokenCollectionHolder {
        @JsonDeserialize(as = String.class)
        public void setStrings(Collection<String> value) { }
    }
static final class CollectionHolder {
        Collection<String> strings;

        @JsonDeserialize(as = TreeSet.class)
        public void setStrings(Collection<String> value) {
            strings = value;
        }
    }
static final class CollectionHolderNew {
        Collection<String> strings;

        @JsonDeserializeAs(TreeSet.class)
        public void setStrings(Collection<String> value) {
            strings = value;
        }
    }
static final class ArrayContentHolder {
        Object[] data;

        @JsonDeserialize(contentAs = Long.class)
        public void setData(Object[] value) {
            data = value;
        }
    }
static final class ArrayContentHolderNew {
        Object[] data;

        @JsonDeserializeAs(content = Long.class)
        public void setData(Object[] value) {
            data = value;
        }
    }
static final class ListContentHolder {
        List<?> list;

        @JsonDeserialize(contentAs = StringWrapper.class)
        public void setList(List<?> value) {
            list = value;
        }
    }
static final class ListContentHolderNew {
        List<?> list;

        @JsonDeserializeAs(content = StringWrapper.class)
        public void setList(List<?> value) {
            list = value;
        }
    }
static final class StringWrapper {
        final String value;

        public StringWrapper(String value) {
            this.value = value;
        }
    }
static final class BrokenMapKeyHolder {
        @JsonDeserialize(keyAs = Integer.class)
        public void setStrings(Map<String, String> value) { }
    }

    void __invoke_unwrappedRecordCreatorPropertiesShouldReceiveFlattenedFields() throws Exception {
        try {
            unwrappedRecordCreatorPropertiesShouldReceiveFlattenedFields();
        } finally {
        }
    }


    void __invoke_opaqueUnwrapperAbsorbsUnknownsEvenWithFailOnUnknown() throws Exception {
        try {
            opaqueUnwrapperAbsorbsUnknownsEvenWithFailOnUnknown();
        } finally {
        }
    }

}
