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

package org.springframework.cloud.bus.event;

import java.util.Map;

import org.springframework.core.style.ToStringCreator;
import org.springframework.util.Assert;

/**
 * Remote application event used to change the status of service registry registrations
 * matching the specified service and metadata.
 *
 * @author Yash Chauhan
 */
@SuppressWarnings("serial")
public class ServiceRegistryRemoteApplicationEvent extends RemoteApplicationEvent {

	private String status;

	private Map<String, String> metadata;

	private ServiceRegistryRemoteApplicationEvent() {
		// for serializers
	}

	public ServiceRegistryRemoteApplicationEvent(Object source, String originService, Destination destination,
			String status, Map<String, String> metadata) {
		super(source, originService, destination);
		Assert.hasText(status, "status may not be empty");
		this.status = status;
		this.metadata = metadata;
	}

	public String getStatus() {
		return this.status;
	}

	public Map<String, String> getMetadata() {
		return this.metadata;
	}

	@Override
	public String toString() {
		return new ToStringCreator(this).append("id", getId())
			.append("originService", getOriginService())
			.append("destinationService", getDestinationService())
			.append("status", this.status)
			.append("metadata", this.metadata)
			.toString();
	}

}
