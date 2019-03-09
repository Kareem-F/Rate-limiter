package platform.ratelimiter.utils.ratelimiter;

public final class Constants {

	private Constants() {

	}

	public static final int RATE_LIMITER_GLOBAL_TIME_PERIOD_IN_SECONDS = 10;

	public static final int DEFAULT_REQUESTS_LIMIT = 5;

	public static final int BANNING_TIME_PERIOD_IN_SECONDS = 300; // 5 minutes

	public static final boolean DEBUG = false;

}
