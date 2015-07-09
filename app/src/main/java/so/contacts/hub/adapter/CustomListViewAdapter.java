package so.contacts.hub.adapter;

import com.loader.DataLoader;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * 使用CustomListView的Adapter基类
 *
 */
abstract public class CustomListViewAdapter extends BaseAdapter {

	abstract public DataLoader getmImageLoader();
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
