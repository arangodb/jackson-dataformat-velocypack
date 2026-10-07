package tools.jackson.databind;


import com.arangodb.jackson.dataformat.velocypack.VPackFactory;
import com.arangodb.jackson.dataformat.velocypack.VPackMapper;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.StreamReadConstraints;
import tools.jackson.core.StreamWriteConstraints;
import tools.jackson.core.json.JsonFactory;
import tools.jackson.core.json.JsonReadFeature;
import tools.jackson.databind.json.JsonMapper;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;

/**
 * @author Michele Rastelli
 */
public final class VPackUtils {
    private final static JsonMapper JSON_MAPPER = JsonMapper.builder(JsonFactory.builder()
            .enable(JsonReadFeature.ALLOW_NON_NUMERIC_NUMBERS)
            .streamReadConstraints(StreamReadConstraints.builder()
                    .maxNestingDepth(Integer.MAX_VALUE)
                    .maxStringLength(Integer.MAX_VALUE)
                    .maxNumberLength(Integer.MAX_VALUE)
                    .build())
            .streamWriteConstraints(StreamWriteConstraints.builder()
                    .maxNestingDepth(Integer.MAX_VALUE)
                    .build())
            .build()
    ).build();
    private final static VPackMapper VPACK_MAPPER = VPackMapper.builder(VPackFactory.builder()
            .streamReadConstraints(StreamReadConstraints.builder()
                    .maxNestingDepth(Integer.MAX_VALUE)
                    .maxStringLength(Integer.MAX_VALUE)
                    .maxNumberLength(Integer.MAX_VALUE)
                    .build())
            .streamWriteConstraints(StreamWriteConstraints.builder()
                    .maxNestingDepth(Integer.MAX_VALUE)
                    .build())
            .build()
    ).build();

    private VPackUtils() {
    }

    public static String toJson(byte[] bytes) {
        if (bytes == null) return null;
        else if (bytes.length == 0) return "";
        else return JSON_MAPPER.writeValueAsString(VPACK_MAPPER.readTree(bytes));
    }

    private final static JsonFactory JSON_FACTORY = JsonFactory.builder()
            .enable(JsonReadFeature.ALLOW_NON_NUMERIC_NUMBERS)
            .streamReadConstraints(StreamReadConstraints.builder()
                    .maxNestingDepth(Integer.MAX_VALUE)
                    .maxStringLength(Integer.MAX_VALUE)
                    .maxNumberLength(Integer.MAX_VALUE)
                    .build())
            .streamWriteConstraints(StreamWriteConstraints.builder()
                    .maxNestingDepth(Integer.MAX_VALUE)
                    .build())
            .build();
    private final static VPackFactory VPACK_FACTORY = VPackFactory.builder()
            .streamReadConstraints(StreamReadConstraints.builder()
                    .maxNestingDepth(Integer.MAX_VALUE)
                    .maxStringLength(Integer.MAX_VALUE)
                    .maxNumberLength(Integer.MAX_VALUE)
                    .build())
            .streamWriteConstraints(StreamWriteConstraints.builder()
                    .maxNestingDepth(Integer.MAX_VALUE)
                    .build())
            .build();

    public static byte[] toVPack(String json) {
        if (json == null) return null;
        else if (json.isEmpty()) return new byte[0];
        else {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try (JsonParser p = JSON_FACTORY.createParser(json);
                 JsonGenerator g = VPACK_FACTORY.createGenerator(out)) {
                while (p.nextToken() != null) {
                    // copyCurrentEvent() routes floating-point values through double;
                    // preserve the source literal by explicitly using BigDecimal instead.
                    if (p.currentToken() == tools.jackson.core.JsonToken.VALUE_NUMBER_FLOAT
                            && !p.isNaN()) {
                        BigDecimal decimal = p.getDecimalValue();
                        if (decimal.signum() == 0 && p.getText().startsWith("-")) {
                            g.writeNumber(-0.0d);
                            continue;
                        }
                        double value = decimal.doubleValue();
                        if (Double.isFinite(value)
                                && BigDecimal.valueOf(value).compareTo(decimal) == 0) {
                            g.writeNumber(value);
                        } else {
                            g.writeNumber(decimal);
                        }
                    } else {
                        g.copyCurrentEvent(p);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return out.toByteArray();
        }
    }

    /** @deprecated {@link #toVPack(String)} preserves decimal literals. */
    @Deprecated
    public static byte[] toVPackDecimal(String json) {
        return toVPack(json);
    }

}
