package so.contacts.hub.businessbean;

import java.io.Serializable;
import java.util.List;

public class AppRecommendInfo implements Serializable {
		public long id; //[long][not null][推荐应用ID]
		public String app_name; //[String][not null][应用名]
		public String package_name; //[String][not null][应用包名]
		public String remark; //[String][not null][应用描述]
		public String version; //[String][not null][应用版本号]
		public String icon; //[String][not null][应用ICON]
		public List<String> l_imgs; //[List<String>][not null][应用截图大图列表]
		public List<String> s_imgs; //[List<String>][not null][应用截图小图列表]
		public String down_url; //[String][not null][下载地址]
		public String size; //[String][not null][包大小]
		public int app_type;//[int][not null][0:无类别,1:热门,2:首发,3:最新,4:推广] 
		public long down_count;//[long][not null][下载数 默认为:0] 
		
		// 自定义
		public long download_id; //[long][not null][本地应用下载ID]
		public int status;// 记录该强应用推荐本地状态（0：默认，1.已处理）
		public long install_time;// 记录该强应用推荐安装的时间，用于后续is_delete和delete_time计算处理
	}