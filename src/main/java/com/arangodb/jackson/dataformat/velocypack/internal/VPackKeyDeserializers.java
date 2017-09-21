/*
 * DISCLAIMER
 *
 * Copyright 2017 ArangoDB GmbH, Cologne, Germany
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

package com.arangodb.jackson.dataformat.velocypack.internal;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * @author Mark Vollmary
 *
 */
public class VPackKeyDeserializers {

	public static final JsonDeserializer<Integer> INTEGER = new JsonDeserializer<Integer>() {
		@Override
		public Integer deserialize(final JsonParser p, final DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			return Integer.valueOf(p.getValueAsString());
		}
	};

	public static final JsonDeserializer<Long> LONG = new JsonDeserializer<Long>() {
		@Override
		public Long deserialize(final JsonParser p, final DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			return Long.valueOf(p.getValueAsString());
		}
	};

	public static final JsonDeserializer<Short> SHORT = new JsonDeserializer<Short>() {
		@Override
		public Short deserialize(final JsonParser p, final DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			return Short.valueOf(p.getValueAsString());
		}
	};

	public static final JsonDeserializer<Double> DOUBLE = new JsonDeserializer<Double>() {
		@Override
		public Double deserialize(final JsonParser p, final DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			return Double.valueOf(p.getValueAsString());
		}
	};

	public static final JsonDeserializer<Float> FLOAT = new JsonDeserializer<Float>() {
		@Override
		public Float deserialize(final JsonParser p, final DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			return Float.valueOf(p.getValueAsString());
		}
	};

	public static final JsonDeserializer<BigInteger> BIG_INTEGER = new JsonDeserializer<BigInteger>() {
		@Override
		public BigInteger deserialize(final JsonParser p, final DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			return new BigInteger(p.getValueAsString());
		}
	};

	public static final JsonDeserializer<BigDecimal> BIG_DECIMAL = new JsonDeserializer<BigDecimal>() {
		@Override
		public BigDecimal deserialize(final JsonParser p, final DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			return new BigDecimal(p.getValueAsString());
		}
	};

	public static final JsonDeserializer<Number> NUMBER = new JsonDeserializer<Number>() {
		@Override
		public Number deserialize(final JsonParser p, final DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			return Double.valueOf(p.getValueAsString());
		}
	};

	public static final JsonDeserializer<Character> CHARATCER = new JsonDeserializer<Character>() {
		@Override
		public Character deserialize(final JsonParser p, final DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			return p.getValueAsString().charAt(0);
		}
	};

}
