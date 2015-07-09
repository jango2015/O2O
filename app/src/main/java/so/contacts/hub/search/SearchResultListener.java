package so.contacts.hub.search;

import java.util.List;
import java.util.Map;

import so.putao.findplug.YelloPageItem;

public interface SearchResultListener {
    public void onResult(Solution sol, Map<Integer, List<YelloPageItem> > itemMaps, List<YelloPageItem> itemList, boolean hasMore);

}
