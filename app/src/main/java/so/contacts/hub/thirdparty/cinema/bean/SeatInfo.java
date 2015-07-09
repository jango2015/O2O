
package so.contacts.hub.thirdparty.cinema.bean;

import java.util.ArrayList;

/**
 * 每一排的座位信息
 * 
 * @author Sunny
 */
public class SeatInfo {
    private int row ;

    private String desc = null;

    private ArrayList<Seat> mSeatList = null;

    private String c(String paramString) {
        if (paramString == null)
            paramString = "";
        return paramString;
    }

    public Seat getSeat(int paramInt) {
        if ((paramInt > this.mSeatList.size()) || (paramInt < 0))
            return new Seat();
        return (Seat)this.mSeatList.get(paramInt);
    }

    public String getDesc() {
        return c(this.desc);
    }

    public void addSeat(Seat paramSeat) {
        this.mSeatList.add(paramSeat);
    }

    public void setRow(int paramString) {
        this.row = paramString;
    }

    public int getRow() {
        return this.row;
    }

    public ArrayList getSeatList() {
        return this.mSeatList;
    }

    public void setSeatList(ArrayList<Seat> seat_list) {
        this.mSeatList = seat_list;
    }

    public void setDesc(String paramString) {
        this.desc = paramString;
    }
}
