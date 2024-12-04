package com.fasterxml.jackson.core.read.loc;

import com.arangodb.jackson.dataformat.velocypack.VPackFactory;
import com.fasterxml.jackson.core.JUnit5TestBase;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static com.fasterxml.jackson.VPackUtils.toVPack;
import static org.junit.Assert.assertEquals;


/**
 * Set of tests that checks getCurrentLocation() and getTokenLocation() are as expected during
 * parsing.
 */
public class LocationDuringStreamParsingTest extends JUnit5TestBase
{
    @Test
    public void locationAtEndOfParse() throws Exception
    {
        for (LocationTestCase test : LocationTestCase.values()) {
            //System.out.println(test.name());
            locationAtEndOfParse(test);
        }
    }

// FIXME
//    @Test
//    public void initialLocation() throws Exception
//    {
//        for (LocationTestCase test : LocationTestCase.values()) {
//            System.out.println(test.name());
//            initialLocation(test);
//        }
//    }

    @Test
    public void tokenLocations() throws Exception
    {
        for (LocationTestCase test : LocationTestCase.values()) {
            //System.out.println(test.name());
            tokenLocations(test);
        }
    }

    private void locationAtEndOfParse(LocationTestCase test) throws Exception
    {
        JsonParser p = new VPackFactory().createParser(toVPack(test.json));
        while (p.nextToken() != null) {
            p.nextToken();
        }
        assertCurrentLocation(p, test.getFinalLocation());
        p.close();
    }

    private void initialLocation(LocationTestCase test) throws Exception
    {
        JsonParser p = new VPackFactory().createParser(toVPack(test.json));
        JsonLocation loc = p.currentLocation();
        p.close();

        assertLocation(loc, at(1, 1, 0));
    }

    private void tokenLocations(LocationTestCase test) throws Exception
    {
        JsonParser p = new VPackFactory().createParser(toVPack(test.json));
        int i = 0;
        while (p.nextToken() != null) {
            assertTokenLocation(p, test.locations.get(i));
            i++;
        }
        assertEquals(test.locations.size(), i + 1); // last LocData is end of stream
        p.close();
    }

    private void assertCurrentLocation(JsonParser p, LocData loc)
    {
        assertLocation(p.currentLocation(), loc);
    }

    private void assertTokenLocation(JsonParser p, LocData loc)
    {
        assertLocation(p.currentTokenLocation(), loc);
    }

    private void assertLocation(JsonLocation pLoc, LocData loc)
    {
        String expected = String.format("(%d, %d, %d)",
                loc.lineNumber, loc.columnNumber, loc.offset);
        String actual = String.format("(%d, %d, %d)", pLoc.getLineNr(), pLoc.getColumnNr(),
                pLoc.getByteOffset() == -1 ? pLoc.getCharOffset() : pLoc.getByteOffset());
        assertEquals(expected, actual);
    }

    static class LocData
    {
        long lineNumber;
        long columnNumber;
        long offset;

        LocData(long lineNumber, long columnNumber, long offset)
        {
            this.lineNumber = lineNumber;
            this.columnNumber = columnNumber;
            this.offset = offset;
        }
    }

    static LocData at(long lineNumber, long columnNumber, long offset)
    {
        return new LocData(lineNumber, columnNumber, offset);
    }

    /**
     * Adapted liberally from https://github.com/leadpony/jsonp-test-suite, also
     * released under the Apache License v2.
     */
    enum LocationTestCase
    {
        SIMPLE_VALUE("42", at(-1, -1, 0), at(-1, -1, 5)),

        SIMPLE_VALUE_WITH_MULTIBYTE_CHARS("\"Правда\"",
                at(-1, -1, 0),
                at(-1, -1, 13)
        ),

        SIMPLE_VALUE_INCLUDING_SURROGATE_PAIR_CHARS("\"a П \uD83D\uDE01\"",
                at(-1, -1, 0),
                at(-1, -1, 10)
        ),

        ARRAY_IN_ONE_LINE("[\"hello\",42,true]",
                at(-1, -1, 0), // [
                at(-1, -1, 3), // "hello"
                at(-1, -1, 9), // 42
                at(-1, -1, 14), // true
                at(-1, -1, 14), // ]
                at(-1, -1, 15) // end of input
        ),

        OBJECT_IN_ONE_LINE("{\"first\":\"hello\",\"second\":42}",
                at(-1, -1, 0), // {
                at(-1, -1, 9), // "first"
                at(-1, -1, 9), // "hello"
                at(-1, -1, 22), // "second"
                at(-1, -1, 22), // 42
                at(-1, -1, 22), // }
                at(-1, -1, 27) // end of input
        ),
        ;

        final String json;
        final List<LocData> locations;

        LocationTestCase(String json, LocData... locations)
        {
            this.json = json;
            this.locations = Arrays.asList(locations);
        }

        LocData getFinalLocation()
        {
            return locations.get(locations.size() - 1);
        }
    }

}
