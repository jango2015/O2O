package so.contacts.hub.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.PhoneLookup;

public class ContactsDBImpl{

	private static ContactsDBImpl contactsDBImpl = null;

	private ContactsDBImpl() {
	}

	public static ContactsDBImpl getInstance() {
		if (contactsDBImpl == null) {
			contactsDBImpl = new ContactsDBImpl();
		}
		return contactsDBImpl;
	}
	
	public String lookUpNumber(Context context, String number) {
        // ContentResolver resolver = c.getContentResolver();
        // String whereStr = Phone.NUMBER + " like '%" + number + "'";
        // Cursor cursor = resolver.query(Phone.CONTENT_URI, null,
        // whereStr, null, "sort_key");
        // ContactsBean bean = parseLookupCursor(c, cursor,number);

        Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        return lookUpContactFromUri(context, uri);
    }

	public static final String[] _PROJECTION = new String[] { PhoneLookup._ID,
			PhoneLookup.DISPLAY_NAME, };

	private String lookUpContactFromUri(Context context, Uri uri) {
		String contactName = "";
		Cursor cursor = null;
		try {
			cursor = context.getContentResolver().query(uri, _PROJECTION, null, null,
					null);
			if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {

				int contactId = cursor.getInt(cursor
						.getColumnIndex(PhoneLookup._ID));
				// bean.setRaw_contact_id(contactId);// 通过
				// PhoneLookup.CONTENT_FILTER_URI
				// 查找号码对应的联系人，返回的结果中无RAW_CONTACT_ID

				contactName = cursor.getString(cursor
						.getColumnIndex(PhoneLookup.DISPLAY_NAME));
			}
		} catch(Exception e){
			contactName = "";
		}
		finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return contactName;
	}

}