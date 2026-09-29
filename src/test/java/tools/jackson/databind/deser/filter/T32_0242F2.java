package tools.jackson.databind.deser.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0242F2 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] NO_NULLS_FOO_NULLS_OK_NULL = VPackWireFixtureTest.hex(
            "14 18 47 6e 6f 4e 75 6c 6c 73 43 66 6f 6f "
          + "47 6e 75 6c 6c 73 4f 6b 18 02");
private static final byte[] NO_NULLS_NULL = VPackWireFixtureTest.hex(
            "14 0c 47 6e 6f 4e 75 6c 6c 73 18 01");
private static final byte[] VALUE_NULL = VPackWireFixtureTest.hex(
            "14 0a 45 76 61 6c 75 65 18 01");
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] NULL_ENDING = VPackWireFixtureTest.hex(
            "14 1a 46 66 69 65 6c 64 31 44 64 61 74 61 "
          + "49 6c 69 73 74 49 6e 6e 65 72 18 02");
private static final byte[] NULL_BEGINNING = VPackWireFixtureTest.hex(
            "14 1a 49 6c 69 73 74 49 6e 6e 65 72 18 "
          + "46 66 69 65 6c 64 31 44 64 61 74 61 02");

    // Provenance: NullSkip4441Test#testFields.
    void testFieldsVpack() throws Exception {
        // Preserve both source orderings: null after and before the creator field.
        assertMiddle(MAPPER.readValue(NULL_ENDING, Middle.class));
        assertMiddle(MAPPER.readValue(NULL_BEGINNING, Middle.class));
    }

    // Provenance: NullSkip4441Test#testMethods.
    void testMethodsVpack() throws Exception {
        // Preserve both source orderings: null after and before the creator field.
        assertMiddleSetter(MAPPER.readValue(NULL_ENDING, MiddleSetter.class));
        assertMiddleSetter(MAPPER.readValue(NULL_BEGINNING, MiddleSetter.class));
    }
private static final ObjectMapper MAPPER_WITH_AS_EMPTY = VPackMapper.builder()
            .changeDefaultNullHandling(h -> JsonSetter.Value.construct(Nulls.AS_EMPTY,
                    Nulls.AS_EMPTY))
            .build();
private static void assertMiddle(Middle middle) {
        assertNotNull(middle);
        assertNotNull(middle.getField1());
        assertNotNull(middle.getListInner());
    }
private static void assertMiddleSetter(MiddleSetter middle) {
        assertNotNull(middle);
        assertNotNull(middle.getField1());
        assertNotNull(middle.getListInner());
    }
static class NullSkipMethod {
        String _nullsOk = "a";
        String _noNulls = "b";

        public void setNullsOk(String value) {
            _nullsOk = value;
        }

        @JsonSetter(nulls = Nulls.SKIP)
        public void setNoNulls(String value) {
            _noNulls = value;
        }
    }
static class NullConversionsStringValue {
        String value = "default";

        public void setValue(String value) {
            this.value = value;
        }
    }
static class Pojo {
        List<String> _value;

        @JsonCreator
        public Pojo(@JsonProperty("value") List<String> value) {
            _value = Objects.requireNonNull(value, "value");
        }

        protected Pojo() { }

        public List<String> value() {
            return _value;
        }

        public void setOther(List<String> value) { }
    }
static class Middle {
        @JsonSetter(nulls = Nulls.SKIP)
        private final List<Inner> listInner = new ArrayList<>();
        private final String field1;

        @JsonCreator
        public Middle(@JsonProperty("field1") String field1) {
            this.field1 = field1;
        }

        public List<Inner> getListInner() {
            return listInner;
        }

        public String getField1() {
            return field1;
        }
    }
static class Inner {
        private final String field1;

        @JsonCreator
        public Inner(@JsonProperty("field1") String field1) {
            this.field1 = field1;
        }

        public String getField1() {
            return field1;
        }
    }
static class MiddleSetter {
        private List<InnerSetter> listInner = new ArrayList<>();
        private final String field1;

        @JsonCreator
        public MiddleSetter(@JsonProperty("field1") String field1) {
            this.field1 = field1;
        }

        @JsonSetter(nulls = Nulls.SKIP)
        public void setListInner(List<InnerSetter> listInner) {
            this.listInner = Objects.requireNonNull(listInner);
        }

        public List<InnerSetter> getListInner() {
            return listInner;
        }

        public String getField1() {
            return field1;
        }
    }
static class InnerSetter {
        private final String field1;

        @JsonCreator
        public InnerSetter(@JsonProperty("field1") String field1) {
            this.field1 = field1;
        }

        public String getField1() {
            return field1;
        }
    }

    void __invoke_testFieldsVpack() throws Exception {
        try {
            testFieldsVpack();
        } finally {
        }
    }


    void __invoke_testMethodsVpack() throws Exception {
        try {
            testMethodsVpack();
        } finally {
        }
    }

}
