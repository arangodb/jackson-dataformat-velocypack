package tools.jackson.databind;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.core.FormatSchema;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectWriter;
import tools.jackson.databind.cfg.EnumFeature;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0134F1 {
private static final byte[] ONE_ELEMENT_ARRAY = VPackWireFixtureTest.hex("02 03 31");
private static final byte[] EMPTY_ARRAY = VPackWireFixtureTest.hex("01");
private static final byte[] ENUM_ARRAY_AC = VPackWireFixtureTest.hex(
            "02 06 41 41 41 43");
private static final byte[] ENUM_ARRAY_BC = VPackWireFixtureTest.hex(
            "02 06 41 42 41 43");
private static final byte[] ENUM_MAP = VPackWireFixtureTest.hex(
            "0b 0a 01 43 6b 65 79 41 42 03");
private static final byte[] TREE_ARRAY = VPackWireFixtureTest.hex(
            "02 06 43 78 79 7a");
private static final byte[] UNKNOWN_FIELD_OBJECT = VPackWireFixtureTest.hex(
            "14 21 4c 75 6e 6b 6e 6f 77 6e 46 69 65 6c 64 31 "
            + "4a 6b 6e 6f 77 6e 46 69 65 6c 64 44 74 65 73 74 02");

    void writerArgumentCheckingRejectsNullType() {
        ObjectWriter writer = new VPackMapper().writer();
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> writer.acceptJsonFormatVisitor((JavaType) null, null));
        assertTrue(error.getMessage().contains("argument \"type\" is null"));
    }

    void writerDatatypeFeaturesCanBeToggled() {
        ObjectWriter writer = new VPackMapper().writer();
        assertNotNull(writer.withFeatures(EnumFeature.FAIL_ON_NUMBERS_FOR_ENUMS,
                EnumFeature.WRITE_ENUM_KEYS_USING_INDEX));
        assertNotNull(writer.withoutFeatures(EnumFeature.FAIL_ON_NUMBERS_FOR_ENUMS,
                EnumFeature.WRITE_ENUM_KEYS_USING_INDEX));
    }
private enum ABC { A, B, C }
private static final class BogusSchema implements FormatSchema {
        @Override
        public String getSchemaType() {
            return "test";
        }
    }
private static final class A2297 {
        final String knownField;

        @JsonCreator
        private A2297(@JsonProperty("knownField") String knownField) {
            this.knownField = knownField;
        }
    }

    void __invoke_writerArgumentCheckingRejectsNullType() throws Exception {
        try {
            writerArgumentCheckingRejectsNullType();
        } finally {
        }
    }


    void __invoke_writerDatatypeFeaturesCanBeToggled() throws Exception {
        try {
            writerDatatypeFeaturesCanBeToggled();
        } finally {
        }
    }

}
