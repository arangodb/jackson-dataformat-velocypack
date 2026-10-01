package com.arangodb.jackson.dataformat.velocypack.debug;


import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.dataformat.velocypack.VPackMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class BigDecimalTest {

    @Test
    public void roundTripSholdKeepSameScale() throws IOException {

        final ObjectMapper jsonMapper = new ObjectMapper();
        final ObjectMapper vPackMapper = new VPackMapper();

        List<BigDecimal> input = Arrays.asList(
                new BigDecimal("1.1"),
                new BigDecimal("2.2"),
                new BigDecimal("1.3"),
                new BigDecimal("1.0"),
                new BigDecimal("1"),
                new BigDecimal("1.00"),
                new BigDecimal("1.000")
        );
        for (BigDecimal it : input) {
            doTestRoundTrip(jsonMapper, it);
            doTestRoundTrip(vPackMapper, it);
        }
    }

    private void doTestRoundTrip(ObjectMapper mapper, BigDecimal bd) throws IOException {
        byte[] serialized = mapper.writeValueAsBytes(bd);
        BigDecimal deserialized = mapper.readValue(serialized, BigDecimal.class);
        assertEquals(bd.scale(), deserialized.scale());
    }

}
