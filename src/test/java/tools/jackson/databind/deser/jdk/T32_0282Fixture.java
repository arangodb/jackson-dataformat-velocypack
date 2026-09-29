package tools.jackson.databind.deser.jdk;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.module.SimpleModule;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0282Fixture {
private static final ObjectMapper MAPPER = VPackMapper.builder()
            .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();
private static final byte[] MINIMAL_STACK_TRACE = VPackWireFixtureTest.hex(
            "14 4c 49 63 6c 61 73 73 4e 61 6d 65 47 4d 79 43 6c 61 73 73 "
          + "4a 6d 65 74 68 6f 64 4e 61 6d 65 48 6d 79 4d 65 74 68 6f 64 "
          + "48 66 69 6c 65 4e 61 6d 65 4c 4d 79 43 6c 61 73 73 2e 6a 61 76 61 "
          + "4a 6c 69 6e 65 4e 75 6d 62 65 72 28 0a 04");
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] MODULE_INFO = VPackWireFixtureTest.hex(
            "14 62 49 63 6c 61 73 73 4e 61 6d 65 41 43 4a 6d 65 74 68 6f 64 4e 61 6d 65 41 6d "
          + "48 66 69 6c 65 4e 61 6d 65 41 46 4a 6c 69 6e 65 4e 75 6d 62 65 72 31 "
          + "4a 6d 6f 64 75 6c 65 4e 61 6d 65 41 4d 4d 6d 6f 64 75 6c 65 56 65 72 73 69 6f 6e 41 56 "
          + "4f 63 6c 61 73 73 4c 6f 61 64 65 72 4e 61 6d 65 41 4c 07");
private static final byte[] NULL_MODULE_INFO = VPackWireFixtureTest.hex(
            "14 6d 49 63 6c 61 73 73 4e 61 6d 65 44 54 65 73 74 4a 6d 65 74 68 6f 64 4e 61 6d 65 44 74 65 73 74 "
          + "48 66 69 6c 65 4e 61 6d 65 49 54 65 73 74 2e 6a 61 76 61 4a 6c 69 6e 65 4e 75 6d 62 65 72 31 "
          + "4a 6d 6f 64 75 6c 65 4e 61 6d 65 18 4d 6d 6f 64 75 6c 65 56 65 72 73 69 6f 6e 18 "
          + "4f 63 6c 61 73 73 4c 6f 61 64 65 72 4e 61 6d 65 18 07");
private static final byte[] STACK_TRACE_ARRAY = VPackWireFixtureTest.hex(
            "13 73 14 38 49 63 6c 61 73 73 4e 61 6d 65 41 41 4a 6d 65 74 68 6f 64 4e 61 6d 65 41 61 "
          + "48 66 69 6c 65 4e 61 6d 65 46 41 2e 6a 61 76 61 4a 6c 69 6e 65 4e 75 6d 62 65 72 31 04 "
          + "14 38 49 63 6c 61 73 73 4e 61 6d 65 41 42 4a 6d 65 74 68 6f 64 4e 61 6d 65 41 62 "
          + "48 66 69 6c 65 4e 61 6d 65 46 42 2e 6a 61 76 61 4a 6c 69 6e 65 4e 75 6d 62 65 72 32 04 02");
private static final byte[] SINGLE_STACK_TRACE = VPackWireFixtureTest.hex(
            "13 3e 14 3b 49 63 6c 61 73 73 4e 61 6d 65 41 57 4a 6d 65 74 68 6f 64 4e 61 6d 65 44 77 72 61 70 "
          + "48 66 69 6c 65 4e 61 6d 65 46 57 2e 6a 61 76 61 4a 6c 69 6e 65 4e 75 6d 62 65 72 31 04 01");
private static final byte[] HOLDER = VPackWireFixtureTest.hex(
            "14 47 45 74 72 61 63 65 13 3e 14 3b 49 63 6c 61 73 73 4e 61 6d 65 41 58 "
          + "4a 6d 65 74 68 6f 64 4e 61 6d 65 43 64 6f 58 48 66 69 6c 65 4e 61 6d 65 46 58 2e 6a 61 76 61 "
          + "4a 6c 69 6e 65 4e 75 6d 62 65 72 28 63 04 01 01");
private static final byte[] UNKNOWN_PROPERTY = VPackWireFixtureTest.hex(
            "14 49 49 63 6c 61 73 73 4e 61 6d 65 44 54 65 73 74 4a 6d 65 74 68 6f 64 4e 61 6d 65 41 6d "
          + "48 66 69 6c 65 4e 61 6d 65 46 54 2e 6a 61 76 61 4a 6c 69 6e 65 4e 75 6d 62 65 72 35 "
          + "4c 6e 61 74 69 76 65 4d 65 74 68 6f 64 1a 05");
private static final byte[] CUSTOM_LOCATION = VPackWireFixtureTest.hex(
            "14 0e 48 4c 6f 63 61 74 69 6f 6e 28 7b 01");
