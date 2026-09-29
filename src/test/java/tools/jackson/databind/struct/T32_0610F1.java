package tools.jackson.databind.struct;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.deser.ValueDeserializerModifier;
import tools.jackson.databind.deser.bean.BeanDeserializerBase;
import tools.jackson.databind.deser.std.DelegatingDeserializer;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.util.StdConverter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0610F1 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] UNWRAPPED_WITH_EXTRA = VPackWireFixtureTest.hex(
            "14 16 42 69 64 31 44 6e 61 6d 65 43 61 61 61 " +
            "43 61 67 65 28 0c 03");
private static final byte[] DELEGATED_INNER = VPackWireFixtureTest.hex(
            "14 17 42 70 31 46 76 61 6c 75 65 31 " +
            "42 70 32 46 76 61 6c 75 65 32 02");

    // Provenance: UnwrappedWithConverter6017Test#testUnwrappedWithConverter().
    void unwrappedConverterVpack() throws Exception {
        Map<?, ?> result = MAPPER.readValue(MAPPER.writeValueAsBytes(new ConvertedOuter()), Map.class);
        assertEquals(2, result.get("x"));
        assertEquals(3, result.get("y"));
        assertEquals("test", result.get("name"));
    }

    // Provenance: UnwrappedWithConverter6017Test#testUnwrappedPrefixedWithConverter().
    void unwrappedPrefixedConverterVpack() throws Exception {
        Map<?, ?> result = MAPPER.readValue(MAPPER.writeValueAsBytes(new PrefixedOuter()), Map.class);
        assertEquals(2, result.get("p_x"));
        assertEquals(3, result.get("p_y"));
        assertEquals("test", result.get("name"));
    }

    // Provenance: UnwrappedWithConverter6017Test#testUnwrappedConverterPropertyConflictDetected().
    void unwrappedConverterPropertyConflictDetectedVpack() {
        DatabindException exception = assertThrows(DatabindException.class,
                () -> MAPPER.writeValueAsBytes(new ConverterConflict()));
        assertTrue(exception.getMessage().contains("unwrapped property 'x'"),
                "Unexpected message: " + exception.getMessage());
    }
private static final ObjectMapper DELEGATING_MAPPER = VPackMapper.builder()
            .addModule(new SimpleModule().setDeserializerModifier(new WrappingModifier(1)))
            .build();
static class Outer {
        public Long id;
        @JsonUnwrapped public Inner inner;
        private final Map<String, Object> extra = new HashMap<>();
        @JsonAnyGetter public Map<String, Object> getExtra() { return extra; }
        @JsonAnySetter public void set(String key, Object value) { extra.put(key, value); }
    }
static class Inner { public String name; }
static class Point {
        public int x, y;
        Point(int x, int y) { this.x = x; this.y = y; }
    }
static class PointConverter extends StdConverter<String, Point> {
        @Override public Point convert(String value) {
            String[] parts = value.split(",");
            return new Point(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        }
    }
static class ConvertedOuter {
        public String name = "test";
        @JsonUnwrapped @JsonSerialize(converter = PointConverter.class)
        public String coords = "2,3";
    }
static class PrefixedOuter {
        public String name = "test";
        @JsonUnwrapped(prefix = "p_") @JsonSerialize(converter = PointConverter.class)
        public String coords = "2,3";
    }
static class ConverterConflict {
        public int x = 1;
        @JsonUnwrapped @JsonSerialize(converter = PointConverter.class)
        public String coords = "2,3";
    }
static class DelegatingInner { public String p1, p2; }
static class DelegatingOuter { @JsonUnwrapped public DelegatingInner inner; }
static class WrappingDeserializer extends DelegatingDeserializer {
        WrappingDeserializer(tools.jackson.databind.ValueDeserializer<?> delegate) { super(delegate); }
        @Override protected tools.jackson.databind.ValueDeserializer<?> newDelegatingInstance(
                tools.jackson.databind.ValueDeserializer<?> delegate) {
            return new WrappingDeserializer(delegate);
        }
    }
static class WrappingModifier extends ValueDeserializerModifier {
        private final int levels;
        WrappingModifier(int levels) { this.levels = levels; }
        @Override public tools.jackson.databind.ValueDeserializer<?> modifyDeserializer(
                tools.jackson.databind.DeserializationConfig config,
                tools.jackson.databind.BeanDescription.Supplier beanDescRef,
                tools.jackson.databind.ValueDeserializer<?> deserializer) {
            if (deserializer instanceof BeanDeserializerBase) {
                for (int i = 0; i < levels; ++i) {
                    deserializer = new WrappingDeserializer(deserializer);
                }
            }
            return deserializer;
        }
    }

    void __invoke_unwrappedConverterVpack() throws Exception {
        try {
            unwrappedConverterVpack();
        } finally {
        }
    }


    void __invoke_unwrappedPrefixedConverterVpack() throws Exception {
        try {
            unwrappedPrefixedConverterVpack();
        } finally {
        }
    }


    void __invoke_unwrappedConverterPropertyConflictDetectedVpack() throws Exception {
        try {
            unwrappedConverterPropertyConflictDetectedVpack();
        } finally {
        }
    }

}
