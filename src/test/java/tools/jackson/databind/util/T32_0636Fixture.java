package tools.jackson.databind.util;

import java.io.ByteArrayOutputStream;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.StreamReadFeature;
import tools.jackson.core.exc.StreamWriteException;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0636Fixture {
private final VPackFactory factory = new VPackFactory();

    void parentContextRetainsCurrentFieldAcrossNestedObject() throws Exception {
        try (JsonGenerator generator = factory.createGenerator(new ByteArrayOutputStream())) {
            generator.writeStartObject();
            generator.writeName("b");
            generator.writeStartObject();
            generator.writeName("c");
            assertEquals("b", generator.streamWriteContext().getParent().currentName());
            generator.writeString("cval");
            generator.writeEndObject();
            generator.writeEndObject();
        }
    }

    void parentSiblingContextAdvancesAfterEmptyObject() throws Exception {
        try (JsonGenerator generator = factory.createGenerator(new ByteArrayOutputStream())) {
            generator.writeStartObject();
            generator.writeName("a");
            generator.writeStartObject();
            generator.writeEndObject();
            generator.writeName("b");
            generator.writeStartObject();
            generator.writeName("c");
            assertEquals("b", generator.streamWriteContext().getParent().currentName());
            generator.writeString("cval");
            generator.writeEndObject();
            generator.writeEndObject();
        }
    }

    void parserFeaturesUseJacksonDefaults() throws Exception {
        try (JsonParser parser = factory.createParser(new byte[] { 0x31 })) {
            for (StreamReadFeature feature : StreamReadFeature.values()) {
                assertEquals(feature.enabledByDefault(), parser.isEnabled(feature), "Feature " + feature);
            }
        }
    }

    void rawValuesAreRejectedBecauseRawTextHasNoVpackWireForm() throws Exception {
        JsonGenerator generator = factory.createGenerator(new ByteArrayOutputStream());
        assertThrows(UnsupportedOperationException.class,
                () -> generator.writeRawValue("{\"a\":1}"));
        assertThrows(RuntimeException.class, generator::close);
    }

    void simpleArrayMatchesIndependentLiteralAndWriter() throws Exception {
        ByteArrayOutputStream emptyOutput = new ByteArrayOutputStream();
        try (JsonGenerator generator = factory.createGenerator(emptyOutput)) {
            assertTrue(generator.streamWriteContext().inRoot());
            generator.writeStartArray();
            assertTrue(generator.streamWriteContext().inArray());
            generator.writeEndArray();
            assertTrue(generator.streamWriteContext().inRoot());
        }
        assertArrayEquals(new byte[] { 0x01 }, emptyOutput.toByteArray());
        try (JsonParser parser = factory.createParser(emptyOutput.toByteArray())) {
            assertNull(parser.currentToken());
            assertTrue(parser.streamReadContext().inRoot());
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertTrue(parser.streamReadContext().inArray());
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            assertTrue(parser.streamReadContext().inRoot());
            assertNull(parser.nextToken());
        }

        // Independent literal VPack bytes for [true, null].
        byte[] literal = { 0x02, 0x04, 0x1A, 0x18 };
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = factory.createGenerator(output)) {
            generator.writeStartArray();
            generator.writeBoolean(true);
            generator.writeNull();
            generator.writeEndArray();
        }
        assertArrayEquals(literal, output.toByteArray());
        try (JsonParser parser = factory.createParser(literal)) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(JsonToken.VALUE_TRUE, parser.nextToken());
            assertTrue(parser.getBooleanValue());
            assertEquals(JsonToken.VALUE_NULL, parser.nextToken());
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            assertNull(parser.nextToken());
        }

        ByteArrayOutputStream nestedOutput = new ByteArrayOutputStream();
        try (JsonGenerator generator = factory.createGenerator(nestedOutput)) {
            generator.writeStartArray();
            generator.writeStartArray();
            generator.writeBinary(new byte[3]);
            generator.writeEndArray();
            generator.writeEndArray();
        }
        try (JsonParser parser = factory.createParser(nestedOutput.toByteArray())) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(JsonToken.VALUE_EMBEDDED_OBJECT, parser.nextToken());
            Object embedded = parser.getEmbeddedObject();
            assertTrue(embedded instanceof byte[]);
            assertEquals(3, ((byte[]) embedded).length);
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }

    void simpleNumbersKeepFloatingAndIntegerValuesAcrossRootSequence() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        double[] doubles = { 0.25, Double.NaN, -2.0,
                Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY };
        float[] floats = { Float.NEGATIVE_INFINITY, 0.25f, Float.POSITIVE_INFINITY };
        try (JsonGenerator generator = factory.createGenerator(output)) {
            for (double value : doubles) generator.writeNumber(value);
            for (float value : floats) generator.writeNumber(value);
            generator.writeNumber(13);
        }
        try (JsonParser parser = factory.createParser(output.toByteArray())) {
            for (double expected : doubles) {
                assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
                assertEquals(JsonParser.NumberType.DOUBLE, parser.getNumberType());
                assertEquals(JsonParser.NumberTypeFP.DOUBLE64, parser.getNumberTypeFP());
                double actual = parser.getDoubleValue();
                boolean nonFinite = Double.isNaN(expected) || Double.isInfinite(expected);
                assertEquals(nonFinite, parser.isNaN());
                if (Double.isNaN(expected)) assertTrue(Double.isNaN(actual));
                else assertEquals(expected, actual);
            }
            for (float expected : floats) {
                assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
                // VPack has one native floating width; Float inputs widen to DOUBLE64.
                assertEquals(JsonParser.NumberType.DOUBLE, parser.getNumberType());
                assertEquals(JsonParser.NumberTypeFP.DOUBLE64, parser.getNumberTypeFP());
                float actual = parser.getFloatValue();
                boolean nonFinite = Float.isNaN(expected) || Float.isInfinite(expected);
                assertEquals(nonFinite, parser.isNaN());
                if (Float.isInfinite(expected)) assertEquals(expected, actual);
                else assertEquals(expected, actual);
            }
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(13, parser.getIntValue());
            assertNull(parser.nextToken());
        }
    }

    void segmentOverflowEquivalentReadsTwentyValues() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = factory.createGenerator(output)) {
            generator.writeStartArray();
            for (int i = 0; i < 20; i++) generator.writeNumber(i);
            generator.writeEndArray();
        }
        try (JsonParser parser = factory.createParser(output.toByteArray())) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            for (int i = 0; i < 20; i++) {
                assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
                assertEquals(i, parser.getIntValue());
            }
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }

    void embeddedByteArrayIsBinaryAndRawSerializationIsUnsupported() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = factory.createGenerator(output)) {
            generator.writeStartArray();
            generator.writeEmbeddedObject(new byte[] { 1, 2, 3 });
            generator.writeEndArray();
        }
        try (JsonParser parser = factory.createParser(output.toByteArray())) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(JsonToken.VALUE_EMBEDDED_OBJECT, parser.nextToken());
            assertArrayEquals(new byte[] { 1, 2, 3 }, (byte[]) parser.getEmbeddedObject());
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
        }
        JsonGenerator raw = factory.createGenerator(new ByteArrayOutputStream());
        assertThrows(UnsupportedOperationException.class, () -> raw.writeRawValue("{\"x\":1}"));
        assertThrows(RuntimeException.class, raw::close);
    }

    void floatSerializationRetainsFloatValue() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = factory.createGenerator(output)) {
            generator.writeStartArray();
            generator.writeNumber(3.14f);
            generator.writeEndArray();
        }
        try (JsonParser parser = factory.createParser(output.toByteArray())) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            assertEquals(3.14f, parser.getFloatValue());
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
        }
    }

    void stringNumberIsParsedAsItsNumericValue() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = factory.createGenerator(output)) {
            generator.writeStartArray();
            generator.writeNumber("1.23e10");
            generator.writeEndArray();
        }
        try (JsonParser parser = factory.createParser(output.toByteArray())) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            assertEquals(1.23e10, parser.getDoubleValue());
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
        }
    }

    void nativeIdsAreExplicitlyUnsupportedByVpack() throws Exception {
        JsonGenerator generator = factory.createGenerator(new ByteArrayOutputStream());
        assertFalse(generator.canWriteTypeId());
        assertFalse(generator.canWriteObjectId());
        assertThrows(StreamWriteException.class, () -> generator.writeTypeId("myType"));
        assertThrows(StreamWriteException.class, () -> generator.writeObjectId("myObjId"));
        generator.close();
    }

    void segmentOverflowWithNativeIdsMapsToUnsupportedNativeIds() throws Exception {
        // VPack has no native type-ID channel; ordinary segmented-size behavior is covered above.
        JsonGenerator generator = factory.createGenerator(new ByteArrayOutputStream());
        assertFalse(generator.canWriteTypeId());
        assertThrows(StreamWriteException.class, () -> generator.writeTypeId("type0"));
        generator.close();
    }

    void __invoke_parentContextRetainsCurrentFieldAcrossNestedObject() throws Exception {
        try {
            parentContextRetainsCurrentFieldAcrossNestedObject();
        } finally {
        }
    }


    void __invoke_parentSiblingContextAdvancesAfterEmptyObject() throws Exception {
        try {
            parentSiblingContextAdvancesAfterEmptyObject();
        } finally {
        }
    }


    void __invoke_parserFeaturesUseJacksonDefaults() throws Exception {
        try {
            parserFeaturesUseJacksonDefaults();
        } finally {
        }
    }


    void __invoke_rawValuesAreRejectedBecauseRawTextHasNoVpackWireForm() throws Exception {
        try {
            rawValuesAreRejectedBecauseRawTextHasNoVpackWireForm();
        } finally {
        }
    }


    void __invoke_simpleArrayMatchesIndependentLiteralAndWriter() throws Exception {
        try {
            simpleArrayMatchesIndependentLiteralAndWriter();
        } finally {
        }
    }


    void __invoke_simpleNumbersKeepFloatingAndIntegerValuesAcrossRootSequence() throws Exception {
        try {
            simpleNumbersKeepFloatingAndIntegerValuesAcrossRootSequence();
        } finally {
        }
    }


    void __invoke_segmentOverflowEquivalentReadsTwentyValues() throws Exception {
        try {
            segmentOverflowEquivalentReadsTwentyValues();
        } finally {
        }
    }


    void __invoke_embeddedByteArrayIsBinaryAndRawSerializationIsUnsupported() throws Exception {
        try {
            embeddedByteArrayIsBinaryAndRawSerializationIsUnsupported();
        } finally {
        }
    }


    void __invoke_floatSerializationRetainsFloatValue() throws Exception {
        try {
            floatSerializationRetainsFloatValue();
        } finally {
        }
    }


    void __invoke_stringNumberIsParsedAsItsNumericValue() throws Exception {
        try {
            stringNumberIsParsedAsItsNumericValue();
        } finally {
        }
    }


    void __invoke_nativeIdsAreExplicitlyUnsupportedByVpack() throws Exception {
        try {
            nativeIdsAreExplicitlyUnsupportedByVpack();
        } finally {
        }
    }


    void __invoke_segmentOverflowWithNativeIdsMapsToUnsupportedNativeIds() throws Exception {
        try {
            segmentOverflowWithNativeIdsMapsToUnsupportedNativeIds();
        } finally {
        }
    }

}
