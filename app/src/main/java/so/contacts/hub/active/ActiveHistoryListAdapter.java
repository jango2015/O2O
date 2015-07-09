package so.contacts.hub.active;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import so.contacts.hub.active.bean.ActiveHistoryBean;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.loader.DataLoader;
import com.loader.image.ImageLoaderFactory;
import com.yulong.android.contacts.discover.R;

public class ActiveHistoryListAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<ActiveHistoryBean> mActivitiesList;
	private SimpleDateFormat mDateformat = null;
	private DataLoader dataLoader = null;
	

	public ActiveHistoryListAdapter(Context c,
			ArrayList<ActiveHistoryBean> dataList) {
		this.mContext = c;
		this.mActivitiesList = dataList;
		mDateformat = new SimpleDateFormat("MM月dd日");
		dataLoader = new ImageLoaderFactory(c).getActiveHistoryLoader();
	}

	public void setData(ArrayList<ActiveHistoryBean> dataList) {
		this.mActivitiesList = dataList;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (null != mActivitiesList) {
			return mActivitiesList.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (null != mActivitiesList) {
			return mActivitiesList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (null == convertView) {
			holder = new ViewHolder();
			convertView = View.inflate(mContext,
					R.layout.putao_activities_history_list_item, null);
			
			holder.date = (TextView) convertView
					.findViewById(R.id.item_date);
			holder.name = (TextView) convertView
					.findViewById(R.id.item_name);
			holder.status = (TextView) convertView
					.findViewById(R.id.item_status);
			holder.pic = (ImageView) convertView
					.findViewById(R.id.item_pic);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		ActiveHistoryBean activities = mActivitiesList.get(position);

		holder.date.setText(parseDate(activities.update_time));
		
		holder.name.setText(activities.name);
		
		holder.status.setText(activities.description);
		
		dataLoader.loadData(activities.icon_url, holder.pic);

		return convertView;
	}

	class ViewHolder {
		TextView date;
		TextView status;
		TextView name;
		ImageView pic;
	}
	
	private String parseDate(String time){

	    String date = time;
        Calendar c = Calendar.getInstance();
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            c.setTime(formater.parse(time));
            formater = new SimpleDateFormat("MM月dd日");
            date = formater.format(new Date(c.getTimeInMillis()));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return date;

	}

}
