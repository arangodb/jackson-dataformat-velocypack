package com.arangodb.jackson.dataformat.velocypack.debug;

import com.arangodb.jackson.dataformat.velocypack.VPackMapper;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class BigDecimalTest {

    @Test
    public void roundTripSholdKeepSameScale() {

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

    private void doTestRoundTrip(ObjectMapper mapper, BigDecimal bd) {
        byte[] serialized = mapper.writeValueAsBytes(bd);
        BigDecimal deserialized = mapper.readValue(serialized, BigDecimal.class);
        assertThat(deserialized.scale()).isEqualTo(bd.scale());
    }

}
