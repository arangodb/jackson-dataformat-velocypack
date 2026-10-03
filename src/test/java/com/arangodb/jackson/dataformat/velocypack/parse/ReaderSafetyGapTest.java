package com.arangodb.jackson.dataformat.velocypack.parse;

import org.junit.jupiter.api.Test;
import tools.jackson.core.StreamReadConstraints;
import tools.jackson.core.exc.StreamConstraintsException;

import static com.arangodb.jackson.dataformat.velocypack.parse.ReaderRegressionTest.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Permanent read-limit regressions, originally introduced as opt-in safety probes. */
public class ReaderSafetyGapTest {
    @Test void shortStringMustHonorStringLimit() {
        var m = constrained(StreamReadConstraints.builder().maxStringLength(2).build());
        assertThrows(StreamConstraintsException.class, () -> transcript(m.createParser(string("abc"))));
    }
    @Test void nestedLongStringMustHonorStringLimit() {
        var m = constrained(StreamReadConstraints.builder().maxStringLength(128).build());
        byte[] fixture = indexed(false, false, 2, false, string("x".repeat(129)));
        assertThrows(StreamConstraintsException.class, () -> transcript(m.createParser(fixture)));
    }
    @Test void propertyNameMustHonorNameLimit() {
        var m = constrained(StreamReadConstraints.builder().maxNameLength(2).build());
        byte[] fixture = compact(true, string("abc"), bytes(0x31));
        assertThrows(StreamConstraintsException.class, () -> transcript(m.createParser(fixture)));
    }
    @Test void inputStreamMustHonorDocumentLimit() {
        var m = constrained(StreamReadConstraints.builder().maxDocumentLength(2).build());
        byte[] fixture = compact(false, bytes(0x31));
        assertThrows(StreamConstraintsException.class, () -> transcript(m.createParser(new ShortReads(fixture, 1))));
    }
}
