/*
 * Copyright 2015-2020 the original author or authors.
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

package org.springframework.cloud.bus.rsocket;

import io.rsocket.routing.client.spring.RoutingRSocketRequester;

import org.springframework.cloud.bus.BusBridge;
import org.springframework.cloud.bus.BusConstants;
import org.springframework.cloud.bus.event.RemoteApplicationEvent;

public class RSocketRequesterBusBridge implements BusBridge {

	public RSocketRequesterBusBridge(RoutingRSocketRequester requester) {
		this.requester = requester;
	}

	private final RoutingRSocketRequester requester;

	@Override
	public void send(RemoteApplicationEvent event) {
		RSocketDestination destination = null; //(RSocketDestination) event.getDestination();
		// cast to RSocketDestination
		requester.route(BusConstants.BUS_CONSUMER)
			.address(builder -> {
				// get tags out of destination
				destination.getTags().forEach(builder::with);
			})
			.data(event)
			.send().subscribe();
	}
}
