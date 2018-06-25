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

package com.arangodb.jackson.dataformat.velocypack.internal;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;

import com.arangodb.velocypack.VPackBuilder;
import com.arangodb.velocypack.VPackSlice;
import com.arangodb.velocypack.ValueType;
import com.arangodb.velocypack.exception.VPackBuilderException;
import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.base.GeneratorBase;

/**
 * @author Mark Vollmary
 *
 */
public class VPackGenerator extends GeneratorBase {

	private final VPackBuilder builder = new VPackBuilder();
	private final OutputStream out;
	private String attribute = null;

	public VPackGenerator(final int features, final ObjectCodec codec, final OutputStream out) {
		super(features, codec);
		this.out = out;
	}

	@Override
	public void flush() throws IOException {
		out.write(builder.slice().getBuffer());
		out.flush();
	}

	@Override
	protected void _releaseBuffers() {
	}

	@Override
	protected void _verifyValueWrite(final String typeMsg) throws IOException {
	}

	@Override
	public void writeStartArray() throws IOException {
		try {
			builder.add(attribute, ValueType.ARRAY);
			attribute = null;
		} catch (final VPackBuilderException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void writeEndArray() throws IOException {
		try {
			builder.close();
		} catch (final VPackBuilderException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void writeStartObject() throws IOException {
		try {
			builder.add(attribute, ValueType.OBJECT);
			attribute = null;
		} catch (final VPackBuilderException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void writeEndObject() throws IOException {
		try {
			builder.close();
		} catch (final VPackBuilderException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void writeFieldName(final String name) throws IOException {
		attribute = name;
	}

	@Override
	public void writeString(final String text) throws IOException {
		try {
			builder.add(attribute, text);
			attribute = null;
		} catch (final VPackBuilderException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void writeString(final char[] text, final int offset, final int len) throws IOException {
		try {
			builder.add(attribute, new String(text, offset, len));
			attribute = null;
		} catch (final VPackBuilderException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void writeRawUTF8String(final byte[] text, final int offset, final int length) throws IOException {
		try {
			builder.add(attribute, new String(text, offset, length));
			attribute = null;
		} catch (final VPackBuilderException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void writeUTF8String(final byte[] text, final int offset, final int length) throws IOException {
		try {
			builder.add(attribute, new String(text, offset, length));
			attribute = null;
		} catch (final VPackBuilderException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void writeRaw(final String text) throws IOException {
	}

	@Override
	public void writeRaw(final String text, final int offset, final int len) throws IOException {
	}

	@Override
	public void writeRaw(final char[] text, final int offset, final int len) throws IOException {
	}

	@Override
	public void writeRaw(final char c) throws IOException {
		try {
			builder.add(attribute, c);
			attribute = null;
		} catch (final VPackBuilderException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void writeBinary(final Base64Variant bv, final byte[] data, final int offset, final int len)
			throws IOException {
		try {
			builder.add(attribute, bv.encode(data, false));
			attribute = null;
		} catch (final VPackBuilderException e) {
			throw new IOException(e);
		}
	}

	public void writeVPack(final VPackSlice vpack) throws IOException {
		try {
			builder.add(attribute, vpack);
			attribute = null;
		} catch (final VPackBuilderException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void writeNumber(final int v) throws IOException {
		try {
			builder.add(attribute, v);
			attribute = null;
		} catch (final VPackBuilderException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void writeNumber(final long v) throws IOException {
		try {
			builder.add(attribute, v);
			attribute = null;
		} catch (final VPackBuilderException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void writeNumber(final BigInteger v) throws IOException {
		try {
			builder.add(attribute, v);
			attribute = null;
		} catch (final VPackBuilderException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void writeNumber(final double v) throws IOException {
		try {
			builder.add(attribute, v);
			attribute = null;
		} catch (final VPackBuilderException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void writeNumber(final float v) throws IOException {
		try {
			builder.add(attribute, v);
			attribute = null;
		} catch (final VPackBuilderException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void writeNumber(final BigDecimal v) throws IOException {
		try {
			builder.add(attribute, v);
			attribute = null;
		} catch (final VPackBuilderException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void writeNumber(final String encodedValue) throws IOException {
	}

	@Override
	public void writeBoolean(final boolean state) throws IOException {
		try {
			builder.add(attribute, state);
			attribute = null;
		} catch (final VPackBuilderException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void writeNull() throws IOException {
		try {
			builder.add(attribute, ValueType.NULL);
			attribute = null;
		} catch (final VPackBuilderException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void close() throws IOException {
		super.close();
		flush();
	}
}
