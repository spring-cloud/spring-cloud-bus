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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.cloud.bus.ServiceMatcher;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;
import org.springframework.context.ApplicationListener;

/**
 * Listener for remote service registry status change events.
 *
 * @author Yash Chauhan
 */
public class ServiceRegistryListener implements ApplicationListener<ServiceRegistryRemoteApplicationEvent> {

	private static final Log LOG = LogFactory.getLog(ServiceRegistryListener.class);

	private final ServiceRegistry<Registration> serviceRegistry;

	private final Registration registration;

	private final ServiceMatcher serviceMatcher;

	public ServiceRegistryListener(ServiceRegistry<Registration> serviceRegistry, Registration registration,
			ServiceMatcher serviceMatcher) {
		this.serviceRegistry = serviceRegistry;
		this.registration = registration;
		this.serviceMatcher = serviceMatcher;
	}

	@Override
	public void onApplicationEvent(ServiceRegistryRemoteApplicationEvent event) {
		if (!this.serviceMatcher.isForSelf(event)) {
			LOG.info("Service registry status change not performed, the event was targeting "
					+ event.getDestinationService());
			return;
		}

		if (!matchesMetadata(event.getMetadata())) {
			LOG.info("Service registry status change not performed, registration metadata did not match.");
			return;
		}

		this.serviceRegistry.setStatus(this.registration, event.getStatus());

		LOG.info(
				"Changed service registry status to " + event.getStatus() + " for " + this.registration.getServiceId());
	}

	private boolean matchesMetadata(Map<String, String> metadata) {
		if (metadata == null || metadata.isEmpty()) {
			return true;
		}

		Map<String, String> registrationMetadata = this.registration.getMetadata();

		if (registrationMetadata == null) {
			return false;
		}

		return metadata.entrySet()
			.stream()
			.allMatch(entry -> entry.getValue().equals(registrationMetadata.get(entry.getKey())));
	}

}
