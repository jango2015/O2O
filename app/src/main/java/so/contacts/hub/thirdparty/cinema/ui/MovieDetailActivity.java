
package so.contacts.hub.thirdparty.cinema.ui;

import so.contacts.hub.core.Config;
import so.contacts.hub.remind.BaseRemindActivity;
import so.contacts.hub.thirdparty.cinema.CinemaConstants;
import so.contacts.hub.thirdparty.cinema.bean.CinemaMovieDetail;
import so.contacts.hub.thirdparty.cinema.tool.CinemaApiUtil;
import so.contacts.hub.thirdparty.cinema.utils.GewaApiReqMethod;
import so.contacts.hub.util.CalendarUtil;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.NetUtil;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.loader.DataLoader;
import com.loader.image.ImageLoaderFactory;
import com.yulong.android.contacts.discover.R;

public class MovieDetailActivity extends BaseRemindActivity implements OnClickListener {
    public static final int TYPE_OPEN = 1;

    public static final int TYPE_COMING = 2;

    private int type = TYPE_OPEN;

    private long movieId;

    private String cityCode;

    private String movieName;

    private CinemaMovieDetail movieDetail;

    private DataLoader imageLoader;

    private TextView title, subTitle;

    private ImageView movieLogo;

    private RatingBar movieStar;

    private TextView movieMark;

    private TextView movieLengthStateLanguage;

    private TextView movieReleaseDate;

    private TextView movieHighlight;

    private TextView movieSelectSeat;

    private TextView movieDirector;

    private TextView movieActors;

    private TextView movieContent;

    private LinearLayout typeAndGceditionLayout;
    
    private ScrollView movie_detail_scrollview;//add by hyl 2015-1-5
//    private ProgressBar loading_view;//add by hyl 2015-1-5
    private View exception_layout;//add by hyl 2015-1-8
    
