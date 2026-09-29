package tools.jackson.core.it;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static org.junit.jupiter.api.Assertions.assertFalse;

class T32_0001F0 {
private static final String BASE64_ALPHABET =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
private static final String FDP_UNSHADED = "ch/randelshofer/fastdoubleparser/";
private static final String FDP_SHADED = "tools/jackson/core/internal/shaded/fdp/";

    void packagedVpackJarDoesNotEmbedFastDoubleParserClasses() throws IOException {
        Path jar = packagedJar();
        boolean foundFastDoubleParserClass = false;
        try (JarFile archive = new JarFile(jar.toFile())) {
            for (var entries = archive.entries(); entries.hasMoreElements();) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                if (name.endsWith(".class")
                        && (name.startsWith(FDP_UNSHADED)
                                || name.startsWith(FDP_SHADED)
                                || (name.startsWith("META-INF/versions/")
                                        && (name.contains("/" + FDP_UNSHADED)
                                                || name.contains("/" + FDP_SHADED))))) {
                    foundFastDoubleParserClass = true;
                }
            }
        }
        assertFalse(foundFastDoubleParserClass,
                "VPack must not embed core's FastDoubleParser implementation classes");
    }
private static Path packagedJar() {
        Path jar = Path.of("target", "jackson-dataformat-velocypack-5.0.0-t28-test.jar")
                .toAbsolutePath();
        if (!Files.isRegularFile(jar)) {
            throw new AssertionError("packaged VelocyPack JAR is missing: " + jar);
        }
        return jar;
    }

    void __invoke_packagedVpackJarDoesNotEmbedFastDoubleParserClasses() throws Exception {
        try {
            packagedVpackJarDoesNotEmbedFastDoubleParserClasses();
        } finally {
        }
    }

}
