package platform.ratelimiter.services.interfaces;

import java.util.List;

import platform.ratelimiter.dtos.HotelFilter;
import platform.ratelimiter.models.Hotel;

public interface HotelService {

	List<Hotel> filterHotels(HotelFilter hotelFilter);

}
