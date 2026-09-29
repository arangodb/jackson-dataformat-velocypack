package t28.consumer;

import java.util.ServiceLoader;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.TokenStreamFactory;
import tools.jackson.databind.ObjectMapper;

public final class Consumer {
    public static void main(String[] args) throws Exception {
        TokenStreamFactory factory = ServiceLoader.load(TokenStreamFactory.class)
                .stream()
                .map(ServiceLoader.Provider::get)
                .filter(value -> value.getClass().getName().equals(
                        "tools.jackson.dataformat.velocypack.VPackFactory"))
                .findFirst()
                .orElseThrow();
        if (!factory.getClass().isInstance(factory.getClass().getConstructor().newInstance())) {
            throw new AssertionError("factory provider was not constructible");
        }
        try (JsonParser parser = factory.createParser(new byte[] { 0x30 })) {
            if (parser.nextToken() != JsonToken.VALUE_NUMBER_INT) {
                throw new AssertionError("factory provider did not parse a scalar");
            }
        }

        ObjectMapper mapper = ServiceLoader.load(ObjectMapper.class)
                .stream()
                .map(ServiceLoader.Provider::get)
                .filter(value -> value.getClass().getName().equals(
                        "tools.jackson.dataformat.velocypack.VPackMapper"))
                .findFirst()
                .orElseThrow();
        byte[] encoded = mapper.writeValueAsBytes("module-path");
        if (!"module-path".equals(mapper.readValue(encoded, String.class))) {
            throw new AssertionError("mapper provider did not round-trip a value");
        }
    }
}
