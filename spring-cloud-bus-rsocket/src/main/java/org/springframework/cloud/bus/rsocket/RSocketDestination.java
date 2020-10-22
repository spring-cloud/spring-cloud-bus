/*
 * Copyright 2015-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.bus.rsocket;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import io.rsocket.routing.common.Key;
import io.rsocket.routing.common.WellKnownKey;

import org.springframework.cloud.bus.event.Destination;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.StringUtils;

public class RSocketDestination implements Destination {

	private final Map<Key, String> tags;

	public RSocketDestination(String destination) {
		tags = getMapFromKeyValuePairs(destination);
	}

	public Map<Key, String> getTags() {
		return this.tags;
	}

	private static Map<Key, String> getMapFromKeyValuePairs(String delimitedProperties) {
		String[] properties = StringUtils.tokenizeToStringArray(delimitedProperties, ";");
		Map<Key, String> map = new HashMap<>();
		for (String property : properties) {
			int index = lowestIndexOf(property, ":", "=");
			String key = (index > 0) ? property.substring(0, index) : property;
			String value = (index > 0) ? property.substring(index + 1) : null;

			try {
				WellKnownKey wellKnownKey = WellKnownKey.valueOf(key);
				map.put(Key.of(wellKnownKey), value);
			}
			catch (IllegalArgumentException e) {
				// not a WellKnownKey, use string
				map.put(Key.of(key), value);
			}

		}
		return map;
	}

	private static int lowestIndexOf(String property, String... candidates) {
		int index = -1;
		for (String candidate : candidates) {
			int candidateIndex = property.indexOf(candidate);
			if (candidateIndex > 0) {
				index = (index != -1) ? Math.min(index, candidateIndex) : candidateIndex;
			}
		}
		return index;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		RSocketDestination that = (RSocketDestination) o;
		return Objects.equals(this.tags, that.tags);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.tags);
	}

	@Override
	public String toString() {
		return new ToStringCreator(this)
			.append("tags", tags)
			.toString();

	}
}
