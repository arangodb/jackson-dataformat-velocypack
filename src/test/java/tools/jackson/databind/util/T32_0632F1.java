package tools.jackson.databind.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.regex.Pattern;

import tools.jackson.databind.DeserializationConfig;
import tools.jackson.databind.introspect.AnnotatedField;
import tools.jackson.databind.util.SimpleLookupCache;
import tools.jackson.databind.util.StdDateFormat;
import static org.junit.jupiter.api.Assertions.*;

import tools.jackson.dataformat.velocypack.*;

class T32_0632F1 {
private final VPackMapper mapper = new VPackMapper();
private DeserializationConfig config() { return mapper.deserializationConfig(); }
private AnnotatedField fieldOf() {
        var type = mapper.constructType(SimpleBean.class);
        var annotatedClass = tools.jackson.databind.introspect.AnnotatedClassResolver
                .resolve(config(), type, config());
        return annotatedClass.fields().iterator().next();
    }

    void lookupCachePutGet() {
        SimpleLookupCache<String, Integer> cache = new SimpleLookupCache<>(5, 5);
        assertEquals(0, cache.size());
        cache.put("k1", 100);
        assertEquals(1, cache.size());
        assertNull(cache.get("nosuchkey"));
        assertEquals(Integer.valueOf(100), cache.get("k1"));
        cache.put("k2", 200);
        assertEquals(2, cache.size());
        assertEquals(Integer.valueOf(200), cache.get("k2"));
    }

    void lookupCacheEviction() {
        SimpleLookupCache<String, Integer> cache = new SimpleLookupCache<>(5, 5);
        assertEquals(0, cache.size());
        for (int i = 1; i <= 8; ++i) {
            cache.put("k" + i, 99 + i);
            assertEquals(Math.min(i, 5), cache.size());
        }
        assertNull(cache.get("k3"));
        assertEquals(Integer.valueOf(105), cache.get("k6"));
    }

    void lookupCacheJdkSerialization() throws Exception {
        final int maxEntries = 32;
        SimpleLookupCache<String, Integer> cache = new SimpleLookupCache<>(16, maxEntries);
        cache.put("a", 1);
        assertEquals(1, cache.size());

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream output = new ObjectOutputStream(bytes)) {
            output.writeObject(cache);
        }
        SimpleLookupCache<String, Integer> result;
        try (ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            @SuppressWarnings("unchecked")
            SimpleLookupCache<String, Integer> restored =
                    (SimpleLookupCache<String, Integer>) input.readObject();
            result = restored;
        }
        assertNull(result.get("a"));
        assertEquals(0, result.size());
        assertNull(result.put("a", 2));
        assertEquals(Integer.valueOf(2), result.get("a"));
        assertEquals(1, result.size());
        for (int i = 0; i < maxEntries + 1; ++i) {
            result.put("fill" + i, i);
        }
        assertEquals(maxEntries, result.size());
    }
static class TestStdDateFormat extends StdDateFormat {
        Pattern plainPattern() { return PATTERN_PLAIN; }
        Pattern iso8601Pattern() { return PATTERN_ISO8601; }
        boolean looksLikeIso8601(String input) { return looksLikeISO8601(input); }
    }
static class SimpleBean {
        public String name;
    }

    void __invoke_lookupCachePutGet() throws Exception {
        try {
            lookupCachePutGet();
        } finally {
        }
    }


    void __invoke_lookupCacheEviction() throws Exception {
        try {
            lookupCacheEviction();
        } finally {
        }
    }


    void __invoke_lookupCacheJdkSerialization() throws Exception {
        try {
            lookupCacheJdkSerialization();
        } finally {
        }
    }

}
