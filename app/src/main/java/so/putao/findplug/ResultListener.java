package so.putao.findplug;

import java.util.ArrayList;

import android.os.Handler;

public interface ResultListener {
	public Handler getHandler();
	public void onResult(SearchData searchData,ArrayList<YelloPageItem> itemList,boolean hasMore,boolean isTimeOut);
}
