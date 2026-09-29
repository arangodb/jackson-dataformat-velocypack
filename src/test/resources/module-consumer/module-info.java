module t28.consumer {
    requires tools.jackson.core;
    requires tools.jackson.databind;
    requires tools.jackson.dataformat.velocypack;
    uses tools.jackson.core.TokenStreamFactory;
    uses tools.jackson.databind.ObjectMapper;
}
