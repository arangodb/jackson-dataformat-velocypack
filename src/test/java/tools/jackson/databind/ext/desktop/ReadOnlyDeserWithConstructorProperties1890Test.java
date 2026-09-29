package tools.jackson.databind.ext.desktop;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.VPackUtils;

import java.beans.ConstructorProperties;

import static org.junit.jupiter.api.Assertions.*;
import static tools.jackson.databind.testutil.DatabindTestUtil.newVPackMapper;

public class ReadOnlyDeserWithConstructorProperties1890Test
{
    // [databind#1890]
    public static class PersonAnnotations {
        public String name;
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private TestEnum testEnum = TestEnum.DEFAULT;

        PersonAnnotations() { }

        @ConstructorProperties({"testEnum", "name"})
        public PersonAnnotations(TestEnum testEnum, String name) {
            this.testEnum = testEnum;
            this.name = name;
        }

        public TestEnum getTestEnum() {
            return testEnum;
        }

        public void setTestEnum(TestEnum testEnum) {
            this.testEnum = testEnum;
        }
    }

    public static class Person {
        public String name;
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private TestEnum testEnum = TestEnum.DEFAULT;

        Person() { }

        protected Person(TestEnum testEnum, String name) {
            this.testEnum = testEnum;
            this.name = name;
        }

        public TestEnum getTestEnum() {
            return testEnum;
        }

        public void setTestEnum(TestEnum testEnum) {
            this.testEnum = testEnum;
        }
   }

    public enum TestEnum{
       DEFAULT, TEST;
   }

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    private final ObjectMapper MAPPER = newVPackMapper();

    // [databind#1890]
    @Test
    void testDeserializeAnnotationsOneField() throws Exception {
        PersonAnnotations person = MAPPER.readerFor(PersonAnnotations.class)
                .readValue(VPackUtils.toVPack("{\"testEnum\":\"abc\"}"));
        // cannot remain as is, so becomes `null`
        assertEquals(null, person.getTestEnum());
        assertNull(person.name);
    }

    @Test
    void testDeserializeAnnotationsTwoFields() throws Exception {
        PersonAnnotations person = MAPPER.readerFor(PersonAnnotations.class)
                .readValue(VPackUtils.toVPack("{\"testEnum\":\"xyz\",\"name\":\"changyong\"}"));
        // cannot remain as is, so becomes `null`
        assertEquals(null, person.getTestEnum());
        assertEquals("changyong", person.name);
    }

    @Test
    void testDeserializeOneField() throws Exception {
        Person person = MAPPER.readValue(VPackUtils.toVPack("{\"testEnum\":\"\"}"), Person.class);
        assertEquals(TestEnum.DEFAULT, person.getTestEnum());
        assertNull(person.name);
    }

    @Test
    void testDeserializeTwoFields() throws Exception {
        Person person = MAPPER.readValue(VPackUtils.toVPack("{\"testEnum\":\"\",\"name\":\"changyong\"}"),
                Person.class);
        assertEquals(TestEnum.DEFAULT, person.getTestEnum());
        assertEquals("changyong", person.name);
    }
}
