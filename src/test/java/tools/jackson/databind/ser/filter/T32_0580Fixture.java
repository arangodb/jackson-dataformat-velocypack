package tools.jackson.databind.ser.filter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.cfg.DateTimeFeature;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0580Fixture {
private static final ObjectMapper MAPPER = new VPackMapper();

    void testGlobalVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0c 02 41 61 41 61 41 62 18 03 07"),
                MAPPER.writeValueAsBytes(new SimpleBean()));
    }

    void testDefaultForEmptyListVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                MAPPER.writeValueAsBytes(new ListBean()));
    }

    void testDefaultForIntegersVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(MapperFeature.WRAPPERS_DEFAULT_TO_NULL)
                .build();
        assertArrayEquals(VPackWireFixtureTest.hex("0b 08 01 42 69 32 30 03"),
                mapper.writeValueAsBytes(new DefaultIntBean(0, Integer.valueOf(0))));
        assertArrayEquals(VPackWireFixtureTest.hex("0b 08 01 42 69 32 31 03"),
                mapper.writeValueAsBytes(new DefaultIntBean(0, Integer.valueOf(1))));
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                mapper.writeValueAsBytes(new DefaultIntBean(0, null)));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0d 02 42 69 31 33 42 69 32 30 03 07"),
                mapper.writeValueAsBytes(new DefaultIntBean(3, Integer.valueOf(0))));
        assertArrayEquals(VPackWireFixtureTest.hex("0b 08 01 42 69 31 33 03"),
                mapper.writeValueAsBytes(new DefaultIntBean(3, null)));

        mapper = VPackMapper.builder()
                .disable(MapperFeature.WRAPPERS_DEFAULT_TO_NULL)
                .build();
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                mapper.writeValueAsBytes(new DefaultIntBean(0, Integer.valueOf(0))));
        assertArrayEquals(VPackWireFixtureTest.hex("0b 08 01 42 69 32 31 03"),
                mapper.writeValueAsBytes(new DefaultIntBean(0, Integer.valueOf(1))));
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                mapper.writeValueAsBytes(new DefaultIntBean(0, null)));
        assertArrayEquals(VPackWireFixtureTest.hex("0b 08 01 42 69 31 33 03"),
                mapper.writeValueAsBytes(new DefaultIntBean(3, Integer.valueOf(0))));
        assertArrayEquals(VPackWireFixtureTest.hex("0b 08 01 42 69 31 33 03"),
                mapper.writeValueAsBytes(new DefaultIntBean(3, null)));
    }

    void testDefaultForBooleansVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(MapperFeature.WRAPPERS_DEFAULT_TO_NULL)
                .build();
        assertArrayEquals(VPackWireFixtureTest.hex("0b 08 01 42 62 32 19 03"),
                mapper.writeValueAsBytes(new DefaultBooleanBean(false, Boolean.FALSE)));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0d 02 42 62 31 1a 42 62 32 1a 03 07"),
                mapper.writeValueAsBytes(new DefaultBooleanBean(true, Boolean.TRUE)));
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                mapper.writeValueAsBytes(new DefaultBooleanBean(false, null)));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0d 02 42 62 31 1a 42 62 32 19 03 07"),
                mapper.writeValueAsBytes(new DefaultBooleanBean(true, Boolean.FALSE)));
    }

    void testIssue5570BooleanWrapperVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(MapperFeature.WRAPPERS_DEFAULT_TO_NULL)
                .changeDefaultPropertyInclusion(incl ->
                        incl.withValueInclusion(JsonInclude.Include.NON_DEFAULT))
                .build();
        Issue5570Bean bean = new Issue5570Bean();
        bean.setValue(Boolean.FALSE);
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0b 01 45 76 61 6c 75 65 19 03"),
                mapper.writeValueAsBytes(bean));
        bean.setValue(null);
        assertArrayEquals(VPackWireFixtureTest.hex("0a"), mapper.writeValueAsBytes(bean));
        bean.setValue(Boolean.TRUE);
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0b 01 45 76 61 6c 75 65 1a 03"),
                mapper.writeValueAsBytes(bean));
    }

    void testEmptyInclusionScalarsVpack() throws Exception {
        ObjectMapper inclMapper = VPackMapper.builder()
                .changeDefaultPropertyInclusion(incl ->
                        incl.withValueInclusion(JsonInclude.Include.NON_EMPTY))
                .build();

        assertArrayEquals(VPackWireFixtureTest.hex("0b 09 01 43 73 74 72 40 03"),
                MAPPER.writeValueAsBytes(new StringWrapper("")));
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                inclMapper.writeValueAsBytes(new StringWrapper("")));
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                inclMapper.writeValueAsBytes(new StringWrapper()));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0c 01 45 76 61 6c 75 65 41 78 03"),
                MAPPER.writeValueAsBytes(new NonEmptyString("x")));
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                MAPPER.writeValueAsBytes(new NonEmptyString("")));

        assertArrayEquals(VPackWireFixtureTest.hex("0b 0c 01 45 76 61 6c 75 65 28 0c 03"),
                MAPPER.writeValueAsBytes(new NonEmptyInt(12)));
        assertArrayEquals(VPackWireFixtureTest.hex("0b 0b 01 45 76 61 6c 75 65 30 03"),
                MAPPER.writeValueAsBytes(new NonEmptyInt(0)));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 13 01 45 76 61 6c 75 65 1b 00 00 00 00 00 00 f4 3f 03"),
                MAPPER.writeValueAsBytes(new NonEmptyDouble(1.25)));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 13 01 45 76 61 6c 75 65 1b 00 00 00 00 00 00 00 00 03"),
                MAPPER.writeValueAsBytes(new NonEmptyDouble(0.0)));

        assertArrayEquals(VPackWireFixtureTest.hex("0b 07 01 41 69 30 03"),
                MAPPER.writeValueAsBytes(new IntWrapper(0)));
        assertArrayEquals(VPackWireFixtureTest.hex("0b 07 01 41 69 30 03"),
                inclMapper.writeValueAsBytes(new IntWrapper(0)));
    }

    void test1327ClassDefaultsForEmptyVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .changeDefaultPropertyInclusion(incl ->
                        incl.withValueInclusion(JsonInclude.Include.NON_NULL))
                .build();
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                mapper.writeValueAsBytes(new Issue1327BeanEmpty()));
    }

    void test1327ClassDefaultsForAlwaysVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .changeDefaultPropertyInclusion(incl ->
                        incl.withValueInclusion(JsonInclude.Include.NON_EMPTY))
                .build();
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0c 01 46 6d 79 4c 69 73 74 01 03"),
                mapper.writeValueAsBytes(new Issue1327BeanAlways()));
    }

    void testIssue1351Vpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .changeDefaultPropertyInclusion(incl ->
                        incl.withValueInclusion(JsonInclude.Include.NON_DEFAULT))
                .build();
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                mapper.writeValueAsBytes(new Issue1351Bean(null, 0.0)));
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                mapper.writeValueAsBytes(new Issue1351NonBean(0)));
    }

    void testInclusionOfDateVpack() throws Exception {
        ObjectWriter writer = MAPPER.writer()
                .with(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS);
        assertArrayEquals(VPackWireFixtureTest.hex("0b 0b 01 45 76 61 6c 75 65 30 03"),
                writer.writeValueAsBytes(new NonEmptyDate(new Date(0L))));
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                writer.writeValueAsBytes(new NonDefaultDate(new Date(0L))));
    }

    void testInclusionOfCalendarVpack() throws Exception {
        Calendar input = new GregorianCalendar();
        input.setTimeInMillis(0L);
        ObjectWriter writer = MAPPER.writer()
                .with(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS);
        assertArrayEquals(VPackWireFixtureTest.hex("0b 0b 01 45 76 61 6c 75 65 30 03"),
                writer.writeValueAsBytes(new NonEmptyCalendar(input)));
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                writer.writeValueAsBytes(new NonDefaultCalendar(input)));
    }

    void testInclusionOfUUIDVpack() throws Exception {
        UUID nullUUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
        UUID nonNullUUID = UUID.fromString("540a88d1-e2d8-4fb1-9396-9212280d0a7f");
        byte[] nonNull = VPackWireFixtureTest.hex(
                "0b 1c 01 45 76 61 6c 75 65 c0 10 54 0a 88 d1 e2 d8 4f b1 93 96 92 12 28 0d 0a 7f 03");
        assertArrayEquals(nonNull,
                MAPPER.writeValueAsBytes(new NonEmptyUUID(nonNullUUID)));
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                MAPPER.writeValueAsBytes(new NonEmptyUUID(nullUUID)));
        assertArrayEquals(nonNull,
                MAPPER.writeValueAsBytes(new NonDefaultUUID(nonNullUUID)));
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                MAPPER.writeValueAsBytes(new NonDefaultUUID(nullUUID)));
    }
