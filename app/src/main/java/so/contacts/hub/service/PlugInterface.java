package so.contacts.hub.service;

import so.putao.aidl.IPutaoService;
import android.content.Context;
import android.view.View;

public interface PlugInterface {
    public boolean initPlug(Context host, Context plug);
    
    public void unitPlug(Context host);
    
    public IPutaoService getService();
    
    public View getPlugView(Context hostContext);
    
    public void onPlugStart(); //add by hyl 2014-9-3
    
    public void onPlugResume();
    
    public void onPlugPause();
    
    public void onPlugStop(); //add by hyl 2014-9-3
    
    public void onPlugDestory();
    
    public void onPlugIn(); //add by hyl 2014-9-3
    
    public void onPlugOut();//add by hyl 2014-9-3
    
}
