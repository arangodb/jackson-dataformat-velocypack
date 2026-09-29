package tools.jackson.databind.convert;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.CoercionAction;
import tools.jackson.databind.cfg.CoercionInputShape;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0160Fixture {
private static final byte[] EMPTY_STRING = VPackWireFixtureTest.hex("40");
private static final byte[] BLANK_STRING = VPackWireFixtureTest.hex("41 20");
private static final byte[] EMPTY_ARRAY = VPackWireFixtureTest.hex("01");
private static final ObjectMapper NORMAL_MAPPER = VPackMapper.builder()
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .build();
private static final ObjectMapper COERCION_MAPPER = VPackMapper.builder()
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .withCoercionConfigDefaults(cfg -> cfg.setAcceptBlankAsEmpty(true)
                    .setCoercion(CoercionInputShape.EmptyString, CoercionAction.AsEmpty))
            .build();

    void testEmptyToList() throws Exception {
        assertEquals(Collections.singletonList(""),
                NORMAL_MAPPER.readValue(EMPTY_STRING, new TypeReference<List<String>>() { }));
    }

    void testCoercedEmptyToList() throws Exception {
        assertEquals(Collections.emptyList(),
                COERCION_MAPPER.readValue(EMPTY_STRING,
                        new TypeReference<List<String>>() { }));
    }

    void testCoercedEmptyToListWrapper() throws Exception {
        assertEquals(Collections.emptyList(),
                COERCION_MAPPER.readValue(EMPTY_STRING,
                        new TypeReference<List<StringWrapper>>() { }));
    }

    void testCoercedListToList() throws Exception {
        assertEquals(Collections.emptyList(),
                COERCION_MAPPER.readValue(EMPTY_ARRAY,
                        new TypeReference<List<String>>() { }));
    }

    void testCoercedListToListWrapper() throws Exception {
        assertEquals(Collections.emptyList(),
                COERCION_MAPPER.readValue(EMPTY_ARRAY,
                        new TypeReference<List<StringWrapper>>() { }));
    }

    void testEmptyToArray() throws Exception {
        assertArrayEquals(new String[] { "" },
                NORMAL_MAPPER.readValue(EMPTY_STRING, String[].class));
    }

    void testEmptyToArrayWrapper() throws Exception {
        assertArrayEquals(new StringWrapper[] { new StringWrapper("") },
                NORMAL_MAPPER.readValue(EMPTY_STRING, StringWrapper[].class));
    }

    void testCoercedEmptyToArray() throws Exception {
        assertArrayEquals(new String[0],
                COERCION_MAPPER.readValue(EMPTY_STRING, String[].class));
    }

    void testCoercedEmptyToArrayWrapper() throws Exception {
        assertArrayEquals(new StringWrapper[0],
                COERCION_MAPPER.readValue(EMPTY_STRING, StringWrapper[].class));
    }

    void testCoercedListToArray() throws Exception {
        assertArrayEquals(new String[0],
                COERCION_MAPPER.readValue(EMPTY_ARRAY, String[].class));
    }

    void testCoercedListToArrayWrapper() throws Exception {
        assertArrayEquals(new StringWrapper[0],
                COERCION_MAPPER.readValue(EMPTY_ARRAY, StringWrapper[].class));
    }

    void testCoercedBlankToListWrapper() throws Exception {
        assertEquals(Collections.emptyList(),
                COERCION_MAPPER.readValue(BLANK_STRING,
                        new TypeReference<List<StringWrapper>>() { }));
    }
static final class StringWrapper {
        private final String value;

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        StringWrapper(String value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof StringWrapper other && other.value.equals(value);
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }
    }

    void __invoke_testEmptyToList() throws Exception {
        try {
            testEmptyToList();
        } finally {
        }
    }


    void __invoke_testCoercedEmptyToList() throws Exception {
        try {
            testCoercedEmptyToList();
        } finally {
        }
    }


    void __invoke_testCoercedEmptyToListWrapper() throws Exception {
        try {
            testCoercedEmptyToListWrapper();
        } finally {
        }
    }


    void __invoke_testCoercedListToList() throws Exception {
        try {
            testCoercedListToList();
        } finally {
        }
    }


    void __invoke_testCoercedListToListWrapper() throws Exception {
        try {
            testCoercedListToListWrapper();
        } finally {
        }
    }


    void __invoke_testEmptyToArray() throws Exception {
        try {
            testEmptyToArray();
        } finally {
        }
    }


    void __invoke_testEmptyToArrayWrapper() throws Exception {
        try {
            testEmptyToArrayWrapper();
        } finally {
        }
    }


    void __invoke_testCoercedEmptyToArray() throws Exception {
        try {
            testCoercedEmptyToArray();
        } finally {
        }
    }


    void __invoke_testCoercedEmptyToArrayWrapper() throws Exception {
        try {
            testCoercedEmptyToArrayWrapper();
        } finally {
        }
    }


    void __invoke_testCoercedListToArray() throws Exception {
        try {
            testCoercedListToArray();
        } finally {
        }
    }


    void __invoke_testCoercedListToArrayWrapper() throws Exception {
        try {
            testCoercedListToArrayWrapper();
        } finally {
        }
    }


    void __invoke_testCoercedBlankToListWrapper() throws Exception {
        try {
            testCoercedBlankToListWrapper();
        } finally {
        }
    }

}
