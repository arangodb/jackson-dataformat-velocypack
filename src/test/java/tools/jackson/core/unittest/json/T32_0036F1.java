package tools.jackson.core.unittest.json;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import tools.jackson.core.FormatSchema;
import tools.jackson.core.JsonEncoding;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.core.StreamWriteFeature;
import tools.jackson.core.TokenStreamFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0036F1 {

    void streamWriteFeatureCanBeConfiguredOnVpackFactory() {
        VPackFactory enabled = VPackFactory.builder()
                .enable(StreamWriteFeature.IGNORE_UNKNOWN).build();
        assertTrue(enabled.isEnabled(StreamWriteFeature.IGNORE_UNKNOWN));

        VPackFactory disabled = enabled.rebuild()
                .configure(StreamWriteFeature.IGNORE_UNKNOWN, false).build();
        assertFalse(disabled.isEnabled(StreamWriteFeature.IGNORE_UNKNOWN));
        assertTrue(enabled.isEnabled(StreamWriteFeature.IGNORE_UNKNOWN));
    }

    void factoryConfigurationAndMetadataMatchTheBinaryContract() {
        VPackFactory enabled = VPackFactory.builder()
                .enable(TokenStreamFactory.Feature.INTERN_PROPERTY_NAMES).build();
        assertTrue(enabled.isEnabled(TokenStreamFactory.Feature.INTERN_PROPERTY_NAMES));
        VPackFactory disabled = enabled.rebuild()
                .disable(TokenStreamFactory.Feature.INTERN_PROPERTY_NAMES).build();
        assertFalse(disabled.isEnabled(TokenStreamFactory.Feature.INTERN_PROPERTY_NAMES));

        assertTrue(enabled.canHandleBinaryNatively());
        assertNull(enabled.getInputDecorator());
        assertNull(enabled.getOutputDecorator());
        assertFalse(enabled.canUseSchema(null));
        assertFalse(enabled.canUseSchema(new BogusSchema()));
        assertEquals(VPackReadFeature.class, enabled.getFormatReadFeatureType());
        assertEquals(VPackWriteFeature.class, enabled.getFormatWriteFeatureType());
    }

    void copyRetainsPortableConfigurationAndIsDistinct() {
        VPackWriteConstraints constraints = VPackWriteConstraints.builder()
                .maxRootValueBytes(19).maxRootEntries(4).build();
        VPackFactory original = VPackFactory.builder()
                .enable(TokenStreamFactory.Feature.INTERN_PROPERTY_NAMES)
                .enable(StreamWriteFeature.IGNORE_UNKNOWN)
                .enable(VPackWriteFeature.WRITE_COMPACT_OBJECTS)
                .vpackWriteConstraints(constraints).build();

        VPackFactory copy = original.copy();
        assertNotSame(original, copy);
        assertTrue(copy.isEnabled(TokenStreamFactory.Feature.INTERN_PROPERTY_NAMES));
        assertTrue(copy.isEnabled(StreamWriteFeature.IGNORE_UNKNOWN));
        assertTrue(copy.isEnabled(VPackWriteFeature.WRITE_COMPACT_OBJECTS));
        assertSame(constraints, copy.vpackWriteConstraints());
    }

    void fileGeneratorAndParserUseSelfDelimitingVpackRoots() throws Exception {
        Path path = Files.createTempFile("vpack-t32-0036", ".bin");
        try {
            try (JsonGenerator generator = new VPackFactory().createGenerator(
                    ObjectWriteContext.empty(), path.toFile(), JsonEncoding.UTF8)) {
                generator.writeStartObject();
                generator.writeName("a");
                generator.writeNumber(1);
                generator.writeEndObject();
            }

            try (JsonParser parser = new VPackFactory().createParser(
                    ObjectReadContext.empty(), path.toFile())) {
                assertEquals(JsonToken.START_OBJECT, parser.nextToken());
                assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
                assertEquals("a", parser.currentName());
                assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
                assertEquals(1, parser.getIntValue());
                assertEquals(JsonToken.END_OBJECT, parser.nextToken());
                assertNull(parser.nextToken());
            }
        } finally {
            Files.deleteIfExists(path);
        }
    }
private static final class BogusSchema implements FormatSchema {
        @Override
        public String getSchemaType() {
            return "test";
        }
    }
private static final class FailingFileFactory extends VPackFactory {
        private FailingInputStream lastStream;

        @Override
        protected InputStream _fileInputStream(File file) {
            return lastStream = new FailingInputStream();
        }
    }
private static final class FailingInputStream extends InputStream {
        private boolean closed;

        @Override
        public int read() throws IOException {
            throw new IOException("Will not read");
        }

        @Override
        public void close() {
            closed = true;
        }
    }

    void __invoke_streamWriteFeatureCanBeConfiguredOnVpackFactory() throws Exception {
        try {
            streamWriteFeatureCanBeConfiguredOnVpackFactory();
        } finally {
        }
    }


    void __invoke_factoryConfigurationAndMetadataMatchTheBinaryContract() throws Exception {
        try {
            factoryConfigurationAndMetadataMatchTheBinaryContract();
        } finally {
        }
    }


    void __invoke_copyRetainsPortableConfigurationAndIsDistinct() throws Exception {
        try {
            copyRetainsPortableConfigurationAndIsDistinct();
        } finally {
        }
    }


    void __invoke_fileGeneratorAndParserUseSelfDelimitingVpackRoots() throws Exception {
        try {
            fileGeneratorAndParserUseSelfDelimitingVpackRoots();
        } finally {
        }
    }

}
