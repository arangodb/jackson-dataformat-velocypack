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

import com.arangodb.jackson.dataformat.velocypack.VPackMapper;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author Michele Rastelli
 */
public class PolymorphicDeserializationTest {

	@Test
	public void serialize() {
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

		System.out.println("Original: " + container);

		VPackMapper mapper = new VPackMapper();
		byte[] bytes = mapper.writeValueAsBytes(container);

        String json = mapper.readTree(bytes).toString();
        System.out.println("Serialized: " + json);

        Container result = mapper.readValue(bytes, Container.class);
        assertThat(result).isEqualTo(container);

        System.out.println("Deserialized: " + result);
	}

}
