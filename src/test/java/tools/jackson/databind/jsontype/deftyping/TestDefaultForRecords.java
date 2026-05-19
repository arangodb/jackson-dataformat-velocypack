package tools.jackson.databind.jsontype.deftyping;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.testutil.DatabindTestUtil;
import tools.jackson.databind.testutil.NoCheckSubTypeValidator;

import static org.junit.jupiter.api.Assertions.*;

public class TestDefaultForRecords
    extends DatabindTestUtil
{
    public record TestRecord(String value) { }

    static final class RecordHolder
    {
        public Object value;

        public RecordHolder() { }
        public RecordHolder(TestRecord r) { value = r; }
    }

    /*
    /**********************************************************************
    /* Test methods
    /**********************************************************************
     */
    private final ObjectMapper DEFTYPING_MAPPER = vpackMapperBuilder()
            .activateDefaultTyping(
                    NoCheckSubTypeValidator.instance,
                    DefaultTyping.NON_FINAL_AND_RECORDS
            )
            .build();

    @Test
    public void testRecordDeserializeAsObject() throws Exception
    {
        TestRecord rec = new TestRecord("test");
        String json = VPackUtils.toJson(DEFTYPING_MAPPER.writeValueAsBytes(rec));
        assertEquals("[\"tools.jackson.databind.jsontype.deftyping.TestDefaultForRecords$TestRecord\",{\"value\":\"test\"}]", json);

        Object value = DEFTYPING_MAPPER.readValue(VPackUtils.toVPack(json), Object.class);
        assertInstanceOf(TestRecord.class, value);
    }

    @Test
    public void testNonFinalExcludesRecord() throws Exception
    {
        ObjectMapper nonFinalMapper = vpackMapperBuilder()
                .activateDefaultTyping(NoCheckSubTypeValidator.instance,
                        DefaultTyping.NON_FINAL)
                .build();

        TestRecord rec = new TestRecord("test");
        String json = VPackUtils.toJson(nonFinalMapper.writeValueAsBytes(rec));

        assertFalse(json.contains("TestDefaultForRecords$TestRecord"));
    }

    @Test
    public void testRecordAsObjectField() throws Exception
    {
        TestRecord rec = new TestRecord("test");
        String json = VPackUtils.toJson(DEFTYPING_MAPPER.writeValueAsBytes(new RecordHolder(rec)));
        assertEquals("{\"value\":[\"tools.jackson.databind.jsontype.deftyping.TestDefaultForRecords$TestRecord\",{\"value\":\"test\"}]}", json);
        RecordHolder holder = DEFTYPING_MAPPER.readValue(VPackUtils.toVPack(json), RecordHolder.class);
        assertEquals(rec, holder.value);
    }

    @Test
    public void testRecordInObjectArray() throws Exception
    {
        TestRecord rec = new TestRecord("test");
        String json = VPackUtils.toJson(DEFTYPING_MAPPER.writeValueAsBytes(new Object[] { rec }));
        assertEquals("[\"[Ljava.lang.Object;\",[[\"tools.jackson.databind.jsontype.deftyping.TestDefaultForRecords$TestRecord\",{\"value\":\"test\"}]]]", json);

        Object[] value = DEFTYPING_MAPPER.readValue(VPackUtils.toVPack(json), Object[].class);
        assertEquals(1, value.length);
        assertEquals(new TestRecord("test"), value[0]);
    }
}
