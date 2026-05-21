package tools.jackson.databind.seq;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.*;
import tools.jackson.databind.exc.UnrecognizedPropertyException;
import tools.jackson.databind.testutil.DatabindTestUtil;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests to verify aspects of error recover for reading using
 * iterator.
 */
public class ReadRecoveryTest extends DatabindTestUtil
{
    public static class Bean {
        public int a, b;

        @Override
        public String toString() { return "{Bean, a="+a+", b="+b+"}"; }
    }

    /*
    /**********************************************************************
    /* Unit tests; root-level value sequences via Mapper
    /**********************************************************************
     */

    private final ObjectMapper MAPPER = newVPackMapper();

    // Similar to "raw" root-level Object sequence, but in array
    @Test
    public void testSimpleArrayRecovery() throws Exception
    {
        final String JSON = a2q("[{'a':3},{'a':27,'foo':[1,2],'b':{'x':3}}  ,{'a':1,'b':2}  ]");

        try (MappingIterator<Bean> it = MAPPER.readerFor(Bean.class)
                .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .readValues(VPackUtils.toVPack(JSON))) {
            Bean bean = it.nextValue();
        
            assertNotNull(bean);
            assertEquals(3, bean.a);
        
            // second one problematic
            try {
                it.nextValue();
            } catch (UnrecognizedPropertyException e) {
                verifyException(e, "Unrecognized property \"foo\"");
            }
        
            // but should recover nicely
            bean = it.nextValue();
            assertNotNull(bean);
            assertEquals(1, bean.a);
            assertEquals(2, bean.b);
        
            assertFalse(it.hasNextValue());
        }
    }
}
