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

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.style.ToStringCreator;

/**
 * Configuration properties for Spring Cloud Bus JGroups support.
 *
 * @author Yash Chauhan
 */
@ConfigurationProperties(JGroupsBusProperties.PREFIX)
public class JGroupsBusProperties {

	/**
	 * Configuration prefix for Spring Cloud Bus JGroups.
	 */
	public static final String PREFIX = "spring.cloud.bus.jgroups";

	/**
	 * Name of the JGroups cluster used by Spring Cloud Bus.
	 */
	private String clusterName = "spring-cloud-bus";

	public String getClusterName() {
		return this.clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	@Override
	public String toString() {
		return new ToStringCreator(this).append("clusterName", clusterName).toString();
	}

}
