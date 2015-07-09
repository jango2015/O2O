package so.contacts.hub.thirdparty.tongcheng.bean;


public class HotelOrderPostBean implements java.io.Serializable{
    public String hotelOrderId;
    public String hotelName;
    public String hotelImg;
    
    public HotelOrderPostBean(String hotelOrderId, String hotelName, String hotelImg){
        this.hotelOrderId = hotelOrderId;
        this.hotelName = hotelName;
        this.hotelImg = hotelImg;
    }
}

