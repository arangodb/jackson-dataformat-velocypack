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


import com.arangodb.velocypack.VPackParser;
import com.arangodb.velocypack.VPackSlice;

/**
 * @author Michele Rastelli
 */
public final class VPackUtils {
    private static final VPackParser PARSER = new VPackParser.Builder().build();

    private VPackUtils() {
    }

    public static String toJson(byte[] bytes) {
        return PARSER.toJson(new VPackSlice(bytes), true);
    }

    public static byte[] toBytes(String json) {
        return PARSER.fromJson(json, true).getBuffer();
    }

}