private static final byte[] CUSTOM_EXCEPTION = VPackWireFixtureTest.hex(
            "14 16 4a 73 74 61 63 6b 54 72 61 63 65 13 08 28 7b 29 c8 01 02 01");

    // Provenance: StackTraceElementDeserTest#testWithMinimalFields().
    void testWithMinimalFieldsVpack() throws Exception {
        StackTraceElement result = MAPPER.readValue(MINIMAL_STACK_TRACE, StackTraceElement.class);
        assertNotNull(result);
        assertEquals("MyClass", result.getClassName());
        assertEquals("myMethod", result.getMethodName());
        assertEquals("MyClass.java", result.getFileName());
        assertEquals(10, result.getLineNumber());
    }

    // Provenance: StackTraceElementDeserTest#testWithEmptyObject().
    void testWithEmptyObjectVpack() throws Exception {
        StackTraceElement result = MAPPER.readValue(EMPTY_OBJECT, StackTraceElement.class);
        assertNotNull(result);
        assertEquals("", result.getClassName());
        assertEquals("", result.getMethodName());
        assertEquals("", result.getFileName());
        assertEquals(-1, result.getLineNumber());
    }

    // Provenance: StackTraceElementDeserTest#testWithModuleInfo().
    void testWithModuleInfoVpack() throws Exception {
        StackTraceElement result = MAPPER.readValue(MODULE_INFO, StackTraceElement.class);
        assertNotNull(result);
        assertEquals("C", result.getClassName());
        assertEquals("m", result.getMethodName());
        assertEquals("F", result.getFileName());
        assertEquals(1, result.getLineNumber());
        assertEquals("M", result.getModuleName());
        assertEquals("V", result.getModuleVersion());
        assertEquals("L", result.getClassLoaderName());
    }

    // Provenance: StackTraceElementDeserTest#testWithNullModuleInfo().
    void testWithNullModuleInfoVpack() throws Exception {
        StackTraceElement result = MAPPER.readValue(NULL_MODULE_INFO, StackTraceElement.class);
        assertNotNull(result);
        assertNull(result.getModuleName());
        assertNull(result.getModuleVersion());
        assertNull(result.getClassLoaderName());
    }

    // Provenance: StackTraceElementDeserTest#testRoundTripFromActualException().
    void testRoundTripFromActualExceptionVpack() throws Exception {
        Exception exception;
        try {
            throw new RuntimeException("test");
        } catch (RuntimeException e) {
            exception = e;
        }
        StackTraceElement first = exception.getStackTrace()[0];
        StackTraceElement result = MAPPER.readValue(MAPPER.writeValueAsBytes(first),
                StackTraceElement.class);
        assertEquals(first.getClassName(), result.getClassName());
        assertEquals(first.getMethodName(), result.getMethodName());
        assertEquals(first.getFileName(), result.getFileName());
        assertEquals(first.getLineNumber(), result.getLineNumber());
    }

    // Provenance: StackTraceElementDeserTest#testStackTraceArray().
    void testStackTraceArrayVpack() throws Exception {
        StackTraceElement[] result = MAPPER.readValue(STACK_TRACE_ARRAY,
                StackTraceElement[].class);
        assertNotNull(result);
        assertEquals(2, result.length);
        assertEquals("A", result[0].getClassName());
        assertEquals("B", result[1].getClassName());
    }

    // Provenance: StackTraceElementDeserTest#testStackTraceInHolder().
    void testStackTraceInHolderVpack() throws Exception {
        StackTraceHolder result = MAPPER.readValue(HOLDER, StackTraceHolder.class);
        assertNotNull(result);
        assertNotNull(result.trace);
        assertEquals(1, result.trace.length);
        assertEquals("X", result.trace[0].getClassName());
        assertEquals(99, result.trace[0].getLineNumber());
    }

    // Provenance: StackTraceElementDeserTest#testUnknownPropertiesIgnored().
    void testUnknownPropertiesIgnoredVpack() throws Exception {
        StackTraceElement result = MAPPER.readValue(UNKNOWN_PROPERTY, StackTraceElement.class);
        assertNotNull(result);
        assertEquals("Test", result.getClassName());
    }

    // Provenance: StackTraceElementDeserTest#testSingleValueArrayUnwrap().
    void testSingleValueArrayUnwrapVpack() throws Exception {
        ObjectReader reader = MAPPER.readerFor(StackTraceElement.class)
                .with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS);
        StackTraceElement result = reader.readValue(SINGLE_STACK_TRACE);
        assertNotNull(result);
        assertEquals("W", result.getClassName());
    }

    // Provenance: StackTraceElementDeserTest#testRoundTripWithMixIn().
    void testRoundTripWithMixInVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addMixIn(StackTraceElement.class, StackTraceElementMixIn.class)
                .build();
        StackTraceElement orig = new StackTraceElement(
                "appLoader", "java.base", "17.0.1",
                "java.lang.String", "valueOf", "String.java", 3456);

        byte[] encoded = mapper.writeValueAsBytes(orig);
        Map<?, ?> fields = mapper.readValue(encoded, Map.class);
        assertTrue(fields.containsKey("class"));
        assertTrue(fields.containsKey("method"));
        assertTrue(fields.containsKey("file"));
        assertTrue(fields.containsKey("line"));
        StackTraceElement result = mapper.readValue(encoded, StackTraceElement.class);
        assertEquals(orig.getClassName(), result.getClassName());
        assertEquals(orig.getMethodName(), result.getMethodName());
        assertEquals(orig.getFileName(), result.getFileName());
        assertEquals(orig.getLineNumber(), result.getLineNumber());
    }

    // Provenance: StackTraceElementDeserTest#testStandardDeserUnaffectedByMixInFeature().
    void testStandardDeserUnaffectedByMixInFeatureVpack() throws Exception {
        StackTraceElement result = MAPPER.readValue(MINIMAL_STACK_TRACE, StackTraceElement.class);
        assertNotNull(result);
        assertEquals("MyClass", result.getClassName());
        assertEquals("myMethod", result.getMethodName());
        assertEquals("MyClass.java", result.getFileName());
        assertEquals(10, result.getLineNumber());
    }

    // Provenance: StackTraceElementDeserTest#testStackTraceElementWithCustom().
    void testStackTraceElementWithCustomVpack() throws Exception {
        StackTraceBean bean = MAPPER.readValue(CUSTOM_LOCATION, StackTraceBean.class);
        assertNotNull(bean.location);
        assertEquals(StackTraceBean.NUM, bean.location.getLineNumber());

        ObjectMapper mapper = VPackMapper.builder()
                .addModule(new SimpleModule()
                        .addDeserializer(StackTraceElement.class, new MyStackTraceElementDeserializer()))
                .build();

        StackTraceElement elem = mapper.readValue(new byte[] { 0x28, 0x7b }, StackTraceElement.class);
        assertNotNull(elem);
        assertEquals(StackTraceBean.NUM, elem.getLineNumber());

        IOException exception = mapper.readValue(CUSTOM_EXCEPTION, IOException.class);
        assertNotNull(exception);
        StackTraceElement[] traces = exception.getStackTrace();
        assertNotNull(traces);
        assertEquals(2, traces.length);
        assertEquals(StackTraceBean.NUM, traces[0].getLineNumber());
        assertEquals(StackTraceBean.NUM, traces[1].getLineNumber());
    }
