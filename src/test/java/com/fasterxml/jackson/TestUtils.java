package com.fasterxml.jackson;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.json.PackageVersion;

public final class TestUtils {
    private TestUtils() {
    }

    public static boolean isAtLeastVersion(int reqMajor, int reqMinor) {
        Version version = PackageVersion.VERSION;
        int major = version.getMajorVersion();
        int minor = version.getMinorVersion();

        if(reqMajor < major) return true;
        if(reqMajor > major) return false;
        return reqMinor <= minor;
    }

}
