package so.contacts.hub.adapter;

import java.util.List;
import java.util.Map;

import so.contacts.hub.core.Config;
import so.contacts.hub.remind.simple.SimpleRemindView;
import so.contacts.hub.ui.yellowpage.bean.CategoryBean;
import so.contacts.hub.util.ContactsHubUtils;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.yellow.data.RemindBean;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loader.DataLoader;
import com.loader.image.ImageLoaderFactory;
import com.yulong.android.contacts.discover.R;

public class MyListViewAdapter extends BaseAdapter {
    static final String TAG = "MyListViewAdapter";
    LayoutInflater inflater;
    Context context;
    List<CategoryBean> categroyList;
    MyListViewListener listener;
    Map<Integer, RemindBean> remindMaps = null;
    
    DataLoader mRemindDataLoader = null;

    private static final int COLUMN_NUM = 3;
    
    public MyListViewAdapter(Context context, MyListViewListener listener,
            List<CategoryBean> categroyList, Map<Integer, RemindBean> remindMaps) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.listener = listener;
        mRemindDataLoader = new ImageLoaderFactory(context).getNormalLoader(false, false);
        
        this.categroyList = categroyList;
        this.remindMaps = remindMaps;
    }
    		
    public void setData(List<CategoryBean> categroyList, Map<Integer, RemindBean> remindMaps){
    	this.categroyList = categroyList;
        this.remindMaps = remindMaps;
        notifyDataSetChanged();
    }
    
    @Override
    public int getCount() {
        if(categroyList != null){
            int size = categroyList.size();
            if(size % COLUMN_NUM ==0){
                size = size / COLUMN_NUM;
            }else{
                size = (size / COLUMN_NUM) +1;
            }
            return size;
        }
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
        ViewHolder holder;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.putao_yellow_page_home_second_item, null);
//            LogUtil.d(TAG, "getView ListView inflate time:"+(System.currentTimeMillis() - startTime)+"pos:"+position);
            
            holder.firstItem = (RelativeLayout)convertView.findViewById(R.id.first_item);
            holder.firstItemImageView = (ImageView) convertView.findViewById(R.id.first_item_img_view);
            holder.firstNameView = (TextView)convertView.findViewById(R.id.first_name_text);
//            holder.firstRemindView = (SimpleRemindView)convertView.findViewById(R.id.first_remind_view);
//            holder.firstRemindPointView = (SimpleRemindView)convertView.findViewById(R.id.first_remind_point_view);
//            holder.firstRemindPointDigitView = (SimpleRemindView)convertView.findViewById(R.id.first_remind_point_digit_view);
            
            holder.secondItem = (RelativeLayout)convertView.findViewById(R.id.second_item);
            holder.secondNameView = (TextView)convertView.findViewById(R.id.second_name_text);
            holder.secondItemImageView = (ImageView) convertView.findViewById(R.id.second_item_img_view);
//            holder.secondRemindView = (SimpleRemindView)convertView.findViewById(R.id.second_remind_view);
//            holder.secondRemindPointView = (SimpleRemindView)convertView.findViewById(R.id.second_remind_point_view);
//            holder.secondRemindPointDigitView = (SimpleRemindView)convertView.findViewById(R.id.second_remind_point_digit_view);

            holder.thirdItem = (RelativeLayout)convertView.findViewById(R.id.third_item);
            holder.thirdNameView = (TextView)convertView.findViewById(R.id.third_name_text);
            holder.thirdItemImageView = (ImageView) convertView.findViewById(R.id.third_item_img_view);
