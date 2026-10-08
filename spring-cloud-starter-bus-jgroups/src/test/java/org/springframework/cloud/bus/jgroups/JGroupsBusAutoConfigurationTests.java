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

package org.springframework.cloud.bus.jgroups;

import java.util.function.Consumer;

import org.jgroups.JChannel;
import tools.jackson.databind.ObjectMapper;

import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.cloud.bus.BusBridge;
import org.springframework.cloud.bus.event.RemoteApplicationEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link JGroupsBusAutoConfiguration}.
 *
 * @author Yash Chauhan
 */
class JGroupsBusAutoConfigurationTests {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
		.withUserConfiguration(TestConfiguration.class, JGroupsBusAutoConfiguration.class);

	@org.junit.jupiter.api.Test
	void shouldCreateJGroupsBusBridge() {
		this.contextRunner.run(context -> {
			assertThat(context).hasSingleBean(JGroupsBusBridge.class);
			assertThat(context).hasSingleBean(BusBridge.class);
		});
	}

	@org.junit.jupiter.api.Test
	void shouldBackOffWhenBusBridgeAlreadyExists() {
		this.contextRunner.withBean(BusBridge.class, () -> mock(BusBridge.class)).run(context -> {
			assertThat(context).doesNotHaveBean(JGroupsBusBridge.class);
			assertThat(context).hasSingleBean(BusBridge.class);
		});
	}

	@Configuration(proxyBeanMethods = false)
	static class TestConfiguration {

		@Bean
		JGroupsChannelFactory jGroupsChannelFactory() throws Exception {
			JGroupsChannelFactory factory = mock(JGroupsChannelFactory.class);
			when(factory.create()).thenReturn(mock(JChannel.class));
			return factory;
		}

		@Bean
		ObjectMapper objectMapper() {
			return new ObjectMapper();
		}

		@Bean
		Consumer<RemoteApplicationEvent> eventConsumer() {
			return event -> {
			};
		}

	}

}
