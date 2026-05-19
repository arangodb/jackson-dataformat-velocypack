package tools.jackson.databind.jsontype.ext;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.annotation.JsonTypeIdResolver;
import tools.jackson.databind.jsontype.impl.TypeIdResolverBase;
import tools.jackson.databind.testutil.DatabindTestUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

// [databind#2588] / [databind#2610] / [databind#4354]
public class ExternalTypeId2588Test extends DatabindTestUtil
{
    // [databind#2588]
    interface Animal { }

    static class Cat implements Animal {
        public int lives = 9;
    }

    public static class Dog implements Animal { }

    static class Wolf implements Animal {
        public boolean alive;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Pet {
        final String type;

        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
                include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property = "type")
        @JsonTypeIdResolver(AnimalTypeIdResolver.class)
        private final Animal animal;

        @JsonCreator
        public Pet(@JsonProperty("type") String type,
                   @JsonProperty("animal") Animal animal) {
            this.type = type;
            this.animal = animal;
        }
    }

    static class AnimalTypeIdResolver extends TypeIdResolverBase {
        private static final long serialVersionUID = 1L;

        @Override
        public String idFromValue(DatabindContext context, Object value) {
            return idFromValueAndType(context, value, value.getClass());
        }

        @Override
        public String idFromValueAndType(DatabindContext context,
                Object value, Class<?> suggestedType) {
            if (suggestedType.isAssignableFrom(Cat.class)) {
                return "cat";
            } else if (suggestedType.isAssignableFrom(Dog.class)) {
                return "dog";
            } else if (suggestedType.isAssignableFrom(Wolf.class)) {
                return "wolf";
            }
            return null;
        }

        @Override
        public JavaType typeFromId(DatabindContext context, String id) {
            if ("cat".equals(id)) {
                return context.constructType(Cat.class);
            } else if ("dog".equals(id)) {
                return context.constructType(Dog.class);
            }
            return null;
        }

        @Override
        public JsonTypeInfo.Id getMechanism() {
            return JsonTypeInfo.Id.NAME;
        }
    }

    private final ObjectMapper MAPPER = newVPackMapper();

    // [databind#2588]
    @Test
    public void testExternalTypeId2588Read() throws Exception
    {
        Pet pet;

        // works?

        pet = MAPPER.readValue(VPackUtils.toVPack(a2q(
"{\n" +
"  'type': 'cat',\n" +
"  'animal': { },\n" +
"  'ignoredObject\": {\n" +
"    'someField': 'someValue'\n" +
"  }"+
"}"
                )), Pet.class);
        assertNotNull(pet);

        // fails:
        pet = MAPPER.readValue(VPackUtils.toVPack(a2q(
"{\n" +
"  'animal\": { },\n" +
"  'ignoredObject': {\n" +
"    'someField': 'someValue'\n" +
"  },\n" +
"  'type': 'cat'\n" +
"}"
                )), Pet.class);
        assertNotNull(pet);
    }

    @Test
    public void testExternalTypeId2588Write() throws Exception
    {
        String json = VPackUtils.toJson(MAPPER.writeValueAsBytes(new Pet("cat", new Wolf())));
        assertEquals(a2q("{'animal':{'alive':false},'type':'wolf'}"), json);
    }
}