//            holder.thirdRemindView = (SimpleRemindView)convertView.findViewById(R.id.third_remind_view);
//            holder.thirdRemindPointView = (SimpleRemindView)convertView.findViewById(R.id.third_remind_point_view);
//            holder.thirdRemindPointDigitView = (SimpleRemindView)convertView.findViewById(R.id.third_remind_point_digit_view);
            holder.secondDivideLine = convertView.findViewById(R.id.second_divider_line);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }
        final CategoryBean firstbean = categroyList.get(position * COLUMN_NUM);
        if(firstbean != null){
            if(holder.firstRemindView != null){
                holder.firstRemindView.setVisibility(View.GONE);
            }
            // delete by putao_lhq 2014年12月2日 for CoolUi-更改主页打点为文字右侧居中显示 start
            /*if(holder.firstRemindPointView != null){
                holder.firstRemindPointView.setVisibility(View.GONE);
            }
            if(holder.firstRemindPointDigitView != null){
                holder.firstRemindPointDigitView.setVisibility(View.GONE);
            }*/
            // delete by putao_lhq 2014年12月2日 for CoolUi-更改主页打点为文字右侧居中显示 end
            if(remindMaps != null) {
                RemindBean bean = remindMaps.get(firstbean.getRemind_code());
                if( bean != null && bean.getRemindCode() > 0 ) {
//                    LogUtil.d(TAG, "getView remind test pos:"+position+" style:"+bean.getStyle());
                    if(holder.firstRemindView == null){
                        View view = ((ViewStub)convertView.findViewById(R.id.first_remind_view_layout)).inflate();
                        holder.firstRemindView = (SimpleRemindView) view.findViewById(R.id.remind_view);
                    }
                    // delete by putao_lhq 2014年12月2日 for CoolUi-更改主页打点为文字右侧居中显示 start
                    /*if(holder.firstRemindPointView == null){
                        View view = ((ViewStub)convertView.findViewById(R.id.first_remind_point_view_layout)).inflate();
                        holder.firstRemindPointView = (SimpleRemindView) view.findViewById(R.id.remind_view);
                    }
                    if(holder.firstRemindPointDigitView == null){
                        View view = ((ViewStub)convertView.findViewById(R.id.first_remind_point_digit_view_layout)).inflate();
                        holder.firstRemindPointDigitView = (SimpleRemindView) view.findViewById(R.id.remind_view);
                    }*/
                    // delete by putao_lhq 2014年12月2日 for CoolUi-更改主页打点为文字右侧居中显示 end
                	showRemindByInfo(bean, holder.firstRemindView, holder.firstRemindPointView, holder.firstRemindPointDigitView);
                }
            }
            
            initInfo(firstbean, holder.firstItemImageView, holder.firstItem, holder.firstNameView);
        }
        
        boolean isValid = false;
        int secondPos = position * COLUMN_NUM + 1;
        if(secondPos < categroyList.size()){
            final CategoryBean secondbean = categroyList.get(secondPos);
            if( secondbean != null ){
                if(holder.secondRemindView != null){
                    holder.secondRemindView.setVisibility(View.GONE);
                }
                // delete by putao_lhq 2014年12月2日 for CoolUi-更改主页打点为文字右侧居中显示 start
                /*if(holder.secondRemindPointView != null){
                    holder.secondRemindPointView.setVisibility(View.GONE);
                }
                if(holder.secondRemindPointDigitView != null){
                    holder.secondRemindPointDigitView.setVisibility(View.GONE);
                }*/
                // delete by putao_lhq 2014年12月2日 for CoolUi-更改主页打点为文字右侧居中显示 end
            	if(remindMaps != null) {
            		RemindBean bean = remindMaps.get(secondbean.getRemind_code());
            		if( bean != null && bean.getRemindCode() > 0 ) {
            		    if(holder.secondRemindView == null){
                            View view = ((ViewStub)convertView.findViewById(R.id.second_remind_view_layout)).inflate();
                            holder.secondRemindView = (SimpleRemindView) view.findViewById(R.id.remind_view);
                        }
            		    // delete by putao_lhq 2014年12月2日 for CoolUi-更改主页打点为文字右侧居中显示 start
                       /* if(holder.secondRemindPointView == null){
                            View view = ((ViewStub)convertView.findViewById(R.id.second_remind_point_view_layout)).inflate();
                            holder.secondRemindPointView = (SimpleRemindView) view.findViewById(R.id.remind_view);
                        }
                        if(holder.secondRemindPointDigitView == null){
                            View view = ((ViewStub)convertView.findViewById(R.id.second_remind_point_digit_view_layout)).inflate();
                            holder.secondRemindPointDigitView = (SimpleRemindView) view.findViewById(R.id.remind_view);
                        }*/
            		    // delete by putao_lhq 2014年12月2日 for CoolUi-更改主页打点为文字右侧居中显示 end
            			showRemindByInfo(bean, holder.secondRemindView, holder.secondRemindPointView, holder.secondRemindPointDigitView);
            		}
            	}
            	
            	initInfo(secondbean, holder.secondItemImageView, holder.secondItem, holder.secondNameView);
            	isValid = true;
            }
        }
        if( isValid ){
            holder.secondItem.setVisibility(View.VISIBLE);
        	holder.secondDivideLine.setVisibility(View.VISIBLE);
        }else{
        	holder.secondItem.setVisibility(View.INVISIBLE);
        	holder.secondDivideLine.setVisibility(View.INVISIBLE);
        }
        
        isValid = false;
        int thirdPos = position * COLUMN_NUM + 2;
        if(thirdPos < categroyList.size()){
            final CategoryBean thirdbean = categroyList.get(thirdPos);
            if(thirdbean != null){
                if(holder.thirdRemindView != null){
                    holder.thirdRemindView.setVisibility(View.GONE);
                }
                // delete by putao_lhq 2014年12月2日 for CoolUi-更改主页打点为文字右侧居中显示 start
                /*if(holder.thirdRemindPointView != null){
                    holder.thirdRemindPointView.setVisibility(View.GONE);
                }
                if(holder.thirdRemindPointDigitView != null){
                    holder.thirdRemindPointDigitView.setVisibility(View.GONE);
                }*/
                // delete by putao_lhq 2014年12月2日 for CoolUi-更改主页打点为文字右侧居中显示 end
                if(remindMaps != null) {
                    RemindBean bean = remindMaps.get(thirdbean.getRemind_code());
                    if( bean != null && bean.getRemindCode() > 0 ) {
                        if(holder.thirdRemindView == null){
                            View view = ((ViewStub)convertView.findViewById(R.id.third_remind_view_layout)).inflate();
                            holder.thirdRemindView = (SimpleRemindView) view.findViewById(R.id.remind_view);
                        }
                        // delete by putao_lhq 2014年12月2日 for CoolUi-更改主页打点为文字右侧居中显示 start
                        /*if(holder.thirdRemindPointView == null){
                            View view = ((ViewStub)convertView.findViewById(R.id.third_remind_point_view_layout)).inflate();
                            holder.thirdRemindPointView = (SimpleRemindView) view.findViewById(R.id.remind_view);
                        }
                        if(holder.thirdRemindPointDigitView == null){
                            View view = ((ViewStub)convertView.findViewById(R.id.third_remind_point_digit_view_layout)).inflate();
                            holder.thirdRemindPointDigitView = (SimpleRemindView) view.findViewById(R.id.remind_view);
                        }*/
                        // delete by putao_lhq 2014年12月2日 for CoolUi-更改主页打点为文字右侧居中显示 end
                    	showRemindByInfo(bean, holder.thirdRemindView, holder.thirdRemindPointView, holder.thirdRemindPointDigitView);
            		}
                }
                
                initInfo(thirdbean, holder.thirdItemImageView, holder.thirdItem, holder.thirdNameView);
                isValid = true;
            }
        }
        if( isValid ){
            holder.thirdItem.setVisibility(View.VISIBLE);
        	holder.secondDivideLine.setVisibility(View.VISIBLE);
        }else{
        	holder.thirdItem.setVisibility(View.INVISIBLE);
        }
        long endTime = System.currentTimeMillis();
