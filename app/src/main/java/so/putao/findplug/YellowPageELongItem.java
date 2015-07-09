package so.putao.findplug;

import java.util.ArrayList;
import java.util.List;

import so.contacts.hub.thirdparty.elong.bean.ELongHotelItem;

import android.graphics.Bitmap;
import android.text.TextUtils;

/**
 * 
 * @author cj
 * @version 2014年9月25日
 */
public class YellowPageELongItem extends YelloPageItem<ELongHotelItem> {
    
    private static final long serialVersionUID = 1L;

    private String mBaseUrl = "http://m.elong.com/Hotel/Detail?ref=putao&hotelid=";
    
    private ELongHotelItem data;
    
    private String hotelid;
    
    public YellowPageELongItem(ELongHotelItem data) {
        this.data = data;
    }
    
    @Override
    public ELongHotelItem getData() {
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
        return mBaseUrl+(data!=null?data.getHotelId():"");
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
