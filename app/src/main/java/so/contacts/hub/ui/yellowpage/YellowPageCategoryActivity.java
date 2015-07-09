
package so.contacts.hub.ui.yellowpage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import so.contacts.hub.active.ActiveUtils;
import so.contacts.hub.db.YellowPageDB;
import so.contacts.hub.remind.BaseRemindActivity;
import so.contacts.hub.remind.RemindConfig;
import so.contacts.hub.remind.RemindManager;
import so.contacts.hub.remind.simple.SimpleRemindView;
import so.contacts.hub.remind.utils.RemindUtils;
import so.contacts.hub.ui.yellowpage.bean.CategoryBean;
import so.contacts.hub.ui.yellowpage.bean.ItemBean;
import so.contacts.hub.ui.yellowpage.bean.PuTaoResultItem;
import so.contacts.hub.ui.yellowpage.bean.YellowParams;
import so.contacts.hub.util.ContactsAppUtils;
import so.contacts.hub.util.ContactsHubUtils;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.MobclickAgentUtil;
import so.contacts.hub.util.UMengEventIds;
import so.contacts.hub.util.YellowUtil;
import so.contacts.hub.widget.AdOperatLayout.AdLayoutCallback;
import so.contacts.hub.yellow.data.RemindBean;
import so.putao.findplug.YellowPageItemPutao;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loader.DataLoader;
import com.loader.image.ImageLoaderFactory;
import com.yulong.android.contacts.discover.R;

public class YellowPageCategoryActivity extends BaseRemindActivity implements View.OnClickListener, AdLayoutCallback {

    private static final String TAG = "YellowPageCategoryActivity";

    public static final String DefSearchActivity = "so.contacts.hub.ui.yellowpage.YellowPageSearchActivity";

    public static final String DefDetailActivity = "so.contacts.hub.ui.yellowpage.YellowPageShopDetailActivity";

    private YellowPageDB mDbHelper = null;

    private List<CategoryBean> mParenCategorytList = null;

    private Map<CategoryBean, List<ItemBean>> mCategoryMaps = null;

    private LinearLayout category_layout;

    private DataLoader mDataLoader = null;

    private QueryThread mQueryThread = null;

    private long mCurrentCategoryId = -1;
    
    //private View lineView;

    private Map<ImageView, Bitmap> viewMap = new HashMap<ImageView, Bitmap>();

    private long categoryResumeTime;
    
    private List<SimpleRemindView> mRemindViewList = null;
    
    //private boolean mFirstNeedLoadAd = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d(TAG, "onCreate");

        mDbHelper = ContactsAppUtils.getInstance().getDatabaseHelper().getYellowPageDBHelper();
        mCategoryMaps = new HashMap<CategoryBean, List<ItemBean>>();
        mRemindViewList = new ArrayList<SimpleRemindView>();

        setContentView(R.layout.putao_yellow_page_category_main);
        category_layout = (LinearLayout)findViewById(R.id.category_layout);
        findViewById(R.id.back_layout).setOnClickListener(this);

        mDataLoader = new ImageLoaderFactory(this).getNormalLoader(true, false);
        if( mYellowParams != null ){
        	mCurrentCategoryId = mYellowParams.getCategory_id();
        }

        TextView titleTView = (TextView)findViewById(R.id.title);
        if( TextUtils.isEmpty(mTitleContent) ){
            titleTView.setText("");
        }else{
        	titleTView.setText(mTitleContent);
        }
        
        mQueryThread = new QueryThread();
        mQueryThread.start();            
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgentUtil.onResume(this);
        LogUtil.d(TAG, "onResume");
        categoryResumeTime = System.currentTimeMillis();
        
