package so.putao.findplug;

import java.io.Serializable;
import java.util.ArrayList;

public class DealsResponse implements Serializable{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    String status;
    int count;
    int total_count;
    ArrayList<DianpingDeal> deals;
    @Override
    public String toString() {
        if(!status.equals("OK")) {
            return "{" + "status:" + status + "}";
        }
        // TODO Auto-generated method stub
        return "{" + "status:" + status + ",count:" + count + ",businesses.size:" + deals.size() + ", " + (deals.size()>0?deals.get(0):"") + "}";
    }
}
