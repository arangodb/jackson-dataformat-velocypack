package tools.jackson.core.unittest;

import tools.jackson.core.Version;
import static org.junit.jupiter.api.Assertions.assertEquals;

class T32_0006Fixture {

    void equalsAndHashCode() {
        Version version1 = new Version(1, 2, 3, "", "", "");
        Version version2 = new Version(1, 2, 3, "", "", "");

        assertEquals(version1, version2);
        assertEquals(version2, version1);
        assertEquals(version1.hashCode(), version2.hashCode());
    }

    void __invoke_equalsAndHashCode() throws Exception {
        try {
            equalsAndHashCode();
        } finally {
        }
    }

}
