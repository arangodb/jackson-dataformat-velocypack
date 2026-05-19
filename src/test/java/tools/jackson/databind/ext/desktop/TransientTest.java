package tools.jackson.databind.ext.desktop;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.exc.UnrecognizedPropertyException;
import tools.jackson.databind.testutil.DatabindTestUtil;

import java.beans.Transient;
import java.io.Serializable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Tests for both `transient` keyword and JDK 7
 * {@link Transient} annotation.
 */
public class TransientTest extends DatabindTestUtil
{
    // for [databind#296]
    @JsonPropertyOrder({ "x" })
    static class ClassyTransient
    {
        public transient int value = 3;

        public int getValue() { return value; }

        public int getX() { return 42; }
    }

    static class SimplePrunableTransient {
        public int a = 1;
        public transient int b = 2;
    }

    // for [databind#857]
    static class BeanTransient {
        @Transient
        public int getX() { return 3; }

        public int getY() { return 4; }
    }

    // for [databind#1184]
    static class OverridableTransient {
        @JsonProperty
//        @JsonProperty("value") // should override transient here, to force inclusion
        public transient int tValue;

        public OverridableTransient(int v) { tValue = v; }
    }

    static class TransientToPrune {
        public transient int a;

        public int getA() { return a; }
    }

    // for [databind#3948]
    @JsonPropertyOrder(alphabetic = true)
    static class Obj3948 implements Serializable {

        private static final long serialVersionUID = -1L;

        private String a = "hello";

        @JsonIgnore
        private transient String b = "world";

        @JsonProperty("cat")
        private String c = "jackson";

        @JsonProperty("dog")
        private transient String d = "databind";

        public String getA() {
            return a;
        }

        public String getB() {
            return b;
        }

        public String getC() {
            return c;
        }

        public String getD() {
            return d;
        }
    }

    /*
    /**********************************************************
    /* Unit tests
    /**********************************************************
     */

    private final ObjectMapper MAPPER = newVPackMapper();

    // for [databind#296]
    @Test
    public void testTransientFieldHandling() throws Exception
    {
        // default handling: remove transient field but do not propagate
        assertEquals(a2q("{'x':42,'value':3}"),
                VPackUtils.toJson(MAPPER.writeValueAsBytes(new ClassyTransient())));
        assertEquals(a2q("{'a':1}"),
                VPackUtils.toJson(MAPPER.writeValueAsBytes(new SimplePrunableTransient())));

        // but may change that
        ObjectMapper m = vpackMapperBuilder()
            .enable(MapperFeature.PROPAGATE_TRANSIENT_MARKER)
            .build();
        assertEquals(a2q("{'x':42}"),
                VPackUtils.toJson(m.writeValueAsBytes(new ClassyTransient())));
    }

    // for [databind#857]
    @Test
    public void testBeanTransient() throws Exception
    {
        assertEquals(a2q("{'y':4}"),
                VPackUtils.toJson(MAPPER.writeValueAsBytes(new BeanTransient())));
    }

    // for [databind#1184]
    @Test
    public void testOverridingTransient() throws Exception
    {
        assertEquals(a2q("{'tValue':38}"),
                VPackUtils.toJson(MAPPER.writeValueAsBytes(new OverridableTransient(38))));
    }

    // for [databind#3682]: SHOULD prune `transient` Field, not pull in
    @Test
    public void testTransientToPrune() throws Exception
    {
        try {
            TransientToPrune result = MAPPER.readerFor(TransientToPrune.class)
                    .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .readValue(VPackUtils.toVPack("{\"a\":3}"));
            fail("Should not pass, got: "+result);
        } catch (UnrecognizedPropertyException e) {
            verifyException(e, "Unrecognized", "\"a\"");
        }
    }

    @Test
    public void testJsonIgnoreSerialization() throws Exception {
        Obj3948 obj1 = new Obj3948();

        String json = VPackUtils.toJson(MAPPER.writeValueAsBytes(obj1));

        assertEquals(a2q("{'a':'hello','cat':'jackson','dog':'databind'}"), json);
    }

    @Test
    public void testJsonIgnoreSerializationTransient() throws Exception {
        final ObjectMapper mapperTransient = vpackMapperBuilder()
                .configure(MapperFeature.PROPAGATE_TRANSIENT_MARKER, true)
                .build();

        Obj3948 obj1 = new Obj3948();
        String json = VPackUtils.toJson(mapperTransient.writeValueAsBytes(obj1));
        assertEquals(a2q("{'a':'hello','cat':'jackson','dog':'databind'}"), json);
    }
}
