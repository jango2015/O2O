package so.contacts.hub.search;

import java.util.List;

import so.contacts.hub.search.bean.SearchInfo;
import so.putao.findplug.YelloPageItem;

public interface Searchable {
    
	public List<YelloPageItem> search(Solution sol, SearchInfo searchInfo);

    public List<YelloPageItem> search(Solution sol, String searchInfo);

    public boolean hasMore();
    
    public int getPage();

}
