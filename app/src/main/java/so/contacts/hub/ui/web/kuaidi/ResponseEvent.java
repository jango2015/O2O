package so.contacts.hub.ui.web.kuaidi;

import org.json.JSONException;
import org.json.JSONObject;

public class ResponseEvent {

	public ResponseEvent() {
	}
	
	public void setData(Object data) {
		try {
			this.data.put("result", data);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void setCode(int code) {
		try {
			this.data.put("code", code);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private JSONObject data = new JSONObject();

	JSONObject getData() {
		return data;
	}

}
