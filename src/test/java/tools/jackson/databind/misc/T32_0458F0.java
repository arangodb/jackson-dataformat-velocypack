package tools.jackson.databind.misc;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.UnrecognizedPropertyException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0458F0 {
private static final byte[] CASE_INSENSITIVE_1036 = VPackWireFixtureTest.hex(
            "14 30 49 45 72 72 6f 72 43 6f 64 65 32 "
          + "4c 44 65 62 75 67 4d 65 73 73 61 67 65 54 53 69 67 6e 61 74 75 72 65 "
          + "20 6e 6f 74 20 76 61 6c 69 64 21 02");
private static final byte[] CASE_INSENSITIVE_NESTED = VPackWireFixtureTest.hex(
            "14 43 "
          + "46 56 61 6c 75 65 31 14 1a 44 6e 41 6d 65 45 66 72 75 69 74 "
          + "45 76 41 4c 55 65 45 61 70 70 6c 65 02 "
          + "46 76 61 6c 55 45 32 14 18 44 4e 41 4d 45 45 63 6f 6c 6f 72 "
          + "45 76 61 6c 75 65 43 72 65 64 02 02");
private static final byte[] ROLE_LOWER = VPackWireFixtureTest.hex(
            "14 12 42 69 64 42 31 32 44 6e 61 6d 65 43 46 6f 6f 02");
private static final byte[] ROLE_WRAPPER = VPackWireFixtureTest.hex(
            "14 1a 44 72 6f 6c 65 14 12 42 69 64 42 31 32 "
          + "44 6e 61 6d 65 43 46 6f 6f 02 01");
private static final byte[] CREATOR_UPPER = VPackWireFixtureTest.hex(
            "14 0a 45 56 41 4c 55 45 33 01");
private static final byte[] ISSUE_1854 = VPackWireFixtureTest.hex(
            "14 1d 42 49 44 31 45 49 74 65 6d 73 13 10 "
          + "14 0d 47 43 68 69 6c 64 49 44 20 0a 01 01 02");
private static final byte[] IPHONE_INPUT = VPackWireFixtureTest.hex(
            "14 14 46 69 50 68 6f 6e 65 49 69 50 68 6f 6e 65 20 31 35 01");
private static final byte[] IPHONE_OUTPUT = VPackWireFixtureTest.hex(
            "0b 15 01 46 69 50 68 6f 6e 65 49 69 50 68 6f 6e 65 20 31 35 03");
private static final byte[] DLOG_INPUT = VPackWireFixtureTest.hex(
            "14 1f 4a 44 4c 6f 67 48 65 61 64 65 72 50 44 65 62 75 67 20 4c 6f 67 20 "
          + "48 65 61 64 65 72 01");
private static final byte[] DLOG_OUTPUT = VPackWireFixtureTest.hex(
            "0b 20 01 4a 44 4c 6f 67 48 65 61 64 65 72 50 44 65 62 75 67 20 4c 6f 67 "
          + "20 48 65 61 64 65 72 03");
private static final byte[] KBS_INPUT = VPackWireFixtureTest.hex(
            "14 2e 4f 4b 42 53 42 72 6f 61 64 43 61 73 74 69 6e 67 "
          + "5a 4b 6f 72 65 61 6e 20 42 72 6f 61 64 63 61 73 74 69 6e 67 20 53 79 73 74 65 6d 01");
private static final byte[] KBS_OUTPUT = VPackWireFixtureTest.hex(
            "0b 2f 01 4f 4b 42 53 42 72 6f 61 64 43 61 73 74 69 6e 67 "
          + "5a 4b 6f 72 65 61 6e 20 42 72 6f 61 64 63 61 73 74 69 6e 67 20 53 79 73 74 65 6d 03");
private static final byte[] PHONE_INPUT = VPackWireFixtureTest.hex(
            "14 13 45 50 68 6f 6e 65 49 69 50 68 6f 6e 65 20 31 35 01");
private static final byte[] PHONE_OUTPUT = VPackWireFixtureTest.hex(
            "0b 14 01 45 50 68 6f 6e 65 49 69 50 68 6f 6e 65 20 31 35 03");
private static final byte[] OAUTH_OUTPUT = VPackWireFixtureTest.hex(
            "0b 13 01 4a 6f 41 75 74 68 54 6f 6b 65 6e 43 31 32 33 03");
private static final ObjectMapper MAPPER = VPackMapper.builder()
            .enable(MapperFeature.FIX_FIELD_NAME_UPPER_CASE_PREFIX)
            .build();
private static final ObjectMapper INSENSITIVE_MAPPER = VPackMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
            .build();

    // Provenance: CaseInsensitiveDeserTest#testCaseInsensitive1036().
    void testCaseInsensitive1036Vpack() throws Exception {
        BaseResponse458 response = INSENSITIVE_MAPPER.readValue(
                CASE_INSENSITIVE_1036, BaseResponse458.class);
        assertEquals(2, response.errorCode);
        assertEquals("Signature not valid!", response.debugMessage);
    }

    // Provenance: CaseInsensitiveDeserTest#testCaseInsensitiveDeserialization().
    void testCaseInsensitiveDeserializationVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).build();
        assertFalse(mapper.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES));
        assertThrows(UnrecognizedPropertyException.class,
                () -> mapper.readValue(CASE_INSENSITIVE_NESTED, Issue476Bean458.class));

        Issue476Bean458 result = INSENSITIVE_MAPPER.readValue(
                CASE_INSENSITIVE_NESTED, Issue476Bean458.class);
        assertEquals("fruit", result.value1.name);
        assertEquals("apple", result.value1.value);
        assertEquals("color", result.value2.name);
        assertEquals("red", result.value2.value);
    }

    // Provenance: CaseInsensitiveDeserTest#testCaseInsensitiveWithFormat().
    void testCaseInsensitiveWithFormatVpack() throws Exception {
        CaseInsensitiveRoleWrapper458 result = MAPPER.readValue(
                ROLE_WRAPPER, CaseInsensitiveRoleWrapper458.class);
        assertNotNull(result);
        assertEquals("12", result.role.ID);
        assertEquals("Foo", result.role.Name);
    }

    // Provenance: CaseInsensitiveDeserTest#testCreatorWithInsensitive().
    void testCreatorWithInsensitiveVpack() throws Exception {
        InsensitiveCreator458 bean = INSENSITIVE_MAPPER.readValue(
                CREATOR_UPPER, InsensitiveCreator458.class);
        assertEquals(3, bean.v);
    }

    // Provenance: CaseInsensitiveDeserTest#testCaseInsensitiveViaConfigOverride().
    void testCaseInsensitiveViaConfigOverrideVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .withConfigOverride(Role458.class,
                        o -> o.setFormat(JsonFormat.Value.empty().withFeature(
                                JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)))
                .build();
        Role458 role = mapper.readValue(ROLE_LOWER, Role458.class);
        assertNotNull(role);
        assertEquals("12", role.ID);
        assertEquals("Foo", role.Name);
    }

    // Provenance: CaseInsensitiveDeserTest#testIssue1854().
    void testIssue1854Vpack() throws Exception {
        Obj1854_458 result = INSENSITIVE_MAPPER.readValue(ISSUE_1854, Obj1854_458.class);
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertNotNull(result.getItems());
        assertEquals(1, result.getItems().size());
    }

    // Provenance: CaseInsensitiveDeserTest#testCaseInsensitiveViaClassAnnotation().
    void testCaseInsensitiveViaClassAnnotationVpack() throws Exception {
        CaseInsensitiveRoleContainer458 container = MAPPER.readValue(
                ROLE_WRAPPER, CaseInsensitiveRoleContainer458.class);
        assertEquals("12", container.role.ID);
        assertEquals("Foo", container.role.Name);

        CaseInsensitiveRole458 role = MAPPER.readValue(ROLE_LOWER, CaseInsensitiveRole458.class);
        assertEquals("12", role.ID);
        assertEquals("Foo", role.Name);

        ObjectMapper strict = VPackMapper.builder()
                .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).build();
        assertThrows(UnrecognizedPropertyException.class,
                () -> strict.readValue(ROLE_WRAPPER, CaseSensitiveRoleContainer458.class));
    }
