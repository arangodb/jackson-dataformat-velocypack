package tools.jackson.core.unittest.util;

import tools.jackson.core.Version;
import tools.jackson.core.util.VersionUtil;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0116F1 {

    void packageVersionMatchesVpack() {
        assertEquals(PackageVersion.VERSION, VersionUtil.versionFor(VPackGenerator.class));
    }

    void parseVersionPartReturningPositiveVpack() {
        assertEquals(66, VersionUtil.parseVersionPart("66R"));
    }

    void parseVersionReturningVersionWhereGetMajorVersionIsZeroVpack() {
        Version version = VersionUtil.parseVersion("#M&+m@569P", "#M&+m@569P",
                "com.fasterxml.jackson.core.util.VersionUtil");

        assertEquals(0, version.getMinorVersion());
        assertEquals(0, version.getPatchLevel());
        assertEquals(0, version.getMajorVersion());
        assertFalse(version.isSnapshot());
        assertFalse(version.isUnknownVersion());
    }

    void parseVersionWithEmptyStringAndEmptyStringVpack() {
        Version version = VersionUtil.parseVersion("", "", "\"g2AT");
        assertTrue(version.isUnknownVersion());
    }

    void parseVersionWithNullAndEmptyStringVpack() {
        Version version = VersionUtil.parseVersion(null, "/nUmRN)3", "");
        assertFalse(version.isSnapshot());
    }

    void versionForUnknownVersionVpack() {
        assertEquals(Version.unknownVersion(), VersionUtil.versionFor(VersionUtil.class));
    }

    void __invoke_packageVersionMatchesVpack() throws Exception {
        try {
            packageVersionMatchesVpack();
        } finally {
        }
    }


    void __invoke_parseVersionPartReturningPositiveVpack() throws Exception {
        try {
            parseVersionPartReturningPositiveVpack();
        } finally {
        }
    }


    void __invoke_parseVersionReturningVersionWhereGetMajorVersionIsZeroVpack() throws Exception {
        try {
            parseVersionReturningVersionWhereGetMajorVersionIsZeroVpack();
        } finally {
        }
    }


    void __invoke_parseVersionWithEmptyStringAndEmptyStringVpack() throws Exception {
        try {
            parseVersionWithEmptyStringAndEmptyStringVpack();
        } finally {
        }
    }


    void __invoke_parseVersionWithNullAndEmptyStringVpack() throws Exception {
        try {
            parseVersionWithNullAndEmptyStringVpack();
        } finally {
        }
    }


    void __invoke_versionForUnknownVersionVpack() throws Exception {
        try {
            versionForUnknownVersionVpack();
        } finally {
        }
    }

}
