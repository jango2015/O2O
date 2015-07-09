package so.contacts.hub.adapter;

import so.contacts.hub.core.Config;
import so.contacts.hub.util.YellowUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import so.contacts.hub.util.ContactsHubUtils;
import android.text.TextUtils;
import android.widget.ImageView;

import com.loader.DataLoader;
import com.loader.image.ImageLoaderFactory;
import com.yulong.android.contacts.discover.R;

import so.contacts.hub.ui.yellowpage.bean.CategoryBean;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 自定义编辑服务-全部 部分 适配器
 * "全部"里面按照CategoryBean的 last_sort排序
 */
public class ServerManagerAllAdapter extends BaseAdapter {
	private Context mContext;
	public List<CategoryBean> mCategoryList;
	
	/** 是否可见 */
	boolean isVisible = true;
	
	/** 要添加的位置 */
	private int mAddPosition = -1;
	
	/** 要删除的position */
	private int mRemovePosition = -1;
	
	private DataLoader mDataLoader = null;

	public ServerManagerAllAdapter(Context context, List<CategoryBean> categoryList) {
		this.mContext = context;
		if( categoryList == null ){
			categoryList = new ArrayList<CategoryBean>();
		}
		this.mCategoryList = categoryList;
		mDataLoader = new ImageLoaderFactory(mContext).getNormalLoader(true, false);
	}

	@Override
	public int getCount() {
		return mCategoryList == null ? 0 : mCategoryList.size();
	}

	@Override
	public CategoryBean getItem(int position) {
		if (mCategoryList != null && mCategoryList.size() != 0) {
			return mCategoryList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.putao_yellow_page_server_manager_allitem, null);
            holder.nameTView = (TextView) convertView.findViewById(R.id.allitem_name);
            holder.imgView = (ImageView) convertView.findViewById(R.id.allitem_img);
            holder.tagImgView = (ImageView) convertView.findViewById(R.id.allitem_tagimg);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }
		CategoryBean categoryBean = getItem(position);
		
		if( categoryBean != null ){
			String iconlogo = categoryBean.getIconLogo();
			if(!TextUtils.isEmpty(iconlogo)){
			    //modity by ljq 2014-10-15 start
	            //如果类型不是网络地址 则取本地图片
                if (!ContactsHubUtils.isURlStr(iconlogo)) {
                    int iconResourceId = mContext.getResources().getIdentifier(iconlogo,
                            "drawable", mContext.getPackageName());
                    if (iconResourceId != 0) {
                        holder.imgView.setImageResource(iconResourceId);
                        holder.imgView.setVisibility(View.VISIBLE);
                    } else {
                        holder.imgView.setVisibility(View.INVISIBLE);
                    }
                } else if (ContactsHubUtils.isURlStr(iconlogo)) {
                    if (mDataLoader != null) {
                        //类型是网络地址 则去取缓存  没有会去下载
                        Bitmap bitmap = mDataLoader.loadDataReturnBitmap(iconlogo,
                                holder.imgView);
                        if (bitmap != null) {
                            holder.imgView.setImageBitmap(bitmap);
                        } else {
                            //贴上默认图片
                            int iconResourceId = mContext.getResources().getIdentifier(
                                    Config.DEFAULT_CATEGORY_IMAGE_SMALL, "drawable",
                                    mContext.getPackageName());
                            holder.imgView.setImageResource(iconResourceId);
                        }
                    }
                }
                //modity by ljq 2014-10-15 end
            }
            String name = ContactsHubUtils.getShowName(mContext, categoryBean.getShow_name());
            holder.nameTView.setText(name);
        }
        setLayoutDisable(holder, false);

		if( position == mAddPosition && !isVisible){
			// 新添加进来的（添加位置）且动画还未结束，设置不可见
			setLayoutDisable(holder, true);
		}
		if(mRemovePosition == position){
			// 为删除的项
			setLayoutDisable(holder, true);
		}
		return convertView;
	}
	
	/**
	 * 被删前 或者 添加前设置 不可见
	 */
	private void setLayoutDisable(ViewHolder holder, boolean isDisable) {
        if (isDisable) {
        	holder.nameTView.setVisibility(View.INVISIBLE);
        	holder.imgView.setVisibility(View.INVISIBLE);
        	holder.tagImgView.setVisibility(View.INVISIBLE);
        } else {
        	holder.nameTView.setVisibility(View.VISIBLE);
        	holder.imgView.setVisibility(View.VISIBLE);
        	holder.tagImgView.setVisibility(View.VISIBLE);
        }
    }
	
	/** 获取频道列表 */
	public List<CategoryBean> getCategoryLst() {
		return mCategoryList;
	}
	
	/** 添加频道列表 */
	public void addItem(CategoryBean categoryBean, int pos) {
		if( pos == -1 ){
			// 添加到末尾位置
			pos = mCategoryList.size();
		}
		
		mAddPosition = pos;
		categoryBean.setParent_id(YellowUtil.YELLOW_PAGE_CATEGORY_ID_ALL);
		mCategoryList.add(pos, categoryBean);
		notifyDataSetChanged();
	}
	
	/** 获取需要添加的position */
	public int getAddedPostion(){
		return mAddPosition;
	}

	/** 设置删除的position */
	public void setRemove(int position) {
		mRemovePosition = position;
		notifyDataSetChanged();
	}

	/** 删除频道列表 */
	public boolean remove() {
		if( mRemovePosition == -1 ){
			return false;
		}
		mCategoryList.remove(mRemovePosition);
		mRemovePosition = -1;
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
	
	/** 设置是否可见 */
	public void setVisible(boolean visible) {
		if( visible ){
			mAddPosition = -1;
		}
		isVisible = visible;
	}
	
	/**  根据sort 获取正确的顺序 */
	public int getCurrentIndex(int lastSort){
		if( mCategoryList == null ){
			return -1;
		}
		for(int i = 0; i < mCategoryList.size(); i++){
			if( lastSort <= mCategoryList.get(i).getLastSort() ){
				return i;
			}
		}
		return -1;
	}
	
	private class ViewHolder {
        public TextView nameTView;
        public ImageView imgView;
        public ImageView tagImgView;
    }
}
