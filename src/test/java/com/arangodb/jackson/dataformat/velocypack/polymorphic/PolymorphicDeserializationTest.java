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

package com.arangodb.jackson.dataformat.velocypack.polymorphic;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import tools.jackson.dataformat.velocypack.VPackMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Michele Rastelli
 */
public class PolymorphicDeserializationTest {

	@Test
	public void serialize() throws IOException {
		Map<String, PolyType> attributes = new HashMap<>();

		FirstType ft = new FirstType();
		ft.setValue("FirstType");
		attributes.put("FirstType", ft);

		SecondType st = new SecondType();
		st.setValue("SecondType");
		attributes.put("SecondType", st);

		Container container = new Container();
		container.setAttributes(attributes);
		container.setText("text");

		VPackMapper mapper = new VPackMapper();
		byte[] bytes = mapper.writeValueAsBytes(container);

		Container result = mapper.readValue(bytes, Container.class);
		assertEquals(container, result);
	}

}
