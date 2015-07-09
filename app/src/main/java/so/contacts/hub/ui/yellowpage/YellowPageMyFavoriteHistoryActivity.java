package so.contacts.hub.ui.yellowpage;

import so.putao.findplug.YellowPageTongChengItem;

import so.contacts.hub.thirdparty.tongcheng.bean.TongChengHotelItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import so.contacts.hub.adapter.YellowPageFavoriteListAdapter;
import so.contacts.hub.adapter.YellowPageFavoriteListAdapter.ViewHolder;
import so.contacts.hub.core.ConstantsParameter;
import so.contacts.hub.db.YellowPageDB;
import so.contacts.hub.remind.BaseRemindActivity;
import so.contacts.hub.remind.RemindConfig;
import so.contacts.hub.thirdparty.elong.bean.ELongHotelItem;
import so.contacts.hub.ui.yellowpage.bean.GaoDePoiItem;
import so.contacts.hub.ui.yellowpage.bean.PuTaoResultItem;
import so.contacts.hub.ui.yellowpage.bean.SougouHmtItem;
import so.contacts.hub.util.ContactsAppUtils;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.MobclickAgentUtil;
import so.contacts.hub.util.UMengEventIds;
import so.contacts.hub.widget.CommonDialog;
import so.contacts.hub.widget.CommonDialogFactory;
import so.contacts.hub.widget.ProgressDialog;
import so.putao.findplug.City58Item;
import so.putao.findplug.DianPingBusiness;
import so.putao.findplug.YelloPageItem;
import so.putao.findplug.YelloPageItemSougou;
import so.putao.findplug.YellowPageCollectData;
import so.putao.findplug.YellowPageELongItem;
import so.putao.findplug.YellowPageItemCity58;
import so.putao.findplug.YellowPageItemDianping;
import so.putao.findplug.YellowPageItemGaoDe;
import so.putao.findplug.YellowPageItemPutao;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loader.DataLoader;
import com.loader.image.ImageLoaderFactory;
import com.yulong.android.contacts.discover.R;