static class BaseResponse458 {
        public int errorCode;
        public String debugMessage;
    }
static class Issue476Bean458 {
        public Issue476Type458 value1, value2;
    }
static class Issue476Type458 {
        public String name, value;
    }
static class Role458 {
        public String ID;
        public String Name;
    }
static class CaseInsensitiveRoleWrapper458 {
        @JsonFormat(with = { JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES })
        public Role458 role;
    }
static class InsensitiveCreator458 {
        int v;

        @JsonCreator
        public InsensitiveCreator458(@JsonProperty("value") int v0) {
            v = v0;
        }
    }
@JsonFormat(with = { JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES })
    static class CaseInsensitiveRole458 {
        public String ID;
        public String Name;
    }
static class CaseInsensitiveRoleContainer458 {
        public CaseInsensitiveRole458 role;
    }
static class CaseSensitiveRoleContainer458 {
        @JsonFormat(without = { JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES })
        public CaseInsensitiveRole458 role;
    }
static class Obj1854_458 {
        private final int id;
        private final List<ChildObj1854_458> items;

        public Obj1854_458(int id, List<ChildObj1854_458> items) {
            this.id = id;
            this.items = items;
        }

        @JsonCreator
        public static Obj1854_458 fromJson(@JsonProperty("ID") int id,
                @JsonProperty("Items") List<ChildObj1854_458> items) {
            return new Obj1854_458(id, items);
        }

