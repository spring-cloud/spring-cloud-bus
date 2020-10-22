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

import org.springframework.cloud.bus.BusBridge;
import org.springframework.cloud.bus.event.Destination;
import org.springframework.cloud.bus.event.RemoteApplicationEvent;

/**
 * @author Spencer Gibb
 */
public class AbstractBusEndpoint {

	private BusBridge busBridge;

	private String appId;

	private final Destination.Factory destinationFactory;

	public AbstractBusEndpoint(BusBridge busBridge, String appId, Destination.Factory destinationFactory) {
		this.busBridge = busBridge;
		this.appId = appId;
		this.destinationFactory = destinationFactory;
	}

	protected String getInstanceId() {
		return this.appId;
	}

	protected Destination.Factory getDestinationFactory() {
		return this.destinationFactory;
	}

	protected Destination getDestination(String original) {
		return destinationFactory.getDestination(original);
	}

	protected void publish(RemoteApplicationEvent event) {
		this.busBridge.send(event);
	}

}
