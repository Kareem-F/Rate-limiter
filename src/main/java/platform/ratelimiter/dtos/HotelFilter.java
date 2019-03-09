package platform.ratelimiter.dtos;

import org.hibernate.validator.constraints.NotBlank;

import platform.ratelimiter.enums.SortOrder;

public class HotelFilter {

	@NotBlank
	private String cityId;

	private SortOrder sortOrder = SortOrder.ASC; // assuming default ascending order

	public HotelFilter() {

	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
	}

	public void setSortOrder(SortOrder sortOrder) {
		this.sortOrder = sortOrder;
	}

	public SortOrder getSortOrder() {
		return sortOrder;
	}

	public String getCityId() {
		return cityId;
	}

}
