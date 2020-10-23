/*
 * Copyright 2012-2019 the original author or authors.
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

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.bus.endpoint.EnvironmentBusEndpoint;
import org.springframework.cloud.bus.event.Destination;
import org.springframework.cloud.bus.event.EnvironmentChangeListener;
import org.springframework.cloud.bus.event.PathDestinationFactory;
import org.springframework.cloud.bus.event.TraceListener;
import org.springframework.cloud.context.environment.EnvironmentManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.springframework.cloud.bus.BusConstants.BUS_CONSUMER;

/**
 * @author Spencer Gibb
 * @author Dave Syer
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnBusEnabled
@EnableConfigurationProperties(BusProperties.class)
public class BusAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean(Destination.Factory.class)
	public PathDestinationFactory pathDestinationFactory() {
		return new PathDestinationFactory();
	}

	@Bean
	@ConditionalOnMissingBean
	public RemoteApplicationEventListener busRemoteApplicationEventListener(ServiceMatcher serviceMatcher,
			BusBridge busBridge) {
		return new RemoteApplicationEventListener(serviceMatcher, busBridge);
	}

	@Bean
	@ConditionalOnMissingBean(name = BUS_CONSUMER)
	public BusConsumer busConsumer(ApplicationEventPublisher applicationEventPublisher, ServiceMatcher serviceMatcher,
			ObjectProvider<BusBridge> busBridge, BusProperties properties, Destination.Factory destinationFactory) {
		return new BusConsumer(applicationEventPublisher, serviceMatcher, busBridge, properties, destinationFactory);
	}

	@Configuration(proxyBeanMethods = false)
	@ConditionalOnClass({ Endpoint.class })
	@ConditionalOnBean(HttpTraceRepository.class)
	@ConditionalOnProperty(BusProperties.PREFIX + ".trace.enabled")
	protected static class BusAckTraceConfiguration {

		@Bean
		@ConditionalOnMissingBean
		public TraceListener ackTraceListener(HttpTraceRepository repository) {
			return new TraceListener(repository);
		}

	}

	@Configuration(proxyBeanMethods = false)
	@ConditionalOnClass(EnvironmentManager.class)
	@ConditionalOnBean(EnvironmentManager.class)
	protected static class BusEnvironmentConfiguration {

		@Bean
		@ConditionalOnProperty(value = "spring.cloud.bus.env.enabled", matchIfMissing = true)
		public EnvironmentChangeListener environmentChangeListener() {
			return new EnvironmentChangeListener();
		}

		@Configuration(proxyBeanMethods = false)
		@ConditionalOnClass(Endpoint.class)
		protected static class EnvironmentBusEndpointConfiguration {

			@Bean
			@ConditionalOnAvailableEndpoint
			public EnvironmentBusEndpoint environmentBusEndpoint(ApplicationEventPublisher publisher, BusProperties bus,
					Destination.Factory destinationFactory) {
				return new EnvironmentBusEndpoint(publisher, bus.getId(), destinationFactory);
			}

		}

	}

}
