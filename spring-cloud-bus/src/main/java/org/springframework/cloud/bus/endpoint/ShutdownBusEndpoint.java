/*
 * Copyright 2012-2024 the original author or authors.
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

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.cloud.bus.event.Destination;
import org.springframework.cloud.bus.event.ShutdownRemoteApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.StringUtils;

/**
 * @author Ryan Baxter
 */
@Endpoint(id = "busshutdown")
public class ShutdownBusEndpoint extends AbstractBusEndpoint {

	public ShutdownBusEndpoint(ApplicationEventPublisher publisher, String id, Destination.Factory destinationFactory) {
		super(publisher, id, destinationFactory);
	}

	@WriteOperation
	public void busShutdownWithDestination(@Selector(match = Selector.Match.ALL_REMAINING) String[] destinations) {
		String destination = StringUtils.arrayToDelimitedString(destinations, ":");
		publish(new ShutdownRemoteApplicationEvent(this, getInstanceId(), getDestination(destination)));
	}

	@WriteOperation
	public void busShutdown() {
		publish(new ShutdownRemoteApplicationEvent(this, getInstanceId(), getDestination(null)));
	}

}
