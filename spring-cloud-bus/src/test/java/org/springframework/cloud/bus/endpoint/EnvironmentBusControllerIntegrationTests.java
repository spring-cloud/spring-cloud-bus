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

import org.junit.After;
import org.junit.Test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.cloud.bus.BusAutoConfiguration;
import org.springframework.cloud.bus.event.EnvironmentChangeRemoteApplicationEvent;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

public class EnvironmentBusControllerIntegrationTests {

	private ConfigurableApplicationContext context;

	@After
	public void close() {
		if (this.context != null) {
			this.context.close();
		}
	}

	@Test
	public void busEnvAcceptsMultipleProperties() {
		this.context = SpringApplication.run(TestConfiguration.class, "--server.port=0",
				"--management.endpoints.web.exposure.include=*", "--spring.cloud.bus.id=test");

		EnvironmentChangeListener listener = this.context.getBean(EnvironmentChangeListener.class);
		int port = this.context.getEnvironment().getProperty("local.server.port", Integer.class);

		RestClient.create("http://localhost:" + port)
			.post()
			.uri(uriBuilder -> uriBuilder.path("/actuator/bus-env")
				.queryParam("first", "one")
				.queryParam("second", "two")
				.build())
			.retrieve()
			.toBodilessEntity();

		assertThat(listener.event).isNotNull();
		assertThat(listener.event.getValues()).containsEntry("first", "one").containsEntry("second", "two");
	}

	@Configuration(proxyBeanMethods = false)
	@EnableAutoConfiguration
	@ImportAutoConfiguration({ BusAutoConfiguration.class, TestChannelBinderConfiguration.class })
	static class TestConfiguration {

		@Bean
		EnvironmentChangeListener testEnvironmentChangeListener() {
			return new EnvironmentChangeListener();
		}

	}

	static class EnvironmentChangeListener implements ApplicationListener<EnvironmentChangeRemoteApplicationEvent> {

		private EnvironmentChangeRemoteApplicationEvent event;

		@Override
		public void onApplicationEvent(EnvironmentChangeRemoteApplicationEvent event) {
			this.event = event;
		}

	}

}
