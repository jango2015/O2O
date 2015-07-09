package so.contacts.hub.adapter;

import java.util.List;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import so.contacts.hub.core.Config;
import so.contacts.hub.remind.YellowPageLiveTitleDataBean;
import so.contacts.hub.remind.simple.SimpleRemindView;
import so.contacts.hub.ui.yellowpage.bean.CategoryBean;
import so.contacts.hub.util.ContactsHubUtils;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.widget.SlideView;
import so.contacts.hub.yellow.data.RemindBean;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.StateListDrawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.loader.DataLoader;
import com.loader.image.ImageLoaderFactory;
import com.yulong.android.contacts.discover.R;

@SuppressLint("UseSparseArrays")
public class MyGridViewAdapter extends BaseAdapter {
	private static final String TAG = "MyGridViewAdapter";

	private LayoutInflater inflater = null;

	private Context context = null;

	private List<CategoryBean> categroyList = null;

	private Map<Integer, RemindBean> remindMaps = null;
	
	private DataLoader mDataLoader = null;
	
	/*
     * 去除livetitle功能 注释改代码
     * modified by hyl 2014-12-23 start
     */
//	private Map<Integer,YellowPageLiveTitleDataBean> liveTitleMaps =null;
	//modified by hyl 2014-12-23 end
	
	public MyGridViewAdapter(Context context,List<CategoryBean> categroyList,
			Map<Integer, YellowPageLiveTitleDataBean> liveTitleMaps, Map<Integer, RemindBean> remindMaps) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		mDataLoader = new ImageLoaderFactory(context).getNormalLoader(false, true);
		
		this.categroyList = categroyList;
		
		/*
	     * 去除livetitle功能 注释改代码
	     * modified by hyl 2014-12-23 start
	     */
//		this.liveTitleMaps = liveTitleMaps;
		//modified by hyl 2014-12-23 end
		
