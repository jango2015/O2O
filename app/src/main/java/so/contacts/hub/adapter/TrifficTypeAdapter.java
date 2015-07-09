package so.contacts.hub.adapter;

import java.util.List;

import so.contacts.hub.http.bean.TrafficProductInfo;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yulong.android.contacts.discover.R;

public class TrifficTypeAdapter extends BaseAdapter{
    private List<TrafficProductInfo> mTrafficProductList = null;
    
    private Context mContext = null;
    
    public TrifficTypeAdapter(Context context ,List<TrafficProductInfo> prodList) {
        super();
        this.mTrafficProductList = prodList;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        if(mTrafficProductList == null){
            return 0;
        }
        return mTrafficProductList.size();
    }

    @Override
    public Object getItem(int position) {
        if(mTrafficProductList == null || mTrafficProductList.size() <= position){
            return null;
        }
        return mTrafficProductList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if (null == convertView) {
            view = View.inflate(mContext, R.layout.putao_yellow_page_triffic_type_item, null);
        } else {
            view = convertView;
        }
        TextView typename = (TextView)view.findViewById(R.id.type_name);
        if(mTrafficProductList != null && mTrafficProductList.size() > position){
            typename.setText(mTrafficProductList.get(position).getTraffic_value());
        }
        
        return view;
    }

    public void setmTrafficProductList(List<TrafficProductInfo> mTrafficProductList) {
        this.mTrafficProductList = mTrafficProductList;
    }
}
