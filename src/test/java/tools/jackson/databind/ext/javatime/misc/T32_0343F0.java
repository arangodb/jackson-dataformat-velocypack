package tools.jackson.databind.ext.javatime.misc;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import tools.jackson.core.JsonParser;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.jsonFormatVisitors.JsonAnyFormatVisitor;
import tools.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
import tools.jackson.databind.jsonFormatVisitors.JsonBooleanFormatVisitor;
import tools.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import tools.jackson.databind.jsonFormatVisitors.JsonIntegerFormatVisitor;
import tools.jackson.databind.jsonFormatVisitors.JsonMapFormatVisitor;
import tools.jackson.databind.jsonFormatVisitors.JsonNullFormatVisitor;
import tools.jackson.databind.jsonFormatVisitors.JsonNumberFormatVisitor;
import tools.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import tools.jackson.databind.jsonFormatVisitors.JsonStringFormatVisitor;
import tools.jackson.databind.jsonFormatVisitors.JsonValueFormat;

import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0343F0 {
private static final TypeReference<Map<ZonedDateTime, String>> ZONED_DATE_TIME_KEY_MAP =
            new TypeReference<Map<ZonedDateTime, String>>() { };
private static final ZonedDateTime DATE_TIME_0 = ZonedDateTime.ofInstant(
            java.time.Instant.ofEpochSecond(0), ZoneOffset.UTC);
private static final ZonedDateTime DATE_TIME_1 = ZonedDateTime.of(
            2015, 3, 14, 9, 26, 53, 590 * 1_000_000, ZoneOffset.UTC);
private static final ZonedDateTime DATE_TIME_2 = ZonedDateTime.of(
            2015, 3, 14, 9, 26, 53, 590 * 1_000_000, ZoneId.of("Europe/Budapest"));
private static final ZonedDateTime DATE_TIME_2_OFFSET = DATE_TIME_2.withZoneSameInstant(
            ZoneOffset.ofHours(1));
private static final byte[] DATE_TIME_2_KEY = VPackWireFixtureTest.hex(
            "0b 26 01 5c 32 30 31 35 2d 30 33 2d 31 34 54 30 39 3a 32 36 3a 35 33 2e 35 39"
                    + "2b 30 31 3a 30 30 44 74 65 73 74 03");
private static final byte[] DATE_TIME_NANOS_KEY = VPackWireFixtureTest.hex(
            "0b 1e 01 54 31 34 32 36 33 32 35 32 31 33 2e 35 39 30 30 30 30 30 30 30"
                    + "44 74 65 73 74 03");
private static final byte[] DATE_TIME_MILLIS_KEY = VPackWireFixtureTest.hex(
            "0b 17 01 4d 31 34 32 36 33 32 35 32 31 33 35 39 30 44 74 65 73 74 03");

    // Provenance: DateTimeExceptionTest#testDateTimeExceptionRoundtrip.
    void dateTimeExceptionRoundtripVpack() throws Exception {
        DateTimeException result = new VPackMapper().readValue(
                new VPackMapper().writeValueAsBytes(new DateTimeException("Test!")),
                DateTimeException.class);
        assertEquals("Test!", result.getMessage());
    }
private static Map<String, String> visit(ObjectMapper mapper, Class<?> type) {
        Map<String, String> properties = new HashMap<>();
        mapper.writer().acceptJsonFormatVisitor(type, new VisitorWrapper(null, "", properties));
        return properties;
    }
private static Map<String, String> visit(tools.jackson.databind.ObjectWriter writer, Class<?> type) {
        Map<String, String> properties = new HashMap<>();
        writer.acceptJsonFormatVisitor(type, new VisitorWrapper(null, "", properties));
        return properties;
    }
private static final class VisitorWrapper implements JsonFormatVisitorWrapper {
        private SerializationContext serializationContext;
        private final String baseName;
        private final Map<String, String> traversedProperties;

        VisitorWrapper(SerializationContext ctxt, String baseName, Map<String, String> traversedProperties) {
            this.serializationContext = ctxt;
            this.baseName = baseName;
            this.traversedProperties = traversedProperties;
        }

        private VisitorWrapper createSubtraverser(String name) {
            return new VisitorWrapper(serializationContext, name, traversedProperties);
        }

        @Override
        public JsonObjectFormatVisitor expectObjectFormat(JavaType type) {
            return new JsonObjectFormatVisitor.Base(serializationContext) {
                @Override
                public void property(BeanProperty prop) {
                    visitProperty(prop);
                }

                @Override
                public void optionalProperty(BeanProperty prop) {
                    visitProperty(prop);
                }

                private void visitProperty(BeanProperty prop) {
                    String propertyName = prop.getFullName().toString();
                    traversedProperties.put(baseName + propertyName, "");
                    serializationContext.findPrimaryPropertySerializer(prop.getType(), prop)
                            .acceptJsonFormatVisitor(createSubtraverser(baseName + propertyName + "."), prop.getType());
                }
            };
        }

        @Override
        public JsonArrayFormatVisitor expectArrayFormat(JavaType type) {
            traversedProperties.put(baseName, "ARRAY/" + type.getGenericSignature());
            return null;
        }

        @Override
        public JsonStringFormatVisitor expectStringFormat(JavaType type) {
            return new JsonStringFormatVisitor.Base() {
                @Override
                public void format(JsonValueFormat format) {
                    traversedProperties.put(baseName, "STRING/" + format.name());
                }
            };
        }

        @Override
        public JsonNumberFormatVisitor expectNumberFormat(JavaType type) {
            return new JsonNumberFormatVisitor.Base() {
                @Override
                public void numberType(JsonParser.NumberType format) {
                    traversedProperties.put(baseName, "NUMBER/" + format.name());
                }
            };
        }

        @Override
        public JsonIntegerFormatVisitor expectIntegerFormat(JavaType type) {
            return new JsonIntegerFormatVisitor.Base() {
                @Override
                public void numberType(JsonParser.NumberType numberType) {
                    traversedProperties.put(baseName + "numberType", "INTEGER/" + numberType.name());
                }

                @Override
                public void format(JsonValueFormat format) {
                    traversedProperties.put(baseName + "format", "INTEGER/" + format.name());
                }
            };
        }

        @Override
        public JsonBooleanFormatVisitor expectBooleanFormat(JavaType type) {
            traversedProperties.put(baseName, "BOOLEAN");
            return new JsonBooleanFormatVisitor.Base();
        }

        @Override
        public JsonNullFormatVisitor expectNullFormat(JavaType type) {
            return new JsonNullFormatVisitor.Base();
        }

        @Override
        public JsonAnyFormatVisitor expectAnyFormat(JavaType type) {
            traversedProperties.put(baseName, "ANY");
            return new JsonAnyFormatVisitor.Base();
        }

        @Override
        public JsonMapFormatVisitor expectMapFormat(JavaType type) {
            traversedProperties.put(baseName, "MAP");
            return new JsonMapFormatVisitor.Base(serializationContext);
        }

        @Override
        public SerializationContext getContext() {
            return serializationContext;
        }

        @Override
        public void setContext(SerializationContext ctxt) {
            serializationContext = ctxt;
        }
    }

    void __invoke_dateTimeExceptionRoundtripVpack() throws Exception {
        try {
            dateTimeExceptionRoundtripVpack();
        } finally {
        }
    }

}
