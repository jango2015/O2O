
package so.putao.findplug;

import java.util.ArrayList;
import java.util.List;

import so.contacts.hub.ui.yellowpage.bean.GaoDePoiItem;
import android.graphics.Bitmap;


public class YellowPageItemGaoDe extends YelloPageItem<GaoDePoiItem> {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    Object mObject;

    GaoDePoiItem mPoiItem;

    public YellowPageItemGaoDe(GaoDePoiItem poiResult) {
        mPoiItem = poiResult;
    }

    @Override
    public GaoDePoiItem getData() {
        // TODO Auto-generated method stub
        return mPoiItem;
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return mPoiItem.getName();
    }

    @Override
    public double getDistance() {
        if (mPoiItem.getDistance() < 0) {
            return 0;
        }
        return mPoiItem.getDistance();
    }

    @Override
    public String getBusinessUrl() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Bitmap getLogoBitmap() {
        return null;
    }

    @Override
    public void setTag(Object object) {
        this.mObject = object;

    }

    @Override
    public Object getTag() {
        // TODO Auto-generated method stub
        return this.mObject;
    }

    @Override
    public String getRegion() {
        String str = "";
//        int size = mBusiness.regions.size();
//        if (size > 2) {
//            size = 2;
//        }
//        for (int i = 0; i < size; i++) {
//            str += mBusiness.regions.get(i);
//        }
        return str;
    }

//    @Override
//    public String getCategorie() {
//        return mBusiness.categories.size() > 0 ? mBusiness.categories.get(0) : "";
//    }

//    @Override
//    public float getAvg_rating() {
//        // TODO Auto-generated method stub
//        return mBusiness.avg_rating;
//    }

//    @Override
//    public String getPhotoUrl() {
//        // TODO Auto-generated method stub
//        return mBusiness.s_photo_url;
//    }

    @Override
    public List<String> getNumbers() {
        List<String> numberList = new ArrayList<String>();
        numberList.add(mPoiItem.getTelephone());
        return numberList;
    }

    @Override
    public String getItemId() {
        return "";
    }

    @Override
    public String getAddress() {
        return mPoiItem.getAddress();
    }

    @Override
    public String getCategorie() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public float getAvg_rating() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getPhotoUrl() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean hasCoupon() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean hasDeal() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int getAvgPrice() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean isSeleted() {
        // TODO Auto-generated method stub
        return false;
    }

//	@Override
//	public boolean hasCoupon() {
//		return mBusiness.has_coupon == 1;
//	}
//
//	@Override
//	public boolean hasDeal() {
//		return mBusiness.has_deal == 1;
//	}
//
//	@Override
//	public int getAvgPrice() {
//		return mBusiness.avg_price;
//	}
//
//	@Override
//	public boolean isSeleted() {
//		return mBusiness.isSelected;
//	}

}
