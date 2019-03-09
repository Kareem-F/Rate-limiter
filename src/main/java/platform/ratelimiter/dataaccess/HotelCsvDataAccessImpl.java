package platform.ratelimiter.dataaccess;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import platform.ratelimiter.dataaccess.interfaces.HotelDataAccess;
import platform.ratelimiter.dtos.HotelFilter;
import platform.ratelimiter.models.Hotel;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBeanBuilder;

@Repository
public class HotelCsvDataAccessImpl implements HotelDataAccess {

	@Value("${hoteldb.csv.file.path:hoteldb.csv}")
	private String hoteldbCsvFilePath;

	@Override
	public List<Hotel> filterHotels(HotelFilter hotelFilter) {
		List<Hotel> hotels = fetchHotels();
		// Filtering can be done on the csv file level; creating objects iff CITY ==
		// filterCity and should be done in case of large files
		// to avoid filling the memory
		// In case of a real DB, the filtering and ordering will be done on DB level
		// Here, I am filtering after reading the file for simplicity
		Predicate<Hotel> hotelFilterPredicate = hotel -> hotelFilter.getCityId().equalsIgnoreCase(hotel.getCity());
		List<Hotel> filteredHotels = hotels.stream().filter(hotelFilterPredicate).collect(Collectors.toList());
		switch (hotelFilter.getSortOrder()) {
		case ASC:
			filteredHotels.sort((hotel1, hotel2) -> hotel1.getPrice().compareTo(hotel2.getPrice()));
			break;
		case DESC:
			filteredHotels.sort((hotel1, hotel2) -> hotel2.getPrice().compareTo(hotel1.getPrice()));
			break;
		default:
			break;
		}
		return filteredHotels;
	}

	// More thoughts are given to the CSV Reader in a real case in terms of having
	// it as singleton or having a pool of csv readers to speedup the reading
	// process
	// but this needs more thoughts for managing resources correctly
	private List<Hotel> fetchHotels() {
		List<Hotel> hotels = new ArrayList<>();
		try (CSVReader reader = new CSVReaderBuilder(
				new FileReader(new ClassPathResource(hoteldbCsvFilePath).getFile())).withSkipLines(1).build()) {
			ColumnPositionMappingStrategy<Hotel> mappingStrategy = getHotelsCsvMappingStrategy();
			hotels = new CsvToBeanBuilder<Hotel>(reader).withMappingStrategy(mappingStrategy).build().parse();
		} catch (FileNotFoundException ex) {
			// log it
			// should throw custom exception to include internal server error
			// will throw Runtime exception for simplicity
			throw new RuntimeException("Hotels DB file not found");
		} catch (IOException ex) {
			// log it
			throw new RuntimeException("Erro reading the file");
		}
		return hotels;
	}

	private ColumnPositionMappingStrategy<Hotel> getHotelsCsvMappingStrategy() {
		ColumnPositionMappingStrategy<Hotel> mappingStrategy = new ColumnPositionMappingStrategy<>();
		mappingStrategy.setType(Hotel.class);
		String[] columns = { "CITY", "HOTELID", "ROOM", "PRICE" };
		mappingStrategy.setColumnMapping(columns);
		return mappingStrategy;
	}

}
