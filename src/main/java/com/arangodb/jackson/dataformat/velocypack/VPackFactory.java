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
import java.io.OutputStream;

import com.arangodb.jackson.dataformat.velocypack.internal.VPackGenerator;
import com.arangodb.jackson.dataformat.velocypack.internal.VPackParser;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.io.IOContext;

/**
 * @author Mark Vollmary
 *
 */
public class VPackFactory extends JsonFactory {

	private static final long serialVersionUID = 1L;

	@Override
	protected JsonGenerator _createUTF8Generator(final OutputStream out, final IOContext ctxt) throws IOException {
		return new VPackGenerator(_generatorFeatures, _objectCodec, out);
	}

	@Override
	protected JsonParser _createParser(final byte[] data, final int offset, final int len, final IOContext ctxt)
			throws IOException {
		final VPackParser parser = new VPackParser(data, offset, _parserFeatures);
		parser.setCodec(_objectCodec);
		return parser;
	}

}
