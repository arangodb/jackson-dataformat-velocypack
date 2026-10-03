package com.arangodb.jackson.dataformat.velocypack;
import java.util.*;
import java.math.*;
import java.io.*;
import tools.jackson.core.*;
public class BcdVectors {
    static void check(Number number,byte[] expected) {
        var out=new ByteArrayOutputStream();
        try(var g=new VPackGenerator(ObjectWriteContext.empty(),BaseTestForVPack.testIOContext(),StreamWriteFeature.collectDefaults(),VPackWriteFeature.collectDefaults(),out)){
            if(number instanceof BigDecimal d)g.writeNumber(d);else g.writeNumber((BigInteger)number);
        }
        if(!Arrays.equals(expected,out.toByteArray()))throw new AssertionError(number+": "+HexFormat.of().formatHex(out.toByteArray()));
        System.out.println("PASS independent BCD "+number+" ("+expected.length+" B)");
    }
    public static void main(String[] args) {
        var hex=HexFormat.of();
        check(new BigDecimal("123.45"),hex.parseHex("c803feffffff012345"));
        check(new BigDecimal("-123.45"),hex.parseHex("d003feffffff012345"));
        check(new BigDecimal("0.00001"),hex.parseHex("c801fbffffff01"));
        check(new BigDecimal("1E+6"),hex.parseHex("c8010600000001"));
        check(new BigInteger("18446744073709551616"),hex.parseHex("c80a0000000018446744073709551616"));
        byte[] wide=new byte[263];wide[0]=(byte)0xc9;wide[2]=1;Arrays.fill(wide,7,wide.length,(byte)0x11);
        check(new BigInteger("1".repeat(512)),wide);
    }
}
