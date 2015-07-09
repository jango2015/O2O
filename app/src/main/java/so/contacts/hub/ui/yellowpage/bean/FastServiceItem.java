package so.contacts.hub.ui.yellowpage.bean;

import java.io.Serializable;

public class FastServiceItem implements Serializable {

	private static final long serialVersionUID = 1L;

	private String service_name;
	private String service_url;

	public String getService_name() {
		return service_name;
	}

	public void setService_name(String service_name) {
		this.service_name = service_name;
	}

	public String getService_url() {
		return service_url;
	}

	public void setService_url(String service_url) {
		this.service_url = service_url;
	}

}
