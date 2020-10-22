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

package org.springframework.cloud.bus.event;

import java.util.Objects;

import org.springframework.core.style.ToStringCreator;
import org.springframework.util.StringUtils;

public class PathDestination implements Destination {
	
	private final String path;

	public PathDestination(String path) {
		if (path == null) {
			path = "**";
		}
		// If the path is not already a wildcard, match everything that
		// follows if there at most two path elements, and last element is not a global
		// wildcard already
		if (!"**".equals(path)) {
			if (StringUtils.countOccurrencesOf(path, ":") <= 1
				&& !StringUtils.endsWithIgnoreCase(path, ":**")) {
				// All instances of the destination unless specifically requested
				path = path + ":**";
			}
		}
		this.path = path;
	}

	public String getPath() {
		return this.path;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		PathDestination that = (PathDestination) o;
		return this.path.equals(that.path);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.path);
	}

	@Override
	public String toString() {
		return new ToStringCreator(this)
			.append("path", path)
			.toString();

	}
}
