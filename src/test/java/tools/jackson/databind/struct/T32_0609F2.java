package tools.jackson.databind.struct;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0609F2 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] NAME_AND_LOCATION = VPackWireFixtureTest.hex(
            "14 13 44 6e 61 6d 65 44 74 65 73 74 41 78 31 41 79 32 03");
private static final byte[] PERSON_PRIMARY = VPackWireFixtureTest.hex(
            "14 2e 44 6e 61 6d 65 45 41 6c 69 63 65 47 6a 6f 62 4e 61 6d 65 " +
            "48 65 6e 67 69 6e 65 65 72 4a 6a 6f 62 41 64 64 72 65 73 73 " +
            "43 4e 59 43 03");
private static final byte[] PERSON_ALIAS = VPackWireFixtureTest.hex(
            "14 2b 41 6e 45 41 6c 69 63 65 47 6a 6f 62 4e 61 6d 65 " +
            "48 65 6e 67 69 6e 65 65 72 4a 6a 6f 62 41 64 64 72 65 73 73 " +
            "43 4e 59 43 03");
private static final byte[] ROOT_PERSON_PRIMARY = VPackWireFixtureTest.hex(
            "14 38 46 70 65 72 73 6f 6e 14 2e 44 6e 61 6d 65 45 41 6c 69 63 65 " +
            "47 6a 6f 62 4e 61 6d 65 48 65 6e 67 69 6e 65 65 72 " +
            "4a 6a 6f 62 41 64 64 72 65 73 73 43 4e 59 43 03 01");
private static final byte[] ROOT_PERSON_ALIAS = VPackWireFixtureTest.hex(
            "14 35 46 70 65 72 73 6f 6e 14 2b 41 6e 45 41 6c 69 63 65 " +
            "47 6a 6f 62 4e 61 6d 65 48 65 6e 67 69 6e 65 65 72 " +
            "4a 6a 6f 62 41 64 64 72 65 73 73 43 4e 59 43 03 01");
private static final byte[] RECORD_ALIAS = VPackWireFixtureTest.hex(
            "14 14 41 61 45 48 65 6c 6c 6f 41 62 46 57 6f 72 6c 64 21 02");
private static final byte[] SECOND_ALIAS = VPackWireFixtureTest.hex(
            "14 11 42 61 61 42 48 69 41 62 45 74 68 65 72 65 02");
private static final byte[] PRIMARY_NAME = VPackWireFixtureTest.hex(
            "14 10 41 63 46 64 69 72 65 63 74 41 62 41 78 02");

    // Provenance: UnwrappedWithAlias5911Test#testCreatorPojoAlias().
    void creatorPojoAliasVpack() throws Exception {
        CreatorOuter outer = MAPPER.readValue(RECORD_ALIAS, CreatorOuter.class);
        assertEquals("Hello", outer.inner.c);
        assertEquals("World!", outer.b);
    }

    // Provenance: UnwrappedWithAlias5911Test#testPojoAlias().
    void pojoAliasVpack() throws Exception {
        Outer outer = MAPPER.readValue(RECORD_ALIAS, Outer.class);
        assertEquals("Hello", outer.inner.c);
        assertEquals("World!", outer.b);
    }

    // Provenance: UnwrappedWithAlias5911Test#testPojoPrimaryName().
    void pojoPrimaryNameVpack() throws Exception {
        Outer outer = MAPPER.readValue(PRIMARY_NAME, Outer.class);
        assertEquals("direct", outer.inner.c);
        assertEquals("x", outer.b);
    }

    // Provenance: UnwrappedWithAlias5911Test#testPojoSecondAlias().
    void pojoSecondAliasVpack() throws Exception {
        Outer outer = MAPPER.readValue(SECOND_ALIAS, Outer.class);
        assertEquals("Hi", outer.inner.c);
        assertEquals("there", outer.b);
    }

    // Provenance: UnwrappedWithAlias5911Test#testRecordAlias().
    void recordAliasVpack() throws Exception {
        OuterRecord outer = MAPPER.readValue(RECORD_ALIAS, OuterRecord.class);
        assertEquals("Hello", outer.aOrC.c());
        assertEquals("World!", outer.b());
    }
private static final ObjectMapper ROOT_MAPPER = VPackMapper.builder()
            .enable(tools.jackson.databind.DeserializationFeature.UNWRAP_ROOT_VALUE)
            .build();
static class OuterConflict {
        public InnerB b = new InnerB();
        @JsonUnwrapped public InnerC c = new InnerC();
    }
static class InnerB { public int ba = 3; }
static class InnerC { public InnerD b = new InnerD(); }
static class InnerD { public int da = 4; }
static class OuterNoConflict {
        public InnerB b = new InnerB();
        @JsonUnwrapped(prefix = "c_") public InnerC c = new InnerC();
    }
@JsonRootName("person")
    static class Person {
        @JsonAlias("n") public String name;
        @JsonUnwrapped public Job job;
    }
static class Job { public String jobName; public String jobAddress; }
record AorC(@JsonAlias("a") String c) { }
record OuterRecord(@JsonUnwrapped AorC aOrC, String b) { }
static class InnerAlias {
        @JsonAlias({"a", "aa"}) public String c;
    }
static class Outer {
        @JsonUnwrapped public InnerAlias inner;
        public String b;
    }
static class CreatorInner {
        final String c;
        @JsonCreator CreatorInner(@JsonProperty("c") @JsonAlias("a") String c) { this.c = c; }
    }
static class CreatorOuter {
        @JsonUnwrapped public CreatorInner inner;
        public String b;
    }

    void __invoke_creatorPojoAliasVpack() throws Exception {
        try {
            creatorPojoAliasVpack();
        } finally {
        }
    }


    void __invoke_pojoAliasVpack() throws Exception {
        try {
            pojoAliasVpack();
        } finally {
        }
    }


    void __invoke_pojoPrimaryNameVpack() throws Exception {
        try {
            pojoPrimaryNameVpack();
        } finally {
        }
    }


    void __invoke_pojoSecondAliasVpack() throws Exception {
        try {
            pojoSecondAliasVpack();
        } finally {
        }
    }


    void __invoke_recordAliasVpack() throws Exception {
        try {
            recordAliasVpack();
        } finally {
        }
    }

}
