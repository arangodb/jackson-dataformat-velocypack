module tools.jackson.dataformat.velocypack {
    requires transitive tools.jackson.core;
    requires transitive tools.jackson.databind;
    exports tools.jackson.dataformat.velocypack;
    provides tools.jackson.core.TokenStreamFactory
        with tools.jackson.dataformat.velocypack.VPackFactory;
    provides tools.jackson.databind.ObjectMapper
        with tools.jackson.dataformat.velocypack.VPackMapper;
}
