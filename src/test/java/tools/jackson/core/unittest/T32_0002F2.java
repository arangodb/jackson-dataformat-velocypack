package tools.jackson.core.unittest;

import tools.jackson.core.ErrorReportConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0002F2 {

    void builderConstructorWithErrorReportConfiguration() {
        ErrorReportConfiguration config = ErrorReportConfiguration.builder()
                .maxErrorTokenLength(1234)
                .maxRawContentLength(5678)
                .build();
        VPackFactory original = VPackFactory.builder()
                .errorReportConfiguration(config).build();
        VPackFactory rebuilt = original.rebuild().build();

        assertEquals(config.getMaxErrorTokenLength(), rebuilt.errorReportConfiguration()
                .getMaxErrorTokenLength());
        assertEquals(config.getMaxRawContentLength(), rebuilt.errorReportConfiguration()
                .getMaxRawContentLength());
        assertNotSame(original, rebuilt);
    }

    void defaults() {
        ErrorReportConfiguration defaults = ErrorReportConfiguration.defaults();
        VPackFactory factory = new VPackFactory();

        assertEquals(ErrorReportConfiguration.DEFAULT_MAX_ERROR_TOKEN_LENGTH,
                defaults.getMaxErrorTokenLength());
        assertEquals(ErrorReportConfiguration.DEFAULT_MAX_RAW_CONTENT_LENGTH,
                defaults.getMaxRawContentLength());
        assertEquals(defaults.getMaxErrorTokenLength(),
                factory.errorReportConfiguration().getMaxErrorTokenLength());
        assertEquals(defaults.getMaxRawContentLength(),
                factory.errorReportConfiguration().getMaxRawContentLength());
        assertSame(defaults, ErrorReportConfiguration.defaults());
    }

    void expectedTokenLengthWithConfigurations() {
        ErrorReportConfiguration[] configurations = {
                ErrorReportConfiguration.builder().build(),
                ErrorReportConfiguration.defaults(),
                ErrorReportConfiguration.builder().maxErrorTokenLength(56).build(),
                ErrorReportConfiguration.builder().maxErrorTokenLength(456).build(),
                ErrorReportConfiguration.builder().maxErrorTokenLength(0).build()
        };
        for (ErrorReportConfiguration config : configurations) {
            VPackFactory factory = VPackFactory.builder()
                    .errorReportConfiguration(config).build();
            assertSame(config, factory.errorReportConfiguration());
            assertThrows(UnsupportedOperationException.class,
                    () -> factory.createParser("VPack has no textual token source"));
        }
    }

    void invalidMaxErrorTokenLength() {
        ErrorReportConfiguration.Builder builder = ErrorReportConfiguration.builder();
        IllegalArgumentException errorToken = assertThrows(IllegalArgumentException.class,
                () -> builder.maxErrorTokenLength(-1));
        assertEquals("Value of maxErrorTokenLength (-1) cannot be negative",
                errorToken.getMessage());

        IllegalArgumentException rawContent = assertThrows(IllegalArgumentException.class,
                () -> builder.maxRawContentLength(-1));
        assertEquals("Value of maxRawContentLength (-1) cannot be negative",
                rawContent.getMessage());
    }

    void nonPositiveErrorTokenConfig() {
        ErrorReportConfiguration config = ErrorReportConfiguration.builder()
                .maxErrorTokenLength(0).build();
        assertEquals(0, config.getMaxErrorTokenLength());

        assertThrows(IllegalArgumentException.class,
                () -> ErrorReportConfiguration.builder().maxErrorTokenLength(-1));
    }

    void normalBuild() {
        ErrorReportConfiguration config = ErrorReportConfiguration.builder()
                .maxErrorTokenLength(1004)
                .maxRawContentLength(2008)
                .build();
        VPackFactory factory = VPackFactory.builder()
                .errorReportConfiguration(config).build();

        assertEquals(1004, factory.errorReportConfiguration().getMaxErrorTokenLength());
        assertEquals(2008, factory.errorReportConfiguration().getMaxRawContentLength());
    }

    void overrideDefaultErrorReportConfiguration() {
        ErrorReportConfiguration previous = ErrorReportConfiguration.defaults();
        try {
            ErrorReportConfiguration.overrideDefaultErrorReportConfiguration(null);
            assertEquals(ErrorReportConfiguration.DEFAULT_MAX_ERROR_TOKEN_LENGTH,
                    ErrorReportConfiguration.defaults().getMaxErrorTokenLength());
            assertEquals(ErrorReportConfiguration.DEFAULT_MAX_RAW_CONTENT_LENGTH,
                    ErrorReportConfiguration.defaults().getMaxRawContentLength());

            ErrorReportConfiguration custom = ErrorReportConfiguration.builder()
                    .maxErrorTokenLength(10101)
                    .maxRawContentLength(20202)
                    .build();
            ErrorReportConfiguration.overrideDefaultErrorReportConfiguration(custom);

            assertSame(custom, ErrorReportConfiguration.defaults());
            VPackFactory factory = new VPackFactory();
            assertSame(custom, factory.errorReportConfiguration());
        } finally {
            ErrorReportConfiguration.overrideDefaultErrorReportConfiguration(previous);
        }
    }

    void rebuild() {
        ErrorReportConfiguration config = ErrorReportConfiguration.builder().build();
        ErrorReportConfiguration rebuilt = config.rebuild().build();

        assertEquals(config.getMaxErrorTokenLength(), rebuilt.getMaxErrorTokenLength());
        assertEquals(config.getMaxRawContentLength(), rebuilt.getMaxRawContentLength());
    }

    void __invoke_builderConstructorWithErrorReportConfiguration() throws Exception {
        try {
            builderConstructorWithErrorReportConfiguration();
        } finally {
        }
    }


    void __invoke_defaults() throws Exception {
        try {
            defaults();
        } finally {
        }
    }


    void __invoke_expectedTokenLengthWithConfigurations() throws Exception {
        try {
            expectedTokenLengthWithConfigurations();
        } finally {
        }
    }


    void __invoke_invalidMaxErrorTokenLength() throws Exception {
        try {
            invalidMaxErrorTokenLength();
        } finally {
        }
    }


    void __invoke_nonPositiveErrorTokenConfig() throws Exception {
        try {
            nonPositiveErrorTokenConfig();
        } finally {
        }
    }


    void __invoke_normalBuild() throws Exception {
        try {
            normalBuild();
        } finally {
        }
    }


    void __invoke_overrideDefaultErrorReportConfiguration() throws Exception {
        try {
            overrideDefaultErrorReportConfiguration();
        } finally {
        }
    }


    void __invoke_rebuild() throws Exception {
        try {
            rebuild();
        } finally {
        }
    }

}
