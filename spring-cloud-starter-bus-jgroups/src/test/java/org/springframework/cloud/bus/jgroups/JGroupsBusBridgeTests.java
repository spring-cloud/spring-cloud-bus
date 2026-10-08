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

import java.util.Map;
import java.util.function.Consumer;

import org.jgroups.BytesMessage;
import org.jgroups.JChannel;
import org.jgroups.Receiver;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import org.springframework.cloud.bus.event.EnvironmentChangeRemoteApplicationEvent;
import org.springframework.cloud.bus.event.RemoteApplicationEvent;
import org.springframework.cloud.bus.jackson.SubtypeModule;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link JGroupsBusBridge}.
 *
 * @author Yash Chauhan
 */
class JGroupsBusBridgeTests {

	private final JGroupsBusProperties properties = new JGroupsBusProperties();

	private final ObjectMapper objectMapper = JsonMapper.builder()
		.addModule(new SubtypeModule(EnvironmentChangeRemoteApplicationEvent.class))
		.build();

	private final JChannel channel = mock(JChannel.class);

	private final JGroupsChannelFactory channelFactory = mock(JGroupsChannelFactory.class);

	@org.junit.jupiter.api.BeforeEach
	void setUp() throws Exception {
		when(this.channelFactory.create()).thenReturn(this.channel);
	}

	@org.junit.jupiter.api.Test
	void shouldConnectToConfiguredCluster() throws Exception {
		new JGroupsBusBridge(this.properties, this.objectMapper, event -> {
		}, this.channelFactory);

		verify(this.channel).connect(this.properties.getClusterName());
	}

	@org.junit.jupiter.api.Test
	void shouldSendEventAsBytesMessage() throws Exception {
		JGroupsBusBridge bridge = new JGroupsBusBridge(this.properties, this.objectMapper, event -> {
		}, this.channelFactory);

		RemoteApplicationEvent event = new EnvironmentChangeRemoteApplicationEvent("source", "origin", "destination",
				Map.of("test.property", "test-value"));

		bridge.send(event);

		verify(this.channel).send(any(BytesMessage.class));
	}

	@org.junit.jupiter.api.Test
	void shouldReceiveAndConsumeEvent() throws Exception {
		Consumer<RemoteApplicationEvent> eventConsumer = mock(Consumer.class);

		new JGroupsBusBridge(this.properties, this.objectMapper, eventConsumer, this.channelFactory);

		Receiver receiver = captureReceiver();

		RemoteApplicationEvent event = new EnvironmentChangeRemoteApplicationEvent("source", "origin", "destination",
				Map.of("test.property", "test-value"));
		byte[] payload = this.objectMapper.writeValueAsBytes(event);
		BytesMessage message = new BytesMessage(null, payload);

		receiver.receive(message);

		verify(eventConsumer).accept(any(RemoteApplicationEvent.class));
	}

	@org.junit.jupiter.api.Test
	void shouldCloseChannel() {
		JGroupsBusBridge bridge = new JGroupsBusBridge(this.properties, this.objectMapper, event -> {
		}, this.channelFactory);

		bridge.close();

		verify(this.channel).close();
	}

	private Receiver captureReceiver() {
		org.mockito.ArgumentCaptor<Receiver> captor = org.mockito.ArgumentCaptor.forClass(Receiver.class);
		verify(this.channel).setReceiver(captor.capture());
		return captor.getValue();
	}

}
