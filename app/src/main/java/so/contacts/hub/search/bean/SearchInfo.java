package so.contacts.hub.search.bean;

public class SearchInfo implements java.io.Serializable{
    private int entry_type;
    
    private String words;
    
    private String category;
    
    private String city;
    
    private double longitude;
    
    private double latitude;
    
    private int page;
    
    private int limit;
    
    private int source;    
        
    public int getEntry_type() {
        return entry_type;
    }
    public void setEntry_type(int entry_type) {
        this.entry_type = entry_type;
    }

    public String getWords() {
        return words;
    }
    public void setWords(String words) {
        this.words = words;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public int getSource() {
        return source;
    }
    public void setSource(int source) {
        this.source = source;
    }
    public int getPage() {
        return page;
    }
    public void setPage(int page) {
        this.page = page;
    }
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}  
}
