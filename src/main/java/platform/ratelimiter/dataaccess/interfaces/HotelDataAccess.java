package platform.ratelimiter.dataaccess.interfaces;

import java.util.List;

import platform.ratelimiter.dtos.HotelFilter;
import platform.ratelimiter.models.Hotel;

public interface HotelDataAccess {

	List<Hotel> filterHotels(HotelFilter hotelFilter);

}
