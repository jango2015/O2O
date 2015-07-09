package so.contacts.hub.remind;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import so.contacts.hub.ContactsApp;
import so.contacts.hub.gamecenter.utils.SharedPreferenceUtils;
import so.contacts.hub.util.LogUtil;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

/**
 * @author change
 *
 */
public class RemindManager {
    private static final String TAG = "RemindManager";

    private static RemindManager mInstance = null;  // 可以使用弱引用优化
    public static final String REMIND_NODE_MAPS = "REMIND_NODE_MAPS"; 
        
    private HashMap<Integer, RemindNode> mRemindMaps = null;  // category_id <==> RemindNode
    
    private RemindUpdateListener mListener = null;   
    private volatile boolean changed; // 记录内存更新是否持久化
    
    private int mPersentFlag = 1; // 0-db 1-sharePerfernce xml 
    
    private static Context mContext = null;

    public static RemindManager getInstance() {
        return getInstance(ContactsApp.getInstance());
    }
    
    public static RemindManager getInstance(Context ctx) {
        mContext = ctx;
        if(mInstance == null) {
            synchronized (RemindManager.class) {
                if(mInstance == null)
                    mInstance = new RemindManager();
            }
        }
        return mInstance;
    }    
    
    private RemindManager() {
        changed = false;
        reload();
    }
    
    public void setListener(RemindUpdateListener l) {
        mListener = l;
    }

    public static Remind generateRemind(int remind_code, int type, String logo, String expand_param) {
        Remind r = new Remind();
        r.setRemind_code(remind_code);
        r.setType(type);
        r.setLogo(logo);
        r.setExpand_param(expand_param);
        
        return r;
    }
    
    public static boolean addChildRemind(Integer parent, RemindNode child) {
        RemindNode parentNode = RemindManager.getInstance().get(parent);
        if(parentNode != null) {
            RemindManager.getInstance().put(child);
            child.setParent(parent);
            parentNode.addChild(child);
            return true;
        }
        return false;
    }
    
    /**
     * 进入该activity，模拟点击当前节点
     */
    public static void onRemindClick(Integer remindCode) {
        LogUtil.d(TAG, "onRemindClick remindCode="+remindCode);
        
        if(remindCode != null && remindCode >= 0) {
            RemindNode curNode = RemindManager.getInstance().get(remindCode);
            if (curNode != null) {
                curNode.setClicked(true);
                RemindManager.getInstance().hasUpdate();
                /**
                 * 如果当前Activity是最末端叶子节点，那么进入该activity后，看成节点已访问(包括查看和点击)，则删除该节点
                 */
                if (curNode.getChilds() == null || curNode.getChilds().size() == 0) {
                    RemindNode parentNode = RemindManager.getInstance().get(curNode.getParent());
                    if (parentNode != null) {
                        parentNode.removeChild(curNode.getRemindCode());
                    }
                    
                    RemindManager.getInstance().clearNode(curNode);
                    
                    LogUtil.d(TAG, "remove node: " + curNode.getRemindCode());

                } else {/*
                    Iterator<String> it = curNode.getChilds().iterator();
                    while (it.hasNext()) {
                        RemindNode childNode = RemindManager.getInstance().get(it.next());
                        if(childNode != null) {
                            switch(childNode.getType()) 
                            {
                                case RemindConfig.REMIND_TYPE_VIEW_CLEAN:
                                case RemindConfig.REMIND_TYPE_VIEW_NUMBER:
                                    // 对于查看消除类型，访问父节点时，如果子节点是叶子节点则删除子节点
                                    if (childNode.getChilds() == null || childNode.getChilds().size() == 0) {
                                        it.remove();
                                        RemindManager.getInstance().clearNode(childNode);
                                        LogUtil.d(TAG, "  remove child node: "+childNode.getUniName());
                                    }
                                    break;
                                case RemindConfig.REMIND_TYPE_CLICK_CLEAN:
                                case RemindConfig.REMIND_TYPE_CLICK_NUMBER:
                                    break;
                                    // 对于点击消除类型，访问父节点时不需要动作
                            }
                        }
                    }
                */} // End if (curNode.getChilds() == null || curNode.getChilds().size() == 0) {
            }
        }
    }
    
    public void hasUpdate() {
        changed = true;
    }
    
    public static boolean isRootNode(Integer remindCode) {
        if(RemindConfig.DiscoverPageNode == remindCode)
            return true;
        return false;
    }
    
    // 单例启动时会加载一次，后续需要手动加载
    public synchronized void reload() {
        final int oldsize = (mRemindMaps==null || mRemindMaps.size()==0)?0:mRemindMaps.size();
        if(mRemindMaps != null)
            mRemindMaps.clear();
        
        if(mPersentFlag == 0) {
            loadDb();
        } else {
            loadPref();
        }
        
        if(mRemindMaps == null) {
            mRemindMaps = new HashMap<Integer, RemindNode>();
        }
        
        if(oldsize==0 && mRemindMaps.size()==0)
            changed = false;
        LogUtil.d(TAG, "reload mPersentFlag="+mPersentFlag+" size="+mRemindMaps.size()+" update="+changed);
    }
    
