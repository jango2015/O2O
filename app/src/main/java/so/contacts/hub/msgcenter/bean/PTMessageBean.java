
package so.contacts.hub.msgcenter.bean;

import java.io.Serializable;

import so.contacts.hub.msgcenter.MsgCenterConfig.Product;

/**
 * 消息中心数据接口
 * 
 * @author putao_lhq
 */
public class PTMessageBean implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private long _id;

    private long msgId; // 消息id

    private String subject; // 消息主题

    private String digest; // 消息摘要

    private long time; // 消息更新时间
    
    private int is_notify; // 是否需要弹出notification消息

    private String expand_param; // 扩展参数

    private int productType; // 服务类别， 详情见MsgCenterConfig.Product

    private int status; // 服务状态 0表示未过期,1表示已过期

    public long getMsgId() {
        return msgId;
    }

    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getExpand_param() {
        return expand_param;
    }

    public void setExpand_param(String expand_param) {
        this.expand_param = expand_param;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public String getDigest() {
        return this.digest;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("msgId=").append(msgId).append(" productType=").append(productType)
                .append(" subject=").append(subject).append(" digest=").append(digest)
                .append(" time=").append(time).append(" expand_param=").append(expand_param).append("is_notify=").append(is_notify);
        // 增加tostring 里 is_notify字段;
        return sb.toString();
    }

    public int getProductType() {
        return productType;
    }

    public void setProductType(int productType) {
        this.productType = productType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }
    
    public int getIs_notify() {
        return is_notify;
    }

    public void setIs_notify(int is_notify) {
        this.is_notify = is_notify;
    }

    /**
     * 获取产品类型枚举对象
     * 
     * @return
     */
    public Product getProduct() {
        return Product.getProduct(productType);
    }
}