        public int getId() { return id; }
        public List<ChildObj1854_458> getItems() { return items; }
    }
static class ChildObj1854_458 {
        private final String childId;

        private ChildObj1854_458(String id) { childId = id; }

        @JsonCreator
        public static ChildObj1854_458 fromJson(@JsonProperty("ChildID") String cid) {
            return new ChildObj1854_458(cid);
        }

        public String getId() { return childId; }
    }
static class IPhoneBean458 {
        private String iPhone;
        public String getIPhone() { return iPhone; }
        public void setIPhone(String value) { iPhone = value; }
    }
static class DLogHeaderBean458 {
        private String DLogHeader;
        public String getDLogHeader() { return DLogHeader; }
        public void setDLogHeader(String value) { DLogHeader = value; }
    }
static class KBSBroadCastingBean458 {
        private String KBSBroadCasting;
        public String getKBSBroadCasting() { return KBSBroadCasting; }
        public void setKBSBroadCasting(String value) { KBSBroadCasting = value; }
    }
static class PhoneBean458 {
        private String Phone;
        public String getPhone() { return Phone; }
        public void setPhone(String value) { Phone = value; }
    }
static class OAuthTokenBean458 {
        protected String oAuthToken;

        public OAuthTokenBean458(String token) { oAuthToken = token; }
        public String getOAuthToken() { return oAuthToken; }
        public void setOAuthToken(String token) { oAuthToken = token; }
    }

    void __invoke_testCaseInsensitive1036Vpack() throws Exception {
        try {
            testCaseInsensitive1036Vpack();
        } finally {
        }
    }


    void __invoke_testCaseInsensitiveDeserializationVpack() throws Exception {
        try {
            testCaseInsensitiveDeserializationVpack();
        } finally {
        }
    }


    void __invoke_testCaseInsensitiveWithFormatVpack() throws Exception {
        try {
            testCaseInsensitiveWithFormatVpack();
        } finally {
        }
    }


    void __invoke_testCreatorWithInsensitiveVpack() throws Exception {
        try {
            testCreatorWithInsensitiveVpack();
        } finally {
        }
    }


    void __invoke_testCaseInsensitiveViaConfigOverrideVpack() throws Exception {
        try {
            testCaseInsensitiveViaConfigOverrideVpack();
        } finally {
        }
    }


    void __invoke_testIssue1854Vpack() throws Exception {
        try {
            testIssue1854Vpack();
        } finally {
        }
    }


    void __invoke_testCaseInsensitiveViaClassAnnotationVpack() throws Exception {
        try {
            testCaseInsensitiveViaClassAnnotationVpack();
        } finally {
        }
    }

}
