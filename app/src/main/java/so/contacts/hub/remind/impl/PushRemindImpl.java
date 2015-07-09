package so.contacts.hub.remind.impl;

import so.contacts.hub.db.YellowPageDB;
import so.contacts.hub.push.bean.PushRemindBean;
import so.contacts.hub.remind.BubbleRemindManager;
import so.contacts.hub.remind.IRemindApp;
import so.contacts.hub.remind.RemindConfig;
import so.contacts.hub.remind.RemindManager;
import so.contacts.hub.remind.RemindNode;
import so.contacts.hub.remind.utils.RemindUtils;
import so.contacts.hub.ui.yellowpage.bean.CategoryBean;
import so.contacts.hub.util.ContactsAppUtils;
import so.contacts.hub.util.LogUtil;
import android.text.TextUtils;

public class PushRemindImpl implements IRemindApp{
    private static final String TAG = "PushRemindImpl";
    
    @Override
    public void remindReceived(PushRemindBean r) {
        int category = r.getCode();
        LogUtil.i(TAG, "remindReceived "+r.toString());
        
        YellowPageDB db = ContactsAppUtils.getInstance().getDatabaseHelper().getYellowPageDBHelper();                
        if(RemindConfig.MyService == category) {
            myRemindReceived(r);
            return;
        }
        
        // 增加或更新root节点
        RemindNode rootNode = RemindUtils.addOrUpdateNode(RemindConfig.DiscoverPageNode, r, false);
        
        CategoryBean categoryBean = db.queryCategoryByCategoryId(category);
        if(categoryBean == null) {
            LogUtil.e(TAG, "remindReceived Can not founded category_id: "+category);
            return;
        }

        RemindNode remindNode = RemindUtils.addOrUpdateNode(categoryBean.getRemind_code(), r, false);   
        remindNode.setParent(RemindConfig.DiscoverPageNode);
        rootNode.addChild(categoryBean.getRemind_code());
        
        // 解析叶子节点
        String subCode = r.getSubCode();
        boolean hasChild = false;
        //是否包含正确的子节点
        if( !TextUtils.isEmpty(subCode) ){
        	hasChild = true;
        	String itemCodes[] = null;
        	try {
        		itemCodes = subCode.split(",");
        	} catch (Exception e) {
        		LogUtil.e(TAG, e.getMessage());
        		e.printStackTrace();
        	}
        	if(itemCodes != null && itemCodes.length > 0){
        		// 有子节点
        		for(String item : itemCodes) {
            		int itemId = parseInt(item);
            		if( itemId == -1 ){
            			continue;
            		}
            		CategoryBean bean = db.queryItemByItemId(itemId);
            		if(bean != null) {
            			RemindNode node = RemindUtils.addOrUpdateNode(bean.getRemind_code(), r, true);
            			node.setParent(categoryBean.getRemind_code());
            			remindNode.addChild(bean.getRemind_code());
            		}
            	}
        	}
        }
        
        if(remindNode.hasChild()) {
            RemindUtils.addOrUpdateNode(categoryBean.getRemind_code(), r, true);
            RemindUtils.addOrUpdateNode(RemindConfig.DiscoverPageNode, r, true);
            
            LogUtil.i(TAG, "save remind");
            RemindManager.getInstance().save();
        } else {
            // 没有子节点,什么都不做，返回
            return;
        }
    }    
    
