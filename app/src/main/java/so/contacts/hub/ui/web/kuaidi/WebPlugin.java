/**
 * 
 */
package so.contacts.hub.ui.web.kuaidi;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

/**
 * @author javafx
 * 
 */
public abstract class WebPlugin implements Runnable {

	public abstract void onCreate(Context context);

	public abstract void onDestory();

	public ResponseEvent execute(JSONObject params) {
		return null;
	}

	private Handler handler = null;

	private List<MessageBean> messageBeans = new ArrayList<WebPlugin.MessageBean>();

	private MessageBean lastBean = null;

	void bind(Handler handler) {
		this.handler = handler;
	}

	void load(String eventId, String param, String funId) {
		MessageBean bean = new MessageBean();
		bean.eventId = eventId;
		bean.funId = funId;
		JSONObject params = null;
		if (!TextUtils.isEmpty(param)) {
			try {
				params = new JSONObject(param);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (null == params) {
			bean.params = new JSONObject();
		} else {
			bean.params = params;
		}
		synchronized (messageBeans) {
			messageBeans.add(bean);
		}
	}

	private static class MessageBean {
		private String eventId = null;
		private String funId = null;
		private JSONObject params = null;
	}

	public final void publishEvent(ResponseEvent result) {
		if (this instanceof Session && null != lastBean) {
			Message msg = handler.obtainMessage(WebBinder.WHAT_PUBLISH_RESULT);
			msg.obj = new ResultEvent(lastBean.eventId, lastBean.funId, result);
			msg.sendToTarget();
		} else {
			throw new IllegalAccessError("please implements Session before...");
		}
	}

	@Override
	public final void run() {
		MessageBean bean = null;
		synchronized (messageBeans) {
			if (!messageBeans.isEmpty()) {
				bean = messageBeans.remove(0);
				lastBean = bean;
			}
		}
		if (null != bean) {
			ResponseEvent result = execute(bean.params);
			if (null != result) {
				Message msg = handler
						.obtainMessage(WebBinder.WHAT_PUBLISH_RESULT);
				msg.obj = new ResultEvent(bean.eventId, bean.funId, result);
				msg.sendToTarget();
			}
		}
		if (this instanceof Session) {

		} else {
			Message message = handler
					.obtainMessage(WebBinder.WHAT_PUBLISH_DESTORY);
			message.obj = this;
			message.sendToTarget();
		}
	}
}
