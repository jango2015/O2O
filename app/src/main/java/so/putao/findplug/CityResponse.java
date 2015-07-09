package so.putao.findplug;

import java.io.Serializable;
import java.util.List;

public class CityResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	String status;
	
	List<String> cities;

	@Override
	public String toString() {
		return "CityResponse [status=" + status + ", cities=" + cities + "]";
	}
	
}
