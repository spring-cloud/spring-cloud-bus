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

package org.springframework.cloud.bus.event;

import org.springframework.core.style.ToStringCreator;

/**
 * @author Stefan Pfeiffer
 */
public class UnknownRemoteApplicationEvent extends RemoteApplicationEvent {

	protected String typeInfo;

	protected byte[] payload;

	@SuppressWarnings("unused")
	private UnknownRemoteApplicationEvent() {
		super();
		this.typeInfo = null;
		this.payload = null;
	}

	public UnknownRemoteApplicationEvent(Object source, String typeInfo, byte[] payload) {
		// Initialize originService with an empty String, to avoid NullPointer in
		// AntPathMatcher.
		super(source, "", () -> "unknown");
		this.typeInfo = typeInfo;
		this.payload = payload;
	}

	public String getTypeInfo() {
		return this.typeInfo;
	}

	public byte[] getPayload() {
		return this.payload;
	}

	public String getPayloadAsString() {
		if (this.payload != null) {
			return new String(this.payload);
		}
		return null;
	}

	@Override
	public String toString() {
		return new ToStringCreator(this).append("id", getId()).append("originService", getOriginService())
				.append("destinationService", getDestinationService()).append("typeInfo", typeInfo)
				.append("payload", getPayloadAsString()).toString();

	}

}
