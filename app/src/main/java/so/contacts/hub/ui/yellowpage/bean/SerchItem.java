package so.contacts.hub.ui.yellowpage.bean;

import java.io.Serializable;

public class SerchItem implements Serializable{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String search_key; //搜索关键字
    private String search_category; //搜索类别
    private String search_show;//显示内容
    
    public String getSearchKey() {
        return search_key;
    }
    public void setSearchKey(String searchKey) {
        this.search_key = searchKey;
    }
    public String getSearchShow() {
        return search_show;
    }
    public void setSearchShow(String searchShow) {
        this.search_show = searchShow;
    }
    public String getSearch_category() {
        return search_category;
    }
    public void setSearch_category(String search_category) {
        this.search_category = search_category;
    }
    
}
