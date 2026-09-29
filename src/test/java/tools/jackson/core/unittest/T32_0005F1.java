package tools.jackson.core.unittest;

import tools.jackson.core.Version;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class T32_0005F1 {

    void compareToOne() {
        Version version = Version.unknownVersion();
        Version versionTwo = new Version(0, -263, -1820, "", "", "");

        assertEquals(263, version.compareTo(versionTwo));
    }

    void compareToReturningZero() {
        Version version = Version.unknownVersion();
        Version versionTwo = new Version(0, 0, 0, "", "", "");

        assertEquals(0, version.compareTo(versionTwo));
    }

    void createsVersionTaking6ArgumentsAndCallsCompareTo() {
        Version version = new Version(0, 0, 0, null, null, "");
        Version versionTwo = new Version(0, 0, 0, "", "", "//0.0.0");

        assertTrue(version.compareTo(versionTwo) < 0);
    }

    void compareToTwo() {
        Version version = Version.unknownVersion();
        Version versionTwo = new Version(-1, 0, 0, "SNAPSHOT", "groupId", "artifactId");

        int diff = version.compareTo(versionTwo);
        assertTrue(diff < 0, "Diff should be negative, was: " + diff);
    }

    void compareToAndCreatesVersionTaking6ArgumentsAndUnknownVersion() {
        Version version = Version.unknownVersion();
        Version versionTwo = new Version(0, 0, 0, "SNAPSHOT", "groupId", "artifactId");

        assertTrue(version.compareTo(versionTwo) < 0);
    }

    void compareToSnapshotSame() {
        Version version = new Version(0, 0, 0, "alpha", "com.fasterxml", "bogus");
        Version versionTwo = new Version(0, 0, 0, "alpha", "com.fasterxml", "bogus");

        assertEquals(0, version.compareTo(versionTwo));
    }

    void compareToSnapshotDifferent() {
        Version version = new Version(0, 0, 0, "alpha", "com.fasterxml", "bogus");
        Version versionTwo = new Version(0, 0, 0, "beta", "com.fasterxml", "bogus");

        assertTrue(version.compareTo(versionTwo) < 0);
        assertTrue(versionTwo.compareTo(version) > 0);
    }

    void compareWhenOnlyFirstHasSnapshot() {
        Version version = new Version(0, 0, 0, "beta", "com.fasterxml", "bogus");
        Version versionTwo = new Version(0, 0, 0, null, "com.fasterxml", "bogus");

        assertTrue(version.compareTo(versionTwo) < 0);
        assertTrue(versionTwo.compareTo(version) > 0);
    }

    void compareWhenOnlySecondHasSnapshot() {
        Version version = new Version(0, 0, 0, "", "com.fasterxml", "bogus");
        Version versionTwo = new Version(0, 0, 0, "beta", "com.fasterxml", "bogus");

        assertTrue(version.compareTo(versionTwo) > 0);
        assertTrue(versionTwo.compareTo(version) < 0);
    }

    void __invoke_compareToOne() throws Exception {
        try {
            compareToOne();
        } finally {
        }
    }


    void __invoke_compareToReturningZero() throws Exception {
        try {
            compareToReturningZero();
        } finally {
        }
    }


    void __invoke_createsVersionTaking6ArgumentsAndCallsCompareTo() throws Exception {
        try {
            createsVersionTaking6ArgumentsAndCallsCompareTo();
        } finally {
        }
    }


    void __invoke_compareToTwo() throws Exception {
        try {
            compareToTwo();
        } finally {
        }
    }


    void __invoke_compareToAndCreatesVersionTaking6ArgumentsAndUnknownVersion() throws Exception {
        try {
            compareToAndCreatesVersionTaking6ArgumentsAndUnknownVersion();
        } finally {
        }
    }


    void __invoke_compareToSnapshotSame() throws Exception {
        try {
            compareToSnapshotSame();
        } finally {
        }
    }


    void __invoke_compareToSnapshotDifferent() throws Exception {
        try {
            compareToSnapshotDifferent();
        } finally {
        }
    }


    void __invoke_compareWhenOnlyFirstHasSnapshot() throws Exception {
        try {
            compareWhenOnlyFirstHasSnapshot();
        } finally {
        }
    }


    void __invoke_compareWhenOnlySecondHasSnapshot() throws Exception {
        try {
            compareWhenOnlySecondHasSnapshot();
        } finally {
        }
    }

}