static class SimpleBean {
        public String getA() { return "a"; }
        public String getB() { return null; }
    }
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
    static class ListBean {
        public List<String> strings = new ArrayList<>();
    }
@JsonPropertyOrder({"i1", "i2"})
    static class DefaultIntBean {
        @JsonInclude(JsonInclude.Include.NON_DEFAULT)
        public int i1;
        @JsonInclude(JsonInclude.Include.NON_DEFAULT)
        public Integer i2;
        DefaultIntBean(int i1, Integer i2) { this.i1 = i1; this.i2 = i2; }
    }
@JsonPropertyOrder({"b1", "b2"})
    static class DefaultBooleanBean {
        @JsonInclude(JsonInclude.Include.NON_DEFAULT)
        public boolean b1;
        @JsonInclude(JsonInclude.Include.NON_DEFAULT)
        public Boolean b2;
        DefaultBooleanBean(boolean b1, Boolean b2) { this.b1 = b1; this.b2 = b2; }
    }
static class Issue5570Bean {
        private Boolean value;
        public Boolean getValue() { return value; }
        public void setValue(Boolean value) { this.value = value; }
    }
static class StringWrapper {
        public String str;
        StringWrapper() { }
        StringWrapper(String value) { str = value; }
    }
static class NonEmptyString {
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public String value;
        NonEmptyString(String value) { this.value = value; }
    }
