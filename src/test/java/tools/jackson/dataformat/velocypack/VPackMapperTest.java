package tools.jackson.dataformat.velocypack;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;

class VPackMapperTest {
    private final VPackMapper mapper = new VPackMapper();

    @Test
    void mapsBasicRecordListAndMapValues() throws Exception {
        BasicRecord input = new BasicRecord("hello", 37, List.of(true, "x"),
                Map.of("answer", 42));

        byte[] encoded = mapper.writeValueAsBytes(input);
        BasicRecord output = mapper.readValue(encoded, BasicRecord.class);

        assertEquals(input, output);
    }

    @Test
    void mapperRegistersOnlyOneNativeModule() {
        VPackMapper explicit = VPackMapper.builder()
                .addModule(new VPackModule())
                .build();

        assertEquals(1, explicit.registeredModules().size());
        assertInstanceOf(VPackModule.class, explicit.registeredModules().iterator().next());
    }

    @Test
    void untypedNativeDateRemainsLong() throws Exception {
        byte[] date = { 0x1C, 0x15, (byte) 0xCD, 0x5B, 0x07, 0, 0, 0, 0 };

        Object value = mapper.readValue(date, Object.class);

        assertEquals(123456789L, value);
    }

    @Test
    void factoryAndMapperUseTheSuppliedImmutableFactory() {
        VPackFactory factory = VPackFactory.builder()
                .vpackReadConstraints(VPackReadConstraints.builder()
                        .maxRootValueBytes(19).build())
                .build();
        VPackMapper first = VPackMapper.builder(factory).build();
        VPackMapper second = VPackMapper.builder(factory).build();

        assertSame(factory, first.tokenStreamFactory());
        assertSame(factory, second.tokenStreamFactory());
    }

    record BasicRecord(String text, int number, List<Object> values, Map<String, Object> map) { }
}
