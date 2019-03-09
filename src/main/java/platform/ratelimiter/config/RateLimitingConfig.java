package platform.ratelimiter.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ratelimiter")
public class RateLimitingConfig {

	private final Map<String, Integer> maxRequestsByApiKey = new HashMap<>();

	public Map<String, Integer> getMaxRequestsByApiKey() {
		return maxRequestsByApiKey;
	}

}
