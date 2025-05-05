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

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.Test;

import com.arangodb.velocypack.VPackSlice;
import com.fasterxml.jackson.databind.ObjectMapper;

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
        final VPackSlice slice = new VPackSlice(vpack);
        assertThat(slice, is(notNullValue()));
        assertThat(slice.isObject(), is(true));
        assertThat(slice.size(), is(2));
        assertThat(slice.get("value1").isString(), is(true));
        assertThat(slice.get("value1").getAsString(), is(TEST_STRING));
        assertThat(slice.get("value2").isInteger(), is(true));
        assertThat(slice.get("value2").getAsInt(), is(TEST_INT));

        final TestEntity entity = mapper.readValue(slice.getBuffer(), TestEntity.class);
        assertThat(entity, is(notNullValue()));
        assertThat(entity.getValue1(), is(TEST_STRING));
        assertThat(entity.getValue2(), is(TEST_INT));
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

    @Test
    public void testSimpleBinary() throws Exception {
        final ObjectMapper mapper = new VPackMapper();
        final ObjectWriter w = mapper.writer();
        byte[] data = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
        Binary input = new Binary(123, data, 456);
        byte[] bytes = w.writeValueAsBytes(input);
        VPackSlice slice = new VPackSlice(bytes);
        VPackSlice vpackData = slice.get("data");
        assertThat(vpackData, is(notNullValue()));
        assertThat(vpackData.isBinary(), is(true));
        assertEquals(11, vpackData.getAsBinary().length);
        _verify(data, vpackData.getAsBinary());

        Binary result = mapper.readerFor(Binary.class)
                .readValue(bytes);
        assertEquals(input.id, result.id);
        assertEquals(input.trailer, result.trailer);
        assertNotNull(result.data);

        _verify(data, result.data);

        // and via JsonParser too
        JsonParser p = mapper.getFactory().createParser(bytes);
        assertToken(JsonToken.START_OBJECT, p.nextToken());

        assertToken(JsonToken.FIELD_NAME, p.nextToken());
        assertFalse(p.hasTextCharacters());
        assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
        assertToken(JsonToken.FIELD_NAME, p.nextToken());
        assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
        assertEquals(input.trailer, p.getIntValue());
        assertToken(JsonToken.FIELD_NAME, p.nextToken());
        assertEquals("data", p.getCurrentName());
        assertToken(JsonToken.VALUE_EMBEDDED_OBJECT, p.nextToken());
        _verify(data, p.getBinaryValue());

        assertToken(JsonToken.END_OBJECT, p.nextToken());
        p.close();

        // and with skipping of binary data
        p = mapper.getFactory().createParser(bytes);
        assertToken(JsonToken.START_OBJECT, p.nextToken());
        assertToken(JsonToken.FIELD_NAME, p.nextToken());
        assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
        assertToken(JsonToken.FIELD_NAME, p.nextToken());
        assertEquals(input.trailer, p.nextIntValue(-1));
        assertToken(JsonToken.FIELD_NAME, p.nextToken());
        assertToken(JsonToken.VALUE_EMBEDDED_OBJECT, p.nextToken());
        assertToken(JsonToken.END_OBJECT, p.nextToken());
        p.close();
    }

    private void _verify(byte[] dataExp, byte[] dataAct) {
        assertEquals(dataExp.length, dataAct.length);
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