		this.remindMaps = remindMaps;
	}

	public void setData(List<CategoryBean> categroyList,
			Map<Integer, YellowPageLiveTitleDataBean> liveTitleMaps, Map<Integer, RemindBean> remindMaps) {
		this.categroyList = categroyList;
		/*
	     * 去除livetitle功能 注释改代码
	     * modified by hyl 2014-12-23 start
	     */
//		this.liveTitleMaps = liveTitleMaps;
		//modified by hyl 2014-12-23 end
		this.remindMaps = remindMaps;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (categroyList != null)
			return categroyList.size();
		return 0;
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		long startTime = System.currentTimeMillis();
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			
			/*
	         * 去除livetitle功能 注释改代码
	         * modified by hyl 2014-12-23 start
	         */
			/*
			//add by lisheng start
			convertView = inflater.inflate(R.layout.putao_my_yellow_page_home_item, null);
			holder.slideView = (SlideView) convertView.findViewById(R.id.yellow_page_img_item_slide);
			holder.slideView.setForeView(inflater.inflate(R.layout.putao_my_yellow_page_slideview_fore, null));
			//add by lisheng  end
			*/
			//modified by hyl 2014-12-23 start
			
			convertView = inflater.inflate(R.layout.putao_my_yellow_page_home_item, null);
			holder.categoryName = (TextView) convertView.findViewById(R.id.yellow_page_item);
			holder.categoryIcon = (ImageView) convertView.findViewById(R.id.yellow_page_img_item);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if( holder.remindView != null ){
			holder.remindView.setVisibility(View.GONE);
		}
		if( holder.remindPointView != null ){
			holder.remindPointView.setVisibility(View.GONE);
		}
		if( holder.remindPointDigitView != null ){	
			holder.remindPointDigitView.setVisibility(View.GONE);
		}

		CategoryBean bean = categroyList.get(position);
		if (bean != null) {
			String name = ContactsHubUtils.getShowName(context, bean.getShow_name());
	        holder.categoryName.setText(name);
	        if (remindMaps != null) {
	            RemindBean remind = remindMaps.get(bean.getRemind_code());
	            
	            if( remind != null && remind.getRemindCode() > 0 ) {
	            	if(convertView.findViewById(R.id.putao_remind_layout) == null){
	            		ViewStub viewStub = (ViewStub)convertView.findViewById(R.id.yellow_page_item_remind_layout);
	            		viewStub.inflate();
	            	}

	    			holder.remindView = (SimpleRemindView) convertView.findViewById(R.id.remind_view);
	    			holder.remindPointView = (SimpleRemindView) convertView.findViewById(R.id.remind_point_view);
	    			holder.remindPointDigitView = (SimpleRemindView) convertView.findViewById(R.id.remind_point_digit_view);
	            	
	            	showRemindByInfo(remind, holder.remindView, holder.remindPointView, holder.remindPointDigitView);
                }
	        }
	        
            String icon = bean.getIcon();
            String pressIcon = bean.getPressIcon();
            //modity by ljq 2014-10-15 start
            //如果类型不是网络地址 则取本地图片
            if(!ContactsHubUtils.isURlStr(icon)&&!ContactsHubUtils.isURlStr(pressIcon)){
                int normol = getResourceIdByName(icon);
                int pressed = getResourceIdByName(pressIcon);
                StateListDrawable stateListDrawable = ContactsHubUtils.addStateDrawable(context, normol, pressed,bean);
                holder.categoryIcon.setImageDrawable(stateListDrawable);
            }else if(ContactsHubUtils.isURlStr(icon)&&ContactsHubUtils.isURlStr(pressIcon)){
                Bitmap iconBitmap = null;
                Bitmap iconPassBitmap = null;
                if(mDataLoader != null){
                    //类型是网络地址 则去取缓存  没有会去下载
                    iconPassBitmap = mDataLoader.loadDataReturnBitmap(pressIcon, holder.categoryIcon);
                    iconBitmap = mDataLoader.loadDataReturnBitmap(icon, holder.categoryIcon);
                    if(iconBitmap != null && iconPassBitmap != null){
                        StateListDrawable stateListDrawable = ContactsHubUtils.addStateDrawable(context, iconBitmap, iconPassBitmap);
                        holder.categoryIcon.setImageDrawable(stateListDrawable);
                    }else{
                        //贴上默认图片
                        int normol = getResourceIdByName(Config.DEFAULT_CATEGORY_IMAGE);
                        int pressed = getResourceIdByName(Config.DEFAULT_CATEGORY_IMAGE_DEEP);
                        StateListDrawable stateListDrawable = ContactsHubUtils.addStateDrawable(context, normol, pressed,bean);
                        holder.categoryIcon.setImageDrawable(stateListDrawable);
                    }
                }
            }
            //modity by ljq 2014-10-15 end
            
            /*
	         * 去除livetitle功能 注释改代码
	         * modified by hyl 2014-12-23 start
	         */
            //modify by lisheng  2014-11-08 11:58:12 start 删除冗余遍历操作;
//            if(liveTitleMaps != null && !liveTitleMaps.isEmpty()){
//            	final YellowPageLiveTitleDataBean data = liveTitleMaps.get((int)bean.getCategory_id());
//            	if (data != null) {
//            		if (holder.slideView.getBackView() == null) {
//            			final TextView textView = (TextView) View.inflate(context,
//            					R.layout.putao_live_title_item, null);
//            			textView.setTextColor(data.getColor());
//            			textView.setGravity(Gravity.CENTER);
//            			String imgUrl = data.getImgUrl();
//            			LogUtil.i(TAG, "LiveTitle imgUrl: " + imgUrl);
//            			if (!TextUtils.isEmpty(imgUrl)) {
//            				// 获取网络图片
//            				mDataLoader.loadData(data.getImgUrl(), textView);
//            			} else {
//            				textView.setBackgroundResource(R.drawable.putao_home_livetitle_bg);
//            			}
//            			if (data.getTextSize() != 0) {
//            				textView.setTextSize(data.getTextSize());
//            			}
//            			if (data.getColor() != 0) {
//            				textView.setTextColor(data.getColor());
//            			}
//            			
//            			if(!TextUtils.isEmpty(data.getBubbleText())){
//            				Spannable sp = new SpannableString(data.getBubbleText());
//                			if (data.getKeyWordColor() != 0) {
//                				sp.setSpan(
//                						new ForegroundColorSpan(data
//                								.getKeyWordColor()), data
//                								.getKeyWordStart(), data
//                								.getKeyWordEnd(),
//                								Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                			}
//                			if (data.getKeyWordSize() != 0) {
//                				sp.setSpan(
//                						new RelativeSizeSpan(data.getKeyWordSize()),
//                						data.getKeyWordStart(),
//                						data.getKeyWordEnd(),
//                						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                			}
//                			
//                			textView.setText(sp);
//            			}
//            			// 变色
//            			holder.slideView.setLiveTitleNum(liveTitleMaps.size());
//            			holder.slideView.setDuration(300);
//            			holder.slideView.getQuickSettings().setInterpolator(0,
//            					1.3f);
//            			holder.slideView.setBackView(textView, data.getDelay());
//            		}
//            	}
//            }
//            //modify by lisheng  end 
            //modified by hyl 2014-12-23 end
        }
		Log.i(TAG,"gridview time:"+(System.currentTimeMillis() - startTime));
        return convertView;
    }
    
    /**
     * 显示打点信息
     */
    private void showRemindByInfo(RemindBean bean, SimpleRemindView remindViewOther, SimpleRemindView remindViewPoint, SimpleRemindView remindViewDigit){
    	int pointState = getRemindBeanPointState(bean);
    	if( pointState == REMIND_POINT ){
    		remindViewOther.setVisibility(View.GONE);
    		remindViewDigit.setVisibility(View.GONE);
    		remindViewPoint.setRemind(bean);
    	}else if( pointState == REMIND_POINT_DIGIT){
    		remindViewOther.setVisibility(View.GONE);
    		remindViewPoint.setVisibility(View.GONE);
    		remindViewDigit.setRemind(bean);
    	}else{
    		remindViewDigit.setVisibility(View.GONE);
    		remindViewPoint.setVisibility(View.GONE);
    		remindViewOther.setDataLoader(mDataLoader);
    		remindViewOther.setRemind(bean);
    	}
    }
    
    /**
     * 获取打点的类型（按照布局划分）
     * @param bean
     * @return
     */
    private int getRemindBeanPointState(RemindBean bean){
    	if( bean == null ){
    		return REMIND_OTHER;
    	}
    	String text = bean.getText();
        int count = bean.getRemindCount();
        int style = bean.getStyle();
        String imgUrl = bean.getImgUrl();
        
        if( TextUtils.isEmpty(imgUrl) ){
        	// 无图片
        	if( TextUtils.isEmpty(text) ){
        		// 无文字
        		if(style > 0) {
        	    	// 根据样式显示
        			return REMIND_POINT_DIGIT;
        	    } else if( count == 0 ){
        	    	// 显示小点
        	    	return REMIND_POINT;
        		} else {
        			// 显示大点 后者 根据样式显示
        			return REMIND_POINT_DIGIT;
        		}
        	}else{
        		// 有文字
        		return REMIND_OTHER;
        	}
        }else{
        	// 有图片
        	return REMIND_OTHER;
        }
    }
	
    private int getResourceIdByName(String iconName){
        int iconResourceId = 0;
        if(!TextUtils.isEmpty(iconName)){
            iconResourceId = context.getResources().getIdentifier(iconName, "drawable", context.getPackageName());//2130837744
        }
        if(iconResourceId <= 0){
            iconResourceId = R.drawable.putao_icon_quick_replace;
        }
        return iconResourceId;
    }
    
    private class ViewHolder {
        public TextView categoryName;
        public ImageView categoryIcon;
        public TextView tagTextView;
        public ImageView tagImageView;
        public SimpleRemindView remindView;       // 显示文本
        public SimpleRemindView remindPointDigitView; // 显示数字
        public SimpleRemindView remindPointView;  // 显示打点
        /*
         * 去除livetitle功能 注释改代码
         * modified by hyl 2014-12-23 start
         */
//        public SlideView slideView; //add by lisheng  2014-11-07 10:47:09 
        //modified by hyl 2014-12-23 end
    }
    
    private static final int REMIND_POINT = 0;        // 显示打点
    private static final int REMIND_POINT_DIGIT = 1;  // 显示数字
    private static final int REMIND_OTHER = 2;        // 显示文本
    
    
    /*
     * 去除livetitle功能 注释改代码
     * modified by hyl 2014-12-23 start
     */
