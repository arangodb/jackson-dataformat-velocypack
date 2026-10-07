package tools.jackson.databind.introspect;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.cfg.MapperConfig;
import tools.jackson.databind.testutil.DatabindTestUtil;

import java.beans.ConstructorProperties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class IgnoredCreatorProperty1572Test extends DatabindTestUtil
{
    static class InnerTest
    {
        public String str;
        public String otherStr;
    }

    static class OuterTest
    {
        InnerTest innerTest;

        @JsonIgnore
        String otherOtherStr;

        @JsonCreator
        public OuterTest(/*@JsonProperty("innerTest")*/ InnerTest inner,
                /*@JsonProperty("otherOtherStr")*/ String otherStr) {
            this.innerTest = inner;
        }
    }

    static class ImplicitNames extends JacksonAnnotationIntrospector
    {
        private static final long serialVersionUID = 1L;

        @Override
        public String findImplicitPropertyName(MapperConfig<?> config, AnnotatedMember member) {
            if (member instanceof AnnotatedParameter param) {
                // A placeholder for legitimate property name detection
                // such as what the JDK8 module provides
                switch (param.getIndex()) {
                case 0:
                    return "innerTest";
                case 1:
                    return "otherOtherStr";
                default:
                }
            }
            return null;
        }
    }

    // [databind#2001]
    static public class Foo2001 {
        @JsonIgnore
        public String query;

        @JsonCreator
        @ConstructorProperties("rawQuery")
        public Foo2001(@JsonProperty("query") String rawQuery) {
            query = rawQuery;
        }
    }

    /*
    /********************************************************
    /* Test methods
    /********************************************************
     */

    // [databind#1572]
    @Test
    public void testIgnoredCtorParam() throws Exception
    {
        final ObjectMapper mapper = vpackMapperBuilder()
                .annotationIntrospector(new ImplicitNames())
                .build();
        String JSON = a2q("{'innerTest': {\n"
                +"'str':'str',\n"
                +"'otherStr': 'otherStr'\n"
                +"}}\n");
        OuterTest result = mapper.readValue(VPackUtils.toVPack(JSON), OuterTest.class);
        assertNotNull(result);
        assertNotNull(result.innerTest);
        assertEquals("otherStr", result.innerTest.otherStr);
    }

    // [databind#2001]
    @Test
    public void testIgnoredFieldPresentInPropertyCreator() throws Exception {
        Foo2001 deserialized = newVPackMapper().readValue(VPackUtils.toVPack("{\"query\": \"bar\"}"), Foo2001.class);
        assertEquals("bar", deserialized.query);
    }
}
