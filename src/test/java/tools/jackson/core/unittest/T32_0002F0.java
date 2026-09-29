package tools.jackson.core.unittest;

import java.io.ByteArrayOutputStream;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.Version;

import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0002F0 {

    void coreVersions() throws Exception {
        VPackFactory factory = new VPackFactory();
        assertEquals(PackageVersion.VERSION, factory.version());
        try (JsonParser parser = factory.createParser(new byte[] { 0x18 })) {
            assertEquals(PackageVersion.VERSION, parser.version());
        }
        try (JsonGenerator generator = factory.createGenerator(new ByteArrayOutputStream())) {
            assertEquals(PackageVersion.VERSION, generator.version());
        }
    }

    void equality() {
        Version unknown = Version.unknownVersion();
        assertEquals("0.0.0", unknown.toString());
        assertEquals("//0.0.0", unknown.toFullString());
        assertEquals(unknown, unknown);

        Version other = new Version(2, 8, 4, "", "groupId", "artifactId");
        assertEquals("2.8.4", other.toString());
        assertEquals("groupId/artifactId/2.8.4", other.toFullString());

        Version unknownWithNullSnapshot = new Version(0, 0, 0, null, null, null);
        assertEquals(unknown, unknownWithNullSnapshot);
    }

    void misc() {
        Version unknown = Version.unknownVersion();
        assertEquals(0, unknown.hashCode());
    }

    void __invoke_coreVersions() throws Exception {
        try {
            coreVersions();
        } finally {
        }
    }


    void __invoke_equality() throws Exception {
        try {
            equality();
        } finally {
        }
    }


    void __invoke_misc() throws Exception {
        try {
            misc();
        } finally {
        }
    }

}
