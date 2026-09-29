package tools.jackson.databind;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import tools.jackson.core.JsonPointer;
import tools.jackson.core.StreamReadFeature;
import tools.jackson.core.exc.JacksonIOException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.MappingIterator;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0133Fixture {
private static final byte[] NESTED_POINTER_VALUE = VPackWireFixtureTest.hex(
            "14 2c 43 66 6f 6f 14 25 43 62 61 72 14 1e 46 63 61 6c 6c 65 72 "
            + "14 14 44 6e 61 6d 65 14 0c 45 76 61 6c 75 65 29 d2 04 01 01 01 01 01");
private static final byte[] ONE = VPackWireFixtureTest.hex("31");
private static final byte[] EMPTY_ARRAY = VPackWireFixtureTest.hex("01");
private static final byte[] POINTER_ARRAYS = VPackWireFixtureTest.hex(
            "14 3b 48 77 72 61 70 70 65 72 31 14 2f 44 73 65 74 31 13 11 43 6f 6e 65 "
            + "43 74 77 6f 45 74 68 72 65 65 03 44 73 65 74 32 13 11 44 66 6f 75 72 "
            + "44 66 69 76 65 43 73 69 78 03 02 01");
private static final byte[] TWO_POJO_ARRAY = VPackWireFixtureTest.hex(
            "02 2a 14 14 44 6e 61 6d 65 14 0c 45 76 61 6c 75 65 29 d2 04 01 01 "
            + "14 14 44 6e 61 6d 65 14 0c 45 76 61 6c 75 65 29 2e 16 01 01");
private static final byte[] JOHN_DOE = VPackWireFixtureTest.hex(
            "14 11 44 6e 61 6d 65 48 4a 6f 68 6e 20 44 6f 65 01");
private static final byte[] ONE_PERSON = VPackWireFixtureTest.hex(
            "14 0c 44 6e 61 6d 65 43 4f 6e 65 01");
private static final byte[] TWO_PERSON = VPackWireFixtureTest.hex(
            "14 0c 44 6e 61 6d 65 43 54 77 6f 01");

    void noPointerLoadingReadsNestedLiteralVpackTree() throws Exception {
        ObjectMapper mapper = new VPackMapper();
        JsonNode tree = mapper.readTree(NESTED_POINTER_VALUE);
        JsonNode node = tree.at("/foo/bar/caller");
        POJO pojo = mapper.treeToValue(node, POJO.class);

        assertTrue(pojo.name.containsKey("value"));
        assertEquals(1234, pojo.name.get("value"));
    }

    void noPrefetchReaderReadsLiteralScalar() throws Exception {
        ObjectReader reader = new VPackMapper().reader()
                .without(DeserializationFeature.EAGER_DESERIALIZER_FETCH);

        Number value = reader.forType(Integer.class).readValue(ONE);

        assertEquals(Integer.valueOf(1), value);
    }

    void parserConfigViaReaderEnablesStrictDuplicateDetection() throws Exception {
        ObjectReader reader = new VPackMapper().reader()
                .with(StreamReadFeature.STRICT_DUPLICATE_DETECTION);

        try (tools.jackson.core.JsonParser parser = reader.createParser(EMPTY_ARRAY)) {
            assertTrue(parser.isEnabled(StreamReadFeature.STRICT_DUPLICATE_DETECTION));
        }
    }

    void pointerLoadingReadsNestedLiteralVpackObject() throws Exception {
        ObjectReader reader = new VPackMapper().readerFor(POJO.class)
                .at("/foo/bar/caller");

        POJO pojo = reader.readValue(NESTED_POINTER_VALUE);

        assertTrue(pojo.name.containsKey("value"));
        assertEquals(1234, pojo.name.get("value"));
    }

    void pointerLoadingAsJsonNodeReadsNestedLiteralVpackObject() throws Exception {
        ObjectReader reader = new VPackMapper().readerFor(POJO.class)
                .at(JsonPointer.compile("/foo/bar/caller"));

        JsonNode node = reader.readTree(NESTED_POINTER_VALUE);

        assertTrue(node.has("name"));
        assertEquals("{\"value\":1234}", node.get("name").toString());
    }

    void pointerLoadingMappingIteratorReadsOneLiteralVpackValue() throws Exception {
        ObjectReader reader = new VPackMapper().readerFor(POJO.class)
                .at("/foo/bar/caller");

        try (MappingIterator<POJO> iterator = reader.readValues(NESTED_POINTER_VALUE)) {
            POJO pojo = iterator.next();
            assertTrue(pojo.name.containsKey("value"));
            assertEquals(1234, pojo.name.get("value"));
            assertFalse(iterator.hasNext());
        }
    }

    void pointerLoadingMappingIteratorReadsManyLiteralVpackValues() throws Exception {
        byte[] many = VPackWireFixtureTest.hex(
                "14 45 43 66 6f 6f 14 3e 43 62 61 72 14 37 46 63 61 6c 6c 65 72 "
                + "06 2d 02 "
                + "14 14 44 6e 61 6d 65 14 0c 45 76 61 6c 75 65 29 d2 04 01 01 "
                + "14 14 44 6e 61 6d 65 14 0c 45 76 61 6c 75 65 29 2e 16 01 01 "
                + "03 17 01 01 01");
        ObjectMapper mapper = new VPackMapper();
        ObjectReader pointerReader = mapper.readerFor(POJO.class)
                .at("/foo/bar/caller");
        JsonNode array = pointerReader.readTree(many);
        assertEquals(2, array.size());

        ObjectReader reader = mapper.readerFor(POJO.class);
        try (MappingIterator<POJO> iterator = reader.readValues(TWO_POJO_ARRAY)) {
            POJO first = iterator.next();
            assertTrue(first.name.containsKey("value"));
            assertEquals(1234, first.name.get("value"));
            assertTrue(iterator.hasNext());
            POJO second = iterator.next();
            assertTrue(second.name.containsKey("value"));
            assertEquals(5678, second.name.get("value"));
            assertFalse(iterator.hasNext());
        }
    }

    void pointerWithArraysReadsTargetObjectFromLiteralVpack() throws Exception {
        Pojo1637 result = new VPackMapper().readerFor(Pojo1637.class)
                .at("/wrapper1")
                .readValue(POINTER_ARRAYS);

        assertNotNull(result);
        assertNotNull(result.set1);
        assertFalse(result.set1.isEmpty());
        assertNotNull(result.set2);
        assertFalse(result.set2.isEmpty());
    }

    void readTreeVariantsUseLiteralVpackSources() throws Exception {
        ObjectReader reader = new VPackMapper().reader();
        JsonNode expected = reader.createArrayNode();

        assertEquals(expected, reader.readTree(EMPTY_ARRAY));
        assertEquals(expected, reader.readTree(EMPTY_ARRAY, 0, EMPTY_ARRAY.length));
        assertEquals(expected, reader.readTree(new ByteArrayInputStream(EMPTY_ARRAY)));
        assertThrows(UnsupportedOperationException.class,
                () -> reader.readTree(new StringReader("[]")));
    }

    void readValueFromFileReadsLiteralVpack() throws Exception {
        Path path = Files.createTempFile("vpack-t32-0133-", ".vpack");
        try {
            Files.write(path, JOHN_DOE);
            FilePerson bean = new VPackMapper().readerFor(FilePerson.class)
                    .readValue(path.toFile());
            assertEquals("John Doe", bean.name);
        } finally {
            Files.deleteIfExists(path);
        }
    }

    void readValueFromNonExistentFileReportsJacksonIOException() throws Exception {
        Path directory = Files.createTempDirectory("vpack-t32-0133-missing-");
        Path path = directory.resolve("SHOULD_NOT_EXIST");
        try {
            assertFalse(Files.exists(path));
            JacksonIOException error = assertThrows(JacksonIOException.class,
                    () -> new VPackMapper().readValue(path.toFile(), FilePerson.class));
            assertTrue(error.getMessage().contains("SHOULD_NOT_EXIST"));
        } finally {
            Files.deleteIfExists(directory);
        }
    }

    void readValuesFromFileAndPathReadLiteralVpackSequence() throws Exception {
        byte[] sequence = new byte[ONE_PERSON.length + TWO_PERSON.length];
        System.arraycopy(ONE_PERSON, 0, sequence, 0, ONE_PERSON.length);
        System.arraycopy(TWO_PERSON, 0, sequence, ONE_PERSON.length, TWO_PERSON.length);
        Path path = Files.createTempFile("vpack-t32-0133-sequence-", ".vpack");
        try {
            Files.write(path, sequence);
            ObjectReader reader = new VPackMapper().readerFor(FilePerson.class);
            try (MappingIterator<FilePerson> iterator = reader.readValues(path.toFile())) {
                assertEquals("One", iterator.next().name);
                assertEquals("Two", iterator.next().name);
                assertFalse(iterator.hasNext());
            }
            try (MappingIterator<FilePerson> iterator = reader.readValues(path)) {
                assertEquals("One", iterator.next().name);
                assertEquals("Two", iterator.next().name);
                assertFalse(iterator.hasNext());
            }
        } finally {
            Files.deleteIfExists(path);
        }
    }
static class POJO {
        public java.util.Map<String, Object> name;
    }
static class FilePerson {
        public String name;
    }
public static class Pojo1637 {
        public Set<String> set1;
        public Set<String> set2;
    }

    void __invoke_noPointerLoadingReadsNestedLiteralVpackTree() throws Exception {
        try {
            noPointerLoadingReadsNestedLiteralVpackTree();
        } finally {
        }
    }


    void __invoke_noPrefetchReaderReadsLiteralScalar() throws Exception {
        try {
            noPrefetchReaderReadsLiteralScalar();
        } finally {
        }
    }


    void __invoke_parserConfigViaReaderEnablesStrictDuplicateDetection() throws Exception {
        try {
            parserConfigViaReaderEnablesStrictDuplicateDetection();
        } finally {
        }
    }


    void __invoke_pointerLoadingReadsNestedLiteralVpackObject() throws Exception {
        try {
            pointerLoadingReadsNestedLiteralVpackObject();
        } finally {
        }
    }


    void __invoke_pointerLoadingAsJsonNodeReadsNestedLiteralVpackObject() throws Exception {
        try {
            pointerLoadingAsJsonNodeReadsNestedLiteralVpackObject();
        } finally {
        }
    }


    void __invoke_pointerLoadingMappingIteratorReadsOneLiteralVpackValue() throws Exception {
        try {
            pointerLoadingMappingIteratorReadsOneLiteralVpackValue();
        } finally {
        }
    }


    void __invoke_pointerLoadingMappingIteratorReadsManyLiteralVpackValues() throws Exception {
        try {
            pointerLoadingMappingIteratorReadsManyLiteralVpackValues();
        } finally {
        }
    }


    void __invoke_pointerWithArraysReadsTargetObjectFromLiteralVpack() throws Exception {
        try {
            pointerWithArraysReadsTargetObjectFromLiteralVpack();
        } finally {
        }
    }


    void __invoke_readTreeVariantsUseLiteralVpackSources() throws Exception {
        try {
            readTreeVariantsUseLiteralVpackSources();
        } finally {
        }
    }


    void __invoke_readValueFromFileReadsLiteralVpack() throws Exception {
        try {
            readValueFromFileReadsLiteralVpack();
        } finally {
        }
    }


    void __invoke_readValueFromNonExistentFileReportsJacksonIOException() throws Exception {
        try {
            readValueFromNonExistentFileReportsJacksonIOException();
        } finally {
        }
    }


    void __invoke_readValuesFromFileAndPathReadLiteralVpackSequence() throws Exception {
        try {
            readValuesFromFileAndPathReadLiteralVpackSequence();
        } finally {
        }
    }

}
