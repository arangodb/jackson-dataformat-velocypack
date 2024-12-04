package com.fasterxml.jackson.core.read.loc;

import com.arangodb.jackson.dataformat.velocypack.VPackFactory;
import com.fasterxml.jackson.core.JUnit5TestBase;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.junit.Test;

import java.io.IOException;
import java.util.Random;

import static com.fasterxml.jackson.VPackUtils.toVPack;
import static org.junit.Assert.assertEquals;


public class LocationOffsetsTest extends JUnit5TestBase {
    final JsonFactory JSON_F = new VPackFactory();

    // Trivially simple unit test for basics wrt offsets
    @Test
    public void simpleInitialOffsets() throws Exception {
        JsonLocation loc;
        JsonParser p;
        final byte[] DOC = toVPack("{}");

        // first, char based:
        p = JSON_F.createParser(DOC);
        assertToken(JsonToken.START_OBJECT, p.nextToken());

        loc = p.currentTokenLocation();
        assertEquals(0L, loc.getByteOffset());

        loc = p.currentLocation();
        assertEquals(1L, loc.getByteOffset());

        p.close();
    }

    // for [core#111]
    @Test
    public void offsetWithInputOffset() throws Exception {
        JsonLocation loc;
        JsonParser p;
        byte[] b = toVPack("{}");

        // and then peel them off
        p = JSON_F.createParser(b);
        assertToken(JsonToken.START_OBJECT, p.nextToken());

        loc = p.currentTokenLocation();
        assertEquals(0L, loc.getByteOffset());

        loc = p.currentLocation();
        assertEquals(1L, loc.getByteOffset());

        p.close();
    }

    @Test
    public void array() throws Exception {
        byte[] b = toVPack("[\"text\"]");
        JsonParser p = JSON_F.createParser(b);
        assertToken(JsonToken.START_ARRAY, p.nextToken());
        assertToken(JsonToken.VALUE_STRING, p.nextToken());
        assertEquals(2, p.currentTokenLocation().getByteOffset());
        assertEquals(b.length, p.currentLocation().getByteOffset());
        p.finishToken();
        assertEquals("text", p.getText());
        p.close();
    }


    // [core#603]
    @Test
    public void bigPayload() throws IOException {
        JsonLocation loc;
        JsonParser p;

        byte[] doc = toVPack("{\"key\":\"" + generateRandomAlpha(50000) + "\"}");

        p = JSON_F.createParser(doc);

        assertToken(JsonToken.START_OBJECT, p.nextToken());
        loc = p.currentTokenLocation();
        assertEquals(0, loc.getByteOffset());
        loc = p.currentLocation();
        assertEquals(doc.length, loc.getByteOffset());

        assertToken(JsonToken.FIELD_NAME, p.nextToken());
        loc = p.currentTokenLocation();
        assertEquals(8, loc.getByteOffset());
        loc = p.currentLocation();
        assertEquals(doc.length - 1, loc.getByteOffset());

        assertToken(JsonToken.VALUE_STRING, p.nextToken());
        loc = p.currentTokenLocation();
        assertEquals(8, loc.getByteOffset());
        loc = p.currentLocation();
        assertEquals(doc.length - 1, loc.getByteOffset());

        p.getTextCharacters();
        loc = p.currentTokenLocation();
        assertEquals(8, loc.getByteOffset());
        loc = p.currentLocation();
        assertEquals(doc.length - 1, loc.getByteOffset());

        p.close();
    }

    private String generateRandomAlpha(int length) {
        StringBuilder sb = new StringBuilder(length);
        Random rnd = new Random(length);
        for (int i = 0; i < length; ++i) {
            // let's limit it not to include surrogate pairs:
            char ch = (char) ('A' + rnd.nextInt(26));
            sb.append(ch);
        }
        return sb.toString();
    }
}
