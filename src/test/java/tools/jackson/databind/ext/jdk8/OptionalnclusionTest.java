package tools.jackson.databind.ext.jdk8;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.testutil.DatabindTestUtil;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class OptionalnclusionTest
    extends DatabindTestUtil
{
    @JsonAutoDetect(fieldVisibility=Visibility.ANY)
    public static final class OptionalData {
        public Optional<String> myString = Optional.empty();
    }

    // for [datatype-jdk8#18]
    static class OptionalNonEmptyStringBean {
        @JsonInclude(value=Include.NON_EMPTY, content=Include.NON_EMPTY)
        public Optional<String> value;

        public OptionalNonEmptyStringBean() { }
        OptionalNonEmptyStringBean(String str) {
            value = Optional.ofNullable(str);
        }
    }

    public static final class OptionalGenericData<T> {
        public Optional<T> myData;
        public static <T> OptionalGenericData<T> construct(T data) {
            OptionalGenericData<T> ret = new OptionalGenericData<T>();
            ret.myData = Optional.of(data);
            return ret;
        }
    }

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    private final ObjectMapper MAPPER = newVPackMapper();

    @Test
    public void testSerOptNonEmpty() throws Exception
    {
        OptionalData data = new OptionalData();
        data.myString = null;
        String value = VPackUtils.toJson(vpackMapperBuilder()
                .changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(Include.NON_EMPTY))
                .build()
                .writeValueAsBytes(data));
        assertEquals("{}", value);
    }

    @Test
    public void testSerOptNonDefault() throws Exception
    {
        OptionalData data = new OptionalData();
        data.myString = null;
        String value = VPackUtils.toJson(vpackMapperBuilder()
                .changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(Include.NON_DEFAULT))
                .build()
                .writeValueAsBytes(data));
        assertEquals("{}", value);
    }

    @Test
    public void testSerOptNonAbsent() throws Exception
    {
        OptionalData data = new OptionalData();
        data.myString = null;
        String value = VPackUtils.toJson(vpackMapperBuilder()
                .changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(Include.NON_ABSENT))
                .build()
                .writeValueAsBytes(data));
        assertEquals("{}", value);
    }

    @Test
    public void testExcludeEmptyStringViaOptional() throws Exception
    {
        String json = VPackUtils.toJson(MAPPER.writeValueAsBytes(new OptionalNonEmptyStringBean("x")));
        assertEquals("{\"value\":\"x\"}", json);
        json = VPackUtils.toJson(MAPPER.writeValueAsBytes(new OptionalNonEmptyStringBean(null)));
        assertEquals("{}", json);
        json = VPackUtils.toJson(MAPPER.writeValueAsBytes(new OptionalNonEmptyStringBean("")));
        assertEquals("{}", json);
    }

    @Test
    public void testSerPropInclusionAlways() throws Exception
    {
        ObjectMapper mapper = vpackMapperBuilder()
                .changeDefaultPropertyInclusion(incl ->
                    JsonInclude.Value.construct(Include.NON_ABSENT, Include.ALWAYS))
                .build();
        assertEquals("{\"myData\":true}",
                VPackUtils.toJson(mapper.writeValueAsBytes(OptionalGenericData.construct(Boolean.TRUE))));
    }

    @Test
    public void testSerPropInclusionNonNull() throws Exception
    {
        ObjectMapper mapper = vpackMapperBuilder().changeDefaultPropertyInclusion(
                    i -> JsonInclude.Value.construct(Include.NON_ABSENT, Include.NON_NULL))
                .build();
        assertEquals("{\"myData\":true}",
                VPackUtils.toJson(mapper.writeValueAsBytes(OptionalGenericData.construct(Boolean.TRUE))));
    }

    @Test
    public void testSerPropInclusionNonAbsent() throws Exception
    {
        ObjectMapper mapper = vpackMapperBuilder()
                .changeDefaultPropertyInclusion(
                        i -> JsonInclude.Value.construct(Include.NON_ABSENT, Include.NON_ABSENT))
                .build();
        assertEquals("{\"myData\":true}",
                VPackUtils.toJson(mapper.writeValueAsBytes(OptionalGenericData.construct(Boolean.TRUE))));
    }

    @Test
    public void testSerPropInclusionNonEmpty() throws Exception
    {
        ObjectMapper mapper = vpackMapperBuilder()
                .changeDefaultPropertyInclusion(
                        i -> JsonInclude.Value.construct(Include.NON_ABSENT, Include.NON_EMPTY))
                .build();
        assertEquals("{\"myData\":true}",
                VPackUtils.toJson(mapper.writeValueAsBytes(OptionalGenericData.construct(Boolean.TRUE))));
    }
}
