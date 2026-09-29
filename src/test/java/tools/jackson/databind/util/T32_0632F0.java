package tools.jackson.databind.util;

import java.util.regex.Pattern;

import tools.jackson.databind.DeserializationConfig;
import tools.jackson.databind.PropertyName;
import tools.jackson.databind.introspect.AnnotatedField;
import tools.jackson.databind.util.SimpleBeanPropertyDefinition;
import tools.jackson.databind.util.StdDateFormat;
import static org.junit.jupiter.api.Assertions.*;

import tools.jackson.dataformat.velocypack.*;

class T32_0632F0 {
private final VPackMapper mapper = new VPackMapper();
private DeserializationConfig config() { return mapper.deserializationConfig(); }
private AnnotatedField fieldOf() {
        var type = mapper.constructType(SimpleBean.class);
        var annotatedClass = tools.jackson.databind.introspect.AnnotatedClassResolver
                .resolve(config(), type, config());
        return annotatedClass.fields().iterator().next();
    }

    void withSimpleNameSameName() {
        AnnotatedField field = fieldOf();
        SimpleBeanPropertyDefinition prop = SimpleBeanPropertyDefinition.construct(
                config(), field, PropertyName.construct("foo"));
        assertSame(prop, prop.withSimpleName("foo"));
    }

    void withSimpleNameHasNamespace() {
        AnnotatedField field = fieldOf();
        PropertyName nameWithNs = PropertyName.construct("foo", "http://ns");
        SimpleBeanPropertyDefinition prop = SimpleBeanPropertyDefinition.construct(
                config(), field, nameWithNs);
        var renamed = prop.withSimpleName("foo");
        assertNotSame(prop, renamed);
        assertEquals("foo", renamed.getName());
    }
static class TestStdDateFormat extends StdDateFormat {
        Pattern plainPattern() { return PATTERN_PLAIN; }
        Pattern iso8601Pattern() { return PATTERN_ISO8601; }
        boolean looksLikeIso8601(String input) { return looksLikeISO8601(input); }
    }
static class SimpleBean {
        public String name;
    }

    void __invoke_withSimpleNameSameName() throws Exception {
        try {
            withSimpleNameSameName();
        } finally {
        }
    }


    void __invoke_withSimpleNameHasNamespace() throws Exception {
        try {
            withSimpleNameHasNamespace();
        } finally {
        }
    }

}