        //add ljq 2014 11 12 start 增加打点刷新
        for (int i = 0; i < mRemindViewList.size(); i++) {
            SimpleRemindView view = mRemindViewList.get(i);
            int code = view.getRemindCode();
            RemindBean bean = RemindUtils.getRemind(code);
            view.setRemind(bean);
        }
        //add ljq 2014 11 12 end 增加打点刷新
    }

    @Override
    protected void onPause() {
        MobclickAgentUtil.onPause(this);
        try {
            int value = (int)((System.currentTimeMillis() - categoryResumeTime) / 1000);
            Map<String, String> map_value = new HashMap<String, String>();
            map_value.put("type", null == mTitleContent ? "YellowPageCategoryActivity" : mTitleContent);
//            com.putao.analytics.MobclickAgentUtil.onEventValue(this, UMengEventIds.DISCOVER_YELLOWPAGE_RESUME_TIME_HEADER
//                    + mCurrentCategoryId, map_value, value);
            MobclickAgentUtil.onEventValue(this, UMengEventIds.DISCOVER_YELLOWPAGE_RESUME_TIME_HEADER
                    + mCurrentCategoryId, map_value, value);
        } catch (Exception e) {
        }

        super.onPause();
    }

    private View getItemView(ItemBean item, int resBgId) {
        View itemView = View.inflate(this, R.layout.putao_yellow_page_category_item, null);
        /**
         * delete code
         * modify by putao_lhq
         * itemView.setBackgroundResource(resBgId);*/

        ImageView iconView = (ImageView)itemView.findViewById(R.id.icon);
        SimpleRemindView remindView = (SimpleRemindView)itemView.findViewById(R.id.remind_view);

        RemindBean bean = RemindUtils.getRemind(item.getRemind_code());
        remindView.setRemindCode(item.getRemind_code());
        if(bean != null && bean.getRemindType() > RemindConfig.REMIND_TYPE_NONE ) {
            remindView.setDataLoader(mDataLoader);
            remindView.setRemind(bean);
        }
        //add ljq 2014 11 12 start 增加打点刷新
        mRemindViewList.add(remindView);
        //add ljq 2014 11 12 end 增加打点刷新
        String icon = item.getIcon();
        try {
            int iconResourceId = this.getResources().getIdentifier(icon, "drawable",
                    this.getPackageName());
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), iconResourceId);
            if (bitmap != null) {
                // int imageSize =
                // getResources().getDimensionPixelSize(R.dimen.putao_listview_item_imgsize);
                int corner = getResources().getDimensionPixelSize(R.dimen.putao_image_round_corner);
                Bitmap bitmap2 = ContactsHubUtils.corner(bitmap, corner, 0);
                iconView.setImageBitmap(bitmap2);
                viewMap.put(iconView, bitmap2);
            }

        } catch (NotFoundException ex) {
            ex.printStackTrace();
            LogUtil.e(TAG, ex.getMessage() + ", icon: " + icon);
        }

        TextView name = (TextView)itemView.findViewById(R.id.name);
        name.setText(item.getName());

        TextView description = (TextView)itemView.findViewById(R.id.description);
        if (TextUtils.isEmpty(item.getDescription())) {
            description.setVisibility(View.GONE);
        } else {
            description.setText(item.getDescription());
        }

        itemView.setOnClickListener(this);
        itemView.setTag(item);

        iconView.setOnClickListener(this);
        iconView.setTag(item);

        return itemView;
    }

    /**
     * 获取分类布局
     * 
     * @param category 类别
     * @return 分类布局
     */
    private LinearLayout getCategoryView(CategoryBean category) {
        LinearLayout categoryView = (LinearLayout)View.inflate(this,
                R.layout.putao_yellow_page_category_layout, null);

        /**
         * modify by putao_lhq
         * delete code
         * @start 
        TextView categoryNameTextView = (TextView)categoryView
                .findViewById(R.id.category_name_textview);
        @end*/
        TextView categoryNameTextView = (TextView)categoryView
                .findViewById(R.id.tvTag);
        String name = category.getShow_name();
        if (TextUtils.isEmpty(name)) {
            categoryNameTextView.setVisibility(View.GONE);
        } else {
            name = ContactsHubUtils.getShowName(this, name);
            categoryNameTextView.setText(name);
        }

        return categoryView;
    }

    private void refreshViews() {
        category_layout.removeAllViews();
        LogUtil.i(TAG, "mParenCategorytList Size:" + mParenCategorytList.size());
        for (CategoryBean root : mParenCategorytList) {
            LinearLayout categoryView = getCategoryView(root);
            LinearLayout categoryParentLayout = (LinearLayout)categoryView
                    .findViewById(R.id.category_item_parent_layout);
            List<ItemBean> subList = mCategoryMaps.get(root);
            
            if (null == subList || subList.size() == 0) {
                continue;
            }

            int subListSize = subList.size();
            for (int i = 0; i < subList.size(); i++) {
                /* *
                 * modify by putao_lhq @start
                 * coolui6.0
                 * old code:
                ItemBean item = subList.get(i);
                View itemView = getItemView(item, getItemStyle(subListSize, i));
                categoryParentLayout.addView(itemView, getLayoutParams());
                if (needAddLine(subListSize, i)) {
                    categoryParentLayout.addView(getLineView());
                }*/
                ItemBean item = subList.get(i);
                View itemView = getItemView(item, getItemStyle(subListSize, i));
                categoryParentLayout.addView(itemView, getLayoutParams());
                /*@end*/
            }
            category_layout.addView(categoryView);
        }
    }

    private LayoutParams getLayoutParams() {
        int height = getResources().getDimensionPixelSize(R.dimen.putao_listview_item_height);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, height);
        return params;
    }

    /**
     * 获取子项的样式
     * 
     * @param totalSize 子项总和
     * @param index 当前子项位置
     * @return 样式资源id
     */
    private int getItemStyle(int totalSize, int index) {
        /**
         * delete code
         * modify by putao_lhq
         * coolui6.0
         *
        int itemStyle = R.drawable.putao_bg_yellow_item_default;
        if (totalSize == 1) {
            itemStyle = R.drawable.putao_bg_yellow_item_single;
        } else {
            if (index == 0) {
                itemStyle = R.drawable.putao_bg_yellow_item_up;
            } else if (index == totalSize - 1) {
                itemStyle = R.drawable.putao_bg_yellow_item_down;
            }
        }
        return itemStyle;*/
        return R.drawable.putao_bg_yellow_item_single;
    }

    /**
     * 判断是否需要添加分割线
     * 
     * @param totalSize 子项总和
     * @param index 当前子项位置
     * @return true:需要添加，false:不需要
     */
    /*private boolean needAddLine(int totalSize, int index) {
        if (totalSize == 1 || index == totalSize - 1) {
            return false;
        }
        return true;
    }*/

    /**
     * 获取分割线View
     * 
     * @return 分割线View
     */
    /*private View getLineView() {
        lineView = new View(this);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, getResources()
                .getDimensionPixelSize(R.dimen.putao_divider_line_size));
        lp.setMargins(5, 0, 5, 0);
        lineView.setLayoutParams(lp);
        lineView.setBackgroundResource(R.color.putao_yellow_page_line_color);
        return lineView;
    }*/

    private class QueryThread extends Thread {

        @Override
        public void run() {
            List<CategoryBean> thirdLevelCategoryList = mDbHelper
                    .queryCategoryByParentId(mCurrentCategoryId); // 当前是第二级类别
            if (null == thirdLevelCategoryList) {
                mhandler.sendEmptyMessage(REFRESH_VIEW);
            }
            for (CategoryBean bean : thirdLevelCategoryList) {
                List<ItemBean> subList = mDbHelper.queryItemByCategoryId(bean.getCategory_id());
                mCategoryMaps.put(bean, subList);
            }
            mParenCategorytList = thirdLevelCategoryList;
            mhandler.sendEmptyMessage(REFRESH_VIEW);
        }

    }

    private final int REFRESH_VIEW = 0x1;

    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH_VIEW:
                    refreshViews();
                    break;
                default:
                    break;
            }
        }
    };
    
    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.d(TAG, "onStop");
        

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "onDestroy");
        if(mQueryThread != null){
            mQueryThread.interrupt();
            mQueryThread = null;
        }
        mRemindViewList.clear();
        mRemindViewList = null;
        
        recyleImage();
    }

    private void recyleImage() {
        for (java.util.Map.Entry<ImageView, Bitmap> item : viewMap.entrySet()) {
            ImageView view = item.getKey();
            Bitmap bm = item.getValue();
            view.setImageBitmap(null);
            if (!bm.isRecycled()) {
                bm.recycle();
            }
        }
        viewMap.clear();
    }

    @Override
    public void onClick(View arg0) {
        if (arg0.getId() == R.id.back_layout) {
            finish();
        } else {
            ItemBean category = (ItemBean)arg0.getTag();
            if (category == null)
                return;

            Intent intent = null;
            if (TextUtils.isEmpty(category.getTarget_activity())) {
                // 跳转到shopdetail详情
                String content = category.getContent();
                if (TextUtils.isEmpty(content))
                    return;

                Gson gson = new Gson();
                PuTaoResultItem puTaoResultItem = gson.fromJson(content, PuTaoResultItem.class);

                // 葡萄的图片 和 默认图片均为本地数据
                puTaoResultItem.setPhotoUrl(category.getIcon());
                puTaoResultItem.setDefaultPhotoUrl(category.getIcon());

                YellowPageItemPutao itemPutao = new YellowPageItemPutao(puTaoResultItem);

                try {
                    intent = new Intent(this, Class.forName(DefDetailActivity));
                    intent.putExtra("YelloPageItem", itemPutao);
                    intent.putExtra("CategoryId", mCurrentCategoryId);
                    intent.putExtra("ItemId", category.getItem_id());
                    intent.putExtra("RemindCode", category.getRemind_code());
                    
                    MobclickAgentUtil.onEvent(this,
                    		UMengEventIds.DISCOVER_YELLOWPAGE_CATEGORY_FAST_ITEM_HEADER
									+ category.getItem_id());
                    startActivity(intent);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

            } else {
                // 跳转到指定Activity
                try {
                    intent = new Intent(this, Class.forName(category.getTarget_activity()));

                    String params = category.getTarget_params();
                    if (TextUtils.isEmpty(params))
                        return;
                    Gson gson = new Gson();
                    YellowParams keys = gson.fromJson(params, YellowParams.class);
                    keys.setRemindCode(category.getRemind_code());
                    
                    intent.putExtra(YellowUtil.TargetIntentParams, keys);
                    intent.putExtra("CategoryId", mCurrentCategoryId);
                    intent.putExtra("ItemId", category.getItem_id());
                    intent.putExtra("RemindCode", category.getRemind_code());
                    startActivity(intent);

                    RemindManager.onRemindClick(category.getRemind_code());
                    
                    String str = UMengEventIds.DISCOVER_YELLOWPAGE_CATEGORY_FAST_ITEM_HEADER
					        + category.getItem_id();
                    LogUtil.i(TAG, "upload um: " + str);
					MobclickAgentUtil.onEvent(
                            this,
                            str);

                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public Integer remindCode() {
        return mRemindCode;
    }

    @Override
    public String getServiceNameByUrl() {
        return null;
    }

	@Override
	public String getServiceName() {
		return this.getClass().getName();
	}

	@Override
	public boolean needMatchExpandParam() {
		return true;
	}

	@Override
	public String getReqTailSign() {
		// TODO Auto-generated method stub
		return ActiveUtils.getRequrlOfSignTail();
	}

	@Override
	public void deleteAdBean(int serverCode, int pageIndex) {
		// TODO Auto-generated method stub
		if( mDbHelper != null ){
			mDbHelper.deleteAdData(serverCode, pageIndex);
		}
	}    
	
	public Integer getAdId(){
	    if(-1 == mCurrentCategoryId){
	        return null;
	    }
	    
	    return Integer.valueOf((int)mCurrentCategoryId);
	}
	
	@Override
	protected boolean needReset() {
	    return true;
	}
}
