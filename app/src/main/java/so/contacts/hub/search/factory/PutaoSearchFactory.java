package so.contacts.hub.search.factory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import android.content.Context;
import android.text.TextUtils;
import so.contacts.hub.core.Config;
import so.contacts.hub.db.YellowPageDB;
import so.contacts.hub.search.Searchable;
import so.contacts.hub.search.Solution;
import so.contacts.hub.search.bean.SearchInfo;
import so.contacts.hub.ui.yellowpage.bean.ItemBean;
import so.contacts.hub.ui.yellowpage.bean.PuTaoResultItem;
import so.contacts.hub.ui.yellowpage.bean.YellowParams;
import so.contacts.hub.util.ContactsAppUtils;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.YellowUtil;
import so.putao.findplug.YelloPageItem;
import so.putao.findplug.YellowPageItemPutao;

/**
 * 搜索葡萄静态数据
 * @author zjh
 */
@SuppressWarnings("rawtypes")
public class PutaoSearchFactory implements Searchable {
	
    private static final String TAG = "PutaoSearchFactory";
    
    private List<YelloPageItem> mAllPutaoItemList = new ArrayList<YelloPageItem>();
        
    private Context context = null;
    
    private SearchInfo mSearchInfo = null;
    
    private boolean mHasMore = true;
    
    private int mPage = 0;
    
    /**
     * 获取静态数据(应用服务)的最大数目
     */
    private static final int MAX_SEARCH_SERVER_NUM = 50;


    /**
     * 获取静态数据(详情)的最大数目
     */
    private static final int MAX_SEARCH_DETAIL_NUM = 50;
    
    public PutaoSearchFactory(){
    }

	@Override
	public List<YelloPageItem> search(Solution sol, String searchInfo) {
		// TODO Auto-generated method stub
        if(searchInfo != null) {
            mSearchInfo = Config.mGson.fromJson(searchInfo, SearchInfo.class);
        }
        return search(sol, searchInfo);
	}
	
	@Override
	public List<YelloPageItem> search(Solution sol, SearchInfo searchInfo) {
		// TODO Auto-generated method stub
        context = sol.getActivity();
        mSearchInfo = searchInfo;
        String keyword = mSearchInfo.getWords();
        String category = mSearchInfo.getCategory();
        
        if(TextUtils.isEmpty(keyword))
            keyword = sol.getInputKeyword();
        
        if(TextUtils.isEmpty(category))
            category = "";

        if(TextUtils.isEmpty(keyword) && TextUtils.isEmpty(category)) {
            mHasMore = false;
            return null;
        } else {
            // 点评不管是关键字还是类别都一起传
            return searchData(keyword, sol.getInputCity(), 
                    sol.getInputLongtitude(), sol.getInputLatitude(), 
                    category, 
                    mPage+1);
        }
	}
	
	private ArrayList<YelloPageItem> searchData(String keyword, String city, double longitude,double latitude,String category,int page){
		try {
            ArrayList<YelloPageItem> resultList = new ArrayList<YelloPageItem>();
            if(TextUtils.isEmpty(category) && TextUtils.isEmpty(keyword)){
                mHasMore = false;
                return resultList;
            }
            resultList = searchRaw(keyword, city, longitude,latitude,category,page);
            return resultList;
        } catch (Exception e) {
        	LogUtil.e(TAG, e.getMessage());
            mHasMore = false;
            return new ArrayList<YelloPageItem>();  
        }
	}
	
