package tools.jackson.dataformat.velocypack;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;

import org.junit.jupiter.api.Test;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.StreamWriteFeature;
import tools.jackson.core.exc.StreamWriteException;

import static org.junit.jupiter.api.Assertions.assertThrows;

class VPackObjectWriteStateTest {
    @Test
    void forbidsNamesAndValuesInTheWrongStateIndependently() throws Exception {
        VPackGenerator generator = (VPackGenerator) new VPackFactory()
                .createGenerator(new ByteArrayOutputStream());
        assertThrows(StreamWriteException.class, () -> generator.writeName("outside"));
        assertThrows(StreamWriteException.class, generator::close);

        VPackGenerator dangling = (VPackGenerator) new VPackFactory()
                .createGenerator(new ByteArrayOutputStream());
        dangling.writeStartObject();
        dangling.writeName("dangling");
        assertThrows(StreamWriteException.class, dangling::writeEndObject);
        assertThrows(StreamWriteException.class, dangling::writeNull);
        assertThrows(StreamWriteException.class, dangling::close);
    }

    @Test
    void detectsDuplicateResolvedAliasesWhenStrictWritingIsEnabled() {
        VPackAttributeNameCodec codec = new VPackAttributeNameCodec() {
            @Override public String decode(BigInteger id) {
                return id.equals(BigInteger.ONE) || id.equals(BigInteger.TWO) ? "same" : null;
            }
            @Override public BigInteger encode(String name) { return null; }
        };
        assertThrows(StreamWriteException.class, () -> {
            try (JsonGenerator generator = VPackFactory.builder().attributeNameCodec(codec)
                    .enable(StreamWriteFeature.STRICT_DUPLICATE_DETECTION).build()
                    .createGenerator(new ByteArrayOutputStream())) {
                generator.writeStartObject();
                generator.writePropertyId(1);
                generator.writeNull();
                generator.writePropertyId(2);
            }
        });
    }

    @Test
    void preservesNestedMixedContainerOwnership() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator generator = new VPackFactory().createGenerator(out)) {
            generator.writeStartObject();
            generator.writeName("array");
            generator.writeStartArray();
            generator.writeStartObject();
            generator.writeName("value");
            generator.writeNumber(7);
            generator.writeEndObject();
            generator.writeEndArray();
            generator.writeEndObject();
        }
        try (tools.jackson.core.JsonParser parser = new VPackFactory().createParser(out.toByteArray())) {
            while (parser.nextToken() != null) { }
        }
    }

    @Test
    void countsObjectEntriesBeforeNestedValuesAgainstOneRootBudget() {
        VPackGenerator generator = (VPackGenerator) VPackFactory.builder()
                .vpackWriteConstraints(VPackWriteConstraints.builder().maxRootEntries(1).build())
                .build().createGenerator(new ByteArrayOutputStream());
        generator.writeStartObject();
        generator.writeName("array");
        generator.writeStartArray();
        assertThrows(tools.jackson.core.exc.StreamConstraintsException.class,
                () -> generator.writeNull());
        assertThrows(tools.jackson.core.exc.StreamConstraintsException.class, generator::close);
    }
}
