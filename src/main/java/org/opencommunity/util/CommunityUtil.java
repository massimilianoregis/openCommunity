package org.opencommunity.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@PropertySource({"application.properties"})
@Configuration
public class CommunityUtil {
	@Value("${proxy}")	private String proxy;
	
	@Bean
	public Client client()
		{
		return new Client(proxy);
		}
}
