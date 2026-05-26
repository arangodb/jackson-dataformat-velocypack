package tools.jackson.databind.introspect;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.cfg.MapperConfig;
import tools.jackson.databind.exc.InvalidDefinitionException;
import tools.jackson.databind.testutil.DatabindTestUtil;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests verifying handling of potential and actual
 * conflicts, regarding property handling.
 */
public class TestPropertyConflicts extends DatabindTestUtil
{
    // error message for conflicting getters sub-optimal
    static class BeanWithConflict
    {
        public int getX() { return 3; }
        public boolean getx() { return false; }
    }

    // [databind#238]
    protected static class Getters1A
    {
        @JsonProperty
        protected int value = 3;

        public int getValue() { return value+1; }
        public boolean isValue() { return false; }
    }

    // variant where order of declarations is reversed; to try to
    // ensure declaration order won't break things
    protected static class Getters1B
    {
        public boolean isValue() { return false; }

        @JsonProperty
        protected int value = 3;

        public int getValue() { return value+1; }
    }

    protected static class InferingIntrospector extends JacksonAnnotationIntrospector
    {
        private static final long serialVersionUID = 1L;

        @Override
        public String findImplicitPropertyName(MapperConfig<?> config, AnnotatedMember member) {
            String name = member.getName();
            if (name.startsWith("_")) {
                return name.substring(1);
            }
            return null;
        }
    }

    static class Infernal {
        public String _name() { return "foo"; }
        public String getName() { return "Bob"; }

        public void setStuff(String value) { ; // ok
        }

        public void _stuff(String value) {
            throw new UnsupportedOperationException();
        }
    }

    // For [databind#541]
    static class Bean541 {
        protected String str;

        @JsonCreator
        public Bean541(@JsonProperty("str") String str) {
            this.str = str;
        }

        @JsonProperty("s")
        public String getStr() {
            return str;
        }
     }

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    private final ObjectMapper MAPPER = newVPackMapper();

    @Test
    public void testFailWithDupProps() throws Exception
    {
        BeanWithConflict bean = new BeanWithConflict();
        // Must allow "getx"
        ObjectMapper mapper = vpackMapperBuilder()
                .accessorNaming(new DefaultAccessorNamingStrategy.Provider()
                        .withFirstCharAcceptance(true, false))
                .build();
        InvalidDefinitionException e = assertThrows(InvalidDefinitionException.class,
                () -> VPackUtils.toJson(mapper.writeValueAsBytes(bean)));
        verifyException(e, "Conflicting getter definitions");
    }

    // [databind#238]: ok to have getter, "isGetter"
    @Test
    public void testRegularAndIsGetter() throws Exception
    {
        final ObjectWriter writer = MAPPER.writer();

        // first, serialize without probs:
        assertEquals("{\"value\":4}", VPackUtils.toJson(writer.writeValueAsBytes(new Getters1A())));
        assertEquals("{\"value\":4}", VPackUtils.toJson(writer.writeValueAsBytes(new Getters1B())));

        // and similarly, deserialize
        ObjectMapper mapper = newVPackMapper();
        assertEquals(1, mapper.readValue(VPackUtils.toVPack("{\"value\":1}"), Getters1A.class).value);
        assertEquals(2, mapper.readValue(VPackUtils.toVPack("{\"value\":2}"), Getters1B.class).value);
    }

    @Test
    public void testInferredNameConflictsWithGetters() throws Exception
    {
        ObjectMapper mapper = vpackMapperBuilder()
                .annotationIntrospector(new InferingIntrospector())
                .build();
        String json = VPackUtils.toJson(mapper.writeValueAsBytes(new Infernal()));
        assertEquals(a2q("{'name':'Bob'}"), json);
    }

    @Test
    public void testInferredNameConflictsWithSetters() throws Exception
    {
        ObjectMapper mapper = vpackMapperBuilder()
                .annotationIntrospector(new InferingIntrospector())
                .build();
        Infernal inf = mapper.readValue(VPackUtils.toVPack(a2q("{'stuff':'Bob'}")), Infernal.class);
        assertNotNull(inf);
    }

    @Test
    public void testIssue541() throws Exception {
        ObjectMapper mapper = vpackMapperBuilder()
                .disable(MapperFeature.USE_GETTERS_AS_SETTERS)
                .build();
        Bean541 data = mapper.readValue(VPackUtils.toVPack("{\"str\":\"the string\"}"), Bean541.class);
        if (data == null) {
            throw new IllegalStateException("data is null");
        }
        if (!"the string".equals(data.getStr())) {
            throw new IllegalStateException("bad value for data.str");
        }
    }
}
