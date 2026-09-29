package tools.jackson.databind.convert;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import tools.jackson.databind.BeanDescription;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationConfig;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.module.SimpleSerializers;
import tools.jackson.databind.ser.std.StdConvertingSerializer;
import tools.jackson.databind.util.StdConverter;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0161F1 {
private static final byte[] EMPTY_STRING = VPackWireFixtureTest.hex("40");
private static final byte[] SINGLE_MAP = VPackWireFixtureTest.hex(
            "0b 07 01 41 61 31 03");
private static final byte[] NESTED_REQUEST = VPackWireFixtureTest.hex(
            "0b 15 01 45 68 65 6c 6c 6f "
          + "0b 0b 01 45 76 61 6c 75 65 31 03 03");
private final ObjectMapper MAPPER = new VPackMapper();

    void testMapConverter() throws Exception {
        SimpleModule module = new SimpleModule();
        module.setSerializers(new Serializers4878());
        ObjectMapper mapper = VPackMapper.builder().addModule(module).build();
        assertArrayEquals(SINGLE_MAP,
                mapper.writeValueAsBytes(new MapWrapper4878(
                        Collections.singletonMap("a", 1))));
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

    void __invoke_testMapConverter() throws Exception {
        try {
            testMapConverter();
        } finally {
        }
    }

}
