package so.putao.findplug;

import java.util.List;

import so.contacts.hub.ui.yellowpage.bean.PuTaoResultItem;
import android.graphics.Bitmap;

public class YellowPageItemPutao extends YelloPageItem<PuTaoResultItem> {

    private static final long serialVersionUID = 1L;
    
    PuTaoResultItem puTaoResultItem;
    
    public YellowPageItemPutao(PuTaoResultItem puTaoResultItem) {
        this.puTaoResultItem = puTaoResultItem;
    }
    
    @Override
    public PuTaoResultItem getData() {
        return puTaoResultItem;
    }

    @Override
    public String getName() {
        return puTaoResultItem.getName();
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
        // TODO Auto-generated method stub
        return puTaoResultItem.getPhotoUrl();
    }

    @Override
    public void setTag(Object object) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Object getTag() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getItemId() {
        // TODO Auto-generated method stub
        return String.valueOf(puTaoResultItem.getItemId());
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
		return -1;
	}

	@Override
	public boolean isSeleted() {
		return puTaoResultItem.isSelected();
	}

}
