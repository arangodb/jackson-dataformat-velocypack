package tools.jackson.dataformat.velocypack;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.jar.JarFile;
import java.lang.module.ModuleDescriptor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VPackModuleMetadataTest {
    private static final String FACTORY = "tools.jackson.dataformat.velocypack.VPackFactory";
    private static final String MAPPER = "tools.jackson.dataformat.velocypack.VPackMapper";

    @Test
    void packagedJarHasTheDeclaredModuleAndServices() throws Exception {
        Path jar = packagedJar();
        try (JarFile archive = new JarFile(jar.toFile())) {
            ModuleDescriptor descriptor = ModuleDescriptor.read(
                    archive.getInputStream(archive.getJarEntry("module-info.class")));
            assertEquals("tools.jackson.dataformat.velocypack", descriptor.name());
            assertTrue(descriptor.requires().stream().anyMatch(required ->
                    required.name().equals("tools.jackson.core") && required.modifiers()
                            .contains(ModuleDescriptor.Requires.Modifier.TRANSITIVE)));
            assertTrue(descriptor.requires().stream().anyMatch(required ->
                    required.name().equals("tools.jackson.databind") && required.modifiers()
                            .contains(ModuleDescriptor.Requires.Modifier.TRANSITIVE)));
            assertEquals(List.of("tools.jackson.dataformat.velocypack"),
                    descriptor.exports().stream().map(ModuleDescriptor.Exports::source).toList());
            assertEquals(List.of(FACTORY), providerClasses(descriptor,
                    "tools.jackson.core.TokenStreamFactory"));
            assertEquals(List.of(MAPPER), providerClasses(descriptor,
                    "tools.jackson.databind.ObjectMapper"));

            assertEquals(FACTORY, serviceContents(archive,
                    "META-INF/services/tools.jackson.core.TokenStreamFactory"));
            assertEquals(MAPPER, serviceContents(archive,
                    "META-INF/services/tools.jackson.databind.ObjectMapper"));
            assertNotNull(archive.getEntry(
                    "tools/jackson/dataformat/velocypack/PackageVersion.class"));
            assertFalse(archive.getEntry(
                    "tools/jackson/dataformat/velocypack/PackageVersion.java") != null);
            assertEquals("5.0.0", archive.getManifest().getMainAttributes()
                    .getValue("Implementation-Version"));
            assertEquals(65, classMajorVersion(archive,
                    "tools/jackson/dataformat/velocypack/VPackFactory.class"));
        }
    }

    @Test
    void packagedJarWorksForSeparateModulePathConsumer() throws Exception {
        Path jar = packagedJar();
        Path consumerRoot = Files.createTempDirectory("vpack-t28-module-consumer");
        try {
            Path sources = Files.createDirectories(consumerRoot.resolve("sources"));
            Path classes = Files.createDirectories(consumerRoot.resolve("classes"));
            copyResource("/module-consumer/module-info.java", sources.resolve("module-info.java"));
            Path packageDir = Files.createDirectories(sources.resolve("t28/consumer"));
            copyResource("/module-consumer/Consumer.java", packageDir.resolve("Consumer.java"));

            List<Path> modulePath = List.of(jar, codeSource(tools.jackson.core.TokenStreamFactory.class),
                    codeSource(tools.jackson.databind.ObjectMapper.class),
                    codeSource(Class.forName("com.fasterxml.jackson.annotation.JsonProperty")));
            String modulePathValue = pathList(modulePath);
            runJavaTool("javac", "--release", "21", "--module-path", modulePathValue,
                    "-d", classes.toString(), sources.resolve("module-info.java").toString(),
                    packageDir.resolve("Consumer.java").toString());
            List<Path> runtimePath = new ArrayList<>();
            runtimePath.add(classes);
            runtimePath.addAll(modulePath);
            runJavaTool("java", "--module-path", pathList(runtimePath),
                    "-m", "t28.consumer/t28.consumer.Consumer");
        } finally {
            deleteTree(consumerRoot);
        }
    }

    private static String serviceContents(JarFile archive, String name) throws IOException {
        try (InputStream input = archive.getInputStream(archive.getJarEntry(name))) {
            return new String(input.readAllBytes()).trim();
        }
    }

    private static byte[] entryBytes(JarFile archive, String name) throws IOException {
        var entry = archive.getJarEntry(name);
        assertNotNull(entry, "missing packaged file: " + name);
        try (InputStream input = archive.getInputStream(entry)) {
            return input.readAllBytes();
        }
    }

    private static String sha256(byte[] bytes) throws Exception {
        return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));
    }

    private static void copyResource(String resource, Path target) throws IOException {
        try (InputStream input = VPackModuleMetadataTest.class.getResourceAsStream(resource)) {
            if (input == null) {
                throw new AssertionError("missing module consumer resource: " + resource);
            }
            Files.copy(input, target);
        }
    }

    private static Path codeSource(Class<?> type) throws URISyntaxException {
        return Path.of(type.getProtectionDomain().getCodeSource().getLocation().toURI());
    }

    private static String pathList(List<Path> paths) {
        return paths.stream().map(Path::toString).reduce((left, right) ->
                left + java.io.File.pathSeparator + right).orElseThrow();
    }

    private static void runJavaTool(String tool, String... arguments) throws Exception {
        Path javaHome = Path.of(System.getProperty("java.home"));
        Path executable = javaHome.resolve("bin").resolve(tool);
        if (!Files.isExecutable(executable)) {
            throw new AssertionError("JDK 21 tool is unavailable: " + executable);
        }
        List<String> command = new ArrayList<>();
        command.add(executable.toString());
        command.addAll(List.of(arguments));
        Process process = new ProcessBuilder(command).redirectErrorStream(true).start();
        String output = new String(process.getInputStream().readAllBytes());
        int exitCode = process.waitFor();
        assertEquals(0, exitCode, tool + " failed:\n" + output);
    }

    private static void deleteTree(Path root) throws IOException {
        try (var paths = Files.walk(root)) {
            paths.sorted(java.util.Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException e) {
                    throw new java.io.UncheckedIOException(e);
                }
            });
        } catch (java.io.UncheckedIOException e) {
            throw e.getCause();
        }
    }

    private static List<String> providerClasses(ModuleDescriptor descriptor, String service) {
        return descriptor.provides().stream()
                .filter(provider -> provider.service().equals(service))
                .findFirst()
                .orElseThrow()
                .providers();
    }

    private static int classMajorVersion(JarFile archive, String name) throws IOException {
        try (DataInputStream input = new DataInputStream(archive.getInputStream(archive.getJarEntry(name)))) {
            assertEquals(0xCAFEBABE, input.readInt());
            input.readUnsignedShort();
            return input.readUnsignedShort();
        }
    }

    private static Path packagedJar() {
        Path jar = Path.of("target", "jackson-dataformat-velocypack-5.0.0-t28-test.jar")
                .toAbsolutePath();
        if (!Files.isRegularFile(jar)) {
            throw new AssertionError("packaged VelocyPack JAR is missing: " + jar);
        }
        return jar;
    }
}
