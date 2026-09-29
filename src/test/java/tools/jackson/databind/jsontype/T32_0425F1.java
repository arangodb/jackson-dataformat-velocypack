package tools.jackson.databind.jsontype;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.DatabindContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;
import tools.jackson.databind.cfg.DateTimeFeature;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0425F1 {
private static final byte[] MULTIPLE_NAMES = VPackWireFixtureTest.hex(
            "14 5a 44 62 61 73 65 13 52 "
          + "14 15 44 74 79 70 65 41 61 44 64 61 74 61 14 06 41 78 35 01 02 "
          + "14 1d 44 74 79 70 65 41 62 44 64 61 74 61 14 0e 41 79 "
          + "1b cd cc cc cc cc cc 08 40 01 02 "
          + "14 1d 44 74 79 70 65 41 63 44 64 61 74 61 14 0e 41 79 "
          + "1b 66 66 66 66 66 e6 40 40 01 02 03 01");
private static final byte[] MULTIPLE_NAMES_BAD_SHAPE = VPackWireFixtureTest.hex(
            "14 5a 44 64 61 74 61 13 52 "
          + "14 15 44 74 79 70 65 41 61 44 64 61 74 61 14 06 41 78 35 01 02 "
          + "14 1d 44 74 79 70 65 41 62 44 64 61 74 61 14 0e 41 79 "
          + "1b cd cc cc cc cc cc 08 40 01 02 "
          + "14 1d 44 74 79 70 65 41 63 44 64 61 74 61 14 0e 41 79 "
          + "1b 66 66 66 66 66 e6 40 40 01 02 03 01");
private static final byte[] OVERLAPPING_A = VPackWireFixtureTest.hex(
            "14 0d 44 74 79 70 65 41 61 41 78 37 02");
private static final byte[] OVERLAPPING_B = VPackWireFixtureTest.hex(
            "14 0d 44 74 79 70 65 41 62 41 78 33 02");
private static final byte[] POLYMORPHIC_MAP = VPackWireFixtureTest.hex(
            "0b 7b 01 45 76 61 6c 75 65 0b 71 02 46 40 63 6c" +
                "61 73 73 7a 74 6f 6f 6c 73 2e 6a 61 63 6b 73 6f" +
                "6e 2e 64 61 74 61 62 69 6e 64 2e 6a 73 6f 6e 74" +
                "79 70 65 2e 54 33 32 5f 30 34 32 35 46 31 24 4d" +
                "61 70 43 6f 6e 74 61 69 6e 65 72 34 32 35 43 6d" +
                "61 70 0b 26 01 49 44 61 74 65 56 61 6c 75 65 06" +
                "18 02 4e 6a 61 76 61 2e 75 74 69 6c 2e 44 61 74" +
                "65 2a 40 e2 01 03 12 03 03 45 03");
private static final ObjectMapper NAMES_MAPPER = VPackMapper.builder()
            .enable(tools.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();
private static final ObjectMapper MAPPER = new VPackMapper();

    // Provenance: TestOverlappingTypeIdNames#testOverlappingNameDeser().
    void testOverlappingNameDeserVpack() throws Exception {
        Base312_425 a = MAPPER.readValue(OVERLAPPING_A, Base312_425.class);
        assertNotNull(a);
        assertEquals(Impl312_425.class, a.getClass());
        assertEquals(7, ((Impl312_425) a).x);

        Base312_425 b = MAPPER.readValue(OVERLAPPING_B, Base312_425.class);
        assertNotNull(b);
        assertEquals(Impl312_425.class, b.getClass());
        assertEquals(3, ((Impl312_425) b).x);
    }

    // Provenance: TestOverlappingTypeIdNames#testOverlappingNameSer().
    void testOverlappingNameSerVpack() throws Exception {
        assertEquals("a", asMap(MAPPER.writeValueAsBytes(new Impl312B1_425())).get("type"));
        assertEquals("a", asMap(MAPPER.writeValueAsBytes(new Impl312B2_425())).get("type"));
        assertEquals(1, asMap(MAPPER.writeValueAsBytes(new Impl312B1_425())).get("value"));
    }
private static ObjectMapper polymorphicMapper(boolean ordered) {
        VPackMapper.Builder builder = VPackMapper.builder()
                .polymorphicTypeValidator(new NoCheckSubTypeValidator425())
                .enable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS);
        if (ordered) {
            builder.enable(tools.jackson.databind.SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);
        }
        return builder.build();
    }
private static PolymorphicValueWrapper425 readPolymorphicWrapper(ObjectMapper mapper)
            throws IOException {
        return mapper.readValue(POLYMORPHIC_MAP, PolymorphicValueWrapper425.class);
    }
private static Map<String, Object> originMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("DateValue", new Date(123456L));
        return map;
    }