public class YellowPageMyFavoriteHistoryActivity extends BaseRemindActivity implements OnClickListener, OnItemClickListener,
		OnCheckedChangeListener, OnItemLongClickListener {

	private static final String TAG = "YellowPageMyActivity";
	
	// layout
	private CheckBox mSelectAllCheckBox = null;
	private TextView mSelectAllTextView = null;
	private ListView mListView = null;
	private LinearLayout mDeleteLayout = null;
	private LinearLayout mNoDataLayout = null;
	private TextView mNoDataTView = null;
	private ProgressDialog mProgressDialog = null;
	private CommonDialog mDeleteDialog = null;
	
	private DataLoader mImageLoader = null;

	private YellowPageDB mYellowPageDB = null;
	
	private QueryTask mQueryTask = null;
	
	private List<YellowPageCollectData> mAllDBDataList = new ArrayList<YellowPageCollectData>(); // 所有数据（来自于数据库）
	
	@SuppressWarnings("rawtypes")
	private List<YelloPageItem> mAllDataList = new ArrayList<YelloPageItem>(); // 所有数据（来自于数据库，解析的数据）

	private List<YellowPageCollectData> mDeleteDataList = new ArrayList<YellowPageCollectData>(); //存储选中的删除数据
	
	private YellowPageFavoriteListAdapter mAllDataAdapter = null;
	
	private boolean needRefresAllData = true; // 是否需要刷新数据

	private int mSelectType = ConstantsParameter.YELLOWPAGE_DATA_TYPE_DEFAULT; // 类型：历史/收藏

	private boolean isSelectFavorite = true;
	
	private boolean isDeleteMode = false; // 是否是编辑模式
	
	private static final int MSG_SHOW_PROGRESS_ACTION = 0x2001;
	
	private static final int MSG_HIDE_PROGRESS_ACTION = 0x2002;

	private static final int MSG_NO_DATA_ACTION = 0x2003;

    private static final int RESULT_SHOP_DETAIL_CODE = 0x1001; //进入黄页详情返回Code

	// 友盟统计：进入时间
	private long startTime = 0L;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.putao_yellow_page_my_favoritehistory);

		mYellowPageDB = ContactsAppUtils.getInstance().getDatabaseHelper().getYellowPageDBHelper();
		mImageLoader = new ImageLoaderFactory(this).getDefaultYellowPageLoader();

		parseIntent();
		initView();
	}
	
	private void parseIntent(){
		Intent intent = getIntent();
		if (null == intent) {
			finish();
		}
		mSelectType = intent.getIntExtra("type", ConstantsParameter.YELLOWPAGE_DATA_TYPE_DEFAULT);
		if (mSelectType == ConstantsParameter.YELLOWPAGE_DATA_TYPE_FAVORITE) {
			isSelectFavorite = true;
		} else if (mSelectType == ConstantsParameter.YELLOWPAGE_DATA_TYPE_HISTORY) {
			isSelectFavorite = false;
		}else{
			finish();
		}
	}
	
	private void initView() {
		if (isSelectFavorite) {
			((TextView) findViewById(R.id.title)).setText(R.string.putao_yellow_page_favorite_title);
		} else{
			((TextView) findViewById(R.id.title)).setText(R.string.putao_yellow_page_history_title);
		}
		
		findViewById(R.id.back_layout).setOnClickListener(this);
		findViewById(R.id.delete_button).setOnClickListener(this);

		mListView = (ListView) findViewById(R.id.delete_listview);
		mListView.setOnItemClickListener(this);
		mListView.setOnItemLongClickListener(this);
		mAllDataAdapter = new YellowPageFavoriteListAdapter(this, mAllDataList, mImageLoader);
		mAllDataAdapter.notifyDataSetChanged();
		mListView.setAdapter(mAllDataAdapter);
		
		mSelectAllCheckBox = (CheckBox) findViewById(R.id.favorite_select_all_check_box);
		mSelectAllCheckBox.setOnCheckedChangeListener(this);
		mSelectAllTextView = (TextView) findViewById(R.id.select_all_tv);
		mNoDataLayout = (LinearLayout) findViewById(R.id.my_nodata_layout);
		mNoDataTView = (TextView) findViewById(R.id.exception_desc);
		
		mDeleteLayout = (LinearLayout) findViewById(R.id.delete_parent_layout);

		mProgressDialog = new ProgressDialog(this);
		if (!mProgressDialog.isShowing()) {
			mProgressDialog.setMessage(getString(R.string.putao_yellow_page_loading));
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgentUtil.onResume(this);
		startTime = System.currentTimeMillis();
		if( needRefresAllData ){
			refreshData();
		}
	}

	private void refreshData() {
		if( (mQueryTask != null && mQueryTask.getStatus() != AsyncTask.Status.RUNNING)
				|| mQueryTask == null ) {
			mQueryTask = new QueryTask();
			mQueryTask.execute();
        }
		needRefresAllData = false;
	}

	@SuppressWarnings("rawtypes")
	private class QueryTask extends AsyncTask<Void, Void, List<YelloPageItem>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mHandler.sendEmptyMessage(MSG_SHOW_PROGRESS_ACTION);
		}

		@Override
		protected List<YelloPageItem> doInBackground(Void... params) {
			mAllDBDataList = mYellowPageDB.queryAllMyData(mSelectType);
			if (null == mAllDBDataList) {
				mAllDataList.clear();
				mDeleteDataList.clear();
				return null;
			}
			LogUtil.i(TAG, "query favoritehistory item size: " + mAllDBDataList.size());

			List<YelloPageItem> queryList = new ArrayList<YelloPageItem>();
			for (YellowPageCollectData ypcd : mAllDBDataList) {
				YelloPageItem item = parseYellowPagrItem(ypcd.getType(), ypcd.getContent());
				if (null == item) {
					continue;
				}
				queryList.add(item);
			}
			return queryList;
		}

		@Override
		protected void onPostExecute(List<YelloPageItem> result) {
			mHandler.sendEmptyMessage(MSG_HIDE_PROGRESS_ACTION);
			if( result == null || result.size() == 0 ){
				mHandler.sendEmptyMessage(MSG_NO_DATA_ACTION);
				return;
			}
			mAllDataList = result;
			mAllDataAdapter.setData(mAllDataList);
		}
	}

	private Handler mHandler = new Handler() {
		@Override
        public void handleMessage(android.os.Message msg) {
			//add by ffh 2014-10-19 修改bug#1587
			if(isFinishing()){
				return;
			}
			//end
			int what = msg.what;
			switch (what) {
			case MSG_SHOW_PROGRESS_ACTION:
				mHandler.removeMessages(MSG_SHOW_PROGRESS_ACTION);
				if( mProgressDialog != null ){
					mProgressDialog.show();
				}
				break;
			case MSG_HIDE_PROGRESS_ACTION:
				mHandler.removeMessages(MSG_HIDE_PROGRESS_ACTION);
				if (mProgressDialog != null && mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();
				}
				break;
			case MSG_NO_DATA_ACTION:
				// 无数据
				mListView.setVisibility(View.GONE);
				mNoDataLayout.setVisibility(View.VISIBLE);
				if( isSelectFavorite ){
					mNoDataTView.setText(getResources().getString(R.string.putao_yellow_page_favorite_no_data));
				}else{
					mNoDataTView.setText(getResources().getString(R.string.putao_yellow_page_history_no_data));
				}
				break;
			default:
				break;
			}
		};
	};

	@SuppressWarnings("rawtypes")
	public YelloPageItem parseYellowPagrItem(int type, String content) {
		if (null == content || "".equals(content)) {
			return null;
		}
		try {
			Gson gson = new Gson();
			switch (type) {
			case ConstantsParameter.YELLOWPAGE_SOURCETYPE_PUTAO:
				PuTaoResultItem putao = gson.fromJson(content, PuTaoResultItem.class);
				YellowPageItemPutao putaoItem = new YellowPageItemPutao(putao);
				return putaoItem;
			case ConstantsParameter.YELLOWPAGE_SOURCETYPE_DIANPING:
				DianPingBusiness dianPing = gson.fromJson(content, DianPingBusiness.class);
				YellowPageItemDianping dianPingItem = new YellowPageItemDianping(dianPing);
				return dianPingItem;
			case ConstantsParameter.YELLOWPAGE_SOURCETYPE_SOUGOU:
				SougouHmtItem sogou = gson.fromJson(content, SougouHmtItem.class);
				YelloPageItemSougou sogouItem = new YelloPageItemSougou(sogou);
				return sogouItem;
			case ConstantsParameter.YELLOWPAGE_SOURCETYPE_GAODE:
				GaoDePoiItem gaode = gson.fromJson(content,
						GaoDePoiItem.class);
				YellowPageItemGaoDe gaodeItem = new YellowPageItemGaoDe(gaode);
				return gaodeItem;
			case ConstantsParameter.YELLOWPAGE_SOURCETYPE_WUBA:
				City58Item wuba = gson.fromJson(content, City58Item.class);
				YellowPageItemCity58 wubaItem = new YellowPageItemCity58(wuba);
				return wubaItem;
			case ConstantsParameter.YELLOWPAGE_SOURCETYPE_ELONG:
				ELongHotelItem elong = gson.fromJson(content, ELongHotelItem.class);
				YellowPageELongItem elongItem = new YellowPageELongItem(elong);
				return elongItem;
			// add xcx 2014_12_25 start 新增同程搜索
			case ConstantsParameter.YELLOWPAGE_SOURCETYPE_TONGCHENG:
                TongChengHotelItem tongCheng = gson.fromJson(content, TongChengHotelItem.class);
                YellowPageTongChengItem tongChengItem = new YellowPageTongChengItem(tongCheng);
                return tongChengItem;
             // add xcx 2014_12_25 end 新增同程搜索
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressLint("UseValueOf")
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
		LogUtil.d(TAG, "onClick item: " + position);
		if( isDeleteMode ){
			ViewHolder vHollder = (ViewHolder)view.getTag();
			YellowPageCollectData itemData = mAllDBDataList.get(position);
			if(mDeleteDataList.contains(itemData)){
				mDeleteDataList.remove(itemData);
			} else {
				mDeleteDataList.add(itemData);
			}
			// add by putao_lhq 2014年10月17日 for BUG #1626 start
			mSelectAllCheckBox.setOnCheckedChangeListener(null);
			if (mDeleteDataList.size() == mAllDBDataList.size()) {
				mSelectAllTextView.setText(R.string.putao_cancle_all_choose);
				mSelectAllCheckBox.setChecked(true);
			} else {
				mSelectAllTextView.setText(R.string.putao_all_choose);
				mSelectAllCheckBox.setChecked(false);
			}
			mSelectAllCheckBox.setOnCheckedChangeListener(this);
			// add by putao_lhq 2014年10月17日 for BUG #1626 end
			mAllDataAdapter.setSelected(position, !vHollder.selectCheckBox.isChecked());
		}else{
			YelloPageItem item = null;
	        if (isSelectFavorite) {
	        	item = mAllDataList.get(position);
	        } else {
	        	item = mAllDataList.get(position);
	        }
	        long itemId = mAllDBDataList.get(position).getItemId();
	        try {
	              Intent intent = new Intent(YellowPageMyFavoriteHistoryActivity.this, YellowPageShopDetailActivity.class);
	              intent.putExtra("YelloPageItem", item);
	              intent.putExtra("ItemId", itemId);
	              startActivityForResult(intent, RESULT_SHOP_DETAIL_CODE);
	        } catch (Exception e) {
	        }	
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if( data == null ){
			return;
		}
		if( requestCode == RESULT_SHOP_DETAIL_CODE && resultCode == RESULT_OK){
            int collectResult = data.getIntExtra("COLLECT_RESULT", -1);
            if( collectResult == 1 ){
                  // 进入黄页详情没有改变 收藏结果
                  // isNeedRefresAllData = false;
            }else{
                  // 进入黄页详情改变了 收藏结果
                  needRefresAllData = true;
            }
      }
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		if( isDeleteMode ){
			return true;
		}
		isDeleteMode = true;
		mDeleteLayout.setVisibility(View.VISIBLE);
		mAllDataAdapter.setDeleteMode(true);
		return false;
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if( isDeleteMode ){
			/**add by ls for bug# 3097*/
			if(mDeleteDataList!=null){
				mDeleteDataList.clear();
			}
			exitDeleteMode();
		}else{
			super.onBackPressed();
		}
		
	}
	
	/**
	 * 退出删除模式
	 */
	private void exitDeleteMode(){
		mDeleteLayout.setVisibility(View.GONE);
		mAllDataAdapter.setDeleteMode(false);
		isDeleteMode = false;
	}

	/**
	 * 删除选中记录
	 */
	private class DeleteTask extends AsyncTask<Object, Object, Object> {
		@Override
		protected void onPreExecute() {
			mHandler.sendEmptyMessage(MSG_SHOW_PROGRESS_ACTION);
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		protected Void doInBackground(Object... params) {
			if (null == mDeleteDataList) {
				return null;
			}
			for (YellowPageCollectData ypi : mDeleteDataList) {
				mYellowPageDB.delCollectData(ypi.getItemId(), ypi.getType(), ypi.getName());
			}
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			LogUtil.i(TAG, "delete favoritehistory item size: " + mDeleteDataList.size());
			mHandler.sendEmptyMessage(MSG_HIDE_PROGRESS_ACTION);
			mDeleteDataList.clear();
			refreshData();
		}
	}

	private Toast mToast;
	private void showToast(int stringId) {
		if (null == mToast) {
			mToast = Toast.makeText(this, stringId, Toast.LENGTH_SHORT);
		} else {
			mToast.setText(stringId);
			mToast.setDuration(Toast.LENGTH_SHORT);
		}
		mToast.show();
	}

	@Override
	public void onClick(View arg0) {
		int id = arg0.getId();
		if (id == R.id.back_layout) {
			finish();
		} else if (id == R.id.delete_button) {
			if (null == mDeleteDataList || mDeleteDataList.size() == 0) {
				if( isSelectFavorite ){
					showToast(R.string.putao_yellow_page_favorite_no_selected);
				}else{
					showToast(R.string.putao_yellow_page_history_no_selected);
				}
				return;
			}
			if (mDeleteDialog == null) {
				mDeleteDialog = CommonDialogFactory.getOkCancelCommonDialog(this);
			}
			mDeleteDialog.setTitle(R.string.putao_yellow_page_favorite_delete_dialog_title);
			TextView msgTv = mDeleteDialog.getMessageTextView();
			if (isSelectFavorite) {
				msgTv.setText(R.string.putao_yellow_page_favorite_delete_confirm);
			} else{
				msgTv.setText(R.string.putao_yellow_page_history_delete_confirm);
			}
			mDeleteDialog.setOkButtonClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mDeleteDialog.dismiss();
					exitDeleteMode();
					new DeleteTask().execute();
					// add by putao_lhq 2014年10月23日 for 发现-我的-通过长按删除数 start
					MobclickAgentUtil.onEvent(YellowPageMyFavoriteHistoryActivity.this, 
							UMengEventIds.DISCOVER_YELLOWPAGE_MY_LONGCLICK_DELETE);
					// add by putao_lhq 2014年10月23日 for 发现-我的-通过长按删除数 end
				}
			});
			mDeleteDialog.show();
		} else {
		}
	}
	
	

	@SuppressWarnings("rawtypes")
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		mDeleteDataList.clear();//putao_lhq add for BUG #1626 start
		for (YelloPageItem item : mAllDataList) {
			item.getData().setSelected(isChecked);
		}
		if (isChecked) {
			mDeleteDataList.addAll(mAllDBDataList);
			mSelectAllTextView.setText(R.string.putao_cancle_all_choose);
		} else {
			mDeleteDataList.clear();
			mSelectAllTextView.setText(R.string.putao_all_choose);
		}
		mAllDataAdapter.setData(mAllDataList);
	}

	
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgentUtil.onPause(this);
		try {
			int time = ((int) ((System.currentTimeMillis() - startTime) / 1000));
			Map<String, String> map_value = new HashMap<String, String>();
			map_value.put("type", this.getClass().getName());
//			com.putao.analytics.MobclickAgentUtil.onEventValue(this, UMengEventIds.DISCOVER_YELLOWPAGE_RESUME_TIME_60, map_value,
//                    time);
			MobclickAgentUtil.onEventValue(this, UMengEventIds.DISCOVER_YELLOWPAGE_RESUME_TIME_60, map_value,
					time);
		} catch (Exception e) {
		}
	}

	@Override
	protected void onDestroy() {
		if (mImageLoader != null) {
			mImageLoader.clearCache();
		}
		super.onDestroy();
	}

    @Override
    public Integer remindCode() {
        return RemindConfig.MyHistory;
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
		return false;
	}   

}
