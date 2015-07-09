
package so.contacts.hub.thirdparty.cinema.adapter;

import java.util.Calendar;
import java.util.List;

import so.contacts.hub.thirdparty.cinema.bean.OpenPlayItem;
import so.contacts.hub.util.CalendarUtil;
import so.contacts.hub.util.LogUtil;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yulong.android.contacts.discover.R;

/**
 * 电影场次列表适配器
 * 
 * @author lixiaohui
 */
public class OpiListAdapter extends BaseAdapter {

    private Context mContext;

    private List<OpenPlayItem> mOpiList;

    private int mMovieLength;

    public OpiListAdapter(Context context, List<OpenPlayItem> opiList, String movieLength) {
        mContext = context;
        mOpiList = opiList;
        try {
            // “120分钟”转换成120
            mMovieLength = Integer.parseInt(movieLength.substring(0, movieLength.length() - 2));
        } catch (Exception e) {
        }
    }

    @Override
    public int getCount() {
        if (mOpiList == null) {
            return 0;
        }
        LogUtil.d("OpenPlayListActivity", "adpter count :" + mOpiList.size());
        return mOpiList.size();
    }

    @Override
    public Object getItem(int position) {
        if (mOpiList == null || mOpiList.size() <= position) {
            return null;
        }
        return mOpiList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        OpenPlayItem item = mOpiList.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.putao_open_play_list_item, null);
            holder.startTime = (TextView)convertView.findViewById(R.id.start_time);
            holder.endTime = (TextView)convertView.findViewById(R.id.end_time);
            holder.language = (TextView)convertView.findViewById(R.id.language);
            holder.edition = (TextView)convertView.findViewById(R.id.edition);
            holder.gewaPrice = (TextView)convertView.findViewById(R.id.gewa_price);
            holder.originalPrice = (TextView)convertView.findViewById(R.id.original_price);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.startTime.setText(CalendarUtil.getDateStrFromDate(item.getPlaytime(),
                DATE_FORMAT_STR));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(item.getPlaytime());
        calendar.add(Calendar.MINUTE, mMovieLength);
        String argEnd = CalendarUtil.getDateStrFromDate(calendar.getTime(), DATE_FORMAT_STR);
        String endtime = mContext.getString(R.string.putao_movie_playlist_item_end, argEnd);
        holder.endTime.setText(endtime);
        holder.language.setText(item.getLanguage()+"/"+item.getEdition());
        holder.edition.setText(item.getRoomname());
        holder.gewaPrice.setText("￥" + item.getGewaprice());
        holder.originalPrice.setText("￥" + item.getPrice());
        holder.originalPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        return convertView;
    }

    private String DATE_FORMAT_STR = "HH:mm";

    class ViewHolder {
        TextView startTime;

        TextView endTime;

        TextView language;

        TextView edition;

        TextView gewaPrice;

        TextView originalPrice;
    }

}
