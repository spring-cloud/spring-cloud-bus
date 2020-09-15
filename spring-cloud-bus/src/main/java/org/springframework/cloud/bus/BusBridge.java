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

package org.springframework.cloud.bus;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.cloud.bus.event.AckRemoteApplicationEvent;
import org.springframework.cloud.bus.event.RemoteApplicationEvent;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.support.MessageBuilder;

public class BusBridge implements ApplicationListener<RemoteApplicationEvent> {

	private final Log log = LogFactory.getLog(getClass());

	private final ServiceMatcher serviceMatcher;

	private final StreamBridge streamBridge;

	private final BusProperties properties;

	public BusBridge(ServiceMatcher serviceMatcher, StreamBridge streamBridge,
			BusProperties properties) {
		this.serviceMatcher = serviceMatcher;
		this.streamBridge = streamBridge;
		this.properties = properties;
	}

	@Override
	public void onApplicationEvent(RemoteApplicationEvent event) {
		if (this.serviceMatcher.isFromSelf(event)
				&& !(event instanceof AckRemoteApplicationEvent)) {
			if (log.isDebugEnabled()) {
				log.debug("Sending remote event on bus: " + event);
			}
			// TODO: configurable mimetype?
			this.streamBridge.send(properties.getDestination(),
					MessageBuilder.withPayload(event).build());
		}
	}

}
