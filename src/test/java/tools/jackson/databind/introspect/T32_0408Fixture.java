package tools.jackson.databind.introspect;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import tools.jackson.databind.BeanDescription;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.introspect.BeanPropertyDefinition;
import tools.jackson.databind.introspect.VisibilityChecker;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0408Fixture {
private static final byte[] GETTER_XY = VPackWireFixtureTest.hex(
            "0b 0b 02 41 78 3e 41 79 31 03 06");
private static final byte[] GETTER_X = VPackWireFixtureTest.hex(
            "0b 07 01 41 78 3e 03");
private static final byte[] IS_GETTER_OK = VPackWireFixtureTest.hex(
            "0b 08 01 42 6f 6b 31 03");
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");

    // Provenance: VisibilityForSerializationTest#testGlobalAutoDetection().
    void testGlobalAutoDetectionVpack() throws Exception {
        ObjectMapper mapper = new VPackMapper();
        assertArrayEquals(GETTER_XY, mapper.writeValueAsBytes(new GetterClass()));

        mapper = VPackMapper.builder()
                .changeDefaultVisibility(vc -> vc.withVisibility(
                        PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE))
                .build();
        assertArrayEquals(GETTER_X, mapper.writeValueAsBytes(new GetterClass()));
    }

    // Provenance: VisibilityForSerializationTest#testPerClassAutoDetection().
    void testPerClassAutoDetectionVpack() throws Exception {
        ObjectMapper mapper = new VPackMapper();
        assertArrayEquals(GETTER_X, mapper.writeValueAsBytes(new DisabledGetterClass()));

        mapper = VPackMapper.builder()
                .changeDefaultVisibility(vc -> vc.withVisibility(
                        PropertyAccessor.GETTER, JsonAutoDetect.Visibility.PUBLIC_ONLY))
                .build();
        assertArrayEquals(GETTER_XY, mapper.writeValueAsBytes(new EnabledGetterClass()));
    }

    // Provenance: VisibilityForSerializationTest#testPerClassAutoDetectionForIsGetter().
    void testPerClassAutoDetectionForIsGetterVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .changeDefaultVisibility(vc -> vc
                        .withVisibility(PropertyAccessor.GETTER,
                                JsonAutoDetect.Visibility.PUBLIC_ONLY)
                        .withVisibility(PropertyAccessor.IS_GETTER,
                                JsonAutoDetect.Visibility.NONE))
                .build();
        assertArrayEquals(EMPTY_OBJECT, mapper.writeValueAsBytes(new EnabledIsGetterClass()));
    }

    // Provenance: VisibilityForSerializationTest#testVisibilityCheckerHashCode().
    void testVisibilityCheckerHashCodeVpack() {
        VisibilityChecker defaultInstance = VisibilityChecker.defaultInstance();
        VisibilityChecker allPublic = VisibilityChecker.allPublicInstance();
        VisibilityChecker another = new VisibilityChecker(
                JsonAutoDetect.Visibility.PUBLIC_ONLY,
                JsonAutoDetect.Visibility.PUBLIC_ONLY,
                JsonAutoDetect.Visibility.PUBLIC_ONLY,
                JsonAutoDetect.Visibility.PUBLIC_ONLY,
                JsonAutoDetect.Visibility.PUBLIC_ONLY,
                JsonAutoDetect.Visibility.PUBLIC_ONLY);

        assertEquals(allPublic.hashCode(), another.hashCode());
        assertTrue(defaultInstance.hashCode() != allPublic.hashCode());
    }

    // Provenance: VisibilityForSerializationTest#testVisibilityCheckerEquals().
    void testVisibilityCheckerEqualsVpack() {
        VisibilityChecker defaultInstance = VisibilityChecker.defaultInstance();
        VisibilityChecker allPublic = VisibilityChecker.allPublicInstance();
        VisibilityChecker another = new VisibilityChecker(
                JsonAutoDetect.Visibility.PUBLIC_ONLY,
                JsonAutoDetect.Visibility.PUBLIC_ONLY,
                JsonAutoDetect.Visibility.PUBLIC_ONLY,
                JsonAutoDetect.Visibility.PUBLIC_ONLY,
                JsonAutoDetect.Visibility.PUBLIC_ONLY,
                JsonAutoDetect.Visibility.PUBLIC_ONLY);

        assertEquals(defaultInstance, defaultInstance);
        assertEquals(allPublic, another);
        assertEquals(allPublic,
                allPublic.withFieldVisibility(JsonAutoDetect.Visibility.DEFAULT));
        assertEquals(allPublic,
                allPublic.withGetterVisibility(JsonAutoDetect.Visibility.DEFAULT));
        assertEquals(allPublic,
                allPublic.withIsGetterVisibility(JsonAutoDetect.Visibility.DEFAULT));
        assertFalse(allPublic.equals(
                allPublic.withSetterVisibility(JsonAutoDetect.Visibility.DEFAULT)));
        assertEquals(allPublic,
                allPublic.withCreatorVisibility(JsonAutoDetect.Visibility.DEFAULT));

        assertFalse(defaultInstance.equals(allPublic));
        assertFalse(allPublic.equals(allPublic.withFieldVisibility(
                JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC)));
        assertFalse(allPublic.equals(allPublic.withGetterVisibility(
                JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC)));
        assertFalse(allPublic.equals(allPublic.withIsGetterVisibility(
                JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC)));
        assertFalse(allPublic.equals(allPublic.withSetterVisibility(
                JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC)));
        assertFalse(allPublic.equals(allPublic.withCreatorVisibility(
                JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC)));
        assertFalse(defaultInstance.equals("not a VisibilityChecker"));
    }

    // Provenance: VisibilityForSerializationTest#testVisibilityCheckerMisc().
    void testVisibilityCheckerMiscVpack() {
        VisibilityChecker checker = new VisibilityChecker(JsonAutoDetect.Visibility.DEFAULT);
        assertEquals(VisibilityChecker.defaultInstance(), checker);
        assertEquals(VisibilityChecker.defaultInstance(),
                checker.with(JsonAutoDetect.Visibility.DEFAULT));
        assertEquals(checker, checker.withVisibility(PropertyAccessor.NONE,
                JsonAutoDetect.Visibility.ANY));
    }

    
    // Provenance: VisibilityForSerializationTest#testVisibilityCheckerDeprecated().
    void testVisibilityCheckerDeprecatedVpack() {
        JsonAutoDetect annotation = EnabledGetterClass.class
                .getAnnotation(JsonAutoDetect.class);
        VisibilityChecker checker = new VisibilityChecker(annotation);
        VisibilityChecker expected = new VisibilityChecker(
                JsonAutoDetect.Visibility.DEFAULT,
                JsonAutoDetect.Visibility.DEFAULT,
                JsonAutoDetect.Visibility.NONE,
                JsonAutoDetect.Visibility.DEFAULT,
                JsonAutoDetect.Visibility.DEFAULT,
                JsonAutoDetect.Visibility.DEFAULT);
        assertEquals(expected, checker);
    }

    // Provenance: VisibilityForSerializationTest#testVisibilityFeatures().
    void testVisibilityFeaturesVpack() {
        ObjectMapper mapper = VPackMapper.builder()
                .disable(MapperFeature.USE_GETTERS_AS_SETTERS, MapperFeature.INFER_PROPERTY_MUTATORS)
                .enable(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS, MapperFeature.USE_ANNOTATIONS)
                .changeDefaultVisibility(vc -> vc.withVisibility(
                        PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE))
                .build();
        BeanDescription description = mapper._serializationContext()
                .introspectBeanDescription(mapper.constructType(TCls.class));
        List<BeanPropertyDefinition> properties = description.findProperties();
        assertEquals(1, properties.size());
    }
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
    static class DisabledGetterClass {
        @JsonProperty("x") public int getX() { return -2; }
        public int getY() { return 1; }
    }
