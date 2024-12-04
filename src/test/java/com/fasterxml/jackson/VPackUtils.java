package com.fasterxml.jackson;/*
 * DISCLAIMER
 *
 * Copyright 2016 ArangoDB GmbH, Cologne, Germany
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright holder is ArangoDB GmbH, Cologne, Germany
 */


import com.arangodb.jackson.dataformat.velocypack.VPackMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * @author Michele Rastelli
 */
public final class VPackUtils {
    private final static ObjectMapper JSON_MAPPER = new ObjectMapper();
    private final static ObjectMapper VPACK_MAPPER = new VPackMapper();

    private VPackUtils() {
    }

    public static String toJson(byte[] bytes) {
        try {
            return JSON_MAPPER.writeValueAsString(VPACK_MAPPER.readTree(bytes));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] toVPack(String json) throws JsonProcessingException {
        return VPACK_MAPPER.writeValueAsBytes(JSON_MAPPER.readTree(json));
    }

}
