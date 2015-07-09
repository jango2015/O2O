/**
 * sml
 */
package so.putao.findplug;

import java.util.ArrayList;

public class DianpingCoupon extends SourceItemObject {
	
	private static final long serialVersionUID = 1L;
	public String coupon_id;// deal ID
	public String title;//
	public String description;//
	// public String city;//
	// public String list_price;//
	// public String current_price;//

	public ArrayList<String> regions;//
	public ArrayList<String> categories;//

	public int download_count;//
	public String expiration_date;
	public String publish_date;
	public int distance;//
	public String coupon_url;
	public String coupon_h5_url;

	public float commission_ratio;//
	public ArrayList<Business> businesses;//

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "{" + "deal_id:" + coupon_id + ",title:" + title
				+ ",description:" + description + ",publish_date:"
				+ publish_date + ",image_url:" + getPhotoUrl() + ",deal_url:"
				+ coupon_url + "}";
	}

	@Override
	public double getLatitude() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getLongitude() {
		// TODO Auto-generated method stub
		return 0;
	}
}
