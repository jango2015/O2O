/**
 * sml
 */
package so.putao.findplug;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class YellowPageItemDianpingCoupon extends YelloPageItem<DianpingCoupon> {
	
    private static final long serialVersionUID = 1L;

    Object mObject;

    DianpingCoupon mBusiness;

    public YellowPageItemDianpingCoupon(DianpingCoupon business) {
        mBusiness = business;
    }

	@Override
	public DianpingCoupon getData() {
		// TODO Auto-generated method stub
		return mBusiness;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getNumbers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getDistance() {
		// TODO Auto-generated method stub
		return mBusiness.distance;
	}

	@Override
	public String getBusinessUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bitmap getLogoBitmap() {
		// TODO Auto-generated method stub
        if (mBusiness.getPhotoUrl() == null || mBusiness.getPhotoUrl().equals("")) {
            return null;
        }
        HttpURLConnection conn = null;
        InputStream inputStream = null;
        try {
            URL url = new URL(mBusiness.getPhotoUrl());
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
	public String getRegion() {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
        return mBusiness.categories.size() > 0 ? mBusiness.categories.get(0) : "";
	}

	@Override
	public float getAvg_rating() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getPhotoUrl() {
		// TODO Auto-generated method stub
		return mBusiness.getPhotoUrl();
	}

	@Override
	public void setTag(Object object) {
		// TODO Auto-generated method stub
        this.mObject = object;
	}

	@Override
	public Object getTag() {
		// TODO Auto-generated method stub
		return this.mObject;
	}

	@Override
	public String getItemId() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getAddress() {
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
		return -1;
	}

	@Override
	public boolean isSeleted() {
		// TODO Auto-generated method stub
		return false;
	} 

}
