package tools.jackson.databind.jsontype;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.jsontype.subpackage.SubCSubPackage;
import tools.jackson.databind.testutil.DatabindTestUtil;
import com.arangodb.jackson.dataformat.velocypack.VPackMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

// For [databind#4983]: `JsonTypeInfo.Id.MINIMAL_CLASS` generates invalid type on sub-package
// For [databind#5247]: Faulty Serialization using `Id.MINIMAL_CLASS` (dup of #4983)
public class TestSubtypesSubPackage extends DatabindTestUtil
{
	// Extended by SubCSubPackage which is in a sub package
    @JsonTypeInfo(use=JsonTypeInfo.Id.MINIMAL_CLASS)
    public static abstract class SuperType {

        public static class InnerType extends SuperType {
        	public int b = 2;
        }
    }

    /*
    /**********************************************************
    /* Unit tests
    /**********************************************************
     */

    private final ObjectMapper MAPPER = new VPackMapper();

    @Test
    public void testSubPackage() throws Exception
    {
    	// type should be computed consider base=SuperType (as it provides the annotation)
    	SubCSubPackage bean = new SubCSubPackage();
        assertEquals("{\"@c\":\".subpackage.SubCSubPackage\",\"c\":2}", VPackUtils.toJson(MAPPER.writeValueAsBytes(bean)));
    }

    @Test
    public void testInner() throws Exception
    {
    	// type should be computed consider base=SuperType (as it provides the annotation)
    	SuperType.InnerType bean = new SuperType.InnerType();
        assertEquals("{\"@c\":\".TestSubtypesSubPackage$SuperType$InnerType\",\"b\":2}", VPackUtils.toJson(MAPPER.writeValueAsBytes(bean)));
    }

    // [databind#5247]: verify round-trip (serialize then deserialize) works for sub-package types
    @Test
    public void testSubPackageRoundTrip() throws Exception
    {
        SubCSubPackage original = new SubCSubPackage();
        String json = VPackUtils.toJson(MAPPER.writeValueAsBytes(original));
        SuperType result = MAPPER.readValue(VPackUtils.toVPack(json), SuperType.class);
        assertInstanceOf(SubCSubPackage.class, result);
        assertEquals(original.c, ((SubCSubPackage) result).c);
    }

    // [databind#5247]: verify round-trip works for inner types too
    @Test
    public void testInnerRoundTrip() throws Exception
    {
        SuperType.InnerType original = new SuperType.InnerType();
        String json = VPackUtils.toJson(MAPPER.writeValueAsBytes(original));
        SuperType result = MAPPER.readValue(VPackUtils.toVPack(json), SuperType.class);
        assertInstanceOf(SuperType.InnerType.class, result);
        assertEquals(original.b, ((SuperType.InnerType) result).b);
    }
}
