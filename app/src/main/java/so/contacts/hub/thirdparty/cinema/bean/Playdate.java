package so.contacts.hub.thirdparty.cinema.bean;

import java.io.Serializable;

public class Playdate implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3484966879537513274L;
	private String playdate;

	public String getPlaydate() {
		return playdate;
	}

	public void setPlaydate(String playdate) {
		this.playdate = playdate;
	}

    @Override
    public String toString() {
        return "Playdate [playdate=" + playdate + "]";
    }
	
	
}
