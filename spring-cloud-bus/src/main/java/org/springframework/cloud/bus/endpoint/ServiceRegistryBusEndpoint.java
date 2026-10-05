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

import java.util.Collections;
import java.util.Map;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.cloud.bus.event.Destination;
import org.springframework.cloud.bus.event.ServiceRegistryRemoteApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.Assert;

/**
 * Actuator endpoint for changing the status of service registry registrations through
 * Spring Cloud Bus.
 *
 * @author Yash Chauhan
 */
@Endpoint(id = "busserviceregistry")
public class ServiceRegistryBusEndpoint extends AbstractBusEndpoint {

	public ServiceRegistryBusEndpoint(ApplicationEventPublisher publisher, String id,
			Destination.Factory destinationFactory) {
		super(publisher, id, destinationFactory);
	}

	@WriteOperation
	public void setStatus(String status, String destination, String metadata) {
		Assert.hasText(status, "status may not be empty");

		Map<String, String> metadataMap = parseMetadata(metadata);

		publish(new ServiceRegistryRemoteApplicationEvent(this, getInstanceId(), getDestination(destination), status,
				metadataMap));
	}

	private Map<String, String> parseMetadata(String metadata) {
		if (metadata == null || metadata.isBlank()) {
			return Collections.emptyMap();
		}

		int separator = metadata.indexOf('=');
		Assert.isTrue(separator > 0 && separator < metadata.length() - 1, "metadata must be in the format 'key=value'");

		String key = metadata.substring(0, separator);
		String value = metadata.substring(separator + 1);

		return Collections.singletonMap(key, value);
	}

}