	/*
	 * 根据keyword过滤掉无用的静态数据
	 */
	private void filteringData(List<ItemBean> list,String keyword){
        if (list != null && list.size() > 0) {
            boolean isSortChange = false; //用于标记特殊的组合（地铁，公交） 对searchsort的改变 暂时不用
            int size = list.size();
            for (int i = 0; i < size; i++) {
                ItemBean itemBean = list.get(i);
                String keytag_str = itemBean.getKey_tag();
                //一条数据对应多条搜索词时用 , 分割开 如：热门电影，电影，看电影
                if (keytag_str.contains(",")) {
                    String[] arrayTags = keytag_str.split(",");
                    boolean isContains = false;
                    for (int j = 0; j < arrayTags.length; j++) {
                        //如果搜索词包含 "_" 说明是特殊的情况 同一搜索词集合下会出现不同的搜索词显示的结果不同 如 公交 地铁_2,公交_1  
                        //说明搜索地铁时排序为2 搜索公交时排名为1
                        if (arrayTags[j].contains("_")) {
                            String[] tagAndSort = arrayTags[j].split("_");
                            if (tagAndSort[0].equalsIgnoreCase(keyword)) {
                                itemBean.setSearch_sort(Integer.valueOf(tagAndSort[1]));
                                isContains = true;
                                isSortChange = true;
                                break;
                            }
                        } else {
                            if (arrayTags[j].equalsIgnoreCase(keyword)) {
                                isContains = true;
                                break;
                            }
                        }
                    }
                    //不匹配的item sort被标示为-1 将被过滤
                    if (!isContains) {
                        itemBean.setSearch_sort(-1);
                    }
                } else {
                    //不匹配的item sort被标示为-1 将被过滤
                    if (!keytag_str.equalsIgnoreCase(keyword)) {
                        itemBean.setSearch_sort(-1);
                    }
                }
            }

//            if (isSortChange) {
            //因为可能出现sort被改变或者sort相等的情况 所以重新排序
                Collections.sort(list, new Comparator<ItemBean>() {
                    @Override
                    public int compare(ItemBean lhs, ItemBean rhs) {
                        // TODO Auto-generated method stub
                        int search_sort = lhs.getSearch_sort() - rhs.getSearch_sort();
                        if(search_sort != 0){
                            return search_sort;
                        }else{//如果sort相等 则使用Category_id来排序
                            return (int)(lhs.getCategory_id()-rhs.getCategory_id());
                        }
                    }

                });
//            }
        }
	}
	
	
    public ArrayList<YelloPageItem> searchRaw(String keyword, String city, double longitude,
            double latitude, String category, int page) {
        this.mPage = page;
        ArrayList<YelloPageItem> list = new ArrayList<YelloPageItem>();

        YellowPageDB yellowPageDB = ContactsAppUtils.getInstance().getDatabaseHelper()
                .getYellowPageDBHelper();
        // 查询优先级：后台标签、名称、号码
        // 1、获取卡片内容

        // 2、获取本地应用服务数据
        List<ItemBean> putaoServerList = yellowPageDB.queryPutaoServerByKey(keyword,
                MAX_SEARCH_SERVER_NUM);
        filteringData(putaoServerList, keyword);
        if (putaoServerList != null && putaoServerList.size() > 0) {
            for (int i = 0; i < putaoServerList.size(); i++) {
                ItemBean itemBean = putaoServerList.get(i);
                if (itemBean.getSearch_sort() == -1) {
                    continue;
                }
                String icon = itemBean.getIcon();
                if (isExist(list, icon)) {
                    // 根据图片判断该条数据是否重复
                    continue;
                }
                String content = itemBean.getContent();
                PuTaoResultItem puTaoResultItem = new PuTaoResultItem();
                puTaoResultItem.setSource_type(PuTaoResultItem.SOURCE_TYPE_SERVER);
                if (!TextUtils.isEmpty(content)) {
                    YellowParams yellowParams = Config.mGson.fromJson(content, YellowParams.class);
                    puTaoResultItem.setIntent_url(yellowParams.getUrl());
                }
                String targetActivityName = itemBean.getTarget_activity();
                if (TextUtils.isEmpty(targetActivityName)) {
                    targetActivityName = YellowUtil.DefCategoryActivity;
                }
                puTaoResultItem.setPhotoUrl(icon);
                puTaoResultItem.setIntent_activity(targetActivityName);
                puTaoResultItem.setName(itemBean.getName());
                puTaoResultItem.setItemId(itemBean.getItem_id());
                puTaoResultItem.setCategory_id(itemBean.getCategory_id());
                puTaoResultItem.setRemind_code(itemBean.getRemind_code());
                YellowPageItemPutao putaoItem = new YellowPageItemPutao(puTaoResultItem);
                list.add(putaoItem);
            }
        }

        // 3、获取本地静态详情数据
        List<ItemBean> putaoDetailList = yellowPageDB.queryPutaoDetailByKey(keyword,
                MAX_SEARCH_DETAIL_NUM);
        filteringData(putaoDetailList, keyword);
        if (putaoDetailList != null && putaoDetailList.size() > 0) {
            for (int i = 0; i < putaoDetailList.size(); i++) {
                ItemBean itemBean = putaoDetailList.get(i);
                if (itemBean.getSearch_sort() == -1) {
                    continue;
                }
                String icon = itemBean.getIcon();
    
                if (isExist(list, icon)) {
                    // 根据图片判断该条数据是否重复
                    continue;
                }
                String content = itemBean.getContent();
                PuTaoResultItem puTaoResultItem = null;
                YellowParams params = null;
                if(!TextUtils.isEmpty(itemBean.getTarget_params())){
                    params = Config.mGson.fromJson(itemBean.getTarget_params(), YellowParams.class);
                }
                if(!TextUtils.isEmpty(content)){
                    puTaoResultItem = Config.mGson.fromJson(content, PuTaoResultItem.class);
                }else{
                    puTaoResultItem = new PuTaoResultItem();
                }
                puTaoResultItem.setSource_type(PuTaoResultItem.SOURCE_TYPE_DETAIL);
                puTaoResultItem.setPhotoUrl(icon);
                puTaoResultItem.setItemId(itemBean.getItem_id());
                puTaoResultItem.setRemind_code(itemBean.getRemind_code());
                puTaoResultItem.setIntent_activity(itemBean.getTarget_activity());
                if (params != null) {
                    puTaoResultItem.setWebsite(params.getUrl());
                    puTaoResultItem.setTitle(params.getTitle());
                }
                YellowPageItemPutao putaoItem = new YellowPageItemPutao(puTaoResultItem);
                list.add(putaoItem);

            }
        }

        mHasMore = false;

        mAllPutaoItemList.addAll(list);
        return list;
    }
	
	/**
	 * 判断数据是否存在
	 */
	private boolean isExist(List<YelloPageItem> itemList, String icon){
		for(int i = 0; i < itemList.size(); i++){
			if( icon.equals(itemList.get(i).getPhotoUrl()) ){
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean hasMore() {
		// TODO Auto-generated method stub
		return mHasMore;
	}

	@Override
	public int getPage() {
		// TODO Auto-generated method stub
		return mPage;
	}

}
