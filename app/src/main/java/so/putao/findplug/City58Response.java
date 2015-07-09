package so.putao.findplug;

import java.io.Serializable;
import java.util.List;

/**
 * 58同城请求对应GSON数据
 * @author putao_lhq
 * @version 2014年9月24日
 */
public class City58Response implements Serializable {

	private static final long serialVersionUID = 2660651000387373411L;
	public int status;
	public List<City58Item> data;
	
	@Override
	public String toString() {
		return new StringBuilder("status = ").append(status)
				.append(" ,items size: " + data.size()).toString();
	}
}
