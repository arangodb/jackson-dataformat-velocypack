package tools.jackson.databind.struct;

import com.fasterxml.jackson.annotation.*;
import org.junit.jupiter.api.Test;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.testutil.DatabindTestUtil;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class FormatFeatureAcceptSingleTest extends DatabindTestUtil
{
    // // // Inner types for per-property @JsonFormat annotation tests

    static class StringArrayNotAnnoted {
        public String[] values;

        protected StringArrayNotAnnoted() { }
        public StringArrayNotAnnoted(String ... v) { values = v; }
    }

    static class StringArrayWrapper {
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        public String[] values;
    }

    static class BooleanArrayWrapper {
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        public boolean[] values;
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

    static class DoubleArrayWrapper {
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        public double[] values;
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

    static class EnumSetWrapper {
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        public EnumSet<ABC> values;
    }

    static class RolesInArray {
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        public Role[] roles;
    }

    static class RolesInList {
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        public List<Role> roles;
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

    static class Role {
        public String ID;
        public String Name;
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

    // // // Inner types for global ACCEPT_SINGLE_VALUE_AS_ARRAY feature tests

    // [databind#1421]
    static class Bean1421A
    {
        List<Messages> bs = Collections.emptyList();

        @JsonCreator
        Bean1421A(final List<Messages> bs)
        {
            this.bs = bs;
        }
    }

    static class Messages
    {
        List<MessageWrapper> cs = Collections.emptyList();

        @JsonCreator
        Messages(final List<MessageWrapper> cs)
        {
            this.cs = cs;
        }
    }

    static class MessageWrapper
    {
        String message;

        @JsonCreator
        MessageWrapper(@JsonProperty("message") String message)
        {
            this.message = message;
        }
    }

    static class Bean1421B<T> {
        T value;

        @JsonCreator
        public Bean1421B(T value) {
            this.value = value;
        }
    }

    // for [databind#5537]
    static class Bean5537 {
        Collection<IdentifiedType5537> collection;
        IdentifiedType5537 value;

        public void setValue(IdentifiedType5537 value) {
            this.value = value;
        }

        public void setCollection(Collection<IdentifiedType5537> collection) {
            this.collection = collection;
        }
    }

    @JsonIdentityInfo(generator=ObjectIdGenerators.IntSequenceGenerator.class)
    static class IdentifiedType5537 {
        String entry;

        @JsonCreator
        IdentifiedType5537(@JsonProperty("entry") String entry)
        {
            this.entry = entry;
        }
    }

    static class Bean5541 {
        IdentifiedType5537[] array;
        IdentifiedType5537 value;

        public void setValue(IdentifiedType5537 value) {
            this.value = value;
        }

        public void setArray(IdentifiedType5537[] array) {
            this.array = array;
        }
    }

    /*
    /**********************************************************
    /* Mapper instances
    /**********************************************************
     */

    private final ObjectMapper MAPPER = newVPackMapper();

    private final ObjectMapper ACCEPT_SINGLE_MAPPER = vpackMapperBuilder()
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .build();

    /*
    /**********************************************************
    /* Test methods, reading with per-property single-element unwrapping
    /**********************************************************
     */

    @Test
    public void testSingleStringArrayRead() throws Exception {
        String json = a2q(
                "{ 'values': 'first' }");
        StringArrayWrapper result = MAPPER.readValue(VPackUtils.toVPack(json), StringArrayWrapper.class);
        assertNotNull(result.values);
        assertEquals(1, result.values.length);
        assertEquals("first", result.values[0]);

        // and then without annotation, but with global override
        ObjectMapper mapper = vpackMapperBuilder()
                .withConfigOverride(String[].class,
                        o -> o.setFormat(JsonFormat.Value.empty()
                                .withFeature(JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)))
                .build();
        StringArrayNotAnnoted result2 = mapper.readValue(VPackUtils.toVPack(json), StringArrayNotAnnoted.class);
        assertNotNull(result2.values);
        assertEquals(1, result2.values.length);
        assertEquals("first", result2.values[0]);
    }

    @Test
    public void testSingleIntArrayRead() throws Exception {
        String json = a2q(
                "{ 'values': 123 }");
        IntArrayWrapper result = MAPPER.readValue(VPackUtils.toVPack(json), IntArrayWrapper.class);
        assertNotNull(result.values);
        assertEquals(1, result.values.length);
        assertEquals(123, result.values[0]);
    }

    @Test
    public void testSingleLongArrayRead() throws Exception {
        String json = a2q(
                "{ 'values': -205 }");
        LongArrayWrapper result = MAPPER.readValue(VPackUtils.toVPack(json), LongArrayWrapper.class);
        assertNotNull(result.values);
        assertEquals(1, result.values.length);
        assertEquals(-205L, result.values[0]);
    }

    @Test
    public void testSingleBooleanArrayRead() throws Exception {
        String json = a2q(
                "{ 'values': true }");
        BooleanArrayWrapper result = MAPPER.readValue(VPackUtils.toVPack(json), BooleanArrayWrapper.class);
        assertNotNull(result.values);
        assertEquals(1, result.values.length);
        assertTrue(result.values[0]);
    }

    @Test
    public void testSingleDoubleArrayRead() throws Exception {
        String json = a2q(
                "{ 'values': -0.5 }");
        DoubleArrayWrapper result = MAPPER.readValue(VPackUtils.toVPack(json), DoubleArrayWrapper.class);
        assertNotNull(result.values);
        assertEquals(1, result.values.length);
        assertEquals(-0.5, result.values[0]);
    }

    @Test
    public void testSingleFloatArrayRead() throws Exception {
        String json = a2q(
                "{ 'values': 0.25 }");
        FloatArrayWrapper result = MAPPER.readValue(VPackUtils.toVPack(json), FloatArrayWrapper.class);
        assertNotNull(result.values);
        assertEquals(1, result.values.length);
        assertEquals(0.25f, result.values[0]);
    }

    @Test
    public void testSingleElementArrayRead() throws Exception {
        String json = a2q(
                "{ 'roles': { 'Name': 'User', 'ID': '333' } }");
        RolesInArray response = MAPPER.readValue(VPackUtils.toVPack(json), RolesInArray.class);
        assertNotNull(response.roles);
        assertEquals(1, response.roles.length);
        assertEquals("333", response.roles[0].ID);
    }

    @Test
    public void testSingleStringListRead() throws Exception {
        String json = a2q(
                "{ 'values': 'first' }");
        StringListWrapper result = MAPPER.readValue(VPackUtils.toVPack(json), StringListWrapper.class);
        assertNotNull(result.values);
        assertEquals(1, result.values.size());
        assertEquals("first", result.values.get(0));
    }

    @Test
    public void testSingleStringListReadWithBuilder() throws Exception {
        String json = a2q(
                "{ 'values': 'first' }");
        StringListWrapperWithBuilder result =
                MAPPER.readValue(VPackUtils.toVPack(json), StringListWrapperWithBuilder.class);
        assertNotNull(result.values);
        assertEquals(1, result.values.size());
        assertEquals("first", result.values.get(0));
    }

    @Test
    public void testSingleElementListRead() throws Exception {
        String json = a2q(
                "{ 'roles': { 'Name': 'User', 'ID': '333' } }");
        RolesInList response = MAPPER.readValue(VPackUtils.toVPack(json), RolesInList.class);
        assertNotNull(response.roles);
        assertEquals(1, response.roles.size());
        assertEquals("333", response.roles.get(0).ID);
    }

    @Test
    public void testSingleElementListReadWithBuilder() throws Exception {
        String json = a2q(
                "{ 'roles': { 'Name': 'User', 'ID': '333' } }");
        RolesInListWithBuilder response = MAPPER.readValue(VPackUtils.toVPack(json), RolesInListWithBuilder.class);
        assertNotNull(response.roles);
        assertEquals(1, response.roles.size());
        assertEquals("333", response.roles.get(0).ID);
    }

    @Test
    public void testSingleElementWithStringFactoryRead() throws Exception {
        String json = a2q(
                "{ 'values': '333' }");
        WrapperWithStringFactoryInList response = MAPPER.readValue(VPackUtils.toVPack(json), WrapperWithStringFactoryInList.class);
        assertNotNull(response.values);
        assertEquals(1, response.values.size());
        assertEquals("333", response.values.get(0).role.Name);
    }

    @Test
    public void testSingleEnumSetRead() throws Exception {
        EnumSetWrapper result = MAPPER.readValue(VPackUtils.toVPack(a2q("{ 'values': 'B' }")),
                EnumSetWrapper.class);
        assertNotNull(result.values);
        assertEquals(1, result.values.size());
        assertEquals(ABC.B, result.values.iterator().next());
    }

    /*
    /**********************************************************
    /* Test methods, reading with global ACCEPT_SINGLE_VALUE_AS_ARRAY feature
    /**********************************************************
     */

    @Test
    public void testSuccessfulDeserializationOfObjectWithChainedArrayCreators() throws Exception
    {
        Bean1421A result = ACCEPT_SINGLE_MAPPER.readValue(VPackUtils.toVPack("[{\"message\":\"messageHere\"}]"), Bean1421A.class);
        assertNotNull(result);
        assertNotNull(result.bs);
        assertEquals(1, result.bs.size());
    }

    @Test
    public void testWithSingleString() throws Exception {
        Bean1421B<List<String>> a = ACCEPT_SINGLE_MAPPER.readValue(VPackUtils.toVPack(q("test2")),
                new TypeReference<Bean1421B<List<String>>>() {});
        List<String> expected = new ArrayList<>();
        expected.add("test2");
        assertEquals(expected, a.value);
    }

    @Test
    public void testPrimitives() throws Exception {
        int[] i = ACCEPT_SINGLE_MAPPER.readValue(VPackUtils.toVPack("16"), int[].class);
        assertEquals(1, i.length);
        assertEquals(16, i[0]);

        long[] l = ACCEPT_SINGLE_MAPPER.readValue(VPackUtils.toVPack("1234"), long[].class);
        assertEquals(1, l.length);
        assertEquals(1234L, l[0]);

        double[] d = ACCEPT_SINGLE_MAPPER.readValue(VPackUtils.toVPack("12.5"), double[].class);
        assertEquals(1, d.length);
        assertEquals(12.5, d[0]);

        boolean[] b = ACCEPT_SINGLE_MAPPER.readValue(VPackUtils.toVPack("true"), boolean[].class);
        assertEquals(1, d.length);
        assertTrue(b[0]);
    }

    // for [databind#5537]
    @Test
    public void testCollectionWithObjectId() throws Exception
    {
        Bean5537 result = ACCEPT_SINGLE_MAPPER.readValue(
                VPackUtils.toVPack("{\"collection\":1,\"value\":{\"@id\":1,\"entry\":\"s\"}}"),
                Bean5537.class);
        assertNotNull(result);
        assertNotNull(result.value);
        assertEquals(1, result.collection.size());
        assertNotNull(result.collection.iterator().next());
        assertEquals("s", result.collection.iterator().next().entry);
    }

    // for [databind#5541]
    @Test
    public void testArrayWithObjectId() throws Exception
    {
        Bean5541 result = ACCEPT_SINGLE_MAPPER.readValue(
                VPackUtils.toVPack("{\"array\":1,\"value\":{\"@id\":1,\"entry\":\"s\"}}"),
                Bean5541.class);
        assertNotNull(result);
        assertNotNull(result.value);
        assertEquals(1, result.array.length);
        assertNotNull(result.array[0]);
        assertEquals("s", result.array[0].entry);
    }
}
