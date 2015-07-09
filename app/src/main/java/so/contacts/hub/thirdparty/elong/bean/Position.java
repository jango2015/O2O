package so.contacts.hub.thirdparty.elong.bean;

public class Position {

	protected double longitude;
	protected double latitude;
	protected int radius;
	
	public Position(){
		
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double value) {
		this.longitude = value;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double value) {
		this.latitude = value;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int value) {
		this.radius = value;
	}

}
