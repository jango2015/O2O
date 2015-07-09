package so.contacts.hub.businessbean;

import java.io.Serializable;

import android.text.TextUtils;

public class SmsInfo extends ContactRecord implements Serializable {

	public int _id;
	public int threadId;
	public String address;
	public String person;
	public String body;
	public String date;
	public int read;
	public int status;
	public int type;

	public String getDate() {
		if (TextUtils.isEmpty(date) || "null".equals(date)) {
			date = "0";
		}
		return date;
	}

	// 关于content://sms/inbox表，大致包含的域有：
	// _id | 短消息序号 如100
	// thread_id | 对话的序号 如100
	// address | 发件人地址，手机号.如+8613811810000
	// person　| 发件人，返回一个数字就是联系人列表里的序号，陌生人为null
	// date | 日期 long型。如1256539465022
	// protocol | 协议 0 SMS_RPOTO, 1 MMS_PROTO
	// read | 是否阅读 0未读， 1已读
	// status | 状态 -1接收，0 complete, 64 pending, 128 failed
	// type | 类型 1是接收到的，2是已发出
	// body | 短消息内容
	// service_center | 短信服务中心号码编号。如+8613800755500

}
