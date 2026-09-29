package tools.jackson.core.unittest.filter;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.core.filter.FilteringGeneratorDelegate;
import tools.jackson.core.filter.TokenFilter;
import tools.jackson.core.filter.TokenFilter.Inclusion;
import tools.jackson.core.io.SerializedString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0016Fixture {

    void nonFiltering() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = generator(output)) {
            writeStandardDocument(generator);
        }
        assertEquals(Map.of("a", 123, "array", List.of(1, 2),
                "ob", Map.of("value0", 2, "value", 3, "value2", 4), "b", true),
                read(output));
    }

    void singleMatchFilteringWithoutPath() throws Exception {
        FilteredResult result = filtered(new NameMatchFilter("value"),
                T32_0016Fixture::writeStandardDocument, Inclusion.ONLY_INCLUDE_ALL);
        assertEquals(3, result.value());
    }

    void singleMatchFilteringWithPath() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        NameMatchFilter filter = new NameMatchFilter("value");
        FilteringGeneratorDelegate delegate = new FilteringGeneratorDelegate(
                generator(output), filter, Inclusion.INCLUDE_ALL_AND_PATH, false);
        assertSame(output, delegate.streamWriteOutputTarget());
        assertNotNull(delegate.getFilterContext());
        assertSame(filter, delegate.getFilter());
        writeStandardDocument(delegate);
        delegate.close();
        assertEquals(Map.of("ob", Map.of("value", 3)), read(output));
        assertEquals(1, delegate.getMatchCount());
    }

    void singleMatchFilteringWithPathSkippedArray() throws Exception {
        FilteredResult result = filtered(new NameMatchFilter("value"),
                T32_0016Fixture::writeSkippedArrayDocument);
        assertEquals(Map.of("ob", List.of(Map.of("value", "bar"))), result.value());
        assertEquals(1, result.matchCount());
    }

    void singleMatchFilteringWithPathAlternate1() throws Exception {
        FilteredResult result = filtered(new NameMatchFilter("value"),
                T32_0016Fixture::writeAlternateDocument);
        assertEquals(Map.of("ob", Map.of("value", List.of("x"))), result.value());
        assertEquals(1, result.matchCount());

        result = filtered(new NameExcludeFilter(true, "value", "a"),
                T32_0016Fixture::writeAlternateDocument);
        assertEquals(Map.of("array", List.of(1, 2),
                "ob", Map.of("value0", 2, "value2", "foo"), "b", true), result.value());
        assertEquals(5, result.matchCount());
    }

    void singleMatchFilteringWithPathRawBinary() {
        JsonGenerator generator = generator(new ByteArrayOutputStream());
        try {
            generator.writeStartArray();
            generator.writeBinary(new byte[] { 1 });
            assertThrows(UnsupportedOperationException.class,
                    () -> generator.writeRawValue(new SerializedString("1")));
        } finally {
            try {
                generator.close();
            } catch (RuntimeException ignored) {
                // A failed VPack generator retains the unsupported raw-output failure.
            }
        }
    }

    void noMatchFiltering6() throws Exception {
        FilteredResult result = filtered(new StrictNameMatchFilter("invalid"), generator -> {
            generator.writeStartArray();
            for (int i = 0; i < 3; ++i) {
                generator.writeStartArray();
                writeNoMatchObject(generator);
                generator.writeEndArray();
            }
            generator.writeEndArray();
        }, Inclusion.INCLUDE_NON_NULL);
        assertEquals(List.of(List.of(Map.of()), List.of(Map.of()), List.of(Map.of())), result.value());
        assertEquals(0, result.matchCount());
    }

    void valueOmitsFieldName1() throws Exception {
        FilteredResult result = filtered(new NoArraysFilter(), generator -> {
            generator.writeStartObject();
            generator.writeArrayPropertyStart("root");
            generator.writeString("a");
            generator.writeEndArray();
            generator.writeBooleanProperty("b0", false);
            generator.writeEndObject();
        }, Inclusion.INCLUDE_NON_NULL);
        assertEquals(Map.of("b0", false), result.value());
        assertEquals(1, result.matchCount());
    }

    void valueOmitsFieldName2() throws Exception {
        FilteredResult result = filtered(new NoObjectsFilter(), generator -> {
            generator.writeStartArray();
            generator.writeString("a");
            generator.writeStartObject();
            generator.writeObjectPropertyStart("root");
            generator.writeObjectPropertyStart("b");
            generator.writeNumberProperty("value", 4);
            generator.writeEndObject();
            generator.writeEndObject();
            generator.writeBooleanProperty("b0", false);
            generator.writeEndObject();
            generator.writeEndArray();
        }, Inclusion.INCLUDE_NON_NULL);
        assertEquals(List.of("a"), result.value());
        assertEquals(1, result.matchCount());
    }

    void writeStartObjectWithObject() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        FilteringGeneratorDelegate delegate = new FilteringGeneratorDelegate(
                generator(output), TokenFilter.INCLUDE_ALL, Inclusion.INCLUDE_ALL_AND_PATH, true);
        delegate.writeStartObject(new Object(), 2);
        delegate.writeName("field1");
        delegate.writeStartObject("val");
        delegate.writeEndObject();
        delegate.writeName("field2");
        delegate.writeNumber(new BigDecimal("1.0"));
        delegate.writeEndObject();
        delegate.close();
        assertEquals(Map.of("field1", Map.of(), "field2", new BigDecimal("1.0")),
                read(output));
    }

    void rawValueDelegationWithArray() {
        JsonGenerator generator = generator(new ByteArrayOutputStream());
        try {
            generator.writeStartArray();
            assertThrows(UnsupportedOperationException.class,
                    () -> generator.writeRawValue(new char[] { '1' }, 0, 1));
        } finally {
            try {
                generator.close();
            } catch (RuntimeException ignored) {
                // A failed VPack generator retains the unsupported raw-output failure.
            }
        }
    }

    void rawValueDelegationWithObject() {
        JsonGenerator generator = generator(new ByteArrayOutputStream());
        try {
            generator.writeStartObject();
            generator.writeNumberProperty("f1", 1);
            generator.writeName("f2");
            assertThrows(UnsupportedOperationException.class,
                    () -> generator.writeRawValue(new char[] { '1', '2', '.', '3', '-' }, 0, 4));
        } finally {
            try {
                generator.close();
            } catch (RuntimeException ignored) {
                // A failed VPack generator retains the unsupported raw-output failure.
            }
        }
    }
