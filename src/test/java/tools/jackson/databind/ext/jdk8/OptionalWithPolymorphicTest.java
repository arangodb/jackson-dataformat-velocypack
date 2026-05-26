package tools.jackson.databind.ext.jdk8;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.testutil.DatabindTestUtil;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class OptionalWithPolymorphicTest
    extends DatabindTestUtil
{
    static class ContainerA {
        @JsonProperty private Optional<String> name = Optional.empty();
        @JsonProperty private Optional<Strategy> strategy = Optional.empty();
    }

    static class ContainerB {
        @JsonProperty private Optional<String> name = Optional.empty();
        @JsonProperty private Strategy strategy = null;
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
    @JsonSubTypes({ @JsonSubTypes.Type(name = "Foo", value = Foo.class),
        @JsonSubTypes.Type(name = "Bar", value = Bar.class),
        @JsonSubTypes.Type(name = "Baz", value = Baz.class) })
    interface Strategy { }

    static class Foo implements Strategy {
        @JsonProperty private final int foo;

        @JsonCreator
        Foo(@JsonProperty("foo") int foo) {
            this.foo = foo;
        }
    }

    static class Bar implements Strategy {
        @JsonProperty private final boolean bar;

        @JsonCreator
        Bar(@JsonProperty("bar") boolean bar) {
            this.bar = bar;
        }
    }

    static class Baz implements Strategy {
        @JsonProperty private final String baz;

        @JsonCreator
        Baz(@JsonProperty("baz") String baz) {
            this.baz = baz;
        }
    }

    static class AbstractOptional {
        @JsonDeserialize(contentAs=Integer.class)
        public Optional<java.io.Serializable> value;
    }

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    private final ObjectMapper MAPPER = newVPackMapper();

    @Test
    public void testOptionalMapsFoo() throws Exception {

		Map<String, Object> foo = new LinkedHashMap<>();
		Map<String, Object> loop = new LinkedHashMap<>();
		loop.put("type", "Foo");
		loop.put("foo", 42);

		foo.put("name", "foo strategy");
		foo.put("strategy", loop);

		_test(MAPPER, foo);
    }

    @Test
    public void testOptionalMapsBar() throws Exception {

		Map<String, Object> bar = new LinkedHashMap<>();
		Map<String, Object> loop = new LinkedHashMap<>();
		loop.put("type", "Bar");
		loop.put("bar", true);

		bar.put("name", "bar strategy");
		bar.put("strategy", loop);

		_test(MAPPER, bar);
    }

    @Test
    public void testOptionalMapsBaz() throws Exception {
		Map<String, Object> baz = new LinkedHashMap<>();
		Map<String, Object> loop = new LinkedHashMap<>();
		loop.put("type", "Baz");
		loop.put("baz", "hello world!");

		baz.put("name", "bar strategy");
		baz.put("strategy", loop);

		_test(MAPPER, baz);
    }

    @Test
    public void testOptionalWithTypeAnnotation13() throws Exception
    {
        AbstractOptional result = MAPPER.readValue(VPackUtils.toVPack("{\"value\" : 5}"),
                AbstractOptional.class);
        assertNotNull(result);
        assertNotNull(result.value);
        Object ob = result.value.get();
        assertEquals(Integer.class, ob.getClass());
        assertEquals(Integer.valueOf(5), ob);
    }

    private void _test(ObjectMapper m, Map<String, ?> map) throws Exception {
        String json = VPackUtils.toJson(m.writeValueAsBytes(map));

        ContainerA objA = m.readValue(VPackUtils.toVPack(json), ContainerA.class);
        assertNotNull(objA);

        ContainerB objB = m.readValue(VPackUtils.toVPack(json), ContainerB.class);
        assertNotNull(objB);
    }
}
