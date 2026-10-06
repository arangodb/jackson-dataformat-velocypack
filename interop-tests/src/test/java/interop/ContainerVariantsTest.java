package interop;

import java.math.*;
import java.util.*;
import java.util.stream.*;
import org.junit.jupiter.api.*;
import com.arangodb.jackson.dataformat.velocypack.*;
import static interop.Harness.*;
import static org.junit.jupiter.api.Assertions.*;

class ContainerVariantsTest {
    //FIXME
    // The valid 0x0e eight-byte indexed object is rejected by pinned Validator
    // with code 50 "Object has fewer items than in index". VelocyPack.md's
    // Objects section permits it; Validator::validateIndexedObject sets
    // firstMember=ptr+byteSize. Java VPackParser._startContainerRoot reads it.
    @TestFactory Stream<DynamicTest> allContainerHeadFamiliesWithAndWithoutPadding() {
        var tests=new ArrayList<DynamicTest>();
        for(int w:new int[]{1,2,4,8})for(boolean pad:new boolean[]{false,true}) {
            String id="indexed-array-width"+w+"-pad="+pad;byte[] b=Spec.indexedArray(List.of(Spec.head(0x31),Spec.string("two",false),Spec.head(0x18)),w,pad);
            tests.add(ScalarFamiliesTest.test(id,Report.Policy.EXACT_VALUE,b,Arrays.asList(1,"two",null),()->wire(id,b,V.of(Arrays.asList(1,"two",null)),"array")));
            String equalId="unindexed-array-width"+w+"-pad="+pad;byte[] equal=Spec.unindexedArray(List.of(Spec.head(0x19),Spec.head(0x1a),Spec.head(0x18)),w,pad);
            tests.add(ScalarFamiliesTest.test(equalId,Report.Policy.EXACT_VALUE,equal,Arrays.asList(false,true,null),()->wire(equalId,equal,V.of(Arrays.asList(false,true,null)),"array")));
            for(boolean sorted:new boolean[]{false,true}){
                String objId="object-width"+w+"-sorted="+sorted+"-pad="+pad;byte[] obj=Spec.indexedObject(w,sorted,pad);
                tests.add(ScalarFamiliesTest.test(objId,sorted?Report.Policy.EXACT_VALUE:Report.Policy.REJECTION,obj,Map.of("a",1,"z","x"),()->{
                    if(sorted)wire(objId,obj,V.of(Map.of("a",1,"z","x")),"object");
                    else {for(boolean stream:new boolean[]{false,true})equal(V.of(Map.of("a",1,"z","x")),javaRead(obj,stream),"$",false);nativeError(objId,"validate",obj,2,51,"Invalid type");Report.coverage("object",obj[0]&255,"NATIVE_REJECTED_LEGACY_UNSORTED");}
                }));
            }
        }return tests.stream();
    }
    @TestFactory Stream<DynamicTest> lengthCountAndIndexTransitions() {
        var tests=new ArrayList<DynamicTest>();
        for(int count:new int[]{1,2,9,10,120,121,122,123,126,127,128,247,248,253,254,255,256,16374,16375,16376,16377,16383,16384,65522,65523,65532,65533,65535,65536}) {
            for(boolean compact:new boolean[]{false,true}) {
                String id="array-count"+count+"-compact="+compact;var values=Collections.nCopies(count,1);String source=JSON.writeValueAsString(values);
                tests.add(DynamicTest.dynamicTest(id,()->Report.run(id,Report.Policy.NUMERIC_VALUE,source,"array of "+count+" integers",()->differential(id,source,V.of(values),compact,true))));
            }
        }
        for(int size:new int[]{120,121,122,123,126,127,128,247,248,253,254,255,256,16374,16375,16376,16377,65522,65523,65524}) {
            String s="x".repeat(size);Object value=Map.of("long",Arrays.asList(null,s,1),"short",1);String source=JSON.writeValueAsString(value);
            for(boolean compact:new boolean[]{true,false}){String id="container-length-"+size+"-compact="+compact;
                tests.add(DynamicTest.dynamicTest(id,()->Report.run(id,Report.Policy.NUMERIC_VALUE,source,"exact nested value",()->{
                    differential(id,source,V.of(value),compact,true);
                    byte[] padded=REF.parse(source,2|4|128|(compact?1:0));equal(V.of(value),javaRead(padded,false),"$.padded",false);equal(V.of(value),javaRead(padded,true),"$.paddedStream",false);
                })));
            }
        }return tests.stream();
    }
    @Test void nestedExtendedValuesAndNumericAttributeTranslation() throws Throwable {
        Report.run("nested-extended",Report.Policy.JSON_PROJECTION,null,"binary/custom/date/tag projections and numeric key policies",()->{
            byte[] date=Spec.cat(Spec.head(0x1c),Spec.le(-1,8));
            byte[] binary=Spec.binary(new byte[]{0,1,(byte)255},2), custom=Spec.custom(0xf8,new byte[]{1,2,3});
            byte[] tag=Spec.tag(BigInteger.valueOf(256),true,Spec.string("v",false));
            byte[] b=Spec.compact(false,List.of(binary,custom,date,tag),4);
            V e=new V("array",List.of(new V("binary","0001ff"),new V("custom",0xf8+":010203"),V.of(-1),V.of("v")));
            wire("nested-extended",b,e,"array");
            for(int width=1;width<=8;width++){
                byte[] key=Spec.integer(BigInteger.valueOf(6),width,true),obj=Spec.compact(true,List.of(key,Spec.head(0x35)),1);Report.bytes("numericKey",obj);
                wire("numeric-key",obj,V.of(Map.of("6",5)),"object");
                nativeError("untranslated-key","dump",obj,2,20,"translator");
                equal(V.of(Map.of("id6",5)),json(REF.call("dump",obj,2|64).text()),"$.translated",false);
            }
            byte[] java=write(g->{g.writeStartObject();g.writePropertyId(6);g.writeNumber(5);g.writeEndObject();},DEFAULT);
            // Jackson's property-id API writes the decimal string name; native
            // attribute translation applies only to integer keys on the wire.
            equal(V.of(Map.of("6",5)),javaRead(java,false),"$.propertyId",false);
            equal(V.of(Map.of("6",5)),nativeValue(inspection(java)),"$.propertyIdNative",false);
        });
    }
}
