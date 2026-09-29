package tools.jackson.databind.interop;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0389F2 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] IDENTITY_GRAPH = VPackWireFixtureTest.hex(
            "0b 82 04 43 40 69 64 31 "
          + "44 6e 61 6d 65 4c 74 65 73 74 5f 65 6e 74 69 74 79 31 "
          + "47 65 6e 74 69 74 79 32 "
          + "0b 26 03 43 40 69 64 32 "
          + "44 6e 61 6d 65 4c 74 65 73 74 5f 65 6e 74 69 74 79 32 "
          + "47 65 6e 74 69 74 79 31 31 03 1a 08 "
          + "46 70 61 72 65 6e 74 "
          + "0b 2f 04 43 40 69 64 33 "
          + "44 6e 61 6d 65 4c 72 6f 6f 74 5f 65 6e 74 69 74 79 31 "
          + "47 65 6e 74 69 74 79 32 32 "
          + "46 70 61 72 65 6e 74 18 03 1a 08 23 "
          + "03 1a 08 48");
private static final byte[] UNWRAPPED_WIDGET = VPackWireFixtureTest.hex(
            "0b 88 06 "
          + "51 77 69 64 67 65 74 52 65 66 65 72 65 6e 63 65 49 64 "
          + "51 77 69 64 67 65 74 52 65 66 65 72 65 6e 63 65 49 64 "
          + "48 68 65 61 64 6c 69 6e 65 48 68 65 61 64 6c 69 6e 65 "
          + "42 69 64 42 69 64 "
          + "46 69 6d 61 67 65 73 "
          + "02 26 "
          + "0b 12 01 42 69 64 4a 74 65 73 74 49 6d 61 67 65 31 03 "
          + "0b 12 01 42 69 64 4a 74 65 73 74 49 6d 61 67 65 32 03 "
          + "45 69 6e 74 72 6f 45 69 6e 74 72 6f "
          + "44 72 6f 6c 65 44 72 6f 6c 65 "
          + "27 39 3f 6c 78 03");
private static final byte[] EXTERNALIZABLE_PAYLOAD = VPackWireFixtureTest.hex(
            "0b 23 03 42 69 64 28 0d "
          + "44 6e 61 6d 65 46 46 6f 6f 62 61 72 "
          + "46 76 61 6c 75 65 73 02 05 31 32 33 03 08 14");

    // Provenance: TestExternalizable#testSerializeAsExternalizable().
    void testSerializeAsExternalizableVpack() throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ObjectOutputStream output = new ObjectOutputStream(bytes);
        MyPojo input = new MyPojo(13, "Foobar", new int[] { 1, 2, 3 });
        output.writeObject(input);
        output.close();

        byte[] serialized = bytes.toByteArray();
        assertContains(serialized, EXTERNALIZABLE_PAYLOAD);

        ObjectInputStream inputStream = new ObjectInputStream(
                new ByteArrayInputStream(serialized));
        MyPojo result = (MyPojo) inputStream.readObject();
        inputStream.close();
        assertNotNull(result);
        assertEquals(input, result);
    }
private static TestGallery createGallery() {
        return new TestGallery("id", "headline", "intro", "role",
                Arrays.asList(new TestImage("testImage1"), new TestImage("testImage2")));
    }
private static void assertContains(byte[] full, byte[] fragment) {
        for (int offset = 0; offset <= full.length - fragment.length; ++offset) {
            boolean matches = true;
            for (int i = 0; i < fragment.length; ++i) {
                if (full[offset + i] != fragment[i]) {
                    matches = false;
                    break;
                }
            }
            if (matches) return;
        }
        throw new AssertionError("Externalizable stream does not contain the VPack payload");
    }
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
    static class Entity1 {
        private final String name;
        private Entity2 entity2;
        private Entity1 parent;

        @JsonCreator
        public Entity1(@JsonProperty("name") String name,
                @JsonProperty("entity2") Entity2 entity2,
                @JsonProperty("parent") Entity1 parent) {
            this.name = name;
            this.entity2 = entity2;
            this.parent = parent;
        }

        public String getName() { return name; }
        public Entity2 getEntity2() { return entity2; }
        public void setEntity2(Entity2 entity2) { this.entity2 = entity2; }
        public Entity1 getParent() { return parent; }
        public void setParent(Entity1 parent) { this.parent = parent; }
    }
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
    static class Entity2 {
        private final String name;
        private Entity1 entity1;

        @JsonCreator
        public Entity2(@JsonProperty("name") String name,
                @JsonProperty("entity1") Entity1 entity1) {
            this.name = name;
            this.entity1 = entity1;
        }

        public String getName() { return name; }
        public Entity1 getEntity1() { return entity1; }
        public void setEntity1(Entity1 entity1) { this.entity1 = entity1; }
    }
