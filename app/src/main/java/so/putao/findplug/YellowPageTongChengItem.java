package so.putao.findplug;

import so.contacts.hub.thirdparty.tongcheng.bean.TongChengHotelItem;

import java.util.ArrayList;
import java.util.List;


import android.graphics.Bitmap;
import android.text.TextUtils;

/**
 * 
 * @author xcx
 * @version 2014年12月25日
 */
public class YellowPageTongChengItem extends YelloPageItem<TongChengHotelItem> {
    
    private static final long serialVersionUID = 1L;

    private String mBaseUrl = "";
    
    private TongChengHotelItem data;
    
    private String hotelid;
    
    public double getMarkNum() {
        return data.getMarkNum();
    }
    public String getStarRatedName() {
        return data.getStarRatedName();
    }
    public double getLatitude() {
        return data.getLatitude();
    }


    public double getLongitude() {
        return data.getLongitude();
    }

    public YellowPageTongChengItem(TongChengHotelItem data) {
        this.data = data;
    }
    
    @Override
    public TongChengHotelItem getData() {
        return data;
    }

    @Override
    public String getName() {
        return data.getHotelName();
    }

    @Override
    public List<String> getNumbers() {
    	String phone = data.getPhone();
    	if( TextUtils.isEmpty(phone) ){
    		return null;
    	}
    	List<String> numberList = new ArrayList<String>();
        numberList.add(phone);
        return numberList;
    }

    @Override
    public double getDistance() {
        return data.getDistance();
    }

    @Override
    public String getBusinessUrl() {
        return "";
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
        return (float) data.getMarkNum();
    }

    @Override
    public String getPhotoUrl() {
        return data.getPhotoUrl();
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
        return data.getAddress();
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

    public String getHotelid() {
        return hotelid;
    }

    public void setHotelid(String hotelid) {
        this.hotelid = hotelid;
    }

}
