package so.contacts.hub.http.bean;

import java.util.List;

import so.contacts.hub.ui.yellowpage.bean.CategoryBean;
import so.contacts.hub.ui.yellowpage.bean.ItemBean;

public class UpdateYellowPageDataResponse extends BaseResponseData {

    public List<CategoryBean> categoryList; //黄页分类数据
    public List<ItemBean> itemList; //黄页item数据
    public int data_version;        //黄页数据version
}