static class NonEmptyInt {
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public int value;
        NonEmptyInt(int value) { this.value = value; }
    }
static class NonEmptyDouble {
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public double value;
        NonEmptyDouble(double value) { this.value = value; }
    }
static class IntWrapper {
        public int i;
        IntWrapper(int value) { i = value; }
    }
@JsonInclude(JsonInclude.Include.NON_EMPTY)
    static class Issue1327BeanEmpty {
        public List<String> myList = new ArrayList<>();
    }
static class Issue1327BeanAlways {
        @JsonInclude(JsonInclude.Include.ALWAYS)
        public List<String> myList = new ArrayList<>();
    }
static class Issue1351Bean {
        public final String first;
        public final double second;
        Issue1351Bean(String first, double second) { this.first = first; this.second = second; }
    }
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
    static abstract class Issue1351NonBeanParent {
        protected final int num;
        Issue1351NonBeanParent(int num) { this.num = num; }
        public int getNum() { return num; }
    }
static class Issue1351NonBean extends Issue1351NonBeanParent {
        Issue1351NonBean(int num) { super(num); }
        public String getStr() { return null; }
    }
static class NonEmptyDate {
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public Date value;
        NonEmptyDate(Date value) { this.value = value; }
    }
static class NonDefaultDate {
        @JsonInclude(JsonInclude.Include.NON_DEFAULT)
        public Date value;
        NonDefaultDate(Date value) { this.value = value; }
    }
static class NonEmptyCalendar {
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public Calendar value;
        NonEmptyCalendar(Calendar value) { this.value = value; }
    }
static class NonDefaultCalendar {
        @JsonInclude(JsonInclude.Include.NON_DEFAULT)
        public Calendar value;
        NonDefaultCalendar(Calendar value) { this.value = value; }
    }
static class NonEmptyUUID {
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public UUID value;
        NonEmptyUUID(UUID value) { this.value = value; }
    }
static class NonDefaultUUID {
        @JsonInclude(JsonInclude.Include.NON_DEFAULT)
        public UUID value;
        NonDefaultUUID(UUID value) { this.value = value; }
    }

    void __invoke_testGlobalVpack() throws Exception {
        try {
            testGlobalVpack();
        } finally {
        }
    }


    void __invoke_testDefaultForEmptyListVpack() throws Exception {
        try {
            testDefaultForEmptyListVpack();
        } finally {
        }
    }


    void __invoke_testDefaultForIntegersVpack() throws Exception {
        try {
            testDefaultForIntegersVpack();
        } finally {
        }
    }


    void __invoke_testDefaultForBooleansVpack() throws Exception {
        try {
            testDefaultForBooleansVpack();
        } finally {
        }
    }


    void __invoke_testIssue5570BooleanWrapperVpack() throws Exception {
        try {
            testIssue5570BooleanWrapperVpack();
        } finally {
        }
    }


    void __invoke_testEmptyInclusionScalarsVpack() throws Exception {
        try {
            testEmptyInclusionScalarsVpack();
        } finally {
        }
    }


    void __invoke_test1327ClassDefaultsForEmptyVpack() throws Exception {
        try {
            test1327ClassDefaultsForEmptyVpack();
        } finally {
        }
    }


    void __invoke_test1327ClassDefaultsForAlwaysVpack() throws Exception {
        try {
            test1327ClassDefaultsForAlwaysVpack();
        } finally {
        }
    }


    void __invoke_testIssue1351Vpack() throws Exception {
        try {
            testIssue1351Vpack();
        } finally {
        }
    }


    void __invoke_testInclusionOfDateVpack() throws Exception {
        try {
            testInclusionOfDateVpack();
        } finally {
        }
    }


    void __invoke_testInclusionOfCalendarVpack() throws Exception {
        try {
            testInclusionOfCalendarVpack();
        } finally {
        }
    }


    void __invoke_testInclusionOfUUIDVpack() throws Exception {
        try {
            testInclusionOfUUIDVpack();
        } finally {
        }
    }

}
