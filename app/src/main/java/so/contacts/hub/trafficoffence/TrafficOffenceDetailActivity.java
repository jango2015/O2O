package so.contacts.hub.trafficoffence;


import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.yulong.android.contacts.discover.R;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import so.contacts.hub.trafficoffence.bean.PeccancyDetailBean;
import so.contacts.hub.trafficoffence.bean.PeccancyResult;
import so.contacts.hub.ui.BaseActivity;
import so.contacts.hub.util.LogUtil;

public class TrafficOffenceDetailActivity extends BaseActivity implements
		OnClickListener {
	private static final String TAG =TrafficOffenceDetailActivity.class.getSimpleName();
	private Button query_all_offence = null;
	private TextView car_number = null;
	private TextView punish_points = null;
	private TextView punish_money = null;
	private TextView offence_location = null;
	private TextView offence_rules = null;
	private TextView offence_time = null;
	private PeccancyResult peccancy = null;
	private PeccancyDetailBean latestOffence = null;// 违章详情显示最新的一条违章;

	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		setContentView(R.layout.putao_traffic_offence_detail_layout);
		parseIntent();
		init();
	}

	private void parseIntent() {
		// TODO Auto-generated method stub
		Intent intent = getIntent();
		if (intent == null) {
			return;
		}
		peccancy = (PeccancyResult) intent
				.getSerializableExtra(PeccancyResult.class.getSimpleName());
		if (peccancy != null) {
			LogUtil.d(TAG, "peccancy---------"+peccancy.toString());
			String json_detail =peccancy.getDetail_list();
			ArrayList<PeccancyDetailBean> detail_list = getPeccancyDetailList(json_detail);
			if (detail_list != null && detail_list.size() > 0){
				latestOffence = detail_list.get(0);
				LogUtil.d(TAG, "latestOffence---------"+latestOffence.toString());
			}
			
		}

	}

	private ArrayList<PeccancyDetailBean> getPeccancyDetailList(String json_detail) {
		ArrayList<PeccancyDetailBean> list =null;
		if(json_detail==null){
			return list;
		}
		list = new ArrayList<PeccancyDetailBean>();
		try {
			JSONArray array = new JSONArray(json_detail);
			for(int i =0;i<array.length();i++){
				PeccancyDetailBean bean = new PeccancyDetailBean();
				JSONObject obj = array.getJSONObject(i);
				bean.setAddress(obj.optString("address"));
				bean.setFine(obj.optString("fine"));
				bean.setPoint(obj.optString("point"));
				bean.setReason(obj.optString("reason"));
				bean.setTime(obj.optString("time"));
				list.add(bean);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}

	private void init() {
	    setTitle(R.string.putao_traffic_detail_title);
		query_all_offence = (Button) findViewById(R.id.query_all_offence);
		query_all_offence.setOnClickListener(this);
		car_number = (TextView) findViewById(R.id.car_number);
		punish_points = (TextView) findViewById(R.id.punish_points);
		punish_money = (TextView) findViewById(R.id.punish_money);
		offence_location = (TextView) findViewById(R.id.offence_location);
		offence_rules = (TextView) findViewById(R.id.offence_rules);
		offence_time = (TextView) findViewById(R.id.offence_time);
		if (peccancy != null && latestOffence != null) {
			car_number.setText(peccancy.getCar_no());
			
			/**add by ls 2015-01-23*/
			String points = latestOffence.getPoint();
			if(!TextUtils.isEmpty(points)){
				if(!points.startsWith("0")){
					punish_points.setText("-"+points);
				}else{
					punish_points.setText(points);
				}
			}
//			punish_points.setText(latestOffence.getPoint());
			/**end */
			
			punish_money.setText(getString(R.string.putao_traffic_detail_punish_money, latestOffence.getFine()));
			offence_location.setText(latestOffence.getAddress());
			offence_time.setText(latestOffence.getTime());
			offence_rules.setText(latestOffence.getReason());
		}

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if(id == R.id.query_all_offence){
			//TODO开启查询单个车牌号下所有违章行为;
			Intent intent = null;
			try {
				intent = new Intent(TrafficOffenceDetailActivity.this,
						YellowPageAllPeccancyDetailsActivity.class);
				intent.putExtra("type", 2);
				intent.putExtra("title", getString(R.string.all_traffic_offence_records));
				intent.putExtra("peccancydetaillist", peccancy.getDetail_list());
				startActivity(intent);
			} catch (Exception e) {
			}
		}
	}

}
