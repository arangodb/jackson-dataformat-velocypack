package tools.jackson.dataformat.velocypack;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ServiceLoader;

import org.junit.jupiter.api.Test;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.TokenStreamFactory;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class VPackServiceLoaderTest {
    @Test
    void packagedJarProvidersLoadAndWorkOnClasspath() throws Exception {
        Path jar = packagedJar();
        ClassLoader parent = VPackServiceLoaderTest.class.getClassLoader();
        try (ChildFirstVPackLoader loader = new ChildFirstVPackLoader(jar, parent)) {
            TokenStreamFactory factory = ServiceLoader.load(TokenStreamFactory.class, loader)
                    .stream()
                    .map(ServiceLoader.Provider::get)
                    .filter(value -> value.getClass().getName().equals(VPackFactory.class.getName()))
                    .findFirst()
                    .orElseThrow();
            assertEquals(VPackFactory.class.getName(), factory.getClass().getName());
            try (JsonParser parser = factory.createParser(new byte[] { 0x30 })) {
                assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            }

            ObjectMapper mapper = ServiceLoader.load(ObjectMapper.class, loader)
                    .stream()
                    .map(ServiceLoader.Provider::get)
                    .filter(value -> value.getClass().getName().equals(VPackMapper.class.getName()))
                    .findFirst()
                    .orElseThrow();
            byte[] encoded = mapper.writeValueAsBytes("classpath");
            assertEquals("classpath", mapper.readValue(encoded, String.class));
        }
    }

    private static Path packagedJar() {
        Path jar = Path.of("target", "jackson-dataformat-velocypack-5.0.0-t28-test.jar")
                .toAbsolutePath();
        assertNotNull(jar);
        if (!Files.isRegularFile(jar)) {
            throw new AssertionError("packaged VelocyPack JAR is missing: " + jar);
        }
        return jar;
    }

    private static final class ChildFirstVPackLoader extends URLClassLoader {
        ChildFirstVPackLoader(Path jar, ClassLoader parent) throws IOException {
            super(new URL[] { jar.toUri().toURL() }, parent);
        }

        @Override
        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            if (name.startsWith("tools.jackson.dataformat.velocypack.")) {
                synchronized (getClassLoadingLock(name)) {
                    Class<?> loaded = findLoadedClass(name);
                    if (loaded == null) {
                        try {
                            loaded = findClass(name);
                        } catch (ClassNotFoundException e) {
                            loaded = super.loadClass(name, false);
                        }
                    }
                    if (resolve) {
                        resolveClass(loaded);
                    }
                    return loaded;
                }
            }
            return super.loadClass(name, resolve);
        }
    }
}
