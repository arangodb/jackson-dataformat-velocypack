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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * @author Mark Vollmary
 *
 */
public class VPackKeySerializers {

	public static final JsonSerializer<Boolean> BOOLEAN = new JsonSerializer<Boolean>() {
		@Override
		public void serialize(final Boolean value, final JsonGenerator gen, final SerializerProvider serializers)
				throws IOException, JsonProcessingException {
			gen.writeString(value.toString());
		}
	};

	public static final JsonSerializer<Integer> INTEGER = new JsonSerializer<Integer>() {
		@Override
		public void serialize(final Integer value, final JsonGenerator gen, final SerializerProvider serializers)
				throws IOException, JsonProcessingException {
			gen.writeString(value.toString());
		}
	};

	public static final JsonSerializer<Long> LONG = new JsonSerializer<Long>() {
		@Override
		public void serialize(final Long value, final JsonGenerator gen, final SerializerProvider serializers)
				throws IOException, JsonProcessingException {
			gen.writeString(value.toString());
		}
	};

	public static final JsonSerializer<Short> SHORT = new JsonSerializer<Short>() {
		@Override
		public void serialize(final Short value, final JsonGenerator gen, final SerializerProvider serializers)
				throws IOException, JsonProcessingException {
			gen.writeString(value.toString());
		}
	};

	public static final JsonSerializer<Double> DOUBLE = new JsonSerializer<Double>() {
		@Override
		public void serialize(final Double value, final JsonGenerator gen, final SerializerProvider serializers)
				throws IOException, JsonProcessingException {
			gen.writeString(value.toString());
		}
	};

	public static final JsonSerializer<Float> FLOAT = new JsonSerializer<Float>() {
		@Override
		public void serialize(final Float value, final JsonGenerator gen, final SerializerProvider serializers)
				throws IOException, JsonProcessingException {
			gen.writeString(value.toString());
		}
	};

	public static final JsonSerializer<Character> CHARACTER = new JsonSerializer<Character>() {
		@Override
		public void serialize(final Character value, final JsonGenerator gen, final SerializerProvider serializers)
				throws IOException, JsonProcessingException {
			gen.writeString(value.toString());
		}
	};

}
