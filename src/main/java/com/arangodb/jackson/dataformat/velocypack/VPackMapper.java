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

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.MapperBuilder;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Mark Vollmary
 */
public class VPackMapper extends ObjectMapper {

	private static final long serialVersionUID = 1L;

	public static class Builder extends MapperBuilder<VPackMapper, Builder> {
		public Builder(VPackMapper m) {
			super(m);
		}
	}

	public static VPackMapper.Builder builder() {
		return new VPackMapper.Builder(new VPackMapper());
	}

	public static VPackMapper.Builder builder(VPackFactory jf) {
		return new VPackMapper.Builder(new VPackMapper(jf));
	}

	public VPackMapper() {
		this(new VPackFactory());
	}

	public VPackMapper(VPackFactory jf) {
		super(jf);
		checkSupportedVersion();
	}

	protected VPackMapper(VPackMapper src) {
		super(src);
		checkSupportedVersion();
	}

	private void checkSupportedVersion() {
		Version version = version();
		int major = version.getMajorVersion();
		int minor = version.getMinorVersion();
		if (major != 2 || minor < 10 || minor > 14) {
			Logger.getLogger(VPackMapper.class.getName())
					.log(Level.WARNING, "Unsupported version of jackson-databind: {0}", version);
		}
	}

	@Override
	public VPackMapper copy() {
		_checkInvalidCopy(VPackMapper.class);
		return new VPackMapper(this);
	}

	@Override
	public VPackFactory getFactory() {
		return (VPackFactory) _jsonFactory;
	}

}
