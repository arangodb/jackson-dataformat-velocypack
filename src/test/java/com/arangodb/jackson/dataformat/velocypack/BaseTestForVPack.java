package com.arangodb.jackson.dataformat.velocypack;

import tools.jackson.core.*;
import tools.jackson.core.io.ContentReference;
import tools.jackson.core.io.IOContext;
import tools.jackson.core.util.BufferRecycler;
import tools.jackson.databind.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import static org.assertj.core.api.Assertions.fail;

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
}
