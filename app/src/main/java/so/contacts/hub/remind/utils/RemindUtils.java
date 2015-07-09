/**
 * @date	: 
 * @author	:
 * @descrip	:
 */
package so.contacts.hub.remind.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

import so.contacts.hub.ContactsApp;
import so.contacts.hub.core.ConstantsParameter;
import so.contacts.hub.db.YellowPageDB;
import so.contacts.hub.push.bean.PushRemindBean;
import so.contacts.hub.remind.BubbleRemindManager;
import so.contacts.hub.remind.RemindConfig;
import so.contacts.hub.remind.RemindManager;
import so.contacts.hub.remind.RemindNode;
import so.contacts.hub.ui.yellowpage.bean.CategoryBean;
import so.contacts.hub.util.ContactsAppUtils;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.yellow.data.RemindBean;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.util.Base64;

public class RemindUtils {
    private static final String TAG = "RemindUtils";

    /**
     * 处理游戏打点通知,把游戏中心根节点的remind信息保存在RemindManager中管理,
     * 把子游戏的打点和访问属性保存在RemindManager中统一管理。
     */    
    public static final void doRemindRequest(final boolean sendBroadcast) {/*
        LogUtil.i(TAG, "doRemindRequest");

        DiscoverRemindResponse responseData = null;

        final DiscoverRemindRequest requestData = new DiscoverRemindRequest();
        IgnitedHttpResponse httpResponse;
        try {
            httpResponse = Config.getApiHttp().post(Config.SERVER, requestData.getData()).send();
            String content = httpResponse.getResponseBodyAsString();
            LogUtil.v(TAG, "content="+content);
            responseData = requestData.getObject(content);
            if (responseData != null) {
                if (responseData.isSuccess()) {
                    LogUtil.d(TAG, "doRemindRequest success");
                    LogUtil.v(TAG, responseData.toString());
                    for (Remind r : responseData.getRemind_list()) {
                        if(r.getRemind_code() == RemindConfig.GameCenterRemindCode) {
                            IRemindApp app = RemindFactory.create(IRemindApp.RemindType.GameCenter);
                            app.remindReceived(r);
                            RemindUtils.setGameRemindFlag(true);
                            
                            // 检查游戏打点信息是否更新
                            if(RemindUtils.getGameRemindFlag()) {
                                LogUtil.d(TAG,"update game remind ... ");
                                RemindManager.getInstance().reload();
                                RemindUtils.setGameRemindFlag(false);
                            }
                        }
                    }
                    
                    if(sendBroadcast) {
                        ContactsApp.getInstance().sendBroadcast(new Intent(PlugService.ACTION_REMIND_UPDATE));
                    }
                    
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(TAG, "doRemindRequest " + e.getMessage());
        }
        return;
    */}

    /**
     * 解析打点协议扩展字段expand_param
     */
    public static List<String> parseChilds(String expand_param) {
        if (expand_param == null || expand_param.length() == 0)
            return null;

        List<String> childs = new ArrayList<String>();

        TextUtils.StringSplitter splitter = new TextUtils.SimpleStringSplitter(',');
        splitter.setString(expand_param);
        for (String s : splitter) {
            childs.add(s);
        }

        return childs;
    }

    // 设置动态打点更新标志
    public static void setSnsRemindFlag(boolean updated) {
        SharedPreferences pref = ContactsApp.getInstance().getSharedPreferences(
                RemindConfig.REMIND_CONFIG, Context.MODE_MULTI_PROCESS);
        Editor editor = pref.edit();
        editor.putBoolean(RemindConfig.SnsDynmaicRemindUpdateFlag, updated);
        editor.commit();
    }    
    
    // 设置动态打点更新标志
    public static boolean getSnsRemindFlag(boolean defval) {
        SharedPreferences pref = ContactsApp.getInstance().getSharedPreferences(
                RemindConfig.REMIND_CONFIG, Context.MODE_MULTI_PROCESS);
        
        return pref.getBoolean(RemindConfig.SnsDynmaicRemindUpdateFlag, defval);
    }
    
