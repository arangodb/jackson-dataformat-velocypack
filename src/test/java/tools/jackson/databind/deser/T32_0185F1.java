package tools.jackson.databind.deser;

import java.math.BigInteger;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.exc.InvalidDefinitionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0185F1 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] LONG_VALUE = VPackWireFixtureTest.hex(
            "2c 35 1c dc df 02");
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] EMPTY_ARRAY = VPackWireFixtureTest.hex("01");
private static final byte[] TWO_EMPTY_OBJECTS = VPackWireFixtureTest.hex(
            "13 05 0a 0a 02");
private static final byte[] NEXT_NULL = VPackWireFixtureTest.hex(
            "14 09 44 6e 65 78 74 18 01");
private static final byte[] TWO_CLASS_LINK = VPackWireFixtureTest.hex(
            "14 0e 44 6e 65 78 74 14 06 41 61 18 01 01");
private static final byte[] LINKED = VPackWireFixtureTest.hex(
            "14 26 44 6e 61 6d 65 45 66 69 72 73 74 "
          + "44 6e 65 78 74 14 13 44 6e 61 6d 65 44 6c 61 73 74 "
          + "44 6e 65 78 74 18 02 02");
private static final byte[] DOG_THINKING = VPackWireFixtureTest.hex(
            "14 21 44 6e 61 6d 65 45 53 6d 75 72 66 "
          + "45 62 72 61 69 6e 14 0d 48 62 72 61 69 6e 69 61 63 "
          + "1a 01 02");
private static final byte[] DOG_NULL_BRAIN = VPackWireFixtureTest.hex(
            "14 15 44 6e 61 6d 65 45 53 6d 75 72 66 "
          + "45 62 72 61 69 6e 18 02");

    void testCycleWith2Classes() {
        LinkA result = MAPPER.readValue(TWO_CLASS_LINK, LinkA.class);
        assertNotNull(result.next);
        assertNull(result.next.a);
    }

    void testEmptyArrayCase() {
        ArrayOrObject result = MAPPER.readValue(EMPTY_ARRAY, ArrayOrObject.class);
        assertNotNull(result.objects);
        assertTrue(result.objects.isEmpty());
        assertNull(result.object);
    }

    void testIgnoredCycle() {
        Selfie405 self = new Selfie405(1);
        self.parent = self;

        assertTrue(MAPPER.isEnabled(SerializationFeature.FAIL_ON_SELF_REFERENCES));
        assertThrows(InvalidDefinitionException.class, () -> MAPPER.writeValueAsBytes(self));

        ObjectWriter writer = MAPPER.writer()
                .without(SerializationFeature.FAIL_ON_SELF_REFERENCES);
        Map<String, Object> result = MAPPER.readValue(writer.writeValueAsBytes(self),
                new TypeReference<Map<String, Object>>() { });
        assertEquals(Map.of("id", 1, "parent", Map.of("id", 1)), result);
    }

    void testLinked() {
        CyclicBean first = MAPPER.readValue(LINKED, CyclicBean.class);
        assertNotNull(first);
        assertEquals("first", first._name);
        CyclicBean last = first._next;
        assertNotNull(last);
        assertEquals("last", last._name);
        assertNull(last._next);
    }

    void testLinkedGeneric() {
        StringLink result = MAPPER.readValue(NEXT_NULL, StringLink.class);
        assertNotNull(result);
        assertNull(result.next);
    }

    void testNotEmptyArrayCase() {
        ArrayOrObject result = MAPPER.readValue(TWO_EMPTY_OBJECTS, ArrayOrObject.class);
        assertNotNull(result.objects);
        assertEquals(2, result.objects.size());
        assertNull(result.object);
    }

    void testObjectCase() {
        ArrayOrObject result = MAPPER.readValue(EMPTY_OBJECT, ArrayOrObject.class);
        assertNull(result.objects);
        assertNotNull(result.object);
    }

    void testSimpleNonStaticInner() {
        Dog output = MAPPER.readValue(DOG_THINKING, Dog.class);
        assertEquals("Smurf", output.name);
        assertNotNull(output.brain);
        assertTrue(output.brain.isThinking);
        assertEquals("Smurf", output.brain.parentName());
        output.name = "Foo";
        assertEquals("Foo", output.brain.parentName());

        output = MAPPER.readValue(DOG_NULL_BRAIN, Dog.class);
        assertNull(output.brain);
    }
static class A2 {
        final int creatorType;
        A2(int value) { creatorType = 1; }
        A2(long value) { creatorType = 2; }
        A2(BigInteger value) { creatorType = 3; }
        A2(double value) { creatorType = 4; }
    }
static class B2 {
        final int creatorType;
        B2(BigInteger value) { creatorType = 3; }
        B2(double value) { creatorType = 4; }
    }
static final class D {
        D(double value) { throw new IllegalArgumentException("boo"); }
    }
static class Stuff {
        final double value;
        Stuff(double value) { this.value = value; }
    }
static class ArrayOrObject {
        final java.util.List<SomeObject> objects;
        final SomeObject object;

        @com.fasterxml.jackson.annotation.JsonCreator(mode =
                com.fasterxml.jackson.annotation.JsonCreator.Mode.DELEGATING)
        public ArrayOrObject(java.util.List<SomeObject> objects) {
            this.objects = objects;
            this.object = null;
        }

        @com.fasterxml.jackson.annotation.JsonCreator(mode =
                com.fasterxml.jackson.annotation.JsonCreator.Mode.DELEGATING)
        public ArrayOrObject(SomeObject object) {
            this.objects = null;
            this.object = object;
        }
    }
public static class SomeObject {
        public String someField;
    }
static class CyclicBean {
        CyclicBean _next;
        String _name;

        public void setNext(CyclicBean value) { _next = value; }
        public void setName(String value) { _name = value; }
    }
static class LinkA {
        public LinkB next;
    }
static class LinkB {
        protected LinkA a;

        public void setA(LinkA value) { a = value; }
        public LinkA getA() { return a; }
    }
static class GenericLink<T> {
        public GenericLink<T> next;
    }
static class StringLink extends GenericLink<String> { }
static class Selfie405 {
        public int id;
        @JsonIgnoreProperties({ "parent" })
        public Selfie405 parent;

        public Selfie405(int value) { id = value; }
    }
static class Dog {
        public String name;
        public Brain brain;

        public Dog() { }

        public class Brain {
            @com.fasterxml.jackson.annotation.JsonProperty("brainiac")
            public boolean isThinking;

            public String parentName() { return name; }
        }
    }

    void __invoke_testCycleWith2Classes() throws Exception {
        try {
            testCycleWith2Classes();
        } finally {
        }
    }


    void __invoke_testEmptyArrayCase() throws Exception {
        try {
            testEmptyArrayCase();
        } finally {
        }
    }


    void __invoke_testIgnoredCycle() throws Exception {
        try {
            testIgnoredCycle();
        } finally {
        }
    }


    void __invoke_testLinked() throws Exception {
        try {
            testLinked();
        } finally {
        }
    }


    void __invoke_testLinkedGeneric() throws Exception {
        try {
            testLinkedGeneric();
        } finally {
        }
    }


    void __invoke_testNotEmptyArrayCase() throws Exception {
        try {
            testNotEmptyArrayCase();
        } finally {
        }
    }


    void __invoke_testObjectCase() throws Exception {
        try {
            testObjectCase();
        } finally {
        }
    }


    void __invoke_testSimpleNonStaticInner() throws Exception {
        try {
            testSimpleNonStaticInner();
        } finally {
        }
    }

}
