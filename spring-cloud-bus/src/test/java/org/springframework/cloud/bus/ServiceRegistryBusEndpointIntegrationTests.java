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

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.bus.event.ServiceRegistryRemoteApplicationEvent;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author Yash Chauhan
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
		classes = ServiceRegistryBusEndpointIntegrationTests.MyApp.class,
		properties = { "management.endpoints.web.exposure.include=*", "spring.application.name=foobar" })
@AutoConfigureTestRestTemplate
public class ServiceRegistryBusEndpointIntegrationTests {

	@Autowired
	private TestRestTemplate rest;

	@MockitoBean
	private BusBridge busBridge;

	@Test
	public void testEndpoint() {
		Map<String, Object> request = Map.of("status", "UP", "destination", "user", "metadata", "version=2");

		var response = this.rest.postForEntity("/actuator/busserviceregistry", request, String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		verify(this.busBridge, times(1)).send(argThat(event -> {
			assertThat(event).isInstanceOf(ServiceRegistryRemoteApplicationEvent.class);

			ServiceRegistryRemoteApplicationEvent registryEvent = (ServiceRegistryRemoteApplicationEvent) event;

			assertThat(registryEvent.getStatus()).isEqualTo("UP");
			assertThat(registryEvent.getDestinationService()).isEqualTo("user:**");
			assertThat(registryEvent.getMetadata()).containsEntry("version", "2");

			return true;
		}));
	}

	@SpringBootApplication
	@Import(TestChannelBinderConfiguration.class)
	static class MyApp {

	}

}
