package so.contacts.hub.thirdparty.express;

public class ExpressMsgBean {
    private String company_code;
    private String company;
    private String order_no;//快递单号
//  private String status;//快递最新状态;
    public String getCompany_code() {
        return company_code;
    }
    public void setCompany_code(String company_code) {
        this.company_code = company_code;
    }
    public String getCompany() {
        return company;
    }
    public void setCompany(String company) {
        this.company = company;
    }
    public String getOrder_no() {
        return order_no;
    }
    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }
    @Override
    public String toString() {
        return "ExpressMsgBean [company_code=" + company_code + ", company="
                + company + ", order_no=" + order_no + "]";
    }
    
    
}