private static JsonGenerator generator(ByteArrayOutputStream output) {
        return new VPackFactory().createGenerator(ObjectWriteContext.empty(), output);
    }
private static Object read(ByteArrayOutputStream output) throws Exception {
        return new VPackMapper().readValue(output.toByteArray(), Object.class);
    }
private static FilteredResult filtered(TokenFilter filter, WriterCall call) throws Exception {
        return filtered(filter, call, Inclusion.INCLUDE_ALL_AND_PATH);
    }
private static FilteredResult filtered(TokenFilter filter, WriterCall call, Inclusion inclusion)
            throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        FilteringGeneratorDelegate delegate = new FilteringGeneratorDelegate(
                generator(output), filter, inclusion, true);
        try (delegate) {
            call.write(delegate);
        }
        return new FilteredResult(read(output), delegate.getMatchCount());
    }
private static void writeStandardDocument(JsonGenerator generator) throws Exception {
        generator.writeStartObject();
        generator.writeNumberProperty("a", 123);
        generator.writeArrayPropertyStart("array");
        generator.writeNumber(1);
        generator.writeNumber(2);
        generator.writeEndArray();
        generator.writeObjectPropertyStart("ob");
        generator.writeNumberProperty("value0", 2);
        generator.writeNumberProperty("value", 3);
        generator.writeNumberProperty("value2", 4);
        generator.writeEndObject();
        generator.writeBooleanProperty("b", true);
        generator.writeEndObject();
    }
private static void writeSkippedArrayDocument(JsonGenerator generator) throws Exception {
        generator.writeStartObject();
        generator.writeArrayPropertyStart("array");
        generator.writeNumber(1);
        generator.writeStartArray();
        generator.writeNumber(2);
        generator.writeNumber(3);
        generator.writeEndArray();
        generator.writeEndArray();
        generator.writeArrayPropertyStart("ob");
        generator.writeStartObject();
        generator.writeStringProperty("value", "bar");
        generator.writeEndObject();
        generator.writeEndArray();
        generator.writeObjectPropertyStart("b");
        generator.writeArrayPropertyStart("foo");
        generator.writeNumber(1);
        generator.writeString("foo");
        generator.writeEndArray();
        generator.writeEndObject();
        generator.writeEndObject();
    }
