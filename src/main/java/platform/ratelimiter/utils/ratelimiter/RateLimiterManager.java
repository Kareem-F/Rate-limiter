package platform.ratelimiter.utils.ratelimiter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import platform.ratelimiter.config.RateLimitingConfig;

// Spring takes care that this bean is singleton (only one instance is created
// per app context)
@Component
public class RateLimiterManager {

	@Autowired
	private RateLimitingConfig rateLimitingConfig;

	private Map<String, Integer> maxRequestsByApiKey;

	private Map<String, LocalDateTime> bannedKeysVsUnbanningTime;

	private Map<String, RateLimitingInfo> rateLimitingInfoByApiKey;

	@PostConstruct
	public void init() {
		maxRequestsByApiKey = rateLimitingConfig.getMaxRequestsByApiKey();
		bannedKeysVsUnbanningTime = new HashMap<>();
		rateLimitingInfoByApiKey = new HashMap<>();
	}

	/**
	 * processes rate limiting info returns true if key rate is valid, false
	 * otherwise
	 */
	public boolean verifyKeyRateLimit(String apiKey) {
		if (!isClientBanned(apiKey)) {
			RateLimitingInfo clientRateInfo = rateLimitingInfoByApiKey.getOrDefault(apiKey, null);
			if (clientRateInfo == null) {
				clientRateInfo = new RateLimitingInfo(apiKey,
						maxRequestsByApiKey.getOrDefault(apiKey, Constants.DEFAULT_REQUESTS_LIMIT));
			}
			boolean clientExceededRate = clientRateInfo.clientExceededRate();
			if (clientExceededRate) {
				banClient(apiKey);
				return false;
			}
			rateLimitingInfoByApiKey.put(apiKey, clientRateInfo);
			return true;

		}
		return false;
	}

	private boolean isClientBanned(String apiKey) {
		if (apiKey != null && !apiKey.trim().isEmpty()) {
			if (bannedKeysVsUnbanningTime.containsKey(apiKey)) {
				LocalDateTime unbanningTime = bannedKeysVsUnbanningTime.get(apiKey);
				LocalDateTime currentTime = LocalDateTime.now();
				if (currentTime.isAfter(unbanningTime)) {
					bannedKeysVsUnbanningTime.remove(apiKey);
					return false;
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * add client to banned keys list and remove it from active keys
	 *
	 */
	private void banClient(String apiKey) {
		bannedKeysVsUnbanningTime.put(apiKey,
				LocalDateTime.now().plusSeconds(Constants.BANNING_TIME_PERIOD_IN_SECONDS));
		rateLimitingInfoByApiKey.remove(apiKey);
		if (Constants.DEBUG) {
			System.out.println("Banning client: " + apiKey);
		}
	}

}
