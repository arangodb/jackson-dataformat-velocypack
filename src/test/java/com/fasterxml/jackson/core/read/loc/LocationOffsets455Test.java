package com.fasterxml.jackson.core.read.loc;

import com.arangodb.jackson.dataformat.velocypack.VPackFactory;
import com.fasterxml.jackson.core.JUnit5TestBase;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.junit.Test;

import static com.fasterxml.jackson.VPackUtils.toVPack;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class LocationOffsets455Test extends JUnit5TestBase
{

    // for [jackson-core#455]
    @Test
    public void eofLocationViaStream() throws Exception
    {
        JsonParser p = new VPackFactory().createParser(toVPack("42"));
        assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
        assertEquals(42, p.getIntValue());
        assertEquals(0, p.getTokenLocation().getByteOffset());
        assertEquals(5, p.getCurrentLocation().getByteOffset());

        assertNull(p.nextToken());
        assertEquals(5, p.getCurrentLocation().getByteOffset());
        p.close();
    }

}
