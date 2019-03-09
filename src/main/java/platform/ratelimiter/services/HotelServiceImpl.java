package platform.ratelimiter.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import platform.ratelimiter.dataaccess.interfaces.HotelDataAccess;
import platform.ratelimiter.dtos.HotelFilter;
import platform.ratelimiter.models.Hotel;
import platform.ratelimiter.services.interfaces.HotelService;

@Service
public class HotelServiceImpl implements HotelService {

	@Autowired
	private HotelDataAccess hotelDA;

	@Override
	public List<Hotel> filterHotels(HotelFilter hotelFilter) {
		return hotelDA.filterHotels(hotelFilter);
	}

}
