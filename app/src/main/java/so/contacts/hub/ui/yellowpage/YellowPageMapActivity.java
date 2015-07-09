
package so.contacts.hub.ui.yellowpage;

import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import so.contacts.hub.ui.yellowpage.bean.GaoDePoiItem;
import so.contacts.hub.util.UMengEventIds;
import so.putao.findplug.LBSServiceGaode;
import so.putao.findplug.SourceItemObject;
import so.putao.findplug.YelloPageItem;
import so.putao.findplug.YellowPageELongItem;
import so.putao.findplug.YellowPageItemDianping;
import so.putao.findplug.YellowPageItemGaoDe;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapLoadedCallback;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.utils.CoordinateConverter;
import so.contacts.hub.util.MobclickAgentUtil;
import com.yulong.android.contacts.discover.R;

public class YellowPageMapActivity extends Activity implements OnClickListener {

    /**
     * MapView 是地图主控件
     */
    private MapView mMapView;

    private BaiduMap mBaiduMap;

    private TextView itemTitle;

    private TextView itemName;

    private RatingBar starRating;

    private TextView averagePrice;

    private LinearLayout detailLayout;

    private Button btnHomePage;

    private Button btnShowRoute;

    private View verticalDivider;

    private View verticalSecondDivider;

    private LinearLayout itemInfo;

    private YelloPageItem item;

    private ImageView nextStepImage;
    
    private ImageView imageTuan;
    
    private TextView distanceText;
    
    private BitmapDescriptor bitmap,pressedBitmap;
    
    String defaultLogo;
    
