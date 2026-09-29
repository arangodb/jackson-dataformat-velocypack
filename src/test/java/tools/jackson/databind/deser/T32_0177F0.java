package tools.jackson.databind.deser;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.DeserializationContextExt;
import tools.jackson.databind.module.SimpleModule;

import static org.junit.jupiter.api.Assertions.fail;

import tools.jackson.dataformat.velocypack.*;

class T32_0177F0 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] BAR_FIELD = VPackWireFixtureTest.hex(
            "0b 13 01 43 62 61 72 4a 66 69 65 6c 64 56 61 6c 75 65 03");
private static final byte[] STRING_TEST_VALUE = VPackWireFixtureTest.hex(
            "4a 74 65 73 74 2d 76 61 6c 75 65");
private static final byte[] STRING_INVALID = VPackWireFixtureTest.hex(
            "47 69 6e 76 61 6c 69 64");
private static final byte[] STRING_BAD = VPackWireFixtureTest.hex(
            "43 62 61 64");
private static final byte[] STRING_TEST = VPackWireFixtureTest.hex(
            "44 74 65 73 74");
private static final byte[] SIMPLE_OBJECT = VPackWireFixtureTest.hex(
            "0b 07 01 41 78 31 03");

    void testJDKScalarDeserializerExistence() {
        verifyIsFound(String.class);
        verifyIsFound(Float.class);
        verifyIsFound(BigDecimal.class);
        verifyIsFound(URL.class);
        verifyIsFound(UUID.class);
        verifyIsFound(Calendar.class);
        verifyIsFound(GregorianCalendar.class);
        verifyIsFound(Date.class);
    }

    void testJDKContainerDeserializerExistence() {
        verifyIsFound(Collection.class);
        verifyIsFound(List.class);
        verifyIsFound(Map.class);
        verifyIsFound(Set.class);
        verifyIsFound(ArrayList.class);
        verifyIsFound(HashMap.class);
        verifyIsFound(LinkedHashMap.class);
        verifyIsFound(HashSet.class);
    }

    void testJDKArraysOfExistence() {
        verifyIsFound(String[].class);
        verifyIsFound(BigDecimal[].class);
        verifyIsFound(URL[].class);
        verifyIsFound(UUID[].class);
    }

    void testNoDeserTypes() {
        verifyNotFound(POJO2539.class);
        verifyNotFound(Process.class);
        verifyNotFound(System.class);
        verifyNotFound(Thread.class);
    }
private static SimpleModule barModule(ValueDeserializer<Bar> deserializer) {
        SimpleModule module = new SimpleModule("test");
        module.addDeserializer(Bar.class, deserializer);
        return module;
    }
private static void verifyIsFound(Class<?> rawType) {
        if (!verifyDeserializerExistence(rawType)) {
            fail("Should have explicit deserializer for " + rawType.getName());
        }
    }
private static void verifyNotFound(Class<?> rawType) {
        if (verifyDeserializerExistence(rawType)) {
            fail("Should NOT have explicit deserializer for " + rawType.getName());
        }
    }
private static boolean verifyDeserializerExistence(Class<?> rawType) {
        DeserializationContextExt context = MAPPER._deserializationContext();
        return context.hasExplicitDeserializerFor(rawType);
    }
static class POJO2539 { }
static class MyValue {
        public int x;
    }
static class Bar {
        private final String value;

        private Bar(String value) {
            this.value = value;
        }

        static Bar of(String value) {
            return new Bar(value);
        }

        String getValue() {
            return value;
        }
    }
static class BarWrapper {
        public Bar bar;
    }

    void __invoke_testJDKScalarDeserializerExistence() throws Exception {
        try {
            testJDKScalarDeserializerExistence();
        } finally {
        }
    }


    void __invoke_testJDKContainerDeserializerExistence() throws Exception {
        try {
            testJDKContainerDeserializerExistence();
        } finally {
        }
    }


    void __invoke_testJDKArraysOfExistence() throws Exception {
        try {
            testJDKArraysOfExistence();
        } finally {
        }
    }


    void __invoke_testNoDeserTypes() throws Exception {
        try {
            testNoDeserTypes();
        } finally {
        }
    }

}