    /**
     * 我的功能数据解析
     */
    private void myRemindReceived(PushRemindBean r) {
        if(r == null)
            return;
        int category = r.getCode();
        
        // 增加或更新root节点
        RemindNode rootNode = RemindUtils.addOrUpdateNode(RemindConfig.DiscoverPageNode, r, false);
        
        YellowPageDB db = ContactsAppUtils.getInstance().getDatabaseHelper().getYellowPageDBHelper();                

        CategoryBean categoryBean = db.queryCategoryByCategoryId(category);
        if(categoryBean == null) {
            LogUtil.e(TAG, "myRemindReceived Can not founded category_id: "+category);
        }
        int remindCode = categoryBean.getRemind_code();

        RemindNode remindNode = RemindUtils.addOrUpdateNode(remindCode, r, false);
        remindNode.setParent(RemindConfig.DiscoverPageNode);
        rootNode.addChild(remindCode);
        
        // 解析叶子节点
        String itemCodes[] = null;
        String subCode = r.getSubCode();
        boolean hasChild = false;
        if( !TextUtils.isEmpty(subCode) ){
        	hasChild = true;
        	try {
        		itemCodes = subCode.split(",");
        	} catch (Exception e) {
        		LogUtil.e(TAG, e.getMessage());
        		e.printStackTrace();
        	}
        	
        	if(itemCodes != null && itemCodes.length > 0) {
        		for(String item : itemCodes) {
        			int itemId = parseInt(item);        			
        			if(itemId == RemindConfig.MyOrder) {
        				RemindNode myServiceNode = RemindUtils.addOrUpdateNode(itemId, r, true);
        				myServiceNode.setParent(remindCode);
        				remindNode.addChild(itemId);
        				
        			} else if(itemId == RemindConfig.MyFavorite) {
        				RemindNode myServiceNode = RemindUtils.addOrUpdateNode(itemId, r, true);
                        myServiceNode.setParent(remindCode);
                        remindNode.addChild(itemId);

        			} else if(itemId == RemindConfig.MyHistory) {
        				RemindNode myServiceNode = RemindUtils.addOrUpdateNode(itemId, r, true);
                        myServiceNode.setParent(remindCode);
                        remindNode.addChild(itemId);

        			} else if(itemId == RemindConfig.MyOrderTuan) {
                        RemindNode myServiceNode = RemindUtils.addOrUpdateNode(itemId, r, true);
                        myServiceNode.setParent(remindCode);
                        remindNode.addChild(itemId);

        			} else if(itemId == RemindConfig.MyOrderHotel) {
                        RemindNode myServiceNode = RemindUtils.addOrUpdateNode(itemId, r, true);
                        myServiceNode.setParent(remindCode);
                        remindNode.addChild(itemId);

                    } else if(itemId == RemindConfig.MyOrderChargeHistory) {
                        RemindNode myServiceNode = RemindUtils.addOrUpdateNode(itemId, r, true);
                        myServiceNode.setParent(remindCode);
                        remindNode.addChild(itemId);

                    }  else if(itemId == RemindConfig.MyActivies) {
                        RemindNode myServiceNode = RemindUtils.addOrUpdateNode(itemId, r, true);
                        myServiceNode.setParent(remindCode);
                        remindNode.addChild(itemId);

                    }	else if(itemId == RemindConfig.MyTongchengTrain) {
                        RemindNode myServiceNode = RemindUtils.addOrUpdateNode(itemId, r, true);
                        myServiceNode.setParent(remindCode);
                        remindNode.addChild(itemId);

                    }	    
        		}
        	}else{
        		hasChild = false;
        	}
        }
        if( !hasChild ){
        	RemindUtils.addOrUpdateNode(remindCode, r, true);
        }
        
        LogUtil.i(TAG, "save remind");
        RemindManager.getInstance().save();
    }
    
    /**
     * 气泡push数据接收
     */
    @Override
    public void bubbleReceived(PushRemindBean r) {
        int category = r.getCode();
        LogUtil.i(TAG, "remindReceived category=" + category);
        
        YellowPageDB db = ContactsAppUtils.getInstance().getDatabaseHelper().getYellowPageDBHelper();
        
        // 增加或更新root节点
        CategoryBean categoryBean = db.queryCategoryByCategoryId(category);
        if(categoryBean == null) {
            LogUtil.e(TAG, "bubbleReceived Can not founded category_id: "+category);
            return;
        }

        // 解析叶子节点
        String subCode = r.getSubCode();
        boolean hasChild = false;
        if( !TextUtils.isEmpty(subCode) ){
        	hasChild = true;
        	String itemCodes[] = null;
        	try {
        		itemCodes = subCode.split(",");
        	} catch (Exception e) {
        		LogUtil.e(TAG, e.getMessage());
        		e.printStackTrace();
        	}
        	
        	if(itemCodes != null && itemCodes.length > 0) {
        		for(String item : itemCodes) {
        			int itemId = parseInt(item);
        			if(itemId == -1)
        			    continue;
        			CategoryBean bean = db.queryItemByItemId(itemId);
        			if(bean != null) {
        				RemindUtils.addOrUpdateBubbleNode(bean.getRemind_code(), r);
        			}
        		}
        	}else{
        		hasChild = false;
        	}
        }
        if( !hasChild ){
            RemindUtils.addOrUpdateBubbleNode(categoryBean.getRemind_code(), r);
        }
        
        LogUtil.i(TAG, "save bubble");
        BubbleRemindManager.getInstance().save();
        BubbleRemindManager.getInstance().dumps();
    }
    
    private int parseInt(String word) {
        int num = -1;
        try {
            num = Integer.parseInt(word);
        }catch(Exception e){
            num = -1;
        }
        return num;
    }

}
