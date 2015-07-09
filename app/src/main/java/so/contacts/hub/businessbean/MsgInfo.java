package so.contacts.hub.businessbean;

import java.io.Serializable;

public class MsgInfo implements Serializable, Comparable<MsgInfo> {

	public int raw_contacts_id;
	public String number;

	public static final int MSG_SENDING = 1;
	public static final int MSG_SEND_SUCCESS = 2;
	public static final int MSG_SEND_FAIL = 3;

	private static final long serialVersionUID = 0;
	private int row_id;
	private String msg_id;
	private String msg_user_jid;
	private String msg_circle_jid;
	private String msg_content;
	private int msg_type; // 1-文字消息 2-图片 3-赞 4-任务 5-系统消息
	private long msg_time;
	private int msg_is_handler;
	private int msg_is_read; // 0-未读 1-已读
	private int msg_status; // 1-发送中 2-发送完成 3-发送失败
	private int msg_owner; // 1-自己发送 0-他人发送

	public String getMsg_id() {
		return msg_id;
	}

	public void setMsg_id(String msg_id) {
		this.msg_id = msg_id;
	}

	public int getMsg_owner() {
		return msg_owner;
	}

	public void setMsg_owner(int msg_owner) {
		this.msg_owner = msg_owner;
	}

	public int getRow_id() {
		return row_id;
	}

	public void setRow_id(int row_id) {
		this.row_id = row_id;
	}

	public String getMsg_user_jid() {
		return msg_user_jid;
	}

	public int getMsg_is_read() {
		return msg_is_read;
	}

	public void setMsg_is_read(int msg_is_read) {
		this.msg_is_read = msg_is_read;
	}

	public void setMsg_user_jid(String msg_user_jid) {
		this.msg_user_jid = msg_user_jid;
	}

	public String getMsg_circle_jid() {
		return msg_circle_jid;
	}

	public void setMsg_circle_jid(String msg_circle_jid) {
		this.msg_circle_jid = msg_circle_jid;
	}

	public String getMsg_content() {
		return msg_content;
	}

	public void setMsg_content(String msg_content) {
		this.msg_content = msg_content;
	}

	public int getMsg_type() {
		return msg_type;
	}

	public void setMsg_type(int msg_type) {
		this.msg_type = msg_type;
	}

	public long getMsg_time() {
		return msg_time;
	}

	public void setMsg_time(long msg_time) {
		this.msg_time = msg_time;
	}

	public int getMsg_is_handler() {
		return msg_is_handler;
	}

	public void setMsg_is_handler(int msg_is_handler) {
		this.msg_is_handler = msg_is_handler;
	}

	public int getMsg_status() {
		return msg_status;
	}

	public void setMsg_status(int msg_status) {
		this.msg_status = msg_status;
	}

	@Override
	public boolean equals(Object o) {
		return this.msg_id.equals(((MsgInfo) o).msg_id);
	}

	@Override
	public int compareTo(MsgInfo another) {
		return msg_time > another.msg_time ? 1 : -1;
	}

}
