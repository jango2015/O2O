package so.putao.findplug;

import java.util.ArrayList;

public abstract class YelloPageFactory {
	public abstract ArrayList<YelloPageItem> search(String keyword, String city, double longitude,double latitude,String category,int source);
	public abstract ArrayList<YelloPageItem> searchMore();
	public abstract boolean hasMore();
}
