/**
 * sml
 */
package so.putao.findplug;

import java.io.Serializable;
import java.util.ArrayList;

public class CouponResponse implements Serializable{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    String status;
    int count;
    int total_count;
    ArrayList<DianpingCoupon> coupons;
    @Override
    public String toString() {
        if(!status.equals("OK")) {
            return "{" + "status:" + status + "}";
        }
        // TODO Auto-generated method stub
        return "{" + "status:" + status + ",count:" + count + ",businesses.size:" + coupons.size() + ", " + (coupons.size()>0?coupons.get(0):"") + "}";
    }
}

