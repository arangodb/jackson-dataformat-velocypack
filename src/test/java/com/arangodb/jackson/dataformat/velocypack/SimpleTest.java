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

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.junit.jupiter.api.Test;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;


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
    public void mapper() {
        final ObjectMapper mapper = new VPackMapper();

        final byte[] vpack = mapper.writeValueAsBytes(new TestEntity(TEST_STRING, TEST_INT));
        final JsonNode node = mapper.readTree(vpack);
        assertThat(node).isNotNull();
        assertThat(node.isObject()).isTrue();
        assertThat(node ).hasSize(2);
        assertThat(node.get("value1").isString()).isTrue();
        assertThat(node.get("value1").asString()).isEqualTo(TEST_STRING);
        assertThat(node.get("value2").isIntegralNumber()).isTrue();
        assertThat(node.get("value2").asInt()).isEqualTo(TEST_INT);

        final TestEntity entity = mapper.treeToValue(node, TestEntity.class);
        assertThat(entity).isNotNull();
        assertThat(entity.getValue1()).isEqualTo(TEST_STRING);
        assertThat(entity.getValue2()).isEqualTo(TEST_INT);
    }

    @JsonPropertyOrder({"id", "trailer", "data"})
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

    @Test
    public void testSimpleBinary() {
        final ObjectMapper mapper = new VPackMapper();
        final ObjectWriter w = mapper.writer();
        byte[] data = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
        Binary input = new Binary(123, data, 456);
        byte[] bytes = w.writeValueAsBytes(input);

        JsonNode node = mapper.readTree(bytes);
        JsonNode binaryNode = node.get("data");

        assertThat(binaryNode).isNotNull();
        assertThat(binaryNode.isBinary()).isTrue();
        assertThat(binaryNode.binaryValue()).hasSize(11);
        _verify(data, binaryNode.binaryValue());

        Binary result = mapper.readerFor(Binary.class)
                .readValue(bytes);
        assertThat(result.id).isEqualTo(input.id);
        assertThat(result.trailer).isEqualTo(input.trailer);
        assertThat(result.data).isNotNull();

        _verify(data, result.data);

        // and via JsonParser too
        JsonParser p = mapper.createParser(bytes);
        assertToken(JsonToken.START_OBJECT, p.nextToken());

        assertToken(JsonToken.PROPERTY_NAME, p.nextToken());
        assertThat(p.hasStringCharacters()).isTrue();
        assertThat(p.currentName()).isEqualTo("id");
        assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
        assertToken(JsonToken.PROPERTY_NAME, p.nextToken());
        assertThat(p.currentName()).isEqualTo("trailer");
        assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
        assertThat(p.getIntValue()).isEqualTo(input.trailer);
        assertToken(JsonToken.PROPERTY_NAME, p.nextToken());
        assertThat(p.currentName()).isEqualTo("data");
        assertToken(JsonToken.VALUE_EMBEDDED_OBJECT, p.nextToken());
        _verify(data, p.getBinaryValue());

        assertToken(JsonToken.END_OBJECT, p.nextToken());
        p.close();

        // and with skipping of binary data
        p = mapper.createParser(bytes);
        assertToken(JsonToken.START_OBJECT, p.nextToken());
        assertToken(JsonToken.PROPERTY_NAME, p.nextToken());
        assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
        assertToken(JsonToken.PROPERTY_NAME, p.nextToken());
        assertThat(p.nextIntValue(-1)).isEqualTo(input.trailer);
        assertToken(JsonToken.PROPERTY_NAME, p.nextToken());
        assertToken(JsonToken.VALUE_EMBEDDED_OBJECT, p.nextToken());
        assertToken(JsonToken.END_OBJECT, p.nextToken());
        p.close();
    }

    private void _verify(byte[] dataExp, byte[] dataAct) {
        assertThat(dataAct).hasSize(dataExp.length);
        for (int i = 0, len = dataExp.length; i < len; ++i) {
            if (dataExp[i] != dataAct[i]) {
                fail("Binary data differs at #" + i);
            }
        }
    }

    private void assertToken(JsonToken expToken, JsonToken actToken) {
        if (actToken != expToken) {
            fail("Expected token " + expToken + ", current token " + actToken);
        }
    }

}