    /**
     * 从配置文件中加载游戏列表和详情到mRemindMaps中
     */
    private synchronized void loadPref() {
        SharedPreferences pref = mContext.getSharedPreferences(RemindConfig.REMIND_CONFIG,
                Context.MODE_MULTI_PROCESS);

        if (!pref.contains(REMIND_NODE_MAPS)) {
            LogUtil.d(TAG, "loadPref not found remindata");
            return;
        }

        String v = pref.getString(REMIND_NODE_MAPS, "");
        LogUtil.v(TAG, "loadPref value="+v.length());

        byte[] base64 = Base64.decode(v.getBytes(), Base64.DEFAULT);
        // 封装到字节流
        ByteArrayInputStream bais = new ByteArrayInputStream(base64);
        try {
            ObjectInputStream bis = new ObjectInputStream(bais);
            mRemindMaps = (HashMap<Integer, RemindNode>)bis.readObject();
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
    }
       
    private synchronized void loadDb() {/*
        String v = Config.getDatabaseHelper().getRemindDB().getNodes();
        if (v == null || "".equals(v)) {
            LogUtil.d(TAG, "loadDb not found remindata");            
            return;
        }
        
        LogUtil.v(TAG, "loadDb value="+v.length());
        byte[] base64 = Base64.decode(v.getBytes(), Base64.DEFAULT);
        // 封装到字节流
        ByteArrayInputStream bais = new ByteArrayInputStream(base64);
        try {
            ObjectInputStream bis = new ObjectInputStream(bais);
            mRemindMaps = (HashMap<String, RemindNode>)bis.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            LogUtil.e(TAG, e.getMessage());
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
            LogUtil.e(TAG, e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.e(TAG, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(TAG, e.getMessage());
        }
        if(mListener != null) 
            mListener.remindNodeChanged();        
    */}
    
    /**
     * 将内存对象mRemindMaps保存到配置文件中
     */
    public synchronized void save() {
        if(mPersentFlag == 0) {
            saveDb();
        } else {
            savePref();
        }
        
        if(changed && mListener != null) {
            mListener.remindNodeChanged();
            changed = false;
        }
    }
    
    private synchronized void savePref() {
        if(mRemindMaps == null) {
            LogUtil.d(TAG, "savePref reminddata is null");
            return;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(mRemindMaps);
            String productBase64 = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
            LogUtil.v(TAG, "savePref value="+productBase64.length());
            
            SharedPreferenceUtils.setPreference(RemindConfig.REMIND_CONFIG, 
                    REMIND_NODE_MAPS, productBase64, mContext.MODE_MULTI_PROCESS);
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.e(TAG, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(TAG, e.getMessage());
        }
    }
    
    private synchronized void saveDb() {/*
        if(mRemindMaps == null) {
            LogUtil.d(TAG, "saveDb reminddata is null");
            return;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(mRemindMaps);
            String productBase64 = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
            LogUtil.v(TAG, "saveDb value="+productBase64.length());
            
            Config.getDatabaseHelper().getRemindDB().save(productBase64);
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.e(TAG, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(TAG, e.getMessage());
        }
    */}
    
    
    public synchronized int size() {
        return mRemindMaps.size();
    }
    
    public synchronized RemindNode get(Integer remindCode) {
        if(remindCode == null)
            return null;
        return mRemindMaps.get(remindCode);
    }
    
    /**
     * 更新节点以及它的子节点
     * @date
     * @author
     * @description
     * @params
     */
    public synchronized void put(RemindNode node) {
        if(node != null) {
            mRemindMaps.put(node.getRemindCode(), node);
            hasUpdate();
        }
    }
        
    /**
     * 当前节点是否有点要打，需递归遍历本节点和子节点
     *  0-查看消除 1-点击消除
     */
    public synchronized int remindType(Integer remindCode) {
        RemindNode node = get(remindCode);
        return remindType(node);
//    	return RemindConfig.REMIND_TYPE_NONE;
    }
    
    /**
     * 当前节点是否有点要打，需递归遍历本节点和子节点
     *  0-查看消除 1-点击消除
     */    
    public synchronized int remindType(RemindNode node) {
        int type = RemindConfig.REMIND_TYPE_NONE;
        if(node == null) {
            return type;
        } else {
            if(node.getType() > type)
                type = node.getType();
            
            if(node.getChilds() != null && node.getChilds().size() > 0) {
                List<Integer> childs = node.getChilds();
                for (Integer child : childs) {
                    int t = remindType(child);
                    if(t > type)
                        type = t;
                }
            }
        }
        
        return type;
    }
    
    
    /**
     * 如果当前节点是需要查看打点类型，递归计算出本节点和子节点的打点数量。
     */
    public synchronized int remindCount(Integer remindCode) {
        RemindNode node = get(remindCode);
        return remindCount(node);
    }
    
    /**
     * 返回当前节点需要打点数，不递归，只计算子节点
     */
    public synchronized int remindCount(RemindNode node) {
        if(node == null || node.getChilds() == null)
            return 0;
        
        int count = 0;
        for(Integer remindCode : node.getChilds()) {
            //add start xcx 虚拟节点打点数统计特殊处理  2014-12-27
            if(RemindConfig.REMIND_VIRTUAL_NODE_CODE==remindCode){
                count++;
                continue;
            }
            //add end xcx 虚拟节点打点数统计特殊处理  2014-12-27
            RemindNode child = mRemindMaps.get(remindCode);
            if(child == null) continue;
//            if(child.getType() == RemindConfig.REMIND_TYPE_VIEW_CLEAN ||
//                    child.getType() == RemindConfig.REMIND_TYPE_VIEW_NUMBER) {
                //if(!child.isClicked())
                    count++;
//            }
        }
        
        return count;        
    }
    
    /**
     * 返回当前节点需要打点数，只计算当前子节点的数字打点总和，不计算非数字节点。
     */
    public synchronized int remindCountR(RemindNode node) {
        if(node == null || node.getChilds() == null)
            return 0;
        
        int count = 0;
        for(Integer remindCode : node.getChilds()) {
            //add start xcx 虚拟节点打点数统计特殊处理  2014-12-27
            if(RemindConfig.REMIND_VIRTUAL_NODE_CODE==remindCode){
                count++;
                continue;
            }
            //add end xcx 虚拟节点打点数统计特殊处理  2014-12-27
            RemindNode child = mRemindMaps.get(remindCode);
            if(child == null || 
                    child.getType()==RemindConfig.REMIND_TYPE_VIEW_CLEAN || 
                    child.getType()==RemindConfig.REMIND_TYPE_CLICK_CLEAN) 
                continue;
            if(child.getChilds()==null || child.getChilds().size()==0)
                count++;
            else if(RemindConfig.REMIND_TYPE_VIEW_NUMBER == child.getType() && !child.isClicked())
                count += remindCountR(child);
            else if(RemindConfig.REMIND_TYPE_CLICK_NUMBER == child.getType())
                count += remindCountR(child);
        }
        
        return count;
    }
    
    
    /**
     * 清除节点及子节点, 递归调用
     * 不做持久化
     */
    public synchronized void clearNode(Integer remindCode) {
        RemindNode node = get(remindCode);
        if(node == null)
            return;
        
        if(node.getChilds() == null || node.getChilds().size() == 0) {
            mRemindMaps.remove(remindCode);
            hasUpdate();
        } else {
            List<Integer> childs = node.getChilds();
            for(Integer child : childs) {
                clearNode(child);
                hasUpdate();
            }
        }
    }
    
    /**
     * 清除节点及子节点, 递归调用
     * 不做持久化
     */    
    public synchronized void clearNode(RemindNode node) {
        if(node == null)
            return;
        
        if(node.getChilds() == null || node.getChilds().size() == 0) {
            mRemindMaps.remove(node.getRemindCode());
        } else {
            List<Integer> childs = node.getChilds();
            for(Integer child : childs) {
                clearNode(child);
            }
        }
    }
    
    public synchronized void clearNodeWithVirtualChildNodes(Integer remindCode) {
        mRemindMaps.remove(remindCode);
        hasUpdate();
        save();
    }
    /**
     * 清除老配置后，保存
     * 持久化
     * @date
     * @author
     * @description
     * @params
     */
    public synchronized void cleanAndSave() {
        LogUtil.d(TAG, "cleanAndSave");
        mRemindMaps.clear();
        hasUpdate();
        save();
    }
    
    /**
     * 释放内存，不做持久化
     * @date
     * @author
     * @description
     * @params
     */
    public synchronized void release() {
        LogUtil.d(TAG, "release size="+mRemindMaps.size());
        mRemindMaps.clear();
        hasUpdate();
        mInstance = null;
    }    
    
    public synchronized void dumps() {
        dumps("");
    }
    
    public synchronized void dumps(String title) {
        LogUtil.d(TAG, "dumps "+title+" size="+mRemindMaps.size());        
        Collection<RemindNode> c = mRemindMaps.values();
        for (RemindNode node : c) {
            LogUtil.d(TAG, " "+node.toString());
        }
    }
    
    
    public static class RemindUpdateListener {
        public void remindNodeChanged(){
            
        }
    }
}
