package tools.jackson.databind.misc;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0458F1 {
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

    // Provenance: IPhoneStyleProperty5152Test#testIPhoneStyleProperty().
    void testIPhoneStylePropertyVpack() throws Exception {
        IPhoneBean458 result = MAPPER.readValue(IPHONE_INPUT, IPhoneBean458.class);
        assertNotNull(result);
        assertEquals("iPhone 15", result.getIPhone());
        assertArrayEquals(IPHONE_OUTPUT, MAPPER.writeValueAsBytes(result));
    }

    // Provenance: IPhoneStyleProperty5152Test#testDLogHeaderStyleProperty().
    void testDLogHeaderStylePropertyVpack() throws Exception {
        DLogHeaderBean458 result = MAPPER.readValue(DLOG_INPUT, DLogHeaderBean458.class);
        assertNotNull(result);
        assertEquals("Debug Log Header", result.getDLogHeader());
        assertArrayEquals(DLOG_OUTPUT, MAPPER.writeValueAsBytes(result));
    }

    // Provenance: IPhoneStyleProperty5152Test#testKBSBroadCastingStyleProperty().
    void testKBSBroadCastingStylePropertyVpack() throws Exception {
        KBSBroadCastingBean458 result = MAPPER.readValue(KBS_INPUT, KBSBroadCastingBean458.class);
        assertNotNull(result);
        assertEquals("Korean Broadcasting System", result.getKBSBroadCasting());
        assertArrayEquals(KBS_OUTPUT, MAPPER.writeValueAsBytes(result));
    }

    // Provenance: IPhoneStyleProperty5152Test#testPhoneStyleProperty().
    void testPhoneStylePropertyVpack() throws Exception {
        PhoneBean458 result = MAPPER.readValue(PHONE_INPUT, PhoneBean458.class);
        assertNotNull(result);
        assertEquals("iPhone 15", result.getPhone());
        assertArrayEquals(PHONE_OUTPUT, MAPPER.writeValueAsBytes(result));
    }

    // Provenance: IPhoneStyleProperty5152Test#testOAuthProperty().
    void testOAuthPropertyVpack() throws Exception {
        OAuthTokenBean458 input = new OAuthTokenBean458("123");
        assertArrayEquals(OAUTH_OUTPUT, MAPPER.writeValueAsBytes(input));
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

    void __invoke_testIPhoneStylePropertyVpack() throws Exception {
        try {
            testIPhoneStylePropertyVpack();
        } finally {
        }
    }


    void __invoke_testDLogHeaderStylePropertyVpack() throws Exception {
        try {
            testDLogHeaderStylePropertyVpack();
        } finally {
        }
    }


    void __invoke_testKBSBroadCastingStylePropertyVpack() throws Exception {
        try {
            testKBSBroadCastingStylePropertyVpack();
        } finally {
        }
    }


    void __invoke_testPhoneStylePropertyVpack() throws Exception {
        try {
            testPhoneStylePropertyVpack();
        } finally {
        }
    }


    void __invoke_testOAuthPropertyVpack() throws Exception {
        try {
            testOAuthPropertyVpack();
        } finally {
        }
    }

}
