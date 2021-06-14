package com.fasterxml.jackson.dataformat.velocypack;

import com.arangodb.jackson.dataformat.velocypack.VPackFactory;
import com.arangodb.jackson.dataformat.velocypack.VPackMapper;
import com.arangodb.velocypack.VPackParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.io.IOException;

/**
 * @author Michele Rastelli
 */
public class TestVelocypackMapper extends VPackMapper {

    private static final VPackParser PARSER = new VPackParser.Builder().build();


    private static final long serialVersionUID = 1L;

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
            return super.readValue(PARSER.fromJson(content, true).getBuffer(), valueType);
        } catch (JsonProcessingException e) {
            throw e;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T readValue(String content, TypeReference valueTypeRef) throws JsonProcessingException, JsonMappingException {
        try {
            return super.readValue(PARSER.fromJson(content, true).getBuffer(), valueTypeRef);
        } catch (JsonProcessingException e) {
            throw e;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T readValue(String content, JavaType valueType) throws JsonProcessingException, JsonMappingException {
        try {
            return super.readValue(PARSER.fromJson(content, true).getBuffer(), valueType);
        } catch (JsonProcessingException e) {
            throw e;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
