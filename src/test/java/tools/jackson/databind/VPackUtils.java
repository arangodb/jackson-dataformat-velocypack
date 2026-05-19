package tools.jackson.databind;


import com.arangodb.jackson.dataformat.velocypack.VPackMapper;

/**
 * @author Michele Rastelli
 */
public final class VPackUtils {
    private final static ObjectMapper JSON_MAPPER = new ObjectMapper();
    private final static ObjectMapper VPACK_MAPPER = new VPackMapper();

    private VPackUtils() {
    }

    public static String toJson(byte[] bytes) {
        return JSON_MAPPER.writeValueAsString(VPACK_MAPPER.readTree(bytes));
    }

    public static byte[] toVPack(String json) {
        return VPACK_MAPPER.writeValueAsBytes(JSON_MAPPER.readTree(json));
    }

}
