package so.contacts.hub.thirdparty.cinema.adapter;

import java.util.List;

import so.contacts.hub.adapter.CustomListViewAdapter;
import so.contacts.hub.thirdparty.cinema.bean.CinemaMovieDetail;
import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.loader.DataLoader;
import com.yulong.android.contacts.discover.R;

public class MovieListAdapter extends CustomListViewAdapter {
    
    private List<CinemaMovieDetail> cinemaList;
    private Context mContext;
    private DataLoader mLoader ;
    
    public MovieListAdapter(Context context,List<CinemaMovieDetail> cinemaList,DataLoader mLoader){
        this.setCinemaList(cinemaList);
        this.mContext = context;
        /*
         * modified by hyl 2014-12-31 start
         * old code:
         * mLoader = new ImageLoaderFactory(context).getMovieListLoader();
         */
        this.mLoader = mLoader;
        //modified by hyl 2014-12-31 end
    }
    



    public void setData(List<CinemaMovieDetail> mMovieList) {
        this.setCinemaList(cinemaList);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if(getCinemaList() == null){
            return 0;
        }
        return getCinemaList().size();
    }

    @Override
    public Object getItem(int position) {
        if(getCinemaList() == null || getCinemaList().size() <= position){
           return null;
        }
        return getCinemaList().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    
    class ViewHolder{
        TextView movie_name;
        TextView movie_star;
        TextView movie_property;
        TextView movie_description;
        TextView movie_director;
        TextView movie_actor;
        ImageView movie_poster;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        CinemaMovieDetail movie = getCinemaList().get(position);
        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.putao_movie_list_item, null);
            holder = new ViewHolder();
            holder.movie_name = (TextView)convertView.findViewById(R.id.movie_name);
            holder.movie_star = (TextView)convertView.findViewById(R.id.movie_star);
            holder.movie_property = (TextView)convertView.findViewById(R.id.movie_property);
            holder.movie_description = (TextView)convertView.findViewById(R.id.movie_description);
            holder.movie_director = (TextView)convertView.findViewById(R.id.movie_director);
            holder.movie_actor = (TextView)convertView.findViewById(R.id.movie_actor);
            holder.movie_poster = (ImageView)convertView.findViewById(R.id.movie_poster);
            
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }
        
        holder.movie_name.setText(movie.getMoviename());
        String startStr = mContext.getString(R.string.putao_movie_dtl_rating,
                movie.getGeneralmark());
        SpannableString spanText = new SpannableString(startStr);
        spanText.setSpan(new AbsoluteSizeSpan(22, true), 0,1,Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        holder.movie_star.setText(spanText);
        if (!TextUtils.isEmpty(movie.getGcedition())) {
            holder.movie_property.setText(movie.getGcedition());
            holder.movie_property.setBackgroundResource(R.drawable.putao_bg_state_blue);
            holder.movie_property.setVisibility(View.VISIBLE);
        }else{
        	holder.movie_property.setVisibility(View.INVISIBLE);
        }
        
        holder.movie_description.setText(movie.getHighlight());
        holder.movie_director.setText(mContext.getString(R.string.putao_movie_director,
                movie.getDirector()));
        holder.movie_actor
                .setText(mContext.getString(R.string.putao_movie_actor, movie.getActors()));
        
        /*
         * modified by hyl 2014-12-31 start
         * old code:
         *  mLoader.loadData(movie.getLogo(), holder.movie_poster, new DataLoaderListener() {
            @Override
            public void fillDataInView(Object result, View view) {
                if (result == null || view == null)
                    return;
                
                if (result instanceof Bitmap) {
                    Bitmap sourceBitmap = (Bitmap)result;
                    try {
                        
                        Bitmap bitmapmask = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.putao_bg_pic_dianying);
                        Bitmap circleBitamp = ContactsHubUtils.makeRoundCornerforCoolPad(sourceBitmap, bitmapmask);
                        ((ImageView)view).setImageBitmap(sourceBitmap);
                        if (circleBitamp != null) {
                            ((ImageView)view).setImageBitmap(circleBitamp);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
         */
        mLoader.loadData(movie.getLogo(), holder.movie_poster);
        //modified by hyl 2014-12-31 end
        
//        int photoResouceId = getResources().getIdentifier(photoName, "drawable",
//                getPackageName());
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), photoResouceId);
//        if (bitmap != null) {
//            Bitmap circlelogo = ContactsHubUtils.makeRoundCornerforCoolPad(bitmap, bitmapmask);
//            logoImg.setImageBitmap(circlelogo);
//        }
        return convertView;
    }
    
    public List<CinemaMovieDetail> getCinemaList() {
        return cinemaList;
    }

    public void setCinemaList(List<CinemaMovieDetail> cinemaList) {
        this.cinemaList = cinemaList;
    }




    @Override
    public DataLoader getmImageLoader() {
        // TODO Auto-generated method stub
        return null;
    }



}
