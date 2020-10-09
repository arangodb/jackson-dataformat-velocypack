package com.fasterxml.jackson.databind.deser.dos;

import com.arangodb.velocypack.VPackBuilder;
import com.fasterxml.jackson.databind.BaseMapTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

// for [databind#2157]
public class HugeIntegerCoerceTest extends BaseMapTest
{
    private final static int BIG_NUM_LEN = 199999;
    private final static String BIG_POS_INTEGER;
    static {
        StringBuilder sb = new StringBuilder(BIG_NUM_LEN);
        for (int i = 0; i < BIG_NUM_LEN; ++i) {
            sb.append('9');
        }
        BIG_POS_INTEGER = sb.toString();
    }

    private final ObjectMapper MAPPER = objectMapper(); // shared is fine
    
    public void testMaliciousLongForEnum() throws Exception
    {
        // Note: due to [jackson-core#488], fix verified with streaming over multiple
        // parser types. Here we focus on databind-level

        try {
            byte[] bytes = new VPackBuilder().add(BIG_POS_INTEGER).slice().toByteArray();
            /*ABC value =*/
            MAPPER.readValue(bytes, ABC.class);
            fail("Should not pass");
        } catch (InvalidFormatException e) {
            verifyException(e, "Cannot deserialize value of type");
        }
    }    
}
