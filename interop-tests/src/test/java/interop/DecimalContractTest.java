package interop;

import java.math.*;
import java.util.*;
import java.util.stream.*;
import org.junit.jupiter.api.*;
import com.arangodb.jackson.dataformat.velocypack.*;
import tools.jackson.core.*;
import tools.jackson.core.exc.StreamConstraintsException;
import static interop.Harness.*;
import static org.junit.jupiter.api.Assertions.*;

class DecimalContractTest {
    static void decimal(byte[] b,BigDecimal n,boolean preserveScale) {
        Report.bytes("decimal",b);
        for(boolean stream:new boolean[]{false,true})try(var p=stream?DEFAULT.createParser(new Chunked(b,1)):DEFAULT.createParser(b)) {
            assertNotNull(p.nextToken());var actual=p.getDecimalValue();assertEquals(0,n.compareTo(actual),"exact decimal value");
            if(preserveScale)assertEquals(n.scale(),actual.scale());assertNull(p.nextToken());
        }
        nativeError("bcd-validate","validate",b,2,2,"Not implemented");
        nativeError("bcd-dump","dump",b,2,2,"Not implemented");
        Report.coverage("bcd",b[0]&255,"NATIVE_UNSUPPORTED_JAVA_EXACT");
    }
    @TestFactory Stream<DynamicTest> independentBcdVectorsAllLengthWidthsAndSigns() {
        var values=new ArrayList<BigDecimal>();
        for(String s:List.of("0","0.00","1","12","123","1234","123.45","-123.45","-0.0001","1.2300","10.0","1000","1E+1000000","-1E-1000000"))values.add(new BigDecimal(s));
        values.add(new BigDecimal(BigInteger.ONE,Integer.MAX_VALUE));values.add(new BigDecimal(BigInteger.ONE,Integer.MIN_VALUE+1));
        return values.stream().flatMap(n->IntStream.rangeClosed(1,8).mapToObj(w->{byte[] b=Spec.bcd(n,w);String id="bcd-"+n.toString()+"-width"+w;
            return ScalarFamiliesTest.test(id,Report.Policy.UNSUPPORTED_OPERATION,b,n,()->decimal(b,n,true));
        }));
    }
    @TestFactory Stream<DynamicTest> decimalAndBigIntegerDatabindingRemainSeparateFromStreaming() {
        return Stream.of("0.00","123.45","-123.4500","123456789012345678901234567890","1E+1000000","1E-1000000").map(s->{var n=new BigDecimal(s);String id="databinding-decimal-"+s;
            return DynamicTest.dynamicTest(id,()->Report.run(id,Report.Policy.UNSUPPORTED_OPERATION,null,n,()->{
                byte[] b=new VPackMapper().writeValueAsBytes(n);decimal(b,n,false);
                byte[] streaming=write(g->g.writeNumber(n),DEFAULT);decimal(streaming,n,false);
                if(n.scale()==0){var integer=n.toBigIntegerExact();byte[] bi=new VPackMapper().writeValueAsBytes(integer);equal(V.of(integer),javaRead(bi,false),"$.bigInteger",false);nativeError(id,"validate",bi,2,2,"Not implemented");}
            }));});
    }
    @TestFactory Stream<DynamicTest> precisionBudgetBoundaries() {
        return Stream.of(4095,4096,4097).map(digits->{var n=new BigDecimal(new BigInteger("1".repeat(digits)),3);byte[] b=Spec.bcd(n,2);String id="precision-"+digits;
            return ScalarFamiliesTest.test(id,digits<=4096?Report.Policy.EXACT_VALUE:Report.Policy.REJECTION,b,"4096-digit budget",()->{
                if(digits<=4096)decimal(b,n,true);
                else for(boolean stream:new boolean[]{false,true})assertThrows(StreamConstraintsException.class,()->javaRead(b,stream));
                var low=VPackFactory.builder().streamReadConstraints(StreamReadConstraints.builder().maxNumberLength(128).build()).build();
                assertThrows(StreamConstraintsException.class,()->javaRead(b,false,low));
                nativeError(id,"validate",b,2|1024,42,"BCD");
            });});
    }
    @Test void bigIntegersOutsideNativeRangesHaveExactJavaBcdResults() throws Throwable {
        Report.run("huge-integers",Report.Policy.UNSUPPORTED_OPERATION,null,"exact integer values; native BCD unsupported",()->{
            for(BigInteger n:List.of(BigInteger.valueOf(Long.MIN_VALUE).subtract(BigInteger.ONE),BigInteger.ONE.shiftLeft(64),BigInteger.TEN.pow(4095).subtract(BigInteger.ONE))) {
                byte[] b=write(g->g.writeNumber(n),DEFAULT);Report.bytes("integer",b);equal(V.of(n),javaRead(b,false),"$.buffer",false);equal(V.of(n),javaRead(b,true),"$.stream",false);nativeError("bcd-bigint","validate",b,2,2,"Not implemented");
            }
        });
    }
}
