package tools.jackson.databind.struct;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0607Fixture {
private static final ObjectMapper VPACK = new VPackMapper();
private static final ObjectMapper JSON = JsonMapper.builder().build();
private static final byte[] SAME_NAME = VPackWireFixtureTest.hex(
            "14 1e 44 6d 61 69 6c 14 16 44 6d 61 69 6c 4d 74 68 65 20 6d 61 69 6c 20 74 65 78 74 01 01");
private static final byte[] TWO_PREFIXED_CREATOR_PROPERTIES = VPackWireFixtureTest.hex(
            "14 95 01 49 75 6e 72 65 6c 61 74 65 64 4e 75 6e 72 65 6c 61 74 65 64 56 61 6c 75 65 "
          + "4f 66 69 72 73 74 2d 70 72 6f 70 65 72 74 79 31 4c 66 69 72 73 74 2d 76 61 6c 75 65 31 "
          + "4f 66 69 72 73 74 2d 70 72 6f 70 65 72 74 79 32 4c 66 69 72 73 74 2d 76 61 6c 75 65 32 "
          + "50 73 65 63 6f 6e 64 2d 70 72 6f 70 65 72 74 79 31 4d 73 65 63 6f 6e 64 2d 76 61 6c 75 65 31 "
          + "50 73 65 63 6f 6e 64 2d 70 72 6f 70 65 72 74 79 32 4d 73 65 63 6f 6e 64 2d 76 61 6c 75 65 32 05");
private static final byte[] PREFIX_CREATOR = VPackWireFixtureTest.hex(
            "14 22 44 6e 61 6d 65 44 74 65 73 74 4e 69 6e 6e 65 72 2d 70 72 6f 70 65 72 74 79 45 76 61 6c 75 65 02");
private static final byte[] UNKNOWN = VPackWireFixtureTest.hex(
            "14 1d 45 66 69 65 6c 64 45 76 61 6c 75 65 43 62 61 64 49 62 61 64 20 76 61 6c 75 65 02");
private static final byte[] PREFIX_UNKNOWN = VPackWireFixtureTest.hex(
            "14 24 4c 6e 65 73 74 65 64 2e 66 69 65 6c 64 45 76 61 6c 75 65 43 62 61 64 49 62 61 64 20 76 61 6c 75 65 02");
private static final byte[] CREATOR = VPackWireFixtureTest.hex(
            "14 19 44 6e 61 6d 65 44 74 65 73 74 45 66 69 65 6c 64 45 76 61 6c 75 65 02");
private static final byte[] CREATOR_UNKNOWN = VPackWireFixtureTest.hex(
            "14 27 44 6e 61 6d 65 44 74 65 73 74 45 66 69 65 6c 64 45 76 61 6c 75 65 43 62 61 64 49 62 61 64 20 76 61 6c 75 65 03");
private static final byte[] CREATOR_PREFIX = VPackWireFixtureTest.hex(
            "14 20 44 6e 61 6d 65 44 74 65 73 74 4c 6e 65 73 74 65 64 2e 66 69 65 6c 64 45 76 61 6c 75 65 02");
private static final byte[] CREATOR_PREFIX_UNKNOWN = VPackWireFixtureTest.hex(
            "14 2e 44 6e 61 6d 65 44 74 65 73 74 4c 6e 65 73 74 65 64 2e 66 69 65 6c 64 45 76 61 6c 75 65 43 62 61 64 49 62 61 64 20 76 61 6c 75 65 03");

    // Provenance: UnwrappedBasicTest#testUnwrappedWithSamePropertyName().
    void testUnwrappedWithSamePropertyNameVpack() throws Exception {
        SameName result = VPACK.readValue(SAME_NAME, SameName.class);
        assertEquals("the mail text", result.mail.mail.mail);
    }

    // Provenance: UnwrappedBasicTest#testUnwrappedWithTwoUnwrappedProperties().
    void testUnwrappedWithTwoUnwrappedPropertiesVpack() throws Exception {
        TwoUnwrapped result = VPACK.readValue(TWO_PREFIXED_CREATOR_PROPERTIES, TwoUnwrapped.class);
        assertEquals("unrelatedValue", result.unrelated);
        assertEquals("first-value1", result.inner1.property1);
        assertEquals("first-value2", result.inner1.property2);
        assertEquals("second-value1", result.inner2.property1);
        assertEquals("second-value2", result.inner2.property2);
    }

    // Provenance: UnwrappedBasicTest#testUnwrappedWithPrefixCreator().
    void testUnwrappedWithPrefixCreatorVpack() throws Exception {
        PrefixOuter result = VPACK.readValue(PREFIX_CREATOR, PrefixOuter.class);
        assertEquals("value", result.inner.property);
    }

    // Provenance: UnwrappedBasicTest#testUnwrappedWithTypeInfoAndFeatureDisabled().
    void testUnwrappedWithTypeInfoAndFeatureDisabledVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .disable(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS)
                .enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
                .build();
        Outer81 outer = new Outer81();
        outer.p1 = "101";
        outer.inner = new Inner81();
        outer.inner.p2 = "202";
        assertEquals(JSON.readTree("{\"@type\":\"OuterType\",\"p2\":\"202\",\"p1\":\"101\"}"),
                VPACK.readTree(mapper.writeValueAsBytes(outer)));
    }

    // Provenance: UnwrappedBasicTest#testWorkOnUnknownWithAnnotation().
    void testWorkOnUnknownWithAnnotationVpack() throws Exception {
        assertEquals("value", VPACK.readValue(UNKNOWN, UnknownsOk.class).b.field);
    }

    // Provenance: UnwrappedBasicTest#testWorkOnUnknownWithCreatorAndAnnotation().
    void testWorkOnUnknownWithCreatorAndAnnotationVpack() throws Exception {
        CreatorUnknownsOk result = VPACK.readValue(CREATOR_UNKNOWN, CreatorUnknownsOk.class);
        assertEquals("test", result.name);
        assertEquals("value", result.b.field);
    }

    // Provenance: UnwrappedBasicTest#testWorkOnUnknownWithCreatorAndPrefixAndAnnotation().
    void testWorkOnUnknownWithCreatorAndPrefixAndAnnotationVpack() throws Exception {
        CreatorPrefixUnknownsOk result = VPACK.readValue(CREATOR_PREFIX_UNKNOWN, CreatorPrefixUnknownsOk.class);
        assertEquals("test", result.name);
        assertEquals("value", result.b.field);
    }

    // Provenance: UnwrappedBasicTest#testWorkOnUnknownWithPrefixAndAnnotation().
    void testWorkOnUnknownWithPrefixAndAnnotationVpack() throws Exception {
        assertEquals("value", VPACK.readValue(PREFIX_UNKNOWN, PrefixUnknownsOk.class).b.field);
    }

    // Provenance: UnwrappedBasicTest#testWorksOnRegularPropertyUnwrapped().
    void testWorksOnRegularPropertyUnwrappedVpack() throws Exception {
        assertEquals("value", VPACK.readValue(VPackWireFixtureTest.hex("14 0f 45 66 69 65 6c 64 45 76 61 6c 75 65 01"), Regular.class).b.field);
    }

    // Provenance: UnwrappedBasicTest#testWorksOnRegularPropertyUnwrappedWithPrefix().
    void testWorksOnRegularPropertyUnwrappedWithPrefixVpack() throws Exception {
        assertEquals("value", VPACK.readValue(VPackWireFixtureTest.hex("14 16 4c 6e 65 73 74 65 64 2e 66 69 65 6c 64 45 76 61 6c 75 65 01"), Prefix.class).b.field);
    }

    // Provenance: UnwrappedBasicTest#testWorksOnRegularPropertyWithCreator().
    void testWorksOnRegularPropertyWithCreatorVpack() throws Exception {
        Creator result = VPACK.readValue(CREATOR, Creator.class);
        assertEquals("test", result.name);
        assertEquals("value", result.b.field);
    }

    // Provenance: UnwrappedBasicTest#testWorksOnRegularPropertyWithCreatorAndPrefix().
    void testWorksOnRegularPropertyWithCreatorAndPrefixVpack() throws Exception {
        CreatorPrefix result = VPACK.readValue(CREATOR_PREFIX, CreatorPrefix.class);
        assertEquals("test", result.name);
        assertEquals("value", result.b.field);
    }
