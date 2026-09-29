package tools.jackson.databind.util;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.util.ExceptionUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0626F0 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();

    // Provenance: ExceptionUtilTest#testNoClassDefError().
    void exceptionUtilNoClassDefErrorVpack() {
        ExceptionUtil.rethrowIfFatal(new NoClassDefFoundError("fake"));
    }

    // Provenance: ExceptionUtilTest#testExceptionInInitializerError().
    void exceptionUtilInitializerErrorVpack() {
        ExceptionUtil.rethrowIfFatal(new ExceptionInInitializerError("fake"));
    }

    // Provenance: ExceptionUtilTest#testOutOfMemoryError().
    void exceptionUtilOutOfMemoryErrorVpack() {
        OutOfMemoryError error = assertThrows(OutOfMemoryError.class,
                () -> ExceptionUtil.rethrowIfFatal(new OutOfMemoryError("fake")));
        assertEquals("fake", error.getMessage());
    }

    // Provenance: ExceptionUtilTest#testVerifyError().
    void exceptionUtilVerifyErrorVpack() {
        VerifyError error = assertThrows(VerifyError.class,
                () -> ExceptionUtil.rethrowIfFatal(new VerifyError("fake")));
        assertEquals("fake", error.getMessage());
    }

    void __invoke_exceptionUtilNoClassDefErrorVpack() throws Exception {
        try {
            exceptionUtilNoClassDefErrorVpack();
        } finally {
        }
    }


    void __invoke_exceptionUtilInitializerErrorVpack() throws Exception {
        try {
            exceptionUtilInitializerErrorVpack();
        } finally {
        }
    }


    void __invoke_exceptionUtilOutOfMemoryErrorVpack() throws Exception {
        try {
            exceptionUtilOutOfMemoryErrorVpack();
        } finally {
        }
    }


    void __invoke_exceptionUtilVerifyErrorVpack() throws Exception {
        try {
            exceptionUtilVerifyErrorVpack();
        } finally {
        }
    }

}
