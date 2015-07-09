package so.contacts.hub.ui.yellowpage.bean;

import java.io.Serializable;

public class NumberItem implements Serializable{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String number;            //号码
    private String number_description; //号码描述
    
    public String getNumber() {
        return number;
    }
    public void setNumber(String number) {
        this.number = number;
    }
    public String getNumberDescription() {
        return number_description;
    }
    public void setNumberDescription(String numberDescription) {
        this.number_description = numberDescription;
    }
}
