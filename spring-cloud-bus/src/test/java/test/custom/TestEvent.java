/*
 * Copyright 2012-present the original author or authors.
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

package test.custom;

import org.springframework.cloud.bus.event.RemoteApplicationEvent;

@SuppressWarnings("serial")
public class TestEvent extends RemoteApplicationEvent {

	private String key;

	@SuppressWarnings("unused")
	private TestEvent() {
	}

	public TestEvent(Object source, String originService, String destinationService, String key) {
		super(source, originService, destinationService);
		this.key = key;
	}

	public String getKey() {
		return this.key;
	}

}
