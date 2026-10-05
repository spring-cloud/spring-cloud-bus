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

import org.junit.Test;

import org.springframework.cloud.bus.event.ServiceRegistryRemoteApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Yash Chauhan
 */
public class ServiceRegistryBusEndpointTests {

	@Test
	public void instanceId() {
		ServiceRegistryBusEndpoint endpoint = new ServiceRegistryBusEndpoint(null, "foo",
				originalDestination -> () -> originalDestination);
		assertThat(endpoint.getInstanceId()).isEqualTo("foo");
	}

	@Test
	public void publishesServiceRegistryEvent() {
		ApplicationEventPublisher publisher = mock(ApplicationEventPublisher.class);
		ServiceRegistryBusEndpoint endpoint = new ServiceRegistryBusEndpoint(publisher, "foo",
				originalDestination -> () -> originalDestination);

		endpoint.setStatus("UP", "user", "version=2");

		verify(publisher).publishEvent(argThat(event -> {
			assertThat(event).isInstanceOf(ServiceRegistryRemoteApplicationEvent.class);

			ServiceRegistryRemoteApplicationEvent registryEvent = (ServiceRegistryRemoteApplicationEvent) event;

			assertThat(registryEvent.getStatus()).isEqualTo("UP");
			assertThat(registryEvent.getDestinationService()).isEqualTo("user");
			assertThat(registryEvent.getMetadata()).containsEntry("version", "2");

			return true;
		}));
	}

}
