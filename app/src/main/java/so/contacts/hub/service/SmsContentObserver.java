package so.contacts.hub.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import so.contacts.hub.core.ConstantsParameter;
import so.contacts.hub.util.ContactsAppUtils;
import so.contacts.hub.util.ContactsHubUtils;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

public class SmsContentObserver extends ContentObserver {

	private Context mContext;

	public SmsContentObserver(Context c, Handler handler) {
		super(handler);
		mContext = c;
	}

	@Override
	public void onChange(boolean selfChange) {
		super.onChange(selfChange);
		mhandler.removeMessages(ConstantsParameter.READ_CHECK_CODE);
		mhandler.sendEmptyMessageDelayed(ConstantsParameter.READ_CHECK_CODE,
				2000);
	}

	public Handler mhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ConstantsParameter.READ_CHECK_CODE:
				String checkCode = readCodeFormSms(mContext,
						ContactsAppUtils.getInstance().getManual_check_code(),
						ContactsAppUtils.getInstance().getCurrent_mobile());
				if (!TextUtils.isEmpty(checkCode)) {
				    ContactsAppUtils.getInstance().setCheck_code(checkCode);
				}
				break;
			default:
				break;
			}
		}
	};

	protected String readCodeFormSms(Context c, String manual_check_code,
			String current_mobile) {
		StringBuilder sb = new StringBuilder();
		String where = sb.toString();
		if (!TextUtils.isEmpty(manual_check_code)
				&& !TextUtils.isEmpty(current_mobile)) {
			where = "address like '%" + current_mobile + "'";
		}
		String checkCode = null;
		Cursor cursor = null;
		try {
			cursor = c.getContentResolver().query(
					Uri.parse("content://sms/inbox"),
					new String[] { "_id", "address", "body" }, where, null,
					"date desc");
			if (cursor != null && cursor.moveToFirst()) {
				String address = cursor.getString(1);
				String body = cursor.getString(2); // 短信内容
	
				if (!TextUtils.isEmpty(address)) {
					address = ContactsHubUtils.formatIPNumber(address,c);
					if (!address.equals(ContactsHubUtils
							.formatIPNumber(current_mobile,c))) {
						checkCode = null;
					} else {
						checkCode = getCheckCodeFromMsg(body);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return checkCode;
	}

	private static String getCheckCodeFromMsg(String msg) {
		Pattern pattern = Pattern.compile("[0-9]+");
		Matcher matcher = pattern.matcher(msg);
		if (matcher.find()) {
			return matcher.group();
		}
		return null;
	}
}
