package so.contacts.hub.search.bean;

public class SearchStrategyBean extends SearchInfo {

	private static final long serialVersionUID = 1L;

	private String service_name; // 搜索数据商的名字，如“大众点评”

	private String factory; // 搜索数据商的类全名

	private int sort; // 排序

	private String orderby; // 排序规则

	private long out_time; // 过期时间

	public String getFactory() {
		return factory;
	}

	public void setFactory(String factory) {
		this.factory = factory;
	}

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	public String getService_name() {
		return service_name;
	}

	public void setService_name(String service_name) {
		this.service_name = service_name;
	}

	public String getOrderby() {
		return orderby;
	}

	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}

	public long getOut_time() {
		return out_time;
	}

	public void setOut_time(long out_time) {
		this.out_time = out_time;
	}

}
