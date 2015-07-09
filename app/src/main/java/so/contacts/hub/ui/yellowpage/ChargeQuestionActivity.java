package so.contacts.hub.ui.yellowpage;

import so.contacts.hub.remind.BaseRemindActivity;
import so.contacts.hub.util.MobclickAgentUtil;
import com.yulong.android.contacts.discover.R;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

/**
 * 充值注意事项
 * @author change
 *
 */
public class ChargeQuestionActivity extends BaseRemindActivity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.putao_charge_question);
		TextView title = (TextView)findViewById(R.id.title);
		title.setText(R.string.putao_charge_question);
		
		findViewById(R.id.back_layout).setOnClickListener(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		MobclickAgentUtil.onResume(this);
		super.onResume();
	}

	@Override
	protected void onPause() {
		MobclickAgentUtil.onPause(this);
		super.onPause();
	}
	
	@Override
	public void onClick(View arg0) {
		if(arg0.getId() == R.id.back_layout) {
			finish();
		}
	}

	@Override
	public String getServiceNameByUrl() {
		return null;
	}

	@Override
	public String getServiceName() {
		return getClass().getName();
	}

	@Override
	public boolean needMatchExpandParam() {
		return false;
	}

	@Override
	public Integer remindCode() {
		return null;
	}

}
