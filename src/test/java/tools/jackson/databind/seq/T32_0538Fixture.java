package tools.jackson.databind.seq;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DatabindContext;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.StdSerializer;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0538Fixture {
private static final VPackMapper MAPPER = new VPackMapper();

    void testPolyCustomKeySerializerVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .activateDefaultTyping(NoCheckSubTypeValidator.INSTANCE,
                        DefaultTyping.NON_FINAL)
                .addModule(new SimpleModule("keySerializerModule")
                        .addKeySerializer(CustomKey.class, new CustomKeySerializer()))
                .build();
        Map<CustomKey, String> map = new HashMap<>();
        CustomKey key = new CustomKey();
        key.a = "foo";
        key.b = 1;
        map.put(key, "bar");

        byte[] encoded = mapper.writerFor(new TypeReference<Map<CustomKey, String>>() { })
                .writeValueAsBytes(map);
        List<?> wire = MAPPER.readValue(encoded, List.class);
        assertEquals(List.of("java.util.HashMap", Map.of("foo,1", "bar")), wire);
    }
record RecordWithReadOnly(int id,
            @JsonProperty(access = JsonProperty.Access.READ_ONLY) String name) { }
static class View4085Default { }
static class View4085Field { }
@JsonView(View4085Default.class)
    record Record4085(int total, @JsonView(View4085Field.class) int current) { }
static class CustomKey {
        String a;
        int b;

        @Override
        public String toString() { return "BAD-KEY"; }
    }
static class CustomKeySerializer extends StdSerializer<CustomKey> {
        CustomKeySerializer() { super(CustomKey.class); }

        @Override
        public void serialize(CustomKey key, JsonGenerator generator,
                tools.jackson.databind.SerializationContext context) {
            generator.writeName(key.a + "," + key.b);
        }
    }
static final class NoCheckSubTypeValidator extends PolymorphicTypeValidator.Base {
        private static final long serialVersionUID = 1L;
        static final NoCheckSubTypeValidator INSTANCE = new NoCheckSubTypeValidator();

        @Override
        public Validity validateBaseType(DatabindContext context, JavaType baseType) {
            return Validity.ALLOWED;
        }
    }

    void __invoke_testPolyCustomKeySerializerVpack() throws Exception {
        try {
            testPolyCustomKeySerializerVpack();
        } finally {
        }
    }

}
