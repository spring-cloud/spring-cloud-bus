package org.springframework.cloud.bus.jackson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.bus.BusAutoConfiguration;
import org.springframework.cloud.bus.ConditionalOnBusEnabled;
import org.springframework.cloud.bus.endpoint.RefreshBusEndpoint;
import org.springframework.cloud.bus.event.RemoteApplicationEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.AbstractMessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.util.ClassUtils;
import org.springframework.util.MimeTypeUtils;

/**
 * @author Spencer Gibb
 * @author Dave Syer
 */
@Configuration
@ConditionalOnBusEnabled
@ConditionalOnClass({ RefreshBusEndpoint.class, ObjectMapper.class })
@AutoConfigureAfter(BusAutoConfiguration.class)
public class BusJacksonAutoConfiguration {

	@Bean
	public MessageConverter busJsonConverter() {
		return new BusJacksonMessageConverter();
	}

}

class BusJacksonMessageConverter extends AbstractMessageConverter {

	private static final String DEFAULT_PACKAGE = ClassUtils
			.getPackageName(RemoteApplicationEvent.class);

	private static final String CLASS_RESOURCE_PATTERN = "/**/*.class";

	private final ObjectMapper mapper = new ObjectMapper();

	private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

	private String[] packagesToScan = new String[] { DEFAULT_PACKAGE };

	public void setPackagesToScan(String[] packagesToScan) {
		List<String> packages = new ArrayList<>(Arrays.asList(packagesToScan));
		if (!packages.contains(DEFAULT_PACKAGE)) {
			packages.add(DEFAULT_PACKAGE);
		}
		this.packagesToScan = packages.toArray(new String[0]);
	}

	public BusJacksonMessageConverter() {
		super(MimeTypeUtils.APPLICATION_JSON);
		this.mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		this.mapper.registerModule(new SubtypeModule(findSubTypes()));
	}

	private Class<?>[] findSubTypes() {
		List<Class<?>> types = new ArrayList<>();
		if (this.packagesToScan != null) {
			for (String pkg : this.packagesToScan) {
				try {
					String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
							+ ClassUtils.convertClassNameToResourcePath(pkg)
							+ CLASS_RESOURCE_PATTERN;
					Resource[] resources = this.resourcePatternResolver
							.getResources(pattern);
					MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(
							this.resourcePatternResolver);
					for (Resource resource : resources) {
						if (resource.isReadable()) {
							MetadataReader reader = readerFactory
									.getMetadataReader(resource);
							String className = reader.getClassMetadata().getClassName();
							try {
								Class<?> type = ClassUtils.forName(className, null);
								types.add(type);
							}
							catch (Exception e) {
							}
						}
					}
				}
				catch (IOException ex) {
					throw new IllegalStateException(
							"Failed to scan classpath for remote event classes", ex);
				}
			}
		}
		return types.toArray(new Class<?>[0]);
	}

	@Override
	protected boolean supports(Class<?> aClass) {
		// This converter applies only to RemoteApplicationEvent and subclasses
		return RemoteApplicationEvent.class.isAssignableFrom(aClass);
	}

	@Override
	public Object convertFromInternal(Message<?> message, Class<?> targetClass,
			Object conversionHint) {
		Object result = null;
		try {
			Object payload = message.getPayload();

			if (payload instanceof byte[]) {
				result = this.mapper.readValue((byte[]) payload, targetClass);
			}
			else if (payload instanceof String) {
				result = this.mapper.readValue((String) payload, targetClass);
			}
		}
		catch (Exception e) {
			this.logger.error(e.getMessage(), e);
			return null;
		}
		return result;
	}
}
