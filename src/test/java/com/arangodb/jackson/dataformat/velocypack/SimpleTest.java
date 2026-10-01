/*
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

package com.arangodb.jackson.dataformat.velocypack;


import java.io.IOException;
import java.util.Map;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;
import tools.jackson.dataformat.velocypack.VPackMapper;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;



/**
 * @author Mark Vollmary
 */
public class SimpleTest {

    private static final String TEST_STRING = "hello world";
    private static final int TEST_INT = 69;

    public static class TestEntity {
        private String value1;
        private int value2;

        public TestEntity(final String value1, final int value2) {
            super();
            this.value1 = value1;
            this.value2 = value2;
        }

        public TestEntity() {
            super();
        }

        public String getValue1() {
            return value1;
        }

        public void setValue1(final String value1) {
            this.value1 = value1;
        }

        public int getValue2() {
            return value2;
        }

        public void setValue2(final int value2) {
            this.value2 = value2;
        }

    }

    @Test
    public void mapper() throws IOException {
        final ObjectMapper mapper = new VPackMapper();

        final byte[] vpack = mapper.writeValueAsBytes(new TestEntity(TEST_STRING, TEST_INT));
        final Map<?, ?> decoded = mapper.readValue(vpack, Map.class);
        assertEquals(2, decoded.size());
        assertEquals(TEST_STRING, decoded.get("value1"));
        assertEquals(TEST_INT, decoded.get("value2"));

        final TestEntity entity = mapper.readValue(vpack, TestEntity.class);
        assertNotNull(entity);
        assertEquals(TEST_STRING, entity.getValue1());
        assertEquals(TEST_INT, entity.getValue2());
    }

    static class Binary {
        public int id, trailer;
        public byte[] data;

        public Binary() {
        }

        public Binary(int id, byte[] data, int trailer) {
            this.id = id;
            this.data = data;
            this.trailer = trailer;
        }
    }

    // FIXME
    @Disabled("FIXME")
    @Test
    public void testSimpleBinary() throws Exception {
        final ObjectMapper mapper = new VPackMapper();
        final ObjectWriter w = mapper.writer();
        byte[] data = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
        Binary input = new Binary(123, data, 456);
        byte[] bytes = w.writeValueAsBytes(input);
        try (JsonParser parser = mapper.tokenStreamFactory().createParser(bytes)) {
            assertToken(JsonToken.START_OBJECT, parser.nextToken());
            assertToken(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertToken(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertToken(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertToken(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertToken(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("data", parser.currentName());
            assertToken(JsonToken.VALUE_EMBEDDED_OBJECT, parser.nextToken());
            byte[] parsedData = parser.getBinaryValue();
            assertEquals(11, parsedData.length);
            _verify(data, parsedData);
            assertToken(JsonToken.END_OBJECT, parser.nextToken());
        }

        Binary result = mapper.readerFor(Binary.class)
                .readValue(bytes);
        assertEquals(input.id, result.id);
        assertEquals(input.trailer, result.trailer);
        assertNotNull(result.data);
        assertArrayEquals(data, result.data);

        // and via JsonParser too
        JsonParser p = mapper.tokenStreamFactory().createParser(bytes);
        assertToken(JsonToken.START_OBJECT, p.nextToken());

        assertToken(JsonToken.PROPERTY_NAME, p.nextToken());
        assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
        assertToken(JsonToken.PROPERTY_NAME, p.nextToken());
        assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
        assertEquals(input.trailer, p.getIntValue());
        assertToken(JsonToken.PROPERTY_NAME, p.nextToken());
        assertEquals("data", p.currentName());
        assertToken(JsonToken.VALUE_EMBEDDED_OBJECT, p.nextToken());
        _verify(data, p.getBinaryValue());

        assertToken(JsonToken.END_OBJECT, p.nextToken());
        p.close();

        // and with skipping of binary data
        p = mapper.tokenStreamFactory().createParser(bytes);
        assertToken(JsonToken.START_OBJECT, p.nextToken());
        assertToken(JsonToken.PROPERTY_NAME, p.nextToken());
        assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
        assertToken(JsonToken.PROPERTY_NAME, p.nextToken());
        assertEquals(input.trailer, p.nextIntValue(-1));
        assertToken(JsonToken.PROPERTY_NAME, p.nextToken());
        assertToken(JsonToken.VALUE_EMBEDDED_OBJECT, p.nextToken());
        assertToken(JsonToken.END_OBJECT, p.nextToken());
        p.close();
    }

    private void _verify(byte[] dataExp, byte[] dataAct) {
        assertArrayEquals(dataExp, dataAct);
    }

    private void assertToken(JsonToken expToken, JsonToken actToken) {
        if (actToken != expToken) {
            fail("Expected token " + expToken + ", current token " + actToken);
        }
    }

}
