package tools.jackson.core.unittest.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

import tools.jackson.core.SerializableString;
import tools.jackson.core.io.SerializedString;

import static org.junit.jupiter.api.Assertions.assertEquals;

class T32_0111F0 {
private static final String QUOTED = "\\\"quo\\\\ted\\\"";

    void appendingVpack() throws IOException {
        final String input = "\"quo\\ted\"";
        SerializableString sstr = new SerializedString(input);

        assertEquals(input, sstr.getValue());
        assertEquals(QUOTED, new String(sstr.asQuotedChars()));

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        assertEquals(QUOTED.length(), sstr.writeQuotedUTF8(bytes));
        assertEquals(QUOTED, bytes.toString("UTF-8"));
        bytes.reset();
        assertEquals(input.length(), sstr.writeUnquotedUTF8(bytes));
        assertEquals(input, bytes.toString("UTF-8"));

        byte[] buffer = new byte[100];
        assertEquals(QUOTED.length(), sstr.appendQuotedUTF8(buffer, 3));
        assertEquals(QUOTED, new String(buffer, 3, QUOTED.length()));
        Arrays.fill(buffer, (byte) 0);
        assertEquals(input.length(), sstr.appendUnquotedUTF8(buffer, 5));
        assertEquals(input, new String(buffer, 5, input.length()));
    }

    void failedAccessVpack() throws IOException {
        final String input = "Bit longer text";
        SerializableString sstr = new SerializedString(input);

        byte[] buffer = new byte[input.length() - 2];
        char[] chars = new char[input.length() - 2];
        ByteBuffer byteBuffer = ByteBuffer.allocate(input.length() - 2);

        assertEquals(-1, sstr.appendQuotedUTF8(buffer, 0));
        assertEquals(-1, sstr.appendQuoted(chars, 0));
        assertEquals(-1, sstr.putQuotedUTF8(byteBuffer));

        byteBuffer.rewind();
        assertEquals(-1, sstr.appendUnquotedUTF8(buffer, 0));
        assertEquals(-1, sstr.appendUnquoted(chars, 0));
        assertEquals(-1, sstr.putUnquotedUTF8(byteBuffer));
    }

    void testAppendQuotedUTF8Vpack() throws IOException {
        SerializedString sstr = new SerializedString(QUOTED);
        assertEquals(QUOTED, sstr.getValue());
        byte[] buffer = new byte[100];
        int length = sstr.appendQuotedUTF8(buffer, 3);
        assertEquals("\\\\\\\"quo\\\\\\\\ted\\\\\\\"",
                new String(buffer, 3, length));
    }

    void testJdkSerializeVpack() throws IOException, ClassNotFoundException {
        byte[] bytes;
        try (ByteArrayOutputStream output = new ByteArrayOutputStream();
                ObjectOutputStream objectOutput = new ObjectOutputStream(output)) {
            objectOutput.writeObject(new SerializedString(QUOTED));
            objectOutput.flush();
            bytes = output.toByteArray();
        }

        SerializedString sstr;
        try (ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            sstr = (SerializedString) input.readObject();
        }
        assertEquals(QUOTED, sstr.getValue());
        byte[] buffer = new byte[100];
        int length = sstr.appendQuotedUTF8(buffer, 3);
        assertEquals("\\\\\\\"quo\\\\\\\\ted\\\\\\\"",
                new String(buffer, 3, length));
    }

    void __invoke_appendingVpack() throws Exception {
        try {
            appendingVpack();
        } finally {
        }
    }


    void __invoke_failedAccessVpack() throws Exception {
        try {
            failedAccessVpack();
        } finally {
        }
    }


    void __invoke_testAppendQuotedUTF8Vpack() throws Exception {
        try {
            testAppendQuotedUTF8Vpack();
        } finally {
        }
    }


    void __invoke_testJdkSerializeVpack() throws Exception {
        try {
            testJdkSerializeVpack();
        } finally {
        }
    }

}
