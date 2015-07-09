package so.putao.findplug;

import java.io.Serializable;
import java.util.List;

import android.graphics.Bitmap;

public abstract class YelloPageItem<T> implements Serializable{

    private static final long serialVersionUID = 1L;
    public abstract SourceItemObject getData(); //YellowPageAdapter
    public abstract String getName();           //商户名称
    public abstract List<String> getNumbers();  //商户号码List
    public abstract double getDistance();       //距离
    public abstract String getBusinessUrl();    //商户主页
    public abstract Bitmap getLogoBitmap();     //商户logo
    public abstract String getRegion();         //区域
    public abstract String getCategorie();      //类别
    public abstract float getAvg_rating();      //评分、星级
    public abstract String getPhotoUrl();       //图片url
    public abstract void setTag(Object object); //
    public abstract Object getTag();
    public abstract String getItemId();
    public abstract String getAddress();        //商户地址
    public abstract boolean hasCoupon();        //标识是否有优惠信息
    public abstract boolean hasDeal();          //标识是否有团购
    public abstract int getAvgPrice();          //人均价格
    public abstract boolean isSeleted();
    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "[name:" + getName() + ",number:" + getNumbers() + ",distance:" + getDistance() +  ",detailUrl:" + getBusinessUrl()+"]";
    }
}
