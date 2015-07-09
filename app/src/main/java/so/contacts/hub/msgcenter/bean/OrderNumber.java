
package so.contacts.hub.msgcenter.bean;

import java.io.Serializable;

public class OrderNumber implements Serializable {

    private static final long serialVersionUID = 1L;

    private String pt_order_no;//有订单的业务为订单号,没订单的业务则为特征码

    private String order_no;
    
    private int pt_order_status=-1;

    public String getPt_order_no() {
        return pt_order_no;
    }

    public void setPt_order_no(String pt_order_no) {
        this.pt_order_no = pt_order_no;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    @Override
    public String toString() {
        return "OrderNumber [pt_order_no=" + pt_order_no + ", order_no=" + order_no + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((order_no == null) ? 0 : order_no.hashCode());
        result = prime * result + ((pt_order_no == null) ? 0 : pt_order_no.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        OrderNumber other = (OrderNumber)obj;
        if (order_no == null) {
            if (other.order_no != null)
                return false;
        } else if (!order_no.equals(other.order_no))
            return false;
        if (pt_order_no == null) {
            if (other.pt_order_no != null)
                return false;
        } else if (!pt_order_no.equals(other.pt_order_no))
            return false;
        return true;
    }

    public int getPt_order_status() {
        return pt_order_status;
    }

    public void setPt_order_status(int pt_order_status) {
        this.pt_order_status = pt_order_status;
    }

}
