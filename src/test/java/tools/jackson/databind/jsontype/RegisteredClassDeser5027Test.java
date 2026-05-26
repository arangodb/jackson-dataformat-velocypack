package tools.jackson.databind.jsontype;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.exc.InvalidTypeIdException;
import tools.jackson.databind.testutil.DatabindTestUtil;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

// For [databind#5027]
public class RegisteredClassDeser5027Test extends DatabindTestUtil
{
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
    @JsonSubTypes({@JsonSubTypes.Type(value = FooClassImpl.class)})
    static abstract class FooClass { }
    static class FooClassImpl extends FooClass { }
    static class FooClassImpl2 extends FooClass { }

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
    static abstract class FooClassNoRegSubTypes { }
    static class FooClassNoRegSubTypesImpl extends FooClassNoRegSubTypes { }

    @JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS)
    @JsonSubTypes({@JsonSubTypes.Type(value = FooMinClassImpl.class)})
    static abstract class FooMinClass { }
    static class FooMinClassImpl extends FooMinClass { }
    static class FooMinClassImpl2 extends FooMinClass { }

    /*
    /************************************************************
    /* Unit tests, valid
    /************************************************************
    */

    private final ObjectMapper MAPPER = vpackMapperBuilder()
            .enable(DeserializationFeature.FAIL_ON_SUBTYPE_CLASS_NOT_REGISTERED)
            .build();

    @Test
    public void testDeserializationIdClass() throws Exception
    {
        //trying to test if JsonSubTypes enforced
        final String foo1 = VPackUtils.toJson(MAPPER.writeValueAsBytes(new FooClassImpl()));
        final String foo2 = VPackUtils.toJson(MAPPER.writeValueAsBytes(new FooClassImpl2()));
        FooClass res1 = MAPPER.readValue(VPackUtils.toVPack(foo1), FooClass.class);
        assertInstanceOf(FooClassImpl.class, res1);
        // next bit should fail because FooClassImpl2 is not listed as a subtype (see mapper config)
        assertThrows(InvalidTypeIdException.class, () -> MAPPER.readValue(VPackUtils.toVPack(foo2), FooClass.class));
    }

    @Test
    public void testDeserializationIdClassNoReg() throws Exception
    {
        final ObjectMapper mapper = newVPackMapper();
        final String foo1 = VPackUtils.toJson(mapper.writeValueAsBytes(new FooClassNoRegSubTypesImpl()));
        // the default mapper should be able to deserialize the object (sub type check not enforced)
        FooClassNoRegSubTypes res1 = mapper.readValue(VPackUtils.toVPack(foo1), FooClassNoRegSubTypes.class);
        assertInstanceOf(FooClassNoRegSubTypesImpl.class, res1);
    }

    @Test
    public void testDefaultDeserializationIdClassNoReg() throws Exception
    {
        //trying to test if JsonSubTypes enforced
        final String foo1 = VPackUtils.toJson(MAPPER.writeValueAsBytes(new FooClassNoRegSubTypesImpl()));
        // next bit should fail because FooClassImpl2 is not listed as a subtype (see mapper config)
        assertThrows(InvalidTypeIdException.class, () -> MAPPER.readValue(VPackUtils.toVPack(foo1), FooClassNoRegSubTypes.class));
    }

    @Test
    public void testDeserializationIdMinimalClass() throws Exception
    {
        //trying to test if JsonSubTypes enforced
        final String foo1 = VPackUtils.toJson(MAPPER.writeValueAsBytes(new FooMinClassImpl()));
        final String foo2 = VPackUtils.toJson(MAPPER.writeValueAsBytes(new FooMinClassImpl2()));
        FooMinClass res1 = MAPPER.readValue(VPackUtils.toVPack(foo1), FooMinClass.class);
        assertInstanceOf(FooMinClassImpl.class, res1);
        // next bit should fail because FooMinClassImpl2 is not listed as a subtype (see mapper config)
        assertThrows(InvalidTypeIdException.class, () -> MAPPER.readValue(VPackUtils.toVPack(foo2), FooMinClass.class));
    }
}
