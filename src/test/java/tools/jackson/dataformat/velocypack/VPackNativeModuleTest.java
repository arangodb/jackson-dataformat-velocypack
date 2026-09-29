package tools.jackson.dataformat.velocypack;

import org.junit.jupiter.api.Test;

import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.exc.MismatchedInputException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VPackNativeModuleTest {
    private final VPackMapper mapper = new VPackMapper();

    @Test
    void typedNativeWrappersRoundTripFromIndependentWireBytes() throws Exception {
        byte[] date = { 0x1C, (byte) 0xFE, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF };

        assertEquals(new VPackDate(-2L), mapper.readValue(date, VPackDate.class));
        assertEquals(VPackSpecialValue.MIN_KEY,
                mapper.readValue(new byte[] { 0x1E }, VPackSpecialValue.class));
        assertEquals(VPackSpecialValue.MAX_KEY,
                mapper.readValue(new byte[] { 0x1F }, VPackSpecialValue.class));
    }

    @Test
    void typedWrappersPreservePhysicalMarkersInsideRecords() throws Exception {
        NativeRecord input = new NativeRecord(new VPackDate(7L), VPackSpecialValue.MAX_KEY,
                9007199254740993L);

        NativeRecord output = mapper.readValue(mapper.writeValueAsBytes(input), NativeRecord.class);

        assertEquals(input, output);
    }

    @Test
    void ordinaryLongIsNotEncodedAsDate() throws Exception {
        byte[] encoded = mapper.writeValueAsBytes(7L);

        try (VPackParser parser = (VPackParser) mapper.tokenStreamFactory().createParser(encoded)) {
            parser.nextToken();
            assertEquals(VPackType.SMALL_INTEGER, parser.currentVPackType());
        }
    }

    @Test
    void incompatibleParserAndGeneratorFailuresIdentifyTheRequiredNativeBackend() {
        MismatchedInputException parserFailure = assertThrows(MismatchedInputException.class,
                () -> mapper.readValue(new byte[] { 0x30 }, VPackDate.class));
        assertEquals(true, parserFailure.getMessage().contains("physical VPack DATE"));

        JsonMapper jsonMapper = JsonMapper.builder().addModule(new VPackModule()).build();
        IllegalStateException generatorFailure = assertThrows(IllegalStateException.class,
                () -> jsonMapper.writeValueAsBytes(new VPackDate(1L)));
        assertEquals(true, generatorFailure.getMessage().contains("VPackGenerator"));
    }

    record NativeRecord(VPackDate date, VPackSpecialValue special, long exactLong) { }
}