private static MapContainer425 originContainer() {
        return new MapContainer425(originMap());
    }
private static Map<?, ?> asMap(byte[] bytes) throws IOException {
        return MAPPER.readValue(bytes, Map.class);
    }
private static void assertNames(WrapperForNamesBase425 value) {
        assertNotNull(value);
        List<? extends NamedEntry425> base = value.getBase();
        assertEquals(3, base.size());
        assertInstanceOf(A425.class, base.get(0).getData());
        assertEquals(5L, ((A425) base.get(0).getData()).x);
        assertInstanceOf(B425.class, base.get(1).getData());
        assertEquals(3.1F, ((B425) base.get(1).getData()).y, 0F);
        assertInstanceOf(B425.class, base.get(2).getData());
        assertEquals(33.8F, ((B425) base.get(2).getData()).y, 0F);
    }
private interface WrapperForNamesBase425 {
        List<? extends NamedEntry425> getBase();
    }
private interface NamedEntry425 {
        MultiTypeName425 getData();
    }
static class MultiTypeName425 { }
static class A425 extends MultiTypeName425 { public long x; }
static class B425 extends MultiTypeName425 { public float y; }
static class BaseForNamesBase425 implements NamedEntry425 {
        public String type;
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property = "type")
        @JsonSubTypes({
            @JsonSubTypes.Type(value = A425.class, names = "a"),
            @JsonSubTypes.Type(value = B425.class, names = { "b", "c" })
        })
        public MultiTypeName425 data;
        @Override public MultiTypeName425 getData() { return data; }
    }
static class WrapperForNames425 implements WrapperForNamesBase425 {
        public List<BaseForNamesBase425> base;
        @Override public List<BaseForNamesBase425> getBase() { return base; }
    }
static class BaseForNameAndNames425 implements NamedEntry425 {
        public String type;
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property = "type")
        @JsonSubTypes({
            @JsonSubTypes.Type(value = A425.class, name = "a"),
            @JsonSubTypes.Type(value = B425.class, names = { "b", "c" })
        })
        public MultiTypeName425 data;
        @Override public MultiTypeName425 getData() { return data; }
    }
static class WrapperForNameAndNames425 implements WrapperForNamesBase425 {
        public List<BaseForNameAndNames425> base;
        @Override public List<BaseForNameAndNames425> getBase() { return base; }
    }
static class BaseForNotUniqueNames425 {
        public String type;
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property = "type")
        @JsonSubTypes(value = {
            @JsonSubTypes.Type(value = A425.class, name = "a"),
            @JsonSubTypes.Type(value = B425.class, names = { "b", "a" })
        }, failOnRepeatedNames = true)
        public MultiTypeName425 data;
    }
static class WrapperForNotUniqueNames425 {
        public List<BaseForNotUniqueNames425> base;
    }
static class NoCheckSubTypeValidator425 extends PolymorphicTypeValidator.Base {
        private static final long serialVersionUID = 1L;
        @Override
        public Validity validateBaseType(DatabindContext ctxt, JavaType baseType) {
            return Validity.ALLOWED;
        }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
    @JsonSubTypes({
        @JsonSubTypes.Type(name = "a", value = Impl312_425.class),
        @JsonSubTypes.Type(name = "b", value = Impl312_425.class)
    })
    static abstract class Base312_425 { }
static class Impl312_425 extends Base312_425 { public int x; }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
    @JsonSubTypes({
        @JsonSubTypes.Type(name = "a", value = Impl312B1_425.class),
        @JsonSubTypes.Type(name = "a", value = Impl312B2_425.class)
    })
    static class Base312B_425 { public int value = 1; }
static class Impl312B1_425 extends Base312B_425 { }
static class Impl312B2_425 extends Base312B_425 { }
@JsonInclude(JsonInclude.Include.NON_NULL)
    public static class MapContainer425 {
        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY,
                property = "@class")
        public Map<String, Object> map;
        public MapContainer425() { }
        public MapContainer425(Map<String, Object> map) { this.map = map; }
        @Override public boolean equals(Object o) {
            return o instanceof MapContainer425 other && map.equals(other.map);
        }
    }
@JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PolymorphicValueWrapper425 {
        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY,
                property = "@class")
        public Object value;
    }

    void __invoke_testOverlappingNameDeserVpack() throws Exception {
        try {
            testOverlappingNameDeserVpack();
        } finally {
        }
    }


    void __invoke_testOverlappingNameSerVpack() throws Exception {
        try {
            testOverlappingNameSerVpack();
        } finally {
        }
    }

}