    // 设置游戏中心打点更新标志
    public static void setGameRemindFlag(boolean updated) {
        SharedPreferences pref = ContactsApp.getInstance().getSharedPreferences(
                RemindConfig.REMIND_CONFIG, Context.MODE_MULTI_PROCESS);
        Editor editor = pref.edit();
        editor.putBoolean(RemindConfig.GameCeterRemindUpdateFlag, updated);
        editor.commit();
    }
        
    // 返回游戏中心打点更新标志
    public static boolean getGameRemindFlag() {
        SharedPreferences pref = ContactsApp.getInstance().getSharedPreferences(
                RemindConfig.REMIND_CONFIG, Context.MODE_MULTI_PROCESS);
        
        return pref.getBoolean(RemindConfig.GameCeterRemindUpdateFlag, false);
    }
    
    // 返回游戏中心打点更新标志
    public static boolean getGameRemindFlag(final Context context) {
        SharedPreferences pref = context.getSharedPreferences(
                RemindConfig.REMIND_CONFIG, Context.MODE_MULTI_PROCESS);
        
        return pref.getBoolean(RemindConfig.GameCeterRemindUpdateFlag, false);
    }

    public static Object convertBase64StringToObj(String base64str) {
        if(base64str == null || "".equals(base64str)) 
            return null;

        Object obj = null;
        byte[] base64 = Base64.decode(base64str.getBytes(), Base64.DEFAULT);
        // 封装到字节流
        ByteArrayInputStream bais = new ByteArrayInputStream(base64);
        try {
            ObjectInputStream bis = new ObjectInputStream(bais);
            obj = bis.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            LogUtil.e(TAG, e.getMessage());
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
            LogUtil.e(TAG, e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.e(TAG, e.getMessage());
        }
        return obj;
    }
    
    
    public static boolean removeBubbuleRemind(Integer remindCode) {
        BubbleRemindManager.getInstance().clearNode(remindCode);
        return true;
    }

    /**
     * 得到发现页打点类型和数字
     */
    public static RemindBean getDiscoverRemind() {
        return getRemind(RemindConfig.DiscoverPageNode);
    }    
    
    /**
     * 返回打点数据，若没有则返回气泡数据
     * @param remindCode
     * @return
     */
    public static RemindBean getRemind(Integer remindCode) {
        // 增加首页Tab打点的控制
        if(remindCode == RemindConfig.DiscoverPageNode && !RemindUtils.isTabRemind()) {
            return null;
        }
        
        RemindBean bean = RemindUtils.getRemindPrv(remindCode);
        if(bean == null || bean.getRemindType() <= RemindConfig.REMIND_TYPE_NONE) {
            bean = RemindUtils.getBubbleRemind(remindCode);
            if(bean == null || bean.getRemindType() != RemindConfig.REMIND_TYPE_TIME_CLEAN) {
                return null;
            }
        }
        if(bean != null) {
            LogUtil.v(TAG, "getRemind remindCode:"+remindCode+" node:"+bean.toString());
        }
        return bean;
    }

    /**
     * 返回气泡数据
     * @param remindCode
     * @return
     */
    public static RemindBean getBubbleRemind(Integer remindCode) {

        RemindBean bean = null;
        RemindNode node = BubbleRemindManager.getInstance().get(remindCode);
        if(node == null || node.getType() != RemindConfig.REMIND_TYPE_TIME_CLEAN) {
            return null;
        } else if(node.isExpired()) {
            LogUtil.i(TAG, "getBubbleRemind remindCode="+remindCode+" has expired");
            BubbleRemindManager.getInstance().clearNode(remindCode);
            return null;
        }
        
        bean = new RemindBean();
        bean.setRemindType(node.getType());
        bean.setRemindCode(node.getRemindCode());
        bean.setStyle(node.getStyle());
        bean.setImgUrl(node.getImgUrl());
        bean.setClicked(node.isClicked());
        bean.setText(node.getText());
        bean.setEndTime(node.getEndTime());
        return bean;
    }

    /**
     * 返回打点数据
     * @param remindCode
     * @return
     */
    public static RemindBean getRemindPrv(Integer remindCode) {
        RemindBean bean = null;
        RemindNode node = RemindManager.getInstance().get(remindCode);
        
        if(node != null) {
            int type = RemindManager.getInstance().remindType(node);
            bean = new RemindBean();
            bean.setRemindType(type);
            bean.setRemindCode(node.getRemindCode());
            bean.setClicked(node.isClicked());
            bean.setImgUrl(node.getImgUrl());
            bean.setText(node.getText());
            bean.setStyle(node.getStyle());
            switch(type){
                case RemindConfig.REMIND_TYPE_VIEW_CLEAN:
                    if(!node.isClicked()) {
                        bean.setRemindType(RemindConfig.REMIND_TYPE_VIEW_CLEAN);
                    } else {
                        bean.setClicked(true);
                        return null;
                    }
                    break;
                    
                case RemindConfig.REMIND_TYPE_VIEW_NUMBER:
                    if(!node.isClicked()) {
//                        int number = RemindManager.getInstance().remindCount(node);
                        int number = RemindManager.getInstance().remindCountR(node);

                        bean.setRemindType(RemindConfig.REMIND_TYPE_VIEW_NUMBER);
                        bean.setRemindCount(number);
                    } else {
                        bean.setClicked(true);
                        return null;
                    }
                    break;
                    
                case RemindConfig.REMIND_TYPE_CLICK_CLEAN:
                    bean.setRemindType(RemindConfig.REMIND_TYPE_CLICK_CLEAN);
                    bean.setRemindCount(0);
                    break;
                    
                case RemindConfig.REMIND_TYPE_CLICK_NUMBER:                   
                    int number = RemindManager.getInstance().remindCountR(node);
                    bean.setRemindType(RemindConfig.REMIND_TYPE_CLICK_NUMBER);
                    bean.setRemindCount(number);

                    break;
                default:
                    return null;
            }
        }
        return bean;
    }
    
    // 打点节点增加或更新
    public static RemindNode addOrUpdateNode(int remindCode, PushRemindBean r, boolean isChild) {
        RemindNode node = RemindManager.getInstance().get(remindCode);
        if(node == null) {
            node = new RemindNode(remindCode, r.getType());
            node.setClicked(false);
            if(isChild) {
                node.setStyle(r.getStyle());
                node.setImgUrl(r.getImg_url());
                node.setEndTime(r.getTime());
                node.setText(r.getText());
            }

            RemindManager.getInstance().put(node);
            
            LogUtil.d(TAG, "add node for remindCode: " + node.toString());
            
        } else {
            node.setType(r.getType());
            node.setClicked(false);
            
            if(isChild) {
                node.setStyle(r.getStyle());
                node.setImgUrl(r.getImg_url());
                node.setEndTime(r.getTime());
                node.setText(r.getText());
            }

            LogUtil.d(TAG, "update node for remindCode: " + node.toString());
        }
        
        return node;
    }    
    
    /**
     * 增加我的里面各服务的打点信息数据结构
     * @param remindCode
     */
    public static void addMyServiceRemind(int remindCode, boolean sendBroadcast) {
        LogUtil.i(TAG, "addMyServiceRemind remindCode="+remindCode+" "+sendBroadcast);

        int type = RemindConfig.REMIND_TYPE_VIEW_CLEAN;
        
        if(remindCode >= RemindConfig.MyOrder && remindCode < RemindConfig.MaxMyServiceRemindCode) {
            YellowPageDB db = ContactsAppUtils.getInstance().getDatabaseHelper().getYellowPageDBHelper();
            
            CategoryBean myBean = db.queryCategoryByCategoryId(RemindConfig.MyService);
            if(myBean == null)
                return;
            
            PushRemindBean root = new PushRemindBean();
            root.setCode(RemindConfig.DiscoverPageNode);
            root.setType(type);
            RemindNode rootNode = RemindUtils.addOrUpdateNode(root.getCode(), root, false);

            PushRemindBean my = new PushRemindBean();
            my.setCode(RemindConfig.MyService);
            my.setType(type);
            RemindNode myNode = RemindUtils.addOrUpdateNode(myBean.getRemind_code(), my, false);
            myNode.setParent(root.getCode());
            rootNode.addChild(myBean.getRemind_code());
            
//            PushRemindBean myorder = new PushRemindBean();
//            myorder.setCode(RemindConfig.MyOrder);
//            myorder.setType(type);
//            RemindNode myOrderNode = RemindUtils.addOrUpdateNode(myorder.getCode(), myorder, false);
//            myOrderNode.setParent(myBean.getRemind_code());
//            myNode.addChild(myorder.getCode());
            if(remindCode==RemindConfig.MyMsgCenter){
                type = RemindConfig.REMIND_TYPE_CLICK_NUMBER;
            }
            PushRemindBean child = new PushRemindBean();
            child.setType(type);
            RemindNode childNode = RemindUtils.addOrUpdateNode(remindCode, child, true);
            childNode.setParent(myNode.getRemindCode());
            myNode.addChild(remindCode);
            
            RemindManager.getInstance().save();
            RemindManager.getInstance().dumps("addMyServiceRemind remindCode="+remindCode);
            
            if(sendBroadcast) {
                ContactsApp.getInstance().sendBroadcast(new Intent(ConstantsParameter.ACTION_REMIND_UPDATE));        
                ContactsApp.getInstance().sendBroadcast(new Intent(ConstantsParameter.ACTION_REMIND_UPDATE_PLUG));        
            }
        }
    }
    
    /**
     * 增加首页里各服务的打点信息数据结构
     * @param remindCode
     */
    public static void addServiceRemind(int remindCode, boolean sendBroadcast) {
        LogUtil.i(TAG, "addServiceRemind remindCode="+remindCode+" "+sendBroadcast);
        int type = RemindConfig.REMIND_TYPE_VIEW_CLEAN;

        PushRemindBean root = new PushRemindBean();
        root.setCode(RemindConfig.DiscoverPageNode);
        root.setType(type);
        RemindNode rootNode = RemindUtils.addOrUpdateNode(RemindConfig.DiscoverPageNode, root, false);

        RemindNode serviceNode = RemindUtils.addOrUpdateNode(remindCode, root, true);
        serviceNode.setParent(RemindConfig.DiscoverPageNode);
        rootNode.addChild(remindCode);

        RemindManager.getInstance().save();
        RemindManager.getInstance().dumps("addServiceRemind remindCode=" + remindCode);

        if (sendBroadcast) {
            ContactsApp.getInstance().sendBroadcast(
                    new Intent(ConstantsParameter.ACTION_REMIND_UPDATE));
            ContactsApp.getInstance().sendBroadcast(
                    new Intent(ConstantsParameter.ACTION_REMIND_UPDATE_PLUG));
            }
    }    
    
    public static void addRemindBySelf(int type, int remindCode, boolean isMyService) {
        LogUtil.i(TAG, " type="+type+" remindCode="+remindCode+" isMyService="+isMyService);
        
        if(type == RemindConfig.REMIND_ADD) {  // 增加打点
            if(isMyService) 
                RemindUtils.addMyServiceRemind(remindCode, true);
            else 
                RemindUtils.addServiceRemind(remindCode, true);
            
        } else if(type == RemindConfig.REMIND_UPDATE) { // 更新打点
            
        } else if(type == RemindConfig.REMIND_DELETE) { // 删除打点
            
        }

    }
    
    
    /**
     * 酷派V1.7.0x增加默认的几个热图
     * “酒店15”“美食3”“租房13”
     */
    public static void initDefaultBubbleForV17xx() {
        PushRemindBean r = new PushRemindBean();
        long offset = 3122064000000L; // 99年的毫秒数
//        long offset = 1*60*1000;
        r.setTime(System.currentTimeMillis()+offset);
        r.setStyle(RemindConfig.REMIND_STYLE_RECOMMENT);
        r.setType(RemindConfig.REMIND_TYPE_TIME_CLEAN);
        
        YellowPageDB db = ContactsAppUtils.getInstance().getDatabaseHelper().getYellowPageDBHelper();
        
        // 查询cateogry表是否存在
        // 酒店15
        int category_id = 15;
        CategoryBean cateogry = db.queryCategoryByCategoryId(category_id);
        if(cateogry == null) {
            LogUtil.e(TAG, "Can not founded category_id: "+category_id);
        } else {
            addOrUpdateBubbleNode(cateogry.getRemind_code(), r);
        }
        
//        // 查美食
//        category_id = 3;
//        cateogry = db.queryCategoryByCategoryId(category_id);
//        if(cateogry == null) {
//            LogUtil.e(TAG, "Can not founded category_id: "+category_id);
//        } else {
//            addOrUpdateBubbleNode(cateogry.getRemind_code(), r);
//        }
//        
//        // 查出行订票
//        category_id = 8;
//        cateogry = db.queryCategoryByCategoryId(category_id);
//        if(cateogry == null) {
//            LogUtil.e(TAG, "Can not founded category_id: "+category_id);
//        } else {
//            addOrUpdateBubbleNode(cateogry.getRemind_code(), r);
//        }

        // 美食3
        category_id = 3;
        cateogry = db.queryCategoryByCategoryId(category_id);
        if(cateogry == null) {
            LogUtil.e(TAG, "Can not founded category_id: "+category_id);
        } else {
            addOrUpdateBubbleNode(cateogry.getRemind_code(), r);
        }

        // 租房13
        category_id = 13;
        cateogry = db.queryCategoryByCategoryId(category_id);
        if(cateogry == null) {
            LogUtil.e(TAG, "Can not founded category_id: "+category_id);
        } else {
            addOrUpdateBubbleNode(cateogry.getRemind_code(), r);
        }
        
//        // 电影56 - 选座
//        category_id = 56;
//        cateogry = db.queryCategoryByCategoryId(category_id);
//        if(cateogry == null) {
//            LogUtil.e(TAG, "Can not founded category_id: "+category_id);
//        } else {
//            r.setText("选座");
//            addOrUpdateBubbleNode(cateogry.getRemind_code(), r);
//        }

        BubbleRemindManager.getInstance().save();
        BubbleRemindManager.getInstance().dumps("Initialize default bubble.");
    }
    
    // 打点节点增加或更新
    public static RemindNode addOrUpdateBubbleNode(int remindCode, PushRemindBean r) {
        RemindNode node = BubbleRemindManager.getInstance().get(remindCode);
        if(node == null) {
            node = new RemindNode(remindCode, r.getType());
            node.setImgUrl(r.getImg_url());
            node.setStyle(r.getStyle());
            node.setEndTime(r.getTime());
            node.setText(r.getText());
            node.setClicked(false);

            BubbleRemindManager.getInstance().put(node);
            
            LogUtil.d(TAG, "add node for remindCode: " + node.toString());
            
        } else {
            node.setType(r.getType());
            node.setClicked(false);
            node.setImgUrl(r.getImg_url());
            node.setStyle(r.getStyle());
            node.setEndTime(r.getTime());
            node.setText(r.getText());

            LogUtil.d(TAG, "update node for remindCode: " + node.toString());
        }
        
        return node;
    }        
    
    // 检查是否加载默认气泡
    public static boolean isLoadDefBubbles() {
        SharedPreferences pref = ContactsApp.getInstance().getSharedPreferences(
                ConstantsParameter.CONTACTS_SETTING, Context.MODE_MULTI_PROCESS);

        return pref.getBoolean(ConstantsParameter.DEF_BUBBLES, true);
    }
    
    // 保存默认加载气泡标志
    public static void setDefBubles(boolean flag) {
        SharedPreferences pref = ContactsApp.getInstance().getSharedPreferences(
                ConstantsParameter.CONTACTS_SETTING, Context.MODE_MULTI_PROCESS);

        pref.edit().putBoolean(ConstantsParameter.DEF_BUBBLES, flag).commit();
    }
    
    // 检查是否在tab页上打点
    public static boolean isTabRemind() {
        SharedPreferences pref = ContactsApp.getInstance().getSharedPreferences(
                ConstantsParameter.CONTACTS_SETTING, Context.MODE_MULTI_PROCESS);

        return pref.getBoolean(ConstantsParameter.TAB_REMIND_FLAG, false);
    }
    
    // 保存tab页打点标志
    public static void setTabRemind(boolean flag) {
        SharedPreferences pref = ContactsApp.getInstance().getSharedPreferences(
                ConstantsParameter.CONTACTS_SETTING, Context.MODE_MULTI_PROCESS);

        pref.edit().putBoolean(ConstantsParameter.TAB_REMIND_FLAG, flag).commit();
    }
    
    // 获取打点、气泡相关push信息版本
    public static int getRemindVersion() {
        SharedPreferences pref = ContactsApp.getInstance().getSharedPreferences(
                ConstantsParameter.CONTACTS_SETTING, Context.MODE_MULTI_PROCESS);

        return pref.getInt(ConstantsParameter.REMIND_VERSION, 0);
    }
    
    // 保存打点、气泡相关push信息版本
    public static void setRemindVersion(int version) {
        SharedPreferences pref = ContactsApp.getInstance().getSharedPreferences(
                ConstantsParameter.CONTACTS_SETTING, Context.MODE_MULTI_PROCESS);

        pref.edit().putInt(ConstantsParameter.REMIND_VERSION, version).commit();
    }
    
    /**
     * 远程配置打点最大显示数
     * @param count
     * @author putao_lhq
     */
    public static void setRemindMaxCount(int count) {
    	SharedPreferences pref = ContactsApp.getInstance().getSharedPreferences(
                ConstantsParameter.CONTACTS_SETTING, Context.MODE_MULTI_PROCESS);

        pref.edit().putInt(ConstantsParameter.REMIND_MAX_COUNT, count).commit();
    }
    
    /**
     * 获取远程配置打点最大显示数
     * @return
     * @author putao_lhq
     */
    public static int getRemindMaxCount() {
        SharedPreferences pref = ContactsApp.getInstance().getSharedPreferences(
                ConstantsParameter.CONTACTS_SETTING, Context.MODE_MULTI_PROCESS);

        return pref.getInt(ConstantsParameter.REMIND_MAX_COUNT, 6);
    }
    
    // 获取alarmManager的唤醒间隔时间,默认0关闭
    public static int getAlarmDelay() {
        SharedPreferences pref = ContactsApp.getInstance().getSharedPreferences(
                ConstantsParameter.CONTACTS_SETTING, Context.MODE_MULTI_PROCESS);

        return pref.getInt(ConstantsParameter.ALARM_WAKEUP_TIME, 0);
    }
    
    // 设置alarmManager的唤醒间隔时间
    public static void setAlarmDelay(int alarm_m_s) {
        SharedPreferences pref = ContactsApp.getInstance().getSharedPreferences(
                ConstantsParameter.CONTACTS_SETTING, Context.MODE_MULTI_PROCESS);

        pref.edit().putInt(ConstantsParameter.ALARM_WAKEUP_TIME, alarm_m_s).commit();
    }
    
    /**
     * 插入消息中心虚拟节点
     * @author xcx
     */
    public static void addMsgVirtualNode(){
        RemindNode node = RemindManager.getInstance().get(RemindConfig.MyMsgCenter);
        if(null==node){
            addMyServiceRemind(RemindConfig.MyMsgCenter, true);
            node = RemindManager.getInstance().get(RemindConfig.MyMsgCenter);
            if(null==node){
                return;
            }
        }
        node.addChild(RemindConfig.REMIND_VIRTUAL_NODE_CODE);
        ContactsApp.getInstance().sendBroadcast(new Intent(ConstantsParameter.ACTION_REMIND_UPDATE));        
        ContactsApp.getInstance().sendBroadcast(new Intent(ConstantsParameter.ACTION_REMIND_UPDATE_PLUG));
    }
    
    public static void clearMsgNode(){
        RemindManager.getInstance().clearNodeWithVirtualChildNodes(RemindConfig.MyMsgCenter);
        ContactsApp.getInstance().sendBroadcast(new Intent(ConstantsParameter.ACTION_REMIND_UPDATE));        
        ContactsApp.getInstance().sendBroadcast(new Intent(ConstantsParameter.ACTION_REMIND_UPDATE_PLUG));
    }
}
