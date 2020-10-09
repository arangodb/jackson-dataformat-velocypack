package com.arangodb.jackson.dataformat.velocypack;


import com.fasterxml.jackson.core.TSFBuilder;

/**
 * {@link TSFBuilder} implementation for constructing {@link VPackFactory} instances.
 */
public class VPackFactoryBuilder extends TSFBuilder<VPackFactory, VPackFactoryBuilder> {
    public VPackFactoryBuilder() {
        super();
    }

    public VPackFactoryBuilder(VPackFactory base) {
        super(base);
    }

    @Override
    public VPackFactory build() {
        return new VPackFactory(this);
    }
}
