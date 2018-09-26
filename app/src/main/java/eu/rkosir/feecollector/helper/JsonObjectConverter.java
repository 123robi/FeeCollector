package eu.rkosir.feecollector.helper;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonObjectConverter {

	private JSONObject object;
	private String json;

	public JsonObjectConverter(String json) {
		this.json = json;
	}

	public String getString(String key) {
		try {
			object = new JSONObject(json);
			return object.getString(key);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
}
