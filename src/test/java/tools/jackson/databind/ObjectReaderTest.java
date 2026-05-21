package tools.jackson.databind;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.Test;
import tools.jackson.core.*;
import tools.jackson.core.exc.JacksonIOException;
import tools.jackson.core.exc.StreamReadException;
import tools.jackson.core.type.ResolvedType;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.cfg.ContextAttributes;
import tools.jackson.databind.cfg.EnumFeature;
import tools.jackson.databind.deser.DeserializationProblemHandler;
import tools.jackson.databind.exc.InvalidDefinitionException;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.jsontype.TypeSerializer;
import tools.jackson.databind.node.*;
import tools.jackson.databind.testutil.DatabindTestUtil;
import tools.jackson.databind.util.TokenBuffer;
import com.arangodb.jackson.dataformat.velocypack.VPackMapper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class ObjectReaderTest extends DatabindTestUtil
{
    private final ObjectMapper MAPPER = newVPackMapper();

    static class POJO {
        public Map<String, Object> name;
    }

    static class FilePerson {
        public String name;
    }

    /*
    /**********************************************************************
    /* Test methods, simple read/write with defaults
    /**********************************************************************
     */

    @Test
    public void testSimpleViaParser() throws Exception
    {
        final String JSON = "[1]";
        try (JsonParser p = MAPPER.createParser(VPackUtils.toVPack(JSON))) {
            Object ob = MAPPER.readerFor(Object.class)
                    .readValue(p);
            assertInstanceOf(List.class, ob);
        }
    }

    @Test
    public void testSimpleAltSources() throws Exception
    {
        final String JSON = "[1]";
        final byte[] BYTES = VPackUtils.toVPack(JSON);
        final Object EXP = Arrays.asList(1);
        assertEquals(EXP, MAPPER
                .readerFor(Object.class)
                .readValue(BYTES));
        assertEquals(EXP, MAPPER
                .readerFor(Object.class)
                .readValue(BYTES, 0, BYTES.length));

        assertEquals(EXP, MAPPER
                .readerFor(Object.class)
                .readValue(VPackUtils.toVPack(JSON)));

        // but also failure mode(s)
        try {
            MAPPER.readerFor(Object.class)
                .readValue(new byte[0]);
            fail("Should not pass");
        } catch (MismatchedInputException e) {
            verifyException(e, "No content to map due to end-of-input");
        }
    }

    // [databind#2693]: convenience read methods:
    @Test
    public void testReaderForArrayOf() throws Exception
    {
        Object value = MAPPER.readerForArrayOf(ABC.class)
                .readValue(VPackUtils.toVPack("[ \"A\", \"C\" ]"));
        assertEquals(ABC[].class, value.getClass());
        ABC[] abcs = (ABC[]) value;
        assertEquals(2, abcs.length);
        assertEquals(ABC.A, abcs[0]);
        assertEquals(ABC.C, abcs[1]);
    }

    // [databind#2693]: convenience read methods:
    @Test
    public void testReaderForListOf() throws Exception
    {
        Object value = MAPPER.readerForListOf(ABC.class)
                .readValue(VPackUtils.toVPack("[ \"B\", \"C\" ]"));
        assertEquals(ArrayList.class, value.getClass());
        assertEquals(Arrays.asList(ABC.B, ABC.C), value);
    }

    // [databind#2693]: convenience read methods:
    @Test
    public void testReaderForMapOf() throws Exception
    {
        Object value = MAPPER.readerForMapOf(ABC.class)
                .readValue(VPackUtils.toVPack("{\"key\" : \"B\" }"));
        assertEquals(LinkedHashMap.class, value.getClass());
        assertEquals(Collections.singletonMap("key", ABC.B), value);
    }

    /*
    /**********************************************************************
    /* Test methods, config setting verification
    /**********************************************************************
     */

    @Test
    public void testDeserializationFeatures() throws Exception
    {
        ObjectReader r = MAPPER.reader();
        assertFalse(r.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES));
        assertFalse(r.isEnabled(StreamReadFeature.IGNORE_UNDEFINED));

        r = r.withoutFeatures(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES,
                DeserializationFeature.FAIL_ON_INVALID_SUBTYPE);
        assertFalse(r.isEnabled(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES));
        assertFalse(r.isEnabled(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE));
        r = r.withFeatures(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES,
                DeserializationFeature.FAIL_ON_INVALID_SUBTYPE);
        assertTrue(r.isEnabled(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES));
        assertTrue(r.isEnabled(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE));

        // alternative method too... can't recall why two
        assertSame(r, r.with(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES,
                DeserializationFeature.FAIL_ON_INVALID_SUBTYPE));

        // and another one
        assertSame(r, r.with(r.getConfig()));

    }

    @Test
    public void testStreamReadFeatures() throws Exception
    {
        ObjectReader r = MAPPER.reader();
        assertFalse(r.isEnabled(StreamReadFeature.IGNORE_UNDEFINED));
        ObjectReader r2 = r.with(StreamReadFeature.IGNORE_UNDEFINED);
        assertTrue(r2.isEnabled(StreamReadFeature.IGNORE_UNDEFINED));
        ObjectReader r3 = r2.without(StreamReadFeature.IGNORE_UNDEFINED);
        assertFalse(r3.isEnabled(StreamReadFeature.IGNORE_UNDEFINED));

        r = r.withFeatures(StreamReadFeature.AUTO_CLOSE_SOURCE,
                StreamReadFeature.CLEAR_CURRENT_TOKEN_ON_CLOSE);
        assertTrue(r.isEnabled(StreamReadFeature.AUTO_CLOSE_SOURCE));
        assertTrue(r.isEnabled(StreamReadFeature.CLEAR_CURRENT_TOKEN_ON_CLOSE));

        r = r.withoutFeatures(StreamReadFeature.AUTO_CLOSE_SOURCE,
                StreamReadFeature.CLEAR_CURRENT_TOKEN_ON_CLOSE);
        assertFalse(r.isEnabled(StreamReadFeature.AUTO_CLOSE_SOURCE));
        assertFalse(r.isEnabled(StreamReadFeature.CLEAR_CURRENT_TOKEN_ON_CLOSE));
    }

    @Test
    public void testDatatypeFeatures() throws Exception
    {
        ObjectReader r = MAPPER.reader();
        r = r.withFeatures(EnumFeature.READ_ENUM_KEYS_USING_INDEX,
                EnumFeature.FAIL_ON_NUMBERS_FOR_ENUMS);
        assertTrue(r.isEnabled(EnumFeature.READ_ENUM_KEYS_USING_INDEX));
        assertTrue(r.isEnabled(EnumFeature.FAIL_ON_NUMBERS_FOR_ENUMS));

        r = r.withoutFeatures(EnumFeature.READ_ENUM_KEYS_USING_INDEX,
                EnumFeature.FAIL_ON_NUMBERS_FOR_ENUMS);
        assertFalse(r.isEnabled(EnumFeature.READ_ENUM_KEYS_USING_INDEX));
        assertFalse(r.isEnabled(EnumFeature.FAIL_ON_NUMBERS_FOR_ENUMS));
    }

    @Test
    public void testMiscSettings() throws Exception
    {
        ObjectReader r = MAPPER.reader();
        assertSame(MAPPER.tokenStreamFactory(), r.parserFactory());

        assertNotNull(r.typeFactory());
        assertNull(r.getInjectableValues());

        r = r.withAttributes(Collections.emptyMap());
        ContextAttributes attrs = r.getAttributes();
        assertNotNull(attrs);
        assertNull(attrs.getAttribute("abc"));
        assertSame(r, r.withoutAttribute("foo"));

        ObjectReader newR = r.forType(MAPPER.constructType(String.class));
        assertNotSame(r, newR);
        assertSame(newR, newR.forType(String.class));

        DeserializationProblemHandler probH = new DeserializationProblemHandler() {
        };
        newR = r.withHandler(probH);
        assertNotSame(r, newR);
        assertSame(newR, newR.withHandler(probH));
        r = newR;
    }

    @Test
    public void testNoPrefetch() throws Exception
    {
        ObjectReader r = MAPPER.reader()
                .without(DeserializationFeature.EAGER_DESERIALIZER_FETCH);
        Number n = r.forType(Integer.class).readValue(VPackUtils.toVPack("123 "));
        assertEquals(Integer.valueOf(123), n);
    }

    @Test
    public void testGetValueType() throws Exception
    {
        ObjectReader r = MAPPER.reader();
        assertNull(r.getValueType());

        r = r.forType(String.class);
        assertEquals(MAPPER.constructType(String.class), r.getValueType());
    }

    @Test
    public void testMiscReaderCreation() {
        JsonNodeFactory nf = new JsonNodeFactory();
        ObjectReader r = MAPPER.reader(nf);
        assertSame(nf, r.jsonNodeFactory());

        r = MAPPER.reader(Base64Variants.MODIFIED_FOR_URL);
        assertEquals(Base64Variants.MODIFIED_FOR_URL,
                r.getConfig().getBase64Variant());
    }

    /*
    /**********************************************************************
    /* Test methods, createParser() variants
    /**********************************************************************
     */

    @Test
    void createParserVariants() throws Exception
    {
        final ObjectReader R = MAPPER.reader();
        File f = _createFileWithNameAndJson("test.json", "{}");
        try (JsonParser p = R.createParser(f)) {
            assertNotNull(p);
        }
        try (JsonParser p = R.createParser(f.toPath())) {
            assertNotNull(p);
        }
        f.delete();

        try (JsonParser p = R.createParser(
                new ByteArrayInputStream(VPackUtils.toVPack("{}")))) {
            assertToken(JsonToken.START_OBJECT, p.nextToken());
            assertToken(JsonToken.END_OBJECT, p.nextToken());
        }
        try (JsonParser p = R.createParser(VPackUtils.toVPack("[]"))) {
            assertToken(JsonToken.START_ARRAY, p.nextToken());
            assertToken(JsonToken.END_ARRAY, p.nextToken());
        }
        try (JsonParser p = R.createParser(new byte[0])) {
            assertNotNull(p);
        }
        try (JsonParser p = R.createParser(new byte[0], 0, 0)) {
            assertNotNull(p);
        }
        try (JsonParser p = R.createParser(VPackUtils.toVPack("[]"))) {
            assertToken(JsonToken.START_ARRAY, p.nextToken());
            assertToken(JsonToken.END_ARRAY, p.nextToken());
        }
    }

    @Test
    public void testParserConfigViaReader() throws Exception
    {
        try (JsonParser p = MAPPER.reader()
                .with(StreamReadFeature.STRICT_DUPLICATE_DETECTION)
                .createParser(VPackUtils.toVPack("[ ]"))) {
            assertTrue(p.isEnabled(StreamReadFeature.STRICT_DUPLICATE_DETECTION));
        }
    }

    /*
    /**********************************************************************
    /* Test methods, readXxx() variants
    /**********************************************************************
     */

    @Test
    public void testReadValuesVariants()
    {
        final ObjectReader R = MAPPER.reader();
        try (JsonParser p = R.createParser(VPackUtils.toVPack("[]"))) {
            assertNotNull(R.readValues(p, List.class));
        }
        try (JsonParser p = R.createParser(VPackUtils.toVPack("[]"))) {
            assertNotNull(R.readValues(p, R.constructType(List.class)));
        }
        try (JsonParser p = R.createParser(VPackUtils.toVPack("[]"))) {
            assertNotNull(R.readValues(p, (ResolvedType) R.constructType(List.class)));
        }
        try (JsonParser p = R.createParser(VPackUtils.toVPack("[]"))) {
            assertNotNull(R.readValues(p, new TypeReference<List<String>>() { }));
        }
        try (TokenBuffer tb = TokenBuffer.forGeneration()) {
            tb.writeStartArray();
            tb.writeEndArray();
            assertNotNull(R.forType(List.class).readValues(tb));
        }
    }

    @Test
    public void testReadTreeVariants()
    {
        final ObjectReader R = MAPPER.reader();
        final String JSON = "[]";
        final byte[] JSON_B = VPackUtils.toVPack(JSON);
        final JsonNode EXP = R.createArrayNode();

        assertEquals(EXP, R.readTree(VPackUtils.toVPack(JSON)));
        assertEquals(EXP, R.readTree(JSON_B));
        assertEquals(EXP, R.readTree(JSON_B, 0, JSON_B.length));
        assertEquals(EXP, R.readTree(new ByteArrayInputStream(JSON_B)));
    }

    /*
    /**********************************************************************
    /* Test methods, JsonPointer
    /**********************************************************************
     */

    @Test
    public void testNoPointerLoading() throws Exception {
        final String source = "{\"foo\":{\"bar\":{\"caller\":{\"name\":{\"value\":1234}}}}}";

        JsonNode tree = MAPPER.readTree(VPackUtils.toVPack(source));
        JsonNode node = tree.at("/foo/bar/caller");
        POJO pojo = MAPPER.treeToValue(node, POJO.class);
        assertTrue(pojo.name.containsKey("value"));
        assertEquals(1234, pojo.name.get("value"));
    }

    @Test
    public void testPointerLoading() throws Exception {
        final String source = "{\"foo\":{\"bar\":{\"caller\":{\"name\":{\"value\":1234}}}}}";

        ObjectReader reader = MAPPER.readerFor(POJO.class).at("/foo/bar/caller");

        POJO pojo = reader.readValue(VPackUtils.toVPack(source));
        assertTrue(pojo.name.containsKey("value"));
        assertEquals(1234, pojo.name.get("value"));
    }

    @Test
    public void testPointerLoadingAsJsonNode() throws Exception {
        final String source = "{\"foo\":{\"bar\":{\"caller\":{\"name\":{\"value\":1234}}}}}";

        ObjectReader reader = MAPPER.readerFor(POJO.class).at(JsonPointer.compile("/foo/bar/caller"));

        JsonNode node = reader.readTree(VPackUtils.toVPack(source));
        assertTrue(node.has("name"));
        assertEquals("{\"value\":1234}", node.get("name").toString());
    }

    @Test
    public void testPointerLoadingMappingIteratorOne() throws Exception {
        final String source = "{\"foo\":{\"bar\":{\"caller\":{\"name\":{\"value\":1234}}}}}";

        ObjectReader reader = MAPPER.readerFor(POJO.class).at("/foo/bar/caller");

        MappingIterator<POJO> itr = reader.readValues(VPackUtils.toVPack(source));

        POJO pojo = itr.next();

        assertTrue(pojo.name.containsKey("value"));
        assertEquals(1234, pojo.name.get("value"));
        assertFalse(itr.hasNext());
        itr.close();
    }

    @Test
    public void testPointerLoadingMappingIteratorMany() throws Exception {
        final String source = "{\"foo\":{\"bar\":{\"caller\":[{\"name\":{\"value\":1234}}, {\"name\":{\"value\":5678}}]}}}";

        ObjectReader reader = MAPPER.readerFor(POJO.class).at("/foo/bar/caller");

        MappingIterator<POJO> itr = reader.readValues(VPackUtils.toVPack(source));

        POJO pojo = itr.next();

        assertTrue(pojo.name.containsKey("value"));
        assertEquals(1234, pojo.name.get("value"));
        assertTrue(itr.hasNext());

        pojo = itr.next();

        assertNotNull(pojo.name);
        assertTrue(pojo.name.containsKey("value"));
        assertEquals(5678, pojo.name.get("value"));
        assertFalse(itr.hasNext());
        itr.close();
    }

    // [databind#1637]
    @Test
    public void testPointerWithArrays() throws Exception
    {
        final String json = a2q("{\n'wrapper1': {\n" +
                "  'set1': ['one', 'two', 'three'],\n" +
                "  'set2': ['four', 'five', 'six']\n" +
                "},\n" +
                "'wrapper2': {\n" +
                "  'set1': ['one', 'two', 'three'],\n" +
                "  'set2': ['four', 'five', 'six']\n" +
                "}\n}");

        final Pojo1637 testObject = MAPPER.readerFor(Pojo1637.class)
                .at("/wrapper1")
                .readValue(VPackUtils.toVPack(json));
        assertNotNull(testObject);

        assertNotNull(testObject.set1);
        assertTrue(!testObject.set1.isEmpty());

        assertNotNull(testObject.set2);
        assertTrue(!testObject.set2.isEmpty());
    }

    public static class Pojo1637 {
        public Set<String> set1;
        public Set<String> set2;
    }

    /*
    /**********************************************************************
    /* Test methods, other
    /**********************************************************************
     */

    @Test
    public void testJsonNodeCreation() throws Exception
    {
        final ObjectReader R = MAPPER.reader();
        assertTrue(R.createArrayNode().isArray());
        assertTrue(R.createObjectNode().isObject());
        assertTrue(R.booleanNode(true).isBoolean());
        assertTrue(R.nullNode().isNull());
        assertTrue(R.missingNode().isMissingNode());
        assertTrue(R.stringNode("abc").isString());
    }
    
    @Test
    public void testTreeToValue() throws Exception
    {
        ArrayNode n = MAPPER.createArrayNode();
        n.add("xyz");
        ObjectReader r = MAPPER.readerFor(String.class);
        List<?> list = r.treeToValue(n, List.class);
        assertEquals(1, list.size());

        // since 2.13:
        String[] arr = r.treeToValue(n, MAPPER.constructType(String[].class));
        assertEquals(1, arr.length);
        assertEquals("xyz", arr[0]);
    }

    @Test
    public void testCodecUnsupportedWrites() throws Exception
    {
        ObjectReader r = MAPPER.readerFor(String.class);
        JsonGenerator g = MAPPER.createGenerator(new ByteArrayOutputStream());
        ObjectNode n = MAPPER.createObjectNode();
        try {
            r.writeTree(g, n);
            fail("Should not pass");
        } catch (UnsupportedOperationException e) {
            ;
        }
    }

    /*
    /**********************************************************************
    /* Test methods, failures, other
    /**********************************************************************
     */

    @Test
    public void testMissingType() throws Exception
    {
        ObjectReader r = MAPPER.reader();
        try {
            r.readValue(VPackUtils.toVPack("1"));
            fail("Should not pass");
        } catch (InvalidDefinitionException e) {
            verifyException(e, "No value type configured");
        }
    }

    @Test
    public void testSchema() throws Exception
    {
        ObjectReader r = MAPPER.readerFor(String.class);

        // Ok to try to set `null` schema, always works:
        assertNotNull(MAPPER.reader((FormatSchema) null));
        r = r.with((FormatSchema) null);
        
        try {
            // but not schema that doesn't match format (no schema exists for json)
            r = r.with(new BogusSchema());

            fail("Should not pass");
        } catch (IllegalArgumentException e) {
            verifyException(e, "Cannot use FormatSchema");
        }

        try {
            MAPPER.reader(new BogusSchema());
            fail("Should not pass");
        } catch (IllegalArgumentException e) {
            verifyException(e, "Cannot use FormatSchema");
        }
    }

    // For [databind#2297]
    @Test
    public void testUnknownFields() throws Exception
    {
        ObjectMapper mapper = VPackMapper.builder().addHandler(new DeserializationProblemHandler(){
            @Override
            public boolean handleUnknownProperty(DeserializationContext ctxt, JsonParser p, ValueDeserializer<?> deserializer, Object beanOrClass, String propertyName) {
                ctxt.readTree(p);
                return true;
            }
        }).build();
        A2297 aObject = mapper.readValue(VPackUtils.toVPack("{\"unknownField\" : 1, \"knownField\": \"test\"}"),
                A2297.class);

        assertEquals("test", aObject.knownField);
    }

    // For [databind#2297]
    private static class A2297 {
        String knownField;

        @JsonCreator
        private A2297(@JsonProperty("knownField") String knownField) {
            this.knownField = knownField;
        }
    }

    // [databind#3699]: custom object node classes
    @Test
    public void testCustomObjectNode() throws Exception
    {
        ObjectNode defaultNode = (ObjectNode) MAPPER.readTree(VPackUtils.toVPack("{\"x\": 1, \"y\": 2}"));
        CustomObjectNode customObjectNode = new CustomObjectNode(defaultNode);
        Point point = MAPPER.readerFor(Point.class).readValue(customObjectNode);
        assertEquals(1, point.x);
        assertEquals(2, point.y);
    }

    // [databind#3699]: custom array node classes
    @Test
    public void testCustomArrayNode() throws Exception
    {
        ArrayNode defaultNode = (ArrayNode) MAPPER.readTree(
                VPackUtils.toVPack("[{\"x\": 1, \"y\": 2}]"));
        DelegatingArrayNode customArrayNode = new DelegatingArrayNode(defaultNode);
        Point[] points = MAPPER.readerFor(Point[].class).readValue(customArrayNode);
        Point point = points[0];
        assertEquals(1, point.x);
        assertEquals(2, point.y);
    }

    // for [databind#3699]
    static class CustomObjectNode extends BaseJsonNode
    {
        private static final long serialVersionUID = 1L;

        private final ObjectNode _delegate;

        CustomObjectNode(ObjectNode delegate) {
            _delegate = delegate;
        }

        @Override
        protected String _valueDesc() {
            return "<CUSTOM>";
        }
        
        @Override
        public boolean isObject() {
            return true;
        }

        @Override
        public int size() {
            return _delegate.size();
        }

        @Override
        public Set<Map.Entry<String, JsonNode>> properties() {
            return _delegate.properties();
        }

        @Override
        public Collection<JsonNode> values() {
            return Collections.emptyList();
        }

        @Override
        public JsonToken asToken() {
            return JsonToken.START_OBJECT;
        }

        @Override
        public void serialize(JsonGenerator g, SerializationContext ctxt) {
            // ignore, will not be called
        }

        @Override
        public void serializeWithType(JsonGenerator g, SerializationContext ctxt, TypeSerializer typeSer) {
            // ignore, will not be called
        }

        @Override
        public CustomObjectNode deepCopy() {
            return new CustomObjectNode(_delegate);
        }

        @Override
        public JsonNode get(int index) {
            return null;
        }

        @Override
        public JsonNode path(String fieldName) {
            return null;
        }

        @Override
        public JsonNode path(int index) {
            return null;
        }

        @Override
        protected JsonNode _at(JsonPointer ptr) {
            return null;
        }

        @Override
        public JsonNodeType getNodeType() {
            return JsonNodeType.OBJECT;
        }

        @Override
        public String asString() {
            return "";
        }

        @Override
        public JsonNode findValue(String fieldName) {
            return null;
        }

        @Override
        public JsonNode findParent(String fieldName) {
            return null;
        }

        @Override
        public List<JsonNode> findValues(String fieldName, List<JsonNode> foundSoFar) {
            return Collections.emptyList();
        }

        @Override
        public List<String> findValuesAsString(String fieldName, List<String> foundSoFar) {
            return foundSoFar;
        }

        @Override
        public List<JsonNode> findParents(String fieldName, List<JsonNode> foundSoFar) {
            return foundSoFar;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof CustomObjectNode)) {
                return false;
            }
            CustomObjectNode other = (CustomObjectNode) o;
            return this._delegate.equals(other._delegate);
        }

        @Override
        public int hashCode() {
            return _delegate.hashCode();
        }

    }

    // for [databind#3699]
    static class DelegatingArrayNode extends BaseJsonNode
    {
        private static final long serialVersionUID = 1L;

        private final ArrayNode _delegate;

        DelegatingArrayNode(ArrayNode delegate) {
            this._delegate = delegate;
        }

        @Override
        protected String _valueDesc() {
            return "<CUSTOM>";
        }

        @Override
        public boolean isArray() {
            return true;
        }

        @Override
        public int size() {
            return _delegate.size();
        }

        @Override
        public Collection<JsonNode> values() {
            return _delegate.values();
        }

        @Override
        public JsonToken asToken() {
            return JsonToken.START_ARRAY;
        }

        @Override
        public void serialize(JsonGenerator g, SerializationContext ctxt) {
            // ignore, will not be called
        }

        @Override
        public void serializeWithType(JsonGenerator g, SerializationContext ctxt, TypeSerializer typeSer) {
            // ignore, will not be called
        }

        @Override
        public DelegatingArrayNode deepCopy() {
            return new DelegatingArrayNode(_delegate);
        }

        @Override
        public JsonNode get(int index) {
            return _delegate.get(index);
        }

        @Override
        public JsonNode path(String fieldName) {
            return null;
        }

        @Override
        public JsonNode path(int index) {
            return _delegate.path(index);
        }

        @Override
        protected JsonNode _at(JsonPointer ptr) {
            return null;
        }

        @Override
        public JsonNodeType getNodeType() {
            return JsonNodeType.ARRAY;
        }

        @Override
        public String asString() {
            return "";
        }

        @Override
        public JsonNode findValue(String fieldName) {
            return null;
        }

        @Override
        public JsonNode findParent(String fieldName) {
            return null;
        }

        @Override
        public List<JsonNode> findValues(String fieldName, List<JsonNode> foundSoFar) {
            return foundSoFar;
        }

        @Override
        public List<String> findValuesAsString(String fieldName, List<String> foundSoFar) {
            return foundSoFar;
        }

        @Override
        public List<JsonNode> findParents(String fieldName, List<JsonNode> foundSoFar) {
            return foundSoFar;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof DelegatingArrayNode)) {
                return false;
            }
            DelegatingArrayNode other = (DelegatingArrayNode) o;
            return this._delegate.equals(other._delegate);
        }

        @Override
        public int hashCode() {
            return _delegate.hashCode();
        }
    }

    // // // Tests for reading from Files

    @Test
    public void testReadValueFromFile() throws Exception {
        File file = _createFileWithNameAndJson(
            "testReadValueFromFile",
            a2q("{ 'name': 'John Doe'}"));

        FilePerson bean = MAPPER.readerFor(FilePerson.class).readValue(file);

        assertEquals("John Doe", bean.name);
        assertTrue(file.delete());
    }

    @Test
    public void testReadValueFromNonExistentFile() throws Exception {
        File file = new File("SHOULD_NOT_EXIST");
        assertFalse(file.exists());

        try {
            MAPPER.readValue(file, FilePerson.class);
            fail("should not pass");
        } catch (JacksonIOException e) {
            verifyException(e, "SHOULD_NOT_EXIST");
        }
    }

    @Test
    public void testInputStreamFromEmptyFile() throws Exception {
        File file = _createFileWithNameAndJson(
            "testInputStreamFromEmptyFile",
            "");

        try {
            MAPPER.readerFor(FilePerson.class).readValue(file);
            fail("should not pass");
        } catch (MismatchedInputException e) {
            verifyException(e, "No content to map due to end-of-input");
        } finally {
            assertTrue(file.delete());
        }
    }

    private File _createFileWithNameAndJson(String fileName, String json) throws Exception {
        File file = File.createTempFile(fileName, ".json");
        file.deleteOnExit();
        try (OutputStream out = new FileOutputStream(file)) {
            out.write(VPackUtils.toVPack(json));
            out.flush();
        }
        return file;
    }
}
