package tools.jackson.core.it;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.JarFile;

import static org.junit.jupiter.api.Assertions.assertFalse;

class T32_0001F1 {
private static final String BASE64_ALPHABET =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
private static final String FDP_UNSHADED = "ch/randelshofer/fastdoubleparser/";
private static final String FDP_SHADED = "tools/jackson/core/internal/shaded/fdp/";

    void packagedVpackJarHasNoVersionedModuleInfo() throws IOException {
        Path jar = packagedJar();
        boolean versionedModuleInfo = false;
        try (JarFile archive = new JarFile(jar.toFile())) {
            for (var entries = archive.entries(); entries.hasMoreElements();) {
                String name = entries.nextElement().getName();
                if (name.startsWith("META-INF/versions/")
                        && name.endsWith("/module-info.class")) {
                    versionedModuleInfo = true;
                }
            }
        }
        assertFalse(versionedModuleInfo,
                "VPack module-info.class must not be under META-INF/versions/");
    }
private static Path packagedJar() {
        Path jar = Path.of("target", "jackson3-dataformat-velocypack-5.0.0-t28-test.jar")
                .toAbsolutePath();
        if (!Files.isRegularFile(jar)) {
            throw new AssertionError("packaged VelocyPack JAR is missing: " + jar);
        }
        return jar;
    }

    void __invoke_packagedVpackJarHasNoVersionedModuleInfo() throws Exception {
        try {
            packagedVpackJarHasNoVersionedModuleInfo();
        } finally {
        }
    }

}
