package tools.jackson.databind.mixins;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0466F2 {
private static final byte[] A_1 = VPackWireFixtureTest.hex(
            "14 07 41 61 41 31 01");
private static final byte[] A_1_BANANA_2 = VPackWireFixtureTest.hex(
            "14 10 41 61 41 31 46 62 61 6e 61 6e 61 41 32 02");
private static final byte[] BANANA_2 = VPackWireFixtureTest.hex(
            "14 0c 46 62 61 6e 61 6e 61 41 32 01");
private static final byte[] A_A1_B2_B2 = VPackWireFixtureTest.hex(
            "14 0e 41 61 42 61 31 42 62 32 42 62 32 02");
private static final byte[] A_XXX = VPackWireFixtureTest.hex(
            "14 09 41 61 43 58 58 58 01");
private static final byte[] X_42 = VPackWireFixtureTest.hex(
            "14 07 41 78 28 2a 01");
private static final byte[] VIEWED_NESTED = VPackWireFixtureTest.hex(
            "14 0e 44 6e 61 6d 65 45 73 68 6f 77 6e 01");
private static final byte[] NAME_MYNAME_AGE_29 = VPackWireFixtureTest.hex(
            "14 15 44 6e 61 6d 65 46 6d 79 6e 61 6d 65"
          + "43 61 67 65 28 1d 02");
private static final ObjectMapper MAPPER = new VPackMapper();

    // Provenance: TestMixinSerWithViews#testDataBindingUsage().
    void testDataBindingUsageVpack() throws Exception {
        ObjectMapper mapper = createViewMapper();
        byte[] actual = mapper.writerWithView(Views.View.class)
                .writeValueAsBytes(new ComplexTestData());
        JsonNode actualTree = MAPPER.readTree(actual);

        assertEquals(MAPPER.readTree(VIEWED_NESTED), actualTree.get("testData"));
        assertEquals("complexValue", actualTree.get("nameComplex").asText());
        assertTrue(actualTree.has("nameComplexHidden"));
        assertEquals(2, actualTree.get("testDataArray").size());
        assertEquals("shown", actualTree.get("testDataArray").get(0).get("name").asText());
        assertTrue(actualTree.get("testDataArray").get(1).isNull());
        assertFalse(actualTree.toString().contains("nameHidden"));
    }

    // Provenance: TestMixinSerWithViews#testIssue560().
    void testIssue560Vpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .disable(MapperFeature.DEFAULT_VIEW_INCLUSION)
                .addMixIn(ViewedBean.class, ViewedBeanMixIn.class)
                .build();

        assertEquals(MAPPER.readTree(NAME_MYNAME_AGE_29),
                MAPPER.readTree(mapper.writerWithView(AView.class)
                        .writeValueAsBytes(new ViewedBean("myname", 29, "mysurname"))));
    }
private static ObjectMapper createViewMapper() {
        Map<Class<?>, Class<?>> sourceMixins = new HashMap<>();
        sourceMixins.put(SimpleTestData.class, SimpleTestDataMixIn.class);
        sourceMixins.put(ComplexTestData.class, ComplexTestDataMixIn.class);
        return VPackMapper.builder()
                .changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(
                        JsonInclude.Include.NON_NULL))
                .disable(MapperFeature.DEFAULT_VIEW_INCLUSION)
                .addMixIns(sourceMixins)
                .build();
    }
static class BaseFieldClass {
        public String a;
        protected String b;

        BaseFieldClass(String a, String b) {
            this.a = a;
            this.b = b;
        }
    }
static class SubFieldClass extends BaseFieldClass {
        SubFieldClass(String a, String b) {
            super(a, b);
        }
    }
abstract static class FieldMixIn {
        @JsonProperty("banana")
        public String b;
    }
abstract static class FieldMixIn2 {
        @JsonIgnore
        public String a;

        @JsonProperty
        public String xyz;
    }
static class MethodBaseClass {
        private String a;
        private String b;

        protected MethodBaseClass() { }

        MethodBaseClass(String a, String b) {
            this.a = a;
            this.b = b;
        }

        @JsonProperty("b")
        public String takeB() {
            return b;
        }
    }
abstract static class MethodMixIn extends MethodBaseClass {
        @JsonProperty
        String a;

        @Override
        @JsonProperty("b2")
        public abstract String takeB();

        @JsonProperty
        abstract String getFoobar();
    }
static class MethodLeafClass extends MethodBaseClass {
        MethodLeafClass(String a, String b) {
            super(a, b);
        }

        @Override
        @JsonIgnore
        public String takeB() {
            return null;
        }
    }
static class EmptyBean { }
static class SimpleBean extends EmptyBean {
        int x() {
            return 42;
        }
    }
abstract static class SimpleMethodMixIn {
        @JsonProperty("x")
        abstract int x();

        @JsonProperty("notreally")
        public int xxx() {
            return 3;
        }

        public abstract int getIt();
    }
static class SimpleTestData {
        private String name = "shown";
        private String nameHidden = "hidden";

        public String getName() {
            return name;
        }

        public String getNameHidden() {
            return nameHidden;
        }
    }
static class ComplexTestData {
        private String nameNull;
        private String nameComplex = "complexValue";
        private String nameComplexHidden = "nameComplexHiddenValue";
        private SimpleTestData testData = new SimpleTestData();
        private SimpleTestData[] testDataArray = new SimpleTestData[] {
                new SimpleTestData(), null
        };

        public String getNameNull() {
            return nameNull;
        }

        public String getNameComplex() {
            return nameComplex;
        }

        public String getNameComplexHidden() {
            return nameComplexHidden;
        }

        public SimpleTestData getTestData() {
            return testData;
        }

        public SimpleTestData[] getTestDataArray() {
            return testDataArray;
        }
    }
interface SimpleTestDataMixIn {
        @JsonView(Views.View.class)
        String getName();
    }
interface ComplexTestDataMixIn {
        @JsonView(Views.View.class)
        String getNameNull();

        @JsonView(Views.View.class)
        String getNameComplex();

        @JsonView(Views.View.class)
        String getNameComplexHidden();

        @JsonView(Views.View.class)
        SimpleTestData getTestData();

        @JsonView(Views.View.class)
        SimpleTestData[] getTestDataArray();
    }
static class Views {
        static class View { }
    }
static class ViewedBean {
        private String name;
        private int age;
        private String surname;

        ViewedBean(String name, int age, String surname) {
            this.name = name;
            this.age = age;
            this.surname = surname;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        public String getSurname() {
            return surname;
        }
    }
interface AView { }
abstract static class ViewedBeanMixIn {
        @JsonProperty("name")
        @JsonView(AView.class)
        abstract String getName();

        @JsonProperty("age")
        @JsonView(AView.class)
        abstract int getAge();
    }

    void __invoke_testDataBindingUsageVpack() throws Exception {
        try {
            testDataBindingUsageVpack();
        } finally {
        }
    }


    void __invoke_testIssue560Vpack() throws Exception {
        try {
            testIssue560Vpack();
        } finally {
        }
    }

}
