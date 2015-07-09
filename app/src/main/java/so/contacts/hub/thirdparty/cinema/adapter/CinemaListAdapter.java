
package so.contacts.hub.thirdparty.cinema.adapter;

import java.text.DecimalFormat;
import java.util.List;

import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.yulong.android.contacts.discover.R;

import so.contacts.hub.thirdparty.cinema.bean.CinemaDetail;
import so.contacts.hub.util.DistanceUtil;
import so.putao.findplug.LBSServiceGaode;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CinemaListAdapter extends BaseAdapter {

    private List<CinemaDetail> cinemaList;

    private Context context;

    public CinemaListAdapter(Context context, List<CinemaDetail> cinemaList) {
        this.setCinemaList(cinemaList);
        this.context = context;
    }

    @Override
    public int getCount() {
        if (getCinemaList() == null) {
            return 0;
        }
        return getCinemaList().size();
    }

    @Override
    public Object getItem(int position) {
        if (getCinemaList() == null || getCinemaList().size() <= position) {
            return null;
        }
        return getCinemaList().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        CinemaDetail cinema = getCinemaList().get(position);
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.putao_cinema_item, null);
            holder = new ViewHolder();
            holder.cinemaMark = (TextView)convertView.findViewById(R.id.cinema_mark);
            holder.cinemaDistance = (TextView)convertView.findViewById(R.id.cinema_distance);
            holder.cinemaName = (TextView)convertView.findViewById(R.id.cinema_name);
            holder.cinemaAddress = (TextView)convertView.findViewById(R.id.cinema_address);
            holder.cinemaFeature = (TextView)convertView.findViewById(R.id.cinema_feature);
            holder.cinemaPopcorn = (TextView)convertView.findViewById(R.id.cinema_popcorn);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
//        holder.cinemaMark.setText(cinema.getGeneralmark());
        /*
         * 
         * modified by hyl 2015-1-6 start
         */
        holder.cinemaMark.setText(context.getString(R.string.putao_movie_dtl_rating, cinema.getGeneralmark()));
        //modified by hyl 2015-1-6 end
        
        holder.cinemaName.setText(cinema.getCinemaname());
        holder.cinemaAddress.setText(cinema.getAddress());
        if(!TextUtils.isEmpty(cinema.getFeature())){
            holder.cinemaFeature.setText(cinema.getFeature());
        }else {
        	holder.cinemaFeature.setVisibility(View.GONE);
        }
        //隐藏影院特色
        holder.cinemaFeature.setVisibility(View.GONE);
        
        if(!TextUtils.isEmpty(cinema.getPopcorn())){
	        if(Integer.parseInt(cinema.getPopcorn())==1){
	            holder.cinemaPopcorn.setText(R.string.putao_cinemalist_combo);
	        }else if(Integer.parseInt(cinema.getPopcorn())==0){
	        	holder.cinemaPopcorn.setVisibility(View.GONE);
	        }
        }else{
        	holder.cinemaPopcorn.setVisibility(View.GONE);
        }
        double distance = cinema.getDistance();
        DecimalFormat df = new DecimalFormat("#");
	    String distanceStr = df.format(distance);
	    if (distance < 1000) {
           holder.cinemaDistance.setText(context.getString(
                  R.string.putao_yellow_page_distance_meter, Integer.parseInt(distanceStr)));
        } else {
           String result = String.format("%.2f", distance / 1000.0d);
           holder.cinemaDistance.setText(context.getString(
                   R.string.putao_yellow_page_distance_kilometer, result));
        }
        return convertView;
    }

    public List<CinemaDetail> getCinemaList() {
        return cinemaList;
    }

    public void setCinemaList(List<CinemaDetail> cinemaList) {
        this.cinemaList = cinemaList;
    }

    class ViewHolder {
        TextView cinemaMark;

        TextView cinemaDistance;

        TextView cinemaName;

        TextView cinemaAddress;

        TextView cinemaFeature;

        TextView cinemaPopcorn;
    }

}