static class SameName { public MailHolder mail; }
static class MailHolder { @JsonUnwrapped public Mail mail; }
static class Mail { public String mail; }
static class Inner1467 {
        public String property1;
        public String property2;
        @JsonCreator Inner1467(@JsonProperty("property1") String p1, @JsonProperty("property2") String p2) {
            property1 = p1; property2 = p2;
        }
    }
static class TwoUnwrapped {
        public final String unrelated;
        @JsonUnwrapped(prefix = "first-") public final Inner1467 inner1;
        @JsonUnwrapped(prefix = "second-") public final Inner1467 inner2;
        @JsonCreator TwoUnwrapped(@JsonProperty("unrelated") String unrelated,
                @JsonUnwrapped(prefix = "first-") Inner1467 inner1,
                @JsonUnwrapped(prefix = "second-") Inner1467 inner2) {
            this.unrelated = unrelated; this.inner1 = inner1; this.inner2 = inner2;
        }
    }
static class PrefixOuter {
        @JsonUnwrapped(prefix = "inner-") public PrefixInner inner;
    }
static class PrefixInner {
        public final String property;
        @JsonCreator PrefixInner(@JsonProperty("property") String property) { this.property = property; }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@type")
    @JsonTypeName("OuterType")
    static class Outer81 {
        @JsonProperty public String p1;
        public Inner81 inner;
        @JsonUnwrapped public Inner81 getInner() { return inner; }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@type")
    @JsonTypeName("InnerType")
    static class Inner81 { @JsonProperty public String p2; }
static class Bean { public String field; }
static class Regular { @JsonUnwrapped public Bean b; }
@JsonIgnoreProperties(ignoreUnknown = true) static class UnknownsOk { @JsonUnwrapped public Bean b; }
static class Prefix { @JsonUnwrapped(prefix = "nested.") public Bean b; }
@JsonIgnoreProperties(ignoreUnknown = true) static class PrefixUnknownsOk { @JsonUnwrapped(prefix = "nested.") public Bean b; }
static class Creator {
        public final String name;
        @JsonUnwrapped public Bean b;
        @JsonCreator Creator(@JsonProperty("name") String name) { this.name = name; }
    }
@JsonIgnoreProperties(ignoreUnknown = true) static class CreatorUnknownsOk {
        public final String name;
        @JsonUnwrapped public Bean b;
        @JsonCreator CreatorUnknownsOk(@JsonProperty("name") String name) { this.name = name; }
    }
static class CreatorPrefix {
        public final String name;
        @JsonUnwrapped(prefix = "nested.") public Bean b;
        @JsonCreator CreatorPrefix(@JsonProperty("name") String name) { this.name = name; }
    }
@JsonIgnoreProperties(ignoreUnknown = true) static class CreatorPrefixUnknownsOk {
        public final String name;
        @JsonUnwrapped(prefix = "nested.") public Bean b;
        @JsonCreator CreatorPrefixUnknownsOk(@JsonProperty("name") String name) { this.name = name; }
    }

