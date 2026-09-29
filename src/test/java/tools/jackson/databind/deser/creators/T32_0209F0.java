package tools.jackson.databind.deser.creators;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.BeanDescription;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.deser.ValueInstantiator;
import tools.jackson.databind.deser.ValueInstantiators;
import tools.jackson.databind.cfg.MapperConfig;
import tools.jackson.databind.introspect.AnnotatedMember;
import tools.jackson.databind.introspect.AnnotatedMethod;
import tools.jackson.databind.introspect.AnnotatedParameter;
import tools.jackson.databind.introspect.AnnotatedWithParams;
import tools.jackson.databind.introspect.JacksonAnnotationIntrospector;
import tools.jackson.databind.module.SimpleModule;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0209F0 {
private static final byte[] MULTI_ARG_VISIBLE = VPackWireFixtureTest.hex(
            "14 0e 41 62 28 0d 41 63 32 41 61 20 9d 03");
private static final byte[] MULTI_ARG_PARTIAL_OVERRIDE = VPackWireFixtureTest.hex(
            "14 10 42 62 32 37 41 63 29 de 00 41 61 20 9d 03");
private static final byte[] MULTI_ARG_NOT_VISIBLE = VPackWireFixtureTest.hex(
            "14 0b 41 62 28 0d 41 61 20 9d 02");
private static final byte[] BIG_PARTIAL = VPackWireFixtureTest.hex(
            "14 17 42 76 37 37 42 76 38 38 43 76 32 39 28 1d "
          + "43 76 33 35 28 23 04");
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final ObjectMapper MULTI_ARG_MAPPER = VPackMapper.builder()
            .annotationIntrospector(new MyParamIntrospector())
            .build();

    // Provenance: JsonCreatorNoArgs4777Test#testCreatorDetection4777.
    void testCreatorDetection4777() throws Exception {
        SimpleModule module = new SimpleModule() {
            @Override
            public void setupModule(SetupContext context) {
                super.setupModule(context);
                context.addValueInstantiators(new Instantiators4777());
            }
        };
        ObjectMapper mapper = VPackMapper.builder().addModule(module).build();

        Foo4777 result = mapper.readValue(EMPTY_OBJECT, Foo4777.class);
        assertNotNull(result);
    }
static class MultiArgCtorBean {
        protected int _a, _b;
        public int c;

        public MultiArgCtorBean(int a, int b) {
            _a = a;
            _b = b;
        }
    }
static class MultiArgCtorBeanWithAnnotations {
        protected int _a, _b;
        public int c;

        public MultiArgCtorBeanWithAnnotations(int a, @JsonProperty("b2") int b) {
            _a = a;
            _b = b;
        }
    }
static class Biggie {
        final int[] stuff;

        @JsonCreator
        public Biggie(
                @JsonProperty("v1") int v1, @JsonProperty("v2") int v2,
                @JsonProperty("v3") int v3, @JsonProperty("v4") int v4,
                @JsonProperty("v5") int v5, @JsonProperty("v6") int v6,
                @JsonProperty("v7") int v7, @JsonProperty("v8") int v8,
                @JsonProperty("v9") int v9, @JsonProperty("v10") int v10,
                @JsonProperty("v11") int v11, @JsonProperty("v12") int v12,
                @JsonProperty("v13") int v13, @JsonProperty("v14") int v14,
                @JsonProperty("v15") int v15, @JsonProperty("v16") int v16,
                @JsonProperty("v17") int v17, @JsonProperty("v18") int v18,
                @JsonProperty("v19") int v19, @JsonProperty("v20") int v20,
                @JsonProperty("v21") int v21, @JsonProperty("v22") int v22,
                @JsonProperty("v23") int v23, @JsonProperty("v24") int v24,
                @JsonProperty("v25") int v25, @JsonProperty("v26") int v26,
                @JsonProperty("v27") int v27, @JsonProperty("v28") int v28,
                @JsonProperty("v29") int v29, @JsonProperty("v30") int v30,
                @JsonProperty("v31") int v31, @JsonProperty("v32") int v32,
                @JsonProperty("v33") int v33, @JsonProperty("v34") int v34,
                @JsonProperty("v35") int v35, @JsonProperty("v36") int v36,
                @JsonProperty("v37") int v37, @JsonProperty("v38") int v38,
                @JsonProperty("v39") int v39, @JsonProperty("v40") int v40) {
            stuff = new int[] {
                    v1, v2, v3, v4, v5, v6, v7, v8, v9, v10,
                    v11, v12, v13, v14, v15, v16, v17, v18, v19, v20,
                    v21, v22, v23, v24, v25, v26, v27, v28, v29, v30,
                    v31, v32, v33, v34, v35, v36, v37, v38, v39, v40
            };
        }
    }
static class MyParamIntrospector extends JacksonAnnotationIntrospector {
        @Override
        public String findImplicitPropertyName(MapperConfig<?> config, AnnotatedMember member) {
            if (member instanceof AnnotatedParameter parameter) {
                return switch (parameter.getIndex()) {
                case 0 -> "a";
                case 1 -> "b";
                default -> "param" + parameter.getIndex();
                };
            }
            return super.findImplicitPropertyName(config, member);
        }
    }
static class Foo4777 {
        Foo4777() { }

        @JsonCreator
        static Foo4777 create() {
            return new Foo4777();
        }
    }
static class Instantiators4777 implements ValueInstantiators {
        @Override
        public ValueInstantiator modifyValueInstantiator(
                tools.jackson.databind.DeserializationConfig config,
                BeanDescription.Supplier beanDescRef,
                ValueInstantiator defaultInstantiator) {
            if (beanDescRef.getBeanClass() == Foo4777.class) {
                AnnotatedWithParams creator = defaultInstantiator.getDefaultCreator();
                if (!(creator instanceof AnnotatedMethod)
                        || !creator.getName().equals("create")) {
                    throw new IllegalArgumentException("Wrong DefaultCreator: should be static-method 'create()', is: "
                            + creator);
                }
            }
            return defaultInstantiator;
        }

        @Override
        public ValueInstantiator findValueInstantiator(
                tools.jackson.databind.DeserializationConfig config,
                BeanDescription.Supplier beanDescRef) {
            return null;
        }
    }

    void __invoke_testCreatorDetection4777() throws Exception {
        try {
            testCreatorDetection4777();
        } finally {
        }
    }

}
