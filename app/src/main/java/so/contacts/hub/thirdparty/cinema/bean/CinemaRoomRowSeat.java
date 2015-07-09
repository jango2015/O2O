package so.contacts.hub.thirdparty.cinema.bean;

import java.io.Serializable;

public class CinemaRoomRowSeat implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1545603061015130035L;
	private Integer rownum;
	private String rowid;
	private String columns;
	public Integer getRownum() {
		return rownum;
	}
	public void setRownum(Integer rownum) {
		this.rownum = rownum;
	}
	public String getRowid() {
		return rowid;
	}
	public void setRowid(String rowid) {
		this.rowid = rowid;
	}
	public String getColumns() {
		return columns;
	}
	public void setColumns(String columns) {
		this.columns = columns;
	}
    @Override
    public String toString() {
        return "CinemaRoomRowSeat [rownum=" + rownum + ", rowid=" + rowid + ", columns=" + columns
                + "]";
    }
	
}