    void __invoke_testUnwrappedWithSamePropertyNameVpack() throws Exception {
        try {
            testUnwrappedWithSamePropertyNameVpack();
        } finally {
        }
    }


    void __invoke_testUnwrappedWithTwoUnwrappedPropertiesVpack() throws Exception {
        try {
            testUnwrappedWithTwoUnwrappedPropertiesVpack();
        } finally {
        }
    }


    void __invoke_testUnwrappedWithPrefixCreatorVpack() throws Exception {
        try {
            testUnwrappedWithPrefixCreatorVpack();
        } finally {
        }
    }


    void __invoke_testUnwrappedWithTypeInfoAndFeatureDisabledVpack() throws Exception {
        try {
            testUnwrappedWithTypeInfoAndFeatureDisabledVpack();
        } finally {
        }
    }


    void __invoke_testWorkOnUnknownWithAnnotationVpack() throws Exception {
        try {
            testWorkOnUnknownWithAnnotationVpack();
        } finally {
        }
    }


    void __invoke_testWorkOnUnknownWithCreatorAndAnnotationVpack() throws Exception {
        try {
            testWorkOnUnknownWithCreatorAndAnnotationVpack();
        } finally {
        }
    }


    void __invoke_testWorkOnUnknownWithCreatorAndPrefixAndAnnotationVpack() throws Exception {
        try {
            testWorkOnUnknownWithCreatorAndPrefixAndAnnotationVpack();
        } finally {
        }
    }


    void __invoke_testWorkOnUnknownWithPrefixAndAnnotationVpack() throws Exception {
        try {
            testWorkOnUnknownWithPrefixAndAnnotationVpack();
        } finally {
        }
    }


    void __invoke_testWorksOnRegularPropertyUnwrappedVpack() throws Exception {
        try {
            testWorksOnRegularPropertyUnwrappedVpack();
        } finally {
        }
    }


    void __invoke_testWorksOnRegularPropertyUnwrappedWithPrefixVpack() throws Exception {
        try {
            testWorksOnRegularPropertyUnwrappedWithPrefixVpack();
        } finally {
        }
    }


    void __invoke_testWorksOnRegularPropertyWithCreatorVpack() throws Exception {
        try {
            testWorksOnRegularPropertyWithCreatorVpack();
        } finally {
        }
    }


    void __invoke_testWorksOnRegularPropertyWithCreatorAndPrefixVpack() throws Exception {
        try {
            testWorksOnRegularPropertyWithCreatorAndPrefixVpack();
        } finally {
        }
    }

}
