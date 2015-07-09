package so.contacts.hub.db;

import java.util.ArrayList;
import java.util.List;

import so.contacts.hub.businessbean.AppRecommendInfo;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

//import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class AppRecommendDB {
	private SQLiteDatabase mDb;

	public AppRecommendDB(DatabaseHelper helper) {
		mDb = helper.getWritableDatabase();
	}

	static String getCreateTableSQL() {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE IF NOT EXISTS ").append(Table.TABLE_NAME).append(" (");
		sb.append(Table._ID).append(" INTEGER  PRIMARY KEY  autoincrement,");
		sb.append(Table.ID).append(" LONG  ,");
		sb.append(Table.KIND_ID).append(" LONG,");
		sb.append(Table.APP_NAME).append(" TEXT,");
		sb.append(Table.APP_TYPE).append(" INTEGER,");
		sb.append(Table.REMARK).append(" TEXT,");
		sb.append(Table.ICON).append(" TEXT,");
		sb.append(Table.DOWN_URL).append(" TEXT,");
		sb.append(Table.DOWN_COUNT).append(" LONG,");
		sb.append(Table.S_IMGS).append(" TEXT,");
		sb.append(Table.PACKAGE_NAME).append(" TEXT,");
		sb.append(Table.VERSION).append(" TEXT,");
		sb.append(Table.L_IMGS).append(" TEXT,");
		sb.append(Table.SIZE).append(" TEXT,");
		sb.append(Table.DOWNLOAD_ID).append(" LONG  ,");
		sb.append(Table.STATUS).append(" INTEGER,");
		sb.append(Table.INSTALL_TIME).append(" LONG");
		sb.append(");");

		return sb.toString();
	}

	public List<AppRecommendInfo> findAll(long kind_id) {
		List<AppRecommendInfo> infos = new ArrayList<AppRecommendInfo>();
		Cursor cursor = null;
		try {
			String whereClause = Table.KIND_ID + " = ? ";
			String[] whereArgs = new String[] { String.valueOf(kind_id) };
			cursor = mDb.query(Table.TABLE_NAME, Table.PROJECTION, whereClause,
					whereArgs, null, null, Table.DEFAULT_SORT_ORDER);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					infos.add(parse(cursor));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return infos;
	}
	
	public List<AppRecommendInfo> findCanceled() {
		List<AppRecommendInfo> fai_list = new ArrayList<AppRecommendInfo>();
		Cursor cursor = null;
		try {
			String whereClause = Table.STATUS + " = ? ";
			String[] whereArgs = new String[] { "0" };
			cursor = mDb.query(Table.TABLE_NAME, Table.PROJECTION, whereClause,
					whereArgs, null, null, Table.DEFAULT_SORT_ORDER);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					fai_list.add(parse(cursor));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return fai_list;
	}
	
	public List<AppRecommendInfo> findUnreported() {
		List<AppRecommendInfo> fai_list = new ArrayList<AppRecommendInfo>();
		Cursor cursor = null;
		try {
			String whereClause = Table.STATUS + " = ? or " + Table.STATUS + " = ? ";
			String[] whereArgs = new String[] { "1", "4" };
			cursor = mDb.query(Table.TABLE_NAME, Table.PROJECTION, whereClause,
					whereArgs, null, null, Table.DEFAULT_SORT_ORDER);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					fai_list.add(parse(cursor));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return fai_list;
	}
	
	public List<AppRecommendInfo> findNeedToUninstalled() {
		List<AppRecommendInfo> fai_list = new ArrayList<AppRecommendInfo>();
		Cursor cursor = null;
		try {
			String whereClause = Table.STATUS + " = ? ";
			String[] whereArgs = new String[] { "5" };
			cursor = mDb.query(Table.TABLE_NAME, Table.PROJECTION, whereClause,
					whereArgs, null, null, Table.DEFAULT_SORT_ORDER);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					fai_list.add(parse(cursor));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return fai_list;
	}
	
	public boolean exist(long id) {
		boolean exist = false;
		Cursor cursor = null;
		try {
			String whereClause = Table.ID + " = ?";
			String[] whereArgs = new String[] { String.valueOf(id) };
			cursor = mDb.query(Table.TABLE_NAME, Table.PROJECTION,
					whereClause, whereArgs, null, null,
					Table.DEFAULT_SORT_ORDER);
			if (cursor != null) {
				exist = cursor.getCount() > 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return exist;
	}

	public void save(long kind_id, List<AppRecommendInfo> fai_list) {
		try {
			mDb.beginTransaction();
			if (fai_list != null && fai_list.size() > 0) {
				delAll(kind_id);
				
				ContentValues values;
				Gson gson = new Gson();
				for (AppRecommendInfo info : fai_list) {
					values = new ContentValues();
					values.put(Table.ID, info.id);
					values.put(Table.KIND_ID, kind_id);
					values.put(Table.APP_NAME, info.app_name);
					values.put(Table.APP_TYPE, info.app_type);
					values.put(Table.REMARK, info.remark);
					values.put(Table.S_IMGS, gson.toJson(info.s_imgs));
					values.put(Table.PACKAGE_NAME, info.package_name);
					values.put(Table.VERSION, info.version);
					values.put(Table.ICON, info.icon);
					values.put(Table.DOWN_URL, info.down_url);
					values.put(Table.DOWN_COUNT, info.down_count);
					values.put(Table.L_IMGS, gson.toJson(info.l_imgs));
					values.put(Table.SIZE, info.size);
					
					values.put(Table.DOWNLOAD_ID, 0);
					values.put(Table.STATUS, 0);
					values.put(Table.INSTALL_TIME, 0);

					if (exist(info.id)) {
						String whereClause = Table.ID + " = ?";
						String[] whereArgs = new String[] { String
								.valueOf(info.id) };
						mDb.update(Table.TABLE_NAME, values, whereClause,
								whereArgs);
					} else {
						mDb.insert(Table.TABLE_NAME, null, values);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mDb.setTransactionSuccessful();
			mDb.endTransaction();
		}
	}

	public void delAll(long kind_id) {
		String whereClause = Table.KIND_ID + " = ?";
		String[] whereArgs = new String[] { String
				.valueOf(kind_id) };
		mDb.delete(Table.TABLE_NAME, whereClause, whereArgs);
	}

	public static class Table implements BaseColumns {

		public static final String TABLE_NAME = "app_recommend";

		public static final String ID = "id"; 
		public static final String KIND_ID = "kind_id";
		public static final String APP_NAME = "app_name"; 
		public static final String APP_TYPE = "app_type"; 
		public static final String PACKAGE_NAME = "package_name"; 
		public static final String REMARK = "remark"; 
		public static final String VERSION = "version"; 
		public static final String ICON = "icon";
		public static final String L_IMGS = "l_imgs";
		public static final String S_IMGS = "s_imgs"; 
		public static final String DOWN_URL = "down_url";
		public static final String DOWN_COUNT = "down_count";
		public static final String SIZE = "size";
		
		public static final String DOWNLOAD_ID = "download_id";
		public static final String STATUS = "status";
		public static final String INSTALL_TIME = "install_time";

		public static final String DEFAULT_SORT_ORDER = Table._ID + " asc";

		public static final String[] PROJECTION = { _ID, ID, KIND_ID, APP_NAME, APP_TYPE,
				REMARK, S_IMGS, PACKAGE_NAME, VERSION, ICON,  DOWN_URL, DOWN_COUNT, L_IMGS, SIZE, DOWNLOAD_ID, STATUS, INSTALL_TIME };

	}

	/**
	 * @param f_a_id
	 * @return
	 */
	public AppRecommendInfo find(long f_a_id) {
		AppRecommendInfo appInfo = null;
		Cursor cursor = null;
		try {
			String whereClause = Table.ID + " = ? ";
			String[] whereArgs = new String[] { String.valueOf(f_a_id) };
			cursor = mDb.query(Table.TABLE_NAME, Table.PROJECTION, whereClause,
					whereArgs, null, null, Table.DEFAULT_SORT_ORDER);
			if (cursor != null) {
				if (cursor.moveToNext()) {
					appInfo = parse(cursor);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return appInfo;
	}

	private AppRecommendInfo parse(Cursor cursor) {
		AppRecommendInfo appInfo = new AppRecommendInfo();
		appInfo.id = cursor.getLong(cursor.getColumnIndex(Table.ID));
//		appInfo.kind_id = cursor.getInt(cursor.getColumnIndex(Table.KIND_ID));
		appInfo.app_name = cursor.getString(cursor.getColumnIndex(Table.APP_NAME));
		appInfo.app_type = cursor.getInt(cursor.getColumnIndex(Table.APP_TYPE));
		appInfo.remark = cursor.getString(cursor.getColumnIndex(Table.REMARK));
		appInfo.package_name = cursor.getString(cursor.getColumnIndex(Table.PACKAGE_NAME));
		appInfo.version = cursor.getString(cursor.getColumnIndex(Table.VERSION));
		appInfo.icon = cursor.getString(cursor.getColumnIndex(Table.ICON));
		appInfo.down_url = cursor.getString(cursor.getColumnIndex(Table.DOWN_URL));
		appInfo.down_count = cursor.getLong(cursor.getColumnIndex(Table.DOWN_COUNT));
		
		String json = cursor.getString(cursor.getColumnIndex(Table.L_IMGS));
		List<String> l_imgs = new Gson().fromJson(json, new com.google.gson.reflect.TypeToken<List<String>>() {}.getType());
		appInfo.l_imgs = l_imgs;
		
		json = cursor.getString(cursor.getColumnIndex(Table.S_IMGS));
		List<String> s_imgs = new Gson().fromJson(json, new TypeToken<List<String>>() {}.getType());
		appInfo.s_imgs = s_imgs;
		
		appInfo.size = cursor.getString(cursor.getColumnIndex(Table.SIZE));
		
		appInfo.download_id = cursor.getInt(cursor.getColumnIndex(Table.DOWNLOAD_ID));
		appInfo.status = cursor.getInt(cursor.getColumnIndex(Table.STATUS));
		appInfo.install_time = cursor.getLong(cursor.getColumnIndex(Table.INSTALL_TIME));
		
		return appInfo;
	}

	/**
	 * @param appInfo
	 */
	public void update(AppRecommendInfo appInfo) {
		try {
			ContentValues values = new ContentValues();
			values.put(Table.DOWNLOAD_ID, appInfo.download_id);// 已保存
			values.put(Table.STATUS, appInfo.status);// 已保存
			values.put(Table.INSTALL_TIME, appInfo.install_time);// 已保存

			String whereClause = Table.ID + " = ? ";
			String[] whereArgs = new String[] { String.valueOf(appInfo.id) };

			mDb.update(Table.TABLE_NAME, values, whereClause, whereArgs);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param packageName
	 * @return
	 */
	public AppRecommendInfo findByPackageName(String packageName) {
		AppRecommendInfo appInfo = null;
		Cursor cursor = null;
		try {
			String whereClause = Table.PACKAGE_NAME + " = ? ";
			String[] whereArgs = new String[] { packageName };
			cursor = mDb.query(Table.TABLE_NAME, Table.PROJECTION, whereClause,
					whereArgs, null, null, Table.DEFAULT_SORT_ORDER);
			if (cursor != null) {
				if (cursor.moveToNext()) {
					appInfo = parse(cursor);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return appInfo;
	}

	/**
	 * @param id
	 * @return
	 */
	public AppRecommendInfo findByDownloadId(long id) {
		AppRecommendInfo appInfo = null;
		Cursor cursor = null;
		try {
			String whereClause = Table.DOWNLOAD_ID + " = ? ";
			String[] whereArgs = new String[] { String.valueOf(id) };
			cursor = mDb.query(Table.TABLE_NAME, Table.PROJECTION, whereClause,
					whereArgs, null, null, Table.DEFAULT_SORT_ORDER);
			if (cursor != null) {
				if (cursor.moveToNext()) {
					appInfo = parse(cursor);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return appInfo;
	}

}
