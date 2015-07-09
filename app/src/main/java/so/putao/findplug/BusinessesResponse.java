package so.putao.findplug;

import java.io.Serializable;
import java.util.ArrayList;

public class BusinessesResponse implements Serializable{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    public String status;
    public int count;
    public int total_count;
    public ArrayList<DianPingBusiness> businesses;
    @Override
    public String toString() {
        if(!status.equals("OK")) {
            return "{" + "status:" + status + "}";
        }
        // TODO Auto-generated method stub
        return "{" + "status:" + status + ",count:" + count + ",businesses.size:" + businesses.size() + ", " + (businesses.size()>0?businesses.get(0):"") + "}";
    }
}
