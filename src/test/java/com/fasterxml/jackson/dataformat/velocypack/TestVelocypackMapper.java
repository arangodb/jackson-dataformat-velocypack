package com.fasterxml.jackson.dataformat.velocypack;

import com.arangodb.jackson.dataformat.velocypack.VPackFactory;
import com.arangodb.jackson.dataformat.velocypack.VPackMapper;
import com.fasterxml.jackson.VPackUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.cfg.MapperBuilder;

import java.io.IOException;

/**
 * @author Michele Rastelli
 */
public class TestVelocypackMapper extends VPackMapper {

    private static final long serialVersionUID = 1L;

    public static class Builder extends MapperBuilder<TestVelocypackMapper, TestVelocypackMapper.Builder> {
        public Builder(TestVelocypackMapper m) {
            super(m);
        }
    }

    public static TestVelocypackMapper.Builder testBuilder() {
        return new TestVelocypackMapper.Builder(new TestVelocypackMapper());
    }

    public static TestVelocypackMapper.Builder testBuilder(VPackFactory jf) {
        return new TestVelocypackMapper.Builder(new TestVelocypackMapper(jf));
    }

    public TestVelocypackMapper() {
        this(new VPackFactory());
    }

    public TestVelocypackMapper(VPackFactory jf) {
        super(jf);
    }

    protected TestVelocypackMapper(TestVelocypackMapper src) {
        super(src);
    }

    @Override
    public TestVelocypackMapper copy() {
        _checkInvalidCopy(TestVelocypackMapper.class);
        return new TestVelocypackMapper(this);
    }

    @Override
    public <T> T readValue(String content, Class<T> valueType) throws JsonProcessingException {
        try {
            return super.readValue(VPackUtils.toBytes(content), valueType);
        } catch (JsonProcessingException e) {
            throw e;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T readValue(String content, TypeReference<T> valueTypeRef) throws JsonProcessingException {
        try {
            return super.readValue(VPackUtils.toBytes(content), valueTypeRef);
        } catch (JsonProcessingException e) {
            throw e;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T readValue(String content, JavaType valueType) throws JsonProcessingException {
        try {
            return super.readValue(VPackUtils.toBytes(content), valueType);
        } catch (JsonProcessingException e) {
            throw e;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
