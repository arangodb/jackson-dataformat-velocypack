package tools.jackson.databind.deser.creators;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.cfg.ConstructorDetector;
import tools.jackson.databind.cfg.MapperConfig;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.introspect.AnnotatedClass;
import tools.jackson.databind.introspect.PotentialCreator;
import tools.jackson.databind.testutil.DatabindTestUtil;
import com.arangodb.jackson.dataformat.velocypack.VPackMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

// For [databind#5318]: allow auto-detection of Properties-based Constructor
// even with class having Default (no-parameters) Constructor
public class NoParamsCreator5318Test extends DatabindTestUtil
{
    static class Pojo5318Working {
         final int productId;
         final String name;

         public Pojo5318Working() {
              this(0, null);
         }

         public Pojo5318Working(int productId, String name) {
              this.productId = productId;
              this.name = name;
         }
    }

    // No auto-detection, due to explicit annotation for 0-params ctor
    static class Pojo5318Annotated {
        @JsonCreator
        public Pojo5318Annotated() { }

        public Pojo5318Annotated(int productId, String name) {
            throw new IllegalStateException("Should not be called");  
        }
    }

    // [databind#5045]
    static class User5045 {
        public int age;

        public User5045(@ImplicitName("age") int age) {
            throw new IllegalStateException("Should not be called");
        }

        @JsonCreator
        public User5045() {
            this.age = -1;
        }

        public int getAge() { return age; }
    }

    // [databind#5045]
    @SuppressWarnings("serial")
    static class AI5045 extends ImplicitNameIntrospector {
        @Override
        public PotentialCreator findPreferredCreator(MapperConfig<?> config,
                AnnotatedClass valueClass,
                List<PotentialCreator> declaredConstructors,
                List<PotentialCreator> declaredFactories,
                Optional<PotentialCreator> zeroParamsConstructor)
        {
            for (PotentialCreator pc : declaredConstructors) {
                if (pc.paramCount() != 0) {
                    return pc;
                }
            }
            return null;
        }
    }

    // No auto-detection, due to explicit ignoral of 0-params ctor
    static class Pojo5318Ignore {
        protected Pojo5318Ignore() { }

        @JsonIgnore
        public Pojo5318Ignore(int productId, String name) {
            throw new IllegalStateException("Should not be called");  
        }
    }

    private final ObjectMapper MAPPER = vpackMapperBuilder()
            .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();
    
    // For [databind#5318]: intended usage
    @Test
    void creatorDetectionWithNoParamsCtor() throws Exception {
        Pojo5318Working pojo = MAPPER.readValue(VPackUtils.toVPack("{\"productId\":1,\"name\":\"foo\"}"), Pojo5318Working.class);
        assertEquals(1, pojo.productId);
        assertEquals("foo", pojo.name);
    }

    // For [databind#5318]: avoid detection with explicit annotation on 0-params ctor
    @Test
    void noCreatorDetectionDueToCreatorAnnotation() throws Exception {
        assertNotNull(MAPPER.readValue(VPackUtils.toVPack("{}"), Pojo5318Annotated.class));
    }

    // For [databind#5318]: avoid detection with explicit ignoral of parameterized ctor
    @Test
    void noCreatorDetectionDueToIgnore() throws Exception {
        assertNotNull(MAPPER.readValue(VPackUtils.toVPack("{}"), Pojo5318Ignore.class));
    }

    // For [databind#5318]: avoid detection when configured to do so (not allow
    // implicit with default ctor)
    @Test
    void noCreatorDetectionDueToFeatureDisabled() throws Exception {
        final ObjectMapper mapper = vpackMapperBuilder()
                .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .constructorDetector(ConstructorDetector.DEFAULT
                        .withAllowImplicitWithDefaultConstructor(false))
                .build();
        try {
            mapper.readValue(VPackUtils.toVPack("{\"productId\": 1}"), Pojo5318Working.class);
            fail("Should not pass");
        } catch (MismatchedInputException e) {
            verifyException(e, "Unrecognized property \"productId\"");
        }
    }

    // [databind#5045]
    @Test
    public void defaultCreator5045() throws Exception {
        ObjectMapper mapper = VPackMapper.builder().annotationIntrospector(new AI5045()).build();
        String json = "{ }";

        User5045 user = mapper.readValue(VPackUtils.toVPack(json), User5045.class);
        assertNotNull(user);
        assertEquals(-1, user.getAge());
    }
}
