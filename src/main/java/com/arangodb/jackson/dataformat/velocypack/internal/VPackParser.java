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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.Map.Entry;

import com.arangodb.velocypack.VPackSlice;
import com.arangodb.velocypack.ValueType;
import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.base.ParserMinimalBase;

/**
 * @author Mark - mark at arangodb.com
 *
 */
public class VPackParser extends ParserMinimalBase {

	private VPackSlice currentValue;
	private String currentName;
	private Iterator<Entry<String, VPackSlice>> objectIterator;

	public VPackParser(final byte[] data, final int offset, final int features) {
		super(features);
		currentValue = new VPackSlice(data, offset);
		_currToken = null;
	}

	@Override
	public JsonToken nextToken() throws IOException {
		if (_currToken == null) {
			_currToken = JsonToken.START_OBJECT;
			return _currToken;
		}
		if (_currToken == JsonToken.START_OBJECT) {
			objectIterator = currentValue.objectIterator();
		}
		if (_currToken == JsonToken.FIELD_NAME) {
			_currToken = getToken(currentValue.getType(), currentValue);
			return _currToken;
		}
		if (objectIterator != null) {
			if (objectIterator.hasNext()) {
				final Entry<String, VPackSlice> next = objectIterator.next();
				currentName = next.getKey();
				currentValue = next.getValue();
				_currToken = JsonToken.FIELD_NAME;
			} else {
				_currToken = JsonToken.END_OBJECT;
				objectIterator = null;
			}
		}
		return _currToken;
	}

	private JsonToken getToken(final ValueType type, final VPackSlice value) {
		final JsonToken token;
		switch (type) {
		case OBJECT:
			token = value.getStart() == 0 ? JsonToken.START_OBJECT : JsonToken.VALUE_EMBEDDED_OBJECT;
			break;
		case ARRAY:
			token = JsonToken.START_ARRAY;
			break;
		case STRING:
			token = JsonToken.VALUE_STRING;
			break;
		case BOOL:
			token = value.isTrue() ? JsonToken.VALUE_TRUE : JsonToken.VALUE_FALSE;
			break;
		case DOUBLE:
			token = JsonToken.VALUE_NUMBER_FLOAT;
			break;
		case INT:
		case SMALLINT:
		case UINT:
			token = JsonToken.VALUE_NUMBER_INT;
			break;
		case NULL:
			token = JsonToken.VALUE_NULL;
			break;
		default:
			token = null;
			break;
		}
		return token;
	}

	@Override
	protected void _handleEOF() throws JsonParseException {
	}

	@Override
	public String getCurrentName() throws IOException {
		return currentName;
	}

	@Override
	public void close() throws IOException {
	}

	@Override
	public boolean isClosed() {
		return false;
	}

	@Override
	public JsonStreamContext getParsingContext() {
		return null;
	}

	@Override
	public void overrideCurrentName(final String name) {
		currentName = name;
	}

	@Override
	public String getText() throws IOException {
		return currentValue.getAsString();
	}

	@Override
	public char[] getTextCharacters() throws IOException {
		return null;
	}

	@Override
	public boolean hasTextCharacters() {
		return false;
	}

	@Override
	public int getTextLength() throws IOException {
		return currentValue.getLength();
	}

	@Override
	public int getTextOffset() throws IOException {
		return 0;
	}

	@Override
	public byte[] getBinaryValue(final Base64Variant b64variant) throws IOException {
		return currentValue.getAsBinary();
	}

	@Override
	public ObjectCodec getCodec() {
		return null;
	}

	@Override
	public void setCodec(final ObjectCodec c) {
	}

	@Override
	public Version version() {
		return null;
	}

	@Override
	public JsonLocation getTokenLocation() {
		return null;
	}

	@Override
	public JsonLocation getCurrentLocation() {
		return null;
	}

	@Override
	public Number getNumberValue() throws IOException {
		return currentValue.getAsNumber();
	}

	@Override
	public NumberType getNumberType() throws IOException {
		final NumberType type;
		switch (currentValue.getType()) {
		case SMALLINT:
			type = NumberType.INT;
			break;
		case INT:
			type = NumberType.LONG;
			break;
		case UINT:
			type = NumberType.BIG_INTEGER;
			break;
		case DOUBLE:
			type = NumberType.DOUBLE;
			break;
		default:
			type = null;
			break;
		}
		return type;
	}

	@Override
	public int getIntValue() throws IOException {
		return currentValue.getAsInt();
	}

	@Override
	public long getLongValue() throws IOException {
		return currentValue.getAsLong();
	}

	@Override
	public BigInteger getBigIntegerValue() throws IOException {
		return currentValue.getAsBigInteger();
	}

	@Override
	public float getFloatValue() throws IOException {
		return currentValue.getAsFloat();
	}

	@Override
	public double getDoubleValue() throws IOException {
		return currentValue.getAsDouble();
	}

	@Override
	public BigDecimal getDecimalValue() throws IOException {
		return currentValue.getAsBigDecimal();
	}

}
