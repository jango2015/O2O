package so.contacts.hub.widget;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import so.contacts.hub.util.UiHelper;
import com.yulong.android.contacts.discover.R;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View.OnClickListener;

/**
 * 显示年、月的选择器
 * @author Michael
 *
 */
public class CustomYearAndMonthDialog extends Dialog implements OnClickListener, OnItemClickListener{

	private TextView mTitleTView;
	private ImageView mLeftArrowImgView = null;
	private ImageView mRightArrowImgView = null;
	private TextView mYearTView = null;
    private GridView mGridView;
	private Context mContext = null;
	
	private int mCurrentYear = -1;
	private int mCurrentMonth = -1;
	
	private int mYear = -1;
	private int mMonth = -1;
	
	private DateAdapter mDateAdapter = null;
	private LayoutInflater mInflater = null;
	private String[] mMonthList = null;
	private int mGrayColor = 0; // 灰色
	
	public CustomYearAndMonthDialog(Context context) {
		super(context);
		this.mContext = context;
		init();
	}

	public CustomYearAndMonthDialog(Context context, int theme) {
		super(context, theme);
		this.mContext = context;
		init();
	}

	private void init() {
		setContentView(R.layout.putao_yearandmonth_dialog);

		final DisplayMetrics displayMetrics = UiHelper.getDisplayMetrics(mContext);
		int screenWidth = displayMetrics.widthPixels;
		int padding = UiHelper.getDialogPadding(mContext);

		// 设置对话框宽度
		//getWindow().setBackgroundDrawable(null);
		getWindow().setBackgroundDrawableResource(R.color.putao_transparent);
		WindowManager.LayoutParams p = getWindow().getAttributes();
		p.width = screenWidth - padding * 2;
		getWindow().setAttributes(p);
		// getWindow().setGravity(Gravity.BOTTOM); //此处可以设置dialog显示的位置
		getWindow().setWindowAnimations(R.style.putao_AnimationDialog); // 添加动画
		
		initView();
		initData();
	}
	
	private void initView(){
		mTitleTView = (TextView) findViewById(R.id.title_tv);
		mLeftArrowImgView = (ImageView) findViewById(R.id.calendar_left);
		mRightArrowImgView = (ImageView) findViewById(R.id.calendar_right);
		mLeftArrowImgView.setOnClickListener(this);
		mRightArrowImgView.setOnClickListener(this);
		mYearTView = (TextView) findViewById(R.id.calendar_year);
		mGridView = (GridView) findViewById(R.id.gridView_calendar);
		mGridView.setOnItemClickListener(this);
	}
	
	private void initData(){
		mInflater = LayoutInflater.from(mContext);
		mGrayColor = mContext.getResources().getColor(R.color.putao_express_result_no_data_info);
		mTitleTView.setText(R.string.putao_hotelpay_select_date);
		mMonthList = mContext.getResources().getStringArray(R.array.putao_hotelpay_calendar);
		
		mDateAdapter = new DateAdapter();
		mGridView.setAdapter(mDateAdapter);
		setYearAndMonth(mCurrentYear, mCurrentMonth);
	}
	
	@Override
	public void dismiss() {
		// TODO Auto-generated method stub
		// Dialog所属的Activity没有结束时，则dismiss
		if ( mContext != null && !((Activity)mContext).isFinishing() ){
			super.dismiss();
        }
	}
	
	public void setYearAndMonth(int year, int month){
		if( year == -1 ){
			Calendar calendar = Calendar.getInstance(Locale.CHINA);
			calendar.setTime(new Date());
			mCurrentYear = calendar.get(Calendar.YEAR);
			mCurrentMonth = calendar.get(Calendar.MONTH);
			year = mCurrentYear;
			month = mCurrentMonth;
		}
		mYear = year;
		mMonth = month;
		mYearTView.setText(mContext.getString(R.string.putao_hotelpay_select_date_year, mYear));
		mGridView.setSelection(mMonth);
		
		updateArrowViewState();
	}
	
	private void updateArrowViewState(){
		if( mYear <= mCurrentYear ){
			mLeftArrowImgView.setVisibility(View.INVISIBLE);
		}else{
			mLeftArrowImgView.setVisibility(View.VISIBLE);
		}
		mDateAdapter.notifyDataSetChanged();
	}
	
	private IGetYearAndMonthCallback mIGetYearAndMonthCallback = null;
	
	public void setIGetYearAndMonthCallback(IGetYearAndMonthCallback iGetYearAndMonthCallback){
		mIGetYearAndMonthCallback = iGetYearAndMonthCallback;
	}
	
	public interface IGetYearAndMonthCallback{
		void getYearAndMonthCallback(int year, int month);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		int viewId = view.getId();
		if( viewId == R.id.calendar_left ){
			mYearTView.setText(mContext.getString(R.string.putao_hotelpay_select_date_year, --mYear));
			updateArrowViewState();
		}else if( viewId == R.id.calendar_right ){
			mYearTView.setText(mContext.getString(R.string.putao_hotelpay_select_date_year, ++mYear));
			updateArrowViewState();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
		// TODO Auto-generated method stub
		mMonth = position + 1;
		if( mIGetYearAndMonthCallback != null ){
			mIGetYearAndMonthCallback.getYearAndMonthCallback(mYear, mMonth);
		}
		dismiss();
	}
	
	private class DateAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mMonthList.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mMonthList[position];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup viewGroup) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.putao_select_year_and_month_item, null);
			}
			TextView tview = (TextView) convertView;
			tview.setText(mMonthList[position]);
			if( mYear == mCurrentYear && position < mCurrentMonth ){
				tview.setTextColor(mGrayColor);
				tview.setClickable(true);
			}else{
				tview.setTextColor(Color.BLACK);
				tview.setClickable(false);
			}
			return tview;
		}
		
	}
	
}
