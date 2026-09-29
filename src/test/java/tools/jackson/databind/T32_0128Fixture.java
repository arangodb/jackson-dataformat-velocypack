package tools.jackson.databind;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.TimeZone;

import tools.jackson.core.JsonEncoding;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.JacksonModule;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.node.JsonNodeFactory;
import tools.jackson.databind.module.SimpleModule;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0128Fixture {
private static final byte[] VALUE_STRING = VPackWireFixtureTest.hex(
            "45 76 61 6c 75 65");

    void rebuildPreservesVpackMapperFactoryContract() {
        VPackMapper mapper = new VPackMapper();
        assertNotNull(mapper.version());
        assertNotNull(mapper.tokenStreamFactory());
        assertNotSame(mapper, mapper.rebuild().build());
    }

    void propsExposeAndRetainConfiguredNodeFactory() {
        VPackMapper mapper = new VPackMapper();
        assertNotNull(mapper.getNodeFactory());
        JsonNodeFactory nodeFactory = new JsonNodeFactory();
        VPackMapper configured = VPackMapper.builder()
                .nodeFactory(nodeFactory)
                .build();
        assertNull(configured.getInjectableValues());
        assertSame(nodeFactory, configured.getNodeFactory());
    }

    void registerDependentModulesUsesDeclaredOrder() {
        SimpleModule first = new SimpleModule() {
            @Override
            public Object getRegistrationId() {
                return "dep1";
            }
        };
        SimpleModule second = new SimpleModule() {
            @Override
            public Object getRegistrationId() {
                return "dep2";
            }
        };
        SimpleModule main = new SimpleModule() {
            @Override
            public Iterable<? extends JacksonModule> getDependencies() {
                return Arrays.asList(first, second);
            }

            @Override
            public Object getRegistrationId() {
                return "main";
            }
        };

        VPackMapper mapper = VPackMapper.builder().addModule(main).build();
        Collection<JacksonModule> modules = mapper.registeredModules();
        List<Object> ids = modules.stream().map(JacksonModule::getRegistrationId).toList();
        assertEquals(Arrays.asList("VPackModule", "dep1", "dep2", "main"), ids);
    }

    void registerTransitiveModuleDependenciesUsesDepthFirstOrder() {
        SimpleModule moduleA = new SimpleModule() {
            @Override
            public Object getRegistrationId() {
                return "A";
            }
        };
        SimpleModule moduleB = new SimpleModule() {
            @Override
            public Iterable<? extends JacksonModule> getDependencies() {
                return List.of(moduleA);
            }

            @Override
            public Object getRegistrationId() {
                return "B";
            }
        };
        SimpleModule moduleC = new SimpleModule() {
            @Override
            public Iterable<? extends JacksonModule> getDependencies() {
                return List.of(moduleB);
            }

            @Override
            public Object getRegistrationId() {
                return "C";
            }
        };

        VPackMapper mapper = VPackMapper.builder().addModule(moduleC).build();
        List<Object> ids = mapper.registeredModules().stream()
                .map(JacksonModule::getRegistrationId)
                .toList();
        assertEquals(Arrays.asList("VPackModule", "A", "B", "C"), ids);
    }

    void explicitTimeZoneStateMatchesDatabindContract() {
        VPackMapper mapper = new VPackMapper();
        TimeZone defaultTimeZone = TimeZone.getTimeZone("UTC");
        assertFalse(mapper.serializationConfig().hasExplicitTimeZone());
        assertFalse(mapper.deserializationConfig().hasExplicitTimeZone());
        assertEquals(defaultTimeZone, mapper.serializationConfig().getTimeZone());
        assertEquals(defaultTimeZone, mapper.deserializationConfig().getTimeZone());
        assertFalse(mapper.reader().getConfig().hasExplicitTimeZone());
        assertFalse(mapper.writer().getConfig().hasExplicitTimeZone());

        TimeZone configuredTimeZone = TimeZone.getTimeZone("GMT+4");
        VPackMapper configured = VPackMapper.builder()
                .defaultTimeZone(configuredTimeZone)
                .build();
        assertSame(configuredTimeZone, configured.serializationConfig().getTimeZone());
        assertSame(configuredTimeZone, configured.deserializationConfig().getTimeZone());
        assertTrue(configured.serializationConfig().hasExplicitTimeZone());
        assertTrue(configured.deserializationConfig().hasExplicitTimeZone());
        assertTrue(configured.reader().getConfig().hasExplicitTimeZone());
        assertTrue(configured.writer().getConfig().hasExplicitTimeZone());

        var reader = mapper.reader().with(configuredTimeZone);
        var writer = mapper.writer().with(configuredTimeZone);
        assertTrue(reader.getConfig().hasExplicitTimeZone());
        assertSame(configuredTimeZone, reader.getConfig().getTimeZone());
        assertTrue(writer.getConfig().hasExplicitTimeZone());
        assertSame(configuredTimeZone, writer.getConfig().getTimeZone());

        var defaultReader = reader.with((TimeZone) null);
        var defaultWriter = writer.with((TimeZone) null);
        assertFalse(defaultReader.getConfig().hasExplicitTimeZone());
        assertEquals(defaultTimeZone, defaultReader.getConfig().getTimeZone());
        assertFalse(defaultWriter.getConfig().hasExplicitTimeZone());
        assertEquals(defaultTimeZone, defaultWriter.getConfig().getTimeZone());
    }

    void closeCloseableFeatureClosesValuesForVpackTargets() throws Exception {
        VPackMapper mapper = VPackMapper.builder()
                .enable(SerializationFeature.CLOSE_CLOSEABLE)
                .build();
        CloseableValue input = new CloseableValue();
        assertFalse(input.closed);
        byte[] bytes = mapper.writeValueAsBytes(input);
        assertNotNull(bytes);
        assertTrue(input.closed);

        input = new CloseableValue();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = mapper.createGenerator(output)) {
            mapper.writeValue(generator, input);
        }
        assertNotNull(output.toByteArray());
        assertTrue(input.closed);
    }

    void createGeneratorOutputStreamWritesLiteralVpackAndBorrowsTarget() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = new VPackMapper().createGenerator(output)) {
            generator.writeString("value");
        }
        assertArrayEquals(VALUE_STRING, output.toByteArray());
        output.write(1);
    }

    void createGeneratorFileWritesLiteralVpack() throws Exception {
        Path path = Files.createTempFile("vpack-t32-0128-", ".vpack");
        try {
            try (JsonGenerator generator = new VPackMapper()
                    .createGenerator(path.toFile(), JsonEncoding.UTF8)) {
                generator.writeString("value");
            }
            assertArrayEquals(VALUE_STRING, Files.readAllBytes(path));
        } finally {
            Files.deleteIfExists(path);
        }
    }

    void createGeneratorPathWritesLiteralVpack() throws Exception {
        Path path = Files.createTempFile("vpack-t32-0128-", ".vpack");
        try {
            try (JsonGenerator generator = new VPackMapper()
                    .createGenerator(path, JsonEncoding.UTF8)) {
                generator.writeString("value");
            }
            assertArrayEquals(VALUE_STRING, Files.readAllBytes(path));
        } finally {
            Files.deleteIfExists(path);
        }
    }

    void createGeneratorDataOutputWritesLiteralVpackAndBorrowsTarget() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        DataOutput dataOutput = new DataOutputStream(output);
        try (JsonGenerator generator = new VPackMapper().createGenerator(dataOutput)) {
            generator.writeString("value");
        }
        assertArrayEquals(VALUE_STRING, output.toByteArray());
        dataOutput.writeByte(1);
    }

    void createGeneratorWriterIsExplicitlyUnsupportedForBinaryVpack() {
        assertThrows(UnsupportedOperationException.class,
                () -> new VPackMapper().createGenerator(new StringWriter()));
    }

    void createGeneratorRejectsNullArguments() {
        ObjectMapper mapper = new VPackMapper();
        assertThrows(IllegalArgumentException.class,
                () -> mapper.createGenerator((OutputStream) null));
        assertThrows(IllegalArgumentException.class,
                () -> mapper.createGenerator((OutputStream) null, null));
        assertThrows(IllegalArgumentException.class,
                () -> mapper.createGenerator((DataOutput) null));
        assertThrows(IllegalArgumentException.class,
                () -> mapper.createGenerator((Path) null, null));
        assertThrows(IllegalArgumentException.class,
                () -> mapper.createGenerator((File) null, null));
        assertThrows(IllegalArgumentException.class,
                () -> mapper.createGenerator((java.io.Writer) null));
    }
