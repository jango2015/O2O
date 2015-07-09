package so.contacts.hub.net;

import java.util.HashMap;
import java.util.Map;

public class EmptyRequestData extends BaseRequestData {

    private Map<String, String> mParams = new HashMap<String, String>();
    
    @Override
    protected void setParams(Map<String, String> params) {

    }

    @Override
    public Map<String, String> getParams() {
        return mParams;
    }
    
    @Override
    public void setParam(String param, String value) {
       mParams.put(param, value);
    }
}
