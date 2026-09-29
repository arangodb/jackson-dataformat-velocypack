package tools.jackson.databind.convert;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.BeanDescription;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationConfig;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.module.SimpleSerializers;
import tools.jackson.databind.ser.std.StdConvertingSerializer;
import tools.jackson.databind.util.StdConverter;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0161F2 {
private static final byte[] EMPTY_STRING = VPackWireFixtureTest.hex("40");
private static final byte[] SINGLE_MAP = VPackWireFixtureTest.hex(
            "0b 07 01 41 61 31 03");
private static final byte[] NESTED_REQUEST = VPackWireFixtureTest.hex(
            "0b 15 01 45 68 65 6c 6c 6f "
          + "0b 0b 01 45 76 61 6c 75 65 31 03 03");
private final ObjectMapper MAPPER = new VPackMapper();

    void testMapToMap() {
        Map<String, Integer> input = new LinkedHashMap<>();
        input.put("A", Integer.valueOf(3));
        input.put("B", Integer.valueOf(-4));

        Map<AB, String> output = MAPPER.convertValue(input,
                new TypeReference<Map<AB, String>>() { });
        assertEquals(2, output.size());
        assertEquals("3", output.get(AB.A));
        assertEquals("-4", output.get(AB.B));

        Map<String, Integer> roundtrip = MAPPER.convertValue(input,
                new TypeReference<TreeMap<String, Integer>>() { });
        assertEquals(2, roundtrip.size());
        assertEquals(Integer.valueOf(3), roundtrip.get("A"));
        assertEquals(Integer.valueOf(-4), roundtrip.get("B"));
    }

    void testMapToBean() {
        EnumMap<AB, String> map = new EnumMap<>(AB.class);
        map.put(AB.A, "17");
        map.put(AB.B, "-1");
        Bean bean = MAPPER.convertValue(map, Bean.class);
        assertEquals(Integer.valueOf(17), bean.A);
        assertEquals("-1", bean.B);
    }

    void testBeanToMap() {
        Bean bean = new Bean();
        bean.A = 129;
        bean.B = "13";
        EnumMap<AB, String> result = MAPPER.convertValue(bean,
                new TypeReference<EnumMap<AB, String>>() { });
        assertEquals("129", result.get(AB.A));
        assertEquals("13", result.get(AB.B));
    }

    void testIssue287() throws Exception {
        ObjectMapper mapper = new VPackMapper();
        assertArrayEquals(NESTED_REQUEST,
                mapper.writeValueAsBytes(new Request()));
    }

    void testMapToProperties() throws Exception {
        Bean bean = new Bean();
        bean.A = 129;
        bean.B = "13";
        Properties props = MAPPER.convertValue(bean, Properties.class);

        assertEquals(2, props.size());
        assertEquals("13", props.getProperty("B"));
        assertEquals("129", props.getProperty("A"));
    }
enum AB { A, B }
static class Bean {
        public Integer A;
        public String B;
    }
static final class StringWrapper {
        private final String value;

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        StringWrapper(String value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof StringWrapper other && other.value.equals(value);
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }
    }
static class MapWrapper4878 {
        final Map<String, Object> value;

        MapWrapper4878(Map<String, Object> value) {
            this.value = value;
        }
    }
static class WrapperConverter4878 extends StdConverter<MapWrapper4878, Object> {
        @Override
        public Object convert(MapWrapper4878 value) {
            return value.value;
        }
    }
@SuppressWarnings("serial")
    static class Serializers4878 extends SimpleSerializers {
        @Override
        public ValueSerializer<?> findSerializer(SerializationConfig config,
                JavaType type, BeanDescription.Supplier beanDescRef,
                JsonFormat.Value formatOverrides) {
            if (MapWrapper4878.class.isAssignableFrom(type.getRawClass())) {
                return new StdConvertingSerializer(new WrapperConverter4878());
            }
            return super.findSerializer(config, type, beanDescRef, formatOverrides);
        }
    }
@JsonSerialize(converter = RequestConverter.class)
    static class Request {
        public int x() {
            return 1;
        }
    }
static class RequestConverter extends StdConverter<Request, Map<String, Object>> {
        @Override
        public Map<String, Object> convert(Request value) {
            Map<String, Object> test = new LinkedHashMap<>();
            Map<String, Object> innerTest = new LinkedHashMap<>();
            innerTest.put("value", value.x());
            test.put("hello", innerTest);
            return test;
        }
    }

    void __invoke_testMapToMap() throws Exception {
        try {
            testMapToMap();
        } finally {
        }
    }


    void __invoke_testMapToBean() throws Exception {
        try {
            testMapToBean();
        } finally {
        }
    }


    void __invoke_testBeanToMap() throws Exception {
        try {
            testBeanToMap();
        } finally {
        }
    }


    void __invoke_testIssue287() throws Exception {
        try {
            testIssue287();
        } finally {
        }
    }


    void __invoke_testMapToProperties() throws Exception {
        try {
            testMapToProperties();
        } finally {
        }
    }

}
