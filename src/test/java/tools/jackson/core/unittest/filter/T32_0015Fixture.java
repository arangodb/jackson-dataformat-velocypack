package tools.jackson.core.unittest.filter;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.core.filter.FilteringGeneratorDelegate;
import tools.jackson.core.filter.TokenFilter;
import tools.jackson.core.filter.TokenFilter.Inclusion;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0015Fixture {

    void multipleMatchFilteringWithPath1() throws Exception {
        final String document = "{'a':123,'array':[1,2],'ob':{'value0':2,'value':3,'value2':4},'b':true}";

        FilteredResult result = filtered(new NameMatchFilter("value0", "value2"), generator -> {
            writeObject(generator, document);
        });
        assertEquals(Map.of("ob", Map.of("value0", 2, "value2", 4)), result.value());
        assertEquals(2, result.matchCount());

        result = filtered(new NameExcludeFilter(true, "ob"), generator -> {
            writeObject(generator, document);
        });
        assertEquals(Map.of("a", 123, "array", List.of(1, 2), "b", true), result.value());

        result = filtered(new NameExcludeFilter(false, "ob"), generator -> {
            writeObject(generator, document);
        });
        assertEquals(Map.of("a", 123, "b", true), result.value());
    }

    void multipleMatchFilteringWithPath2() throws Exception {
        FilteredResult result = filtered(new NameMatchFilter("array", "b", "value"), generator -> {
            writeObject(generator, "{'a':123,'array':[1,2],'ob':{'value0':2,'value':3,'value2':4},'b':true}");
        });
        assertEquals(Map.of("array", List.of(1, 2), "ob", Map.of("value", 3), "b", true), result.value());
        assertEquals(3, result.matchCount());
    }

    void multipleMatchFilteringWithPath3() throws Exception {
        FilteredResult result = filtered(new NameMatchFilter("value"), generator -> {
            writeObject(generator, "{'root':{'a0':true,'a':{'value':3},'b':{'value':'abc'}},'b0':false}");
        });
        assertEquals(Map.of("root", Map.of("a", Map.of("value", 3), "b", Map.of("value", "abc"))), result.value());
        assertEquals(2, result.matchCount());
    }

    void multipleMatchFilteringWithPath4() throws Exception {
        FilteredResult result = filtered(new NameMatchFilter("b0"), generator -> {
            writeObject(generator, "{'root':{'a0':true,'a':{'value':3},'b':{'value':'abc'}},'b0':false}");
        });
        assertEquals(Map.of("b0", false), result.value());
        assertEquals(1, result.matchCount());
    }

    void noMatchFiltering1() throws Exception {
        FilteredResult result = filtered(new NameMatchFilter("invalid"), generator -> {
            writeObject(generator, "{'root':{'a0':true,'b':{'value':4}},'b0':false}");
        }, Inclusion.INCLUDE_NON_NULL);
        assertEquals(Map.of("root", Map.of("b", Map.of())), result.value());
        assertEquals(0, result.matchCount());
    }

    void noMatchFiltering2() throws Exception {
        FilteredResult result = filtered(new NameMatchFilter("invalid"), generator -> {
            generator.writeStartArray();
            writeObject(generator, "{'root':{'a0':true,'b':{'value':4}},'b0':false}");
            writeObject(generator, "{'root':{'a0':true,'b':{'value':4}},'b0':false}");
            writeObject(generator, "{'root':{'a0':true,'b':{'value':4}},'b0':false}");
            generator.writeEndArray();
        }, Inclusion.INCLUDE_NON_NULL);
        assertEquals(List.of(
                Map.of("root", Map.of("b", Map.of())),
                Map.of("root", Map.of("b", Map.of())),
                Map.of("root", Map.of("b", Map.of()))), result.value());
        assertEquals(0, result.matchCount());
    }

    void noMatchFiltering3() throws Exception {
        FilteredResult result = filtered(new NameMatchFilter("invalid"), generator -> {
            generator.writeStartArray();
            for (int i = 0; i < 3; ++i) {
                generator.writeStartArray();
                writeObject(generator, "{'root':{'a0':true,'b':{'value':4}},'b0':false}");
                generator.writeEndArray();
            }
            generator.writeEndArray();
        }, Inclusion.INCLUDE_NON_NULL);
        assertEquals(List.of(
                List.of(Map.of("root", Map.of("b", Map.of()))),
                List.of(Map.of("root", Map.of("b", Map.of()))),
                List.of(Map.of("root", Map.of("b", Map.of())))), result.value());
        assertEquals(0, result.matchCount());
    }

    void noMatchFiltering4() throws Exception {
        FilteredResult result = filtered(new StrictNameMatchFilter("invalid"), generator -> {
            writeObject(generator, "{'root':{'a0':true,'a':{'value':3},'b':{'value':4}},'b0':false}");
        }, Inclusion.INCLUDE_NON_NULL);
        assertEquals(Map.of(), result.value());
        assertEquals(0, result.matchCount());
    }

    void noMatchFiltering5() throws Exception {
        FilteredResult result = filtered(new StrictNameMatchFilter("invalid"), generator -> {
            generator.writeStartArray();
            for (int i = 0; i < 3; ++i) {
                writeObject(generator, "{'root':{'a0':true,'b':{'value':4}},'b0':false}");
            }
            generator.writeEndArray();
        }, Inclusion.INCLUDE_NON_NULL);
        assertEquals(List.of(Map.of(), Map.of(), Map.of()), result.value());
        assertEquals(0, result.matchCount());
    }

    void indexMatchWithPath1() throws Exception {
        final String document = "{'a':123,'array':[1,2],'ob':{'value0':2,'value':3,'value2':'abc'},'b':true}";

        FilteredResult result = filtered(new IndexMatchFilter(1), generator -> {
            writeObject(generator, document);
        });
        assertEquals(Map.of("array", List.of(2)), result.value());

        result = filtered(new IndexMatchFilter(0), generator -> {
            writeObject(generator, document);
        });
        assertEquals(Map.of("array", List.of(1)), result.value());
        assertEquals(1, result.matchCount());
    }

    void indexMatchWithPath2() throws Exception {
        FilteredResult result = filtered(new IndexMatchFilter(0, 1), generator -> {
            writeObject(generator, "{'a':123,'array':[1,2],'ob':{'value0':2,'value':3,'value2':4},'b':true}");
        });
        assertEquals(Map.of("array", List.of(1, 2)), result.value());
        assertEquals(2, result.matchCount());

        result = filtered(new IndexMatchFilter(1, 3, 5), generator -> {
            writeObject(generator, "{'a':123,'misc':[1,2,null,true,false,'abc',123],'ob':null,'b':true}");
        });
        assertEquals(Map.of("misc", List.of(2, true, "abc")), result.value());
        assertEquals(3, result.matchCount());

        result = filtered(new IndexMatchFilter(2, 6), generator -> {
            writeObject(generator, "{'misc':[1,2,null,0.25,false,'abc',11234567890]}");
        });
        assertEquals(Map.of("misc", Arrays.asList(null, 11234567890L)), result.value());
        assertEquals(2, result.matchCount());

        result = filtered(new IndexMatchFilter(1), generator -> {
            writeObject(generator, "{'misc':[1,0.25,11234567890]}");
        });
        assertEquals(Map.of("misc", List.of(0.25)), result.value());
        assertEquals(1, result.matchCount());
    }

    void includeEmptyTopLevelObject() throws Exception {
        FilteredResult result = filtered(INCLUDE_EMPTY_IF_NOT_FILTERED, generator -> {
            generator.writeStartObject();
            generator.writeEndObject();
        });
        assertEquals(Map.of(), result.value());
    }
private static void writeObject(JsonGenerator generator, String document) throws Exception {
        if (document.contains("'")) {
            // The deterministic documents above are written directly so the
            // filtering delegate sees the same token structure as the source.
            writeKnownDocument(generator, document);
        } else {
            throw new IllegalArgumentException("unexpected fixture");
        }
    }
private static void writeKnownDocument(JsonGenerator generator, String document) throws Exception {
        switch (document) {
        case "{'a':123,'array':[1,2],'ob':{'value0':2,'value':3,'value2':4},'b':true}" -> {
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
        case "{'a':123,'array':[1,2],'ob':{'value0':2,'value':3,'value2':'abc'},'b':true}" -> {
            generator.writeStartObject();
            generator.writeNumberProperty("a", 123);
            generator.writeArrayPropertyStart("array");
            generator.writeNumber(1);
            generator.writeNumber(2);
            generator.writeEndArray();
            generator.writeObjectPropertyStart("ob");
            generator.writeNumberProperty("value0", 2);
            generator.writeNumberProperty("value", 3);
            generator.writeStringProperty("value2", "abc");
            generator.writeEndObject();
            generator.writeBooleanProperty("b", true);
            generator.writeEndObject();
        }
        case "{'root':{'a0':true,'a':{'value':3},'b':{'value':'abc'}},'b0':false}" -> {
            generator.writeStartObject();
            generator.writeObjectPropertyStart("root");
            generator.writeBooleanProperty("a0", true);
            generator.writeObjectPropertyStart("a");
            generator.writeNumberProperty("value", 3);
            generator.writeEndObject();
            generator.writeObjectPropertyStart("b");
            generator.writeStringProperty("value", "abc");
            generator.writeEndObject();
            generator.writeEndObject();
            generator.writeBooleanProperty("b0", false);
            generator.writeEndObject();
        }
        case "{'root':{'a0':true,'b':{'value':4}},'b0':false}" -> {
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
        case "{'root':{'a0':true,'a':{'value':3},'b':{'value':4}},'b0':false}" -> {
            generator.writeStartObject();
            generator.writeObjectPropertyStart("root");
            generator.writeBooleanProperty("a0", true);
            generator.writeObjectPropertyStart("a");
            generator.writeNumberProperty("value", 3);
            generator.writeEndObject();
            generator.writeObjectPropertyStart("b");
            generator.writeNumberProperty("value", 4);
            generator.writeEndObject();
            generator.writeEndObject();
            generator.writeBooleanProperty("b0", false);
            generator.writeEndObject();
        }
        case "{'a':123,'misc':[1,2,null,true,false,'abc',123],'ob':null,'b':true}" -> {
            generator.writeStartObject();
            generator.writeNumberProperty("a", 123);
            generator.writeArrayPropertyStart("misc");
            generator.writeNumber(1);
            generator.writeNumber(2);
            generator.writeNull();
            generator.writeBoolean(true);
            generator.writeBoolean(false);
            generator.writeString("abc");
            generator.writeNumber(123);
            generator.writeEndArray();
            generator.writeNullProperty("ob");
            generator.writeBooleanProperty("b", true);
            generator.writeEndObject();
        }
        case "{'misc':[1,2,null,0.25,false,'abc',11234567890]}" -> {
            generator.writeStartObject();
            generator.writeArrayPropertyStart("misc");
            generator.writeNumber(1);
            generator.writeNumber(2);
            generator.writeNull();
            generator.writeNumber(0.25);
            generator.writeBoolean(false);
            generator.writeString("abc");
            generator.writeNumber(11234567890L);
            generator.writeEndArray();
            generator.writeEndObject();
        }
        case "{'misc':[1,0.25,11234567890]}" -> {
            generator.writeStartObject();
            generator.writeArrayPropertyStart("misc");
            generator.writeNumber(1);
            generator.writeNumber(0.25);
            generator.writeNumber(11234567890L);
            generator.writeEndArray();
            generator.writeEndObject();
        }
        default -> throw new IllegalArgumentException("unexpected fixture: " + document);
        }
    }
private static FilteredResult filtered(TokenFilter filter, WriterCall call) throws Exception {
        return filtered(filter, call, Inclusion.INCLUDE_ALL_AND_PATH);
    }
private static FilteredResult filtered(TokenFilter filter, WriterCall call, Inclusion inclusion) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        FilteringGeneratorDelegate delegate = new FilteringGeneratorDelegate(
                new VPackFactory().createGenerator(ObjectWriteContext.empty(), output),
                filter, inclusion, true);
        try (delegate) {
            call.write(delegate);
        }
        return new FilteredResult(new VPackMapper().readValue(output.toByteArray(), Object.class),
                delegate.getMatchCount());
    }
private static final TokenFilter INCLUDE_EMPTY_IF_NOT_FILTERED = new TokenFilter() {
        @Override
        public boolean includeEmptyArray(boolean contentsFiltered) {
            return !contentsFiltered;
        }

        @Override
        public boolean includeEmptyObject(boolean contentsFiltered) {
            return !contentsFiltered;
        }

        @Override
        protected boolean _includeScalar() {
            return false;
        }
    };
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
private static final class IndexMatchFilter extends TokenFilter {
        private final BitSet indices = new BitSet();

        IndexMatchFilter(int... indices) {
            for (int index : indices) {
                this.indices.set(index);
            }
        }

        @Override
        public TokenFilter includeProperty(String name) {
            return this;
        }

        @Override
        public TokenFilter includeElement(int index) {
            return indices.get(index) ? TokenFilter.INCLUDE_ALL : null;
        }

        @Override
        protected boolean _includeScalar() {
            return false;
        }
    }
private record FilteredResult(Object value, int matchCount) { }
@FunctionalInterface
    private interface WriterCall {
        void write(JsonGenerator generator) throws Exception;
    }

    void __invoke_multipleMatchFilteringWithPath1() throws Exception {
        try {
            multipleMatchFilteringWithPath1();
        } finally {
        }
    }


    void __invoke_multipleMatchFilteringWithPath2() throws Exception {
        try {
            multipleMatchFilteringWithPath2();
        } finally {
        }
    }


    void __invoke_multipleMatchFilteringWithPath3() throws Exception {
        try {
            multipleMatchFilteringWithPath3();
        } finally {
        }
    }


    void __invoke_multipleMatchFilteringWithPath4() throws Exception {
        try {
            multipleMatchFilteringWithPath4();
        } finally {
        }
    }


    void __invoke_noMatchFiltering1() throws Exception {
        try {
            noMatchFiltering1();
        } finally {
        }
    }


    void __invoke_noMatchFiltering2() throws Exception {
        try {
            noMatchFiltering2();
        } finally {
        }
    }


    void __invoke_noMatchFiltering3() throws Exception {
        try {
            noMatchFiltering3();
        } finally {
        }
    }


    void __invoke_noMatchFiltering4() throws Exception {
        try {
            noMatchFiltering4();
        } finally {
        }
    }


    void __invoke_noMatchFiltering5() throws Exception {
        try {
            noMatchFiltering5();
        } finally {
        }
    }


    void __invoke_indexMatchWithPath1() throws Exception {
        try {
            indexMatchWithPath1();
        } finally {
        }
    }


    void __invoke_indexMatchWithPath2() throws Exception {
        try {
            indexMatchWithPath2();
        } finally {
        }
    }


    void __invoke_includeEmptyTopLevelObject() throws Exception {
        try {
            includeEmptyTopLevelObject();
        } finally {
        }
    }

}