@JsonAutoDetect(isGetterVisibility = JsonAutoDetect.Visibility.NONE)
    static class EnabledGetterClass {
        @JsonProperty("x") public int getX() { return -2; }
        public int getY() { return 1; }
        public boolean isOk() { return true; }
    }
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
    static class EnabledIsGetterClass {
        public int getY() { return 1; }
        public boolean isOk() { return true; }
    }
static class GetterClass {
        @JsonProperty("x") public int getX() { return -2; }
        public int getY() { return 1; }
    }
static class TCls {
        @JsonProperty("groupname")
        private String groupname;

        public void setName(String value) { groupname = value; }
        public String getName() { return groupname; }
    }

    void __invoke_testGlobalAutoDetectionVpack() throws Exception {
        try {
            testGlobalAutoDetectionVpack();
        } finally {
        }
    }


    void __invoke_testPerClassAutoDetectionVpack() throws Exception {
        try {
            testPerClassAutoDetectionVpack();
        } finally {
        }
    }


    void __invoke_testPerClassAutoDetectionForIsGetterVpack() throws Exception {
        try {
            testPerClassAutoDetectionForIsGetterVpack();
        } finally {
        }
    }


    void __invoke_testVisibilityCheckerHashCodeVpack() throws Exception {
        try {
            testVisibilityCheckerHashCodeVpack();
        } finally {
        }
    }


    void __invoke_testVisibilityCheckerEqualsVpack() throws Exception {
        try {
            testVisibilityCheckerEqualsVpack();
        } finally {
        }
    }


    void __invoke_testVisibilityCheckerMiscVpack() throws Exception {
        try {
            testVisibilityCheckerMiscVpack();
        } finally {
        }
    }


    void __invoke_testVisibilityCheckerDeprecatedVpack() throws Exception {
        try {
            testVisibilityCheckerDeprecatedVpack();
        } finally {
        }
    }


    void __invoke_testVisibilityFeaturesVpack() throws Exception {
        try {
            testVisibilityFeaturesVpack();
        } finally {
        }
    }

}
