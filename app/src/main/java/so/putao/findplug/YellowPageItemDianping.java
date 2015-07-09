
package so.putao.findplug;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class YellowPageItemDianping extends YelloPageItem<DianPingBusiness> {
	
    private static final long serialVersionUID = 1L;

    Object mObject;

    DianPingBusiness mBusiness;

    public YellowPageItemDianping(DianPingBusiness business) {
        mBusiness = business;
    }

    @Override
    public DianPingBusiness getData() {
        // TODO Auto-generated method stub
        return mBusiness;
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return mBusiness.getName() + mBusiness.branch_name;
    }

    @Override
    public double getDistance() {
        if (mBusiness.distance < 0) {
            return 0;
        }
        return mBusiness.distance;
    }

    @Override
    public String getBusinessUrl() {
        // TODO Auto-generated method stub
        return mBusiness.business_url;
    }

    @Override
    public Bitmap getLogoBitmap() {
        if (mBusiness.s_photo_url == null || mBusiness.s_photo_url.equals("")) {
            return null;
        }
        HttpURLConnection conn = null;
        InputStream inputStream = null;
        try {
            URL url = new URL(mBusiness.s_photo_url);
            conn = (HttpURLConnection)url.openConnection();
            conn.setDoInput(true);
            conn.connect();
            inputStream = conn.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            return bitmap;
        } catch (Exception e) {
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
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
        int size = mBusiness.regions.size();
        if (size > 2) {
            size = 2;
        }
        for (int i = 0; i < size; i++) {
            str += mBusiness.regions.get(i);
        }
        return str;
    }

    @Override
    public String getCategorie() {
        return mBusiness.categories.size() > 0 ? mBusiness.categories.get(0) : "";
    }

    @Override
    public float getAvg_rating() {
        // TODO Auto-generated method stub
        return mBusiness.avg_rating;
    }

    @Override
    public String getPhotoUrl() {
        // TODO Auto-generated method stub
        return mBusiness.s_photo_url;
    }

    @Override
    public List<String> getNumbers() {
        List<String> numberList = new ArrayList<String>();
        numberList.add(mBusiness.telephone);
        return numberList;
    }

    @Override
    public String getItemId() {
        return "";
    }

    @Override
    public String getAddress() {
        return mBusiness.address;
    }

	@Override
	public boolean hasCoupon() {
		return mBusiness.has_coupon == 1;
	}

	@Override
	public boolean hasDeal() {
		return mBusiness.has_deal == 1;
	}

	@Override
	public int getAvgPrice() {
		return mBusiness.avg_price;
	}

	@Override
	public boolean isSeleted() {
		return mBusiness.isSelected();
	}

}
