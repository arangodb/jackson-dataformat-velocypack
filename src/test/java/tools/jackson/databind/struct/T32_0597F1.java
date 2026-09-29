package tools.jackson.databind.struct;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonDeserialize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import tools.jackson.dataformat.velocypack.*;

class T32_0597F1 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final ObjectMapper ACCEPT_SINGLE_MAPPER = VPackMapper.builder()
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .build();

    // Provenance: ManagedReferenceNullHandling4758Test#testNonNullChildrenWithManagedReference().
    void testNonNullChildrenWithManagedReferenceVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .withConfigOverride(List.class,
                        o -> o.setNullHandling(JsonSetter.Value.forValueNulls(Nulls.AS_EMPTY)))
                .build();

        Parent result = mapper.readValue(MANAGED_CHILDREN_INPUT, Parent.class);
        assertNotNull(result.children);
        assertEquals(2, result.children.size());
        assertEquals("child1", result.children.get(0).name);
        assertEquals("child2", result.children.get(1).name);
        assertSame(result, result.children.get(0).parent);
        assertSame(result, result.children.get(1).parent);
    }
private static final byte[] SINGLE_STRING_VALUES = VPackWireFixtureTest.hex(
            "0b 11 01 46 76 61 6c 75 65 73 45 66 69 72 73 74 03");
private static final byte[] SINGLE_INT_VALUES = VPackWireFixtureTest.hex(
            "0b 0d 01 46 76 61 6c 75 65 73 28 7b 03");
private static final byte[] SINGLE_LONG_VALUES = VPackWireFixtureTest.hex(
            "0b 0e 01 46 76 61 6c 75 65 73 21 33 ff 03");
private static final byte[] SINGLE_FLOAT_VALUES = VPackWireFixtureTest.hex(
            "0b 14 01 46 76 61 6c 75 65 73 1b 00 00 00 00 00 00 d0 3f 03");
private static final byte[] SINGLE_FACTORY_VALUES = VPackWireFixtureTest.hex(
            "0b 0f 01 46 76 61 6c 75 65 73 43 33 33 33 03");
private static final byte[] SINGLE_ENUM_VALUES = VPackWireFixtureTest.hex(
            "0b 0d 01 46 76 61 6c 75 65 73 41 42 03");
private static final byte[] ROLE_INPUT = VPackWireFixtureTest.hex(
            "0b 20 01 45 72 6f 6c 65 73 0b 16 02 44 4e 61 6d 65"
          + "44 55 73 65 72 42 49 44 43 33 33 33 0d 03 03");
private static final byte[] CHAINED_CREATORS_INPUT = VPackWireFixtureTest.hex(
            "06 1c 01 0b 18 01 47 6d 65 73 73 61 67 65 4b 6d 65 73 73 61 67 65 48 65 72 65 03 03");
private static final byte[] MANAGED_CHILDREN_INPUT = VPackWireFixtureTest.hex(
            "0b 3f 02 44 6e 61 6d 65 46 70 61 72 65 6e 74"
          + "48 63 68 69 6c 64 72 65 6e 06 25 02"
          + "0b 10 01 44 6e 61 6d 65 46 63 68 69 6c 64 31 03"
          + "0b 10 01 44 6e 61 6d 65 46 63 68 69 6c 64 32 03"
          + "03 13 0f 03");
static class StringArrayNotAnnotated {
        public String[] values;
    }
static class StringArrayWrapper {
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        public String[] values;
    }
static class IntArrayWrapper {
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        public int[] values;
    }
static class LongArrayWrapper {
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        public long[] values;
    }
static class FloatArrayWrapper {
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        public float[] values;
    }
static class StringListWrapper {
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        public List<String> values;
    }
@JsonDeserialize(builder = StringListWrapperWithBuilder.Builder.class)
    static class StringListWrapperWithBuilder {
        public final List<String> values;

        StringListWrapperWithBuilder(List<String> values) {
            this.values = values;
        }

        static class Builder {
            private List<String> values = Collections.emptyList();

            @JsonProperty
            @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            public Builder values(Iterable<? extends String> elements) {
                values = new ArrayList<>();
                for (String value : elements) {
                    values.add(value);
                }
                return this;
            }

            public StringListWrapperWithBuilder build() {
                return new StringListWrapperWithBuilder(values);
            }
        }
    }
@JsonDeserialize(builder = RolesInListWithBuilder.Builder.class)
    static class RolesInListWithBuilder {
        public final List<Role> roles;

        RolesInListWithBuilder(List<Role> roles) {
            this.roles = roles;
        }

        static class Builder {
            private List<Role> values = Collections.emptyList();

            @JsonProperty
            @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            public Builder roles(Iterable<? extends Role> elements) {
                values = new ArrayList<>();
                for (Role value : elements) {
                    values.add(value);
                }
                return this;
            }

            public RolesInListWithBuilder build() {
                return new RolesInListWithBuilder(values);
            }
        }
    }
static class WrapperWithStringFactoryInList {
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        public List<WrapperWithStringFactory> values;
    }
@JsonDeserialize
    static class WrapperWithStringFactory {
        final Role role;

        private WrapperWithStringFactory(Role role) {
            this.role = role;
        }

        @JsonCreator
        static WrapperWithStringFactory from(String value) {
            Role role = new Role();
            role.ID = "1";
            role.Name = value;
            return new WrapperWithStringFactory(role);
        }
    }
enum ABC { A, B, C }
static class EnumSetWrapper {
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        public EnumSet<ABC> values;
    }
static class Role {
        public String ID;
        public String Name;
    }
static class Bean1421A {
        List<Messages> bs = Collections.emptyList();

        @JsonCreator
        Bean1421A(final List<Messages> bs) {
            this.bs = bs;
        }
    }
static class Messages {
        List<MessageWrapper> cs = Collections.emptyList();

        @JsonCreator
        Messages(final List<MessageWrapper> cs) {
            this.cs = cs;
        }
    }
static class MessageWrapper {
        String message;

        @JsonCreator
        MessageWrapper(@JsonProperty("message") String message) {
            this.message = message;
        }
    }
static class Bean1421B<T> {
        T value;

        @JsonCreator
        Bean1421B(T value) {
            this.value = value;
        }
    }
static class Parent {
        public String name;

        @JsonManagedReference
        public List<Item> children = new ArrayList<>();
    }
static class Item {
        public String name;

        @JsonBackReference
        public Parent parent;
    }

    void __invoke_testNonNullChildrenWithManagedReferenceVpack() throws Exception {
        try {
            testNonNullChildrenWithManagedReferenceVpack();
        } finally {
        }
    }

}
