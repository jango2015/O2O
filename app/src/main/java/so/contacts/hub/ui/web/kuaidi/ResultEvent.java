/**
 * 
 */
package so.contacts.hub.ui.web.kuaidi;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author javafx
 * 
 */
public class ResultEvent {

	public ResultEvent(String eventId, String funId, ResponseEvent response) {
		this.eventId = eventId;
		this.data = response.getData();
		this.funId = funId;
		if (!data.has("code")) {
			try {
				data.put("code", "0");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public String getEventId() {
		return eventId;
	}

	public String getFunId() {
		return funId;
	}

	public JSONObject getData() {
		return data;
	}

	private final String funId;
	private final String eventId;
	private final JSONObject data;

}
