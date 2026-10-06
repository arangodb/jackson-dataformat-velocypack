package interop;

import java.math.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.junit.jupiter.api.*;
import com.arangodb.jackson.dataformat.velocypack.*;
import tools.jackson.core.exc.*;
import static interop.Harness.*;
import static org.junit.jupiter.api.Assertions.*;

/** Active regressions: requirements are not relaxed to match current defects. */
class UnexpectedDefectsTest {
    //FIXME
    // Input is the reduced root JSON fraction -0.12 with no delimiter.
    // Expected its correctly rounded IEEE-754 double; pinned C++ returns
    // -0.12000000000000001 (one ULP away). Parser::parseNumber takes the EOF
    // scanDigitsFractional branch instead of atof. Java's corresponding
    // production path is VPackGenerator.writeNumber(double)/_doWriteDouble.
    @Test void upstreamRootFractionMustBeCorrectlyRounded() throws Throwable {
        String source="-0.12";
        Report.run("upstream-root-fraction",Report.Policy.EXACT_VALUE,source,V.of(-0.12),()->differential("upstream-root-fraction",source,V.of(-0.12),true,true));
    }
    //FIXME
    // Input 0e plus valid 8-byte length, member pairs, offsets, and count.
    // Expected native validation success and exact object values; observed
    // Validator code 50. The pinned VelocyPack.md Objects layout permits
    // this encoding, but Validator::validateIndexedObject initializes
    // firstMember to the end. Java location: VPackParser._startContainerRoot.
    @Test void upstreamValidatorMustAcceptEightByteIndexedObject() throws Throwable {
        byte[] b=Spec.indexedObject(8,true,false);
        Report.run("upstream-object-eight-byte",Report.Policy.EXACT_VALUE,null,Map.of("a",1,"z","x"),()->wire("upstream-object-eight-byte",b,V.of(Map.of("a",1,"z","x")),"object"));
    }
    //FIXME
    // Input 42 c0 af is an overlong UTF-8 slash. Expected strict read failure;
    // observed two replacement characters. Pinned Validator returns code 15.
    // VPackParser._readShortString/_readLongString use new String(UTF_8)
    // without consulting LENIENT_UTF_ENCODING, including buffered spans.
    @Test void strictReaderMustRejectInvalidUtf8Value() throws Throwable {
        byte[] b={0x42,(byte)0xc0,(byte)0xaf};
        Report.run("strict-invalid-utf8-value",Report.Policy.REJECTION,null,"StreamReadException",()->{
            nativeError("utf8-native","validate",b,2,15,"UTF-8");javaReject(b,DEFAULT,"UTF");
        });
    }
    //FIXME
    // Same overlong sequence used as an object name. Expected strict read
    // failure; observed replacement name. Pinned Validator returns code 15.
    // Production location: VPackParser._decodePropertyName/_findPropertyName.
    @Test void strictReaderMustRejectInvalidUtf8Name() throws Throwable {
        byte[] b=Spec.compact(true,List.of(new byte[]{0x42,(byte)0xc0,(byte)0xaf},Spec.head(0x18)),1);
        Report.run("strict-invalid-utf8-name",Report.Policy.REJECTION,null,"StreamReadException",()->{
            nativeError("utf8-name-native","validate",b,2,15,"UTF-8");javaReject(b,DEFAULT,"UTF");
        });
    }
    //FIXME
    // Input String containing an unpaired high surrogate U+D800. Expected
    // StreamWriteException with default strict feature; observed "?".
    // Pinned Parser rejects JSON "\ud800" with code 15 when UTF checks are on.
    // VPackGenerator._utf8Length/_codePoint deliberately ignore the feature.
    @Test void strictWriterMustRejectUnpairedSurrogate() throws Throwable {
        Report.run("strict-unpaired-surrogate",Report.Policy.REJECTION,"\"\\ud800\"","StreamWriteException",()->{
            nativeError("surrogate-native","parse","\"\\ud800\"".getBytes(StandardCharsets.UTF_8),2,15,"surrogate");
            assertThrows(StreamWriteException.class,()->{
                byte[] b=write(g->g.writeString("\ud800"),DEFAULT);Report.bytes("unexpectedWriter",b);
                Report.detail("unexpectedValue",javaRead(b,false).toString());
            });
        });
    }
    //FIXME
    // Input U+D800 with LENIENT_UTF_ENCODING enabled. Feature documentation
    // declares U+FFFD replacement; observed "?" from VPackGenerator._codePoint.
    // Upstream's strict parser rejects the original; the independently
    // specified replacement is valid UTF-8 and validates natively.
    @Test void lenientWriterMustUseUnicodeReplacementCharacter() throws Throwable {
        Report.run("lenient-surrogate-replacement",Report.Policy.EXACT_VALUE,null,"U+FFFD",()->{
            var f=VPackFactory.builder().enable(VPackWriteFeature.LENIENT_UTF_ENCODING).build();
            byte[] b=write(g->g.writeString("\ud800"),f);Report.bytes("java",b);
            equal(V.of("\ufffd"),javaRead(b,false),"$.replacement",false);
        });
    }
    //FIXME
    // Packed BCD digit 0xfa has nibbles outside 0..9. Expected read error;
    // observed decimal 1510 from concatenating nibble values. VelocyPack.md
    // Packed BCD section specifies two decimal digits per byte; pinned
    // native validation/dumping returns NotImplemented for all BCD.
    // Production location: VPackUtil.getDigits/decodeBcd and VPackParser._readBcdFloat.
    @Test void invalidBcdDigitsMustBeRejected() throws Throwable {
        byte[] b={(byte)0xc8,1,0,0,0,0,(byte)0xfa};
        Report.run("invalid-bcd-digit",Report.Policy.REJECTION,null,"invalid packed decimal digit",()->{
            assertEquals(2,isolatedError("invalid-bcd-native","validate",b,2,2).code());
            Report.bytes("input",b);javaReject(b,DEFAULT,"BCD");
        });
    }
    //FIXME
    // BigDecimal(1, Integer.MIN_VALUE) needs BCD exponent +2147483648,
    // outside the specification's signed 32-bit field. Expected writer
    // rejection; observed wrapped exponent -2147483648. Native BCD code 2
    // cannot check this. Production: VPackUtil.encodeBcd's int exponent=-scale.
    @Test void writerMustRejectScaleOutsideBcdExponentRange() throws Throwable {
        Report.run("bcd-scale-overflow-write",Report.Policy.REJECTION,null,"exponent range error",()->{
            var value=new BigDecimal(BigInteger.ONE,Integer.MIN_VALUE);
            assertThrows(StreamWriteException.class,()->{
                byte[] b=write(g->g.writeNumber(value),DEFAULT);Report.bytes("unexpectedWriter",b);
                Report.detail("unexpectedValue",javaRead(b,false).toString());
            });
        });
    }
    //FIXME
    // Wire BCD exponent -2147483648 denotes scale +2147483648, which Java
    // BigDecimal cannot represent. Expected explicit read error; observed
    // scale -2147483648 from integer overflow. VelocyPack.md specifies the
    // signed exponent; native BCD is unsupported. Production: VPackUtil.decodeBcd.
    @Test void readerMustRejectUnrepresentableBcdScale() throws Throwable {
        byte[] b=Spec.cat(Spec.head(0xc8),Spec.head(1),Spec.le(Integer.MIN_VALUE,4),Spec.head(1));
        Report.run("bcd-scale-overflow-read",Report.Policy.REJECTION,null,"unrepresentable scale error",()->javaReject(b,DEFAULT,"scale"));
    }
}
