package tools.jackson.core.unittest.read;

import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0076Fixture {

    void jsonOnlyNumberSpellingsRemainOutsideVpackWireSurface() {
        VPackFactory factory = new VPackFactory();
        for (String json : new String[] {
                "[.5]", "[-.5]", "[+.5]", "[+0x10]", "[-0x10]", "[.5.]"
        }) {
            assertThrows(UnsupportedOperationException.class,
                    () -> factory.createParser(json), json);
        }
    }

    void __invoke_jsonOnlyNumberSpellingsRemainOutsideVpackWireSurface() throws Exception {
        try {
            jsonOnlyNumberSpellingsRemainOutsideVpackWireSurface();
        } finally {
        }
    }

}
