package so.contacts.hub.shuidianmei.bean;

public class WEGUserBean {
    //账单
    private String bills;
    //年月
    private String yearmonth ;
    //账户名
    private String username ;
    //产品ID
    private String proid;
    //账户ID
    private String account;
    //回调结果
    private String result ;
    //手续费
    private String fee;
    

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getProid() {
        return proid;
    }

    public void setProid(String proid) {
        this.proid = proid;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getBills() {
        return bills;
    }

    public void setBills(String bills) {
        this.bills = bills;
    }

    public String getYearmonth() {
        return yearmonth;
    }

    public void setYearmonth(String yearmonth) {
        this.yearmonth = yearmonth;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    @Override
    public String toString() {
        return "WEGUserBean [bills=" + bills + ", yearmonth=" + yearmonth + ", username="
                + username + ", proid=" + proid + ", account=" + account + ", result=" + result
                + ", fee=" + fee + "]";
    }

    
    
    
    
}
