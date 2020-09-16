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

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.cloud.commons.util.IdUtils;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

import static org.springframework.cloud.bus.BusProperties.PREFIX;

/**
 * {@link EnvironmentPostProcessor} that sets the default properties for the Bus.
 *
 * @author Dave Syer
 * @since 1.0.0
 */
public class BusEnvironmentPostProcessor implements EnvironmentPostProcessor {

	private static final String PROPERTY_SOURCE_NAME = "springCloudBusDefaultProperties";

	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
		Map<String, Object> map = new HashMap<>();
		// TODO: use user imports as defaults.
		map.put("spring.cloud.function.definition", "busConsumer");
		map.put("spring.cloud.stream.function.bindings.busConsumer-in-0", SpringCloudBusClient.INPUT);
		map.put("spring.cloud.stream.source", SpringCloudBusClient.DESTINATION);
		map.put("spring.cloud.stream.bindings." + SpringCloudBusClient.INPUT + ".destination",
				SpringCloudBusClient.DESTINATION);
		map.put("spring.cloud.stream.bindings." + SpringCloudBusClient.OUTPUT + ".content-type",
				environment.getProperty(PREFIX + ".content-type", "application/json"));
		map.put("spring.cloud.bus.id", IdUtils.getUnresolvedServiceId());
		addOrReplace(environment.getPropertySources(), map);
	}

	private void addOrReplace(MutablePropertySources propertySources, Map<String, Object> map) {
		MapPropertySource target = null;
		if (propertySources.contains(PROPERTY_SOURCE_NAME)) {
			PropertySource<?> source = propertySources.get(PROPERTY_SOURCE_NAME);
			if (source instanceof MapPropertySource) {
				target = (MapPropertySource) source;
				for (String key : map.keySet()) {
					if (!target.containsProperty(key)) {
						target.getSource().put(key, map.get(key));
					}
				}
			}
		}
		if (target == null) {
			target = new MapPropertySource(PROPERTY_SOURCE_NAME, map);
		}
		if (!propertySources.contains(PROPERTY_SOURCE_NAME)) {
			propertySources.addLast(target);
		}
	}

}
