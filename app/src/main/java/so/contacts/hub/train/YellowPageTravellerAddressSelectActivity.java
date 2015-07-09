package so.contacts.hub.train;

import java.util.HashMap;
import java.util.Map;

import com.yulong.android.contacts.discover.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import so.contacts.hub.train.bean.TravellerInfo;
import so.contacts.hub.ui.BaseActivity;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.widget.MyListView;

public class YellowPageTravellerAddressSelectActivity extends BaseActivity implements OnClickListener,OnItemClickListener {
	private TextView title;
	private Button add_traveller =null;
	private MyListView traveller_info_lv =null;
	private SelectTravellerAdapter adapter; 
	private Map<Integer,TravellerInfo> map =null;
	private ImageView back;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.putao_train_select_traveller_address_layout);
		initViews();
		initData();
	}
	
	
	private void initData() {
		
		if(map==null){
			map = new HashMap<Integer,TravellerInfo>();
		}
		//测试数据
		TravellerInfo info1 =new TravellerInfo("张三", "13542311234", 1, "421125198212345678", "1990-11-11", "深圳市南山区高新科技园");
		TravellerInfo info2 =new TravellerInfo("李四", "13542311234", 2, "421125198212345678", "1990-11-11", "广州市天河区高新科技园");
		map.put(0, info1);
		map.put(1, info2);
		map.put(2, info1);
		map.put(3, info2);
		map.put(4, info1);
		map.put(5, info2);
		map.put(6, info1);
		map.put(7, info2);
		map.put(8, info1);
		map.put(9, info2);
		map.put(10, info1);
		map.put(11, info2);
		//测试数据
		adapter = new SelectTravellerAdapter();
		traveller_info_lv.setAdapter(adapter);
		
		
		
//		traveller_info_lv.setOnItemClickListener(this);
	}


	private void initViews() {
		
		back =(ImageView) findViewById(R.id.back);
		title  = (TextView) findViewById(R.id.title);
		
		//测试
		title.setText(getResources().getString(R.string.putao_traintriket_address));
		//测试
		
		add_traveller = (Button) findViewById(R.id.add_traveller);
		traveller_info_lv =(MyListView) findViewById(R.id.traveller_info_lv);
		traveller_info_lv.setOnItemClickListener(this);
		traveller_info_lv.setDividerHeight(20);
		add_traveller.setOnClickListener(this);
		findViewById(R.id.head_layout).setOnClickListener(this);
	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	

	
	
	public class SelectTravellerAdapter extends BaseAdapter{
		

		@Override
		public int getCount() {
			return map.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final TravellerInfo ti =map.get(position);
			if(convertView == null){
				convertView = View.inflate(YellowPageTravellerAddressSelectActivity.this, R.layout.putao_train_oftentraveller_address_item, null);
			}
			TextView traveller_name=(TextView) convertView.findViewById(R.id.traveller_name);
			TextView address = (TextView) convertView.findViewById(R.id.address);
			ImageView edit_address = (ImageView) convertView.findViewById(R.id.edit_address);
			convertView.setTag(ti);
			traveller_name.setText(ti.name);
			address.setText(ti.address);
			edit_address.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent  = new Intent(YellowPageTravellerAddressSelectActivity.this,YellowPagePostAddressActivity.class);
					intent.putExtra("address", ti);
					startActivityForResult(intent, position);
				}
			});
			
			return convertView;
		}
		
	} 
	
	
	
	
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		if(id==R.id.add_traveller){
			Intent intent  = new Intent(this,YellowPagePostAddressActivity.class);
			startActivity(intent);
		}else if(id == R.id.head_layout){
			finish();
		}
	}

	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		//TODO 点击item的事件处理待完成;
		
	}
	
	
	
}