@JsonInclude(JsonInclude.Include.NON_EMPTY)
    static class TestGallery {
        public String id;
        public String headline;
        public String intro;
        public String role;
        public List<TestImage> images;

        public TestGallery() { }

        TestGallery(String id, String headline, String intro, String role, List<TestImage> images) {
            this.id = id;
            this.headline = headline;
            this.intro = intro;
            this.role = role;
            this.images = images;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (!(other instanceof TestGallery that)) return false;
            return java.util.Objects.equals(id, that.id)
                    && java.util.Objects.equals(headline, that.headline)
                    && java.util.Objects.equals(intro, that.intro)
                    && java.util.Objects.equals(role, that.role)
                    && java.util.Objects.equals(images, that.images);
        }
    }
@JsonInclude(JsonInclude.Include.NON_EMPTY)
    static class TestImage {
        public String id;
        public String escenicId;
        public String caption;
        public String copyright;
        public java.util.Map<String, String> crops;

        public TestImage() { }
        TestImage(String id) { this.id = id; }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (!(other instanceof TestImage that)) return false;
            return java.util.Objects.equals(id, that.id)
                    && java.util.Objects.equals(escenicId, that.escenicId)
                    && java.util.Objects.equals(caption, that.caption)
                    && java.util.Objects.equals(copyright, that.copyright)
                    && java.util.Objects.equals(crops, that.crops);
        }
    }
static class TestGalleryWidget {
        private String widgetReferenceId;
        private TestGallery gallery;

        @JsonCreator
        public TestGalleryWidget(@JsonProperty("widgetReferenceId") String widgetReferenceId,
                @JsonUnwrapped TestGallery gallery) {
            this.widgetReferenceId = widgetReferenceId;
            this.gallery = gallery;
        }

        public String getWidgetReferenceId() { return widgetReferenceId; }

        @JsonUnwrapped
        public TestGallery getGallery() { return gallery; }
    }
static class MyPojo implements Externalizable {
        public int id;
        public String name;
        public int[] values;

        public MyPojo() { }

        MyPojo(int id, String name, int[] values) {
            this.id = id;
            this.name = name;
            this.values = values;
        }

        @Override
        public void readExternal(ObjectInput in) throws IOException {
            MAPPER.readerForUpdating(this).readValue(new ExternalizableInput(in));
        }

        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            MAPPER.writeValue(new ExternalizableOutput(out), this);
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof MyPojo that
                    && id == that.id
                    && java.util.Objects.equals(name, that.name)
                    && Arrays.equals(values, that.values);
        }
    }
static final class ExternalizableInput extends InputStream {
        private final ObjectInput input;

        ExternalizableInput(ObjectInput input) { this.input = input; }

        @Override public int available() throws IOException { return input.available(); }
        @Override public void close() throws IOException { input.close(); }
        @Override public int read() throws IOException { return input.read(); }
        @Override public int read(byte[] buffer) throws IOException { return input.read(buffer); }
        @Override public int read(byte[] buffer, int offset, int length) throws IOException {
            return input.read(buffer, offset, length);
        }
        @Override public long skip(long count) throws IOException { return input.skip(count); }
    }
static final class ExternalizableOutput extends OutputStream {
        private final ObjectOutput output;

        ExternalizableOutput(ObjectOutput output) { this.output = output; }

        @Override public void flush() throws IOException { output.flush(); }
        @Override public void close() throws IOException { output.close(); }
        @Override public void write(int value) throws IOException { output.write(value); }
        @Override public void write(byte[] data) throws IOException { output.write(data); }
        @Override public void write(byte[] data, int offset, int length) throws IOException {
            output.write(data, offset, length);
        }
    }

    void __invoke_testSerializeAsExternalizableVpack() throws Exception {
        try {
            testSerializeAsExternalizableVpack();
        } finally {
        }
    }

}
