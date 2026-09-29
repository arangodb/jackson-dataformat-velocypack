package tools.jackson.databind.introspect;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.MapperConfig;
import tools.jackson.databind.introspect.Annotated;
import tools.jackson.databind.introspect.AnnotatedConstructor;
import tools.jackson.databind.introspect.AnnotatedField;
import tools.jackson.databind.introspect.AnnotatedMember;
import tools.jackson.databind.introspect.AnnotatedMethod;
import tools.jackson.databind.introspect.AnnotatedParameter;
import tools.jackson.databind.introspect.JacksonAnnotationIntrospector;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0407F1 {
private static final byte[] PROP_VALUE = VPackWireFixtureTest.hex(
            "0b 0d 01 44 70 72 6f 70 43 76 61 6c 03");
private static final byte[] RENAMED_B = VPackWireFixtureTest.hex(
            "0b 07 01 41 62 37 03");
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");

    // Provenance: TestScalaLikeImplicitProperties#testValProperty().
    void testValPropertyVpack() throws Exception {
        assertArrayEquals(PROP_VALUE,
                manglingMapper().writeValueAsBytes(new ValProperty("val")));
    }

    // Provenance: TestScalaLikeImplicitProperties#testValWithBeanProperty().
    void testValWithBeanPropertyVpack() throws Exception {
        assertArrayEquals(PROP_VALUE,
                manglingMapper().writeValueAsBytes(new ValWithBeanProperty("val")));
    }

    // Provenance: TestScalaLikeImplicitProperties#testVarProperty().
    void testVarPropertyVpack() throws Exception {
        ObjectMapper mapper = manglingMapper();
        assertArrayEquals(PROP_VALUE,
                mapper.writeValueAsBytes(new VarProperty("val")));
        VarProperty result = mapper.readValue(
                VPackWireFixtureTest.hex("0b 0e 01 44 70 72 6f 70 44 72 65 61 64 03"),
                VarProperty.class);
        assertEquals("read", result.prop());
    }

    // Provenance: TestScalaLikeImplicitProperties#testVarWithBeanProperty().
    void testVarWithBeanPropertyVpack() throws Exception {
        ObjectMapper mapper = manglingMapper();
        assertArrayEquals(PROP_VALUE,
                mapper.writeValueAsBytes(new VarWithBeanProperty("val")));
        VarWithBeanProperty result = mapper.readValue(
                VPackWireFixtureTest.hex("0b 0e 01 44 70 72 6f 70 44 72 65 61 64 03"),
                VarWithBeanProperty.class);
        assertEquals("read", result.prop());
    }

    // Provenance: TestScalaLikeImplicitProperties#testGetterSetterProperty().
    void testGetterSetterPropertyVpack() throws Exception {
        ObjectMapper mapper = manglingMapper();
        assertArrayEquals(
                VPackWireFixtureTest.hex("0b 11 01 44 70 72 6f 70 47 67 65 74 2d 73 65 74 03"),
                mapper.writeValueAsBytes(new GetterSetterProperty()));
        GetterSetterProperty result = mapper.readValue(
                VPackWireFixtureTest.hex("0b 0e 01 44 70 72 6f 70 44 72 65 61 64 03"),
                GetterSetterProperty.class);
        assertEquals("read", result.prop());
    }
private static ObjectMapper manglingMapper() {
        return VPackMapper.builder()
                .annotationIntrospector(new NameMangler())
                .build();
    }
static class NameMangler extends JacksonAnnotationIntrospector {
        private static final long serialVersionUID = 1L;

        @Override
        public String findImplicitPropertyName(MapperConfig<?> config, AnnotatedMember member) {
            String name = null;
            if (member instanceof AnnotatedField) {
                name = member.getName();
                if (name.endsWith("‿")) {
                    return name.substring(0, name.length() - 1);
                }
            } else if (member instanceof AnnotatedMethod) {
                name = member.getName();
                if (name.endsWith("_⁀")) {
                    return name.substring(0, name.length() - 2);
                }
                if (!name.startsWith("get") && !name.startsWith("set")) {
                    return name;
                }
            } else if (member instanceof AnnotatedParameter) {
                return "prop";
            }
            return null;
        }

        @Override
        public JsonCreator.Mode findCreatorAnnotation(MapperConfig<?> config, Annotated annotated) {
            return (annotated instanceof AnnotatedConstructor)
                    ? JsonCreator.Mode.DEFAULT : null;
        }
    }
static class Bean323WithIgnore {
        @JsonIgnore
        private int a;

        Bean323WithIgnore(@JsonProperty("a") int value) {
            a = value;
        }

        @JsonProperty("b")
        private int getA() {
            return a;
        }
    }
static class ValProperty {
        private final String prop‿;

        ValProperty(String prop) {
            prop‿ = prop;
        }

        public String prop() {
            return prop‿;
        }
    }
static class ValWithBeanProperty {
        private final String prop‿;

        ValWithBeanProperty(String prop) {
            prop‿ = prop;
        }

        public String prop() {
            return prop‿;
        }

        public String getProp() {
            return prop‿;
        }
    }
static class VarProperty {
        private String prop‿;

        VarProperty(String prop) {
            prop‿ = prop;
        }

        public String prop() {
            return prop‿;
        }

        public void prop_⁀(String prop) {
            prop‿ = prop;
        }
    }
static class VarWithBeanProperty {
        private String prop‿;

        VarWithBeanProperty(String prop) {
            prop‿ = prop;
        }

        public String prop() {
            return prop‿;
        }

        public void prop_⁀(String prop) {
            prop‿ = prop;
        }

        public String getProp() {
            return prop‿;
        }

        public void setProp(String prop) {
            prop‿ = prop;
        }
    }
static class GetterSetterProperty {
        private String _prop_impl = "get-set";

        public String prop() {
            return _prop_impl;
        }

        public void prop_⁀(String prop) {
            _prop_impl = prop;
        }
    }
static class Bean1592 {
        @JsonSerialize(as = Integer.class)
        public int i;

        @JsonDeserialize(as = Long.class)
        public long l;
    }

    void __invoke_testValPropertyVpack() throws Exception {
        try {
            testValPropertyVpack();
        } finally {
        }
    }


    void __invoke_testValWithBeanPropertyVpack() throws Exception {
        try {
            testValWithBeanPropertyVpack();
        } finally {
        }
    }


    void __invoke_testVarPropertyVpack() throws Exception {
        try {
            testVarPropertyVpack();
        } finally {
        }
    }


    void __invoke_testVarWithBeanPropertyVpack() throws Exception {
        try {
            testVarWithBeanPropertyVpack();
        } finally {
        }
    }


    void __invoke_testGetterSetterPropertyVpack() throws Exception {
        try {
            testGetterSetterPropertyVpack();
        } finally {
        }
    }

}
