package tools.jackson.databind.deser;

import java.math.BigInteger;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.ValueInstantiationException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0185F0 {
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

    void testJsonLongDeserializationPrefersLong() {
        A2 result = MAPPER.readValue(LONG_VALUE, A2.class);
        assertEquals(2, result.creatorType);
    }

    void testJsonLongDeserializationPrefersBigInteger() {
        B2 result = MAPPER.readValue(LONG_VALUE, B2.class);
        assertEquals(3, result.creatorType);
    }

    void testJsonLongIntoDoubleConstructorThrows() {
        ValueInstantiationException exception = assertThrows(
                ValueInstantiationException.class,
                () -> MAPPER.readValue(LONG_VALUE, D.class));
        assertInstanceOf(IllegalArgumentException.class, exception.getCause());
        assertEquals("boo", exception.getCause().getMessage());
    }

    void testJsonLongToDouble() {
        assertTrue(12_345_678_901L > Integer.MAX_VALUE);
        Stuff result = MAPPER.readValue(LONG_VALUE, Stuff.class);
        assertEquals(12_345_678_901L, result.value);
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

    void __invoke_testJsonLongDeserializationPrefersLong() throws Exception {
        try {
            testJsonLongDeserializationPrefersLong();
        } finally {
        }
    }


    void __invoke_testJsonLongDeserializationPrefersBigInteger() throws Exception {
        try {
            testJsonLongDeserializationPrefersBigInteger();
        } finally {
        }
    }


    void __invoke_testJsonLongIntoDoubleConstructorThrows() throws Exception {
        try {
            testJsonLongIntoDoubleConstructorThrows();
        } finally {
        }
    }


    void __invoke_testJsonLongToDouble() throws Exception {
        try {
            testJsonLongToDouble();
        } finally {
        }
    }

}
