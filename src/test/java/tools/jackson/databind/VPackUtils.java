package tools.jackson.databind;


import com.arangodb.jackson.dataformat.velocypack.VPackFactory;
import com.arangodb.jackson.dataformat.velocypack.VPackMapper;
import tools.jackson.core.StreamReadConstraints;
import tools.jackson.core.StreamWriteConstraints;
import tools.jackson.core.json.JsonFactory;
import tools.jackson.databind.json.JsonMapper;

/**
 * @author Michele Rastelli
 */
public final class VPackUtils {
    private final static JsonMapper JSON_MAPPER = JsonMapper.builder(JsonFactory.builder()
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

    public static byte[] toVPack(String json) {
        if (json == null) return null;
        else if (json.isEmpty()) return new byte[0];
        else return VPACK_MAPPER.writeValueAsBytes(JSON_MAPPER.readTree(json));
    }

}
