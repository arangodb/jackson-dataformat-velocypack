package com.fasterxml.jackson.core.read.loc;

import com.arangodb.jackson.dataformat.velocypack.VPackFactory;
import com.arangodb.velocypack.VPackSlice;
import com.fasterxml.jackson.core.JUnit5TestBase;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

import static com.fasterxml.jackson.VPackUtils.toVPack;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

// Tests mostly for [core#229]
public class LocationInArrayTest extends JUnit5TestBase {
    final JsonFactory JSON_F = new VPackFactory();

    // for [core#229]
    @Test
    public void offsetInArraysBytes() throws Exception {
        _testOffsetInArrays();
    }

    private VPackSlice extract(JsonParser p, byte[] DOC) {
        return new VPackSlice(Arrays.copyOfRange(DOC,
                (int) p.getTokenLocation().getByteOffset(),
                (int) p.getCurrentLocation().getByteOffset()));
    }

    private void _testOffsetInArrays() throws Exception {
        JsonParser p;
        VPackSlice slice;
        final byte[] DOC = toVPack("[1, 2, 3]");

        p = JSON_F.createParser(DOC);
        assertToken(JsonToken.START_ARRAY, p.nextToken());
        _assertLocation(p.getTokenLocation(), 0L);
        _assertLocation(p.getCurrentLocation(), 5L);
        slice = extract(p, DOC);
        assertTrue(slice.isArray());
        assertEquals(3, slice.size());
        Iterator<VPackSlice> ait = slice.arrayIterator();
        assertEquals(1, ait.next().getAsInt());
        assertEquals(2, ait.next().getAsInt());
        assertEquals(3, ait.next().getAsInt());

        assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
        _assertLocation(p.getTokenLocation(), 2L);
        assertEquals(1, p.getIntValue()); // just to ensure read proceeds to end
        _assertLocation(p.getCurrentLocation(), 3L);

        assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
        _assertLocation(p.getTokenLocation(), 3L);
        assertEquals(2, p.getIntValue()); // just to ensure read proceeds to end
        _assertLocation(p.getCurrentLocation(), 4L);

        assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
        _assertLocation(p.getTokenLocation(), 4L);
        assertEquals(3, p.getIntValue());
        _assertLocation(p.getCurrentLocation(), 5L);

        assertToken(JsonToken.END_ARRAY, p.nextToken());
        _assertLocation(p.getTokenLocation(), 4L);
        _assertLocation(p.getCurrentLocation(), 5L);

        p.close();
    }

    private void _assertLocation(JsonLocation loc, long offset) {
        assertEquals(offset, loc.getByteOffset());
    }
}
