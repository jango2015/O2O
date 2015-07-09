package so.contacts.hub.adapter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import so.contacts.hub.ui.yellowpage.bean.PuTaoResultItem;
import so.contacts.hub.ui.yellowpage.bean.SearchItemImage;
import so.contacts.hub.ui.yellowpage.bean.SearchItemLocalImage;
import so.contacts.hub.util.ContactsHubUtils;
import so.contacts.hub.util.LogUtil;
import so.putao.findplug.YelloPageItem;
import so.putao.findplug.YelloPageItemNumber;
import so.putao.findplug.YellowPageItemDianping;
import so.putao.findplug.YellowPageItemPutao;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.loader.DataLoader;
import com.yulong.android.contacts.discover.R;

public class YellowPageAdapter extends CustomListViewAdapter {

	private static final String TAG = "YellowPageAdapter";
	
	protected Context mContext;
	private LayoutInflater mInflater;
	private List<YelloPageItem> mPageItemList = new ArrayList<YelloPageItem>();//modify by putao_lhq for BUG #1725
	private DataLoader mImageLoader;
	private SearchItemImage mItemImage;
	private Map<ImageView,Bitmap> viewMap = new HashMap<ImageView, Bitmap>();
	
	/**
	 * ��ʾͼƬ����ʽ
	 */
	// ����ͼƬ ��Բ��
	private static final int LOAD_IMG_TYPE_NATIVE_WITH_CORNER = 1;
	// ����ͼƬ ����Բ��
	private static final int LOAD_IMG_TYPE_NATIVE_NOT_CORNER = 2;
	// Ĭ��ͼƬ ��Բ��
	private static final int LOAD_IMG_TYPE_DEFAULT_WITH_CORNER = 3;
	// ����ͼƬ
	private static final int LOAD_IMG_TYPE_NETWORK = 4;
	
	public YellowPageAdapter(Context context,List<YelloPageItem> itemList,DataLoader imageLoader, SearchItemImage itemImage) {
		this.mContext = context;
		this.mInflater = LayoutInflater.from(mContext);
		//modify by putao_lhq for BUG #1725 start
		//this.mPageItemList = itemList;
		this.mPageItemList.clear();
		this.mPageItemList.addAll(itemList);
		//modify by putao_lhq for BUG #1725 end
		this.mImageLoader = imageLoader;
		this.mItemImage = itemImage;
	}

	@Override
	public int getCount() {
		if (mPageItemList == null) {
			return 0;
		} else {
			return mPageItemList.size();
		}
	}

