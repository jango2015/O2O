package so.contacts.hub.ui.yellowpage.bean;

/**
 * 查询话费 
 * num： 运营商号码
 * text: 查询内容
 */
public class QueryCommandInfo {
	
	private String num;
	
	private String text;

	public QueryCommandInfo(String num, String text) {
		this.num = num;
		this.text = text;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "name: " + num + " ,text: " + text;
	}
	

}