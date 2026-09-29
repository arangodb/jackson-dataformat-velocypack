package tools.jackson.databind.records;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonValue;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.testutil.DatabindTestUtil;
import com.arangodb.jackson.dataformat.velocypack.VPackMapper;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RecordWithVPackIncludeTest extends DatabindTestUtil
{
    // Basic @JsonInclude
    public record AnnotatedParamRecordClass(
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String omitFieldIfNull,
        String standardField
    ) { }

    public record AnnotatedGetterRecordClass(
        String omitFieldIfNull,
        String standardField
    ) {
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @Override
        public String omitFieldIfNull() {
            return omitFieldIfNull;
        }
    }

    // [databind#4629], [databind#4630]
    public record Id2Name(int id, String name) { }

    // [databind#4629]
    public record RecordWithInclude4629(
            @JsonIncludeProperties("id") Id2Name child
    ) { }

    public record RecordWithIgnore4629(
            @JsonIgnoreProperties("name") Id2Name child
    ) { }

    // [databind#4630]
    public record RecordWithJsonIncludeProperties(@JsonIncludeProperties("id") Id2Name child) {
        @Override
        public Id2Name child() {
            return child;
        }
    }

    public record RecordWithJsonIgnoreProperties(@JsonIgnoreProperties("name") Id2Name child) {
        @Override
        public Id2Name child() {
            return child;
        }
    }

    // [databind#5312]
    record StringValue5312(String value) {
        @Override
        @JsonValue
        public String value() {
            return value;
        }
    }

    record Pojo1_5312(StringValue5312 value) { }

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    record Pojo2_5312(StringValue5312 value) { }

    record Pojo3_5312(@JsonInclude(JsonInclude.Include.NON_DEFAULT) StringValue5312 value) { }

    // Record with user-added 0-arg "default" constructor
    record Pojo4_5312(StringValue5312 value) {
        Pojo4_5312() { this(null); }
    }

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    record Pojo5_5312(StringValue5312 value) {
        Pojo5_5312() { this(null); }
    }

    private final ObjectMapper MAPPER = newVPackMapper();

    private final ObjectMapper MAPPER_5312 = VPackMapper.builder()
            .changeDefaultPropertyInclusion(incl -> JsonInclude.Value.construct(NON_DEFAULT, NON_DEFAULT))
            .withConfigOverride(String.class,
                    o -> o.setInclude(JsonInclude.Value.construct(NON_NULL, NON_NULL)))
            .build();

    /*
    /**********************************************************************
    /* Test methods, @JsonInclude on record parameters/getters
    /**********************************************************************
     */

    @Test
    public void testJsonIncludeOnRecordParam() throws Exception
    {
        assertEquals(a2q("{'standardField':'def'}"),
            VPackUtils.toJson(MAPPER.writeValueAsBytes(new AnnotatedParamRecordClass(null, "def"))));
        assertEquals(a2q("{'standardField':'def'}"),
            VPackUtils.toJson(MAPPER.writeValueAsBytes(new AnnotatedGetterRecordClass(null, "def"))));
    }

    /*
    /**********************************************************************
    /* Test methods, @JsonIncludeProperties/@JsonIgnoreProperties on fields [databind#4629]
    /**********************************************************************
     */

    // [databind#4629]
    @Test
    void testJsonIncludeProperties4629() throws Exception
    {
        RecordWithInclude4629 expected = new RecordWithInclude4629(new Id2Name(123, null));
        String input = "{\"child\":{\"id\":123,\"name\":\"Bob\"}}";

        RecordWithInclude4629 actual = MAPPER.readValue(VPackUtils.toVPack(input), RecordWithInclude4629.class);

        assertEquals(expected, actual);
    }

    @Test
    void testJsonIgnoreProperties4629() throws Exception
    {
        RecordWithIgnore4629 expected = new RecordWithIgnore4629(new Id2Name(123, null));
        String input = "{\"child\":{\"id\":123,\"name\":\"Bob\"}}";

        RecordWithIgnore4629 actual = MAPPER.readValue(VPackUtils.toVPack(input), RecordWithIgnore4629.class);

        assertEquals(expected, actual);
    }

    /*
    /**********************************************************************
    /* Test methods, @JsonIncludeProperties/@JsonIgnoreProperties with overridden accessor [databind#4630]
    /**********************************************************************
     */

    // [databind#4630]
    @Test
    public void testSerializeJsonIncludeProperties4630() throws Exception {
        String json = VPackUtils.toJson(MAPPER.writeValueAsBytes(new RecordWithJsonIncludeProperties(new Id2Name(123, "Bob"))));
        assertEquals(a2q("{'child':{'id':123}}"), json);
    }

    @Test
    public void testSerializeJsonIgnoreProperties4630() throws Exception {
        String json = VPackUtils.toJson(MAPPER.writeValueAsBytes(new RecordWithJsonIgnoreProperties(new Id2Name(123, "Bob"))));
        assertEquals(a2q("{'child':{'id':123}}"), json);
    }

    /*
    /**********************************************************************
    /* Test methods, Include.NON_DEFAULT with @JsonValue [databind#5312]
    /**********************************************************************
     */

    // [databind#5312]
    @Test
    void testSerialization5312_1() throws Exception {
        assertEquals("{\"value\":\"\"}",
                VPackUtils.toJson(MAPPER_5312.writeValueAsBytes(new Pojo1_5312(new StringValue5312("")))));
    }

    // [databind#5312]
    @Test
    void testSerialization5312_2() throws Exception {
        assertEquals("{\"value\":\"\"}",
                VPackUtils.toJson(MAPPER_5312.writeValueAsBytes(new Pojo2_5312(new StringValue5312("")))));
    }

    // [databind#5312]
    @Test
    void testSerialization5312_3() throws Exception {
        assertEquals("{\"value\":\"\"}",
                VPackUtils.toJson(MAPPER_5312.writeValueAsBytes(new Pojo3_5312(new StringValue5312("")))));
    }

    // [databind#5312]: Record with user-added 0-arg constructor, global NON_DEFAULT
    @Test
    void testSerialization5312WithDefaultCtor() throws Exception {
        // Non-null value should be included
        assertEquals("{\"value\":\"\"}",
                VPackUtils.toJson(MAPPER_5312.writeValueAsBytes(new Pojo4_5312(new StringValue5312("")))));
        // Null value should be suppressed (matches default from 0-arg ctor)
        assertEquals("{}",
                VPackUtils.toJson(MAPPER_5312.writeValueAsBytes(new Pojo4_5312(null))));
    }

    // [databind#5312]: Record with user-added 0-arg constructor, class-level NON_DEFAULT
    @Test
    void testSerialization5312WithDefaultCtorClassLevel() throws Exception {
        // Non-null value should be included
        assertEquals("{\"value\":\"\"}",
                VPackUtils.toJson(MAPPER_5312.writeValueAsBytes(new Pojo5_5312(new StringValue5312("")))));
        // Null value should be suppressed (matches default from 0-arg ctor)
        assertEquals("{}",
                VPackUtils.toJson(MAPPER_5312.writeValueAsBytes(new Pojo5_5312(null))));
    }
}
