package tools.jackson.databind.mixins;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.testutil.DatabindTestUtil;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.junit.jupiter.api.Assertions.*;

// for [databind#771]
public class MixinsWithBundlesTest extends DatabindTestUtil
{
    @Target(value={ ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.METHOD })
    @Retention(value=RetentionPolicy.RUNTIME)
    @JacksonAnnotationsInside
    @JsonProperty("bar")
    public @interface ExposeStuff {

    }

    public abstract class FooMixin {
        @ExposeStuff
        public abstract String getStuff();
    }

    public static class Foo {

        private String stuff;

        Foo(String stuff) {
            this.stuff = stuff;
        }

        public String getStuff() {
            return stuff;
        }
    }

    @Test
    public void testMixinWithBundles() throws Exception
    {
        ObjectMapper mapper = vpackMapperBuilder()
               .addMixIn(Foo.class, FooMixin.class)
               .build();
        String result = VPackUtils.toJson(mapper.writeValueAsBytes(new Foo("result")));
        assertEquals("{\"bar\":\"result\"}", result);
    }
}
