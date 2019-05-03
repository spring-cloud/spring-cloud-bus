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

package org.springframework.cloud.bus.endpoint;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import org.springframework.cloud.bus.trace.BusEventTraceRepository;
import org.springframework.cloud.bus.trace.InMemoryBusEventTraceRepository;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Vibhor Tayal
 */
public class TraceBusEndpointTests {

	@Test
	public void trace() {
		BusEventTraceRepository repository = new InMemoryBusEventTraceRepository();
		repository.add(createBusEvent("id-1"));
		List<Map<String, Object>> traces = new TraceBusEndpoint(repository)
				.getBusTraceEvents();
		assertThat(traces).hasSize(1);
		Map<String, Object> trace = traces.get(0);
		assertThat(trace.get("id")).isEqualTo("id-1");
	}

	private Map<String, Object> createBusEvent(String id) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("signal", "spring.cloud.bus.sent");
		map.put("type", "type");
		map.put("id", id);
		map.put("origin", "Origin");
		map.put("destination", "Destination");
		return map;
	}

}
