package tools.jackson.databind.records;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.DatabindContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;
import tools.jackson.databind.ser.std.StdSerializer;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0538F0 {
private static final VPackMapper MAPPER = new VPackMapper();

    void testSerializeReadOnlyPropertyVpack() throws Exception {
        Map<?, ?> actual = MAPPER.readValue(MAPPER.writeValueAsBytes(
                new RecordWithReadOnly(123, "Bob")), Map.class);
        assertEquals(Map.of("id", 123, "name", "Bob"), actual);
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

    void __invoke_testSerializeReadOnlyPropertyVpack() throws Exception {
        try {
            testSerializeReadOnlyPropertyVpack();
        } finally {
        }
    }

}
