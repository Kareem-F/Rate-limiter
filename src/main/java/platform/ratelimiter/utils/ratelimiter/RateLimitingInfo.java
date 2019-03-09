package platform.ratelimiter.utils.ratelimiter;

import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.Deque;

public class RateLimitingInfo {

	private String apiKey;

	private Integer requestsLimit;

	private Deque<LocalDateTime> requestsTimestamps;

	private LocalDateTime resetAt;

	protected RateLimitingInfo(String apiKey, Integer requestsLimit) {
		this.apiKey = apiKey;
		this.requestsLimit = requestsLimit;
		this.requestsTimestamps = new ArrayDeque<>();
		resetConfig();
	}

	/**
	 * adds request to client and checks if client exceeded rate
	 */
	protected boolean clientExceededRate() {
		LocalDateTime currentTime = LocalDateTime.now();
		if (currentTime.isAfter(resetAt)) {
			resetConfig();
			requestsTimestamps.addLast(currentTime);
			if (Constants.DEBUG) {
				System.out.println("added one event for client " + apiKey);
			}
		} else {
			requestsTimestamps.addLast(currentTime);
			if (Constants.DEBUG) {
				System.out.println("added one event for client " + apiKey);
			}
		}
		if (requestsTimestamps.size() > requestsLimit) {
			return true;
		}
		return false;
	}

	private void resetConfig() {
		LocalDateTime currentTime = LocalDateTime.now();
		while (!requestsTimestamps.isEmpty()
				&& currentTime.minusSeconds(Constants.RATE_LIMITER_GLOBAL_TIME_PERIOD_IN_SECONDS)
						.isAfter(requestsTimestamps.peekFirst())) {
			requestsTimestamps.pollFirst();
			if (Constants.DEBUG) {
				System.out.println("Poped one event for client " + apiKey);
			}
		}
		this.resetAt = currentTime.plusSeconds(Constants.RATE_LIMITER_GLOBAL_TIME_PERIOD_IN_SECONDS);
	}

}
