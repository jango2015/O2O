package so.contacts.hub.adapter;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class HomeAdapter extends PagerAdapter {

	public List<View> views = null;

	public HomeAdapter() {
	}
	
	public HomeAdapter(List<View> list) {
		views = list;
	}
	
	public void setData(List<View> list) {
	    views = list;
	}

	@Override
	public int getCount() {
		return views != null ? views.size() : 0;
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public Object instantiateItem(View container, int position) {
		((ViewPager) container).addView(views.get(position));
		return views.get(position);
	}

	@Override
	public void destroyItem(View container, int position, Object object) {
//		if (position < getCount()) {
//			((ViewPager) container).removeView(views.get(position));
			((ViewPager) container).removeView((View)object);
//		}
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

}
