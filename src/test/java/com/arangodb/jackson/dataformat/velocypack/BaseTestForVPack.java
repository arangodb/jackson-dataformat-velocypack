package com.arangodb.jackson.dataformat.velocypack;

import tools.jackson.core.*;
import tools.jackson.core.io.ContentReference;
import tools.jackson.core.io.IOContext;
import tools.jackson.core.util.BufferRecycler;
import tools.jackson.databind.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Base class for VelocyPack tests.
 */
public abstract class BaseTestForVPack
{
    private static final VPackMapper VPACK_MAPPER = new VPackMapper();

    /*
    /**********************************************************
    /* Factory methods
    /**********************************************************
     */

    protected VPackMapper newMapper() {
        return new VPackMapper();
    }

    protected VPackMapper vpackMapper() {
        return VPACK_MAPPER;
    }

    protected VPackMapper vpackMapper(VPackWriteFeature... writeFeatures) {
        VPackMapper.Builder b = VPackMapper.builder();
        for (VPackWriteFeature f : writeFeatures) {
            b.enable(f);
        }
        return b.build();
    }

    protected byte[] vpackBytes(String json) {
        ObjectMapper jsonMapper = new ObjectMapper();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonParser p = jsonMapper.createParser(json);
             JsonGenerator g = VPACK_MAPPER.createGenerator(out)) {
            while (p.nextToken() != null) {
                g.copyCurrentEvent(p);
            }
        }
        return out.toByteArray();
    }

    protected JsonParser vpackParser(byte[] bytes) {
        return VPACK_MAPPER.createParser(bytes);
    }

    protected JsonGenerator vpackGenerator(OutputStream out) {
        return VPACK_MAPPER.createGenerator(out);
    }

    /**
     * Factory method for creating {@link IOContext}s for tests
     */
    public static IOContext testIOContext() {
        return new IOContext(StreamReadConstraints.defaults(),
                StreamWriteConstraints.defaults(),
                ErrorReportConfiguration.defaults(),
                new BufferRecycler(), ContentReference.unknown(), false,
                JsonEncoding.UTF8);
    }

    /*
    /**********************************************************
    /* Assertion helpers
    /**********************************************************
     */

    protected void assertToken(JsonToken expToken, JsonToken actToken) {
        if (actToken != expToken) {
            fail("Expected token " + expToken + ", current token " + actToken);
        }
    }

    protected void assertToken(JsonToken expToken, JsonParser p) {
        assertToken(expToken, p.currentToken());
    }

    protected void verifyException(Throwable e, String... matches) {
        String msg = e.getMessage();
        String lmsg = (msg == null) ? "" : msg.toLowerCase();
        for (String match : matches) {
            String lmatch = match.toLowerCase();
            if (lmsg.contains(lmatch)) {
                return;
            }
        }
        fail("Expected an exception with one of substrings (" + Arrays.asList(matches)
                + "): got one with message \"" + msg + "\"");
    }

    protected void _verifyBytes(byte[] actBytes, byte... expBytes) {
        assertArrayEquals(expBytes, actBytes);
    }

    protected String getAndVerifyText(JsonParser p) {
        int actLen = p.getStringLength();
        char[] ch = p.getStringCharacters();
        String str2 = new String(ch, p.getStringOffset(), actLen);
        String str = p.getString();
        if (str.length() != actLen) {
            fail("Internal problem: p.getText().length() == " + str.length()
                    + "; p.getTextLength() == " + actLen);
        }
        assertEquals(str, str2, "String access via getText(), getTextXxx() must be the same");
        return str;
    }

    protected static String a2q(String str) {
        return str.replace("'", "\"");
    }

    protected static byte[] utf8Bytes(String str) {
        return str.getBytes(StandardCharsets.UTF_8);
    }
}
