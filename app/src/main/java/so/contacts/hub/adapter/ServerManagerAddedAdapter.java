package so.contacts.hub.adapter;

import java.util.ArrayList;
import java.util.List;
import so.contacts.hub.core.Config;
import so.contacts.hub.ui.yellowpage.bean.CategoryBean;
import so.contacts.hub.util.ContactsHubUtils;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.MyCenterConstant;
import so.contacts.hub.util.UMengEventIds;
import so.contacts.hub.util.YellowUtil;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.StateListDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.loader.DataLoader;
import com.loader.image.ImageLoaderFactory;
import so.contacts.hub.util.MobclickAgentUtil;
import com.yulong.android.contacts.discover.R;

/**
 * 自定义编辑服务-添加 部分 适配器
 */
public class ServerManagerAddedAdapter extends BaseAdapter {
	/** TAG */
	private final static String TAG = "ServerManagerAddedAdapter";
	
	/** 是否显示底部的ITEM */
	private boolean isItemShow = false;
	
	private Context mContext;
	
	/** 控制的postion */
	private int holdPosition;
	
	/** 是否改变 */
	private boolean isChanged = false;
	
	/** 列表数据是否改变 */
	private boolean isListChanged = false;
	
	/** 是否可见 */
	boolean isVisible = true;
	
	public List<CategoryBean> mCategoryList;
	
	/** 要删除的position */
	public int remove_position = -1;
	
	private DataLoader mDataLoader = null;

	public ServerManagerAddedAdapter(Context context, List<CategoryBean> categoryList) {
		this.mContext = context;
		if( categoryList == null ){
			categoryList = new ArrayList<CategoryBean>();
		}
		this.mCategoryList = categoryList;
		mDataLoader = new ImageLoaderFactory(mContext).getNormalLoader(true, false);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mCategoryList == null ? 0 : mCategoryList.size();
	}

