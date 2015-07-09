package so.contacts.hub.thirdparty.elong.bean;

import java.util.Date;

/**
 * 获取酒店列表请求
 *
 */
public class HotelListBean {

	private Date ArrivalDate;
	
	private Date DepartureDate;
	
	private String CityId;
	
	private Position Position;
	
	private EnumCustomerType CustomerType;
	
	private EnumSortType Sort;
	
	private int PageIndex;
	
	private int PageSize;
	
	private String ResultType;
	
	public HotelListBean(){
		
	}

	public Date getArrivalDate() {
		return ArrivalDate;
	}

	public void setArrivalDate(Date arrivalDate) {
		ArrivalDate = arrivalDate;
	}

	public Date getDepartureDate() {
		return DepartureDate;
	}

	public void setDepartureDate(Date departureDate) {
		DepartureDate = departureDate;
	}

	public String getCityId() {
		return CityId;
	}

	public void setCityId(String cityId) {
		CityId = cityId;
	}

	public Position getPosition() {
		return Position;
	}

	public void setPosition(Position position) {
		Position = position;
	}

	public EnumCustomerType getCustomerType() {
		return CustomerType;
	}

	public void setCustomerType(EnumCustomerType customerType) {
		CustomerType = customerType;
	}

	public EnumSortType getSort() {
		return Sort;
	}

	public void setSort(EnumSortType sort) {
		Sort = sort;
	}

    public int getPageIndex() {
        return PageIndex;
    }

    public void setPageIndex(int pageIndex) {
        PageIndex = pageIndex;
    }

    public int getPageSize() {
        return PageSize;
    }

    public void setPageSize(int pageSize) {
        PageSize = pageSize;
    }

    public String getResultType() {
        return ResultType;
    }

    public void setResultType(String resultType) {
        ResultType = resultType;
    }
	
}
