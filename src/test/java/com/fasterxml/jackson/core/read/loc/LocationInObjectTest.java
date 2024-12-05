package com.fasterxml.jackson.core.read.loc;

import com.arangodb.jackson.dataformat.velocypack.VPackFactory;
import com.fasterxml.jackson.core.JUnit5TestBase;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.junit.Test;

import static com.fasterxml.jackson.VPackUtils.toVPack;
import static org.junit.Assert.assertEquals;

// tests for [core#37]
public class LocationInObjectTest extends JUnit5TestBase
{

    @Test
    public void offsetWithObjectFieldsUsingReader() throws Exception
    {
        final JsonFactory f = new VPackFactory();
        byte[] b = toVPack("{\"f1\":\"v1\",\"f2\":{\"f3\":\"v3\"},\"f4\":[true,false],\"f5\":5}");
        JsonParser p = f.createParser(b);

        assertEquals(JsonToken.START_OBJECT, p.nextToken());

        assertEquals(JsonToken.FIELD_NAME, p.nextToken());
        assertEquals(6L, p.getTokenLocation().getByteOffset());
        assertEquals(9L, p.getCurrentLocation().getByteOffset());
        assertEquals(JsonToken.VALUE_STRING, p.nextToken());
        assertEquals(6L, p.getTokenLocation().getByteOffset());
        assertEquals(9L, p.getCurrentLocation().getByteOffset());

        assertEquals("f2", p.nextFieldName());
        assertEquals(12L, p.getTokenLocation().getByteOffset());
        assertEquals(21L, p.getCurrentLocation().getByteOffset());
        assertEquals(JsonToken.START_OBJECT, p.nextValue());
        assertEquals(12L, p.getTokenLocation().getByteOffset());
        assertEquals(21L, p.getCurrentLocation().getByteOffset());

        assertEquals("f3", p.nextFieldName());
        assertEquals(17L, p.getTokenLocation().getByteOffset());
        assertEquals(20L, p.getCurrentLocation().getByteOffset());
        assertEquals(JsonToken.VALUE_STRING, p.nextValue());
        assertEquals(17L, p.getTokenLocation().getByteOffset());
        assertEquals(20L, p.getCurrentLocation().getByteOffset());
        assertEquals(JsonToken.END_OBJECT, p.nextToken());
        assertEquals(17L, p.getTokenLocation().getByteOffset());
        assertEquals(20L, p.getCurrentLocation().getByteOffset());

        assertEquals("f4", p.nextFieldName());
        assertEquals(24L, p.getTokenLocation().getByteOffset());
        assertEquals(28L, p.getCurrentLocation().getByteOffset());
        assertEquals(JsonToken.START_ARRAY, p.nextValue());
        assertEquals(24L, p.getTokenLocation().getByteOffset());
        assertEquals(28L, p.getCurrentLocation().getByteOffset());

        assertEquals(JsonToken.VALUE_TRUE, p.nextValue());
        assertEquals(26L, p.getTokenLocation().getByteOffset());
        assertEquals(27L, p.getCurrentLocation().getByteOffset());

        assertEquals(JsonToken.VALUE_FALSE, p.nextValue());
        assertEquals(27L, p.getTokenLocation().getByteOffset());
        assertEquals(28L, p.getCurrentLocation().getByteOffset());
        assertEquals(JsonToken.END_ARRAY, p.nextToken());
        assertEquals(27L, p.getTokenLocation().getByteOffset());
        assertEquals(28L, p.getCurrentLocation().getByteOffset());

        assertEquals("f5", p.nextFieldName());
        assertEquals(31L, p.getTokenLocation().getByteOffset());
        assertEquals(32L, p.getCurrentLocation().getByteOffset());
        assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
        assertEquals(31L, p.getTokenLocation().getByteOffset());
        assertEquals(32L, p.getCurrentLocation().getByteOffset());
        assertEquals(JsonToken.END_OBJECT, p.nextToken());
        assertEquals(31L, p.getTokenLocation().getByteOffset());
        assertEquals(32L, p.getCurrentLocation().getByteOffset());

        p.close();
    }
}
