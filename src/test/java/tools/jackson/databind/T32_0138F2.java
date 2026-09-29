package tools.jackson.databind;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import tools.jackson.databind.PropertyName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import tools.jackson.dataformat.velocypack.*;

class T32_0138F2 {
private static final byte[] VALUE_STRING = VPackWireFixtureTest.hex(
            "45 76 61 6c 75 65");
private static final byte[] POLY_OBJECT = VPackWireFixtureTest.hex(
            "0b 21 02 45 40 74 79 70 65 47 73 75 62 74 79 70 65 "
            + "47 63 6f 6e 74 65 6e 74 45 68 65 6c 6c 6f 03 11");
private static final byte[] POLY_ARRAY = VPackWireFixtureTest.hex(
            "02 23 0b 21 02 45 40 74 79 70 65 47 73 75 62 74 79 70 65 "
            + "47 63 6f 6e 74 65 6e 74 45 68 65 6c 6c 6f 03 11");

    void propertyNameMergeRetainsJacksonPortableSemantics() {
        PropertyName name1 = PropertyName.construct("name1", "ns1");
        PropertyName name2 = PropertyName.construct("name2", "ns2");
        PropertyName empty = PropertyName.construct("", null);
        PropertyName nsX = PropertyName.construct("", "nsX");

        assertSame(name1, PropertyName.merge(name1, name2));
        assertSame(name2, PropertyName.merge(name2, name1));
        assertSame(name1, PropertyName.merge(name1, empty));
        assertSame(name1, PropertyName.merge(empty, name1));
        assertEquals(PropertyName.construct("name1", "nsX"),
                PropertyName.merge(nsX, name1));
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY,
            property = "@type")
    @JsonSubTypes(@JsonSubTypes.Type(name = "subtype", value = Subtype.class))
    private interface Supertype { }
@JsonTypeName("subtype")
    private static class Subtype implements Supertype {
        public String content = "hello";
    }

    void __invoke_propertyNameMergeRetainsJacksonPortableSemantics() throws Exception {
        try {
            propertyNameMergeRetainsJacksonPortableSemantics();
        } finally {
        }
    }

}
