package so.putao.findplug;

import java.util.ArrayList;
import java.util.List;

import so.contacts.hub.ui.yellowpage.bean.SougouHmtItem;
import android.graphics.Bitmap;

public class YelloPageItemSougou extends YelloPageItem<SougouHmtItem>{
	
    private static final long serialVersionUID = 1L;
    Object mObject;
	SougouHmtItem mHMTResultItem;
	
	public YelloPageItemSougou(SougouHmtItem hMTResultItem) {
		this.mHMTResultItem = hMTResultItem;
	}
	
	@Override
	public SougouHmtItem getData() {
		return mHMTResultItem;
	}
	
	@Override
	public String getName() {
		return mHMTResultItem.getName();
	}
	
	
	@Override
	public double getDistance() {
		if(mHMTResultItem.getDistance() < 0){
			return 0;
		}
		return mHMTResultItem.getDistance();
	}
	
	@Override
	public String getBusinessUrl() {
		return this.mHMTResultItem.getMerchantDetailUrl();
	}
	
	@Override
	public Bitmap getLogoBitmap() {
		return this.mHMTResultItem.LoadItemLogo();
	}

	@Override
	public void setTag(Object object) {
		mObject = object;
	}

	@Override
	public Object getTag() {
		return mObject;
	}

	@Override
	public String getRegion() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getCategorie() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public float getAvg_rating() {
		// TODO Auto-generated method stub
		return -100;
	}

	@Override
	public String getPhotoUrl() {
		// TODO Auto-generated method stub
		return this.mHMTResultItem.getPhotoUrl();
	}

	@Override
	public List<String> getNumbers() {
		List<String> numberList = new ArrayList<String>();
		numberList.add(mHMTResultItem.getNumber());
		return numberList;
	}

	@Override
	public String getItemId() {
		return "";
	}

	@Override
	public String getAddress() {
		return "";
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
		return -1;
	}

	@Override
	public boolean isSeleted() {
		if(null != mHMTResultItem){
			return mHMTResultItem.isSelected();
		}
		return false;
	}	
}