//        LogUtil.d(TAG, "getView ListView time:"+(endTime - startTime)+"pos:"+position);
//        if(position == getCount()-1){
//            LogUtil.d(TAG, "getView ListView setAdapter end: " + System.currentTimeMillis());
//        }
        return convertView;
    }
    
    /**
     * 显示打点信息
     */
    private void showRemindByInfo(final RemindBean bean, SimpleRemindView remindViewOther, SimpleRemindView remindViewPoint, SimpleRemindView remindViewDigit){
    	int pointState = getRemindBeanPointState(bean);
    	 // modify by putao_lhq 2014年12月2日 for CoolUi-更改主页打点为文字右侧居中显示 start
    	/*if( pointState == REMIND_POINT ){
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
    		remindViewOther.setRemind(bean);
    		remindViewOther.setDataLoader(mRemindDataLoader);
    	}*/
    	if (remindViewOther == null) {
    	    LogUtil.e(TAG, "remindView is null");
    	}
    	remindViewOther.setVisibility(View.VISIBLE);
    	remindViewOther.setRemind(bean);
    	if( pointState == REMIND_POINT ){
        }else if( pointState == REMIND_POINT_DIGIT){
        }else{
            remindViewOther.setDataLoader(mRemindDataLoader);
        }
    	 // modify by putao_lhq 2014年12月2日 for CoolUi-更改主页打点为文字右侧居中显示 end
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
        String imgUrl = bean.getImgUrl();
        int style = bean.getStyle();
        
        if( TextUtils.isEmpty(imgUrl) ){
        	// 无图片
        	if( TextUtils.isEmpty(text) ){
        		// 无文字
        		if(style > 0) {
        	    	// 根据样式显示
        			return REMIND_OTHER;
        	    } else if( count == 0 ){
        	    	// 显示小点
        	    	return REMIND_POINT;
        		} else {
        			// 显示大点
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
    
    private void initInfo(final CategoryBean bean, ImageView iconView, RelativeLayout itemLayout, TextView nameView) {
        String iconlogo = bean.getIconLogo();
        // modity by ljq 2014-10-15 start
        if (!TextUtils.isEmpty(iconlogo)) {
            // 如果类型不是网络地址 则取本地图片
            if (!ContactsHubUtils.isURlStr(iconlogo)) {
                int iconResourceId = context.getResources().getIdentifier(iconlogo, "drawable",
                        context.getPackageName());
                if (iconResourceId != 0) {
                    iconView.setImageResource(iconResourceId);
                    iconView.setVisibility(View.VISIBLE);
                } else {

                }
            } else if (ContactsHubUtils.isURlStr(iconlogo)) {
                if (mRemindDataLoader != null) {
                    // 类型是网络地址 则去取缓存 没有会去下载
                    Bitmap bitmap = mRemindDataLoader.loadDataReturnBitmap(iconlogo, iconView);
                    if (bitmap != null) {
                        iconView.setImageBitmap(bitmap);
                    } else {
                        //贴上默认图片
                        int iconResourceId = context.getResources().getIdentifier(
                                Config.DEFAULT_CATEGORY_IMAGE_SMALL, "drawable",
                                context.getPackageName());
                        iconView.setImageResource(iconResourceId);
                    }
                }
            }
        }
        // modity by ljq 2014-10-15 end
        String tagIcon = bean.getTagIcon();
        if(!TextUtils.isEmpty(tagIcon)){
            int iconResourceId = context.getResources().getIdentifier(tagIcon, "drawable", context.getPackageName());//2130837744
            if(iconResourceId != 0){
//                tagView.setImageResource(iconResourceId);
//                tagView.setVisibility(View.VISIBLE);
            }
        }else{
//            tagView.setVisibility(View.GONE);
        }
        String name = ContactsHubUtils.getShowName(context, bean.getShow_name());
        nameView.setText(name);
        itemLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                listener.onCustomItemClick(bean);
            }
        });
    }

    private class ViewHolder{
        public RelativeLayout firstItem;
        public ImageView firstItemImageView;
        public TextView firstNameView;
        public SimpleRemindView firstRemindView;       // 显示文本
        public SimpleRemindView firstRemindPointDigitView; // 显示数字
        public SimpleRemindView firstRemindPointView;  // 显示打点

        public RelativeLayout secondItem;
        public ImageView secondItemImageView;
        public TextView secondNameView;
        public SimpleRemindView secondRemindView;
        public SimpleRemindView secondRemindPointDigitView;
        public SimpleRemindView secondRemindPointView;

        public RelativeLayout thirdItem;
        public ImageView thirdItemImageView;
        public TextView thirdNameView;
        public SimpleRemindView thirdRemindView;
        public SimpleRemindView thirdRemindPointDigitView;
        public SimpleRemindView thirdRemindPointView;

        public View secondDivideLine;
    }
    
    private static final int REMIND_POINT = 0;        // 显示打点
    private static final int REMIND_POINT_DIGIT = 1;  // 显示数字
    private static final int REMIND_OTHER = 2;        // 显示文本

}
