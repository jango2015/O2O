package so.contacts.hub.remind;

import java.util.ArrayList;
import java.util.List;
/**
 * @author change
 * 所有需要打点的Activity的节点，用来描述该节点的打点类型，打点数
 */
public class RemindNode implements java.io.Serializable{
	
	private static final long serialVersionUID = 9140699357439092408L;

	private int remindCode;  // 记录类别id
    
    private int type;         // 打点类型,0x1:查看消除打点,0x2:点击消除打点,0x3:查看消除数字,0x4:点击消除数字,0x4:时间消除
    private boolean isClicked;   
    private String imgUrl;
    
    private int style;
    private String text; // 打点显示文字
    private long endTime; // 时间消除类型的截止时间

    private Integer parent;
    private List<Integer> childs;
    
    private long insertTime; //add by putao_lhq 引入时间
    
    public RemindNode(int remindCode, int type) {
        this.remindCode = remindCode;
        this.type = type;
        this.isClicked = false;
        this.parent = null;
        this.childs = null;
        this.insertTime = System.currentTimeMillis();//add by putao_lhq
    }
        
    public boolean hasChild() {
        if(childs == null || childs.size()==0) {
            return false;
        }
        return true;
    }
    
    public void addChild(Integer remindCode) {
        if(childs == null) {
            childs = new ArrayList<Integer>();
        }
        
        //modify xcx start 2014-12-27 虚拟节点特殊处理
        if(!childs.contains(remindCode)||RemindConfig.REMIND_VIRTUAL_NODE_CODE==remindCode) 
            childs.add(remindCode);
        //modify xcx end 2014-12-27 虚拟节点特殊处理
    }
    
    public void addChild(RemindNode child) {
        if(childs == null) {
            childs = new ArrayList<Integer>();
        }
        
        if(!childs.contains(child.getRemindCode())) 
            childs.add(child.getRemindCode());
    }
    
	public int getRemindCode() {
		return remindCode;
	}

	public void setRemindCode(int remindCode) {
		this.remindCode = remindCode;
	}

	public Object removeChild(Integer childCode) {
        if(childs != null)
            return childs.remove(childCode);

        return null;
    }
    
    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isClicked() {
        return isClicked;
    }

    public void setClicked(boolean isClicked) {
        this.isClicked = isClicked;
    }

    public Integer getParent() {
        return parent;
    }

    public void setParent(Integer parent) {
        this.parent = parent;
    }

    public List<Integer> getChilds() {
        return childs;
    }

    public void setChilds(List<Integer> childs) {
        this.childs = childs;
    }
    
    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
    
    /**
     * 当前气泡节点是否已过期
     * @return
     */
    public boolean isExpired() {
        return System.currentTimeMillis() >= endTime;
    }

    /**
     * 获取插入时间
     * @return
     * </br>add by putao_lhq
     */
    public long getInsertTime() {
    	return insertTime;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(" remindCode=");
        sb.append(remindCode);
        sb.append(" type=");
        sb.append(type);
        sb.append(" text=");
        sb.append(text);
        sb.append(" style=");
        sb.append(style);
        sb.append(" imgUrl=");
        sb.append(imgUrl);
        sb.append(" isClicked=");
        sb.append(isClicked);
        sb.append(" endTime=");
        sb.append(endTime);        
        sb.append(" parent=");
        sb.append(parent);
        sb.append(" childs={");
        if(childs != null) {
            for(Integer i : childs) {
                sb.append(i).append(",");
            }
        }
        sb.append("}");
        return sb.toString();
    }    
}
