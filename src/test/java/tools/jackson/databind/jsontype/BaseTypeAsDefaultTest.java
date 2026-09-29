package tools.jackson.databind.jsontype;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.exc.InvalidTypeIdException;
import tools.jackson.databind.testutil.DatabindTestUtil;

import static org.junit.jupiter.api.Assertions.*;

public class BaseTypeAsDefaultTest extends DatabindTestUtil
{
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
    static class Parent {
    }

    static class Child extends Parent {
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class", defaultImpl = ChildOfChild.class)
    static abstract class AbstractParentWithDefault {
    }

    static class ChildOfAbstract extends AbstractParentWithDefault {
    }

    static class ChildOfChild extends ChildOfAbstract {
    }

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    private final ObjectMapper MAPPER_WITH_BASE =  vpackMapperBuilder()
                .enable(MapperFeature.USE_BASE_TYPE_AS_DEFAULT_IMPL)
                .build();

    private final ObjectMapper MAPPER_WITHOUT_BASE = vpackMapperBuilder()
            .disable(MapperFeature.USE_BASE_TYPE_AS_DEFAULT_IMPL)
            .build();

    private final ObjectMapper MAPPER_WITHOUT_BASE_OR_SUBTYPE_ID = vpackMapperBuilder()
            .disable(MapperFeature.USE_BASE_TYPE_AS_DEFAULT_IMPL)
            .disable(MapperFeature.REQUIRE_TYPE_ID_FOR_SUBTYPES)
            .build();

    @Test
    public void testPositiveForParent() throws Exception {
        Object o = MAPPER_WITH_BASE.readerFor(Parent.class).readValue(VPackUtils.toVPack("{}"));
        assertEquals(o.getClass(), Parent.class);
    }

    @Test
    public void testPositiveForChild() throws Exception {
        Object o = MAPPER_WITH_BASE.readerFor(Child.class).readValue(VPackUtils.toVPack("{}"));
        assertEquals(o.getClass(), Child.class);
    }

    @Test
    public void testNegativeForParent() throws Exception {
        InvalidTypeIdException e = assertThrows(InvalidTypeIdException.class,
                () -> MAPPER_WITHOUT_BASE.readerFor(Parent.class).readValue(VPackUtils.toVPack("{}")));
        verifyException(e, "missing type id property '@class'");
    }

    // 12-Mar-2023, tatu: As per [databind#2968] this should work like so, but
    //   alas fix for 2.x not directly portable to 3.0 so need to comment out
    @Test
    public void testNegativeForChild() throws Exception {
        InvalidTypeIdException e = assertThrows(InvalidTypeIdException.class,
                () -> MAPPER_WITHOUT_BASE.readerFor(Child.class).readValue(VPackUtils.toVPack("{}")));
        verifyException(e, "missing type id property '@class'");
    }

    @Test
    public void testNegativeForChildWithoutRequiringTypeId() throws Exception {
        Child child = MAPPER_WITHOUT_BASE_OR_SUBTYPE_ID.readerFor(Child.class).readValue(VPackUtils.toVPack("{}"));

        assertEquals(Child.class, child.getClass());
    }

    @Test
    public void testConversionForAbstractWithDefault() throws Exception {
        // should pass shouldn't it?
        Object o = MAPPER_WITH_BASE.readerFor(AbstractParentWithDefault.class).readValue(VPackUtils.toVPack("{}"));
        assertEquals(o.getClass(), ChildOfChild.class);
    }

    @Test
    public void testPositiveWithTypeSpecification() throws Exception {
        Object o = MAPPER_WITH_BASE.readerFor(Parent.class)
                .readValue(VPackUtils.toVPack("{\"@class\":\""+Child.class.getName()+"\"}"));
        assertEquals(o.getClass(), Child.class);
    }

    @Test
    public void testPositiveWithManualDefault() throws Exception {
        Object o = MAPPER_WITH_BASE.readerFor(ChildOfAbstract.class).readValue(VPackUtils.toVPack("{}"));

        assertEquals(o.getClass(), ChildOfChild.class);
    }
}
