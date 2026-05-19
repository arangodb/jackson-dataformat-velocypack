package tools.jackson.databind.exc;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.testutil.DatabindTestUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

// for [databind#1794]
public class StackTraceElementTest extends DatabindTestUtil
{
    public static class ErrorObject {

        public String throwable;
        public String message;

//        @JsonDeserialize(contentUsing = StackTraceElementDeserializer.class)
        public StackTraceElement[] stackTrace;

        ErrorObject() {}

        public ErrorObject(Throwable throwable) {
            this.throwable = throwable.getClass().getName();
            message = throwable.getMessage();
            stackTrace = throwable.getStackTrace();
        }
    }

    // for [databind#1794] where extra `declaringClass` is serialized from private field.
    @Test
    public void testCustomStackTraceDeser() throws Exception
    {
        ObjectMapper mapper = vpackMapperBuilder()
                .changeDefaultVisibility(vc ->
                    vc.withVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY))
                .build();

        String json = VPackUtils.toJson(mapper
                .writer()
                .writeValueAsBytes(new ErrorObject(new Exception("exception message"))));

        ErrorObject result = mapper.readValue(VPackUtils.toVPack(json), ErrorObject.class);
        assertNotNull(result);
    }

    // for [databind#2593]: missing fields (due to JDK 8 compatibility)
    @Test
    public void testAllFieldsDeserialized() throws Exception
    {
        final ObjectMapper mapper = sharedMapper();
        StackTraceElement input = new StackTraceElement("classLoaderX", "moduleY", "1.0",
                "MyClass", "MyMethod", "MyClass.java", 10);
        String json = VPackUtils.toJson(mapper.writeValueAsBytes(input));
        StackTraceElement output = mapper.readValue(VPackUtils.toVPack(json), StackTraceElement.class);
        assertEquals(input, output);
    }
}
