package so.putao.findplug;

import java.util.List;

import android.graphics.Bitmap;

/**
 * 
 * @author putao_lhq
 * @version 2014年9月24日
 */
public class YellowPageItemCity58 extends YelloPageItem<City58Item> {
	
	private static final long serialVersionUID = 1L;

	private City58Item data;
	
	public YellowPageItemCity58(City58Item data) {
		this.data = data;
	}
	
	@Override
	public SourceItemObject getData() {
		return data;
	}

	@Override
	public String getName() {
		return data.title;
	}

	@Override
	public List<String> getNumbers() {
		return null;
	}

	@Override
	public double getDistance() {
		return 0;
	}

	@Override
	public String getBusinessUrl() {
		return data.targeturl;
	}

	@Override
	public Bitmap getLogoBitmap() {
		return null;
	}

	@Override
	public String getRegion() {
		return null;
	}

	@Override
	public String getCategorie() {
		return null;
	}

	@Override
	public float getAvg_rating() {
		return 0;
	}

	@Override
	public String getPhotoUrl() {
		return null;
	}

	@Override
	public void setTag(Object object) {
		
	}

	@Override
	public Object getTag() {
		return null;
	}

	@Override
	public String getItemId() {
		return null;
	}

	@Override
	public String getAddress() {
		return null;
	}

	@Override
	public boolean hasCoupon() {
		return false;
	}

	@Override
	public boolean hasDeal() {
		return false;
	}

	@Override
	public int getAvgPrice() {
		return 0;
	}

	@Override
	public boolean isSeleted() {
		return false;
	}

}
