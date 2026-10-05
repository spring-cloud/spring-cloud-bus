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

package org.springframework.cloud.bus;

import java.util.Collections;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import org.springframework.cloud.bus.event.ServiceRegistryListener;
import org.springframework.cloud.bus.event.ServiceRegistryRemoteApplicationEvent;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Yash Chauhan
 */
public class ServiceRegistryListenerTests {

	private ServiceRegistry<Registration> serviceRegistry;

	private Registration registration;

	private ServiceMatcher serviceMatcher;

	private ServiceRegistryListener listener;

	@Before
	public void setUp() {
		this.serviceRegistry = mock(ServiceRegistry.class);
		this.registration = mock(Registration.class);
		this.serviceMatcher = mock(ServiceMatcher.class);
		this.listener = new ServiceRegistryListener(this.serviceRegistry, this.registration, this.serviceMatcher);
	}

	@Test
	public void statusIsChangedWhenDestinationAndMetadataMatch() {
		when(this.serviceMatcher.isForSelf(org.mockito.ArgumentMatchers.any())).thenReturn(true);
		when(this.registration.getMetadata()).thenReturn(Collections.singletonMap("version", "2"));

		ServiceRegistryRemoteApplicationEvent event = event("UP", Collections.singletonMap("version", "2"));

		this.listener.onApplicationEvent(event);

		verify(this.serviceRegistry).setStatus(this.registration, "UP");
	}

	@Test
	public void statusIsNotChangedWhenDestinationDoesNotMatch() {
		when(this.serviceMatcher.isForSelf(org.mockito.ArgumentMatchers.any())).thenReturn(false);

		ServiceRegistryRemoteApplicationEvent event = event("UP", Collections.singletonMap("version", "2"));

		this.listener.onApplicationEvent(event);

		verify(this.serviceRegistry, never()).setStatus(this.registration, "UP");
	}

	@Test
	public void statusIsNotChangedWhenMetadataDoesNotMatch() {
		when(this.serviceMatcher.isForSelf(org.mockito.ArgumentMatchers.any())).thenReturn(true);
		when(this.registration.getMetadata()).thenReturn(Collections.singletonMap("version", "1"));

		ServiceRegistryRemoteApplicationEvent event = event("UP", Collections.singletonMap("version", "2"));

		this.listener.onApplicationEvent(event);

		verify(this.serviceRegistry, never()).setStatus(this.registration, "UP");
	}

	@Test
	public void statusIsChangedWhenMetadataFilterIsEmpty() {
		when(this.serviceMatcher.isForSelf(org.mockito.ArgumentMatchers.any())).thenReturn(true);

		ServiceRegistryRemoteApplicationEvent event = event("UP", Collections.emptyMap());

		this.listener.onApplicationEvent(event);

		verify(this.serviceRegistry).setStatus(this.registration, "UP");
	}

	@Test
	public void allMetadataEntriesMustMatch() {
		Map<String, String> registrationMetadata = Map.of("version", "2", "region", "india");
		Map<String, String> eventMetadata = Map.of("version", "2", "region", "india");

		when(this.serviceMatcher.isForSelf(org.mockito.ArgumentMatchers.any())).thenReturn(true);
		when(this.registration.getMetadata()).thenReturn(registrationMetadata);

		ServiceRegistryRemoteApplicationEvent event = event("OUT_OF_SERVICE", eventMetadata);

		this.listener.onApplicationEvent(event);

		verify(this.serviceRegistry).setStatus(this.registration, "OUT_OF_SERVICE");
	}

	@Test
	public void statusIsNotChangedWhenOneMetadataEntryDoesNotMatch() {
		Map<String, String> registrationMetadata = Map.of("version", "2", "region", "india");
		Map<String, String> eventMetadata = Map.of("version", "2", "region", "us");

		when(this.serviceMatcher.isForSelf(org.mockito.ArgumentMatchers.any())).thenReturn(true);
		when(this.registration.getMetadata()).thenReturn(registrationMetadata);

		ServiceRegistryRemoteApplicationEvent event = event("OUT_OF_SERVICE", eventMetadata);

		this.listener.onApplicationEvent(event);

		verify(this.serviceRegistry, never()).setStatus(eq(this.registration), eq("OUT_OF_SERVICE"));
	}

	private ServiceRegistryRemoteApplicationEvent event(String status, Map<String, String> metadata) {
		return new ServiceRegistryRemoteApplicationEvent(this, "origin", () -> "user:**", status, metadata);
	}

}