    private Marker prMarker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.putao_map_view_layout);
        initViews();
        
        detailLayout.setOnClickListener(this);
        btnHomePage.setOnClickListener(this);
        btnShowRoute.setOnClickListener(this);

        String title = getIntent().getStringExtra("title");
        itemTitle.setText(title);
        defaultLogo = getIntent().getStringExtra("defaultLogo");
        

        nextStepImage = (ImageView)findViewById(R.id.next_step_img);
        nextStepImage.setVisibility(View.VISIBLE);
        nextStepImage.setImageResource(R.drawable.putao_icon_list);
        
        findViewById(R.id.next_step_btn).setVisibility(View.VISIBLE);
        findViewById(R.id.next_setp_layout).setOnClickListener(this);
        findViewById(R.id.back_layout).setOnClickListener(this);
        
        mBaiduMap.setOnMapLoadedCallback(new OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                initOverlay();
            }
        });
        MobclickAgentUtil.onEvent(this, UMengEventIds.DISCOVER_YELLOWPAGE_NEAR_MAP_MODE);
    }
    
    private void initViews(){
        mMapView = (MapView)findViewById(R.id.mapView);
        mBaiduMap = mMapView.getMap();
        itemTitle = (TextView)findViewById(R.id.title);
        itemName = (TextView)findViewById(R.id.name);
        starRating = (RatingBar)findViewById(R.id.star_layout);
        averagePrice = (TextView)findViewById(R.id.average_price);
        detailLayout = (LinearLayout)findViewById(R.id.show_detail);
        verticalDivider = findViewById(R.id.vertical_line_view);
        verticalSecondDivider = findViewById(R.id.vertical_secondline_view);
        btnHomePage = (Button)findViewById(R.id.btn_home_page);
        btnShowRoute = (Button)findViewById(R.id.btn_show_route);
        itemInfo = (LinearLayout)findViewById(R.id.item_info);
        imageTuan = (ImageView) findViewById(R.id.additional_tuan);
        distanceText = (TextView) findViewById(R.id.distance);
    }
    

    @SuppressWarnings({
            "unchecked", "rawtypes"
    })
    public void initOverlay() {
        CoordinateConverter converter = new CoordinateConverter();// 坐标转换工具
        converter.from(CoordinateConverter.CoordType.COMMON);
        converter.coord(new LatLng(LBSServiceGaode.getLatitude(), LBSServiceGaode.getLongitude()));
        LatLng latLng = converter.convert();

        MyLocationData locData = new MyLocationData.Builder().accuracy(0).latitude(latLng.latitude)
                .longitude(latLng.longitude).build();
        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setMyLocationData(locData);// 我的位置
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(LocationMode.NORMAL, true, null));

        Intent intent = getIntent();
        ArrayList<YelloPageItem> searchResult = (ArrayList<YelloPageItem>)intent
                .getSerializableExtra("result_key");// 获取传过来的搜索结果
        if (searchResult != null && searchResult.size() > 0) {
            final List<Overlay> overlays = new ArrayList<Overlay>();
            bitmap = BitmapDescriptorFactory.fromResource(R.drawable.putao_icon_marker_red);
            pressedBitmap = BitmapDescriptorFactory.fromResource(R.drawable.putao_icon_marker_red_select);
            for (int i = 0; i < searchResult.size(); i++) {
                YelloPageItem item = searchResult.get(i);
                double latitude = 0;
                double longitude = 0;
                
                if (item instanceof YellowPageItemDianping) {// 大众点评
                    YellowPageItemDianping itemDianping = (YellowPageItemDianping)item;
                    latitude = itemDianping.getData().latitude;
                    longitude = itemDianping.getData().longitude;
                    
                    itemDianping.getData().setDefaultPhotoUrl(defaultLogo);
                    
                }else if(item instanceof YellowPageItemGaoDe){//高德地图的数据
                    YellowPageItemGaoDe itemGaoDe = (YellowPageItemGaoDe)item;
                    latitude = itemGaoDe.getData().getLatitude();
                    longitude = itemGaoDe.getData().getLongitude();
                    
                    itemGaoDe.getData().setDefaultPhotoUrl(defaultLogo);
                }else if(item instanceof YellowPageELongItem){//艺龙的数据
                	YellowPageELongItem elongItem = (YellowPageELongItem)item;
                    latitude = elongItem.getData().getLatitude();
                    longitude = elongItem.getData().getLongitude();
                    elongItem.getData().setDefaultPhotoUrl(defaultLogo);
                }
                
                if(latitude != 0 && longitude !=0){
                    converter.coord(new LatLng(latitude, longitude));
                    // 添加Poi点
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("YelloPageItem", item);
                    overlays.add(mBaiduMap.addOverlay(new MarkerOptions()
                            .position(converter.convert()).icon(bitmap).extraInfo(bundle)));
                }
                
            }
            if (overlays.size() > 0) {
                mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {// 点击Poi点
                        item = (YelloPageItem)marker.getExtraInfo().getSerializable("YelloPageItem");
                        itemInfo.setVisibility(View.VISIBLE);
                        initItemInfo();
                        if(prMarker != null){
                        	prMarker.setIcon(bitmap);
                        }
                        marker.setIcon(pressedBitmap);
                        prMarker = marker;
                        return true;
                    }
                });
                LatLngBounds.Builder localBuilder = new LatLngBounds.Builder();
                for (Overlay overlay : overlays) {
                    localBuilder.include(((Marker)overlay).getPosition());
                }
                mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngBounds(localBuilder.build()));
            }
        }
    }

    /**
     * 获取所选商户的网址信息
     */
    private String getWebAddress(){
    	if( item == null ){
    		return "";
    	}
    	String webAddr = item.getBusinessUrl();
    	if( !TextUtils.isEmpty(webAddr) ){
    		if( item instanceof YellowPageItemDianping ){
    			if (!webAddr.contains("hasheader")) {
    				// 大众点评的去掉title栏（隐藏顶部栏 0-隐藏 1-打开）
    				webAddr = webAddr + "?hasheader=0";
                }
    		}
    		return webAddr;
    	}
    	SourceItemObject obj = item.getData();
    	if( obj == null ){
    		return "";
    	}
    	if( obj instanceof GaoDePoiItem ){
    		webAddr = ((GaoDePoiItem)obj).getWebsite();
    	}
    	return webAddr;
    }
    
    private void initItemInfo() {
    	if( item == null ){
    		return;
    	}
    	if( TextUtils.isEmpty(getWebAddress()) ){
    		btnHomePage.setVisibility(View.GONE);
    	}else{
    		btnHomePage.setVisibility(View.VISIBLE);
    	}
        itemName.setText(item.getName());
        if (item.getAvg_rating() > 0) {
            starRating.setVisibility(View.VISIBLE);
            float star = item.getAvg_rating();
            starRating.setRating(star);
            verticalDivider.setVisibility(View.VISIBLE);
        } else {
            starRating.setVisibility(View.GONE);
            verticalDivider.setVisibility(View.GONE);
        }

        if (item.getAvgPrice() > 0) {
        	verticalSecondDivider.setVisibility(View.VISIBLE);
            averagePrice.setVisibility(View.VISIBLE);
            averagePrice.setText(getString(R.string.putao_yellow_page_avr_price, item.getAvgPrice()));
        } else {
        	verticalSecondDivider.setVisibility(View.GONE);
            averagePrice.setVisibility(View.GONE);
        }

        if(item.hasDeal()){
			imageTuan.setVisibility(View.VISIBLE);
		}else{
			imageTuan.setVisibility(View.GONE);
		}
        
        if(item.getDistance() > 0){
			distanceText.setVisibility(View.VISIBLE);
			DecimalFormat df=new DecimalFormat("#");
			String distance = df.format(item.getDistance());
			if(item.getDistance() < 1000){
				distanceText.setText(getString(R.string.putao_yellow_page_distance_meter,Integer.parseInt(distance)));
			}else if(item.getDistance() < 500000){
				String result = String.format("%.2f", Double.parseDouble(distance)/1000.0d);
				distanceText.setText(getString(R.string.putao_yellow_page_distance_kilometer, result));
			}
		}else{
			distanceText.setVisibility(View.GONE);
		}
    }

    /** 获取"到这里去"的intent */
    private Intent getGoHereIntent(double latitude,double longtitude,String name) {//YellowPageItemDianping item
        Intent intent = null;
        List<ResolveInfo> list = null;

        CoordinateConverter converter = new CoordinateConverter();// 坐标转换工具
        converter.from(CoordinateConverter.CoordType.COMMON);
        converter.coord(new LatLng(LBSServiceGaode.getLatitude(), LBSServiceGaode.getLongitude()));
        LatLng baiduFrom = converter.convert();

        converter.coord(new LatLng(latitude, longtitude));
        LatLng baiduTo = converter.convert();

        try {// 百度地图intent
            intent = Intent.parseUri("intent://map/direction?origin=latlng:" + baiduFrom.latitude
                    + "," + baiduFrom.longitude + "|name:我的位置&destination=latlng:"
                    + baiduTo.latitude + "," + baiduTo.longitude + "|name=" + name
                    + "&mode=driving#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end", 0);
            list = getPackageManager().queryIntentActivities(intent, PackageManager.GET_ACTIVITIES);
        } catch (URISyntaxException e) {
            Log.i("map", "catch URISyntaxException.");
            e.printStackTrace();
        }

        if (list == null || list.size() == 0) {
            Log.i("map", "baidu map app not found or version too old.");
            intent = new Intent(Intent.ACTION_VIEW);// 高德地图intent
            Uri uri = Uri.parse("androidamap://route?sourceApplication=yellowpage&slat="
                    + LBSServiceGaode.getLatitude() + "&slon=" + LBSServiceGaode.getLongitude()
                    + "&sname=我的位置&dlat=" + latitude + "&dlon="
                    + longtitude + "&dname=" + name
                    + "&dev=0&m=0&t=2&showType=1");
            intent.setData(uri);
            list = getPackageManager().queryIntentActivities(intent, PackageManager.GET_ACTIVITIES);
            if (list == null || list.size() == 0) {// 百度地图网页版
                Log.i("map", "gaode map app not found or version too old.");
                String uriStr = "http://api.map.baidu.com/direction?origin=name:我的位置|latlng:"
                        + baiduFrom.latitude + "," + baiduFrom.longitude + "&destination=name:"
                        + item.getName() + "|latlng:" + baiduTo.latitude + "," + baiduTo.longitude
                        + "&region=" + LBSServiceGaode.getCity() + "&mode=driving&output=html";
                intent.setData(Uri.parse(uriStr));
            }
        }
        return intent;
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
        MobclickAgentUtil.onPause(this);
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
        MobclickAgentUtil.onResume(this);
    }

    @Override
    protected void onDestroy() {
     	// 关闭定位图层
     	mBaiduMap.setMyLocationEnabled(false);
     	mMapView.onDestroy();
     	mMapView = null;
     	// 回收 bitmap 资源
     	if(bitmap != null){
     		bitmap.recycle();
     		bitmap = null;
     	}
        super.onDestroy();
    }
    
    @Override
    public void onBackPressed() {
    	// TODO Auto-generated method stub
    	if( itemInfo.getVisibility() == View.VISIBLE ){
    		itemInfo.setVisibility(View.GONE);
    		return;
    	}
    	super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
		if (id == R.id.back_layout) {
			finish();
		} else if (id == R.id.next_setp_layout) {
			finish();
		} else if (id == R.id.btn_show_route) {
			double latitude = 0;
			double longtitude = 0;
			String name = item.getName();
			if(item instanceof YellowPageItemDianping){
			    YellowPageItemDianping itemDianping = (YellowPageItemDianping)item;
			    latitude = itemDianping.getData().latitude;
			    longtitude = itemDianping.getData().longitude;
			}else if(item instanceof YellowPageItemGaoDe){
			    YellowPageItemGaoDe gaoDe = (YellowPageItemGaoDe)item;
			    latitude = gaoDe.getData().getLatitude();
			    longtitude = gaoDe.getData().getLongitude();
			} else if (item instanceof YellowPageELongItem) {
				// add by putao_lhq 2014年10月18日 for BUG #1543 start
				YellowPageELongItem elongItem = (YellowPageELongItem)item;
				latitude = elongItem.getData().getLatitude();
				longtitude = elongItem.getData().getLongitude();
				// add by putao_lhq 2014年10月18日 for BUG #1543 end
			}
			startActivity(YellowPageMapActivity.this.getGoHereIntent(latitude,longtitude,name));
		} else if (id == R.id.show_detail) {
			Intent intent = new Intent(this, YellowPageShopDetailActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("YelloPageItem", item);
			intent.putExtra("CategoryId", -1);
			intent.putExtras(bundle);
			startActivity(intent);
		} else if (id == R.id.btn_home_page) {
			Intent intent = new Intent(this, YellowPageDetailActivity.class);
			String businessUrl = getWebAddress();
			if (businessUrl.startsWith("www.")) {
			    intent.putExtra("url", "http://" + businessUrl);//
			} else {
			    intent.putExtra("url", businessUrl);// +businessUrl
			}
			intent.putExtra("title", item.getName());
			startActivity(intent);
		} else {
		}

    }

}
