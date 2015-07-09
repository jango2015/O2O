package so.contacts.hub.ui.yellowpage.bean;

import java.util.ArrayList;
import java.util.List;

import com.yulong.android.contacts.discover.R;

import so.contacts.hub.ContactsApp;

public class SubwayManager {

	private static List<SubwayCity> cities = new ArrayList<SubwayCity>();
	static {
	    String[] citysInfo = ContactsApp.getInstance().getResources().getStringArray(R.array.putao_subway_cities);
	    if(citysInfo != null && citysInfo.length > 0) {
    	    try {
        	    for(String city : citysInfo) {
        	        String[] arr = city.split(",");
        	        if(arr==null || arr.length!=3)
        	            continue;
        	        
        	        cities.add(new SubwayCity(arr[0], arr[1], arr[2]));
        	    }
    	    } catch (Exception e){
    	    }
	    }
	}
	
	public static String getCode(String city) {
		for (int i = 0; i < cities.size(); i++) {
			SubwayCity subCity = cities.get(i);
			if (subCity.getCityName().equals(city)) {
				return subCity.getCode();
			}
		}
		return "";
	}
	
	public static List<SubwayCity> getCities() {
		return cities;
	}
}
