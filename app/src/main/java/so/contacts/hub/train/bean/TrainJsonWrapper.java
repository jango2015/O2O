package so.contacts.hub.train.bean;

import java.util.List;

public class TrainJsonWrapper<T> implements java.io.Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -2148510850497345587L;
    
    private String Retcode;
    private int Count;
    private List<T> Data;

	public void setRetcode(String retcode) {
		Retcode = retcode;
	}

    public String getRetcode() {
    	return this.Retcode;
    }

	public int getCount() {
		return Count;
	}

	public void setCount(int count) {
		Count = count;
	}

	public List<T> getData() {
		return Data;
	}

	public void setData(List<T> data) {
		Data = data;
	}
    
}
