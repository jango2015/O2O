package so.contacts.hub.ui.yellowpage.bean;


/**
 * 话费查询
 * 描述一条查询运营商的信息
 *
 */
public class OperatorsInfo {
	
	private int tag;
	
	private QueryCommandInfo commandinfo;
	
	public OperatorsInfo(int tag, QueryCommandInfo commandinfo){
		this.tag = tag;
		this.commandinfo = commandinfo;
	}
	
	public OperatorsInfo(int tag, String num, String text){
		this.tag = tag;
		this.commandinfo = new QueryCommandInfo(num, text);
	}
	

	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

	public QueryCommandInfo getCommandinfo() {
		return commandinfo;
	}

	public void setCommandinfo(QueryCommandInfo commandinfo) {
		this.commandinfo = commandinfo;
	}
	
	
	

}