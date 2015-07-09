package so.contacts.hub.adapter;

import java.util.ArrayList;
import java.util.List;

import so.contacts.hub.ui.yellowpage.bean.PuTaoResultItem;
import so.contacts.hub.util.LogUtil;
import so.putao.findplug.SourceItemObject;
import so.putao.findplug.YelloPageItem;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loader.DataLoader;
import com.yulong.android.contacts.discover.R;

public class YellowPageFavoriteListAdapter extends BaseAdapter {

	protected Context mContext = null;
	private LayoutInflater mInflater = null;
	private List<YelloPageItem> mDataList = new ArrayList<YelloPageItem>();
	private DataLoader mImageLoader = null;

	private boolean isDeleteMode;
	
	public YellowPageFavoriteListAdapter(Context context,
			List<YelloPageItem> itemList, DataLoader imageLoader) {
		this.mContext = context;
		this.mInflater = LayoutInflater.from(mContext);
		this.mDataList = itemList;
		this.mImageLoader = imageLoader;
	}
	
	public void setData(List<YelloPageItem> mDataList) {
		this.mDataList = mDataList;
		notifyDataSetChanged();
	}
	
	public void setSelected(int postion, boolean isSelected){
		if( mDataList == null ){
			return;
		}
		if( postion >= mDataList.size() ){
			return;
		}
		mDataList.get(postion).getData().setSelected(isSelected);
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		if (mDataList == null) {
			return 0;
		} else {
			LogUtil.i("count",
					"count1 :" + String.valueOf(mDataList.size()));
			return mDataList.size();
		}
	}

	@Override
	public Object getItem(int position) {
		if (mDataList == null) {
			return null;
		} else {
			return mDataList.get(position);
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		YelloPageItem item = mDataList.get(position);
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(
					R.layout.putao_yellow_page_favorite_list_item, null);
			holder = new ViewHolder();
			/**
			 * delete code by putao_lhq
			holder.divider = convertView.findViewById(R.id.divider_view);
			holder.divider_bottom = convertView.findViewById(R.id.divider_view_bottom);*/
			
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.itemImg = (ImageView) convertView
					.findViewById(R.id.item_img);

			holder.itemLayout = (RelativeLayout) convertView
					.findViewById(R.id.item_layout);

			holder.region = (TextView) convertView.findViewById(R.id.region);
			holder.deleteLayout = (RelativeLayout) convertView
					.findViewById(R.id.delete_parent_layout);
			holder.selectCheckBox = (CheckBox) convertView
					.findViewById(R.id.express_history_check_box);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.name.setText(item.getName());
		
		SourceItemObject itemData = item.getData();
		String photoUrl = item.getPhotoUrl();
		String localPhotoUrl = itemData.getLocalPhotoUrl();
        if (TextUtils.isEmpty(photoUrl) || photoUrl.endsWith("no_photo_278.png")){
        	photoUrl = itemData.getDefaultPhotoUrl();
        	if( TextUtils.isEmpty(photoUrl) ){
        		mImageLoader.loadData(R.drawable.putao_icon_logo_placeholder, holder.itemImg);
        	} else{
        		mImageLoader.loadData(mContext.getResources().getIdentifier(photoUrl, "drawable", 
        				mContext.getPackageName()), holder.itemImg);
        	}
		} else{
			if (TextUtils.isEmpty(localPhotoUrl)) {
				if (itemData instanceof PuTaoResultItem) {
					mImageLoader.loadData(
							mContext.getResources().getIdentifier(photoUrl,
									"drawable", mContext.getPackageName()),
							holder.itemImg);
				} else {
					mImageLoader.loadData(photoUrl, holder.itemImg);
				}
			}else{
				mImageLoader.loadData(mContext.getResources().getIdentifier(localPhotoUrl, "drawable", 
        				mContext.getPackageName()), holder.itemImg);
			}
		}	
        
		if (isDeleteMode) {
			holder.deleteLayout.setVisibility(View.VISIBLE);
			holder.selectCheckBox.setChecked(itemData.isSelected());
		} else {
			holder.deleteLayout.setVisibility(View.GONE);
		}

		if (!TextUtils.isEmpty(item.getAddress())) {
			holder.region.setVisibility(View.VISIBLE);
			holder.region.setText(item.getAddress());
		} else {
			if (item.getNumbers() != null && item.getNumbers().size() > 0) {
				holder.region.setVisibility(View.VISIBLE);
				holder.region.setText((CharSequence) item.getNumbers().get(0));
			} else {
				holder.region.setVisibility(View.GONE);
			}
		}
		
		/**
		 * delete code by putao_lhq
		if(position == getCount()-1){
		    holder.divider_bottom.setVisibility(View.VISIBLE);
		}else{
		    holder.divider_bottom.setVisibility(View.GONE);
		}*/
		
		
		return convertView;
	}

	public DataLoader getmImageLoader() {
		return mImageLoader;
	}

	public void setmImageLoader(DataLoader mImageLoader) {
		this.mImageLoader = mImageLoader;
	}

	public boolean isDeleteMode() {
		return isDeleteMode;
	}

	public void setDeleteMode(boolean isDeleteMode) {
		this.isDeleteMode = isDeleteMode;
        if (!isDeleteMode && !mDataList.isEmpty()) {
            for(YelloPageItem yp : mDataList){
                try {
                    yp.getData().setSelected(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
		notifyDataSetChanged();
	}

	public static class ViewHolder {
		private TextView name;
		private TextView region;
		private ImageView itemImg;
		private View divider;
		private View divider_bottom;

		private RelativeLayout itemLayout;

		protected RelativeLayout deleteLayout;
		public CheckBox selectCheckBox;
	}

}