abstract static class StackTraceElementMixIn {
        @JsonProperty("class")
        public abstract String getClassName();

        @JsonProperty("method")
        public abstract String getMethodName();

        @JsonProperty("file")
        public abstract String getFileName();

        @JsonProperty("line")
        public abstract int getLineNumber();
    }
static class StackTraceHolder {
        public StackTraceElement[] trace;
    }
static class StackTraceBean {
        static final int NUM = 13;

        @JsonProperty("Location")
        @JsonDeserialize(using = MyStackTraceElementDeserializer.class)
        protected StackTraceElement location;
    }
static class MyStackTraceElementDeserializer extends StdDeserializer<StackTraceElement> {
        MyStackTraceElementDeserializer() {
            super(StackTraceElement.class);
        }

        @Override
        public StackTraceElement deserialize(JsonParser parser, DeserializationContext context) {
            parser.skipChildren();
            return new StackTraceElement("a", "b", "b", StackTraceBean.NUM);
        }
    }

    void __invoke_testWithMinimalFieldsVpack() throws Exception {
        try {
            testWithMinimalFieldsVpack();
        } finally {
        }
    }


    void __invoke_testWithEmptyObjectVpack() throws Exception {
        try {
            testWithEmptyObjectVpack();
        } finally {
        }
    }


    void __invoke_testWithModuleInfoVpack() throws Exception {
        try {
            testWithModuleInfoVpack();
        } finally {
        }
    }


    void __invoke_testWithNullModuleInfoVpack() throws Exception {
        try {
            testWithNullModuleInfoVpack();
        } finally {
        }
    }


    void __invoke_testRoundTripFromActualExceptionVpack() throws Exception {
        try {
            testRoundTripFromActualExceptionVpack();
        } finally {
        }
    }


    void __invoke_testStackTraceArrayVpack() throws Exception {
        try {
            testStackTraceArrayVpack();
        } finally {
        }
    }


    void __invoke_testStackTraceInHolderVpack() throws Exception {
        try {
            testStackTraceInHolderVpack();
        } finally {
        }
    }


    void __invoke_testUnknownPropertiesIgnoredVpack() throws Exception {
        try {
            testUnknownPropertiesIgnoredVpack();
        } finally {
        }
    }


    void __invoke_testSingleValueArrayUnwrapVpack() throws Exception {
        try {
            testSingleValueArrayUnwrapVpack();
        } finally {
        }
    }


    void __invoke_testRoundTripWithMixInVpack() throws Exception {
        try {
            testRoundTripWithMixInVpack();
        } finally {
        }
    }


    void __invoke_testStandardDeserUnaffectedByMixInFeatureVpack() throws Exception {
        try {
            testStandardDeserUnaffectedByMixInFeatureVpack();
        } finally {
        }
    }


    void __invoke_testStackTraceElementWithCustomVpack() throws Exception {
        try {
            testStackTraceElementWithCustomVpack();
        } finally {
        }
    }

}
