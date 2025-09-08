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

package org.springframework.cloud.bus.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import org.springframework.context.ApplicationEvent;

/**
 * An event signalling that a remote event was sent somewhere in the system. This is not
 * itself a {@link RemoteApplicationEvent}, so it isn't sent over the bus, instead it is
 * generated locally (possibly in response to a remote event). Applications that want to
 * audit remote events can listen for this one and the {@link AckRemoteApplicationEvent}
 * from all the consumers (the {@link #getId() id} of this event is the
 * {@link AckRemoteApplicationEvent#getAckId() ackId} of the corresponding ACK.
 *
 * @author Dave Syer
 */
@SuppressWarnings("serial")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonIgnoreProperties("source")
public class SentApplicationEvent extends ApplicationEvent {

	private static final Object TRANSIENT_SOURCE = new Object();

	private final String originService;

	private final String destinationService;

	private final String id;

	private Class<? extends RemoteApplicationEvent> type;

	protected SentApplicationEvent() {
		// for serialization libs like jackson
		this(TRANSIENT_SOURCE, null, null, null, RemoteApplicationEvent.class);
	}

	public SentApplicationEvent(Object source, String originService, String destinationService, String id,
			Class<? extends RemoteApplicationEvent> type) {
		super(source);
		this.originService = originService;
		this.type = type;
		if (destinationService == null) {
			destinationService = "*";
		}
		if (!destinationService.contains(":")) {
			// All instances of the destination unless specifically requested
			destinationService = destinationService + ":**";
		}
		this.destinationService = destinationService;
		this.id = id;
	}

	public Class<? extends RemoteApplicationEvent> getType() {
		return this.type;
	}

	public void setType(Class<? extends RemoteApplicationEvent> type) {
		this.type = type;
	}

	public String getOriginService() {
		return this.originService;
	}

	public String getDestinationService() {
		return this.destinationService;
	}

	public String getId() {
		return this.id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.destinationService == null) ? 0 : this.destinationService.hashCode());
		result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
		result = prime * result + ((this.originService == null) ? 0 : this.originService.hashCode());
		result = prime * result + ((this.type == null) ? 0 : this.type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SentApplicationEvent other = (SentApplicationEvent) obj;
		if (this.destinationService == null) {
			if (other.destinationService != null) {
				return false;
			}
		}
		else if (!this.destinationService.equals(other.destinationService)) {
			return false;
		}
		if (this.id == null) {
			if (other.id != null) {
				return false;
			}
		}
		else if (!this.id.equals(other.id)) {
			return false;
		}
		if (this.originService == null) {
			if (other.originService != null) {
				return false;
			}
		}
		else if (!this.originService.equals(other.originService)) {
			return false;
		}
		if (this.type == null) {
			if (other.type != null) {
				return false;
			}
		}
		else if (!this.type.equals(other.type)) {
			return false;
		}
		return true;
	}

}
