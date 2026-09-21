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

import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.cloud.bus.event.Destination;
import org.springframework.cloud.bus.event.EnvironmentChangeRemoteApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestControllerEndpoint(id = "bus-env")
public class EnvironmentBusController extends AbstractBusEndpoint {

	public EnvironmentBusController(ApplicationEventPublisher publisher, String id,
			Destination.Factory destinationFactory) {
		super(publisher, id, destinationFactory);
	}

	@PostMapping
	public void busEnv(@RequestParam Map<String, String> values) {
		publish(new EnvironmentChangeRemoteApplicationEvent(this, getInstanceId(), getDestination(null), values));
	}

}
