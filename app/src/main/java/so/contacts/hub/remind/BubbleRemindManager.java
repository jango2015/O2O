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
import so.contacts.hub.core.Config;
import so.contacts.hub.gamecenter.utils.SharedPreferenceUtils;
import so.contacts.hub.remind.utils.RemindUtils;
import so.contacts.hub.util.LogUtil;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

/**
 * @author change
 *
 */
public class BubbleRemindManager{
    private static final String TAG = "BubbleRemindManager";

    private static BubbleRemindManager mInstance = null;  // 可以使用弱引用优化
    private static final String REMIND_NODE_MAPS = "BUBBLE_REMIND_NODE_MAPS"; 
        
    private HashMap<Integer, RemindNode> mRemindMaps = null;  // category_id <==> RemindNode
    
    private volatile boolean changed; // 记录内存更新是否持久化
    
    private int mPersentFlag = 1; // 0-db 1-sharePerfernce xml 
    
    private static Context mContext = null;

    public static BubbleRemindManager getInstance() {
        mContext = ContactsApp.getInstance();        
        if(mInstance == null) {
            mInstance = new BubbleRemindManager();
        }
        return mInstance;
    }
    
    public static BubbleRemindManager getInstance(Context ctx) {
        mContext = ctx;
        if(mInstance == null) {
            mInstance = new BubbleRemindManager();
        }
        return mInstance;
    }    
    
    private BubbleRemindManager() {
        changed = false;
        reload();
    }
    
    /**
     * 进入该activity，模拟点击当前节点
     */
    public static void onRemindClick(Integer remindCode) {
        LogUtil.d(TAG, "onRemindClick remindCode="+remindCode);
        
        if(remindCode != null && remindCode >= 0) {
            RemindNode curNode = BubbleRemindManager.getInstance().get(remindCode);
            if (curNode != null) {
                curNode.setClicked(true);
                BubbleRemindManager.getInstance().hasUpdate();
                /**
                 * 如果当前Activity是最末端叶子节点，那么进入该activity后，看成节点已访问(包括查看和点击)，则删除该节点
                 */
                if (curNode.getChilds() == null || curNode.getChilds().size() == 0) {
                    RemindNode parentNode = BubbleRemindManager.getInstance().get(curNode.getParent());
                    if (parentNode != null) {
                        parentNode.removeChild(curNode.getRemindCode());
                    }
                    
                    BubbleRemindManager.getInstance().clearNode(curNode);
                    
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
            mListener.BubbleNodeChanged();        
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
        
        changed = false;
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
        return node.getType();
    }
    
    /**
     * 当前节点是否有点要打，需递归遍历本节点和子节点
     *  0-查看消除 1-点击消除
     */    
    public synchronized int remindType(RemindNode node) {
        return remindType(node.getRemindCode());
    }
        
    /**
     * 清除节点及子节点, 递归调用
     * 不做持久化
     */
    public synchronized void clearNode(Integer remindCode) {
        if(mRemindMaps != null)
            mRemindMaps.remove(remindCode);
        hasUpdate();
    }
    
    /**
     * 清除节点及子节点, 递归调用
     * 不做持久化
     */    
    public synchronized void clearNode(RemindNode node) {
        if(node == null)
            return;
        
        clearNode(node.getRemindCode());
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
}
