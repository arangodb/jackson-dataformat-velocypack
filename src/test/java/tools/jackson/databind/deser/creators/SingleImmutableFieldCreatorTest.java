package tools.jackson.databind.deser.creators;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.cfg.MapperConfig;
import tools.jackson.databind.introspect.AnnotatedMember;
import tools.jackson.databind.introspect.JacksonAnnotationIntrospector;
import tools.jackson.databind.testutil.DatabindTestUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

// [databind#3898]
public class SingleImmutableFieldCreatorTest
    extends DatabindTestUtil
{
    static class ImmutableId {
        final int id;

        public ImmutableId(int id) { this.id = id; }

        public int getId() {
            return id;
        }
    }

    // Won't work if parameter names available, as in 3.0 (or param names module)
    // ... because of the default (no-args) constructor
    /*
    static class ImmutableIdWithEmptyConstuctor {
        final int id;

        public ImmutableIdWithEmptyConstuctor() { this(-1); }

        public ImmutableIdWithEmptyConstuctor(int id) { this.id = id; }

        public int getId() {
            return id;
        }
    }
    */

    static class ImmutableIdWithJsonCreatorAnnotation {
        final int id;

        @JsonCreator
        public ImmutableIdWithJsonCreatorAnnotation(int id) { this.id = id; }

        public int getId() {
            return id;
        }
    }

    static class ImmutableIdWithJsonPropertyFieldAnnotation {
        @JsonProperty("id")
        final int id;

        public ImmutableIdWithJsonPropertyFieldAnnotation(int id) { this.id = id; }

        public int getId() {
            return id;
        }
    }

    static class ImmutableIdWithJsonPropertyConstructorAnnotation {
        final int id;

        public ImmutableIdWithJsonPropertyConstructorAnnotation(@JsonProperty("id") int id) { this.id = id; }

        public int getId() {
            return id;
        }
    }

    static class MyParamIntrospector extends JacksonAnnotationIntrospector
    {
        private static final long serialVersionUID = 1L;

        @Override
        public String findImplicitPropertyName(MapperConfig<?> config, AnnotatedMember param) {
            return "id";
        }
    }

    /*
    /**********************************************************************
    /* Unit tests
    /**********************************************************************
     */

    private final ObjectMapper MAPPER = newVPackMapper();

    @Test
    public void testSetterlessProperty() throws Exception
    {
        ImmutableId input = new ImmutableId(13);
        ObjectMapper m = vpackMapperBuilder()
                .annotationIntrospector(new MyParamIntrospector())
                .build();
        String json = VPackUtils.toJson(m.writer().writeValueAsBytes(input));

        ImmutableId output = m.readValue(VPackUtils.toVPack(json), ImmutableId.class);
        assertNotNull(output);

        assertEquals(input.id, output.id);
    }

    // Won't work if parameter names available, as in 3.0 (or param names module)
    // ... because of the default (no-args) constructor
/*
    // in the past, this was a workaround for the first test
    @Test
    public void testSetterlessPropertyWithEmptyConstructor() throws Exception
    {
        ImmutableIdWithEmptyConstuctor input = new ImmutableIdWithEmptyConstuctor(13);
        String json = VPackUtils.toJson(MAPPER.writer().writeValueAsBytes(input));

        ImmutableIdWithEmptyConstuctor output = MAPPER.readValue(VPackUtils.toVPack(json), ImmutableIdWithEmptyConstuctor.class);
        assertNotNull(output);

        assertEquals(input.id, output.id);
    }
    */

    @Test
    public void testSetterlessPropertyWithJsonCreator() throws Exception
    {
        ImmutableIdWithJsonCreatorAnnotation input = new ImmutableIdWithJsonCreatorAnnotation(13);
        ObjectMapper m = vpackMapperBuilder()
                .annotationIntrospector(new MyParamIntrospector())
                .build();
        String json = VPackUtils.toJson(m.writer().writeValueAsBytes(input));

        ImmutableIdWithJsonCreatorAnnotation output =
                m.readValue(VPackUtils.toVPack(json), ImmutableIdWithJsonCreatorAnnotation.class);
        assertNotNull(output);

        assertEquals(input.id, output.id);
    }

    // in the past, this was a workaround for the first test
    @Test
    public void testSetterlessPropertyWithJsonPropertyField() throws Exception
    {
        ImmutableIdWithJsonPropertyConstructorAnnotation input = new ImmutableIdWithJsonPropertyConstructorAnnotation(13);
        String json = VPackUtils.toJson(MAPPER.writer().writeValueAsBytes(input));

        ImmutableIdWithJsonPropertyConstructorAnnotation output =
                MAPPER.readValue(VPackUtils.toVPack(json), ImmutableIdWithJsonPropertyConstructorAnnotation.class);
        assertNotNull(output);

        assertEquals(input.id, output.id);
    }

    @Test
    public void testSetterlessPropertyWithJsonPropertyConstructor() throws Exception
    {
        ImmutableIdWithJsonPropertyFieldAnnotation input = new ImmutableIdWithJsonPropertyFieldAnnotation(13);
        ObjectMapper m = vpackMapperBuilder()
                .annotationIntrospector(new MyParamIntrospector())
                .build();
        String json = VPackUtils.toJson(m.writer().writeValueAsBytes(input));

        ImmutableIdWithJsonPropertyFieldAnnotation output =
                m.readValue(VPackUtils.toVPack(json), ImmutableIdWithJsonPropertyFieldAnnotation.class);
        assertNotNull(output);

        assertEquals(input.id, output.id);
    }
}
