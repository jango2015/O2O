package so.contacts.hub.ui.yellowpage;

import so.contacts.hub.remind.BaseRemindActivity;
import so.contacts.hub.util.MobclickAgentUtil;
import so.contacts.hub.util.UMengEventIds;
import so.contacts.hub.util.Utils;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.yulong.android.contacts.discover.R;

public class YellowPageAboutActivity extends BaseRemindActivity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.putao_yellow_page_about);

		initViewAndData();
		MobclickAgentUtil.onEvent(this, UMengEventIds.DISCOVER_YELLOWPAGE_AGGREGATED_ABOUT);
	}

	private void initViewAndData() {
		((TextView) findViewById(R.id.title)).setText(getResources().getString(
				R.string.putao_yellow_page_about));
		findViewById(R.id.back_layout).setOnClickListener(this);

		TextView tView = (TextView) findViewById(R.id.show_contact);
		String firstStr = getResources().getString(R.string.putao_yellow_page_about_content2);
		final String secondStrEmail = getResources().getString(R.string.putao_yellow_page_about_content3);
		String contactStr = firstStr + secondStrEmail;

		ColorStateList redColors = ColorStateList.valueOf(0xff4db602);
		SpannableStringBuilder spanBuilder = new SpannableStringBuilder(contactStr);
		spanBuilder.setSpan(new TextAppearanceSpan(null, 0, 0, redColors, null),
				firstStr.length(), contactStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		spanBuilder.setSpan(new Clickable(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					sendEmail(secondStrEmail);
				}
			}), firstStr.length(), contactStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		tView.setText(spanBuilder);
		tView.setMovementMethod(LinkMovementMethod.getInstance());
	}

	private void sendEmail(String emailAddr){
		Intent emailIntent = null;
		try{
			emailIntent = new Intent(Intent.ACTION_SENDTO);
			if( emailIntent != null ){
				emailIntent.setData(Uri.parse("mailto:" + emailAddr)); 
				emailIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.putao_yellow_page_about_sendemail_head)); 
				emailIntent.putExtra(Intent.EXTRA_TEXT, "");  
				startActivity(emailIntent);
			}
			
//			emailIntent = new Intent(Intent.ACTION_SEND);
//	        if( emailIntent != null ){
//	        	String subject = getResources().getString(R.string.putao_yellow_page_about_sendemail_head);
//	        	String[] extra = new String[]{emailAddr};
//	        	emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
//	        	emailIntent.putExtra(Intent.EXTRA_TEXT, "");
//	        	emailIntent.putExtra(Intent.EXTRA_EMAIL, extra);
//	        	emailIntent.setType("message/rfc822");
//	        	startActivity(emailIntent);
//	        }
		}catch(Exception e){
			Utils.showToast(YellowPageAboutActivity.this, R.string.putao_yellow_page_about_sendemailhint, false);
		}
	}
	
	private class Clickable extends ClickableSpan implements OnClickListener {
		private final View.OnClickListener mListener;

		public Clickable(View.OnClickListener listener) {
			mListener = listener;
		}

		@Override
		public void onClick(View v) {
			mListener.onClick(v);
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.back_layout) {
			finish();
		} else {
		}
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

	@Override
	public Integer remindCode() {
		return null;
	}
	
}
