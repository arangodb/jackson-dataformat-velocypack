package tools.jackson.databind.records;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.DatabindContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectWriter;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;
import tools.jackson.databind.ser.std.StdSerializer;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0538F1 {
private static final VPackMapper MAPPER = new VPackMapper();

    void testRecordWithView4085Vpack() throws Exception {
        Record4085 input = new Record4085(1, 2);
        ObjectWriter writer = MAPPER.writer();

        assertEquals(Map.of("total", 1, "current", 2),
                MAPPER.readValue(writer.writeValueAsBytes(input), Map.class));
        assertEquals(Map.of(),
                MAPPER.readValue(writer.withView(Void.class).writeValueAsBytes(input), Map.class));
        assertEquals(Map.of("total", 1),
                MAPPER.readValue(writer.withView(View4085Default.class)
                        .writeValueAsBytes(input), Map.class));
        assertEquals(Map.of("current", 2),
                MAPPER.readValue(writer.withView(View4085Field.class)
                        .writeValueAsBytes(input), Map.class));
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

    void __invoke_testRecordWithView4085Vpack() throws Exception {
        try {
            testRecordWithView4085Vpack();
        } finally {
        }
    }

}