//    // add by lisheng start 2014-11-08 
//    private YellowPageLiveTitleDataBean parse2LiveTitleBean(String expand_param,int code) {
//    	   YellowPageLiveTitleDataBean bean =null;
//    	   JSONObject expandObj = null;
//    	   try {
//			expandObj=new JSONObject(expand_param);
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//    	   if(expandObj == null){
//    		   return null;
//    	   }
//    	   bean = new YellowPageLiveTitleDataBean();
//    	   bean.setCode(code);
//           try {
//        	   if (expandObj.has("color"))
//				   bean.setColor((int) Long.parseLong(expandObj.getString("color"), 16));
//        	   if (expandObj.has("text"))
//				   bean.setText(expandObj.getString("text"));
//			   if (expandObj.has("imgUrl"))
//				   bean.setImgUrl(expandObj.getString("imgUrl"));
//			   if (expandObj.has("dismissTime"))
//				   bean.setDismissTime(expandObj.getLong("dismissTime"));
//			   if (expandObj.has("textSize"))
//				   bean.setTextSize((float) expandObj.getDouble("textSize"));
//			   if (expandObj.has("keyWordColor"))
//				   bean.setKeyWordColor((int) Long.parseLong(expandObj.getString("keyWordColor"), 16));
//			   if (expandObj.has("keyWordStart"))
//				   bean.setKeyWordStart(expandObj.getInt("keyWordStart"));
//			   if (expandObj.has("keyWordEnd"))
//				   bean.setKeyWordEnd(expandObj.getInt("keyWordEnd"));
//			   if (expandObj.has("keyWordSize"))
//				   bean.setKeyWordSize((float) expandObj.getDouble("keyWordSize"));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//           return bean;
//	}
//    //add by lisheng end
    //modified by hyl 2014-12-23 start
}
