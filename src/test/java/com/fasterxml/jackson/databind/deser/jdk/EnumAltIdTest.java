package com.fasterxml.jackson.databind.deser.jdk;

import java.io.IOException;
import java.util.EnumSet;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.BaseMapTest;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.dataformat.velocypack.TestVelocypackMapper;

public class EnumAltIdTest extends BaseMapTest
{
    // [databind#1313]

    enum TestEnum { JACKSON, RULES, OK; }
    protected enum LowerCaseEnum {
        A, B, C;
        private LowerCaseEnum() { }
        @Override
        public String toString() { return name().toLowerCase(); }
    }

    protected static class EnumBean {
        @JsonFormat(with={ JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES })
        public TestEnum value;
    }

    protected static class StrictCaseBean {
        @JsonFormat(without={ JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES })
        public TestEnum value;
    }
    
    /*
    /**********************************************************
    /* Test methods, basic
    /**********************************************************
     */

    protected final ObjectMapper MAPPER = new TestVelocypackMapper();
    protected final ObjectMapper MAPPER_IGNORE_CASE = jsonMapperBuilder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .build();

    protected final ObjectReader READER_DEFAULT = MAPPER.reader();
    protected final ObjectReader READER_IGNORE_CASE = MAPPER_IGNORE_CASE.reader();

    // Tests for [databind#1313], case-insensitive

    public void testFailWhenCaseSensitiveAndNameIsNotUpperCase() throws IOException {
        try {
            READER_DEFAULT.forType(TestEnum.class).readValue(com.fasterxml.jackson.VPackUtils.toVPack("\"Jackson\""));
            fail("InvalidFormatException expected");
        } catch (InvalidFormatException e) {
            verifyException(e, "not one of the values accepted for Enum class");
            verifyException(e, "[JACKSON, OK, RULES]");
        }
    }
    
    public void testFailWhenCaseSensitiveAndToStringIsUpperCase() throws IOException {
        ObjectReader r = READER_DEFAULT.forType(LowerCaseEnum.class)
                .with(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        try {
            r.readValue(com.fasterxml.jackson.VPackUtils.toVPack("\"A\""));
            fail("InvalidFormatException expected");
        } catch (InvalidFormatException e) {
            verifyException(e, "not one of the values accepted for Enum class");
            verifyException(e,"[a, b, c]");
        }
    }

    public void testEnumDesIgnoringCaseWithLowerCaseContent() throws IOException {
        assertEquals(TestEnum.JACKSON,
                READER_IGNORE_CASE.forType(TestEnum.class).readValue(com.fasterxml.jackson.VPackUtils.toVPack(quote("jackson"))));
    }

    public void testEnumDesIgnoringCaseWithUpperCaseToString() throws IOException {
        ObjectReader r = MAPPER_IGNORE_CASE.readerFor(LowerCaseEnum.class)
                .with(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        assertEquals(LowerCaseEnum.A, r.readValue(com.fasterxml.jackson.VPackUtils.toVPack("\"A\"")));
    }

    /*
    /**********************************************************
    /* Test methods, containers
    /**********************************************************
     */

    public void testIgnoreCaseInEnumList() throws Exception {
        TestEnum[] enums = READER_IGNORE_CASE.forType(TestEnum[].class)
            .readValue(com.fasterxml.jackson.VPackUtils.toVPack("[\"jacksON\", \"ruLes\"]"));

        assertEquals(2, enums.length);
        assertEquals(TestEnum.JACKSON, enums[0]);
        assertEquals(TestEnum.RULES, enums[1]);
    }

    public void testIgnoreCaseInEnumSet() throws IOException {
        ObjectReader r = READER_IGNORE_CASE.forType(new TypeReference<EnumSet<TestEnum>>() { });
        EnumSet<TestEnum> set = r.readValue(com.fasterxml.jackson.VPackUtils.toVPack("[\"jackson\"]"));
        assertEquals(1, set.size());
        assertTrue(set.contains(TestEnum.JACKSON));
    }

    /*
    /**********************************************************
    /* Test methods, property overrides
    /**********************************************************
     */

    public void testIgnoreCaseViaFormat() throws Exception
    {
        final String JSON = aposToQuotes("{'value':'ok'}");

        // should be able to allow on per-case basis:
        EnumBean pojo = READER_DEFAULT.forType(EnumBean.class)
            .readValue(com.fasterxml.jackson.VPackUtils.toVPack(JSON));
        assertEquals(TestEnum.OK, pojo.value);

        // including disabling acceptance
        try {
            READER_DEFAULT.forType(StrictCaseBean.class)
                    .readValue(com.fasterxml.jackson.VPackUtils.toVPack(JSON));
            fail("Should not pass");
        } catch (InvalidFormatException e) {
            verifyException(e, "not one of the values accepted for Enum class");
            verifyException(e, "[JACKSON, OK, RULES]");
        }
    }
}
