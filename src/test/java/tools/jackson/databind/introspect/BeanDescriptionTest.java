package tools.jackson.databind.introspect;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.*;
import tools.jackson.databind.testutil.DatabindTestUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BeanDescriptionTest extends DatabindTestUtil
{
    private final ObjectMapper MAPPER = newVPackMapper();

    private final static String CLASS_DESC = "Description, yay!";

    @JsonClassDescription(CLASS_DESC)
    static class DocumentedBean {
        public int x;
    }

    @Test
    public void testClassDesc() throws Exception
    {
        BeanDescription beanDesc = ObjectMapperTestAccess.beanDescriptionForDeser(MAPPER, DocumentedBean.class);
        assertEquals(CLASS_DESC, MAPPER.deserializationConfig().getAnnotationIntrospector()
                .findClassDescription(MAPPER.deserializationConfig(), beanDesc.getClassInfo()));
    }
}
