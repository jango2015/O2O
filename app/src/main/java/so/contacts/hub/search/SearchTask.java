package so.contacts.hub.search;

import so.contacts.hub.search.bean.SearchInfo;
import so.contacts.hub.ui.yellowpage.bean.SearchProvider;

public class SearchTask {
	
	public static final int SEARCH_TASK_TYPE_LOCAL = 0;
	public static final int SEARCH_TASK_TYPE_SERVER = 1;
	public static final int SEARCH_TASK_TYPE_DEFAULT = 2;
	
    private int id; 
    
    private int bussEntry;               // 业务入口
    
    private int sort;                    // 任务排序字段
    
    private int type;                    // 任务类型 0-本地任务 1-服务器返回任务 2-默认搜索任务
    
    private boolean hasMore;             // 有更多
        
    private SearchInfo searchInfo;           // json 搜索数据    
    
    private SearchProvider provider;       // 提供商数据
    
    private String orderBy;                // 该任务排序规则
    
    public SearchTask() {
        hasMore = true; // default value
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBussEntry() {
        return bussEntry;
    }

    public void setBussEntry(int bussEntry) {
        this.bussEntry = bussEntry;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public SearchInfo getSearchInfo() {
        return searchInfo;
    }

    public void setSearchInfo(SearchInfo searchInfo) {
        this.searchInfo = searchInfo;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    public SearchProvider getProvider() {
        return provider;
    }

    public void setProvider(SearchProvider provider) {
        this.provider = provider;
    }    
    
    public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	@Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("SearchTask id:").append(id).append(" entry:").append(bussEntry).append(" sort:").append(sort).append(" orderby:").append(orderBy)
        .append(" searchInfo:").append(searchInfo)
        .append("\n\t").append(provider.toString());
        
        return sb.toString();
    }
}
