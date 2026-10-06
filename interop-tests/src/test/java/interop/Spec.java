package interop;

import java.io.ByteArrayOutputStream;
import java.math.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/** Independent specification encoders; never calls production or native encoders. */
final class Spec {
    static byte[] cat(byte[]... parts) { var out=new ByteArrayOutputStream(); for(var p:parts)out.writeBytes(p); return out.toByteArray(); }
    static byte[] head(int head) { return new byte[]{(byte)head}; }
    static byte[] le(BigInteger n,int width) {
        byte[] b=new byte[width]; for(int i=0;i<width;i++) b[i]=n.shiftRight(8*i).byteValue(); return b;
    }
    static byte[] le(long n,int width) { return le(BigInteger.valueOf(n),width); }
    static byte[] integer(BigInteger n,int width,boolean unsigned) { return cat(head((unsigned?0x27:0x1f)+width),le(n,width)); }
    static byte[] doubleValue(double n) { return cat(head(0x1b),le(Double.doubleToRawLongBits(n),8)); }
    static byte[] string(String s,boolean longForm) {
        byte[] b=s.getBytes(StandardCharsets.UTF_8);
        return cat(longForm || b.length>126 ? cat(head(0xbf),le(b.length,8)):head(0x40+b.length),b);
    }
    static byte[] binary(byte[] b,int width) { return cat(head(0xbf+width),le(b.length,width),b); }
    static byte[] custom(int h,byte[] payload) {
        return cat(head(h),h<0xf4?new byte[0]:le(payload.length,1<<((h-0xf4)/3)),payload);
    }
    static byte[] bcd(BigDecimal n,int width) {
        String digits=n.unscaledValue().abs().toString(); if(digits.length()%2!=0)digits="0"+digits;
        byte[] payload=new byte[digits.length()/2];
        for(int i=0;i<payload.length;i++) payload[i]=(byte)((digits.charAt(i*2)-'0')*16+digits.charAt(i*2+1)-'0');
        return cat(head((n.signum()<0?0xcf:0xc7)+width),le(payload.length,width),le(-(long)n.scale(),4),payload);
    }
    static byte[] tag(BigInteger tag,boolean wide,byte[] value) { return cat(head(wide?0xef:0xee),le(tag,wide?8:1),value); }
    static byte[] vbyte(long n,boolean reverse) {
        var out=new ByteArrayOutputStream(); do {int b=(int)(n&127);n>>>=7;out.write(b+(n>0?128:0));}while(n>0);
        byte[] b=out.toByteArray(); if(reverse)for(int i=0,j=b.length-1;i<j;i++,j--){byte t=b[i];b[i]=b[j];b[j]=t;} return b;
    }
    static byte[] compact(boolean object,List<byte[]> values,int count) {
        byte[] payload=cat(values.toArray(byte[][]::new)), tail=vbyte(count,true);
        long size=1+1+payload.length+tail.length;
        while(1+vbyte(size,false).length+payload.length+tail.length!=size) size=1+vbyte(size,false).length+payload.length+tail.length;
        return cat(head(object?0x14:0x13),vbyte(size,false),payload,tail);
    }
    // Indexed arrays, all four offset widths, optionally NUL padded to offset 9.
    static byte[] indexedArray(List<byte[]> values,int width,boolean padding) {
        int offset=width==8?9:(padding?9:1+2*width);
        int payloadSize=values.stream().mapToInt(b->b.length).sum();
        long size=offset+payloadSize+(long)values.size()*width+(width==8?8:0);
        var out=new ByteArrayOutputStream(); out.write(6+Integer.numberOfTrailingZeros(width)); out.writeBytes(le(size,width));
        if(width!=8)out.writeBytes(le(values.size(),width));
        while(out.size()<offset)out.write(0);
        List<Integer> indexes=new ArrayList<>(); for(var v:values){indexes.add(out.size());out.writeBytes(v);}
        for(var i:indexes)out.writeBytes(le(i,width)); if(width==8)out.writeBytes(le(values.size(),8));
        return out.toByteArray();
    }
    static byte[] unindexedArray(List<byte[]> values,int width,boolean padding) {
        int offset=padding?9:1+width;
        int size=offset+values.stream().mapToInt(b->b.length).sum();
        var out=new ByteArrayOutputStream();out.write(2+Integer.numberOfTrailingZeros(width));out.writeBytes(le(size,width));
        while(out.size()<offset)out.write(0); for(var v:values)out.writeBytes(v); return out.toByteArray();
    }
    static byte[] indexedObject(int width,boolean sorted,boolean padding) {
        // Two unique keys in byte order, deliberately mixed value widths.
        byte[] a=cat(string("a",false),head(0x31)), z=cat(string("z",false),string("x",false));
        int offset=width==8?9:(padding?9:1+2*width),size=offset+a.length+z.length+2*width+(width==8?8:0);
        var out=new ByteArrayOutputStream();out.write((sorted?0x0b:0x0f)+Integer.numberOfTrailingZeros(width));out.writeBytes(le(size,width));
        if(width!=8)out.writeBytes(le(2,width));while(out.size()<offset)out.write(0);
        out.writeBytes(a);out.writeBytes(z);out.writeBytes(le(offset,width));out.writeBytes(le(offset+a.length,width));if(width==8)out.writeBytes(le(2,8));return out.toByteArray();
    }
}