static class CloseableValue implements Closeable {
        public int x;
        public boolean closed;

        @Override
        public void close() {
            closed = true;
        }
    }

    void __invoke_rebuildPreservesVpackMapperFactoryContract() throws Exception {
        try {
            rebuildPreservesVpackMapperFactoryContract();
        } finally {
        }
    }


    void __invoke_propsExposeAndRetainConfiguredNodeFactory() throws Exception {
        try {
            propsExposeAndRetainConfiguredNodeFactory();
        } finally {
        }
    }


    void __invoke_registerDependentModulesUsesDeclaredOrder() throws Exception {
        try {
            registerDependentModulesUsesDeclaredOrder();
        } finally {
        }
    }


    void __invoke_registerTransitiveModuleDependenciesUsesDepthFirstOrder() throws Exception {
        try {
            registerTransitiveModuleDependenciesUsesDepthFirstOrder();
        } finally {
        }
    }


    void __invoke_explicitTimeZoneStateMatchesDatabindContract() throws Exception {
        try {
            explicitTimeZoneStateMatchesDatabindContract();
        } finally {
        }
    }


    void __invoke_closeCloseableFeatureClosesValuesForVpackTargets() throws Exception {
        try {
            closeCloseableFeatureClosesValuesForVpackTargets();
        } finally {
        }
    }


    void __invoke_createGeneratorOutputStreamWritesLiteralVpackAndBorrowsTarget() throws Exception {
        try {
            createGeneratorOutputStreamWritesLiteralVpackAndBorrowsTarget();
        } finally {
        }
    }


    void __invoke_createGeneratorFileWritesLiteralVpack() throws Exception {
        try {
            createGeneratorFileWritesLiteralVpack();
        } finally {
        }
    }


    void __invoke_createGeneratorPathWritesLiteralVpack() throws Exception {
        try {
            createGeneratorPathWritesLiteralVpack();
        } finally {
        }
    }


    void __invoke_createGeneratorDataOutputWritesLiteralVpackAndBorrowsTarget() throws Exception {
        try {
            createGeneratorDataOutputWritesLiteralVpackAndBorrowsTarget();
        } finally {
        }
    }


    void __invoke_createGeneratorWriterIsExplicitlyUnsupportedForBinaryVpack() throws Exception {
        try {
            createGeneratorWriterIsExplicitlyUnsupportedForBinaryVpack();
        } finally {
        }
    }


    void __invoke_createGeneratorRejectsNullArguments() throws Exception {
        try {
            createGeneratorRejectsNullArguments();
        } finally {
        }
    }

}
