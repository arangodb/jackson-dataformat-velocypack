package tools.jackson.databind.jsontype.jdk;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.jsontype.TypeDeserializer;
import tools.jackson.databind.jsontype.TypeIdResolver;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import tools.jackson.dataformat.velocypack.*;

class T32_0449F1 {
private static final byte[] INTEGER_42 = VPackWireFixtureTest.hex("28 2a");
private static final byte[] ABSTRACT_LIST = VPackWireFixtureTest.hex(
            "14 22 44 74 79 70 65 47 77 72 61 70 70 65 72 "
          + "44 6c 69 73 74 13 0d 44 6c 69 73 74 13 05 41 78 01 02 02");
private static final byte[] ABSTRACT_MAP = VPackWireFixtureTest.hex(
            "0b 3a 02 44 74 79 70 65 47 77 72 61 70 70 65 72 "
          + "43 6d 61 70 0b 24 02 46 5f 74 79 70 65 5f 4c 44 61 74 61 56 61 6c 75 65 4d 61 70 "
          + "44 6b 65 79 31 45 6e 61 6d 65 31 03 17 10 03");
private static final byte[] FLOAT_TYPE_FIRST = VPackWireFixtureTest.hex(
            "14 28 44 74 79 70 65 43 4d 41 50 43 6d 61 70 "
          + "14 18 4b 64 6f 75 62 6c 65 56 61 6c 75 65 "
          + "1b 9a 99 99 99 99 99 b9 3f 01 02");
private static final byte[] FLOAT_VALUE_FIRST = VPackWireFixtureTest.hex(
            "14 28 43 6d 61 70 14 18 4b 64 6f 75 62 6c 65 56 61 6c 75 65 "
          + "1b 9a 99 99 99 99 99 b9 3f 01 44 74 79 70 65 43 4d 41 50 02");

    // Provenance: BigDecimalForFloatDisabled3133Test#testDeserializeWithDifferentOrdering3133().
    void testDeserializeWithDifferentOrdering3133Vpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .disable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
                .build();

        TestMapContainer3133 typeFirst = mapper.readValue(FLOAT_TYPE_FIRST,
                TestMapContainer3133.class);
        assertInstanceOf(Double.class, typeFirst.getMap().get("doubleValue"));

        TestMapContainer3133 valueFirst = mapper.readValue(FLOAT_VALUE_FIRST,
                TestMapContainer3133.class);
        assertInstanceOf(Double.class, valueFirst.getMap().get("doubleValue"));
    }
static class WrapperArrayTypeDeser extends TypeDeserializer {
        @Override public TypeDeserializer forProperty(BeanProperty prop) { return this; }
        @Override public JsonTypeInfo.As getTypeInclusion() { return JsonTypeInfo.As.WRAPPER_ARRAY; }
        @Override public String getPropertyName() { return null; }
        @Override public TypeIdResolver getTypeIdResolver() { return null; }
        @Override public Class<?> getDefaultImpl() { return null; }

        @Override
        public Object deserializeTypedFromArray(JsonParser p, DeserializationContext ctxt)
                throws JacksonException {
            p.nextToken();
            String typeId = p.getString();
            p.nextToken();
            int value = p.getIntValue();
            p.nextToken();
            return typeId + ":" + value;
        }

        @Override public Object deserializeTypedFromObject(JsonParser p, DeserializationContext ctxt) throws JacksonException { return deserializeTypedFromArray(p, ctxt); }
        @Override public Object deserializeTypedFromScalar(JsonParser p, DeserializationContext ctxt) throws JacksonException { return deserializeTypedFromArray(p, ctxt); }
        @Override public Object deserializeTypedFromAny(JsonParser p, DeserializationContext ctxt) throws JacksonException { return deserializeTypedFromArray(p, ctxt); }
    }
static class Box {
        final String payload;
        Box(String payload) { this.payload = payload; }
    }
static class BoxDeser extends ValueDeserializer<Box> {
        private static final WrapperArrayTypeDeser TD = new WrapperArrayTypeDeser();

        @Override
        public Box deserialize(JsonParser p, DeserializationContext ctxt)
                throws JacksonException {
            Object decoded = TD.deserializeTypedWithKnownTypeId(p, ctxt, "tag");
            return new Box((String) decoded);
        }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
    @JsonSubTypes(@JsonSubTypes.Type(value = MapWrapper.class, name = "wrapper"))
    static class MapWrapper {
        public IDataValueMap map = new DataValueMap();
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "_type_")
    @JsonSubTypes(@JsonSubTypes.Type(value = DataValueMap.class, name = "DataValueMap"))
    public interface IDataValueMap extends Map<String, String> { }
static class DataValueMap extends HashMap<String, String> implements IDataValueMap { }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
    @JsonSubTypes(@JsonSubTypes.Type(value = ListWrapper.class, name = "wrapper"))
    static class ListWrapper {
        public IDataValueList list = new DataValueList();
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
    @JsonSubTypes(@JsonSubTypes.Type(value = DataValueList.class, name = "list"))
    public interface IDataValueList extends List<String> { }
static class DataValueList extends LinkedList<String> implements IDataValueList { }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
    @JsonSubTypes(@JsonSubTypes.Type(value = TestMapContainer3133.class, name = "MAP"))
    interface BaseType3133 { }
static class TestMapContainer3133 implements BaseType3133 {
        private Map<String, ? extends Object> map = new HashMap<>();

        public Map<String, ? extends Object> getMap() { return map; }
        public void setMap(Map<String, ? extends Object> map) { this.map = map; }
    }

    void __invoke_testDeserializeWithDifferentOrdering3133Vpack() throws Exception {
        try {
            testDeserializeWithDifferentOrdering3133Vpack();
        } finally {
        }
    }

}
