package so.contacts.hub.push.bean;

import java.io.Serializable;

public class OpConfig implements Serializable{
    private int version;
    
    private String data;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(" OpConfig version=").append(version).append(" data=\n").append(data);
        return sb.toString();
    }
    
}