private static void writeAlternateDocument(JsonGenerator generator) throws Exception {
        generator.writeStartObject();
        generator.writeName(new SerializedString("a"));
        generator.writeNumber(123);
        generator.writeName("array");
        generator.writeStartArray(2);
        generator.writeNumber("1");
        generator.writeNumber((short) 2);
        generator.writeEndArray();
        generator.writeName(new SerializedString("ob"));
        generator.writeStartObject();
        generator.writeNumberProperty("value0", 2);
        generator.writeName(new SerializedString("value"));
        generator.writeStartArray(1);
        generator.writeString(new SerializedString("x"));
        generator.writeEndArray();
        generator.writeStringProperty("value2", "foo");
        generator.writeEndObject();
        generator.writeBooleanProperty("b", true);
        generator.writeEndObject();
    }
private static void writeNoMatchObject(JsonGenerator generator) throws Exception {
        generator.writeStartObject();
        generator.writeObjectPropertyStart("root");
        generator.writeBooleanProperty("a0", true);
        generator.writeObjectPropertyStart("b");
        generator.writeNumberProperty("value", 4);
        generator.writeEndObject();
        generator.writeEndObject();
        generator.writeBooleanProperty("b0", false);
        generator.writeEndObject();
    }
private static final class NameMatchFilter extends TokenFilter {
        private final Set<String> names;

        NameMatchFilter(String... names) {
            this.names = new HashSet<>(Arrays.asList(names));
        }

        @Override
        public TokenFilter includeElement(int index) {
            return this;
        }

        @Override
        public TokenFilter includeProperty(String name) {
            return names.contains(name) ? TokenFilter.INCLUDE_ALL : this;
        }

        @Override
        protected boolean _includeScalar() {
            return false;
        }
    }
private static final class NameExcludeFilter extends TokenFilter {
        private final Set<String> names;
        private final boolean includeArrays;

        NameExcludeFilter(boolean includeArrays, String... names) {
            this.names = new HashSet<>(Arrays.asList(names));
            this.includeArrays = includeArrays;
        }

        @Override
        public TokenFilter includeElement(int index) {
            return includeArrays ? this : null;
        }

        @Override
        public TokenFilter includeProperty(String name) {
            return names.contains(name) ? null : this;
        }
    }
private static final class StrictNameMatchFilter extends TokenFilter {
        private final Set<String> names;

        StrictNameMatchFilter(String... names) {
            this.names = new HashSet<>(Arrays.asList(names));
        }

        @Override
        public TokenFilter includeProperty(String name) {
            return names.contains(name) ? TokenFilter.INCLUDE_ALL : null;
        }
    }
private static final class NoArraysFilter extends TokenFilter {
        @Override
        public TokenFilter filterStartArray() {
            return null;
        }
    }
private static final class NoObjectsFilter extends TokenFilter {
        @Override
        public TokenFilter filterStartObject() {
            return null;
        }
    }
private record FilteredResult(Object value, int matchCount) { }
@FunctionalInterface
    private interface WriterCall {
        void write(JsonGenerator generator) throws Exception;
    }

    void __invoke_nonFiltering() throws Exception {
        try {
            nonFiltering();
        } finally {
        }
    }


    void __invoke_singleMatchFilteringWithoutPath() throws Exception {
        try {
            singleMatchFilteringWithoutPath();
        } finally {
        }
    }


    void __invoke_singleMatchFilteringWithPath() throws Exception {
        try {
            singleMatchFilteringWithPath();
        } finally {
        }
    }


    void __invoke_singleMatchFilteringWithPathSkippedArray() throws Exception {
        try {
            singleMatchFilteringWithPathSkippedArray();
        } finally {
        }
    }


    void __invoke_singleMatchFilteringWithPathAlternate1() throws Exception {
        try {
            singleMatchFilteringWithPathAlternate1();
        } finally {
        }
    }


    void __invoke_singleMatchFilteringWithPathRawBinary() throws Exception {
        try {
            singleMatchFilteringWithPathRawBinary();
        } finally {
        }
    }


    void __invoke_noMatchFiltering6() throws Exception {
        try {
            noMatchFiltering6();
        } finally {
        }
    }


    void __invoke_valueOmitsFieldName1() throws Exception {
        try {
            valueOmitsFieldName1();
        } finally {
        }
    }


    void __invoke_valueOmitsFieldName2() throws Exception {
        try {
            valueOmitsFieldName2();
        } finally {
        }
    }


    void __invoke_writeStartObjectWithObject() throws Exception {
        try {
            writeStartObjectWithObject();
        } finally {
        }
    }


    void __invoke_rawValueDelegationWithArray() throws Exception {
        try {
            rawValueDelegationWithArray();
        } finally {
        }
    }


    void __invoke_rawValueDelegationWithObject() throws Exception {
        try {
            rawValueDelegationWithObject();
        } finally {
        }
    }

}