	@Override
	public Object getItem(int position) {
		if (mPageItemList == null) {
			return null;
		} else {
			return mPageItemList.get(position);
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		YelloPageItem item = mPageItemList.get(position);
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(
					R.layout.putao_yellow_page_search_list_item, null);
			holder = new ViewHolder();
			holder.divider = convertView.findViewById(R.id.divider_view);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.distance = (TextView) convertView
					.findViewById(R.id.distance);
			holder.itemImg = (ImageView) convertView
					.findViewById(R.id.item_img);
			
			holder.starLayout = (RatingBar) convertView
					.findViewById(R.id.star_layout);
			holder.verticalLineView = convertView.findViewById(R.id.vertical_line_view);
			holder.itemLayout = (RelativeLayout) convertView.findViewById(R.id.item_layout);
			
			holder.region = (TextView) convertView.findViewById(R.id.region);
			holder.additinalDeal = (ImageView) convertView.findViewById(R.id.additional_tuan);
			holder.averagePrice = (TextView) convertView.findViewById(R.id.average_price);
			holder.regionLayout = (RelativeLayout) convertView.findViewById(R.id.region_layout);
			holder.starPriceLayout = (RelativeLayout) convertView.findViewById(R.id.star_price_layout);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		if (position == 0) {
			holder.divider.setVisibility(View.GONE);
		} else {
			holder.divider.setVisibility(View.VISIBLE);
		}

		String photoUrl = item.getPhotoUrl();
		String name = item.getName();
		String address = item.getAddress();
		int loadImgType = LOAD_IMG_TYPE_NATIVE_WITH_CORNER;
		holder.name.setText(name);
		if(item instanceof YellowPageItemDianping){
			RelativeLayout.LayoutParams imgParams = (RelativeLayout.LayoutParams) holder.itemImg.getLayoutParams();
			imgParams.height = mContext.getResources().getDimensionPixelSize(R.dimen.putao_listview_multirow_item_imgsize);
			imgParams.width = mContext.getResources().getDimensionPixelSize(R.dimen.putao_listview_multirow_item_imgsize);
			holder.itemImg.setLayoutParams(imgParams);
			loadImgType = LOAD_IMG_TYPE_NETWORK;
		} else if(item instanceof YellowPageItemPutao){
			PuTaoResultItem putaoItem = (PuTaoResultItem) item.getData();
			if( putaoItem.getSource_type() == PuTaoResultItem.SOURCE_TYPE_SERVER ){
				// ���Ѿ�̬���� Ӧ�÷������� ���ü�Բ��
				loadImgType = LOAD_IMG_TYPE_NATIVE_NOT_CORNER;
			}
		} else if (item instanceof YelloPageItemNumber) {
			if (item.getNumbers() != null && item.getNumbers().size() > 0) {
				holder.starPriceLayout.setVisibility(View.VISIBLE);
				holder.regionLayout.setVisibility(View.VISIBLE);
			}else{
				holder.starPriceLayout.setVisibility(View.GONE);
				holder.regionLayout.setVisibility(View.GONE);
			}
		} else {
			RelativeLayout.LayoutParams imgParams = (RelativeLayout.LayoutParams) holder.itemImg.getLayoutParams();
			imgParams.height = mContext.getResources().getDimensionPixelSize(R.dimen.putao_listview_item_imgsize);
			imgParams.width = mContext.getResources().getDimensionPixelSize(R.dimen.putao_listview_item_imgsize);
			holder.itemImg.setLayoutParams(imgParams);
			loadImgType = LOAD_IMG_TYPE_NETWORK;
		}
		if (TextUtils.isEmpty(photoUrl) || photoUrl.endsWith("no_photo_278.png")) { //У�鲻�Ϸ�ͼƬ
			loadImgType = LOAD_IMG_TYPE_DEFAULT_WITH_CORNER;
		}
		if( mItemImage == null ){
			if( loadImgType == LOAD_IMG_TYPE_NATIVE_WITH_CORNER ){
				int defaultResId = mContext.getResources().getIdentifier(photoUrl, "drawable", mContext.getPackageName());
		        int corner = mContext.getResources().getDimensionPixelSize(R.dimen.putao_image_round_corner);
		        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), defaultResId);
		        if(bitmap == null){
		        	bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.putao_icon_logo_placeholder);
		        }
		        Bitmap bitmap2 = ContactsHubUtils.corner(bitmap, corner, 0);
                holder.itemImg.setImageBitmap(bitmap2);
                viewMap.put(holder.itemImg, bitmap2);
			}else if( loadImgType == LOAD_IMG_TYPE_NATIVE_NOT_CORNER ){
				int defaultResId = mContext.getResources().getIdentifier(photoUrl, "drawable", mContext.getPackageName());
		        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), defaultResId);
                holder.itemImg.setImageBitmap(bitmap);
                viewMap.put(holder.itemImg, bitmap);
			}else if( loadImgType == LOAD_IMG_TYPE_DEFAULT_WITH_CORNER ){
				Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.putao_icon_logo_placeholder);
		        int corner = mContext.getResources().getDimensionPixelSize(R.dimen.putao_image_round_corner);
				holder.itemImg.setImageBitmap(ContactsHubUtils.corner(bitmap, corner, 0));
			}else if( loadImgType == LOAD_IMG_TYPE_NETWORK ){
				mImageLoader.loadData(photoUrl, holder.itemImg);
			}
		}else{
			holder.itemImg.setTag(position);
			setItemImage(holder.itemImg, item);
		}
		
		if(item.hasDeal()){
			holder.additinalDeal.setVisibility(View.VISIBLE);
		}else{
			holder.additinalDeal.setVisibility(View.GONE);
		}
		if(item.getDistance() > 0){
			holder.distance.setVisibility(View.VISIBLE);
			DecimalFormat df=new DecimalFormat("#");
			String distance = df.format(item.getDistance());
			if(item.getDistance() < 1000){
				holder.distance.setText(mContext.getString(R.string.putao_yellow_page_distance_meter, Integer.parseInt(distance)));
			}else if(item.getDistance() < 500000){
				String result = String.format("%.2f", Double.parseDouble(distance)/1000.0d);
				holder.distance.setText(mContext.getString(R.string.putao_yellow_page_distance_kilometer, result));
			}else{
				holder.distance.setVisibility(View.GONE);
			}
		}else{
			holder.distance.setVisibility(View.GONE);
		}
		
		if (item.getAvg_rating() > 0) {
			holder.starLayout.setVisibility(View.VISIBLE);
			float star = item.getAvg_rating();
			holder.starLayout.setRating(star);
		} else {
			holder.starLayout.setVisibility(View.GONE);
		}
		
		if(item.getAvgPrice() > 0){
			holder.averagePrice.setVisibility(View.VISIBLE);
			holder.averagePrice.setText(mContext.getString(R.string.putao_yellow_page_avr_price, item.getAvgPrice()));
		}else{
			holder.averagePrice.setVisibility(View.GONE);
		}
		
		if (item.getAvg_rating() > 0 && item.getAvgPrice() > 0) {
			holder.verticalLineView.setVisibility(View.VISIBLE);
		} else {
			holder.verticalLineView.setVisibility(View.GONE);
		}
		
		if(!TextUtils.isEmpty(address)){
			holder.region.setVisibility(View.VISIBLE);
			holder.region.setText(item.getAddress());
		}else {
			if (item.getNumbers() != null && item.getNumbers().size() > 0) {
				holder.region.setVisibility(View.VISIBLE);
				holder.region.setText((CharSequence) item.getNumbers().get(0));
			}else{
				holder.region.setVisibility(View.GONE);
			}
		}
		return convertView;
	}
	
	private void setItemImage(ImageView itemImage, YelloPageItem item){
		List<SearchItemLocalImage> localImageList = mItemImage.getLocalImages();
		int defaultResId = mContext.getResources().getIdentifier(mItemImage.getDefaultImg(), "drawable",mContext.getPackageName());
		String itemName = item.getName();
		int imageSize = mContext.getResources().getDimensionPixelSize(R.dimen.putao_listview_item_imgsize);
        int corner = mContext.getResources().getDimensionPixelSize(R.dimen.putao_image_round_corner);
		boolean hasLocalImage = false;
		if(localImageList != null && localImageList.size() > 0){
			for(SearchItemLocalImage localImage : localImageList){
				if(itemName.contains(localImage.getKeyword())){
					int iconResourceId = mContext.getResources().getIdentifier(localImage.getImgId(), "drawable",mContext.getPackageName());
		            Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), iconResourceId);
		            if (bitmap != null) {
		            	if(item instanceof YellowPageItemDianping){
		            		imageSize = mContext.getResources().getDimensionPixelSize(R.dimen.putao_listview_multirow_item_imgsize);
		            	}
		                Bitmap bitmap2 = ContactsHubUtils.corner(bitmap, corner, imageSize);
		                itemImage.setImageBitmap(bitmap2);
		                viewMap.put(itemImage, bitmap2);
		                hasLocalImage = true;
		                item.getData().setLocalPhotoUrl(localImage.getImgId());//putao_lhq add for BUG #1534
		            }
		            break;
				}
			}
			if(!hasLocalImage){
				if(mItemImage.isShowNetImg()){
				if (TextUtils.isEmpty(item.getPhotoUrl()) || item.getPhotoUrl().endsWith("no_photo_278.png")) {
					Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), defaultResId);
					if (bitmap != null) {
		            	if(item instanceof YellowPageItemDianping){
		            		imageSize = mContext.getResources().getDimensionPixelSize(R.dimen.putao_listview_multirow_item_imgsize);
		            	}
		                Bitmap bitmap2 = ContactsHubUtils.corner(bitmap, corner, imageSize);
		                itemImage.setImageBitmap(bitmap2);
		                viewMap.put(itemImage, bitmap2);
		            }
				} else {
					mImageLoader.loadData(item.getPhotoUrl(), itemImage);
				}
			}else{
				Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), defaultResId);
				if (bitmap != null) {
	            	if(item instanceof YellowPageItemDianping){
	            		imageSize = mContext.getResources().getDimensionPixelSize(R.dimen.putao_listview_multirow_item_imgsize);
	            	}
	                Bitmap bitmap2 = ContactsHubUtils.corner(bitmap, corner, imageSize);
	                itemImage.setImageBitmap(bitmap2);
	                viewMap.put(itemImage, bitmap2);
	            }
			}
			}
		}else{
			if(mItemImage.isShowNetImg()){
				if (TextUtils.isEmpty(item.getPhotoUrl()) || item.getPhotoUrl().endsWith("no_photo_278.png")) {
					Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), defaultResId);
					if (bitmap != null) {
		            	if(item instanceof YellowPageItemDianping){
		            		imageSize = mContext.getResources().getDimensionPixelSize(R.dimen.putao_listview_multirow_item_imgsize);
		            	}
		                Bitmap bitmap2 = ContactsHubUtils.corner(bitmap, corner, imageSize);
		                itemImage.setImageBitmap(bitmap2);
		                viewMap.put(itemImage, bitmap2);
		            }
				} else {
					mImageLoader.loadData(item.getPhotoUrl(), itemImage);
				}
			}else{
				Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), defaultResId);
				if (bitmap != null) {
	            	if(item instanceof YellowPageItemDianping){
	            		imageSize = mContext.getResources().getDimensionPixelSize(R.dimen.putao_listview_multirow_item_imgsize);
	            	}
	                Bitmap bitmap2 = ContactsHubUtils.corner(bitmap, corner, imageSize);
	                itemImage.setImageBitmap(bitmap2);
	                viewMap.put(itemImage, bitmap2);
	            }
			}
		}
	}
	
	public void setmPageItemList(List<YelloPageItem> pageItemList) {
		//modify by putao_lhq for BUG #1725 start
		//this.mPageItemList = mPageItemList;
		if (pageItemList == null) {
			LogUtil.v(TAG, "page list is null");
			pageItemList = new ArrayList<YelloPageItem>();
		}
		this.mPageItemList.clear();
		this.mPageItemList.addAll(pageItemList);
		notifyDataSetChanged();
		//modify by putao_lhq for BUG #1725 start
	}

	public DataLoader getmImageLoader() {
		return mImageLoader;
	}

	public void setmImageLoader(DataLoader mImageLoader) {
		this.mImageLoader = mImageLoader;
	}
	
	public synchronized void recyleAllImage(){
        for (Entry<ImageView, Bitmap> item: viewMap.entrySet()) {
            ImageView view = item.getKey();
            Bitmap bm = item.getValue();
            view.setImageBitmap(null);
            if(!bm.isRecycled()){
                bm.recycle();
            }
        }
        viewMap.clear();
    }
	
	public synchronized void recyleScrolledImage(int firstIndex,int lastIndex){
		for (Entry<ImageView, Bitmap> item: viewMap.entrySet()) {
            ImageView view = item.getKey();
            Bitmap bm = item.getValue();
            int position = (Integer) view.getTag();
            if(position > lastIndex || position < firstIndex){
	            view.setImageBitmap(null);
	            if(!bm.isRecycled()){
	                bm.recycle();
	            }
            }
        }
	}

	public static class ViewHolder {
		private TextView name;
		private TextView distance;
		private TextView region;
		private ImageView itemImg;
		private ImageView additinalDeal;
		private View divider;
		
		private RatingBar starLayout;
		private View verticalLineView;
		
		private TextView averagePrice;
		private RelativeLayout itemLayout;
		
		private RelativeLayout starPriceLayout;
		private RelativeLayout regionLayout;
	}

}
