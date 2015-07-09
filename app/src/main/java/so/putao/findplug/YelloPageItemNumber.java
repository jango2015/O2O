package so.putao.findplug;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.text.TextUtils;

public class YelloPageItemNumber extends YelloPageItem<String>{

	String mName;
	String mLogoUrl;
	String mNumber;
	
	private SourceItemObject itemObj;
	
	public YelloPageItemNumber(String name,String logoUrl,String number){
		mName = name;
		mLogoUrl = logoUrl;
		mNumber = number;
	}
	
	@Override
	public SourceItemObject getData() {
		return itemObj;
	}

	@Override
	public String getName() {
		return mName;
	}

	@Override
	public List<String> getNumbers() {
		if(TextUtils.isEmpty(mNumber)){
			return null;
		}else{
			List<String> numberList = new ArrayList<String>();
			numberList.add(mNumber);
			return numberList;
		}
	}

	@Override
	public double getDistance() {
		return 0;
	}

	@Override
	public String getBusinessUrl() {
		return null;
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
		return mLogoUrl;
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
