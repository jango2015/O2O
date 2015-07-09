package so.contacts.hub.train;

import com.yulong.android.contacts.discover.R;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import so.contacts.hub.train.bean.TravellerInfo;
import so.contacts.hub.ui.BaseActivity;
import so.contacts.hub.ui.yellowpage.YellowPageCitySelectActivity;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.YellowPageDataUtils;
import so.contacts.hub.widget.CommonDialog;
import so.contacts.hub.widget.CommonDialogFactory;

/** 新增常旅客邮寄地址 */
public class YellowPagePostAddressActivity extends BaseActivity implements
		OnClickListener {
	private TextView title;
	private ImageView back;
	private EditText edit_traveller_name;
	private EditText edit_traveller_phone;
	private TextView address_select;
	private EditText detail_address;
//	private Button bt_traveller_delete;
	private Button bt_traveller_commit;
	private RelativeLayout back_layout;
	private LinearLayout address_layout;
	private String selectAddessFromDB = "";// 保存从城市列表里返回的字符串

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.putao_train_edittraveller_address_layout);
		initViews();
		initData();
		//initAddressDB();
	}

//	private void initAddressDB() {
//		// add by lisheng 2014-12-01 start
//		final SharedPreferences sp = getSharedPreferences("postaddress",
//				Context.MODE_MULTI_PROCESS);
//		boolean isLoaded = sp.getBoolean("dbIsLoaded", false);
//		if (!isLoaded) {
//			Config.execute(new Runnable() {
//				@Override
//				public void run() {
//					Editor edit = sp.edit();
//					edit.putBoolean("dbIsLoaded", true);
//					edit.commit();
//
//				}
//			});
//		}
//		// add by lisheng end;
//
//	}

	private void initData() {
		Intent intent = getIntent();
		if (intent != null) {
			TravellerInfo info = (TravellerInfo) intent
					.getSerializableExtra("address");
			if (info != null) {
				edit_traveller_name.setText(info.name);
				detail_address.setText(info.address);
			}
		}
	}

	private void initViews() {
		back_layout = (RelativeLayout) findViewById(R.id.back_layout);
		title = (TextView) findViewById(R.id.title);

		edit_traveller_name = (EditText) findViewById(R.id.username);
		edit_traveller_phone = (EditText) findViewById(R.id.phone);
		address_select = (TextView) findViewById(R.id.address_select);
		detail_address = (EditText) findViewById(R.id.detail_address);
		address_layout = (LinearLayout) findViewById(R.id.address_layout);

		bt_traveller_commit = (Button) findViewById(R.id.bt_traveller_commit);
//		bt_traveller_delete = (Button) findViewById(R.id.bt_traveller_delete);

		title.setText(getResources().getString(R.string.putao_traintriket_add));

//		bt_traveller_delete.setOnClickListener(this);
		bt_traveller_commit.setOnClickListener(this);
		back_layout.setOnClickListener(this);
		findViewById(R.id.address_layout).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		String name = edit_traveller_name.getText().toString().trim();
		String phone = edit_traveller_phone.getText().toString().trim();
		// String address = address_select.getText().toString().trim();
		String detail = detail_address.getText().toString().trim();
		int id = v.getId();
		if (id == R.id.bt_traveller_commit) {
			if (TextUtils.isEmpty(name)) {
				Toast.makeText(
						this,
						getResources().getString(
								R.string.putao_traintriket_username)
								+ getResources().getString(
										R.string.putao_traintriket_notnull), 0)
						.show();
				return;
			}
			if (TextUtils.isEmpty(phone)) {
				Toast.makeText(
						this,
						getResources().getString(
								R.string.putao_traintriket_phone)
								+ getResources().getString(
										R.string.putao_traintriket_notnull), 0)
						.show();
				return;
			}
			String[] add = selectAddessFromDB.split("_");
			if (add.length < 3) {
				Toast.makeText(
						this,
						getResources().getString(
								R.string.putao_traintriket_selecterr), 0)
						.show();
				return;
			}
			if (TextUtils.isEmpty(detail)) {
				Toast.makeText(
						this,
						getResources().getString(
								R.string.putao_traintriket_detailaddress)
								+ getResources().getString(
										R.string.putao_traintriket_notnull), 0)
						.show();
				return;
			}
			// String name, String mobile, String pro, String city, String reg,
			// String address, String zcode
			PutaoTrainH5JsBridge.addOftenAddress(name, phone, add[2], add[1],
					add[0], detail, "");
			setResult(RESULT_OK);
			finish();
		} 
//		else if (id == R.id.bt_traveller_delete) {
//			showDeleteDialog();  //modify by ls 隐藏删除按钮
// 		} 
		else if (id == R.id.back_layout) {
			finish();
		} else if (id == R.id.address_layout) {
			Intent intent = new Intent(this, YellowPageCitySelectActivity.class);
			intent.putExtra(YellowPageCitySelectActivity.FROM_ACTIVITY_KEY,
					"fromPost");
			intent.putExtra(YellowPageCitySelectActivity.SHOW_MODE_KEY,
					YellowPageCitySelectActivity.SHOW_MODE_PURE);
			intent.putExtra(
					"title",
					getResources().getString(
							R.string.putao_traintriket_district_select_hint));
			startActivityForResult(intent, 1);
		}

	}

/**modify by ls 2015-01-22 隐藏删除按钮*/
//	private void showDeleteDialog() {
//		final CommonDialog commonDialog = CommonDialogFactory
//				.getOkCancelCommonDialog(this);
//		commonDialog.setTitle(R.string.putao_traintriket_delete_address);
//		commonDialog.getMessageTextView().setText(
//				R.string.putao_traintriket_confirm_delete_address);
//		commonDialog.setOkButtonClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				LogUtil.d(YellowPageDataUtils.TAG, "User confirm logout");
//				edit_traveller_name.setText("");
//				edit_traveller_phone.setText("");
//				address_select.setText("");
//				detail_address.setText("");
//				commonDialog.dismiss();
//			}
//		});
//		commonDialog.setCancelButtonClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				commonDialog.dismiss();
//			}
//		});
//		commonDialog.show();
//	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		super.onActivityResult(arg0, arg1, arg2);
		if (arg0 == 1 && arg2 != null && arg1 == RESULT_OK) {
			selectAddessFromDB = arg2.getStringExtra("select");
			String str[] = selectAddessFromDB.split("_");
			address_select.setText(str[2] + str[1] + str[0]);
		}
	}

}
