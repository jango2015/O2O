package so.contacts.hub.thirdparty.cinema.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CinemaRoomInfo implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final short SEAT_STATUS_ZL = 0;		// 位置可选
	public static final short SEAT_STATUS_LK = 1;		// 位置被锁
	
	public CinemaRoomInfo() {
		
	}	
	
	public long mpid;
	public long movieid;
	public long cinemaid;
	public String moviename;
	public String cinemaname;
	
	public String playtime;
	public int gewaprice;
	public int price;
	public int servicefee;
	public String language;
	public String edition;
	public long roomid;
	public String roomname;
	public String roomtype;
	public float lockminute;
	
	public int maxseat;
	public String closetime;
	
	public String remark;
	public String seatAmountStatus;
	
	public ArrayList<SeatRow> seatList;

    public String error_msg = "";
	
	public static class SeatRow implements Serializable{
	    private static final long serialVersionUID = 1L;
		public int rownum;
		public String rowid;
		public String columns; 
	}

}
