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

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.environment.EnvironmentManager;
import org.springframework.context.ApplicationListener;

/**
 * @author Spencer Gibb
 */
public class EnvironmentChangeListener implements ApplicationListener<EnvironmentChangeRemoteApplicationEvent> {

	private static Log log = LogFactory.getLog(EnvironmentChangeListener.class);

	@Autowired
	private EnvironmentManager env;

	@Override
	public void onApplicationEvent(EnvironmentChangeRemoteApplicationEvent event) {
		Map<String, String> values = event.getValues();
		log.info("Received remote environment change request. Keys/values to update " + values);
		for (Map.Entry<String, String> entry : values.entrySet()) {
			this.env.setProperty(entry.getKey(), entry.getValue());
		}
	}

}
