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

import com.arangodb.jackson.dataformat.velocypack.internal.VPackDeserializers;
import com.arangodb.jackson.dataformat.velocypack.internal.VPackSerializers;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * @author Mark Vollmary
 *
 */
public class VPackMapper extends ObjectMapper {

	private static final long serialVersionUID = 1L;

	public VPackMapper() {
		super(new VPackFactory());
		configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		final SimpleModule module = new SimpleModule();
		module.addSerializer(java.util.Date.class, VPackSerializers.UTIL_DATE);
		module.addSerializer(java.sql.Date.class, VPackSerializers.SQL_DATE);
		module.addSerializer(java.sql.Timestamp.class, VPackSerializers.SQL_TIMESTAMP);
		module.addDeserializer(java.util.Date.class, VPackDeserializers.UTIL_DATE);
		module.addDeserializer(java.sql.Date.class, VPackDeserializers.SQL_DATE);
		module.addDeserializer(java.sql.Timestamp.class, VPackDeserializers.SQL_TIMESTAMP);
		// module.addKeySerializer(Boolean.class, VPackKeySerializers.BOOLEAN);
		// module.addKeySerializer(Integer.class, VPackKeySerializers.INTEGER);
		// module.addKeySerializer(Long.class, VPackKeySerializers.LONG);
		// module.addKeySerializer(Short.class, VPackKeySerializers.SHORT);
		// module.addKeySerializer(Double.class, VPackKeySerializers.DOUBLE);
		// module.addKeySerializer(Float.class, VPackKeySerializers.FLOAT);
		// module.addKeySerializer(Character.class, VPackKeySerializers.CHARACTER);
		// module.addKeyDeserializer(Boolean.class, StdKeyDeserializer.forType(Boolean.class));
		// module.addKeyDeserializer(Integer.class, StdKeyDeserializer.forType(Integer.class));
		// module.addKeyDeserializer(Long.class, StdKeyDeserializer.forType(Long.class));
		// module.addKeyDeserializer(Short.class, StdKeyDeserializer.forType(Short.class));
		// module.addKeyDeserializer(Double.class, StdKeyDeserializer.forType(Double.class));
		// module.addKeyDeserializer(Float.class, StdKeyDeserializer.forType(Float.class));
		// module.addKeyDeserializer(Character.class, StdKeyDeserializer.forType(Character.class));
		registerModule(module);
	}

}