    private String mCityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.putao_movie_detail_layout);

        initViews();

        if (getIntent() != null) {
        	/*
        	 * 在title栏显示电影名称
        	 * add by hyl 2015-1-5 start
        	 */
        	String movie_name = getIntent().getStringExtra("movie_name");//电影名称
        	String movie_english_name = getIntent().getStringExtra("movie_english_name");//电影英文名称
        	mCityName = getIntent().getStringExtra(CinemaConstants.CINEMA_CITY);
        	
        	title.setText(movie_name);
        	subTitle.setText(movie_english_name);
        	
        	//open_buy： true-表示可以选座购票 false-表示等待排片，暂不支持购票
        	boolean open_buy = getIntent().getBooleanExtra("open_buy",false);
        	if(!open_buy){
        		movieSelectSeat.setClickable(false);
        		movieSelectSeat.getBackground().setAlpha(80);
        		movieSelectSeat.setText(R.string.putao_movie_wait_for_plan);
        	}
        	//add by hyl 2015-1-5 end
        	
        	
            movieId = getIntent().getLongExtra("movieid", 0);
            cityCode = getIntent().getStringExtra("citycode");
            type = getIntent().getIntExtra("type", TYPE_OPEN);
        }
        LogUtil.d("MovieDetailActivity", "movieId:" + movieId);
        if (movieId != 0) {
            loadData();
        }
        imageLoader = new ImageLoaderFactory(this).getMovieListLoader();
    }

    private void loadData() {
        showLoadingDialog(false);
    	
        String movieDetailUrl = CinemaApiUtil.getMovieDetailUrl(movieId, "",
                CinemaConstants.PIC_SIZE_5[0], CinemaConstants.PIC_SIZE_5[1]);
        Config.asynGetGewara(movieDetailUrl, GewaApiReqMethod.MOVIE_DETAIL, mHandler);
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            LogUtil.d("MovieDetailActivity", "msg.what = " + msg.what);
            dismissLoadingDialog();
            switch (msg.what) {
                case 0:
                	if (msg.obj != null) {
                        movieDetail = (CinemaMovieDetail)msg.obj;
                        LogUtil.d("MovieDetailActivity", movieDetail.toString());
                        setMovieDetailData(movieDetail);
                    }
                    break;
                case 1:
                	exception_layout.setVisibility(View.VISIBLE);//add by hyl 2015-1-8
                	movie_detail_scrollview.setVisibility(View.GONE);//add by hyl 2015-1-8
                    Toast.makeText(MovieDetailActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT)
                            .show();
                    break;
                case 2:
                	exception_layout.setVisibility(View.VISIBLE);//add by hyl 2015-1-8
                	movie_detail_scrollview.setVisibility(View.GONE);//add by hyl 2015-1-8
                    Toast.makeText(MovieDetailActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT)
                            .show();
                    break;
                default:
                    break;
            }
        }

    };

    private void initViews() {
        title = (TextView)findViewById(R.id.title);
        subTitle = (TextView)findViewById(R.id.subtitle);
        
        //add by hyl 2015-1-5 start
        movie_detail_scrollview = (ScrollView)findViewById(R.id.movie_detail_scrollview);
//        loading_view = (ProgressBar)findViewById(R.id.loading_view);
        //add by hyl 2015-1-5 end
        
        movieLogo = (ImageView)findViewById(R.id.movie_logo);
        movieStar = (RatingBar)findViewById(R.id.movie_star);
        movieMark = (TextView)findViewById(R.id.movie_general_mark);
        typeAndGceditionLayout = (LinearLayout)findViewById(R.id.movie_type_gcedition_layout);
        movieLengthStateLanguage = (TextView)findViewById(R.id.movie_length_state_language);
        movieReleaseDate = (TextView)findViewById(R.id.movie_release_date);
        movieHighlight = (TextView)findViewById(R.id.movie_highlight);
        movieSelectSeat = (TextView)findViewById(R.id.movie_select_seat);
        movieDirector = (TextView)findViewById(R.id.movie_director);
        movieActors = (TextView)findViewById(R.id.movie_actors);
        movieContent = (TextView)findViewById(R.id.movie_content);

        findViewById(R.id.back_layout).setOnClickListener(this);
        movieSelectSeat.setOnClickListener(this);
        
        //add by hyl 2015-1-8 start 增加异常界面显示
        exception_layout = findViewById(R.id.network_exception_layout);
        exception_layout.setOnClickListener(this);
        //add by hyl 2015-1-8 end
    }

    private void setMovieDetailData(CinemaMovieDetail movieDetail) {
        if (movieDetail != null) {
            movieName = movieDetail.getMoviename();
            title.setText(movieDetail.getMoviename());
            subTitle.setText(movieDetail.getEnglishname());
            subTitle.setVisibility(View.VISIBLE);
            imageLoader.loadData(movieDetail.getLogo(), movieLogo);
            // 设置影片类型
            String generalmark = movieDetail.getGeneralmark();
            if (!TextUtils.isEmpty(generalmark)) {
                try {
                    float mark = Float.parseFloat(generalmark);
                    movieStar.setRating(mark / 2.0f);
                } catch (Exception e) {
                }
                generalmark = getString(R.string.putao_movie_dtl_rating, generalmark);
            }
            movieMark.setText(generalmark);
            // 设置影片类型
            String type = movieDetail.getType();
            if (!TextUtils.isEmpty(type)) {
                String typeArr[] = type.split("/");
                if (typeArr.length < 2) {
                    typeArr = type.split(",");
                }
                for (int i = 0; i < typeArr.length && i < 2; i++) {// 因为显示空间不够，目前只显示两个影片类型
                    fillTypeAndGcedition(typeArr[i], R.drawable.putao_bg_state_orange);
                }
            }
            // 设置影片版本
            String gcedition = movieDetail.getGcedition();// 影片版本
            if (!TextUtils.isEmpty(gcedition)) {
                fillTypeAndGcedition(gcedition, R.drawable.putao_bg_state_blue);
            }
            movieLengthStateLanguage.setText(movieDetail.getLength() + "/" + movieDetail.getState()
                    + "/" + movieDetail.getLanguage());
            if (movieDetail.getReleasedate() != null) {
                movieReleaseDate.setText(getString(R.string.putao_movie_dtl_release, CalendarUtil
                        .getDateStrFromDate(movieDetail.getReleasedate(),
                                CalendarUtil.DATE_PATTERN_CN)));
            }
            if(TextUtils.isEmpty(movieDetail.getHighlight())) {
                movieHighlight.setText(movieDetail.getHighlight());
            } else {
                movieHighlight.setText("\""+movieDetail.getHighlight()+"\"");
            }
            movieDirector.setText(movieDetail.getDirector());
            movieActors.setText(movieDetail.getActors());

            String content = movieDetail.getContent();
            if (!TextUtils.isEmpty(content)) {// 处理首字符是换行\n制表\t空格符情况
                content = content.trim();
                while (content.length() > 1 && content.startsWith("\n") || content.startsWith("\t")) {
                    content = content.substring(1);
                }
            }
            movieContent.setText(content);
            
            movie_detail_scrollview.setVisibility(View.VISIBLE);//add by hyl 2015-1-5 填充完数据后 显示电影票详情view
            exception_layout.setVisibility(View.GONE);//add by hyl 2015-1-8
        }
    }

    /**
     * 填充影片类型和版本
     * 
     * @param txt
     * @param resId
     */
    private void fillTypeAndGcedition(String txt, int resId) {
        TextView child = (TextView)View.inflate(this, R.layout.putao_movie_detail_txt, null);
        child.setText(txt);
        child.setBackgroundResource(resId);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.rightMargin = so.contacts.hub.util.Utils.dip2px(this, 8);
        typeAndGceditionLayout.addView(child, params);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.back_layout:
                finish();
                break;
            case R.id.movie_select_seat:
                intent = new Intent(this, CinemaListActivity.class);
                intent.putExtra("movieid", movieId);
                intent.putExtra("movieName", movieName);
                intent.putExtra("citycode", cityCode);
                intent.putExtra("type", type);
                intent.putExtra(CinemaConstants.CINEMA_CITY, mCityName);
                if (movieDetail != null) {
                    intent.putExtra("length", movieDetail.getLength());
                    intent.putExtra(CinemaConstants.MOVIE_PHOTO_URL, movieDetail.getLogo());
                }
                startActivity(intent);
                break;
            case R.id.network_exception_layout:
                if (NetUtil.isNetworkAvailable(this)) {
                	exception_layout.setVisibility(View.GONE);
                    loadData();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected boolean needReset() {
        return true;
    }

}
