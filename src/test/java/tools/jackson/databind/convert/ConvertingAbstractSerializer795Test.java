package tools.jackson.databind.convert;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.annotation.*;
import tools.jackson.databind.util.StdConverter;

import static org.junit.jupiter.api.Assertions.*;
import static tools.jackson.databind.testutil.DatabindTestUtil.*;

// for [databind#795]
public class ConvertingAbstractSerializer795Test
{
    public static abstract class AbstractCustomType {
        final String value;
        public AbstractCustomType(String v) {
            this.value = v;
        }
    }

    public static class ConcreteCustomType extends AbstractCustomType {
        public ConcreteCustomType(String v) {
            super(v);
        }
    }

    public static class AbstractCustomTypeDeserializationConverter extends StdConverter<String, AbstractCustomType>{

        @Override
        public AbstractCustomType convert(String arg) {
            return new ConcreteCustomType(arg);
        }
    }

    public static class AbstractCustomTypeUser {
        @JsonProperty
        @JsonDeserialize(converter = AbstractCustomTypeDeserializationConverter.class)
        protected AbstractCustomType customField;

        public AbstractCustomTypeUser(@JsonProperty("customField") AbstractCustomType cf) {
            this.customField = cf;
        }
    }

    public static  class NonAbstractCustomType {
        final String value;
        public NonAbstractCustomType(String v) {
            this.value = v;
        }
    }

    public static class NonAbstractCustomTypeDeserializationConverter extends StdConverter<String, NonAbstractCustomType>{

        @Override
        public NonAbstractCustomType convert(String arg) {
            return new NonAbstractCustomType(arg);
        }
    }

    public static class NonAbstractCustomTypeUser {
        @JsonProperty
        @JsonDeserialize(converter = NonAbstractCustomTypeDeserializationConverter.class)
        private final NonAbstractCustomType customField;

        @JsonCreator NonAbstractCustomTypeUser(@JsonProperty("customField") NonAbstractCustomType customField) {
            this.customField = customField;
        }
    }

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    private static final ObjectMapper VPACK_MAPPER = newVPackMapper();

    @Test
    public void testAbstractTypeDeserialization() throws Exception {
        String test = a2q("{'customField': 'customString'}");
        AbstractCustomTypeUser cu = VPACK_MAPPER.readValue(VPackUtils.toVPack(test), AbstractCustomTypeUser.class);
        assertNotNull(cu);
    }

    @Test
    public void testNonAbstractDeserialization() throws Exception {
        String test = a2q("{'customField': 'customString'}");
        NonAbstractCustomTypeUser cu = VPACK_MAPPER.readValue(VPackUtils.toVPack(test), NonAbstractCustomTypeUser.class);
        assertNotNull(cu);
    }
}