	@Override
	public CategoryBean getItem(int position) {
		// TODO Auto-generated method stub
		if (mCategoryList != null && mCategoryList.size() != 0) {
			return mCategoryList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if( convertView == null ){
			convertView = LayoutInflater.from(mContext).inflate(R.layout.putao_yellow_page_server_manager_addeditem, null);
		}
		TextView item_text = (TextView) convertView.findViewById(R.id.added_item_name);
		ImageView imgView = (ImageView) convertView.findViewById(R.id.added_item_img);
		ImageView tagImgView = (ImageView) convertView.findViewById(R.id.added_item_tagimg);
		CategoryBean categoryBean = getItem(position);
		
		if(categoryBean != null){
			String name = ContactsHubUtils.getShowName(mContext, categoryBean.getShow_name());
			item_text.setText(name);
	          
			String icon = categoryBean.getIcon();
			String pressIcon = categoryBean.getPressIcon();
			//modity by ljq 2014-10-15 start
			//如果类型不是网络地址 则取本地图片
			//LogUtil.d(TAG, "icon 11: " + icon + "  name " + categoryBean.getName() + " sort " + categoryBean.getSort());
            if(!ContactsHubUtils.isURlStr(icon) && !ContactsHubUtils.isURlStr(pressIcon)){
                int normol = getResourceIdByName(icon);
                int pressed = getResourceIdByName(pressIcon);
                StateListDrawable stateListDrawable = ContactsHubUtils.addStateDrawable(mContext, normol, pressed,categoryBean);
                imgView.setImageDrawable(stateListDrawable);
            }else if(ContactsHubUtils.isURlStr(icon) && ContactsHubUtils.isURlStr(pressIcon)){
                Bitmap iconBitmap = null;
                Bitmap iconPassBitmap = null;
                if(mDataLoader != null){
                    //类型是网络地址 则去取缓存  没有会去下载
                    iconPassBitmap = mDataLoader.loadDataReturnBitmap(pressIcon, imgView);
                    iconBitmap = mDataLoader.loadDataReturnBitmap(icon, imgView);
                    if(iconBitmap != null && iconPassBitmap != null){
                        StateListDrawable stateListDrawable = ContactsHubUtils.addStateDrawable(mContext, iconBitmap, iconPassBitmap);
                        imgView.setImageDrawable(stateListDrawable);
                    }else{
                        //贴上默认图片
                        int normol = getResourceIdByName(Config.DEFAULT_CATEGORY_IMAGE);
                        int pressed = getResourceIdByName(Config.DEFAULT_CATEGORY_IMAGE_DEEP);
                        StateListDrawable stateListDrawable = ContactsHubUtils.addStateDrawable(mContext, normol, pressed,categoryBean);
                        imgView.setImageDrawable(stateListDrawable);
                    }
                }
            }
            //modity by ljq 2014-10-15 end

			if( categoryBean.getEditType() == YellowUtil.YELLOW_CATEGORY_EDITTYPE_NOT_DEL ){
				// 为不可删除
				tagImgView.setImageResource(R.drawable.putao_icon_edit_s_p);
			}else{
				tagImgView.setImageResource(R.drawable.putao_icon_edit_s);
			}
		}
		setViewDisable(convertView, false);
		
		if (isChanged && (position == holdPosition) && !isItemShow) {
			//在拖动时，拖动的View在GridView上的位置不可见
			setViewDisable(convertView, true);
			isChanged = false;
		}
		if( position == mCategoryList.size() - 1 && !isVisible){
			// 新添加进来的（最后一个）且 动画还未结束，设置不可见
			setViewDisable(convertView, true);
		}
		if (remove_position == position) {
			// 为删除的项
			setViewDisable(convertView, true);
		}
		return convertView;
	}
	
	private void setViewDisable(View view, boolean isDisable){
		if( isDisable ){
			view.setVisibility(View.INVISIBLE);
		}else{
			view.setVisibility(View.VISIBLE);
		}
	}

	/** 添加频道列表 */
	public void addItem(CategoryBean categoryBean, boolean needNotify) {
		int len = mCategoryList.size();
		if( len > 0 ){
		    String targetActivity = categoryBean.getTarget_activity();
            if( MyCenterConstant.MY_NODE.equals(targetActivity) ){//add ljq 2014-10-17 modify
                //现在为了避免服务器推送SORT_Id大于“我的” 所以这里不对“我的”SORT_Id 进行修改
            }else{
                CategoryBean categoryBeanTemp = mCategoryList.get(len - 1);
                if( categoryBeanTemp != null ){
                    // 如果有数据，则将新添加的数据的sort + 1
                    categoryBean.setSort(categoryBeanTemp.getSort() + 1);
                }
            }
		}
		categoryBean.setParent_id(YellowUtil.YELLOW_PAGE_CATEGORY_ID_OFFEN);
		mCategoryList.add(categoryBean);
		isListChanged = true;
		if( needNotify ){
			notifyDataSetChanged();
		}
	}

	/** 拖动变更频道排序 */
	public void exchange(int dragPostion, int dropPostion) {
		MobclickAgentUtil.onEvent(mContext, UMengEventIds.DISCOVER_YELLOWPAGE_MY_SERVER_MANAGER_DRAG);
		Log.d(TAG, "startPostion=" + dragPostion + " ,endPosition=" + dropPostion);
		holdPosition = dropPostion;
		CategoryBean dragItem = getItem(dragPostion);
        //add ljq start 2014/10/13 标记用户改变行为
		dragItem.setChange_type(YellowUtil.YELLOW_CATEGORY_CHANGE_TYPE_USER_MODITY);
        //add ljq end
		if (dragPostion < dropPostion) {
			mCategoryList.add(dropPostion + 1, dragItem);
			mCategoryList.remove(dragPostion);
		} else {
			mCategoryList.add(dropPostion, dragItem);
			mCategoryList.remove(dragPostion + 1);
		}
		verifyListSort();
		isChanged = true;
		isListChanged = true;
		notifyDataSetChanged();
	}
	
	/** 拖动位置后重新设置所有排序 */
	private void verifyListSort(){
		if( mCategoryList == null ){
			return;
		}
		for(int i = 0; i < mCategoryList.size(); i++){
			mCategoryList.get(i).setSort(i);
		}
	}

	/** 获取频道列表 */
	public List<CategoryBean> getCategoryLst() {
		return mCategoryList;
	}

	/** 设置删除的position */
	public void setRemove(int position) {
		remove_position = position;
		notifyDataSetChanged();
	}

	/** 删除频道列表 */
	public boolean remove() {
		if( remove_position == -1 ){
			return false;
		}
		mCategoryList.remove(remove_position);
		remove_position = -1;
		isListChanged = true;
		notifyDataSetChanged();
		return true;
	}

	/** 设置频道列表 */
	public void setListData(List<CategoryBean> list) {
		mCategoryList = list;
	}

	/** 获取是否可见 */
	public boolean isVisible() {
		return isVisible;
	}

	
	/** 排序是否发生改变 */
	public boolean isListChanged() {
		return isListChanged;
	}

	/** 设置是否可见 */
	public void setVisible(boolean visible) {
		isVisible = visible;
	}

	/** 显示放下的ITEM */
	public void setShowDropItem(boolean show) {
		isItemShow = show;
	}
	
    private int getResourceIdByName(String iconName){
        int iconResourceId = 0;
        if(!TextUtils.isEmpty(iconName)){
            iconResourceId = mContext.getResources().getIdentifier(iconName, "drawable", mContext.getPackageName());//2130837744
        }
        if(iconResourceId <= 0){
            iconResourceId = R.drawable.putao_icon_quick_replace;
        }
        return iconResourceId;
    }

}
