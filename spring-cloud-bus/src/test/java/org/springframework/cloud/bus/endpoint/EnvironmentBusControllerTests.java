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

package org.springframework.cloud.bus.endpoint;

import java.util.Map;

import org.junit.Test;

import org.springframework.cloud.bus.event.EnvironmentChangeRemoteApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;

import static org.assertj.core.api.Assertions.assertThat;

public class EnvironmentBusControllerTests {

	@Test
	public void publishesMultipleProperties() {
		ApplicationEventPublisher publisher = event -> {
			assertThat(event).isInstanceOf(EnvironmentChangeRemoteApplicationEvent.class);
			EnvironmentChangeRemoteApplicationEvent environmentEvent = (EnvironmentChangeRemoteApplicationEvent) event;
			assertThat(environmentEvent.getValues()).containsEntry("first", "one").containsEntry("second", "two");
		};

		EnvironmentBusController controller = new EnvironmentBusController(publisher, "foo",
				originalDestination -> () -> "test");

		controller.busEnv(Map.of("first", "one", "second", "two"));
	}

}
