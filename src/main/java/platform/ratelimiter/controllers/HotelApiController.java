package platform.ratelimiter.controllers;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import platform.ratelimiter.dtos.HotelFilter;
import platform.ratelimiter.models.Hotel;
import platform.ratelimiter.services.interfaces.HotelService;
import platform.ratelimiter.utils.ratelimiter.RateLimiterManager;

@RestController
@RequestMapping("/api/hotels")
public class HotelApiController {

	@Autowired
	private HotelService hotelService;

	@Autowired
	private RateLimiterManager rateLimitManager;

	// In a real scenario, the response would have a structure including status,
	// message, data and errors for example
	// For simplicity it's a generic to facilitate returning messages and hotels
	@GetMapping
	public ResponseEntity<?> filterHotels(@RequestParam(value = "apiKey", required = true) String apiKey,
			@Valid HotelFilter hotelFilter) {
		if (rateLimitManager.verifyKeyRateLimit(apiKey)) {
			try {
				List<Hotel> hotels = hotelService.filterHotels(hotelFilter);
				return new ResponseEntity<List<Hotel>>(hotels, HttpStatus.OK);
			} catch (Exception ex) {
				return new ResponseEntity<String>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			// returning too many requests for both banned and just exceeding rate clients
			// for simplicity
			return new ResponseEntity<String>("Too many requests", HttpStatus.TOO_MANY_REQUESTS);
		}
	}
}
